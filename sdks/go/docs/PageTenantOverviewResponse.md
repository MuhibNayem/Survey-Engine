# PageTenantOverviewResponse

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**Content** | Pointer to [**[]TenantOverviewResponse**](TenantOverviewResponse.md) |  | [optional] 
**Pageable** | Pointer to [**PageableObject**](PageableObject.md) |  | [optional] 
**Sort** | Pointer to [**SortObject**](SortObject.md) |  | [optional] 
**TotalElements** | Pointer to **int64** |  | [optional] 
**TotalPages** | Pointer to **int32** |  | [optional] 
**Number** | Pointer to **int32** |  | [optional] 
**Size** | Pointer to **int32** |  | [optional] 
**NumberOfElements** | Pointer to **int32** |  | [optional] 
**First** | Pointer to **bool** |  | [optional] 
**Last** | Pointer to **bool** |  | [optional] 
**Empty** | Pointer to **bool** |  | [optional] 

## Methods

### NewPageTenantOverviewResponse

`func NewPageTenantOverviewResponse() *PageTenantOverviewResponse`

NewPageTenantOverviewResponse instantiates a new PageTenantOverviewResponse object
This constructor will assign default values to properties that have it defined,
and makes sure properties required by API are set, but the set of arguments
will change when the set of required properties is changed

### NewPageTenantOverviewResponseWithDefaults

`func NewPageTenantOverviewResponseWithDefaults() *PageTenantOverviewResponse`

NewPageTenantOverviewResponseWithDefaults instantiates a new PageTenantOverviewResponse object
This constructor will only assign default values to properties that have it defined,
but it doesn't guarantee that properties required by API are set

### GetContent

`func (o *PageTenantOverviewResponse) GetContent() []TenantOverviewResponse`

GetContent returns the Content field if non-nil, zero value otherwise.

### GetContentOk

`func (o *PageTenantOverviewResponse) GetContentOk() (*[]TenantOverviewResponse, bool)`

GetContentOk returns a tuple with the Content field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetContent

`func (o *PageTenantOverviewResponse) SetContent(v []TenantOverviewResponse)`

SetContent sets Content field to given value.

### HasContent

`func (o *PageTenantOverviewResponse) HasContent() bool`

HasContent returns a boolean if a field has been set.

### GetPageable

`func (o *PageTenantOverviewResponse) GetPageable() PageableObject`

GetPageable returns the Pageable field if non-nil, zero value otherwise.

### GetPageableOk

`func (o *PageTenantOverviewResponse) GetPageableOk() (*PageableObject, bool)`

GetPageableOk returns a tuple with the Pageable field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetPageable

`func (o *PageTenantOverviewResponse) SetPageable(v PageableObject)`

SetPageable sets Pageable field to given value.

### HasPageable

`func (o *PageTenantOverviewResponse) HasPageable() bool`

HasPageable returns a boolean if a field has been set.

### GetSort

`func (o *PageTenantOverviewResponse) GetSort() SortObject`

GetSort returns the Sort field if non-nil, zero value otherwise.

### GetSortOk

`func (o *PageTenantOverviewResponse) GetSortOk() (*SortObject, bool)`

GetSortOk returns a tuple with the Sort field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetSort

`func (o *PageTenantOverviewResponse) SetSort(v SortObject)`

SetSort sets Sort field to given value.

### HasSort

`func (o *PageTenantOverviewResponse) HasSort() bool`

HasSort returns a boolean if a field has been set.

### GetTotalElements

`func (o *PageTenantOverviewResponse) GetTotalElements() int64`

GetTotalElements returns the TotalElements field if non-nil, zero value otherwise.

### GetTotalElementsOk

`func (o *PageTenantOverviewResponse) GetTotalElementsOk() (*int64, bool)`

GetTotalElementsOk returns a tuple with the TotalElements field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetTotalElements

`func (o *PageTenantOverviewResponse) SetTotalElements(v int64)`

SetTotalElements sets TotalElements field to given value.

### HasTotalElements

`func (o *PageTenantOverviewResponse) HasTotalElements() bool`

HasTotalElements returns a boolean if a field has been set.

### GetTotalPages

`func (o *PageTenantOverviewResponse) GetTotalPages() int32`

