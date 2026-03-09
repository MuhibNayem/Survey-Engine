
# CampaignPreviewResponse


## Properties

Name | Type
------------ | -------------
`campaignId` | string
`tenantId` | string
`campaignName` | string
`campaignStatus` | string
`authMode` | string
`surveyId` | string
`surveyTitle` | string
`surveyDescription` | string
`showQuestionNumbers` | boolean
`showProgressIndicator` | boolean
`allowBackButton` | boolean
`startMessage` | string
`finishMessage` | string
`headerHtml` | string
`footerHtml` | string
`collectName` | boolean
`collectEmail` | boolean
`collectPhone` | boolean
`collectAddress` | boolean
`pages` | [Array&lt;CampaignPreviewResponsePagesInner&gt;](CampaignPreviewResponsePagesInner.md)

## Example

```typescript
import type { CampaignPreviewResponse } from '@survey-engine/sdk'

// TODO: Update the object below with actual values
const example = {
  "campaignId": null,
  "tenantId": null,
  "campaignName": null,
  "campaignStatus": null,
  "authMode": null,
  "surveyId": null,
  "surveyTitle": null,
  "surveyDescription": null,
  "showQuestionNumbers": null,
  "showProgressIndicator": null,
  "allowBackButton": null,
  "startMessage": null,
  "finishMessage": null,
  "headerHtml": null,
  "footerHtml": null,
  "collectName": null,
  "collectEmail": null,
  "collectPhone": null,
  "collectAddress": null,
  "pages": null,
} satisfies CampaignPreviewResponse

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as CampaignPreviewResponse
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


