
# WeightProfileRequest


## Properties

Name | Type
------------ | -------------
`name` | string
`campaignId` | string
`categoryWeights` | [Array&lt;CategoryWeightRequest&gt;](CategoryWeightRequest.md)

## Example

```typescript
import type { WeightProfileRequest } from '@survey-engine/sdk'

// TODO: Update the object below with actual values
const example = {
  "name": null,
  "campaignId": null,
  "categoryWeights": null,
} satisfies WeightProfileRequest

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as WeightProfileRequest
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


