# CategoryRequest

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**Name** | **string** |  | 
**Description** | Pointer to **string** |  | [optional] 
**QuestionMappings** | Pointer to [**[]CategoryQuestionMappingRequest**](CategoryQuestionMappingRequest.md) |  | [optional] 

## Methods

### NewCategoryRequest

`func NewCategoryRequest(name string, ) *CategoryRequest`

NewCategoryRequest instantiates a new CategoryRequest object
This constructor will assign default values to properties that have it defined,
and makes sure properties required by API are set, but the set of arguments
will change when the set of required properties is changed

### NewCategoryRequestWithDefaults

`func NewCategoryRequestWithDefaults() *CategoryRequest`

NewCategoryRequestWithDefaults instantiates a new CategoryRequest object
This constructor will only assign default values to properties that have it defined,
but it doesn't guarantee that properties required by API are set

### GetName

`func (o *CategoryRequest) GetName() string`

GetName returns the Name field if non-nil, zero value otherwise.

### GetNameOk

`func (o *CategoryRequest) GetNameOk() (*string, bool)`

GetNameOk returns a tuple with the Name field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetName

`func (o *CategoryRequest) SetName(v string)`

SetName sets Name field to given value.


### GetDescription

`func (o *CategoryRequest) GetDescription() string`

GetDescription returns the Description field if non-nil, zero value otherwise.

### GetDescriptionOk

`func (o *CategoryRequest) GetDescriptionOk() (*string, bool)`

GetDescriptionOk returns a tuple with the Description field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetDescription

`func (o *CategoryRequest) SetDescription(v string)`

SetDescription sets Description field to given value.

### HasDescription

`func (o *CategoryRequest) HasDescription() bool`

HasDescription returns a boolean if a field has been set.

### GetQuestionMappings

`func (o *CategoryRequest) GetQuestionMappings() []CategoryQuestionMappingRequest`

GetQuestionMappings returns the QuestionMappings field if non-nil, zero value otherwise.

### GetQuestionMappingsOk

`func (o *CategoryRequest) GetQuestionMappingsOk() (*[]CategoryQuestionMappingRequest, bool)`

GetQuestionMappingsOk returns a tuple with the QuestionMappings field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetQuestionMappings

`func (o *CategoryRequest) SetQuestionMappings(v []CategoryQuestionMappingRequest)`

SetQuestionMappings sets QuestionMappings field to given value.

### HasQuestionMappings

`func (o *CategoryRequest) HasQuestionMappings() bool`

HasQuestionMappings returns a boolean if a field has been set.


[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


