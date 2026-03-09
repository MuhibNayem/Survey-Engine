# PageableObject

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**Offset** | Pointer to **int64** |  | [optional] 
**PageNumber** | Pointer to **int32** |  | [optional] 
**PageSize** | Pointer to **int32** |  | [optional] 
**Paged** | Pointer to **bool** |  | [optional] 
**Unpaged** | Pointer to **bool** |  | [optional] 
**Sort** | Pointer to [**SortObject**](SortObject.md) |  | [optional] 

## Methods

### NewPageableObject

`func NewPageableObject() *PageableObject`

NewPageableObject instantiates a new PageableObject object
This constructor will assign default values to properties that have it defined,
and makes sure properties required by API are set, but the set of arguments
will change when the set of required properties is changed

### NewPageableObjectWithDefaults

`func NewPageableObjectWithDefaults() *PageableObject`

NewPageableObjectWithDefaults instantiates a new PageableObject object
This constructor will only assign default values to properties that have it defined,
but it doesn't guarantee that properties required by API are set

### GetOffset

`func (o *PageableObject) GetOffset() int64`

GetOffset returns the Offset field if non-nil, zero value otherwise.

### GetOffsetOk

`func (o *PageableObject) GetOffsetOk() (*int64, bool)`

GetOffsetOk returns a tuple with the Offset field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetOffset

`func (o *PageableObject) SetOffset(v int64)`

SetOffset sets Offset field to given value.

### HasOffset

`func (o *PageableObject) HasOffset() bool`

HasOffset returns a boolean if a field has been set.

### GetPageNumber

`func (o *PageableObject) GetPageNumber() int32`

GetPageNumber returns the PageNumber field if non-nil, zero value otherwise.

### GetPageNumberOk

`func (o *PageableObject) GetPageNumberOk() (*int32, bool)`

GetPageNumberOk returns a tuple with the PageNumber field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetPageNumber

`func (o *PageableObject) SetPageNumber(v int32)`

SetPageNumber sets PageNumber field to given value.

### HasPageNumber

`func (o *PageableObject) HasPageNumber() bool`

HasPageNumber returns a boolean if a field has been set.

### GetPageSize

`func (o *PageableObject) GetPageSize() int32`

GetPageSize returns the PageSize field if non-nil, zero value otherwise.

### GetPageSizeOk

`func (o *PageableObject) GetPageSizeOk() (*int32, bool)`

GetPageSizeOk returns a tuple with the PageSize field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetPageSize

`func (o *PageableObject) SetPageSize(v int32)`

SetPageSize sets PageSize field to given value.

### HasPageSize

`func (o *PageableObject) HasPageSize() bool`

HasPageSize returns a boolean if a field has been set.

### GetPaged

`func (o *PageableObject) GetPaged() bool`

GetPaged returns the Paged field if non-nil, zero value otherwise.

### GetPagedOk

`func (o *PageableObject) GetPagedOk() (*bool, bool)`

GetPagedOk returns a tuple with the Paged field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetPaged

`func (o *PageableObject) SetPaged(v bool)`

SetPaged sets Paged field to given value.

### HasPaged

`func (o *PageableObject) HasPaged() bool`

HasPaged returns a boolean if a field has been set.

### GetUnpaged

`func (o *PageableObject) GetUnpaged() bool`

GetUnpaged returns the Unpaged field if non-nil, zero value otherwise.

### GetUnpagedOk

`func (o *PageableObject) GetUnpagedOk() (*bool, bool)`

GetUnpagedOk returns a tuple with the Unpaged field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetUnpaged

`func (o *PageableObject) SetUnpaged(v bool)`

SetUnpaged sets Unpaged field to given value.

### HasUnpaged

`func (o *PageableObject) HasUnpaged() bool`

HasUnpaged returns a boolean if a field has been set.

### GetSort

`func (o *PageableObject) GetSort() SortObject`

GetSort returns the Sort field if non-nil, zero value otherwise.

### GetSortOk

`func (o *PageableObject) GetSortOk() (*SortObject, bool)`

GetSortOk returns a tuple with the Sort field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetSort

`func (o *PageableObject) SetSort(v SortObject)`

SetSort sets Sort field to given value.

### HasSort

`func (o *PageableObject) HasSort() bool`

HasSort returns a boolean if a field has been set.


[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


