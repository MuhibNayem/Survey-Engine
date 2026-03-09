# OidcStartRequest


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**tenant_id** | **str** |  | 
**campaign_id** | **UUID** |  | 
**return_path** | **str** |  | [optional] 

## Example

```python
from openapi_client.models.oidc_start_request import OidcStartRequest

# TODO update the JSON string below
json = "{}"
# create an instance of OidcStartRequest from a JSON string
oidc_start_request_instance = OidcStartRequest.from_json(json)
# print the JSON string representation of the object
print(OidcStartRequest.to_json())

# convert the object into a dict
oidc_start_request_dict = oidc_start_request_instance.to_dict()
# create an instance of OidcStartRequest from a dict
oidc_start_request_from_dict = OidcStartRequest.from_dict(oidc_start_request_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


