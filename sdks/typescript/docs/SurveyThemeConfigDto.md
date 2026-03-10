
# SurveyThemeConfigDto


## Properties

Name | Type
------------ | -------------
`templateKey` | string
`paletteKey` | string
`palette` | [SurveyThemePalette](SurveyThemePalette.md)
`branding` | [SurveyThemeBranding](SurveyThemeBranding.md)
`layout` | [SurveyThemeLayout](SurveyThemeLayout.md)
`motion` | [SurveyThemeMotion](SurveyThemeMotion.md)
`header` | [SurveyThemeHeader](SurveyThemeHeader.md)
`footer` | [SurveyThemeFooter](SurveyThemeFooter.md)
`advanced` | [SurveyThemeAdvanced](SurveyThemeAdvanced.md)

## Example

```typescript
import type { SurveyThemeConfigDto } from '@survey-engine/sdk'

// TODO: Update the object below with actual values
const example = {
  "templateKey": null,
  "paletteKey": null,
  "palette": null,
  "branding": null,
  "layout": null,
  "motion": null,
  "header": null,
  "footer": null,
  "advanced": null,
} satisfies SurveyThemeConfigDto

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as SurveyThemeConfigDto
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


