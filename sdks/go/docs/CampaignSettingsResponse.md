# CampaignSettingsResponse

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**CampaignId** | Pointer to **string** |  | [optional] 
**Password** | Pointer to **string** |  | [optional] 
**CaptchaEnabled** | Pointer to **bool** |  | [optional] 
**OneResponsePerDevice** | Pointer to **bool** |  | [optional] 
**IpRestrictionEnabled** | Pointer to **bool** |  | [optional] 
**EmailRestrictionEnabled** | Pointer to **bool** |  | [optional] 
**ResponseQuota** | Pointer to **int32** |  | [optional] 
**CloseDate** | Pointer to **time.Time** |  | [optional] 
**SessionTimeoutMinutes** | Pointer to **int32** |  | [optional] 
**ShowQuestionNumbers** | Pointer to **bool** |  | [optional] 
**ShowProgressIndicator** | Pointer to **bool** |  | [optional] 
**AllowBackButton** | Pointer to **bool** |  | [optional] 
**StartMessage** | Pointer to **string** |  | [optional] 
**FinishMessage** | Pointer to **string** |  | [optional] 
**HeaderHtml** | Pointer to **string** |  | [optional] 
**FooterHtml** | Pointer to **string** |  | [optional] 
**CollectName** | Pointer to **bool** |  | [optional] 
**CollectEmail** | Pointer to **bool** |  | [optional] 
**CollectPhone** | Pointer to **bool** |  | [optional] 
**CollectAddress** | Pointer to **bool** |  | [optional] 

## Methods

### NewCampaignSettingsResponse

`func NewCampaignSettingsResponse() *CampaignSettingsResponse`

NewCampaignSettingsResponse instantiates a new CampaignSettingsResponse object
This constructor will assign default values to properties that have it defined,
and makes sure properties required by API are set, but the set of arguments
will change when the set of required properties is changed

### NewCampaignSettingsResponseWithDefaults

`func NewCampaignSettingsResponseWithDefaults() *CampaignSettingsResponse`

NewCampaignSettingsResponseWithDefaults instantiates a new CampaignSettingsResponse object
This constructor will only assign default values to properties that have it defined,
but it doesn't guarantee that properties required by API are set

### GetCampaignId

`func (o *CampaignSettingsResponse) GetCampaignId() string`

GetCampaignId returns the CampaignId field if non-nil, zero value otherwise.

### GetCampaignIdOk

`func (o *CampaignSettingsResponse) GetCampaignIdOk() (*string, bool)`

GetCampaignIdOk returns a tuple with the CampaignId field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetCampaignId

`func (o *CampaignSettingsResponse) SetCampaignId(v string)`

SetCampaignId sets CampaignId field to given value.

### HasCampaignId

`func (o *CampaignSettingsResponse) HasCampaignId() bool`

HasCampaignId returns a boolean if a field has been set.

### GetPassword

`func (o *CampaignSettingsResponse) GetPassword() string`

GetPassword returns the Password field if non-nil, zero value otherwise.

### GetPasswordOk

`func (o *CampaignSettingsResponse) GetPasswordOk() (*string, bool)`

GetPasswordOk returns a tuple with the Password field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetPassword

`func (o *CampaignSettingsResponse) SetPassword(v string)`

SetPassword sets Password field to given value.

### HasPassword

`func (o *CampaignSettingsResponse) HasPassword() bool`

HasPassword returns a boolean if a field has been set.

### GetCaptchaEnabled

`func (o *CampaignSettingsResponse) GetCaptchaEnabled() bool`

GetCaptchaEnabled returns the CaptchaEnabled field if non-nil, zero value otherwise.

### GetCaptchaEnabledOk

`func (o *CampaignSettingsResponse) GetCaptchaEnabledOk() (*bool, bool)`

GetCaptchaEnabledOk returns a tuple with the CaptchaEnabled field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetCaptchaEnabled

`func (o *CampaignSettingsResponse) SetCaptchaEnabled(v bool)`

SetCaptchaEnabled sets CaptchaEnabled field to given value.

### HasCaptchaEnabled

`func (o *CampaignSettingsResponse) HasCaptchaEnabled() bool`

HasCaptchaEnabled returns a boolean if a field has been set.

### GetOneResponsePerDevice

