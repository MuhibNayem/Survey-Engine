# CreateFeatureRequest


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**feature_key** | **str** |  | 
**feature_type** | [**FeatureType**](FeatureType.md) |  | 
**category** | [**FeatureCategory**](FeatureCategory.md) |  | 
**name** | **str** |  | 
**description** | **str** |  | [optional] 
**enabled** | **bool** |  | [default to True]
**rollout_percentage** | **int** |  | [optional] [default to 100]
**min_plan** | **str** |  | [optional] [default to 'BASIC']
**roles** | **List[str]** |  | [optional] [default to [SUPER_ADMIN, ADMIN, EDITOR, VIEWER]]
**platforms** | **List[str]** |  | [optional] [default to [WEB]]
**metadata** | **Dict[str, object]** |  | [optional] 

## Example

```python
from openapi_client.models.create_feature_request import CreateFeatureRequest

# TODO update the JSON string below
json = "{}"
# create an instance of CreateFeatureRequest from a JSON string
create_feature_request_instance = CreateFeatureRequest.from_json(json)
# print the JSON string representation of the object
print(CreateFeatureRequest.to_json())

# convert the object into a dict
create_feature_request_dict = create_feature_request_instance.to_dict()
# create an instance of CreateFeatureRequest from a dict
create_feature_request_from_dict = CreateFeatureRequest.from_dict(create_feature_request_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


