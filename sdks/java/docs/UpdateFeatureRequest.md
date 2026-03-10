

# UpdateFeatureRequest


## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
|**name** | **String** |  |  [optional] |
|**description** | **String** |  |  [optional] |
|**enabled** | **Boolean** |  |  [optional] |
|**rolloutPercentage** | **Integer** |  |  [optional] |
|**minPlan** | [**MinPlanEnum**](#MinPlanEnum) |  |  [optional] |
|**roles** | [**List&lt;RolesEnum&gt;**](#List&lt;RolesEnum&gt;) |  |  [optional] |
|**platforms** | [**List&lt;PlatformsEnum&gt;**](#List&lt;PlatformsEnum&gt;) |  |  [optional] |
|**metadata** | **Map&lt;String, Object&gt;** |  |  [optional] |



## Enum: MinPlanEnum

| Name | Value |
|---- | -----|
| BASIC | &quot;BASIC&quot; |
| PRO | &quot;PRO&quot; |
| ENTERPRISE | &quot;ENTERPRISE&quot; |



## Enum: List&lt;RolesEnum&gt;

| Name | Value |
|---- | -----|
| SUPER_ADMIN | &quot;SUPER_ADMIN&quot; |
| ADMIN | &quot;ADMIN&quot; |
| EDITOR | &quot;EDITOR&quot; |
| VIEWER | &quot;VIEWER&quot; |



## Enum: List&lt;PlatformsEnum&gt;

| Name | Value |
|---- | -----|
| WEB | &quot;WEB&quot; |
| MOBILE | &quot;MOBILE&quot; |
| DESKTOP | &quot;DESKTOP&quot; |



