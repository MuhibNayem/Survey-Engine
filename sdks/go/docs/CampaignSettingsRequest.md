# CampaignSettingsRequest

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
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
**Theme** | Pointer to [**SurveyThemeConfigDto**](SurveyThemeConfigDto.md) |  | [optional] 
**CollectName** | Pointer to **bool** |  | [optional] 
**CollectEmail** | Pointer to **bool** |  | [optional] 
**CollectPhone** | Pointer to **bool** |  | [optional] 
**CollectAddress** | Pointer to **bool** |  | [optional] 
**DataCollectionFields** | Pointer to [**[]DataCollectionFieldRequest**](DataCollectionFieldRequest.md) |  | [optional] 

## Methods

### NewCampaignSettingsRequest

`func NewCampaignSettingsRequest() *CampaignSettingsRequest`

NewCampaignSettingsRequest instantiates a new CampaignSettingsRequest object
This constructor will assign default values to properties that have it defined,
and makes sure properties required by API are set, but the set of arguments
will change when the set of required properties is changed

### NewCampaignSettingsRequestWithDefaults

`func NewCampaignSettingsRequestWithDefaults() *CampaignSettingsRequest`

NewCampaignSettingsRequestWithDefaults instantiates a new CampaignSettingsRequest object
This constructor will only assign default values to properties that have it defined,
but it doesn't guarantee that properties required by API are set

### GetPassword

`func (o *CampaignSettingsRequest) GetPassword() string`

GetPassword returns the Password field if non-nil, zero value otherwise.

### GetPasswordOk

`func (o *CampaignSettingsRequest) GetPasswordOk() (*string, bool)`

GetPasswordOk returns a tuple with the Password field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetPassword

`func (o *CampaignSettingsRequest) SetPassword(v string)`

SetPassword sets Password field to given value.

### HasPassword

`func (o *CampaignSettingsRequest) HasPassword() bool`

HasPassword returns a boolean if a field has been set.

### GetCaptchaEnabled

`func (o *CampaignSettingsRequest) GetCaptchaEnabled() bool`

GetCaptchaEnabled returns the CaptchaEnabled field if non-nil, zero value otherwise.

### GetCaptchaEnabledOk

`func (o *CampaignSettingsRequest) GetCaptchaEnabledOk() (*bool, bool)`

GetCaptchaEnabledOk returns a tuple with the CaptchaEnabled field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetCaptchaEnabled

`func (o *CampaignSettingsRequest) SetCaptchaEnabled(v bool)`

SetCaptchaEnabled sets CaptchaEnabled field to given value.

### HasCaptchaEnabled

`func (o *CampaignSettingsRequest) HasCaptchaEnabled() bool`

HasCaptchaEnabled returns a boolean if a field has been set.

### GetOneResponsePerDevice

`func (o *CampaignSettingsRequest) GetOneResponsePerDevice() bool`

GetOneResponsePerDevice returns the OneResponsePerDevice field if non-nil, zero value otherwise.

### GetOneResponsePerDeviceOk

`func (o *CampaignSettingsRequest) GetOneResponsePerDeviceOk() (*bool, bool)`

GetOneResponsePerDeviceOk returns a tuple with the OneResponsePerDevice field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetOneResponsePerDevice

`func (o *CampaignSettingsRequest) SetOneResponsePerDevice(v bool)`

SetOneResponsePerDevice sets OneResponsePerDevice field to given value.

### HasOneResponsePerDevice

`func (o *CampaignSettingsRequest) HasOneResponsePerDevice() bool`

HasOneResponsePerDevice returns a boolean if a field has been set.

### GetIpRestrictionEnabled

`func (o *CampaignSettingsRequest) GetIpRestrictionEnabled() bool`

GetIpRestrictionEnabled returns the IpRestrictionEnabled field if non-nil, zero value otherwise.

### GetIpRestrictionEnabledOk

`func (o *CampaignSettingsRequest) GetIpRestrictionEnabledOk() (*bool, bool)`

GetIpRestrictionEnabledOk returns a tuple with the IpRestrictionEnabled field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetIpRestrictionEnabled

`func (o *CampaignSettingsRequest) SetIpRestrictionEnabled(v bool)`

SetIpRestrictionEnabled sets IpRestrictionEnabled field to given value.

