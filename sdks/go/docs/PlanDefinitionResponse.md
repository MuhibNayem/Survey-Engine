# PlanDefinitionResponse

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**PlanCode** | **string** |  | 
**DisplayName** | **string** |  | 
**Price** | **float32** |  | 
**Currency** | **string** |  | 
**BillingCycleDays** | **int32** |  | 
**TrialDays** | **int32** |  | 
**MaxCampaigns** | Pointer to **int32** |  | [optional] 
**MaxResponsesPerCampaign** | Pointer to **int32** |  | [optional] 
**MaxAdminUsers** | Pointer to **int32** |  | [optional] 
**Active** | Pointer to **bool** |  | [optional] [default to true]
**Id** | Pointer to **string** |  | [optional] 

## Methods

### NewPlanDefinitionResponse

`func NewPlanDefinitionResponse(planCode string, displayName string, price float32, currency string, billingCycleDays int32, trialDays int32, ) *PlanDefinitionResponse`

NewPlanDefinitionResponse instantiates a new PlanDefinitionResponse object
This constructor will assign default values to properties that have it defined,
and makes sure properties required by API are set, but the set of arguments
will change when the set of required properties is changed

### NewPlanDefinitionResponseWithDefaults

`func NewPlanDefinitionResponseWithDefaults() *PlanDefinitionResponse`

NewPlanDefinitionResponseWithDefaults instantiates a new PlanDefinitionResponse object
This constructor will only assign default values to properties that have it defined,
but it doesn't guarantee that properties required by API are set

### GetPlanCode

`func (o *PlanDefinitionResponse) GetPlanCode() string`

GetPlanCode returns the PlanCode field if non-nil, zero value otherwise.

### GetPlanCodeOk

`func (o *PlanDefinitionResponse) GetPlanCodeOk() (*string, bool)`

GetPlanCodeOk returns a tuple with the PlanCode field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetPlanCode

`func (o *PlanDefinitionResponse) SetPlanCode(v string)`

SetPlanCode sets PlanCode field to given value.


### GetDisplayName

`func (o *PlanDefinitionResponse) GetDisplayName() string`

GetDisplayName returns the DisplayName field if non-nil, zero value otherwise.

### GetDisplayNameOk

`func (o *PlanDefinitionResponse) GetDisplayNameOk() (*string, bool)`

GetDisplayNameOk returns a tuple with the DisplayName field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetDisplayName

`func (o *PlanDefinitionResponse) SetDisplayName(v string)`

SetDisplayName sets DisplayName field to given value.


### GetPrice

`func (o *PlanDefinitionResponse) GetPrice() float32`

GetPrice returns the Price field if non-nil, zero value otherwise.

### GetPriceOk

`func (o *PlanDefinitionResponse) GetPriceOk() (*float32, bool)`

GetPriceOk returns a tuple with the Price field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetPrice

`func (o *PlanDefinitionResponse) SetPrice(v float32)`

SetPrice sets Price field to given value.


### GetCurrency

`func (o *PlanDefinitionResponse) GetCurrency() string`

GetCurrency returns the Currency field if non-nil, zero value otherwise.

### GetCurrencyOk

`func (o *PlanDefinitionResponse) GetCurrencyOk() (*string, bool)`

GetCurrencyOk returns a tuple with the Currency field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetCurrency

`func (o *PlanDefinitionResponse) SetCurrency(v string)`

SetCurrency sets Currency field to given value.


### GetBillingCycleDays

`func (o *PlanDefinitionResponse) GetBillingCycleDays() int32`

GetBillingCycleDays returns the BillingCycleDays field if non-nil, zero value otherwise.

### GetBillingCycleDaysOk

`func (o *PlanDefinitionResponse) GetBillingCycleDaysOk() (*int32, bool)`

GetBillingCycleDaysOk returns a tuple with the BillingCycleDays field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetBillingCycleDays

`func (o *PlanDefinitionResponse) SetBillingCycleDays(v int32)`

SetBillingCycleDays sets BillingCycleDays field to given value.


### GetTrialDays

