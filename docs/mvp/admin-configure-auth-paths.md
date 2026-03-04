# Admin Guide: Configure Each Responder Access Path

Date: March 4, 2026
Audience: Tenant admins and onboarding teams

## What this guide covers

This guide explains, in plain language, how an admin configures each responder access path:

1. Public campaign (no responder login).
2. Private campaign with OIDC (company SSO login).
3. Private campaign with Signed Launch Token (fallback for legacy/custom systems).

For each path, this guide includes:

1. Why you choose it.
2. What admin must configure.
3. Exact API inputs needed.
4. What happens after setup.

---

## Before you start (common for all paths)

You always need:

1. Admin account login.
2. A published survey.
3. A campaign that points to that survey.
4. Tenant ID.

Useful endpoints you will use often:

1. `POST /api/v1/admin/auth/login`
2. `POST /api/v1/surveys`
3. `POST /api/v1/surveys/{id}/lifecycle`
4. `POST /api/v1/campaigns`
5. `POST /api/v1/campaigns/{id}/activate`

---

## Path 1: Public Campaign (No responder authentication)

## Why choose this path

Choose this when anyone can answer the survey without logging in.

## What admin configures

Admin sets campaign access mode to `PUBLIC`.

## Exact input needed

### Endpoint

`POST /api/v1/campaigns`

### Required fields

1. `name` (string)
2. `surveyId` (UUID)

### Important field for this path

1. `authMode` = `PUBLIC`

### Example request

```json
{
  "name": "Student Feedback - Public",
  "description": "Open participation campaign",
  "surveyId": "2d9bfa6c-2f01-4b4d-b4f1-129eb71a3dcb",
  "authMode": "PUBLIC"
}
```

### Activate campaign

Endpoint: `POST /api/v1/campaigns/{id}/activate`

No request body required.

## What happens after setup

1. Responder can submit directly to `POST /api/v1/responses`.
2. No responder token or access code is required.

---

## Path 2: Private Campaign with OIDC (Recommended for modern IdP)

## Why choose this path

Choose this when responders should sign in through company SSO (Okta/Auth0/Azure AD/Keycloak).

## What admin configures

Admin configures tenant auth profile with `EXTERNAL_SSO_TRUST`, then creates private campaign.

## Step A: Create or update tenant auth profile

### Endpoint

1. Create: `POST /api/v1/auth/profiles`
2. Update: `PUT /api/v1/auth/profiles/{id}`

### Required core fields

1. `tenantId`
2. `authMode` = `EXTERNAL_SSO_TRUST`

### OIDC fields needed for this path

1. `issuer`
2. `audience`
3. `oidcDiscoveryUrl`
4. `oidcClientId`
5. `oidcClientSecret`
6. `oidcRedirectUri` (or system default callback)

### Optional but recommended fields

1. `jwksEndpoint` (if needed by provider policy)
2. `oidcScopes` (default is `openid email profile`)
3. `clockSkewSeconds`
4. `tokenTtlSeconds`
5. `fallbackPolicy` (recommended: `SSO_REQUIRED` for private campaigns)

### Claim mapping input (important)

You can provide `claimMappings`. If you provide mappings, include a required `respondentId` mapping.

Required rule:

1. `respondentId` must be mapped and `required=true`.

Safe default mapping:

1. `sub -> respondentId` (required)
2. `email -> email` (optional)

### Example request (OIDC profile)

```json
{
  "tenantId": "tenant-acme",
  "authMode": "EXTERNAL_SSO_TRUST",
  "issuer": "https://acme.okta.com/oauth2/default",
  "audience": "survey-engine-acme",
  "oidcDiscoveryUrl": "https://acme.okta.com/oauth2/default/.well-known/openid-configuration",
  "oidcClientId": "0oa1exampleClientId",
  "oidcClientSecret": "exampleClientSecret",
  "oidcRedirectUri": "https://survey.example.com/api/v1/auth/respondent/oidc/callback",
  "oidcScopes": "openid email profile",
  "fallbackPolicy": "SSO_REQUIRED",
  "claimMappings": [
    {
      "externalClaim": "sub",
      "internalField": "respondentId",
      "required": true
    },
    {
      "externalClaim": "email",
      "internalField": "email",
      "required": false
    }
  ]
}
```

## Step B: Create private campaign

### Endpoint

`POST /api/v1/campaigns`

### Required fields

1. `name`
2. `surveyId`
3. `authMode` = `PRIVATE`

### Example request

