# Survey Engine Comprehensive Operations Runbook
## Product: Headless Multi-Tenant Survey Engine (MVP+)
## Version: 1.0
## Date: March 11, 2026

## 1. Purpose
This runbook is the operational source of truth for running Survey Engine in production.

It covers:
1. Platform operations and environment controls.
2. Every major feature domain and its operational procedures.
3. Security controls and hardening expectations.
4. Monitoring, alerting, and incident response.
5. Change management, backup, and recovery.

Primary reference specs:
1. `docs/mvp/lifecycle-flows.md`
2. `docs/mvp/survey-engine-mvp-srs.md`
3. `docs/mvp/API_REFERENCE.md`
4. `docs/mvp/enterprise-embed-auth-operations-runbook.md`

---

## 2. Audience and Ownership
1. Platform/SRE: infrastructure, rollout, scaling, incident response.
2. Backend engineering: API runtime, database, auth, migrations.
3. Security engineering: auth controls, key lifecycle, abuse controls.
4. Support operations: tenant-level troubleshooting.

Ownership model:
1. API runtime and release: Backend team.
2. Data platform and recovery: Platform/SRE.
3. Security hardening and policies: Security team.
4. Tenant onboarding run operations: Support + Product Ops.

---

## 3. Environment Model
1. Local: feature development and functional validation.
2. Staging: production-like validation and release candidate testing.
3. Production: live tenant traffic.

Minimum parity requirements between staging and production:
1. Same auth mode coverage (admin cookie and token mode, private responder auth).
2. Same core data stores (PostgreSQL, Redis, RabbitMQ where applicable).
3. Same proxy/security header behavior.
4. Same migration sequence and schema version.

---

## 4. Platform Dependencies and Critical Paths
Core components:
1. Survey Engine application (Spring Boot).
2. PostgreSQL (system of record).
3. Redis (cache and selected anti-abuse support).
4. Reverse proxy/API gateway/WAF.

Critical paths:
1. Admin authentication and tenant context resolution.
2. Campaign preview and responder entry paths.
3. Private responder auth exchange/session establishment.
4. Draft save/load and final submit.
5. Subscription enforcement on protected admin operations.

---

## 5. Security Baseline (Global)
1. HTTPS only in production.
2. HSTS enabled at app and edge.
3. JWT and refresh token cookie protections enabled.
4. CSRF enabled for admin browser mutation flows.
5. Tenant isolation enforced in service/repository and DB relationships.
6. Audit logging enabled for privileged and lifecycle-sensitive operations.
7. Role-based access control enforced (`SUPER_ADMIN`, `ADMIN`, `EDITOR`, `VIEWER`).

Responder/private embed security baseline:
1. One-time or signed credential exchange into server session.
2. Origin allowlist enforcement for exchange endpoint.
3. Exchange rate limit enforcement.
4. Session-bound responder CSRF for private mutation flows.
5. Exchange success/failure audit trails.

---

## 6. Configuration Baseline
Mandatory configuration groups:
1. `spring.datasource.*`
2. `spring.flyway.*`
3. `spring.data.redis.*`
4. `survey-engine.links.public-base-url`
5. `survey-engine.search.*`
6. `survey-engine.security.cookies.*` (admin cookies)
7. `survey-engine.auth.responder.*` (private responder session/exchange hardening)

Production responder auth variables (minimum):
1. `SURVEY_ENGINE_AUTH_RESPONDER_COOKIE_SAME_SITE=None`
2. `SURVEY_ENGINE_AUTH_RESPONDER_COOKIE_SECURE=true`
3. `SURVEY_ENGINE_AUTH_RESPONDER_CSRF_ENABLED=true`
4. `SURVEY_ENGINE_AUTH_RESPONDER_CSRF_SECRET=<strong-random-secret>`
5. `SURVEY_ENGINE_AUTH_RESPONDER_EXCHANGE_ENFORCE_ORIGIN_ALLOWLIST=true`
6. `SURVEY_ENGINE_AUTH_RESPONDER_EXCHANGE_ALLOWED_ORIGINS=<trusted-origins>`
7. `SURVEY_ENGINE_AUTH_RESPONDER_EXCHANGE_ALLOW_MISSING_ORIGIN=false`
8. `SURVEY_ENGINE_AUTH_RESPONDER_EXCHANGE_RATE_LIMIT_ENABLED=true`
9. `SURVEY_ENGINE_AUTH_RESPONDER_EXCHANGE_RATE_LIMIT_PER_MINUTE=<value>`

