# Survey Engine MVP Lifecycle Flows (Step-by-Step Guide)
## Product: Headless Multi-Tenant Survey Engine (MVP)
## Version: 1.2
## Date: March 9, 2026

## Purpose
This document explains each MVP flow in execution order with practical guidance:

1. What to create first.
2. What to create next.
3. Why each step is necessary.
4. How each step is done (including API paths).

This is intentionally operational and sequence-driven for product, onboarding, and implementation teams.

---

## Table of Contents
1. Tenant Bootstrap and Admin Access Flow
2. Subscription and Plan Flow
3. Question and Category Lifecycle Flow
4. Survey Lifecycle Flow
5. Campaign Lifecycle Flow
6. Tenant Auth Profile Lifecycle Flow
7. Private Responder OIDC Flow
8. Private Responder Signed Token Flow
9. Response Lifecycle Flow
10. Automated Scoring Lifecycle Flow (Default)
11. End-to-End Recommended Build Order
12. Explicit Tagging Chain (Question -> Category -> Survey -> Campaign)
13. Out-of-MVP Lifecycle Items
14. Super-Admin Tenant Operations Flow
15. Audit Log Query Flow

---

## 1. Tenant Bootstrap and Admin Access Flow

### Goal
Bring a new tenant from zero state to authenticated admin access.

### Step-by-step

1. Create admin account (`POST /api/v1/admin/auth/register` for browser mode or `POST /api/v1/admin/auth/token/register` for headless mode).
- Why: This creates the first trusted operator account.
- How: Send full name, email, password, confirm password. Tenant ID is generated server-side.
- Result:
  - Browser mode: tenant is provisioned, trial subscription is initialized, auth cookies are set, and user profile payload is returned.
  - Token mode: tenant is provisioned, trial subscription is initialized, and explicit token pair payload is returned.

2. Establish authenticated admin session.
- Why: Protected admin APIs require authenticated context.
- How:
  - Browser mode: cookies carry tokens automatically.
  - Headless mode: send bearer token from token-mode login/register response.
- Result: Admin can call protected endpoints.

3. Start normal login cycle (`POST /api/v1/admin/auth/login` for browser mode or `POST /api/v1/admin/auth/token/login` for token mode).
- Why: Ongoing operations should not rely on registration response tokens.
- How: Login with email/password.
- Result:
  - Browser mode: refreshed auth cookies + user payload.
  - Token mode: fresh access + refresh token pair.

4. Rotate session using refresh (`POST /api/v1/admin/auth/refresh` for browser mode or `POST /api/v1/admin/auth/token/refresh` for token mode).
- Why: Access token is short-lived by design.
- How:
  - Browser mode: refresh token is read from cookie.
  - Token mode: send refresh token in request payload.
- Result: New token state is issued and old refresh token is revoked.

5. (Browser mode) Bootstrap CSRF token (`GET /api/v1/admin/auth/csrf`) before state-changing admin calls.
- Why: CSRF protection is enabled for cookie-authenticated browser requests.
- How: Read `XSRF-TOKEN` cookie / csrf token endpoint and include token in mutation requests.
- Result: POST/PUT/DELETE admin flows pass CSRF checks.

### Key implementation intent
- Tenant context is carried in JWT claims and used throughout service authorization and data scoping.

---

## 2. Subscription and Plan Flow

### Goal
Ensure tenant has active entitlement and system-enforced usage limits.

### Step-by-step

1. Read current subscription (`GET /api/v1/admin/subscriptions/me`).
- Why: Admin must know current plan and status before setup decisions.
- How: Call endpoint with authenticated admin session (browser cookies or bearer token).
- Result: Current plan, status, and period details.

2. Review available plans (`GET /api/v1/admin/plans`).
- Why: Tenant chooses what limits/features apply.
- How: Fetch active plan catalog.
- Result: Plan list with quota definitions.

