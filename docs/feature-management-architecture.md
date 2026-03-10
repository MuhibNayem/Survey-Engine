# Enterprise Feature Management System

## Architecture Documentation

### Overview

The Enterprise Feature Management System provides a comprehensive solution for managing feature flags, guided tours, tooltips, banners, and announcements across the Survey Engine platform. It enables granular control over feature availability based on subscription plans, user roles, tenant configurations, and rollout percentages.

### System Architecture

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           FRONTEND (Svelte 5)                                │
├─────────────────────────────────────────────────────────────────────────────┤
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐             │
│  │ useFeatureFlag  │  │   FeatureTour   │  │ FeatureTooltip  │             │
│  │      Hook       │  │    Component    │  │    Component    │             │
│  └────────┬────────┘  └────────┬────────┘  └────────┬────────┘             │
│           │                    │                    │                       │
│  ┌────────▼────────────────────▼────────────────────▼────────┐             │
│  │                    API Client (axios)                      │             │
│  └────────────────────────────┬───────────────────────────────┘             │
└───────────────────────────────┼─────────────────────────────────────────────┘
                                │ HTTP/REST
┌───────────────────────────────▼─────────────────────────────────────────────┐
│                         BACKEND (Spring Boot)                                │
├─────────────────────────────────────────────────────────────────────────────┤
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │                  FeatureManagementController                         │   │
│  │  ┌────────────────────┐  ┌────────────────────┐  ┌───────────────┐ │   │
│  │  │ Admin APIs         │  │  User APIs         │  │ Analytics APIs│ │   │
│  │  │ /admin/features/*  │  │  /features/*       │  │ /analytics/*  │ │   │
│  │  └────────────────────┘  └────────────────────┘  └───────────────┘ │   │
│  └─────────────────────────────┬───────────────────────────────────────┘   │
│                                │                                            │
│  ┌─────────────────────────────▼───────────────────────────────────────┐   │
│  │                  FeatureManagementService                            │   │
│  │  ┌────────────────┐ ┌────────────────┐ ┌─────────────────────────┐  │   │
│  │  │ Availability   │ │  Completion    │ │  Tenant Configuration   │  │   │
│  │  │ Checks         │ │  Tracking      │ │  & Analytics            │  │   │
│  │  └────────────────┘ └────────────────┘ └─────────────────────────┘  │   │
│  └─────────────────────────────┬───────────────────────────────────────┘   │
│                                │                                            │
│  ┌─────────────────────────────▼───────────────────────────────────────┐   │
│  │                    Repositories Layer                                │   │
│  │  ┌──────────────────────┐ ┌──────────────────────┐                 │   │
│  │  │ FeatureDefinition    │ │ TenantFeatureConfig  │                 │   │
│  │  │ Repository           │ │ Repository           │                 │   │
│  │  └──────────────────────┘ └──────────────────────┘                 │   │
│  │  ┌──────────────────────┐                                           │   │
│  │  │ UserFeatureAccess    │                                           │   │
│  │  │ Repository           │                                           │   │
│  │  └──────────────────────┘                                           │   │
│  └─────────────────────────────┬───────────────────────────────────────┘   │
└────────────────────────────────┼───────────────────────────────────────────┘
                                 │ JPA/Hibernate
┌────────────────────────────────▼───────────────────────────────────────────┐
│                         PostgreSQL Database                                │
├────────────────────────────────────────────────────────────────────────────┤
│  ┌──────────────────────┐ ┌──────────────────────┐ ┌──────────────────┐  │
│  │ feature_definition   │ │ tenant_feature_config│ │ user_feature_access│ │
│  │ - id (UUID)          │ │ - id (UUID)          │ │ - id (UUID)      │  │
│  │ - feature_key        │ │ - tenant_id          │ │ - user_id        │  │
│  │ - feature_type       │ │ - feature_id (FK)    │ │ - feature_id (FK)│  │
│  │ - category           │ │ - enabled            │ │ - accessed       │  │
│  │ - enabled            │ │ - rollout_percentage │ │ - completed      │  │
│  │ - rollout_percentage │ │ - custom_metadata    │ │ - access_count   │  │
│  │ - min_plan           │ │                      │ │ - metadata       │  │
│  │ - roles (JSONB)      │ │                      │ │                  │  │
│  │ - platforms (JSONB)  │ │                      │ │                  │  │
│  │ - metadata (JSONB)   │ │                      │ │                  │  │
│  └──────────────────────┘ └──────────────────────┘ └──────────────────┘  │
└────────────────────────────────────────────────────────────────────────────┘
```

### Core Components

#### 1. Feature Definition (`feature_definition`)

Master registry of all features in the system.

**Fields:**
- `feature_key`: Unique identifier (e.g., "tour.dashboard")
- `feature_type`: TOUR, TOOLTIP, BANNER, FEATURE_FLAG, ANNOUNCEMENT
- `category`: Functional grouping (DASHBOARD, SURVEYS, CAMPAIGNS, etc.)
- `enabled`: Global enable/disable flag
- `rollout_percentage`: Percentage of users who see the feature (0-100)
- `min_plan`: Minimum subscription plan (BASIC, PRO, ENTERPRISE)
- `roles`: JSON array of allowed roles
- `platforms`: JSON array of supported platforms
- `metadata`: Feature-specific configuration (tour steps, tooltip content, etc.)

#### 2. Tenant Feature Configuration (`tenant_feature_config`)

Tenant-level overrides for feature behavior.

**Fields:**
- `tenant_id`: Tenant identifier
- `feature_id`: Reference to feature definition
- `enabled`: Tenant-specific enable override
- `rollout_percentage`: Tenant-specific rollout override
- `custom_metadata`: Tenant-specific metadata overrides

#### 3. User Feature Access (`user_feature_access`)

Tracks user-level feature access and completion status.

**Fields:**
- `user_id`: Reference to admin user
- `feature_id`: Reference to feature definition
- `accessed`: Whether user has seen the feature
- `completed`: Whether user has completed/dismissed the feature
- `access_count`: Number of times accessed
- `last_accessed_at`: Timestamp of last access
- `completed_at`: Timestamp when completed

### Feature Availability Flow

```
                    ┌─────────────────────┐
                    │  Feature Request    │
                    └──────────┬──────────┘
                               │
                    ┌──────────▼──────────┐
                    │  Get Feature Def    │
                    └──────────┬──────────┘
                               │
                    ┌──────────▼──────────┐
                    │  Check Global       │───DISABLED───► Return "DISABLED"
                    │  Enabled Status     │
                    └──────────┬──────────┘
                               │ ENABLED
                    ┌──────────▼──────────┐
                    │  Check User Role    │───NOT_ALLOWED──► Return "ROLE_NOT_ALLOWED"
                    └──────────┬──────────┘
                               │ ALLOWED
                    ┌──────────▼──────────┐
                    │  Check Plan         │───INSUFFICIENT──► Return "PLAN_TOO_LOW"
                    │  Requirements       │
                    └──────────┬──────────┘
                               │ SUFFICIENT
                    ┌──────────▼──────────┐
                    │  Check Tenant       │───DISABLED───► Return "DISABLED"
                    │  Configuration      │
                    └──────────┬──────────┘
                               │ ENABLED
                    ┌──────────▼──────────┐
                    │  Check Rollout      │───EXCLUDED───► Return "ROLLOUT_EXCLUDED"
                    │  Percentage         │
                    └──────────┬──────────┘
                               │ INCLUDED
                    ┌──────────▼──────────┐
                    │  Check User         │
                    │  Completion Status  │
                    └──────────┬──────────┘
                               │
                    ┌──────────▼──────────┐
                    │  Return AVAILABLE   │
                    │  with shouldShow    │
                    └─────────────────────┘
```

### API Endpoints

#### Admin APIs (SUPER_ADMIN only)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/admin/features` | List all features with pagination |
| GET | `/api/v1/admin/features/search?q={term}` | Search features |
| GET | `/api/v1/admin/features/{featureKey}` | Get feature details |
| POST | `/api/v1/admin/features` | Create new feature |
| PUT | `/api/v1/admin/features/{featureKey}` | Update feature |
| DELETE | `/api/v1/admin/features/{featureKey}` | Delete feature |
| POST | `/api/v1/admin/features/bulk` | Bulk create/update |
| POST | `/api/v1/admin/features/{featureKey}/tenants/{tenantId}/configure` | Configure for tenant |
| GET | `/api/v1/admin/features/{featureKey}/tenants` | List tenant configs |
| DELETE | `/api/v1/admin/features/{featureKey}/tenants/{tenantId}/configure` | Remove tenant config |
| GET | `/api/v1/admin/features/{featureKey}/analytics` | Get feature analytics |

#### User APIs (Any authenticated user)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/features/available` | Get available features |
| GET | `/api/v1/features/{featureKey}/status` | Check feature status |
| GET | `/api/v1/features/{featureKey}/completed` | Check completion |
| POST | `/api/v1/features/{featureKey}/complete` | Mark as completed |
| POST | `/api/v1/features/{featureKey}/reset` | Reset completion |
| POST | `/api/v1/features/{featureKey}/access` | Record access |

### Frontend Integration

#### useFeatureFlag Hook

```typescript
import { useFeatureFlag } from '$lib/hooks/useFeatureFlag';

const { 
  status,      // Current feature status
  isLoading,   // Loading state
  isAvailable, // Feature is available
  isCompleted, // Feature is completed
  shouldShow,  // Feature should be shown
  complete,    // Mark as completed
  reset,       // Reset completion
  refresh      // Refresh status
} = useFeatureFlag('tour.dashboard');
```

#### FeatureTour Component

```svelte
<script lang="ts">
  import { FeatureTour } from '$lib/components/onboarding';
  
  const tourConfig = {
    id: 'dashboard',
    title: 'Dashboard Tour',
    steps: [
      {
        id: 'welcome',
        title: 'Welcome',
        description: 'Let us explore the dashboard',
        targetSelector: '[data-tour="dashboard-header"]'
      }
      // ... more steps
    ]
  };
</script>

<FeatureTour {tourConfig} autoStart={true} />
```

#### FeatureTooltip Component

```svelte
<script lang="ts">
  import { FeatureTooltip } from '$lib/components/onboarding';
</script>

<FeatureTooltip
  tooltipId="survey-create"
  title="Quick Survey Creation"
  content="Click here to create a new survey"
  targetSelector="[data-tooltip=\"survey-create\"]"
  placement="bottom"
/>
```

#### FeatureBanner Component

```svelte
<script lang="ts">
  import { FeatureBanner } from '$lib/components/onboarding';
  
  const bannerConfig = {
    id: 'new-year-2026',
    title: 'New Year Update',
    content: 'Exciting new features for 2026',
    variant: 'info',
    dismissible: true,
    ctaText: 'Learn More',
    ctaUrl: '/docs/whats-new'
  };
</script>

<FeatureBanner {bannerConfig} position="top" />
```

### Security Considerations

1. **Role-Based Access Control**: All admin APIs require SUPER_ADMIN role
2. **Plan Enforcement**: Features are gated by subscription plan requirements
3. **Tenant Isolation**: Tenant configurations are scoped to specific tenants
4. **Audit Logging**: All feature changes are logged with actor information
5. **Input Validation**: All inputs are validated using Jakarta Validation

### Performance Optimizations

1. **Database Indexes**: Indexes on feature_key, category, enabled, min_plan
2. **JSONB Indexing**: GIN indexes on metadata columns for flexible querying
3. **Pagination**: All list endpoints support pagination
4. **Caching**: Consider adding Redis caching for frequently accessed features

### Monitoring & Observability

1. **Metrics to Track**:
   - Feature availability check rate
   - Feature completion rate
   - Rollout percentage effectiveness
   - Tenant configuration usage

2. **Logs to Monitor**:
   - Feature access denials (plan/role restrictions)
   - Configuration changes
   - Bulk operations

### Migration Guide

#### From Old System (UserPreference-based)

The new system replaces the simple key-value preferences with a comprehensive feature management system.

**Migration Steps:**

1. Run Flyway migration V28 to create new tables
2. Existing tour/tooltip completions in `user_preference` will continue to work
3. New features should use the FeatureManagement APIs
4. Gradually migrate existing tours to use the new system

**Backward Compatibility:**

- The existing `UserPreferenceController` remains functional
- New `FeatureManagementController` provides enhanced functionality
- Both systems can coexist during migration

### Testing Strategy

1. **Unit Tests**: Service layer tests with mocked repositories
2. **Integration Tests**: Controller tests with full Spring context
3. **E2E Tests**: Cypress tests for frontend feature flows

### Future Enhancements

1. **A/B Testing**: Support for multiple feature variants
2. **Scheduled Rollouts**: Time-based feature releases
3. **User Segments**: Advanced targeting beyond plan/role
4. **Feature Dependencies**: Require other features to be enabled
5. **Export/Import**: Feature configuration portability
