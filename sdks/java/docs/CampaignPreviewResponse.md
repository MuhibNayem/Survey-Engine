

# CampaignPreviewResponse


## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
|**campaignId** | **UUID** |  |  [optional] |
|**tenantId** | **String** |  |  [optional] |
|**campaignName** | **String** |  |  [optional] |
|**campaignStatus** | [**CampaignStatusEnum**](#CampaignStatusEnum) |  |  [optional] |
|**authMode** | [**AuthModeEnum**](#AuthModeEnum) |  |  [optional] |
|**surveyId** | **UUID** |  |  [optional] |
|**surveyTitle** | **String** |  |  [optional] |
|**surveyDescription** | **String** |  |  [optional] |
|**showQuestionNumbers** | **Boolean** |  |  [optional] |
|**showProgressIndicator** | **Boolean** |  |  [optional] |
|**allowBackButton** | **Boolean** |  |  [optional] |
|**startMessage** | **String** |  |  [optional] |
|**finishMessage** | **String** |  |  [optional] |
|**headerHtml** | **String** |  |  [optional] |
|**footerHtml** | **String** |  |  [optional] |
|**theme** | [**SurveyThemeConfigDto**](SurveyThemeConfigDto.md) |  |  [optional] |
|**collectName** | **Boolean** |  |  [optional] |
|**collectEmail** | **Boolean** |  |  [optional] |
|**collectPhone** | **Boolean** |  |  [optional] |
|**collectAddress** | **Boolean** |  |  [optional] |
|**dataCollectionFields** | [**List&lt;DataCollectionFieldResponse&gt;**](DataCollectionFieldResponse.md) |  |  [optional] |
|**pages** | [**List&lt;CampaignPreviewResponsePagesInner&gt;**](CampaignPreviewResponsePagesInner.md) |  |  [optional] |



## Enum: CampaignStatusEnum

| Name | Value |
|---- | -----|
| DRAFT | &quot;DRAFT&quot; |
| ACTIVE | &quot;ACTIVE&quot; |
| PAUSED | &quot;PAUSED&quot; |
| COMPLETED | &quot;COMPLETED&quot; |
| ARCHIVED | &quot;ARCHIVED&quot; |



## Enum: AuthModeEnum

| Name | Value |
|---- | -----|
| PUBLIC | &quot;PUBLIC&quot; |
| PRIVATE | &quot;PRIVATE&quot; |
| SIGNED_TOKEN | &quot;SIGNED_TOKEN&quot; |
| SSO | &quot;SSO&quot; |



