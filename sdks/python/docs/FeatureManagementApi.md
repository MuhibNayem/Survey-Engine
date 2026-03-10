# openapi_client.FeatureManagementApi

All URIs are relative to *http://localhost*

Method | HTTP request | Description
------------- | ------------- | -------------
[**configure_feature_for_tenant**](FeatureManagementApi.md#configure_feature_for_tenant) | **POST** /api/v1/admin/features/{featureKey}/tenants/{tenantId}/configure | Configure feature for a specific tenant
[**create_feature**](FeatureManagementApi.md#create_feature) | **POST** /api/v1/admin/features | Create a new feature definition
[**delete_feature**](FeatureManagementApi.md#delete_feature) | **DELETE** /api/v1/admin/features/{featureKey} | Delete a feature definition
[**get_feature_analytics**](FeatureManagementApi.md#get_feature_analytics) | **GET** /api/v1/admin/features/{featureKey}/analytics | Get usage analytics for a feature
[**list_features**](FeatureManagementApi.md#list_features) | **GET** /api/v1/admin/features | List all feature definitions
[**update_feature**](FeatureManagementApi.md#update_feature) | **PUT** /api/v1/admin/features/{featureKey} | Update an existing feature definition


# **configure_feature_for_tenant**
> configure_feature_for_tenant(feature_key, tenant_id, tenant_feature_config_dto)

Configure feature for a specific tenant

Why this endpoint is needed:
Enterprise tenants may need to enable/disable features or customize behavior for their user base.


### Example

* Bearer (JWT) Authentication (bearerAuth):

```python
import openapi_client
from openapi_client.models.tenant_feature_config_dto import TenantFeatureConfigDTO
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
    api_instance = openapi_client.FeatureManagementApi(api_client)
    feature_key = 'feature_key_example' # str | 
    tenant_id = 'tenant_id_example' # str | 
    tenant_feature_config_dto = openapi_client.TenantFeatureConfigDTO() # TenantFeatureConfigDTO | 

    try:
        # Configure feature for a specific tenant
        api_instance.configure_feature_for_tenant(feature_key, tenant_id, tenant_feature_config_dto)
    except Exception as e:
        print("Exception when calling FeatureManagementApi->configure_feature_for_tenant: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **feature_key** | **str**|  | 
 **tenant_id** | **str**|  | 
 **tenant_feature_config_dto** | [**TenantFeatureConfigDTO**](TenantFeatureConfigDTO.md)|  | 

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
**200** | Tenant configuration saved |  -  |
**404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **create_feature**
> FeatureDefinitionDTO create_feature(create_feature_request)

Create a new feature definition

Why this endpoint is needed:
Super administrators need to register new features in the central registry with metadata, access rules, and rollout configuration.


### Example

* Bearer (JWT) Authentication (bearerAuth):

```python
import openapi_client
from openapi_client.models.create_feature_request import CreateFeatureRequest
from openapi_client.models.feature_definition_dto import FeatureDefinitionDTO
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
    api_instance = openapi_client.FeatureManagementApi(api_client)
    create_feature_request = openapi_client.CreateFeatureRequest() # CreateFeatureRequest | 

    try:
        # Create a new feature definition
        api_response = api_instance.create_feature(create_feature_request)
        print("The response of FeatureManagementApi->create_feature:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling FeatureManagementApi->create_feature: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **create_feature_request** | [**CreateFeatureRequest**](CreateFeatureRequest.md)|  | 

### Return type

[**FeatureDefinitionDTO**](FeatureDefinitionDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**201** | Feature created |  -  |
**400** | Request validation failed or business rule validation failed |  -  |
**401** | Authentication missing or invalid |  -  |
**409** | Requested action conflicts with current resource state |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **delete_feature**
> delete_feature(feature_key)

Delete a feature definition

Why this endpoint is needed:
Retired features need to be removed from the registry to keep the catalog clean.


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
    api_instance = openapi_client.FeatureManagementApi(api_client)
    feature_key = 'feature_key_example' # str | 

    try:
        # Delete a feature definition
        api_instance.delete_feature(feature_key)
    except Exception as e:
        print("Exception when calling FeatureManagementApi->delete_feature: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **feature_key** | **str**|  | 

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
**204** | Feature deleted |  -  |
**404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **get_feature_analytics**
> FeatureAnalyticsDTO get_feature_analytics(feature_key)

Get usage analytics for a feature

Why this endpoint is needed:
Product teams need to measure feature adoption, completion rates, and user engagement.


### Example

* Bearer (JWT) Authentication (bearerAuth):

```python
import openapi_client
from openapi_client.models.feature_analytics_dto import FeatureAnalyticsDTO
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
    api_instance = openapi_client.FeatureManagementApi(api_client)
    feature_key = 'feature_key_example' # str | 

    try:
        # Get usage analytics for a feature
        api_response = api_instance.get_feature_analytics(feature_key)
        print("The response of FeatureManagementApi->get_feature_analytics:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling FeatureManagementApi->get_feature_analytics: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **feature_key** | **str**|  | 

### Return type

[**FeatureAnalyticsDTO**](FeatureAnalyticsDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | Feature analytics |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **list_features**
> List[FeatureDefinitionDTO] list_features(category=category, type=type, page=page, size=size)

List all feature definitions

Why this endpoint is needed:
Super administrators need a centralized view of all registered features including tours, tooltips, banners, and feature flags.

What this endpoint does:
Returns paginated list of feature definitions with filtering by category and type.


### Example

* Bearer (JWT) Authentication (bearerAuth):

```python
import openapi_client
from openapi_client.models.feature_category import FeatureCategory
from openapi_client.models.feature_definition_dto import FeatureDefinitionDTO
from openapi_client.models.feature_type import FeatureType
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
    api_instance = openapi_client.FeatureManagementApi(api_client)
    category = openapi_client.FeatureCategory() # FeatureCategory |  (optional)
    type = openapi_client.FeatureType() # FeatureType |  (optional)
    page = 0 # int |  (optional) (default to 0)
    size = 20 # int |  (optional) (default to 20)

    try:
        # List all feature definitions
        api_response = api_instance.list_features(category=category, type=type, page=page, size=size)
        print("The response of FeatureManagementApi->list_features:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling FeatureManagementApi->list_features: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **category** | [**FeatureCategory**](.md)|  | [optional] 
 **type** | [**FeatureType**](.md)|  | [optional] 
 **page** | **int**|  | [optional] [default to 0]
 **size** | **int**|  | [optional] [default to 20]

### Return type

[**List[FeatureDefinitionDTO]**](FeatureDefinitionDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | List of feature definitions |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **update_feature**
> FeatureDefinitionDTO update_feature(feature_key, update_feature_request)

Update an existing feature definition

Why this endpoint is needed:
Feature metadata, access rules, and rollout percentages need to be adjustable as product strategy evolves.


### Example

* Bearer (JWT) Authentication (bearerAuth):

```python
import openapi_client
from openapi_client.models.feature_definition_dto import FeatureDefinitionDTO
from openapi_client.models.update_feature_request import UpdateFeatureRequest
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
    api_instance = openapi_client.FeatureManagementApi(api_client)
    feature_key = 'feature_key_example' # str | 
    update_feature_request = openapi_client.UpdateFeatureRequest() # UpdateFeatureRequest | 

    try:
        # Update an existing feature definition
        api_response = api_instance.update_feature(feature_key, update_feature_request)
        print("The response of FeatureManagementApi->update_feature:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling FeatureManagementApi->update_feature: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **feature_key** | **str**|  | 
 **update_feature_request** | [**UpdateFeatureRequest**](UpdateFeatureRequest.md)|  | 

### Return type

[**FeatureDefinitionDTO**](FeatureDefinitionDTO.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | Feature updated |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

