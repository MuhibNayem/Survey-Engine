# SurveyResponseResponse


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**id** | **UUID** |  | [optional] 
**campaign_id** | **UUID** |  | [optional] 
**survey_snapshot_id** | **UUID** |  | [optional] 
**respondent_identifier** | **str** |  | [optional] 
**respondent_metadata** | **str** |  | [optional] 
**status** | **str** |  | [optional] 
**started_at** | **datetime** |  | [optional] 
**submitted_at** | **datetime** |  | [optional] 
**locked_at** | **datetime** |  | [optional] 
**weight_profile_id** | **UUID** |  | [optional] 
**weighted_total_score** | **float** |  | [optional] 
**scored_at** | **datetime** |  | [optional] 
**answers** | [**List[SurveyResponseResponseAnswersInner]**](SurveyResponseResponseAnswersInner.md) |  | [optional] 

## Example

```python
from openapi_client.models.survey_response_response import SurveyResponseResponse

# TODO update the JSON string below
json = "{}"
# create an instance of SurveyResponseResponse from a JSON string
survey_response_response_instance = SurveyResponseResponse.from_json(json)
# print the JSON string representation of the object
print(SurveyResponseResponse.to_json())

# convert the object into a dict
survey_response_response_dict = survey_response_response_instance.to_dict()
# create an instance of SurveyResponseResponse from a dict
survey_response_response_from_dict = SurveyResponseResponse.from_dict(survey_response_response_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


