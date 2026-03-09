# WeightProfileResponseCategoryWeightsInner


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**id** | **UUID** |  | [optional] 
**category_id** | **UUID** |  | [optional] 
**weight_percentage** | **float** |  | [optional] 

## Example

```python
from openapi_client.models.weight_profile_response_category_weights_inner import WeightProfileResponseCategoryWeightsInner

# TODO update the JSON string below
json = "{}"
# create an instance of WeightProfileResponseCategoryWeightsInner from a JSON string
weight_profile_response_category_weights_inner_instance = WeightProfileResponseCategoryWeightsInner.from_json(json)
# print the JSON string representation of the object
print(WeightProfileResponseCategoryWeightsInner.to_json())

# convert the object into a dict
weight_profile_response_category_weights_inner_dict = weight_profile_response_category_weights_inner_instance.to_dict()
# create an instance of WeightProfileResponseCategoryWeightsInner from a dict
weight_profile_response_category_weights_inner_from_dict = WeightProfileResponseCategoryWeightsInner.from_dict(weight_profile_response_category_weights_inner_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


