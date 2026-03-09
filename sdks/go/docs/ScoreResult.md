# ScoreResult

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**CampaignId** | Pointer to **string** |  | [optional] 
**WeightProfileId** | Pointer to **string** |  | [optional] 
**TotalScore** | Pointer to **float32** |  | [optional] 
**CategoryScores** | Pointer to [**[]ScoreResultCategoryScoresInner**](ScoreResultCategoryScoresInner.md) |  | [optional] 

## Methods

### NewScoreResult

`func NewScoreResult() *ScoreResult`

NewScoreResult instantiates a new ScoreResult object
This constructor will assign default values to properties that have it defined,
and makes sure properties required by API are set, but the set of arguments
will change when the set of required properties is changed

### NewScoreResultWithDefaults

`func NewScoreResultWithDefaults() *ScoreResult`

NewScoreResultWithDefaults instantiates a new ScoreResult object
This constructor will only assign default values to properties that have it defined,
but it doesn't guarantee that properties required by API are set

### GetCampaignId

`func (o *ScoreResult) GetCampaignId() string`

GetCampaignId returns the CampaignId field if non-nil, zero value otherwise.

### GetCampaignIdOk

`func (o *ScoreResult) GetCampaignIdOk() (*string, bool)`

GetCampaignIdOk returns a tuple with the CampaignId field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetCampaignId

`func (o *ScoreResult) SetCampaignId(v string)`

SetCampaignId sets CampaignId field to given value.

### HasCampaignId

`func (o *ScoreResult) HasCampaignId() bool`

HasCampaignId returns a boolean if a field has been set.

### GetWeightProfileId

`func (o *ScoreResult) GetWeightProfileId() string`

GetWeightProfileId returns the WeightProfileId field if non-nil, zero value otherwise.

### GetWeightProfileIdOk

`func (o *ScoreResult) GetWeightProfileIdOk() (*string, bool)`

GetWeightProfileIdOk returns a tuple with the WeightProfileId field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetWeightProfileId

`func (o *ScoreResult) SetWeightProfileId(v string)`

SetWeightProfileId sets WeightProfileId field to given value.

### HasWeightProfileId

`func (o *ScoreResult) HasWeightProfileId() bool`

HasWeightProfileId returns a boolean if a field has been set.

### GetTotalScore

`func (o *ScoreResult) GetTotalScore() float32`

GetTotalScore returns the TotalScore field if non-nil, zero value otherwise.

### GetTotalScoreOk

`func (o *ScoreResult) GetTotalScoreOk() (*float32, bool)`

GetTotalScoreOk returns a tuple with the TotalScore field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetTotalScore

`func (o *ScoreResult) SetTotalScore(v float32)`

SetTotalScore sets TotalScore field to given value.

### HasTotalScore

`func (o *ScoreResult) HasTotalScore() bool`

HasTotalScore returns a boolean if a field has been set.

### GetCategoryScores

`func (o *ScoreResult) GetCategoryScores() []ScoreResultCategoryScoresInner`

GetCategoryScores returns the CategoryScores field if non-nil, zero value otherwise.

### GetCategoryScoresOk

`func (o *ScoreResult) GetCategoryScoresOk() (*[]ScoreResultCategoryScoresInner, bool)`

GetCategoryScoresOk returns a tuple with the CategoryScores field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetCategoryScores

`func (o *ScoreResult) SetCategoryScores(v []ScoreResultCategoryScoresInner)`

SetCategoryScores sets CategoryScores field to given value.

### HasCategoryScores

`func (o *ScoreResult) HasCategoryScores() bool`

HasCategoryScores returns a boolean if a field has been set.


[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