3. Activate/upgrade plan (`POST /api/v1/admin/subscriptions/checkout`).
- Why: Required to unlock higher quotas or continue usage outside trial.
- How: Submit selected plan code.
- Result: Subscription state updates (mock payment success in MVP).

4. Enforce plan constraints at runtime (automatic).
- Why: SaaS fairness and commercial control.
- How: Filters/services check subscription and quotas on admin flows.
- Result: Non-compliant requests blocked (e.g., 402 for inactive subscription, quota errors where applicable).

5. Super-admin updates plan definitions (`PUT /api/v1/admin/plans`) when business needs change.
- Why: Commercial model evolves over time.
- How: Super-admin sends upsert payload.
- Result: Future tenant behavior follows updated plan definitions.

---

## 3. Question and Category Lifecycle Flow

### Goal
Create reusable building blocks before survey design.

### Recommended creation order
1. Questions first.
2. Categories second.

### Step-by-step

1. Create question (`POST /api/v1/questions`).
- Why: Categories and surveys depend on question definitions.
- How: Send `text`, `type`, `maxScore`, and `optionConfig` when type requires options.
- Result: Question created with live bank version (`version_number = 1`).

2. List and review active questions (`GET /api/v1/questions`, `GET /api/v1/questions/{id}`).
- Why: Validate content quality before categorization.
- How: Retrieve list and details.
- Result: Curated question pool.

3. Update question if needed (`PUT /api/v1/questions/{id}`).
- Why: Fix wording/scoring issues early.
- How: Submit revised payload.
- Result: Updated live bank definition (still version `1` in bank lifecycle).

4. Create category (`POST /api/v1/categories`).
- Why: Group related questions for structure and scoring alignment.
- How: Create category metadata and mappings.
- Result: Category created with live bank version (`version_number = 1`).

5. Verify categories (`GET /api/v1/categories`, `GET /api/v1/categories/{id}`).
- Why: Confirm mappings before survey assembly.
- How: Fetch category list and detail.
- Result: Ready-to-use category catalog.

6. Deactivate obsolete items (`DELETE /api/v1/questions/{id}`, `DELETE /api/v1/categories/{id}`).
- Why: Keep active catalog clean without deleting historical context.
- How: Soft deactivate.
- Result: No longer appears as active, audit/history integrity remains.

### Exact tagging instructions in this flow

Question -> Category tag (how it is created):
1. Create question first (`POST /api/v1/questions`).
2. Create category with `questionMappings` (`POST /api/v1/categories`).
3. For each mapping item, provide:
- `questionId` (required)
- `sortOrder` (required)
- `weight` (optional)
4. System behavior:
- Backend resolves and stores live `questionVersionId` automatically for each mapped `questionId`.
- Updating question/category bank later does not retroactively modify already-pinned survey copies.

### Option ownership rule (explicit)

1. Choice options belong to question bank `optionConfig`.
2. `optionConfig.options` is required for:
- `SINGLE_CHOICE`
- `MULTIPLE_CHOICE`
- `RANK`
3. Survey-level `answerConfig` does not own option lists in default model.

Example category payload:
```json
{
  "name": "Teaching Quality",
  "description": "Questions about instructor quality",
  "questionMappings": [
    {
      "questionId": "11111111-1111-1111-1111-111111111111",
      "sortOrder": 1,
      "weight": 40.0
    },
    {
      "questionId": "22222222-2222-2222-2222-222222222222",
      "sortOrder": 2,
      "weight": 60.0
    }
  ]
}
```

---

## 4. Survey Lifecycle Flow

### Goal
Move survey from draft to publish-ready state, then through closure states.

### Recommended creation order
1. Build question/category foundation.
2. Create survey draft.
3. Refine draft.
4. Publish.
5. Manage lifecycle transitions as campaign needs evolve.

### Step-by-step

1. Create survey draft (`POST /api/v1/surveys`).
- Why: Survey is the template container for campaign execution.
- How: Provide title, structure, page/question configuration.
- Result: Survey in draft state and pinned question/category versions are created for that draft.

