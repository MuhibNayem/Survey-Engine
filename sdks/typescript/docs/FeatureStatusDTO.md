
# FeatureStatusDTO


## Properties

Name | Type
------------ | -------------
`featureKey` | string
`available` | boolean
`completed` | boolean
`accessed` | boolean
`accessCount` | number
`lastAccessedAt` | Date
`completedAt` | Date

## Example

```typescript
import type { FeatureStatusDTO } from '@survey-engine/sdk'

// TODO: Update the object below with actual values
const example = {
  "featureKey": null,
  "available": null,
  "completed": null,
  "accessed": null,
  "accessCount": null,
  "lastAccessedAt": null,
  "completedAt": null,
} satisfies FeatureStatusDTO

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as FeatureStatusDTO
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


