
# CampaignAnalytics


## Properties

Name | Type
------------ | -------------
`totalResponses` | number
`submittedCount` | number
`inProgressCount` | number
`lockedCount` | number
`completionRate` | number

## Example

```typescript
import type { CampaignAnalytics } from '@survey-engine/sdk'

// TODO: Update the object below with actual values
const example = {
  "totalResponses": null,
  "submittedCount": null,
  "inProgressCount": null,
  "lockedCount": null,
  "completionRate": null,
} satisfies CampaignAnalytics

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as CampaignAnalytics
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


