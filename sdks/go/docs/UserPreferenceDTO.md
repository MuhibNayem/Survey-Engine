# UserPreferenceDTO

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**Preferences** | Pointer to **map[string]string** |  | [optional] 
**FeatureCompletion** | Pointer to **map[string]bool** |  | [optional] 

## Methods

### NewUserPreferenceDTO

`func NewUserPreferenceDTO() *UserPreferenceDTO`

NewUserPreferenceDTO instantiates a new UserPreferenceDTO object
This constructor will assign default values to properties that have it defined,
and makes sure properties required by API are set, but the set of arguments
will change when the set of required properties is changed

### NewUserPreferenceDTOWithDefaults

`func NewUserPreferenceDTOWithDefaults() *UserPreferenceDTO`

NewUserPreferenceDTOWithDefaults instantiates a new UserPreferenceDTO object
This constructor will only assign default values to properties that have it defined,
but it doesn't guarantee that properties required by API are set

### GetPreferences

`func (o *UserPreferenceDTO) GetPreferences() map[string]string`

GetPreferences returns the Preferences field if non-nil, zero value otherwise.

### GetPreferencesOk

`func (o *UserPreferenceDTO) GetPreferencesOk() (*map[string]string, bool)`

GetPreferencesOk returns a tuple with the Preferences field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetPreferences

`func (o *UserPreferenceDTO) SetPreferences(v map[string]string)`

SetPreferences sets Preferences field to given value.

### HasPreferences

`func (o *UserPreferenceDTO) HasPreferences() bool`

HasPreferences returns a boolean if a field has been set.

### GetFeatureCompletion

`func (o *UserPreferenceDTO) GetFeatureCompletion() map[string]bool`

GetFeatureCompletion returns the FeatureCompletion field if non-nil, zero value otherwise.

### GetFeatureCompletionOk

`func (o *UserPreferenceDTO) GetFeatureCompletionOk() (*map[string]bool, bool)`

GetFeatureCompletionOk returns a tuple with the FeatureCompletion field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetFeatureCompletion

`func (o *UserPreferenceDTO) SetFeatureCompletion(v map[string]bool)`

SetFeatureCompletion sets FeatureCompletion field to given value.

### HasFeatureCompletion

`func (o *UserPreferenceDTO) HasFeatureCompletion() bool`

HasFeatureCompletion returns a boolean if a field has been set.


[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


