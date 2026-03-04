# Survey Engine MVP Lifecycle Flows (Step-by-Step Guide)
## Product: Headless Multi-Tenant Survey Engine (MVP)
## Version: 1.0
## Date: March 4, 2026

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
10. Scoring Lifecycle Flow
11. End-to-End Recommended Build Order
12. Explicit Tagging Chain (Question -> Category -> Survey -> Campaign)
13. Out-of-MVP Lifecycle Items

---

## 1. Tenant Bootstrap and Admin Access Flow

### Goal
Bring a new tenant from zero state to authenticated admin access.

### Step-by-step

1. Create admin account (`POST /api/v1/admin/auth/register`).
- Why: This creates the first trusted operator account.
- How: Send full name, email, password, tenant ID.
- Result: Tenant is provisioned, trial subscription is initialized, and token pair is returned.

2. Store access and refresh tokens securely in admin session.
- Why: Admin APIs require bearer authentication.
- How: Keep access token for API calls, refresh token for renewal.
- Result: Admin can call protected endpoints.

3. Start normal login cycle (`POST /api/v1/admin/auth/login`).
- Why: Ongoing operations should not rely on registration response tokens.
- How: Login with email/password.
- Result: Fresh access + refresh token pair.

4. Rotate session using refresh (`POST /api/v1/admin/auth/refresh`).
- Why: Access token is short-lived by design.
- How: Send refresh token before/after access token expiry.
- Result: New token pair, old refresh token revoked.

### Key implementation intent
- Tenant context is carried in JWT claims and used throughout service authorization and data scoping.

---

## 2. Subscription and Plan Flow

### Goal
Ensure tenant has active entitlement and system-enforced usage limits.

### Step-by-step

1. Read current subscription (`GET /api/v1/admin/subscriptions/me`).
- Why: Admin must know current plan and status before setup decisions.
- How: Call endpoint with admin bearer token.
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
- How: Send only `text`, `type`, and `maxScore`.
- Result: Question created and versioned.

2. List and review active questions (`GET /api/v1/questions`, `GET /api/v1/questions/{id}`).
- Why: Validate content quality before categorization.
- How: Retrieve list and details.
- Result: Curated question pool.

3. Update question if needed (`PUT /api/v1/questions/{id}`).
- Why: Fix wording/scoring issues early.
- How: Submit revised payload.
- Result: Updated question and version snapshot.

4. Create category (`POST /api/v1/categories`).
- Why: Group related questions for structure and scoring alignment.
- How: Create category metadata and mappings.
- Result: Category created and versioned.

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
- Backend resolves and stores current `questionVersionId` automatically for each mapped `questionId`.

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
- Result: Survey in draft state.

2. Inspect draft (`GET /api/v1/surveys/{id}` and list endpoint).
- Why: Validate structure before publication.
- How: Retrieve survey content and status.
- Result: Confirmed draft readiness.

3. Update draft (`PUT /api/v1/surveys/{id}`).
- Why: Apply final edits before publish.
- How: Submit updated survey schema.
- Result: Updated draft.

4. Transition lifecycle (`POST /api/v1/surveys/{id}/lifecycle`).
- Why: Campaign activation requires published survey.
- How: Request valid transition (e.g., DRAFT -> PUBLISHED).
- Result: Survey state changes when transition is valid.

5. Preserve immutability after publish (enforced behavior).
- Why: Response and campaign consistency require stable published structure.
- How: System blocks unsafe structural changes after publish.
- Result: Published snapshot integrity.

6. Move through end states when appropriate.
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
- `mandatory` (optional)
- `answerConfig` (optional)
3. System behavior:
- Backend resolves and stores current `questionVersionId` automatically from `questionId`.
- Category mapping from category definitions is not auto-injected into survey pages.
- You must explicitly place each question in survey pages, and explicitly set `categoryId` when needed.

### `answerConfig` definition (question options/rules)

`answerConfig` is configured at survey-question level (not in question bank create/update payload).

1. Format:
- Must be a JSON object.

2. By question type:
- `SINGLE_CHOICE`: use `options` array for allowed values.
- `MULTIPLE_CHOICE`: use `options` array and optionally `minSelections`, `maxSelections`.
- `RATING_SCALE`: use `min`, `max`, optionally `step`.
- `RANK`: use `options`, optionally `allowPartialRanking`, `correctOrder`.

3. Storage and publish behavior:
- Stored in `survey_question.answer_config`.
- Copied into immutable `survey_snapshot.snapshot_data` at publish.
- Runtime response validation reads the published snapshot copy.

4. MVP implementation note:
- For `SINGLE_CHOICE` and `MULTIPLE_CHOICE`, `options` are currently optional in backend validation.
- If `options` is omitted, backend does not whitelist submitted values for that question.