---

## 7. Deployment and Release Procedure
Pre-deploy:
1. Verify schema migration set and target DB version.
2. Verify production secrets and responder CSRF secret present.
3. Verify embed allowlist inventory with tenant operations.
4. Verify backward compatibility for current frontend build.
5. Verify smoke tests in staging.

Deploy:
1. Roll out app version using rolling strategy.
2. Observe startup logs for Flyway success and health readiness.
3. Validate actuator health and key API health checks.

Post-deploy validation:
1. Admin login/register/refresh/logout in cookie mode.
2. Headless token-mode auth login/refresh.
3. Public campaign preview fetch.
4. Private campaign auth exchange and session check.
5. Draft save/load and final submit.
6. Super-admin tenant listing and impersonation return flow.

Rollback:
1. If app regression, roll back app image first.
2. If migration introduces breaking behavior, follow DB rollback policy and emergency playbook.
3. Keep a release note of impact scope by tenant.

---

## 8. Feature Operations: Tenant Bootstrap and Admin Access
Core APIs:
1. `POST /api/v1/admin/auth/register`
2. `POST /api/v1/admin/auth/login`
3. `POST /api/v1/admin/auth/refresh`
4. `POST /api/v1/admin/auth/logout`
5. `GET /api/v1/admin/auth/me`
6. `GET /api/v1/admin/auth/csrf`

Routine checks:
1. Auth success/failure ratios.
2. Refresh rotation failures.
3. CSRF mismatch rate.

Failure playbook:
1. 401 spike: verify signing keys and token parsing path.
2. CSRF failures: verify token cookie rendering and frontend header propagation.
3. Missing tenant context: verify JWT tenant claim and filter chain.

---

## 9. Feature Operations: Subscription and Plan Controls
Core APIs:
1. `GET /api/v1/admin/subscriptions/me`
2. `POST /api/v1/admin/subscriptions/checkout`
3. `GET /api/v1/admin/plans`
4. `PUT /api/v1/admin/plans` (super-admin)

Routine checks:
1. 402 rate by tenant.
2. Plan update audit events.
3. Quota exceeded event trends.

Failure playbook:
1. Unexpected 402: validate subscription state and exemption rules.
2. Plan drift: verify latest plan catalog rows and update audit trails.

---

## 10. Feature Operations: Question and Category Bank
Core APIs:
1. `POST/GET/PUT/DELETE /api/v1/questions`
2. `POST/GET/PUT/DELETE /api/v1/categories`

Operational concerns:
1. Enforce option uniqueness and type-specific validation.
2. Ensure deactivation does not break historical responses.
3. Monitor validation failure trends for authoring UX quality.

---

## 11. Feature Operations: Survey Authoring and Lifecycle
Core APIs:
1. `POST /api/v1/surveys`
2. `PUT /api/v1/surveys/{id}`
3. `GET /api/v1/surveys/{id}`
4. `POST /api/v1/surveys/{id}/lifecycle`

Operational concerns:
1. Pinning integrity for question/category versions during draft edits.
2. Lifecycle transition validity enforcement.
3. Immutable behavior after publish.

Run checks:
1. Draft save and publish for a regression suite survey.
2. Confirm published survey blocks structural mutation.

---

## 12. Feature Operations: Campaign Management and Distribution
Core APIs:
1. `POST/GET/PUT/DELETE /api/v1/campaigns`
2. `POST /api/v1/campaigns/{id}/activate`
3. Distribution channel generation endpoints under campaign APIs.

Operational concerns:
1. Campaign activation only with published survey snapshot.
2. Runtime settings integrity (`closeDate`, quotas, restrictions, session timeout).
3. Distribution link generation consistency with `public-base-url`.

---

## 13. Feature Operations: Theme Studio and Runtime Theming
Operational scope:
1. Structured theme config persistence via campaign settings.
2. Admin preview payload and responder preview payload consistency.
3. Advanced override safety (`headerHtml`, `footerHtml`, custom CSS).

Run checks:
1. Theme save and preview for each template key/palette key.
2. Responder runtime rendering parity with admin preview.
3. Verify no regression in button semantic colors and readability.

---

## 14. Feature Operations: Tenant Auth Profile and Claim Mapping
Core APIs:
1. `POST /api/v1/auth/profiles`
2. `PUT /api/v1/auth/profiles/{id}`
3. `GET /api/v1/auth/profiles/tenant/{tenantId}`
4. `POST /api/v1/auth/profiles/{id}/rotate-key`
5. `GET /api/v1/auth/profiles/{id}/audit`
6. Provider template endpoints.

