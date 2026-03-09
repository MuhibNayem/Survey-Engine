# AuthConfigAudit


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**id** | **UUID** |  | [optional] 
**auth_profile_id** | **UUID** |  | [optional] 
**action** | **str** |  | [optional] 
**changed_by** | **str** |  | [optional] 
**before_value** | **str** |  | [optional] 
**after_value** | **str** |  | [optional] 
**changed_at** | **datetime** |  | [optional] 

## Example

```python
from openapi_client.models.auth_config_audit import AuthConfigAudit

# TODO update the JSON string below
json = "{}"
# create an instance of AuthConfigAudit from a JSON string
auth_config_audit_instance = AuthConfigAudit.from_json(json)
# print the JSON string representation of the object
print(AuthConfigAudit.to_json())

# convert the object into a dict
auth_config_audit_dict = auth_config_audit_instance.to_dict()
# create an instance of AuthConfigAudit from a dict
auth_config_audit_from_dict = AuthConfigAudit.from_dict(auth_config_audit_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


