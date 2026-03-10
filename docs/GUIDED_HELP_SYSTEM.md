# Guided Help & Feature Tour System

## Overview

The Survey Engine now includes a comprehensive guided help system with:
- **Feature Tours** - Step-by-step walkthroughs for new features
- **Dismissible Tooltips** - Contextual help that remembers user preferences
- **Backend Persistence** - User preferences stored in database (not just localStorage)
- **"Don't Show Again"** - Users can permanently dismiss help elements

---

## Architecture

### Backend Components

#### 1. UserPreference Entity

**File:** `src/main/java/com/bracits/surveyengine/admin/entity/UserPreference.java`

```java
@Entity
@Table(name = "user_preference")
public class UserPreference {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "user_id", nullable = false)
    private UUID userId;
    
    @Column(name = "tenant_id", nullable = false)
    private String tenantId;
    
    @Column(name = "preferences", columnDefinition = "jsonb")
    @Builder.Default
    private Map<String, String> preferences = new HashMap<>();
    
    // Methods: isFeatureCompleted(), setFeatureCompleted(), etc.
}
```

**Purpose:** Stores all user UI/UX preferences in a JSONB column.

---

#### 2. UserPreferenceService

**File:** `src/main/java/com/bracits/surveyengine/admin/service/UserPreferenceService.java`

**Key Methods:**
```java
// Get all preferences
UserPreference getPreferences();

// Check if feature/tour is completed
boolean isFeatureCompleted(String featureKey);

// Mark feature as completed
void setFeatureCompleted(String featureKey, boolean completed);

// Reset all preferences
void resetPreferences();
```

---

#### 3. UserPreferenceController

**File:** `src/main/java/com/bracits/surveyengine/admin/controller/UserPreferenceController.java`

**API Endpoints:**

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/v1/admin/preferences` | Get all preferences |
| GET | `/api/v1/admin/preferences/{key}/completed` | Check if feature completed |
| POST | `/api/v1/admin/preferences/{key}/complete` | Mark as completed |
| PATCH | `/api/v1/admin/preferences/{key}` | Update single preference |
| PATCH | `/api/v1/admin/preferences` | Update multiple |
| DELETE | `/api/v1/admin/preferences` | Reset all preferences |

---

#### 4. Database Schema

**File:** `src/main/resources/db/migration/V27__add_user_preferences.sql`

```sql
CREATE TABLE user_preference (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    tenant_id VARCHAR(255) NOT NULL,
    preferences JSONB NOT NULL DEFAULT '{}',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    
    CONSTRAINT uk_user_preference_user_id UNIQUE (user_id),
    FOREIGN KEY (user_id) REFERENCES admin_user(id) ON DELETE CASCADE
);
```

**Example Data:**
```json
{
  "tour.dashboard.completed": "true",
  "tour.surveys.completed": "false",
  "tooltip.survey-builder.dismissed": "true",
  "help.campaign-settings.viewed": "false"
}
```

---

### Frontend Components

#### 1. FeatureTour Component

**File:** `frontend/src/lib/components/onboarding/FeatureTour.svelte`

**Usage:**
```svelte
<script lang="ts">
    import { FeatureTour } from '$lib/components/onboarding';
    import type { TourConfig } from '$lib/components/onboarding';

    const dashboardTour: TourConfig = {
        id: 'tour.dashboard',
        title: 'Dashboard Overview',
        steps: [
            {
                id: 'step1',
                title: 'Welcome to Your Dashboard',
                description: 'This is your command center for all survey activities.',
                content: 'You can view metrics, recent campaigns, and quick actions here.',
                targetSelector: '[data-tour="dashboard-metrics"]',
                placement: 'bottom'
            },
            {
                id: 'step2',
                title: 'Create New Survey',
                description: 'Click here to start creating your first survey.',
                targetSelector: '[data-tour="new-survey-btn"]',
                placement: 'right'
            }
        ]
    };
