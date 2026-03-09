# ResponseSubmissionRequest

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**CampaignId** | **string** |  | 
**RespondentIdentifier** | Pointer to **string** |  | [optional] 
**RespondentIp** | Pointer to **string** |  | [optional] 
**RespondentDeviceFingerprint** | Pointer to **string** |  | [optional] 
**ResponderToken** | Pointer to **string** |  | [optional] 
**ResponderAccessCode** | Pointer to **string** |  | [optional] 
**Answers** | Pointer to [**[]ResponseSubmissionRequestAnswersInner**](ResponseSubmissionRequestAnswersInner.md) |  | [optional] 

## Methods

### NewResponseSubmissionRequest

`func NewResponseSubmissionRequest(campaignId string, ) *ResponseSubmissionRequest`

NewResponseSubmissionRequest instantiates a new ResponseSubmissionRequest object
This constructor will assign default values to properties that have it defined,
and makes sure properties required by API are set, but the set of arguments
will change when the set of required properties is changed

### NewResponseSubmissionRequestWithDefaults

`func NewResponseSubmissionRequestWithDefaults() *ResponseSubmissionRequest`

NewResponseSubmissionRequestWithDefaults instantiates a new ResponseSubmissionRequest object
This constructor will only assign default values to properties that have it defined,
but it doesn't guarantee that properties required by API are set

### GetCampaignId

`func (o *ResponseSubmissionRequest) GetCampaignId() string`

GetCampaignId returns the CampaignId field if non-nil, zero value otherwise.

### GetCampaignIdOk

`func (o *ResponseSubmissionRequest) GetCampaignIdOk() (*string, bool)`

GetCampaignIdOk returns a tuple with the CampaignId field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetCampaignId

`func (o *ResponseSubmissionRequest) SetCampaignId(v string)`

SetCampaignId sets CampaignId field to given value.


### GetRespondentIdentifier

`func (o *ResponseSubmissionRequest) GetRespondentIdentifier() string`

GetRespondentIdentifier returns the RespondentIdentifier field if non-nil, zero value otherwise.

### GetRespondentIdentifierOk

`func (o *ResponseSubmissionRequest) GetRespondentIdentifierOk() (*string, bool)`

GetRespondentIdentifierOk returns a tuple with the RespondentIdentifier field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetRespondentIdentifier

`func (o *ResponseSubmissionRequest) SetRespondentIdentifier(v string)`

SetRespondentIdentifier sets RespondentIdentifier field to given value.

### HasRespondentIdentifier

`func (o *ResponseSubmissionRequest) HasRespondentIdentifier() bool`

HasRespondentIdentifier returns a boolean if a field has been set.

### GetRespondentIp

`func (o *ResponseSubmissionRequest) GetRespondentIp() string`

GetRespondentIp returns the RespondentIp field if non-nil, zero value otherwise.

### GetRespondentIpOk

`func (o *ResponseSubmissionRequest) GetRespondentIpOk() (*string, bool)`

GetRespondentIpOk returns a tuple with the RespondentIp field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetRespondentIp

`func (o *ResponseSubmissionRequest) SetRespondentIp(v string)`

SetRespondentIp sets RespondentIp field to given value.

### HasRespondentIp

`func (o *ResponseSubmissionRequest) HasRespondentIp() bool`

HasRespondentIp returns a boolean if a field has been set.

### GetRespondentDeviceFingerprint

`func (o *ResponseSubmissionRequest) GetRespondentDeviceFingerprint() string`

GetRespondentDeviceFingerprint returns the RespondentDeviceFingerprint field if non-nil, zero value otherwise.

### GetRespondentDeviceFingerprintOk

`func (o *ResponseSubmissionRequest) GetRespondentDeviceFingerprintOk() (*string, bool)`

GetRespondentDeviceFingerprintOk returns a tuple with the RespondentDeviceFingerprint field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetRespondentDeviceFingerprint

`func (o *ResponseSubmissionRequest) SetRespondentDeviceFingerprint(v string)`

SetRespondentDeviceFingerprint sets RespondentDeviceFingerprint field to given value.

### HasRespondentDeviceFingerprint

`func (o *ResponseSubmissionRequest) HasRespondentDeviceFingerprint() bool`

HasRespondentDeviceFingerprint returns a boolean if a field has been set.

### GetResponderToken

`func (o *ResponseSubmissionRequest) GetResponderToken() string`

GetResponderToken returns the ResponderToken field if non-nil, zero value otherwise.

### GetResponderTokenOk

`func (o *ResponseSubmissionRequest) GetResponderTokenOk() (*string, bool)`

GetResponderTokenOk returns a tuple with the ResponderToken field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetResponderToken

`func (o *ResponseSubmissionRequest) SetResponderToken(v string)`

SetResponderToken sets ResponderToken field to given value.

### HasResponderToken

`func (o *ResponseSubmissionRequest) HasResponderToken() bool`

HasResponderToken returns a boolean if a field has been set.

### GetResponderAccessCode

`func (o *ResponseSubmissionRequest) GetResponderAccessCode() string`

GetResponderAccessCode returns the ResponderAccessCode field if non-nil, zero value otherwise.

### GetResponderAccessCodeOk

`func (o *ResponseSubmissionRequest) GetResponderAccessCodeOk() (*string, bool)`

GetResponderAccessCodeOk returns a tuple with the ResponderAccessCode field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetResponderAccessCode

`func (o *ResponseSubmissionRequest) SetResponderAccessCode(v string)`

SetResponderAccessCode sets ResponderAccessCode field to given value.

### HasResponderAccessCode

`func (o *ResponseSubmissionRequest) HasResponderAccessCode() bool`

HasResponderAccessCode returns a boolean if a field has been set.

### GetAnswers

`func (o *ResponseSubmissionRequest) GetAnswers() []ResponseSubmissionRequestAnswersInner`

GetAnswers returns the Answers field if non-nil, zero value otherwise.

### GetAnswersOk

`func (o *ResponseSubmissionRequest) GetAnswersOk() (*[]ResponseSubmissionRequestAnswersInner, bool)`

GetAnswersOk returns a tuple with the Answers field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetAnswers

`func (o *ResponseSubmissionRequest) SetAnswers(v []ResponseSubmissionRequestAnswersInner)`

SetAnswers sets Answers field to given value.

### HasAnswers

`func (o *ResponseSubmissionRequest) HasAnswers() bool`

HasAnswers returns a boolean if a field has been set.


[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


