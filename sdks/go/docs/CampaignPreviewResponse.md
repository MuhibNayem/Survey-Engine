# CampaignPreviewResponse

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**CampaignId** | Pointer to **string** |  | [optional] 
**TenantId** | Pointer to **string** |  | [optional] 
**CampaignName** | Pointer to **string** |  | [optional] 
**CampaignStatus** | Pointer to **string** |  | [optional] 
**AuthMode** | Pointer to **string** |  | [optional] 
**SurveyId** | Pointer to **string** |  | [optional] 
**SurveyTitle** | Pointer to **string** |  | [optional] 
**SurveyDescription** | Pointer to **string** |  | [optional] 
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
**DataCollectionFields** | Pointer to [**[]DataCollectionFieldResponse**](DataCollectionFieldResponse.md) |  | [optional] 
**Pages** | Pointer to [**[]CampaignPreviewResponsePagesInner**](CampaignPreviewResponsePagesInner.md) |  | [optional] 

## Methods

### NewCampaignPreviewResponse

`func NewCampaignPreviewResponse() *CampaignPreviewResponse`

NewCampaignPreviewResponse instantiates a new CampaignPreviewResponse object
This constructor will assign default values to properties that have it defined,
and makes sure properties required by API are set, but the set of arguments
will change when the set of required properties is changed

### NewCampaignPreviewResponseWithDefaults

`func NewCampaignPreviewResponseWithDefaults() *CampaignPreviewResponse`

NewCampaignPreviewResponseWithDefaults instantiates a new CampaignPreviewResponse object
This constructor will only assign default values to properties that have it defined,
but it doesn't guarantee that properties required by API are set

### GetCampaignId

`func (o *CampaignPreviewResponse) GetCampaignId() string`

GetCampaignId returns the CampaignId field if non-nil, zero value otherwise.

### GetCampaignIdOk

`func (o *CampaignPreviewResponse) GetCampaignIdOk() (*string, bool)`

GetCampaignIdOk returns a tuple with the CampaignId field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetCampaignId

`func (o *CampaignPreviewResponse) SetCampaignId(v string)`

SetCampaignId sets CampaignId field to given value.

### HasCampaignId

`func (o *CampaignPreviewResponse) HasCampaignId() bool`

HasCampaignId returns a boolean if a field has been set.

### GetTenantId

`func (o *CampaignPreviewResponse) GetTenantId() string`

GetTenantId returns the TenantId field if non-nil, zero value otherwise.

### GetTenantIdOk

`func (o *CampaignPreviewResponse) GetTenantIdOk() (*string, bool)`

GetTenantIdOk returns a tuple with the TenantId field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetTenantId

`func (o *CampaignPreviewResponse) SetTenantId(v string)`

SetTenantId sets TenantId field to given value.

### HasTenantId

`func (o *CampaignPreviewResponse) HasTenantId() bool`

HasTenantId returns a boolean if a field has been set.

### GetCampaignName

`func (o *CampaignPreviewResponse) GetCampaignName() string`

GetCampaignName returns the CampaignName field if non-nil, zero value otherwise.

### GetCampaignNameOk

`func (o *CampaignPreviewResponse) GetCampaignNameOk() (*string, bool)`

GetCampaignNameOk returns a tuple with the CampaignName field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetCampaignName

`func (o *CampaignPreviewResponse) SetCampaignName(v string)`

SetCampaignName sets CampaignName field to given value.

### HasCampaignName

`func (o *CampaignPreviewResponse) HasCampaignName() bool`

HasCampaignName returns a boolean if a field has been set.

### GetCampaignStatus

`func (o *CampaignPreviewResponse) GetCampaignStatus() string`

GetCampaignStatus returns the CampaignStatus field if non-nil, zero value otherwise.

### GetCampaignStatusOk

`func (o *CampaignPreviewResponse) GetCampaignStatusOk() (*string, bool)`

GetCampaignStatusOk returns a tuple with the CampaignStatus field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetCampaignStatus

`func (o *CampaignPreviewResponse) SetCampaignStatus(v string)`

SetCampaignStatus sets CampaignStatus field to given value.

### HasCampaignStatus

`func (o *CampaignPreviewResponse) HasCampaignStatus() bool`

HasCampaignStatus returns a boolean if a field has been set.

