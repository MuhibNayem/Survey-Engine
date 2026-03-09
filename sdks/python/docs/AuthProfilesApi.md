# openapi_client.AuthProfilesApi

All URIs are relative to *http://localhost*

Method | HTTP request | Description
------------- | ------------- | -------------
[**create_auth_profile**](AuthProfilesApi.md#create_auth_profile) | **POST** /api/v1/auth/profiles | Create tenant responder-auth profile
[**get_auth_profile_audit**](AuthProfilesApi.md#get_auth_profile_audit) | **GET** /api/v1/auth/profiles/{id}/audit | Get auth profile audit history
[**get_auth_profile_by_tenant**](AuthProfilesApi.md#get_auth_profile_by_tenant) | **GET** /api/v1/auth/profiles/tenant/{tenantId} | Get auth profile for a tenant
[**get_provider_template**](AuthProfilesApi.md#get_provider_template) | **GET** /api/v1/auth/providers/templates/{providerCode} | Get one provider setup template
[**list_provider_templates**](AuthProfilesApi.md#list_provider_templates) | **GET** /api/v1/auth/providers/templates | List built-in provider setup templates
[**rotate_auth_profile_key**](AuthProfilesApi.md#rotate_auth_profile_key) | **POST** /api/v1/auth/profiles/{id}/rotate-key | Rotate auth profile key version
[**update_auth_profile**](AuthProfilesApi.md#update_auth_profile) | **PUT** /api/v1/auth/profiles/{id} | Update tenant responder-auth profile
[**validate_responder_token**](AuthProfilesApi.md#validate_responder_token) | **POST** /api/v1/auth/validate/{tenantId} | Validate responder token against tenant auth policy


# **create_auth_profile**
> AuthProfileResponse create_auth_profile(auth_profile_request)

Create tenant responder-auth profile

Why this endpoint is needed:
Private campaign authentication must be configured once per tenant so every campaign can reuse a consistent trust policy.

What this endpoint does:
It creates a tenant auth profile containing auth mode, trust metadata, claim mappings, fallback policy,
and OIDC/signing settings.

How this endpoint does it:
The service validates tenant scope, enforces mapping rules (including required respondentId mapping),
applies defaults (such as OIDC scope defaults), persists profile + mappings, and writes audit events.


### Example

* Bearer (JWT) Authentication (bearerAuth):

```python
import openapi_client
from openapi_client.models.auth_profile_request import AuthProfileRequest
from openapi_client.models.auth_profile_response import AuthProfileResponse
from openapi_client.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://localhost
# See configuration.py for a list of all supported configuration parameters.
configuration = openapi_client.Configuration(
    host = "http://localhost"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization (JWT): bearerAuth
configuration = openapi_client.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with openapi_client.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = openapi_client.AuthProfilesApi(api_client)
    auth_profile_request = openapi_client.AuthProfileRequest() # AuthProfileRequest | 

    try:
        # Create tenant responder-auth profile
        api_response = api_instance.create_auth_profile(auth_profile_request)
        print("The response of AuthProfilesApi->create_auth_profile:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling AuthProfilesApi->create_auth_profile: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **auth_profile_request** | [**AuthProfileRequest**](AuthProfileRequest.md)|  | 

### Return type

[**AuthProfileResponse**](AuthProfileResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**201** | Auth profile created |  -  |
**400** | Request validation failed or business rule validation failed |  -  |
**401** | Authentication missing or invalid |  -  |
**403** | Authenticated but not allowed by tenant/role/security policy |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **get_auth_profile_audit**
> List[AuthConfigAudit] get_auth_profile_audit(id)

Get auth profile audit history

Why this endpoint is needed:
Security-sensitive configuration changes require traceability for compliance and troubleshooting.

What this endpoint does:
It returns ordered audit entries for an auth profile including actor and before/after values.

How this endpoint does it:
The repository queries audit rows by auth_profile_id in descending timestamp order and returns serialized audit objects.


### Example

* Bearer (JWT) Authentication (bearerAuth):

```python
import openapi_client
from openapi_client.models.auth_config_audit import AuthConfigAudit
from openapi_client.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://localhost
# See configuration.py for a list of all supported configuration parameters.
configuration = openapi_client.Configuration(
    host = "http://localhost"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization (JWT): bearerAuth
configuration = openapi_client.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with openapi_client.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = openapi_client.AuthProfilesApi(api_client)
    id = UUID('38400000-8cf0-11bd-b23e-10b96e4ef00d') # UUID | 

    try:
        # Get auth profile audit history
        api_response = api_instance.get_auth_profile_audit(id)
        print("The response of AuthProfilesApi->get_auth_profile_audit:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling AuthProfilesApi->get_auth_profile_audit: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **UUID**|  | 

### Return type

[**List[AuthConfigAudit]**](AuthConfigAudit.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | Audit entries returned |  -  |
**401** | Authentication missing or invalid |  -  |
**403** | Authenticated but not allowed by tenant/role/security policy |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **get_auth_profile_by_tenant**
> AuthProfileResponse get_auth_profile_by_tenant(tenant_id)

Get auth profile for a tenant

Why this endpoint is needed:
Operators need an easy way to verify effective auth configuration before launching or troubleshooting private campaigns.

What this endpoint does:
It returns the tenant's auth profile and claim mapping configuration.

How this endpoint does it:
Tenant access checks are performed, then profile data is loaded and mapped to response DTO.


### Example

* Bearer (JWT) Authentication (bearerAuth):

```python
import openapi_client
from openapi_client.models.auth_profile_response import AuthProfileResponse
from openapi_client.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://localhost
# See configuration.py for a list of all supported configuration parameters.
configuration = openapi_client.Configuration(
    host = "http://localhost"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization (JWT): bearerAuth
configuration = openapi_client.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with openapi_client.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = openapi_client.AuthProfilesApi(api_client)
    tenant_id = 'tenant_id_example' # str | 

    try:
        # Get auth profile for a tenant
        api_response = api_instance.get_auth_profile_by_tenant(tenant_id)
        print("The response of AuthProfilesApi->get_auth_profile_by_tenant:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling AuthProfilesApi->get_auth_profile_by_tenant: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **tenant_id** | **str**|  | 

### Return type

[**AuthProfileResponse**](AuthProfileResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | Auth profile returned |  -  |
**401** | Authentication missing or invalid |  -  |
**403** | Authenticated but not allowed by tenant/role/security policy |  -  |
**404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **get_provider_template**
> ProviderTemplateResponse get_provider_template(provider_code)

Get one provider setup template

Why this endpoint is needed:
Setup forms often require provider-specific field guidance and defaults rather than a full template catalog.

What this endpoint does:
It returns one template by provider code (for example OKTA, AUTH0, AZURE_AD, KEYCLOAK).

How this endpoint does it:
The service validates provider code input, resolves the template from in-memory catalog, and returns structured metadata.


### Example

* Bearer (JWT) Authentication (bearerAuth):

```python
import openapi_client
from openapi_client.models.provider_template_response import ProviderTemplateResponse
from openapi_client.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://localhost
# See configuration.py for a list of all supported configuration parameters.
configuration = openapi_client.Configuration(
    host = "http://localhost"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization (JWT): bearerAuth
configuration = openapi_client.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with openapi_client.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = openapi_client.AuthProfilesApi(api_client)
    provider_code = 'provider_code_example' # str | 

    try:
        # Get one provider setup template
        api_response = api_instance.get_provider_template(provider_code)
        print("The response of AuthProfilesApi->get_provider_template:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling AuthProfilesApi->get_provider_template: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **provider_code** | **str**|  | 

### Return type

[**ProviderTemplateResponse**](ProviderTemplateResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | Provider template returned |  -  |
**401** | Authentication missing or invalid |  -  |
**404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **list_provider_templates**
> List[ProviderTemplateResponse] list_provider_templates()

List built-in provider setup templates

Why this endpoint is needed:
Tenant onboarding is faster and less error-prone when common IdP providers have pre-defined setup templates.

What this endpoint does:
It returns template metadata for supported providers, including default scopes, required config fields,
and default claim mapping suggestions.

How this endpoint does it:
A template service returns static curated provider definitions consumed by UI and SDK onboarding flows.


### Example

* Bearer (JWT) Authentication (bearerAuth):

```python
import openapi_client
from openapi_client.models.provider_template_response import ProviderTemplateResponse
from openapi_client.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://localhost
# See configuration.py for a list of all supported configuration parameters.
configuration = openapi_client.Configuration(
    host = "http://localhost"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization (JWT): bearerAuth
configuration = openapi_client.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with openapi_client.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = openapi_client.AuthProfilesApi(api_client)

    try:
        # List built-in provider setup templates
        api_response = api_instance.list_provider_templates()
        print("The response of AuthProfilesApi->list_provider_templates:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling AuthProfilesApi->list_provider_templates: %s\n" % e)
```



### Parameters

This endpoint does not need any parameter.

### Return type

[**List[ProviderTemplateResponse]**](ProviderTemplateResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | Provider templates returned |  -  |
**401** | Authentication missing or invalid |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **rotate_auth_profile_key**
> AuthProfileResponse rotate_auth_profile_key(id)

Rotate auth profile key version

Why this endpoint is needed:
Signing key version rotation is required for security hygiene and incident response readiness.

What this endpoint does:
It increments the active key version for a tenant auth profile.

How this endpoint does it:
The service updates active key version, persists change, and creates an audit record describing the rotation action.


### Example

* Bearer (JWT) Authentication (bearerAuth):

```python
import openapi_client
from openapi_client.models.auth_profile_response import AuthProfileResponse
from openapi_client.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://localhost
# See configuration.py for a list of all supported configuration parameters.
configuration = openapi_client.Configuration(
    host = "http://localhost"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization (JWT): bearerAuth
configuration = openapi_client.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with openapi_client.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = openapi_client.AuthProfilesApi(api_client)
    id = UUID('38400000-8cf0-11bd-b23e-10b96e4ef00d') # UUID | 

    try:
        # Rotate auth profile key version
        api_response = api_instance.rotate_auth_profile_key(id)
        print("The response of AuthProfilesApi->rotate_auth_profile_key:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling AuthProfilesApi->rotate_auth_profile_key: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **UUID**|  | 

### Return type

[**AuthProfileResponse**](AuthProfileResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | Key version rotated |  -  |
**401** | Authentication missing or invalid |  -  |
**403** | Authenticated but not allowed by tenant/role/security policy |  -  |
**404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **update_auth_profile**
> AuthProfileResponse update_auth_profile(id, auth_profile_request)

Update tenant responder-auth profile

Why this endpoint is needed:
Identity provider details and claim models change over time; profile updates must be supported without rebuilding tenants.

What this endpoint does:
It updates auth profile trust settings, claim mappings, scope behavior, and fallback policies.

How this endpoint does it:
The service validates tenant access and mapping constraints, applies secure defaults where required,
persists profile changes, and records before/after snapshots in auth audit.


### Example

* Bearer (JWT) Authentication (bearerAuth):

```python
import openapi_client
from openapi_client.models.auth_profile_request import AuthProfileRequest
from openapi_client.models.auth_profile_response import AuthProfileResponse
from openapi_client.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://localhost
# See configuration.py for a list of all supported configuration parameters.
configuration = openapi_client.Configuration(
    host = "http://localhost"
)

# The client must configure the authentication and authorization parameters
# in accordance with the API server security policy.
# Examples for each auth method are provided below, use the example that
# satisfies your auth use case.

# Configure Bearer authorization (JWT): bearerAuth
configuration = openapi_client.Configuration(
    access_token = os.environ["BEARER_TOKEN"]
)

# Enter a context with an instance of the API client
with openapi_client.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = openapi_client.AuthProfilesApi(api_client)
    id = UUID('38400000-8cf0-11bd-b23e-10b96e4ef00d') # UUID | 
    auth_profile_request = openapi_client.AuthProfileRequest() # AuthProfileRequest | 

    try:
        # Update tenant responder-auth profile
        api_response = api_instance.update_auth_profile(id, auth_profile_request)
        print("The response of AuthProfilesApi->update_auth_profile:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling AuthProfilesApi->update_auth_profile: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **UUID**|  | 
 **auth_profile_request** | [**AuthProfileRequest**](AuthProfileRequest.md)|  | 

### Return type

[**AuthProfileResponse**](AuthProfileResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | Auth profile updated |  -  |
**400** | Request validation failed or business rule validation failed |  -  |
**401** | Authentication missing or invalid |  -  |
**403** | Authenticated but not allowed by tenant/role/security policy |  -  |
**404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **validate_responder_token**
> TokenValidationResult validate_responder_token(tenant_id, body=body)

Validate responder token against tenant auth policy

Why this endpoint is needed:
Integrations and runtime checks require an explicit token-validation endpoint that applies tenant-specific trust policy.

What this endpoint does:
It validates responder token input according to configured auth mode and claim-mapping rules,
then returns standardized success/failure payload.

How this endpoint does it:
Service loads tenant auth profile, routes validation by mode (public/signed/OIDC external trust),
applies cryptographic and claim checks, and returns TokenValidationResult.


### Example


```python
import openapi_client
from openapi_client.models.token_validation_result import TokenValidationResult
from openapi_client.rest import ApiException
from pprint import pprint

# Defining the host is optional and defaults to http://localhost
# See configuration.py for a list of all supported configuration parameters.
configuration = openapi_client.Configuration(
    host = "http://localhost"
)


# Enter a context with an instance of the API client
with openapi_client.ApiClient(configuration) as api_client:
    # Create an instance of the API class
    api_instance = openapi_client.AuthProfilesApi(api_client)
    tenant_id = 'tenant_id_example' # str | 
    body = 'body_example' # str |  (optional)

    try:
        # Validate responder token against tenant auth policy
        api_response = api_instance.validate_responder_token(tenant_id, body=body)
        print("The response of AuthProfilesApi->validate_responder_token:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling AuthProfilesApi->validate_responder_token: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **tenant_id** | **str**|  | 
 **body** | **str**|  | [optional] 

### Return type

[**TokenValidationResult**](TokenValidationResult.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: text/plain, application/json
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | Token validation result returned |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

