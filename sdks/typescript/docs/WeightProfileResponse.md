
# WeightProfileResponse


## Properties

Name | Type
------------ | -------------
`id` | string
`name` | string
`campaignId` | string
`active` | boolean
`categoryWeights` | [Array&lt;WeightProfileResponseCategoryWeightsInner&gt;](WeightProfileResponseCategoryWeightsInner.md)
`createdBy` | string
`createdAt` | Date

## Example

```typescript
import type { WeightProfileResponse } from '@survey-engine/sdk'

// TODO: Update the object below with actual values
const example = {
  "id": null,
  "name": null,
  "campaignId": null,
  "active": null,
  "categoryWeights": null,
  "createdBy": null,
  "createdAt": null,
} satisfies WeightProfileResponse

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as WeightProfileResponse
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


