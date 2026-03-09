

# AuthProfileRequest


## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
|**tenantId** | **String** |  |  |
|**authMode** | [**AuthModeEnum**](#AuthModeEnum) |  |  |
|**issuer** | **String** |  |  [optional] |
|**audience** | **String** |  |  [optional] |
|**jwksEndpoint** | **String** |  |  [optional] |
|**oidcDiscoveryUrl** | **String** |  |  [optional] |
|**oidcClientId** | **String** |  |  [optional] |
|**oidcClientSecret** | **String** |  |  [optional] |
|**oidcRedirectUri** | **String** |  |  [optional] |
|**oidcScopes** | **String** |  |  [optional] |
|**clockSkewSeconds** | **Integer** |  |  [optional] |
|**tokenTtlSeconds** | **Integer** |  |  [optional] |
|**signingSecret** | **String** |  |  [optional] |
|**fallbackPolicy** | [**FallbackPolicyEnum**](#FallbackPolicyEnum) |  |  [optional] |
|**claimMappings** | [**List&lt;ClaimMappingRequest&gt;**](ClaimMappingRequest.md) |  |  [optional] |



## Enum: AuthModeEnum

| Name | Value |
|---- | -----|
| PUBLIC_ANONYMOUS | &quot;PUBLIC_ANONYMOUS&quot; |
| SIGNED_LAUNCH_TOKEN | &quot;SIGNED_LAUNCH_TOKEN&quot; |
| EXTERNAL_SSO_TRUST | &quot;EXTERNAL_SSO_TRUST&quot; |



## Enum: FallbackPolicyEnum

| Name | Value |
|---- | -----|
| SSO_REQUIRED | &quot;SSO_REQUIRED&quot; |
| ANONYMOUS_FALLBACK | &quot;ANONYMOUS_FALLBACK&quot; |
| DISABLE_ON_FAILURE | &quot;DISABLE_ON_FAILURE&quot; |



