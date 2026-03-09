# ScoreResult


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**campaign_id** | **UUID** |  | [optional] 
**weight_profile_id** | **UUID** |  | [optional] 
**total_score** | **float** |  | [optional] 
**category_scores** | [**List[ScoreResultCategoryScoresInner]**](ScoreResultCategoryScoresInner.md) |  | [optional] 

## Example

```python
from openapi_client.models.score_result import ScoreResult

# TODO update the JSON string below
json = "{}"
# create an instance of ScoreResult from a JSON string
score_result_instance = ScoreResult.from_json(json)
# print the JSON string representation of the object
print(ScoreResult.to_json())

# convert the object into a dict
score_result_dict = score_result_instance.to_dict()
# create an instance of ScoreResult from a dict
score_result_from_dict = ScoreResult.from_dict(score_result_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