### HasIpRestrictionEnabled

`func (o *CampaignSettingsRequest) HasIpRestrictionEnabled() bool`

HasIpRestrictionEnabled returns a boolean if a field has been set.

### GetEmailRestrictionEnabled

`func (o *CampaignSettingsRequest) GetEmailRestrictionEnabled() bool`

GetEmailRestrictionEnabled returns the EmailRestrictionEnabled field if non-nil, zero value otherwise.

### GetEmailRestrictionEnabledOk

`func (o *CampaignSettingsRequest) GetEmailRestrictionEnabledOk() (*bool, bool)`

GetEmailRestrictionEnabledOk returns a tuple with the EmailRestrictionEnabled field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetEmailRestrictionEnabled

`func (o *CampaignSettingsRequest) SetEmailRestrictionEnabled(v bool)`

SetEmailRestrictionEnabled sets EmailRestrictionEnabled field to given value.

### HasEmailRestrictionEnabled

`func (o *CampaignSettingsRequest) HasEmailRestrictionEnabled() bool`

HasEmailRestrictionEnabled returns a boolean if a field has been set.

### GetResponseQuota

`func (o *CampaignSettingsRequest) GetResponseQuota() int32`

GetResponseQuota returns the ResponseQuota field if non-nil, zero value otherwise.

### GetResponseQuotaOk

`func (o *CampaignSettingsRequest) GetResponseQuotaOk() (*int32, bool)`

GetResponseQuotaOk returns a tuple with the ResponseQuota field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetResponseQuota

`func (o *CampaignSettingsRequest) SetResponseQuota(v int32)`

SetResponseQuota sets ResponseQuota field to given value.

### HasResponseQuota

`func (o *CampaignSettingsRequest) HasResponseQuota() bool`

HasResponseQuota returns a boolean if a field has been set.

### GetCloseDate

`func (o *CampaignSettingsRequest) GetCloseDate() time.Time`

GetCloseDate returns the CloseDate field if non-nil, zero value otherwise.

### GetCloseDateOk

`func (o *CampaignSettingsRequest) GetCloseDateOk() (*time.Time, bool)`

GetCloseDateOk returns a tuple with the CloseDate field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetCloseDate

`func (o *CampaignSettingsRequest) SetCloseDate(v time.Time)`

SetCloseDate sets CloseDate field to given value.

### HasCloseDate

`func (o *CampaignSettingsRequest) HasCloseDate() bool`

HasCloseDate returns a boolean if a field has been set.

### GetSessionTimeoutMinutes

`func (o *CampaignSettingsRequest) GetSessionTimeoutMinutes() int32`

GetSessionTimeoutMinutes returns the SessionTimeoutMinutes field if non-nil, zero value otherwise.

### GetSessionTimeoutMinutesOk

`func (o *CampaignSettingsRequest) GetSessionTimeoutMinutesOk() (*int32, bool)`

GetSessionTimeoutMinutesOk returns a tuple with the SessionTimeoutMinutes field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetSessionTimeoutMinutes

`func (o *CampaignSettingsRequest) SetSessionTimeoutMinutes(v int32)`

SetSessionTimeoutMinutes sets SessionTimeoutMinutes field to given value.

### HasSessionTimeoutMinutes

`func (o *CampaignSettingsRequest) HasSessionTimeoutMinutes() bool`

HasSessionTimeoutMinutes returns a boolean if a field has been set.

### GetShowQuestionNumbers

`func (o *CampaignSettingsRequest) GetShowQuestionNumbers() bool`

GetShowQuestionNumbers returns the ShowQuestionNumbers field if non-nil, zero value otherwise.

### GetShowQuestionNumbersOk

`func (o *CampaignSettingsRequest) GetShowQuestionNumbersOk() (*bool, bool)`

GetShowQuestionNumbersOk returns a tuple with the ShowQuestionNumbers field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetShowQuestionNumbers

`func (o *CampaignSettingsRequest) SetShowQuestionNumbers(v bool)`

SetShowQuestionNumbers sets ShowQuestionNumbers field to given value.

### HasShowQuestionNumbers

`func (o *CampaignSettingsRequest) HasShowQuestionNumbers() bool`

HasShowQuestionNumbers returns a boolean if a field has been set.

