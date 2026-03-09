# AdminAuthenticationApi

All URIs are relative to *http://localhost*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**getCsrfToken**](AdminAuthenticationApi.md#getCsrfToken) | **GET** /api/v1/admin/auth/csrf | Get CSRF token for browser cookie session mode |
| [**getCurrentAdmin**](AdminAuthenticationApi.md#getCurrentAdmin) | **GET** /api/v1/admin/auth/me | Retrieve current authenticated admin profile |
| [**loginAdmin**](AdminAuthenticationApi.md#loginAdmin) | **POST** /api/v1/admin/auth/login | Log in an existing admin and issue fresh auth cookies |
| [**loginAdminTokenMode**](AdminAuthenticationApi.md#loginAdminTokenMode) | **POST** /api/v1/admin/auth/token/login | Log in admin in headless token mode |
| [**logoutAdmin**](AdminAuthenticationApi.md#logoutAdmin) | **POST** /api/v1/admin/auth/logout | Clear admin auth cookies |
| [**refreshAdminToken**](AdminAuthenticationApi.md#refreshAdminToken) | **POST** /api/v1/admin/auth/refresh | Rotate refresh cookie and issue a new admin session |
| [**refreshAdminTokenMode**](AdminAuthenticationApi.md#refreshAdminTokenMode) | **POST** /api/v1/admin/auth/token/refresh | Refresh session in headless token mode |
| [**registerAdmin**](AdminAuthenticationApi.md#registerAdmin) | **POST** /api/v1/admin/auth/register | Register a tenant admin account and bootstrap tenant access |
| [**registerAdminTokenMode**](AdminAuthenticationApi.md#registerAdminTokenMode) | **POST** /api/v1/admin/auth/token/register | Register admin in headless token mode |
| [**revertImpersonation**](AdminAuthenticationApi.md#revertImpersonation) | **POST** /api/v1/admin/auth/revert-impersonation | Restore original super-admin cookies after impersonation |


<a id="getCsrfToken"></a>
# **getCsrfToken**
> CsrfTokenResponse getCsrfToken()

Get CSRF token for browser cookie session mode

Why this endpoint is needed: Browser-based admin sessions use HttpOnly auth cookies and require CSRF token submission for state-changing requests.  What this endpoint does: It returns the current CSRF token value and ensures CSRF cookie state is initialized for the caller.  How this endpoint does it: Spring Security creates/loads token via cookie token repository, controller returns it as a simple JSON payload. 

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.models.*;
import org.openapitools.client.api.AdminAuthenticationApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");

    AdminAuthenticationApi apiInstance = new AdminAuthenticationApi(defaultClient);
    try {
      CsrfTokenResponse result = apiInstance.getCsrfToken();
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling AdminAuthenticationApi#getCsrfToken");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
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
| **200** | CSRF token returned |  -  |

<a id="getCurrentAdmin"></a>
# **getCurrentAdmin**
> AuthUserResponse getCurrentAdmin()

Retrieve current authenticated admin profile

Why this endpoint is needed: Frontend needs a lightweight session-hydration endpoint after reload to confirm authentication and tenant context.  What this endpoint does: It returns current authenticated user&#39;s profile details (no tokens).  How this endpoint does it: JWT filter populates security/tenant context from cookie or bearer token, and controller maps context to response DTO. 

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.AdminAuthenticationApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    AdminAuthenticationApi apiInstance = new AdminAuthenticationApi(defaultClient);
    try {
      AuthUserResponse result = apiInstance.getCurrentAdmin();
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling AdminAuthenticationApi#getCurrentAdmin");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
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
| **200** | Current admin profile returned |  -  |
| **401** | Authentication missing or invalid |  -  |

<a id="loginAdmin"></a>
# **loginAdmin**
> AuthUserResponse loginAdmin(loginRequest)

Log in an existing admin and issue fresh auth cookies

Why this endpoint is needed: Admin operators need a secure login path to start authenticated management sessions for tenant resources.  What this endpoint does: It authenticates the admin using email/password, revokes prior refresh tokens for session hygiene, and issues a fresh access token plus a new refresh token as HttpOnly cookies.  How this endpoint does it: The service verifies account status and password hash, rotates refresh-token state to avoid stale session reuse, and controller writes cookies while returning safe user context payload. 

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.models.*;
import org.openapitools.client.api.AdminAuthenticationApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");

    AdminAuthenticationApi apiInstance = new AdminAuthenticationApi(defaultClient);
    LoginRequest loginRequest = new LoginRequest(); // LoginRequest | 
    try {
      AuthUserResponse result = apiInstance.loginAdmin(loginRequest);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling AdminAuthenticationApi#loginAdmin");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **loginRequest** | [**LoginRequest**](LoginRequest.md)|  | |

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
| **200** | Login successful and new auth cookies issued |  -  |
| **400** | Request validation failed or business rule validation failed |  -  |
| **404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

<a id="loginAdminTokenMode"></a>
# **loginAdminTokenMode**
> AuthResponse loginAdminTokenMode(loginRequest)

Log in admin in headless token mode

Why this endpoint is needed: API clients and external automation flows need explicit JWT credentials in response body.  What this endpoint does: It authenticates admin credentials and returns fresh access/refresh tokens directly in payload.  How this endpoint does it: Service validates credentials, rotates refresh-token state, and controller returns AuthResponse DTO. 

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.models.*;
import org.openapitools.client.api.AdminAuthenticationApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");

    AdminAuthenticationApi apiInstance = new AdminAuthenticationApi(defaultClient);
    LoginRequest loginRequest = new LoginRequest(); // LoginRequest | 
    try {
      AuthResponse result = apiInstance.loginAdminTokenMode(loginRequest);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling AdminAuthenticationApi#loginAdminTokenMode");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **loginRequest** | [**LoginRequest**](LoginRequest.md)|  | |

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
| **200** | Login successful and token payload returned |  -  |
| **400** | Request validation failed or business rule validation failed |  -  |
| **404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

<a id="logoutAdmin"></a>
# **logoutAdmin**
> logoutAdmin()

Clear admin auth cookies

Why this endpoint is needed: Sessions must support explicit and secure sign-out to prevent lingering browser-side authentication state.  What this endpoint does: It clears access_token and refresh_token cookies.  How this endpoint does it: Controller writes both token cookies with maxAge&#x3D;0, path&#x3D;/, HttpOnly/Secure attributes. 

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.models.*;
import org.openapitools.client.api.AdminAuthenticationApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");

    AdminAuthenticationApi apiInstance = new AdminAuthenticationApi(defaultClient);
    try {
      apiInstance.logoutAdmin();
    } catch (ApiException e) {
      System.err.println("Exception when calling AdminAuthenticationApi#logoutAdmin");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters
This endpoint does not need any parameter.

### Return type

null (empty response body)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **204** | Logout completed and cookies cleared |  -  |

<a id="refreshAdminToken"></a>
# **refreshAdminToken**
> AuthUserResponse refreshAdminToken()

Rotate refresh cookie and issue a new admin session

Why this endpoint is needed: Access tokens are intentionally short-lived. A secure refresh mechanism is required to keep admins signed in without repeatedly collecting credentials.  What this endpoint does: It reads refresh token from HttpOnly cookie, validates it, rotates token state, and sets fresh access/refresh cookies.  How this endpoint does it: Controller extracts refresh_token cookie, service validates persistence state (including expiry/revocation), applies one-time-use rotation semantics, and controller rewrites secure cookies. 

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.models.*;
import org.openapitools.client.api.AdminAuthenticationApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");

    AdminAuthenticationApi apiInstance = new AdminAuthenticationApi(defaultClient);
    try {
      AuthUserResponse result = apiInstance.refreshAdminToken();
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling AdminAuthenticationApi#refreshAdminToken");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
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
| **200** | Cookie rotation successful |  -  |
| **401** | Authentication missing or invalid |  -  |
| **404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

<a id="refreshAdminTokenMode"></a>
# **refreshAdminTokenMode**
> AuthResponse refreshAdminTokenMode(refreshTokenRequest)

Refresh session in headless token mode

Why this endpoint is needed: Headless integrations need refresh-token rotation without cookie transport.  What this endpoint does: It validates provided refresh token and returns a rotated access/refresh token pair.  How this endpoint does it: Controller accepts RefreshTokenRequest payload and delegates to refresh-token rotation service. 

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.models.*;
import org.openapitools.client.api.AdminAuthenticationApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");

    AdminAuthenticationApi apiInstance = new AdminAuthenticationApi(defaultClient);
    RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest(); // RefreshTokenRequest | 
    try {
      AuthResponse result = apiInstance.refreshAdminTokenMode(refreshTokenRequest);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling AdminAuthenticationApi#refreshAdminTokenMode");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **refreshTokenRequest** | [**RefreshTokenRequest**](RefreshTokenRequest.md)|  | |

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
| **200** | Token rotation successful |  -  |
| **400** | Request validation failed or business rule validation failed |  -  |
| **401** | Authentication missing or invalid |  -  |

<a id="registerAdmin"></a>
# **registerAdmin**
> AuthUserResponse registerAdmin(registerRequest)

Register a tenant admin account and bootstrap tenant access

Why this endpoint is needed: A brand-new subscriber needs a secure, first-class admin identity to operate the platform. Without this endpoint, onboarding would require manual platform-team intervention and would break self-service SaaS adoption.  What this endpoint does: It creates the first admin user for a tenant, ensures tenant provisioning exists, ensures a trial subscription exists, sets access/refresh tokens as HttpOnly cookies, and returns safe user context in response body.  How this endpoint does it: The service validates uniqueness of email, provisions tenant records when necessary, enforces admin-user quota policy, stores password hash securely, issues an engine-signed JWT access token, creates a refresh token row, and controller writes both tokens into secure cookies before returning AuthUserResponse. 

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.models.*;
import org.openapitools.client.api.AdminAuthenticationApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");

    AdminAuthenticationApi apiInstance = new AdminAuthenticationApi(defaultClient);
    RegisterRequest registerRequest = new RegisterRequest(); // RegisterRequest | 
    try {
      AuthUserResponse result = apiInstance.registerAdmin(registerRequest);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling AdminAuthenticationApi#registerAdmin");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **registerRequest** | [**RegisterRequest**](RegisterRequest.md)|  | |

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
| **201** | Admin account created and authenticated session returned |  -  |
| **400** | Request validation failed or business rule validation failed |  -  |
| **409** | Requested action conflicts with current resource state |  -  |

<a id="registerAdminTokenMode"></a>
# **registerAdminTokenMode**
> AuthResponse registerAdminTokenMode(registerRequest)

Register admin in headless token mode

Why this endpoint is needed: Headless/API clients cannot reliably consume browser cookie flows and need explicit token payloads.  What this endpoint does: It performs the same registration flow as cookie mode but returns access/refresh tokens directly in response body.  How this endpoint does it: Service runs standard tenant bootstrap/registration pipeline and controller returns AuthResponse without writing cookies. 

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.models.*;
import org.openapitools.client.api.AdminAuthenticationApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");

    AdminAuthenticationApi apiInstance = new AdminAuthenticationApi(defaultClient);
    RegisterRequest registerRequest = new RegisterRequest(); // RegisterRequest | 
    try {
      AuthResponse result = apiInstance.registerAdminTokenMode(registerRequest);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling AdminAuthenticationApi#registerAdminTokenMode");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **registerRequest** | [**RegisterRequest**](RegisterRequest.md)|  | |

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
| **201** | Admin registered and token payload returned |  -  |
| **400** | Request validation failed or business rule validation failed |  -  |
| **409** | Requested action conflicts with current resource state |  -  |

<a id="revertImpersonation"></a>
# **revertImpersonation**
> revertImpersonation()

Restore original super-admin cookies after impersonation

Why this endpoint is needed: Super-admin support sessions must safely return from tenant impersonation to original privileged context.  What this endpoint does: It restores stashed super-admin auth cookies and clears temporary impersonation cookies.  How this endpoint does it: Cookie utility reads stashed &#x60;sa_*&#x60; cookies, rewrites primary auth cookies, and clears impersonation artifacts. 

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.models.*;
import org.openapitools.client.api.AdminAuthenticationApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");

    AdminAuthenticationApi apiInstance = new AdminAuthenticationApi(defaultClient);
    try {
      apiInstance.revertImpersonation();
    } catch (ApiException e) {
      System.err.println("Exception when calling AdminAuthenticationApi#revertImpersonation");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters
This endpoint does not need any parameter.

### Return type

null (empty response body)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: Not defined

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Impersonation context reverted |  -  |

