# ResponseSubmissionRequest


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**response_id** | **UUID** |  | [optional] 
**campaign_id** | **UUID** |  | 
**respondent_identifier** | **str** |  | [optional] 
**respondent_ip** | **str** |  | [optional] 
**respondent_device_fingerprint** | **str** |  | [optional] 
**responder_token** | **str** |  | [optional] 
**responder_access_code** | **str** |  | [optional] 
**respondent_metadata** | **Dict[str, str]** |  | [optional] 
**answers** | [**List[ResponseSubmissionRequestAnswersInner]**](ResponseSubmissionRequestAnswersInner.md) |  | [optional] 

## Example

```python
from openapi_client.models.response_submission_request import ResponseSubmissionRequest

# TODO update the JSON string below
json = "{}"
# create an instance of ResponseSubmissionRequest from a JSON string
response_submission_request_instance = ResponseSubmissionRequest.from_json(json)
# print the JSON string representation of the object
print(ResponseSubmissionRequest.to_json())

# convert the object into a dict
response_submission_request_dict = response_submission_request_instance.to_dict()
# create an instance of ResponseSubmissionRequest from a dict
response_submission_request_from_dict = ResponseSubmissionRequest.from_dict(response_submission_request_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


