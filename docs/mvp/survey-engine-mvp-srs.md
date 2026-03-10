# **Software Requirements Specification (SRS)**

## **Survey Engine MVP (Implemented Baseline)**

| Field | Value |
| ----- | ----- |
| Document Title | Survey Engine MVP SRS |
| Version | 3.1 - Theme Studio, Draft Resume, and Private Session Flow |
| Date | March 11, 2026 |
| Prepared For | Product and Engineering |
| Classification | Internal |
| Status | Reflects implemented code and schema |

## **1\. Purpose**

Define the implemented MVP requirements for a multi-tenant Survey Engine that supports survey authoring, campaign distribution, response collection, scoring, tenant-scoped authentication, and SaaS subscription/plan controls.

## **2\. Scope**

### **2.1 In Scope (Implemented)**

* Multi-tenant data model with tenant isolation in service/repository layer and DB constraints.
* Engine-owned admin authentication with dual delivery modes:
  * browser session mode (HttpOnly cookie-based tokens)
  * headless/API mode (explicit token response contract)
* Admin RBAC using JWT roles (`SUPER_ADMIN`, `ADMIN`, `EDITOR`, `VIEWER`).
* Question bank and category CRUD with live mutable bank definitions.
* Survey CRUD with draft-time pinning, lifecycle transitions, and immutable published snapshots.
* Campaign CRUD, runtime settings, activation, and distribution channel generation.
* Structured campaign theming via Theme Studio, including palette, branding, layout, motion, and advanced header/footer overrides.
* Responder submission, response locking, reopen workflow, and basic analytics.
* Draft-capable response lifecycle with `IN_PROGRESS` persistence, save/resume flow, and stable responder draft restoration.
* Campaign access mode (`PUBLIC`/`PRIVATE`) with tenant-level external auth profile for private responder access.
* Private responder session cookies for OIDC-based campaigns, including responder-side logout.
* Automated campaign scoring using category weights pinned from survey structure.
* Optional manual scoring profile APIs retained for advanced/administrative use (not part of default frontend flow).
* Optional per-question remarks and configurable rating-scale display mode (`NUMBERS` / `STARS`).
* SaaS subscription domain (tenant subscription, plan catalog, mock payment success flow).
* Super-admin plan catalog management and tenant plan quota enforcement.
* Super-admin tenant operations: tenant listing, suspend/activate, impersonation, and subscription override.
* Tenant and platform audit log query APIs.
* **NEW: Enterprise Feature Management System (tours, tooltips, banners, feature flags, announcements).**
* **NEW: First-time user tracking with backend-persisted onboarding progress.**
* **NEW: Guided help system with multi-layer access control (global/tenant/plan/role/rollout).**
* **NEW: Super admin UI for feature governance and tenant-level configuration.**
* **NEW: Persisted admin appearance preferences (`light` / `dark` / `system`) with backend synchronization.**

### **2.2 Out of Scope (Current Codebase)**

* Real payment gateway integration (currently mock-success gateway).  
* Per-feature usage metering beyond implemented quotas (admin users, active campaigns, responses per campaign).  
* Full enterprise permission matrix beyond role-based API controls.  
* Standalone theme-template CRUD API outside campaign settings (themeing is currently managed through campaign settings and preview payloads rather than a dedicated public template controller).  
* Native SAML protocol stack, native LDAP bind integration, and provider-specific SDK integrations for each IdP vendor.

## **3\. Definitions**

* **Tenant**: Organizational boundary for data ownership and access isolation.  
* **Survey**: Authoring container of pages/questions and lifecycle state.  
* **Campaign**: Delivery container for one survey snapshot with runtime settings.  
* **Question Bank**: Reusable questions used by categories/surveys.  
* **Category**: Grouping of questions with live mappings in the question bank.  
* **Pinned Version**: Immutable question/category copy created at survey draft composition time.  
* **Weight Profile**: Category weight model used for score computation (default profile is auto-managed per campaign).  
* **Auth Profile**: Tenant-level respondent auth trust configuration.  
* **Theme Config**: Structured campaign presentation contract covering palette, branding, layout, motion, and advanced overrides.
* **Subscription**: Tenant billing state and active plan assignment.  
* **Plan Definition**: Super-admin managed pricing/quota configuration for plan codes.

## **4\. Functional Requirements**

### **4.1 Admin Authentication and Authorization**

