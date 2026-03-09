

# SubscriptionResponse


## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
|**id** | **UUID** |  |  [optional] |
|**tenantId** | **String** |  |  [optional] |
|**plan** | [**PlanEnum**](#PlanEnum) |  |  [optional] |
|**status** | [**StatusEnum**](#StatusEnum) |  |  [optional] |
|**currentPeriodStart** | **OffsetDateTime** |  |  [optional] |
|**currentPeriodEnd** | **OffsetDateTime** |  |  [optional] |
|**lastPaymentGatewayReference** | **String** |  |  [optional] |
|**planPrice** | **BigDecimal** |  |  [optional] |
|**currency** | **String** |  |  [optional] |
|**maxCampaigns** | **Integer** |  |  [optional] |
|**maxResponsesPerCampaign** | **Integer** |  |  [optional] |
|**maxAdminUsers** | **Integer** |  |  [optional] |



## Enum: PlanEnum

| Name | Value |
|---- | -----|
| BASIC | &quot;BASIC&quot; |
| PRO | &quot;PRO&quot; |
| ENTERPRISE | &quot;ENTERPRISE&quot; |



## Enum: StatusEnum

| Name | Value |
|---- | -----|
| TRIAL | &quot;TRIAL&quot; |
| ACTIVE | &quot;ACTIVE&quot; |
| CANCELED | &quot;CANCELED&quot; |
| EXPIRED | &quot;EXPIRED&quot; |



