

# QuestionResponse


## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
|**id** | **UUID** |  |  [optional] |
|**text** | **String** |  |  [optional] |
|**type** | [**TypeEnum**](#TypeEnum) |  |  [optional] |
|**maxScore** | **BigDecimal** |  |  [optional] |
|**optionConfig** | **String** |  |  [optional] |
|**currentVersion** | **Integer** |  |  [optional] |
|**active** | **Boolean** |  |  [optional] |
|**createdBy** | **String** |  |  [optional] |
|**createdAt** | **OffsetDateTime** |  |  [optional] |
|**updatedBy** | **String** |  |  [optional] |
|**updatedAt** | **OffsetDateTime** |  |  [optional] |



## Enum: TypeEnum

| Name | Value |
|---- | -----|
| RANK | &quot;RANK&quot; |
| RATING_SCALE | &quot;RATING_SCALE&quot; |
| SINGLE_CHOICE | &quot;SINGLE_CHOICE&quot; |
| MULTIPLE_CHOICE | &quot;MULTIPLE_CHOICE&quot; |



