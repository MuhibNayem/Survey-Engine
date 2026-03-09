# SurveyResponse


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**id** | **UUID** |  | [optional] 
**title** | **str** |  | [optional] 
**description** | **str** |  | [optional] 
**lifecycle_state** | **str** |  | [optional] 
**current_version** | **int** |  | [optional] 
**active** | **bool** |  | [optional] 
**pages** | [**List[SurveyResponsePagesInner]**](SurveyResponsePagesInner.md) |  | [optional] 
**created_by** | **str** |  | [optional] 
**created_at** | **datetime** |  | [optional] 
**updated_by** | **str** |  | [optional] 
**updated_at** | **datetime** |  | [optional] 

## Example

```python
from openapi_client.models.survey_response import SurveyResponse

# TODO update the JSON string below
json = "{}"
# create an instance of SurveyResponse from a JSON string
survey_response_instance = SurveyResponse.from_json(json)
# print the JSON string representation of the object
print(SurveyResponse.to_json())

# convert the object into a dict
survey_response_dict = survey_response_instance.to_dict()
# create an instance of SurveyResponse from a dict
survey_response_from_dict = SurveyResponse.from_dict(survey_response_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


