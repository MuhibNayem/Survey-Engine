
# PlanDefinitionRequest


## Properties

Name | Type
------------ | -------------
`planCode` | string
`displayName` | string
`price` | number
`currency` | string
`billingCycleDays` | number
`trialDays` | number
`maxCampaigns` | number
`maxResponsesPerCampaign` | number
`maxAdminUsers` | number
`active` | boolean

## Example

```typescript
import type { PlanDefinitionRequest } from '@survey-engine/sdk'

// TODO: Update the object below with actual values
const example = {
  "planCode": null,
  "displayName": null,
  "price": null,
  "currency": null,
  "billingCycleDays": null,
  "trialDays": null,
  "maxCampaigns": null,
  "maxResponsesPerCampaign": null,
  "maxAdminUsers": null,
  "active": null,
} satisfies PlanDefinitionRequest

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as PlanDefinitionRequest
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


