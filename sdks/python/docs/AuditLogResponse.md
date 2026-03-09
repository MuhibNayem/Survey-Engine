# AuditLogResponse


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**id** | **int** |  | [optional] 
**tenant_id** | **str** |  | [optional] 
**entity_type** | **str** |  | [optional] 
**entity_id** | **str** |  | [optional] 
**action** | **str** |  | [optional] 
**actor** | **str** |  | [optional] 
**reason** | **str** |  | [optional] 
**before_value** | **str** |  | [optional] 
**after_value** | **str** |  | [optional] 
**ip_address** | **str** |  | [optional] 
**created_at** | **datetime** |  | [optional] 

## Example

```python
from openapi_client.models.audit_log_response import AuditLogResponse

# TODO update the JSON string below
json = "{}"
# create an instance of AuditLogResponse from a JSON string
audit_log_response_instance = AuditLogResponse.from_json(json)
# print the JSON string representation of the object
print(AuditLogResponse.to_json())

# convert the object into a dict
audit_log_response_dict = audit_log_response_instance.to_dict()
# create an instance of AuditLogResponse from a dict
audit_log_response_from_dict = AuditLogResponse.from_dict(audit_log_response_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


