# FeatureDefinitionDTO

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**Id** | Pointer to **string** |  | [optional] 
**FeatureKey** | Pointer to **string** |  | [optional] 
**FeatureType** | Pointer to [**FeatureType**](FeatureType.md) |  | [optional] 
**Category** | Pointer to [**FeatureCategory**](FeatureCategory.md) |  | [optional] 
**Name** | Pointer to **string** |  | [optional] 
**Description** | Pointer to **string** |  | [optional] 
**Enabled** | Pointer to **bool** |  | [optional] 
**RolloutPercentage** | Pointer to **int32** |  | [optional] 
**MinPlan** | Pointer to **string** |  | [optional] 
**Roles** | Pointer to **[]string** |  | [optional] 
**Platforms** | Pointer to **[]string** |  | [optional] 
**Metadata** | Pointer to **map[string]interface{}** |  | [optional] 
**CreatedBy** | Pointer to **string** |  | [optional] 
**CreatedAt** | Pointer to **time.Time** |  | [optional] 
**UpdatedBy** | Pointer to **string** |  | [optional] 
**UpdatedAt** | Pointer to **time.Time** |  | [optional] 

## Methods

### NewFeatureDefinitionDTO

`func NewFeatureDefinitionDTO() *FeatureDefinitionDTO`

NewFeatureDefinitionDTO instantiates a new FeatureDefinitionDTO object
This constructor will assign default values to properties that have it defined,
and makes sure properties required by API are set, but the set of arguments
will change when the set of required properties is changed

### NewFeatureDefinitionDTOWithDefaults

`func NewFeatureDefinitionDTOWithDefaults() *FeatureDefinitionDTO`

NewFeatureDefinitionDTOWithDefaults instantiates a new FeatureDefinitionDTO object
This constructor will only assign default values to properties that have it defined,
but it doesn't guarantee that properties required by API are set

### GetId

`func (o *FeatureDefinitionDTO) GetId() string`

GetId returns the Id field if non-nil, zero value otherwise.

### GetIdOk

`func (o *FeatureDefinitionDTO) GetIdOk() (*string, bool)`

GetIdOk returns a tuple with the Id field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetId

`func (o *FeatureDefinitionDTO) SetId(v string)`

SetId sets Id field to given value.

### HasId

`func (o *FeatureDefinitionDTO) HasId() bool`

HasId returns a boolean if a field has been set.

### GetFeatureKey

`func (o *FeatureDefinitionDTO) GetFeatureKey() string`

GetFeatureKey returns the FeatureKey field if non-nil, zero value otherwise.

### GetFeatureKeyOk

`func (o *FeatureDefinitionDTO) GetFeatureKeyOk() (*string, bool)`

GetFeatureKeyOk returns a tuple with the FeatureKey field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetFeatureKey

`func (o *FeatureDefinitionDTO) SetFeatureKey(v string)`

SetFeatureKey sets FeatureKey field to given value.

### HasFeatureKey

`func (o *FeatureDefinitionDTO) HasFeatureKey() bool`

HasFeatureKey returns a boolean if a field has been set.

### GetFeatureType

`func (o *FeatureDefinitionDTO) GetFeatureType() FeatureType`

GetFeatureType returns the FeatureType field if non-nil, zero value otherwise.

### GetFeatureTypeOk

`func (o *FeatureDefinitionDTO) GetFeatureTypeOk() (*FeatureType, bool)`

GetFeatureTypeOk returns a tuple with the FeatureType field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetFeatureType

`func (o *FeatureDefinitionDTO) SetFeatureType(v FeatureType)`

SetFeatureType sets FeatureType field to given value.

### HasFeatureType

`func (o *FeatureDefinitionDTO) HasFeatureType() bool`

HasFeatureType returns a boolean if a field has been set.

### GetCategory

`func (o *FeatureDefinitionDTO) GetCategory() FeatureCategory`

GetCategory returns the Category field if non-nil, zero value otherwise.

### GetCategoryOk

`func (o *FeatureDefinitionDTO) GetCategoryOk() (*FeatureCategory, bool)`

GetCategoryOk returns a tuple with the Category field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetCategory

`func (o *FeatureDefinitionDTO) SetCategory(v FeatureCategory)`

SetCategory sets Category field to given value.

### HasCategory

`func (o *FeatureDefinitionDTO) HasCategory() bool`

HasCategory returns a boolean if a field has been set.

### GetName

`func (o *FeatureDefinitionDTO) GetName() string`

GetName returns the Name field if non-nil, zero value otherwise.

### GetNameOk

`func (o *FeatureDefinitionDTO) GetNameOk() (*string, bool)`

GetNameOk returns a tuple with the Name field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetName

`func (o *FeatureDefinitionDTO) SetName(v string)`

SetName sets Name field to given value.

### HasName

`func (o *FeatureDefinitionDTO) HasName() bool`

HasName returns a boolean if a field has been set.

### GetDescription

`func (o *FeatureDefinitionDTO) GetDescription() string`

GetDescription returns the Description field if non-nil, zero value otherwise.

### GetDescriptionOk

`func (o *FeatureDefinitionDTO) GetDescriptionOk() (*string, bool)`

GetDescriptionOk returns a tuple with the Description field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetDescription

`func (o *FeatureDefinitionDTO) SetDescription(v string)`

SetDescription sets Description field to given value.

### HasDescription

`func (o *FeatureDefinitionDTO) HasDescription() bool`

HasDescription returns a boolean if a field has been set.

### GetEnabled

`func (o *FeatureDefinitionDTO) GetEnabled() bool`

GetEnabled returns the Enabled field if non-nil, zero value otherwise.

### GetEnabledOk

`func (o *FeatureDefinitionDTO) GetEnabledOk() (*bool, bool)`

GetEnabledOk returns a tuple with the Enabled field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetEnabled

`func (o *FeatureDefinitionDTO) SetEnabled(v bool)`

SetEnabled sets Enabled field to given value.

### HasEnabled

`func (o *FeatureDefinitionDTO) HasEnabled() bool`

HasEnabled returns a boolean if a field has been set.

### GetRolloutPercentage

`func (o *FeatureDefinitionDTO) GetRolloutPercentage() int32`

GetRolloutPercentage returns the RolloutPercentage field if non-nil, zero value otherwise.

### GetRolloutPercentageOk

`func (o *FeatureDefinitionDTO) GetRolloutPercentageOk() (*int32, bool)`

GetRolloutPercentageOk returns a tuple with the RolloutPercentage field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetRolloutPercentage

`func (o *FeatureDefinitionDTO) SetRolloutPercentage(v int32)`

SetRolloutPercentage sets RolloutPercentage field to given value.

### HasRolloutPercentage

`func (o *FeatureDefinitionDTO) HasRolloutPercentage() bool`

HasRolloutPercentage returns a boolean if a field has been set.

### GetMinPlan

`func (o *FeatureDefinitionDTO) GetMinPlan() string`

GetMinPlan returns the MinPlan field if non-nil, zero value otherwise.

### GetMinPlanOk

`func (o *FeatureDefinitionDTO) GetMinPlanOk() (*string, bool)`

GetMinPlanOk returns a tuple with the MinPlan field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetMinPlan

`func (o *FeatureDefinitionDTO) SetMinPlan(v string)`

SetMinPlan sets MinPlan field to given value.

### HasMinPlan

`func (o *FeatureDefinitionDTO) HasMinPlan() bool`

HasMinPlan returns a boolean if a field has been set.

### GetRoles

`func (o *FeatureDefinitionDTO) GetRoles() []string`

GetRoles returns the Roles field if non-nil, zero value otherwise.

### GetRolesOk

`func (o *FeatureDefinitionDTO) GetRolesOk() (*[]string, bool)`

GetRolesOk returns a tuple with the Roles field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetRoles

`func (o *FeatureDefinitionDTO) SetRoles(v []string)`

SetRoles sets Roles field to given value.

### HasRoles

`func (o *FeatureDefinitionDTO) HasRoles() bool`

HasRoles returns a boolean if a field has been set.

### GetPlatforms

`func (o *FeatureDefinitionDTO) GetPlatforms() []string`

