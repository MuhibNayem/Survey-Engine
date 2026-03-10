# SurveyThemeConfigDto

## Properties

Name | Type | Description | Notes
------------ | ------------- | ------------- | -------------
**TemplateKey** | Pointer to **string** |  | [optional] 
**PaletteKey** | Pointer to **string** |  | [optional] 
**Palette** | Pointer to [**SurveyThemePalette**](SurveyThemePalette.md) |  | [optional] 
**Branding** | Pointer to [**SurveyThemeBranding**](SurveyThemeBranding.md) |  | [optional] 
**Layout** | Pointer to [**SurveyThemeLayout**](SurveyThemeLayout.md) |  | [optional] 
**Motion** | Pointer to [**SurveyThemeMotion**](SurveyThemeMotion.md) |  | [optional] 
**Header** | Pointer to [**SurveyThemeHeader**](SurveyThemeHeader.md) |  | [optional] 
**Footer** | Pointer to [**SurveyThemeFooter**](SurveyThemeFooter.md) |  | [optional] 
**Advanced** | Pointer to [**SurveyThemeAdvanced**](SurveyThemeAdvanced.md) |  | [optional] 

## Methods

### NewSurveyThemeConfigDto

`func NewSurveyThemeConfigDto() *SurveyThemeConfigDto`

NewSurveyThemeConfigDto instantiates a new SurveyThemeConfigDto object
This constructor will assign default values to properties that have it defined,
and makes sure properties required by API are set, but the set of arguments
will change when the set of required properties is changed

### NewSurveyThemeConfigDtoWithDefaults

`func NewSurveyThemeConfigDtoWithDefaults() *SurveyThemeConfigDto`

NewSurveyThemeConfigDtoWithDefaults instantiates a new SurveyThemeConfigDto object
This constructor will only assign default values to properties that have it defined,
but it doesn't guarantee that properties required by API are set

### GetTemplateKey

`func (o *SurveyThemeConfigDto) GetTemplateKey() string`

GetTemplateKey returns the TemplateKey field if non-nil, zero value otherwise.

### GetTemplateKeyOk

`func (o *SurveyThemeConfigDto) GetTemplateKeyOk() (*string, bool)`

GetTemplateKeyOk returns a tuple with the TemplateKey field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetTemplateKey

`func (o *SurveyThemeConfigDto) SetTemplateKey(v string)`

SetTemplateKey sets TemplateKey field to given value.

### HasTemplateKey

`func (o *SurveyThemeConfigDto) HasTemplateKey() bool`

HasTemplateKey returns a boolean if a field has been set.

### GetPaletteKey

`func (o *SurveyThemeConfigDto) GetPaletteKey() string`

GetPaletteKey returns the PaletteKey field if non-nil, zero value otherwise.

### GetPaletteKeyOk

`func (o *SurveyThemeConfigDto) GetPaletteKeyOk() (*string, bool)`

GetPaletteKeyOk returns a tuple with the PaletteKey field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetPaletteKey

`func (o *SurveyThemeConfigDto) SetPaletteKey(v string)`

SetPaletteKey sets PaletteKey field to given value.

### HasPaletteKey

`func (o *SurveyThemeConfigDto) HasPaletteKey() bool`

HasPaletteKey returns a boolean if a field has been set.

### GetPalette

`func (o *SurveyThemeConfigDto) GetPalette() SurveyThemePalette`

GetPalette returns the Palette field if non-nil, zero value otherwise.

### GetPaletteOk

`func (o *SurveyThemeConfigDto) GetPaletteOk() (*SurveyThemePalette, bool)`

GetPaletteOk returns a tuple with the Palette field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetPalette

`func (o *SurveyThemeConfigDto) SetPalette(v SurveyThemePalette)`

SetPalette sets Palette field to given value.

### HasPalette

`func (o *SurveyThemeConfigDto) HasPalette() bool`

HasPalette returns a boolean if a field has been set.

### GetBranding

`func (o *SurveyThemeConfigDto) GetBranding() SurveyThemeBranding`

GetBranding returns the Branding field if non-nil, zero value otherwise.

