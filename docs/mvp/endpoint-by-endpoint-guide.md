# Survey Engine API Guide (Endpoint by Endpoint, Business-Friendly)

Date: March 4, 2026
Audience: Product owners, operations teams, customer onboarding, and non-technical stakeholders

## How to read this guide

Each endpoint answers 6 questions:
1. Why this endpoint is necessary.
2. Who uses it.
3. What the caller provides.
4. What the system does.
5. What the caller gets back.
6. Where it sits in the end-to-end flow.

---

## A) Admin Authentication Endpoints

### 1) `POST /api/v1/admin/auth/register`
- Why necessary: A new tenant admin needs an account to start using the platform.
- Who uses it: First admin user for a tenant.
- Caller provides: Name, email, password, confirmPassword.
- System does:
  1. Generates tenant ID server-side and provisions tenant.
  2. Creates admin account.
  3. Starts trial subscription (if not present).
  4. Sets `access_token` and `refresh_token` as HttpOnly, Secure, SameSite=Strict cookies.
- Caller gets back: Admin profile basics (userId, email, fullName, tenantId, role). **No tokens in response body.**
- Flow position: First step of tenant onboarding.

### 2) `POST /api/v1/admin/auth/login`
- Why necessary: Existing admin must securely enter the dashboard.
- Who uses it: Any active admin.
- Caller provides: Email and password.
- System does:
  1. Verifies credentials.
  2. Revokes old refresh tokens for safety.
  3. Sets new `access_token` and `refresh_token` as HttpOnly cookies.
- Caller gets back: User info (no tokens in body).
- Flow position: Start of daily admin operations.

### 3) `POST /api/v1/admin/auth/refresh`
- Why necessary: Keep user logged in without full re-login when access token expires.
- Who uses it: Browser session (automatic via interceptor on 401).
- Caller provides: Nothing explicitly — `refresh_token` cookie is sent automatically by browser.
- System does:
  1. Reads `refresh_token` cookie.
  2. Rotates (revokes old one, issues new pair as cookies).
- Caller gets back: User info + fresh cookies set.
- Flow position: Session continuity during dashboard usage.

### 3b) `POST /api/v1/admin/auth/logout`
- Why necessary: Securely end admin session and clear auth cookies.
- Who uses it: Any logged-in admin.
- Caller provides: Nothing — cookies are sent automatically.
- System does: Clears `access_token` and `refresh_token` cookies (sets maxAge=0).
- Caller gets back: 204 No Content.
- Flow position: Session termination.

### 3c) `GET /api/v1/admin/auth/me`
- Why necessary: Verify current session is valid and retrieve user info on page load.
- Who uses it: Frontend on app initialization / page refresh.
- Caller provides: Nothing — `access_token` cookie is sent automatically.
- System does: Parses JWT from cookie, returns user info from security context.
- Caller gets back: Current user profile (email, tenantId, role).
- Flow position: Session hydration after page refresh.

---

## B) Subscription and Plan Endpoints

### 4) `GET /api/v1/admin/subscriptions/me`
- Why necessary: Tenant must see current plan and billing status before using features.
- Who uses it: Tenant admin.
- Caller provides: Auth token only.
- System does: Reads tenant’s subscription state (plan, status, period).
- Caller gets back: Current subscription summary.
- Flow position: Billing dashboard and permission checks.

### 5) `POST /api/v1/admin/subscriptions/checkout`
- Why necessary: Tenant needs to activate/upgrade a plan.
- Who uses it: Tenant admin.
- Caller provides: Requested plan code.
- System does:
  1. Runs payment flow (currently mock success in MVP).
  2. Activates/updates tenant subscription.
  3. Stores payment transaction record.
- Caller gets back: Updated subscription status.
- Flow position: Commercial activation before scale usage.

### 6) `GET /api/v1/admin/plans`
- Why necessary: Show all available commercial plans and limits.
- Who uses it: Tenant admin and super admin.
- Caller provides: Auth token.
- System does: Fetches active plan catalog.
- Caller gets back: List of plans with quotas/features.
- Flow position: Plan selection and internal pricing operations.

