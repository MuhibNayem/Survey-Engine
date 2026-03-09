# OIDCRespondentFlowApi

All URIs are relative to *http://localhost*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**completeRespondentOidc**](OIDCRespondentFlowApi.md#completerespondentoidc) | **GET** /api/v1/auth/respondent/oidc/callback | Complete OIDC callback and issue one-time responder access code |
| [**startRespondentOidc**](OIDCRespondentFlowApi.md#startrespondentoidc) | **POST** /api/v1/auth/respondent/oidc/start | Start private responder OIDC flow |



## completeRespondentOidc

> OidcCallbackResponse completeRespondentOidc(state, code)

Complete OIDC callback and issue one-time responder access code

Why this endpoint is needed: Authorization-code OIDC requires callback handling to exchange code, verify trust, and convert external auth into internal responder access state.  What this endpoint does: It validates callback state/code, exchanges token at IdP, validates token claims, and issues one-time responder access code.  How this endpoint does it: OIDC state record is checked for expiry and one-time use, token exchange is executed via configured metadata, token validation service verifies claims/mappings, responder access code row is persisted, and either JSON response or redirect response is returned. 

### Example

```ts
import {
  Configuration,
  OIDCRespondentFlowApi,
} from '@survey-engine/sdk';
import type { CompleteRespondentOidcRequest } from '@survey-engine/sdk';

async function example() {
  console.log("🚀 Testing @survey-engine/sdk SDK...");
  const api = new OIDCRespondentFlowApi();

  const body = {
    // string
    state: state_example,
    // string
    code: code_example,
  } satisfies CompleteRespondentOidcRequest;

  try {
    const data = await api.completeRespondentOidc(body);
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
| **state** | `string` |  | [Defaults to `undefined`] |
| **code** | `string` |  | [Defaults to `undefined`] |

### Return type

[**OidcCallbackResponse**](OidcCallbackResponse.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Callback processed and access code returned |  -  |
| **302** | Redirect response emitted when returnPath was provided |  -  |
| **403** | Authenticated but not allowed by tenant/role/security policy |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## startRespondentOidc

> OidcStartResponse startRespondentOidc(oidcStartRequest)

Start private responder OIDC flow

Why this endpoint is needed: Private campaigns need a secure and tenant-aware way to initiate responder SSO without exposing platform-internal logic.  What this endpoint does: It validates campaign + tenant eligibility for OIDC flow, creates one-time state/nonce context, and returns IdP authorization URL details.  How this endpoint does it: The OIDC service checks campaign auth mode and tenant auth profile mode, fetches OIDC metadata, stores state with expiry, composes authorization URL with scopes/state/nonce, and returns OidcStartResponse. 

### Example

```ts
import {
  Configuration,
  OIDCRespondentFlowApi,
} from '@survey-engine/sdk';
import type { StartRespondentOidcRequest } from '@survey-engine/sdk';

async function example() {
  console.log("🚀 Testing @survey-engine/sdk SDK...");
  const api = new OIDCRespondentFlowApi();

  const body = {
    // OidcStartRequest
    oidcStartRequest: ...,
  } satisfies StartRespondentOidcRequest;

  try {
    const data = await api.startRespondentOidc(body);
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
| **oidcStartRequest** | [OidcStartRequest](OidcStartRequest.md) |  | |

### Return type

[**OidcStartResponse**](OidcStartResponse.md)

### Authorization

No authorization required

### HTTP request headers

- **Content-Type**: `application/json`
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | OIDC authorization URL generated |  -  |
| **400** | Request validation failed or business rule validation failed |  -  |
| **403** | Authenticated but not allowed by tenant/role/security policy |  -  |
| **404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)

