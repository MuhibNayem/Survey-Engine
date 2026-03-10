
# UserPreferenceDTO


## Properties

Name | Type
------------ | -------------
`preferences` | { [key: string]: string; }
`featureCompletion` | { [key: string]: boolean; }

## Example

```typescript
import type { UserPreferenceDTO } from '@survey-engine/sdk'

// TODO: Update the object below with actual values
const example = {
  "preferences": null,
  "featureCompletion": null,
} satisfies UserPreferenceDTO

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as UserPreferenceDTO
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


