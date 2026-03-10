# FeatureStatusDTO


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**feature_key** | **str** |  | [optional] 
**available** | **bool** |  | [optional] 
**completed** | **bool** |  | [optional] 
**accessed** | **bool** |  | [optional] 
**access_count** | **int** |  | [optional] 
**last_accessed_at** | **datetime** |  | [optional] 
**completed_at** | **datetime** |  | [optional] 

## Example

```python
from openapi_client.models.feature_status_dto import FeatureStatusDTO

# TODO update the JSON string below
json = "{}"
# create an instance of FeatureStatusDTO from a JSON string
feature_status_dto_instance = FeatureStatusDTO.from_json(json)
# print the JSON string representation of the object
print(FeatureStatusDTO.to_json())

# convert the object into a dict
feature_status_dto_dict = feature_status_dto_instance.to_dict()
# create an instance of FeatureStatusDTO from a dict
feature_status_dto_from_dict = FeatureStatusDTO.from_dict(feature_status_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


