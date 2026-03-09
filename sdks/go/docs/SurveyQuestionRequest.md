# SurveyQuestionRequest

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**QuestionId** | **string** |  | 
**CategoryId** | Pointer to **string** |  | [optional] 
**CategoryWeightPercentage** | Pointer to **float32** |  | [optional] 
**SortOrder** | **int32** |  | 
**Mandatory** | Pointer to **bool** |  | [optional] 
**AnswerConfig** | Pointer to **string** |  | [optional] 
**PinnedQuestionText** | Pointer to **string** |  | [optional] 
**PinnedQuestionMaxScore** | Pointer to **float32** |  | [optional] 
**PinnedQuestionOptionConfig** | Pointer to **string** |  | [optional] 
**PinnedCategoryName** | Pointer to **string** |  | [optional] 
**PinnedCategoryDescription** | Pointer to **string** |  | [optional] 

## Methods

### NewSurveyQuestionRequest

`func NewSurveyQuestionRequest(questionId string, sortOrder int32, ) *SurveyQuestionRequest`

NewSurveyQuestionRequest instantiates a new SurveyQuestionRequest object
This constructor will assign default values to properties that have it defined,
and makes sure properties required by API are set, but the set of arguments
will change when the set of required properties is changed

### NewSurveyQuestionRequestWithDefaults

`func NewSurveyQuestionRequestWithDefaults() *SurveyQuestionRequest`

NewSurveyQuestionRequestWithDefaults instantiates a new SurveyQuestionRequest object
This constructor will only assign default values to properties that have it defined,
but it doesn't guarantee that properties required by API are set

### GetQuestionId

`func (o *SurveyQuestionRequest) GetQuestionId() string`

GetQuestionId returns the QuestionId field if non-nil, zero value otherwise.

### GetQuestionIdOk

`func (o *SurveyQuestionRequest) GetQuestionIdOk() (*string, bool)`

GetQuestionIdOk returns a tuple with the QuestionId field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetQuestionId

`func (o *SurveyQuestionRequest) SetQuestionId(v string)`

SetQuestionId sets QuestionId field to given value.


### GetCategoryId

`func (o *SurveyQuestionRequest) GetCategoryId() string`

GetCategoryId returns the CategoryId field if non-nil, zero value otherwise.

### GetCategoryIdOk

`func (o *SurveyQuestionRequest) GetCategoryIdOk() (*string, bool)`

GetCategoryIdOk returns a tuple with the CategoryId field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetCategoryId

`func (o *SurveyQuestionRequest) SetCategoryId(v string)`

SetCategoryId sets CategoryId field to given value.

### HasCategoryId

`func (o *SurveyQuestionRequest) HasCategoryId() bool`

HasCategoryId returns a boolean if a field has been set.

### GetCategoryWeightPercentage

`func (o *SurveyQuestionRequest) GetCategoryWeightPercentage() float32`

GetCategoryWeightPercentage returns the CategoryWeightPercentage field if non-nil, zero value otherwise.

### GetCategoryWeightPercentageOk

`func (o *SurveyQuestionRequest) GetCategoryWeightPercentageOk() (*float32, bool)`

GetCategoryWeightPercentageOk returns a tuple with the CategoryWeightPercentage field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetCategoryWeightPercentage

`func (o *SurveyQuestionRequest) SetCategoryWeightPercentage(v float32)`

SetCategoryWeightPercentage sets CategoryWeightPercentage field to given value.

### HasCategoryWeightPercentage

`func (o *SurveyQuestionRequest) HasCategoryWeightPercentage() bool`

HasCategoryWeightPercentage returns a boolean if a field has been set.

### GetSortOrder

`func (o *SurveyQuestionRequest) GetSortOrder() int32`

GetSortOrder returns the SortOrder field if non-nil, zero value otherwise.

### GetSortOrderOk

`func (o *SurveyQuestionRequest) GetSortOrderOk() (*int32, bool)`

GetSortOrderOk returns a tuple with the SortOrder field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetSortOrder

`func (o *SurveyQuestionRequest) SetSortOrder(v int32)`

SetSortOrder sets SortOrder field to given value.


### GetMandatory

`func (o *SurveyQuestionRequest) GetMandatory() bool`

GetMandatory returns the Mandatory field if non-nil, zero value otherwise.

### GetMandatoryOk

`func (o *SurveyQuestionRequest) GetMandatoryOk() (*bool, bool)`

GetMandatoryOk returns a tuple with the Mandatory field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetMandatory

`func (o *SurveyQuestionRequest) SetMandatory(v bool)`

SetMandatory sets Mandatory field to given value.

### HasMandatory

`func (o *SurveyQuestionRequest) HasMandatory() bool`

HasMandatory returns a boolean if a field has been set.

### GetAnswerConfig

`func (o *SurveyQuestionRequest) GetAnswerConfig() string`

GetAnswerConfig returns the AnswerConfig field if non-nil, zero value otherwise.

