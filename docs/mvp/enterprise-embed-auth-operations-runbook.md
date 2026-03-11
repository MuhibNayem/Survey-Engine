# Enterprise Embed Auth Operations Runbook
## Version: 1.0
## Date: March 11, 2026

## Purpose
This runbook defines production operations controls for private responder authentication in embedded tenant experiences.

Scope:
1. `POST /api/v1/public/campaigns/{id}/auth/exchange`
2. `GET /api/v1/public/campaigns/{id}/auth/session`
3. `POST /api/v1/public/campaigns/{id}/auth/logout`
4. `POST /api/v1/public/campaigns/{id}/responses/draft`
5. `POST /api/v1/responses`

---

## 1. Required Runtime Configuration

Set these in production:

1. `SURVEY_ENGINE_AUTH_RESPONDER_COOKIE_SAME_SITE=None`
2. `SURVEY_ENGINE_AUTH_RESPONDER_COOKIE_SECURE=true`
3. `SURVEY_ENGINE_AUTH_RESPONDER_CSRF_ENABLED=true`
4. `SURVEY_ENGINE_AUTH_RESPONDER_CSRF_SECRET=<strong-random-secret>`
5. `SURVEY_ENGINE_AUTH_RESPONDER_EXCHANGE_ENFORCE_ORIGIN_ALLOWLIST=true`
6. `SURVEY_ENGINE_AUTH_RESPONDER_EXCHANGE_ALLOWED_ORIGINS=https://tenant-a.example.com,https://app.tenant-b.example.com`
7. `SURVEY_ENGINE_AUTH_RESPONDER_EXCHANGE_ALLOW_MISSING_ORIGIN=false`
8. `SURVEY_ENGINE_AUTH_RESPONDER_EXCHANGE_RATE_LIMIT_ENABLED=true`
9. `SURVEY_ENGINE_AUTH_RESPONDER_EXCHANGE_RATE_LIMIT_PER_MINUTE=30`

Notes:
1. `SameSite=None` without `Secure=true` is invalid and startup will fail.
2. If origin allowlist enforcement is enabled and the allowlist is empty, startup will fail.

---

## 2. Reverse Proxy / Gateway Baseline

### Required behavior
1. Preserve `Origin`, `Referer`, and `X-Forwarded-For`.
2. Preserve `Set-Cookie` from backend unchanged.
3. Enforce HTTPS and HSTS at edge.
4. Do not strip CSP response headers.

### NGINX example
```nginx
location /api/v1/ {
  proxy_pass http://survey-engine:8080;
  proxy_set_header Host $host;
  proxy_set_header X-Real-IP $remote_addr;
  proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
  proxy_set_header X-Forwarded-Proto https;
  proxy_set_header Origin $http_origin;
  proxy_set_header Referer $http_referer;
}
```

---

## 3. CORS Policy (Only if Cross-Origin API Calls Are Used)

Preferred model:
1. Load survey runtime inside iframe from survey domain.
2. Let iframe call its own origin API.
3. Avoid parent-page direct API calls to survey backend.

If parent-page cross-origin API calls are unavoidable:
1. Allow only explicit tenant origins.
2. Enable credentials.
3. Never use `*` with credentials.
4. Allow header `X-Responder-Csrf`.

---

## 4. WAF / API Gateway Rules

Apply route-specific rules:

1. Path: `/api/v1/public/campaigns/*/auth/exchange`
- Method allowlist: `POST` only
- Rate limit: start at `30/min` per source IP
- Block requests with empty body
- Alert on spikes in `403` and `429`

2. Path: `/api/v1/public/campaigns/*/responses/draft`, `/api/v1/responses`
- Method allowlist: `POST` only
- For private flows using session cookie: require header `X-Responder-Csrf`
- Reject obviously malformed JSON at edge

3. Bot controls:
- Enable managed bot protection for public responder endpoints
- Add country or ASN controls only if tenant policy requires it

---

## 5. Security Header Validation

Backend emits CSP with `frame-ancestors` derived from configured embed allowlist.

Ops checks:
1. Confirm `Content-Security-Policy` contains only approved tenant origins.
2. Confirm no proxy overrides `frame-ancestors`.
3. Confirm `Strict-Transport-Security` present on HTTPS responses.

Verification command:
```bash
curl -I https://survey.example.com/api/v1/public/campaigns/<campaign-id>/preview
```

---

## 6. Logging and Monitoring

Audit actions emitted:
1. `RESPONDER_AUTH_EXCHANGE_SUCCEEDED`
2. `RESPONDER_AUTH_EXCHANGE_FAILED`

Minimum dashboards:
1. Exchange success rate by tenant and campaign
2. Exchange 403/429 rate
3. Draft/submit CSRF rejection count
4. Top source IPs by exchange attempts

Minimum alerts:
1. Exchange success rate drops below 90% for 10 minutes
2. Exchange 429 rate exceeds baseline by 3x
3. Sudden spike in `RESPONDER_AUTH_EXCHANGE_FAILED` for one tenant

---

## 7. Incident Response (Private Responder Auth)

1. Validate config state:
- allowlist enabled
- allowed origins list is correct
- CSRF secret is set

2. Check headers reaching backend:
- `Origin`
- `Referer`
- `X-Forwarded-For`

3. Inspect latest exchange audit events:
- identify failure reason (`Origin not allowed`, `rate limit exceeded`, token validation errors)

4. If WAF false positive:
- apply temporary tenant-scoped exception
- keep backend controls enabled

5. Post-incident:
- update allowlist/runbook
- add regression test for discovered pattern

---

## 8. Change Management

Before deploy:
1. Confirm production env vars are populated.
2. Confirm embed origins inventory is reviewed by security.
3. Validate in staging with a real embedded tenant domain.

After deploy:
1. Monitor 403/429 and audit failure trends for 24 hours.
2. Roll back only edge rules first if outage is caused by gateway mismatch.

