# QuestionResponse

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**Id** | Pointer to **string** |  | [optional] 
**Text** | Pointer to **string** |  | [optional] 
**Type** | Pointer to **string** |  | [optional] 
**MaxScore** | Pointer to **float32** |  | [optional] 
**OptionConfig** | Pointer to **string** |  | [optional] 
**CurrentVersion** | Pointer to **int32** |  | [optional] 
**Active** | Pointer to **bool** |  | [optional] 
**CreatedBy** | Pointer to **string** |  | [optional] 
**CreatedAt** | Pointer to **time.Time** |  | [optional] 
**UpdatedBy** | Pointer to **string** |  | [optional] 
**UpdatedAt** | Pointer to **time.Time** |  | [optional] 

## Methods

### NewQuestionResponse

`func NewQuestionResponse() *QuestionResponse`

NewQuestionResponse instantiates a new QuestionResponse object
This constructor will assign default values to properties that have it defined,
and makes sure properties required by API are set, but the set of arguments
will change when the set of required properties is changed

### NewQuestionResponseWithDefaults

`func NewQuestionResponseWithDefaults() *QuestionResponse`

NewQuestionResponseWithDefaults instantiates a new QuestionResponse object
This constructor will only assign default values to properties that have it defined,
but it doesn't guarantee that properties required by API are set

### GetId

`func (o *QuestionResponse) GetId() string`

GetId returns the Id field if non-nil, zero value otherwise.

### GetIdOk

`func (o *QuestionResponse) GetIdOk() (*string, bool)`

GetIdOk returns a tuple with the Id field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetId

`func (o *QuestionResponse) SetId(v string)`

SetId sets Id field to given value.

### HasId

`func (o *QuestionResponse) HasId() bool`

HasId returns a boolean if a field has been set.

### GetText

`func (o *QuestionResponse) GetText() string`

GetText returns the Text field if non-nil, zero value otherwise.

### GetTextOk

`func (o *QuestionResponse) GetTextOk() (*string, bool)`

GetTextOk returns a tuple with the Text field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetText

`func (o *QuestionResponse) SetText(v string)`

SetText sets Text field to given value.

### HasText

`func (o *QuestionResponse) HasText() bool`

HasText returns a boolean if a field has been set.

### GetType

`func (o *QuestionResponse) GetType() string`

GetType returns the Type field if non-nil, zero value otherwise.

### GetTypeOk

`func (o *QuestionResponse) GetTypeOk() (*string, bool)`

GetTypeOk returns a tuple with the Type field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetType

`func (o *QuestionResponse) SetType(v string)`

SetType sets Type field to given value.

### HasType

`func (o *QuestionResponse) HasType() bool`

HasType returns a boolean if a field has been set.

### GetMaxScore

`func (o *QuestionResponse) GetMaxScore() float32`

GetMaxScore returns the MaxScore field if non-nil, zero value otherwise.

### GetMaxScoreOk

`func (o *QuestionResponse) GetMaxScoreOk() (*float32, bool)`

GetMaxScoreOk returns a tuple with the MaxScore field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetMaxScore

`func (o *QuestionResponse) SetMaxScore(v float32)`

SetMaxScore sets MaxScore field to given value.

### HasMaxScore

`func (o *QuestionResponse) HasMaxScore() bool`

HasMaxScore returns a boolean if a field has been set.

### GetOptionConfig

`func (o *QuestionResponse) GetOptionConfig() string`

GetOptionConfig returns the OptionConfig field if non-nil, zero value otherwise.

### GetOptionConfigOk

`func (o *QuestionResponse) GetOptionConfigOk() (*string, bool)`

GetOptionConfigOk returns a tuple with the OptionConfig field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetOptionConfig

`func (o *QuestionResponse) SetOptionConfig(v string)`

SetOptionConfig sets OptionConfig field to given value.

### HasOptionConfig

`func (o *QuestionResponse) HasOptionConfig() bool`

