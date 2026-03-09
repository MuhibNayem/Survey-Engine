# SurveyResponsePagesInnerQuestionsInner


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**id** | **UUID** |  | [optional] 
**question_id** | **UUID** |  | [optional] 
**question_version_id** | **UUID** |  | [optional] 
**category_id** | **UUID** |  | [optional] 
**category_version_id** | **UUID** |  | [optional] 
**category_weight_percentage** | **float** |  | [optional] 
**sort_order** | **int** |  | [optional] 
**mandatory** | **bool** |  | [optional] 
**answer_config** | **str** |  | [optional] 
**pinned_question_text** | **str** |  | [optional] 
**pinned_question_type** | **str** |  | [optional] 
**pinned_question_max_score** | **float** |  | [optional] 
**pinned_question_option_config** | **str** |  | [optional] 
**pinned_category_name** | **str** |  | [optional] 
**pinned_category_description** | **str** |  | [optional] 

## Example

```python
from openapi_client.models.survey_response_pages_inner_questions_inner import SurveyResponsePagesInnerQuestionsInner

# TODO update the JSON string below
json = "{}"
# create an instance of SurveyResponsePagesInnerQuestionsInner from a JSON string
survey_response_pages_inner_questions_inner_instance = SurveyResponsePagesInnerQuestionsInner.from_json(json)
# print the JSON string representation of the object
print(SurveyResponsePagesInnerQuestionsInner.to_json())

# convert the object into a dict
survey_response_pages_inner_questions_inner_dict = survey_response_pages_inner_questions_inner_instance.to_dict()
# create an instance of SurveyResponsePagesInnerQuestionsInner from a dict
survey_response_pages_inner_questions_inner_from_dict = SurveyResponsePagesInnerQuestionsInner.from_dict(survey_response_pages_inner_questions_inner_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


