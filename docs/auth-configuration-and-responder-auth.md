# Auth Flows (Scenario-Based, Brief)

Date: March 3, 2026

## Goal

Use one tenant-level auth configuration for all campaigns, then run responder access by campaign mode:

1. `PUBLIC` campaign: no responder auth.
2. `PRIVATE` campaign: responder auth required via tenant auth profile.

## Core Problem -> Solution

1. Problem: Per-campaign auth setup is duplicated and error-prone.
Solution: Auth is configured once per tenant (`auth_profile`), reused by all tenant campaigns.

2. Problem: Different subscribers use different IdPs or legacy systems.
Solution: Two responder auth paths:
- `EXTERNAL_SSO_TRUST` (OIDC/JWT)
- `SIGNED_LAUNCH_TOKEN` (fallback for legacy/custom)

3. Problem: Token replay and unsafe URL token exposure.
Solution: `jti` replay protection + OIDC one-time access code exchange.

## Flow 0: Admin Auth (Engine Access)

Used by subscriber admins to manage tenant settings.

1. `POST /api/v1/admin/auth/register`
2. `POST /api/v1/admin/auth/login`
3. `POST /api/v1/admin/auth/refresh`

Result: admin gets access token + refresh token.

## Flow 1: Configure Tenant Auth (Once)

Admin sets responder auth for tenant.

1. Create/update profile:
- `POST /api/v1/auth/profiles`
- `PUT /api/v1/auth/profiles/{id}`

2. Read/audit:
- `GET /api/v1/auth/profiles/tenant/{tenantId}`
- `GET /api/v1/auth/profiles/{id}/audit`

3. Optional key rotation:
- `POST /api/v1/auth/profiles/{id}/rotate-key`

Required mapping rule:
- `respondentId` must be mapped and required.

Default mapping (if omitted):
- `sub -> respondentId` (required)
- `email -> email` (optional)

Scope default:
- `openid email profile` (tenant can override, `openid` always enforced)

## Flow 2: Public Campaign Response

Scenario: campaign is public.

1. Campaign has `authMode=PUBLIC`.
2. Responder submits directly:
- `POST /api/v1/responses`
3. No `responderToken`/`responderAccessCode` needed.

## Flow 3: Private Campaign with Signed Launch Token

Scenario: subscriber cannot use OIDC directly.

1. Campaign has `authMode=PRIVATE`.
2. Tenant profile `authMode=SIGNED_LAUNCH_TOKEN`.
3. Subscriber provides signed JWT to responder.
4. Responder submits:
- `POST /api/v1/responses` with `responderToken`.
5. Engine validates signature + claims + replay + required mappings.

Success: response accepted.
Failure: access denied (fail-closed).

## Flow 4: Private Campaign with OIDC + One-Time Access Code

Scenario: standard OIDC with minimal token exposure.

1. Campaign has `authMode=PRIVATE`.
2. Tenant profile `authMode=EXTERNAL_SSO_TRUST` and OIDC fields configured.
3. Start auth:
- `POST /api/v1/auth/respondent/oidc/start`
4. IdP authenticates responder and calls back:
- `GET /api/v1/auth/respondent/oidc/callback?state=...&code=...`
5. Engine exchanges code, validates token, issues one-time `accessCode`.
6. Responder submits:
- `POST /api/v1/responses` with `responderAccessCode`.
7. Engine consumes code once (tenant+campaign bound, expiry enforced).

## Flow 5: Provider Onboarding Template (UI Prefill)

Use template metadata to reduce setup errors.

1. `GET /api/v1/auth/providers/templates`
2. `GET /api/v1/auth/providers/templates/{providerCode}`

Supported codes:
- `OKTA`, `AUTH0`, `AZURE_AD`, `KEYCLOAK`

## Quick Path Matrix

1. Public survey link only: Flow 2.
2. Private + modern IdP: Flow 1 + Flow 4.
3. Private + legacy/custom IdP: Flow 1 + Flow 3.

## Auth-Related API Paths (Complete)

1. `POST /api/v1/admin/auth/register`
2. `POST /api/v1/admin/auth/login`
3. `POST /api/v1/admin/auth/refresh`
4. `POST /api/v1/auth/profiles`
5. `PUT /api/v1/auth/profiles/{id}`
6. `GET /api/v1/auth/profiles/tenant/{tenantId}`
7. `POST /api/v1/auth/profiles/{id}/rotate-key`
8. `GET /api/v1/auth/profiles/{id}/audit`
9. `GET /api/v1/auth/providers/templates`
10. `GET /api/v1/auth/providers/templates/{providerCode}`
11. `POST /api/v1/auth/validate/{tenantId}`
12. `POST /api/v1/auth/respondent/oidc/start`
13. `GET /api/v1/auth/respondent/oidc/callback`
14. `POST /api/v1/responses`