GetPlatforms returns the Platforms field if non-nil, zero value otherwise.

### GetPlatformsOk

`func (o *FeatureDefinitionDTO) GetPlatformsOk() (*[]string, bool)`

GetPlatformsOk returns a tuple with the Platforms field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetPlatforms

`func (o *FeatureDefinitionDTO) SetPlatforms(v []string)`

SetPlatforms sets Platforms field to given value.

### HasPlatforms

`func (o *FeatureDefinitionDTO) HasPlatforms() bool`

HasPlatforms returns a boolean if a field has been set.

### GetMetadata

`func (o *FeatureDefinitionDTO) GetMetadata() map[string]interface{}`

GetMetadata returns the Metadata field if non-nil, zero value otherwise.

### GetMetadataOk

`func (o *FeatureDefinitionDTO) GetMetadataOk() (*map[string]interface{}, bool)`

GetMetadataOk returns a tuple with the Metadata field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetMetadata

`func (o *FeatureDefinitionDTO) SetMetadata(v map[string]interface{})`

SetMetadata sets Metadata field to given value.

### HasMetadata

`func (o *FeatureDefinitionDTO) HasMetadata() bool`

HasMetadata returns a boolean if a field has been set.

### GetCreatedBy

`func (o *FeatureDefinitionDTO) GetCreatedBy() string`

GetCreatedBy returns the CreatedBy field if non-nil, zero value otherwise.

### GetCreatedByOk

`func (o *FeatureDefinitionDTO) GetCreatedByOk() (*string, bool)`

GetCreatedByOk returns a tuple with the CreatedBy field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetCreatedBy

`func (o *FeatureDefinitionDTO) SetCreatedBy(v string)`

SetCreatedBy sets CreatedBy field to given value.

### HasCreatedBy

`func (o *FeatureDefinitionDTO) HasCreatedBy() bool`

HasCreatedBy returns a boolean if a field has been set.

### GetCreatedAt

`func (o *FeatureDefinitionDTO) GetCreatedAt() time.Time`

GetCreatedAt returns the CreatedAt field if non-nil, zero value otherwise.

### GetCreatedAtOk

`func (o *FeatureDefinitionDTO) GetCreatedAtOk() (*time.Time, bool)`

GetCreatedAtOk returns a tuple with the CreatedAt field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetCreatedAt

`func (o *FeatureDefinitionDTO) SetCreatedAt(v time.Time)`

SetCreatedAt sets CreatedAt field to given value.

### HasCreatedAt

`func (o *FeatureDefinitionDTO) HasCreatedAt() bool`

HasCreatedAt returns a boolean if a field has been set.

### GetUpdatedBy

`func (o *FeatureDefinitionDTO) GetUpdatedBy() string`

GetUpdatedBy returns the UpdatedBy field if non-nil, zero value otherwise.

### GetUpdatedByOk

`func (o *FeatureDefinitionDTO) GetUpdatedByOk() (*string, bool)`

GetUpdatedByOk returns a tuple with the UpdatedBy field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetUpdatedBy

`func (o *FeatureDefinitionDTO) SetUpdatedBy(v string)`

SetUpdatedBy sets UpdatedBy field to given value.

### HasUpdatedBy

`func (o *FeatureDefinitionDTO) HasUpdatedBy() bool`

HasUpdatedBy returns a boolean if a field has been set.

### GetUpdatedAt

`func (o *FeatureDefinitionDTO) GetUpdatedAt() time.Time`

GetUpdatedAt returns the UpdatedAt field if non-nil, zero value otherwise.

### GetUpdatedAtOk

`func (o *FeatureDefinitionDTO) GetUpdatedAtOk() (*time.Time, bool)`

GetUpdatedAtOk returns a tuple with the UpdatedAt field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetUpdatedAt

`func (o *FeatureDefinitionDTO) SetUpdatedAt(v time.Time)`

SetUpdatedAt sets UpdatedAt field to given value.

### HasUpdatedAt

`func (o *FeatureDefinitionDTO) HasUpdatedAt() bool`

HasUpdatedAt returns a boolean if a field has been set.


[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