* Engine issues admin JWT access tokens and refresh tokens.
* Supported admin auth modes:
  * Browser session mode:
    * `POST /api/v1/admin/auth/register`
    * `POST /api/v1/admin/auth/login`
    * `POST /api/v1/admin/auth/refresh` (refresh token from HttpOnly cookie)
    * `POST /api/v1/admin/auth/logout`
    * `GET /api/v1/admin/auth/me`
  * Headless token mode:
    * `POST /api/v1/admin/auth/token/register`
    * `POST /api/v1/admin/auth/token/login`
    * `POST /api/v1/admin/auth/token/refresh`
* JWT contains tenant and role claims and populates request tenant context.  
* API authorization baseline requires authenticated admin JWT for protected admin APIs; plan update endpoint requires `SUPER_ADMIN`.

#### **4.1.1 CSRF and Session Mode**

* CSRF protection is enabled for browser session mode using cookie token repository.
* CSRF token bootstrap endpoint: `GET /api/v1/admin/auth/csrf`.
* CSRF checks are bypassed for:
  * Bearer-token authenticated requests
  * public/respondent endpoints
  * admin auth bootstrap endpoints (`/api/v1/admin/auth/**`)

### **4.2 Multi-Tenancy and Data Isolation**

* Core entities are tenant-owned via `tenant_id`: question, category, survey, campaign, weight_profile, auth_profile, survey_response, admin_user.  
* Tenant ownership is enforced in service/repository access patterns for admin operations.  
* DB-level tenant integrity is enforced with foreign keys to `tenant` and tenant-consistent composite FKs on major relationships.  
* Cross-tenant access is rejected for protected admin operations.

### **4.3 Question Bank and Category Model**

* Create, read, update, deactivate questions.  
* Create, read, update, deactivate categories.  
* Question option definitions are owned by question bank `optionConfig` and versioned with question versions.  
* Category bank mappings reference live question versions (`version_number = 1`) and remain editable.

#### **4.3.1 Question Option Model (`optionConfig`)**

* Question create/update payload supports `optionConfig` as JSON object.
* `optionConfig.options` is required for:
  * `SINGLE_CHOICE`
  * `MULTIPLE_CHOICE`
  * `RANK`
* Each option value must be non-empty and unique.
* `RATING_SCALE` does not require option lists.
* Question bank update behavior:
  * Updates mutate the live bank definition.
  * Live question/category versions remain anchored at version `1` for bank operations.

#### **4.3.2 Survey Pinning Model (Question + Category)**

* Pinning happens during survey draft composition (`POST/PUT /api/v1/surveys`), not at publish time.
* For every draft save:
  * Included questions get pinned immutable `question_version` rows (`version_number > 1`).
  * Tagged categories get pinned immutable `category_version` rows (`version_number > 1`) that reference pinned question versions.
* Bank updates after pinning do not mutate survey-pinned copies.
* Survey updates while still `DRAFT` rebuild the pinned copy set.
* Publish only snapshots the already-pinned survey structure into `survey_snapshot`.

#### **4.3.3 Survey Question Configuration Responsibilities**

* Choice options must be defined in question bank `optionConfig`, not in `answerConfig`.
* `answerConfig` remains survey-question scoped and optional for answer-behavior rules (for example: `minSelections`, `maxSelections`, `step`, ranking rules).
* For category-linked survey questions:
  * `categoryWeightPercentage` is required.
  * All questions under the same category must use the same `categoryWeightPercentage`.
  * Total distinct category weights in a survey draft must equal exactly `100.00`.

### **4.4 Survey Builder and Lifecycle**

* Create, read, update, deactivate surveys with pages/questions.  
* Lifecycle transitions supported:
  * `DRAFT -> PUBLISHED`
  * `PUBLISHED -> CLOSED`
  * `CLOSED -> RESULTS_PUBLISHED`
  * `RESULTS_PUBLISHED -> ARCHIVED`
  * `CLOSED -> PUBLISHED` (reopen with reason)
* Draft create/update generates pinned question/category versions used by that survey draft.  
* Survey Details (draft mode) allows editing pinned copies directly:
  * `pinnedQuestionText`, `pinnedQuestionMaxScore`, `pinnedQuestionOptionConfig`
  * `pinnedCategoryName`, `pinnedCategoryDescription`