`func (o *CampaignSettingsResponse) GetOneResponsePerDevice() bool`

GetOneResponsePerDevice returns the OneResponsePerDevice field if non-nil, zero value otherwise.

### GetOneResponsePerDeviceOk

`func (o *CampaignSettingsResponse) GetOneResponsePerDeviceOk() (*bool, bool)`

GetOneResponsePerDeviceOk returns a tuple with the OneResponsePerDevice field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetOneResponsePerDevice

`func (o *CampaignSettingsResponse) SetOneResponsePerDevice(v bool)`

SetOneResponsePerDevice sets OneResponsePerDevice field to given value.

### HasOneResponsePerDevice

`func (o *CampaignSettingsResponse) HasOneResponsePerDevice() bool`

HasOneResponsePerDevice returns a boolean if a field has been set.

### GetIpRestrictionEnabled

`func (o *CampaignSettingsResponse) GetIpRestrictionEnabled() bool`

GetIpRestrictionEnabled returns the IpRestrictionEnabled field if non-nil, zero value otherwise.

### GetIpRestrictionEnabledOk

`func (o *CampaignSettingsResponse) GetIpRestrictionEnabledOk() (*bool, bool)`

GetIpRestrictionEnabledOk returns a tuple with the IpRestrictionEnabled field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetIpRestrictionEnabled

`func (o *CampaignSettingsResponse) SetIpRestrictionEnabled(v bool)`

SetIpRestrictionEnabled sets IpRestrictionEnabled field to given value.

### HasIpRestrictionEnabled

`func (o *CampaignSettingsResponse) HasIpRestrictionEnabled() bool`

HasIpRestrictionEnabled returns a boolean if a field has been set.

### GetEmailRestrictionEnabled

`func (o *CampaignSettingsResponse) GetEmailRestrictionEnabled() bool`

GetEmailRestrictionEnabled returns the EmailRestrictionEnabled field if non-nil, zero value otherwise.

### GetEmailRestrictionEnabledOk

`func (o *CampaignSettingsResponse) GetEmailRestrictionEnabledOk() (*bool, bool)`

GetEmailRestrictionEnabledOk returns a tuple with the EmailRestrictionEnabled field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetEmailRestrictionEnabled

`func (o *CampaignSettingsResponse) SetEmailRestrictionEnabled(v bool)`

SetEmailRestrictionEnabled sets EmailRestrictionEnabled field to given value.

### HasEmailRestrictionEnabled

`func (o *CampaignSettingsResponse) HasEmailRestrictionEnabled() bool`

HasEmailRestrictionEnabled returns a boolean if a field has been set.

### GetResponseQuota

`func (o *CampaignSettingsResponse) GetResponseQuota() int32`

GetResponseQuota returns the ResponseQuota field if non-nil, zero value otherwise.

### GetResponseQuotaOk

`func (o *CampaignSettingsResponse) GetResponseQuotaOk() (*int32, bool)`

GetResponseQuotaOk returns a tuple with the ResponseQuota field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetResponseQuota

`func (o *CampaignSettingsResponse) SetResponseQuota(v int32)`

SetResponseQuota sets ResponseQuota field to given value.

### HasResponseQuota

`func (o *CampaignSettingsResponse) HasResponseQuota() bool`

HasResponseQuota returns a boolean if a field has been set.

### GetCloseDate

`func (o *CampaignSettingsResponse) GetCloseDate() time.Time`

GetCloseDate returns the CloseDate field if non-nil, zero value otherwise.

### GetCloseDateOk

`func (o *CampaignSettingsResponse) GetCloseDateOk() (*time.Time, bool)`

GetCloseDateOk returns a tuple with the CloseDate field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetCloseDate

`func (o *CampaignSettingsResponse) SetCloseDate(v time.Time)`

SetCloseDate sets CloseDate field to given value.

### HasCloseDate

`func (o *CampaignSettingsResponse) HasCloseDate() bool`

HasCloseDate returns a boolean if a field has been set.

### GetSessionTimeoutMinutes

`func (o *CampaignSettingsResponse) GetSessionTimeoutMinutes() int32`

GetSessionTimeoutMinutes returns the SessionTimeoutMinutes field if non-nil, zero value otherwise.

