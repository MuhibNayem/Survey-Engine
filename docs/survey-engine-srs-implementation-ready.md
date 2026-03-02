# Software Requirements Specification (SRS)
## Product: Headless Multi-Tenant Survey Engine (SaaS)
## Version: 1.1 (Implementation-Ready)
## Date: March 2, 2026
## Prepared For: Engineering, QA, Security, and Delivery

## 1. Document Control
- Status: Draft for implementation kickoff
- Intended audience: Product, Engineering, QA, DevOps, Security, Compliance
- Related docs:
  - `survey-engine-prd.md`
  - `survey-engine-srs.md`

## 2. Purpose and Scope
This document defines implementable requirements for delivering a non-AI, headless, multi-tenant survey SaaS platform with clear feature boundaries, interfaces, data model expectations, and testable acceptance criteria.

In scope:
- Tenant/workspace management
- Survey authoring and runtime
- Response capture and reporting
- Generic campaign orchestration and policy-driven assignment
- Audience/roster ingestion from external systems
- Distribution (link/embed/email)
- Security/governance baseline
- Headless APIs and webhooks
- Subscription-time tenant bootstrap onboarding with external IdP authentication

Out of scope (for v1):
- AI authoring and AI analytics
- Native mobile apps
- Third-party marketplace ecosystem

## 3. Release Scope and Prioritization

### 3.1 MoSCoW Priorities
Must have (v1):
- Multi-tenant isolation and workspace model
- External IdP authentication (OIDC/SAML) with tenant bootstrap flow
- Survey builder with required question types
- Campaign windows + audience assignment + eligibility constraints
- Anonymity mode with anti-duplicate enforcement
- Conditional logic and required validation
- Publish/version lifecycle (draft/published)
- Runtime controls (quota, close date, progress, back button, session timeout)
- Link/embed/email distribution
- Response capture (partial + complete)
- Dashboard + question breakdown + exports
- Hierarchical rollup reporting
- RBAC baseline, audit logs, encryption, retention controls
- Versioned REST APIs + webhooks + API docs

Should have (v1.x):
- Approval workflow before publish
- Scheduled report exports
- Additional connector set (CRM/chat)
- Enhanced anti-fraud scoring

Could have (future):
- Template marketplace
- Regional residency choices per tenant self-service

Won’t have (v1):
- AI features

### 3.2 Release Gates
- Gate A: Architecture + security sign-off
- Gate B: Feature-complete + integration testing
- Gate C: Performance/reliability certification
- Gate D: UAT and go-live readiness

## 4. Stakeholders and User Roles
- Tenant Admin: tenant configuration, policies, users, limits
- Workspace Admin: workspace users, survey access, approvals
- Survey Manager: authoring, publishing, distribution
- Analyst: dashboards, segments, exports
- Viewer: read-only reporting
- Respondent: survey completion

## 5. System Context and High-Level Architecture
Core components:
- API Gateway/Auth
- Tenant and Access Service
- Survey Definition Service
- Logic/Validation Engine
- Survey Runtime Service
- Response Ingestion Service
- Analytics/Reporting Service
- Integration/Webhook Service
- Notification Service
- Object Storage Adapter
- Audit/Observability Stack

Architecture constraints:
- All tenant-bound requests require tenant context.
- Service-to-service communication must propagate trace and tenant metadata.
- Soft delete default for business entities (hard delete only policy-driven jobs).

## 6. Functional Requirements

### 6.1 Tenant and Workspace
- FR-001: Create tenant with unique tenant key.
- FR-002: Create/update/delete workspaces within tenant.
- FR-003: Enforce tenant and workspace scoping for all resources.
- FR-004: Configure plan quotas (surveys, responses/month, storage, users).
- FR-005: Expose usage metrics by billing period.

### 6.2 Identity, Access, and Governance
- FR-010: Delegate admin/manager/analyst authentication to external customer IdP (OIDC/SAML).
- FR-011: Support RBAC roles: Tenant Admin, Workspace Admin, Manager, Analyst, Viewer.
- FR-012: Enforce permission checks on all write operations.
- FR-013: Record immutable audit events for privileged actions.
- FR-014: Support claim/group-to-role mapping configuration per tenant.
- FR-015: Support Just-In-Time (JIT) user provisioning from trusted IdP claims.
- FR-016: Keep authorization and policy enforcement inside Survey Engine even when authentication is external.

