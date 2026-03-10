# SurveyThemePalette


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**background** | **str** |  | [optional] 
**shell** | **str** |  | [optional] 
**panel** | **str** |  | [optional] 
**card** | **str** |  | [optional] 
**border** | **str** |  | [optional] 
**text_primary** | **str** |  | [optional] 
**text_secondary** | **str** |  | [optional] 
**primary** | **str** |  | [optional] 
**primary_text** | **str** |  | [optional] 
**accent** | **str** |  | [optional] 
**accent_soft** | **str** |  | [optional] 
**header_background** | **str** |  | [optional] 
**header_text** | **str** |  | [optional] 
**footer_background** | **str** |  | [optional] 
**footer_text** | **str** |  | [optional] 

## Example

```python
from openapi_client.models.survey_theme_palette import SurveyThemePalette

# TODO update the JSON string below
json = "{}"
# create an instance of SurveyThemePalette from a JSON string
survey_theme_palette_instance = SurveyThemePalette.from_json(json)
# print the JSON string representation of the object
print(SurveyThemePalette.to_json())

# convert the object into a dict
survey_theme_palette_dict = survey_theme_palette_instance.to_dict()
# create an instance of SurveyThemePalette from a dict
survey_theme_palette_from_dict = SurveyThemePalette.from_dict(survey_theme_palette_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


