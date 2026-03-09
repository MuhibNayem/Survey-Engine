
# CategoryResponseQuestionMappingsInner


## Properties

Name | Type
------------ | -------------
`questionId` | string
`questionVersionId` | string
`questionText` | string
`sortOrder` | number
`weight` | number

## Example

```typescript
import type { CategoryResponseQuestionMappingsInner } from '@survey-engine/sdk'

// TODO: Update the object below with actual values
const example = {
  "questionId": null,
  "questionVersionId": null,
  "questionText": null,
  "sortOrder": null,
  "weight": null,
} satisfies CategoryResponseQuestionMappingsInner

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as CategoryResponseQuestionMappingsInner
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


