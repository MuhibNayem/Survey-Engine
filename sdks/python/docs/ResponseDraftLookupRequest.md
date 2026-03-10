# ResponseDraftLookupRequest


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**response_id** | **UUID** |  | [optional] 
**respondent_identifier** | **str** |  | [optional] 
**responder_token** | **str** |  | [optional] 
**responder_access_code** | **str** |  | [optional] 

## Example

```python
from openapi_client.models.response_draft_lookup_request import ResponseDraftLookupRequest

# TODO update the JSON string below
json = "{}"
# create an instance of ResponseDraftLookupRequest from a JSON string
response_draft_lookup_request_instance = ResponseDraftLookupRequest.from_json(json)
# print the JSON string representation of the object
print(ResponseDraftLookupRequest.to_json())

# convert the object into a dict
response_draft_lookup_request_dict = response_draft_lookup_request_instance.to_dict()
# create an instance of ResponseDraftLookupRequest from a dict
response_draft_lookup_request_from_dict = ResponseDraftLookupRequest.from_dict(response_draft_lookup_request_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


