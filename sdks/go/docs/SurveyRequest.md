# SurveyRequest

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**Title** | **string** |  | 
**Description** | Pointer to **string** |  | [optional] 
**Pages** | Pointer to [**[]SurveyPageRequest**](SurveyPageRequest.md) |  | [optional] 

## Methods

### NewSurveyRequest

`func NewSurveyRequest(title string, ) *SurveyRequest`

NewSurveyRequest instantiates a new SurveyRequest object
This constructor will assign default values to properties that have it defined,
and makes sure properties required by API are set, but the set of arguments
will change when the set of required properties is changed

### NewSurveyRequestWithDefaults

`func NewSurveyRequestWithDefaults() *SurveyRequest`

NewSurveyRequestWithDefaults instantiates a new SurveyRequest object
This constructor will only assign default values to properties that have it defined,
but it doesn't guarantee that properties required by API are set

### GetTitle

`func (o *SurveyRequest) GetTitle() string`

GetTitle returns the Title field if non-nil, zero value otherwise.

### GetTitleOk

`func (o *SurveyRequest) GetTitleOk() (*string, bool)`

GetTitleOk returns a tuple with the Title field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetTitle

`func (o *SurveyRequest) SetTitle(v string)`

SetTitle sets Title field to given value.


### GetDescription

`func (o *SurveyRequest) GetDescription() string`

GetDescription returns the Description field if non-nil, zero value otherwise.

### GetDescriptionOk

`func (o *SurveyRequest) GetDescriptionOk() (*string, bool)`

GetDescriptionOk returns a tuple with the Description field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetDescription

`func (o *SurveyRequest) SetDescription(v string)`

SetDescription sets Description field to given value.

### HasDescription

`func (o *SurveyRequest) HasDescription() bool`

HasDescription returns a boolean if a field has been set.

### GetPages

`func (o *SurveyRequest) GetPages() []SurveyPageRequest`

GetPages returns the Pages field if non-nil, zero value otherwise.

### GetPagesOk

`func (o *SurveyRequest) GetPagesOk() (*[]SurveyPageRequest, bool)`

GetPagesOk returns a tuple with the Pages field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetPages

`func (o *SurveyRequest) SetPages(v []SurveyPageRequest)`

SetPages sets Pages field to given value.

### HasPages

`func (o *SurveyRequest) HasPages() bool`

HasPages returns a boolean if a field has been set.


[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


