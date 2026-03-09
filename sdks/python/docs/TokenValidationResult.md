# TokenValidationResult


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**valid** | **bool** |  | [optional] 
**respondent_id** | **str** |  | [optional] 
**email** | **str** |  | [optional] 
**mapped_claims** | **Dict[str, str]** |  | [optional] 
**error_code** | **str** |  | [optional] 
**error_message** | **str** |  | [optional] 

## Example

```python
from openapi_client.models.token_validation_result import TokenValidationResult

# TODO update the JSON string below
json = "{}"
# create an instance of TokenValidationResult from a JSON string
token_validation_result_instance = TokenValidationResult.from_json(json)
# print the JSON string representation of the object
print(TokenValidationResult.to_json())

# convert the object into a dict
token_validation_result_dict = token_validation_result_instance.to_dict()
# create an instance of TokenValidationResult from a dict
token_validation_result_from_dict = TokenValidationResult.from_dict(token_validation_result_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


