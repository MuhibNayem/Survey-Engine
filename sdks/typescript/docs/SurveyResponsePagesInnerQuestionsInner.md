
# SurveyResponsePagesInnerQuestionsInner


## Properties

Name | Type
------------ | -------------
`id` | string
`questionId` | string
`questionVersionId` | string
`categoryId` | string
`categoryVersionId` | string
`categoryWeightPercentage` | number
`sortOrder` | number
`mandatory` | boolean
`answerConfig` | string
`pinnedQuestionText` | string
`pinnedQuestionType` | string
`pinnedQuestionMaxScore` | number
`pinnedQuestionOptionConfig` | string
`pinnedCategoryName` | string
`pinnedCategoryDescription` | string

## Example

```typescript
import type { SurveyResponsePagesInnerQuestionsInner } from '@survey-engine/sdk'

// TODO: Update the object below with actual values
const example = {
  "id": null,
  "questionId": null,
  "questionVersionId": null,
  "categoryId": null,
  "categoryVersionId": null,
  "categoryWeightPercentage": null,
  "sortOrder": null,
  "mandatory": null,
  "answerConfig": null,
  "pinnedQuestionText": null,
  "pinnedQuestionType": null,
  "pinnedQuestionMaxScore": null,
  "pinnedQuestionOptionConfig": null,
  "pinnedCategoryName": null,
  "pinnedCategoryDescription": null,
} satisfies SurveyResponsePagesInnerQuestionsInner

console.log(example)

// Convert the instance to a JSON string
const exampleJSON: string = JSON.stringify(example)
console.log(exampleJSON)

// Parse the JSON string back to an object
const exampleParsed = JSON.parse(exampleJSON) as SurveyResponsePagesInnerQuestionsInner
console.log(exampleParsed)
```

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


