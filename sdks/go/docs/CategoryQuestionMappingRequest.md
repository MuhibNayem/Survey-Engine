# CategoryQuestionMappingRequest

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**QuestionId** | **string** |  | 
**SortOrder** | **int32** |  | 
**Weight** | Pointer to **float32** |  | [optional] 

## Methods

### NewCategoryQuestionMappingRequest

`func NewCategoryQuestionMappingRequest(questionId string, sortOrder int32, ) *CategoryQuestionMappingRequest`

NewCategoryQuestionMappingRequest instantiates a new CategoryQuestionMappingRequest object
This constructor will assign default values to properties that have it defined,
and makes sure properties required by API are set, but the set of arguments
will change when the set of required properties is changed

### NewCategoryQuestionMappingRequestWithDefaults

`func NewCategoryQuestionMappingRequestWithDefaults() *CategoryQuestionMappingRequest`

NewCategoryQuestionMappingRequestWithDefaults instantiates a new CategoryQuestionMappingRequest object
This constructor will only assign default values to properties that have it defined,
but it doesn't guarantee that properties required by API are set

### GetQuestionId

`func (o *CategoryQuestionMappingRequest) GetQuestionId() string`

GetQuestionId returns the QuestionId field if non-nil, zero value otherwise.

### GetQuestionIdOk

`func (o *CategoryQuestionMappingRequest) GetQuestionIdOk() (*string, bool)`

GetQuestionIdOk returns a tuple with the QuestionId field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetQuestionId

`func (o *CategoryQuestionMappingRequest) SetQuestionId(v string)`

SetQuestionId sets QuestionId field to given value.


### GetSortOrder

`func (o *CategoryQuestionMappingRequest) GetSortOrder() int32`

GetSortOrder returns the SortOrder field if non-nil, zero value otherwise.

### GetSortOrderOk

`func (o *CategoryQuestionMappingRequest) GetSortOrderOk() (*int32, bool)`

GetSortOrderOk returns a tuple with the SortOrder field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetSortOrder

`func (o *CategoryQuestionMappingRequest) SetSortOrder(v int32)`

SetSortOrder sets SortOrder field to given value.


### GetWeight

`func (o *CategoryQuestionMappingRequest) GetWeight() float32`

GetWeight returns the Weight field if non-nil, zero value otherwise.

### GetWeightOk

`func (o *CategoryQuestionMappingRequest) GetWeightOk() (*float32, bool)`

GetWeightOk returns a tuple with the Weight field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetWeight

`func (o *CategoryQuestionMappingRequest) SetWeight(v float32)`

SetWeight sets Weight field to given value.

### HasWeight

`func (o *CategoryQuestionMappingRequest) HasWeight() bool`

HasWeight returns a boolean if a field has been set.


[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


