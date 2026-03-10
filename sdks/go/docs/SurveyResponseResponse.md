# SurveyResponseResponse

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**Id** | Pointer to **string** |  | [optional] 
**CampaignId** | Pointer to **string** |  | [optional] 
**SurveySnapshotId** | Pointer to **string** |  | [optional] 
**RespondentIdentifier** | Pointer to **string** |  | [optional] 
**RespondentMetadata** | Pointer to **string** |  | [optional] 
**Status** | Pointer to **string** |  | [optional] 
**StartedAt** | Pointer to **time.Time** |  | [optional] 
**SubmittedAt** | Pointer to **time.Time** |  | [optional] 
**LockedAt** | Pointer to **time.Time** |  | [optional] 
**WeightProfileId** | Pointer to **string** |  | [optional] 
**WeightedTotalScore** | Pointer to **float32** |  | [optional] 
**ScoredAt** | Pointer to **time.Time** |  | [optional] 
**Answers** | Pointer to [**[]SurveyResponseResponseAnswersInner**](SurveyResponseResponseAnswersInner.md) |  | [optional] 

## Methods

### NewSurveyResponseResponse

`func NewSurveyResponseResponse() *SurveyResponseResponse`

NewSurveyResponseResponse instantiates a new SurveyResponseResponse object
This constructor will assign default values to properties that have it defined,
and makes sure properties required by API are set, but the set of arguments
will change when the set of required properties is changed

### NewSurveyResponseResponseWithDefaults

`func NewSurveyResponseResponseWithDefaults() *SurveyResponseResponse`

NewSurveyResponseResponseWithDefaults instantiates a new SurveyResponseResponse object
This constructor will only assign default values to properties that have it defined,
but it doesn't guarantee that properties required by API are set

### GetId

`func (o *SurveyResponseResponse) GetId() string`

GetId returns the Id field if non-nil, zero value otherwise.

### GetIdOk

`func (o *SurveyResponseResponse) GetIdOk() (*string, bool)`

GetIdOk returns a tuple with the Id field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetId

`func (o *SurveyResponseResponse) SetId(v string)`

SetId sets Id field to given value.

### HasId

`func (o *SurveyResponseResponse) HasId() bool`

HasId returns a boolean if a field has been set.

### GetCampaignId

`func (o *SurveyResponseResponse) GetCampaignId() string`

GetCampaignId returns the CampaignId field if non-nil, zero value otherwise.

### GetCampaignIdOk

`func (o *SurveyResponseResponse) GetCampaignIdOk() (*string, bool)`

GetCampaignIdOk returns a tuple with the CampaignId field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetCampaignId

`func (o *SurveyResponseResponse) SetCampaignId(v string)`

SetCampaignId sets CampaignId field to given value.

### HasCampaignId

`func (o *SurveyResponseResponse) HasCampaignId() bool`

HasCampaignId returns a boolean if a field has been set.

### GetSurveySnapshotId

`func (o *SurveyResponseResponse) GetSurveySnapshotId() string`

GetSurveySnapshotId returns the SurveySnapshotId field if non-nil, zero value otherwise.

### GetSurveySnapshotIdOk

`func (o *SurveyResponseResponse) GetSurveySnapshotIdOk() (*string, bool)`

GetSurveySnapshotIdOk returns a tuple with the SurveySnapshotId field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetSurveySnapshotId

`func (o *SurveyResponseResponse) SetSurveySnapshotId(v string)`

SetSurveySnapshotId sets SurveySnapshotId field to given value.

### HasSurveySnapshotId

`func (o *SurveyResponseResponse) HasSurveySnapshotId() bool`

HasSurveySnapshotId returns a boolean if a field has been set.

### GetRespondentIdentifier

`func (o *SurveyResponseResponse) GetRespondentIdentifier() string`

GetRespondentIdentifier returns the RespondentIdentifier field if non-nil, zero value otherwise.

### GetRespondentIdentifierOk

`func (o *SurveyResponseResponse) GetRespondentIdentifierOk() (*string, bool)`

GetRespondentIdentifierOk returns a tuple with the RespondentIdentifier field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetRespondentIdentifier

`func (o *SurveyResponseResponse) SetRespondentIdentifier(v string)`

SetRespondentIdentifier sets RespondentIdentifier field to given value.

### HasRespondentIdentifier

`func (o *SurveyResponseResponse) HasRespondentIdentifier() bool`

HasRespondentIdentifier returns a boolean if a field has been set.

### GetRespondentMetadata

`func (o *SurveyResponseResponse) GetRespondentMetadata() string`

GetRespondentMetadata returns the RespondentMetadata field if non-nil, zero value otherwise.

### GetRespondentMetadataOk

`func (o *SurveyResponseResponse) GetRespondentMetadataOk() (*string, bool)`

GetRespondentMetadataOk returns a tuple with the RespondentMetadata field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetRespondentMetadata

`func (o *SurveyResponseResponse) SetRespondentMetadata(v string)`

SetRespondentMetadata sets RespondentMetadata field to given value.

### HasRespondentMetadata

`func (o *SurveyResponseResponse) HasRespondentMetadata() bool`

HasRespondentMetadata returns a boolean if a field has been set.

### GetStatus

`func (o *SurveyResponseResponse) GetStatus() string`

GetStatus returns the Status field if non-nil, zero value otherwise.

