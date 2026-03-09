# ReopenRequest


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**reason** | **str** |  | 
**reopen_window_minutes** | **int** |  | [optional] 

## Example

```python
from openapi_client.models.reopen_request import ReopenRequest

# TODO update the JSON string below
json = "{}"
# create an instance of ReopenRequest from a JSON string
reopen_request_instance = ReopenRequest.from_json(json)
# print the JSON string representation of the object
print(ReopenRequest.to_json())

# convert the object into a dict
reopen_request_dict = reopen_request_instance.to_dict()
# create an instance of ReopenRequest from a dict
reopen_request_from_dict = ReopenRequest.from_dict(reopen_request_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


