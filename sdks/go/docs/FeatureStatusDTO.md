# FeatureStatusDTO

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**FeatureKey** | Pointer to **string** |  | [optional] 
**Available** | Pointer to **bool** |  | [optional] 
**Completed** | Pointer to **bool** |  | [optional] 
**Accessed** | Pointer to **bool** |  | [optional] 
**AccessCount** | Pointer to **int32** |  | [optional] 
**LastAccessedAt** | Pointer to **time.Time** |  | [optional] 
**CompletedAt** | Pointer to **time.Time** |  | [optional] 

## Methods

### NewFeatureStatusDTO

`func NewFeatureStatusDTO() *FeatureStatusDTO`

NewFeatureStatusDTO instantiates a new FeatureStatusDTO object
This constructor will assign default values to properties that have it defined,
and makes sure properties required by API are set, but the set of arguments
will change when the set of required properties is changed

### NewFeatureStatusDTOWithDefaults

`func NewFeatureStatusDTOWithDefaults() *FeatureStatusDTO`

NewFeatureStatusDTOWithDefaults instantiates a new FeatureStatusDTO object
This constructor will only assign default values to properties that have it defined,
but it doesn't guarantee that properties required by API are set

### GetFeatureKey

`func (o *FeatureStatusDTO) GetFeatureKey() string`

GetFeatureKey returns the FeatureKey field if non-nil, zero value otherwise.

### GetFeatureKeyOk

`func (o *FeatureStatusDTO) GetFeatureKeyOk() (*string, bool)`

GetFeatureKeyOk returns a tuple with the FeatureKey field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetFeatureKey

`func (o *FeatureStatusDTO) SetFeatureKey(v string)`

SetFeatureKey sets FeatureKey field to given value.

### HasFeatureKey

`func (o *FeatureStatusDTO) HasFeatureKey() bool`

HasFeatureKey returns a boolean if a field has been set.

### GetAvailable

`func (o *FeatureStatusDTO) GetAvailable() bool`

GetAvailable returns the Available field if non-nil, zero value otherwise.

### GetAvailableOk

`func (o *FeatureStatusDTO) GetAvailableOk() (*bool, bool)`

GetAvailableOk returns a tuple with the Available field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetAvailable

`func (o *FeatureStatusDTO) SetAvailable(v bool)`

SetAvailable sets Available field to given value.

### HasAvailable

`func (o *FeatureStatusDTO) HasAvailable() bool`

HasAvailable returns a boolean if a field has been set.

### GetCompleted

`func (o *FeatureStatusDTO) GetCompleted() bool`

GetCompleted returns the Completed field if non-nil, zero value otherwise.

### GetCompletedOk

`func (o *FeatureStatusDTO) GetCompletedOk() (*bool, bool)`

GetCompletedOk returns a tuple with the Completed field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetCompleted

`func (o *FeatureStatusDTO) SetCompleted(v bool)`

SetCompleted sets Completed field to given value.

### HasCompleted

`func (o *FeatureStatusDTO) HasCompleted() bool`

HasCompleted returns a boolean if a field has been set.

### GetAccessed

`func (o *FeatureStatusDTO) GetAccessed() bool`

GetAccessed returns the Accessed field if non-nil, zero value otherwise.

### GetAccessedOk

`func (o *FeatureStatusDTO) GetAccessedOk() (*bool, bool)`

GetAccessedOk returns a tuple with the Accessed field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetAccessed

`func (o *FeatureStatusDTO) SetAccessed(v bool)`

SetAccessed sets Accessed field to given value.

### HasAccessed

`func (o *FeatureStatusDTO) HasAccessed() bool`

HasAccessed returns a boolean if a field has been set.

### GetAccessCount

`func (o *FeatureStatusDTO) GetAccessCount() int32`

GetAccessCount returns the AccessCount field if non-nil, zero value otherwise.

### GetAccessCountOk

`func (o *FeatureStatusDTO) GetAccessCountOk() (*int32, bool)`

GetAccessCountOk returns a tuple with the AccessCount field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetAccessCount

`func (o *FeatureStatusDTO) SetAccessCount(v int32)`

SetAccessCount sets AccessCount field to given value.

### HasAccessCount

`func (o *FeatureStatusDTO) HasAccessCount() bool`

HasAccessCount returns a boolean if a field has been set.

### GetLastAccessedAt

`func (o *FeatureStatusDTO) GetLastAccessedAt() time.Time`

GetLastAccessedAt returns the LastAccessedAt field if non-nil, zero value otherwise.

### GetLastAccessedAtOk

`func (o *FeatureStatusDTO) GetLastAccessedAtOk() (*time.Time, bool)`

GetLastAccessedAtOk returns a tuple with the LastAccessedAt field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetLastAccessedAt

`func (o *FeatureStatusDTO) SetLastAccessedAt(v time.Time)`

SetLastAccessedAt sets LastAccessedAt field to given value.

### HasLastAccessedAt

`func (o *FeatureStatusDTO) HasLastAccessedAt() bool`

HasLastAccessedAt returns a boolean if a field has been set.

### GetCompletedAt

`func (o *FeatureStatusDTO) GetCompletedAt() time.Time`

GetCompletedAt returns the CompletedAt field if non-nil, zero value otherwise.

### GetCompletedAtOk

`func (o *FeatureStatusDTO) GetCompletedAtOk() (*time.Time, bool)`

GetCompletedAtOk returns a tuple with the CompletedAt field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetCompletedAt

`func (o *FeatureStatusDTO) SetCompletedAt(v time.Time)`

SetCompletedAt sets CompletedAt field to given value.

### HasCompletedAt

`func (o *FeatureStatusDTO) HasCompletedAt() bool`

HasCompletedAt returns a boolean if a field has been set.


[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