2. Inspect draft (`GET /api/v1/surveys/{id}` and list endpoint).
- Why: Validate structure before publication.
- How: Retrieve survey content and status.
- Result: Confirmed draft readiness.

3. Refine pinned copies in Survey Details (draft only).
- Why: Final wording/score/category-label adjustments should be survey-specific, not bank-wide.
- How: Edit pinned fields in draft payload:
  - per question: `pinnedQuestionText`, `pinnedQuestionMaxScore`, `pinnedQuestionOptionConfig`
  - per category: `pinnedCategoryName`, `pinnedCategoryDescription`
- Result: Only survey-pinned versions are updated; question/category bank definitions remain unchanged.

4. Update draft (`PUT /api/v1/surveys/{id}`).
- Why: Apply final edits before publish.
- How: Submit updated survey schema and pinned override fields.
- Result: Draft structure and pinned version set are rebuilt from current draft payload.

5. Transition lifecycle (`POST /api/v1/surveys/{id}/lifecycle`).
- Why: Campaign activation requires published survey.
- How: Request valid transition (e.g., DRAFT -> PUBLISHED).
- Result: Survey state changes when transition is valid; publish snapshots the already-pinned draft.

6. Preserve immutability after publish (enforced behavior).
- Why: Response and campaign consistency require stable published structure.
- How: System blocks `POST/PUT` structural edits in non-`DRAFT` states.
- Result: Published snapshot integrity and read-only Survey Details for pinned copies.

7. Move through end states when appropriate.
- Why: Operational clarity (closed/results/archive) and governance.
- How: Use lifecycle transition endpoint.
- Result: Predictable survey state management.

### Exact tagging instructions in this flow

Category -> Survey tag (how it is created):
1. While building survey pages/questions, provide `categoryId` on each survey question item.
2. Required/optional fields per survey question item:
- `questionId` (required)
- `sortOrder` (required)
- `categoryId` (optional but required if you want category tagging in survey)
- `categoryWeightPercentage` (required when `categoryId` is set)
- `mandatory` (optional)
- `answerConfig` (optional)
3. System behavior:
- Backend resolves and stores pinned `questionVersionId` from the current question bank state.
- Backend resolves and stores pinned `categoryVersionId` when `categoryId` is set.
- Category mappings are not auto-injected into pages; explicit placement is required.
- For each category in a survey draft:
  - all tagged questions must carry the same `categoryWeightPercentage`
  - total distinct category weights across survey must be exactly `100.00`

### `optionConfig` vs `answerConfig` (explicit separation)

1. `optionConfig` (question bank):
- Defines selectable options and per-option score metadata.
- Used by response validator to whitelist `SINGLE_CHOICE` / `MULTIPLE_CHOICE` / `RANK` values.

2. `answerConfig` (survey question):
- Optional per-survey behavior tuning.
- Examples: `minSelections`, `maxSelections`, `step`, `allowPartialRanking`, `correctOrder`.
- Must be JSON object when provided.

Example survey payload:
```json
{
  "title": "Midterm Course Evaluation",
  "description": "Student feedback survey",
  "pages": [
    {
      "title": "Section A",
      "sortOrder": 1,
      "questions": [
        {
          "questionId": "11111111-1111-1111-1111-111111111111",
          "categoryId": "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa",
          "categoryWeightPercentage": 60.0,
          "sortOrder": 1,
          "mandatory": true,
          "answerConfig": "{\"minSelections\":1,\"maxSelections\":1}"
        },
        {
          "questionId": "22222222-2222-2222-2222-222222222222",
          "categoryId": "bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb",
          "categoryWeightPercentage": 40.0,
          "sortOrder": 2,
          "mandatory": false
        }
      ]
    }
  ]
}
```

---

## 5. Campaign Lifecycle Flow

### Goal
Create a live delivery instance from a published survey and control runtime behavior.

### Recommended creation order
1. Ensure survey is published.
2. Create campaign.
3. Configure runtime settings.
4. Activate.
5. Generate and use distribution channels.

### Step-by-step

