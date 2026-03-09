# WeightProfileResponse

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**Id** | Pointer to **string** |  | [optional] 
**Name** | Pointer to **string** |  | [optional] 
**CampaignId** | Pointer to **string** |  | [optional] 
**Active** | Pointer to **bool** |  | [optional] 
**CategoryWeights** | Pointer to [**[]WeightProfileResponseCategoryWeightsInner**](WeightProfileResponseCategoryWeightsInner.md) |  | [optional] 
**CreatedBy** | Pointer to **string** |  | [optional] 
**CreatedAt** | Pointer to **time.Time** |  | [optional] 

## Methods

### NewWeightProfileResponse

`func NewWeightProfileResponse() *WeightProfileResponse`

NewWeightProfileResponse instantiates a new WeightProfileResponse object
This constructor will assign default values to properties that have it defined,
and makes sure properties required by API are set, but the set of arguments
will change when the set of required properties is changed

### NewWeightProfileResponseWithDefaults

`func NewWeightProfileResponseWithDefaults() *WeightProfileResponse`

NewWeightProfileResponseWithDefaults instantiates a new WeightProfileResponse object
This constructor will only assign default values to properties that have it defined,
but it doesn't guarantee that properties required by API are set

### GetId

`func (o *WeightProfileResponse) GetId() string`

GetId returns the Id field if non-nil, zero value otherwise.

### GetIdOk

`func (o *WeightProfileResponse) GetIdOk() (*string, bool)`

GetIdOk returns a tuple with the Id field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetId

`func (o *WeightProfileResponse) SetId(v string)`

SetId sets Id field to given value.

### HasId

`func (o *WeightProfileResponse) HasId() bool`

HasId returns a boolean if a field has been set.

### GetName

`func (o *WeightProfileResponse) GetName() string`

GetName returns the Name field if non-nil, zero value otherwise.

### GetNameOk

`func (o *WeightProfileResponse) GetNameOk() (*string, bool)`

GetNameOk returns a tuple with the Name field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetName

`func (o *WeightProfileResponse) SetName(v string)`

SetName sets Name field to given value.

### HasName

`func (o *WeightProfileResponse) HasName() bool`

HasName returns a boolean if a field has been set.

### GetCampaignId

`func (o *WeightProfileResponse) GetCampaignId() string`

GetCampaignId returns the CampaignId field if non-nil, zero value otherwise.

### GetCampaignIdOk

`func (o *WeightProfileResponse) GetCampaignIdOk() (*string, bool)`

GetCampaignIdOk returns a tuple with the CampaignId field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetCampaignId

`func (o *WeightProfileResponse) SetCampaignId(v string)`

SetCampaignId sets CampaignId field to given value.

### HasCampaignId

`func (o *WeightProfileResponse) HasCampaignId() bool`

HasCampaignId returns a boolean if a field has been set.

### GetActive

`func (o *WeightProfileResponse) GetActive() bool`

GetActive returns the Active field if non-nil, zero value otherwise.

### GetActiveOk

`func (o *WeightProfileResponse) GetActiveOk() (*bool, bool)`

GetActiveOk returns a tuple with the Active field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetActive

`func (o *WeightProfileResponse) SetActive(v bool)`

SetActive sets Active field to given value.

### HasActive

`func (o *WeightProfileResponse) HasActive() bool`

HasActive returns a boolean if a field has been set.

### GetCategoryWeights

`func (o *WeightProfileResponse) GetCategoryWeights() []WeightProfileResponseCategoryWeightsInner`

GetCategoryWeights returns the CategoryWeights field if non-nil, zero value otherwise.

### GetCategoryWeightsOk

`func (o *WeightProfileResponse) GetCategoryWeightsOk() (*[]WeightProfileResponseCategoryWeightsInner, bool)`

GetCategoryWeightsOk returns a tuple with the CategoryWeights field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetCategoryWeights

`func (o *WeightProfileResponse) SetCategoryWeights(v []WeightProfileResponseCategoryWeightsInner)`

SetCategoryWeights sets CategoryWeights field to given value.

### HasCategoryWeights

`func (o *WeightProfileResponse) HasCategoryWeights() bool`

HasCategoryWeights returns a boolean if a field has been set.

### GetCreatedBy

`func (o *WeightProfileResponse) GetCreatedBy() string`

GetCreatedBy returns the CreatedBy field if non-nil, zero value otherwise.

### GetCreatedByOk

`func (o *WeightProfileResponse) GetCreatedByOk() (*string, bool)`

GetCreatedByOk returns a tuple with the CreatedBy field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetCreatedBy

`func (o *WeightProfileResponse) SetCreatedBy(v string)`

SetCreatedBy sets CreatedBy field to given value.

### HasCreatedBy

`func (o *WeightProfileResponse) HasCreatedBy() bool`

HasCreatedBy returns a boolean if a field has been set.

### GetCreatedAt

`func (o *WeightProfileResponse) GetCreatedAt() time.Time`

GetCreatedAt returns the CreatedAt field if non-nil, zero value otherwise.

### GetCreatedAtOk

`func (o *WeightProfileResponse) GetCreatedAtOk() (*time.Time, bool)`

GetCreatedAtOk returns a tuple with the CreatedAt field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetCreatedAt

`func (o *WeightProfileResponse) SetCreatedAt(v time.Time)`

SetCreatedAt sets CreatedAt field to given value.

### HasCreatedAt

`func (o *WeightProfileResponse) HasCreatedAt() bool`

HasCreatedAt returns a boolean if a field has been set.


[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


