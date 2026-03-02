# Survey Engine Architecture Flows
## Product: Headless Multi-Tenant Survey Engine (SaaS)
## Version: 1.0
## Date: March 2, 2026

## 1. Purpose
This document describes implementation-facing system flows using both ASCII and Mermaid diagrams. It aligns with:
- `survey-engine-srs.md`
- `survey-engine-srs-implementation-ready.md`

Scope covered:
- Subscription bootstrap onboarding
- External authentication and internal authorization (RBAC)
- Survey authoring and publish lifecycle
- Campaign orchestration, roster sync, and assignment policy execution
- Respondent runtime and response ingestion
- Reporting and analytics
- Anonymity-preserving reporting and eligibility enforcement
- Webhook delivery and retries
- Setup-later fallback and expiry enforcement

---

## 2. Architecture Overview

### 2.1 ASCII Component View
```text
+------------------------+       +------------------------+
| Customer IdP (OIDC/    |       | Customer Systems       |
| SAML)                  |       | (CRM, Slack, ETL, etc) |
+-----------+------------+       +------------+-----------+
            |                                 ^
            | tokens/assertions               | webhooks/events
            v                                 |
+-----------+--------------------------------------------------+
|                 Survey Engine Platform                        |
|                                                               |
|  +-------------------+    +-------------------------------+   |
|  | API Gateway/Auth  |<-->| Tenant & Access Service      |   |
|  | (token verify)    |    | (RBAC, policies, onboarding) |   |
|  +---------+---------+    +---------------+---------------+   |
|            |                              |                   |
|            v                              v                   |
|  +-------------------+    +-------------------------------+   |
|  | Survey Definition |<-->| Rules/Validation Engine       |   |
|  | Service           |    | (logic graph, constraints)    |   |
|  +---------+---------+    +---------------+---------------+   |
|            |                              |                   |
|            v                              v                   |
|  +-------------------+    +-------------------------------+   |
|  | Survey Runtime    |--->| Response Ingestion Service    |   |
|  | Service           |    | (partial/complete, quality)   |   |
|  +---------+---------+    +---------------+---------------+   |
|            |                              |                   |
|            v                              v                   |
|  +-------------------+    +-------------------------------+   |
|  | Campaign &        |<-->| Audience/Roster Adapter       |   |
|  | Assignment Engine |    | (API/CSV/connectors)          |   |
|  +---------+---------+    +---------------+---------------+   |
|            |                              |                   |
|            v                              v                   |
|  +-------------------+    +-------------------------------+   |
|  | Analytics/Report  |<---| Data Stores (OLTP + analytics)|   |
|  | Service           |    | + object storage              |   |
|  +---------+---------+    +---------------+---------------+   |
|            |                              |                   |
|            v                              v                   |
|  +-------------------+    +-------------------------------+   |
|  | Integration &     |--->| Notification Service          |   |
|  | Webhook Service   |    | (email/invite)               |   |
|  +-------------------+    +-------------------------------+   |
|                                                               |
+---------------------------------------------------------------+
            ^
            |
            | REST/SDK/Embed
            |
+-----------+------------+
| Admin UI / SDK / Embed |
+------------------------+
```

### 2.2 Mermaid Component Diagram
```mermaid
graph LR
  IdP[Customer IdP\nOIDC/SAML]
  Ext[Customer Systems\nCRM/Slack/ETL]
  UI[Admin UI / SDK / Embed]

  GW[API Gateway & Auth]
  TAS[Tenant & Access Service\nRBAC + Onboarding]
  SDS[Survey Definition Service]
  RVE[Rules & Validation Engine]
  SRS[Survey Runtime Service]
  RIS[Response Ingestion Service]
  CAE[Campaign & Assignment Engine]
  RAS[Audience/Roster Adapter]
  ARS[Analytics & Reporting Service]
  IWS[Integration & Webhook Service]
  NS[Notification Service]
  DB[(OLTP DB)]
  DWH[(Analytics Store)]
  OBJ[(Object Storage)]

  UI --> GW
  IdP --> GW
  GW <--> TAS
  GW --> SDS
  SDS <--> RVE
  GW --> SRS
  SRS --> RIS
  SDS --> CAE
  CAE <--> RAS
  CAE --> RIS
  RIS --> DB
  RIS --> DWH
  RIS --> OBJ
  ARS --> DWH
  ARS --> DB
  IWS --> Ext
  NS --> Ext
  TAS --> DB
  SDS --> DB
```

---

## 3. Flow: Subscription Bootstrap Onboarding

### 3.1 Intent
Enable a new subscriber to configure external authentication and activate tenant administration without platform-team manual steps.