1. Create campaign (`POST /api/v1/campaigns`).
- Why: Campaign represents the live response-collection window.
- How: Provide name, surveyId, and `authMode` (`PUBLIC` or `PRIVATE`).
- Result: Campaign created in non-active state.

2. Review campaign (`GET /api/v1/campaigns/{id}` and list endpoint).
- Why: Confirm survey binding, auth mode, and metadata.
- How: Fetch campaign details.
- Result: Campaign ready for settings update.

2a. Retrieve responder-safe preview (`GET /api/v1/public/campaigns/{id}/preview`) when building public-facing form experience.
- Why: Public responders should not depend on admin-protected campaign endpoints.
- How: Call public preview endpoint by campaign id.
- Result: Responder-facing survey/campaign preview payload.

3. Configure runtime settings (`PUT /api/v1/campaigns/{id}/settings`).
- Why: Set operational controls before launch.
- How: Configure quota, restrictions, close time, UX controls, etc.
- Result: Runtime enforcement rules stored. If `closeDate <= now`, system immediately auto-locks open campaign responses in `IN_PROGRESS`/`REOPENED`.

4. Activate campaign (`POST /api/v1/campaigns/{id}/activate`).
- Why: Only active campaigns accept submissions.
- How: Activation call checks linked survey lifecycle constraints.
- Result: Campaign moves to active state if preconditions pass; latest survey snapshot is linked and default scoring profile is auto-upserted.

5. Generate distribution channels (`POST /api/v1/campaigns/{id}/distribute`).
- Why: Operations teams need shareable channel assets.
- How: Generate links/embed/email templates.
- Result: Distribution channel records created.

6. Retrieve channels (`GET /api/v1/campaigns/{id}/channels`).
- Why: Reuse and operational visibility.
- How: Fetch channel set for active campaign.
- Result: Ready-to-share channel inventory.

7. Deactivate campaign when needed (`DELETE /api/v1/campaigns/{id}`).
- Why: Retire campaign without hard deleting history.
- How: Soft deactivation.
- Result: Campaign removed from active operations.

### Exact tagging instructions in this flow

Survey -> Campaign tag (how it is created):
1. Create campaign with `surveyId` (`POST /api/v1/campaigns`).
2. Required/optional fields:
- `name` (required)
- `surveyId` (required)
- `description` (optional)
- `authMode` (`PUBLIC` or `PRIVATE`, optional; defaults as per backend logic)
3. Activate campaign (`POST /api/v1/campaigns/{id}/activate`).
4. System behavior during activation:
- Backend verifies survey is in `PUBLISHED` state.
- Backend links campaign to latest survey snapshot (`surveySnapshotId`) automatically.
- Backend creates/updates campaign default weight profile from pinned survey category weights and stores `defaultWeightProfileId`.
- Campaign then accepts responses only when active.

Example campaign payload:
```json
{
  "name": "Spring 2026 CS101 Campaign",
  "description": "Main collection window",
  "surveyId": "33333333-3333-3333-3333-333333333333",
  "authMode": "PRIVATE"
}
```

---

## 6. Tenant Auth Profile Lifecycle Flow

### Goal
Configure responder authentication once per tenant so private campaigns can enforce identity trust.

### Recommended creation order
1. Decide private auth strategy (OIDC or signed token fallback).
2. Create tenant auth profile.
3. Validate mappings and profile retrieval.
4. Use profile for private campaign execution.

### Step-by-step

1. Choose auth mode:
- `EXTERNAL_SSO_TRUST` for OIDC/JWT provider path.
- `SIGNED_LAUNCH_TOKEN` for fallback when OIDC is unavailable.
- Why: This decides private responder authentication behavior for all tenant campaigns.

2. Create auth profile (`POST /api/v1/auth/profiles`).
- Why: Private campaign access depends on tenant auth profile.
- How: Provide tenantId, authMode, and mode-specific fields.
- Result: Auth profile stored.

