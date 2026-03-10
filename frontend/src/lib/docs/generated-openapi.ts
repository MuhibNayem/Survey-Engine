/* AUTO-GENERATED FILE. DO NOT EDIT MANUALLY. */
export const generatedOpenApi = {
  "info": {
    "title": "Survey Engine MVP API",
    "version": "1.1.0",
    "description": "Comprehensive API contract for the Survey Engine MVP.\n\nThis specification is written for product, implementation, and integration teams.\nEvery endpoint description intentionally includes:\n1) why the endpoint is needed,\n2) what the endpoint does,\n3) how the endpoint works inside the platform lifecycle.\n\nSecurity model summary:\n- Admin APIs accept engine-issued JWT via HttpOnly access_token cookie (primary) or Bearer header (fallback).\n- Headless clients can use explicit token-mode auth endpoints under /api/v1/admin/auth/token/*.\n- Admin sessions are maintained with refresh-token cookie rotation.\n- Browser cookie mode exposes CSRF bootstrap endpoint at /api/v1/admin/auth/csrf.\n- Responder submission endpoint is public at transport level, but private campaigns enforce tenant-scoped responder authentication in business logic.\n"
  },
  "tags": [
    {
      "name": "Admin Authentication",
      "description": "Register, login, and refresh token operations for tenant administrators."
    },
    {
      "name": "Subscription",
      "description": "Tenant subscription visibility and checkout operations."
    },
    {
      "name": "Plan Catalog",
      "description": "Plan listing and super-admin plan governance endpoints."
    },
    {
      "name": "Questions",
      "description": "Question bank lifecycle endpoints."
    },
    {
      "name": "Categories",
      "description": "Category lifecycle endpoints and question grouping."
    },
    {
      "name": "Surveys",
      "description": "Survey authoring and lifecycle transition endpoints."
    },
    {
      "name": "Campaigns",
      "description": "Campaign setup, settings, activation, and distribution endpoints."
    },
    {
      "name": "Auth Profiles",
      "description": "Tenant responder-auth configuration and diagnostics endpoints."
    },
    {
      "name": "OIDC Respondent Flow",
      "description": "Private responder OIDC start and callback endpoints."
    },
    {
      "name": "Responses",
      "description": "Response submission, lock/reopen workflow, and campaign analytics endpoints."
    },
    {
      "name": "Scoring",
      "description": "Weight profile lifecycle and score calculation endpoints."
    },
    {
      "name": "Super Admin",
      "description": "Platform-level tenant administration, impersonation, and metrics endpoints."
    },
    {
      "name": "Audit Logs",
      "description": "Tenant-scoped and platform-scoped activity log retrieval endpoints."
    },
    {
      "name": "Feature Management",
      "description": "Enterprise feature management APIs for tours, tooltips, banners, and feature flags. Super admin endpoints for feature governance and tenant configuration."
    },
    {
      "name": "User Features",
      "description": "User-level feature access and completion tracking endpoints."
    }
  ],
  "operations": [
    {
      "id": "getCsrfToken",
      "method": "GET",
      "endpoint": "/admin/auth/csrf",
      "path": "/api/v1/admin/auth/csrf",
      "summary": "Get CSRF token for browser cookie session mode",
      "description": "Why this endpoint is needed:\nBrowser-based admin sessions use HttpOnly auth cookies and require CSRF token submission for state-changing requests.\n\nWhat this endpoint does:\nIt returns the current CSRF token value and ensures CSRF cookie state is initialized for the caller.\n\nHow this endpoint does it:\nSpring Security creates/loads token via cookie token repository, controller returns it as a simple JSON payload.\n",
      "tags": [
        "Admin Authentication"
      ],
      "pathParams": [],
      "queryParams": [],
      "headers": [],
      "requestBody": []
    },
    {
      "id": "loginAdmin",
      "method": "POST",
      "endpoint": "/admin/auth/login",
      "path": "/api/v1/admin/auth/login",
      "summary": "Log in an existing admin and issue fresh auth cookies",
      "description": "Why this endpoint is needed:\nAdmin operators need a secure login path to start authenticated management sessions for tenant resources.\n\nWhat this endpoint does:\nIt authenticates the admin using email/password, revokes prior refresh tokens for session hygiene, and issues\na fresh access token plus a new refresh token as HttpOnly cookies.\n\nHow this endpoint does it:\nThe service verifies account status and password hash, rotates refresh-token state to avoid stale session reuse,\nand controller writes cookies while returning safe user context payload.\n",
      "tags": [
        "Admin Authentication"
      ],
      "pathParams": [],
      "queryParams": [],
      "headers": [],
      "requestBody": [
        {
          "name": "email",
          "type": "string",
          "required": true
        },
        {
          "name": "password",
          "type": "string",
          "required": true
        }
      ]
    },
    {
      "id": "logoutAdmin",
      "method": "POST",
      "endpoint": "/admin/auth/logout",
      "path": "/api/v1/admin/auth/logout",
      "summary": "Clear admin auth cookies",
      "description": "Why this endpoint is needed:\nSessions must support explicit and secure sign-out to prevent lingering browser-side authentication state.\n\nWhat this endpoint does:\nIt clears access_token and refresh_token cookies.\n\nHow this endpoint does it:\nController writes both token cookies with maxAge=0, path=/, HttpOnly/Secure attributes.\n",
      "tags": [
        "Admin Authentication"
      ],
      "pathParams": [],
      "queryParams": [],
      "headers": [],
      "requestBody": []
    },
    {
      "id": "getCurrentAdmin",
      "method": "GET",
      "endpoint": "/admin/auth/me",
      "path": "/api/v1/admin/auth/me",
      "summary": "Retrieve current authenticated admin profile",
      "description": "Why this endpoint is needed:\nFrontend needs a lightweight session-hydration endpoint after reload to confirm authentication and tenant context.\n\nWhat this endpoint does:\nIt returns current authenticated user's profile details (no tokens).\n\nHow this endpoint does it:\nJWT filter populates security/tenant context from cookie or bearer token, and controller maps context to response DTO.\n",
      "tags": [
        "Admin Authentication"
      ],
      "pathParams": [],
      "queryParams": [],
      "headers": [],
      "requestBody": []
    },
    {
      "id": "refreshAdminToken",
      "method": "POST",
      "endpoint": "/admin/auth/refresh",
      "path": "/api/v1/admin/auth/refresh",
      "summary": "Rotate refresh cookie and issue a new admin session",
      "description": "Why this endpoint is needed:\nAccess tokens are intentionally short-lived. A secure refresh mechanism is required to keep admins signed in\nwithout repeatedly collecting credentials.\n\nWhat this endpoint does:\nIt reads refresh token from HttpOnly cookie, validates it, rotates token state, and sets fresh access/refresh cookies.\n\nHow this endpoint does it:\nController extracts refresh_token cookie, service validates persistence state (including expiry/revocation),\napplies one-time-use rotation semantics, and controller rewrites secure cookies.\n",
      "tags": [
        "Admin Authentication"
      ],
      "pathParams": [],
      "queryParams": [],
      "headers": [],
      "requestBody": []
    },
    {
      "id": "registerAdmin",
      "method": "POST",
      "endpoint": "/admin/auth/register",
      "path": "/api/v1/admin/auth/register",
      "summary": "Register a tenant admin account and bootstrap tenant access",
      "description": "Why this endpoint is needed:\nA brand-new subscriber needs a secure, first-class admin identity to operate the platform. Without this endpoint,\nonboarding would require manual platform-team intervention and would break self-service SaaS adoption.\n\nWhat this endpoint does:\nIt creates the first admin user for a tenant, ensures tenant provisioning exists, ensures a trial subscription exists,\nsets access/refresh tokens as HttpOnly cookies, and returns safe user context in response body.\n\nHow this endpoint does it:\nThe service validates uniqueness of email, provisions tenant records when necessary, enforces admin-user quota policy,\nstores password hash securely, issues an engine-signed JWT access token, creates a refresh token row, and controller\nwrites both tokens into secure cookies before returning AuthUserResponse.\n",
      "tags": [
        "Admin Authentication"
      ],
      "pathParams": [],
      "queryParams": [],
      "headers": [],
      "requestBody": [
        {
          "name": "fullName",
          "type": "string",
          "required": true
        },
        {
          "name": "email",
          "type": "string",
          "required": true
        },
        {
          "name": "password",
          "type": "string",
          "required": true
        },
        {
          "name": "confirmPassword",
          "type": "string",
          "required": true
        },
        {
          "name": "tenantId",
          "type": "string",
          "required": false
        }
      ]
    },
    {
      "id": "revertImpersonation",
      "method": "POST",
      "endpoint": "/admin/auth/revert-impersonation",
      "path": "/api/v1/admin/auth/revert-impersonation",
      "summary": "Restore original super-admin cookies after impersonation",
      "description": "Why this endpoint is needed:\nSuper-admin support sessions must safely return from tenant impersonation to original privileged context.\n\nWhat this endpoint does:\nIt restores stashed super-admin auth cookies and clears temporary impersonation cookies.\n\nHow this endpoint does it:\nCookie utility reads stashed `sa_*` cookies, rewrites primary auth cookies, and clears impersonation artifacts.\n",
      "tags": [
        "Admin Authentication"
      ],
      "pathParams": [],
      "queryParams": [],
      "headers": [],
      "requestBody": []
    },
    {
      "id": "loginAdminTokenMode",
      "method": "POST",
      "endpoint": "/admin/auth/token/login",
      "path": "/api/v1/admin/auth/token/login",
      "summary": "Log in admin in headless token mode",
      "description": "Why this endpoint is needed:\nAPI clients and external automation flows need explicit JWT credentials in response body.\n\nWhat this endpoint does:\nIt authenticates admin credentials and returns fresh access/refresh tokens directly in payload.\n\nHow this endpoint does it:\nService validates credentials, rotates refresh-token state, and controller returns AuthResponse DTO.\n",
      "tags": [
        "Admin Authentication"
      ],
      "pathParams": [],
      "queryParams": [],
      "headers": [],
      "requestBody": [
        {
          "name": "email",
          "type": "string",
          "required": true
        },
        {
          "name": "password",
          "type": "string",
          "required": true
        }
      ]
    },
    {
      "id": "refreshAdminTokenMode",
      "method": "POST",
      "endpoint": "/admin/auth/token/refresh",
      "path": "/api/v1/admin/auth/token/refresh",
      "summary": "Refresh session in headless token mode",
      "description": "Why this endpoint is needed:\nHeadless integrations need refresh-token rotation without cookie transport.\n\nWhat this endpoint does:\nIt validates provided refresh token and returns a rotated access/refresh token pair.\n\nHow this endpoint does it:\nController accepts RefreshTokenRequest payload and delegates to refresh-token rotation service.\n",
      "tags": [
        "Admin Authentication"
      ],
      "pathParams": [],
      "queryParams": [],
      "headers": [],
      "requestBody": [
        {
          "name": "refreshToken",
          "type": "string",
          "required": true
        }
      ]
    },
    {
      "id": "registerAdminTokenMode",
      "method": "POST",
      "endpoint": "/admin/auth/token/register",
      "path": "/api/v1/admin/auth/token/register",
      "summary": "Register admin in headless token mode",
      "description": "Why this endpoint is needed:\nHeadless/API clients cannot reliably consume browser cookie flows and need explicit token payloads.\n\nWhat this endpoint does:\nIt performs the same registration flow as cookie mode but returns access/refresh tokens directly in response body.\n\nHow this endpoint does it:\nService runs standard tenant bootstrap/registration pipeline and controller returns AuthResponse without writing cookies.\n",
      "tags": [
        "Admin Authentication"
      ],
      "pathParams": [],
      "queryParams": [],
      "headers": [],
      "requestBody": [
        {
          "name": "fullName",
          "type": "string",
          "required": true
        },
        {
          "name": "email",
          "type": "string",
          "required": true
        },
        {
          "name": "password",
          "type": "string",
          "required": true
        },
        {
          "name": "confirmPassword",
          "type": "string",
          "required": true
        },
        {
          "name": "tenantId",
          "type": "string",
          "required": false
        }
      ]
    },
    {
      "id": "listFeatures",
      "method": "GET",
      "endpoint": "/admin/features",
      "path": "/api/v1/admin/features",
      "summary": "List all feature definitions",
      "description": "Why this endpoint is needed:\nSuper administrators need a centralized view of all registered features including tours, tooltips, banners, and feature flags.\n\nWhat this endpoint does:\nReturns paginated list of feature definitions with filtering by category and type.\n",
      "tags": [
        "Feature Management"
      ],
      "pathParams": [],
      "queryParams": [
        {
          "name": "category",
          "type": "string(GENERAL|DASHBOARD|SURVEYS|CAMPAIGNS|QUESTIONS|ANALYTICS|RESPONSES|SETTINGS|ADMIN)",
          "required": false
        },
        {
          "name": "type",
          "type": "string(TOUR|TOOLTIP|BANNER|FEATURE_FLAG|ANNOUNCEMENT)",
          "required": false
        },
        {
          "name": "page",
          "type": "integer",
          "required": false,
          "defaultValue": "0"
        },
        {
          "name": "size",
          "type": "integer",
          "required": false,
          "defaultValue": "20"
        }
      ],
      "headers": [],
      "requestBody": []
    },
    {
      "id": "createFeature",
      "method": "POST",
      "endpoint": "/admin/features",
      "path": "/api/v1/admin/features",
      "summary": "Create a new feature definition",
      "description": "Why this endpoint is needed:\nSuper administrators need to register new features in the central registry with metadata, access rules, and rollout configuration.\n",
      "tags": [
        "Feature Management"
      ],
      "pathParams": [],
      "queryParams": [],
      "headers": [],
      "requestBody": [
        {
          "name": "featureKey",
          "type": "string",
          "required": true
        },
        {
          "name": "featureType",
          "type": "string(TOUR|TOOLTIP|BANNER|FEATURE_FLAG|ANNOUNCEMENT)",
          "required": true
        },
        {
          "name": "category",
          "type": "string(GENERAL|DASHBOARD|SURVEYS|CAMPAIGNS|QUESTIONS|ANALYTICS|RESPONSES|SETTINGS|ADMIN)",
          "required": true
        },
        {
          "name": "name",
          "type": "string",
          "required": true
        },
        {
          "name": "description",
          "type": "string",
          "required": false
        },
        {
          "name": "enabled",
          "type": "boolean",
          "required": true,
          "defaultValue": "true"
        },
        {
          "name": "rolloutPercentage",
          "type": "integer",
          "required": false,
          "defaultValue": "100"
        },
        {
          "name": "minPlan",
          "type": "string(BASIC|PRO|ENTERPRISE)",
          "required": false,
          "defaultValue": "BASIC"
        },
        {
          "name": "roles",
          "type": "string(SUPER_ADMIN|ADMIN|EDITOR|VIEWER)[]",
          "required": false,
          "defaultValue": "SUPER_ADMIN,ADMIN,EDITOR,VIEWER"
        },
        {
          "name": "platforms",
          "type": "string(WEB|MOBILE|DESKTOP)[]",
          "required": false,
          "defaultValue": "WEB"
        },
        {
          "name": "metadata",
          "type": "object",
          "required": false
        }
      ]
    },
    {
      "id": "updateFeature",
      "method": "PUT",
      "endpoint": "/admin/features/{featureKey}",
      "path": "/api/v1/admin/features/{featureKey}",
      "summary": "Update an existing feature definition",
      "description": "Why this endpoint is needed:\nFeature metadata, access rules, and rollout percentages need to be adjustable as product strategy evolves.\n",
      "tags": [
        "Feature Management"
      ],
      "pathParams": [
        {
          "name": "featureKey",
          "type": "string",
          "required": true
        }
      ],
      "queryParams": [],
      "headers": [],
      "requestBody": [
        {
          "name": "name",
          "type": "string",
          "required": false
        },
        {
          "name": "description",
          "type": "string",
          "required": false
        },
        {
          "name": "enabled",
          "type": "boolean",
          "required": false
        },
        {
          "name": "rolloutPercentage",
          "type": "integer",
          "required": false
        },
        {
          "name": "minPlan",
          "type": "string(BASIC|PRO|ENTERPRISE)",
          "required": false
        },
        {
          "name": "roles",
          "type": "string(SUPER_ADMIN|ADMIN|EDITOR|VIEWER)[]",
          "required": false
        },
        {
          "name": "platforms",
          "type": "string(WEB|MOBILE|DESKTOP)[]",
          "required": false
        },
        {
          "name": "metadata",
          "type": "object",
          "required": false
        }
      ]
    },
    {
      "id": "deleteFeature",
      "method": "DELETE",
      "endpoint": "/admin/features/{featureKey}",
      "path": "/api/v1/admin/features/{featureKey}",
      "summary": "Delete a feature definition",
      "description": "Why this endpoint is needed:\nRetired features need to be removed from the registry to keep the catalog clean.\n",
      "tags": [
        "Feature Management"
      ],
      "pathParams": [
        {
          "name": "featureKey",
          "type": "string",
          "required": true
        }
      ],
      "queryParams": [],
      "headers": [],
      "requestBody": []
    },
    {
      "id": "getFeatureAnalytics",
      "method": "GET",
      "endpoint": "/admin/features/{featureKey}/analytics",
      "path": "/api/v1/admin/features/{featureKey}/analytics",
      "summary": "Get usage analytics for a feature",
      "description": "Why this endpoint is needed:\nProduct teams need to measure feature adoption, completion rates, and user engagement.\n",
      "tags": [
        "Feature Management"
      ],
      "pathParams": [
        {
          "name": "featureKey",
          "type": "string",
          "required": true
        }
      ],
      "queryParams": [],
      "headers": [],
      "requestBody": []
    },
    {
      "id": "configureFeatureForTenant",
      "method": "POST",
      "endpoint": "/admin/features/{featureKey}/tenants/{tenantId}/configure",
      "path": "/api/v1/admin/features/{featureKey}/tenants/{tenantId}/configure",
      "summary": "Configure feature for a specific tenant",
      "description": "Why this endpoint is needed:\nEnterprise tenants may need to enable/disable features or customize behavior for their user base.\n",
      "tags": [
        "Feature Management"
      ],
      "pathParams": [
        {
          "name": "featureKey",
          "type": "string",
          "required": true
        },
        {
          "name": "tenantId",
          "type": "string",
          "required": true
        }
      ],
      "queryParams": [],
      "headers": [],
      "requestBody": [
        {
          "name": "enabled",
          "type": "boolean",
          "required": false
        },
        {
          "name": "rolloutPercentage",
          "type": "integer",
          "required": false
        },
        {
          "name": "customMetadata",
          "type": "object",
          "required": false
        }
      ]
    },
    {
      "id": "listPlans",
      "method": "GET",
      "endpoint": "/admin/plans",
      "path": "/api/v1/admin/plans",
      "summary": "List active plan definitions",
      "description": "Why this endpoint is needed:\nTenants and platform operators both need a canonical list of active plans with limits and pricing attributes.\n\nWhat this endpoint does:\nIt returns all active plan definitions including quota constraints such as maximum campaigns,\nmax responses per campaign, and max admin users.\n\nHow this endpoint does it:\nThe plan catalog service reads persistent plan definition rows, filters active plans, maps entities to API DTOs,\nand returns a list suitable for onboarding forms and pricing pages.\n",
      "tags": [
        "Plan Catalog"
      ],
      "pathParams": [],
      "queryParams": [],
      "headers": [],
      "requestBody": []
    },
    {
      "id": "upsertPlan",
      "method": "PUT",
      "endpoint": "/admin/plans",
      "path": "/api/v1/admin/plans",
      "summary": "Create or update a plan definition (SUPER_ADMIN)",
      "description": "Why this endpoint is needed:\nPlan governance must evolve over time as business strategy changes. A controlled mutation endpoint is needed\nfor super administrators to adjust commercial limits safely.\n\nWhat this endpoint does:\nIt inserts or updates a plan definition, including display metadata, billing-cycle metadata, and quota parameters.\n\nHow this endpoint does it:\nThe endpoint is protected by super-admin role checks, validates request constraints,\npersists the plan catalog mutation, and returns the resulting plan definition used by runtime quota enforcement.\n",
      "tags": [
        "Plan Catalog"
      ],
      "pathParams": [],
      "queryParams": [],
      "headers": [],
      "requestBody": [
        {
          "name": "planCode",
          "type": "string(BASIC|PRO|ENTERPRISE)",
          "required": true
        },
        {
          "name": "displayName",
          "type": "string",
          "required": true
        },
        {
          "name": "price",
          "type": "number",
          "required": true
        },
        {
          "name": "currency",
          "type": "string",
          "required": true
        },
        {
          "name": "billingCycleDays",
          "type": "integer",
          "required": true
        },
        {
          "name": "trialDays",
          "type": "integer",
          "required": true
        },
        {
          "name": "maxCampaigns",
          "type": "integer",
          "required": false
        },
        {
          "name": "maxResponsesPerCampaign",
          "type": "integer",
          "required": false
        },
        {
          "name": "maxAdminUsers",
          "type": "integer",
          "required": false
        },
        {
          "name": "active",
          "type": "boolean",
          "required": false,
          "defaultValue": "true"
        }
      ]
    },
    {
      "id": "checkoutSubscription",
      "method": "POST",
      "endpoint": "/admin/subscriptions/checkout",
      "path": "/api/v1/admin/subscriptions/checkout",
      "summary": "Start or change tenant subscription plan",
      "description": "Why this endpoint is needed:\nSaaS tenants need a direct API path to activate or upgrade plans without out-of-band support workflows.\n\nWhat this endpoint does:\nIt takes the requested plan, executes the checkout flow (mock success in MVP), updates subscription status,\nrecords payment transaction metadata, and returns updated subscription state.\n\nHow this endpoint does it:\nThe service uses authenticated tenant context, validates requested plan existence, executes payment gateway abstraction,\npersists subscription changes and payment reference details, and returns normalized quota-aware response fields.\n",
      "tags": [
        "Subscription"
      ],
      "pathParams": [],
      "queryParams": [],
      "headers": [],
      "requestBody": [
        {
          "name": "plan",
          "type": "string(BASIC|PRO|ENTERPRISE)",
          "required": true
        }
      ]
    },
    {
      "id": "getMySubscription",
      "method": "GET",
      "endpoint": "/admin/subscriptions/me",
      "path": "/api/v1/admin/subscriptions/me",
      "summary": "Get current tenant subscription details",
      "description": "Why this endpoint is needed:\nTenant operators must understand their active entitlement before launching campaigns or scaling usage.\n\nWhat this endpoint does:\nIt returns the authenticated tenant's current subscription record, including plan, status, billing period,\nquota limits, and latest payment reference.\n\nHow this endpoint does it:\nTenant context is extracted from admin JWT claims, then subscription and plan metadata are joined and mapped into\na SubscriptionResponse object for a single, dashboard-friendly read.\n",
      "tags": [
        "Subscription"
      ],
      "pathParams": [],
      "queryParams": [],
      "headers": [],
      "requestBody": []
    },
    {
      "id": "getPlatformAuditLogs",
      "method": "GET",
      "endpoint": "/admin/superadmin/audit-logs",
      "path": "/api/v1/admin/superadmin/audit-logs",
      "summary": "Query platform-wide audit logs (SUPER_ADMIN)",
      "description": "Why this endpoint is needed:\nSuper-admin operations require cross-tenant traceability for investigations and compliance.\n\nWhat this endpoint does:\nIt returns paged audit logs across all tenants with optional filters for actor/action/entity/tenant.\n\nHow this endpoint does it:\nController applies optional filter predicates and queries audit repository with descending timestamp ordering.\n",
      "tags": [
        "Audit Logs"
      ],
      "pathParams": [],
      "queryParams": [
        {
          "name": "page",
          "type": "integer",
          "required": false,
          "defaultValue": "0"
        },
        {
          "name": "size",
          "type": "integer",
          "required": false,
          "defaultValue": "20"
        },
        {
          "name": "action",
          "type": "string",
          "required": false
        },
        {
          "name": "entityType",
          "type": "string",
          "required": false
        },
        {
          "name": "tenantId",
          "type": "string",
          "required": false
        },
        {
          "name": "actor",
          "type": "string",
          "required": false
        },
        {
          "name": "from",
          "type": "string",
          "required": false
        },
        {
          "name": "to",
          "type": "string",
          "required": false
        }
      ],
      "headers": [],
      "requestBody": []
    },
    {
      "id": "getSuperAdminMetrics",
      "method": "GET",
      "endpoint": "/admin/superadmin/metrics",
      "path": "/api/v1/admin/superadmin/metrics",
      "summary": "Retrieve platform-level metrics snapshot",
      "description": "Why this endpoint is needed:\nPlatform operators require fast, aggregate visibility into tenant and subscription health.\n\nWhat this endpoint does:\nIt returns key platform counters such as total tenants, active subscriptions, and aggregate usage.\n\nHow this endpoint does it:\nSuper-admin service aggregates metrics from tenant/subscription/usage stores and maps them to response DTO.\n",
      "tags": [
        "Super Admin"
      ],
      "pathParams": [],
      "queryParams": [],
      "headers": [],
      "requestBody": []
    },
    {
      "id": "listTenantsForSuperAdmin",
      "method": "GET",
      "endpoint": "/admin/superadmin/tenants",
      "path": "/api/v1/admin/superadmin/tenants",
      "summary": "List tenants with subscription and status overview",
      "description": "Why this endpoint is needed:\nSuper-admin workflows require tenant inventory with billing and active-state context for support and governance.\n\nWhat this endpoint does:\nIt returns paged tenant overview data including subscription plan and activity state.\n\nHow this endpoint does it:\nTenant-admin service queries tenant/subscription aggregates and returns Spring Data paged response structure.\n",
      "tags": [
        "Super Admin"
      ],
      "pathParams": [],
      "queryParams": [
        {
          "name": "page",
          "type": "integer",
          "required": false,
          "defaultValue": "0"
        },
        {
          "name": "size",
          "type": "integer",
          "required": false,
          "defaultValue": "20"
        },
        {
          "name": "sort",
          "type": "string",
          "required": false
        }
      ],
      "headers": [],
      "requestBody": []
    },
    {
      "id": "activateTenant",
      "method": "PUT",
      "endpoint": "/admin/superadmin/tenants/{tenantId}/activate",
      "path": "/api/v1/admin/superadmin/tenants/{tenantId}/activate",
      "summary": "Activate a tenant",
      "tags": [
        "Super Admin"
      ],
      "pathParams": [
        {
          "name": "tenantId",
          "type": "string",
          "required": true
        }
      ],
      "queryParams": [],
      "headers": [],
      "requestBody": []
    },
    {
      "id": "impersonateTenant",
      "method": "POST",
      "endpoint": "/admin/superadmin/tenants/{tenantId}/impersonate",
      "path": "/api/v1/admin/superadmin/tenants/{tenantId}/impersonate",
      "summary": "Impersonate a tenant admin context",
      "tags": [
        "Super Admin"
      ],
      "pathParams": [
        {
          "name": "tenantId",
          "type": "string",
          "required": true
        }
      ],
      "queryParams": [],
      "headers": [],
      "requestBody": []
    },
    {
      "id": "overrideTenantSubscription",
      "method": "POST",
      "endpoint": "/admin/superadmin/tenants/{tenantId}/subscriptions/override",
      "path": "/api/v1/admin/superadmin/tenants/{tenantId}/subscriptions/override",
      "summary": "Override tenant subscription plan as super admin",
      "tags": [
        "Super Admin"
      ],
      "pathParams": [
        {
          "name": "tenantId",
          "type": "string",
          "required": true
        }
      ],
      "queryParams": [],
      "headers": [],
      "requestBody": [
        {
          "name": "plan",
          "type": "string(BASIC|PRO|ENTERPRISE)",
          "required": true
        }
      ]
    },
    {
      "id": "suspendTenant",
      "method": "PUT",
      "endpoint": "/admin/superadmin/tenants/{tenantId}/suspend",
      "path": "/api/v1/admin/superadmin/tenants/{tenantId}/suspend",
      "summary": "Suspend a tenant",
      "tags": [
        "Super Admin"
      ],
      "pathParams": [
        {
          "name": "tenantId",
          "type": "string",
          "required": true
        }
      ],
      "queryParams": [],
      "headers": [],
      "requestBody": []
    },
    {
      "id": "getTenantAdminMetrics",
      "method": "GET",
      "endpoint": "/admin/superadmin/tenants/metrics",
      "path": "/api/v1/admin/superadmin/tenants/metrics",
      "summary": "Retrieve platform metrics via tenant-admin namespace",
      "description": "Why this endpoint is needed:\nSome super-admin UI bundles consume tenant-admin namespace for platform metrics.\n\nWhat this endpoint does:\nIt returns the same platform metrics payload as `/api/v1/admin/superadmin/metrics`.\n\nHow this endpoint does it:\nController delegates to the same metrics service used by primary super-admin metrics endpoint.\n",
      "tags": [
        "Super Admin"
      ],
      "pathParams": [],
      "queryParams": [],
      "headers": [],
      "requestBody": []
    },
    {
      "id": "compareSegments",
      "method": "POST",
      "endpoint": "/analytics/campaigns/{campaignId}/compare",
      "path": "/api/v1/analytics/campaigns/{campaignId}/compare",
      "summary": "Compare multiple analytics segments",
      "description": "Why this endpoint is needed:\nPlatform users need side-by-side comparative analytics of demographic segments.\n\nWhat this endpoint does:\nMultiplexes the /full-report generation across up to 5 concurrent demographic segments.\n",
      "tags": [
        "Responses"
      ],
      "pathParams": [
        {
          "name": "campaignId",
          "type": "string",
          "required": true
        }
      ],
      "queryParams": [],
      "headers": [],
      "requestBody": []
    },
    {
      "id": "getFullReport",
      "method": "GET",
      "endpoint": "/analytics/campaigns/{campaignId}/full-report",
      "path": "/api/v1/analytics/campaigns/{campaignId}/full-report",
      "summary": "Get full analytics report with metadata filters",
      "description": "Why this endpoint is needed:\nThe dashboard needs an aggregated snapshot of campaign performance, score curves, and individual question breakdowns.\n\nWhat this endpoint does:\nReturns all analytics metrics for a single segment (or the entire campaign).\n",
      "tags": [
        "Responses"
      ],
      "pathParams": [
        {
          "name": "campaignId",
          "type": "string",
          "required": true
        }
      ],
      "queryParams": [],
      "headers": [],
      "requestBody": []
    },
    {
      "id": "getTenantAuditLogs",
      "method": "GET",
      "endpoint": "/audit-logs",
      "path": "/api/v1/audit-logs",
      "summary": "Query tenant-scoped activity logs",
      "description": "Why this endpoint is needed:\nTenant operators need visibility into operational activity for troubleshooting and governance.\n\nWhat this endpoint does:\nIt returns paged audit logs filtered to the current tenant context.\n\nHow this endpoint does it:\nController derives tenant id from security context and applies optional filter parameters on audit-log repository.\n",
      "tags": [
        "Audit Logs"
      ],
      "pathParams": [],
      "queryParams": [
        {
          "name": "page",
          "type": "integer",
          "required": false,
          "defaultValue": "0"
        },
        {
          "name": "size",
          "type": "integer",
          "required": false,
          "defaultValue": "20"
        },
        {
          "name": "action",
          "type": "string",
          "required": false
        },
        {
          "name": "entityType",
          "type": "string",
          "required": false
        },
        {
          "name": "from",
          "type": "string",
          "required": false
        },
        {
          "name": "to",
          "type": "string",
          "required": false
        }
      ],
      "headers": [],
      "requestBody": []
    },
    {
      "id": "createAuthProfile",
      "method": "POST",
      "endpoint": "/auth/profiles",
      "path": "/api/v1/auth/profiles",
      "summary": "Create tenant responder-auth profile",
      "description": "Why this endpoint is needed:\nPrivate campaign authentication must be configured once per tenant so every campaign can reuse a consistent trust policy.\n\nWhat this endpoint does:\nIt creates a tenant auth profile containing auth mode, trust metadata, claim mappings, fallback policy,\nand OIDC/signing settings.\n\nHow this endpoint does it:\nThe service validates tenant scope, enforces mapping rules (including required respondentId mapping),\napplies defaults (such as OIDC scope defaults), persists profile + mappings, and writes audit events.\n",
      "tags": [
        "Auth Profiles"
      ],
      "pathParams": [],
      "queryParams": [],
      "headers": [],
      "requestBody": [
        {
          "name": "tenantId",
          "type": "string",
          "required": true
        },
        {
          "name": "authMode",
          "type": "string(PUBLIC_ANONYMOUS|SIGNED_LAUNCH_TOKEN|EXTERNAL_SSO_TRUST)",
          "required": true
        },
        {
          "name": "issuer",
          "type": "string",
          "required": false
        },
        {
          "name": "audience",
          "type": "string",
          "required": false
        },
        {
          "name": "jwksEndpoint",
          "type": "string",
          "required": false
        },
        {
          "name": "oidcDiscoveryUrl",
          "type": "string",
          "required": false
        },
        {
          "name": "oidcClientId",
          "type": "string",
          "required": false
        },
        {
          "name": "oidcClientSecret",
          "type": "string",
          "required": false
        },
        {
          "name": "oidcRedirectUri",
          "type": "string",
          "required": false
        },
        {
          "name": "oidcScopes",
          "type": "string",
          "required": false
        },
        {
          "name": "clockSkewSeconds",
          "type": "integer",
          "required": false
        },
        {
          "name": "tokenTtlSeconds",
          "type": "integer",
          "required": false
        },
        {
          "name": "signingSecret",
          "type": "string",
          "required": false
        },
        {
          "name": "fallbackPolicy",
          "type": "string(SSO_REQUIRED|ANONYMOUS_FALLBACK|DISABLE_ON_FAILURE)",
          "required": false
        },
        {
          "name": "claimMappings",
          "type": "object[]",
          "required": false
        }
      ]
    },
    {
      "id": "updateAuthProfile",
      "method": "PUT",
      "endpoint": "/auth/profiles/{id}",
      "path": "/api/v1/auth/profiles/{id}",
      "summary": "Update tenant responder-auth profile",
      "description": "Why this endpoint is needed:\nIdentity provider details and claim models change over time; profile updates must be supported without rebuilding tenants.\n\nWhat this endpoint does:\nIt updates auth profile trust settings, claim mappings, scope behavior, and fallback policies.\n\nHow this endpoint does it:\nThe service validates tenant access and mapping constraints, applies secure defaults where required,\npersists profile changes, and records before/after snapshots in auth audit.\n",
      "tags": [
        "Auth Profiles"
      ],
      "pathParams": [
        {
          "name": "id",
          "type": "string",
          "required": true,
          "description": "Path parameter"
        }
      ],
      "queryParams": [],
      "headers": [],
      "requestBody": [
        {
          "name": "tenantId",
          "type": "string",
          "required": true
        },
        {
          "name": "authMode",
          "type": "string(PUBLIC_ANONYMOUS|SIGNED_LAUNCH_TOKEN|EXTERNAL_SSO_TRUST)",
          "required": true
        },
        {
          "name": "issuer",
          "type": "string",
          "required": false
        },
        {
          "name": "audience",
          "type": "string",
          "required": false
        },
        {
          "name": "jwksEndpoint",
          "type": "string",
          "required": false
        },
        {
          "name": "oidcDiscoveryUrl",
          "type": "string",
          "required": false
        },
        {
          "name": "oidcClientId",
          "type": "string",
          "required": false
        },
        {
          "name": "oidcClientSecret",
          "type": "string",
          "required": false
        },
        {
          "name": "oidcRedirectUri",
          "type": "string",
          "required": false
        },
        {
          "name": "oidcScopes",
          "type": "string",
          "required": false
        },
        {
          "name": "clockSkewSeconds",
          "type": "integer",
          "required": false
        },
        {
          "name": "tokenTtlSeconds",
          "type": "integer",
          "required": false
        },
        {
          "name": "signingSecret",
          "type": "string",
          "required": false
        },
        {
          "name": "fallbackPolicy",
          "type": "string(SSO_REQUIRED|ANONYMOUS_FALLBACK|DISABLE_ON_FAILURE)",
          "required": false
        },
        {
          "name": "claimMappings",
          "type": "object[]",
          "required": false
        }
      ]
    },
    {
      "id": "getAuthProfileAudit",
      "method": "GET",
      "endpoint": "/auth/profiles/{id}/audit",
      "path": "/api/v1/auth/profiles/{id}/audit",
      "summary": "Get auth profile audit history",
      "description": "Why this endpoint is needed:\nSecurity-sensitive configuration changes require traceability for compliance and troubleshooting.\n\nWhat this endpoint does:\nIt returns ordered audit entries for an auth profile including actor and before/after values.\n\nHow this endpoint does it:\nThe repository queries audit rows by auth_profile_id in descending timestamp order and returns serialized audit objects.\n",
      "tags": [
        "Auth Profiles"
      ],
      "pathParams": [
        {
          "name": "id",
          "type": "string",
          "required": true,
          "description": "Path parameter"
        }
      ],
      "queryParams": [],
      "headers": [],
      "requestBody": []
    },
    {
      "id": "rotateAuthProfileKey",
      "method": "POST",
      "endpoint": "/auth/profiles/{id}/rotate-key",
      "path": "/api/v1/auth/profiles/{id}/rotate-key",
      "summary": "Rotate auth profile key version",
      "description": "Why this endpoint is needed:\nSigning key version rotation is required for security hygiene and incident response readiness.\n\nWhat this endpoint does:\nIt increments the active key version for a tenant auth profile.\n\nHow this endpoint does it:\nThe service updates active key version, persists change, and creates an audit record describing the rotation action.\n",
      "tags": [
        "Auth Profiles"
      ],
      "pathParams": [
        {
          "name": "id",
          "type": "string",
          "required": true,
          "description": "Path parameter"
        }
      ],
      "queryParams": [],
      "headers": [],
      "requestBody": []
    },
    {
      "id": "getAuthProfileByTenant",
      "method": "GET",
      "endpoint": "/auth/profiles/tenant/{tenantId}",
      "path": "/api/v1/auth/profiles/tenant/{tenantId}",
      "summary": "Get auth profile for a tenant",
      "description": "Why this endpoint is needed:\nOperators need an easy way to verify effective auth configuration before launching or troubleshooting private campaigns.\n\nWhat this endpoint does:\nIt returns the tenant's auth profile and claim mapping configuration.\n\nHow this endpoint does it:\nTenant access checks are performed, then profile data is loaded and mapped to response DTO.\n",
      "tags": [
        "Auth Profiles"
      ],
      "pathParams": [
        {
          "name": "tenantId",
          "type": "string",
          "required": true
        }
      ],
      "queryParams": [],
      "headers": [],
      "requestBody": []
    },
    {
      "id": "listProviderTemplates",
      "method": "GET",
      "endpoint": "/auth/providers/templates",
      "path": "/api/v1/auth/providers/templates",
      "summary": "List built-in provider setup templates",
      "description": "Why this endpoint is needed:\nTenant onboarding is faster and less error-prone when common IdP providers have pre-defined setup templates.\n\nWhat this endpoint does:\nIt returns template metadata for supported providers, including default scopes, required config fields,\nand default claim mapping suggestions.\n\nHow this endpoint does it:\nA template service returns static curated provider definitions consumed by UI and SDK onboarding flows.\n",
      "tags": [
        "Auth Profiles"
      ],
      "pathParams": [],
      "queryParams": [],
      "headers": [],
      "requestBody": []
    },
    {
      "id": "getProviderTemplate",
      "method": "GET",
      "endpoint": "/auth/providers/templates/{providerCode}",
      "path": "/api/v1/auth/providers/templates/{providerCode}",
      "summary": "Get one provider setup template",
      "description": "Why this endpoint is needed:\nSetup forms often require provider-specific field guidance and defaults rather than a full template catalog.\n\nWhat this endpoint does:\nIt returns one template by provider code (for example OKTA, AUTH0, AZURE_AD, KEYCLOAK).\n\nHow this endpoint does it:\nThe service validates provider code input, resolves the template from in-memory catalog, and returns structured metadata.\n",
      "tags": [
        "Auth Profiles"
      ],
      "pathParams": [
        {
          "name": "providerCode",
          "type": "string",
          "required": true
        }
      ],
      "queryParams": [],
      "headers": [],
      "requestBody": []
    },
    {
      "id": "completeRespondentOidc",
      "method": "GET",
      "endpoint": "/auth/respondent/oidc/callback",
      "path": "/api/v1/auth/respondent/oidc/callback",
      "summary": "Complete OIDC callback and issue one-time responder access code",
      "description": "Why this endpoint is needed:\nAuthorization-code OIDC requires callback handling to exchange code, verify trust, and convert external auth into\ninternal responder access state.\n\nWhat this endpoint does:\nIt validates callback state/code, exchanges token at IdP, validates token claims, and issues one-time responder access code.\n\nHow this endpoint does it:\nOIDC state record is checked for expiry and one-time use, token exchange is executed via configured metadata,\ntoken validation service verifies claims/mappings, responder access code row is persisted, and either JSON response\nor redirect response is returned.\n",
      "tags": [
        "OIDC Respondent Flow"
      ],
      "pathParams": [],
      "queryParams": [
        {
          "name": "state",
          "type": "string",
          "required": true
        },
        {
          "name": "code",
          "type": "string",
          "required": true
        }
      ],
      "headers": [],
      "requestBody": []
    },
    {
      "id": "startRespondentOidc",
      "method": "POST",
      "endpoint": "/auth/respondent/oidc/start",
      "path": "/api/v1/auth/respondent/oidc/start",
      "summary": "Start private responder OIDC flow",
      "description": "Why this endpoint is needed:\nPrivate campaigns need a secure and tenant-aware way to initiate responder SSO without exposing platform-internal logic.\n\nWhat this endpoint does:\nIt validates campaign + tenant eligibility for OIDC flow, creates one-time state/nonce context,\nand returns IdP authorization URL details.\n\nHow this endpoint does it:\nThe OIDC service checks campaign auth mode and tenant auth profile mode, fetches OIDC metadata,\nstores state with expiry, composes authorization URL with scopes/state/nonce, and returns OidcStartResponse.\n",
      "tags": [
        "OIDC Respondent Flow"
      ],
      "pathParams": [],
      "queryParams": [],
      "headers": [],
      "requestBody": [
        {
          "name": "tenantId",
          "type": "string",
          "required": true
        },
        {
          "name": "campaignId",
          "type": "string",
          "required": true
        },
        {
          "name": "returnPath",
          "type": "string",
          "required": false
        }
      ]
    },
    {
      "id": "validateResponderToken",
      "method": "POST",
      "endpoint": "/auth/validate/{tenantId}",
      "path": "/api/v1/auth/validate/{tenantId}",
      "summary": "Validate responder token against tenant auth policy",
      "description": "Why this endpoint is needed:\nIntegrations and runtime checks require an explicit token-validation endpoint that applies tenant-specific trust policy.\n\nWhat this endpoint does:\nIt validates responder token input according to configured auth mode and claim-mapping rules,\nthen returns standardized success/failure payload.\n\nHow this endpoint does it:\nService loads tenant auth profile, routes validation by mode (public/signed/OIDC external trust),\napplies cryptographic and claim checks, and returns TokenValidationResult.\n",
      "tags": [
        "Auth Profiles"
      ],
      "pathParams": [
        {
          "name": "tenantId",
          "type": "string",
          "required": true
        }
      ],
      "queryParams": [],
      "headers": [],
      "requestBody": [
        {
          "name": "payload",
          "type": "string",
          "required": true
        }
      ]
    },
    {
      "id": "listCampaigns",
      "method": "GET",
      "endpoint": "/campaigns",
      "path": "/api/v1/campaigns",
      "summary": "List active campaigns",
      "description": "Why this endpoint is needed:\nCampaign operations teams need a dashboard view of active and manageable campaigns.\n\nWhat this endpoint does:\nIt returns campaigns under authenticated tenant scope.\n\nHow this endpoint does it:\nCampaign service executes tenant-scoped retrieval and response mapping.\n",
      "tags": [
        "Campaigns"
      ],
      "pathParams": [],
      "queryParams": [],
      "headers": [],
      "requestBody": []
    },
    {
      "id": "createCampaign",
      "method": "POST",
      "endpoint": "/campaigns",
      "path": "/api/v1/campaigns",
      "summary": "Create a campaign from a survey",
      "description": "Why this endpoint is needed:\nCampaigns are the executable delivery unit for collecting responses from a specific survey snapshot and runtime policy set.\n\nWhat this endpoint does:\nIt creates a tenant-scoped campaign with name, survey binding, description, and access mode (PUBLIC/PRIVATE).\n\nHow this endpoint does it:\nThe service validates survey reference and tenant scope, normalizes deprecated auth mode values, persists campaign state,\nand returns CampaignResponse.\n",
      "tags": [
        "Campaigns"
      ],
      "pathParams": [],
      "queryParams": [],
      "headers": [],
      "requestBody": [
        {
          "name": "name",
          "type": "string",
          "required": true
        },
        {
          "name": "description",
          "type": "string",
          "required": false
        },
        {
          "name": "surveyId",
          "type": "string",
          "required": true
        },
        {
          "name": "authMode",
          "type": "string(PUBLIC|PRIVATE|SIGNED_TOKEN|SSO)",
          "required": false
        }
      ]
    },
    {
      "id": "getCampaign",
      "method": "GET",
      "endpoint": "/campaigns/{id}",
      "path": "/api/v1/campaigns/{id}",
      "summary": "Get campaign by id",
      "description": "Why this endpoint is needed:\nCampaign detail retrieval is required for settings updates, monitoring, and support troubleshooting.\n\nWhat this endpoint does:\nIt returns one campaign record with current status and metadata.\n\nHow this endpoint does it:\nThe service resolves campaign by tenant and id and returns mapped DTO fields.\n",
      "tags": [
        "Campaigns"
      ],
      "pathParams": [
        {
          "name": "id",
          "type": "string",
          "required": true,
          "description": "Path parameter"
        }
      ],
      "queryParams": [],
      "headers": [],
      "requestBody": []
    },
    {
      "id": "updateCampaign",
      "method": "PUT",
      "endpoint": "/campaigns/{id}",
      "path": "/api/v1/campaigns/{id}",
      "summary": "Update campaign metadata",
      "description": "Why this endpoint is needed:\nCampaigns require controlled edits to operational metadata and associations.\n\nWhat this endpoint does:\nIt updates campaign request fields while preserving lifecycle constraints.\n\nHow this endpoint does it:\nService validates ownership and allowed update semantics, then persists and returns updated campaign data.\n",
      "tags": [
        "Campaigns"
      ],
      "pathParams": [
        {
          "name": "id",
          "type": "string",
          "required": true,
          "description": "Path parameter"
        }
      ],
      "queryParams": [],
      "headers": [],
      "requestBody": [
        {
          "name": "name",
          "type": "string",
          "required": true
        },
        {
          "name": "description",
          "type": "string",
          "required": false
        },
        {
          "name": "surveyId",
          "type": "string",
          "required": true
        },
        {
          "name": "authMode",
          "type": "string(PUBLIC|PRIVATE|SIGNED_TOKEN|SSO)",
          "required": false
        }
      ]
    },
    {
      "id": "deactivateCampaign",
      "method": "DELETE",
      "endpoint": "/campaigns/{id}",
      "path": "/api/v1/campaigns/{id}",
      "summary": "Deactivate campaign",
      "description": "Why this endpoint is needed:\nRetiring campaign availability should not erase historical response evidence.\n\nWhat this endpoint does:\nIt marks campaign inactive.\n\nHow this endpoint does it:\nTenant-scoped campaign lookup is followed by status/active-state mutation and no-content completion.\n",
      "tags": [
        "Campaigns"
      ],
      "pathParams": [
        {
          "name": "id",
          "type": "string",
          "required": true,
          "description": "Path parameter"
        }
      ],
      "queryParams": [],
      "headers": [],
      "requestBody": []
    },
    {
      "id": "activateCampaign",
      "method": "POST",
      "endpoint": "/campaigns/{id}/activate",
      "path": "/api/v1/campaigns/{id}/activate",
      "summary": "Activate campaign to accept responses",
      "description": "Why this endpoint is needed:\nActivation is a controlled launch gate. It prevents collecting responses from improperly prepared campaigns.\n\nWhat this endpoint does:\nIt transitions a campaign into active state when preconditions pass, links latest survey snapshot,\nand upserts campaign default weight profile from pinned category weights.\n\nHow this endpoint does it:\nThe service verifies linked survey lifecycle requirements (must be PUBLISHED), loads latest snapshot,\nupserts default weight profile, updates campaign status to ACTIVE, and returns campaign response.\n",
      "tags": [
        "Campaigns"
      ],
      "pathParams": [
        {
          "name": "id",
          "type": "string",
          "required": true,
          "description": "Path parameter"
        }
      ],
      "queryParams": [],
      "headers": [],
      "requestBody": []
    },
    {
      "id": "listCampaignChannels",
      "method": "GET",
      "endpoint": "/campaigns/{id}/channels",
      "path": "/api/v1/campaigns/{id}/channels",
      "summary": "List generated channels for a campaign",
      "description": "Why this endpoint is needed:\nTeams often need to re-fetch channel assets after initial generation for reminders and omnichannel delivery.\n\nWhat this endpoint does:\nIt returns all persisted distribution channels for the campaign.\n\nHow this endpoint does it:\nDistribution service reads campaign channel rows under tenant scope and maps to response DTOs.\n",
      "tags": [
        "Campaigns"
      ],
      "pathParams": [
        {
          "name": "id",
          "type": "string",
          "required": true,
          "description": "Path parameter"
        }
      ],
      "queryParams": [],
      "headers": [],
      "requestBody": []
    },
    {
      "id": "generateCampaignChannels",
      "method": "POST",
      "endpoint": "/campaigns/{id}/distribute",
      "path": "/api/v1/campaigns/{id}/distribute",
      "summary": "Generate distribution channels for a campaign",
      "description": "Why this endpoint is needed:\nOperations teams need standardized distribution artifacts (links and embed formats) to launch outreach quickly.\n\nWhat this endpoint does:\nIt generates and stores channel records such as public/private links and embed snippets.\n\nHow this endpoint does it:\nDistribution service builds channel values from campaign context, persists channel rows, and returns generated list.\n",
      "tags": [
        "Campaigns"
      ],
      "pathParams": [
        {
          "name": "id",
          "type": "string",
          "required": true,
          "description": "Path parameter"
        }
      ],
      "queryParams": [],
      "headers": [],
      "requestBody": []
    },
    {
      "id": "getCampaignPreview",
      "method": "GET",
      "endpoint": "/campaigns/{id}/preview",
      "path": "/api/v1/campaigns/{id}/preview",
      "summary": "Get admin preview payload for a campaign",
      "description": "Why this endpoint is needed:\nCampaign owners need a pre-launch visual/data validation endpoint to verify header/footer/questions and responder UX flags.\n\nWhat this endpoint does:\nIt assembles campaign, survey, and settings into a responder-facing preview model for admin users.\n\nHow this endpoint does it:\nCampaign service resolves campaign/settings/survey in tenant scope, maps each page/question with pinned version context,\nand returns CampaignPreviewResponse.\n",
      "tags": [
        "Campaigns"
      ],
      "pathParams": [
        {
          "name": "id",
          "type": "string",
          "required": true,
          "description": "Path parameter"
        }
      ],
      "queryParams": [],
      "headers": [],
      "requestBody": []
    },
    {
      "id": "getCampaignSettings",
      "method": "GET",
      "endpoint": "/campaigns/{id}/settings",
      "path": "/api/v1/campaigns/{id}/settings",
      "summary": "Get campaign runtime settings",
      "description": "Why this endpoint is needed:\nOperations and UI flows require a read endpoint for persisted runtime controls before edit/save cycles.\n\nWhat this endpoint does:\nIt returns the current campaign settings object.\n\nHow this endpoint does it:\nCampaign service validates tenant-scoped campaign visibility and returns mapped CampaignSettingsResponse.\n",
      "tags": [
        "Campaigns"
      ],
      "pathParams": [
        {
          "name": "id",
          "type": "string",
          "required": true,
          "description": "Path parameter"
        }
      ],
      "queryParams": [],
      "headers": [],
      "requestBody": []
    },
    {
      "id": "updateCampaignSettings",
      "method": "PUT",
      "endpoint": "/campaigns/{id}/settings",
      "path": "/api/v1/campaigns/{id}/settings",
      "summary": "Update campaign runtime settings",
      "description": "Why this endpoint is needed:\nCampaign runtime behavior (quota, timeout, restrictions, UI toggles, metadata capture) must be adjustable\nindependently from core campaign identity.\n\nWhat this endpoint does:\nIt stores runtime policy controls that are enforced when responders submit answers.\nIf closeDate is set to now/past, open responses are auto-locked immediately.\n\nHow this endpoint does it:\nThe service loads target campaign by tenant, merges settings payload, persists state,\nand returns updated campaign context.\n",
      "tags": [
        "Campaigns"
      ],
      "pathParams": [
        {
          "name": "id",
          "type": "string",
          "required": true,
          "description": "Path parameter"
        }
      ],
      "queryParams": [],
      "headers": [],
      "requestBody": [
        {
          "name": "password",
          "type": "string",
          "required": false
        },
        {
          "name": "captchaEnabled",
          "type": "boolean",
          "required": false
        },
        {
          "name": "oneResponsePerDevice",
          "type": "boolean",
          "required": false
        },
        {
          "name": "ipRestrictionEnabled",
          "type": "boolean",
          "required": false
        },
        {
          "name": "emailRestrictionEnabled",
          "type": "boolean",
          "required": false
        },
        {
          "name": "responseQuota",
          "type": "integer",
          "required": false
        },
        {
          "name": "closeDate",
          "type": "string",
          "required": false
        },
        {
          "name": "sessionTimeoutMinutes",
          "type": "integer",
          "required": false
        },
        {
          "name": "showQuestionNumbers",
          "type": "boolean",
          "required": false
        },
        {
          "name": "showProgressIndicator",
          "type": "boolean",
          "required": false
        },
        {
          "name": "allowBackButton",
          "type": "boolean",
          "required": false
        },
        {
          "name": "startMessage",
          "type": "string",
          "required": false
        },
        {
          "name": "finishMessage",
          "type": "string",
          "required": false
        },
        {
          "name": "headerHtml",
          "type": "string",
          "required": false
        },
        {
          "name": "footerHtml",
          "type": "string",
          "required": false
        },
        {
          "name": "collectName",
          "type": "boolean",
          "required": false
        },
        {
          "name": "collectEmail",
          "type": "boolean",
          "required": false
        },
        {
          "name": "collectPhone",
          "type": "boolean",
          "required": false
        },
        {
          "name": "collectAddress",
          "type": "boolean",
          "required": false
        }
      ]
    },
    {
      "id": "listCategories",
      "method": "GET",
      "endpoint": "/categories",
      "path": "/api/v1/categories",
      "summary": "List active categories",
      "description": "Why this endpoint is needed:\nAuthoring screens need category inventory for survey composition and weighting strategy.\n\nWhat this endpoint does:\nIt returns active categories available in the authenticated tenant workspace.\n\nHow this endpoint does it:\nThe category service applies tenant filters and active-state filters, then maps entity results to response DTOs.\n",
      "tags": [
        "Categories"
      ],
      "pathParams": [],
      "queryParams": [],
      "headers": [],
      "requestBody": []
    },
    {
      "id": "createCategory",
      "method": "POST",
      "endpoint": "/categories",
      "path": "/api/v1/categories",
      "summary": "Create a category grouping for questions",
      "description": "Why this endpoint is needed:\nCategories group question assets into business dimensions that support structure, analytics, and scoring design.\n\nWhat this endpoint does:\nIt creates a category with optional question mappings and stores category metadata under tenant ownership.\n\nHow this endpoint does it:\nThe service validates category payloads, checks referenced question constraints where needed,\npersists category + mappings, and returns CategoryResponse with mapping summaries.\n",
      "tags": [
        "Categories"
      ],
      "pathParams": [],
      "queryParams": [],
      "headers": [],
      "requestBody": [
        {
          "name": "name",
          "type": "string",
          "required": true
        },
        {
          "name": "description",
          "type": "string",
          "required": false
        },
        {
          "name": "questionMappings",
          "type": "object[]",
          "required": false
        }
      ]
    },
    {
      "id": "getCategory",
      "method": "GET",
      "endpoint": "/categories/{id}",
      "path": "/api/v1/categories/{id}",
      "summary": "Get one category by id",
      "description": "Why this endpoint is needed:\nCategory detail pages and editor forms need full mapping visibility.\n\nWhat this endpoint does:\nIt returns a single category record including mapped-question metadata.\n\nHow this endpoint does it:\nThe service loads category and mapping state under tenant scope and returns a DTO optimized for UI consumption.\n",
      "tags": [
        "Categories"
      ],
      "pathParams": [
        {
          "name": "id",
          "type": "string",
          "required": true,
          "description": "Path parameter"
        }
      ],
      "queryParams": [],
      "headers": [],
      "requestBody": []
    },
    {
      "id": "updateCategory",
      "method": "PUT",
      "endpoint": "/categories/{id}",
      "path": "/api/v1/categories/{id}",
      "summary": "Update category and mappings",
      "description": "Why this endpoint is needed:\nCategory definitions and ordering can change as survey programs mature.\n\nWhat this endpoint does:\nIt updates category metadata and mapped-question configuration.\n\nHow this endpoint does it:\nValidation and tenant checks run first, then mapping set updates are persisted and reflected in the response DTO.\n",
      "tags": [
        "Categories"
      ],
      "pathParams": [
        {
          "name": "id",
          "type": "string",
          "required": true,
          "description": "Path parameter"
        }
      ],
      "queryParams": [],
      "headers": [],
      "requestBody": [
        {
          "name": "name",
          "type": "string",
          "required": true
        },
        {
          "name": "description",
          "type": "string",
          "required": false
        },
        {
          "name": "questionMappings",
          "type": "object[]",
          "required": false
        }
      ]
    },
    {
      "id": "deactivateCategory",
      "method": "DELETE",
      "endpoint": "/categories/{id}",
      "path": "/api/v1/categories/{id}",
      "summary": "Deactivate a category",
      "description": "Why this endpoint is needed:\nTeams need to retire categories without deleting historical campaign and scoring references.\n\nWhat this endpoint does:\nIt marks the category as inactive for future authoring while retaining historical records.\n\nHow this endpoint does it:\nThe service enforces tenant ownership, flips active status, and returns no content.\n",
      "tags": [
        "Categories"
      ],
      "pathParams": [
        {
          "name": "id",
          "type": "string",
          "required": true,
          "description": "Path parameter"
        }
      ],
      "queryParams": [],
      "headers": [],
      "requestBody": []
    },
    {
      "id": "completeFeature",
      "method": "POST",
      "endpoint": "/features/{featureKey}/complete",
      "path": "/api/v1/features/{featureKey}/complete",
      "summary": "Mark a feature as completed",
      "description": "Why this endpoint is needed:\nUsers need to record completion of tours, tooltips, or feature interactions for progress tracking.\n",
      "tags": [
        "User Features"
      ],
      "pathParams": [
        {
          "name": "featureKey",
          "type": "string",
          "required": true
        }
      ],
      "queryParams": [],
      "headers": [],
      "requestBody": [
        {
          "name": "completed",
          "type": "boolean",
          "required": false,
          "defaultValue": "true"
        }
      ]
    },
    {
      "id": "getFeatureStatus",
      "method": "GET",
      "endpoint": "/features/{featureKey}/status",
      "path": "/api/v1/features/{featureKey}/status",
      "summary": "Get feature status for current user",
      "description": "Why this endpoint is needed:\nFrontend needs to check if a specific feature has been completed by the current user.\n",
      "tags": [
        "User Features"
      ],
      "pathParams": [
        {
          "name": "featureKey",
          "type": "string",
          "required": true
        }
      ],
      "queryParams": [],
      "headers": [],
      "requestBody": []
    },
    {
      "id": "getAvailableFeatures",
      "method": "GET",
      "endpoint": "/features/available",
      "path": "/api/v1/features/available",
      "summary": "Get features available to current user",
      "description": "Why this endpoint is needed:\nFrontend applications need to know which features are available and incomplete for the logged-in user.\n",
      "tags": [
        "User Features"
      ],
      "pathParams": [],
      "queryParams": [
        {
          "name": "category",
          "type": "string(GENERAL|DASHBOARD|SURVEYS|CAMPAIGNS|QUESTIONS|ANALYTICS|RESPONSES|SETTINGS|ADMIN)",
          "required": false
        }
      ],
      "headers": [],
      "requestBody": []
    },
    {
      "id": "getPublicCampaignPreview",
      "method": "GET",
      "endpoint": "/public/campaigns/{id}/preview",
      "path": "/api/v1/public/campaigns/{id}/preview",
      "summary": "Get responder-facing preview payload (public endpoint)",
      "description": "Why this endpoint is needed:\nResponder runtime needs a non-admin endpoint to load campaign form structure for public/private submission flows.\n\nWhat this endpoint does:\nIt returns preview payload for campaigns currently active.\n\nHow this endpoint does it:\nService resolves campaign/settings/survey and returns CampaignPreviewResponse when campaign is active; access\nmode enforcement for private campaigns happens on submit/auth endpoints.\n",
      "tags": [
        "Campaigns"
      ],
      "pathParams": [
        {
          "name": "id",
          "type": "string",
          "required": true,
          "description": "Path parameter"
        }
      ],
      "queryParams": [],
      "headers": [],
      "requestBody": []
    },
    {
      "id": "listQuestions",
      "method": "GET",
      "endpoint": "/questions",
      "path": "/api/v1/questions",
      "summary": "List active questions for current tenant",
      "description": "Why this endpoint is needed:\nAuthoring UIs require fast retrieval of available question inventory during survey design.\n\nWhat this endpoint does:\nIt returns active, tenant-scoped question records currently available for composition.\n\nHow this endpoint does it:\nThe question service uses tenant-aware repository filtering and maps entity rows into response DTOs for list rendering.\n",
      "tags": [
        "Questions"
      ],
      "pathParams": [],
      "queryParams": [],
      "headers": [],
      "requestBody": []
    },
    {
      "id": "createQuestion",
      "method": "POST",
      "endpoint": "/questions",
      "path": "/api/v1/questions",
      "summary": "Create a reusable question",
      "description": "Why this endpoint is needed:\nThe question bank is the foundation of reusable survey content. Teams need a way to add standardized question assets\nonce and reuse them across many surveys.\n\nWhat this endpoint does:\nIt creates a question with text, type, and scoring metadata and stores version context used by downstream snapshots.\n\nHow this endpoint does it:\nThe service validates required fields and max score constraints, applies tenant scoping from auth context,\npersists the question entity, and returns a QuestionResponse containing metadata and version pointers.\n",
      "tags": [
        "Questions"
      ],
      "pathParams": [],
      "queryParams": [],
      "headers": [],
      "requestBody": [
        {
          "name": "text",
          "type": "string",
          "required": true
        },
        {
          "name": "type",
          "type": "string(RANK|RATING_SCALE|SINGLE_CHOICE|MULTIPLE_CHOICE)",
          "required": true
        },
        {
          "name": "maxScore",
          "type": "number",
          "required": true
        },
        {
          "name": "optionConfig",
          "type": "string",
          "required": false,
          "description": "JSON string containing question-level options/configuration."
        }
      ]
    },
    {
      "id": "getQuestion",
      "method": "GET",
      "endpoint": "/questions/{id}",
      "path": "/api/v1/questions/{id}",
      "summary": "Get one question by id",
      "description": "Why this endpoint is needed:\nDetailed question retrieval is required for edit forms, review pages, and diagnostics.\n\nWhat this endpoint does:\nIt returns one question record by identifier under tenant access control.\n\nHow this endpoint does it:\nThe service resolves the record by ID with tenant-scoped checks and maps the result to a QuestionResponse DTO.\n",
      "tags": [
        "Questions"
      ],
      "pathParams": [
        {
          "name": "id",
          "type": "string",
          "required": true,
          "description": "Path parameter"
        }
      ],
      "queryParams": [],
      "headers": [],
      "requestBody": []
    },
    {
      "id": "updateQuestion",
      "method": "PUT",
      "endpoint": "/questions/{id}",
      "path": "/api/v1/questions/{id}",
      "summary": "Update an existing question",
      "description": "Why this endpoint is needed:\nContent quality evolves over time and questions require controlled updates.\n\nWhat this endpoint does:\nIt updates question fields and maintains version continuity used by snapshot-based references.\n\nHow this endpoint does it:\nThe service validates payload constraints, checks tenant ownership, persists changes, and returns updated question data.\n",
      "tags": [
        "Questions"
      ],
      "pathParams": [
        {
          "name": "id",
          "type": "string",
          "required": true,
          "description": "Path parameter"
        }
      ],
      "queryParams": [],
      "headers": [],
      "requestBody": [
        {
          "name": "text",
          "type": "string",
          "required": true
        },
        {
          "name": "type",
          "type": "string(RANK|RATING_SCALE|SINGLE_CHOICE|MULTIPLE_CHOICE)",
          "required": true
        },
        {
          "name": "maxScore",
          "type": "number",
          "required": true
        },
        {
          "name": "optionConfig",
          "type": "string",
          "required": false,
          "description": "JSON string containing question-level options/configuration."
        }
      ]
    },
    {
      "id": "deactivateQuestion",
      "method": "DELETE",
      "endpoint": "/questions/{id}",
      "path": "/api/v1/questions/{id}",
      "summary": "Deactivate a question",
      "description": "Why this endpoint is needed:\nHistorical integrity is important, so hard-deletes are avoided while obsolete questions must be removed from active authoring lists.\n\nWhat this endpoint does:\nIt marks the target question as inactive.\n\nHow this endpoint does it:\nThe service resolves the question by tenant context, updates active status, and returns no-content on success.\n",
      "tags": [
        "Questions"
      ],
      "pathParams": [
        {
          "name": "id",
          "type": "string",
          "required": true,
          "description": "Path parameter"
        }
      ],
      "queryParams": [],
      "headers": [],
      "requestBody": []
    },
    {
      "id": "submitResponse",
      "method": "POST",
      "endpoint": "/responses",
      "path": "/api/v1/responses",
      "summary": "Submit survey response (public and private campaign entry)",
      "description": "Why this endpoint is needed:\nThis is the central data-ingestion endpoint where responder participation is captured.\n\nWhat this endpoint does:\nIt accepts answer payload for a campaign, enforces campaign status and runtime controls,\nenforces private-auth requirements where applicable, validates against pinned snapshot versions,\nauto-calculates weighted score when default profile exists, stores response and answers, then auto-locks response.\n\nHow this endpoint does it:\nThe service performs campaign lookup, access-mode enforcement (public or private), credential validation\n(responderToken or responderAccessCode for private), settings checks (quota/restrictions/timeouts), snapshot-based\nanswer validation/scoring, persistence, and status transition to LOCKED before returning SurveyResponseResponse.\n",
      "tags": [
        "Responses"
      ],
      "pathParams": [],
      "queryParams": [],
      "headers": [],
      "requestBody": [
        {
          "name": "campaignId",
          "type": "string",
          "required": true
        },
        {
          "name": "respondentIdentifier",
          "type": "string",
          "required": false
        },
        {
          "name": "respondentIp",
          "type": "string",
          "required": false
        },
        {
          "name": "respondentDeviceFingerprint",
          "type": "string",
          "required": false
        },
        {
          "name": "responderToken",
          "type": "string",
          "required": false
        },
        {
          "name": "responderAccessCode",
          "type": "string",
          "required": false
        },
        {
          "name": "answers",
          "type": "object[]",
          "required": false
        }
      ]
    },
    {
      "id": "getResponse",
      "method": "GET",
      "endpoint": "/responses/{id}",
      "path": "/api/v1/responses/{id}",
      "summary": "Get one response by id",
      "description": "Why this endpoint is needed:\nSupport and operational teams need direct access to a specific response record for verification and exception handling.\n\nWhat this endpoint does:\nIt returns one response and its answer details.\n\nHow this endpoint does it:\nResponse service resolves response by id under tenant context and maps nested answer records to DTO.\n",
      "tags": [
        "Responses"
      ],
      "pathParams": [
        {
          "name": "id",
          "type": "string",
          "required": true,
          "description": "Path parameter"
        }
      ],
      "queryParams": [],
      "headers": [],
      "requestBody": []
    },
    {
      "id": "lockResponse",
      "method": "POST",
      "endpoint": "/responses/{id}/lock",
      "path": "/api/v1/responses/{id}/lock",
      "summary": "Lock a response manually",
      "description": "Why this endpoint is needed:\nAlthough responses auto-lock on submit, operations teams may need explicit lock controls for exceptional workflows.\n\nWhat this endpoint does:\nIt changes response state to locked and records lock timestamp.\n\nHow this endpoint does it:\nLocking service validates current state and tenant ownership, applies status mutation, and returns updated response.\n",
      "tags": [
        "Responses"
      ],
      "pathParams": [
        {
          "name": "id",
          "type": "string",
          "required": true,
          "description": "Path parameter"
        }
      ],
      "queryParams": [],
      "headers": [],
      "requestBody": []
    },
    {
      "id": "reopenResponse",
      "method": "POST",
      "endpoint": "/responses/{id}/reopen",
      "path": "/api/v1/responses/{id}/reopen",
      "summary": "Reopen a locked response with reason",
      "description": "Why this endpoint is needed:\nReal operations require a controlled exception path for corrections after lock.\n\nWhat this endpoint does:\nIt reopens a previously locked response and captures reason/window metadata.\n\nHow this endpoint does it:\nLocking service validates state transitions and reason constraints, updates response status,\nand returns reopened response record. If campaign close date has passed, close-time lock enforcement may lock it again.\n",
      "tags": [
        "Responses"
      ],
      "pathParams": [
        {
          "name": "id",
          "type": "string",
          "required": true,
          "description": "Path parameter"
        }
      ],
      "queryParams": [],
      "headers": [],
      "requestBody": [
        {
          "name": "reason",
          "type": "string",
          "required": true
        },
        {
          "name": "reopenWindowMinutes",
          "type": "integer",
          "required": false
        }
      ]
    },
    {
      "id": "getCampaignAnalytics",
      "method": "GET",
      "endpoint": "/responses/analytics/{campaignId}",
      "path": "/api/v1/responses/analytics/{campaignId}",
      "summary": "Get aggregated analytics for a campaign",
      "description": "Why this endpoint is needed:\nCampaign operators need near-real-time aggregate visibility without reading raw response payloads.\n\nWhat this endpoint does:\nIt returns aggregate counts and completion rate metrics for a campaign.\n\nHow this endpoint does it:\nAnalytics service runs campaign-scoped aggregate queries under tenant context and returns CampaignAnalytics DTO.\n",
      "tags": [
        "Responses"
      ],
      "pathParams": [
        {
          "name": "campaignId",
          "type": "string",
          "required": true
        }
      ],
      "queryParams": [],
      "headers": [],
      "requestBody": []
    },
    {
      "id": "listResponsesByCampaign",
      "method": "GET",
      "endpoint": "/responses/campaign/{campaignId}",
      "path": "/api/v1/responses/campaign/{campaignId}",
      "summary": "List responses for a campaign",
      "description": "Why this endpoint is needed:\nCampaign managers need a scoped list view of responses for review, quality checks, and manual workflows.\n\nWhat this endpoint does:\nIt returns campaign-scoped response list under tenant ownership checks.\n\nHow this endpoint does it:\nThe service validates campaign accessibility under tenant context and fetches mapped response rows by campaign id.\n",
      "tags": [
        "Responses"
      ],
      "pathParams": [
        {
          "name": "campaignId",
          "type": "string",
          "required": true
        }
      ],
      "queryParams": [],
      "headers": [],
      "requestBody": []
    },
    {
      "id": "calculateWeightedScore",
      "method": "POST",
      "endpoint": "/scoring/calculate/{profileId}",
      "path": "/api/v1/scoring/calculate/{profileId}",
      "summary": "Calculate weighted score from raw category scores",
      "description": "Why this endpoint is needed:\nConsumers need deterministic score output from raw category data and configured business weighting policy.\n\nWhat this endpoint does:\nIt computes normalized category contributions and returns a total weighted score breakdown.\n\nHow this endpoint does it:\nScoring engine loads profile weights, validates inputs, computes per-category normalized and weighted values,\nand returns ScoreResult with category details.\n",
      "tags": [
        "Scoring"
      ],
      "pathParams": [
        {
          "name": "profileId",
          "type": "string",
          "required": true
        }
      ],
      "queryParams": [],
      "headers": [],
      "requestBody": []
    },
    {
      "id": "createWeightProfile",
      "method": "POST",
      "endpoint": "/scoring/profiles",
      "path": "/api/v1/scoring/profiles",
      "summary": "Create scoring weight profile",
      "description": "Why this endpoint is needed:\nScore computation requires explicit category weighting definitions linked to campaign context.\n\nWhat this endpoint does:\nIt creates a weight profile that defines how raw category scores contribute to total score.\n\nHow this endpoint does it:\nService validates campaign scope and category-weight payload, persists profile and nested weights,\nand returns profile representation.\n",
      "tags": [
        "Scoring"
      ],
      "pathParams": [],
      "queryParams": [],
      "headers": [],
      "requestBody": [
        {
          "name": "name",
          "type": "string",
          "required": true
        },
        {
          "name": "campaignId",
          "type": "string",
          "required": true
        },
        {
          "name": "categoryWeights",
          "type": "object[]",
          "required": false
        }
      ]
    },
    {
      "id": "getWeightProfile",
      "method": "GET",
      "endpoint": "/scoring/profiles/{id}",
      "path": "/api/v1/scoring/profiles/{id}",
      "summary": "Get one scoring profile by id",
      "description": "Why this endpoint is needed:\nTeams need profile-level visibility for verification and lifecycle updates.\n\nWhat this endpoint does:\nIt returns one weight profile including category weights.\n\nHow this endpoint does it:\nService loads profile by id under tenant context and maps to DTO.\n",
      "tags": [
        "Scoring"
      ],
      "pathParams": [
        {
          "name": "id",
          "type": "string",
          "required": true,
          "description": "Path parameter"
        }
      ],
      "queryParams": [],
      "headers": [],
      "requestBody": []
    },
    {
      "id": "updateWeightProfile",
      "method": "PUT",
      "endpoint": "/scoring/profiles/{id}",
      "path": "/api/v1/scoring/profiles/{id}",
      "summary": "Update scoring profile",
      "description": "Why this endpoint is needed:\nScoring rubrics evolve and profiles require controlled updates.\n\nWhat this endpoint does:\nIt updates profile metadata and category-weight assignments.\n\nHow this endpoint does it:\nService validates payload and tenant ownership, persists new values, and returns updated profile DTO.\n",
      "tags": [
        "Scoring"
      ],
      "pathParams": [
        {
          "name": "id",
          "type": "string",
          "required": true,
          "description": "Path parameter"
        }
      ],
      "queryParams": [],
      "headers": [],
      "requestBody": [
        {
          "name": "name",
          "type": "string",
          "required": true
        },
        {
          "name": "campaignId",
          "type": "string",
          "required": true
        },
        {
          "name": "categoryWeights",
          "type": "object[]",
          "required": false
        }
      ]
    },
    {
      "id": "deactivateWeightProfile",
      "method": "DELETE",
      "endpoint": "/scoring/profiles/{id}",
      "path": "/api/v1/scoring/profiles/{id}",
      "summary": "Deactivate scoring profile",
      "description": "Why this endpoint is needed:\nObsolete profiles must be retired while preserving historical result integrity.\n\nWhat this endpoint does:\nIt marks profile inactive.\n\nHow this endpoint does it:\nTenant-scoped lookup followed by active-state mutation and no-content response.\n",
      "tags": [
        "Scoring"
      ],
      "pathParams": [
        {
          "name": "id",
          "type": "string",
          "required": true,
          "description": "Path parameter"
        }
      ],
      "queryParams": [],
      "headers": [],
      "requestBody": []
    },
    {
      "id": "validateWeightProfile",
      "method": "POST",
      "endpoint": "/scoring/profiles/{id}/validate",
      "path": "/api/v1/scoring/profiles/{id}/validate",
      "summary": "Validate category weight sum for a profile",
      "description": "Why this endpoint is needed:\nInvalid weighting definitions can produce misleading score outputs; explicit pre-check endpoint reduces operational mistakes.\n\nWhat this endpoint does:\nIt validates profile constraints (including total weight sum rule).\n\nHow this endpoint does it:\nService computes profile totals and throws deterministic business error when constraints are violated.\n",
      "tags": [
        "Scoring"
      ],
      "pathParams": [
        {
          "name": "id",
          "type": "string",
          "required": true,
          "description": "Path parameter"
        }
      ],
      "queryParams": [],
      "headers": [],
      "requestBody": []
    },
    {
      "id": "listWeightProfilesByCampaign",
      "method": "GET",
      "endpoint": "/scoring/profiles/campaign/{campaignId}",
      "path": "/api/v1/scoring/profiles/campaign/{campaignId}",
      "summary": "List scoring profiles for campaign",
      "description": "Why this endpoint is needed:\nCampaign-level scoring operations need discoverability of attached profiles.\n\nWhat this endpoint does:\nIt returns all active/in-scope profiles linked to the specified campaign.\n\nHow this endpoint does it:\nService validates campaign access by tenant and fetches profile rows filtered by campaign id.\n",
      "tags": [
        "Scoring"
      ],
      "pathParams": [
        {
          "name": "campaignId",
          "type": "string",
          "required": true
        }
      ],
      "queryParams": [],
      "headers": [],
      "requestBody": []
    },
    {
      "id": "listSurveys",
      "method": "GET",
      "endpoint": "/surveys",
      "path": "/api/v1/surveys",
      "summary": "List active surveys",
      "description": "Why this endpoint is needed:\nAdmin dashboards require an overview of reusable survey assets and lifecycle states.\n\nWhat this endpoint does:\nIt returns active surveys under current tenant scope.\n\nHow this endpoint does it:\nSurvey repository filters by tenant and active state, then maps records to response DTOs.\n",
      "tags": [
        "Surveys"
      ],
      "pathParams": [],
      "queryParams": [],
      "headers": [],
      "requestBody": []
    },
    {
      "id": "createSurvey",
      "method": "POST",
      "endpoint": "/surveys",
      "path": "/api/v1/surveys",
      "summary": "Create a survey draft",
      "description": "Why this endpoint is needed:\nSurvey lifecycle begins with a draft container where structure can be assembled before publication.\n\nWhat this endpoint does:\nIt creates a tenant-scoped survey draft with title, description, and page/question structure.\n\nHow this endpoint does it:\nThe service validates schema, persists draft state, and returns the survey representation including lifecycle status.\n",
      "tags": [
        "Surveys"
      ],
      "pathParams": [],
      "queryParams": [],
      "headers": [],
      "requestBody": [
        {
          "name": "title",
          "type": "string",
          "required": true
        },
        {
          "name": "description",
          "type": "string",
          "required": false
        },
        {
          "name": "pages",
          "type": "object[]",
          "required": false
        }
      ]
    },
    {
      "id": "getSurvey",
      "method": "GET",
      "endpoint": "/surveys/{id}",
      "path": "/api/v1/surveys/{id}",
      "summary": "Get one survey by id",
      "description": "Why this endpoint is needed:\nTeams need detailed survey context for editing, publishing decisions, and audits.\n\nWhat this endpoint does:\nIt returns one survey including pages/questions and lifecycle metadata.\n\nHow this endpoint does it:\nThe service resolves survey by id and tenant, then maps nested structures into SurveyResponse.\n",
      "tags": [
        "Surveys"
      ],
      "pathParams": [
        {
          "name": "id",
          "type": "string",
          "required": true,
          "description": "Path parameter"
        }
      ],
      "queryParams": [],
      "headers": [],
      "requestBody": []
    },
    {
      "id": "updateSurvey",
      "method": "PUT",
      "endpoint": "/surveys/{id}",
      "path": "/api/v1/surveys/{id}",
      "summary": "Update survey draft content",
      "description": "Why this endpoint is needed:\nDraft surveys evolve before publication and require iterative edits.\n\nWhat this endpoint does:\nIt updates survey details and structure when lifecycle rules allow modification.\n\nHow this endpoint does it:\nThe service validates immutability/lifecycle constraints, persists updates, and returns the updated survey.\n",
      "tags": [
        "Surveys"
      ],
      "pathParams": [
        {
          "name": "id",
          "type": "string",
          "required": true,
          "description": "Path parameter"
        }
      ],
      "queryParams": [],
      "headers": [],
      "requestBody": [
        {
          "name": "title",
          "type": "string",
          "required": true
        },
        {
          "name": "description",
          "type": "string",
          "required": false
        },
        {
          "name": "pages",
          "type": "object[]",
          "required": false
        }
      ]
    },
    {
      "id": "deactivateSurvey",
      "method": "DELETE",
      "endpoint": "/surveys/{id}",
      "path": "/api/v1/surveys/{id}",
      "summary": "Deactivate a survey",
      "description": "Why this endpoint is needed:\nOrganizations need to retire obsolete surveys without physically deleting historical references.\n\nWhat this endpoint does:\nIt marks the survey inactive.\n\nHow this endpoint does it:\nTenant ownership is validated and active flag is disabled; endpoint returns no-content on success.\n",
      "tags": [
        "Surveys"
      ],
      "pathParams": [
        {
          "name": "id",
          "type": "string",
          "required": true,
          "description": "Path parameter"
        }
      ],
      "queryParams": [],
      "headers": [],
      "requestBody": []
    },
    {
      "id": "transitionSurveyLifecycle",
      "method": "POST",
      "endpoint": "/surveys/{id}/lifecycle",
      "path": "/api/v1/surveys/{id}/lifecycle",
      "summary": "Transition survey lifecycle state",
      "description": "Why this endpoint is needed:\nLifecycle transitions are a governance gate that prevents unsafe operations (for example activating campaigns from unpublished surveys).\n\nWhat this endpoint does:\nIt applies a requested lifecycle transition (such as Draft->Published or Closed->Published with reason).\n\nHow this endpoint does it:\nThe service checks allowed transition matrix, validates transition requirements, updates lifecycle state,\nand returns the updated survey representation.\n",
      "tags": [
        "Surveys"
      ],
      "pathParams": [
        {
          "name": "id",
          "type": "string",
          "required": true,
          "description": "Path parameter"
        }
      ],
      "queryParams": [],
      "headers": [],
      "requestBody": [
        {
          "name": "targetState",
          "type": "string",
          "required": true
        },
        {
          "name": "reason",
          "type": "string",
          "required": false
        }
      ]
    }
  ]
} as const;
