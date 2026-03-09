# CampaignResponse

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**Id** | Pointer to **string** |  | [optional] 
**Name** | Pointer to **string** |  | [optional] 
**Description** | Pointer to **string** |  | [optional] 
**SurveyId** | Pointer to **string** |  | [optional] 
**SurveySnapshotId** | Pointer to **string** |  | [optional] 
**DefaultWeightProfileId** | Pointer to **string** |  | [optional] 
**AuthMode** | Pointer to **string** |  | [optional] 
**Status** | Pointer to **string** |  | [optional] 
**Active** | Pointer to **bool** |  | [optional] 
**CreatedBy** | Pointer to **string** |  | [optional] 
**CreatedAt** | Pointer to **time.Time** |  | [optional] 
**UpdatedBy** | Pointer to **string** |  | [optional] 
**UpdatedAt** | Pointer to **time.Time** |  | [optional] 

## Methods

### NewCampaignResponse

`func NewCampaignResponse() *CampaignResponse`

NewCampaignResponse instantiates a new CampaignResponse object
This constructor will assign default values to properties that have it defined,
and makes sure properties required by API are set, but the set of arguments
will change when the set of required properties is changed

### NewCampaignResponseWithDefaults

`func NewCampaignResponseWithDefaults() *CampaignResponse`

NewCampaignResponseWithDefaults instantiates a new CampaignResponse object
This constructor will only assign default values to properties that have it defined,
but it doesn't guarantee that properties required by API are set

### GetId

`func (o *CampaignResponse) GetId() string`

GetId returns the Id field if non-nil, zero value otherwise.

### GetIdOk

`func (o *CampaignResponse) GetIdOk() (*string, bool)`

GetIdOk returns a tuple with the Id field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetId

`func (o *CampaignResponse) SetId(v string)`

SetId sets Id field to given value.

### HasId

`func (o *CampaignResponse) HasId() bool`

HasId returns a boolean if a field has been set.

### GetName

`func (o *CampaignResponse) GetName() string`

GetName returns the Name field if non-nil, zero value otherwise.

### GetNameOk

`func (o *CampaignResponse) GetNameOk() (*string, bool)`

GetNameOk returns a tuple with the Name field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetName

`func (o *CampaignResponse) SetName(v string)`

SetName sets Name field to given value.

### HasName

`func (o *CampaignResponse) HasName() bool`

HasName returns a boolean if a field has been set.

### GetDescription

`func (o *CampaignResponse) GetDescription() string`

GetDescription returns the Description field if non-nil, zero value otherwise.

### GetDescriptionOk

`func (o *CampaignResponse) GetDescriptionOk() (*string, bool)`

GetDescriptionOk returns a tuple with the Description field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetDescription

`func (o *CampaignResponse) SetDescription(v string)`

SetDescription sets Description field to given value.

### HasDescription

`func (o *CampaignResponse) HasDescription() bool`

HasDescription returns a boolean if a field has been set.

### GetSurveyId

`func (o *CampaignResponse) GetSurveyId() string`

GetSurveyId returns the SurveyId field if non-nil, zero value otherwise.

### GetSurveyIdOk

`func (o *CampaignResponse) GetSurveyIdOk() (*string, bool)`

GetSurveyIdOk returns a tuple with the SurveyId field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetSurveyId

`func (o *CampaignResponse) SetSurveyId(v string)`

SetSurveyId sets SurveyId field to given value.

### HasSurveyId

`func (o *CampaignResponse) HasSurveyId() bool`

HasSurveyId returns a boolean if a field has been set.

### GetSurveySnapshotId

`func (o *CampaignResponse) GetSurveySnapshotId() string`

GetSurveySnapshotId returns the SurveySnapshotId field if non-nil, zero value otherwise.

### GetSurveySnapshotIdOk

`func (o *CampaignResponse) GetSurveySnapshotIdOk() (*string, bool)`

GetSurveySnapshotIdOk returns a tuple with the SurveySnapshotId field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetSurveySnapshotId

`func (o *CampaignResponse) SetSurveySnapshotId(v string)`

SetSurveySnapshotId sets SurveySnapshotId field to given value.

### HasSurveySnapshotId

`func (o *CampaignResponse) HasSurveySnapshotId() bool`

HasSurveySnapshotId returns a boolean if a field has been set.

### GetDefaultWeightProfileId

`func (o *CampaignResponse) GetDefaultWeightProfileId() string`

GetDefaultWeightProfileId returns the DefaultWeightProfileId field if non-nil, zero value otherwise.

### GetDefaultWeightProfileIdOk

`func (o *CampaignResponse) GetDefaultWeightProfileIdOk() (*string, bool)`

