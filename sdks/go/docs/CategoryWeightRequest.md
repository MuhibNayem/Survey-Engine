# CategoryWeightRequest

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**CategoryId** | **string** |  | 
**WeightPercentage** | **float32** |  | 

## Methods

### NewCategoryWeightRequest

`func NewCategoryWeightRequest(categoryId string, weightPercentage float32, ) *CategoryWeightRequest`

NewCategoryWeightRequest instantiates a new CategoryWeightRequest object
This constructor will assign default values to properties that have it defined,
and makes sure properties required by API are set, but the set of arguments
will change when the set of required properties is changed

### NewCategoryWeightRequestWithDefaults

`func NewCategoryWeightRequestWithDefaults() *CategoryWeightRequest`

NewCategoryWeightRequestWithDefaults instantiates a new CategoryWeightRequest object
This constructor will only assign default values to properties that have it defined,
but it doesn't guarantee that properties required by API are set

### GetCategoryId

`func (o *CategoryWeightRequest) GetCategoryId() string`

GetCategoryId returns the CategoryId field if non-nil, zero value otherwise.

### GetCategoryIdOk

`func (o *CategoryWeightRequest) GetCategoryIdOk() (*string, bool)`

GetCategoryIdOk returns a tuple with the CategoryId field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetCategoryId

`func (o *CategoryWeightRequest) SetCategoryId(v string)`

SetCategoryId sets CategoryId field to given value.


### GetWeightPercentage

`func (o *CategoryWeightRequest) GetWeightPercentage() float32`

GetWeightPercentage returns the WeightPercentage field if non-nil, zero value otherwise.

### GetWeightPercentageOk

`func (o *CategoryWeightRequest) GetWeightPercentageOk() (*float32, bool)`

GetWeightPercentageOk returns a tuple with the WeightPercentage field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetWeightPercentage

`func (o *CategoryWeightRequest) SetWeightPercentage(v float32)`

SetWeightPercentage sets WeightPercentage field to given value.



[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


