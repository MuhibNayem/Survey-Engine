
# SurveyPageRequest


## Properties

Name | Type
------------ | -------------
`title` | string
`sortOrder` | number
`questions` | [Array&lt;SurveyQuestionRequest&gt;](SurveyQuestionRequest.md)

## Example

```typescript
import type { SurveyPageRequest } from '@survey-engine/sdk'

// TODO: Update the object below with actual values
const example = {
  "title": null,
  "sortOrder": null,
  "questions": null,
} satisfies SurveyPageRequest

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as SurveyPageRequest
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