### 7) `PUT /api/v1/admin/plans`
- Why necessary: Platform owner must adjust plan definitions over time.
- Who uses it: Super admin only.
- Caller provides: Plan code + quota/pricing rule updates.
- System does: Creates or updates plan definition.
- Caller gets back: Final saved plan definition.
- Flow position: SaaS governance and commercial control.

---

## C) Question Endpoints

### 8) `POST /api/v1/questions`
- Why necessary: Build reusable question library once.
- Who uses it: Content/admin teams.
- Caller provides: Question text, type, max score, and `optionConfig` (required for choice/rank types).
- System does:
  1. Creates mutable question-bank definition.
  2. Upserts live question version (`version_number = 1`).
  3. Validates `optionConfig` rules for choice/rank question types.
- Caller gets back: Created question record.
- Flow position: Survey content preparation.

### 9) `GET /api/v1/questions/{id}`
- Why necessary: View one question in detail for review/editing.
- Who uses it: Content/admin teams.
- Caller provides: Question ID.
- System does: Fetches tenant-scoped question by ID.
- Caller gets back: Full question details.
- Flow position: Question maintenance.

### 10) `GET /api/v1/questions`
- Why necessary: Browse active question bank quickly.
- Who uses it: Content/admin teams.
- Caller provides: Auth token.
- System does: Returns active tenant questions.
- Caller gets back: Question list.
- Flow position: Survey authoring pick-list.

### 11) `PUT /api/v1/questions/{id}`
- Why necessary: Improve or correct question text/config over time.
- Who uses it: Content/admin teams.
- Caller provides: Question ID + updated data.
- System does:
  1. Updates mutable question-bank definition.
  2. Updates live question version (`version_number = 1`) in place.
- Caller gets back: Updated question.
- Flow position: Ongoing content governance.

### 12) `DELETE /api/v1/questions/{id}`
- Why necessary: Retire questions without hard-deleting history.
- Who uses it: Content/admin teams.
- Caller provides: Question ID.
- System does: Soft-deactivates question.
- Caller gets back: No-content success.
- Flow position: Content cleanup lifecycle.

---

## D) Category Endpoints

### 13) `POST /api/v1/categories`
- Why necessary: Group questions into reusable dimensions/themes.
- Who uses it: Content/admin teams.
- Caller provides: Category title/metadata and mappings.
- System does:
  1. Creates mutable category-bank definition.
  2. Upserts live category version (`version_number = 1`) with live question-version mappings.
- Caller gets back: Created category details.
- Flow position: Survey structure planning.

### 14) `GET /api/v1/categories/{id}`
- Why necessary: Review one category and its mapped questions.
- Who uses it: Content/admin teams.
- Caller provides: Category ID.
- System does: Returns tenant-scoped category.
- Caller gets back: Category details.
- Flow position: Category maintenance.

### 15) `GET /api/v1/categories`
- Why necessary: View all active categories for reuse.
- Who uses it: Content/admin teams.
- Caller provides: Auth token.
- System does: Returns active category list.
- Caller gets back: Category list.
- Flow position: Survey builder source list.

### 16) `PUT /api/v1/categories/{id}`
- Why necessary: Keep category definitions current.
- Who uses it: Content/admin teams.
- Caller provides: Category ID + updated payload.
- System does:
  1. Updates mutable category-bank definition.
  2. Rewrites live category version (`version_number = 1`) mappings.
- Caller gets back: Updated category.
- Flow position: Controlled updates before/after releases.

### 17) `DELETE /api/v1/categories/{id}`
- Why necessary: Disable obsolete categories while retaining historical integrity.
- Who uses it: Content/admin teams.
- Caller provides: Category ID.
- System does: Soft-deactivates category.
- Caller gets back: No-content success.
- Flow position: Content retirement.

---

## E) Survey Endpoints

### 18) `POST /api/v1/surveys`
- Why necessary: Create a survey draft from prepared content.
- Who uses it: Survey managers.
- Caller provides: Survey title, pages, question references/settings.
- System does:
  1. Creates tenant-scoped survey in draft state.
  2. Pins immutable question/category versions for the draft structure.
