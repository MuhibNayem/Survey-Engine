# Private Survey Link: Responder Journey (Detailed)

Date: March 4, 2026
Audience: Product, onboarding, support, implementation teams

## Purpose

Explain exactly what happens when a responder clicks a **private** survey link.

## 1) Entry Point: Responder Clicks Private Link

1. Responder opens a campaign link.
2. Campaign is marked `PRIVATE`.
3. System does not allow anonymous submit for this campaign.

## 2) Tenant Auth Policy Is Applied

Responder auth is configured at **tenant level**, not campaign level.

System checks tenant auth profile and selects one path:

1. `EXTERNAL_SSO_TRUST` -> OIDC login path.
2. `SIGNED_LAUNCH_TOKEN` -> signed token path.
3. `PUBLIC_ANONYMOUS` with private campaign -> rejected.

---

## 3) Path A: OIDC Flow (`EXTERNAL_SSO_TRUST`)

### Step A1: Start authentication

1. Frontend calls `POST /api/v1/auth/respondent/oidc/start` with tenant + campaign.
2. System validates:
   - campaign exists
   - campaign belongs to tenant
   - campaign is private
   - tenant auth mode is OIDC trust
3. System creates short-lived `state` and `nonce`.
4. System returns IdP authorization URL.

### Step A2: Responder signs in at IdP

1. Browser redirects to company identity provider login.
2. Responder completes login there.

### Step A3: Callback returns to Survey Engine

1. IdP calls back: `GET /api/v1/auth/respondent/oidc/callback?state=...&code=...`
2. System validates callback state:
   - exists
   - not expired
   - not previously used
3. System exchanges `code` for token(s) at IdP token endpoint.
4. System validates token using tenant policy:
   - signature
   - issuer
   - audience
   - expiry/time checks
   - required claim mappings (including required `respondentId`)

### Step A4: One-time responder code is issued

1. If token is valid, system creates short-lived one-time `accessCode`.
2. Code is bound to tenant + campaign and can be used once.

### Step A5: Responder submits answers

1. Frontend submits `POST /api/v1/responses` with:
   - `campaignId`
   - answers
   - `responderAccessCode`
2. System consumes code (one-time, non-expired, tenant+campaign match).
3. Response is saved and then locked.

---

## 4) Path B: Signed Token Flow (`SIGNED_LAUNCH_TOKEN`)

### Step B1: Responder receives signed token

1. Subscriber system provides a signed JWT for that responder.

### Step B2: Responder submits answers

1. Frontend submits `POST /api/v1/responses` with:
   - `campaignId`
   - answers
   - `responderToken`
2. System validates token:
   - JWT format + algorithm
   - signature
   - issuer/audience
   - expiry/time checks
   - replay protection (`jti` cannot be reused)
   - required mapped claims (`respondentId` required)
3. If valid, response is saved and locked.

---

## 5) Failure Cases (Private Campaign)

Submission is rejected when:

1. No `responderToken` and no `responderAccessCode` provided.
2. Token/code is expired, invalid, reused, or campaign/tenant mismatch.
3. Required claim mapping is missing (fail-closed behavior).
4. Private campaign is paired with anonymous tenant auth mode.

---

## 6) Final Outcome

A private survey response is accepted only when responder identity is proven via:

1. OIDC + one-time access code, or
2. Valid signed responder token.

Any invalid or incomplete auth state is denied.
