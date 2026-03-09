# AuthProfileRequest

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**TenantId** | **string** |  | 
**AuthMode** | **string** |  | 
**Issuer** | Pointer to **string** |  | [optional] 
**Audience** | Pointer to **string** |  | [optional] 
**JwksEndpoint** | Pointer to **string** |  | [optional] 
**OidcDiscoveryUrl** | Pointer to **string** |  | [optional] 
**OidcClientId** | Pointer to **string** |  | [optional] 
**OidcClientSecret** | Pointer to **string** |  | [optional] 
**OidcRedirectUri** | Pointer to **string** |  | [optional] 
**OidcScopes** | Pointer to **string** |  | [optional] 
**ClockSkewSeconds** | Pointer to **int32** |  | [optional] 
**TokenTtlSeconds** | Pointer to **int32** |  | [optional] 
**SigningSecret** | Pointer to **string** |  | [optional] 
**FallbackPolicy** | Pointer to **string** |  | [optional] 
**ClaimMappings** | Pointer to [**[]ClaimMappingRequest**](ClaimMappingRequest.md) |  | [optional] 

## Methods

### NewAuthProfileRequest

`func NewAuthProfileRequest(tenantId string, authMode string, ) *AuthProfileRequest`

NewAuthProfileRequest instantiates a new AuthProfileRequest object
This constructor will assign default values to properties that have it defined,
and makes sure properties required by API are set, but the set of arguments
will change when the set of required properties is changed

### NewAuthProfileRequestWithDefaults

`func NewAuthProfileRequestWithDefaults() *AuthProfileRequest`

NewAuthProfileRequestWithDefaults instantiates a new AuthProfileRequest object
This constructor will only assign default values to properties that have it defined,
but it doesn't guarantee that properties required by API are set

### GetTenantId

`func (o *AuthProfileRequest) GetTenantId() string`

GetTenantId returns the TenantId field if non-nil, zero value otherwise.

### GetTenantIdOk

`func (o *AuthProfileRequest) GetTenantIdOk() (*string, bool)`

GetTenantIdOk returns a tuple with the TenantId field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetTenantId

`func (o *AuthProfileRequest) SetTenantId(v string)`

SetTenantId sets TenantId field to given value.


### GetAuthMode

`func (o *AuthProfileRequest) GetAuthMode() string`

GetAuthMode returns the AuthMode field if non-nil, zero value otherwise.

### GetAuthModeOk

`func (o *AuthProfileRequest) GetAuthModeOk() (*string, bool)`

GetAuthModeOk returns a tuple with the AuthMode field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetAuthMode

`func (o *AuthProfileRequest) SetAuthMode(v string)`

SetAuthMode sets AuthMode field to given value.


### GetIssuer

`func (o *AuthProfileRequest) GetIssuer() string`

GetIssuer returns the Issuer field if non-nil, zero value otherwise.

### GetIssuerOk

`func (o *AuthProfileRequest) GetIssuerOk() (*string, bool)`

GetIssuerOk returns a tuple with the Issuer field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetIssuer

`func (o *AuthProfileRequest) SetIssuer(v string)`

SetIssuer sets Issuer field to given value.

### HasIssuer

`func (o *AuthProfileRequest) HasIssuer() bool`

HasIssuer returns a boolean if a field has been set.

### GetAudience

`func (o *AuthProfileRequest) GetAudience() string`

GetAudience returns the Audience field if non-nil, zero value otherwise.

### GetAudienceOk

`func (o *AuthProfileRequest) GetAudienceOk() (*string, bool)`

GetAudienceOk returns a tuple with the Audience field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetAudience

`func (o *AuthProfileRequest) SetAudience(v string)`

SetAudience sets Audience field to given value.

### HasAudience

`func (o *AuthProfileRequest) HasAudience() bool`

HasAudience returns a boolean if a field has been set.

### GetJwksEndpoint

`func (o *AuthProfileRequest) GetJwksEndpoint() string`

GetJwksEndpoint returns the JwksEndpoint field if non-nil, zero value otherwise.

