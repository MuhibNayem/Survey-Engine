
# AuthConfigAudit


## Properties

Name | Type
------------ | -------------
`id` | string
`authProfileId` | string
`action` | string
`changedBy` | string
`beforeValue` | string
`afterValue` | string
`changedAt` | Date

## Example

```typescript
import type { AuthConfigAudit } from '@survey-engine/sdk'

// TODO: Update the object below with actual values
const example = {
  "id": null,
  "authProfileId": null,
  "action": null,
  "changedBy": null,
  "beforeValue": null,
  "afterValue": null,
  "changedAt": null,
} satisfies AuthConfigAudit

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as AuthConfigAudit
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


