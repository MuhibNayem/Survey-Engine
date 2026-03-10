

# SurveyResponseResponse


## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
|**id** | **UUID** |  |  [optional] |
|**campaignId** | **UUID** |  |  [optional] |
|**surveySnapshotId** | **UUID** |  |  [optional] |
|**respondentIdentifier** | **String** |  |  [optional] |
|**respondentMetadata** | **String** |  |  [optional] |
|**status** | [**StatusEnum**](#StatusEnum) |  |  [optional] |
|**startedAt** | **OffsetDateTime** |  |  [optional] |
|**submittedAt** | **OffsetDateTime** |  |  [optional] |
|**lockedAt** | **OffsetDateTime** |  |  [optional] |
|**weightProfileId** | **UUID** |  |  [optional] |
|**weightedTotalScore** | **BigDecimal** |  |  [optional] |
|**scoredAt** | **OffsetDateTime** |  |  [optional] |
|**answers** | [**List&lt;SurveyResponseResponseAnswersInner&gt;**](SurveyResponseResponseAnswersInner.md) |  |  [optional] |



## Enum: StatusEnum

| Name | Value |
|---- | -----|
| IN_PROGRESS | &quot;IN_PROGRESS&quot; |
| SUBMITTED | &quot;SUBMITTED&quot; |
| LOCKED | &quot;LOCKED&quot; |
| REOPENED | &quot;REOPENED&quot; |



