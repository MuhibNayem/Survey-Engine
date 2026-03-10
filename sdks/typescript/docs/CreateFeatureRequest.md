
# CreateFeatureRequest


## Properties

Name | Type
------------ | -------------
`featureKey` | string
`featureType` | [FeatureType](FeatureType.md)
`category` | [FeatureCategory](FeatureCategory.md)
`name` | string
`description` | string
`enabled` | boolean
`rolloutPercentage` | number
`minPlan` | string
`roles` | Array&lt;string&gt;
`platforms` | Array&lt;string&gt;
`metadata` | { [key: string]: any; }

## Example

```typescript
import type { CreateFeatureRequest } from '@survey-engine/sdk'

// TODO: Update the object below with actual values
const example = {
  "featureKey": null,
  "featureType": null,
  "category": null,
  "name": null,
  "description": null,
  "enabled": null,
  "rolloutPercentage": null,
  "minPlan": null,
  "roles": null,
  "platforms": null,
  "metadata": null,
} satisfies CreateFeatureRequest

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as CreateFeatureRequest
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


