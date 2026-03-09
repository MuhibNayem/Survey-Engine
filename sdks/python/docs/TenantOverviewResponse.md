# TenantOverviewResponse


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**tenant_id** | **str** |  | [optional] 
**name** | **str** |  | [optional] 
**primary_email** | **str** |  | [optional] 
**plan** | **str** |  | [optional] 
**subscription_status** | **str** |  | [optional] 
**active** | **bool** |  | [optional] 
**created_at** | **datetime** |  | [optional] 

## Example

```python
from openapi_client.models.tenant_overview_response import TenantOverviewResponse

# TODO update the JSON string below
json = "{}"
# create an instance of TenantOverviewResponse from a JSON string
tenant_overview_response_instance = TenantOverviewResponse.from_json(json)
# print the JSON string representation of the object
print(TenantOverviewResponse.to_json())

# convert the object into a dict
tenant_overview_response_dict = tenant_overview_response_instance.to_dict()
# create an instance of TenantOverviewResponse from a dict
tenant_overview_response_from_dict = TenantOverviewResponse.from_dict(tenant_overview_response_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


