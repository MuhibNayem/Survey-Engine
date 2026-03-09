# ClaimMappingRequest


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**external_claim** | **str** |  | [optional] 
**internal_field** | **str** |  | [optional] 
**required** | **bool** |  | [optional] 

## Example

```python
from openapi_client.models.claim_mapping_request import ClaimMappingRequest

# TODO update the JSON string below
json = "{}"
# create an instance of ClaimMappingRequest from a JSON string
claim_mapping_request_instance = ClaimMappingRequest.from_json(json)
# print the JSON string representation of the object
print(ClaimMappingRequest.to_json())

# convert the object into a dict
claim_mapping_request_dict = claim_mapping_request_instance.to_dict()
# create an instance of ClaimMappingRequest from a dict
claim_mapping_request_from_dict = ClaimMappingRequest.from_dict(claim_mapping_request_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


