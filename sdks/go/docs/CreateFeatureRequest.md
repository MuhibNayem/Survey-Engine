# CreateFeatureRequest

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**FeatureKey** | **string** |  | 
**FeatureType** | [**FeatureType**](FeatureType.md) |  | 
**Category** | [**FeatureCategory**](FeatureCategory.md) |  | 
**Name** | **string** |  | 
**Description** | Pointer to **string** |  | [optional] 
**Enabled** | **bool** |  | [default to true]
**RolloutPercentage** | Pointer to **int32** |  | [optional] [default to 100]
**MinPlan** | Pointer to **string** |  | [optional] [default to "BASIC"]
**Roles** | Pointer to **[]string** |  | [optional] [default to {"SUPER_ADMIN", "ADMIN", "EDITOR", "VIEWER"}]
**Platforms** | Pointer to **[]string** |  | [optional] [default to {"WEB"}]
**Metadata** | Pointer to **map[string]interface{}** |  | [optional] 

## Methods

### NewCreateFeatureRequest

`func NewCreateFeatureRequest(featureKey string, featureType FeatureType, category FeatureCategory, name string, enabled bool, ) *CreateFeatureRequest`

NewCreateFeatureRequest instantiates a new CreateFeatureRequest object
This constructor will assign default values to properties that have it defined,
and makes sure properties required by API are set, but the set of arguments
will change when the set of required properties is changed

### NewCreateFeatureRequestWithDefaults

`func NewCreateFeatureRequestWithDefaults() *CreateFeatureRequest`

NewCreateFeatureRequestWithDefaults instantiates a new CreateFeatureRequest object
This constructor will only assign default values to properties that have it defined,
but it doesn't guarantee that properties required by API are set

### GetFeatureKey

`func (o *CreateFeatureRequest) GetFeatureKey() string`

GetFeatureKey returns the FeatureKey field if non-nil, zero value otherwise.

### GetFeatureKeyOk

`func (o *CreateFeatureRequest) GetFeatureKeyOk() (*string, bool)`

GetFeatureKeyOk returns a tuple with the FeatureKey field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetFeatureKey

`func (o *CreateFeatureRequest) SetFeatureKey(v string)`

SetFeatureKey sets FeatureKey field to given value.


### GetFeatureType

`func (o *CreateFeatureRequest) GetFeatureType() FeatureType`

GetFeatureType returns the FeatureType field if non-nil, zero value otherwise.

### GetFeatureTypeOk

`func (o *CreateFeatureRequest) GetFeatureTypeOk() (*FeatureType, bool)`

GetFeatureTypeOk returns a tuple with the FeatureType field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetFeatureType

`func (o *CreateFeatureRequest) SetFeatureType(v FeatureType)`

SetFeatureType sets FeatureType field to given value.


### GetCategory

`func (o *CreateFeatureRequest) GetCategory() FeatureCategory`

GetCategory returns the Category field if non-nil, zero value otherwise.

### GetCategoryOk

`func (o *CreateFeatureRequest) GetCategoryOk() (*FeatureCategory, bool)`

GetCategoryOk returns a tuple with the Category field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetCategory

`func (o *CreateFeatureRequest) SetCategory(v FeatureCategory)`

SetCategory sets Category field to given value.


### GetName

`func (o *CreateFeatureRequest) GetName() string`

GetName returns the Name field if non-nil, zero value otherwise.

### GetNameOk

`func (o *CreateFeatureRequest) GetNameOk() (*string, bool)`

GetNameOk returns a tuple with the Name field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetName

`func (o *CreateFeatureRequest) SetName(v string)`

SetName sets Name field to given value.


### GetDescription

`func (o *CreateFeatureRequest) GetDescription() string`

GetDescription returns the Description field if non-nil, zero value otherwise.

### GetDescriptionOk

`func (o *CreateFeatureRequest) GetDescriptionOk() (*string, bool)`

GetDescriptionOk returns a tuple with the Description field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetDescription

`func (o *CreateFeatureRequest) SetDescription(v string)`

SetDescription sets Description field to given value.

### HasDescription

`func (o *CreateFeatureRequest) HasDescription() bool`

HasDescription returns a boolean if a field has been set.

### GetEnabled

`func (o *CreateFeatureRequest) GetEnabled() bool`

GetEnabled returns the Enabled field if non-nil, zero value otherwise.

### GetEnabledOk

`func (o *CreateFeatureRequest) GetEnabledOk() (*bool, bool)`

GetEnabledOk returns a tuple with the Enabled field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetEnabled

`func (o *CreateFeatureRequest) SetEnabled(v bool)`

SetEnabled sets Enabled field to given value.


### GetRolloutPercentage

`func (o *CreateFeatureRequest) GetRolloutPercentage() int32`

GetRolloutPercentage returns the RolloutPercentage field if non-nil, zero value otherwise.

### GetRolloutPercentageOk

`func (o *CreateFeatureRequest) GetRolloutPercentageOk() (*int32, bool)`

GetRolloutPercentageOk returns a tuple with the RolloutPercentage field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetRolloutPercentage

`func (o *CreateFeatureRequest) SetRolloutPercentage(v int32)`

SetRolloutPercentage sets RolloutPercentage field to given value.

### HasRolloutPercentage

`func (o *CreateFeatureRequest) HasRolloutPercentage() bool`

HasRolloutPercentage returns a boolean if a field has been set.

### GetMinPlan

`func (o *CreateFeatureRequest) GetMinPlan() string`

GetMinPlan returns the MinPlan field if non-nil, zero value otherwise.

### GetMinPlanOk

`func (o *CreateFeatureRequest) GetMinPlanOk() (*string, bool)`

GetMinPlanOk returns a tuple with the MinPlan field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetMinPlan

`func (o *CreateFeatureRequest) SetMinPlan(v string)`

SetMinPlan sets MinPlan field to given value.

### HasMinPlan

`func (o *CreateFeatureRequest) HasMinPlan() bool`

HasMinPlan returns a boolean if a field has been set.

### GetRoles

`func (o *CreateFeatureRequest) GetRoles() []string`

GetRoles returns the Roles field if non-nil, zero value otherwise.

### GetRolesOk

`func (o *CreateFeatureRequest) GetRolesOk() (*[]string, bool)`

GetRolesOk returns a tuple with the Roles field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetRoles

`func (o *CreateFeatureRequest) SetRoles(v []string)`

SetRoles sets Roles field to given value.

### HasRoles

`func (o *CreateFeatureRequest) HasRoles() bool`

HasRoles returns a boolean if a field has been set.

### GetPlatforms

`func (o *CreateFeatureRequest) GetPlatforms() []string`

GetPlatforms returns the Platforms field if non-nil, zero value otherwise.

### GetPlatformsOk

`func (o *CreateFeatureRequest) GetPlatformsOk() (*[]string, bool)`

GetPlatformsOk returns a tuple with the Platforms field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetPlatforms

`func (o *CreateFeatureRequest) SetPlatforms(v []string)`

SetPlatforms sets Platforms field to given value.

### HasPlatforms

`func (o *CreateFeatureRequest) HasPlatforms() bool`

HasPlatforms returns a boolean if a field has been set.

### GetMetadata

`func (o *CreateFeatureRequest) GetMetadata() map[string]interface{}`

GetMetadata returns the Metadata field if non-nil, zero value otherwise.

### GetMetadataOk

`func (o *CreateFeatureRequest) GetMetadataOk() (*map[string]interface{}, bool)`

GetMetadataOk returns a tuple with the Metadata field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetMetadata

`func (o *CreateFeatureRequest) SetMetadata(v map[string]interface{})`

SetMetadata sets Metadata field to given value.

### HasMetadata

`func (o *CreateFeatureRequest) HasMetadata() bool`

HasMetadata returns a boolean if a field has been set.


[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