### GetShowProgressIndicator

`func (o *CampaignSettingsRequest) GetShowProgressIndicator() bool`

GetShowProgressIndicator returns the ShowProgressIndicator field if non-nil, zero value otherwise.

### GetShowProgressIndicatorOk

`func (o *CampaignSettingsRequest) GetShowProgressIndicatorOk() (*bool, bool)`

GetShowProgressIndicatorOk returns a tuple with the ShowProgressIndicator field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetShowProgressIndicator

`func (o *CampaignSettingsRequest) SetShowProgressIndicator(v bool)`

SetShowProgressIndicator sets ShowProgressIndicator field to given value.

### HasShowProgressIndicator

`func (o *CampaignSettingsRequest) HasShowProgressIndicator() bool`

HasShowProgressIndicator returns a boolean if a field has been set.

### GetAllowBackButton

`func (o *CampaignSettingsRequest) GetAllowBackButton() bool`

GetAllowBackButton returns the AllowBackButton field if non-nil, zero value otherwise.

### GetAllowBackButtonOk

`func (o *CampaignSettingsRequest) GetAllowBackButtonOk() (*bool, bool)`

GetAllowBackButtonOk returns a tuple with the AllowBackButton field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetAllowBackButton

`func (o *CampaignSettingsRequest) SetAllowBackButton(v bool)`

SetAllowBackButton sets AllowBackButton field to given value.

### HasAllowBackButton

`func (o *CampaignSettingsRequest) HasAllowBackButton() bool`

HasAllowBackButton returns a boolean if a field has been set.

### GetStartMessage

`func (o *CampaignSettingsRequest) GetStartMessage() string`

GetStartMessage returns the StartMessage field if non-nil, zero value otherwise.

### GetStartMessageOk

`func (o *CampaignSettingsRequest) GetStartMessageOk() (*string, bool)`

GetStartMessageOk returns a tuple with the StartMessage field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetStartMessage

`func (o *CampaignSettingsRequest) SetStartMessage(v string)`

SetStartMessage sets StartMessage field to given value.

### HasStartMessage

`func (o *CampaignSettingsRequest) HasStartMessage() bool`

HasStartMessage returns a boolean if a field has been set.

### GetFinishMessage

`func (o *CampaignSettingsRequest) GetFinishMessage() string`

GetFinishMessage returns the FinishMessage field if non-nil, zero value otherwise.

### GetFinishMessageOk

`func (o *CampaignSettingsRequest) GetFinishMessageOk() (*string, bool)`

GetFinishMessageOk returns a tuple with the FinishMessage field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetFinishMessage

`func (o *CampaignSettingsRequest) SetFinishMessage(v string)`

SetFinishMessage sets FinishMessage field to given value.

### HasFinishMessage

`func (o *CampaignSettingsRequest) HasFinishMessage() bool`

HasFinishMessage returns a boolean if a field has been set.

### GetHeaderHtml

`func (o *CampaignSettingsRequest) GetHeaderHtml() string`

GetHeaderHtml returns the HeaderHtml field if non-nil, zero value otherwise.

### GetHeaderHtmlOk

`func (o *CampaignSettingsRequest) GetHeaderHtmlOk() (*string, bool)`

GetHeaderHtmlOk returns a tuple with the HeaderHtml field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetHeaderHtml

`func (o *CampaignSettingsRequest) SetHeaderHtml(v string)`

SetHeaderHtml sets HeaderHtml field to given value.

### HasHeaderHtml

`func (o *CampaignSettingsRequest) HasHeaderHtml() bool`

HasHeaderHtml returns a boolean if a field has been set.

### GetFooterHtml

`func (o *CampaignSettingsRequest) GetFooterHtml() string`

GetFooterHtml returns the FooterHtml field if non-nil, zero value otherwise.

### GetFooterHtmlOk

`func (o *CampaignSettingsRequest) GetFooterHtmlOk() (*string, bool)`

GetFooterHtmlOk returns a tuple with the FooterHtml field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetFooterHtml

`func (o *CampaignSettingsRequest) SetFooterHtml(v string)`

SetFooterHtml sets FooterHtml field to given value.

### HasFooterHtml

`func (o *CampaignSettingsRequest) HasFooterHtml() bool`

