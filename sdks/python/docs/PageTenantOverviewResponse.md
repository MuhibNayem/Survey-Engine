# PageTenantOverviewResponse


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**content** | [**List[TenantOverviewResponse]**](TenantOverviewResponse.md) |  | [optional] 
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
from openapi_client.models.page_tenant_overview_response import PageTenantOverviewResponse

# TODO update the JSON string below
json = "{}"
# create an instance of PageTenantOverviewResponse from a JSON string
page_tenant_overview_response_instance = PageTenantOverviewResponse.from_json(json)
# print the JSON string representation of the object
print(PageTenantOverviewResponse.to_json())

# convert the object into a dict
page_tenant_overview_response_dict = page_tenant_overview_response_instance.to_dict()
# create an instance of PageTenantOverviewResponse from a dict
page_tenant_overview_response_from_dict = PageTenantOverviewResponse.from_dict(page_tenant_overview_response_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


