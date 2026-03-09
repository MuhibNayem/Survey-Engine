
# DistributionChannelResponse


## Properties

Name | Type
------------ | -------------
`id` | string
`campaignId` | string
`channelType` | string
`channelValue` | string
`createdAt` | Date

## Example

```typescript
import type { DistributionChannelResponse } from '@survey-engine/sdk'

// TODO: Update the object below with actual values
const example = {
  "id": null,
  "campaignId": null,
  "channelType": null,
  "channelValue": null,
  "createdAt": null,
} satisfies DistributionChannelResponse

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as DistributionChannelResponse
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


