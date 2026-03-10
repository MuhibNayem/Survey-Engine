
# CampaignPreviewResponsePagesInner


## Properties

Name | Type
------------ | -------------
`id` | string
`title` | string
`sortOrder` | number
`categories` | [Array&lt;CampaignPreviewResponsePagesInnerCategoriesInner&gt;](CampaignPreviewResponsePagesInnerCategoriesInner.md)
`questions` | [Array&lt;CampaignPreviewResponsePagesInnerCategoriesInnerQuestionsInner&gt;](CampaignPreviewResponsePagesInnerCategoriesInnerQuestionsInner.md)

## Example

```typescript
import type { CampaignPreviewResponsePagesInner } from '@survey-engine/sdk'

// TODO: Update the object below with actual values
const example = {
  "id": null,
  "title": null,
  "sortOrder": null,
  "categories": null,
  "questions": null,
} satisfies CampaignPreviewResponsePagesInner

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as CampaignPreviewResponsePagesInner
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


