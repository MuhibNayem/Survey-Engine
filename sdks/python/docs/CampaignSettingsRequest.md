# CampaignSettingsRequest


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**password** | **str** |  | [optional] 
**captcha_enabled** | **bool** |  | [optional] 
**one_response_per_device** | **bool** |  | [optional] 
**ip_restriction_enabled** | **bool** |  | [optional] 
**email_restriction_enabled** | **bool** |  | [optional] 
**response_quota** | **int** |  | [optional] 
**close_date** | **datetime** |  | [optional] 
**session_timeout_minutes** | **int** |  | [optional] 
**show_question_numbers** | **bool** |  | [optional] 
**show_progress_indicator** | **bool** |  | [optional] 
**allow_back_button** | **bool** |  | [optional] 
**start_message** | **str** |  | [optional] 
**finish_message** | **str** |  | [optional] 
**header_html** | **str** |  | [optional] 
**footer_html** | **str** |  | [optional] 
**theme** | [**SurveyThemeConfigDto**](SurveyThemeConfigDto.md) |  | [optional] 
**collect_name** | **bool** |  | [optional] 
**collect_email** | **bool** |  | [optional] 
**collect_phone** | **bool** |  | [optional] 
**collect_address** | **bool** |  | [optional] 
**data_collection_fields** | [**List[DataCollectionFieldRequest]**](DataCollectionFieldRequest.md) |  | [optional] 

## Example

```python
from openapi_client.models.campaign_settings_request import CampaignSettingsRequest

# TODO update the JSON string below
json = "{}"
# create an instance of CampaignSettingsRequest from a JSON string
campaign_settings_request_instance = CampaignSettingsRequest.from_json(json)
# print the JSON string representation of the object
print(CampaignSettingsRequest.to_json())

# convert the object into a dict
campaign_settings_request_dict = campaign_settings_request_instance.to_dict()
# create an instance of CampaignSettingsRequest from a dict
campaign_settings_request_from_dict = CampaignSettingsRequest.from_dict(campaign_settings_request_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