### 6.3 Survey Authoring
- FR-020: Create survey in draft state.
- FR-021: Configure survey type: single-page or multi-page.
- FR-022: Add/edit/delete/reorder sections and questions.
- FR-023: Set mandatory/optional at question level.
- FR-024: Supported question types:
  - Free text
  - Multiple choice (single/multiple)
  - Matrix/Likert
  - Rank
  - Rating scale
- FR-025: Attach rich media references (image/video/GIF/MP3).
- FR-026: Add helper text/notes/comments.
- FR-027: Define logic rules (skip/show/hide) with condition builder.
- FR-028: Validate logic graph before publish.

### 6.4 Respondent Metadata Fields
- FR-030: Provide fields: Name, Email, Address, Phone, Date/Time, Number, URL, File Upload.
- FR-031: Configurable validation (regex/range/format/required).
- FR-032: File upload policy by tenant (types, size, scan requirement hook).

### 6.5 Styling and Templates
- FR-040: Apply built-in themes.
- FR-041: Configure custom style tokens (color, font, spacing, radius).
- FR-042: Configure header/footer content.
- FR-043: Style preview must match runtime output.

### 6.6 Runtime and Survey Settings
- FR-050: Password-protected survey option.
- FR-051: CAPTCHA at start or submission.
- FR-052: One response per device/browser fingerprint mode (best-effort).
- FR-053: IP allow/deny policy.
- FR-054: Email allow/deny policy.
- FR-055: Closing rule by date/time and/or quota.
- FR-056: Toggle question numbering, progress bar, back button.
- FR-057: Session timeout with reset behavior.
- FR-058: Start and finish page custom messages.
- FR-059: Language pack selection and runtime text localization.

### 6.7 Publish, Versioning, and Approval
- FR-060: Draft cannot collect responses.
- FR-061: Publish creates immutable version snapshot.
- FR-062: New edits create new draft from latest version.
- FR-063: Optional approval workflow (Workspace Admin approval).
- FR-064: Track version history and published timestamps.

### 6.8 Distribution and Integrations
- FR-070: Generate direct link for each published survey version.
- FR-071: Generate embed snippet (iframe and JS SDK modes).
- FR-072: Send email invitations via provider integration.
- FR-073: Emit webhooks for events:
  - survey.published
  - response.started
  - response.completed
  - response.flagged
- FR-074: Retry failed webhook deliveries with exponential backoff.

### 6.9 Response Capture and Quality
- FR-080: Save partial responses when enabled.
- FR-081: Mark responses as complete/incomplete.
- FR-082: Store respondent metadata and technical metadata.
- FR-083: Duplicate detection modes: strict (email/device) and relaxed (heuristic).
- FR-084: Speeding checks with configurable threshold.
- FR-085: Quality flags shown in reporting and export.

### 6.10 Reporting and Analytics
- FR-090: Overview metrics: traffic by time, completions, incompletions, completion rate.
- FR-091: Question-by-question breakdown with counts and percentages.
- FR-092: Participant table with metadata and status.
- FR-093: Filters by date, segment, and metadata fields.
- FR-094: Comparative views across two selected time windows.
- FR-095: Exports in CSV/JSON; XLSX in v1.x.
- FR-096: Hierarchical rollup reporting across configurable dimensions (e.g., department/program/region/branch).

### 6.11 Campaigns, Audience Sync, Assignment, and Eligibility
- FR-100: Create campaign with configured start/end windows and closure conditions.
- FR-101: Bind campaign to one or more survey versions.
- FR-102: Ingest audience/roster records via API and CSV import.
- FR-103: Support connector-based roster sync jobs with execution status and error logs.
- FR-104: Define evaluator-target assignment policies using rule expressions.
- FR-105: Enforce attempt constraints using uniqueness keys (e.g., evaluator + target + campaign).
- FR-106: Support policy templates reusable across tenants/workspaces.
- FR-107: Maintain source lineage for synced roster records.

### 6.12 Privacy and Anonymity Controls
- FR-110: Support anonymity mode at campaign or survey level.
- FR-111: In anonymity mode, reporting and exports shall exclude direct respondent identity attributes.
- FR-112: In anonymity mode, dedup and eligibility checks shall continue using protected identity keys.
- FR-113: Support role-restricted access to identity-linking data for approved compliance users only.
- FR-114: Provide anonymity threshold controls (minimum sample size before segment display/export).

### 6.13 API and DX
- FR-120: Versioned REST API under `/api/v1`.
- FR-121: Auth via OAuth2 client credentials and scoped API keys.
- FR-122: Idempotency key support for create endpoints.
- FR-123: Standard error format with machine-readable codes.
- FR-124: OpenAPI spec published and versioned.
- FR-125: JS/TS SDK covering survey CRUD, campaigns, responses, and reports.