* Those edits apply only to the survey-pinned versions, never to live bank question/category records.
* Publishing creates immutable survey snapshot data from pinned versions.  
* Structural modifications are blocked for all non-`DRAFT` states (including `PUBLISHED`).
* After publish, Survey Details is read-only for pinned copies and structure.

### **4.5 Campaign Management and Access Modes**

* Create, read, update, deactivate campaigns.  
* Activate campaign only when linked survey is `PUBLISHED`.  
* Activation binds campaign to latest survey snapshot and auto-upserts campaign default scoring profile from pinned survey category weights.  
* Campaign access mode is:
  * `PUBLIC`: responder auth credential not required.
  * `PRIVATE`: trusted responder credential required and validated through tenant auth profile (`responderToken` or one-time `responderAccessCode`).
* Deprecated compatibility values (`SIGNED_TOKEN`, `SSO`) are normalized to private access behavior.

### **4.6 Campaign Runtime Settings**

* Configurable controls:
  * Password
  * CAPTCHA
  * One response per device
  * IP restriction
  * Email restriction
  * Response quota
  * Close date/time
  * Session timeout
  * Question numbers / progress indicator / back button
  * Start/finish/header/footer messages
  * Structured theme configuration (`theme`) for palette, branding, layout, motion, and advanced custom HTML/CSS
  * Optional metadata capture flags
  * Configurable campaign-specific responder metadata fields (`dataCollectionFields`)
* Runtime checks are enforced during response submission.
* Close-time locking behavior:
  * At/after `closeDate`, system automatically locks open responses in `IN_PROGRESS` or `REOPENED`.
  * If settings are updated with `closeDate <= now`, locking is triggered immediately.

#### **4.6.1 Theme Studio Contract**

* Campaign settings support structured theme payload persistence through `theme`.
* Theme contract includes:
  * `templateKey`, `paletteKey`
  * `palette` colors
  * `branding` (`brandLabel`, `logoUrl`, `logoPosition`, `fontFamily`)
  * `layout` (`contentWidth`, `headerStyle`, `footerStyle`, `sectionStyle`, `questionCardStyle`, `categorySeparatorStyle`, alignments)
  * `motion.animationPreset`
  * structured `header` and `footer` content
  * `advanced` header/footer HTML and custom CSS override fields
* Public preview payload and admin preview payload both include effective theme configuration.
* Runtime responder form renders from theme config first and only falls back to legacy `headerHtml` / `footerHtml` when advanced override flags are enabled.

### **4.7 Distribution**

* Generate and list campaign distribution channels:
  * Public link
  * Private link tokenized URL
  * HTML embed
  * WordPress embed
  * JavaScript embed
  * Email channel template

### **4.8 Respondent Authentication (Tenant-Scoped)**

#### **4.8.1 Provider Strategy**

* Auth profile is configured per tenant, not per campaign.  
* Integration strategy is protocol-first, not provider-specific code per vendor:
  * OIDC/OAuth2 trust mode for modern IdPs (Keycloak, Okta, Auth0, Google, Entra ID, etc.).
  * Signed launch token mode as universal fallback when subscriber cannot provide OIDC.
  * Identity broker pattern for legacy SAML/LDAP environments (broker translates to OIDC/JWT for Survey Engine).
* Native SAML and LDAP protocol implementations are not required for MVP.

#### **4.8.2 Supported Modes**

* `PUBLIC_ANONYMOUS`  
* `SIGNED_LAUNCH_TOKEN`  
* `EXTERNAL_SSO_TRUST`

#### **4.8.2.1 Tenant Claim Mapping Rules**

* Claim mapping is tenant-scoped and configurable per auth profile.
* Mapping supports required and optional rules using:
  * `externalClaim` -> claim name/path from IdP token payload.
  * `internalField` -> normalized engine identity field.
  * `required` -> fail authentication when claim is missing.
* `respondentId` mapping is mandatory and must be marked required.
* Default mapping (applied when admin does not configure mappings):
  * `sub` -> `respondentId` (required)
  * `email` -> `email` (optional)
* Validation is fail-closed when required claims are absent.

#### **4.8.3 Fallback Policies**

* `SSO_REQUIRED`  
* `ANONYMOUS_FALLBACK`  
* `DISABLE_ON_FAILURE`

#### **4.8.4 Private Campaign Enforcement**

