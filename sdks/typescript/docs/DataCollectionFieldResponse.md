
# DataCollectionFieldResponse


## Properties

Name | Type
------------ | -------------
`id` | string
`fieldKey` | string
`label` | string
`fieldType` | string
`required` | boolean
`sortOrder` | number
`enabled` | boolean

## Example

```typescript
import type { DataCollectionFieldResponse } from '@survey-engine/sdk'

// TODO: Update the object below with actual values
const example = {
  "id": null,
  "fieldKey": null,
  "label": null,
  "fieldType": null,
  "required": null,
  "sortOrder": null,
  "enabled": null,
} satisfies DataCollectionFieldResponse

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as DataCollectionFieldResponse
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


