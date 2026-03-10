# Tour Implementation Guide

## ✅ Completed Tours

### 1. Dashboard Tour (`/dashboard`)
**Status:** ✅ IMPLEMENTED

**Tour ID:** `tour.dashboard`

**Steps:**
1. Dashboard Stats (data-tour="dashboard-stats")
2. Recent Campaigns (data-tour="recent-campaigns")  
3. Quick Actions (data-tour="quick-actions")

**Files Modified:**
- `src/routes/(app)/dashboard/+page.svelte`

---

### 2. Surveys Tour (`/surveys`)
**Status:** ✅ IMPLEMENTED

**Tour ID:** `tour.surveys`

**Steps:**
1. New Survey Button (data-tour="new-survey-btn")
2. Survey List (data-tour="survey-list")

**Files Modified:**
- `src/routes/(app)/surveys/+page.svelte`

---

## 📋 Remaining Tours to Implement

### 3. Questions Tour (`/questions`)
**Status:** ⏳ TODO

**Tour ID:** `tour.questions`

**Steps:**
1. New Question Button
2. Question List

**Required Changes:**

**File:** `src/routes/(app)/questions/+page.svelte`

**Add import:**
```typescript
import { FeatureTour } from '$lib/components/onboarding';
```

**Add tour config (after imports):**
```typescript
const questionsTour = {
    id: 'tour.questions',
    title: 'Question Bank',
    steps: [
        {
            id: 'step1',
            title: 'Add Question',
            description: 'Create reusable questions for your surveys.',
            targetSelector: '[data-tour="new-question-btn"]',
            placement: 'bottom' as const
        },
        {
            id: 'step2',
            title: 'Question List',
            description: 'View and manage all your questions.',
            targetSelector: '[data-tour="question-list"]',
            placement: 'top' as const
        }
    ]
};
```

**Add data-tour attributes:**
```html
<!-- PageHeader action button -->
<PageHeader
    actionLabel="New Question"
    data-tour="new-question-btn"
    ...
/>

<!-- Questions list container -->
<div data-tour="question-list">
    ...
</div>
```

**Add tour component (before closing tag):**
```html
<FeatureTour tour={questionsTour} autoStart={true} />
```

---

### 4. Campaigns Tour (`/campaigns`)
**Status:** ⏳ TODO

**Tour ID:** `tour.campaigns`

**Steps:**
1. New Campaign Button
2. Campaign Settings
3. Launch Campaign Button

**Required Changes:**

**File:** `src/routes/(app)/campaigns/+page.svelte`

**Add import:**
```typescript
import { FeatureTour } from '$lib/components/onboarding';
```

**Add tour config:**
```typescript
const campaignsTour = {
    id: 'tour.campaigns',
    title: 'Campaign Setup',
    steps: [
        {
            id: 'step1',
            title: 'Create Campaign',
            description: 'Start by clicking New Campaign.',
            targetSelector: '[data-tour="new-campaign-btn"]',
            placement: 'right' as const
        },
        {
            id: 'step2',
            title: 'Campaign Settings',
            description: 'Configure runtime controls before launch.',
            targetSelector: '[data-tour="campaign-settings"]',
            placement: 'top' as const
        },
        {
            id: 'step3',
            title: 'Launch Campaign',
            description: 'Click here to activate your campaign.',
            targetSelector: '[data-tour="launch-campaign-btn"]',
            placement: 'bottom' as const
        }
    ]
};
```

**Add data-tour attributes to appropriate elements**

**Add tour component:**
```html
<FeatureTour tour={campaignsTour} autoStart={true} />
```

---

### 5. Categories Tour (`/categories`)
**Status:** ⏳ TODO

**Tour ID:** `tour.categories`

**Steps:**
1. New Category Button
2. Category List

**Required Changes:**

**File:** `src/routes/(app)/categories/+page.svelte`

**Add import, tour config, data-tour attributes, and FeatureTour component** (similar pattern as above)

---

## 🎯 Implementation Pattern

For each page, follow this pattern:

### Step 1: Import FeatureTour
```typescript
import { FeatureTour } from '$lib/components/onboarding';
```

### Step 2: Define Tour Configuration
```typescript
const pageTour = {
    id: 'tour.page-name',
    title: 'Page Title',
    steps: [
        {
            id: 'step1',
            title: 'Step Title',
            description: 'Step description.',
            targetSelector: '[data-tour="element-id"]',
            placement: 'bottom' as const
        }
    ]
};
```

### Step 3: Add data-tour Attributes
```html
<div data-tour="element-id">...</div>
```

### Step 4: Add FeatureTour Component
```html
<FeatureTour tour={pageTour} autoStart={true} />
```

---

## 📊 Tour Seed Data

All tours are already seeded in the database via migration `V29__add_tour_seed_data.sql`:

- ✅ `tour.dashboard`
- ✅ `tour.surveys`
- ✅ `tour.campaigns`
- ✅ `tour.questions`
- ✅ `tour.categories`

---

## 🧪 Testing

To test tours:

1. **Clear tour completion:**
   ```sql
   DELETE FROM user_feature_access 
   WHERE feature_id IN (
     SELECT id FROM feature_definition 
     WHERE feature_key LIKE 'tour.%'
   );
   ```

2. **OR use incognito/private browsing**

3. **Navigate to page** - Tour should auto-start

4. **Complete or skip tour**

5. **Tour won't show again** (completion is saved)

---

## ✅ Status Summary

| Page | Tour ID | Status |
|------|---------|--------|
| Dashboard | tour.dashboard | ✅ IMPLEMENTED |
| Surveys | tour.surveys | ✅ IMPLEMENTED |
| Questions | tour.questions | ⏳ TODO |
| Campaigns | tour.campaigns | ⏳ TODO |
| Categories | tour.categories | ⏳ TODO |

---

**Priority:** Implement Questions, Campaigns, and Categories tours following the same pattern as Dashboard and Surveys.
