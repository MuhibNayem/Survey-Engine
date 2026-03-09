# CategoryResponse

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**Id** | Pointer to **string** |  | [optional] 
**Name** | Pointer to **string** |  | [optional] 
**Description** | Pointer to **string** |  | [optional] 
**CurrentVersion** | Pointer to **int32** |  | [optional] 
**Active** | Pointer to **bool** |  | [optional] 
**QuestionMappings** | Pointer to [**[]CategoryResponseQuestionMappingsInner**](CategoryResponseQuestionMappingsInner.md) |  | [optional] 
**CreatedBy** | Pointer to **string** |  | [optional] 
**CreatedAt** | Pointer to **time.Time** |  | [optional] 
**UpdatedBy** | Pointer to **string** |  | [optional] 
**UpdatedAt** | Pointer to **time.Time** |  | [optional] 

## Methods

### NewCategoryResponse

`func NewCategoryResponse() *CategoryResponse`

NewCategoryResponse instantiates a new CategoryResponse object
This constructor will assign default values to properties that have it defined,
and makes sure properties required by API are set, but the set of arguments
will change when the set of required properties is changed

### NewCategoryResponseWithDefaults

`func NewCategoryResponseWithDefaults() *CategoryResponse`

NewCategoryResponseWithDefaults instantiates a new CategoryResponse object
This constructor will only assign default values to properties that have it defined,
but it doesn't guarantee that properties required by API are set

### GetId

`func (o *CategoryResponse) GetId() string`

GetId returns the Id field if non-nil, zero value otherwise.

### GetIdOk

`func (o *CategoryResponse) GetIdOk() (*string, bool)`

GetIdOk returns a tuple with the Id field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetId

`func (o *CategoryResponse) SetId(v string)`

SetId sets Id field to given value.

### HasId

`func (o *CategoryResponse) HasId() bool`

HasId returns a boolean if a field has been set.

### GetName

`func (o *CategoryResponse) GetName() string`

GetName returns the Name field if non-nil, zero value otherwise.

### GetNameOk

`func (o *CategoryResponse) GetNameOk() (*string, bool)`

GetNameOk returns a tuple with the Name field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetName

`func (o *CategoryResponse) SetName(v string)`

SetName sets Name field to given value.

### HasName

`func (o *CategoryResponse) HasName() bool`

HasName returns a boolean if a field has been set.

### GetDescription

`func (o *CategoryResponse) GetDescription() string`

GetDescription returns the Description field if non-nil, zero value otherwise.

### GetDescriptionOk

`func (o *CategoryResponse) GetDescriptionOk() (*string, bool)`

GetDescriptionOk returns a tuple with the Description field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetDescription

`func (o *CategoryResponse) SetDescription(v string)`

SetDescription sets Description field to given value.

### HasDescription

`func (o *CategoryResponse) HasDescription() bool`

HasDescription returns a boolean if a field has been set.

### GetCurrentVersion

`func (o *CategoryResponse) GetCurrentVersion() int32`

GetCurrentVersion returns the CurrentVersion field if non-nil, zero value otherwise.

### GetCurrentVersionOk

`func (o *CategoryResponse) GetCurrentVersionOk() (*int32, bool)`

GetCurrentVersionOk returns a tuple with the CurrentVersion field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetCurrentVersion

`func (o *CategoryResponse) SetCurrentVersion(v int32)`

SetCurrentVersion sets CurrentVersion field to given value.

### HasCurrentVersion

`func (o *CategoryResponse) HasCurrentVersion() bool`

HasCurrentVersion returns a boolean if a field has been set.

### GetActive

`func (o *CategoryResponse) GetActive() bool`

GetActive returns the Active field if non-nil, zero value otherwise.

### GetActiveOk

`func (o *CategoryResponse) GetActiveOk() (*bool, bool)`

GetActiveOk returns a tuple with the Active field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetActive

`func (o *CategoryResponse) SetActive(v bool)`

SetActive sets Active field to given value.

### HasActive

`func (o *CategoryResponse) HasActive() bool`

HasActive returns a boolean if a field has been set.

### GetQuestionMappings

`func (o *CategoryResponse) GetQuestionMappings() []CategoryResponseQuestionMappingsInner`

GetQuestionMappings returns the QuestionMappings field if non-nil, zero value otherwise.

### GetQuestionMappingsOk

`func (o *CategoryResponse) GetQuestionMappingsOk() (*[]CategoryResponseQuestionMappingsInner, bool)`

GetQuestionMappingsOk returns a tuple with the QuestionMappings field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetQuestionMappings

`func (o *CategoryResponse) SetQuestionMappings(v []CategoryResponseQuestionMappingsInner)`

SetQuestionMappings sets QuestionMappings field to given value.

### HasQuestionMappings

`func (o *CategoryResponse) HasQuestionMappings() bool`

HasQuestionMappings returns a boolean if a field has been set.

### GetCreatedBy

`func (o *CategoryResponse) GetCreatedBy() string`

GetCreatedBy returns the CreatedBy field if non-nil, zero value otherwise.

### GetCreatedByOk

`func (o *CategoryResponse) GetCreatedByOk() (*string, bool)`

GetCreatedByOk returns a tuple with the CreatedBy field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetCreatedBy

`func (o *CategoryResponse) SetCreatedBy(v string)`

SetCreatedBy sets CreatedBy field to given value.

### HasCreatedBy

`func (o *CategoryResponse) HasCreatedBy() bool`

HasCreatedBy returns a boolean if a field has been set.

### GetCreatedAt

`func (o *CategoryResponse) GetCreatedAt() time.Time`

GetCreatedAt returns the CreatedAt field if non-nil, zero value otherwise.

### GetCreatedAtOk

`func (o *CategoryResponse) GetCreatedAtOk() (*time.Time, bool)`

GetCreatedAtOk returns a tuple with the CreatedAt field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetCreatedAt

`func (o *CategoryResponse) SetCreatedAt(v time.Time)`

SetCreatedAt sets CreatedAt field to given value.

### HasCreatedAt

`func (o *CategoryResponse) HasCreatedAt() bool`

HasCreatedAt returns a boolean if a field has been set.

### GetUpdatedBy

`func (o *CategoryResponse) GetUpdatedBy() string`

GetUpdatedBy returns the UpdatedBy field if non-nil, zero value otherwise.

### GetUpdatedByOk

`func (o *CategoryResponse) GetUpdatedByOk() (*string, bool)`

GetUpdatedByOk returns a tuple with the UpdatedBy field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetUpdatedBy

`func (o *CategoryResponse) SetUpdatedBy(v string)`

SetUpdatedBy sets UpdatedBy field to given value.

### HasUpdatedBy

`func (o *CategoryResponse) HasUpdatedBy() bool`

HasUpdatedBy returns a boolean if a field has been set.

### GetUpdatedAt

`func (o *CategoryResponse) GetUpdatedAt() time.Time`

GetUpdatedAt returns the UpdatedAt field if non-nil, zero value otherwise.

### GetUpdatedAtOk

`func (o *CategoryResponse) GetUpdatedAtOk() (*time.Time, bool)`

GetUpdatedAtOk returns a tuple with the UpdatedAt field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetUpdatedAt

`func (o *CategoryResponse) SetUpdatedAt(v time.Time)`

SetUpdatedAt sets UpdatedAt field to given value.

### HasUpdatedAt

`func (o *CategoryResponse) HasUpdatedAt() bool`

HasUpdatedAt returns a boolean if a field has been set.


[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


