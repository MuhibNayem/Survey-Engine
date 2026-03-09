# AuthProfileResponse

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**Id** | Pointer to **string** |  | [optional] 
**TenantId** | Pointer to **string** |  | [optional] 
**AuthMode** | Pointer to **string** |  | [optional] 
**Issuer** | Pointer to **string** |  | [optional] 
**Audience** | Pointer to **string** |  | [optional] 
**JwksEndpoint** | Pointer to **string** |  | [optional] 
**OidcDiscoveryUrl** | Pointer to **string** |  | [optional] 
**OidcClientId** | Pointer to **string** |  | [optional] 
**OidcRedirectUri** | Pointer to **string** |  | [optional] 
**OidcScopes** | Pointer to **string** |  | [optional] 
**ClockSkewSeconds** | Pointer to **int32** |  | [optional] 
**TokenTtlSeconds** | Pointer to **int32** |  | [optional] 
**ActiveKeyVersion** | Pointer to **int32** |  | [optional] 
**FallbackPolicy** | Pointer to **string** |  | [optional] 
**ClaimMappings** | Pointer to [**[]AuthProfileResponseClaimMappingsInner**](AuthProfileResponseClaimMappingsInner.md) |  | [optional] 

## Methods

### NewAuthProfileResponse

`func NewAuthProfileResponse() *AuthProfileResponse`

NewAuthProfileResponse instantiates a new AuthProfileResponse object
This constructor will assign default values to properties that have it defined,
and makes sure properties required by API are set, but the set of arguments
will change when the set of required properties is changed

### NewAuthProfileResponseWithDefaults

`func NewAuthProfileResponseWithDefaults() *AuthProfileResponse`

NewAuthProfileResponseWithDefaults instantiates a new AuthProfileResponse object
This constructor will only assign default values to properties that have it defined,
but it doesn't guarantee that properties required by API are set

### GetId

`func (o *AuthProfileResponse) GetId() string`

GetId returns the Id field if non-nil, zero value otherwise.

### GetIdOk

`func (o *AuthProfileResponse) GetIdOk() (*string, bool)`

GetIdOk returns a tuple with the Id field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetId

`func (o *AuthProfileResponse) SetId(v string)`

SetId sets Id field to given value.

### HasId

`func (o *AuthProfileResponse) HasId() bool`

HasId returns a boolean if a field has been set.

### GetTenantId

`func (o *AuthProfileResponse) GetTenantId() string`

GetTenantId returns the TenantId field if non-nil, zero value otherwise.

### GetTenantIdOk

`func (o *AuthProfileResponse) GetTenantIdOk() (*string, bool)`

GetTenantIdOk returns a tuple with the TenantId field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetTenantId

`func (o *AuthProfileResponse) SetTenantId(v string)`

SetTenantId sets TenantId field to given value.

### HasTenantId

`func (o *AuthProfileResponse) HasTenantId() bool`

HasTenantId returns a boolean if a field has been set.

### GetAuthMode

`func (o *AuthProfileResponse) GetAuthMode() string`

GetAuthMode returns the AuthMode field if non-nil, zero value otherwise.

### GetAuthModeOk

`func (o *AuthProfileResponse) GetAuthModeOk() (*string, bool)`

GetAuthModeOk returns a tuple with the AuthMode field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetAuthMode

`func (o *AuthProfileResponse) SetAuthMode(v string)`

SetAuthMode sets AuthMode field to given value.

### HasAuthMode

`func (o *AuthProfileResponse) HasAuthMode() bool`

HasAuthMode returns a boolean if a field has been set.

### GetIssuer

`func (o *AuthProfileResponse) GetIssuer() string`

GetIssuer returns the Issuer field if non-nil, zero value otherwise.

### GetIssuerOk

`func (o *AuthProfileResponse) GetIssuerOk() (*string, bool)`

GetIssuerOk returns a tuple with the Issuer field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetIssuer

`func (o *AuthProfileResponse) SetIssuer(v string)`

SetIssuer sets Issuer field to given value.

### HasIssuer

`func (o *AuthProfileResponse) HasIssuer() bool`

HasIssuer returns a boolean if a field has been set.