HasOptionConfig returns a boolean if a field has been set.

### GetCurrentVersion

`func (o *QuestionResponse) GetCurrentVersion() int32`

GetCurrentVersion returns the CurrentVersion field if non-nil, zero value otherwise.

### GetCurrentVersionOk

`func (o *QuestionResponse) GetCurrentVersionOk() (*int32, bool)`

GetCurrentVersionOk returns a tuple with the CurrentVersion field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetCurrentVersion

`func (o *QuestionResponse) SetCurrentVersion(v int32)`

SetCurrentVersion sets CurrentVersion field to given value.

### HasCurrentVersion

`func (o *QuestionResponse) HasCurrentVersion() bool`

HasCurrentVersion returns a boolean if a field has been set.

### GetActive

`func (o *QuestionResponse) GetActive() bool`

GetActive returns the Active field if non-nil, zero value otherwise.

### GetActiveOk

`func (o *QuestionResponse) GetActiveOk() (*bool, bool)`

GetActiveOk returns a tuple with the Active field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetActive

`func (o *QuestionResponse) SetActive(v bool)`

SetActive sets Active field to given value.

### HasActive

`func (o *QuestionResponse) HasActive() bool`

HasActive returns a boolean if a field has been set.

### GetCreatedBy

`func (o *QuestionResponse) GetCreatedBy() string`

GetCreatedBy returns the CreatedBy field if non-nil, zero value otherwise.

### GetCreatedByOk

`func (o *QuestionResponse) GetCreatedByOk() (*string, bool)`

GetCreatedByOk returns a tuple with the CreatedBy field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetCreatedBy

`func (o *QuestionResponse) SetCreatedBy(v string)`

SetCreatedBy sets CreatedBy field to given value.

### HasCreatedBy

`func (o *QuestionResponse) HasCreatedBy() bool`

HasCreatedBy returns a boolean if a field has been set.

### GetCreatedAt

`func (o *QuestionResponse) GetCreatedAt() time.Time`

GetCreatedAt returns the CreatedAt field if non-nil, zero value otherwise.

### GetCreatedAtOk

`func (o *QuestionResponse) GetCreatedAtOk() (*time.Time, bool)`

GetCreatedAtOk returns a tuple with the CreatedAt field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetCreatedAt

`func (o *QuestionResponse) SetCreatedAt(v time.Time)`

SetCreatedAt sets CreatedAt field to given value.

### HasCreatedAt

`func (o *QuestionResponse) HasCreatedAt() bool`

HasCreatedAt returns a boolean if a field has been set.

### GetUpdatedBy

`func (o *QuestionResponse) GetUpdatedBy() string`

GetUpdatedBy returns the UpdatedBy field if non-nil, zero value otherwise.

### GetUpdatedByOk

`func (o *QuestionResponse) GetUpdatedByOk() (*string, bool)`

GetUpdatedByOk returns a tuple with the UpdatedBy field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetUpdatedBy

`func (o *QuestionResponse) SetUpdatedBy(v string)`

SetUpdatedBy sets UpdatedBy field to given value.

### HasUpdatedBy

`func (o *QuestionResponse) HasUpdatedBy() bool`

HasUpdatedBy returns a boolean if a field has been set.

### GetUpdatedAt

`func (o *QuestionResponse) GetUpdatedAt() time.Time`

GetUpdatedAt returns the UpdatedAt field if non-nil, zero value otherwise.

### GetUpdatedAtOk

`func (o *QuestionResponse) GetUpdatedAtOk() (*time.Time, bool)`

GetUpdatedAtOk returns a tuple with the UpdatedAt field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetUpdatedAt

`func (o *QuestionResponse) SetUpdatedAt(v time.Time)`

SetUpdatedAt sets UpdatedAt field to given value.

### HasUpdatedAt

`func (o *QuestionResponse) HasUpdatedAt() bool`

HasUpdatedAt returns a boolean if a field has been set.


[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


