# Survey Engine - Complete Backend Analysis Report

**Generated:** March 9, 2026  
**Project:** Survey-Engine  
**Stack:** Spring Boot 4.0.3, Java 25, PostgreSQL, Redis, RabbitMQ

---

## Table of Contents

1. [System Architecture Overview](#1-system-architecture-overview)
2. [Module-by-Module Breakdown](#2-module-by-module-breakdown)
3. [Entity Relationship Map](#3-entity-relationship-map)
4. [Business Logic Flows](#4-business-logic-flows)
5. [API Structure](#5-api-structure)
6. [Security Implementation](#6-security-implementation)
7. [Database Schema](#7-database-schema)
8. [Key Design Decisions & Patterns](#8-key-design-decisions--patterns)
9. [Notable Observations & Recommendations](#9-notable-observations--recommendations)

---

## 1. System Architecture Overview

### Technology Stack

| Component | Technology |
|-----------|------------|
| **Framework** | Spring Boot 4.0.3 |
| **Language** | Java 25 |
| **Database** | PostgreSQL (prod), H2 (dev/test) |
| **Cache** | Redis |
| **Message Broker** | RabbitMQ |
| **Security** | Spring Security 6 + JWT |
| **ORM** | Spring Data JPA + Hibernate |
| **Migration** | Flyway |
| **Build** | Gradle + Lombok |

### Architecture Pattern

Multi-tenant SaaS application with:
- **Tenant Isolation:** Via `tenant_id` columns in all tables
- **Role-Based Access Control:** SUPER_ADMIN, ADMIN, EDITOR, VIEWER
- **Subscription-Based Feature Gating:** BASIC, PRO, ENTERPRISE plans
- **Comprehensive Audit Logging:** All business actions tracked

### High-Level Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                        Load Balancer                             │
└─────────────────────────────────────────────────────────────────┘
                                │
                                ▼
┌─────────────────────────────────────────────────────────────────┐
│                    Survey Engine (Spring Boot)                   │
│  ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐   │
│  │  Admin  │ │  Auth   │ │ Survey  │ │Campaign │ │ Response│   │
│  │ Module  │ │ Module  │ │ Module  │ │ Module  │ │ Module  │   │
│  └─────────┘ └─────────┘ └─────────┘ └─────────┘ └─────────┘   │
│  ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐   │
│  │ Scoring │ │Subscription│ Tenant  │ Analytics│ Common  │   │
│  │ Module  │ │  Module  │ Module  │ Module  │ Module  │   │
│  └─────────┘ └─────────┘ └─────────┘ └─────────┘ └─────────┘   │
└─────────────────────────────────────────────────────────────────┘
          │                │                │
          ▼                ▼                ▼
    ┌──────────┐    ┌──────────┐    ┌──────────┐
    │PostgreSQL│    │  Redis   │    │ RabbitMQ │
    │ Database │    │  Cache   │    │  Broker  │
    └──────────┘    └──────────┘    └──────────┘
```

---

## 2. Module-by-Module Breakdown

### 2.1 Admin Module (`com.bracits.surveyengine.admin`)

**Purpose:** Platform administration, tenant management, and user authentication

#### Key Entities

| Entity | Description |
|--------|-------------|
| `AdminUser` | Admin users with roles and tenant association |
| `RefreshToken` | JWT refresh token storage with rotation support |
| `AdminRole` | Enum: SUPER_ADMIN, ADMIN, EDITOR, VIEWER |

#### Services

| Service | Responsibility |
|---------|----------------|
| `AdminAuthService` | Registration, login, token refresh with bcrypt |
| `JwtService` | JWT generation/validation (HMAC-SHA256) |
| `TenantAdminService` | Super admin tenant management |

#### Controllers

| Controller | Endpoints |
|------------|-----------|
| `AdminAuthController` | `/api/v1/admin/auth/**` - Login, register, refresh, logout |
| `TenantAdminController` | `/api/v1/admin/superadmin/tenants/**` - Tenant CRUD |
| `SuperAdminMetricsController` | `/api/v1/admin/superadmin/metrics` - Platform metrics |

#### Security Features

- JWT stored in HttpOnly cookies (`access_token`, `refresh_token`)
- Impersonation support for super admins
- CSRF protection for browser-based clients
- Token rotation on each refresh

---

### 2.2 Auth Module (`com.bracits.surveyengine.auth`)

**Purpose:** Respondent authentication configuration and validation

#### Key Entities

| Entity | Description |
|--------|-------------|
| `AuthProfile` | Per-tenant auth configuration (one per tenant) |
| `AuthenticationMode` | PUBLIC_ANONYMOUS, SIGNED_LAUNCH_TOKEN, EXTERNAL_SSO_TRUST |
| `ClaimMapping` | Maps external JWT claims to internal fields |
| `FallbackPolicy` | SSO_REQUIRED, ANONYMOUS_FALLBACK, DISABLE_ON_FAILURE |
| `OidcAuthState` | OIDC flow state with PKCE support |
| `ResponderAccessCode` | One-time access codes for OIDC flow |
| `AuthTokenReplay` | Replay protection for signed tokens |

#### Authentication Modes

| Mode | Description |
|------|-------------|
| `PUBLIC_ANONYMOUS` | No authentication required, generates anonymous ID |
| `SIGNED_LAUNCH_TOKEN` | HMAC-SHA256 signed JWT validation |
| `EXTERNAL_SSO_TRUST` | External OIDC provider (RS256/HS256) |

#### Services

| Service | Responsibility |
|---------|----------------|
| `AuthProfileServiceImpl` | CRUD for auth profiles with audit logging |
| `TokenValidationServiceImpl` | Validates tokens based on auth mode |
| `OidcResponderAuthService` | OIDC authorization code flow with PKCE |

#### API Endpoints

```
POST   /api/v1/auth/profiles              - Create auth profile
PUT    /api/v1/auth/profiles/{id}         - Update auth profile
GET    /api/v1/auth/profiles/tenant/{id}  - Get by tenant
POST   /api/v1/auth/validate/{tenantId}   - Validate respondent token
POST   /api/v1/auth/respondent/oidc/start - Start OIDC flow
GET    /api/v1/auth/respondent/oidc/callback - OIDC callback
```

---

### 2.3 Survey Module (`com.bracits.surveyengine.survey`)

**Purpose:** Survey design, versioning, and lifecycle management

#### Key Entities

| Entity | Description |
|--------|-------------|
| `Survey` | Root survey entity with lifecycle state |
| `SurveyPage` | Pages within a survey (visual grouping) |
| `SurveyQuestion` | Questions on pages with pinned versions |
| `SurveyLifecycleState` | DRAFT → PUBLISHED → CLOSED → RESULTS_PUBLISHED → ARCHIVED |
| `SurveySnapshot` | Immutable JSON snapshot at publish time |
| `SkipLogicRule` | Conditional navigation based on answers |

#### Lifecycle States

```
DRAFT ──→ PUBLISHED ──→ CLOSED ──→ RESULTS_PUBLISHED ──→ ARCHIVED
                         │
                         └────→ PUBLISHED (reopen with reason)
```

#### Business Logic

- **Versioning:** Questions/categories are pinned at publish time
- **Immutability:** Published surveys cannot be modified
- **Snapshots:** Full survey structure frozen as JSON on publish
- **Lifecycle Transitions:** Strict state machine with validation

#### Service: SurveyServiceImpl

- Creates drafts with pinned question/category versions
- Publishes by creating immutable snapshots
- Validates lifecycle transitions

---

### 2.4 Question Bank Module (`com.bracits.surveyengine.questionbank`)

**Purpose:** Reusable question library with versioning

#### Key Entities

| Entity | Description |
|--------|-------------|
| `Question` | Mutable question until published |
| `QuestionVersion` | Immutable snapshot with version number |
| `QuestionType` | RATING_SCALE, SINGLE_CHOICE, MULTIPLE_CHOICE, RANK |
| `Category` | Logical grouping of questions |
| `CategoryVersion` | Versioned category with question mappings |
| `CategoryQuestionMapping` | Links questions to categories with weights |

#### Versioning Strategy

```
┌─────────────────────────────────────────────────────────────┐
│  Question Bank (Mutable)         Published Survey (Immutable)│
│  ┌──────────────┐                ┌──────────────────────┐   │
│  │  Question    │ ──publish──→   │ SurveyQuestion       │   │
│  │  (v=N)       │                │ └─→ QuestionVersion  │   │
│  └──────────────┘                │     (v=pinned)       │   │
│         │                        └──────────────────────┘   │
│         │ edit                                               │
│         ▼                                                    │
│  ┌──────────────┐                                            │
│  │  Question    │ (v=N+1) - New version created              │
│  └──────────────┘                                            │
└─────────────────────────────────────────────────────────────┘
```

---

### 2.5 Campaign Module (`com.bracits.surveyengine.campaign`)

**Purpose:** Survey distribution and respondent configuration

#### Key Entities

| Entity | Description |
|--------|-------------|
| `Campaign` | Links survey to distribution channels |
| `CampaignStatus` | DRAFT, ACTIVE, CLOSED |
| `AuthMode` | PUBLIC or PRIVATE |
| `CampaignSettings` | Comprehensive respondent behavior config |
| `DataCollectionField` | Dynamic metadata collection fields |
| `DataCollectionFieldType` | TEXT, EMAIL, PHONE, ADDRESS |

#### Campaign Settings (SRS §4.5)

| Setting | Description |
|---------|-------------|
| Password | Password protection |
| CAPTCHA | CAPTCHA verification |
| One per device | Device fingerprint deduplication |
| IP restriction | IP-based deduplication |
| Email restriction | Email-based deduplication |
| Response quota | Maximum responses allowed |
| Close date | Campaign expiry date |
| Session timeout | Session timeout in minutes |
| UI options | Progress indicator, back button, messages |

#### Service: CampaignServiceImpl

- Activates campaigns by linking to survey snapshots
- Enforces settings at response time
- Generates distribution channels

---

### 2.6 Response Module (`com.bracits.surveyengine.response`)

**Purpose:** Response collection, validation, and locking

#### Key Entities

| Entity | Description |
|--------|-------------|
| `SurveyResponse` | Respondent submission |
| `ResponseStatus` | IN_PROGRESS, SUBMITTED, LOCKED |
| `Answer` | Individual question responses with scores |

#### Key Features

- **Validation:** Answers validated against snapshot config
- **Mandatory Enforcement:** Missing mandatory answers rejected
- **Auto-Locking:** Responses locked on submission (SRS §8)
- **Deduplication:** Device/IP/email checks based on settings
- **Quota Enforcement:** Campaign quota checks

#### Service: ResponseServiceImpl

```java
// Submission Flow
1. Validate campaign is ACTIVE
2. Enforce access mode (PUBLIC/PRIVATE)
3. Enforce runtime settings (quota, close date, dedup)
4. Validate answers against snapshot
5. Enforce mandatory questions
6. Calculate weighted score
7. Save response with LOCKED status
```

---

### 2.7 Scoring Module (`com.bracits.surveyengine.scoring`)

**Purpose:** Weighted score calculation engine

#### Key Entities

| Entity | Description |
|--------|-------------|
| `WeightProfile` | Named scoring configuration per campaign |
| `CategoryWeight` | Percentage weight per category (must sum to 100%) |

#### Scoring Formula (SRS §5)

```
normalized_score = raw_score / category_max_score
weighted_score = normalized_score × (weight_percentage / 100)
total_score = Σ weighted_scores
```

#### Example Calculation

```
Category A: raw=80, max=100, weight=40%
  → normalized = 80/100 = 0.8
  → weighted = 0.8 × 0.4 = 0.32

Category B: raw=45, max=50, weight=60%
  → normalized = 45/50 = 0.9
  → weighted = 0.9 × 0.6 = 0.54

Total Score = 0.32 + 0.54 = 0.86 (86%)
```

#### Services

| Service | Responsibility |
|---------|----------------|
| `ScoringEngineServiceImpl` | Calculates weighted scores |
| `WeightProfileServiceImpl` | Manages weight profiles with 100% validation |

---

### 2.8 Subscription Module (`com.bracits.surveyengine.subscription`)

**Purpose:** SaaS subscription management and quota enforcement

#### Key Entities

| Entity | Description |
|--------|-------------|
| `Subscription` | Tenant subscription with plan and status |
| `SubscriptionPlan` | BASIC, PRO, ENTERPRISE |
| `SubscriptionStatus` | TRIAL, ACTIVE, SUSPENDED, CANCELLED |
| `PlanDefinition` | Plan catalog with features and limits |
| `PlanFeature` | Feature flags per plan |
| `PaymentTransaction` | Payment records |

#### Plan Features

| Feature | BASIC | PRO | ENTERPRISE |
|---------|-------|-----|------------|
| Max Campaigns | ✓ | ✓ | ✓ |
| Max Responses | ✓ | ✓ | ✓ |
| Max Admin Users | ✓ | ✓ | ✓ |
| Weight Profiles | ✗ | ✓ | ✓ |
| Signed Tokens | ✗ | ✓ | ✓ |
| SSO Integration | ✗ | ✗ | ✓ |
| Custom Branding | ✗ | ✓ | ✓ |
| Device Fingerprint | ✗ | ✓ | ✓ |
| API Access | ✗ | ✓ | ✓ |

#### Services

| Service | Responsibility |
|---------|----------------|
| `SubscriptionService` | Subscription lifecycle management |
| `PlanQuotaService` | Quota enforcement (campaigns, users, responses) |
| `SubscriptionEnforcementFilter` | Filter blocking unauthenticated tenant ops |

---

### 2.9 Tenant Module (`com.bracits.surveyengine.tenant`)

**Purpose:** Multi-tenant isolation

#### Key Entity: Tenant

```java
@Entity
@Table(name = "tenant")
public class Tenant {
    @Id
    private String id;          // Unique tenant identifier
    private String name;        // Display name
    private boolean active;     // Active status
}
```

#### Service: TenantService

- Auto-provisions tenants on first use
- Ensures tenant existence

---

### 2.10 Analytics Module (`com.bracits.surveyengine.analytics`)

**Purpose:** Advanced response analytics

#### Services: AdvancedAnalyticsServiceImpl

#### Features

| Feature | Description |
|---------|-------------|
| Question Analytics | Frequency analysis, average scores |
| Score Distribution | Buckets, median, percentiles |
| Temporal Trends | Response trends over time |
| Campaign Summary | Completion rates, status breakdown |
| Segment Comparison | Compare metadata-filtered segments |

#### DTOs

| DTO | Purpose |
|-----|---------|
| `QuestionAnalyticsResponse` | Per-question statistics |
| `ScoreDistributionResponse` | Score bucket distribution |
| `TemporalAnalyticsResponse` | Time-series data |
| `FullCampaignAnalyticsResponse` | Complete analytics package |

---

### 2.11 Common Module (`com.bracits.surveyengine.common`)

#### Audit System

| Component | Description |
|-----------|-------------|
| `AuditableEntity` | Base class with created/modified timestamps |
| `@Auditable` | Method annotation for audit logging |
| `AuditAspect` | AOP interceptor for audit logging |
| `AuditLogServiceImpl` | Writes audit logs |
| `HibernateAuditListener` | Entity lifecycle listener |

#### Tenant Support

| Component | Description |
|-----------|-------------|
| `TenantContext` | ThreadLocal for current tenant |
| `TenantSupport` | Helper methods for tenant resolution |

#### Exception Handling

| Component | Description |
|-----------|-------------|
| `BusinessException` | Carries `ErrorCode` |
| `ErrorCode` | Enum of all error codes |
| `ResourceNotFoundException` | Standard 404 |

---

## 3. Entity Relationship Map

```
Tenant (1) ──┬── (N) AdminUser
             │         └── Roles: SUPER_ADMIN, ADMIN, EDITOR, VIEWER
             │
             ├── (1) Subscription ── (1) PlanDefinition
             │         │                    └── (N) PlanFeature
             │         └── (N) PaymentTransaction
             │
             ├── (1) AuthProfile ── (N) ClaimMapping
             │
             ├── (N) Survey ── (N) SurveyPage ── (N) SurveyQuestion
             │       │              │                    │
             │       │              │                    ├── (1) QuestionVersion
             │       │              │                    └── (1) CategoryVersion
             │       │              │
             │       │              └── (N) SkipLogicRule
             │       │
             │       └── (N) SurveySnapshot
             │
             ├── (N) Category ── (N) CategoryVersion ── (N) CategoryQuestionMapping
             │
             ├── (N) Question ── (N) QuestionVersion
             │
             ├── (N) Campaign ── (1) CampaignSettings
             │       │                    └── (N) DataCollectionField
             │       │
             │       ├── (1) WeightProfile ── (N) CategoryWeight
             │       │
             │       ├── (N) DistributionChannel
             │       │
             │       └── (N) SurveyResponse ── (N) Answer
             │
             └── (N) AuditLog
```

---

## 4. Business Logic Flows

### 4.1 Survey Publishing Flow

```
┌─────────────┐
│   DRAFT     │
│  Survey     │
└──────┬──────┘
       │ Admin triggers publish
       ▼
┌─────────────────────────────────┐
│ 1. Validate survey structure    │
│ 2. Pin question versions        │
│ 3. Pin category versions        │
│ 4. Create SurveySnapshot (JSON) │
│ 5. Set lifecycle = PUBLISHED    │
│ 6. Increment version number     │
└─────────────────────────────────┘
       │
       ▼
┌─────────────┐
│  PUBLISHED  │
│  (Immutable)│
└─────────────┘
```

### 4.2 Campaign Activation Flow

```
┌─────────────┐
│   DRAFT     │
│  Campaign   │
└──────┬──────┘
       │ Admin activates
       ▼
┌─────────────────────────────────┐
│ 1. Verify survey is PUBLISHED   │
│ 2. Link to latest snapshot      │
│ 3. Create default weight profile│
│ 4. Set status = ACTIVE          │
└─────────────────────────────────┘
       │
       ▼
┌─────────────┐
│   ACTIVE    │
│  (Live)     │
└─────────────┘
```

### 4.3 Response Submission Flow

```
┌─────────────────────────────────────────────────────────────┐
│                    Respondent Submits                        │
└─────────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│ 1. Campaign Active Check                                     │
│    - Status must be ACTIVE                                   │
└─────────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│ 2. Authentication Check                                      │
│    - PUBLIC: Allow anonymous                                 │
│    - PRIVATE: Validate token/access code                     │
└─────────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│ 3. Settings Enforcement                                      │
│    - Quota check                                             │
│    - Close date check                                        │
│    - Device/IP/Email deduplication                           │
└─────────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│ 4. Answer Validation                                         │
│    - Validate against question type                          │
│    - Enforce mandatory questions                             │
│    - Calculate scores                                        │
└─────────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│ 5. Calculate Weighted Score                                  │
│    - Get category raw scores                                 │
│    - Apply weight profile                                    │
│    - Calculate total weighted score                          │
└─────────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│ 6. Save & Lock Response                                      │
│    - Save response with LOCKED status                        │
│    - Set locked_at timestamp                                 │
└─────────────────────────────────────────────────────────────┘
```

### 4.4 OIDC Responder Flow

```
┌──────────┐     ┌──────────┐     ┌──────────┐     ┌──────────┐
│ Frontend │     │  Survey  │     │   IdP    │     │  Survey  │
│          │     │  Engine  │     │ (Google, │     │  Engine  │
│          │     │          │     │  Azure)  │     │          │
└────┬─────┘     └────┬─────┘     └────┬─────┘     └────┬─────┘
     │                │                │                │
     │ 1. Start OIDC  │                │                │
     │───────────────>│                │                │
     │                │                │                │
     │ 2. Return      │                │                │
     │    Auth URL    │                │                │
     │<───────────────│                │                │
     │                │                │                │
     │ 3. Redirect    │                │                │
     │────────────────────────────────>│                │
     │    User Login  │                │                │
     │                │                │                │
     │ 4. Redirect    │                │                │
     │    with code   │                │                │
     │<────────────────────────────────│                │
     │                │                │                │
     │ 5. Callback    │                │                │
     │───────────────>│                │                │
     │                │                │                │
     │                │ 6. Exchange    │                │
     │                │    code for    │                │
     │                │    tokens      │                │
     │                │───────────────>│                │
     │                │                │                │
     │                │ 7. Return ID   │                │
     │                │    token       │                │
     │                │<───────────────│                │
     │                │                │                │
     │                │ 8. Validate    │                │
     │                │    token       │                │
     │                │                │                │
     │                │ 9. Generate    │                │
     │                │    access code │                │
     │                │                │                │
     │ 10. Return     │                │                │
     │     access     │                │                │
     │     code       │                │                │
     │<───────────────│                │                │
     │                │                │                │
     │ 11. Submit     │                │                │
     │     response   │                │                │
     │     with code  │                │                │
     │───────────────>│                │                │
     │                │                │                │
     │                │ 12. Consume    │                │
     │                │     code       │                │
     │                │                │                │
     │ 13. Success    │                │                │
     │<───────────────│                │                │
     │                │                │                │
```

---

## 5. API Structure

### Admin APIs (`/api/v1/admin/**`)

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/auth/register` | Public registration | Public |
| POST | `/auth/login` | Login | Public |
| POST | `/auth/refresh` | Refresh token | Cookie |
| POST | `/auth/logout` | Logout | Authenticated |
| GET | `/auth/me` | Current user info | Authenticated |
| GET | `/superadmin/tenants` | List all tenants | SUPER_ADMIN |
| PUT | `/superadmin/tenants/{id}/suspend` | Suspend tenant | SUPER_ADMIN |
| PUT | `/superadmin/tenants/{id}/activate` | Activate tenant | SUPER_ADMIN |
| POST | `/superadmin/tenants/{id}/impersonate` | Impersonate tenant | SUPER_ADMIN |
| POST | `/superadmin/tenants/{id}/subscriptions/override` | Override subscription | SUPER_ADMIN |
| GET | `/superadmin/metrics` | Platform metrics | SUPER_ADMIN |

### Auth APIs (`/api/v1/auth/**`)

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/profiles` | Create auth profile | Authenticated |
| PUT | `/profiles/{id}` | Update auth profile | Authenticated |
| GET | `/profiles/tenant/{tenantId}` | Get by tenant | Authenticated |
| POST | `/profiles/{id}/rotate-key` | Rotate signing key | Authenticated |
| POST | `/validate/{tenantId}` | Validate respondent token | Public |
| GET | `/profiles/{id}/audit` | Get audit history | Authenticated |
| GET | `/providers/templates` | List provider templates | Public |
| GET | `/providers/templates/{providerCode}` | Get provider template | Public |
| POST | `/respondent/oidc/start` | Start OIDC flow | Public |
| GET | `/respondent/oidc/callback` | OIDC callback | Public |

### Survey APIs (`/api/v1/surveys/**`)

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/` | Create survey | Authenticated |
| GET | `/{id}` | Get by ID | Authenticated |
| GET | `/` | Get all active | Authenticated |
| PUT | `/{id}` | Update survey | Authenticated |
| DELETE | `/{id}` | Deactivate survey | Authenticated |
| POST | `/{id}/lifecycle` | Transition lifecycle | Authenticated |

### Campaign APIs (`/api/v1/campaigns/**`)

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/` | Create campaign | Authenticated |
| GET | `/{id}` | Get by ID | Authenticated |
| GET | `/` | Get all active | Authenticated |
| PUT | `/{id}` | Update campaign | Authenticated |
| DELETE | `/{id}` | Deactivate campaign | Authenticated |
| GET | `/{id}/preview` | Get preview | Authenticated |
| GET | `/{id}/settings` | Get settings | Authenticated |
| PUT | `/{id}/settings` | Update settings | Authenticated |
| POST | `/{id}/activate` | Activate campaign | Authenticated |
| POST | `/{id}/distribute` | Generate channels | Authenticated |
| GET | `/{id}/channels` | Get channels | Authenticated |

### Response APIs (`/api/v1/responses/**`)

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/` | Submit response | Public |
| GET | `/{id}` | Get by ID | Authenticated |
| GET | `/campaign/{campaignId}` | Get by campaign | Authenticated |
| POST | `/{id}/lock` | Lock response | Authenticated |
| POST | `/{id}/reopen` | Reopen locked response | Authenticated |
| GET | `/analytics/{campaignId}` | Get analytics | Authenticated |

### Scoring APIs (`/api/v1/scoring/**`)

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/profiles` | Create weight profile | Authenticated |
| GET | `/profiles/{id}` | Get profile | Authenticated |
| GET | `/profiles/campaign/{campaignId}` | Get by campaign | Authenticated |
| PUT | `/profiles/{id}` | Update profile | Authenticated |
| DELETE | `/profiles/{id}` | Deactivate profile | Authenticated |
| POST | `/profiles/{id}/validate` | Validate weight sum | Authenticated |
| POST | `/calculate/{profileId}` | Calculate score | Authenticated |

---

## 6. Security Implementation

### Authentication

#### Admin Authentication
```
┌─────────────────────────────────────────────────────────────┐
│ 1. User submits credentials                                  │
│ 2. BCrypt password verification                              │
│ 3. Generate JWT (HMAC-SHA256)                                │
│    - Access token: 1 hour TTL                                │
│    - Refresh token: 7 days TTL                               │
│ 4. Set HttpOnly cookies                                      │
│ 5. Store refresh token in database                           │
└─────────────────────────────────────────────────────────────┘
```

#### Respondent Authentication (Configurable per Tenant)

| Mode | Flow |
|------|------|
| PUBLIC_ANONYMOUS | No auth, generate anonymous ID |
| SIGNED_LAUNCH_TOKEN | HMAC-SHA256 JWT validation |
| EXTERNAL_SSO_TRUST | OIDC with PKCE |

### Authorization

#### Role Hierarchy
```
SUPER_ADMIN > ADMIN > EDITOR > VIEWER
```

| Role | Permissions |
|------|-------------|
| SUPER_ADMIN | Full platform access, tenant management |
| ADMIN | Create/manage surveys, campaigns, settings |
| EDITOR | Edit surveys and questions |
| VIEWER | Read-only access to reports and analytics |

### Token Management

```yaml
Access Token:
  - TTL: 1 hour (3600 seconds)
  - Algorithm: HMAC-SHA256
  - Storage: HttpOnly cookie (access_token)
  - Claims: tenant_id, email, role, name

Refresh Token:
  - TTL: 7 days (604800 seconds)
  - Storage: Database + HttpOnly cookie (refresh_token)
  - Rotation: Revoked on each use
```

### CSRF Protection

- Cookie-based CSRF for SPA
- X-XSRF-TOKEN header support
- Ignored for token-based authentication

---

## 7. Database Schema

### Core Tables

| Table | Description |
|-------|-------------|
| `tenant` | Tenant isolation |
| `admin_user` | Admin users |
| `refresh_token` | Refresh token storage |
| `survey` | Survey root entity |
| `survey_page` | Survey pages |
| `survey_question` | Survey questions |
| `survey_snapshot` | Immutable snapshots |
| `skip_logic_rule` | Skip logic rules |
| `question` | Question bank |
| `question_version` | Versioned questions |
| `category` | Category grouping |
| `category_version` | Versioned categories |
| `category_question_mapping` | Category-question links |
| `campaign` | Campaign entity |
| `campaign_settings` | Campaign configuration |
| `distribution_channel` | Distribution channels |
| `survey_response` | Response submissions |
| `answer` | Individual answers |
| `weight_profile` | Scoring profiles |
| `category_weight` | Category weights |
| `subscription` | Tenant subscriptions |
| `plan_definition` | Plan catalog |
| `plan_feature` | Feature definitions |
| `auth_profile` | Auth configuration |
| `claim_mapping` | Claim mappings |
| `oidc_auth_state` | OIDC state storage |
| `responder_access_code` | One-time access codes |
| `auth_token_replay` | Replay protection |
| `audit_log` | Audit trail |

### Key Constraints

- All entities scoped by `tenant_id`
- Unique constraints on versioned entities
- Foreign keys with RESTRICT on pinned versions
- JSONB columns for flexible configs

---

## 8. Key Design Decisions & Patterns

### Design Patterns

| Pattern | Usage |
|---------|-------|
| **Versioning Pattern** | Immutable snapshots for published content |
| **Strategy Pattern** | Auth mode switching (PUBLIC/SIGNED/SSO) |
| **Template Method** | OIDC flow with PKCE |
| **AOP** | Audit logging via aspects |
| **ThreadLocal** | Tenant context propagation |
| **Specification Pattern** | Dynamic response filtering |

### Architectural Decisions

| Decision | Rationale |
|----------|-----------|
| **Pinned Versions** | Survey questions reference specific versions, not live bank |
| **Snapshot Immutability** | Published surveys frozen as JSON |
| **Response Locking** | Submitted responses immediately locked |
| **Subscription Enforcement** | Filter-based quota/feature gating |
| **Multi-tenancy** | Logical isolation via tenant_id |
| **Cache Strategy** | Redis for OIDC metadata and JWKS |

### SRS Compliance

| SRS Section | Feature | Status |
|-------------|---------|--------|
| §4.1 | Survey design with pages, questions, skip logic | ✓ |
| §4.2 | Question bank versioning | ✓ |
| §4.3 | Response metadata collection | ✓ |
| §4.5 | Campaign settings | ✓ |
| §4.6 | Survey lifecycle | ✓ |
| §4.7 | Response collection | ✓ |
| §4.9 | Respondent authentication | ✓ |
| §5 | Weighted scoring | ✓ |
| §8 | Response locking | ✓ |

---

## 9. Notable Observations & Recommendations

### Strengths

1. **Comprehensive Versioning:** Bank updates don't affect published surveys
2. **Flexible Auth:** Three modes with OIDC support
3. **Strong Audit:** Full audit trail with IP tracking
4. **Subscription Gating:** Feature flags per plan
5. **Score Calculation:** Normalized weighted scoring
6. **Security Best Practices:** BCrypt, token rotation, PKCE, replay protection

### Potential Improvements

1. **Payment Gateway:** Currently interface-only, needs implementation
2. **RabbitMQ Usage:** Defined but not actively used in code
3. **OpenTelemetry:** Configured but integration unclear
4. **Circuit Breaker:** Resilience4j included but not configured
5. **Response Validation:** Complex validation logic could be extracted

### Security Considerations

| Aspect | Status | Notes |
|--------|--------|-------|
| Secret Management | ✓ Good | JWT secret in environment variables |
| Password Hashing | ✓ Good | BCrypt |
| Token Rotation | ✓ Good | Refresh token rotation implemented |
| Replay Protection | ✓ Good | JTI-based for signed tokens |
| PKCE | ✓ Good | OIDC flow uses PKCE |
| CSRF Protection | ✓ Good | Cookie-based for SPA |

### Technical Debt

1. **Legacy Fields:** `collectName`, `collectEmail`, etc. deprecated in favor of `dataCollectionFields`
2. **Hardcoded Defaults:** Some default values scattered across services
3. **Error Messages:** Could benefit from internationalization

### Performance Considerations

1. **Caching:** Redis used for OIDC metadata and JWKS (good)
2. **N+1 Queries:** Some services may benefit from batch fetching
3. **JSON Parsing:** Snapshot parsing could be optimized for large surveys

---

## Appendix A: Configuration Properties

### JWT Configuration

```yaml
survey-engine:
  jwt:
    secret: ${SURVEY_ENGINE_JWT_SECRET}
    ttl-seconds: 3600          # 1 hour
    refresh-ttl-seconds: 604800 # 7 days
```

### Security Configuration

```yaml
survey-engine:
  security:
    cookies:
      secure: ${COOKIE_SECURE:true}
      same-site: Strict
```

### Cache Configuration

```yaml
survey-engine:
  auth:
    cache:
      oidc-metadata:
        ttl: 24h
      jwks:
        ttl: 15m
```

---

## Appendix B: Error Codes

| Error Code | Description |
|------------|-------------|
| `INVALID_WEIGHT_SUM` | Category weights must total exactly 100% |
| `CATEGORY_MAX_SCORE_ZERO` | Category max score cannot be zero |
| `QUESTION_MAX_SCORE_INVALID` | Question max score must be > 0 |
| `SURVEY_IMMUTABLE_AFTER_PUBLISH` | Survey is immutable after publish |
| `INVALID_LIFECYCLE_TRANSITION` | Lifecycle transition is not allowed |
| `RESPONSE_LOCKED` | Response is locked and cannot be modified |
| `RESPONSE_NOT_LOCKED` | Response is not in locked state |
| `CAMPAIGN_NOT_ACTIVE` | Campaign is not in active state |
| `RESPONSE_QUOTA_EXCEEDED` | Response quota has been reached |
| `DUPLICATE_RESPONSE` | A response from this device/IP already exists |
| `SUBSCRIPTION_INACTIVE` | Active subscription is required |
| `PAYMENT_FAILED` | Payment processing failed |
| `QUOTA_EXCEEDED` | Plan quota exceeded |
| `RESOURCE_NOT_FOUND` | Requested resource not found |
| `VALIDATION_FAILED` | Request validation failed |
| `ACCESS_DENIED` | Access denied |
| `INTERNAL_ERROR` | An unexpected error occurred |

---

## Appendix C: Docker Configuration

### Services

| Service | Image | Port | Purpose |
|---------|-------|------|---------|
| `postgres` | postgres:17 | 5432 | Primary database |
| `redis` | redis:7-alpine | 6379 | Caching layer |
| `rabbitmq` | rabbitmq:4-management | 5672, 15672 | Message broker |
| `survey-engine` | Custom build | 8080 | Application |

### Health Checks

All services have health checks configured with:
- Interval: 10 seconds
- Timeout: 5 seconds
- Retries: 10

---

**End of Document**
