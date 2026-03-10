
# CampaignResponse


## Properties

Name | Type
------------ | -------------
`id` | string
`name` | string
`description` | string
`surveyId` | string
`surveySnapshotId` | string
`defaultWeightProfileId` | string
`authMode` | string
`status` | string
`active` | boolean
`createdBy` | string
`createdAt` | Date
`updatedBy` | string
`updatedAt` | Date
`dataCollectionFields` | [Array&lt;DataCollectionFieldResponse&gt;](DataCollectionFieldResponse.md)

## Example

```typescript
import type { CampaignResponse } from '@survey-engine/sdk'

// TODO: Update the object below with actual values
const example = {
  "id": null,
  "name": null,
  "description": null,
  "surveyId": null,
  "surveySnapshotId": null,
  "defaultWeightProfileId": null,
  "authMode": null,
  "status": null,
  "active": null,
  "createdBy": null,
  "createdAt": null,
  "updatedBy": null,
  "updatedAt": null,
  "dataCollectionFields": null,
} satisfies CampaignResponse

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as CampaignResponse
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


