
# ResponseSubmissionRequest


## Properties

Name | Type
------------ | -------------
`campaignId` | string
`respondentIdentifier` | string
`respondentIp` | string
`respondentDeviceFingerprint` | string
`responderToken` | string
`responderAccessCode` | string
`answers` | [Array&lt;ResponseSubmissionRequestAnswersInner&gt;](ResponseSubmissionRequestAnswersInner.md)

## Example

```typescript
import type { ResponseSubmissionRequest } from '@survey-engine/sdk'

// TODO: Update the object below with actual values
const example = {
  "campaignId": null,
  "respondentIdentifier": null,
  "respondentIp": null,
  "respondentDeviceFingerprint": null,
  "responderToken": null,
  "responderAccessCode": null,
  "answers": null,
} satisfies ResponseSubmissionRequest

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as ResponseSubmissionRequest
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