### 6.14 Subscription Onboarding and Bootstrap
- FR-130: On subscription, system shall create tenant and default workspace in `onboarding` status.
- FR-131: System shall provide onboarding wizard APIs/UI for:
  - IdP metadata registration (OIDC/SAML)
  - claim mapping (`subject`, `email`, `groups`, tenant context)
  - group/claim-to-role mapping
- FR-132: System shall provide IdP connection test before activation.
- FR-133: First successful mapped admin login shall activate tenant and set initial Tenant Admin.
- FR-134: System shall support setup-later mode with time-limited bootstrap admin access.
- FR-135: System shall enforce completion of IdP setup before production publish/distribution actions when setup-later mode expires.

## 7. Use Cases (Detailed)

### UC-01 Create and Publish Survey
- Actor: Survey Manager
- Preconditions: Authenticated; `survey:write` permission.
- Main flow:
  1. Create draft survey.
  2. Add questions and metadata fields.
  3. Configure logic and settings.
  4. Run validation.
  5. Submit for approval (optional) or publish.
- Alternate flows:
  - Validation fails: system returns exact question/rule references.
  - Approval required: status becomes `pending_approval`.
- Postconditions: Immutable published version exists; link/embed available.

### UC-02 Respondent Completes Survey
- Actor: Respondent
- Preconditions: Survey published and open.
- Main flow:
  1. Open survey link/embed.
  2. Pass access checks (password/CAPTCHA/IP/email rules).
  3. Submit answers page by page.
  4. Submit final response.
- Alternate flows:
  - Timeout: session reset per config.
  - Quota reached mid-session: show closure message, prevent submit.
- Postconditions: Response stored with `complete` status and timestamps.

### UC-03 Analyst Reviews Results
- Actor: Analyst
- Preconditions: `report:read` permission.
- Main flow:
  1. Open dashboard.
  2. Apply date and segment filters.
  3. View question breakdown and participant table.
  4. Export data.
- Postconditions: Export audit event recorded.

### UC-04 API Client Ingests Webhooks
- Actor: External System
- Preconditions: Webhook endpoint registered and verified.
- Main flow:
  1. Platform emits `response.completed` event.
  2. Endpoint receives signed payload.
  3. External system acknowledges 2xx.
- Alternate flow:
  - Non-2xx: retry with backoff; dead-letter after max attempts.

### UC-05 Tenant Subscription Bootstrap
- Actor: Subscriber Admin + Customer IT/IdP Admin
- Preconditions: New subscription created.
- Main flow:
  1. Platform creates tenant in `onboarding` state.
  2. Subscriber opens onboarding wizard.
  3. IdP config and claim mappings are submitted.
  4. System validates IdP and mapping.
  5. First successful external login assigns Tenant Admin and activates tenant.
- Alternate flows:
  - Setup-later selected: temporary bootstrap access granted with expiry.
  - Invalid claims mapping: onboarding remains incomplete; publish/distribution blocked.
- Postconditions: Tenant is active with external authentication and internal RBAC mapping.

### UC-06 Campaign Setup with Assignment Rules
- Actor: Survey Manager
- Preconditions: Published survey version available; campaign write permission.
- Main flow:
  1. Create campaign and select active window.
  2. Attach survey version to campaign.
  3. Import/sync audience roster.
  4. Configure evaluator-target assignment rules.
  5. Configure eligibility key and attempt constraints.
  6. Activate campaign.
- Alternate flows:
  - Roster sync errors: invalid records are quarantined with error report.
  - Assignment gaps: activation blocked until unresolved mappings are fixed.
- Postconditions: Eligible evaluator-target pairs are generated for runtime enforcement.

### UC-07 Anonymous Evaluation with Controlled Reporting
- Actor: Respondent and Analyst
- Preconditions: Campaign anonymity mode enabled.
- Main flow:
  1. Respondent submits evaluation.
  2. System records protected identity key for dedup/eligibility only.
  3. Analyst views aggregated reports without direct identity attributes.
- Alternate flows:
  - Segment sample below threshold: segment details suppressed.
- Postconditions: Confidentiality preserved while maintaining response integrity controls.

## 8. Data Model Baseline

