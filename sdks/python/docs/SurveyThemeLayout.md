# SurveyThemeLayout


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**content_width** | **str** |  | [optional] 
**header_style** | **str** |  | [optional] 
**header_alignment** | **str** |  | [optional] 
**footer_style** | **str** |  | [optional] 
**footer_alignment** | **str** |  | [optional] 
**section_style** | **str** |  | [optional] 
**question_card_style** | **str** |  | [optional] 
**category_separator_style** | **str** |  | [optional] 

## Example

```python
from openapi_client.models.survey_theme_layout import SurveyThemeLayout

# TODO update the JSON string below
json = "{}"
# create an instance of SurveyThemeLayout from a JSON string
survey_theme_layout_instance = SurveyThemeLayout.from_json(json)
# print the JSON string representation of the object
print(SurveyThemeLayout.to_json())

# convert the object into a dict
survey_theme_layout_dict = survey_theme_layout_instance.to_dict()
# create an instance of SurveyThemeLayout from a dict
survey_theme_layout_from_dict = SurveyThemeLayout.from_dict(survey_theme_layout_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


