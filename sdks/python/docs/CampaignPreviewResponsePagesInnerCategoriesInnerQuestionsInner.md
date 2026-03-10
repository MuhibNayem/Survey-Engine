# CampaignPreviewResponsePagesInnerCategoriesInnerQuestionsInner


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**id** | **UUID** |  | [optional] 
**question_id** | **UUID** |  | [optional] 
**question_version_id** | **UUID** |  | [optional] 
**category_version_id** | **UUID** |  | [optional] 
**category_weight_percentage** | **float** |  | [optional] 
**text** | **str** |  | [optional] 
**type** | **str** |  | [optional] 
**max_score** | **float** |  | [optional] 
**mandatory** | **bool** |  | [optional] 
**sort_order** | **int** |  | [optional] 
**option_config** | **str** |  | [optional] 
**answer_config** | **str** |  | [optional] 

## Example

```python
from openapi_client.models.campaign_preview_response_pages_inner_categories_inner_questions_inner import CampaignPreviewResponsePagesInnerCategoriesInnerQuestionsInner

# TODO update the JSON string below
json = "{}"
# create an instance of CampaignPreviewResponsePagesInnerCategoriesInnerQuestionsInner from a JSON string
campaign_preview_response_pages_inner_categories_inner_questions_inner_instance = CampaignPreviewResponsePagesInnerCategoriesInnerQuestionsInner.from_json(json)
# print the JSON string representation of the object
print(CampaignPreviewResponsePagesInnerCategoriesInnerQuestionsInner.to_json())

# convert the object into a dict
campaign_preview_response_pages_inner_categories_inner_questions_inner_dict = campaign_preview_response_pages_inner_categories_inner_questions_inner_instance.to_dict()
# create an instance of CampaignPreviewResponsePagesInnerCategoriesInnerQuestionsInner from a dict
campaign_preview_response_pages_inner_categories_inner_questions_inner_from_dict = CampaignPreviewResponsePagesInnerCategoriesInnerQuestionsInner.from_dict(campaign_preview_response_pages_inner_categories_inner_questions_inner_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


