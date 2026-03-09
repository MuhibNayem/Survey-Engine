# WeightProfileResponse


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**id** | **UUID** |  | [optional] 
**name** | **str** |  | [optional] 
**campaign_id** | **UUID** |  | [optional] 
**active** | **bool** |  | [optional] 
**category_weights** | [**List[WeightProfileResponseCategoryWeightsInner]**](WeightProfileResponseCategoryWeightsInner.md) |  | [optional] 
**created_by** | **str** |  | [optional] 
**created_at** | **datetime** |  | [optional] 

## Example

```python
from openapi_client.models.weight_profile_response import WeightProfileResponse

# TODO update the JSON string below
json = "{}"
# create an instance of WeightProfileResponse from a JSON string
weight_profile_response_instance = WeightProfileResponse.from_json(json)
# print the JSON string representation of the object
print(WeightProfileResponse.to_json())

# convert the object into a dict
weight_profile_response_dict = weight_profile_response_instance.to_dict()
# create an instance of WeightProfileResponse from a dict
weight_profile_response_from_dict = WeightProfileResponse.from_dict(weight_profile_response_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