Example survey question items with `answerConfig`:
```json
[
  {
    "questionId": "11111111-1111-1111-1111-111111111111",
    "sortOrder": 1,
    "mandatory": true,
    "answerConfig": "{\"options\":[{\"value\":\"A\"},{\"value\":\"B\"}]}"
  },
  {
    "questionId": "22222222-2222-2222-2222-222222222222",
    "sortOrder": 2,
    "mandatory": false,
    "answerConfig": "{\"options\":[{\"value\":\"X\",\"score\":2},{\"value\":\"Y\",\"score\":1}],\"minSelections\":1,\"maxSelections\":2}"
  },
  {
    "questionId": "33333333-3333-3333-3333-333333333333",
    "sortOrder": 3,
    "mandatory": true,
    "answerConfig": "{\"min\":1,\"max\":5,\"step\":1}"
  }
]
```

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
          "sortOrder": 1,
          "mandatory": true
        },
        {
          "questionId": "22222222-2222-2222-2222-222222222222",
          "categoryId": "bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb",
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

3. Configure runtime settings (`PUT /api/v1/campaigns/{id}/settings`).
- Why: Set operational controls before launch.
- How: Configure quota, restrictions, close time, UX controls, etc.
- Result: Runtime enforcement rules stored.

4. Activate campaign (`POST /api/v1/campaigns/{id}/activate`).
- Why: Only active campaigns accept submissions.
- How: Activation call checks linked survey lifecycle constraints.
- Result: Campaign moves to active state if preconditions pass.

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

5. Auto-lock on successful submit.
- Why: Avoid unintended post-submit mutation.
- How: Response status moved to locked.
- Result: Integrity-preserving final state.

6. Admin review operations:
- `GET /api/v1/responses/{id}` (single)
- `GET /api/v1/responses/campaign/{campaignId}` (campaign list)
- Why: Support operations and review.

7. Manual lock/reopen operations:
- `POST /api/v1/responses/{id}/lock`
- `POST /api/v1/responses/{id}/reopen`
- Why: Controlled exception handling for corrections.

8. Campaign analytics retrieval (`GET /api/v1/responses/analytics/{campaignId}`).
- Why: Campaign-level completion and activity insights.
- Result: Aggregated response metrics.

---

## 10. Scoring Lifecycle Flow

### Goal
Define weighted scoring policy and compute deterministic score outcomes.

### Recommended creation order
1. Build category model.
2. Create weight profile.
3. Validate weights.
4. Calculate score.

### Step-by-step

1. Create weight profile (`POST /api/v1/scoring/profiles`).
- Why: Encode business weighting logic per campaign.
- How: Provide campaign-scoped category weight distribution.
- Result: Scoring profile stored.

2. Retrieve and review profile (`GET /api/v1/scoring/profiles/{id}` or campaign list endpoint).
- Why: Confirm profile composition before use.
- Result: Verified scoring configuration.

3. Validate weight sum (`POST /api/v1/scoring/profiles/{id}/validate`).
- Why: Prevent mathematically invalid score outputs.
- How: System checks total equals required sum.
- Result: Pass/fail.

4. Update profile if needed (`PUT /api/v1/scoring/profiles/{id}`).
- Why: Business rubric changes over time.
- Result: Updated profile for future calculations.

5. Calculate weighted score (`POST /api/v1/scoring/calculate/{profileId}`).
- Why: Transform raw category scores into final weighted result.
- How: Submit category raw scores.
- Result: Deterministic weighted score response.

6. Deactivate obsolete profile (`DELETE /api/v1/scoring/profiles/{id}`).
- Why: Keep active scoring catalog clean.
- Result: Profile removed from active usage.

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

9. Configure scoring profiles and compute weighted outcomes.
- Why ninth: Post-ingestion interpretation and reporting.

---

## 12. Explicit Tagging Chain (Question -> Category -> Survey -> Campaign)

This section is the canonical no-ambiguity chain for entity tagging.

### A. Question -> Category

1. Create question (`POST /api/v1/questions`) to get `questionId`.
2. Create/update category (`POST/PUT /api/v1/categories`) with `questionMappings[*].questionId`.
3. Backend stores:
- `category_question_mapping.question_id`
- `category_question_mapping.question_version_id` (auto-resolved latest version)
4. Important rule:
- Category holds mappings, but this does not automatically place questions into survey pages.

### B. Category -> Survey

1. Create/update survey (`POST/PUT /api/v1/surveys`) with page question entries.
2. To tag a survey question to a category, set `categoryId` in each survey question item.
3. Backend stores:
- `survey_question.question_id`
- `survey_question.question_version_id` (auto-resolved latest version)
- `survey_question.category_id` (your explicit tag)
4. Important rule:
- Category tagging in survey is explicit per survey question item.

### C. Survey -> Campaign

1. Create campaign (`POST /api/v1/campaigns`) with `surveyId`.
2. Activate campaign (`POST /api/v1/campaigns/{id}/activate`).
3. Backend stores:
- `campaign.survey_id` at create time.
- `campaign.survey_snapshot_id` at activation from latest published survey snapshot.
4. Important rule:
- Campaign is the runtime delivery object for one survey context.

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

---

## 13. Out-of-MVP Lifecycle Items

The following flows exist in broader documentation but are not part of implemented MVP lifecycle scope:

1. Native SAML and LDAP protocol engines.
2. Roster connector ingestion and assignment-policy lifecycle.
3. Webhook delivery and retry dead-letter lifecycle.
4. Setup-later onboarding state machine with bootstrap expiry controls.
5. Full enterprise approval workflow states beyond current implemented lifecycle transitions.