### GetAudience

`func (o *AuthProfileResponse) GetAudience() string`

GetAudience returns the Audience field if non-nil, zero value otherwise.

### GetAudienceOk

`func (o *AuthProfileResponse) GetAudienceOk() (*string, bool)`

GetAudienceOk returns a tuple with the Audience field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetAudience

`func (o *AuthProfileResponse) SetAudience(v string)`

SetAudience sets Audience field to given value.

### HasAudience

`func (o *AuthProfileResponse) HasAudience() bool`

HasAudience returns a boolean if a field has been set.

### GetJwksEndpoint

`func (o *AuthProfileResponse) GetJwksEndpoint() string`

GetJwksEndpoint returns the JwksEndpoint field if non-nil, zero value otherwise.

### GetJwksEndpointOk

`func (o *AuthProfileResponse) GetJwksEndpointOk() (*string, bool)`

GetJwksEndpointOk returns a tuple with the JwksEndpoint field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetJwksEndpoint

`func (o *AuthProfileResponse) SetJwksEndpoint(v string)`

SetJwksEndpoint sets JwksEndpoint field to given value.

### HasJwksEndpoint

`func (o *AuthProfileResponse) HasJwksEndpoint() bool`

HasJwksEndpoint returns a boolean if a field has been set.

### GetOidcDiscoveryUrl

`func (o *AuthProfileResponse) GetOidcDiscoveryUrl() string`

GetOidcDiscoveryUrl returns the OidcDiscoveryUrl field if non-nil, zero value otherwise.

### GetOidcDiscoveryUrlOk

`func (o *AuthProfileResponse) GetOidcDiscoveryUrlOk() (*string, bool)`

GetOidcDiscoveryUrlOk returns a tuple with the OidcDiscoveryUrl field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetOidcDiscoveryUrl

`func (o *AuthProfileResponse) SetOidcDiscoveryUrl(v string)`

SetOidcDiscoveryUrl sets OidcDiscoveryUrl field to given value.

### HasOidcDiscoveryUrl

`func (o *AuthProfileResponse) HasOidcDiscoveryUrl() bool`

HasOidcDiscoveryUrl returns a boolean if a field has been set.

### GetOidcClientId

`func (o *AuthProfileResponse) GetOidcClientId() string`

GetOidcClientId returns the OidcClientId field if non-nil, zero value otherwise.

### GetOidcClientIdOk

`func (o *AuthProfileResponse) GetOidcClientIdOk() (*string, bool)`

GetOidcClientIdOk returns a tuple with the OidcClientId field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetOidcClientId

`func (o *AuthProfileResponse) SetOidcClientId(v string)`

SetOidcClientId sets OidcClientId field to given value.

### HasOidcClientId

`func (o *AuthProfileResponse) HasOidcClientId() bool`

HasOidcClientId returns a boolean if a field has been set.

### GetOidcRedirectUri

`func (o *AuthProfileResponse) GetOidcRedirectUri() string`

GetOidcRedirectUri returns the OidcRedirectUri field if non-nil, zero value otherwise.

### GetOidcRedirectUriOk

`func (o *AuthProfileResponse) GetOidcRedirectUriOk() (*string, bool)`

GetOidcRedirectUriOk returns a tuple with the OidcRedirectUri field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetOidcRedirectUri

`func (o *AuthProfileResponse) SetOidcRedirectUri(v string)`

SetOidcRedirectUri sets OidcRedirectUri field to given value.

### HasOidcRedirectUri

`func (o *AuthProfileResponse) HasOidcRedirectUri() bool`

HasOidcRedirectUri returns a boolean if a field has been set.

### GetOidcScopes

`func (o *AuthProfileResponse) GetOidcScopes() string`

GetOidcScopes returns the OidcScopes field if non-nil, zero value otherwise.

### GetOidcScopesOk

`func (o *AuthProfileResponse) GetOidcScopesOk() (*string, bool)`

GetOidcScopesOk returns a tuple with the OidcScopes field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetOidcScopes

`func (o *AuthProfileResponse) SetOidcScopes(v string)`

SetOidcScopes sets OidcScopes field to given value.