### GetBrandingOk

`func (o *SurveyThemeConfigDto) GetBrandingOk() (*SurveyThemeBranding, bool)`

GetBrandingOk returns a tuple with the Branding field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetBranding

`func (o *SurveyThemeConfigDto) SetBranding(v SurveyThemeBranding)`

SetBranding sets Branding field to given value.

### HasBranding

`func (o *SurveyThemeConfigDto) HasBranding() bool`

HasBranding returns a boolean if a field has been set.

### GetLayout

`func (o *SurveyThemeConfigDto) GetLayout() SurveyThemeLayout`

GetLayout returns the Layout field if non-nil, zero value otherwise.

### GetLayoutOk

`func (o *SurveyThemeConfigDto) GetLayoutOk() (*SurveyThemeLayout, bool)`

GetLayoutOk returns a tuple with the Layout field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetLayout

`func (o *SurveyThemeConfigDto) SetLayout(v SurveyThemeLayout)`

SetLayout sets Layout field to given value.

### HasLayout

`func (o *SurveyThemeConfigDto) HasLayout() bool`

HasLayout returns a boolean if a field has been set.

### GetMotion

`func (o *SurveyThemeConfigDto) GetMotion() SurveyThemeMotion`

GetMotion returns the Motion field if non-nil, zero value otherwise.

### GetMotionOk

`func (o *SurveyThemeConfigDto) GetMotionOk() (*SurveyThemeMotion, bool)`

GetMotionOk returns a tuple with the Motion field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetMotion

`func (o *SurveyThemeConfigDto) SetMotion(v SurveyThemeMotion)`

SetMotion sets Motion field to given value.

### HasMotion

`func (o *SurveyThemeConfigDto) HasMotion() bool`

HasMotion returns a boolean if a field has been set.

### GetHeader

`func (o *SurveyThemeConfigDto) GetHeader() SurveyThemeHeader`

GetHeader returns the Header field if non-nil, zero value otherwise.

### GetHeaderOk

`func (o *SurveyThemeConfigDto) GetHeaderOk() (*SurveyThemeHeader, bool)`

GetHeaderOk returns a tuple with the Header field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetHeader

`func (o *SurveyThemeConfigDto) SetHeader(v SurveyThemeHeader)`

SetHeader sets Header field to given value.

### HasHeader

`func (o *SurveyThemeConfigDto) HasHeader() bool`

HasHeader returns a boolean if a field has been set.

### GetFooter

`func (o *SurveyThemeConfigDto) GetFooter() SurveyThemeFooter`

GetFooter returns the Footer field if non-nil, zero value otherwise.

### GetFooterOk

`func (o *SurveyThemeConfigDto) GetFooterOk() (*SurveyThemeFooter, bool)`

GetFooterOk returns a tuple with the Footer field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetFooter

`func (o *SurveyThemeConfigDto) SetFooter(v SurveyThemeFooter)`

SetFooter sets Footer field to given value.

### HasFooter

`func (o *SurveyThemeConfigDto) HasFooter() bool`

HasFooter returns a boolean if a field has been set.

### GetAdvanced

`func (o *SurveyThemeConfigDto) GetAdvanced() SurveyThemeAdvanced`

GetAdvanced returns the Advanced field if non-nil, zero value otherwise.

### GetAdvancedOk

`func (o *SurveyThemeConfigDto) GetAdvancedOk() (*SurveyThemeAdvanced, bool)`

GetAdvancedOk returns a tuple with the Advanced field if it's non-nil, zero value otherwise
and a boolean to check if the value has been set.

### SetAdvanced

`func (o *SurveyThemeConfigDto) SetAdvanced(v SurveyThemeAdvanced)`

SetAdvanced sets Advanced field to given value.

### HasAdvanced

`func (o *SurveyThemeConfigDto) HasAdvanced() bool`

HasAdvanced returns a boolean if a field has been set.


[[Back to Model list]](../README.md#documentation-for-models) [[Back to API list]](../README.md#documentation-for-api-endpoints) [[Back to README]](../README.md)


