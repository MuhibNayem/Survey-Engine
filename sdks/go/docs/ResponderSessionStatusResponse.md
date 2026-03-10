# ResponderSessionStatusResponse

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**Authenticated** | Pointer to **bool** |  | [optional] 
**Email** | Pointer to **string** |  | [optional] 

## Methods

### NewResponderSessionStatusResponse

`func NewResponderSessionStatusResponse() *ResponderSessionStatusResponse`

NewResponderSessionStatusResponse instantiates a new ResponderSessionStatusResponse object
This constructor will assign default values to properties that have it defined,
and makes sure properties required by API are set, but the set of arguments
will change when the set of required properties is changed

### NewResponderSessionStatusResponseWithDefaults

`func NewResponderSessionStatusResponseWithDefaults() *ResponderSessionStatusResponse`

NewResponderSessionStatusResponseWithDefaults instantiates a new ResponderSessionStatusResponse object
This constructor will only assign default values to properties that have it defined,
but it doesn't guarantee that properties required by API are set

### GetAuthenticated

`func (o *ResponderSessionStatusResponse) GetAuthenticated() bool`

GetAuthenticated returns the Authenticated field if non-nil, zero value otherwise.

### GetAuthenticatedOk

`func (o *ResponderSessionStatusResponse) GetAuthenticatedOk() (*bool, bool)`

GetAuthenticatedOk returns a tuple with the Authenticated field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetAuthenticated

`func (o *ResponderSessionStatusResponse) SetAuthenticated(v bool)`

SetAuthenticated sets Authenticated field to given value.

### HasAuthenticated

`func (o *ResponderSessionStatusResponse) HasAuthenticated() bool`

HasAuthenticated returns a boolean if a field has been set.

### GetEmail

`func (o *ResponderSessionStatusResponse) GetEmail() string`

GetEmail returns the Email field if non-nil, zero value otherwise.

### GetEmailOk

`func (o *ResponderSessionStatusResponse) GetEmailOk() (*string, bool)`

GetEmailOk returns a tuple with the Email field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetEmail

`func (o *ResponderSessionStatusResponse) SetEmail(v string)`

SetEmail sets Email field to given value.

### HasEmail

`func (o *ResponderSessionStatusResponse) HasEmail() bool`

HasEmail returns a boolean if a field has been set.


[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


