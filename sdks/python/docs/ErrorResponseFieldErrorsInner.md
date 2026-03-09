# ErrorResponseFieldErrorsInner


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**var_field** | **str** |  | [optional] 
**message** | **str** |  | [optional] 
**rejected_value** | **object** |  | [optional] 

## Example

```python
from openapi_client.models.error_response_field_errors_inner import ErrorResponseFieldErrorsInner

# TODO update the JSON string below
json = "{}"
# create an instance of ErrorResponseFieldErrorsInner from a JSON string
error_response_field_errors_inner_instance = ErrorResponseFieldErrorsInner.from_json(json)
# print the JSON string representation of the object
print(ErrorResponseFieldErrorsInner.to_json())

# convert the object into a dict
error_response_field_errors_inner_dict = error_response_field_errors_inner_instance.to_dict()
# create an instance of ErrorResponseFieldErrorsInner from a dict
error_response_field_errors_inner_from_dict = ErrorResponseFieldErrorsInner.from_dict(error_response_field_errors_inner_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