```json
{
  "name": "Employee Engagement - Private",
  "description": "SSO protected campaign",
  "surveyId": "2d9bfa6c-2f01-4b4d-b4f1-129eb71a3dcb",
  "authMode": "PRIVATE"
}
```

## Step C: Activate campaign

Endpoint: `POST /api/v1/campaigns/{id}/activate`

No request body required.

## What happens after setup

1. Responder opens private link.
2. Frontend starts OIDC using `POST /api/v1/auth/respondent/oidc/start`.
3. IdP login completes and callback endpoint is called.
4. System issues one-time responder access code.
5. Responder submits answers using that access code.

---

## Path 3: Private Campaign with Signed Launch Token (Fallback)

## Why choose this path

Choose this when subscriber cannot use OIDC directly (legacy/custom identity environment).

## What admin configures

Admin configures tenant auth profile with `SIGNED_LAUNCH_TOKEN`, then creates private campaign.

## Step A: Create or update tenant auth profile

### Endpoint

1. Create: `POST /api/v1/auth/profiles`
2. Update: `PUT /api/v1/auth/profiles/{id}`

### Required core fields

1. `tenantId`
2. `authMode` = `SIGNED_LAUNCH_TOKEN`
3. `signingSecret`

### Strongly recommended fields

1. `issuer`
2. `audience`
3. `clockSkewSeconds`
4. `tokenTtlSeconds`
5. `fallbackPolicy` (recommended: `SSO_REQUIRED` for private campaigns)

### Claim mapping input (important)

Provide or keep mapping so required identity is always present.

Required rule:

1. `respondentId` must be mapped and `required=true`.

Example mappings:

1. `sub -> respondentId` (required)
2. `email -> email` (optional or required based on business policy)

### Example request (Signed Token profile)

```json
{
  "tenantId": "tenant-acme",
  "authMode": "SIGNED_LAUNCH_TOKEN",
  "issuer": "acme-identity-service",
  "audience": "survey-engine",
  "signingSecret": "a-very-strong-shared-secret",
  "clockSkewSeconds": 30,
  "tokenTtlSeconds": 3600,
  "fallbackPolicy": "SSO_REQUIRED",
  "claimMappings": [
    {
      "externalClaim": "sub",
      "internalField": "respondentId",
      "required": true
    },
    {
      "externalClaim": "email",
      "internalField": "email",
      "required": false
    }
  ]
}
```

## Step B: Create private campaign

Use same campaign setup as OIDC private path.

### Endpoint

`POST /api/v1/campaigns`

### Key field

`authMode` = `PRIVATE`

### Example request

```json
{
  "name": "Partner Feedback - Private",
  "description": "Legacy identity fallback",
  "surveyId": "2d9bfa6c-2f01-4b4d-b4f1-129eb71a3dcb",
  "authMode": "PRIVATE"
}
```

## Step C: Activate campaign

Endpoint: `POST /api/v1/campaigns/{id}/activate`

No request body required.

## What happens after setup

1. Subscriber system provides signed JWT (`responderToken`) to responder.
2. Responder submits answers with `responderToken`.
3. Survey Engine validates signature, claims, and replay (`jti`).
4. Valid token -> response accepted and locked.

---

## Provider template helper for admin onboarding

To simplify OIDC setup, admin can fetch template guidance.

### Endpoints

1. `GET /api/v1/auth/providers/templates`
2. `GET /api/v1/auth/providers/templates/{providerCode}`

### Common provider codes

1. `OKTA`
2. `AUTH0`
3. `AZURE_AD`
4. `KEYCLOAK`

These endpoints return default scopes, default claim mappings, and required config fields.

---

## Check and audit configuration

Use these after any config change:

1. Read profile: `GET /api/v1/auth/profiles/tenant/{tenantId}`
2. Read audit history: `GET /api/v1/auth/profiles/{id}/audit`
3. Optional key rotation: `POST /api/v1/auth/profiles/{id}/rotate-key`

---

## Practical checklist for admins

## Public path checklist

1. Campaign `authMode=PUBLIC`.
2. Campaign activated.
3. Test submit without token.

## Private OIDC checklist

1. Tenant profile `authMode=EXTERNAL_SSO_TRUST`.
2. OIDC fields set correctly.
3. Claim mapping contains required `respondentId`.
4. Campaign `authMode=PRIVATE` and activated.
5. End-to-end callback and submit test successful.

## Private Signed Token checklist

1. Tenant profile `authMode=SIGNED_LAUNCH_TOKEN`.
2. Strong `signingSecret` configured.
3. Claim mapping contains required `respondentId`.
4. Campaign `authMode=PRIVATE` and activated.
5. Signed token submit test successful.
