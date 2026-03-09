# TokenValidationResult

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**Valid** | Pointer to **bool** |  | [optional] 
**RespondentId** | Pointer to **string** |  | [optional] 
**Email** | Pointer to **string** |  | [optional] 
**MappedClaims** | Pointer to **map[string]string** |  | [optional] 
**ErrorCode** | Pointer to **string** |  | [optional] 
**ErrorMessage** | Pointer to **string** |  | [optional] 

## Methods

### NewTokenValidationResult

`func NewTokenValidationResult() *TokenValidationResult`

NewTokenValidationResult instantiates a new TokenValidationResult object
This constructor will assign default values to properties that have it defined,
and makes sure properties required by API are set, but the set of arguments
will change when the set of required properties is changed

### NewTokenValidationResultWithDefaults

`func NewTokenValidationResultWithDefaults() *TokenValidationResult`

NewTokenValidationResultWithDefaults instantiates a new TokenValidationResult object
This constructor will only assign default values to properties that have it defined,
but it doesn't guarantee that properties required by API are set

### GetValid

`func (o *TokenValidationResult) GetValid() bool`

GetValid returns the Valid field if non-nil, zero value otherwise.

### GetValidOk

`func (o *TokenValidationResult) GetValidOk() (*bool, bool)`

GetValidOk returns a tuple with the Valid field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetValid

`func (o *TokenValidationResult) SetValid(v bool)`

SetValid sets Valid field to given value.

### HasValid

`func (o *TokenValidationResult) HasValid() bool`

HasValid returns a boolean if a field has been set.

### GetRespondentId

`func (o *TokenValidationResult) GetRespondentId() string`

GetRespondentId returns the RespondentId field if non-nil, zero value otherwise.

### GetRespondentIdOk

`func (o *TokenValidationResult) GetRespondentIdOk() (*string, bool)`

GetRespondentIdOk returns a tuple with the RespondentId field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetRespondentId

`func (o *TokenValidationResult) SetRespondentId(v string)`

SetRespondentId sets RespondentId field to given value.

### HasRespondentId

`func (o *TokenValidationResult) HasRespondentId() bool`

HasRespondentId returns a boolean if a field has been set.

### GetEmail

`func (o *TokenValidationResult) GetEmail() string`

GetEmail returns the Email field if non-nil, zero value otherwise.

### GetEmailOk

`func (o *TokenValidationResult) GetEmailOk() (*string, bool)`

GetEmailOk returns a tuple with the Email field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetEmail

`func (o *TokenValidationResult) SetEmail(v string)`

SetEmail sets Email field to given value.

### HasEmail

`func (o *TokenValidationResult) HasEmail() bool`

HasEmail returns a boolean if a field has been set.

### GetMappedClaims

`func (o *TokenValidationResult) GetMappedClaims() map[string]string`

GetMappedClaims returns the MappedClaims field if non-nil, zero value otherwise.

### GetMappedClaimsOk

`func (o *TokenValidationResult) GetMappedClaimsOk() (*map[string]string, bool)`

GetMappedClaimsOk returns a tuple with the MappedClaims field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetMappedClaims

`func (o *TokenValidationResult) SetMappedClaims(v map[string]string)`

SetMappedClaims sets MappedClaims field to given value.

### HasMappedClaims

`func (o *TokenValidationResult) HasMappedClaims() bool`

HasMappedClaims returns a boolean if a field has been set.

### GetErrorCode

`func (o *TokenValidationResult) GetErrorCode() string`

GetErrorCode returns the ErrorCode field if non-nil, zero value otherwise.

### GetErrorCodeOk

`func (o *TokenValidationResult) GetErrorCodeOk() (*string, bool)`

GetErrorCodeOk returns a tuple with the ErrorCode field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetErrorCode

`func (o *TokenValidationResult) SetErrorCode(v string)`

SetErrorCode sets ErrorCode field to given value.

### HasErrorCode

`func (o *TokenValidationResult) HasErrorCode() bool`

HasErrorCode returns a boolean if a field has been set.

### GetErrorMessage

`func (o *TokenValidationResult) GetErrorMessage() string`

GetErrorMessage returns the ErrorMessage field if non-nil, zero value otherwise.

### GetErrorMessageOk

`func (o *TokenValidationResult) GetErrorMessageOk() (*string, bool)`

GetErrorMessageOk returns a tuple with the ErrorMessage field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetErrorMessage

`func (o *TokenValidationResult) SetErrorMessage(v string)`

SetErrorMessage sets ErrorMessage field to given value.

### HasErrorMessage

`func (o *TokenValidationResult) HasErrorMessage() bool`

HasErrorMessage returns a boolean if a field has been set.


[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