- Caller gets back: Created survey data.
- Flow position: Start of survey lifecycle.

### 19) `GET /api/v1/surveys/{id}`
- Why necessary: Open one survey for review.
- Who uses it: Survey managers.
- Caller provides: Survey ID.
- System does: Reads survey by tenant and ID.
- Caller gets back: Survey details.
- Flow position: Quality review and editing.

### 20) `GET /api/v1/surveys`
- Why necessary: List all active surveys.
- Who uses it: Survey managers.
- Caller provides: Auth token.
- System does: Returns active surveys for tenant.
- Caller gets back: Survey list.
- Flow position: Dashboard browse/search.

### 21) `PUT /api/v1/surveys/{id}`
- Why necessary: Update draft survey before publishing.
- Who uses it: Survey managers.
- Caller provides: Survey ID + updated structure.
- System does:
  1. Allows update only while lifecycle state is `DRAFT`.
  2. Rebuilds pinned question/category versions for updated draft content.
- Caller gets back: Updated survey.
- Flow position: Draft refinement.

### 22) `DELETE /api/v1/surveys/{id}`
- Why necessary: Retire unused surveys safely.
- Who uses it: Survey managers.
- Caller provides: Survey ID.
- System does: Soft-deactivates survey.
- Caller gets back: No-content success.
- Flow position: Portfolio cleanup.

### 23) `POST /api/v1/surveys/{id}/lifecycle`
- Why necessary: Move survey through controlled states (publish, close, archive).
- Who uses it: Survey managers.
- Caller provides: Target lifecycle transition request.
- System does:
  1. Validates allowed transition.
  2. Creates immutable snapshot on `DRAFT -> PUBLISHED`.
  3. Requires reopen reason on `CLOSED -> PUBLISHED`.
- Caller gets back: Survey in new lifecycle state.
- Flow position: Governance gate before campaigns can go live.

---

## F) Campaign Endpoints

### 24) `POST /api/v1/campaigns`
- Why necessary: Turn a survey into a live distribution unit.
- Who uses it: Campaign managers.
- Caller provides: Survey reference + campaign name + access mode + basic config.
- System does: Creates tenant campaign in `DRAFT` status (not responder-active yet).
- Caller gets back: Campaign record.
- Flow position: Campaign planning.

### 25) `GET /api/v1/campaigns/{id}`
- Why necessary: View one campaign’s settings and status.
- Who uses it: Campaign managers.
- Caller provides: Campaign ID.
- System does: Returns campaign details.
- Caller gets back: Campaign data.
- Flow position: Monitoring and updates.

### 26) `GET /api/v1/campaigns`
- Why necessary: Track all active campaigns.
- Who uses it: Campaign managers.
- Caller provides: Auth token.
- System does: Returns active campaign list.
- Caller gets back: Campaign list.
- Flow position: Campaign operations dashboard.

### 27) `PUT /api/v1/campaigns/{id}`
- Why necessary: Update campaign metadata before or during run (as allowed).
- Who uses it: Campaign managers.
- Caller provides: Campaign ID + updated payload.
- System does: Applies valid updates.
- Caller gets back: Updated campaign.
- Flow position: Campaign maintenance.

### 28) `DELETE /api/v1/campaigns/{id}`
- Why necessary: Retire campaigns without data loss.
- Who uses it: Campaign managers.
- Caller provides: Campaign ID.
- System does: Soft-deactivates campaign.
- Caller gets back: No-content success.
- Flow position: Campaign archive/cleanup.

### 29) `PUT /api/v1/campaigns/{id}/settings`
- Why necessary: Configure live controls (quota, restrictions, close date, etc.).
- Who uses it: Campaign managers.
- Caller provides: Runtime settings object.
- System does:
  1. Saves campaign runtime behavior rules.
  2. If `closeDate` is already reached/past at update time, immediately auto-locks open responses (`IN_PROGRESS`/`REOPENED`) for that campaign.
- Caller gets back: Campaign with updated settings.
- Flow position: Pre-launch and in-flight control tuning.

