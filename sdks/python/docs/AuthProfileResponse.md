# AuthProfileResponse


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**id** | **UUID** |  | [optional] 
**tenant_id** | **str** |  | [optional] 
**auth_mode** | **str** |  | [optional] 
**issuer** | **str** |  | [optional] 
**audience** | **str** |  | [optional] 
**jwks_endpoint** | **str** |  | [optional] 
**oidc_discovery_url** | **str** |  | [optional] 
**oidc_client_id** | **str** |  | [optional] 
**oidc_redirect_uri** | **str** |  | [optional] 
**oidc_scopes** | **str** |  | [optional] 
**clock_skew_seconds** | **int** |  | [optional] 
**token_ttl_seconds** | **int** |  | [optional] 
**active_key_version** | **int** |  | [optional] 
**fallback_policy** | **str** |  | [optional] 
**claim_mappings** | [**List[AuthProfileResponseClaimMappingsInner]**](AuthProfileResponseClaimMappingsInner.md) |  | [optional] 

## Example

```python
from openapi_client.models.auth_profile_response import AuthProfileResponse

# TODO update the JSON string below
json = "{}"
# create an instance of AuthProfileResponse from a JSON string
auth_profile_response_instance = AuthProfileResponse.from_json(json)
# print the JSON string representation of the object
print(AuthProfileResponse.to_json())

# convert the object into a dict
auth_profile_response_dict = auth_profile_response_instance.to_dict()
# create an instance of AuthProfileResponse from a dict
auth_profile_response_from_dict = AuthProfileResponse.from_dict(auth_profile_response_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


