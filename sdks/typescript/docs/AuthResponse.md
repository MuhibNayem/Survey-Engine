
# AuthResponse


## Properties

Name | Type
------------ | -------------
`userId` | string
`email` | string
`fullName` | string
`tenantId` | string
`role` | string
`accessToken` | string
`refreshToken` | string
`tokenType` | string
`expiresIn` | number

## Example

```typescript
import type { AuthResponse } from '@survey-engine/sdk'

// TODO: Update the object below with actual values
const example = {
  "userId": null,
  "email": null,
  "fullName": null,
  "tenantId": null,
  "role": null,
  "accessToken": null,
  "refreshToken": null,
  "tokenType": Bearer,
  "expiresIn": null,
} satisfies AuthResponse

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as AuthResponse
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