3. Provide claim mappings (required/optional).
- Why: System needs deterministic identity extraction.
- How: Map external claim names to internal fields.
- Mandatory rule: `respondentId` mapping must exist and be required.
- Result: Fail-closed identity resolution policy.

4. Set OIDC scopes (or rely on defaults).
- Why: OIDC claim surface depends on requested scopes.
- How: Set `oidcScopes` or allow default `openid email profile`.
- Result: Minimum secure scope baseline.

5. Retrieve profile (`GET /api/v1/auth/profiles/tenant/{tenantId}`).
- Why: Confirm effective configuration.
- How: Read back saved profile.
- Result: Operational verification before launch.

6. Review audit log (`GET /api/v1/auth/profiles/{id}/audit`).
- Why: Track sensitive auth changes for compliance and troubleshooting.
- How: Fetch audit records.
- Result: Change history with before/after snapshot context.

7. Rotate key (`POST /api/v1/auth/profiles/{id}/rotate-key`) when required.
- Why: Periodic key hygiene and incident response.
- How: Trigger rotation endpoint.
- Result: Active key version increments and audit event recorded.

8. Use provider templates for onboarding support.
- Endpoints:
  - `GET /api/v1/auth/providers/templates`
  - `GET /api/v1/auth/providers/templates/{providerCode}`
- Why: Reduce setup mistakes.
- Result: Pre-filled defaults and required field guidance.

---

## 7. Private Responder OIDC Flow (Recommended)

### Goal
Authenticate private responders through subscriber SSO and submit with one-time access code.

### Preconditions
1. Campaign `authMode=PRIVATE` and active.
2. Tenant auth profile set to `EXTERNAL_SSO_TRUST`.
3. OIDC fields configured and valid.

### Step-by-step

1. Responder starts auth (`POST /api/v1/auth/respondent/oidc/start`).
- Why: Initiates trusted SSO flow for this campaign.
- How: Send `tenantId`, `campaignId`, optional `returnPath`.
- Result: Authorization URL + short-lived state metadata.

2. Redirect responder to IdP authorization URL.
- Why: IdP performs user authentication.
- How: Frontend redirect.
- Result: User signs in (or silent SSO if already logged in).

3. IdP callback (`GET /api/v1/auth/respondent/oidc/callback?state=...&code=...`).
- Why: Completes authorization code flow.
- How: Engine validates state, exchanges code for token(s), validates claims and trust.
- Result: One-time short-lived `responderAccessCode` issued.

4. Submit response with one-time access code (`POST /api/v1/responses`).
- Why: Avoid passing long-lived responder tokens in URL/query during final submit.
- How: Include `campaignId`, answers, `responderAccessCode`.
- Result: Code consumed once; response accepted and locked.

5. Handle failures deterministically.
- Why: Consistent support behavior.
- How: Invalid/expired state, invalid claims, or mismatched code yields access-denied style error.
- Result: No response persisted for failed auth.

---

## 8. Private Responder Signed Token Flow (Fallback)

### Goal
Support private responder auth when subscriber cannot use direct OIDC flow.

### Preconditions
1. Campaign `authMode=PRIVATE` and active.
2. Tenant auth profile set to `SIGNED_LAUNCH_TOKEN`.
3. Subscriber system can issue signed JWT with required claims.

### Step-by-step

1. Subscriber system authenticates user in its own environment.
- Why: Survey Engine trusts subscriber-signed identity assertion.
- How: Subscriber uses its existing auth stack.
- Result: Known user context on subscriber side.

2. Subscriber issues signed JWT (`responderToken`).
- Why: Provide proof of trusted identity to Survey Engine.
- How: Sign with shared secret and include required claims (`jti`, exp, mapped identity claims).
- Result: Token ready for response submit.

3. Responder submits answers (`POST /api/v1/responses` with `responderToken`).
- Why: Submit private response through trusted credential.
- How: Include campaignId, answers, responderToken.
- Result: Token validation pipeline runs.