### GetStatusOk

`func (o *SurveyResponseResponse) GetStatusOk() (*string, bool)`

GetStatusOk returns a tuple with the Status field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetStatus

`func (o *SurveyResponseResponse) SetStatus(v string)`

SetStatus sets Status field to given value.

### HasStatus

`func (o *SurveyResponseResponse) HasStatus() bool`

HasStatus returns a boolean if a field has been set.

### GetStartedAt

`func (o *SurveyResponseResponse) GetStartedAt() time.Time`

GetStartedAt returns the StartedAt field if non-nil, zero value otherwise.

### GetStartedAtOk

`func (o *SurveyResponseResponse) GetStartedAtOk() (*time.Time, bool)`

GetStartedAtOk returns a tuple with the StartedAt field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetStartedAt

`func (o *SurveyResponseResponse) SetStartedAt(v time.Time)`

SetStartedAt sets StartedAt field to given value.

### HasStartedAt

`func (o *SurveyResponseResponse) HasStartedAt() bool`

HasStartedAt returns a boolean if a field has been set.

### GetSubmittedAt

`func (o *SurveyResponseResponse) GetSubmittedAt() time.Time`

GetSubmittedAt returns the SubmittedAt field if non-nil, zero value otherwise.

### GetSubmittedAtOk

`func (o *SurveyResponseResponse) GetSubmittedAtOk() (*time.Time, bool)`

GetSubmittedAtOk returns a tuple with the SubmittedAt field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetSubmittedAt

`func (o *SurveyResponseResponse) SetSubmittedAt(v time.Time)`

SetSubmittedAt sets SubmittedAt field to given value.

### HasSubmittedAt

`func (o *SurveyResponseResponse) HasSubmittedAt() bool`

HasSubmittedAt returns a boolean if a field has been set.

### GetLockedAt

`func (o *SurveyResponseResponse) GetLockedAt() time.Time`

GetLockedAt returns the LockedAt field if non-nil, zero value otherwise.

### GetLockedAtOk

`func (o *SurveyResponseResponse) GetLockedAtOk() (*time.Time, bool)`

GetLockedAtOk returns a tuple with the LockedAt field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetLockedAt

`func (o *SurveyResponseResponse) SetLockedAt(v time.Time)`

SetLockedAt sets LockedAt field to given value.

### HasLockedAt

`func (o *SurveyResponseResponse) HasLockedAt() bool`

HasLockedAt returns a boolean if a field has been set.

### GetWeightProfileId

`func (o *SurveyResponseResponse) GetWeightProfileId() string`

GetWeightProfileId returns the WeightProfileId field if non-nil, zero value otherwise.

### GetWeightProfileIdOk

`func (o *SurveyResponseResponse) GetWeightProfileIdOk() (*string, bool)`

GetWeightProfileIdOk returns a tuple with the WeightProfileId field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetWeightProfileId

`func (o *SurveyResponseResponse) SetWeightProfileId(v string)`

SetWeightProfileId sets WeightProfileId field to given value.

### HasWeightProfileId

`func (o *SurveyResponseResponse) HasWeightProfileId() bool`

HasWeightProfileId returns a boolean if a field has been set.

### GetWeightedTotalScore

`func (o *SurveyResponseResponse) GetWeightedTotalScore() float32`

GetWeightedTotalScore returns the WeightedTotalScore field if non-nil, zero value otherwise.

### GetWeightedTotalScoreOk

`func (o *SurveyResponseResponse) GetWeightedTotalScoreOk() (*float32, bool)`

GetWeightedTotalScoreOk returns a tuple with the WeightedTotalScore field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetWeightedTotalScore

`func (o *SurveyResponseResponse) SetWeightedTotalScore(v float32)`

SetWeightedTotalScore sets WeightedTotalScore field to given value.

### HasWeightedTotalScore

`func (o *SurveyResponseResponse) HasWeightedTotalScore() bool`

HasWeightedTotalScore returns a boolean if a field has been set.

### GetScoredAt

`func (o *SurveyResponseResponse) GetScoredAt() time.Time`

GetScoredAt returns the ScoredAt field if non-nil, zero value otherwise.

### GetScoredAtOk

`func (o *SurveyResponseResponse) GetScoredAtOk() (*time.Time, bool)`

GetScoredAtOk returns a tuple with the ScoredAt field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetScoredAt

`func (o *SurveyResponseResponse) SetScoredAt(v time.Time)`

SetScoredAt sets ScoredAt field to given value.

### HasScoredAt

`func (o *SurveyResponseResponse) HasScoredAt() bool`

HasScoredAt returns a boolean if a field has been set.

### GetAnswers

`func (o *SurveyResponseResponse) GetAnswers() []SurveyResponseResponseAnswersInner`

GetAnswers returns the Answers field if non-nil, zero value otherwise.

### GetAnswersOk

`func (o *SurveyResponseResponse) GetAnswersOk() (*[]SurveyResponseResponseAnswersInner, bool)`

GetAnswersOk returns a tuple with the Answers field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetAnswers

`func (o *SurveyResponseResponse) SetAnswers(v []SurveyResponseResponseAnswersInner)`

SetAnswers sets Answers field to given value.

### HasAnswers

`func (o *SurveyResponseResponse) HasAnswers() bool`

HasAnswers returns a boolean if a field has been set.


[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