### HasOidcScopes

`func (o *AuthProfileResponse) HasOidcScopes() bool`

HasOidcScopes returns a boolean if a field has been set.

### GetClockSkewSeconds

`func (o *AuthProfileResponse) GetClockSkewSeconds() int32`

GetClockSkewSeconds returns the ClockSkewSeconds field if non-nil, zero value otherwise.

### GetClockSkewSecondsOk

`func (o *AuthProfileResponse) GetClockSkewSecondsOk() (*int32, bool)`

GetClockSkewSecondsOk returns a tuple with the ClockSkewSeconds field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetClockSkewSeconds

`func (o *AuthProfileResponse) SetClockSkewSeconds(v int32)`

SetClockSkewSeconds sets ClockSkewSeconds field to given value.

### HasClockSkewSeconds

`func (o *AuthProfileResponse) HasClockSkewSeconds() bool`

HasClockSkewSeconds returns a boolean if a field has been set.

### GetTokenTtlSeconds

`func (o *AuthProfileResponse) GetTokenTtlSeconds() int32`

GetTokenTtlSeconds returns the TokenTtlSeconds field if non-nil, zero value otherwise.

### GetTokenTtlSecondsOk

`func (o *AuthProfileResponse) GetTokenTtlSecondsOk() (*int32, bool)`

GetTokenTtlSecondsOk returns a tuple with the TokenTtlSeconds field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetTokenTtlSeconds

`func (o *AuthProfileResponse) SetTokenTtlSeconds(v int32)`

SetTokenTtlSeconds sets TokenTtlSeconds field to given value.

### HasTokenTtlSeconds

`func (o *AuthProfileResponse) HasTokenTtlSeconds() bool`

HasTokenTtlSeconds returns a boolean if a field has been set.

### GetActiveKeyVersion

`func (o *AuthProfileResponse) GetActiveKeyVersion() int32`

GetActiveKeyVersion returns the ActiveKeyVersion field if non-nil, zero value otherwise.

### GetActiveKeyVersionOk

`func (o *AuthProfileResponse) GetActiveKeyVersionOk() (*int32, bool)`

GetActiveKeyVersionOk returns a tuple with the ActiveKeyVersion field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetActiveKeyVersion

`func (o *AuthProfileResponse) SetActiveKeyVersion(v int32)`

SetActiveKeyVersion sets ActiveKeyVersion field to given value.

### HasActiveKeyVersion

`func (o *AuthProfileResponse) HasActiveKeyVersion() bool`

HasActiveKeyVersion returns a boolean if a field has been set.

### GetFallbackPolicy

`func (o *AuthProfileResponse) GetFallbackPolicy() string`

GetFallbackPolicy returns the FallbackPolicy field if non-nil, zero value otherwise.

### GetFallbackPolicyOk

`func (o *AuthProfileResponse) GetFallbackPolicyOk() (*string, bool)`

GetFallbackPolicyOk returns a tuple with the FallbackPolicy field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetFallbackPolicy

`func (o *AuthProfileResponse) SetFallbackPolicy(v string)`

SetFallbackPolicy sets FallbackPolicy field to given value.

### HasFallbackPolicy

`func (o *AuthProfileResponse) HasFallbackPolicy() bool`

HasFallbackPolicy returns a boolean if a field has been set.

### GetClaimMappings

`func (o *AuthProfileResponse) GetClaimMappings() []AuthProfileResponseClaimMappingsInner`

GetClaimMappings returns the ClaimMappings field if non-nil, zero value otherwise.

### GetClaimMappingsOk

`func (o *AuthProfileResponse) GetClaimMappingsOk() (*[]AuthProfileResponseClaimMappingsInner, bool)`

GetClaimMappingsOk returns a tuple with the ClaimMappings field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetClaimMappings

`func (o *AuthProfileResponse) SetClaimMappings(v []AuthProfileResponseClaimMappingsInner)`

SetClaimMappings sets ClaimMappings field to given value.

### HasClaimMappings

`func (o *AuthProfileResponse) HasClaimMappings() bool`

HasClaimMappings returns a boolean if a field has been set.


[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


