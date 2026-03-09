
# QuestionRequest


## Properties

Name | Type
------------ | -------------
`text` | string
`type` | string
`maxScore` | number
`optionConfig` | string

## Example

```typescript
import type { QuestionRequest } from '@survey-engine/sdk'

// TODO: Update the object below with actual values
const example = {
  "text": null,
  "type": null,
  "maxScore": null,
  "optionConfig": null,
} satisfies QuestionRequest

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as QuestionRequest
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