### GetJwksEndpointOk

`func (o *AuthProfileRequest) GetJwksEndpointOk() (*string, bool)`

GetJwksEndpointOk returns a tuple with the JwksEndpoint field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetJwksEndpoint

`func (o *AuthProfileRequest) SetJwksEndpoint(v string)`

SetJwksEndpoint sets JwksEndpoint field to given value.

### HasJwksEndpoint

`func (o *AuthProfileRequest) HasJwksEndpoint() bool`

HasJwksEndpoint returns a boolean if a field has been set.

### GetOidcDiscoveryUrl

`func (o *AuthProfileRequest) GetOidcDiscoveryUrl() string`

GetOidcDiscoveryUrl returns the OidcDiscoveryUrl field if non-nil, zero value otherwise.

### GetOidcDiscoveryUrlOk

`func (o *AuthProfileRequest) GetOidcDiscoveryUrlOk() (*string, bool)`

GetOidcDiscoveryUrlOk returns a tuple with the OidcDiscoveryUrl field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetOidcDiscoveryUrl

`func (o *AuthProfileRequest) SetOidcDiscoveryUrl(v string)`

SetOidcDiscoveryUrl sets OidcDiscoveryUrl field to given value.

### HasOidcDiscoveryUrl

`func (o *AuthProfileRequest) HasOidcDiscoveryUrl() bool`

HasOidcDiscoveryUrl returns a boolean if a field has been set.

### GetOidcClientId

`func (o *AuthProfileRequest) GetOidcClientId() string`

GetOidcClientId returns the OidcClientId field if non-nil, zero value otherwise.

### GetOidcClientIdOk

`func (o *AuthProfileRequest) GetOidcClientIdOk() (*string, bool)`

GetOidcClientIdOk returns a tuple with the OidcClientId field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetOidcClientId

`func (o *AuthProfileRequest) SetOidcClientId(v string)`

SetOidcClientId sets OidcClientId field to given value.

### HasOidcClientId

`func (o *AuthProfileRequest) HasOidcClientId() bool`

HasOidcClientId returns a boolean if a field has been set.

### GetOidcClientSecret

`func (o *AuthProfileRequest) GetOidcClientSecret() string`

GetOidcClientSecret returns the OidcClientSecret field if non-nil, zero value otherwise.

### GetOidcClientSecretOk

`func (o *AuthProfileRequest) GetOidcClientSecretOk() (*string, bool)`

GetOidcClientSecretOk returns a tuple with the OidcClientSecret field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetOidcClientSecret

`func (o *AuthProfileRequest) SetOidcClientSecret(v string)`

SetOidcClientSecret sets OidcClientSecret field to given value.

### HasOidcClientSecret

`func (o *AuthProfileRequest) HasOidcClientSecret() bool`

HasOidcClientSecret returns a boolean if a field has been set.

### GetOidcRedirectUri

`func (o *AuthProfileRequest) GetOidcRedirectUri() string`

GetOidcRedirectUri returns the OidcRedirectUri field if non-nil, zero value otherwise.

### GetOidcRedirectUriOk

`func (o *AuthProfileRequest) GetOidcRedirectUriOk() (*string, bool)`

GetOidcRedirectUriOk returns a tuple with the OidcRedirectUri field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetOidcRedirectUri

`func (o *AuthProfileRequest) SetOidcRedirectUri(v string)`

SetOidcRedirectUri sets OidcRedirectUri field to given value.

### HasOidcRedirectUri

`func (o *AuthProfileRequest) HasOidcRedirectUri() bool`

HasOidcRedirectUri returns a boolean if a field has been set.

### GetOidcScopes

`func (o *AuthProfileRequest) GetOidcScopes() string`

GetOidcScopes returns the OidcScopes field if non-nil, zero value otherwise.

### GetOidcScopesOk

`func (o *AuthProfileRequest) GetOidcScopesOk() (*string, bool)`

GetOidcScopesOk returns a tuple with the OidcScopes field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetOidcScopes

