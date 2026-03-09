# CategoryResponse


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**id** | **UUID** |  | [optional] 
**name** | **str** |  | [optional] 
**description** | **str** |  | [optional] 
**current_version** | **int** |  | [optional] 
**active** | **bool** |  | [optional] 
**question_mappings** | [**List[CategoryResponseQuestionMappingsInner]**](CategoryResponseQuestionMappingsInner.md) |  | [optional] 
**created_by** | **str** |  | [optional] 
**created_at** | **datetime** |  | [optional] 
**updated_by** | **str** |  | [optional] 
**updated_at** | **datetime** |  | [optional] 

## Example

```python
from openapi_client.models.category_response import CategoryResponse

# TODO update the JSON string below
json = "{}"
# create an instance of CategoryResponse from a JSON string
category_response_instance = CategoryResponse.from_json(json)
# print the JSON string representation of the object
print(CategoryResponse.to_json())

# convert the object into a dict
category_response_dict = category_response_instance.to_dict()
# create an instance of CategoryResponse from a dict
category_response_from_dict = CategoryResponse.from_dict(category_response_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


