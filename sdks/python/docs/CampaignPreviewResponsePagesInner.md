# CampaignPreviewResponsePagesInner


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**id** | **UUID** |  | [optional] 
**title** | **str** |  | [optional] 
**sort_order** | **int** |  | [optional] 
**categories** | [**List[CampaignPreviewResponsePagesInnerCategoriesInner]**](CampaignPreviewResponsePagesInnerCategoriesInner.md) |  | [optional] 
**questions** | [**List[CampaignPreviewResponsePagesInnerCategoriesInnerQuestionsInner]**](CampaignPreviewResponsePagesInnerCategoriesInnerQuestionsInner.md) |  | [optional] 

## Example

```python
from openapi_client.models.campaign_preview_response_pages_inner import CampaignPreviewResponsePagesInner

# TODO update the JSON string below
json = "{}"
# create an instance of CampaignPreviewResponsePagesInner from a JSON string
campaign_preview_response_pages_inner_instance = CampaignPreviewResponsePagesInner.from_json(json)
# print the JSON string representation of the object
print(CampaignPreviewResponsePagesInner.to_json())

# convert the object into a dict
campaign_preview_response_pages_inner_dict = campaign_preview_response_pages_inner_instance.to_dict()
# create an instance of CampaignPreviewResponsePagesInner from a dict
campaign_preview_response_pages_inner_from_dict = CampaignPreviewResponsePagesInner.from_dict(campaign_preview_response_pages_inner_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


