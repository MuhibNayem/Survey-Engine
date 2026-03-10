# SurveyThemeBranding


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**brand_label** | **str** |  | [optional] 
**logo_url** | **str** |  | [optional] 
**logo_position** | **str** |  | [optional] 
**font_family** | **str** |  | [optional] 

## Example

```python
from openapi_client.models.survey_theme_branding import SurveyThemeBranding

# TODO update the JSON string below
json = "{}"
# create an instance of SurveyThemeBranding from a JSON string
survey_theme_branding_instance = SurveyThemeBranding.from_json(json)
# print the JSON string representation of the object
print(SurveyThemeBranding.to_json())

# convert the object into a dict
survey_theme_branding_dict = survey_theme_branding_instance.to_dict()
# create an instance of SurveyThemeBranding from a dict
survey_theme_branding_from_dict = SurveyThemeBranding.from_dict(survey_theme_branding_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


