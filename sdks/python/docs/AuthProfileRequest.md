# AuthProfileRequest


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**tenant_id** | **str** |  | 
**auth_mode** | **str** |  | 
**issuer** | **str** |  | [optional] 
**audience** | **str** |  | [optional] 
**jwks_endpoint** | **str** |  | [optional] 
**oidc_discovery_url** | **str** |  | [optional] 
**oidc_client_id** | **str** |  | [optional] 
**oidc_client_secret** | **str** |  | [optional] 
**oidc_redirect_uri** | **str** |  | [optional] 
**oidc_scopes** | **str** |  | [optional] 
**clock_skew_seconds** | **int** |  | [optional] 
**token_ttl_seconds** | **int** |  | [optional] 
**signing_secret** | **str** |  | [optional] 
**fallback_policy** | **str** |  | [optional] 
**claim_mappings** | [**List[ClaimMappingRequest]**](ClaimMappingRequest.md) |  | [optional] 

## Example

```python
from openapi_client.models.auth_profile_request import AuthProfileRequest

# TODO update the JSON string below
json = "{}"
# create an instance of AuthProfileRequest from a JSON string
auth_profile_request_instance = AuthProfileRequest.from_json(json)
# print the JSON string representation of the object
print(AuthProfileRequest.to_json())

# convert the object into a dict
auth_profile_request_dict = auth_profile_request_instance.to_dict()
# create an instance of AuthProfileRequest from a dict
auth_profile_request_from_dict = AuthProfileRequest.from_dict(auth_profile_request_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