GetTotalPages returns the TotalPages field if non-nil, zero value otherwise.

### GetTotalPagesOk

`func (o *PageTenantOverviewResponse) GetTotalPagesOk() (*int32, bool)`

GetTotalPagesOk returns a tuple with the TotalPages field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetTotalPages

`func (o *PageTenantOverviewResponse) SetTotalPages(v int32)`

SetTotalPages sets TotalPages field to given value.

### HasTotalPages

`func (o *PageTenantOverviewResponse) HasTotalPages() bool`

HasTotalPages returns a boolean if a field has been set.

### GetNumber

`func (o *PageTenantOverviewResponse) GetNumber() int32`

GetNumber returns the Number field if non-nil, zero value otherwise.

### GetNumberOk

`func (o *PageTenantOverviewResponse) GetNumberOk() (*int32, bool)`

GetNumberOk returns a tuple with the Number field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetNumber

`func (o *PageTenantOverviewResponse) SetNumber(v int32)`

SetNumber sets Number field to given value.

### HasNumber

`func (o *PageTenantOverviewResponse) HasNumber() bool`

HasNumber returns a boolean if a field has been set.

### GetSize

`func (o *PageTenantOverviewResponse) GetSize() int32`

GetSize returns the Size field if non-nil, zero value otherwise.

### GetSizeOk

`func (o *PageTenantOverviewResponse) GetSizeOk() (*int32, bool)`

GetSizeOk returns a tuple with the Size field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetSize

`func (o *PageTenantOverviewResponse) SetSize(v int32)`

SetSize sets Size field to given value.

### HasSize

`func (o *PageTenantOverviewResponse) HasSize() bool`

HasSize returns a boolean if a field has been set.

### GetNumberOfElements

`func (o *PageTenantOverviewResponse) GetNumberOfElements() int32`

GetNumberOfElements returns the NumberOfElements field if non-nil, zero value otherwise.

### GetNumberOfElementsOk

`func (o *PageTenantOverviewResponse) GetNumberOfElementsOk() (*int32, bool)`

GetNumberOfElementsOk returns a tuple with the NumberOfElements field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetNumberOfElements

`func (o *PageTenantOverviewResponse) SetNumberOfElements(v int32)`

SetNumberOfElements sets NumberOfElements field to given value.

### HasNumberOfElements

`func (o *PageTenantOverviewResponse) HasNumberOfElements() bool`

HasNumberOfElements returns a boolean if a field has been set.

### GetFirst

`func (o *PageTenantOverviewResponse) GetFirst() bool`

GetFirst returns the First field if non-nil, zero value otherwise.

### GetFirstOk

`func (o *PageTenantOverviewResponse) GetFirstOk() (*bool, bool)`

GetFirstOk returns a tuple with the First field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetFirst

`func (o *PageTenantOverviewResponse) SetFirst(v bool)`

SetFirst sets First field to given value.

### HasFirst

`func (o *PageTenantOverviewResponse) HasFirst() bool`

HasFirst returns a boolean if a field has been set.

### GetLast

`func (o *PageTenantOverviewResponse) GetLast() bool`

GetLast returns the Last field if non-nil, zero value otherwise.

### GetLastOk

`func (o *PageTenantOverviewResponse) GetLastOk() (*bool, bool)`

GetLastOk returns a tuple with the Last field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetLast

`func (o *PageTenantOverviewResponse) SetLast(v bool)`

SetLast sets Last field to given value.

### HasLast

`func (o *PageTenantOverviewResponse) HasLast() bool`

HasLast returns a boolean if a field has been set.

### GetEmpty

`func (o *PageTenantOverviewResponse) GetEmpty() bool`

GetEmpty returns the Empty field if non-nil, zero value otherwise.

### GetEmptyOk

`func (o *PageTenantOverviewResponse) GetEmptyOk() (*bool, bool)`

GetEmptyOk returns a tuple with the Empty field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetEmpty

`func (o *PageTenantOverviewResponse) SetEmpty(v bool)`

SetEmpty sets Empty field to given value.

### HasEmpty

`func (o *PageTenantOverviewResponse) HasEmpty() bool`

HasEmpty returns a boolean if a field has been set.


[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


