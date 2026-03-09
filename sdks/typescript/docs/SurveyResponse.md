
# SurveyResponse


## Properties

Name | Type
------------ | -------------
`id` | string
`title` | string
`description` | string
`lifecycleState` | string
`currentVersion` | number
`active` | boolean
`pages` | [Array&lt;SurveyResponsePagesInner&gt;](SurveyResponsePagesInner.md)
`createdBy` | string
`createdAt` | Date
`updatedBy` | string
`updatedAt` | Date

## Example

```typescript
import type { SurveyResponse } from '@survey-engine/sdk'

// TODO: Update the object below with actual values
const example = {
  "id": null,
  "title": null,
  "description": null,
  "lifecycleState": null,
  "currentVersion": null,
  "active": null,
  "pages": null,
  "createdBy": null,
  "createdAt": null,
  "updatedBy": null,
  "updatedAt": null,
} satisfies SurveyResponse

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as SurveyResponse
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


