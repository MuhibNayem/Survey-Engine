# FeatureAnalyticsDTO


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**feature_key** | **str** |  | [optional] 
**total_users** | **int** |  | [optional] 
**accessed_count** | **int** |  | [optional] 
**completed_count** | **int** |  | [optional] 
**completion_rate** | **float** |  | [optional] 
**average_access_count** | **float** |  | [optional] 
**last_accessed_at** | **datetime** |  | [optional] 

## Example

```python
from openapi_client.models.feature_analytics_dto import FeatureAnalyticsDTO

# TODO update the JSON string below
json = "{}"
# create an instance of FeatureAnalyticsDTO from a JSON string
feature_analytics_dto_instance = FeatureAnalyticsDTO.from_json(json)
# print the JSON string representation of the object
print(FeatureAnalyticsDTO.to_json())

# convert the object into a dict
feature_analytics_dto_dict = feature_analytics_dto_instance.to_dict()
# create an instance of FeatureAnalyticsDTO from a dict
feature_analytics_dto_from_dict = FeatureAnalyticsDTO.from_dict(feature_analytics_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


