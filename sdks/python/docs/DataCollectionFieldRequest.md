# DataCollectionFieldRequest


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**field_key** | **str** |  | [optional] 
**label** | **str** |  | [optional] 
**field_type** | **str** |  | [optional] 
**required** | **bool** |  | [optional] 
**sort_order** | **int** |  | [optional] 
**enabled** | **bool** |  | [optional] 

## Example

```python
from openapi_client.models.data_collection_field_request import DataCollectionFieldRequest

# TODO update the JSON string below
json = "{}"
# create an instance of DataCollectionFieldRequest from a JSON string
data_collection_field_request_instance = DataCollectionFieldRequest.from_json(json)
# print the JSON string representation of the object
print(DataCollectionFieldRequest.to_json())

# convert the object into a dict
data_collection_field_request_dict = data_collection_field_request_instance.to_dict()
# create an instance of DataCollectionFieldRequest from a dict
data_collection_field_request_from_dict = DataCollectionFieldRequest.from_dict(data_collection_field_request_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