`func (o *PlanDefinitionResponse) GetTrialDays() int32`

GetTrialDays returns the TrialDays field if non-nil, zero value otherwise.

### GetTrialDaysOk

`func (o *PlanDefinitionResponse) GetTrialDaysOk() (*int32, bool)`

GetTrialDaysOk returns a tuple with the TrialDays field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetTrialDays

`func (o *PlanDefinitionResponse) SetTrialDays(v int32)`

SetTrialDays sets TrialDays field to given value.


### GetMaxCampaigns

`func (o *PlanDefinitionResponse) GetMaxCampaigns() int32`

GetMaxCampaigns returns the MaxCampaigns field if non-nil, zero value otherwise.

### GetMaxCampaignsOk

`func (o *PlanDefinitionResponse) GetMaxCampaignsOk() (*int32, bool)`

GetMaxCampaignsOk returns a tuple with the MaxCampaigns field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetMaxCampaigns

`func (o *PlanDefinitionResponse) SetMaxCampaigns(v int32)`

SetMaxCampaigns sets MaxCampaigns field to given value.

### HasMaxCampaigns

`func (o *PlanDefinitionResponse) HasMaxCampaigns() bool`

HasMaxCampaigns returns a boolean if a field has been set.

### GetMaxResponsesPerCampaign

`func (o *PlanDefinitionResponse) GetMaxResponsesPerCampaign() int32`

GetMaxResponsesPerCampaign returns the MaxResponsesPerCampaign field if non-nil, zero value otherwise.

### GetMaxResponsesPerCampaignOk

`func (o *PlanDefinitionResponse) GetMaxResponsesPerCampaignOk() (*int32, bool)`

GetMaxResponsesPerCampaignOk returns a tuple with the MaxResponsesPerCampaign field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetMaxResponsesPerCampaign

`func (o *PlanDefinitionResponse) SetMaxResponsesPerCampaign(v int32)`

SetMaxResponsesPerCampaign sets MaxResponsesPerCampaign field to given value.

### HasMaxResponsesPerCampaign

`func (o *PlanDefinitionResponse) HasMaxResponsesPerCampaign() bool`

HasMaxResponsesPerCampaign returns a boolean if a field has been set.

### GetMaxAdminUsers

`func (o *PlanDefinitionResponse) GetMaxAdminUsers() int32`

GetMaxAdminUsers returns the MaxAdminUsers field if non-nil, zero value otherwise.

### GetMaxAdminUsersOk

`func (o *PlanDefinitionResponse) GetMaxAdminUsersOk() (*int32, bool)`

GetMaxAdminUsersOk returns a tuple with the MaxAdminUsers field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetMaxAdminUsers

`func (o *PlanDefinitionResponse) SetMaxAdminUsers(v int32)`

SetMaxAdminUsers sets MaxAdminUsers field to given value.

### HasMaxAdminUsers

`func (o *PlanDefinitionResponse) HasMaxAdminUsers() bool`

HasMaxAdminUsers returns a boolean if a field has been set.

### GetActive

`func (o *PlanDefinitionResponse) GetActive() bool`

GetActive returns the Active field if non-nil, zero value otherwise.

### GetActiveOk

`func (o *PlanDefinitionResponse) GetActiveOk() (*bool, bool)`

GetActiveOk returns a tuple with the Active field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetActive

`func (o *PlanDefinitionResponse) SetActive(v bool)`

SetActive sets Active field to given value.

### HasActive

`func (o *PlanDefinitionResponse) HasActive() bool`

HasActive returns a boolean if a field has been set.

### GetId

`func (o *PlanDefinitionResponse) GetId() string`

GetId returns the Id field if non-nil, zero value otherwise.

### GetIdOk

`func (o *PlanDefinitionResponse) GetIdOk() (*string, bool)`

GetIdOk returns a tuple with the Id field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetId

`func (o *PlanDefinitionResponse) SetId(v string)`

SetId sets Id field to given value.

### HasId

`func (o *PlanDefinitionResponse) HasId() bool`

HasId returns a boolean if a field has been set.


[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


