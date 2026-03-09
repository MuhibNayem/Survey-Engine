
# CategoryRequest


## Properties

Name | Type
------------ | -------------
`name` | string
`description` | string
`questionMappings` | [Array&lt;CategoryQuestionMappingRequest&gt;](CategoryQuestionMappingRequest.md)

## Example

```typescript
import type { CategoryRequest } from '@survey-engine/sdk'

// TODO: Update the object below with actual values
const example = {
  "name": null,
  "description": null,
  "questionMappings": null,
} satisfies CategoryRequest

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as CategoryRequest
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


