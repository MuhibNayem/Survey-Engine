
# QuestionResponse


## Properties

Name | Type
------------ | -------------
`id` | string
`text` | string
`type` | string
`maxScore` | number
`optionConfig` | string
`currentVersion` | number
`active` | boolean
`createdBy` | string
`createdAt` | Date
`updatedBy` | string
`updatedAt` | Date

## Example

```typescript
import type { QuestionResponse } from '@survey-engine/sdk'

// TODO: Update the object below with actual values
const example = {
  "id": null,
  "text": null,
  "type": null,
  "maxScore": null,
  "optionConfig": null,
  "currentVersion": null,
  "active": null,
  "createdBy": null,
  "createdAt": null,
  "updatedBy": null,
  "updatedAt": null,
} satisfies QuestionResponse

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as QuestionResponse
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


