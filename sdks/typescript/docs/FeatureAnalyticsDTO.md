
# FeatureAnalyticsDTO


## Properties

Name | Type
------------ | -------------
`featureKey` | string
`totalUsers` | number
`accessedCount` | number
`completedCount` | number
`completionRate` | number
`averageAccessCount` | number
`lastAccessedAt` | Date

## Example

```typescript
import type { FeatureAnalyticsDTO } from '@survey-engine/sdk'

// TODO: Update the object below with actual values
const example = {
  "featureKey": null,
  "totalUsers": null,
  "accessedCount": null,
  "completedCount": null,
  "completionRate": null,
  "averageAccessCount": null,
  "lastAccessedAt": null,
} satisfies FeatureAnalyticsDTO

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as FeatureAnalyticsDTO
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


