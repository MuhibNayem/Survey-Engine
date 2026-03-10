# UserPreferenceDTO


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**preferences** | **Dict[str, str]** |  | [optional] 
**feature_completion** | **Dict[str, bool]** |  | [optional] 

## Example

```python
from openapi_client.models.user_preference_dto import UserPreferenceDTO

# TODO update the JSON string below
json = "{}"
# create an instance of UserPreferenceDTO from a JSON string
user_preference_dto_instance = UserPreferenceDTO.from_json(json)
# print the JSON string representation of the object
print(UserPreferenceDTO.to_json())

# convert the object into a dict
user_preference_dto_dict = user_preference_dto_instance.to_dict()
# create an instance of UserPreferenceDTO from a dict
user_preference_dto_from_dict = UserPreferenceDTO.from_dict(user_preference_dto_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