4. Engine validates token and mapping.
- Why: Prevent forged/replayed/expired identity assertions.
- How: Verify signature, issuer, audience, expiry, replay (`jti`), and required mapped claims.
- Result: Valid token proceeds; invalid token rejected.

5. Store and lock response on success.
- Why: Preserve final submitted state integrity.
- How: Persist submission and apply lock state.
- Result: Response accepted in private mode.

---

## 9. Response Lifecycle Flow

### Goal
Collect responses with proper runtime checks, auth checks, and post-submit integrity controls.

### Recommended creation order
1. Ensure campaign active.
2. Ensure proper auth path if private.
3. Submit response.
4. Use lock/reopen/admin analytics operations as needed.

### Step-by-step

1. Submit response (`POST /api/v1/responses`).
- Why: Primary data ingestion point.
- How: Provide campaignId + answers (+ private credential if required).
- Result: Access and settings checks begin.

2. Access mode check.
- Why: Enforce campaign privacy policy.
- How:
  - Public: no responder credential needed.
  - Private: requires `responderToken` or `responderAccessCode`.
- Result: Unauthorized submissions rejected early.

3. Runtime settings checks.
- Why: Enforce campaign controls.
- How: Validate exactly these submit-time checks:
- `responseQuota`
- `closeDate`
- `oneResponsePerDevice` (device fingerprint dedup)
- `ipRestrictionEnabled` (IP dedup)
- `emailRestrictionEnabled` (respondent identifier dedup)
- Note: `sessionTimeoutMinutes`, `captchaEnabled`, and presentation settings are configuration fields but are not submit-time blockers in current MVP response service.
- Result: Only policy-compliant responses proceed.

4. Persist response and answers.
- Why: Save complete submission record.
- How: Store response entity + answer entities.
- Result: Response state created.

5. Compute weighted score automatically when campaign default profile exists.
- Why: Simplified flow should not require separate manual scoring action.
- How:
  - Aggregate validated answer scores by category.
  - Run scoring engine using campaign `defaultWeightProfileId`.
  - Persist `weightProfileId`, `weightedTotalScore`, and `scoredAt` in response.
- Result: Response is both submitted and scored in one transaction.

6. Auto-lock on successful submit.
- Why: Avoid unintended post-submit mutation.
- How: Response status moved to locked.
- Result: Integrity-preserving final state.

7. Auto-lock on close transition (campaign `closeDate` reached/passed).
- Why: Ensure unresolved open responses do not remain mutable after campaign closes.
- How:
  - Scheduled enforcement scans expired campaign close dates.
  - For each expired campaign, all `IN_PROGRESS` and `REOPENED` responses are moved to `LOCKED`.
- Result: Post-close response set is consistently locked.

8. Admin review operations:
- `GET /api/v1/responses/{id}` (single)
- `GET /api/v1/responses/campaign/{campaignId}` (campaign list)
- Why: Support operations and review.

9. Manual lock/reopen operations:
- `POST /api/v1/responses/{id}/lock`
- `POST /api/v1/responses/{id}/reopen`
- Why: Controlled exception handling for corrections.

10. Campaign analytics retrieval (`GET /api/v1/responses/analytics/{campaignId}`).
- Why: Campaign-level completion and activity insights.
- How: Optional `metadata.<key>=<value>` query params are supported for filtered analytics.
- Result: Aggregated response metrics.

11. Response listing with metadata filters (`GET /api/v1/responses/campaign/{campaignId}`).
- Why: Operations teams often need segmented response views.
- How: Pass query params as `metadata.<field>=<value>` plus pagination params.
- Result: Paged response set filtered by metadata dimensions.

---

## 10. Automated Scoring Lifecycle Flow (Default)

### Goal
Guarantee deterministic weighted scoring with minimal operational steps.

### Recommended creation order
1. Define category structure and category weights in survey draft.
2. Publish survey.
3. Activate campaign.
4. Collect responses (scoring runs automatically).

### Step-by-step

