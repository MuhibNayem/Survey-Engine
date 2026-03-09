# ScoreResultCategoryScoresInner


## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**category_id** | **UUID** |  | [optional] 
**raw_score** | **float** |  | [optional] 
**max_score** | **float** |  | [optional] 
**normalized_score** | **float** |  | [optional] 
**weight_percentage** | **float** |  | [optional] 
**weighted_score** | **float** |  | [optional] 

## Example

```python
from openapi_client.models.score_result_category_scores_inner import ScoreResultCategoryScoresInner

# TODO update the JSON string below
json = "{}"
# create an instance of ScoreResultCategoryScoresInner from a JSON string
score_result_category_scores_inner_instance = ScoreResultCategoryScoresInner.from_json(json)
# print the JSON string representation of the object
print(ScoreResultCategoryScoresInner.to_json())

# convert the object into a dict
score_result_category_scores_inner_dict = score_result_category_scores_inner_instance.to_dict()
# create an instance of ScoreResultCategoryScoresInner from a dict
score_result_category_scores_inner_from_dict = ScoreResultCategoryScoresInner.from_dict(score_result_category_scores_inner_dict)
```
[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


