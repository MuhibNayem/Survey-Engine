# CategoryWeightRequest


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**category_id** | **UUID** |  | 
**weight_percentage** | **float** |  | 

## Example

```python
from openapi_client.models.category_weight_request import CategoryWeightRequest

# TODO update the JSON string below
json = "{}"
# create an instance of CategoryWeightRequest from a JSON string
category_weight_request_instance = CategoryWeightRequest.from_json(json)
# print the JSON string representation of the object
print(CategoryWeightRequest.to_json())

# convert the object into a dict
category_weight_request_dict = category_weight_request_instance.to_dict()
# create an instance of CategoryWeightRequest from a dict
category_weight_request_from_dict = CategoryWeightRequest.from_dict(category_weight_request_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