### GetAuthMode

`func (o *CampaignPreviewResponse) GetAuthMode() string`

GetAuthMode returns the AuthMode field if non-nil, zero value otherwise.

### GetAuthModeOk

`func (o *CampaignPreviewResponse) GetAuthModeOk() (*string, bool)`

GetAuthModeOk returns a tuple with the AuthMode field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetAuthMode

`func (o *CampaignPreviewResponse) SetAuthMode(v string)`

SetAuthMode sets AuthMode field to given value.

### HasAuthMode

`func (o *CampaignPreviewResponse) HasAuthMode() bool`

HasAuthMode returns a boolean if a field has been set.

### GetSurveyId

`func (o *CampaignPreviewResponse) GetSurveyId() string`

GetSurveyId returns the SurveyId field if non-nil, zero value otherwise.

### GetSurveyIdOk

`func (o *CampaignPreviewResponse) GetSurveyIdOk() (*string, bool)`

GetSurveyIdOk returns a tuple with the SurveyId field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetSurveyId

`func (o *CampaignPreviewResponse) SetSurveyId(v string)`

SetSurveyId sets SurveyId field to given value.

### HasSurveyId

`func (o *CampaignPreviewResponse) HasSurveyId() bool`

HasSurveyId returns a boolean if a field has been set.

### GetSurveyTitle

`func (o *CampaignPreviewResponse) GetSurveyTitle() string`

GetSurveyTitle returns the SurveyTitle field if non-nil, zero value otherwise.

### GetSurveyTitleOk

`func (o *CampaignPreviewResponse) GetSurveyTitleOk() (*string, bool)`

GetSurveyTitleOk returns a tuple with the SurveyTitle field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetSurveyTitle

`func (o *CampaignPreviewResponse) SetSurveyTitle(v string)`

SetSurveyTitle sets SurveyTitle field to given value.

### HasSurveyTitle

`func (o *CampaignPreviewResponse) HasSurveyTitle() bool`

HasSurveyTitle returns a boolean if a field has been set.

### GetSurveyDescription

`func (o *CampaignPreviewResponse) GetSurveyDescription() string`

GetSurveyDescription returns the SurveyDescription field if non-nil, zero value otherwise.

### GetSurveyDescriptionOk

`func (o *CampaignPreviewResponse) GetSurveyDescriptionOk() (*string, bool)`

GetSurveyDescriptionOk returns a tuple with the SurveyDescription field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetSurveyDescription

`func (o *CampaignPreviewResponse) SetSurveyDescription(v string)`

SetSurveyDescription sets SurveyDescription field to given value.

### HasSurveyDescription

`func (o *CampaignPreviewResponse) HasSurveyDescription() bool`

HasSurveyDescription returns a boolean if a field has been set.

### GetShowQuestionNumbers

`func (o *CampaignPreviewResponse) GetShowQuestionNumbers() bool`

GetShowQuestionNumbers returns the ShowQuestionNumbers field if non-nil, zero value otherwise.

### GetShowQuestionNumbersOk

`func (o *CampaignPreviewResponse) GetShowQuestionNumbersOk() (*bool, bool)`

GetShowQuestionNumbersOk returns a tuple with the ShowQuestionNumbers field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetShowQuestionNumbers

`func (o *CampaignPreviewResponse) SetShowQuestionNumbers(v bool)`

SetShowQuestionNumbers sets ShowQuestionNumbers field to given value.

### HasShowQuestionNumbers

`func (o *CampaignPreviewResponse) HasShowQuestionNumbers() bool`

HasShowQuestionNumbers returns a boolean if a field has been set.

### GetShowProgressIndicator

`func (o *CampaignPreviewResponse) GetShowProgressIndicator() bool`

GetShowProgressIndicator returns the ShowProgressIndicator field if non-nil, zero value otherwise.

### GetShowProgressIndicatorOk

`func (o *CampaignPreviewResponse) GetShowProgressIndicatorOk() (*bool, bool)`

GetShowProgressIndicatorOk returns a tuple with the ShowProgressIndicator field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetShowProgressIndicator

`func (o *CampaignPreviewResponse) SetShowProgressIndicator(v bool)`

SetShowProgressIndicator sets ShowProgressIndicator field to given value.

