# AuditLogResponse

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**Id** | Pointer to **int64** |  | [optional] 
**TenantId** | Pointer to **string** |  | [optional] 
**EntityType** | Pointer to **string** |  | [optional] 
**EntityId** | Pointer to **string** |  | [optional] 
**Action** | Pointer to **string** |  | [optional] 
**Actor** | Pointer to **string** |  | [optional] 
**Reason** | Pointer to **string** |  | [optional] 
**BeforeValue** | Pointer to **string** |  | [optional] 
**AfterValue** | Pointer to **string** |  | [optional] 
**IpAddress** | Pointer to **string** |  | [optional] 
**CreatedAt** | Pointer to **time.Time** |  | [optional] 

## Methods

### NewAuditLogResponse

`func NewAuditLogResponse() *AuditLogResponse`

NewAuditLogResponse instantiates a new AuditLogResponse object
This constructor will assign default values to properties that have it defined,
and makes sure properties required by API are set, but the set of arguments
will change when the set of required properties is changed

### NewAuditLogResponseWithDefaults

`func NewAuditLogResponseWithDefaults() *AuditLogResponse`

NewAuditLogResponseWithDefaults instantiates a new AuditLogResponse object
This constructor will only assign default values to properties that have it defined,
but it doesn't guarantee that properties required by API are set

### GetId

`func (o *AuditLogResponse) GetId() int64`

GetId returns the Id field if non-nil, zero value otherwise.

### GetIdOk

`func (o *AuditLogResponse) GetIdOk() (*int64, bool)`

GetIdOk returns a tuple with the Id field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetId

`func (o *AuditLogResponse) SetId(v int64)`

SetId sets Id field to given value.

### HasId

`func (o *AuditLogResponse) HasId() bool`

HasId returns a boolean if a field has been set.

### GetTenantId

`func (o *AuditLogResponse) GetTenantId() string`

GetTenantId returns the TenantId field if non-nil, zero value otherwise.

### GetTenantIdOk

`func (o *AuditLogResponse) GetTenantIdOk() (*string, bool)`

GetTenantIdOk returns a tuple with the TenantId field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetTenantId

`func (o *AuditLogResponse) SetTenantId(v string)`

SetTenantId sets TenantId field to given value.

### HasTenantId

`func (o *AuditLogResponse) HasTenantId() bool`

HasTenantId returns a boolean if a field has been set.

### GetEntityType

`func (o *AuditLogResponse) GetEntityType() string`

GetEntityType returns the EntityType field if non-nil, zero value otherwise.

### GetEntityTypeOk

`func (o *AuditLogResponse) GetEntityTypeOk() (*string, bool)`

GetEntityTypeOk returns a tuple with the EntityType field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetEntityType

`func (o *AuditLogResponse) SetEntityType(v string)`

SetEntityType sets EntityType field to given value.

### HasEntityType

`func (o *AuditLogResponse) HasEntityType() bool`

HasEntityType returns a boolean if a field has been set.

### GetEntityId

`func (o *AuditLogResponse) GetEntityId() string`

GetEntityId returns the EntityId field if non-nil, zero value otherwise.

### GetEntityIdOk

`func (o *AuditLogResponse) GetEntityIdOk() (*string, bool)`

GetEntityIdOk returns a tuple with the EntityId field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetEntityId

`func (o *AuditLogResponse) SetEntityId(v string)`

SetEntityId sets EntityId field to given value.

### HasEntityId

`func (o *AuditLogResponse) HasEntityId() bool`

HasEntityId returns a boolean if a field has been set.

### GetAction

`func (o *AuditLogResponse) GetAction() string`

GetAction returns the Action field if non-nil, zero value otherwise.

### GetActionOk

`func (o *AuditLogResponse) GetActionOk() (*string, bool)`

GetActionOk returns a tuple with the Action field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetAction

`func (o *AuditLogResponse) SetAction(v string)`

SetAction sets Action field to given value.

### HasAction

`func (o *AuditLogResponse) HasAction() bool`

HasAction returns a boolean if a field has been set.

### GetActor

`func (o *AuditLogResponse) GetActor() string`

GetActor returns the Actor field if non-nil, zero value otherwise.

### GetActorOk

`func (o *AuditLogResponse) GetActorOk() (*string, bool)`

GetActorOk returns a tuple with the Actor field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetActor

`func (o *AuditLogResponse) SetActor(v string)`

SetActor sets Actor field to given value.

### HasActor

`func (o *AuditLogResponse) HasActor() bool`

HasActor returns a boolean if a field has been set.

### GetReason

`func (o *AuditLogResponse) GetReason() string`

GetReason returns the Reason field if non-nil, zero value otherwise.

### GetReasonOk

`func (o *AuditLogResponse) GetReasonOk() (*string, bool)`

GetReasonOk returns a tuple with the Reason field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetReason

`func (o *AuditLogResponse) SetReason(v string)`

SetReason sets Reason field to given value.

### HasReason

`func (o *AuditLogResponse) HasReason() bool`

HasReason returns a boolean if a field has been set.

### GetBeforeValue

`func (o *AuditLogResponse) GetBeforeValue() string`

GetBeforeValue returns the BeforeValue field if non-nil, zero value otherwise.

### GetBeforeValueOk

`func (o *AuditLogResponse) GetBeforeValueOk() (*string, bool)`

GetBeforeValueOk returns a tuple with the BeforeValue field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetBeforeValue

`func (o *AuditLogResponse) SetBeforeValue(v string)`

SetBeforeValue sets BeforeValue field to given value.

### HasBeforeValue

`func (o *AuditLogResponse) HasBeforeValue() bool`

HasBeforeValue returns a boolean if a field has been set.

### GetAfterValue

`func (o *AuditLogResponse) GetAfterValue() string`

GetAfterValue returns the AfterValue field if non-nil, zero value otherwise.

### GetAfterValueOk

`func (o *AuditLogResponse) GetAfterValueOk() (*string, bool)`

GetAfterValueOk returns a tuple with the AfterValue field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetAfterValue

`func (o *AuditLogResponse) SetAfterValue(v string)`

SetAfterValue sets AfterValue field to given value.

### HasAfterValue

`func (o *AuditLogResponse) HasAfterValue() bool`

HasAfterValue returns a boolean if a field has been set.

### GetIpAddress

`func (o *AuditLogResponse) GetIpAddress() string`

GetIpAddress returns the IpAddress field if non-nil, zero value otherwise.

### GetIpAddressOk

`func (o *AuditLogResponse) GetIpAddressOk() (*string, bool)`

GetIpAddressOk returns a tuple with the IpAddress field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetIpAddress

`func (o *AuditLogResponse) SetIpAddress(v string)`

SetIpAddress sets IpAddress field to given value.

### HasIpAddress

`func (o *AuditLogResponse) HasIpAddress() bool`

HasIpAddress returns a boolean if a field has been set.

### GetCreatedAt

`func (o *AuditLogResponse) GetCreatedAt() time.Time`

GetCreatedAt returns the CreatedAt field if non-nil, zero value otherwise.

### GetCreatedAtOk

`func (o *AuditLogResponse) GetCreatedAtOk() (*time.Time, bool)`

GetCreatedAtOk returns a tuple with the CreatedAt field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetCreatedAt

`func (o *AuditLogResponse) SetCreatedAt(v time.Time)`

SetCreatedAt sets CreatedAt field to given value.

### HasCreatedAt

`func (o *AuditLogResponse) HasCreatedAt() bool`

HasCreatedAt returns a boolean if a field has been set.


[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


