# ClaimMappingRequest

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**ExternalClaim** | Pointer to **string** |  | [optional] 
**InternalField** | Pointer to **string** |  | [optional] 
**Required** | Pointer to **bool** |  | [optional] 

## Methods

### NewClaimMappingRequest

`func NewClaimMappingRequest() *ClaimMappingRequest`

NewClaimMappingRequest instantiates a new ClaimMappingRequest object
This constructor will assign default values to properties that have it defined,
and makes sure properties required by API are set, but the set of arguments
will change when the set of required properties is changed

### NewClaimMappingRequestWithDefaults

`func NewClaimMappingRequestWithDefaults() *ClaimMappingRequest`

NewClaimMappingRequestWithDefaults instantiates a new ClaimMappingRequest object
This constructor will only assign default values to properties that have it defined,
but it doesn't guarantee that properties required by API are set

### GetExternalClaim

`func (o *ClaimMappingRequest) GetExternalClaim() string`

GetExternalClaim returns the ExternalClaim field if non-nil, zero value otherwise.

### GetExternalClaimOk

`func (o *ClaimMappingRequest) GetExternalClaimOk() (*string, bool)`

GetExternalClaimOk returns a tuple with the ExternalClaim field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetExternalClaim

`func (o *ClaimMappingRequest) SetExternalClaim(v string)`

SetExternalClaim sets ExternalClaim field to given value.

### HasExternalClaim

`func (o *ClaimMappingRequest) HasExternalClaim() bool`

HasExternalClaim returns a boolean if a field has been set.

### GetInternalField

`func (o *ClaimMappingRequest) GetInternalField() string`

GetInternalField returns the InternalField field if non-nil, zero value otherwise.

### GetInternalFieldOk

`func (o *ClaimMappingRequest) GetInternalFieldOk() (*string, bool)`

GetInternalFieldOk returns a tuple with the InternalField field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetInternalField

`func (o *ClaimMappingRequest) SetInternalField(v string)`

SetInternalField sets InternalField field to given value.

### HasInternalField

`func (o *ClaimMappingRequest) HasInternalField() bool`

HasInternalField returns a boolean if a field has been set.

### GetRequired

`func (o *ClaimMappingRequest) GetRequired() bool`

GetRequired returns the Required field if non-nil, zero value otherwise.

### GetRequiredOk

`func (o *ClaimMappingRequest) GetRequiredOk() (*bool, bool)`

GetRequiredOk returns a tuple with the Required field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetRequired

`func (o *ClaimMappingRequest) SetRequired(v bool)`

SetRequired sets Required field to given value.

### HasRequired

`func (o *ClaimMappingRequest) HasRequired() bool`

HasRequired returns a boolean if a field has been set.


[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


