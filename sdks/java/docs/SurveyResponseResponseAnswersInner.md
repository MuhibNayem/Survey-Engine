

# SurveyResponseResponseAnswersInner


## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
|**id** | **UUID** |  |  [optional] |
|**questionId** | **UUID** |  |  [optional] |
|**questionVersionId** | **UUID** |  |  [optional] |
|**questionVersionNumber** | **Integer** |  |  [optional] |
|**questionText** | **String** |  |  [optional] |
|**questionType** | [**QuestionTypeEnum**](#QuestionTypeEnum) |  |  [optional] |
|**optionConfig** | **String** |  |  [optional] |
|**value** | **String** |  |  [optional] |
|**remark** | **String** |  |  [optional] |
|**score** | **BigDecimal** |  |  [optional] |



## Enum: QuestionTypeEnum

| Name | Value |
|---- | -----|
| RANK | &quot;RANK&quot; |
| RATING_SCALE | &quot;RATING_SCALE&quot; |
| SINGLE_CHOICE | &quot;SINGLE_CHOICE&quot; |
| MULTIPLE_CHOICE | &quot;MULTIPLE_CHOICE&quot; |



