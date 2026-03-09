
# PageTenantOverviewResponse


## Properties

Name | Type
------------ | -------------
`content` | [Array&lt;TenantOverviewResponse&gt;](TenantOverviewResponse.md)
`pageable` | [PageableObject](PageableObject.md)
`sort` | [SortObject](SortObject.md)
`totalElements` | number
`totalPages` | number
`number` | number
`size` | number
`numberOfElements` | number
`first` | boolean
`last` | boolean
`empty` | boolean

## Example

```typescript
import type { PageTenantOverviewResponse } from '@survey-engine/sdk'

// TODO: Update the object below with actual values
const example = {
  "content": null,
  "pageable": null,
  "sort": null,
  "totalElements": null,
  "totalPages": null,
  "number": null,
  "size": null,
  "numberOfElements": null,
  "first": null,
  "last": null,
  "empty": null,
} satisfies PageTenantOverviewResponse

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as PageTenantOverviewResponse
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


