# @survey-engine/sdk@1.1.0

A TypeScript SDK client for the localhost API.

## Usage

First, install the SDK from npm.

```bash
npm install @survey-engine/sdk --save
```

Next, try it out.


```ts
import {
  Configuration,
  AdminAuthenticationApi,
} from '@survey-engine/sdk';
import type { GetCsrfTokenRequest } from '@survey-engine/sdk';

async function example() {
  console.log("🚀 Testing @survey-engine/sdk SDK...");
  const api = new AdminAuthenticationApi();

  try {
    const data = await api.getCsrfToken();
    console.log(data);
  } catch (error) {
    console.error(error);
  }
}

// Run the test
example().catch(console.error);
```


## Documentation

### API Endpoints

All URIs are relative to *http://localhost*

| Class | Method | HTTP request | Description
| ----- | ------ | ------------ | -------------
*AdminAuthenticationApi* | [**getCsrfToken**](docs/AdminAuthenticationApi.md#getcsrftoken) | **GET** /api/v1/admin/auth/csrf | Get CSRF token for browser cookie session mode
*AdminAuthenticationApi* | [**getCurrentAdmin**](docs/AdminAuthenticationApi.md#getcurrentadmin) | **GET** /api/v1/admin/auth/me | Retrieve current authenticated admin profile
*AdminAuthenticationApi* | [**loginAdmin**](docs/AdminAuthenticationApi.md#loginadmin) | **POST** /api/v1/admin/auth/login | Log in an existing admin and issue fresh auth cookies
*AdminAuthenticationApi* | [**loginAdminTokenMode**](docs/AdminAuthenticationApi.md#loginadmintokenmode) | **POST** /api/v1/admin/auth/token/login | Log in admin in headless token mode
*AdminAuthenticationApi* | [**logoutAdmin**](docs/AdminAuthenticationApi.md#logoutadmin) | **POST** /api/v1/admin/auth/logout | Clear admin auth cookies
*AdminAuthenticationApi* | [**refreshAdminToken**](docs/AdminAuthenticationApi.md#refreshadmintoken) | **POST** /api/v1/admin/auth/refresh | Rotate refresh cookie and issue a new admin session
*AdminAuthenticationApi* | [**refreshAdminTokenMode**](docs/AdminAuthenticationApi.md#refreshadmintokenmode) | **POST** /api/v1/admin/auth/token/refresh | Refresh session in headless token mode
*AdminAuthenticationApi* | [**registerAdmin**](docs/AdminAuthenticationApi.md#registeradmin) | **POST** /api/v1/admin/auth/register | Register a tenant admin account and bootstrap tenant access
*AdminAuthenticationApi* | [**registerAdminTokenMode**](docs/AdminAuthenticationApi.md#registeradmintokenmode) | **POST** /api/v1/admin/auth/token/register | Register admin in headless token mode
*AdminAuthenticationApi* | [**revertImpersonation**](docs/AdminAuthenticationApi.md#revertimpersonation) | **POST** /api/v1/admin/auth/revert-impersonation | Restore original super-admin cookies after impersonation
*AuditLogsApi* | [**getPlatformAuditLogs**](docs/AuditLogsApi.md#getplatformauditlogs) | **GET** /api/v1/admin/superadmin/audit-logs | Query platform-wide audit logs (SUPER_ADMIN)
*AuditLogsApi* | [**getTenantAuditLogs**](docs/AuditLogsApi.md#gettenantauditlogs) | **GET** /api/v1/audit-logs | Query tenant-scoped activity logs
*AuthProfilesApi* | [**createAuthProfile**](docs/AuthProfilesApi.md#createauthprofile) | **POST** /api/v1/auth/profiles | Create tenant responder-auth profile
*AuthProfilesApi* | [**getAuthProfileAudit**](docs/AuthProfilesApi.md#getauthprofileaudit) | **GET** /api/v1/auth/profiles/{id}/audit | Get auth profile audit history
*AuthProfilesApi* | [**getAuthProfileByTenant**](docs/AuthProfilesApi.md#getauthprofilebytenant) | **GET** /api/v1/auth/profiles/tenant/{tenantId} | Get auth profile for a tenant
*AuthProfilesApi* | [**getProviderTemplate**](docs/AuthProfilesApi.md#getprovidertemplate) | **GET** /api/v1/auth/providers/templates/{providerCode} | Get one provider setup template
*AuthProfilesApi* | [**listProviderTemplates**](docs/AuthProfilesApi.md#listprovidertemplates) | **GET** /api/v1/auth/providers/templates | List built-in provider setup templates
*AuthProfilesApi* | [**rotateAuthProfileKey**](docs/AuthProfilesApi.md#rotateauthprofilekey) | **POST** /api/v1/auth/profiles/{id}/rotate-key | Rotate auth profile key version
*AuthProfilesApi* | [**updateAuthProfile**](docs/AuthProfilesApi.md#updateauthprofile) | **PUT** /api/v1/auth/profiles/{id} | Update tenant responder-auth profile
*AuthProfilesApi* | [**validateResponderToken**](docs/AuthProfilesApi.md#validaterespondertoken) | **POST** /api/v1/auth/validate/{tenantId} | Validate responder token against tenant auth policy
*CampaignsApi* | [**activateCampaign**](docs/CampaignsApi.md#activatecampaign) | **POST** /api/v1/campaigns/{id}/activate | Activate campaign to accept responses
*CampaignsApi* | [**createCampaign**](docs/CampaignsApi.md#createcampaign) | **POST** /api/v1/campaigns | Create a campaign from a survey
*CampaignsApi* | [**deactivateCampaign**](docs/CampaignsApi.md#deactivatecampaign) | **DELETE** /api/v1/campaigns/{id} | Deactivate campaign
*CampaignsApi* | [**generateCampaignChannels**](docs/CampaignsApi.md#generatecampaignchannels) | **POST** /api/v1/campaigns/{id}/distribute | Generate distribution channels for a campaign
*CampaignsApi* | [**getCampaign**](docs/CampaignsApi.md#getcampaign) | **GET** /api/v1/campaigns/{id} | Get campaign by id
*CampaignsApi* | [**getCampaignPreview**](docs/CampaignsApi.md#getcampaignpreview) | **GET** /api/v1/campaigns/{id}/preview | Get admin preview payload for a campaign
*CampaignsApi* | [**getCampaignSettings**](docs/CampaignsApi.md#getcampaignsettings) | **GET** /api/v1/campaigns/{id}/settings | Get campaign runtime settings
*CampaignsApi* | [**getPublicCampaignPreview**](docs/CampaignsApi.md#getpubliccampaignpreview) | **GET** /api/v1/public/campaigns/{id}/preview | Get responder-facing preview payload (public endpoint)
*CampaignsApi* | [**listCampaignChannels**](docs/CampaignsApi.md#listcampaignchannels) | **GET** /api/v1/campaigns/{id}/channels | List generated channels for a campaign
*CampaignsApi* | [**listCampaigns**](docs/CampaignsApi.md#listcampaigns) | **GET** /api/v1/campaigns | List active campaigns
*CampaignsApi* | [**updateCampaign**](docs/CampaignsApi.md#updatecampaign) | **PUT** /api/v1/campaigns/{id} | Update campaign metadata
*CampaignsApi* | [**updateCampaignSettings**](docs/CampaignsApi.md#updatecampaignsettings) | **PUT** /api/v1/campaigns/{id}/settings | Update campaign runtime settings
*CategoriesApi* | [**createCategory**](docs/CategoriesApi.md#createcategory) | **POST** /api/v1/categories | Create a category grouping for questions
*CategoriesApi* | [**deactivateCategory**](docs/CategoriesApi.md#deactivatecategory) | **DELETE** /api/v1/categories/{id} | Deactivate a category
*CategoriesApi* | [**getCategory**](docs/CategoriesApi.md#getcategory) | **GET** /api/v1/categories/{id} | Get one category by id
*CategoriesApi* | [**listCategories**](docs/CategoriesApi.md#listcategories) | **GET** /api/v1/categories | List active categories
*CategoriesApi* | [**updateCategory**](docs/CategoriesApi.md#updatecategory) | **PUT** /api/v1/categories/{id} | Update category and mappings
*OIDCRespondentFlowApi* | [**completeRespondentOidc**](docs/OIDCRespondentFlowApi.md#completerespondentoidc) | **GET** /api/v1/auth/respondent/oidc/callback | Complete OIDC callback and issue one-time responder access code
*OIDCRespondentFlowApi* | [**startRespondentOidc**](docs/OIDCRespondentFlowApi.md#startrespondentoidc) | **POST** /api/v1/auth/respondent/oidc/start | Start private responder OIDC flow
*PlanCatalogApi* | [**listPlans**](docs/PlanCatalogApi.md#listplans) | **GET** /api/v1/admin/plans | List active plan definitions
*PlanCatalogApi* | [**upsertPlan**](docs/PlanCatalogApi.md#upsertplan) | **PUT** /api/v1/admin/plans | Create or update a plan definition (SUPER_ADMIN)
*QuestionsApi* | [**createQuestion**](docs/QuestionsApi.md#createquestion) | **POST** /api/v1/questions | Create a reusable question
*QuestionsApi* | [**deactivateQuestion**](docs/QuestionsApi.md#deactivatequestion) | **DELETE** /api/v1/questions/{id} | Deactivate a question
*QuestionsApi* | [**getQuestion**](docs/QuestionsApi.md#getquestion) | **GET** /api/v1/questions/{id} | Get one question by id
*QuestionsApi* | [**listQuestions**](docs/QuestionsApi.md#listquestions) | **GET** /api/v1/questions | List active questions for current tenant
*QuestionsApi* | [**updateQuestion**](docs/QuestionsApi.md#updatequestion) | **PUT** /api/v1/questions/{id} | Update an existing question
*ResponsesApi* | [**compareSegments**](docs/ResponsesApi.md#comparesegments) | **POST** /api/v1/analytics/campaigns/{campaignId}/compare | Compare multiple analytics segments
*ResponsesApi* | [**getCampaignAnalytics**](docs/ResponsesApi.md#getcampaignanalytics) | **GET** /api/v1/responses/analytics/{campaignId} | Get aggregated analytics for a campaign
*ResponsesApi* | [**getFullReport**](docs/ResponsesApi.md#getfullreport) | **GET** /api/v1/analytics/campaigns/{campaignId}/full-report | Get full analytics report with metadata filters
*ResponsesApi* | [**getResponse**](docs/ResponsesApi.md#getresponse) | **GET** /api/v1/responses/{id} | Get one response by id
*ResponsesApi* | [**listResponsesByCampaign**](docs/ResponsesApi.md#listresponsesbycampaign) | **GET** /api/v1/responses/campaign/{campaignId} | List responses for a campaign
*ResponsesApi* | [**lockResponse**](docs/ResponsesApi.md#lockresponse) | **POST** /api/v1/responses/{id}/lock | Lock a response manually
*ResponsesApi* | [**reopenResponse**](docs/ResponsesApi.md#reopenresponse) | **POST** /api/v1/responses/{id}/reopen | Reopen a locked response with reason
*ResponsesApi* | [**submitResponse**](docs/ResponsesApi.md#submitresponse) | **POST** /api/v1/responses | Submit survey response (public and private campaign entry)
*ScoringApi* | [**calculateWeightedScore**](docs/ScoringApi.md#calculateweightedscore) | **POST** /api/v1/scoring/calculate/{profileId} | Calculate weighted score from raw category scores
*ScoringApi* | [**createWeightProfile**](docs/ScoringApi.md#createweightprofile) | **POST** /api/v1/scoring/profiles | Create scoring weight profile
*ScoringApi* | [**deactivateWeightProfile**](docs/ScoringApi.md#deactivateweightprofile) | **DELETE** /api/v1/scoring/profiles/{id} | Deactivate scoring profile
*ScoringApi* | [**getWeightProfile**](docs/ScoringApi.md#getweightprofile) | **GET** /api/v1/scoring/profiles/{id} | Get one scoring profile by id
*ScoringApi* | [**listWeightProfilesByCampaign**](docs/ScoringApi.md#listweightprofilesbycampaign) | **GET** /api/v1/scoring/profiles/campaign/{campaignId} | List scoring profiles for campaign
*ScoringApi* | [**updateWeightProfile**](docs/ScoringApi.md#updateweightprofile) | **PUT** /api/v1/scoring/profiles/{id} | Update scoring profile
*ScoringApi* | [**validateWeightProfile**](docs/ScoringApi.md#validateweightprofile) | **POST** /api/v1/scoring/profiles/{id}/validate | Validate category weight sum for a profile
*SubscriptionApi* | [**checkoutSubscription**](docs/SubscriptionApi.md#checkoutsubscription) | **POST** /api/v1/admin/subscriptions/checkout | Start or change tenant subscription plan
*SubscriptionApi* | [**getMySubscription**](docs/SubscriptionApi.md#getmysubscription) | **GET** /api/v1/admin/subscriptions/me | Get current tenant subscription details
*SuperAdminApi* | [**activateTenant**](docs/SuperAdminApi.md#activatetenant) | **PUT** /api/v1/admin/superadmin/tenants/{tenantId}/activate | Activate a tenant
*SuperAdminApi* | [**getSuperAdminMetrics**](docs/SuperAdminApi.md#getsuperadminmetrics) | **GET** /api/v1/admin/superadmin/metrics | Retrieve platform-level metrics snapshot
*SuperAdminApi* | [**getTenantAdminMetrics**](docs/SuperAdminApi.md#gettenantadminmetrics) | **GET** /api/v1/admin/superadmin/tenants/metrics | Retrieve platform metrics via tenant-admin namespace
*SuperAdminApi* | [**impersonateTenant**](docs/SuperAdminApi.md#impersonatetenant) | **POST** /api/v1/admin/superadmin/tenants/{tenantId}/impersonate | Impersonate a tenant admin context
*SuperAdminApi* | [**listTenantsForSuperAdmin**](docs/SuperAdminApi.md#listtenantsforsuperadmin) | **GET** /api/v1/admin/superadmin/tenants | List tenants with subscription and status overview
*SuperAdminApi* | [**overrideTenantSubscription**](docs/SuperAdminApi.md#overridetenantsubscription) | **POST** /api/v1/admin/superadmin/tenants/{tenantId}/subscriptions/override | Override tenant subscription plan as super admin
*SuperAdminApi* | [**suspendTenant**](docs/SuperAdminApi.md#suspendtenant) | **PUT** /api/v1/admin/superadmin/tenants/{tenantId}/suspend | Suspend a tenant
*SurveysApi* | [**createSurvey**](docs/SurveysApi.md#createsurvey) | **POST** /api/v1/surveys | Create a survey draft
*SurveysApi* | [**deactivateSurvey**](docs/SurveysApi.md#deactivatesurvey) | **DELETE** /api/v1/surveys/{id} | Deactivate a survey
*SurveysApi* | [**getSurvey**](docs/SurveysApi.md#getsurvey) | **GET** /api/v1/surveys/{id} | Get one survey by id
*SurveysApi* | [**listSurveys**](docs/SurveysApi.md#listsurveys) | **GET** /api/v1/surveys | List active surveys
*SurveysApi* | [**transitionSurveyLifecycle**](docs/SurveysApi.md#transitionsurveylifecycle) | **POST** /api/v1/surveys/{id}/lifecycle | Transition survey lifecycle state
*SurveysApi* | [**updateSurvey**](docs/SurveysApi.md#updatesurvey) | **PUT** /api/v1/surveys/{id} | Update survey draft content


### Models

- [AuditLogResponse](docs/AuditLogResponse.md)
- [AuthConfigAudit](docs/AuthConfigAudit.md)
- [AuthProfileRequest](docs/AuthProfileRequest.md)
- [AuthProfileResponse](docs/AuthProfileResponse.md)
- [AuthProfileResponseClaimMappingsInner](docs/AuthProfileResponseClaimMappingsInner.md)
- [AuthResponse](docs/AuthResponse.md)
- [AuthUserResponse](docs/AuthUserResponse.md)
- [CampaignAnalytics](docs/CampaignAnalytics.md)
- [CampaignPreviewResponse](docs/CampaignPreviewResponse.md)
- [CampaignPreviewResponsePagesInner](docs/CampaignPreviewResponsePagesInner.md)
- [CampaignPreviewResponsePagesInnerQuestionsInner](docs/CampaignPreviewResponsePagesInnerQuestionsInner.md)
- [CampaignRequest](docs/CampaignRequest.md)
- [CampaignResponse](docs/CampaignResponse.md)
- [CampaignSettingsRequest](docs/CampaignSettingsRequest.md)
- [CampaignSettingsResponse](docs/CampaignSettingsResponse.md)
- [CategoryQuestionMappingRequest](docs/CategoryQuestionMappingRequest.md)
- [CategoryRequest](docs/CategoryRequest.md)
- [CategoryResponse](docs/CategoryResponse.md)
- [CategoryResponseQuestionMappingsInner](docs/CategoryResponseQuestionMappingsInner.md)
- [CategoryWeightRequest](docs/CategoryWeightRequest.md)
- [ClaimMappingRequest](docs/ClaimMappingRequest.md)
- [CsrfTokenResponse](docs/CsrfTokenResponse.md)
- [DistributionChannelResponse](docs/DistributionChannelResponse.md)
- [ErrorResponse](docs/ErrorResponse.md)
- [ErrorResponseFieldErrorsInner](docs/ErrorResponseFieldErrorsInner.md)
- [LifecycleTransitionRequest](docs/LifecycleTransitionRequest.md)
- [LoginRequest](docs/LoginRequest.md)
- [OidcCallbackResponse](docs/OidcCallbackResponse.md)
- [OidcStartRequest](docs/OidcStartRequest.md)
- [OidcStartResponse](docs/OidcStartResponse.md)
- [OverrideSubscriptionRequest](docs/OverrideSubscriptionRequest.md)
- [PageAuditLogResponse](docs/PageAuditLogResponse.md)
- [PageTenantOverviewResponse](docs/PageTenantOverviewResponse.md)
- [PageableObject](docs/PageableObject.md)
- [PlanDefinitionRequest](docs/PlanDefinitionRequest.md)
- [PlanDefinitionResponse](docs/PlanDefinitionResponse.md)
- [ProviderTemplateResponse](docs/ProviderTemplateResponse.md)
- [QuestionRequest](docs/QuestionRequest.md)
- [QuestionResponse](docs/QuestionResponse.md)
- [RefreshTokenRequest](docs/RefreshTokenRequest.md)
- [RegisterRequest](docs/RegisterRequest.md)
- [ReopenRequest](docs/ReopenRequest.md)
- [ResponseSubmissionRequest](docs/ResponseSubmissionRequest.md)
- [ResponseSubmissionRequestAnswersInner](docs/ResponseSubmissionRequestAnswersInner.md)
- [ScoreResult](docs/ScoreResult.md)
- [ScoreResultCategoryScoresInner](docs/ScoreResultCategoryScoresInner.md)
- [SortObject](docs/SortObject.md)
- [SubscribeRequest](docs/SubscribeRequest.md)
- [SubscriptionResponse](docs/SubscriptionResponse.md)
- [SuperAdminMetricsResponse](docs/SuperAdminMetricsResponse.md)
- [SurveyPageRequest](docs/SurveyPageRequest.md)
- [SurveyQuestionRequest](docs/SurveyQuestionRequest.md)
- [SurveyRequest](docs/SurveyRequest.md)
- [SurveyResponse](docs/SurveyResponse.md)
- [SurveyResponsePagesInner](docs/SurveyResponsePagesInner.md)
- [SurveyResponsePagesInnerQuestionsInner](docs/SurveyResponsePagesInnerQuestionsInner.md)
- [SurveyResponseResponse](docs/SurveyResponseResponse.md)
- [SurveyResponseResponseAnswersInner](docs/SurveyResponseResponseAnswersInner.md)
- [TenantOverviewResponse](docs/TenantOverviewResponse.md)
- [TokenValidationResult](docs/TokenValidationResult.md)
- [WeightProfileRequest](docs/WeightProfileRequest.md)
- [WeightProfileResponse](docs/WeightProfileResponse.md)
- [WeightProfileResponseCategoryWeightsInner](docs/WeightProfileResponseCategoryWeightsInner.md)

### Authorization


Authentication schemes defined for the API:
<a id="bearerAuth"></a>
#### bearerAuth


- **Type**: HTTP Bearer Token authentication (JWT)

## About

This TypeScript SDK client supports the [Fetch API](https://fetch.spec.whatwg.org/)
and is automatically generated by the
[OpenAPI Generator](https://openapi-generator.tech) project:

- API version: `1.1.0`
- Package version: `1.1.0`
- Generator version: `7.20.0`
- Build package: `org.openapitools.codegen.languages.TypeScriptFetchClientCodegen`

The generated npm module supports the following:

- Environments
  * Node.js
  * Webpack
  * Browserify
- Language levels
  * ES5 - you must have a Promises/A+ library installed
  * ES6
- Module systems
  * CommonJS
  * ES6 module system


## Development

### Building

To build the TypeScript source code, you need to have Node.js and npm installed.
After cloning the repository, navigate to the project directory and run:

```bash
npm install
npm run build
```

### Publishing

Once you've built the package, you can publish it to npm:

```bash
npm publish
```

## License

[]()