### 29a) `GET /api/v1/campaigns/{id}/settings`
- Why necessary: Retrieve current campaign runtime settings before editing or auditing.
- Who uses it: Campaign managers.
- Caller provides: Campaign ID.
- System does: Returns persisted runtime settings and presentation flags.
- Caller gets back: Campaign settings payload.
- Flow position: Settings read/edit lifecycle.

### 29b) `GET /api/v1/campaigns/{id}/preview`
- Why necessary: Simulate responder experience for admins before rollout.
- Who uses it: Campaign managers and QA users.
- Caller provides: Campaign ID.
- System does: Builds preview payload from campaign + survey pages/questions + runtime settings.
- Caller gets back: Admin preview data model.
- Flow position: Pre-launch validation.

### 30) `POST /api/v1/campaigns/{id}/activate`
- Why necessary: Officially open campaign for responder participation.
- Who uses it: Campaign managers.
- Caller provides: Campaign ID.
- System does:
  1. Confirms linked survey is publish-ready.
  2. Links latest published survey snapshot to campaign.
  3. Auto-upserts campaign default weight profile from pinned survey category weights.
  4. Marks campaign active.
- Caller gets back: Activated campaign state.
- Flow position: Launch gate.

### 31) `POST /api/v1/campaigns/{id}/distribute`
- Why necessary: Generate participation channels/links in one action.
- Who uses it: Campaign managers.
- Caller provides: Campaign ID.
- System does: Creates distribution channel records.
- Caller gets back: Generated channel list.
- Flow position: Launch execution.

### 32) `GET /api/v1/campaigns/{id}/channels`
- Why necessary: Retrieve already generated sharing channels.
- Who uses it: Campaign managers.
- Caller provides: Campaign ID.
- System does: Fetches campaign channel records.
- Caller gets back: Distribution channel list.
- Flow position: Ongoing outreach and re-sharing.

### 32a) `GET /api/v1/public/campaigns/{id}/preview`
- Why necessary: Provide responder-facing form preview payload without admin auth.
- Who uses it: Public/private responder runtime UI.
- Caller provides: Campaign ID.
- System does: Returns preview for campaigns in active state; private/public responder access is enforced at submission/auth endpoints.
- Caller gets back: Public preview payload.
- Flow position: Responder form load.

---

## G) Responder Auth Configuration Endpoints

### 33) `POST /api/v1/auth/profiles`
- Why necessary: Set tenant-wide responder authentication policy once.
- Who uses it: Tenant admin.
- Caller provides: Auth mode + provider details + mappings + fallback policy.
- System does: Creates tenant auth profile.
- Caller gets back: Saved profile.
- Flow position: Private campaign prerequisite.

### 34) `PUT /api/v1/auth/profiles/{id}`
- Why necessary: Adjust auth settings as identity provider changes.
- Who uses it: Tenant admin.
- Caller provides: Profile ID + updated fields.
- System does:
  1. Updates config.
  2. Validates required claim rules.
  3. Writes audit trail.
- Caller gets back: Updated profile.
- Flow position: Ongoing identity operations.

### 35) `GET /api/v1/auth/profiles/tenant/{tenantId}`
- Why necessary: Confirm current auth setup for troubleshooting and governance.
- Who uses it: Tenant admin.
- Caller provides: Tenant ID.
- System does: Returns tenant’s active auth profile.
- Caller gets back: Full auth profile snapshot.
- Flow position: Support and operations checks.

### 36) `POST /api/v1/auth/profiles/{id}/rotate-key`
- Why necessary: Security hygiene for signing keys/versions.
- Who uses it: Tenant admin/security admin.
- Caller provides: Profile ID.
- System does: Increments active key version and audits action.
- Caller gets back: Profile with new key version.
- Flow position: Security maintenance.

### 37) `GET /api/v1/auth/profiles/{id}/audit`
- Why necessary: Track who changed auth settings and when.
- Who uses it: Tenant admin, audit/compliance team.
- Caller provides: Profile ID.
- System does: Returns change history in reverse time order.
- Caller gets back: Auth config audit records.
- Flow position: Compliance and incident investigation.

