# ProviderTemplateResponse


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**provider_code** | **str** |  | [optional] 
**display_name** | **str** |  | [optional] 
**description** | **str** |  | [optional] 
**default_scopes** | **List[str]** |  | [optional] 
**default_claim_mappings** | [**List[ClaimMappingRequest]**](ClaimMappingRequest.md) |  | [optional] 
**required_config_fields** | **List[str]** |  | [optional] 

## Example

```python
from openapi_client.models.provider_template_response import ProviderTemplateResponse

# TODO update the JSON string below
json = "{}"
# create an instance of ProviderTemplateResponse from a JSON string
provider_template_response_instance = ProviderTemplateResponse.from_json(json)
# print the JSON string representation of the object
print(ProviderTemplateResponse.to_json())

# convert the object into a dict
provider_template_response_dict = provider_template_response_instance.to_dict()
# create an instance of ProviderTemplateResponse from a dict
provider_template_response_from_dict = ProviderTemplateResponse.from_dict(provider_template_response_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


