# AuthProfileResponseClaimMappingsInner


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**id** | **UUID** |  | [optional] 
**external_claim** | **str** |  | [optional] 
**internal_field** | **str** |  | [optional] 
**required** | **bool** |  | [optional] 

## Example

```python
from openapi_client.models.auth_profile_response_claim_mappings_inner import AuthProfileResponseClaimMappingsInner

# TODO update the JSON string below
json = "{}"
# create an instance of AuthProfileResponseClaimMappingsInner from a JSON string
auth_profile_response_claim_mappings_inner_instance = AuthProfileResponseClaimMappingsInner.from_json(json)
# print the JSON string representation of the object
print(AuthProfileResponseClaimMappingsInner.to_json())

# convert the object into a dict
auth_profile_response_claim_mappings_inner_dict = auth_profile_response_claim_mappings_inner_instance.to_dict()
# create an instance of AuthProfileResponseClaimMappingsInner from a dict
auth_profile_response_claim_mappings_inner_from_dict = AuthProfileResponseClaimMappingsInner.from_dict(auth_profile_response_claim_mappings_inner_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


