# ProviderTemplateResponse

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**ProviderCode** | Pointer to **string** |  | [optional] 
**DisplayName** | Pointer to **string** |  | [optional] 
**Description** | Pointer to **string** |  | [optional] 
**DefaultScopes** | Pointer to **[]string** |  | [optional] 
**DefaultClaimMappings** | Pointer to [**[]ClaimMappingRequest**](ClaimMappingRequest.md) |  | [optional] 
**RequiredConfigFields** | Pointer to **[]string** |  | [optional] 

## Methods

### NewProviderTemplateResponse

`func NewProviderTemplateResponse() *ProviderTemplateResponse`

NewProviderTemplateResponse instantiates a new ProviderTemplateResponse object
This constructor will assign default values to properties that have it defined,
and makes sure properties required by API are set, but the set of arguments
will change when the set of required properties is changed

### NewProviderTemplateResponseWithDefaults

`func NewProviderTemplateResponseWithDefaults() *ProviderTemplateResponse`

NewProviderTemplateResponseWithDefaults instantiates a new ProviderTemplateResponse object
This constructor will only assign default values to properties that have it defined,
but it doesn't guarantee that properties required by API are set

### GetProviderCode

`func (o *ProviderTemplateResponse) GetProviderCode() string`

GetProviderCode returns the ProviderCode field if non-nil, zero value otherwise.

### GetProviderCodeOk

`func (o *ProviderTemplateResponse) GetProviderCodeOk() (*string, bool)`

GetProviderCodeOk returns a tuple with the ProviderCode field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetProviderCode

`func (o *ProviderTemplateResponse) SetProviderCode(v string)`

SetProviderCode sets ProviderCode field to given value.

### HasProviderCode

`func (o *ProviderTemplateResponse) HasProviderCode() bool`

HasProviderCode returns a boolean if a field has been set.

### GetDisplayName

`func (o *ProviderTemplateResponse) GetDisplayName() string`

GetDisplayName returns the DisplayName field if non-nil, zero value otherwise.

### GetDisplayNameOk

`func (o *ProviderTemplateResponse) GetDisplayNameOk() (*string, bool)`

GetDisplayNameOk returns a tuple with the DisplayName field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetDisplayName

`func (o *ProviderTemplateResponse) SetDisplayName(v string)`

SetDisplayName sets DisplayName field to given value.

### HasDisplayName

`func (o *ProviderTemplateResponse) HasDisplayName() bool`

HasDisplayName returns a boolean if a field has been set.

### GetDescription

`func (o *ProviderTemplateResponse) GetDescription() string`

GetDescription returns the Description field if non-nil, zero value otherwise.

### GetDescriptionOk

`func (o *ProviderTemplateResponse) GetDescriptionOk() (*string, bool)`

GetDescriptionOk returns a tuple with the Description field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetDescription

`func (o *ProviderTemplateResponse) SetDescription(v string)`

SetDescription sets Description field to given value.

### HasDescription

`func (o *ProviderTemplateResponse) HasDescription() bool`

HasDescription returns a boolean if a field has been set.

### GetDefaultScopes

`func (o *ProviderTemplateResponse) GetDefaultScopes() []string`

GetDefaultScopes returns the DefaultScopes field if non-nil, zero value otherwise.

### GetDefaultScopesOk

`func (o *ProviderTemplateResponse) GetDefaultScopesOk() (*[]string, bool)`

GetDefaultScopesOk returns a tuple with the DefaultScopes field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetDefaultScopes

`func (o *ProviderTemplateResponse) SetDefaultScopes(v []string)`

SetDefaultScopes sets DefaultScopes field to given value.

### HasDefaultScopes

`func (o *ProviderTemplateResponse) HasDefaultScopes() bool`

HasDefaultScopes returns a boolean if a field has been set.

### GetDefaultClaimMappings

`func (o *ProviderTemplateResponse) GetDefaultClaimMappings() []ClaimMappingRequest`

GetDefaultClaimMappings returns the DefaultClaimMappings field if non-nil, zero value otherwise.

### GetDefaultClaimMappingsOk

`func (o *ProviderTemplateResponse) GetDefaultClaimMappingsOk() (*[]ClaimMappingRequest, bool)`

GetDefaultClaimMappingsOk returns a tuple with the DefaultClaimMappings field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetDefaultClaimMappings

`func (o *ProviderTemplateResponse) SetDefaultClaimMappings(v []ClaimMappingRequest)`

SetDefaultClaimMappings sets DefaultClaimMappings field to given value.

### HasDefaultClaimMappings

`func (o *ProviderTemplateResponse) HasDefaultClaimMappings() bool`

HasDefaultClaimMappings returns a boolean if a field has been set.

### GetRequiredConfigFields

`func (o *ProviderTemplateResponse) GetRequiredConfigFields() []string`

GetRequiredConfigFields returns the RequiredConfigFields field if non-nil, zero value otherwise.

### GetRequiredConfigFieldsOk

`func (o *ProviderTemplateResponse) GetRequiredConfigFieldsOk() (*[]string, bool)`

GetRequiredConfigFieldsOk returns a tuple with the RequiredConfigFields field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetRequiredConfigFields

`func (o *ProviderTemplateResponse) SetRequiredConfigFields(v []string)`

SetRequiredConfigFields sets RequiredConfigFields field to given value.

### HasRequiredConfigFields

`func (o *ProviderTemplateResponse) HasRequiredConfigFields() bool`

HasRequiredConfigFields returns a boolean if a field has been set.


[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