HasFooterHtml returns a boolean if a field has been set.

### GetTheme

`func (o *CampaignSettingsRequest) GetTheme() SurveyThemeConfigDto`

GetTheme returns the Theme field if non-nil, zero value otherwise.

### GetThemeOk

`func (o *CampaignSettingsRequest) GetThemeOk() (*SurveyThemeConfigDto, bool)`

GetThemeOk returns a tuple with the Theme field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetTheme

`func (o *CampaignSettingsRequest) SetTheme(v SurveyThemeConfigDto)`

SetTheme sets Theme field to given value.

### HasTheme

`func (o *CampaignSettingsRequest) HasTheme() bool`

HasTheme returns a boolean if a field has been set.

### GetCollectName

`func (o *CampaignSettingsRequest) GetCollectName() bool`

GetCollectName returns the CollectName field if non-nil, zero value otherwise.

### GetCollectNameOk

`func (o *CampaignSettingsRequest) GetCollectNameOk() (*bool, bool)`

GetCollectNameOk returns a tuple with the CollectName field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetCollectName

`func (o *CampaignSettingsRequest) SetCollectName(v bool)`

SetCollectName sets CollectName field to given value.

### HasCollectName

`func (o *CampaignSettingsRequest) HasCollectName() bool`

HasCollectName returns a boolean if a field has been set.

### GetCollectEmail

`func (o *CampaignSettingsRequest) GetCollectEmail() bool`

GetCollectEmail returns the CollectEmail field if non-nil, zero value otherwise.

### GetCollectEmailOk

`func (o *CampaignSettingsRequest) GetCollectEmailOk() (*bool, bool)`

GetCollectEmailOk returns a tuple with the CollectEmail field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetCollectEmail

`func (o *CampaignSettingsRequest) SetCollectEmail(v bool)`

SetCollectEmail sets CollectEmail field to given value.

### HasCollectEmail

`func (o *CampaignSettingsRequest) HasCollectEmail() bool`

HasCollectEmail returns a boolean if a field has been set.

### GetCollectPhone

`func (o *CampaignSettingsRequest) GetCollectPhone() bool`

GetCollectPhone returns the CollectPhone field if non-nil, zero value otherwise.

### GetCollectPhoneOk

`func (o *CampaignSettingsRequest) GetCollectPhoneOk() (*bool, bool)`

GetCollectPhoneOk returns a tuple with the CollectPhone field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetCollectPhone

`func (o *CampaignSettingsRequest) SetCollectPhone(v bool)`

SetCollectPhone sets CollectPhone field to given value.

### HasCollectPhone

`func (o *CampaignSettingsRequest) HasCollectPhone() bool`

HasCollectPhone returns a boolean if a field has been set.

### GetCollectAddress

`func (o *CampaignSettingsRequest) GetCollectAddress() bool`

GetCollectAddress returns the CollectAddress field if non-nil, zero value otherwise.

### GetCollectAddressOk

`func (o *CampaignSettingsRequest) GetCollectAddressOk() (*bool, bool)`

GetCollectAddressOk returns a tuple with the CollectAddress field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetCollectAddress

`func (o *CampaignSettingsRequest) SetCollectAddress(v bool)`

SetCollectAddress sets CollectAddress field to given value.

### HasCollectAddress

`func (o *CampaignSettingsRequest) HasCollectAddress() bool`

HasCollectAddress returns a boolean if a field has been set.

### GetDataCollectionFields

`func (o *CampaignSettingsRequest) GetDataCollectionFields() []DataCollectionFieldRequest`

GetDataCollectionFields returns the DataCollectionFields field if non-nil, zero value otherwise.

### GetDataCollectionFieldsOk

`func (o *CampaignSettingsRequest) GetDataCollectionFieldsOk() (*[]DataCollectionFieldRequest, bool)`

GetDataCollectionFieldsOk returns a tuple with the DataCollectionFields field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetDataCollectionFields

`func (o *CampaignSettingsRequest) SetDataCollectionFields(v []DataCollectionFieldRequest)`

SetDataCollectionFields sets DataCollectionFields field to given value.

### HasDataCollectionFields

`func (o *CampaignSettingsRequest) HasDataCollectionFields() bool`

HasDataCollectionFields returns a boolean if a field has been set.


[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


