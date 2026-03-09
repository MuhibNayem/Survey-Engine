# SubscriptionResponse


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**id** | **UUID** |  | [optional] 
**tenant_id** | **str** |  | [optional] 
**plan** | **str** |  | [optional] 
**status** | **str** |  | [optional] 
**current_period_start** | **datetime** |  | [optional] 
**current_period_end** | **datetime** |  | [optional] 
**last_payment_gateway_reference** | **str** |  | [optional] 
**plan_price** | **float** |  | [optional] 
**currency** | **str** |  | [optional] 
**max_campaigns** | **int** |  | [optional] 
**max_responses_per_campaign** | **int** |  | [optional] 
**max_admin_users** | **int** |  | [optional] 

## Example

```python
from openapi_client.models.subscription_response import SubscriptionResponse

# TODO update the JSON string below
json = "{}"
# create an instance of SubscriptionResponse from a JSON string
subscription_response_instance = SubscriptionResponse.from_json(json)
# print the JSON string representation of the object
print(SubscriptionResponse.to_json())

# convert the object into a dict
subscription_response_dict = subscription_response_instance.to_dict()
# create an instance of SubscriptionResponse from a dict
subscription_response_from_dict = SubscriptionResponse.from_dict(subscription_response_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


