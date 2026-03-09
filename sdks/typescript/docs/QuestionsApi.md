# QuestionsApi

All URIs are relative to *http://localhost*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**createQuestion**](QuestionsApi.md#createquestion) | **POST** /api/v1/questions | Create a reusable question |
| [**deactivateQuestion**](QuestionsApi.md#deactivatequestion) | **DELETE** /api/v1/questions/{id} | Deactivate a question |
| [**getQuestion**](QuestionsApi.md#getquestion) | **GET** /api/v1/questions/{id} | Get one question by id |
| [**listQuestions**](QuestionsApi.md#listquestions) | **GET** /api/v1/questions | List active questions for current tenant |
| [**updateQuestion**](QuestionsApi.md#updatequestion) | **PUT** /api/v1/questions/{id} | Update an existing question |



## createQuestion

> QuestionResponse createQuestion(questionRequest)

Create a reusable question

Why this endpoint is needed: The question bank is the foundation of reusable survey content. Teams need a way to add standardized question assets once and reuse them across many surveys.  What this endpoint does: It creates a question with text, type, and scoring metadata and stores version context used by downstream snapshots.  How this endpoint does it: The service validates required fields and max score constraints, applies tenant scoping from auth context, persists the question entity, and returns a QuestionResponse containing metadata and version pointers. 

### Example

```ts
import {
  Configuration,
  QuestionsApi,
} from '@survey-engine/sdk';
import type { CreateQuestionRequest } from '@survey-engine/sdk';

async function example() {
  console.log("🚀 Testing @survey-engine/sdk SDK...");
  const config = new Configuration({ 
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
  });
  const api = new QuestionsApi(config);

  const body = {
    // QuestionRequest
    questionRequest: ...,
  } satisfies CreateQuestionRequest;

  try {
    const data = await api.createQuestion(body);
    console.log(data);
  } catch (error) {
    console.error(error);
  }
}

// Run the test
example().catch(console.error);
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **questionRequest** | [QuestionRequest](QuestionRequest.md) |  | |

### Return type

[**QuestionResponse**](QuestionResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: `application/json`
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **201** | Question created |  -  |
| **400** | Request validation failed or business rule validation failed |  -  |
| **401** | Authentication missing or invalid |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## deactivateQuestion

> deactivateQuestion(id)

Deactivate a question

Why this endpoint is needed: Historical integrity is important, so hard-deletes are avoided while obsolete questions must be removed from active authoring lists.  What this endpoint does: It marks the target question as inactive.  How this endpoint does it: The service resolves the question by tenant context, updates active status, and returns no-content on success. 

### Example

```ts
import {
  Configuration,
  QuestionsApi,
} from '@survey-engine/sdk';
import type { DeactivateQuestionRequest } from '@survey-engine/sdk';

async function example() {
  console.log("🚀 Testing @survey-engine/sdk SDK...");
  const config = new Configuration({ 
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
  });
  const api = new QuestionsApi(config);

  const body = {
    // string
    id: 38400000-8cf0-11bd-b23e-10b96e4ef00d,
  } satisfies DeactivateQuestionRequest;

  try {
    const data = await api.deactivateQuestion(body);
    console.log(data);
  } catch (error) {
    console.error(error);
  }
}

// Run the test
example().catch(console.error);
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **id** | `string` |  | [Defaults to `undefined`] |

### Return type

`void` (Empty response body)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **204** | Question deactivated |  -  |
| **401** | Authentication missing or invalid |  -  |
| **404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## getQuestion

> QuestionResponse getQuestion(id)

Get one question by id

Why this endpoint is needed: Detailed question retrieval is required for edit forms, review pages, and diagnostics.  What this endpoint does: It returns one question record by identifier under tenant access control.  How this endpoint does it: The service resolves the record by ID with tenant-scoped checks and maps the result to a QuestionResponse DTO. 

### Example

```ts
import {
  Configuration,
  QuestionsApi,
} from '@survey-engine/sdk';
import type { GetQuestionRequest } from '@survey-engine/sdk';

async function example() {
  console.log("🚀 Testing @survey-engine/sdk SDK...");
  const config = new Configuration({ 
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
  });
  const api = new QuestionsApi(config);

  const body = {
    // string
    id: 38400000-8cf0-11bd-b23e-10b96e4ef00d,
  } satisfies GetQuestionRequest;

  try {
    const data = await api.getQuestion(body);
    console.log(data);
  } catch (error) {
    console.error(error);
  }
}

// Run the test
example().catch(console.error);
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **id** | `string` |  | [Defaults to `undefined`] |

### Return type

[**QuestionResponse**](QuestionResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Question returned |  -  |
| **401** | Authentication missing or invalid |  -  |
| **404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## listQuestions

> Array&lt;QuestionResponse&gt; listQuestions()

List active questions for current tenant

Why this endpoint is needed: Authoring UIs require fast retrieval of available question inventory during survey design.  What this endpoint does: It returns active, tenant-scoped question records currently available for composition.  How this endpoint does it: The question service uses tenant-aware repository filtering and maps entity rows into response DTOs for list rendering. 

### Example

```ts
import {
  Configuration,
  QuestionsApi,
} from '@survey-engine/sdk';
import type { ListQuestionsRequest } from '@survey-engine/sdk';

async function example() {
  console.log("🚀 Testing @survey-engine/sdk SDK...");
  const config = new Configuration({ 
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
  });
  const api = new QuestionsApi(config);

  try {
    const data = await api.listQuestions();
    console.log(data);
  } catch (error) {
    console.error(error);
  }
}

// Run the test
example().catch(console.error);
```

### Parameters

This endpoint does not need any parameter.

### Return type

[**Array&lt;QuestionResponse&gt;**](QuestionResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: Not defined
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Active questions returned |  -  |
| **401** | Authentication missing or invalid |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)


## updateQuestion

> QuestionResponse updateQuestion(id, questionRequest)

Update an existing question

Why this endpoint is needed: Content quality evolves over time and questions require controlled updates.  What this endpoint does: It updates question fields and maintains version continuity used by snapshot-based references.  How this endpoint does it: The service validates payload constraints, checks tenant ownership, persists changes, and returns updated question data. 

### Example

```ts
import {
  Configuration,
  QuestionsApi,
} from '@survey-engine/sdk';
import type { UpdateQuestionRequest } from '@survey-engine/sdk';

async function example() {
  console.log("🚀 Testing @survey-engine/sdk SDK...");
  const config = new Configuration({ 
    // Configure HTTP bearer authorization: bearerAuth
    accessToken: "YOUR BEARER TOKEN",
  });
  const api = new QuestionsApi(config);

  const body = {
    // string
    id: 38400000-8cf0-11bd-b23e-10b96e4ef00d,
    // QuestionRequest
    questionRequest: ...,
  } satisfies UpdateQuestionRequest;

  try {
    const data = await api.updateQuestion(body);
    console.log(data);
  } catch (error) {
    console.error(error);
  }
}

// Run the test
example().catch(console.error);
```

### Parameters


| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **id** | `string` |  | [Defaults to `undefined`] |
| **questionRequest** | [QuestionRequest](QuestionRequest.md) |  | |

### Return type

[**QuestionResponse**](QuestionResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

- **Content-Type**: `application/json`
- **Accept**: `application/json`


### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Question updated |  -  |
| **400** | Request validation failed or business rule validation failed |  -  |
| **401** | Authentication missing or invalid |  -  |
| **404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

[[Back to top]](#) [[Back to API list]](../README.md#api-endpoints) [[Back to Model list]](../README.md#models) [[Back to README]](../README.md)