`func (o *AuthProfileRequest) SetOidcScopes(v string)`

SetOidcScopes sets OidcScopes field to given value.

### HasOidcScopes

`func (o *AuthProfileRequest) HasOidcScopes() bool`

HasOidcScopes returns a boolean if a field has been set.

### GetClockSkewSeconds

`func (o *AuthProfileRequest) GetClockSkewSeconds() int32`

GetClockSkewSeconds returns the ClockSkewSeconds field if non-nil, zero value otherwise.

### GetClockSkewSecondsOk

`func (o *AuthProfileRequest) GetClockSkewSecondsOk() (*int32, bool)`

GetClockSkewSecondsOk returns a tuple with the ClockSkewSeconds field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetClockSkewSeconds

`func (o *AuthProfileRequest) SetClockSkewSeconds(v int32)`

SetClockSkewSeconds sets ClockSkewSeconds field to given value.

### HasClockSkewSeconds

`func (o *AuthProfileRequest) HasClockSkewSeconds() bool`

HasClockSkewSeconds returns a boolean if a field has been set.

### GetTokenTtlSeconds

`func (o *AuthProfileRequest) GetTokenTtlSeconds() int32`

GetTokenTtlSeconds returns the TokenTtlSeconds field if non-nil, zero value otherwise.

### GetTokenTtlSecondsOk

`func (o *AuthProfileRequest) GetTokenTtlSecondsOk() (*int32, bool)`

GetTokenTtlSecondsOk returns a tuple with the TokenTtlSeconds field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetTokenTtlSeconds

`func (o *AuthProfileRequest) SetTokenTtlSeconds(v int32)`

SetTokenTtlSeconds sets TokenTtlSeconds field to given value.

### HasTokenTtlSeconds

`func (o *AuthProfileRequest) HasTokenTtlSeconds() bool`

HasTokenTtlSeconds returns a boolean if a field has been set.

### GetSigningSecret

`func (o *AuthProfileRequest) GetSigningSecret() string`

GetSigningSecret returns the SigningSecret field if non-nil, zero value otherwise.

### GetSigningSecretOk

`func (o *AuthProfileRequest) GetSigningSecretOk() (*string, bool)`

GetSigningSecretOk returns a tuple with the SigningSecret field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetSigningSecret

`func (o *AuthProfileRequest) SetSigningSecret(v string)`

SetSigningSecret sets SigningSecret field to given value.

### HasSigningSecret

`func (o *AuthProfileRequest) HasSigningSecret() bool`

HasSigningSecret returns a boolean if a field has been set.

### GetFallbackPolicy

`func (o *AuthProfileRequest) GetFallbackPolicy() string`

GetFallbackPolicy returns the FallbackPolicy field if non-nil, zero value otherwise.

### GetFallbackPolicyOk

`func (o *AuthProfileRequest) GetFallbackPolicyOk() (*string, bool)`

GetFallbackPolicyOk returns a tuple with the FallbackPolicy field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetFallbackPolicy

`func (o *AuthProfileRequest) SetFallbackPolicy(v string)`

SetFallbackPolicy sets FallbackPolicy field to given value.

### HasFallbackPolicy

`func (o *AuthProfileRequest) HasFallbackPolicy() bool`

HasFallbackPolicy returns a boolean if a field has been set.

### GetClaimMappings

`func (o *AuthProfileRequest) GetClaimMappings() []ClaimMappingRequest`

GetClaimMappings returns the ClaimMappings field if non-nil, zero value otherwise.

### GetClaimMappingsOk

`func (o *AuthProfileRequest) GetClaimMappingsOk() (*[]ClaimMappingRequest, bool)`

GetClaimMappingsOk returns a tuple with the ClaimMappings field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetClaimMappings

`func (o *AuthProfileRequest) SetClaimMappings(v []ClaimMappingRequest)`

SetClaimMappings sets ClaimMappings field to given value.

### HasClaimMappings

`func (o *AuthProfileRequest) HasClaimMappings() bool`

HasClaimMappings returns a boolean if a field has been set.


[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


