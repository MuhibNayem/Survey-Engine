
# CampaignRequest


## Properties

Name | Type
------------ | -------------
`name` | string
`description` | string
`surveyId` | string
`authMode` | string

## Example

```typescript
import type { CampaignRequest } from '@survey-engine/sdk'

// TODO: Update the object below with actual values
const example = {
  "name": null,
  "description": null,
  "surveyId": null,
  "authMode": null,
} satisfies CampaignRequest

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as CampaignRequest
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