1. Author category weights in survey draft (`POST/PUT /api/v1/surveys`).
- Why: Category weights are part of survey design contract.
- How:
  - Set `categoryWeightPercentage` on category-tagged survey questions.
  - Ensure one consistent weight per category and total `100.00`.
- Result: Draft contains explicit scoring weights.

2. Publish survey (`POST /api/v1/surveys/{id}/lifecycle` to `PUBLISHED`).
- Why: Campaign activation requires published survey snapshot.
- How: Publish draft after validation.
- Result: Immutable snapshot contains question/category pins and category weights.

3. Activate campaign (`POST /api/v1/campaigns/{id}/activate`).
- Why: This is the scoring bootstrap point in simplified flow.
- How:
  - Backend links latest `surveySnapshotId`.
  - Backend auto-creates/updates campaign `Default` weight profile from snapshot category weights.
- Result: `campaign.defaultWeightProfileId` is ready for runtime scoring.

4. Submit responses (`POST /api/v1/responses`).
- Why: Runtime scoring is now submission-driven, not separate admin action.
- How:
  - Response validator enforces snapshot question/category integrity.
  - Backend computes category raw totals and weighted score using campaign default profile.
- Result: `survey_response.weighted_total_score` and scoring metadata are persisted automatically.

### Manual scoring APIs (non-default path)

`/api/v1/scoring/**` remains implemented for advanced operational use, but:
- it is not required in the default MVP workflow
- frontend no longer exposes a dedicated `/scoring` route in the simplified model

---

## 11. End-to-End Recommended Build Order

If a new tenant asks “what should we configure first,” use this order:

1. Register/login admin and confirm subscription state.
- Why first: No protected operations work without authenticated admin context and active entitlement.

2. Build question bank and categories.
- Why second: Surveys depend on stable reusable content.

3. Create survey draft, refine, and publish.
- Why third: Campaign activation requires published survey.

4. Configure tenant auth profile (if campaign will be private).
- Why fourth: Private responder access depends on tenant auth policy.

5. Create campaign and set `authMode` (`PUBLIC` or `PRIVATE`).
- Why fifth: Campaign is the runtime delivery object.

6. Configure campaign runtime settings and activate.
- Why sixth: Activation is launch gate; settings control data quality and policy compliance.

7. Generate channels and distribute links.
- Why seventh: Responders can now participate.

8. Collect responses, monitor analytics, and apply locking/reopen controls.
- Why eighth: Ongoing campaign operation.

9. Review scored responses and analytics outputs.
- Why ninth: Weighted outcomes are computed automatically at submit time in default flow.

---

## 12. Explicit Tagging Chain (Question -> Category -> Survey -> Campaign)

This section is the canonical no-ambiguity chain for entity tagging.

### A. Question -> Category

1. Create question (`POST /api/v1/questions`) to get `questionId`.
2. Create/update category (`POST/PUT /api/v1/categories`) with `questionMappings[*].questionId`.
3. Backend stores:
- `category_question_mapping.question_id`
- `category_question_mapping.question_version_id` (auto-resolved live bank version)
4. Important rule:
- Category holds mappings, but this does not automatically place questions into survey pages.

### B. Category -> Survey

1. Create/update survey (`POST/PUT /api/v1/surveys`) with page question entries.
2. To tag a survey question to a category, set `categoryId` and `categoryWeightPercentage` in each category-linked survey question item.
3. Backend stores:
- `survey_question.question_id`
- `survey_question.question_version_id` (new pinned version snapshot)
- `survey_question.category_id` (explicit tag)
- `survey_question.category_version_id` (new pinned category snapshot)
- `survey_question.category_weight_percentage` (scoring weight by category)
4. Important rule:
- Category tagging is explicit per survey question item.
- Pinned versions are created at survey draft save; bank updates later do not mutate them.

### C. Survey -> Campaign

