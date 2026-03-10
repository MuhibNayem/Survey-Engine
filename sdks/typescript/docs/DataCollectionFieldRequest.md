
# DataCollectionFieldRequest


## Properties

Name | Type
------------ | -------------
`fieldKey` | string
`label` | string
`fieldType` | string
`required` | boolean
`sortOrder` | number
`enabled` | boolean

## Example

```typescript
import type { DataCollectionFieldRequest } from '@survey-engine/sdk'

// TODO: Update the object below with actual values
const example = {
  "fieldKey": null,
  "label": null,
  "fieldType": null,
  "required": null,
  "sortOrder": null,
  "enabled": null,
} satisfies DataCollectionFieldRequest

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as DataCollectionFieldRequest
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


