# TenantFeatureConfigDTO

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**Enabled** | Pointer to **bool** |  | [optional] 
**RolloutPercentage** | Pointer to **int32** |  | [optional] 
**CustomMetadata** | Pointer to **map[string]interface{}** |  | [optional] 

## Methods

### NewTenantFeatureConfigDTO

`func NewTenantFeatureConfigDTO() *TenantFeatureConfigDTO`

NewTenantFeatureConfigDTO instantiates a new TenantFeatureConfigDTO object
This constructor will assign default values to properties that have it defined,
and makes sure properties required by API are set, but the set of arguments
will change when the set of required properties is changed

### NewTenantFeatureConfigDTOWithDefaults

`func NewTenantFeatureConfigDTOWithDefaults() *TenantFeatureConfigDTO`

NewTenantFeatureConfigDTOWithDefaults instantiates a new TenantFeatureConfigDTO object
This constructor will only assign default values to properties that have it defined,
but it doesn't guarantee that properties required by API are set

### GetEnabled

`func (o *TenantFeatureConfigDTO) GetEnabled() bool`

GetEnabled returns the Enabled field if non-nil, zero value otherwise.

### GetEnabledOk

`func (o *TenantFeatureConfigDTO) GetEnabledOk() (*bool, bool)`

GetEnabledOk returns a tuple with the Enabled field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetEnabled

`func (o *TenantFeatureConfigDTO) SetEnabled(v bool)`

SetEnabled sets Enabled field to given value.

### HasEnabled

`func (o *TenantFeatureConfigDTO) HasEnabled() bool`

HasEnabled returns a boolean if a field has been set.

### GetRolloutPercentage

`func (o *TenantFeatureConfigDTO) GetRolloutPercentage() int32`

GetRolloutPercentage returns the RolloutPercentage field if non-nil, zero value otherwise.

### GetRolloutPercentageOk

`func (o *TenantFeatureConfigDTO) GetRolloutPercentageOk() (*int32, bool)`

GetRolloutPercentageOk returns a tuple with the RolloutPercentage field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetRolloutPercentage

`func (o *TenantFeatureConfigDTO) SetRolloutPercentage(v int32)`

SetRolloutPercentage sets RolloutPercentage field to given value.

### HasRolloutPercentage

`func (o *TenantFeatureConfigDTO) HasRolloutPercentage() bool`

HasRolloutPercentage returns a boolean if a field has been set.

### GetCustomMetadata

`func (o *TenantFeatureConfigDTO) GetCustomMetadata() map[string]interface{}`

GetCustomMetadata returns the CustomMetadata field if non-nil, zero value otherwise.

### GetCustomMetadataOk

`func (o *TenantFeatureConfigDTO) GetCustomMetadataOk() (*map[string]interface{}, bool)`

GetCustomMetadataOk returns a tuple with the CustomMetadata field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetCustomMetadata

`func (o *TenantFeatureConfigDTO) SetCustomMetadata(v map[string]interface{})`

SetCustomMetadata sets CustomMetadata field to given value.

### HasCustomMetadata

`func (o *TenantFeatureConfigDTO) HasCustomMetadata() bool`

HasCustomMetadata returns a boolean if a field has been set.


[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