GetDefaultWeightProfileIdOk returns a tuple with the DefaultWeightProfileId field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetDefaultWeightProfileId

`func (o *CampaignResponse) SetDefaultWeightProfileId(v string)`

SetDefaultWeightProfileId sets DefaultWeightProfileId field to given value.

### HasDefaultWeightProfileId

`func (o *CampaignResponse) HasDefaultWeightProfileId() bool`

HasDefaultWeightProfileId returns a boolean if a field has been set.

### GetAuthMode

`func (o *CampaignResponse) GetAuthMode() string`

GetAuthMode returns the AuthMode field if non-nil, zero value otherwise.

### GetAuthModeOk

`func (o *CampaignResponse) GetAuthModeOk() (*string, bool)`

GetAuthModeOk returns a tuple with the AuthMode field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetAuthMode

`func (o *CampaignResponse) SetAuthMode(v string)`

SetAuthMode sets AuthMode field to given value.

### HasAuthMode

`func (o *CampaignResponse) HasAuthMode() bool`

HasAuthMode returns a boolean if a field has been set.

### GetStatus

`func (o *CampaignResponse) GetStatus() string`

GetStatus returns the Status field if non-nil, zero value otherwise.

### GetStatusOk

`func (o *CampaignResponse) GetStatusOk() (*string, bool)`

GetStatusOk returns a tuple with the Status field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetStatus

`func (o *CampaignResponse) SetStatus(v string)`

SetStatus sets Status field to given value.

### HasStatus

`func (o *CampaignResponse) HasStatus() bool`

HasStatus returns a boolean if a field has been set.

### GetActive

`func (o *CampaignResponse) GetActive() bool`

GetActive returns the Active field if non-nil, zero value otherwise.

### GetActiveOk

`func (o *CampaignResponse) GetActiveOk() (*bool, bool)`

GetActiveOk returns a tuple with the Active field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetActive

`func (o *CampaignResponse) SetActive(v bool)`

SetActive sets Active field to given value.

### HasActive

`func (o *CampaignResponse) HasActive() bool`

HasActive returns a boolean if a field has been set.

### GetCreatedBy

`func (o *CampaignResponse) GetCreatedBy() string`

GetCreatedBy returns the CreatedBy field if non-nil, zero value otherwise.

### GetCreatedByOk

`func (o *CampaignResponse) GetCreatedByOk() (*string, bool)`

GetCreatedByOk returns a tuple with the CreatedBy field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetCreatedBy

`func (o *CampaignResponse) SetCreatedBy(v string)`

SetCreatedBy sets CreatedBy field to given value.

### HasCreatedBy

`func (o *CampaignResponse) HasCreatedBy() bool`

HasCreatedBy returns a boolean if a field has been set.

### GetCreatedAt

`func (o *CampaignResponse) GetCreatedAt() time.Time`

GetCreatedAt returns the CreatedAt field if non-nil, zero value otherwise.

### GetCreatedAtOk

`func (o *CampaignResponse) GetCreatedAtOk() (*time.Time, bool)`

GetCreatedAtOk returns a tuple with the CreatedAt field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetCreatedAt

`func (o *CampaignResponse) SetCreatedAt(v time.Time)`

SetCreatedAt sets CreatedAt field to given value.

### HasCreatedAt

`func (o *CampaignResponse) HasCreatedAt() bool`

HasCreatedAt returns a boolean if a field has been set.

### GetUpdatedBy

`func (o *CampaignResponse) GetUpdatedBy() string`

GetUpdatedBy returns the UpdatedBy field if non-nil, zero value otherwise.

### GetUpdatedByOk

`func (o *CampaignResponse) GetUpdatedByOk() (*string, bool)`

GetUpdatedByOk returns a tuple with the UpdatedBy field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetUpdatedBy

`func (o *CampaignResponse) SetUpdatedBy(v string)`

SetUpdatedBy sets UpdatedBy field to given value.

### HasUpdatedBy

`func (o *CampaignResponse) HasUpdatedBy() bool`

HasUpdatedBy returns a boolean if a field has been set.

### GetUpdatedAt

`func (o *CampaignResponse) GetUpdatedAt() time.Time`

GetUpdatedAt returns the UpdatedAt field if non-nil, zero value otherwise.

### GetUpdatedAtOk

`func (o *CampaignResponse) GetUpdatedAtOk() (*time.Time, bool)`

GetUpdatedAtOk returns a tuple with the UpdatedAt field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetUpdatedAt

`func (o *CampaignResponse) SetUpdatedAt(v time.Time)`

SetUpdatedAt sets UpdatedAt field to given value.

### HasUpdatedAt

`func (o *CampaignResponse) HasUpdatedAt() bool`

HasUpdatedAt returns a boolean if a field has been set.


[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