### 8.1 Core Entities
- Tenant(id, name, status, plan_id, created_at)
- Workspace(id, tenant_id, name, created_at)
- User(id, tenant_id, email, status)
- RoleAssignment(id, user_id, workspace_id, role)
- Survey(id, workspace_id, title, mode, status, current_version)
- SurveyVersion(id, survey_id, version_no, schema_json, settings_json, published_at)
- Question(id, survey_version_id, type, config_json, position)
- LogicRule(id, survey_version_id, expression_json, action_json)
- Response(id, survey_version_id, status, started_at, completed_at, quality_flags)
- Answer(id, response_id, question_id, value_json)
- ParticipantMetadata(id, response_id, key, value)
- Campaign(id, workspace_id, name, status, start_at, end_at, closure_rules_json)
- CampaignSurveyBinding(id, campaign_id, survey_version_id)
- AudienceRecord(id, tenant_id, source_type, source_ref, external_subject_id, attributes_json, hash_key)
- AssignmentRule(id, campaign_id, evaluator_filter_json, target_filter_json, policy_json)
- AssignmentInstance(id, campaign_id, evaluator_id, target_id, status, constraint_key)
- EligibilityConstraint(id, campaign_id, key_template, max_attempts, policy_json)
- AuditEvent(id, tenant_id, actor_id, action, resource_type, resource_id, payload, created_at)
- WebhookSubscription(id, tenant_id, event_type, endpoint, secret, status)
- WebhookDelivery(id, subscription_id, event_id, attempt, status, response_code)
- TenantIdentityConfig(id, tenant_id, protocol, issuer, metadata_json, mapping_json, status, validated_at)
- TenantOnboardingState(id, tenant_id, status, bootstrap_expires_at, completed_at)

### 8.2 Data Classification
- Highly sensitive: PII fields, response content
- Sensitive: authentication events, audit events
- Internal: aggregated metrics
- Restricted: identity-linking keys used for anonymity-preserving dedup and eligibility checks

### 8.3 Retention Matrix (v1 defaults)
- Responses: 24 months (tenant configurable)
- Audit logs: 24 months (non-configurable minimum 12)
- Webhook delivery logs: 90 days
- Media/uploads: aligned with response retention unless overridden
- Roster sync snapshots: 12 months (configurable)
- Identity-linking keys in anonymity mode: minimum required period only, separate restricted store

## 9. API Contract Baseline

### 9.1 Resource Endpoints (minimum)
- `POST /api/v1/surveys`
- `GET /api/v1/surveys/{id}`
- `PATCH /api/v1/surveys/{id}`
- `POST /api/v1/surveys/{id}/publish`
- `GET /api/v1/surveys/{id}/versions`
- `POST /api/v1/surveys/{id}/responses`
- `PATCH /api/v1/responses/{id}`
- `POST /api/v1/responses/{id}/complete`
- `GET /api/v1/reports/surveys/{id}/overview`
- `GET /api/v1/reports/surveys/{id}/questions`
- `GET /api/v1/reports/campaigns/{id}/rollups`
- `POST /api/v1/webhooks/subscriptions`
- `POST /api/v1/campaigns`
- `PATCH /api/v1/campaigns/{id}`
- `POST /api/v1/campaigns/{id}/activate`
- `POST /api/v1/campaigns/{id}/roster:import`
- `POST /api/v1/campaigns/{id}/roster:sync`
- `POST /api/v1/campaigns/{id}/assignment-rules`
- `POST /api/v1/tenants/{id}/identity-config`
- `POST /api/v1/tenants/{id}/identity-config/validate`
- `POST /api/v1/tenants/{id}/onboarding/activate`

### 9.2 API Error Contract
```json
{
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "Question logic contains unresolved target",
    "details": [{"field": "logic[2].targetQuestionId", "reason": "NOT_FOUND"}],
    "traceId": "01H..."
  }
}
```

### 9.3 API Standards
- Pagination: cursor-based
- Time format: ISO-8601 UTC
- Idempotency: `Idempotency-Key` header for POST create operations
- Rate limits: tenant and API key scoped

## 10. Permission Matrix
| Action | Tenant Admin | Workspace Admin | Manager | Analyst | Viewer |
|---|---|---|---|---|---|
| Manage tenant settings | Yes | No | No | No | No |
| Manage workspace users | Yes | Yes | No | No | No |
| Create/edit survey | Yes | Yes | Yes | No | No |
| Publish survey | Yes | Yes | Yes* | No | No |
| Approve survey (if enabled) | Yes | Yes | No | No | No |
| View analytics | Yes | Yes | Yes | Yes | Yes |
| Export data | Yes | Yes | Yes | Yes | No |
| Configure webhooks | Yes | Yes | Yes | No | No |

