# **Software Requirements Specification (SRS)**

## **Survey Engine MVP (Implemented Baseline)**

| Field | Value |
| ----- | ----- |
| Document Title | Survey Engine MVP SRS |
| Version | 2.1 |
| Date | March 4, 2026 |
| Prepared For | Product and Engineering |
| Classification | Internal |
| Status | Reflects implemented code and schema |

## **1\. Purpose**

Define the implemented MVP requirements for a multi-tenant Survey Engine that supports survey authoring, campaign distribution, response collection, scoring, tenant-scoped authentication, and SaaS subscription/plan controls.

## **2\. Scope**

### **2.1 In Scope (Implemented)**

* Multi-tenant data model with tenant isolation in service/repository layer and DB constraints.  
* Engine-owned admin authentication (register/login/refresh tokens).  
* Admin RBAC using JWT roles (`SUPER_ADMIN`, `ADMIN`, `EDITOR`, `VIEWER`).  
* Question bank and category CRUD with live mutable bank definitions.  
* Survey CRUD with draft-time pinning, lifecycle transitions, and immutable published snapshots.  
* Campaign CRUD, runtime settings, activation, and distribution channel generation.  
* Responder submission, response locking, reopen workflow, and basic analytics.  
* Campaign access mode (`PUBLIC`/`PRIVATE`) with tenant-level external auth profile for private responder access.  
* Automated campaign scoring using category weights pinned from survey structure.  
* Optional manual scoring profile APIs retained for advanced/administrative use (not part of default frontend flow).  
* SaaS subscription domain (tenant subscription, plan catalog, mock payment success flow).  
* Super-admin plan catalog management and tenant plan quota enforcement.

### **2.2 Out of Scope (Current Codebase)**

* Real payment gateway integration (currently mock-success gateway).  
* Per-feature usage metering beyond implemented quotas (admin users, active campaigns, responses per campaign).  
* Advanced analytics (time-series traffic dashboards, segmentation UI, BI pipelines).  
* Full enterprise permission matrix beyond role-based API controls.  
* Public API for theme template lifecycle (theme tables exist but dedicated theming controller is not implemented).  
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
* **Subscription**: Tenant billing state and active plan assignment.  
* **Plan Definition**: Super-admin managed pricing/quota configuration for plan codes.

## **4\. Functional Requirements**

### **4.1 Admin Authentication and Authorization**

* Engine issues admin JWT access tokens and refresh tokens.  
* Supported admin auth endpoints: register, login, refresh.  
* JWT contains tenant and role claims and populates request tenant context.  
* API authorization baseline requires authenticated admin JWT for protected admin APIs; plan update endpoint requires `SUPER_ADMIN`.

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
  * Optional metadata capture flags
* Runtime checks are enforced during response submission.
* Close-time locking behavior:
  * At/after `closeDate`, system automatically locks open responses in `IN_PROGRESS` or `REOPENED`.
  * If settings are updated with `closeDate <= now`, locking is triggered immediately.

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

* Private campaign submission requires:
  * campaign `AuthMode=PRIVATE`
  * tenant auth profile present
  * auth profile mode not `PUBLIC_ANONYMOUS`
  * valid responder credential (`responderToken` or one-time `responderAccessCode`)
  * anonymous fallback is rejected for private access.

#### **4.8.5 Signed Launch Token Operational Model**

* Subscriber backend must generate and sign a launch token after authenticating its own user.  
* Subscriber must configure tenant signing key/secret in its backend runtime and keep it private.  
* Survey Engine validates signature and claims before accepting response submission.  
* This model is intended to support legacy/custom enterprise identity systems without requiring Survey Engine to implement those protocols directly.

#### **4.8.6 Security Hardening Requirements (MVP Baseline + Production Targets)**

Implemented baseline:
* Token transport avoids long-lived responder tokens in URLs for OIDC private flows by using one-time responder access code exchange.  
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

* Responder can submit to active campaign endpoint.  
* On successful submit, response is auto-locked.  
* Admin can lock/reopen responses; reopen is audited with reason/window.  
* Close transition enforcement auto-locks any still-open (`IN_PROGRESS`/`REOPENED`) responses for expired campaigns.  
* Analytics endpoint returns totals and completion-related counts per campaign.

### **4.10 Scoring and Weighting**

* Default scoring profile is automatically created/updated at campaign activation from pinned survey category weights (`campaign.default_weight_profile_id`).  
* Response submission automatically computes and persists weighted score when campaign has default profile (`survey_response.weighted_total_score`, `weight_profile_id`, `scored_at`).  
* Score calculation uses deterministic category aggregation from validated answers and normalized weighted scoring.  
* Manual scoring profile endpoints remain available (`/api/v1/scoring/**`) for non-default use cases, but MVP frontend uses the simplified automatic flow.

### **4.11 SaaS Subscription and Plans**

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
* Plan management: `/api/v1/admin/plans` (PUT requires `SUPER_ADMIN`)  
* Subscription: `/api/v1/admin/subscriptions/me`, `/api/v1/admin/subscriptions/checkout`  
* Question bank: `/api/v1/questions/**`, `/api/v1/categories/**`  
* Surveys: `/api/v1/surveys/**`  
* Campaigns: `/api/v1/campaigns/**`  
* Scoring: `/api/v1/scoring/**`  
  * Note: scoring APIs are implemented, but the default MVP frontend flow no longer exposes a dedicated `/scoring` route.
* Auth profiles/validation: `/api/v1/auth/**`
  * Includes provider template endpoints for onboarding UI.
* Responses: `/api/v1/responses/**` and public submit `/api/v1/responses`

## **7\. Non-Functional Requirements (Implemented Baseline)**

* Stateless JWT-based admin auth.  
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
