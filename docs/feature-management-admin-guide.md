# Feature Management Admin User Guide

## Getting Started

### Accessing the Feature Management Dashboard

1. Log in as a SUPER_ADMIN user
2. Navigate to **Admin > Features** or go to `/admin/features`

## Managing Features

### Creating a New Feature

1. Click the **New Feature** button
2. Fill in the required fields:
   - **Feature Key**: Unique identifier (e.g., `tour.dashboard`, `tooltip.survey.create`)
     - Must start with a lowercase letter
     - Can contain lowercase letters, numbers, dots, and underscores
   - **Name**: Human-readable name
   - **Type**: Select from TOUR, TOOLTIP, BANNER, FEATURE_FLAG, ANNOUNCEMENT
   - **Category**: Select functional category (DASHBOARD, SURVEYS, etc.)

3. Configure optional settings:
   - **Description**: Detailed explanation
   - **Minimum Plan**: BASIC, PRO, or ENTERPRISE
   - **Rollout Percentage**: 0-100% for gradual releases
   - **Allowed Roles**: Select which roles can access
   - **Platforms**: WEB, MOBILE, DESKTOP, API
   - **Enabled**: Toggle to activate/deactivate

4. Click **Create**

### Editing a Feature

1. Find the feature in the list
2. Click the **More Actions** menu (⋮)
3. Select **Edit**
4. Modify the desired fields
5. Click **Update**

**Note**: Feature Key cannot be changed after creation.

### Deleting a Feature

1. Find the feature in the list
2. Click the **More Actions** menu (⋮)
3. Select **Delete**
4. Confirm the deletion

**Warning**: This will also delete all tenant configurations and user access records for this feature.

### Toggling Feature Status

- Use the toggle switch in the **Status** column to quickly enable/disable a feature
- Disabled features are unavailable to all users regardless of other settings

## Tenant Configuration

### Configuring Features for Specific Tenants

1. Navigate to a feature's **Tenant Config** page
2. Enter the Tenant ID
3. Configure:
   - **Enabled**: Override global enabled status
   - **Rollout Percentage**: Override global rollout percentage
4. Click **Add**

### Removing Tenant Configuration

1. Find the tenant in the list
2. Click the **Delete** (trash) icon
3. The tenant will revert to global settings

## Viewing Analytics

### Feature Analytics Dashboard

1. Click the **More Actions** menu (⋮) on any feature
2. Select **Analytics**

**Metrics Displayed:**
- **Total Accessed**: Number of users who saw the feature
- **Total Completed**: Number of users who completed the feature
- **Completion Rate**: Percentage of accessed users who completed
- **Unique Tenants**: Number of organizations using the feature

### Understanding Metrics

- **High Access, Low Completion**: Users see but don't complete (may need UX improvement)
- **Low Access**: Feature may need better placement or promotion
- **High Completion Rate**: Feature is well-received

## Feature Types

### Tours (TOUR)

Interactive guided experiences through UI features.

**Best Practices:**
- Keep tours under 5 steps
- Use clear, concise descriptions
- Target specific UI elements with selectors
- Allow users to skip

**Metadata Structure:**
```json
{
  "steps": [
    {
      "id": "step-1",
      "title": "Step Title",
      "description": "Step description",
      "targetSelector": "[data-tour=\"element\"]"
    }
  ]
}
```

### Tooltips (TOOLTIP)

Contextual help that appears near UI elements.

**Best Practices:**
- Keep content brief (1-2 sentences)
- Position near relevant elements
- Allow dismissal with "Don't show again"

**Metadata Structure:**
```json
{
  "title": "Tooltip Title",
  "content": "Helpful information",
  "targetSelector": "[data-tooltip=\"element\"]",
  "placement": "top"
}
```

### Banners (BANNER)

Prominent announcements at the top or bottom of the page.

**Best Practices:**
- Use for important announcements only
- Include clear call-to-action
- Set expiration dates for time-sensitive content

