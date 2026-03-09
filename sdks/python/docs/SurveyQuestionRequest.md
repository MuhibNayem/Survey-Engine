# SurveyQuestionRequest


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**question_id** | **UUID** |  | 
**category_id** | **UUID** |  | [optional] 
**category_weight_percentage** | **float** |  | [optional] 
**sort_order** | **int** |  | 
**mandatory** | **bool** |  | [optional] 
**answer_config** | **str** |  | [optional] 
**pinned_question_text** | **str** |  | [optional] 
**pinned_question_max_score** | **float** |  | [optional] 
**pinned_question_option_config** | **str** |  | [optional] 
**pinned_category_name** | **str** |  | [optional] 
**pinned_category_description** | **str** |  | [optional] 

## Example

```python
from openapi_client.models.survey_question_request import SurveyQuestionRequest

# TODO update the JSON string below
json = "{}"
# create an instance of SurveyQuestionRequest from a JSON string
survey_question_request_instance = SurveyQuestionRequest.from_json(json)
# print the JSON string representation of the object
print(SurveyQuestionRequest.to_json())

# convert the object into a dict
survey_question_request_dict = survey_question_request_instance.to_dict()
# create an instance of SurveyQuestionRequest from a dict
survey_question_request_from_dict = SurveyQuestionRequest.from_dict(survey_question_request_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


