# SubscriptionResponse

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**Id** | Pointer to **string** |  | [optional] 
**TenantId** | Pointer to **string** |  | [optional] 
**Plan** | Pointer to **string** |  | [optional] 
**Status** | Pointer to **string** |  | [optional] 
**CurrentPeriodStart** | Pointer to **time.Time** |  | [optional] 
**CurrentPeriodEnd** | Pointer to **time.Time** |  | [optional] 
**LastPaymentGatewayReference** | Pointer to **string** |  | [optional] 
**PlanPrice** | Pointer to **float32** |  | [optional] 
**Currency** | Pointer to **string** |  | [optional] 
**MaxCampaigns** | Pointer to **int32** |  | [optional] 
**MaxResponsesPerCampaign** | Pointer to **int32** |  | [optional] 
**MaxAdminUsers** | Pointer to **int32** |  | [optional] 

## Methods

### NewSubscriptionResponse

`func NewSubscriptionResponse() *SubscriptionResponse`

NewSubscriptionResponse instantiates a new SubscriptionResponse object
This constructor will assign default values to properties that have it defined,
and makes sure properties required by API are set, but the set of arguments
will change when the set of required properties is changed

### NewSubscriptionResponseWithDefaults

`func NewSubscriptionResponseWithDefaults() *SubscriptionResponse`

NewSubscriptionResponseWithDefaults instantiates a new SubscriptionResponse object
This constructor will only assign default values to properties that have it defined,
but it doesn't guarantee that properties required by API are set

### GetId

`func (o *SubscriptionResponse) GetId() string`

GetId returns the Id field if non-nil, zero value otherwise.

### GetIdOk

`func (o *SubscriptionResponse) GetIdOk() (*string, bool)`

GetIdOk returns a tuple with the Id field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetId

`func (o *SubscriptionResponse) SetId(v string)`

SetId sets Id field to given value.

### HasId

`func (o *SubscriptionResponse) HasId() bool`

HasId returns a boolean if a field has been set.

### GetTenantId

`func (o *SubscriptionResponse) GetTenantId() string`

GetTenantId returns the TenantId field if non-nil, zero value otherwise.

### GetTenantIdOk

`func (o *SubscriptionResponse) GetTenantIdOk() (*string, bool)`

GetTenantIdOk returns a tuple with the TenantId field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetTenantId

`func (o *SubscriptionResponse) SetTenantId(v string)`

SetTenantId sets TenantId field to given value.

### HasTenantId

`func (o *SubscriptionResponse) HasTenantId() bool`

HasTenantId returns a boolean if a field has been set.

### GetPlan

`func (o *SubscriptionResponse) GetPlan() string`

GetPlan returns the Plan field if non-nil, zero value otherwise.

### GetPlanOk

`func (o *SubscriptionResponse) GetPlanOk() (*string, bool)`

GetPlanOk returns a tuple with the Plan field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetPlan

`func (o *SubscriptionResponse) SetPlan(v string)`

SetPlan sets Plan field to given value.

### HasPlan

`func (o *SubscriptionResponse) HasPlan() bool`

HasPlan returns a boolean if a field has been set.

### GetStatus

`func (o *SubscriptionResponse) GetStatus() string`

GetStatus returns the Status field if non-nil, zero value otherwise.

### GetStatusOk

`func (o *SubscriptionResponse) GetStatusOk() (*string, bool)`

GetStatusOk returns a tuple with the Status field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetStatus

`func (o *SubscriptionResponse) SetStatus(v string)`

SetStatus sets Status field to given value.

### HasStatus

`func (o *SubscriptionResponse) HasStatus() bool`

HasStatus returns a boolean if a field has been set.

### GetCurrentPeriodStart

`func (o *SubscriptionResponse) GetCurrentPeriodStart() time.Time`

GetCurrentPeriodStart returns the CurrentPeriodStart field if non-nil, zero value otherwise.

### GetCurrentPeriodStartOk

`func (o *SubscriptionResponse) GetCurrentPeriodStartOk() (*time.Time, bool)`

GetCurrentPeriodStartOk returns a tuple with the CurrentPeriodStart field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetCurrentPeriodStart

`func (o *SubscriptionResponse) SetCurrentPeriodStart(v time.Time)`

SetCurrentPeriodStart sets CurrentPeriodStart field to given value.

### HasCurrentPeriodStart

`func (o *SubscriptionResponse) HasCurrentPeriodStart() bool`

HasCurrentPeriodStart returns a boolean if a field has been set.

### GetCurrentPeriodEnd

`func (o *SubscriptionResponse) GetCurrentPeriodEnd() time.Time`

GetCurrentPeriodEnd returns the CurrentPeriodEnd field if non-nil, zero value otherwise.

### GetCurrentPeriodEndOk

`func (o *SubscriptionResponse) GetCurrentPeriodEndOk() (*time.Time, bool)`

GetCurrentPeriodEndOk returns a tuple with the CurrentPeriodEnd field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetCurrentPeriodEnd

