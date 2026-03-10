# ResponderSessionStatusResponse


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**authenticated** | **bool** |  | [optional] 
**email** | **str** |  | [optional] 

## Example

```python
from openapi_client.models.responder_session_status_response import ResponderSessionStatusResponse

# TODO update the JSON string below
json = "{}"
# create an instance of ResponderSessionStatusResponse from a JSON string
responder_session_status_response_instance = ResponderSessionStatusResponse.from_json(json)
# print the JSON string representation of the object
print(ResponderSessionStatusResponse.to_json())

# convert the object into a dict
responder_session_status_response_dict = responder_session_status_response_instance.to_dict()
# create an instance of ResponderSessionStatusResponse from a dict
responder_session_status_response_from_dict = ResponderSessionStatusResponse.from_dict(responder_session_status_response_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


