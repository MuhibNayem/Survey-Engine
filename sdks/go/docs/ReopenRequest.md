# ReopenRequest

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**Reason** | **string** |  | 
**ReopenWindowMinutes** | Pointer to **int32** |  | [optional] 

## Methods

### NewReopenRequest

`func NewReopenRequest(reason string, ) *ReopenRequest`

NewReopenRequest instantiates a new ReopenRequest object
This constructor will assign default values to properties that have it defined,
and makes sure properties required by API are set, but the set of arguments
will change when the set of required properties is changed

### NewReopenRequestWithDefaults

`func NewReopenRequestWithDefaults() *ReopenRequest`

NewReopenRequestWithDefaults instantiates a new ReopenRequest object
This constructor will only assign default values to properties that have it defined,
but it doesn't guarantee that properties required by API are set

### GetReason

`func (o *ReopenRequest) GetReason() string`

GetReason returns the Reason field if non-nil, zero value otherwise.

### GetReasonOk

`func (o *ReopenRequest) GetReasonOk() (*string, bool)`

GetReasonOk returns a tuple with the Reason field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetReason

`func (o *ReopenRequest) SetReason(v string)`

SetReason sets Reason field to given value.


### GetReopenWindowMinutes

`func (o *ReopenRequest) GetReopenWindowMinutes() int32`

GetReopenWindowMinutes returns the ReopenWindowMinutes field if non-nil, zero value otherwise.

### GetReopenWindowMinutesOk

`func (o *ReopenRequest) GetReopenWindowMinutesOk() (*int32, bool)`

GetReopenWindowMinutesOk returns a tuple with the ReopenWindowMinutes field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetReopenWindowMinutes

`func (o *ReopenRequest) SetReopenWindowMinutes(v int32)`

SetReopenWindowMinutes sets ReopenWindowMinutes field to given value.

### HasReopenWindowMinutes

`func (o *ReopenRequest) HasReopenWindowMinutes() bool`

HasReopenWindowMinutes returns a boolean if a field has been set.


[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


