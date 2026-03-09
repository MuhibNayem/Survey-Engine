

# TenantOverviewResponse


## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
|**tenantId** | **String** |  |  [optional] |
|**name** | **String** |  |  [optional] |
|**primaryEmail** | **String** |  |  [optional] |
|**plan** | [**PlanEnum**](#PlanEnum) |  |  [optional] |
|**subscriptionStatus** | [**SubscriptionStatusEnum**](#SubscriptionStatusEnum) |  |  [optional] |
|**active** | **Boolean** |  |  [optional] |
|**createdAt** | **OffsetDateTime** |  |  [optional] |



## Enum: PlanEnum

| Name | Value |
|---- | -----|
| BASIC | &quot;BASIC&quot; |
| PRO | &quot;PRO&quot; |
| ENTERPRISE | &quot;ENTERPRISE&quot; |



## Enum: SubscriptionStatusEnum

| Name | Value |
|---- | -----|
| TRIAL | &quot;TRIAL&quot; |
| ACTIVE | &quot;ACTIVE&quot; |
| CANCELED | &quot;CANCELED&quot; |
| EXPIRED | &quot;EXPIRED&quot; |



