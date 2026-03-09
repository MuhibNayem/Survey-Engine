
# TenantOverviewResponse


## Properties

Name | Type
------------ | -------------
`tenantId` | string
`name` | string
`primaryEmail` | string
`plan` | string
`subscriptionStatus` | string
`active` | boolean
`createdAt` | Date

## Example

```typescript
import type { TenantOverviewResponse } from '@survey-engine/sdk'

// TODO: Update the object below with actual values
const example = {
  "tenantId": null,
  "name": null,
  "primaryEmail": null,
  "plan": null,
  "subscriptionStatus": null,
  "active": null,
  "createdAt": null,
} satisfies TenantOverviewResponse

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as TenantOverviewResponse
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


