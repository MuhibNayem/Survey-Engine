# BulkFeatureResponseErrorsInner


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**feature_key** | **str** |  | [optional] 
**error** | **str** |  | [optional] 

## Example

```python
from openapi_client.models.bulk_feature_response_errors_inner import BulkFeatureResponseErrorsInner

# TODO update the JSON string below
json = "{}"
# create an instance of BulkFeatureResponseErrorsInner from a JSON string
bulk_feature_response_errors_inner_instance = BulkFeatureResponseErrorsInner.from_json(json)
# print the JSON string representation of the object
print(BulkFeatureResponseErrorsInner.to_json())

# convert the object into a dict
bulk_feature_response_errors_inner_dict = bulk_feature_response_errors_inner_instance.to_dict()
# create an instance of BulkFeatureResponseErrorsInner from a dict
bulk_feature_response_errors_inner_from_dict = BulkFeatureResponseErrorsInner.from_dict(bulk_feature_response_errors_inner_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


