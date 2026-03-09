
# AuditLogResponse


## Properties

Name | Type
------------ | -------------
`id` | number
`tenantId` | string
`entityType` | string
`entityId` | string
`action` | string
`actor` | string
`reason` | string
`beforeValue` | string
`afterValue` | string
`ipAddress` | string
`createdAt` | Date

## Example

```typescript
import type { AuditLogResponse } from '@survey-engine/sdk'

// TODO: Update the object below with actual values
const example = {
  "id": null,
  "tenantId": null,
  "entityType": null,
  "entityId": null,
  "action": null,
  "actor": null,
  "reason": null,
  "beforeValue": null,
  "afterValue": null,
  "ipAddress": null,
  "createdAt": null,
} satisfies AuditLogResponse

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as AuditLogResponse
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