* Private campaign responder runtime requires:
  * campaign `AuthMode=PRIVATE`
  * tenant auth profile present
  * auth profile mode not `PUBLIC_ANONYMOUS`
  * valid responder credential (`responderToken` or one-time `responderAccessCode`) for the initial trust handoff
  * or a valid server-side responder session cookie after successful OIDC callback
  * anonymous fallback is rejected for private access.

#### **4.8.5 Signed Launch Token Operational Model**

* Subscriber backend must generate and sign a launch token after authenticating its own user.  
* Subscriber must configure tenant signing key/secret in its backend runtime and keep it private.  
* Survey Engine validates signature and claims before accepting response submission.  
* This model is intended to support legacy/custom enterprise identity systems without requiring Survey Engine to implement those protocols directly.

#### **4.8.6 Security Hardening Requirements (MVP Baseline + Production Targets)**

Implemented baseline:
* OIDC callback establishes a server-side responder session and clears runtime dependence on long-lived tokens in URLs.  
* Replay protection is enforced for signed launch token flow (`jti` with server-side replay store).  
* Strict claim validation is enforced (issuer, audience, expiry, clock skew, required `respondentId` mapping, tenant binding).  
* Auth failures return deterministic error codes and auth configuration updates are audit logged with before/after snapshots.

Production targets:
* Expand key lifecycle controls to full `kid`-based rotation policies with dual-key validation windows and external key management integration.

#### **4.8.7 OIDC Scope Defaults**

* Secure default scopes: `openid email profile`.
* Tenant admin may override scopes in auth profile.
* `openid` is always enforced even when custom scopes are provided.

#### **4.8.8 Provider Templates (UI/API Contract)**

* Engine exposes provider setup templates for:
  * `OKTA`
  * `AUTH0`
  * `AZURE_AD`
  * `KEYCLOAK`
* API:
  * `GET /api/v1/auth/providers/templates`
  * `GET /api/v1/auth/providers/templates/{providerCode}`
* Response contract includes:
  * `providerCode`, `displayName`, `description`
  * `defaultScopes`
  * `defaultClaimMappings`
  * `requiredConfigFields`
* UI should render template metadata as prefill guidance and allow tenant-level overrides before save.

#### **4.8.9 Existing SSO Session / Pre-Issued Token Behavior**

* Private responder authentication does not always require an interactive re-login prompt.
* If the subscriber uses OIDC start/callback flow and the user already has an active IdP session, the IdP may complete authentication silently (or with minimal interaction), then return control to Survey Engine callback flow.
* If the subscriber already has a valid trusted token for the responder (matching tenant auth profile validation rules), the responder may submit through the trusted token path without running interactive OIDC login again.
* In both cases, Survey Engine still enforces token validation and required claim mapping rules before accepting private response submission.

### **4.9 Responses, Locking, and Analytics**

* Responder can save a draft or submit to an active campaign endpoint.  
* Draft flow creates or updates `IN_PROGRESS` responses and preserves answers plus respondent metadata.  
* On successful final submit, response is auto-locked.  
* Admin can lock/reopen responses; reopen is audited with reason/window.  
* Close transition enforcement auto-locks any still-open (`IN_PROGRESS`/`REOPENED`) responses for expired campaigns.  
* Analytics endpoint returns totals and completion-related counts per campaign.

#### **4.9.1 Draft and Resume Behavior**

* Public responder draft endpoints:
  * `POST /api/v1/public/campaigns/{id}/responses/draft`
  * `POST /api/v1/public/campaigns/{id}/responses/draft/load`
* Draft lookup supports:
  * explicit `responseId`
  * responder identity
  * private responder session-backed identity after OIDC login
* Draft save is idempotent for the same response and updates answers in place by `questionId`.
* Final submit against a saved draft reuses the same response row instead of creating a second response.

#### **4.9.2 Response Payload Enrichment**

* Response detail payload includes:
  * `respondentMetadata`
  * answer-level `questionText`
  * `questionVersionNumber`
  * `questionType`
  * `optionConfig`
  * optional answer `remark`

#### **4.9.3 Question Interaction Enhancements**

* `RATING_SCALE` question rendering can be configured through `optionConfig.displayMode`:
  * `NUMBERS`
  * `STARS`
* Each answer can include an optional `remark` comment persisted with the response.

### **4.10 Scoring and Weighting**

