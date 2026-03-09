

# AuthResponse


## Properties

| Name | Type | Description | Notes |
|------------ | ------------- | ------------- | -------------|
|**userId** | **UUID** |  |  [optional] |
|**email** | **String** |  |  [optional] |
|**fullName** | **String** |  |  [optional] |
|**tenantId** | **String** |  |  [optional] |
|**role** | [**RoleEnum**](#RoleEnum) |  |  [optional] |
|**accessToken** | **String** |  |  [optional] |
|**refreshToken** | **String** |  |  [optional] |
|**tokenType** | **String** |  |  [optional] |
|**expiresIn** | **Long** |  |  [optional] |



## Enum: RoleEnum

| Name | Value |
|---- | -----|
| SUPER_ADMIN | &quot;SUPER_ADMIN&quot; |
| ADMIN | &quot;ADMIN&quot; |
| EDITOR | &quot;EDITOR&quot; |
| VIEWER | &quot;VIEWER&quot; |