### HasShowProgressIndicator

`func (o *CampaignPreviewResponse) HasShowProgressIndicator() bool`

HasShowProgressIndicator returns a boolean if a field has been set.

### GetAllowBackButton

`func (o *CampaignPreviewResponse) GetAllowBackButton() bool`

GetAllowBackButton returns the AllowBackButton field if non-nil, zero value otherwise.

### GetAllowBackButtonOk

`func (o *CampaignPreviewResponse) GetAllowBackButtonOk() (*bool, bool)`

GetAllowBackButtonOk returns a tuple with the AllowBackButton field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetAllowBackButton

`func (o *CampaignPreviewResponse) SetAllowBackButton(v bool)`

SetAllowBackButton sets AllowBackButton field to given value.

### HasAllowBackButton

`func (o *CampaignPreviewResponse) HasAllowBackButton() bool`

HasAllowBackButton returns a boolean if a field has been set.

### GetStartMessage

`func (o *CampaignPreviewResponse) GetStartMessage() string`

GetStartMessage returns the StartMessage field if non-nil, zero value otherwise.

### GetStartMessageOk

`func (o *CampaignPreviewResponse) GetStartMessageOk() (*string, bool)`

GetStartMessageOk returns a tuple with the StartMessage field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetStartMessage

`func (o *CampaignPreviewResponse) SetStartMessage(v string)`

SetStartMessage sets StartMessage field to given value.

### HasStartMessage

`func (o *CampaignPreviewResponse) HasStartMessage() bool`

HasStartMessage returns a boolean if a field has been set.

### GetFinishMessage

`func (o *CampaignPreviewResponse) GetFinishMessage() string`

GetFinishMessage returns the FinishMessage field if non-nil, zero value otherwise.

### GetFinishMessageOk

`func (o *CampaignPreviewResponse) GetFinishMessageOk() (*string, bool)`

GetFinishMessageOk returns a tuple with the FinishMessage field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetFinishMessage

`func (o *CampaignPreviewResponse) SetFinishMessage(v string)`

SetFinishMessage sets FinishMessage field to given value.

### HasFinishMessage

`func (o *CampaignPreviewResponse) HasFinishMessage() bool`

HasFinishMessage returns a boolean if a field has been set.

### GetHeaderHtml

`func (o *CampaignPreviewResponse) GetHeaderHtml() string`

GetHeaderHtml returns the HeaderHtml field if non-nil, zero value otherwise.

### GetHeaderHtmlOk

`func (o *CampaignPreviewResponse) GetHeaderHtmlOk() (*string, bool)`

GetHeaderHtmlOk returns a tuple with the HeaderHtml field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetHeaderHtml

`func (o *CampaignPreviewResponse) SetHeaderHtml(v string)`

SetHeaderHtml sets HeaderHtml field to given value.

### HasHeaderHtml

`func (o *CampaignPreviewResponse) HasHeaderHtml() bool`

HasHeaderHtml returns a boolean if a field has been set.

### GetFooterHtml

`func (o *CampaignPreviewResponse) GetFooterHtml() string`

GetFooterHtml returns the FooterHtml field if non-nil, zero value otherwise.

### GetFooterHtmlOk

`func (o *CampaignPreviewResponse) GetFooterHtmlOk() (*string, bool)`

GetFooterHtmlOk returns a tuple with the FooterHtml field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetFooterHtml

`func (o *CampaignPreviewResponse) SetFooterHtml(v string)`

SetFooterHtml sets FooterHtml field to given value.

### HasFooterHtml

`func (o *CampaignPreviewResponse) HasFooterHtml() bool`

HasFooterHtml returns a boolean if a field has been set.

### GetTheme

`func (o *CampaignPreviewResponse) GetTheme() SurveyThemeConfigDto`

GetTheme returns the Theme field if non-nil, zero value otherwise.

### GetThemeOk

`func (o *CampaignPreviewResponse) GetThemeOk() (*SurveyThemeConfigDto, bool)`

GetThemeOk returns a tuple with the Theme field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetTheme

`func (o *CampaignPreviewResponse) SetTheme(v SurveyThemeConfigDto)`

SetTheme sets Theme field to given value.

### HasTheme

`func (o *CampaignPreviewResponse) HasTheme() bool`

