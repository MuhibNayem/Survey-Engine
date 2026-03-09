
# AuthProfileRequest


## Properties

Name | Type
------------ | -------------
`tenantId` | string
`authMode` | string
`issuer` | string
`audience` | string
`jwksEndpoint` | string
`oidcDiscoveryUrl` | string
`oidcClientId` | string
`oidcClientSecret` | string
`oidcRedirectUri` | string
`oidcScopes` | string
`clockSkewSeconds` | number
`tokenTtlSeconds` | number
`signingSecret` | string
`fallbackPolicy` | string
`claimMappings` | [Array&lt;ClaimMappingRequest&gt;](ClaimMappingRequest.md)

## Example

```typescript
import type { AuthProfileRequest } from '@survey-engine/sdk'

// TODO: Update the object below with actual values
const example = {
  "tenantId": null,
  "authMode": null,
  "issuer": null,
  "audience": null,
  "jwksEndpoint": null,
  "oidcDiscoveryUrl": null,
  "oidcClientId": null,
  "oidcClientSecret": null,
  "oidcRedirectUri": null,
  "oidcScopes": null,
  "clockSkewSeconds": null,
  "tokenTtlSeconds": null,
  "signingSecret": null,
  "fallbackPolicy": null,
  "claimMappings": null,
} satisfies AuthProfileRequest

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as AuthProfileRequest
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


