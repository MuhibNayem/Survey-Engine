# SurveyPageRequest

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**Title** | Pointer to **string** |  | [optional] 
**SortOrder** | **int32** |  | 
**Questions** | Pointer to [**[]SurveyQuestionRequest**](SurveyQuestionRequest.md) |  | [optional] 

## Methods

### NewSurveyPageRequest

`func NewSurveyPageRequest(sortOrder int32, ) *SurveyPageRequest`

NewSurveyPageRequest instantiates a new SurveyPageRequest object
This constructor will assign default values to properties that have it defined,
and makes sure properties required by API are set, but the set of arguments
will change when the set of required properties is changed

### NewSurveyPageRequestWithDefaults

`func NewSurveyPageRequestWithDefaults() *SurveyPageRequest`

NewSurveyPageRequestWithDefaults instantiates a new SurveyPageRequest object
This constructor will only assign default values to properties that have it defined,
but it doesn't guarantee that properties required by API are set

### GetTitle

`func (o *SurveyPageRequest) GetTitle() string`

GetTitle returns the Title field if non-nil, zero value otherwise.

### GetTitleOk

`func (o *SurveyPageRequest) GetTitleOk() (*string, bool)`

GetTitleOk returns a tuple with the Title field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetTitle

`func (o *SurveyPageRequest) SetTitle(v string)`

SetTitle sets Title field to given value.

### HasTitle

`func (o *SurveyPageRequest) HasTitle() bool`

HasTitle returns a boolean if a field has been set.

### GetSortOrder

`func (o *SurveyPageRequest) GetSortOrder() int32`

GetSortOrder returns the SortOrder field if non-nil, zero value otherwise.

### GetSortOrderOk

`func (o *SurveyPageRequest) GetSortOrderOk() (*int32, bool)`

GetSortOrderOk returns a tuple with the SortOrder field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetSortOrder

`func (o *SurveyPageRequest) SetSortOrder(v int32)`

SetSortOrder sets SortOrder field to given value.


### GetQuestions

`func (o *SurveyPageRequest) GetQuestions() []SurveyQuestionRequest`

GetQuestions returns the Questions field if non-nil, zero value otherwise.

### GetQuestionsOk

`func (o *SurveyPageRequest) GetQuestionsOk() (*[]SurveyQuestionRequest, bool)`

GetQuestionsOk returns a tuple with the Questions field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetQuestions

`func (o *SurveyPageRequest) SetQuestions(v []SurveyQuestionRequest)`

SetQuestions sets Questions field to given value.

### HasQuestions

`func (o *SurveyPageRequest) HasQuestions() bool`

HasQuestions returns a boolean if a field has been set.


[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