### 3.2 ASCII Sequence
```text
Subscriber Admin     Subscription Svc   Survey Engine     Customer IdP
      |                     |                |                 |
1. Subscribe                |                |                 |
      |-------------------->|                |                 |
2. Create tenant(workspace,onboarding)       |                 |
      |                     |--------------->|                 |
3. Return onboarding URL                     |                 |
      |<--------------------|                |                 |
4. Open onboarding wizard                    |                 |
      |------------------------------------->|                 |
5. Enter IdP metadata + claim mappings       |                 |
      |------------------------------------->|                 |
6. Validate IdP config                                         |
      |------------------------------------->|---------------->|
      |                                      |<----------------|
7. Validation success/failure                |                 |
      |<-------------------------------------|                 |
8. First SSO login                           |                 |
      |------------------------------------->|---------------->|
      |                                      |<----------------|
9. Map claims->role (TenantAdmin), activate tenant            |
      |                                      |                 |
10. Access admin console                     |                 |
      |<-------------------------------------|                 |
```

### 3.3 Mermaid Sequence
```mermaid
sequenceDiagram
  autonumber
  participant SA as Subscriber Admin
  participant SS as Subscription Service
  participant SE as Survey Engine
  participant IDP as Customer IdP

  SA->>SS: Subscribe plan
  SS->>SE: Create tenant + default workspace (onboarding)
  SS-->>SA: Onboarding URL
  SA->>SE: Open onboarding wizard
  SA->>SE: Submit IdP metadata + claim mappings
  SE->>IDP: Validate OIDC/SAML config
  IDP-->>SE: Validation response
  SE-->>SA: Show validation result
  SA->>SE: Continue with first SSO login
  SE->>IDP: Redirect + auth request
  IDP-->>SE: Signed token/assertion
  SE->>SE: Map claims/groups to TenantAdmin
  SE->>SE: Activate tenant
  SE-->>SA: Admin console access granted
```

---

## 4. Flow: External Authentication + Internal RBAC Authorization

### 4.1 Key Rule
Authentication is external. Authorization is internal.

### 4.2 ASCII Decision Flow
```text
[Request to Admin API]
          |
          v
[Verify token/assertion with IdP trust config]
          |
    +-----+-----+
    | valid?    |
    +-----+-----+
          |yes
          v
[Extract claims: subject,email,groups,tenant_hint]
          |
          v
[Resolve tenant + workspace context]
          |
    +-----+-----+
    | mapped?   |
    +-----+-----+
          |yes
          v
[Map claims/groups -> platform role]
          |
          v
[Check permission for action]
          |
    +-----+-----+
    | allowed?  |
    +-----+-----+
      |yes |no
      v    v
   [200] [403]

invalid token -> [401]
unmapped tenant -> [403]
expired setup-later bootstrap -> [423/403]
```

### 4.3 Mermaid Flowchart
```mermaid
flowchart TD
  A[Admin/API Request] --> B[Validate token/assertion]
  B -->|Invalid| X1[401 Unauthorized]
  B -->|Valid| C[Extract claims/groups]
  C --> D[Resolve tenant/workspace context]
  D -->|Not mapped| X2[403 Forbidden]
  D -->|Mapped| E[Map to role]
  E --> F{Permission granted?}
  F -->|Yes| G[Allow request]
  F -->|No| X3[403 Forbidden]
  D -->|Bootstrap expired| X4[423 Locked or 403]
```

---

## 5. Flow: Survey Authoring and Publish Lifecycle

### 5.1 ASCII Sequence
```text
Manager/Admin       Admin UI        Survey Definition    Rules Engine
    |                  |                    |                |
1. Create draft        |                    |                |
    |----------------->|------------------->|                |
2. Add questions/media/logic               |                |
    |----------------->|------------------->|                |
3. Validate survey schema and logic graph                   |
    |----------------->|------------------->|--------------->|
    |                  |                    |<---------------|
4. Save draft/version                      |                |
    |                  |<-------------------|                |
5. Publish request                          |                |
    |----------------->|------------------->|                |
6. Optional approval path                   |                |
7. Publish immutable version                |                |
    |                  |<-------------------|                |
8. Generate link/embed config               |                |
```

### 5.2 Mermaid Sequence
```mermaid
sequenceDiagram
  autonumber
  participant M as Manager/Admin
  participant UI as Admin UI
  participant SDS as Survey Definition Service
  participant RVE as Rules Engine

  M->>UI: Create draft survey
  UI->>SDS: POST draft
  M->>UI: Add questions/media/logic
  UI->>SDS: PATCH draft schema
  UI->>RVE: Validate logic + constraints
  RVE-->>UI: Validation result
  UI->>SDS: Save draft
  M->>UI: Publish request
  UI->>SDS: Publish current draft
  SDS->>SDS: Create immutable version snapshot
  SDS-->>UI: Publish success + link/embed metadata
```

---

