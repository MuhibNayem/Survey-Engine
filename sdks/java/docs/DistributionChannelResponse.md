

# DistributionChannelResponse


## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
|**id** | **UUID** |  |  [optional] |
|**campaignId** | **UUID** |  |  [optional] |
|**channelType** | [**ChannelTypeEnum**](#ChannelTypeEnum) |  |  [optional] |
|**channelValue** | **String** |  |  [optional] |
|**createdAt** | **OffsetDateTime** |  |  [optional] |



## Enum: ChannelTypeEnum

| Name | Value |
|---- | -----|
| PUBLIC_LINK | &quot;PUBLIC_LINK&quot; |
| PRIVATE_LINK | &quot;PRIVATE_LINK&quot; |
| HTML_EMBED | &quot;HTML_EMBED&quot; |
| WORDPRESS_EMBED | &quot;WORDPRESS_EMBED&quot; |
| JS_EMBED | &quot;JS_EMBED&quot; |
| EMAIL | &quot;EMAIL&quot; |



