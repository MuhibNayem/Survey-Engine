# DataCollectionFieldResponse


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**id** | **UUID** |  | [optional] 
**field_key** | **str** |  | [optional] 
**label** | **str** |  | [optional] 
**field_type** | **str** |  | [optional] 
**required** | **bool** |  | [optional] 
**sort_order** | **int** |  | [optional] 
**enabled** | **bool** |  | [optional] 

## Example

```python
from openapi_client.models.data_collection_field_response import DataCollectionFieldResponse

# TODO update the JSON string below
json = "{}"
# create an instance of DataCollectionFieldResponse from a JSON string
data_collection_field_response_instance = DataCollectionFieldResponse.from_json(json)
# print the JSON string representation of the object
print(DataCollectionFieldResponse.to_json())

# convert the object into a dict
data_collection_field_response_dict = data_collection_field_response_instance.to_dict()
# create an instance of DataCollectionFieldResponse from a dict
data_collection_field_response_from_dict = DataCollectionFieldResponse.from_dict(data_collection_field_response_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


