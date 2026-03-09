# WeightProfileRequest

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**Name** | **string** |  | 
**CampaignId** | **string** |  | 
**CategoryWeights** | Pointer to [**[]CategoryWeightRequest**](CategoryWeightRequest.md) |  | [optional] 

## Methods

### NewWeightProfileRequest

`func NewWeightProfileRequest(name string, campaignId string, ) *WeightProfileRequest`

NewWeightProfileRequest instantiates a new WeightProfileRequest object
This constructor will assign default values to properties that have it defined,
and makes sure properties required by API are set, but the set of arguments
will change when the set of required properties is changed

### NewWeightProfileRequestWithDefaults

`func NewWeightProfileRequestWithDefaults() *WeightProfileRequest`

NewWeightProfileRequestWithDefaults instantiates a new WeightProfileRequest object
This constructor will only assign default values to properties that have it defined,
but it doesn't guarantee that properties required by API are set

### GetName

`func (o *WeightProfileRequest) GetName() string`

GetName returns the Name field if non-nil, zero value otherwise.

### GetNameOk

`func (o *WeightProfileRequest) GetNameOk() (*string, bool)`

GetNameOk returns a tuple with the Name field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetName

`func (o *WeightProfileRequest) SetName(v string)`

SetName sets Name field to given value.


### GetCampaignId

`func (o *WeightProfileRequest) GetCampaignId() string`

GetCampaignId returns the CampaignId field if non-nil, zero value otherwise.

### GetCampaignIdOk

`func (o *WeightProfileRequest) GetCampaignIdOk() (*string, bool)`

GetCampaignIdOk returns a tuple with the CampaignId field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetCampaignId

`func (o *WeightProfileRequest) SetCampaignId(v string)`

SetCampaignId sets CampaignId field to given value.


### GetCategoryWeights

`func (o *WeightProfileRequest) GetCategoryWeights() []CategoryWeightRequest`

GetCategoryWeights returns the CategoryWeights field if non-nil, zero value otherwise.

### GetCategoryWeightsOk

`func (o *WeightProfileRequest) GetCategoryWeightsOk() (*[]CategoryWeightRequest, bool)`

GetCategoryWeightsOk returns a tuple with the CategoryWeights field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetCategoryWeights

`func (o *WeightProfileRequest) SetCategoryWeights(v []CategoryWeightRequest)`

SetCategoryWeights sets CategoryWeights field to given value.

### HasCategoryWeights

`func (o *WeightProfileRequest) HasCategoryWeights() bool`

HasCategoryWeights returns a boolean if a field has been set.


[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