### GetAnswerConfigOk

`func (o *SurveyQuestionRequest) GetAnswerConfigOk() (*string, bool)`

GetAnswerConfigOk returns a tuple with the AnswerConfig field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetAnswerConfig

`func (o *SurveyQuestionRequest) SetAnswerConfig(v string)`

SetAnswerConfig sets AnswerConfig field to given value.

### HasAnswerConfig

`func (o *SurveyQuestionRequest) HasAnswerConfig() bool`

HasAnswerConfig returns a boolean if a field has been set.

### GetPinnedQuestionText

`func (o *SurveyQuestionRequest) GetPinnedQuestionText() string`

GetPinnedQuestionText returns the PinnedQuestionText field if non-nil, zero value otherwise.

### GetPinnedQuestionTextOk

`func (o *SurveyQuestionRequest) GetPinnedQuestionTextOk() (*string, bool)`

GetPinnedQuestionTextOk returns a tuple with the PinnedQuestionText field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetPinnedQuestionText

`func (o *SurveyQuestionRequest) SetPinnedQuestionText(v string)`

SetPinnedQuestionText sets PinnedQuestionText field to given value.

### HasPinnedQuestionText

`func (o *SurveyQuestionRequest) HasPinnedQuestionText() bool`

HasPinnedQuestionText returns a boolean if a field has been set.

### GetPinnedQuestionMaxScore

`func (o *SurveyQuestionRequest) GetPinnedQuestionMaxScore() float32`

GetPinnedQuestionMaxScore returns the PinnedQuestionMaxScore field if non-nil, zero value otherwise.

### GetPinnedQuestionMaxScoreOk

`func (o *SurveyQuestionRequest) GetPinnedQuestionMaxScoreOk() (*float32, bool)`

GetPinnedQuestionMaxScoreOk returns a tuple with the PinnedQuestionMaxScore field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetPinnedQuestionMaxScore

`func (o *SurveyQuestionRequest) SetPinnedQuestionMaxScore(v float32)`

SetPinnedQuestionMaxScore sets PinnedQuestionMaxScore field to given value.

### HasPinnedQuestionMaxScore

`func (o *SurveyQuestionRequest) HasPinnedQuestionMaxScore() bool`

HasPinnedQuestionMaxScore returns a boolean if a field has been set.

### GetPinnedQuestionOptionConfig

`func (o *SurveyQuestionRequest) GetPinnedQuestionOptionConfig() string`

GetPinnedQuestionOptionConfig returns the PinnedQuestionOptionConfig field if non-nil, zero value otherwise.

### GetPinnedQuestionOptionConfigOk

`func (o *SurveyQuestionRequest) GetPinnedQuestionOptionConfigOk() (*string, bool)`

GetPinnedQuestionOptionConfigOk returns a tuple with the PinnedQuestionOptionConfig field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetPinnedQuestionOptionConfig

`func (o *SurveyQuestionRequest) SetPinnedQuestionOptionConfig(v string)`

SetPinnedQuestionOptionConfig sets PinnedQuestionOptionConfig field to given value.

### HasPinnedQuestionOptionConfig

`func (o *SurveyQuestionRequest) HasPinnedQuestionOptionConfig() bool`

HasPinnedQuestionOptionConfig returns a boolean if a field has been set.

### GetPinnedCategoryName

`func (o *SurveyQuestionRequest) GetPinnedCategoryName() string`

GetPinnedCategoryName returns the PinnedCategoryName field if non-nil, zero value otherwise.

### GetPinnedCategoryNameOk

`func (o *SurveyQuestionRequest) GetPinnedCategoryNameOk() (*string, bool)`

GetPinnedCategoryNameOk returns a tuple with the PinnedCategoryName field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetPinnedCategoryName

`func (o *SurveyQuestionRequest) SetPinnedCategoryName(v string)`

SetPinnedCategoryName sets PinnedCategoryName field to given value.

### HasPinnedCategoryName

`func (o *SurveyQuestionRequest) HasPinnedCategoryName() bool`

HasPinnedCategoryName returns a boolean if a field has been set.

### GetPinnedCategoryDescription

`func (o *SurveyQuestionRequest) GetPinnedCategoryDescription() string`

GetPinnedCategoryDescription returns the PinnedCategoryDescription field if non-nil, zero value otherwise.

### GetPinnedCategoryDescriptionOk

`func (o *SurveyQuestionRequest) GetPinnedCategoryDescriptionOk() (*string, bool)`

GetPinnedCategoryDescriptionOk returns a tuple with the PinnedCategoryDescription field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetPinnedCategoryDescription

`func (o *SurveyQuestionRequest) SetPinnedCategoryDescription(v string)`

SetPinnedCategoryDescription sets PinnedCategoryDescription field to given value.

### HasPinnedCategoryDescription

`func (o *SurveyQuestionRequest) HasPinnedCategoryDescription() bool`

HasPinnedCategoryDescription returns a boolean if a field has been set.


[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


