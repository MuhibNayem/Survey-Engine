# **Software Requirements Specification (SRS)**

## **Survey Engine MVP (Implemented Baseline)**

| Field | Value |
| ----- | ----- |
| Document Title | Survey Engine MVP SRS |
| Version | 2.0 |
| Date | March 3, 2026 |
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
* Question bank and category CRUD with version snapshots.  
* Survey CRUD with lifecycle transitions and immutable published snapshots.  
* Campaign CRUD, runtime settings, activation, and distribution channel generation.  
* Responder submission, response locking, reopen workflow, and basic analytics.  
* Campaign access mode (`PUBLIC`/`PRIVATE`) with tenant-level external auth profile for private responder access.  
* Weighted scoring with profile validation and deterministic calculation.  
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
* **Category**: Grouping of questions with versioned mappings.  
* **Weight Profile**: Category weight model used for score computation.  
* **Auth Profile**: Tenant-level respondent auth trust configuration.  
* **Subscription**: Tenant billing state and active plan assignment.  
* **Plan Definition**: Super-admin managed pricing/quota configuration for plan codes.

## **4\. Functional Requirements**

### **4.1 Admin Authentication and Authorization**

* Engine issues admin JWT access tokens and refresh tokens.  
* Supported admin auth endpoints: register, login, refresh.  
* JWT contains tenant and role claims and populates request tenant context.  
* API authorization is role-aware; plan update endpoint requires `SUPER_ADMIN`.

### **4.2 Multi-Tenancy and Data Isolation**

* Core entities are tenant-owned via `tenant_id`: question, category, survey, campaign, weight_profile, auth_profile, survey_response, admin_user.  
* Tenant ownership is enforced in service/repository access patterns for admin operations.  
* DB-level tenant integrity is enforced with foreign keys to `tenant` and tenant-consistent composite FKs on major relationships.  
* Cross-tenant access is rejected for protected admin operations.

### **4.3 Question Bank and Category Model**

* Create, read, update, deactivate questions.  
* Create, read, update, deactivate categories.  
* Question and category version snapshots are generated on create/update.  
* Category mappings reference versioned question snapshots.

### **4.4 Survey Builder and Lifecycle**

* Create, read, update, deactivate surveys with pages/questions.  
* Lifecycle transitions supported:
  * `DRAFT -> PUBLISHED`
  * `PUBLISHED -> CLOSED`
  * `CLOSED -> RESULTS_PUBLISHED`
  * `RESULTS_PUBLISHED -> ARCHIVED`
  * `CLOSED -> PUBLISHED` (reopen with reason)
* Publishing creates immutable survey snapshot data.  
* Structural modifications are blocked after publish.

### **4.5 Campaign Management and Access Modes**

* Create, read, update, deactivate campaigns.  
* Activate campaign only when linked survey is `PUBLISHED`.  
* Campaign access mode is:
  * `PUBLIC`: responder auth token not required.
  * `PRIVATE`: responder token required and validated through tenant auth profile.
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
  * valid responder token
  * anonymous fallback is rejected for private access.

#### **4.8.5 Signed Launch Token Operational Model**

* Subscriber backend must generate and sign a launch token after authenticating its own user.  
* Subscriber must configure tenant signing key/secret in its backend runtime and keep it private.  
* Survey Engine validates signature and claims before accepting response submission.  
* This model is intended to support legacy/custom enterprise identity systems without requiring Survey Engine to implement those protocols directly.

#### **4.8.6 Security Hardening Requirements (Production)**

* Token transport should avoid long-lived bearer tokens in query strings; prefer short-lived exchange code or secure POST/header transport where possible.  
* Replay protection is mandatory (`jti`/nonce + server-side replay cache/store).  
* Strict claim validation is mandatory (issuer, audience, expiry, clock skew, subject/email mapping, tenant binding).  
* Signing key lifecycle must support rotation with key identifiers (`kid`) and dual-key validation windows.  
* Auth failures must return deterministic error codes and be audit logged.
* Claim mapping/profile updates are audit logged with before/after snapshots.

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

### **4.9 Responses, Locking, and Analytics**

* Responder can submit to active campaign endpoint.  
* On successful submit, response is auto-locked.  
* Admin can lock/reopen responses; reopen is audited with reason/window.  
* Analytics endpoint returns totals and completion-related counts per campaign.

### **4.10 Scoring and Weighting**

* Create/update/deactivate weight profiles per campaign.  
* Validate profile category weights must total exactly 100%.  
* Score calculation uses normalized category scoring and weighted aggregation.

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

## **6\. API Surface (Implemented)**

* Admin auth: `/api/v1/admin/auth/**`  
* Plan management: `/api/v1/admin/plans` (PUT requires `SUPER_ADMIN`)  
* Subscription: `/api/v1/admin/subscriptions/me`, `/api/v1/admin/subscriptions/checkout`  
* Question bank: `/api/v1/questions/**`, `/api/v1/categories/**`  
* Surveys: `/api/v1/surveys/**`  
* Campaigns: `/api/v1/campaigns/**`  
* Scoring: `/api/v1/scoring/**`  
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

* Tenant-scoped admin can create and manage questions, categories, surveys, campaigns, and scoring profiles.  
* Published survey snapshot remains immutable for historical consistency.  
* Campaign activation fails for non-published survey.  
* Public campaigns accept anonymous responders; private campaigns require valid tenant-authenticated responder token.  
* Response submissions enforce runtime settings and lock on submit.  
* Tenant subscriptions can be checked out successfully through mock payment.  
* Super admin can update plan definitions and quota changes are enforced in runtime paths.  
* DB constraints reject cross-tenant relational mismatches.

## **10\. MVP Readiness Statement**

### **10.1 Current Maturity**

The authentication model and survey response access model are considered **MVP-ready** for pilot/beta use:

* Tenant-level responder auth configuration (no per-campaign auth duplication).  
* Campaign access control supports both `PUBLIC` and `PRIVATE` modes.  
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
