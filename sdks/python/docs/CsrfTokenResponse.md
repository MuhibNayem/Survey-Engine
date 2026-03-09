# CsrfTokenResponse


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**token** | **str** |  | 

## Example

```python
from openapi_client.models.csrf_token_response import CsrfTokenResponse

# TODO update the JSON string below
json = "{}"
# create an instance of CsrfTokenResponse from a JSON string
csrf_token_response_instance = CsrfTokenResponse.from_json(json)
# print the JSON string representation of the object
print(CsrfTokenResponse.to_json())

# convert the object into a dict
csrf_token_response_dict = csrf_token_response_instance.to_dict()
# create an instance of CsrfTokenResponse from a dict
csrf_token_response_from_dict = CsrfTokenResponse.from_dict(csrf_token_response_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


