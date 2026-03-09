
# CategoryResponse


## Properties

Name | Type
------------ | -------------
`id` | string
`name` | string
`description` | string
`currentVersion` | number
`active` | boolean
`questionMappings` | [Array&lt;CategoryResponseQuestionMappingsInner&gt;](CategoryResponseQuestionMappingsInner.md)
`createdBy` | string
`createdAt` | Date
`updatedBy` | string
`updatedAt` | Date

## Example

```typescript
import type { CategoryResponse } from '@survey-engine/sdk'

// TODO: Update the object below with actual values
const example = {
  "id": null,
  "name": null,
  "description": null,
  "currentVersion": null,
  "active": null,
  "questionMappings": null,
  "createdBy": null,
  "createdAt": null,
  "updatedBy": null,
  "updatedAt": null,
} satisfies CategoryResponse

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as CategoryResponse
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


