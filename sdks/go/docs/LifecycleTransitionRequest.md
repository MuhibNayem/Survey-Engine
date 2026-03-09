# LifecycleTransitionRequest

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**TargetState** | **string** |  | 
**Reason** | Pointer to **string** |  | [optional] 

## Methods

### NewLifecycleTransitionRequest

`func NewLifecycleTransitionRequest(targetState string, ) *LifecycleTransitionRequest`

NewLifecycleTransitionRequest instantiates a new LifecycleTransitionRequest object
This constructor will assign default values to properties that have it defined,
and makes sure properties required by API are set, but the set of arguments
will change when the set of required properties is changed

### NewLifecycleTransitionRequestWithDefaults

`func NewLifecycleTransitionRequestWithDefaults() *LifecycleTransitionRequest`

NewLifecycleTransitionRequestWithDefaults instantiates a new LifecycleTransitionRequest object
This constructor will only assign default values to properties that have it defined,
but it doesn't guarantee that properties required by API are set

### GetTargetState

`func (o *LifecycleTransitionRequest) GetTargetState() string`

GetTargetState returns the TargetState field if non-nil, zero value otherwise.

### GetTargetStateOk

`func (o *LifecycleTransitionRequest) GetTargetStateOk() (*string, bool)`

GetTargetStateOk returns a tuple with the TargetState field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetTargetState

`func (o *LifecycleTransitionRequest) SetTargetState(v string)`

SetTargetState sets TargetState field to given value.


### GetReason

`func (o *LifecycleTransitionRequest) GetReason() string`

GetReason returns the Reason field if non-nil, zero value otherwise.

### GetReasonOk

`func (o *LifecycleTransitionRequest) GetReasonOk() (*string, bool)`

GetReasonOk returns a tuple with the Reason field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetReason

`func (o *LifecycleTransitionRequest) SetReason(v string)`

SetReason sets Reason field to given value.

### HasReason

`func (o *LifecycleTransitionRequest) HasReason() bool`

HasReason returns a boolean if a field has been set.


[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


