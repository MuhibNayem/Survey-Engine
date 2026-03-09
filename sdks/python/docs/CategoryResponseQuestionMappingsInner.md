# CategoryResponseQuestionMappingsInner


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**question_id** | **UUID** |  | [optional] 
**question_version_id** | **UUID** |  | [optional] 
**question_text** | **str** |  | [optional] 
**sort_order** | **int** |  | [optional] 
**weight** | **float** |  | [optional] 

## Example

```python
from openapi_client.models.category_response_question_mappings_inner import CategoryResponseQuestionMappingsInner

# TODO update the JSON string below
json = "{}"
# create an instance of CategoryResponseQuestionMappingsInner from a JSON string
category_response_question_mappings_inner_instance = CategoryResponseQuestionMappingsInner.from_json(json)
# print the JSON string representation of the object
print(CategoryResponseQuestionMappingsInner.to_json())

# convert the object into a dict
category_response_question_mappings_inner_dict = category_response_question_mappings_inner_instance.to_dict()
# create an instance of CategoryResponseQuestionMappingsInner from a dict
category_response_question_mappings_inner_from_dict = CategoryResponseQuestionMappingsInner.from_dict(category_response_question_mappings_inner_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


