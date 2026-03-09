# DistributionChannelResponse


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**id** | **UUID** |  | [optional] 
**campaign_id** | **UUID** |  | [optional] 
**channel_type** | **str** |  | [optional] 
**channel_value** | **str** |  | [optional] 
**created_at** | **datetime** |  | [optional] 

## Example

```python
from openapi_client.models.distribution_channel_response import DistributionChannelResponse

# TODO update the JSON string below
json = "{}"
# create an instance of DistributionChannelResponse from a JSON string
distribution_channel_response_instance = DistributionChannelResponse.from_json(json)
# print the JSON string representation of the object
print(DistributionChannelResponse.to_json())

# convert the object into a dict
distribution_channel_response_dict = distribution_channel_response_instance.to_dict()
# create an instance of DistributionChannelResponse from a dict
distribution_channel_response_from_dict = DistributionChannelResponse.from_dict(distribution_channel_response_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


