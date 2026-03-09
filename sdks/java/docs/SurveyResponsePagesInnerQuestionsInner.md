

# SurveyResponsePagesInnerQuestionsInner


## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
|**id** | **UUID** |  |  [optional] |
|**questionId** | **UUID** |  |  [optional] |
|**questionVersionId** | **UUID** |  |  [optional] |
|**categoryId** | **UUID** |  |  [optional] |
|**categoryVersionId** | **UUID** |  |  [optional] |
|**categoryWeightPercentage** | **BigDecimal** |  |  [optional] |
|**sortOrder** | **Integer** |  |  [optional] |
|**mandatory** | **Boolean** |  |  [optional] |
|**answerConfig** | **String** |  |  [optional] |
|**pinnedQuestionText** | **String** |  |  [optional] |
|**pinnedQuestionType** | [**PinnedQuestionTypeEnum**](#PinnedQuestionTypeEnum) |  |  [optional] |
|**pinnedQuestionMaxScore** | **BigDecimal** |  |  [optional] |
|**pinnedQuestionOptionConfig** | **String** |  |  [optional] |
|**pinnedCategoryName** | **String** |  |  [optional] |
|**pinnedCategoryDescription** | **String** |  |  [optional] |



## Enum: PinnedQuestionTypeEnum

| Name | Value |
|---- | -----|
| RANK | &quot;RANK&quot; |
| RATING_SCALE | &quot;RATING_SCALE&quot; |
| SINGLE_CHOICE | &quot;SINGLE_CHOICE&quot; |
| MULTIPLE_CHOICE | &quot;MULTIPLE_CHOICE&quot; |



