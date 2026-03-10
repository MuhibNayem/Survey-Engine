# FeatureDefinitionDTO


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**id** | **UUID** |  | [optional] 
**feature_key** | **str** |  | [optional] 
**feature_type** | [**FeatureType**](FeatureType.md) |  | [optional] 
**category** | [**FeatureCategory**](FeatureCategory.md) |  | [optional] 
**name** | **str** |  | [optional] 
**description** | **str** |  | [optional] 
**enabled** | **bool** |  | [optional] 
**rollout_percentage** | **int** |  | [optional] 
**min_plan** | **str** |  | [optional] 
**roles** | **List[str]** |  | [optional] 
**platforms** | **List[str]** |  | [optional] 
**metadata** | **Dict[str, object]** |  | [optional] 
**created_by** | **str** |  | [optional] 
**created_at** | **datetime** |  | [optional] 
**updated_by** | **str** |  | [optional] 
**updated_at** | **datetime** |  | [optional] 

## Example

```python
from openapi_client.models.feature_definition_dto import FeatureDefinitionDTO

# TODO update the JSON string below
json = "{}"
# create an instance of FeatureDefinitionDTO from a JSON string
feature_definition_dto_instance = FeatureDefinitionDTO.from_json(json)
# print the JSON string representation of the object
print(FeatureDefinitionDTO.to_json())

# convert the object into a dict
feature_definition_dto_dict = feature_definition_dto_instance.to_dict()
# create an instance of FeatureDefinitionDTO from a dict
feature_definition_dto_from_dict = FeatureDefinitionDTO.from_dict(feature_definition_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