### GetSessionTimeoutMinutesOk

`func (o *CampaignSettingsResponse) GetSessionTimeoutMinutesOk() (*int32, bool)`

GetSessionTimeoutMinutesOk returns a tuple with the SessionTimeoutMinutes field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetSessionTimeoutMinutes

`func (o *CampaignSettingsResponse) SetSessionTimeoutMinutes(v int32)`

SetSessionTimeoutMinutes sets SessionTimeoutMinutes field to given value.

### HasSessionTimeoutMinutes

`func (o *CampaignSettingsResponse) HasSessionTimeoutMinutes() bool`

HasSessionTimeoutMinutes returns a boolean if a field has been set.

### GetShowQuestionNumbers

`func (o *CampaignSettingsResponse) GetShowQuestionNumbers() bool`

GetShowQuestionNumbers returns the ShowQuestionNumbers field if non-nil, zero value otherwise.

### GetShowQuestionNumbersOk

`func (o *CampaignSettingsResponse) GetShowQuestionNumbersOk() (*bool, bool)`

GetShowQuestionNumbersOk returns a tuple with the ShowQuestionNumbers field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetShowQuestionNumbers

`func (o *CampaignSettingsResponse) SetShowQuestionNumbers(v bool)`

SetShowQuestionNumbers sets ShowQuestionNumbers field to given value.

### HasShowQuestionNumbers

`func (o *CampaignSettingsResponse) HasShowQuestionNumbers() bool`

HasShowQuestionNumbers returns a boolean if a field has been set.

### GetShowProgressIndicator

`func (o *CampaignSettingsResponse) GetShowProgressIndicator() bool`

GetShowProgressIndicator returns the ShowProgressIndicator field if non-nil, zero value otherwise.

### GetShowProgressIndicatorOk

`func (o *CampaignSettingsResponse) GetShowProgressIndicatorOk() (*bool, bool)`

GetShowProgressIndicatorOk returns a tuple with the ShowProgressIndicator field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetShowProgressIndicator

`func (o *CampaignSettingsResponse) SetShowProgressIndicator(v bool)`

SetShowProgressIndicator sets ShowProgressIndicator field to given value.

### HasShowProgressIndicator

`func (o *CampaignSettingsResponse) HasShowProgressIndicator() bool`

HasShowProgressIndicator returns a boolean if a field has been set.

### GetAllowBackButton

`func (o *CampaignSettingsResponse) GetAllowBackButton() bool`

GetAllowBackButton returns the AllowBackButton field if non-nil, zero value otherwise.

### GetAllowBackButtonOk

`func (o *CampaignSettingsResponse) GetAllowBackButtonOk() (*bool, bool)`

GetAllowBackButtonOk returns a tuple with the AllowBackButton field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetAllowBackButton

`func (o *CampaignSettingsResponse) SetAllowBackButton(v bool)`

SetAllowBackButton sets AllowBackButton field to given value.

### HasAllowBackButton

`func (o *CampaignSettingsResponse) HasAllowBackButton() bool`

HasAllowBackButton returns a boolean if a field has been set.

### GetStartMessage

`func (o *CampaignSettingsResponse) GetStartMessage() string`

GetStartMessage returns the StartMessage field if non-nil, zero value otherwise.

### GetStartMessageOk

`func (o *CampaignSettingsResponse) GetStartMessageOk() (*string, bool)`

GetStartMessageOk returns a tuple with the StartMessage field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetStartMessage

`func (o *CampaignSettingsResponse) SetStartMessage(v string)`

SetStartMessage sets StartMessage field to given value.

### HasStartMessage

`func (o *CampaignSettingsResponse) HasStartMessage() bool`

HasStartMessage returns a boolean if a field has been set.

### GetFinishMessage

`func (o *CampaignSettingsResponse) GetFinishMessage() string`

GetFinishMessage returns the FinishMessage field if non-nil, zero value otherwise.

### GetFinishMessageOk

`func (o *CampaignSettingsResponse) GetFinishMessageOk() (*string, bool)`

GetFinishMessageOk returns a tuple with the FinishMessage field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetFinishMessage

`func (o *CampaignSettingsResponse) SetFinishMessage(v string)`

SetFinishMessage sets FinishMessage field to given value.

### HasFinishMessage