* Default scoring profile is automatically created/updated at campaign activation from pinned survey category weights (`campaign.default_weight_profile_id`).  
* Response submission automatically computes and persists weighted score when campaign has default profile (`survey_response.weighted_total_score`, `weight_profile_id`, `scored_at`).  
* Score calculation uses deterministic category aggregation from validated answers and normalized weighted scoring.  
* Manual scoring profile endpoints remain available (`/api/v1/scoring/**`) for non-default use cases, but MVP frontend uses the simplified automatic flow.

### **4.11 Advanced Analytics and Cross-Segment Comparison**

* Platform provides real-time aggregated analytics mapped over responses, scores, and temporal metadata.
* **Overall Analytics mode:** Generates a unified `/full-report` that includes total summaries, answer-level histograms, and temporal completion velocity curves. Can be fully filtered by single metadata key-value queries (e.g., `metadata.Department=Sales`).
* **Cross-Segment Comparison mode:** Features a dedicated `/compare` POST endpoint receiving a dynamically built `ComparisonRequest` containing up to 5 demographic segments (each with specific metadata filters).
* The comparison engine isolates datasets concurrently on the backend and maps them into a unified `ComparisonAnalyticsResponse`.
* The frontend seamlessly maps these payload arrays into overlapping comparative geometries for Grouped Bar Charts and Multi-Line Temporal Curves without requiring BI tool extraction.

### **4.12 SaaS Subscription and Plans**

* Tenant subscription records include plan, status, billing period, and activity state.  
* Subscription checkout uses mock payment gateway and records transaction success.  
* Subscription and plan metadata are available via admin subscription endpoint.  
* Plan catalog is DB-driven (`plan_definition`) and editable by super admin.  
* Active quotas enforced from plan definition:
  * max active campaigns per tenant
  * max responses per campaign
  * max active admin users per tenant
* Admin API operations require active subscription (with configured endpoint exemptions for auth/subscription flows).

### **4.12 Identity Provider Compatibility Requirements**

* The engine is expected to support any provider that can issue standards-compliant OIDC/JWT tokens matching tenant configuration.  
* Providers that cannot issue compatible tokens must integrate through:
  * subscriber-side signed launch token generation, or
  * identity broker translation to OIDC/JWT.  
* “Any provider” support is therefore defined as standards compatibility or adapter/broker compatibility, not native protocol implementation for every legacy identity system.

### **4.13 Super-Admin Tenant Operations**

* Super-admin tenant management endpoints are implemented under `/api/v1/admin/superadmin/tenants`.
* Supported operations:
  * list tenants (paged)
  * suspend tenant
  * activate tenant
  * impersonate tenant admin
  * override tenant subscription
  * platform metrics retrieval
* Impersonation supports restore flow via `POST /api/v1/admin/auth/revert-impersonation`.

### **4.14 Audit Log Retrieval**

* Tenant-scoped activity endpoint: `GET /api/v1/audit-logs`.
* Platform-wide audit endpoint: `GET /api/v1/admin/superadmin/audit-logs`.
* Query supports filtering by action/entity/date and paging/sorting.

### **4.15 Enterprise Feature Management System (NEW - V28)**

* Central feature registry with metadata, access rules, and rollout configuration.
* Multi-layer access control:
  * Global enable/disable flag
  * Tenant-level overrides
  * Plan-based gating (BASIC/PRO/ENTERPRISE)
  * Role-based access (SUPER_ADMIN/ADMIN/EDITOR/VIEWER)
  * Rollout percentage (0-100%) for gradual releases
* Feature types supported:
  * TOUR: Multi-step guided tours with element highlighting
  * TOOLTIP: Contextual help with dismiss functionality
  * BANNER: Announcement banners with scheduling
  * FEATURE_FLAG: Binary feature toggles
  * ANNOUNCEMENT: Time-bound announcements
* User progress tracking:
  * Backend-persisted completion status
  * Access count and timestamps
  * Cross-device synchronization
* Super admin governance:
  * Create/update/delete feature definitions
  * Configure tenant-specific overrides
  * View usage analytics and adoption metrics
  * Bulk import/export features
* Frontend integration:
  * Reactive feature flag hook (`useFeatureFlag`)
  * Pre-built components (FeatureTour, FeatureTooltip, FeatureBanner)
  * Automatic availability checking
  * Progress tracking APIs
* Analytics and reporting:
  * Completion rates per feature
  * User engagement metrics
  * Tenant-level adoption dashboards
  * A/B testing support via rollout percentage

