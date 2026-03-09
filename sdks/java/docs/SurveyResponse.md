

# SurveyResponse


## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
|**id** | **UUID** |  |  [optional] |
|**title** | **String** |  |  [optional] |
|**description** | **String** |  |  [optional] |
|**lifecycleState** | [**LifecycleStateEnum**](#LifecycleStateEnum) |  |  [optional] |
|**currentVersion** | **Integer** |  |  [optional] |
|**active** | **Boolean** |  |  [optional] |
|**pages** | [**List&lt;SurveyResponsePagesInner&gt;**](SurveyResponsePagesInner.md) |  |  [optional] |
|**createdBy** | **String** |  |  [optional] |
|**createdAt** | **OffsetDateTime** |  |  [optional] |
|**updatedBy** | **String** |  |  [optional] |
|**updatedAt** | **OffsetDateTime** |  |  [optional] |



## Enum: LifecycleStateEnum

| Name | Value |
|---- | -----|
| DRAFT | &quot;DRAFT&quot; |
| PUBLISHED | &quot;PUBLISHED&quot; |
| CLOSED | &quot;CLOSED&quot; |
| RESULTS_PUBLISHED | &quot;RESULTS_PUBLISHED&quot; |
| ARCHIVED | &quot;ARCHIVED&quot; |