`func (o *CampaignSettingsResponse) HasFinishMessage() bool`

HasFinishMessage returns a boolean if a field has been set.

### GetHeaderHtml

`func (o *CampaignSettingsResponse) GetHeaderHtml() string`

GetHeaderHtml returns the HeaderHtml field if non-nil, zero value otherwise.

### GetHeaderHtmlOk

`func (o *CampaignSettingsResponse) GetHeaderHtmlOk() (*string, bool)`

GetHeaderHtmlOk returns a tuple with the HeaderHtml field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetHeaderHtml

`func (o *CampaignSettingsResponse) SetHeaderHtml(v string)`

SetHeaderHtml sets HeaderHtml field to given value.

### HasHeaderHtml

`func (o *CampaignSettingsResponse) HasHeaderHtml() bool`

HasHeaderHtml returns a boolean if a field has been set.

### GetFooterHtml

`func (o *CampaignSettingsResponse) GetFooterHtml() string`

GetFooterHtml returns the FooterHtml field if non-nil, zero value otherwise.

### GetFooterHtmlOk

`func (o *CampaignSettingsResponse) GetFooterHtmlOk() (*string, bool)`

GetFooterHtmlOk returns a tuple with the FooterHtml field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetFooterHtml

`func (o *CampaignSettingsResponse) SetFooterHtml(v string)`

SetFooterHtml sets FooterHtml field to given value.

### HasFooterHtml

`func (o *CampaignSettingsResponse) HasFooterHtml() bool`

HasFooterHtml returns a boolean if a field has been set.

### GetCollectName

`func (o *CampaignSettingsResponse) GetCollectName() bool`

GetCollectName returns the CollectName field if non-nil, zero value otherwise.

### GetCollectNameOk

`func (o *CampaignSettingsResponse) GetCollectNameOk() (*bool, bool)`

GetCollectNameOk returns a tuple with the CollectName field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetCollectName

`func (o *CampaignSettingsResponse) SetCollectName(v bool)`

SetCollectName sets CollectName field to given value.

### HasCollectName

`func (o *CampaignSettingsResponse) HasCollectName() bool`

HasCollectName returns a boolean if a field has been set.

### GetCollectEmail

`func (o *CampaignSettingsResponse) GetCollectEmail() bool`

GetCollectEmail returns the CollectEmail field if non-nil, zero value otherwise.

### GetCollectEmailOk

`func (o *CampaignSettingsResponse) GetCollectEmailOk() (*bool, bool)`

GetCollectEmailOk returns a tuple with the CollectEmail field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetCollectEmail

`func (o *CampaignSettingsResponse) SetCollectEmail(v bool)`

SetCollectEmail sets CollectEmail field to given value.

### HasCollectEmail

`func (o *CampaignSettingsResponse) HasCollectEmail() bool`

HasCollectEmail returns a boolean if a field has been set.

### GetCollectPhone

`func (o *CampaignSettingsResponse) GetCollectPhone() bool`

GetCollectPhone returns the CollectPhone field if non-nil, zero value otherwise.

### GetCollectPhoneOk

`func (o *CampaignSettingsResponse) GetCollectPhoneOk() (*bool, bool)`

GetCollectPhoneOk returns a tuple with the CollectPhone field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetCollectPhone

`func (o *CampaignSettingsResponse) SetCollectPhone(v bool)`

SetCollectPhone sets CollectPhone field to given value.

### HasCollectPhone

`func (o *CampaignSettingsResponse) HasCollectPhone() bool`

HasCollectPhone returns a boolean if a field has been set.

### GetCollectAddress

`func (o *CampaignSettingsResponse) GetCollectAddress() bool`

GetCollectAddress returns the CollectAddress field if non-nil, zero value otherwise.

### GetCollectAddressOk

`func (o *CampaignSettingsResponse) GetCollectAddressOk() (*bool, bool)`

GetCollectAddressOk returns a tuple with the CollectAddress field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetCollectAddress

`func (o *CampaignSettingsResponse) SetCollectAddress(v bool)`

SetCollectAddress sets CollectAddress field to given value.

### HasCollectAddress

`func (o *CampaignSettingsResponse) HasCollectAddress() bool`

HasCollectAddress returns a boolean if a field has been set.


[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


