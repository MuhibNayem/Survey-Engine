# SurveyThemeAdvanced


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**use_custom_header_html** | **bool** |  | [optional] 
**use_custom_footer_html** | **bool** |  | [optional] 
**custom_header_html** | **str** |  | [optional] 
**custom_footer_html** | **str** |  | [optional] 
**custom_css** | **str** |  | [optional] 

## Example

```python
from openapi_client.models.survey_theme_advanced import SurveyThemeAdvanced

# TODO update the JSON string below
json = "{}"
# create an instance of SurveyThemeAdvanced from a JSON string
survey_theme_advanced_instance = SurveyThemeAdvanced.from_json(json)
# print the JSON string representation of the object
print(SurveyThemeAdvanced.to_json())

# convert the object into a dict
survey_theme_advanced_dict = survey_theme_advanced_instance.to_dict()
# create an instance of SurveyThemeAdvanced from a dict
survey_theme_advanced_from_dict = SurveyThemeAdvanced.from_dict(survey_theme_advanced_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


