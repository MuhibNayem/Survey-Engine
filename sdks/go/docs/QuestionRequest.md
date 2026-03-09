# QuestionRequest

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**Text** | **string** |  | 
**Type** | **string** |  | 
**MaxScore** | **float32** |  | 
**OptionConfig** | Pointer to **string** | JSON string containing question-level options/configuration. | [optional] 

## Methods

### NewQuestionRequest

`func NewQuestionRequest(text string, type_ string, maxScore float32, ) *QuestionRequest`

NewQuestionRequest instantiates a new QuestionRequest object
This constructor will assign default values to properties that have it defined,
and makes sure properties required by API are set, but the set of arguments
will change when the set of required properties is changed

### NewQuestionRequestWithDefaults

`func NewQuestionRequestWithDefaults() *QuestionRequest`

NewQuestionRequestWithDefaults instantiates a new QuestionRequest object
This constructor will only assign default values to properties that have it defined,
but it doesn't guarantee that properties required by API are set

### GetText

`func (o *QuestionRequest) GetText() string`

GetText returns the Text field if non-nil, zero value otherwise.

### GetTextOk

`func (o *QuestionRequest) GetTextOk() (*string, bool)`

GetTextOk returns a tuple with the Text field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetText

`func (o *QuestionRequest) SetText(v string)`

SetText sets Text field to given value.


### GetType

`func (o *QuestionRequest) GetType() string`

GetType returns the Type field if non-nil, zero value otherwise.

### GetTypeOk

`func (o *QuestionRequest) GetTypeOk() (*string, bool)`

GetTypeOk returns a tuple with the Type field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetType

`func (o *QuestionRequest) SetType(v string)`

SetType sets Type field to given value.


### GetMaxScore

`func (o *QuestionRequest) GetMaxScore() float32`

GetMaxScore returns the MaxScore field if non-nil, zero value otherwise.

### GetMaxScoreOk

`func (o *QuestionRequest) GetMaxScoreOk() (*float32, bool)`

GetMaxScoreOk returns a tuple with the MaxScore field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetMaxScore

`func (o *QuestionRequest) SetMaxScore(v float32)`

SetMaxScore sets MaxScore field to given value.


### GetOptionConfig

`func (o *QuestionRequest) GetOptionConfig() string`

GetOptionConfig returns the OptionConfig field if non-nil, zero value otherwise.

### GetOptionConfigOk

`func (o *QuestionRequest) GetOptionConfigOk() (*string, bool)`

GetOptionConfigOk returns a tuple with the OptionConfig field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetOptionConfig

`func (o *QuestionRequest) SetOptionConfig(v string)`

SetOptionConfig sets OptionConfig field to given value.

### HasOptionConfig

`func (o *QuestionRequest) HasOptionConfig() bool`

HasOptionConfig returns a boolean if a field has been set.


[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


