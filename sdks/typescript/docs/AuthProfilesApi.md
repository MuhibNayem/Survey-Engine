# AuthProfilesApi

All URIs are relative to *http://localhost*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**createAuthProfile**](AuthProfilesApi.md#createauthprofile) | **POST** /api/v1/auth/profiles | Create tenant responder-auth profile |
| [**getAuthProfileAudit**](AuthProfilesApi.md#getauthprofileaudit) | **GET** /api/v1/auth/profiles/{id}/audit | Get auth profile audit history |
| [**getAuthProfileByTenant**](AuthProfilesApi.md#getauthprofilebytenant) | **GET** /api/v1/auth/profiles/tenant/{tenantId} | Get auth profile for a tenant |
| [**getProviderTemplate**](AuthProfilesApi.md#getprovidertemplate) | **GET** /api/v1/auth/providers/templates/{providerCode} | Get one provider setup template |
| [**listProviderTemplates**](AuthProfilesApi.md#listprovidertemplates) | **GET** /api/v1/auth/providers/templates | List built-in provider setup templates |
| [**rotateAuthProfileKey**](AuthProfilesApi.md#rotateauthprofilekey) | **POST** /api/v1/auth/profiles/{id}/rotate-key | Rotate auth profile key version |
| [**updateAuthProfile**](AuthProfilesApi.md#updateauthprofile) | **PUT** /api/v1/auth/profiles/{id} | Update tenant responder-auth profile |
| [**validateResponderToken**](AuthProfilesApi.md#validaterespondertoken) | **POST** /api/v1/auth/validate/{tenantId} | Validate responder token against tenant auth policy |



## createAuthProfile

> AuthProfileResponse createAuthProfile(authProfileRequest)

Create tenant responder-auth profile

Why this endpoint is needed: Private campaign authentication must be configured once per tenant so every campaign can reuse a consistent trust policy.  What this endpoint does: It creates a tenant auth profile containing auth mode, trust metadata, claim mappings, fallback policy, and OIDC/signing settings.  How this endpoint does it: The service validates tenant scope, enforces mapping rules (including required respondentId mapping), applies defaults (such as OIDC scope defaults), persists profile + mappings, and writes audit events. 

### Example

```ts
import {
  Configuration,
  AuthProfilesApi,
} from '@survey-engine/sdk';
import type { CreateAuthProfileRequest } from '@survey-engine/sdk';

async function example() {
  console.log("🚀 Testing @survey-engine/sdk SDK...");
  const config = new Configuration({ 
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
  });
  const api = new AuthProfilesApi(config);

  const body = {
    // AuthProfileRequest
    authProfileRequest: ...,
  } satisfies CreateAuthProfileRequest;

  try {
    const data = await api.createAuthProfile(body);
    console.log(data);
  } catch (error) {
    console.error(error);
  }
}

// Run the test
example().catch(console.error);
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **authProfileRequest** | [AuthProfileRequest](AuthProfileRequest.md) |  | |

### Return type

[**AuthProfileResponse**](AuthProfileResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: `application/json`
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **201** | Auth profile created |  -  |
| **400** | Request validation failed or business rule validation failed |  -  |
| **401** | Authentication missing or invalid |  -  |
| **403** | Authenticated but not allowed by tenant/role/security policy |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## getAuthProfileAudit

> Array&lt;AuthConfigAudit&gt; getAuthProfileAudit(id)

Get auth profile audit history

Why this endpoint is needed: Security-sensitive configuration changes require traceability for compliance and troubleshooting.  What this endpoint does: It returns ordered audit entries for an auth profile including actor and before/after values.  How this endpoint does it: The repository queries audit rows by auth_profile_id in descending timestamp order and returns serialized audit objects. 

### Example

```ts
import {
  Configuration,
  AuthProfilesApi,
} from '@survey-engine/sdk';
import type { GetAuthProfileAuditRequest } from '@survey-engine/sdk';

async function example() {
  console.log("🚀 Testing @survey-engine/sdk SDK...");
  const config = new Configuration({ 
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
  });
  const api = new AuthProfilesApi(config);

  const body = {
    // string
    id: 38400000-8cf0-11bd-b23e-10b96e4ef00d,
  } satisfies GetAuthProfileAuditRequest;

  try {
    const data = await api.getAuthProfileAudit(body);
    console.log(data);
  } catch (error) {
    console.error(error);
  }
}

// Run the test
example().catch(console.error);
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **id** | `string` |  | [Defaults to `undefined`] |

### Return type

[**Array&lt;AuthConfigAudit&gt;**](AuthConfigAudit.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Audit entries returned |  -  |
| **401** | Authentication missing or invalid |  -  |
| **403** | Authenticated but not allowed by tenant/role/security policy |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## getAuthProfileByTenant

> AuthProfileResponse getAuthProfileByTenant(tenantId)

Get auth profile for a tenant

Why this endpoint is needed: Operators need an easy way to verify effective auth configuration before launching or troubleshooting private campaigns.  What this endpoint does: It returns the tenant\&#39;s auth profile and claim mapping configuration.  How this endpoint does it: Tenant access checks are performed, then profile data is loaded and mapped to response DTO. 

### Example

```ts
import {
  Configuration,
  AuthProfilesApi,
} from '@survey-engine/sdk';
import type { GetAuthProfileByTenantRequest } from '@survey-engine/sdk';

async function example() {
  console.log("🚀 Testing @survey-engine/sdk SDK...");
  const config = new Configuration({ 
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
  });
  const api = new AuthProfilesApi(config);

  const body = {
    // string
    tenantId: tenantId_example,
  } satisfies GetAuthProfileByTenantRequest;

  try {
    const data = await api.getAuthProfileByTenant(body);
    console.log(data);
  } catch (error) {
    console.error(error);
  }
}

// Run the test
example().catch(console.error);
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **tenantId** | `string` |  | [Defaults to `undefined`] |

### Return type

[**AuthProfileResponse**](AuthProfileResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Auth profile returned |  -  |
| **401** | Authentication missing or invalid |  -  |
| **403** | Authenticated but not allowed by tenant/role/security policy |  -  |
| **404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## getProviderTemplate

> ProviderTemplateResponse getProviderTemplate(providerCode)

Get one provider setup template

Why this endpoint is needed: Setup forms often require provider-specific field guidance and defaults rather than a full template catalog.  What this endpoint does: It returns one template by provider code (for example OKTA, AUTH0, AZURE_AD, KEYCLOAK).  How this endpoint does it: The service validates provider code input, resolves the template from in-memory catalog, and returns structured metadata. 

### Example

```ts
import {
  Configuration,
  AuthProfilesApi,
} from '@survey-engine/sdk';
import type { GetProviderTemplateRequest } from '@survey-engine/sdk';

async function example() {
  console.log("🚀 Testing @survey-engine/sdk SDK...");
  const config = new Configuration({ 
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
  });
  const api = new AuthProfilesApi(config);

  const body = {
    // string
    providerCode: providerCode_example,
  } satisfies GetProviderTemplateRequest;

  try {
    const data = await api.getProviderTemplate(body);
    console.log(data);
  } catch (error) {
    console.error(error);
  }
}

// Run the test
example().catch(console.error);
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **providerCode** | `string` |  | [Defaults to `undefined`] |

### Return type

[**ProviderTemplateResponse**](ProviderTemplateResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Provider template returned |  -  |
| **401** | Authentication missing or invalid |  -  |
| **404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## listProviderTemplates

> Array&lt;ProviderTemplateResponse&gt; listProviderTemplates()

List built-in provider setup templates

Why this endpoint is needed: Tenant onboarding is faster and less error-prone when common IdP providers have pre-defined setup templates.  What this endpoint does: It returns template metadata for supported providers, including default scopes, required config fields, and default claim mapping suggestions.  How this endpoint does it: A template service returns static curated provider definitions consumed by UI and SDK onboarding flows. 

### Example

```ts
import {
  Configuration,
  AuthProfilesApi,
} from '@survey-engine/sdk';
import type { ListProviderTemplatesRequest } from '@survey-engine/sdk';

async function example() {
  console.log("🚀 Testing @survey-engine/sdk SDK...");
  const config = new Configuration({ 
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
  });
  const api = new AuthProfilesApi(config);

  try {
    const data = await api.listProviderTemplates();
    console.log(data);
  } catch (error) {
    console.error(error);
  }
}

// Run the test
example().catch(console.error);
```

### Parameters

This endpoint does not need any parameter.

### Return type

[**Array&lt;ProviderTemplateResponse&gt;**](ProviderTemplateResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Provider templates returned |  -  |
| **401** | Authentication missing or invalid |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## rotateAuthProfileKey

> AuthProfileResponse rotateAuthProfileKey(id)

Rotate auth profile key version

Why this endpoint is needed: Signing key version rotation is required for security hygiene and incident response readiness.  What this endpoint does: It increments the active key version for a tenant auth profile.  How this endpoint does it: The service updates active key version, persists change, and creates an audit record describing the rotation action. 

### Example

```ts
import {
  Configuration,
  AuthProfilesApi,
} from '@survey-engine/sdk';
import type { RotateAuthProfileKeyRequest } from '@survey-engine/sdk';

async function example() {
  console.log("🚀 Testing @survey-engine/sdk SDK...");
  const config = new Configuration({ 
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
  });
  const api = new AuthProfilesApi(config);

  const body = {
    // string
    id: 38400000-8cf0-11bd-b23e-10b96e4ef00d,
  } satisfies RotateAuthProfileKeyRequest;

  try {
    const data = await api.rotateAuthProfileKey(body);
    console.log(data);
  } catch (error) {
    console.error(error);
  }
}

// Run the test
example().catch(console.error);
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **id** | `string` |  | [Defaults to `undefined`] |

### Return type

[**AuthProfileResponse**](AuthProfileResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Key version rotated |  -  |
| **401** | Authentication missing or invalid |  -  |
| **403** | Authenticated but not allowed by tenant/role/security policy |  -  |
| **404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## updateAuthProfile

> AuthProfileResponse updateAuthProfile(id, authProfileRequest)

Update tenant responder-auth profile

Why this endpoint is needed: Identity provider details and claim models change over time; profile updates must be supported without rebuilding tenants.  What this endpoint does: It updates auth profile trust settings, claim mappings, scope behavior, and fallback policies.  How this endpoint does it: The service validates tenant access and mapping constraints, applies secure defaults where required, persists profile changes, and records before/after snapshots in auth audit. 

### Example

```ts
import {
  Configuration,
  AuthProfilesApi,
} from '@survey-engine/sdk';
import type { UpdateAuthProfileRequest } from '@survey-engine/sdk';

async function example() {
  console.log("🚀 Testing @survey-engine/sdk SDK...");
  const config = new Configuration({ 
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
  });
  const api = new AuthProfilesApi(config);

  const body = {
    // string
    id: 38400000-8cf0-11bd-b23e-10b96e4ef00d,
    // AuthProfileRequest
    authProfileRequest: ...,
  } satisfies UpdateAuthProfileRequest;

  try {
    const data = await api.updateAuthProfile(body);
    console.log(data);
  } catch (error) {
    console.error(error);
  }
}

// Run the test
example().catch(console.error);
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **id** | `string` |  | [Defaults to `undefined`] |
| **authProfileRequest** | [AuthProfileRequest](AuthProfileRequest.md) |  | |

### Return type

[**AuthProfileResponse**](AuthProfileResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: `application/json`
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Auth profile updated |  -  |
| **400** | Request validation failed or business rule validation failed |  -  |
| **401** | Authentication missing or invalid |  -  |
| **403** | Authenticated but not allowed by tenant/role/security policy |  -  |
| **404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## validateResponderToken

> TokenValidationResult validateResponderToken(tenantId, body)

Validate responder token against tenant auth policy

Why this endpoint is needed: Integrations and runtime checks require an explicit token-validation endpoint that applies tenant-specific trust policy.  What this endpoint does: It validates responder token input according to configured auth mode and claim-mapping rules, then returns standardized success/failure payload.  How this endpoint does it: Service loads tenant auth profile, routes validation by mode (public/signed/OIDC external trust), applies cryptographic and claim checks, and returns TokenValidationResult. 

### Example

```ts
import {
  Configuration,
  AuthProfilesApi,
} from '@survey-engine/sdk';
import type { ValidateResponderTokenRequest } from '@survey-engine/sdk';

async function example() {
  console.log("🚀 Testing @survey-engine/sdk SDK...");
  const api = new AuthProfilesApi();

  const body = {
    // string
    tenantId: tenantId_example,
    // string (optional)
    body: body_example,
  } satisfies ValidateResponderTokenRequest;

  try {
    const data = await api.validateResponderToken(body);
    console.log(data);
  } catch (error) {
    console.error(error);
  }
}

// Run the test
example().catch(console.error);
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **tenantId** | `string` |  | [Defaults to `undefined`] |
| **body** | `string` |  | [Optional] |

### Return type

[**TokenValidationResult**](TokenValidationResult.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: `text/plain`, `application/json`
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Token validation result returned |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)