### **4.16 First-Time User Onboarding (NEW - V26/V27/V28)**

* First login tracking with `first_login` flag in admin_user table.
* Automatic redirect to onboarding flow for new users.
* Backend-persisted onboarding progress (not browser-dependent).
* Integration with feature management for tour/toOLTIP delivery.
* Plan selection flow during first-time onboarding.
* Contextual help tooltips with permanent dismiss option.
* Dashboard tour for initial product discovery.

### **4.17 Admin Preference Persistence**

* Admin preference API supports:
  * get all preferences
  * patch one preference
  * patch many preferences
  * check/mark onboarding completion
  * reset all preferences
* Current frontend usage includes persisted admin appearance mode:
  * `light`
  * `dark`
  * `system`
* Appearance preference is persisted locally and synchronized to backend user preferences for authenticated admins.

## **5\. Data and Schema Requirements**

### **5.1 Core Tenant Model**

* `tenant` is canonical owner table for tenant IDs.  
* Tenant is provisioned automatically on registration and on tenant-scoped writes when required.

### **5.2 Integrity Constraints**

* Tenant FK constraints exist on tenant-owned tables.  
* Composite tenant consistency constraints include:
  * `campaign(survey_id, tenant_id) -> survey(id, tenant_id)`
  * `weight_profile(campaign_id, tenant_id) -> campaign(id, tenant_id)`
  * `survey_response(campaign_id, tenant_id) -> campaign(id, tenant_id)`

### **5.3 Auth and Billing Tables**

* Admin auth: `admin_user`, `refresh_token`.  
* Respondent auth config: `auth_profile`, `claim_mapping`, `auth_config_audit`.  
* SaaS billing: `tenant_subscription`, `payment_transaction`, `plan_definition`.

### **5.4 Pinning and Scoring Columns**

* Survey pinning:
  * `survey_question.question_version_id` (pinned question reference)
  * `survey_question.category_version_id` (pinned category reference)
  * `survey_question.category_weight_percentage` (survey-level scoring weight by category)
* Campaign scoring linkage:
  * `campaign.default_weight_profile_id`
* Response scoring persistence:
  * `survey_response.weight_profile_id`
  * `survey_response.weighted_total_score`
  * `survey_response.scored_at`

## **6\. API Surface (Implemented)**

* Admin auth: `/api/v1/admin/auth/**`
  * Includes browser mode (`/register`, `/login`, `/refresh`, `/logout`, `/me`, `/csrf`) and token mode (`/token/**`).
* Plan management: `/api/v1/admin/plans` (PUT requires `SUPER_ADMIN`)
* Subscription: `/api/v1/admin/subscriptions/me`, `/api/v1/admin/subscriptions/checkout`
* Super-admin tenant ops: `/api/v1/admin/superadmin/tenants/**`, `/api/v1/admin/superadmin/metrics`
* Question bank: `/api/v1/questions/**`, `/api/v1/categories/**`
* Surveys: `/api/v1/surveys/**`
* Campaigns: `/api/v1/campaigns/**`
* Public campaign preview: `/api/v1/public/campaigns/**`
* Scoring: `/api/v1/scoring/**`
  * Note: scoring APIs are implemented, but the default MVP frontend flow no longer exposes a dedicated `/scoring` route.
* Auth profiles/validation: `/api/v1/auth/**`
  * Includes provider template endpoints for onboarding UI.
* Responses: `/api/v1/responses/**` and public submit `/api/v1/responses`
* Advanced Analytics: `/api/v1/analytics/campaigns/{campaignId}/**`
  * Includes `/full-report` (GET, metadata filtered) and `/compare` (POST, multiplexed segments).
* Audit logs: `/api/v1/audit-logs`, `/api/v1/admin/superadmin/audit-logs`
* **Feature Management: `/api/v1/admin/features/**` (Super Admin)**
  * `GET /` - List all features
  * `POST /` - Create new feature
  * `PUT /{featureKey}` - Update feature
  * `DELETE /{featureKey}` - Delete feature
  * `POST /{featureKey}/tenants/{tenantId}/configure` - Configure for tenant
  * `GET /{featureKey}/analytics` - Get usage analytics
  * `POST /bulk` - Bulk create/update features
* **User Features: `/api/v1/features/**` (Any authenticated user)**
  * `GET /available` - Get available features for current user
  * `POST /{featureKey}/complete` - Mark feature as completed
  * `GET /{featureKey}/status` - Check feature status

