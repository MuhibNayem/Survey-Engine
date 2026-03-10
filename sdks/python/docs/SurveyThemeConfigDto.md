# SurveyThemeConfigDto


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**template_key** | **str** |  | [optional] 
**palette_key** | **str** |  | [optional] 
**palette** | [**SurveyThemePalette**](SurveyThemePalette.md) |  | [optional] 
**branding** | [**SurveyThemeBranding**](SurveyThemeBranding.md) |  | [optional] 
**layout** | [**SurveyThemeLayout**](SurveyThemeLayout.md) |  | [optional] 
**motion** | [**SurveyThemeMotion**](SurveyThemeMotion.md) |  | [optional] 
**header** | [**SurveyThemeHeader**](SurveyThemeHeader.md) |  | [optional] 
**footer** | [**SurveyThemeFooter**](SurveyThemeFooter.md) |  | [optional] 
**advanced** | [**SurveyThemeAdvanced**](SurveyThemeAdvanced.md) |  | [optional] 

## Example

```python
from openapi_client.models.survey_theme_config_dto import SurveyThemeConfigDto

# TODO update the JSON string below
json = "{}"
# create an instance of SurveyThemeConfigDto from a JSON string
survey_theme_config_dto_instance = SurveyThemeConfigDto.from_json(json)
# print the JSON string representation of the object
print(SurveyThemeConfigDto.to_json())

# convert the object into a dict
survey_theme_config_dto_dict = survey_theme_config_dto_instance.to_dict()
# create an instance of SurveyThemeConfigDto from a dict
survey_theme_config_dto_from_dict = SurveyThemeConfigDto.from_dict(survey_theme_config_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