</script>

<FeatureTour 
    tour={dashboardTour} 
    autoStart={true}
    onComplete={() => console.log('Tour completed!')}
/>
```

**Features:**
- ✅ Backend persistence (saves to database)
- ✅ localStorage fallback (if backend unavailable)
- ✅ Element highlighting (rings target elements)
- ✅ Step indicators (clickable dots)
- ✅ "Don't show again" checkbox
- ✅ Keyboard navigation (arrow keys)
- ✅ Responsive design

---

#### 2. TooltipWithDismiss Component

**File:** `frontend/src/lib/components/onboarding/TooltipWithDismiss.svelte`

**Usage:**
```svelte
<script lang="ts">
    import { TooltipWithDismiss } from '$lib/components/onboarding';
</script>

<!-- Button that triggers tooltip -->
<Button data-tour="survey-builder">
    <Plus class="h-4 w-4" />
    New Survey
</Button>

<!-- Tooltip -->
<TooltipWithDismiss
    tooltipId="survey-builder"
    title="Create Your First Survey"
    content="Click this button to start building your survey. You can add multiple pages and questions."
    targetSelector="[data-tour='survey-builder']"
    placement="bottom"
    delay={2000}
/>
```

**Features:**
- ✅ Auto-dismiss after user checks "Don't show again"
- ✅ Backend persistence
- ✅ Smart positioning (avoids screen edges)
- ✅ Animated appearance
- ✅ Close button
- ✅ Checkbox to permanently dismiss

---

## Implementation Examples

### Example 1: Dashboard Tour

**File:** `frontend/src/routes/(app)/dashboard/+page.svelte`

```svelte
<script lang="ts">
    import { FeatureTour } from '$lib/components/onboarding';
    import type { TourConfig } from '$lib/components/onboarding';

    const dashboardTour: TourConfig = {
        id: 'tour.dashboard',
        title: 'Dashboard Guide',
        steps: [
            {
                id: 'step1',
                title: 'Welcome!',
                description: 'Your dashboard shows key metrics at a glance.',
                targetSelector: '[data-tour="metrics-cards"]',
                placement: 'bottom'
            },
            {
                id: 'step2',
                title: 'Recent Campaigns',
                description: 'See your active and recent campaigns here.',
                targetSelector: '[data-tour="recent-campaigns"]',
                placement: 'top'
            },
            {
                id: 'step3',
                title: 'Quick Actions',
                description: 'Create new surveys, campaigns, or view reports.',
                targetSelector: '[data-tour="quick-actions"]',
                placement: 'left'
            }
        ]
    };
</script>

<div class="space-y-6">
    <!-- Add data-tour attributes to elements -->
    <div data-tour="metrics-cards" class="grid gap-4 md:grid-cols-3">
        <!-- Metric cards... -->
    </div>

    <div data-tour="recent-campaigns">
        <!-- Recent campaigns... -->
    </div>

    <div data-tour="quick-actions">
        <!-- Quick actions... -->
    </div>

    <!-- Tour component -->
    <FeatureTour tour={dashboardTour} autoStart={true} />
</div>
```

---

### Example 2: Survey Builder Tooltips

**File:** `frontend/src/routes/(app)/surveys/+page.svelte`

```svelte
<script lang="ts">
    import { TooltipWithDismiss } from '$lib/components/onboarding';
</script>

<div class="space-y-4">
    <PageHeader
        title="Surveys"
        actionLabel="New Survey"
        data-tour="new-survey-btn"
    />

    <!-- Tooltip for New Survey button -->
    <TooltipWithDismiss
        tooltipId="new-survey"
        title="Create Survey"
        content="Start by clicking New Survey. You can add multiple pages, questions, and configure skip logic."
        targetSelector="[data-tour='new-survey-btn']"
        placement="bottom"
        delay={1500}
    />

    <!-- Search input tooltip -->
    <Input 
        placeholder="Search surveys..." 
        data-tour="search-surveys"
    />
    
    <TooltipWithDismiss
        tooltipId="search-surveys"
        title="Search Surveys"
        content="Quickly find surveys by typing keywords. Search works across titles and descriptions."
        targetSelector="[data-tour='search-surveys']"
        placement="right"
        delay={2000}
    />