Operational concerns:
1. Claim mapping must include required responder identity mapping.
2. Key rotation must be audited and communicated.
3. Fallback policies must match tenant risk profile.

---

## 15. Feature Operations: Private Responder OIDC Flow
Core APIs:
1. `POST /api/v1/auth/respondent/oidc/start`
2. `GET /api/v1/auth/respondent/oidc/callback`
3. `POST /api/v1/public/campaigns/{id}/auth/exchange`
4. `GET /api/v1/public/campaigns/{id}/auth/session`
5. `POST /api/v1/public/campaigns/{id}/auth/logout`

Operational concerns:
1. OIDC metadata reachability and cache health.
2. State/nonce/code replay protection behavior.
3. Session cookie and responder CSRF cookie issuance.

Failure playbook:
1. Callback failures: validate discovery URL, redirect URI, nonce/state integrity.
2. Session not established: inspect exchange audits and cookie attributes.

---

## 16. Feature Operations: Signed Token and Embedded Session Exchange
Core APIs:
1. `POST /api/v1/public/campaigns/{id}/auth/exchange`
2. `GET /api/v1/public/campaigns/{id}/auth/session`

Hardening requirements:
1. Origin allowlist on exchange enabled.
2. Exchange rate limiting enabled.
3. Session-bound responder CSRF enabled.
4. Exchange success/failure audit events monitored.

Reference:
1. `docs/mvp/enterprise-embed-auth-operations-runbook.md`

---

## 17. Feature Operations: Response Lifecycle (Draft, Submit, Lock, Reopen)
Core APIs:
1. `POST /api/v1/public/campaigns/{id}/responses/draft`
2. `POST /api/v1/public/campaigns/{id}/responses/draft/load`
3. `POST /api/v1/responses`
4. `POST /api/v1/responses/{id}/lock`
5. `POST /api/v1/responses/{id}/reopen`

Operational checks:
1. Campaign status and runtime settings enforcement.
2. Private auth enforcement path (session/token/access code).
3. Duplicate response restrictions by device/IP/email according to settings.
4. Auto-lock behavior after submit.

Failure playbook:
1. Elevated `ACCESS_DENIED`: split by auth mode and reason.
2. Elevated duplicate responses: validate settings and fingerprint quality.
3. Reopen failures: verify lock state and reopen reason policy.

---

## 18. Feature Operations: Scoring and Weight Profiles
Core APIs:
1. Campaign default scoring on activation.
2. Advanced/manual weight profile APIs.

Operational concerns:
1. Category weights must sum to 100.
2. Score calculation correctness for pinned survey categories.
3. Scoring service latency and error rates.

---

## 19. Feature Operations: Reporting and Advanced Analytics
Core APIs:
1. Campaign analytics endpoints.
2. Advanced analytics endpoints under `/api/v1/analytics/**`.

Operational concerns:
1. Query performance under tenant-scale load.
2. Index and materialization health.
3. Rate limits and query timeout behavior for expensive analytics calls.

---

## 20. Feature Operations: Global Search
Operational concerns:
1. Tenant-scoped search relevance and isolation.
2. Rate limit behavior (`survey-engine.search.rate-limit-per-minute`).
3. Slow query and circuit breaker events.
4. Cache hit/miss and fallback behavior.

---

## 21. Feature Operations: Super-Admin Tenant Controls
Core capabilities:
1. Tenant listing and status management (suspend/activate).
2. Tenant impersonation and restore flow.
3. Subscription overrides.
4. Platform-wide audit query.

Operational concerns:
1. Strict `SUPER_ADMIN` authorization checks.
2. Impersonation cookie hygiene and restoration reliability.
3. Elevated security auditing for super-admin actions.

---

## 22. Feature Operations: Audit Log and Compliance Trails
Core APIs:
1. `GET /api/v1/audit-logs`
2. `GET /api/v1/admin/superadmin/audit-logs`

Operational requirements:
1. Audit records must be immutable at API level.
2. Retention and archival policy must be defined by compliance requirements.
3. Sensitive failure paths must log enough context for investigation without leaking secrets.

---

## 23. Feature Operations: Enterprise Feature Management and Onboarding
Scope:
1. Feature flags, tours, tooltips, announcements, dismiss tracking.
2. Tenant overrides and plan/role/rollout constraints.
3. First-login and onboarding state progress.
4. Admin appearance preference persistence.