HasTheme returns a boolean if a field has been set.

### GetCollectName

`func (o *CampaignPreviewResponse) GetCollectName() bool`

GetCollectName returns the CollectName field if non-nil, zero value otherwise.

### GetCollectNameOk

`func (o *CampaignPreviewResponse) GetCollectNameOk() (*bool, bool)`

GetCollectNameOk returns a tuple with the CollectName field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetCollectName

`func (o *CampaignPreviewResponse) SetCollectName(v bool)`

SetCollectName sets CollectName field to given value.

### HasCollectName

`func (o *CampaignPreviewResponse) HasCollectName() bool`

HasCollectName returns a boolean if a field has been set.

### GetCollectEmail

`func (o *CampaignPreviewResponse) GetCollectEmail() bool`

GetCollectEmail returns the CollectEmail field if non-nil, zero value otherwise.

### GetCollectEmailOk

`func (o *CampaignPreviewResponse) GetCollectEmailOk() (*bool, bool)`

GetCollectEmailOk returns a tuple with the CollectEmail field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetCollectEmail

`func (o *CampaignPreviewResponse) SetCollectEmail(v bool)`

SetCollectEmail sets CollectEmail field to given value.

### HasCollectEmail

`func (o *CampaignPreviewResponse) HasCollectEmail() bool`

HasCollectEmail returns a boolean if a field has been set.

### GetCollectPhone

`func (o *CampaignPreviewResponse) GetCollectPhone() bool`

GetCollectPhone returns the CollectPhone field if non-nil, zero value otherwise.

### GetCollectPhoneOk

`func (o *CampaignPreviewResponse) GetCollectPhoneOk() (*bool, bool)`

GetCollectPhoneOk returns a tuple with the CollectPhone field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetCollectPhone

`func (o *CampaignPreviewResponse) SetCollectPhone(v bool)`

SetCollectPhone sets CollectPhone field to given value.

### HasCollectPhone

`func (o *CampaignPreviewResponse) HasCollectPhone() bool`

HasCollectPhone returns a boolean if a field has been set.

### GetCollectAddress

`func (o *CampaignPreviewResponse) GetCollectAddress() bool`

GetCollectAddress returns the CollectAddress field if non-nil, zero value otherwise.

### GetCollectAddressOk

`func (o *CampaignPreviewResponse) GetCollectAddressOk() (*bool, bool)`

GetCollectAddressOk returns a tuple with the CollectAddress field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetCollectAddress

`func (o *CampaignPreviewResponse) SetCollectAddress(v bool)`

SetCollectAddress sets CollectAddress field to given value.

### HasCollectAddress

`func (o *CampaignPreviewResponse) HasCollectAddress() bool`

HasCollectAddress returns a boolean if a field has been set.

### GetDataCollectionFields

`func (o *CampaignPreviewResponse) GetDataCollectionFields() []DataCollectionFieldResponse`

GetDataCollectionFields returns the DataCollectionFields field if non-nil, zero value otherwise.

### GetDataCollectionFieldsOk

`func (o *CampaignPreviewResponse) GetDataCollectionFieldsOk() (*[]DataCollectionFieldResponse, bool)`

GetDataCollectionFieldsOk returns a tuple with the DataCollectionFields field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetDataCollectionFields

`func (o *CampaignPreviewResponse) SetDataCollectionFields(v []DataCollectionFieldResponse)`

SetDataCollectionFields sets DataCollectionFields field to given value.

### HasDataCollectionFields

`func (o *CampaignPreviewResponse) HasDataCollectionFields() bool`

HasDataCollectionFields returns a boolean if a field has been set.

### GetPages

`func (o *CampaignPreviewResponse) GetPages() []CampaignPreviewResponsePagesInner`

GetPages returns the Pages field if non-nil, zero value otherwise.

### GetPagesOk

`func (o *CampaignPreviewResponse) GetPagesOk() (*[]CampaignPreviewResponsePagesInner, bool)`

GetPagesOk returns a tuple with the Pages field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetPages

`func (o *CampaignPreviewResponse) SetPages(v []CampaignPreviewResponsePagesInner)`

SetPages sets Pages field to given value.

### HasPages

`func (o *CampaignPreviewResponse) HasPages() bool`

HasPages returns a boolean if a field has been set.


[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


