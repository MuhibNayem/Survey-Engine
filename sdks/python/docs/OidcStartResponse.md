# OidcStartResponse


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**authorization_url** | **str** |  | [optional] 
**state** | **str** |  | [optional] 
**expires_at** | **datetime** |  | [optional] 

## Example

```python
from openapi_client.models.oidc_start_response import OidcStartResponse

# TODO update the JSON string below
json = "{}"
# create an instance of OidcStartResponse from a JSON string
oidc_start_response_instance = OidcStartResponse.from_json(json)
# print the JSON string representation of the object
print(OidcStartResponse.to_json())

# convert the object into a dict
oidc_start_response_dict = oidc_start_response_instance.to_dict()
# create an instance of OidcStartResponse from a dict
oidc_start_response_from_dict = OidcStartResponse.from_dict(oidc_start_response_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


