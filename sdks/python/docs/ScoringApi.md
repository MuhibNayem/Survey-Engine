# openapi_client.ScoringApi

All URIs are relative to *http://localhost*

Method | HTTP request | Description
------------- | ------------- | -------------
[**calculate_weighted_score**](ScoringApi.md#calculate_weighted_score) | **POST** /api/v1/scoring/calculate/{profileId} | Calculate weighted score from raw category scores
[**create_weight_profile**](ScoringApi.md#create_weight_profile) | **POST** /api/v1/scoring/profiles | Create scoring weight profile
[**deactivate_weight_profile**](ScoringApi.md#deactivate_weight_profile) | **DELETE** /api/v1/scoring/profiles/{id} | Deactivate scoring profile
[**get_weight_profile**](ScoringApi.md#get_weight_profile) | **GET** /api/v1/scoring/profiles/{id} | Get one scoring profile by id
[**list_weight_profiles_by_campaign**](ScoringApi.md#list_weight_profiles_by_campaign) | **GET** /api/v1/scoring/profiles/campaign/{campaignId} | List scoring profiles for campaign
[**update_weight_profile**](ScoringApi.md#update_weight_profile) | **PUT** /api/v1/scoring/profiles/{id} | Update scoring profile
[**validate_weight_profile**](ScoringApi.md#validate_weight_profile) | **POST** /api/v1/scoring/profiles/{id}/validate | Validate category weight sum for a profile


# **calculate_weighted_score**
> ScoreResult calculate_weighted_score(profile_id, request_body)

Calculate weighted score from raw category scores

Why this endpoint is needed:
Consumers need deterministic score output from raw category data and configured business weighting policy.

What this endpoint does:
It computes normalized category contributions and returns a total weighted score breakdown.

How this endpoint does it:
Scoring engine loads profile weights, validates inputs, computes per-category normalized and weighted values,
and returns ScoreResult with category details.


### Example

* Bearer (JWT) Authentication (bearerAuth):

```python
import openapi_client
from openapi_client.models.score_result import ScoreResult
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
    api_instance = openapi_client.ScoringApi(api_client)
    profile_id = UUID('38400000-8cf0-11bd-b23e-10b96e4ef00d') # UUID | 
    request_body = {"0f2069da-4f19-4f4a-a8f7-f2d067e1a5d0":82.5,"12a6a2f0-69da-4f19-9c80-c84b6df67464":71.0} # Dict[str, float] | 

    try:
        # Calculate weighted score from raw category scores
        api_response = api_instance.calculate_weighted_score(profile_id, request_body)
        print("The response of ScoringApi->calculate_weighted_score:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling ScoringApi->calculate_weighted_score: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **profile_id** | **UUID**|  | 
 **request_body** | [**Dict[str, float]**](float.md)|  | 

### Return type

[**ScoreResult**](ScoreResult.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | Score calculated |  -  |
**400** | Request validation failed or business rule validation failed |  -  |
**401** | Authentication missing or invalid |  -  |
**404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **create_weight_profile**
> WeightProfileResponse create_weight_profile(weight_profile_request)

Create scoring weight profile

Why this endpoint is needed:
Score computation requires explicit category weighting definitions linked to campaign context.

What this endpoint does:
It creates a weight profile that defines how raw category scores contribute to total score.

How this endpoint does it:
Service validates campaign scope and category-weight payload, persists profile and nested weights,
and returns profile representation.


### Example

* Bearer (JWT) Authentication (bearerAuth):

```python
import openapi_client
from openapi_client.models.weight_profile_request import WeightProfileRequest
from openapi_client.models.weight_profile_response import WeightProfileResponse
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
    api_instance = openapi_client.ScoringApi(api_client)
    weight_profile_request = openapi_client.WeightProfileRequest() # WeightProfileRequest | 

    try:
        # Create scoring weight profile
        api_response = api_instance.create_weight_profile(weight_profile_request)
        print("The response of ScoringApi->create_weight_profile:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling ScoringApi->create_weight_profile: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **weight_profile_request** | [**WeightProfileRequest**](WeightProfileRequest.md)|  | 

### Return type

[**WeightProfileResponse**](WeightProfileResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**201** | Weight profile created |  -  |
**400** | Request validation failed or business rule validation failed |  -  |
**401** | Authentication missing or invalid |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **deactivate_weight_profile**
> deactivate_weight_profile(id)

Deactivate scoring profile

Why this endpoint is needed:
Obsolete profiles must be retired while preserving historical result integrity.

What this endpoint does:
It marks profile inactive.

How this endpoint does it:
Tenant-scoped lookup followed by active-state mutation and no-content response.


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
    api_instance = openapi_client.ScoringApi(api_client)
    id = UUID('38400000-8cf0-11bd-b23e-10b96e4ef00d') # UUID | 

    try:
        # Deactivate scoring profile
        api_instance.deactivate_weight_profile(id)
    except Exception as e:
        print("Exception when calling ScoringApi->deactivate_weight_profile: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **UUID**|  | 

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
**204** | Weight profile deactivated |  -  |
**401** | Authentication missing or invalid |  -  |
**404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **get_weight_profile**
> WeightProfileResponse get_weight_profile(id)

Get one scoring profile by id

Why this endpoint is needed:
Teams need profile-level visibility for verification and lifecycle updates.

What this endpoint does:
It returns one weight profile including category weights.

How this endpoint does it:
Service loads profile by id under tenant context and maps to DTO.


### Example

* Bearer (JWT) Authentication (bearerAuth):

```python
import openapi_client
from openapi_client.models.weight_profile_response import WeightProfileResponse
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
    api_instance = openapi_client.ScoringApi(api_client)
    id = UUID('38400000-8cf0-11bd-b23e-10b96e4ef00d') # UUID | 

    try:
        # Get one scoring profile by id
        api_response = api_instance.get_weight_profile(id)
        print("The response of ScoringApi->get_weight_profile:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling ScoringApi->get_weight_profile: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **UUID**|  | 

### Return type

[**WeightProfileResponse**](WeightProfileResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | Weight profile returned |  -  |
**401** | Authentication missing or invalid |  -  |
**404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **list_weight_profiles_by_campaign**
> List[WeightProfileResponse] list_weight_profiles_by_campaign(campaign_id)

List scoring profiles for campaign

Why this endpoint is needed:
Campaign-level scoring operations need discoverability of attached profiles.

What this endpoint does:
It returns all active/in-scope profiles linked to the specified campaign.

How this endpoint does it:
Service validates campaign access by tenant and fetches profile rows filtered by campaign id.


### Example

* Bearer (JWT) Authentication (bearerAuth):

```python
import openapi_client
from openapi_client.models.weight_profile_response import WeightProfileResponse
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
    api_instance = openapi_client.ScoringApi(api_client)
    campaign_id = UUID('38400000-8cf0-11bd-b23e-10b96e4ef00d') # UUID | 

    try:
        # List scoring profiles for campaign
        api_response = api_instance.list_weight_profiles_by_campaign(campaign_id)
        print("The response of ScoringApi->list_weight_profiles_by_campaign:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling ScoringApi->list_weight_profiles_by_campaign: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **campaign_id** | **UUID**|  | 

### Return type

[**List[WeightProfileResponse]**](WeightProfileResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | Profiles returned |  -  |
**401** | Authentication missing or invalid |  -  |
**404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **update_weight_profile**
> WeightProfileResponse update_weight_profile(id, weight_profile_request)

Update scoring profile

Why this endpoint is needed:
Scoring rubrics evolve and profiles require controlled updates.

What this endpoint does:
It updates profile metadata and category-weight assignments.

How this endpoint does it:
Service validates payload and tenant ownership, persists new values, and returns updated profile DTO.


### Example

* Bearer (JWT) Authentication (bearerAuth):

```python
import openapi_client
from openapi_client.models.weight_profile_request import WeightProfileRequest
from openapi_client.models.weight_profile_response import WeightProfileResponse
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
    api_instance = openapi_client.ScoringApi(api_client)
    id = UUID('38400000-8cf0-11bd-b23e-10b96e4ef00d') # UUID | 
    weight_profile_request = openapi_client.WeightProfileRequest() # WeightProfileRequest | 

    try:
        # Update scoring profile
        api_response = api_instance.update_weight_profile(id, weight_profile_request)
        print("The response of ScoringApi->update_weight_profile:\n")
        pprint(api_response)
    except Exception as e:
        print("Exception when calling ScoringApi->update_weight_profile: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **UUID**|  | 
 **weight_profile_request** | [**WeightProfileRequest**](WeightProfileRequest.md)|  | 

### Return type

[**WeightProfileResponse**](WeightProfileResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details

| Status code | Description | Response headers |
|-------------|-------------|------------------|
**200** | Weight profile updated |  -  |
**400** | Request validation failed or business rule validation failed |  -  |
**401** | Authentication missing or invalid |  -  |
**404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

# **validate_weight_profile**
> validate_weight_profile(id)

Validate category weight sum for a profile

Why this endpoint is needed:
Invalid weighting definitions can produce misleading score outputs; explicit pre-check endpoint reduces operational mistakes.

What this endpoint does:
It validates profile constraints (including total weight sum rule).

How this endpoint does it:
Service computes profile totals and throws deterministic business error when constraints are violated.


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
    api_instance = openapi_client.ScoringApi(api_client)
    id = UUID('38400000-8cf0-11bd-b23e-10b96e4ef00d') # UUID | 

    try:
        # Validate category weight sum for a profile
        api_instance.validate_weight_profile(id)
    except Exception as e:
        print("Exception when calling ScoringApi->validate_weight_profile: %s\n" % e)
```



### Parameters


Name | Type | Description  | Notes
------------- | ------------- | ------------- | -------------
 **id** | **UUID**|  | 

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
**200** | Profile is valid |  -  |
**400** | Request validation failed or business rule validation failed |  -  |
**401** | Authentication missing or invalid |  -  |
**404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to Model list]](../README.md#documentation-for-models) [[Back to README]](../README.md)

