
# ScoreResult


## Properties

Name | Type
------------ | -------------
`campaignId` | string
`weightProfileId` | string
`totalScore` | number
`categoryScores` | [Array&lt;ScoreResultCategoryScoresInner&gt;](ScoreResultCategoryScoresInner.md)

## Example

```typescript
import type { ScoreResult } from '@survey-engine/sdk'

// TODO: Update the object below with actual values
const example = {
  "campaignId": null,
  "weightProfileId": null,
  "totalScore": null,
  "categoryScores": null,
} satisfies ScoreResult

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as ScoreResult
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


