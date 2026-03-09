# PlanDefinitionResponse


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**plan_code** | **str** |  | 
**display_name** | **str** |  | 
**price** | **float** |  | 
**currency** | **str** |  | 
**billing_cycle_days** | **int** |  | 
**trial_days** | **int** |  | 
**max_campaigns** | **int** |  | [optional] 
**max_responses_per_campaign** | **int** |  | [optional] 
**max_admin_users** | **int** |  | [optional] 
**active** | **bool** |  | [optional] [default to True]
**id** | **UUID** |  | [optional] 

## Example

```python
from openapi_client.models.plan_definition_response import PlanDefinitionResponse

# TODO update the JSON string below
json = "{}"
# create an instance of PlanDefinitionResponse from a JSON string
plan_definition_response_instance = PlanDefinitionResponse.from_json(json)
# print the JSON string representation of the object
print(PlanDefinitionResponse.to_json())

# convert the object into a dict
plan_definition_response_dict = plan_definition_response_instance.to_dict()
# create an instance of PlanDefinitionResponse from a dict
plan_definition_response_from_dict = PlanDefinitionResponse.from_dict(plan_definition_response_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


