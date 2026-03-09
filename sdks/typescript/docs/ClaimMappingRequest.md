
# ClaimMappingRequest


## Properties

Name | Type
------------ | -------------
`externalClaim` | string
`internalField` | string
`required` | boolean

## Example

```typescript
import type { ClaimMappingRequest } from '@survey-engine/sdk'

// TODO: Update the object below with actual values
const example = {
  "externalClaim": null,
  "internalField": null,
  "required": null,
} satisfies ClaimMappingRequest

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as ClaimMappingRequest
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


