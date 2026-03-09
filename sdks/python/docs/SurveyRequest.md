# SurveyRequest


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**title** | **str** |  | 
**description** | **str** |  | [optional] 
**pages** | [**List[SurveyPageRequest]**](SurveyPageRequest.md) |  | [optional] 

## Example

```python
from openapi_client.models.survey_request import SurveyRequest

# TODO update the JSON string below
json = "{}"
# create an instance of SurveyRequest from a JSON string
survey_request_instance = SurveyRequest.from_json(json)
# print the JSON string representation of the object
print(SurveyRequest.to_json())

# convert the object into a dict
survey_request_dict = survey_request_instance.to_dict()
# create an instance of SurveyRequest from a dict
survey_request_from_dict = SurveyRequest.from_dict(survey_request_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


