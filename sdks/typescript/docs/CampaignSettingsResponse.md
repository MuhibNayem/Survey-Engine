
# CampaignSettingsResponse


## Properties

Name | Type
------------ | -------------
`campaignId` | string
`password` | string
`captchaEnabled` | boolean
`oneResponsePerDevice` | boolean
`ipRestrictionEnabled` | boolean
`emailRestrictionEnabled` | boolean
`responseQuota` | number
`closeDate` | Date
`sessionTimeoutMinutes` | number
`showQuestionNumbers` | boolean
`showProgressIndicator` | boolean
`allowBackButton` | boolean
`startMessage` | string
`finishMessage` | string
`headerHtml` | string
`footerHtml` | string
`theme` | [SurveyThemeConfigDto](SurveyThemeConfigDto.md)
`collectName` | boolean
`collectEmail` | boolean
`collectPhone` | boolean
`collectAddress` | boolean
`dataCollectionFields` | [Array&lt;DataCollectionFieldResponse&gt;](DataCollectionFieldResponse.md)

## Example

```typescript
import type { CampaignSettingsResponse } from '@survey-engine/sdk'

// TODO: Update the object below with actual values
const example = {
  "campaignId": null,
  "password": null,
  "captchaEnabled": null,
  "oneResponsePerDevice": null,
  "ipRestrictionEnabled": null,
  "emailRestrictionEnabled": null,
  "responseQuota": null,
  "closeDate": null,
  "sessionTimeoutMinutes": null,
  "showQuestionNumbers": null,
  "showProgressIndicator": null,
  "allowBackButton": null,
  "startMessage": null,
  "finishMessage": null,
  "headerHtml": null,
  "footerHtml": null,
  "theme": null,
  "collectName": null,
  "collectEmail": null,
  "collectPhone": null,
  "collectAddress": null,
  "dataCollectionFields": null,
} satisfies CampaignSettingsResponse

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as CampaignSettingsResponse
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


