# OidcCallbackResponse


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**access_code** | **str** |  | [optional] 
**tenant_id** | **str** |  | [optional] 
**campaign_id** | **UUID** |  | [optional] 
**respondent_id** | **str** |  | [optional] 
**email** | **str** |  | [optional] 
**expires_at** | **datetime** |  | [optional] 
**redirect_url** | **str** |  | [optional] 

## Example

```python
from openapi_client.models.oidc_callback_response import OidcCallbackResponse

# TODO update the JSON string below
json = "{}"
# create an instance of OidcCallbackResponse from a JSON string
oidc_callback_response_instance = OidcCallbackResponse.from_json(json)
# print the JSON string representation of the object
print(OidcCallbackResponse.to_json())

# convert the object into a dict
oidc_callback_response_dict = oidc_callback_response_instance.to_dict()
# create an instance of OidcCallbackResponse from a dict
oidc_callback_response_from_dict = OidcCallbackResponse.from_dict(oidc_callback_response_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


