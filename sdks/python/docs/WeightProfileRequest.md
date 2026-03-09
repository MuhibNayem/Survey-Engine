# WeightProfileRequest


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**name** | **str** |  | 
**campaign_id** | **UUID** |  | 
**category_weights** | [**List[CategoryWeightRequest]**](CategoryWeightRequest.md) |  | [optional] 

## Example

```python
from openapi_client.models.weight_profile_request import WeightProfileRequest

# TODO update the JSON string below
json = "{}"
# create an instance of WeightProfileRequest from a JSON string
weight_profile_request_instance = WeightProfileRequest.from_json(json)
# print the JSON string representation of the object
print(WeightProfileRequest.to_json())

# convert the object into a dict
weight_profile_request_dict = weight_profile_request_instance.to_dict()
# create an instance of WeightProfileRequest from a dict
weight_profile_request_from_dict = WeightProfileRequest.from_dict(weight_profile_request_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


