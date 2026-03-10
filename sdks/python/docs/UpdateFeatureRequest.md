# UpdateFeatureRequest


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**name** | **str** |  | [optional] 
**description** | **str** |  | [optional] 
**enabled** | **bool** |  | [optional] 
**rollout_percentage** | **int** |  | [optional] 
**min_plan** | **str** |  | [optional] 
**roles** | **List[str]** |  | [optional] 
**platforms** | **List[str]** |  | [optional] 
**metadata** | **Dict[str, object]** |  | [optional] 

## Example

```python
from openapi_client.models.update_feature_request import UpdateFeatureRequest

# TODO update the JSON string below
json = "{}"
# create an instance of UpdateFeatureRequest from a JSON string
update_feature_request_instance = UpdateFeatureRequest.from_json(json)
# print the JSON string representation of the object
print(UpdateFeatureRequest.to_json())

# convert the object into a dict
update_feature_request_dict = update_feature_request_instance.to_dict()
# create an instance of UpdateFeatureRequest from a dict
update_feature_request_from_dict = UpdateFeatureRequest.from_dict(update_feature_request_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