`func (o *SubscriptionResponse) SetCurrentPeriodEnd(v time.Time)`

SetCurrentPeriodEnd sets CurrentPeriodEnd field to given value.

### HasCurrentPeriodEnd

`func (o *SubscriptionResponse) HasCurrentPeriodEnd() bool`

HasCurrentPeriodEnd returns a boolean if a field has been set.

### GetLastPaymentGatewayReference

`func (o *SubscriptionResponse) GetLastPaymentGatewayReference() string`

GetLastPaymentGatewayReference returns the LastPaymentGatewayReference field if non-nil, zero value otherwise.

### GetLastPaymentGatewayReferenceOk

`func (o *SubscriptionResponse) GetLastPaymentGatewayReferenceOk() (*string, bool)`

GetLastPaymentGatewayReferenceOk returns a tuple with the LastPaymentGatewayReference field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetLastPaymentGatewayReference

`func (o *SubscriptionResponse) SetLastPaymentGatewayReference(v string)`

SetLastPaymentGatewayReference sets LastPaymentGatewayReference field to given value.

### HasLastPaymentGatewayReference

`func (o *SubscriptionResponse) HasLastPaymentGatewayReference() bool`

HasLastPaymentGatewayReference returns a boolean if a field has been set.

### GetPlanPrice

`func (o *SubscriptionResponse) GetPlanPrice() float32`

GetPlanPrice returns the PlanPrice field if non-nil, zero value otherwise.

### GetPlanPriceOk

`func (o *SubscriptionResponse) GetPlanPriceOk() (*float32, bool)`

GetPlanPriceOk returns a tuple with the PlanPrice field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetPlanPrice

`func (o *SubscriptionResponse) SetPlanPrice(v float32)`

SetPlanPrice sets PlanPrice field to given value.

### HasPlanPrice

`func (o *SubscriptionResponse) HasPlanPrice() bool`

HasPlanPrice returns a boolean if a field has been set.

### GetCurrency

`func (o *SubscriptionResponse) GetCurrency() string`

GetCurrency returns the Currency field if non-nil, zero value otherwise.

### GetCurrencyOk

`func (o *SubscriptionResponse) GetCurrencyOk() (*string, bool)`

GetCurrencyOk returns a tuple with the Currency field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetCurrency

`func (o *SubscriptionResponse) SetCurrency(v string)`

SetCurrency sets Currency field to given value.

### HasCurrency

`func (o *SubscriptionResponse) HasCurrency() bool`

HasCurrency returns a boolean if a field has been set.

### GetMaxCampaigns

`func (o *SubscriptionResponse) GetMaxCampaigns() int32`

GetMaxCampaigns returns the MaxCampaigns field if non-nil, zero value otherwise.

### GetMaxCampaignsOk

`func (o *SubscriptionResponse) GetMaxCampaignsOk() (*int32, bool)`

GetMaxCampaignsOk returns a tuple with the MaxCampaigns field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetMaxCampaigns

`func (o *SubscriptionResponse) SetMaxCampaigns(v int32)`

SetMaxCampaigns sets MaxCampaigns field to given value.

### HasMaxCampaigns

`func (o *SubscriptionResponse) HasMaxCampaigns() bool`

HasMaxCampaigns returns a boolean if a field has been set.

### GetMaxResponsesPerCampaign

`func (o *SubscriptionResponse) GetMaxResponsesPerCampaign() int32`

GetMaxResponsesPerCampaign returns the MaxResponsesPerCampaign field if non-nil, zero value otherwise.

### GetMaxResponsesPerCampaignOk

`func (o *SubscriptionResponse) GetMaxResponsesPerCampaignOk() (*int32, bool)`

GetMaxResponsesPerCampaignOk returns a tuple with the MaxResponsesPerCampaign field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetMaxResponsesPerCampaign

`func (o *SubscriptionResponse) SetMaxResponsesPerCampaign(v int32)`

SetMaxResponsesPerCampaign sets MaxResponsesPerCampaign field to given value.

### HasMaxResponsesPerCampaign

`func (o *SubscriptionResponse) HasMaxResponsesPerCampaign() bool`

HasMaxResponsesPerCampaign returns a boolean if a field has been set.

### GetMaxAdminUsers

`func (o *SubscriptionResponse) GetMaxAdminUsers() int32`

GetMaxAdminUsers returns the MaxAdminUsers field if non-nil, zero value otherwise.

### GetMaxAdminUsersOk

`func (o *SubscriptionResponse) GetMaxAdminUsersOk() (*int32, bool)`

GetMaxAdminUsersOk returns a tuple with the MaxAdminUsers field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetMaxAdminUsers

`func (o *SubscriptionResponse) SetMaxAdminUsers(v int32)`

SetMaxAdminUsers sets MaxAdminUsers field to given value.

### HasMaxAdminUsers

`func (o *SubscriptionResponse) HasMaxAdminUsers() bool`

HasMaxAdminUsers returns a boolean if a field has been set.


[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


