# OidcStartRequest

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**TenantId** | **string** |  | 
**CampaignId** | **string** |  | 
**ReturnPath** | Pointer to **string** |  | [optional] 

## Methods

### NewOidcStartRequest

`func NewOidcStartRequest(tenantId string, campaignId string, ) *OidcStartRequest`

NewOidcStartRequest instantiates a new OidcStartRequest object
This constructor will assign default values to properties that have it defined,
and makes sure properties required by API are set, but the set of arguments
will change when the set of required properties is changed

### NewOidcStartRequestWithDefaults

`func NewOidcStartRequestWithDefaults() *OidcStartRequest`

NewOidcStartRequestWithDefaults instantiates a new OidcStartRequest object
This constructor will only assign default values to properties that have it defined,
but it doesn't guarantee that properties required by API are set

### GetTenantId

`func (o *OidcStartRequest) GetTenantId() string`

GetTenantId returns the TenantId field if non-nil, zero value otherwise.

### GetTenantIdOk

`func (o *OidcStartRequest) GetTenantIdOk() (*string, bool)`

GetTenantIdOk returns a tuple with the TenantId field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetTenantId

`func (o *OidcStartRequest) SetTenantId(v string)`

SetTenantId sets TenantId field to given value.


### GetCampaignId

`func (o *OidcStartRequest) GetCampaignId() string`

GetCampaignId returns the CampaignId field if non-nil, zero value otherwise.

### GetCampaignIdOk

`func (o *OidcStartRequest) GetCampaignIdOk() (*string, bool)`

GetCampaignIdOk returns a tuple with the CampaignId field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetCampaignId

`func (o *OidcStartRequest) SetCampaignId(v string)`

SetCampaignId sets CampaignId field to given value.


### GetReturnPath

`func (o *OidcStartRequest) GetReturnPath() string`

GetReturnPath returns the ReturnPath field if non-nil, zero value otherwise.

### GetReturnPathOk

`func (o *OidcStartRequest) GetReturnPathOk() (*string, bool)`

GetReturnPathOk returns a tuple with the ReturnPath field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetReturnPath

`func (o *OidcStartRequest) SetReturnPath(v string)`

SetReturnPath sets ReturnPath field to given value.

### HasReturnPath

`func (o *OidcStartRequest) HasReturnPath() bool`

HasReturnPath returns a boolean if a field has been set.


[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


