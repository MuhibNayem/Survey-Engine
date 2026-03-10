# BulkFeatureResponse

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**Created** | Pointer to **int32** |  | [optional] 
**Updated** | Pointer to **int32** |  | [optional] 
**Failed** | Pointer to **int32** |  | [optional] 
**Errors** | Pointer to [**[]BulkFeatureResponseErrorsInner**](BulkFeatureResponseErrorsInner.md) |  | [optional] 

## Methods

### NewBulkFeatureResponse

`func NewBulkFeatureResponse() *BulkFeatureResponse`

NewBulkFeatureResponse instantiates a new BulkFeatureResponse object
This constructor will assign default values to properties that have it defined,
and makes sure properties required by API are set, but the set of arguments
will change when the set of required properties is changed

### NewBulkFeatureResponseWithDefaults

`func NewBulkFeatureResponseWithDefaults() *BulkFeatureResponse`

NewBulkFeatureResponseWithDefaults instantiates a new BulkFeatureResponse object
This constructor will only assign default values to properties that have it defined,
but it doesn't guarantee that properties required by API are set

### GetCreated

`func (o *BulkFeatureResponse) GetCreated() int32`

GetCreated returns the Created field if non-nil, zero value otherwise.

### GetCreatedOk

`func (o *BulkFeatureResponse) GetCreatedOk() (*int32, bool)`

GetCreatedOk returns a tuple with the Created field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetCreated

`func (o *BulkFeatureResponse) SetCreated(v int32)`

SetCreated sets Created field to given value.

### HasCreated

`func (o *BulkFeatureResponse) HasCreated() bool`

HasCreated returns a boolean if a field has been set.

### GetUpdated

`func (o *BulkFeatureResponse) GetUpdated() int32`

GetUpdated returns the Updated field if non-nil, zero value otherwise.

### GetUpdatedOk

`func (o *BulkFeatureResponse) GetUpdatedOk() (*int32, bool)`

GetUpdatedOk returns a tuple with the Updated field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetUpdated

`func (o *BulkFeatureResponse) SetUpdated(v int32)`

SetUpdated sets Updated field to given value.

### HasUpdated

`func (o *BulkFeatureResponse) HasUpdated() bool`

HasUpdated returns a boolean if a field has been set.

### GetFailed

`func (o *BulkFeatureResponse) GetFailed() int32`

GetFailed returns the Failed field if non-nil, zero value otherwise.

### GetFailedOk

`func (o *BulkFeatureResponse) GetFailedOk() (*int32, bool)`

GetFailedOk returns a tuple with the Failed field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetFailed

`func (o *BulkFeatureResponse) SetFailed(v int32)`

SetFailed sets Failed field to given value.

### HasFailed

`func (o *BulkFeatureResponse) HasFailed() bool`

HasFailed returns a boolean if a field has been set.

### GetErrors

`func (o *BulkFeatureResponse) GetErrors() []BulkFeatureResponseErrorsInner`

GetErrors returns the Errors field if non-nil, zero value otherwise.

### GetErrorsOk

`func (o *BulkFeatureResponse) GetErrorsOk() (*[]BulkFeatureResponseErrorsInner, bool)`

GetErrorsOk returns a tuple with the Errors field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetErrors

`func (o *BulkFeatureResponse) SetErrors(v []BulkFeatureResponseErrorsInner)`

SetErrors sets Errors field to given value.

### HasErrors

`func (o *BulkFeatureResponse) HasErrors() bool`

HasErrors returns a boolean if a field has been set.


[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


