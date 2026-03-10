# openapi_client.UserFeaturesApi

All URIs are relative to *http://localhost*

Method | HTTP request | Description
------------- | ------------- | -------------
[**complete_feature**](UserFeaturesApi.md#complete_feature) | **POST** /api/v1/features/{featureKey}/complete | Mark a feature as completed
[**get_available_features**](UserFeaturesApi.md#get_available_features) | **GET** /api/v1/features/available | Get features available to current user
[**get_feature_status**](UserFeaturesApi.md#get_feature_status) | **GET** /api/v1/features/{featureKey}/status | Get feature status for current user


# **complete_feature**
> complete_feature(feature_key, complete_feature_request=complete_feature_request)

Mark a feature as completed

Why this endpoint is needed:
Users need to record completion of tours, tooltips, or feature interactions for progress tracking.


### Example

* Bearer (JWT) Authentication (bearerAuth):

```python
import openapi_client
from openapi_client.models.complete_feature_request import CompleteFeatureRequest
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
    api_instance = openapi_client.UserFeaturesApi(api_client)
    feature_key = 'feature_key_example' # str | 
    complete_feature_request = openapi_client.CompleteFeatureRequest() # CompleteFeatureRequest |  (optional)

    try:
        # Mark a feature as completed
        api_instance.complete_feature(feature_key, complete_feature_request=complete_feature_request)
    except Exception as e:
        print("Exception when calling UserFeaturesApi->complete_feature: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **feature_key** | **str**|  | 
 **complete_feature_request** | [**CompleteFeatureRequest**](CompleteFeatureRequest.md)|  | [optional] 

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
**200** | Feature marked as completed |  -  |
**404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **get_available_features**
> List[FeatureStatusDTO] get_available_features(category=category)

Get features available to current user

Why this endpoint is needed:
Frontend applications need to know which features are available and incomplete for the logged-in user.


### Example

* Bearer (JWT) Authentication (bearerAuth):

```python
import openapi_client
from openapi_client.models.feature_category import FeatureCategory
from openapi_client.models.feature_status_dto import FeatureStatusDTO
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
    api_instance = openapi_client.UserFeaturesApi(api_client)
    category = openapi_client.FeatureCategory() # FeatureCategory |  (optional)

    try:
        # Get features available to current user
        api_response = api_instance.get_available_features(category=category)
        print("The response of UserFeaturesApi->get_available_features:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling UserFeaturesApi->get_available_features: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **category** | [**FeatureCategory**](.md)|  | [optional] 

### Return type

[**List[FeatureStatusDTO]**](FeatureStatusDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | List of available features |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **get_feature_status**
> FeatureStatusDTO get_feature_status(feature_key)

Get feature status for current user

Why this endpoint is needed:
Frontend needs to check if a specific feature has been completed by the current user.


### Example

* Bearer (JWT) Authentication (bearerAuth):

```python
import openapi_client
from openapi_client.models.feature_status_dto import FeatureStatusDTO
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
    api_instance = openapi_client.UserFeaturesApi(api_client)
    feature_key = 'feature_key_example' # str | 

    try:
        # Get feature status for current user
        api_response = api_instance.get_feature_status(feature_key)
        print("The response of UserFeaturesApi->get_feature_status:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling UserFeaturesApi->get_feature_status: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **feature_key** | **str**|  | 

### Return type

[**FeatureStatusDTO**](FeatureStatusDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | Feature status |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

