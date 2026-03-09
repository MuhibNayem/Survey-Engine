# CampaignPreviewResponse


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**campaign_id** | **UUID** |  | [optional] 
**tenant_id** | **str** |  | [optional] 
**campaign_name** | **str** |  | [optional] 
**campaign_status** | **str** |  | [optional] 
**auth_mode** | **str** |  | [optional] 
**survey_id** | **UUID** |  | [optional] 
**survey_title** | **str** |  | [optional] 
**survey_description** | **str** |  | [optional] 
**show_question_numbers** | **bool** |  | [optional] 
**show_progress_indicator** | **bool** |  | [optional] 
**allow_back_button** | **bool** |  | [optional] 
**start_message** | **str** |  | [optional] 
**finish_message** | **str** |  | [optional] 
**header_html** | **str** |  | [optional] 
**footer_html** | **str** |  | [optional] 
**collect_name** | **bool** |  | [optional] 
**collect_email** | **bool** |  | [optional] 
**collect_phone** | **bool** |  | [optional] 
**collect_address** | **bool** |  | [optional] 
**pages** | [**List[CampaignPreviewResponsePagesInner]**](CampaignPreviewResponsePagesInner.md) |  | [optional] 

## Example

```python
from openapi_client.models.campaign_preview_response import CampaignPreviewResponse

# TODO update the JSON string below
json = "{}"
# create an instance of CampaignPreviewResponse from a JSON string
campaign_preview_response_instance = CampaignPreviewResponse.from_json(json)
# print the JSON string representation of the object
print(CampaignPreviewResponse.to_json())

# convert the object into a dict
campaign_preview_response_dict = campaign_preview_response_instance.to_dict()
# create an instance of CampaignPreviewResponse from a dict
campaign_preview_response_from_dict = CampaignPreviewResponse.from_dict(campaign_preview_response_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


