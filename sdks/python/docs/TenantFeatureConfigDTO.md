# TenantFeatureConfigDTO


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**enabled** | **bool** |  | [optional] 
**rollout_percentage** | **int** |  | [optional] 
**custom_metadata** | **Dict[str, object]** |  | [optional] 

## Example

```python
from openapi_client.models.tenant_feature_config_dto import TenantFeatureConfigDTO

# TODO update the JSON string below
json = "{}"
# create an instance of TenantFeatureConfigDTO from a JSON string
tenant_feature_config_dto_instance = TenantFeatureConfigDTO.from_json(json)
# print the JSON string representation of the object
print(TenantFeatureConfigDTO.to_json())

# convert the object into a dict
tenant_feature_config_dto_dict = tenant_feature_config_dto_instance.to_dict()
# create an instance of TenantFeatureConfigDTO from a dict
tenant_feature_config_dto_from_dict = TenantFeatureConfigDTO.from_dict(tenant_feature_config_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


