# CampaignPreviewResponsePagesInnerQuestionsInner


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**id** | **UUID** |  | [optional] 
**question_id** | **UUID** |  | [optional] 
**question_version_id** | **UUID** |  | [optional] 
**category_version_id** | **UUID** |  | [optional] 
**text** | **str** |  | [optional] 
**type** | **str** |  | [optional] 
**max_score** | **float** |  | [optional] 
**mandatory** | **bool** |  | [optional] 
**sort_order** | **int** |  | [optional] 
**option_config** | **str** |  | [optional] 
**answer_config** | **str** |  | [optional] 

## Example

```python
from openapi_client.models.campaign_preview_response_pages_inner_questions_inner import CampaignPreviewResponsePagesInnerQuestionsInner

# TODO update the JSON string below
json = "{}"
# create an instance of CampaignPreviewResponsePagesInnerQuestionsInner from a JSON string
campaign_preview_response_pages_inner_questions_inner_instance = CampaignPreviewResponsePagesInnerQuestionsInner.from_json(json)
# print the JSON string representation of the object
print(CampaignPreviewResponsePagesInnerQuestionsInner.to_json())

# convert the object into a dict
campaign_preview_response_pages_inner_questions_inner_dict = campaign_preview_response_pages_inner_questions_inner_instance.to_dict()
# create an instance of CampaignPreviewResponsePagesInnerQuestionsInner from a dict
campaign_preview_response_pages_inner_questions_inner_from_dict = CampaignPreviewResponsePagesInnerQuestionsInner.from_dict(campaign_preview_response_pages_inner_questions_inner_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