Operational checks:
1. Feature targeting accuracy by tenant/role/plan.
2. Flag rollout impact on frontend performance and error rate.
3. Audit trace for feature governance changes.

---

## 24. Database Operations
Baseline:
1. Flyway migration discipline and one-way migration policy.
2. PITR/backups enabled.
3. Restore drills scheduled.

Daily checks:
1. DB connectivity, replication/backup status.
2. Slow query logs and index health.
3. Table growth for responses, audits, and auth replay/session tables.

---

## 25. Redis Operations
Scope:
1. Auth metadata/JWKS cache.
2. Search cache.
3. Responder exchange rate-limit counters.

Checks:
1. Cache availability and latency.
2. Keyspace growth and TTL effectiveness.
3. Fallback behavior when Redis unavailable.

---

## 26. Messaging Operations (If Enabled)
Scope:
1. RabbitMQ health and queue depth for async domains.
2. Dead-letter monitoring for failed processing.

---

## 27. Observability Standards
Required metrics:
1. Request volume, error rate, latency by endpoint family.
2. Auth failures by reason.
3. Exchange success/failure and rate-limit denials.
4. Submit/draft success and rejection causes.
5. Subscription enforcement blocks.

Required logs:
1. Structured error logs with request path and tenant context.
2. Security-relevant events (auth exchange, access denied patterns).

Required dashboards:
1. Admin auth health.
2. Responder private flow health.
3. Response ingestion and locking.
4. Billing/subscription enforcement health.

---

## 28. Alerting Policy
Starter alerts:
1. `POST /api/v1/responses` 5xx > 2% for 5 min.
2. Private auth exchange failure > threshold for 10 min.
3. Exchange rate-limit denials spike above baseline.
4. DB or Redis connection failures sustained > 2 min.
5. Subscription block anomaly for one tenant.

---

## 29. Incident Response Playbooks
### 29.1 Private campaign responder blocked
1. Validate campaign auth mode and active status.
2. Validate tenant auth profile and claim mapping.
3. Inspect exchange audits for failure reason.
4. Validate allowlist, headers (`Origin`, `Referer`, `X-Forwarded-For`), and rate-limit status.
5. Validate responder CSRF header/cookie propagation for draft/submit calls.

### 29.2 Elevated 401/403 on admin APIs
1. Validate auth cookie issuance and refresh cycle.
2. Validate CSRF bootstrap and header inclusion.
3. Validate role claims and tenant context extraction.

### 29.3 Elevated 402 Payment Required
1. Validate tenant subscription state and period.
2. Validate plan and quota config.
3. Confirm no unintended subscription status mutation.

### 29.4 Elevated 429 responses
1. Identify endpoint source.
2. Distinguish expected abuse control from misconfigured thresholds.
3. Adjust tenant-aware limits with approval and audit.

### 29.5 Data integrity concerns
1. Stop unsafe writes if needed.
2. Capture query evidence and affected IDs.
3. Run integrity checks.
4. Restore or reconcile with controlled script.

---

## 30. Backup, Restore, and DR
1. Daily full DB backup + continuous WAL/PITR.
2. Backup restore test at least monthly.
3. Defined RPO and RTO per environment tier.
4. Documented failover process and decision authority.

---

## 31. Capacity and Scaling
1. Monitor p95/p99 for responder submit and exchange endpoints.
2. Scale app instances before peak campaign windows.
3. Validate DB connection pool and query latency under load.
4. Validate Redis throughput under exchange and search load.

---

## 32. Change Management and Governance
1. Every production change must have rollback notes.
2. Security-sensitive config changes require security review.
3. Tenant-impacting auth changes require support communications.
4. Runbook updates required for any feature or control changes.

---

## 33. Production Acceptance Checklist
1. All critical health endpoints green.
2. Flyway schema up to expected version.
3. Admin auth flows pass.
4. Private responder exchange/session/draft/submit flows pass.
5. WAF and proxy header behavior verified.
6. Audit events visible for auth exchange success/failure.
7. Alert channels and on-call routing validated.
8. Restore test evidence available within policy window.

---

## 34. Related Documents
1. `docs/mvp/lifecycle-flows.md`
2. `docs/mvp/survey-engine-mvp-srs.md`
3. `docs/mvp/API_REFERENCE.md`
4. `docs/mvp/architecture-flow.md`
5. `docs/mvp/enterprise-embed-auth-operations-runbook.md`