`*` publish permission may require approval policy.

## 11. Security and Compliance Requirements
- SEC-001: TLS 1.2+ mandatory for all traffic.
- SEC-002: AES-256 encryption at rest for PII and response stores.
- SEC-003: Secrets stored in managed secret vault.
- SEC-004: Password hashing with modern adaptive algorithm.
- SEC-005: Signed webhook payloads with rotating secrets.
- SEC-006: Audit logs are append-only.

Compliance mappings (minimum):
- GDPR Access/Portability -> export endpoints and admin tools
- GDPR Right to Erasure -> policy-driven delete/anonymize workflow
- Consent evidence -> timestamp + source tracking on consent fields
- Data minimization -> configurable metadata fields and retention

## 12. Non-Functional Requirements

### 12.1 Performance
- NFR-001: P95 runtime page load <= 2.5s for <= 200 concurrent respondents/survey.
- NFR-002: P95 response submit <= 500ms without file upload.
- NFR-003: Dashboard query <= 4s for datasets <= 1M responses per tenant.

### 12.2 Reliability and Availability
- NFR-010: 99.9% monthly availability.
- NFR-011: RPO <= 15 min; RTO <= 60 min.
- NFR-012: Webhook delivery success >= 99% excluding client-side failures.

### 12.3 Scalability
- NFR-020: Horizontal scaling for runtime and response ingestion.
- NFR-021: No single-tenant noisy-neighbor impact beyond throttling thresholds.

### 12.4 Observability
- NFR-030: Distributed traces with tenant and request correlation IDs.
- NFR-031: Alerts for error rate, latency, webhook backlog, queue depth.
- NFR-032: SLO dashboards exposed to operations.

## 13. DevOps and Deployment Requirements
- CI pipeline with unit, integration, contract, and security scanning gates.
- IaC-managed environments (dev/staging/prod).
- Blue/green or canary deployment for runtime and API services.
- Rollback playbooks and incident runbooks mandatory before production launch.

## 14. Test Strategy and Traceability

### 14.1 Test Types
- Unit tests for domain logic and validators
- Integration tests for API-service-database paths
- Contract tests for webhooks and SDK compatibility
- E2E tests for key user flows (UC-01 to UC-03)
- Performance/load tests for ingestion and reporting
- Security tests (SAST/DAST/dependency scans + authz abuse cases)

### 14.2 Requirement Traceability Matrix (sample)
| Requirement | Test ID | Test Type | Owner |
|---|---|---|---|
| FR-028 logic validation | T-LOG-001 | Unit/Integration | Backend |
| FR-061 immutable publish | T-PUB-003 | Integration/E2E | Backend/QA |
| FR-073 webhook events | T-WHK-002 | Contract | Platform |
| FR-085 quality flags | T-QLT-004 | Integration | Data |
| FR-105 eligibility constraints | T-ELG-006 | Integration/E2E | Backend/QA |
| FR-111 anonymized reporting | T-PRV-003 | Integration/Security | Data/Security |
| NFR-001 runtime latency | T-PERF-010 | Performance | SRE |
| SEC-006 append-only audit | T-SEC-007 | Security/Integration | Security |

## 15. Acceptance Criteria (Implementation Exit)
- AC-001: All Must-have requirements implemented and test-passed.
- AC-002: Zero open Critical and High security findings.
- AC-003: P95 latency and availability targets achieved in staging load test.
- AC-004: UAT sign-off from Product and two pilot tenants.
- AC-005: Runbooks, dashboards, and on-call readiness completed.
- AC-006: API docs and SDK examples published.

## 16. Open Decisions (Must Resolve Before Build Freeze)
- OD-001: Setup-later policy defaults (bootstrap access duration and restricted actions).
- OD-002: Device fingerprint policy and legal review by region.
- OD-003: Default retention overrides by paid plan.
- OD-004: Exact connector list in v1.x.
- OD-005: Quota handling behavior when exceeded mid-session.
- OD-006: Default anonymity threshold values for reporting/export suppression.

## 17. Implementation Plan (Suggested)
- Sprint 1-2: tenancy, external IdP bootstrap onboarding, RBAC, survey schema, draft lifecycle
- Sprint 3-4: runtime engine, campaigns, roster sync, assignment/eligibility enforcement
- Sprint 5: reporting rollups, webhooks, exports, audit, observability
- Sprint 6: performance hardening, security remediation, UAT

## 18. Appendix A: Non-Goals
- AI generation, summarization, and sentiment
- Native iOS/Android apps
- White-label reseller billing engine
