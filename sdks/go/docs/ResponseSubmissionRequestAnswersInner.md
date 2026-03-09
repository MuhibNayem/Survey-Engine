# ResponseSubmissionRequestAnswersInner

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**QuestionId** | **string** |  | 
**QuestionVersionId** | Pointer to **string** |  | [optional] 
**Value** | Pointer to **string** |  | [optional] 
**Score** | Pointer to **float32** |  | [optional] 

## Methods

### NewResponseSubmissionRequestAnswersInner

`func NewResponseSubmissionRequestAnswersInner(questionId string, ) *ResponseSubmissionRequestAnswersInner`

NewResponseSubmissionRequestAnswersInner instantiates a new ResponseSubmissionRequestAnswersInner object
This constructor will assign default values to properties that have it defined,
and makes sure properties required by API are set, but the set of arguments
will change when the set of required properties is changed

### NewResponseSubmissionRequestAnswersInnerWithDefaults

`func NewResponseSubmissionRequestAnswersInnerWithDefaults() *ResponseSubmissionRequestAnswersInner`

NewResponseSubmissionRequestAnswersInnerWithDefaults instantiates a new ResponseSubmissionRequestAnswersInner object
This constructor will only assign default values to properties that have it defined,
but it doesn't guarantee that properties required by API are set

### GetQuestionId

`func (o *ResponseSubmissionRequestAnswersInner) GetQuestionId() string`

GetQuestionId returns the QuestionId field if non-nil, zero value otherwise.

### GetQuestionIdOk

`func (o *ResponseSubmissionRequestAnswersInner) GetQuestionIdOk() (*string, bool)`

GetQuestionIdOk returns a tuple with the QuestionId field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetQuestionId

`func (o *ResponseSubmissionRequestAnswersInner) SetQuestionId(v string)`

SetQuestionId sets QuestionId field to given value.


### GetQuestionVersionId

`func (o *ResponseSubmissionRequestAnswersInner) GetQuestionVersionId() string`

GetQuestionVersionId returns the QuestionVersionId field if non-nil, zero value otherwise.

### GetQuestionVersionIdOk

`func (o *ResponseSubmissionRequestAnswersInner) GetQuestionVersionIdOk() (*string, bool)`

GetQuestionVersionIdOk returns a tuple with the QuestionVersionId field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetQuestionVersionId

`func (o *ResponseSubmissionRequestAnswersInner) SetQuestionVersionId(v string)`

SetQuestionVersionId sets QuestionVersionId field to given value.

### HasQuestionVersionId

`func (o *ResponseSubmissionRequestAnswersInner) HasQuestionVersionId() bool`

HasQuestionVersionId returns a boolean if a field has been set.

### GetValue

`func (o *ResponseSubmissionRequestAnswersInner) GetValue() string`

GetValue returns the Value field if non-nil, zero value otherwise.

### GetValueOk

`func (o *ResponseSubmissionRequestAnswersInner) GetValueOk() (*string, bool)`

GetValueOk returns a tuple with the Value field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetValue

`func (o *ResponseSubmissionRequestAnswersInner) SetValue(v string)`

SetValue sets Value field to given value.

### HasValue

`func (o *ResponseSubmissionRequestAnswersInner) HasValue() bool`

HasValue returns a boolean if a field has been set.

### GetScore

`func (o *ResponseSubmissionRequestAnswersInner) GetScore() float32`

GetScore returns the Score field if non-nil, zero value otherwise.

### GetScoreOk

`func (o *ResponseSubmissionRequestAnswersInner) GetScoreOk() (*float32, bool)`

GetScoreOk returns a tuple with the Score field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetScore

`func (o *ResponseSubmissionRequestAnswersInner) SetScore(v float32)`

SetScore sets Score field to given value.

### HasScore

`func (o *ResponseSubmissionRequestAnswersInner) HasScore() bool`

HasScore returns a boolean if a field has been set.


[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


