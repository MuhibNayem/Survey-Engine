# CampaignAnalytics


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**total_responses** | **int** |  | [optional] 
**submitted_count** | **int** |  | [optional] 
**in_progress_count** | **int** |  | [optional] 
**locked_count** | **int** |  | [optional] 
**completion_rate** | **float** |  | [optional] 

## Example

```python
from openapi_client.models.campaign_analytics import CampaignAnalytics

# TODO update the JSON string below
json = "{}"
# create an instance of CampaignAnalytics from a JSON string
campaign_analytics_instance = CampaignAnalytics.from_json(json)
# print the JSON string representation of the object
print(CampaignAnalytics.to_json())

# convert the object into a dict
campaign_analytics_dict = campaign_analytics_instance.to_dict()
# create an instance of CampaignAnalytics from a dict
campaign_analytics_from_dict = CampaignAnalytics.from_dict(campaign_analytics_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