## **7\. Non-Functional Requirements (Implemented Baseline)**

* Stateless JWT-based admin auth with dual transport (HttpOnly cookie for browser sessions, bearer token for headless clients).  
* CSRF protection enabled for browser session mode with explicit token bootstrap endpoint.  
* Deterministic survey snapshot and scoring behavior.  
* Auditing on key lifecycle/auth configuration actions.  
* Tenant isolation enforced in both application access paths and DB constraints.  
* Error contract via centralized exception handling with business error codes.

## **8\. Validation and Error Handling**

Minimum business errors in code include:

* `INVALID_WEIGHT_SUM`  
* `CATEGORY_MAX_SCORE_ZERO`  
* `QUESTION_MAX_SCORE_INVALID`  
* `SURVEY_IMMUTABLE_AFTER_PUBLISH`  
* `INVALID_LIFECYCLE_TRANSITION`  
* `CAMPAIGN_NOT_ACTIVE`  
* `RESPONSE_QUOTA_EXCEEDED`  
* `QUOTA_EXCEEDED`  
* `SUBSCRIPTION_INACTIVE`  
* `ACCESS_DENIED`

## **9\. Acceptance Criteria (Implemented)**

* Tenant-scoped admin can create and manage questions, categories, surveys, and campaigns through the default frontend flow without manual scoring setup.  
* Survey draft create/update pins question/category versions; bank updates do not mutate pinned survey structure.  
* Published survey snapshot remains immutable for historical consistency and survey editing is disabled outside `DRAFT`.  
* Campaign activation fails for non-published survey.  
* Campaign activation auto-links default scoring profile from pinned survey category weights.  
* Public campaigns accept anonymous responders; private campaigns require valid tenant-authenticated responder credential (signed token or one-time access code).  
* Response submissions enforce runtime settings, compute weighted score when configured, and lock on submit.  
* Campaign close transition auto-locks open `IN_PROGRESS`/`REOPENED` responses (scheduled enforcement + immediate lock when `closeDate` is set in the past).  
* Tenant subscriptions can be checked out successfully through mock payment.  
* Super admin can update plan definitions and quota changes are enforced in runtime paths.  
* DB constraints reject cross-tenant relational mismatches.

## **10\. MVP Readiness Statement**

### **10.1 Current Maturity**

The authentication model and survey response access model are considered **MVP-ready** for pilot/beta use:

* Tenant-level responder auth configuration (no per-campaign auth duplication).
* Campaign access control supports both `PUBLIC` and `PRIVATE` modes.
* Survey draft pinning model protects runtime consistency against future question/category bank edits.
* Campaign activation auto-provisions default weighted scoring from pinned survey category weights.
* Private responder flows support:
  * Signed launch token validation with replay protection (`jti`)
  * OIDC authorization code flow with callback and one-time responder access code exchange
* Dynamic tenant claim mapping supports required/optional claims with fail-closed validation.
* Admin authentication includes access token + refresh token rotation.
* Auth configuration changes are audit logged.
* End-to-end test suite passes with these behaviors.
* **NEW: Enterprise Feature Management System with complete backend and frontend implementation.**
* **NEW: First-time user tracking with backend-persisted onboarding progress.**
* **NEW: Super admin UI for feature governance and tenant configuration.**
* **NEW: Multi-layer access control (global/tenant/plan/role/rollout).**
* **NEW: User progress tracking with cross-device synchronization.**

### **10.2 MVP Caveat**

This maturity level is suitable for MVP release and early tenant onboarding, but it is not a final enterprise-hardening endpoint.

## **11\. Known Gaps / Future Extensions**

* Replace mock payment gateway with production provider integration (webhooks, retry, reconciliation).
* Add quota dimensions (storage, API rate, monthly response caps, feature flags).
* Add richer analytics/reporting data products.
* Extend admin RBAC and policy packs for enterprise governance.
* Add dedicated theming management API surface.
* Add production-grade secret management and key lifecycle controls (KMS/Vault backed encryption, scheduled rotation, operational playbooks).
* Add auth abuse protections (rate limits, anomaly controls, lockout policy) on public/auth endpoints.
* Expand live IdP interoperability certification matrix and operational monitoring coverage.
* Add E2E tests for feature management flows.
* Add accessibility testing for guided help components.
* Add performance benchmarks for feature availability checks at scale.
