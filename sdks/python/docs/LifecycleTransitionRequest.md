# LifecycleTransitionRequest


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**target_state** | **str** |  | 
**reason** | **str** |  | [optional] 

## Example

```python
from openapi_client.models.lifecycle_transition_request import LifecycleTransitionRequest

# TODO update the JSON string below
json = "{}"
# create an instance of LifecycleTransitionRequest from a JSON string
lifecycle_transition_request_instance = LifecycleTransitionRequest.from_json(json)
# print the JSON string representation of the object
print(LifecycleTransitionRequest.to_json())

# convert the object into a dict
lifecycle_transition_request_dict = lifecycle_transition_request_instance.to_dict()
# create an instance of LifecycleTransitionRequest from a dict
lifecycle_transition_request_from_dict = LifecycleTransitionRequest.from_dict(lifecycle_transition_request_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


