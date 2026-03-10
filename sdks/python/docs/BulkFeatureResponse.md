# BulkFeatureResponse


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**created** | **int** |  | [optional] 
**updated** | **int** |  | [optional] 
**failed** | **int** |  | [optional] 
**errors** | [**List[BulkFeatureResponseErrorsInner]**](BulkFeatureResponseErrorsInner.md) |  | [optional] 

## Example

```python
from openapi_client.models.bulk_feature_response import BulkFeatureResponse

# TODO update the JSON string below
json = "{}"
# create an instance of BulkFeatureResponse from a JSON string
bulk_feature_response_instance = BulkFeatureResponse.from_json(json)
# print the JSON string representation of the object
print(BulkFeatureResponse.to_json())

# convert the object into a dict
bulk_feature_response_dict = bulk_feature_response_instance.to_dict()
# create an instance of BulkFeatureResponse from a dict
bulk_feature_response_from_dict = BulkFeatureResponse.from_dict(bulk_feature_response_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


