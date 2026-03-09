# SurveyResponsePagesInner

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**Id** | Pointer to **string** |  | [optional] 
**Title** | Pointer to **string** |  | [optional] 
**SortOrder** | Pointer to **int32** |  | [optional] 
**Questions** | Pointer to [**[]SurveyResponsePagesInnerQuestionsInner**](SurveyResponsePagesInnerQuestionsInner.md) |  | [optional] 

## Methods

### NewSurveyResponsePagesInner

`func NewSurveyResponsePagesInner() *SurveyResponsePagesInner`

NewSurveyResponsePagesInner instantiates a new SurveyResponsePagesInner object
This constructor will assign default values to properties that have it defined,
and makes sure properties required by API are set, but the set of arguments
will change when the set of required properties is changed

### NewSurveyResponsePagesInnerWithDefaults

`func NewSurveyResponsePagesInnerWithDefaults() *SurveyResponsePagesInner`

NewSurveyResponsePagesInnerWithDefaults instantiates a new SurveyResponsePagesInner object
This constructor will only assign default values to properties that have it defined,
but it doesn't guarantee that properties required by API are set

### GetId

`func (o *SurveyResponsePagesInner) GetId() string`

GetId returns the Id field if non-nil, zero value otherwise.

### GetIdOk

`func (o *SurveyResponsePagesInner) GetIdOk() (*string, bool)`

GetIdOk returns a tuple with the Id field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetId

`func (o *SurveyResponsePagesInner) SetId(v string)`

SetId sets Id field to given value.

### HasId

`func (o *SurveyResponsePagesInner) HasId() bool`

HasId returns a boolean if a field has been set.

### GetTitle

`func (o *SurveyResponsePagesInner) GetTitle() string`

GetTitle returns the Title field if non-nil, zero value otherwise.

### GetTitleOk

`func (o *SurveyResponsePagesInner) GetTitleOk() (*string, bool)`

GetTitleOk returns a tuple with the Title field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetTitle

`func (o *SurveyResponsePagesInner) SetTitle(v string)`

SetTitle sets Title field to given value.

### HasTitle

`func (o *SurveyResponsePagesInner) HasTitle() bool`

HasTitle returns a boolean if a field has been set.

### GetSortOrder

`func (o *SurveyResponsePagesInner) GetSortOrder() int32`

GetSortOrder returns the SortOrder field if non-nil, zero value otherwise.

### GetSortOrderOk

`func (o *SurveyResponsePagesInner) GetSortOrderOk() (*int32, bool)`

GetSortOrderOk returns a tuple with the SortOrder field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetSortOrder

`func (o *SurveyResponsePagesInner) SetSortOrder(v int32)`

SetSortOrder sets SortOrder field to given value.

### HasSortOrder

`func (o *SurveyResponsePagesInner) HasSortOrder() bool`

HasSortOrder returns a boolean if a field has been set.

### GetQuestions

`func (o *SurveyResponsePagesInner) GetQuestions() []SurveyResponsePagesInnerQuestionsInner`

GetQuestions returns the Questions field if non-nil, zero value otherwise.

### GetQuestionsOk

`func (o *SurveyResponsePagesInner) GetQuestionsOk() (*[]SurveyResponsePagesInnerQuestionsInner, bool)`

GetQuestionsOk returns a tuple with the Questions field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetQuestions

`func (o *SurveyResponsePagesInner) SetQuestions(v []SurveyResponsePagesInnerQuestionsInner)`

SetQuestions sets Questions field to given value.

### HasQuestions

`func (o *SurveyResponsePagesInner) HasQuestions() bool`

HasQuestions returns a boolean if a field has been set.


[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