## 6. Flow: Respondent Runtime and Response Ingestion

### 6.1 ASCII Sequence
```text
Respondent      Runtime Service     Rules Engine    Response Ingestion   Storage
    |                 |                 |                 |               |
1. Open link/embed    |                 |                 |               |
    |---------------->|                 |                 |               |
2. Access checks (password/captcha/ip/email/quota/time)                  |
    |                 |---------------> |                 |               |
    |                 |<--------------- |                 |               |
3. Render first page with logic                               
    |<----------------|                 |                 |               |
4. Submit answers/page
    |---------------->|                 |                 |               |
5. Validate answer + apply skip/display logic
    |                 |---------------> |                 |               |
    |                 |<--------------- |                 |               |
6. Save partial (optional)
    |                 |----------------------------------->|------------->|
7. Final submit
    |---------------->|----------------------------------->|------------->|
8. Mark complete + quality flags
    |                 |                                    |------------->|
9. Show finish message
    |<----------------|                                    |               |
```

### 6.2 Mermaid Sequence
```mermaid
sequenceDiagram
  autonumber
  participant R as Respondent
  participant RT as Runtime Service
  participant RE as Rules Engine
  participant RI as Response Ingestion
  participant DB as Data Stores

  R->>RT: Open survey link/embed
  RT->>RE: Evaluate access controls + status checks
  RE-->>RT: Allowed/blocked
  RT-->>R: Render survey page
  R->>RT: Submit page answers
  RT->>RE: Validate + evaluate logic
  RE-->>RT: Next page/field rules
  RT->>RI: Save partial response (if enabled)
  RI->>DB: Persist partial
  R->>RT: Final submit
  RT->>RI: Complete response
  RI->>RI: Compute quality flags
  RI->>DB: Persist final response + metadata
  RT-->>R: Finish message
```

---

## 7. Flow: Campaign Setup, Roster Sync, and Assignment

### 7.1 ASCII Sequence
```text
Manager/Admin      Campaign Engine      Roster Adapter      External Source
    |                    |                    |                    |
1. Create campaign       |                    |                    |
    |------------------->|                    |                    |
2. Attach survey version |                    |                    |
    |------------------->|                    |                    |
3. Configure rules (evaluator->target, constraints)             |
    |------------------->|                    |                    |
4. Trigger roster sync                        |                    |
    |------------------->|------------------->|------------------->|
    |                    |                    |<-------------------|
5. Validate/map records  |<-------------------|                    |
6. Generate assignment instances + constraint keys               |
    |<-------------------|                    |                    |
7. Activate campaign     |                    |                    |
    |------------------->|                    |                    |
```

### 7.2 Mermaid Sequence
```mermaid
sequenceDiagram
  autonumber
  participant M as Manager/Admin
  participant CE as Campaign Engine
  participant RA as Roster Adapter
  participant ES as External Source (SIS/HRMS/CRM/API/CSV)

  M->>CE: Create campaign + window
  M->>CE: Attach survey version
  M->>CE: Configure assignment and eligibility rules
  CE->>RA: Start roster sync/import
  RA->>ES: Pull/receive audience data
  ES-->>RA: Records + metadata
  RA-->>CE: Validated roster + errors
  CE->>CE: Generate assignments + constraint keys
  M->>CE: Activate campaign
```

---

## 8. Flow: Reporting and Analytics

### 8.1 ASCII Pipeline
```text
[Responses + Metadata] --> [Ingestion/Normalization] --> [Analytics Store]
                                               |                  |
                                               v                  v
                                          [Quality Flags]   [Aggregations]
                                                                 |
                                                                 v
                                                   [Dashboard + Reports API]
                                                                 |
                                                                 v
                                                         [Admin UI / Export]
```

### 8.2 Mermaid Flowchart
```mermaid
flowchart LR
  A[Responses + Metadata] --> B[Ingestion + Normalization]
  B --> C[Analytics Store]
  B --> D[Quality Flagging]
  D --> C
  C --> E[Aggregations]
  E --> F[Dashboard/Reports API]
  F --> G[Admin UI]
  F --> H[CSV/JSON Export]
```

---

## 9. Flow: Anonymity-Preserving Reporting and Eligibility

### 9.1 ASCII Decision Flow
```text
[Incoming Response]
        |
        v
[Resolve campaign policy]
        |
   +----+----+
   | anonymous? |
   +----+----+
        |yes
        v
[Store identity-linking key in restricted store]
        |
        v
[Run dedup/eligibility check using protected key]
        |
        v
[Persist response payload without direct identity in report layer]
        |
        v
[Report query]
        |
   +----+----+
   | sample >= threshold? |
   +----+----+
     |yes |no
     v    v
 [show] [suppress segment]
```