</div>
```

---

### Example 3: Campaign Settings Help

**File:** `frontend/src/routes/(app)/campaigns/[id]/+page.svelte`

```svelte
<script lang="ts">
    import { FeatureTour, TooltipWithDismiss } from '$lib/components/onboarding';
    import type { TourConfig } from '$lib/components/onboarding';

    const campaignTour: TourConfig = {
        id: 'tour.campaigns',
        title: 'Campaign Setup Guide',
        steps: [
            {
                id: 'step1',
                title: 'Campaign Settings',
                description: 'Configure how respondents access your survey.',
                targetSelector: '[data-tour="campaign-settings"]',
                placement: 'top'
            },
            {
                id: 'step2',
                title: 'Distribution Channels',
                description: 'Choose how to share your campaign: link, embed, or email.',
                targetSelector: '[data-tour="distribution-channels"]',
                placement: 'left'
            },
            {
                id: 'step3',
                title: 'Launch Campaign',
                description: 'Click here to activate your campaign and start collecting responses.',
                targetSelector: '[data-tour="launch-campaign-btn"]',
                placement: 'bottom'
            }
        ]
    };
</script>

<div data-tour="campaign-settings">
    <h2>Campaign Settings</h2>
    <!-- Settings form... -->
</div>

<div data-tour="distribution-channels">
    <h2>Distribution</h2>
    <!-- Distribution options... -->
</div>

<Button data-tour="launch-campaign-btn">
    Launch Campaign
</Button>

<FeatureTour tour={campaignTour} autoStart={true} />

<TooltipWithDismiss
    tooltipId="campaign-password"
    title="Password Protection"
    content="Optionally require a password for respondents to access your survey."
    targetSelector="[data-tour='password-field']"
    placement="right"
/>
```

---

## Best Practices

### 1. Naming Conventions

**Use consistent keys:**
```typescript
// Tours
"tour.dashboard.completed"
"tour.surveys.completed"
"tour.campaigns.completed"

// Tooltips
"tooltip.survey-builder.dismissed"
"tooltip.campaign-settings.dismissed"

// Help
"help.analytics-guide.viewed"
"help.export-feature.viewed"
```

---

### 2. Target Selectors

**Use data attributes for reliability:**
```svelte
<!-- ✅ GOOD: Stable selector -->
<Button data-tour="new-survey-btn">New Survey</Button>

<!-- ❌ BAD: May change with styling -->
<Button class="bg-primary text-white">New Survey</Button>
```

---

### 3. Timing

**Don't overwhelm users:**
```typescript
// ✅ GOOD: Staggered tooltips
<TooltipWithDismiss delay={1000} />  // First tooltip
<TooltipWithDismiss delay={3000} />  // Second tooltip (2s later)
<TooltipWithDismiss delay={5000} />  // Third tooltip (2s later)

// ❌ BAD: All at once
<TooltipWithDismiss delay={1000} />
<TooltipWithDismiss delay={1000} />
<TooltipWithDismiss delay={1000} />
```

---

### 4. Content Guidelines

**Keep it concise:**
```typescript
// ✅ GOOD: Clear and brief
{
    title: "Create Survey",
    description: "Click to start building your survey.",
    content: "You can add multiple pages and questions."
}

