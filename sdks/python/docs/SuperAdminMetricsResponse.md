# SuperAdminMetricsResponse


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**total_tenants** | **int** |  | [optional] 
**active_subscriptions** | **int** |  | [optional] 
**total_platform_usage** | **int** |  | [optional] 

## Example

```python
from openapi_client.models.super_admin_metrics_response import SuperAdminMetricsResponse

# TODO update the JSON string below
json = "{}"
# create an instance of SuperAdminMetricsResponse from a JSON string
super_admin_metrics_response_instance = SuperAdminMetricsResponse.from_json(json)
# print the JSON string representation of the object
print(SuperAdminMetricsResponse.to_json())

# convert the object into a dict
super_admin_metrics_response_dict = super_admin_metrics_response_instance.to_dict()
# create an instance of SuperAdminMetricsResponse from a dict
super_admin_metrics_response_from_dict = SuperAdminMetricsResponse.from_dict(super_admin_metrics_response_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