### 38) `GET /api/v1/auth/providers/templates`
- Why necessary: Faster onboarding with known IdP setup templates.
- Who uses it: Tenant admin/onboarding team.
- Caller provides: Auth token.
- System does: Returns supported provider templates.
- Caller gets back: Template list (defaults + required fields).
- Flow position: Initial setup assistance.

### 39) `GET /api/v1/auth/providers/templates/{providerCode}`
- Why necessary: View one provider’s exact expected setup model.
- Who uses it: Tenant admin/onboarding team.
- Caller provides: Provider code (`OKTA`, `AUTH0`, `AZURE_AD`, `KEYCLOAK`).
- System does: Returns provider-specific template details.
- Caller gets back: One provider template.
- Flow position: Configuration form prefill/reference.

### 40) `POST /api/v1/auth/validate/{tenantId}`
- Why necessary: Validate responder token using tenant rules.
- Who uses it: Responder flow backend/integration checks.
- Caller provides: Tenant ID + raw token string in request body.
- System does:
  1. Loads tenant auth mode/profile.
  2. Validates token and required claim mappings.
  3. Applies configured fallback policy when validation fails.
- Caller gets back: Validation result and mapped identity fields.
- Flow position: Trust decision in private responder access.

---

## H) OIDC Responder Flow Endpoints

### 41) `POST /api/v1/auth/respondent/oidc/start`
- Why necessary: Start enterprise SSO login for private campaign responder.
- Who uses it: Responder app/frontend.
- Caller provides: Tenant ID, campaign ID, optional return path.
- System does:
  1. Confirms private campaign + tenant auth policy.
  2. Creates secure state/nonce.
  3. Builds identity provider authorization URL.
- Caller gets back: Authorization URL and expiry metadata.
- Flow position: First step of private OIDC responder login.

### 42) `GET /api/v1/auth/respondent/oidc/callback`
- Why necessary: Finish SSO handshake after identity provider login.
- Who uses it: Identity provider redirects here; frontend follows result.
- Caller provides: Authorization `code` and `state`.
- System does:
  1. Validates state and expiry.
  2. Exchanges code for token.
  3. Validates token using tenant claim rules.
  4. Issues short-lived one-time responder access code.
- Caller gets back: JSON callback payload or HTTP redirect containing `auth_code` (when return path is configured).
- Flow position: Final step before response submission.

---

## I) Response Endpoints

### 43) `POST /api/v1/responses`
- Why necessary: Collect responder answers (core business outcome).
- Who uses it: Responders.
- Caller provides:
  1. Campaign ID.
  2. Answers.
  3. For private campaign: `responderToken` or one-time `responderAccessCode`.
- System does:
  1. Validates campaign active and runtime rules.
  2. Enforces public/private access requirement.
  3. Validates answers against pinned survey snapshot question/category versions.
  4. Auto-calculates weighted score when campaign default profile exists.
  5. Stores response and locks it.
  6. Close-time lifecycle enforcement (background) separately locks any remaining `IN_PROGRESS`/`REOPENED` responses once campaign close date is reached.
- Caller gets back: Saved response summary.
- Flow position: Main data capture endpoint.

### 44) `GET /api/v1/responses/{id}`
- Why necessary: View one stored response for review.
- Who uses it: Admin teams.
- Caller provides: Response ID.
- System does: Returns response details.
- Caller gets back: Response object.
- Flow position: QA, support, and review.

### 45) `GET /api/v1/responses/campaign/{campaignId}`
- Why necessary: Inspect all responses for one campaign.
- Who uses it: Admin teams.
- Caller provides: Campaign ID.
- System does: Fetches campaign responses under tenant boundary.
- Caller gets back: Response list.
- Flow position: Operations and reporting.

### 46) `POST /api/v1/responses/{id}/lock`
- Why necessary: Protect finalized response from further edits.
- Who uses it: Admin teams (when manual lock action needed).
- Caller provides: Response ID.
- System does: Marks response locked.
- Caller gets back: Updated locked response.
- Flow position: Data integrity control.

### 47) `POST /api/v1/responses/{id}/reopen`
- Why necessary: Allow correction in controlled exceptional cases.
- Who uses it: Admin teams.
- Caller provides: Response ID + reopen reason/window.
- System does: Reopens locked response with audit-safe handling (note: if campaign close date has already passed, background close-time enforcement locks it again).
- Caller gets back: Reopened response status.
- Flow position: Exception management.

