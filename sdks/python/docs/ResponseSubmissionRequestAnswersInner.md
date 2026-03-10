# ResponseSubmissionRequestAnswersInner


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**question_id** | **UUID** |  | 
**question_version_id** | **UUID** |  | [optional] 
**value** | **str** |  | [optional] 
**remark** | **str** |  | [optional] 
**score** | **float** |  | [optional] 

## Example

```python
from openapi_client.models.response_submission_request_answers_inner import ResponseSubmissionRequestAnswersInner

# TODO update the JSON string below
json = "{}"
# create an instance of ResponseSubmissionRequestAnswersInner from a JSON string
response_submission_request_answers_inner_instance = ResponseSubmissionRequestAnswersInner.from_json(json)
# print the JSON string representation of the object
print(ResponseSubmissionRequestAnswersInner.to_json())

# convert the object into a dict
response_submission_request_answers_inner_dict = response_submission_request_answers_inner_instance.to_dict()
# create an instance of ResponseSubmissionRequestAnswersInner from a dict
response_submission_request_answers_inner_from_dict = ResponseSubmissionRequestAnswersInner.from_dict(response_submission_request_answers_inner_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


