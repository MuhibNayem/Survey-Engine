
# RegisterRequest


## Properties

Name | Type
------------ | -------------
`fullName` | string
`email` | string
`password` | string
`confirmPassword` | string
`tenantId` | string

## Example

```typescript
import type { RegisterRequest } from '@survey-engine/sdk'

// TODO: Update the object below with actual values
const example = {
  "fullName": null,
  "email": null,
  "password": null,
  "confirmPassword": null,
  "tenantId": null,
} satisfies RegisterRequest

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as RegisterRequest
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


