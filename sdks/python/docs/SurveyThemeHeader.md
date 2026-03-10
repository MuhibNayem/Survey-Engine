# SurveyThemeHeader


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**enabled** | **bool** |  | [optional] 
**eyebrow** | **str** |  | [optional] 
**title** | **str** |  | [optional] 
**subtitle** | **str** |  | [optional] 
**note** | **str** |  | [optional] 

## Example

```python
from openapi_client.models.survey_theme_header import SurveyThemeHeader

# TODO update the JSON string below
json = "{}"
# create an instance of SurveyThemeHeader from a JSON string
survey_theme_header_instance = SurveyThemeHeader.from_json(json)
# print the JSON string representation of the object
print(SurveyThemeHeader.to_json())

# convert the object into a dict
survey_theme_header_dict = survey_theme_header_instance.to_dict()
# create an instance of SurveyThemeHeader from a dict
survey_theme_header_from_dict = SurveyThemeHeader.from_dict(survey_theme_header_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


