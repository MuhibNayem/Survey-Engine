

# CampaignResponse


## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
|**id** | **UUID** |  |  [optional] |
|**name** | **String** |  |  [optional] |
|**description** | **String** |  |  [optional] |
|**surveyId** | **UUID** |  |  [optional] |
|**surveySnapshotId** | **UUID** |  |  [optional] |
|**defaultWeightProfileId** | **UUID** |  |  [optional] |
|**authMode** | [**AuthModeEnum**](#AuthModeEnum) |  |  [optional] |
|**status** | [**StatusEnum**](#StatusEnum) |  |  [optional] |
|**active** | **Boolean** |  |  [optional] |
|**createdBy** | **String** |  |  [optional] |
|**createdAt** | **OffsetDateTime** |  |  [optional] |
|**updatedBy** | **String** |  |  [optional] |
|**updatedAt** | **OffsetDateTime** |  |  [optional] |
|**dataCollectionFields** | [**List&lt;DataCollectionFieldResponse&gt;**](DataCollectionFieldResponse.md) |  |  [optional] |



## Enum: AuthModeEnum

| Name | Value |
|---- | -----|
| PUBLIC | &quot;PUBLIC&quot; |
| PRIVATE | &quot;PRIVATE&quot; |
| SIGNED_TOKEN | &quot;SIGNED_TOKEN&quot; |
| SSO | &quot;SSO&quot; |



## Enum: StatusEnum

| Name | Value |
|---- | -----|
| DRAFT | &quot;DRAFT&quot; |
| ACTIVE | &quot;ACTIVE&quot; |
| PAUSED | &quot;PAUSED&quot; |
| COMPLETED | &quot;COMPLETED&quot; |
| ARCHIVED | &quot;ARCHIVED&quot; |