### 48) `GET /api/v1/responses/analytics/{campaignId}`
- Why necessary: See campaign health without exporting raw data.
- Who uses it: Campaign managers.
- Caller provides: Campaign ID.
- System does: Aggregates key campaign response metrics.
- Caller gets back: Analytics summary.
- Flow position: Monitoring and decision support.

---

## J) Scoring Endpoints

Note: In the simplified MVP runtime flow, scoring is executed automatically during `POST /api/v1/responses`. Endpoints below remain available for advanced/manual operations.

### 49) `POST /api/v1/scoring/profiles`
- Why necessary: Define weighted scoring rules for a campaign.
- Who uses it: Analytics/admin teams.
- Caller provides: Campaign-linked weight model.
- System does: Creates scoring profile.
- Caller gets back: Created profile.
- Flow position: Pre-reporting setup.

### 50) `GET /api/v1/scoring/profiles/{id}`
- Why necessary: View one scoring profile.
- Who uses it: Analytics/admin teams.
- Caller provides: Profile ID.
- System does: Returns profile details.
- Caller gets back: Profile object.
- Flow position: Validation and maintenance.

### 51) `GET /api/v1/scoring/profiles/campaign/{campaignId}`
- Why necessary: See all scoring profiles for campaign context.
- Who uses it: Analytics/admin teams.
- Caller provides: Campaign ID.
- System does: Returns campaign-linked profiles.
- Caller gets back: Profile list.
- Flow position: Analysis setup review.

### 52) `PUT /api/v1/scoring/profiles/{id}`
- Why necessary: Update scoring logic when business rubric changes.
- Who uses it: Analytics/admin teams.
- Caller provides: Profile ID + updated weights.
- System does: Saves updated profile.
- Caller gets back: Updated profile.
- Flow position: Scoring governance.

### 53) `DELETE /api/v1/scoring/profiles/{id}`
- Why necessary: Retire obsolete scoring models.
- Who uses it: Analytics/admin teams.
- Caller provides: Profile ID.
- System does: Deactivates profile.
- Caller gets back: No-content success.
- Flow position: Model lifecycle management.

### 54) `POST /api/v1/scoring/profiles/{id}/validate`
- Why necessary: Ensure profile is mathematically valid before use.
- Who uses it: Analytics/admin teams.
- Caller provides: Profile ID.
- System does: Checks weight sum and related constraints.
- Caller gets back: Success/failure result.
- Flow position: Pre-calculation safety check.

### 55) `POST /api/v1/scoring/calculate/{profileId}`
- Why necessary: Produce final weighted score from category scores.
- Who uses it: Analytics/reporting workflows.
- Caller provides: Profile ID + raw category scores.
- System does: Applies weighted formula and returns result.
- Caller gets back: Score result object.
- Flow position: Reporting and outcome generation.

---

## K) Endpoint Access Summary (Business View)

Public responder-facing endpoints:
1. `POST /api/v1/responses`
2. `POST /api/v1/auth/validate/{tenantId}`
3. `POST /api/v1/auth/respondent/oidc/start`
4. `GET /api/v1/auth/respondent/oidc/callback`
5. `GET /api/v1/public/campaigns/{id}/preview`

Admin-auth required endpoints:
1. All question/category/survey/campaign/scoring management endpoints.
2. Auth profile management and provider-template endpoints (`/api/v1/auth/profiles/**`, `/api/v1/auth/providers/templates/**`).
3. Subscription and plan endpoints (with super-admin restriction for plan update).

## L) Data Flow Path (Simple View)

1. Admin enters system -> auth cookies set -> configures content/campaign/auth.
2. Campaign goes active -> responder accesses campaign path.
3. If public -> responder submits directly.
4. If private -> responder identity is validated (OIDC flow or signed token).
5. Response is validated, scored (default flow), and locked -> analytics/scoring/reporting consume this data.
6. Subscription and plan checks run in parallel to enforce SaaS limits.
