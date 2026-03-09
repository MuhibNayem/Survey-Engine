# SurveyResponsePagesInner


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**id** | **UUID** |  | [optional] 
**title** | **str** |  | [optional] 
**sort_order** | **int** |  | [optional] 
**questions** | [**List[SurveyResponsePagesInnerQuestionsInner]**](SurveyResponsePagesInnerQuestionsInner.md) |  | [optional] 

## Example

```python
from openapi_client.models.survey_response_pages_inner import SurveyResponsePagesInner

# TODO update the JSON string below
json = "{}"
# create an instance of SurveyResponsePagesInner from a JSON string
survey_response_pages_inner_instance = SurveyResponsePagesInner.from_json(json)
# print the JSON string representation of the object
print(SurveyResponsePagesInner.to_json())

# convert the object into a dict
survey_response_pages_inner_dict = survey_response_pages_inner_instance.to_dict()
# create an instance of SurveyResponsePagesInner from a dict
survey_response_pages_inner_from_dict = SurveyResponsePagesInner.from_dict(survey_response_pages_inner_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


