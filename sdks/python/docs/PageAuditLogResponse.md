# PageAuditLogResponse


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**content** | [**List[AuditLogResponse]**](AuditLogResponse.md) |  | [optional] 
**pageable** | [**PageableObject**](PageableObject.md) |  | [optional] 
**sort** | [**SortObject**](SortObject.md) |  | [optional] 
**total_elements** | **int** |  | [optional] 
**total_pages** | **int** |  | [optional] 
**number** | **int** |  | [optional] 
**size** | **int** |  | [optional] 
**number_of_elements** | **int** |  | [optional] 
**first** | **bool** |  | [optional] 
**last** | **bool** |  | [optional] 
**empty** | **bool** |  | [optional] 

## Example

```python
from openapi_client.models.page_audit_log_response import PageAuditLogResponse

# TODO update the JSON string below
json = "{}"
# create an instance of PageAuditLogResponse from a JSON string
page_audit_log_response_instance = PageAuditLogResponse.from_json(json)
# print the JSON string representation of the object
print(PageAuditLogResponse.to_json())

# convert the object into a dict
page_audit_log_response_dict = page_audit_log_response_instance.to_dict()
# create an instance of PageAuditLogResponse from a dict
page_audit_log_response_from_dict = PageAuditLogResponse.from_dict(page_audit_log_response_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


