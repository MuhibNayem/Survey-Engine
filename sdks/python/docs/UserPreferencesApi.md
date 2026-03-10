# openapi_client.UserPreferencesApi

All URIs are relative to *http://localhost*

Method | HTTP request | Description
------------- | ------------- | -------------
[**get_feature_completion_status**](UserPreferencesApi.md#get_feature_completion_status) | **GET** /api/v1/admin/preferences/{featureKey}/completed | Check whether a feature or onboarding item is completed
[**get_user_preferences**](UserPreferencesApi.md#get_user_preferences) | **GET** /api/v1/admin/preferences | Get all preferences for the current admin user
[**reset_user_preferences**](UserPreferencesApi.md#reset_user_preferences) | **DELETE** /api/v1/admin/preferences | Reset all preferences for the current admin user
[**set_feature_completion_status**](UserPreferencesApi.md#set_feature_completion_status) | **POST** /api/v1/admin/preferences/{featureKey}/complete | Mark a feature or onboarding item as completed or incomplete
[**set_user_preference**](UserPreferencesApi.md#set_user_preference) | **PATCH** /api/v1/admin/preferences/{key} | Update one preference key for the current admin user
[**update_user_preferences**](UserPreferencesApi.md#update_user_preferences) | **PATCH** /api/v1/admin/preferences | Update multiple preferences for the current admin user


# **get_feature_completion_status**
> GetFeatureCompletionStatus200Response get_feature_completion_status(feature_key)

Check whether a feature or onboarding item is completed

Why this endpoint is needed:
Onboarding and contextual-help UX need a direct completion check for a single feature key.

What this endpoint does:
It returns whether the current admin has marked the target feature as completed.

How this endpoint does it:
The service resolves the current admin user and reads the completion flag from stored preferences.


### Example

* Bearer (JWT) Authentication (bearerAuth):

```python
import openapi_client
from openapi_client.models.get_feature_completion_status200_response import GetFeatureCompletionStatus200Response
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
    api_instance = openapi_client.UserPreferencesApi(api_client)
    feature_key = 'feature_key_example' # str | 

    try:
        # Check whether a feature or onboarding item is completed
        api_response = api_instance.get_feature_completion_status(feature_key)
        print("The response of UserPreferencesApi->get_feature_completion_status:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling UserPreferencesApi->get_feature_completion_status: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **feature_key** | **str**|  | 

### Return type

[**GetFeatureCompletionStatus200Response**](GetFeatureCompletionStatus200Response.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | Completion state returned |  -  |
**401** | Authentication missing or invalid |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **get_user_preferences**
> UserPreferenceDTO get_user_preferences()

Get all preferences for the current admin user

Why this endpoint is needed:
The admin application persists per-user UI state such as theme mode, completed onboarding flows, and dismissed hints.

What this endpoint does:
It returns the preference map for the currently authenticated admin.

How this endpoint does it:
The service resolves the current admin from auth context, loads the tenant-scoped preference record, and maps it to UserPreferenceDTO.


### Example

* Bearer (JWT) Authentication (bearerAuth):

```python
import openapi_client
from openapi_client.models.user_preference_dto import UserPreferenceDTO
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
    api_instance = openapi_client.UserPreferencesApi(api_client)

    try:
        # Get all preferences for the current admin user
        api_response = api_instance.get_user_preferences()
        print("The response of UserPreferencesApi->get_user_preferences:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling UserPreferencesApi->get_user_preferences: %s\n" % e)
```



### Parameters

This endpoint does not need any parameter.

### Return type

[**UserPreferenceDTO**](UserPreferenceDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | Current user preferences returned |  -  |
**401** | Authentication missing or invalid |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **reset_user_preferences**
> reset_user_preferences()

Reset all preferences for the current admin user

Why this endpoint is needed:
Support and onboarding workflows need a clean reset path so tours, theme defaults, and dismissed tips can be replayed.

What this endpoint does:
It clears all stored preferences for the current admin user.

How this endpoint does it:
The service deletes the current admin preference map and returns success without a body.


### Example

* Bearer (JWT) Authentication (bearerAuth):

```python
import openapi_client
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
    api_instance = openapi_client.UserPreferencesApi(api_client)

    try:
        # Reset all preferences for the current admin user
        api_instance.reset_user_preferences()
    except Exception as e:
        print("Exception when calling UserPreferencesApi->reset_user_preferences: %s\n" % e)
```



### Parameters

This endpoint does not need any parameter.

### Return type

void (empty response body)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | Preferences reset |  -  |
**401** | Authentication missing or invalid |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **set_feature_completion_status**
> set_feature_completion_status(feature_key, completed=completed)

Mark a feature or onboarding item as completed or incomplete

Why this endpoint is needed:
Guided tours and feature education flows need an explicit server-side completion write path that survives browsers and devices.

What this endpoint does:
It stores the completion status for one feature key for the current admin user.

How this endpoint does it:
The service updates the completion map in user preferences and returns success without a body.


### Example

* Bearer (JWT) Authentication (bearerAuth):

```python
import openapi_client
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
    api_instance = openapi_client.UserPreferencesApi(api_client)
    feature_key = 'feature_key_example' # str | 
    completed = True # bool |  (optional) (default to True)

    try:
        # Mark a feature or onboarding item as completed or incomplete
        api_instance.set_feature_completion_status(feature_key, completed=completed)
    except Exception as e:
        print("Exception when calling UserPreferencesApi->set_feature_completion_status: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **feature_key** | **str**|  | 
 **completed** | **bool**|  | [optional] [default to True]

### Return type

void (empty response body)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | Completion state updated |  -  |
**401** | Authentication missing or invalid |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **set_user_preference**
> set_user_preference(key, body)

Update one preference key for the current admin user

Why this endpoint is needed:
Lightweight UI interactions such as theme changes and dismiss actions often update only one preference key.

What this endpoint does:
It sets one preference value for the current admin user.

How this endpoint does it:
The controller accepts a raw string body and the service stores it under the requested key.


### Example

* Bearer (JWT) Authentication (bearerAuth):

```python
import openapi_client
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
    api_instance = openapi_client.UserPreferencesApi(api_client)
    key = 'key_example' # str | 
    body = 'body_example' # str | 

    try:
        # Update one preference key for the current admin user
        api_instance.set_user_preference(key, body)
    except Exception as e:
        print("Exception when calling UserPreferencesApi->set_user_preference: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **key** | **str**|  | 
 **body** | **str**|  | 

### Return type

void (empty response body)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | Preference updated |  -  |
**401** | Authentication missing or invalid |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **update_user_preferences**
> update_user_preferences(request_body)

Update multiple preferences for the current admin user

Why this endpoint is needed:
Modern UI flows often save several preference keys together, such as theme mode and onboarding completion checkpoints.

What this endpoint does:
It updates multiple string-based preference values in a single call.

How this endpoint does it:
The service merges the incoming key-value map into the current admin user's preference store and persists the result.


### Example

* Bearer (JWT) Authentication (bearerAuth):

```python
import openapi_client
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
    api_instance = openapi_client.UserPreferencesApi(api_client)
    request_body = {'key': 'request_body_example'} # Dict[str, str] | 

    try:
        # Update multiple preferences for the current admin user
        api_instance.update_user_preferences(request_body)
    except Exception as e:
        print("Exception when calling UserPreferencesApi->update_user_preferences: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **request_body** | [**Dict[str, str]**](str.md)|  | 

### Return type

void (empty response body)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | Preferences updated |  -  |
**401** | Authentication missing or invalid |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

