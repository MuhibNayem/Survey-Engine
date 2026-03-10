
# CampaignPreviewResponsePagesInnerCategoriesInnerQuestionsInner


## Properties

Name | Type
------------ | -------------
`id` | string
`questionId` | string
`questionVersionId` | string
`categoryVersionId` | string
`categoryWeightPercentage` | number
`text` | string
`type` | string
`maxScore` | number
`mandatory` | boolean
`sortOrder` | number
`optionConfig` | string
`answerConfig` | string

## Example

```typescript
import type { CampaignPreviewResponsePagesInnerCategoriesInnerQuestionsInner } from '@survey-engine/sdk'

// TODO: Update the object below with actual values
const example = {
  "id": null,
  "questionId": null,
  "questionVersionId": null,
  "categoryVersionId": null,
  "categoryWeightPercentage": null,
  "text": null,
  "type": null,
  "maxScore": null,
  "mandatory": null,
  "sortOrder": null,
  "optionConfig": null,
  "answerConfig": null,
} satisfies CampaignPreviewResponsePagesInnerCategoriesInnerQuestionsInner

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as CampaignPreviewResponsePagesInnerCategoriesInnerQuestionsInner
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


