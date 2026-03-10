# CampaignResponse


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**id** | **UUID** |  | [optional] 
**name** | **str** |  | [optional] 
**description** | **str** |  | [optional] 
**survey_id** | **UUID** |  | [optional] 
**survey_snapshot_id** | **UUID** |  | [optional] 
**default_weight_profile_id** | **UUID** |  | [optional] 
**auth_mode** | **str** |  | [optional] 
**status** | **str** |  | [optional] 
**active** | **bool** |  | [optional] 
**created_by** | **str** |  | [optional] 
**created_at** | **datetime** |  | [optional] 
**updated_by** | **str** |  | [optional] 
**updated_at** | **datetime** |  | [optional] 
**data_collection_fields** | [**List[DataCollectionFieldResponse]**](DataCollectionFieldResponse.md) |  | [optional] 

## Example

```python
from openapi_client.models.campaign_response import CampaignResponse

# TODO update the JSON string below
json = "{}"
# create an instance of CampaignResponse from a JSON string
campaign_response_instance = CampaignResponse.from_json(json)
# print the JSON string representation of the object
print(CampaignResponse.to_json())

# convert the object into a dict
campaign_response_dict = campaign_response_instance.to_dict()
# create an instance of CampaignResponse from a dict
campaign_response_from_dict = CampaignResponse.from_dict(campaign_response_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


