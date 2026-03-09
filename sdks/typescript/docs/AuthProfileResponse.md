
# AuthProfileResponse


## Properties

Name | Type
------------ | -------------
`id` | string
`tenantId` | string
`authMode` | string
`issuer` | string
`audience` | string
`jwksEndpoint` | string
`oidcDiscoveryUrl` | string
`oidcClientId` | string
`oidcRedirectUri` | string
`oidcScopes` | string
`clockSkewSeconds` | number
`tokenTtlSeconds` | number
`activeKeyVersion` | number
`fallbackPolicy` | string
`claimMappings` | [Array&lt;AuthProfileResponseClaimMappingsInner&gt;](AuthProfileResponseClaimMappingsInner.md)

## Example

```typescript
import type { AuthProfileResponse } from '@survey-engine/sdk'

// TODO: Update the object below with actual values
const example = {
  "id": null,
  "tenantId": null,
  "authMode": null,
  "issuer": null,
  "audience": null,
  "jwksEndpoint": null,
  "oidcDiscoveryUrl": null,
  "oidcClientId": null,
  "oidcRedirectUri": null,
  "oidcScopes": null,
  "clockSkewSeconds": null,
  "tokenTtlSeconds": null,
  "activeKeyVersion": null,
  "fallbackPolicy": null,
  "claimMappings": null,
} satisfies AuthProfileResponse

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as AuthProfileResponse
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


