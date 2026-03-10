# SurveyThemeFooter


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**enabled** | **bool** |  | [optional] 
**line1** | **str** |  | [optional] 
**line2** | **str** |  | [optional] 
**legal** | **str** |  | [optional] 

## Example

```python
from openapi_client.models.survey_theme_footer import SurveyThemeFooter

# TODO update the JSON string below
json = "{}"
# create an instance of SurveyThemeFooter from a JSON string
survey_theme_footer_instance = SurveyThemeFooter.from_json(json)
# print the JSON string representation of the object
print(SurveyThemeFooter.to_json())

# convert the object into a dict
survey_theme_footer_dict = survey_theme_footer_instance.to_dict()
# create an instance of SurveyThemeFooter from a dict
survey_theme_footer_from_dict = SurveyThemeFooter.from_dict(survey_theme_footer_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