// ❌ BAD: Too long
{
    title: "How to Create a Survey",
    description: "This is a very long explanation about surveys...",
    content: "Surveys are composed of pages which contain questions..."
}
```

---

## Testing

### Manual Testing Checklist

**Feature Tour:**
- [ ] Tour starts automatically on first visit
- [ ] Steps navigate correctly (Next/Previous)
- [ ] Target elements are highlighted
- [ ] "Don't show again" checkbox works
- [ ] Completion saves to backend
- [ ] Tour doesn't show again after completion
- [ ] Keyboard navigation works (arrow keys)
- [ ] Responsive on mobile

**Tooltip:**
- [ ] Tooltip appears after delay
- [ ] Positioned correctly
- [ ] "Don't show again" dismisses permanently
- [ ] Close button works
- [ ] Doesn't reappear on page refresh
- [ ] Backend persistence works

---

### API Testing

**Get Preferences:**
```bash
curl -X GET http://localhost:8080/api/v1/admin/preferences \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**Mark Tour Complete:**
```bash
curl -X POST "http://localhost:8080/api/v1/admin/preferences/tour.dashboard/complete?completed=true" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**Reset All Preferences:**
```bash
curl -X DELETE http://localhost:8080/api/v1/admin/preferences \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

## Troubleshooting

### Issue: Tour shows every time

**Cause:** Backend save failing

**Solution:**
1. Check browser console for API errors
2. Verify backend logs
3. Check database connection
4. Manually verify in database:
   ```sql
   SELECT preferences FROM user_preference WHERE user_id = 'your-user-id';
   ```

---

### Issue: Tooltip not appearing

**Cause:** Target selector not found

**Solution:**
```javascript
// Debug in browser console
document.querySelector('[data-tour="your-selector"]');
// Should return the element
```

---

### Issue: Preferences not saving

**Cause:** API endpoint incorrect or auth issue

**Solution:**
1. Verify JWT token is valid
2. Check network tab for 401/403 errors
3. Verify user has correct role (ADMIN, EDITOR, VIEWER)
4. Check backend logs

---

## Migration from localStorage

If you have existing localStorage-based tours:

```typescript
// In onMount, migrate old data
onMount(async () => {
    // Get old localStorage data
    const oldTourComplete = localStorage.getItem('tour-complete');
    
    if (oldTourComplete) {
        // Migrate to backend
        try {
            await api.patch('/admin/preferences', {
                'tour.main.completed': oldTourComplete
            });
            // Clear old localStorage
            localStorage.removeItem('tour-complete');
        } catch (error) {
            console.error('Migration failed:', error);
        }
    }
});
```

---

## Future Enhancements

### Phase 1 (Recommended)
- [ ] **Analytics** - Track which tours are most viewed
- [ ] **A/B Testing** - Test different tour content
- [ ] **Progressive Disclosure** - Show advanced features over time

### Phase 2 (Advanced)
- [ ] **Video Tours** - Embed video walkthroughs
- [ ] **Interactive Tours** - Let users practice actions
- [ ] **Contextual Help** - AI-powered suggestions

### Phase 3 (Enterprise)
- [ ] **Custom Tours** - Admin-created tours for their team
- [ ] **Multi-language** - i18n support for tours
- [ ] **Compliance Tracking** - Log training completion

---

## Related Files

### Backend
- `src/main/java/com/bracits/surveyengine/admin/entity/UserPreference.java`
- `src/main/java/com/bracits/surveyengine/admin/repository/UserPreferenceRepository.java`
- `src/main/java/com/bracits/surveyengine/admin/service/UserPreferenceService.java`
- `src/main/java/com/bracits/surveyengine/admin/controller/UserPreferenceController.java`
- `src/main/resources/db/migration/V27__add_user_preferences.sql`

### Frontend
- `frontend/src/lib/components/onboarding/FeatureTour.svelte`
- `frontend/src/lib/components/onboarding/TooltipWithDismiss.svelte`
- `frontend/src/lib/components/onboarding/index.ts`

---

**Implemented:** March 10, 2026  
**Status:** ✅ Ready for Integration  
**Backend:** ✅ API endpoints created  
**Frontend:** ✅ Components created  
**Database:** ✅ Migration V27 ready
