# CategoryRequest


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**name** | **str** |  | 
**description** | **str** |  | [optional] 
**question_mappings** | [**List[CategoryQuestionMappingRequest]**](CategoryQuestionMappingRequest.md) |  | [optional] 

## Example

```python
from openapi_client.models.category_request import CategoryRequest

# TODO update the JSON string below
json = "{}"
# create an instance of CategoryRequest from a JSON string
category_request_instance = CategoryRequest.from_json(json)
# print the JSON string representation of the object
print(CategoryRequest.to_json())

# convert the object into a dict
category_request_dict = category_request_instance.to_dict()
# create an instance of CategoryRequest from a dict
category_request_from_dict = CategoryRequest.from_dict(category_request_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


