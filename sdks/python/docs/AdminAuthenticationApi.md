# openapi_client.AdminAuthenticationApi

All URIs are relative to *http://localhost*

Method | HTTP request | Description
------------- | ------------- | -------------
[**get_csrf_token**](AdminAuthenticationApi.md#get_csrf_token) | **GET** /api/v1/admin/auth/csrf | Get CSRF token for browser cookie session mode
[**get_current_admin**](AdminAuthenticationApi.md#get_current_admin) | **GET** /api/v1/admin/auth/me | Retrieve current authenticated admin profile
[**login_admin**](AdminAuthenticationApi.md#login_admin) | **POST** /api/v1/admin/auth/login | Log in an existing admin and issue fresh auth cookies
[**login_admin_token_mode**](AdminAuthenticationApi.md#login_admin_token_mode) | **POST** /api/v1/admin/auth/token/login | Log in admin in headless token mode
[**logout_admin**](AdminAuthenticationApi.md#logout_admin) | **POST** /api/v1/admin/auth/logout | Clear admin auth cookies
[**refresh_admin_token**](AdminAuthenticationApi.md#refresh_admin_token) | **POST** /api/v1/admin/auth/refresh | Rotate refresh cookie and issue a new admin session
[**refresh_admin_token_mode**](AdminAuthenticationApi.md#refresh_admin_token_mode) | **POST** /api/v1/admin/auth/token/refresh | Refresh session in headless token mode
[**register_admin**](AdminAuthenticationApi.md#register_admin) | **POST** /api/v1/admin/auth/register | Register a tenant admin account and bootstrap tenant access
[**register_admin_token_mode**](AdminAuthenticationApi.md#register_admin_token_mode) | **POST** /api/v1/admin/auth/token/register | Register admin in headless token mode
[**revert_impersonation**](AdminAuthenticationApi.md#revert_impersonation) | **POST** /api/v1/admin/auth/revert-impersonation | Restore original super-admin cookies after impersonation


# **get_csrf_token**
> CsrfTokenResponse get_csrf_token()

Get CSRF token for browser cookie session mode

Why this endpoint is needed:
Browser-based admin sessions use HttpOnly auth cookies and require CSRF token submission for state-changing requests.

What this endpoint does:
It returns the current CSRF token value and ensures CSRF cookie state is initialized for the caller.

How this endpoint does it:
Spring Security creates/loads token via cookie token repository, controller returns it as a simple JSON payload.


### Example


```python
import openapi_client
from openapi_client.models.csrf_token_response import CsrfTokenResponse
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
    api_instance = openapi_client.AdminAuthenticationApi(api_client)

    try:
        # Get CSRF token for browser cookie session mode
        api_response = api_instance.get_csrf_token()
        print("The response of AdminAuthenticationApi->get_csrf_token:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling AdminAuthenticationApi->get_csrf_token: %s\n" % e)
```



### Parameters

This endpoint does not need any parameter.

### Return type

[**CsrfTokenResponse**](CsrfTokenResponse.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | CSRF token returned |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **get_current_admin**
> AuthUserResponse get_current_admin()

Retrieve current authenticated admin profile

Why this endpoint is needed:
Frontend needs a lightweight session-hydration endpoint after reload to confirm authentication and tenant context.

What this endpoint does:
It returns current authenticated user's profile details (no tokens).

How this endpoint does it:
JWT filter populates security/tenant context from cookie or bearer token, and controller maps context to response DTO.


### Example

* Bearer (JWT) Authentication (bearerAuth):

```python
import openapi_client
from openapi_client.models.auth_user_response import AuthUserResponse
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
    api_instance = openapi_client.AdminAuthenticationApi(api_client)

    try:
        # Retrieve current authenticated admin profile
        api_response = api_instance.get_current_admin()
        print("The response of AdminAuthenticationApi->get_current_admin:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling AdminAuthenticationApi->get_current_admin: %s\n" % e)
```



### Parameters

This endpoint does not need any parameter.

### Return type

[**AuthUserResponse**](AuthUserResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | Current admin profile returned |  -  |
**401** | Authentication missing or invalid |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **login_admin**
> AuthUserResponse login_admin(login_request)

Log in an existing admin and issue fresh auth cookies

Why this endpoint is needed:
Admin operators need a secure login path to start authenticated management sessions for tenant resources.

What this endpoint does:
It authenticates the admin using email/password, revokes prior refresh tokens for session hygiene, and issues
a fresh access token plus a new refresh token as HttpOnly cookies.

How this endpoint does it:
The service verifies account status and password hash, rotates refresh-token state to avoid stale session reuse,
and controller writes cookies while returning safe user context payload.


### Example


```python
import openapi_client
from openapi_client.models.auth_user_response import AuthUserResponse
from openapi_client.models.login_request import LoginRequest
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
    api_instance = openapi_client.AdminAuthenticationApi(api_client)
    login_request = openapi_client.LoginRequest() # LoginRequest | 

    try:
        # Log in an existing admin and issue fresh auth cookies
        api_response = api_instance.login_admin(login_request)
        print("The response of AdminAuthenticationApi->login_admin:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling AdminAuthenticationApi->login_admin: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **login_request** | [**LoginRequest**](LoginRequest.md)|  | 

### Return type

[**AuthUserResponse**](AuthUserResponse.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | Login successful and new auth cookies issued |  -  |
**400** | Request validation failed or business rule validation failed |  -  |
**404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **login_admin_token_mode**
> AuthResponse login_admin_token_mode(login_request)

Log in admin in headless token mode

Why this endpoint is needed:
API clients and external automation flows need explicit JWT credentials in response body.

What this endpoint does:
It authenticates admin credentials and returns fresh access/refresh tokens directly in payload.

How this endpoint does it:
Service validates credentials, rotates refresh-token state, and controller returns AuthResponse DTO.


### Example


```python
import openapi_client
from openapi_client.models.auth_response import AuthResponse
from openapi_client.models.login_request import LoginRequest
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
    api_instance = openapi_client.AdminAuthenticationApi(api_client)
    login_request = openapi_client.LoginRequest() # LoginRequest | 

    try:
        # Log in admin in headless token mode
        api_response = api_instance.login_admin_token_mode(login_request)
        print("The response of AdminAuthenticationApi->login_admin_token_mode:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling AdminAuthenticationApi->login_admin_token_mode: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **login_request** | [**LoginRequest**](LoginRequest.md)|  | 

### Return type

[**AuthResponse**](AuthResponse.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | Login successful and token payload returned |  -  |
**400** | Request validation failed or business rule validation failed |  -  |
**404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **logout_admin**
> logout_admin()

Clear admin auth cookies

Why this endpoint is needed:
Sessions must support explicit and secure sign-out to prevent lingering browser-side authentication state.

What this endpoint does:
It clears access_token and refresh_token cookies.

How this endpoint does it:
Controller writes both token cookies with maxAge=0, path=/, HttpOnly/Secure attributes.


### Example


```python
import openapi_client
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
    api_instance = openapi_client.AdminAuthenticationApi(api_client)

    try:
        # Clear admin auth cookies
        api_instance.logout_admin()
    except Exception as e:
        print("Exception when calling AdminAuthenticationApi->logout_admin: %s\n" % e)
```



### Parameters

This endpoint does not need any parameter.

### Return type

void (empty response body)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**204** | Logout completed and cookies cleared |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **refresh_admin_token**
> AuthUserResponse refresh_admin_token()

Rotate refresh cookie and issue a new admin session

Why this endpoint is needed:
Access tokens are intentionally short-lived. A secure refresh mechanism is required to keep admins signed in
without repeatedly collecting credentials.

What this endpoint does:
It reads refresh token from HttpOnly cookie, validates it, rotates token state, and sets fresh access/refresh cookies.

How this endpoint does it:
Controller extracts refresh_token cookie, service validates persistence state (including expiry/revocation),
applies one-time-use rotation semantics, and controller rewrites secure cookies.


### Example


```python
import openapi_client
from openapi_client.models.auth_user_response import AuthUserResponse
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
    api_instance = openapi_client.AdminAuthenticationApi(api_client)

    try:
        # Rotate refresh cookie and issue a new admin session
        api_response = api_instance.refresh_admin_token()
        print("The response of AdminAuthenticationApi->refresh_admin_token:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling AdminAuthenticationApi->refresh_admin_token: %s\n" % e)
```



### Parameters

This endpoint does not need any parameter.

### Return type

[**AuthUserResponse**](AuthUserResponse.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | Cookie rotation successful |  -  |
**401** | Authentication missing or invalid |  -  |
**404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **refresh_admin_token_mode**
> AuthResponse refresh_admin_token_mode(refresh_token_request)

Refresh session in headless token mode

Why this endpoint is needed:
Headless integrations need refresh-token rotation without cookie transport.

What this endpoint does:
It validates provided refresh token and returns a rotated access/refresh token pair.

How this endpoint does it:
Controller accepts RefreshTokenRequest payload and delegates to refresh-token rotation service.


### Example


```python
import openapi_client
from openapi_client.models.auth_response import AuthResponse
from openapi_client.models.refresh_token_request import RefreshTokenRequest
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
    api_instance = openapi_client.AdminAuthenticationApi(api_client)
    refresh_token_request = openapi_client.RefreshTokenRequest() # RefreshTokenRequest | 

    try:
        # Refresh session in headless token mode
        api_response = api_instance.refresh_admin_token_mode(refresh_token_request)
        print("The response of AdminAuthenticationApi->refresh_admin_token_mode:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling AdminAuthenticationApi->refresh_admin_token_mode: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **refresh_token_request** | [**RefreshTokenRequest**](RefreshTokenRequest.md)|  | 

### Return type

[**AuthResponse**](AuthResponse.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | Token rotation successful |  -  |
**400** | Request validation failed or business rule validation failed |  -  |
**401** | Authentication missing or invalid |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **register_admin**
> AuthUserResponse register_admin(register_request)

Register a tenant admin account and bootstrap tenant access

Why this endpoint is needed:
A brand-new subscriber needs a secure, first-class admin identity to operate the platform. Without this endpoint,
onboarding would require manual platform-team intervention and would break self-service SaaS adoption.

What this endpoint does:
It creates the first admin user for a tenant, ensures tenant provisioning exists, ensures a trial subscription exists,
sets access/refresh tokens as HttpOnly cookies, and returns safe user context in response body.

How this endpoint does it:
The service validates uniqueness of email, provisions tenant records when necessary, enforces admin-user quota policy,
stores password hash securely, issues an engine-signed JWT access token, creates a refresh token row, and controller
writes both tokens into secure cookies before returning AuthUserResponse.


### Example


```python
import openapi_client
from openapi_client.models.auth_user_response import AuthUserResponse
from openapi_client.models.register_request import RegisterRequest
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
    api_instance = openapi_client.AdminAuthenticationApi(api_client)
    register_request = openapi_client.RegisterRequest() # RegisterRequest | 

    try:
        # Register a tenant admin account and bootstrap tenant access
        api_response = api_instance.register_admin(register_request)
        print("The response of AdminAuthenticationApi->register_admin:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling AdminAuthenticationApi->register_admin: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **register_request** | [**RegisterRequest**](RegisterRequest.md)|  | 

### Return type

[**AuthUserResponse**](AuthUserResponse.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**201** | Admin account created and authenticated session returned |  -  |
**400** | Request validation failed or business rule validation failed |  -  |
**409** | Requested action conflicts with current resource state |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **register_admin_token_mode**
> AuthResponse register_admin_token_mode(register_request)

Register admin in headless token mode

Why this endpoint is needed:
Headless/API clients cannot reliably consume browser cookie flows and need explicit token payloads.

What this endpoint does:
It performs the same registration flow as cookie mode but returns access/refresh tokens directly in response body.

How this endpoint does it:
Service runs standard tenant bootstrap/registration pipeline and controller returns AuthResponse without writing cookies.


### Example


```python
import openapi_client
from openapi_client.models.auth_response import AuthResponse
from openapi_client.models.register_request import RegisterRequest
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
    api_instance = openapi_client.AdminAuthenticationApi(api_client)
    register_request = openapi_client.RegisterRequest() # RegisterRequest | 

    try:
        # Register admin in headless token mode
        api_response = api_instance.register_admin_token_mode(register_request)
        print("The response of AdminAuthenticationApi->register_admin_token_mode:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling AdminAuthenticationApi->register_admin_token_mode: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **register_request** | [**RegisterRequest**](RegisterRequest.md)|  | 

### Return type

[**AuthResponse**](AuthResponse.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**201** | Admin registered and token payload returned |  -  |
**400** | Request validation failed or business rule validation failed |  -  |
**409** | Requested action conflicts with current resource state |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **revert_impersonation**
> revert_impersonation()

Restore original super-admin cookies after impersonation

Why this endpoint is needed:
Super-admin support sessions must safely return from tenant impersonation to original privileged context.

What this endpoint does:
It restores stashed super-admin auth cookies and clears temporary impersonation cookies.

How this endpoint does it:
Cookie utility reads stashed `sa_*` cookies, rewrites primary auth cookies, and clears impersonation artifacts.


### Example


```python
import openapi_client
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
    api_instance = openapi_client.AdminAuthenticationApi(api_client)

    try:
        # Restore original super-admin cookies after impersonation
        api_instance.revert_impersonation()
    except Exception as e:
        print("Exception when calling AdminAuthenticationApi->revert_impersonation: %s\n" % e)
```



### Parameters

This endpoint does not need any parameter.

### Return type

void (empty response body)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | Impersonation context reverted |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

