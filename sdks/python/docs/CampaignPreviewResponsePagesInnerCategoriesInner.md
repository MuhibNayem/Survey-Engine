# CampaignPreviewResponsePagesInnerCategoriesInner


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**category_version_id** | **UUID** |  | [optional] 
**version_number** | **int** |  | [optional] 
**name** | **str** |  | [optional] 
**description** | **str** |  | [optional] 
**weight_percentage** | **float** |  | [optional] 
**sort_order** | **int** |  | [optional] 
**questions** | [**List[CampaignPreviewResponsePagesInnerCategoriesInnerQuestionsInner]**](CampaignPreviewResponsePagesInnerCategoriesInnerQuestionsInner.md) |  | [optional] 

## Example

```python
from openapi_client.models.campaign_preview_response_pages_inner_categories_inner import CampaignPreviewResponsePagesInnerCategoriesInner

# TODO update the JSON string below
json = "{}"
# create an instance of CampaignPreviewResponsePagesInnerCategoriesInner from a JSON string
campaign_preview_response_pages_inner_categories_inner_instance = CampaignPreviewResponsePagesInnerCategoriesInner.from_json(json)
# print the JSON string representation of the object
print(CampaignPreviewResponsePagesInnerCategoriesInner.to_json())

# convert the object into a dict
campaign_preview_response_pages_inner_categories_inner_dict = campaign_preview_response_pages_inner_categories_inner_instance.to_dict()
# create an instance of CampaignPreviewResponsePagesInnerCategoriesInner from a dict
campaign_preview_response_pages_inner_categories_inner_from_dict = CampaignPreviewResponsePagesInnerCategoriesInner.from_dict(campaign_preview_response_pages_inner_categories_inner_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


