
# SubscriptionResponse


## Properties

Name | Type
------------ | -------------
`id` | string
`tenantId` | string
`plan` | string
`status` | string
`currentPeriodStart` | Date
`currentPeriodEnd` | Date
`lastPaymentGatewayReference` | string
`planPrice` | number
`currency` | string
`maxCampaigns` | number
`maxResponsesPerCampaign` | number
`maxAdminUsers` | number

## Example

```typescript
import type { SubscriptionResponse } from '@survey-engine/sdk'

// TODO: Update the object below with actual values
const example = {
  "id": null,
  "tenantId": null,
  "plan": null,
  "status": null,
  "currentPeriodStart": null,
  "currentPeriodEnd": null,
  "lastPaymentGatewayReference": null,
  "planPrice": null,
  "currency": null,
  "maxCampaigns": null,
  "maxResponsesPerCampaign": null,
  "maxAdminUsers": null,
} satisfies SubscriptionResponse

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as SubscriptionResponse
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


