
# ResponseSubmissionRequestAnswersInner


## Properties

Name | Type
------------ | -------------
`questionId` | string
`questionVersionId` | string
`value` | string
`remark` | string
`score` | number

## Example

```typescript
import type { ResponseSubmissionRequestAnswersInner } from '@survey-engine/sdk'

// TODO: Update the object below with actual values
const example = {
  "questionId": null,
  "questionVersionId": null,
  "value": null,
  "remark": null,
  "score": null,
} satisfies ResponseSubmissionRequestAnswersInner

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as ResponseSubmissionRequestAnswersInner
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


