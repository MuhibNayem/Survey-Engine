

# CampaignPreviewResponsePagesInnerQuestionsInner


## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
|**id** | **UUID** |  |  [optional] |
|**questionId** | **UUID** |  |  [optional] |
|**questionVersionId** | **UUID** |  |  [optional] |
|**categoryVersionId** | **UUID** |  |  [optional] |
|**text** | **String** |  |  [optional] |
|**type** | [**TypeEnum**](#TypeEnum) |  |  [optional] |
|**maxScore** | **BigDecimal** |  |  [optional] |
|**mandatory** | **Boolean** |  |  [optional] |
|**sortOrder** | **Integer** |  |  [optional] |
|**optionConfig** | **String** |  |  [optional] |
|**answerConfig** | **String** |  |  [optional] |



## Enum: TypeEnum

| Name | Value |
|---- | -----|
| RANK | &quot;RANK&quot; |
| RATING_SCALE | &quot;RATING_SCALE&quot; |
| SINGLE_CHOICE | &quot;SINGLE_CHOICE&quot; |
| MULTIPLE_CHOICE | &quot;MULTIPLE_CHOICE&quot; |



