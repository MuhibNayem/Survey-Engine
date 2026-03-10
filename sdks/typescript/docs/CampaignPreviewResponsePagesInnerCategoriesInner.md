
# CampaignPreviewResponsePagesInnerCategoriesInner


## Properties

Name | Type
------------ | -------------
`categoryVersionId` | string
`versionNumber` | number
`name` | string
`description` | string
`weightPercentage` | number
`sortOrder` | number
`questions` | [Array&lt;CampaignPreviewResponsePagesInnerCategoriesInnerQuestionsInner&gt;](CampaignPreviewResponsePagesInnerCategoriesInnerQuestionsInner.md)

## Example

```typescript
import type { CampaignPreviewResponsePagesInnerCategoriesInner } from '@survey-engine/sdk'

// TODO: Update the object below with actual values
const example = {
  "categoryVersionId": null,
  "versionNumber": null,
  "name": null,
  "description": null,
  "weightPercentage": null,
  "sortOrder": null,
  "questions": null,
} satisfies CampaignPreviewResponsePagesInnerCategoriesInner

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as CampaignPreviewResponsePagesInnerCategoriesInner
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


