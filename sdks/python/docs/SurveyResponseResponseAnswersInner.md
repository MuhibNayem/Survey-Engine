# SurveyResponseResponseAnswersInner


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**id** | **UUID** |  | [optional] 
**question_id** | **UUID** |  | [optional] 
**question_version_id** | **UUID** |  | [optional] 
**value** | **str** |  | [optional] 
**score** | **float** |  | [optional] 

## Example

```python
from openapi_client.models.survey_response_response_answers_inner import SurveyResponseResponseAnswersInner

# TODO update the JSON string below
json = "{}"
# create an instance of SurveyResponseResponseAnswersInner from a JSON string
survey_response_response_answers_inner_instance = SurveyResponseResponseAnswersInner.from_json(json)
# print the JSON string representation of the object
print(SurveyResponseResponseAnswersInner.to_json())

# convert the object into a dict
survey_response_response_answers_inner_dict = survey_response_response_answers_inner_instance.to_dict()
# create an instance of SurveyResponseResponseAnswersInner from a dict
survey_response_response_answers_inner_from_dict = SurveyResponseResponseAnswersInner.from_dict(survey_response_response_answers_inner_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


