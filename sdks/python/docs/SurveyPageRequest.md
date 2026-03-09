# SurveyPageRequest


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**title** | **str** |  | [optional] 
**sort_order** | **int** |  | 
**questions** | [**List[SurveyQuestionRequest]**](SurveyQuestionRequest.md) |  | [optional] 

## Example

```python
from openapi_client.models.survey_page_request import SurveyPageRequest

# TODO update the JSON string below
json = "{}"
# create an instance of SurveyPageRequest from a JSON string
survey_page_request_instance = SurveyPageRequest.from_json(json)
# print the JSON string representation of the object
print(SurveyPageRequest.to_json())

# convert the object into a dict
survey_page_request_dict = survey_page_request_instance.to_dict()
# create an instance of SurveyPageRequest from a dict
survey_page_request_from_dict = SurveyPageRequest.from_dict(survey_page_request_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