**Metadata Structure:**
```json
{
  "title": "Announcement Title",
  "content": "Announcement details",
  "ctaText": "Learn More",
  "ctaUrl": "/docs/more-info",
  "priority": "high",
  "dismissible": true
}
```

### Feature Flags (FEATURE_FLAG)

Binary toggles for functionality.

**Best Practices:**
- Use for gradual rollouts
- Plan for flag removal after full rollout
- Document flag purpose in description

### Announcements (ANNOUNCEMENT)

System-wide messages with date ranges.

**Best Practices:**
- Set start and end dates
- Use appropriate priority levels
- Make dismissible for non-critical content

## Filtering and Search

### Filtering Features

Use the filter dropdowns to narrow down features by:
- **Category**: DASHBOARD, SURVEYS, CAMPAIGNS, etc.
- **Type**: TOUR, TOOLTIP, BANNER, etc.
- **Plan**: BASIC, PRO, ENTERPRISE

### Searching Features

Enter text in the search box to find features by:
- Feature name
- Feature key
- Description

## Bulk Operations

### Bulk Create/Update

Use the bulk API to manage multiple features at once.

**Request Format:**
```json
[
  {
    "featureKey": "tour.dashboard",
    "name": "Dashboard Tour",
    "featureType": "TOUR",
    "category": "DASHBOARD"
  },
  {
    "featureKey": "tooltip.survey.create",
    "name": "Survey Create Tooltip",
    "featureType": "TOOLTIP",
    "category": "SURVEYS"
  }
]
```

**Endpoint:** `POST /api/v1/admin/features/bulk`

## Rollout Strategies

### Gradual Rollout

1. Start with low percentage (10-20%)
2. Monitor analytics for issues
3. Gradually increase (50%, 75%, 100%)
4. Watch for increased support tickets

### Role-Based Rollout

1. Start with SUPER_ADMIN only
2. Expand to ADMIN
3. Then EDITOR
4. Finally all roles including VIEWER

### Plan-Based Rollout

1. Launch on ENTERPRISE first
2. Expand to PRO
3. Finally BASIC

## Troubleshooting

### Feature Not Showing for User

Check these in order:
1. Is the feature enabled globally?
2. Does user's plan meet minimum requirements?
3. Is user's role allowed?
4. Is user within rollout percentage?
5. Has user already completed the feature?
6. Is there a tenant configuration disabling it?

### Analytics Not Updating

1. Check that users are accessing the feature
2. Verify API calls to `/features/{key}/access` are being made
3. Ensure database is not experiencing issues

### Tenant Configuration Not Taking Effect

1. Verify tenant ID is correct
2. Check that configuration is enabled
3. Ensure no conflicting configurations exist

## API Reference

For programmatic access, see the OpenAPI documentation at `/swagger-ui.html`.

### Quick Reference

```bash
# List all features
curl -H "Authorization: Bearer <token>" \
  https://api.survey-engine.com/api/v1/admin/features

# Create feature
curl -X POST -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"featureKey":"tour.test","name":"Test","featureType":"TOUR","category":"GENERAL"}' \
  https://api.survey-engine.com/api/v1/admin/features

# Configure for tenant
curl -X POST -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"enabled":true,"rolloutPercentage":50}' \
  https://api.survey-engine.com/api/v1/admin/features/tour.test/tenants/tenant-123/configure

# Get analytics
curl -H "Authorization: Bearer <token>" \
  https://api.survey-engine.com/api/v1/admin/features/tour.test/analytics
```

## Best Practices

1. **Naming Conventions**: Use descriptive, hierarchical keys (e.g., `tour.module.feature`)
2. **Documentation**: Always fill in the description field
3. **Cleanup**: Remove feature flags after full rollout
4. **Testing**: Test features in staging before production
5. **Communication**: Notify users of new tours/announcements
6. **Monitoring**: Regularly review analytics for optimization
7. **Security**: Never expose sensitive features to lower plans without review
