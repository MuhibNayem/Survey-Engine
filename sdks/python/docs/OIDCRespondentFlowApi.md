# openapi_client.OIDCRespondentFlowApi

All URIs are relative to *http://localhost*

Method | HTTP request | Description
------------- | ------------- | -------------
[**complete_respondent_oidc**](OIDCRespondentFlowApi.md#complete_respondent_oidc) | **GET** /api/v1/auth/respondent/oidc/callback | Complete OIDC callback and issue one-time responder access code
[**start_respondent_oidc**](OIDCRespondentFlowApi.md#start_respondent_oidc) | **POST** /api/v1/auth/respondent/oidc/start | Start private responder OIDC flow


# **complete_respondent_oidc**
> OidcCallbackResponse complete_respondent_oidc(state, code)

Complete OIDC callback and issue one-time responder access code

Why this endpoint is needed:
Authorization-code OIDC requires callback handling to exchange code, verify trust, and convert external auth into
internal responder access state.

What this endpoint does:
It validates callback state/code, exchanges token at IdP, validates token claims, and issues one-time responder access code.

How this endpoint does it:
OIDC state record is checked for expiry and one-time use, token exchange is executed via configured metadata,
token validation service verifies claims/mappings, responder access code row is persisted, and either JSON response
or redirect response is returned.


### Example


```python
import openapi_client
from openapi_client.models.oidc_callback_response import OidcCallbackResponse
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
    api_instance = openapi_client.OIDCRespondentFlowApi(api_client)
    state = 'state_example' # str | 
    code = 'code_example' # str | 

    try:
        # Complete OIDC callback and issue one-time responder access code
        api_response = api_instance.complete_respondent_oidc(state, code)
        print("The response of OIDCRespondentFlowApi->complete_respondent_oidc:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling OIDCRespondentFlowApi->complete_respondent_oidc: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **state** | **str**|  | 
 **code** | **str**|  | 

### Return type

[**OidcCallbackResponse**](OidcCallbackResponse.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | Callback processed and access code returned |  -  |
**302** | Redirect response emitted when returnPath was provided |  -  |
**403** | Authenticated but not allowed by tenant/role/security policy |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **start_respondent_oidc**
> OidcStartResponse start_respondent_oidc(oidc_start_request)

Start private responder OIDC flow

Why this endpoint is needed:
Private campaigns need a secure and tenant-aware way to initiate responder SSO without exposing platform-internal logic.

What this endpoint does:
It validates campaign + tenant eligibility for OIDC flow, creates one-time state/nonce context,
and returns IdP authorization URL details.

How this endpoint does it:
The OIDC service checks campaign auth mode and tenant auth profile mode, fetches OIDC metadata,
stores state with expiry, composes authorization URL with scopes/state/nonce, and returns OidcStartResponse.


### Example


```python
import openapi_client
from openapi_client.models.oidc_start_request import OidcStartRequest
from openapi_client.models.oidc_start_response import OidcStartResponse
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
    api_instance = openapi_client.OIDCRespondentFlowApi(api_client)
    oidc_start_request = openapi_client.OidcStartRequest() # OidcStartRequest | 

    try:
        # Start private responder OIDC flow
        api_response = api_instance.start_respondent_oidc(oidc_start_request)
        print("The response of OIDCRespondentFlowApi->start_respondent_oidc:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling OIDCRespondentFlowApi->start_respondent_oidc: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **oidc_start_request** | [**OidcStartRequest**](OidcStartRequest.md)|  | 

### Return type

[**OidcStartResponse**](OidcStartResponse.md)

### Authorization

No authorization required

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | OIDC authorization URL generated |  -  |
**400** | Request validation failed or business rule validation failed |  -  |
**403** | Authenticated but not allowed by tenant/role/security policy |  -  |
**404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