1. Create campaign (`POST /api/v1/campaigns`) with `surveyId`.
2. Activate campaign (`POST /api/v1/campaigns/{id}/activate`).
3. Backend stores:
- `campaign.survey_id` at create time.
- `campaign.survey_snapshot_id` at activation from latest published survey snapshot.
- `campaign.default_weight_profile_id` auto-generated from pinned snapshot category weights.
4. Important rule:
- Campaign is the runtime delivery object for one published survey snapshot context.

### D. Runtime response linkage (for completeness)

1. Response submit uses `campaignId` (`POST /api/v1/responses`).
2. Backend derives survey snapshot from campaign and validates submitted answers against that snapshot.
3. Backend stores answer links:
- `answer.question_id`
- `answer.question_version_id`
4. Integrity guarantees:
- Question must exist in campaign snapshot.
- Question version must match snapshot.
- Duplicate answers for same question in one response are rejected.

### E. Runtime scoring linkage (default path)

1. Response scoring uses `campaign.default_weight_profile_id`.
2. Backend aggregates answer scores by snapshot category and calculates weighted total.
3. Backend stores:
- `survey_response.weight_profile_id`
- `survey_response.weighted_total_score`
- `survey_response.scored_at`
4. Important rule:
- No separate manual scoring screen is required for standard campaign execution.

---

## 13. Out-of-MVP Lifecycle Items

The following flows exist in broader documentation but are not part of implemented MVP lifecycle scope:

1. Native SAML and LDAP protocol engines.
2. Roster connector ingestion and assignment-policy lifecycle.
3. Webhook delivery and retry dead-letter lifecycle.
4. Setup-later onboarding state machine with bootstrap expiry controls.
5. Full enterprise approval workflow states beyond current implemented lifecycle transitions.

---

## 14. Super-Admin Tenant Operations Flow

### Goal
Allow platform operators to manage tenant lifecycle and provide support operations.

### Step-by-step

1. List tenants (`GET /api/v1/admin/superadmin/tenants`).
- Why: Platform operations need tenant visibility.
- How: Query paged tenant overview.
- Result: Tenant inventory with current operational state.

2. Suspend or activate tenant (`PUT /api/v1/admin/superadmin/tenants/{tenantId}/suspend`, `PUT /api/v1/admin/superadmin/tenants/{tenantId}/activate`).
- Why: Enforce platform governance and operational control.
- How: Issue lifecycle command for target tenant id.
- Result: Tenant access state changes.

3. Override subscription (`POST /api/v1/admin/superadmin/tenants/{tenantId}/subscriptions/override`).
- Why: Handle support, migration, or emergency commercial adjustments.
- How: Submit override request payload.
- Result: Tenant subscription state is updated.

4. Impersonate tenant admin (`POST /api/v1/admin/superadmin/tenants/{tenantId}/impersonate`).
- Why: Diagnose tenant-side issues in real context.
- How: Start impersonation session.
- Result: Super-admin receives tenant-scoped admin session.

5. Revert impersonation (`POST /api/v1/admin/auth/revert-impersonation`).
- Why: Return safely to original super-admin context.
- How: Call revert endpoint.
- Result: Original super-admin session is restored.

6. View platform metrics (`GET /api/v1/admin/superadmin/metrics` or `GET /api/v1/admin/superadmin/tenants/metrics`).
- Why: Track platform-level health and usage.
- How: Call metrics endpoint from super-admin session.
- Result: Aggregated platform metrics snapshot.

---

## 15. Audit Log Query Flow

### Goal
Provide traceability for tenant-level and platform-wide operational activity.

### Step-by-step

1. Query tenant activity logs (`GET /api/v1/audit-logs`).
- Why: Tenant admins need their own operational audit trail.
- How: Use optional filters (`action`, `entityType`, `from`, `to`) and paging.
- Result: Tenant-scoped audit records ordered by latest activity.

2. Query platform audit logs (`GET /api/v1/admin/superadmin/audit-logs`).
- Why: Super-admin and support teams need cross-tenant visibility.
- How: Use optional filters (`tenantId`, `actor`, `action`, `entityType`, `from`, `to`) and paging.
- Result: Platform-wide audit records for governance and investigation.
