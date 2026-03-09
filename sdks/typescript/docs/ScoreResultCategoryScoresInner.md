
# ScoreResultCategoryScoresInner


## Properties

Name | Type
------------ | -------------
`categoryId` | string
`rawScore` | number
`maxScore` | number
`normalizedScore` | number
`weightPercentage` | number
`weightedScore` | number

## Example

```typescript
import type { ScoreResultCategoryScoresInner } from '@survey-engine/sdk'

// TODO: Update the object below with actual values
const example = {
  "categoryId": null,
  "rawScore": null,
  "maxScore": null,
  "normalizedScore": null,
  "weightPercentage": null,
  "weightedScore": null,
} satisfies ScoreResultCategoryScoresInner

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as ScoreResultCategoryScoresInner
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