### 9.2 Mermaid Flowchart
```mermaid
flowchart TD
  A[Incoming Response] --> B[Resolve campaign privacy policy]
  B -->|Anonymous| C[Store protected identity key]
  B -->|Identified| D[Store standard identity references]
  C --> E[Dedup and eligibility check]
  D --> E
  E --> F[Persist response for analytics]
  F --> G[Report request]
  G --> H{Segment sample >= threshold?}
  H -->|Yes| I[Return data]
  H -->|No| J[Suppress segment details]
```

---

## 10. Flow: Webhook Delivery and Retry

### 10.1 ASCII Sequence
```text
Survey Engine       Webhook Service      Customer Endpoint
     |                     |                    |
1. Event occurs            |                    |
     |-------------------->|                    |
2. Sign payload + deliver  |------------------->|
3. 2xx?                    |<-------------------|
   | yes                                      
   v
 [mark delivered]

if non-2xx/timeout:
  retry with backoff -> attempt N -> dead-letter after max attempts
```

### 10.2 Mermaid Sequence
```mermaid
sequenceDiagram
  autonumber
  participant SE as Survey Engine
  participant WS as Webhook Service
  participant CE as Customer Endpoint

  SE->>WS: Emit event (response.completed)
  WS->>CE: POST signed payload
  alt 2xx response
    CE-->>WS: 2xx
    WS->>WS: Mark delivered
  else non-2xx or timeout
    CE-->>WS: error/timeout
    WS->>WS: Retry with exponential backoff
    WS->>WS: Dead-letter after max attempts
  end
```

---

## 11. Flow: Setup-Later Mode and Enforcement

### 11.1 ASCII State Machine
```text
[onboarding]
    |
    | setup-later selected
    v
[bootstrap_active_until_T]
    |
    | time < T
    | allow limited admin actions
    v
[restricted_publish]
    |
    | complete IdP setup + first mapped admin login
    v
[active]

if time >= T and not configured:
[bootstrap_expired] -> block publish/distribution until resolved
```

### 11.2 Mermaid State Diagram
```mermaid
stateDiagram-v2
  [*] --> Onboarding
  Onboarding --> BootstrapActive: Setup-later selected
  Onboarding --> Active: IdP setup complete + admin mapped
  BootstrapActive --> Active: IdP setup complete + admin mapped
  BootstrapActive --> BootstrapExpired: TTL reached
  BootstrapExpired --> Active: IdP setup completed

  state BootstrapActive {
    [*] --> LimitedAccess
    LimitedAccess --> RestrictedPublish
  }
```

---

## 12. Deployment and Runtime Topology

### 12.1 ASCII Topology
```text
                    +----------------------+
Internet/API -----> | API Gateway / WAF    |
                    +----------+-----------+
                               |
                +--------------+---------------+
                |                              |
        +-------v--------+            +--------v--------+
        | App Services   |            | Async Workers   |
        | (stateless)    |            | (webhooks/jobs) |
        +-------+--------+            +--------+--------+
                |                              |
        +-------v------------------------------v-------+
        | Data Layer: OLTP DB, Cache, Queue, Object    |
        | Storage, Analytics Warehouse                  |
        +-------------------------+---------------------+
                                  |
                          +-------v-------+
                          | Observability |
                          | logs/metrics  |
                          +---------------+
```

### 12.2 Mermaid Deployment Diagram
```mermaid
graph TB
  U[Internet / SDK / Admin UI] --> WAF[WAF + API Gateway]
  WAF --> APP[Stateless App Services]
  WAF --> RT[Runtime Edge Services]
  APP --> Q[Queue/Event Bus]
  APP --> DB[(OLTP DB)]
  APP --> C[(Cache)]
  RT --> DB
  APP --> OBJ[(Object Storage)]
  Q --> WK[Worker Pool]
  WK --> WH[(Analytics Warehouse)]
  WK --> EXT[External Endpoints]
  APP --> OBS[Observability Stack]
  RT --> OBS
  WK --> OBS
```

---

## 13. Operational Guardrails
- Every API request must carry tenant context resolved from trusted claims or scoped keys.
- All privileged mutations must produce audit events.
- Publish endpoints are blocked when tenant onboarding/auth setup is incomplete or expired.
- Webhooks must be signed and replay-protected.
- Survey versions are immutable post-publish.
- Data retention jobs must honor tenant policy and legal holds.
- Assignment and eligibility policies must be validated before campaign activation.
- Anonymous reporting must enforce threshold suppression to reduce re-identification risk.

## 14. Suggested Next Implementation Artifacts
1. `api-contracts/openapi-v1.yaml`
2. `adr/` entries for auth model, tenancy model, and data partition strategy
3. sequence tests for onboarding, campaign activation, and publish gating
4. load-test scenarios for response spikes, roster sync bursts, and webhook backlog
