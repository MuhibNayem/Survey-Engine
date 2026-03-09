

# PlanDefinitionRequest


## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
|**planCode** | [**PlanCodeEnum**](#PlanCodeEnum) |  |  |
|**displayName** | **String** |  |  |
|**price** | **BigDecimal** |  |  |
|**currency** | **String** |  |  |
|**billingCycleDays** | **Integer** |  |  |
|**trialDays** | **Integer** |  |  |
|**maxCampaigns** | **Integer** |  |  [optional] |
|**maxResponsesPerCampaign** | **Integer** |  |  [optional] |
|**maxAdminUsers** | **Integer** |  |  [optional] |
|**active** | **Boolean** |  |  [optional] |



## Enum: PlanCodeEnum

| Name | Value |
|---- | -----|
| BASIC | &quot;BASIC&quot; |
| PRO | &quot;PRO&quot; |
| ENTERPRISE | &quot;ENTERPRISE&quot; |



