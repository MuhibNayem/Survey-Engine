# CategoryQuestionMappingRequest


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**question_id** | **UUID** |  | 
**sort_order** | **int** |  | 
**weight** | **float** |  | [optional] 

## Example

```python
from openapi_client.models.category_question_mapping_request import CategoryQuestionMappingRequest

# TODO update the JSON string below
json = "{}"
# create an instance of CategoryQuestionMappingRequest from a JSON string
category_question_mapping_request_instance = CategoryQuestionMappingRequest.from_json(json)
# print the JSON string representation of the object
print(CategoryQuestionMappingRequest.to_json())

# convert the object into a dict
category_question_mapping_request_dict = category_question_mapping_request_instance.to_dict()
# create an instance of CategoryQuestionMappingRequest from a dict
category_question_mapping_request_from_dict = CategoryQuestionMappingRequest.from_dict(category_question_mapping_request_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


