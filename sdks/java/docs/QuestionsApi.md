# QuestionsApi

All URIs are relative to *http://localhost*

| Method | HTTP request | Description |
|------------- | ------------- | -------------|
| [**createQuestion**](QuestionsApi.md#createQuestion) | **POST** /api/v1/questions | Create a reusable question |
| [**deactivateQuestion**](QuestionsApi.md#deactivateQuestion) | **DELETE** /api/v1/questions/{id} | Deactivate a question |
| [**getQuestion**](QuestionsApi.md#getQuestion) | **GET** /api/v1/questions/{id} | Get one question by id |
| [**listQuestions**](QuestionsApi.md#listQuestions) | **GET** /api/v1/questions | List active questions for current tenant |
| [**updateQuestion**](QuestionsApi.md#updateQuestion) | **PUT** /api/v1/questions/{id} | Update an existing question |


<a id="createQuestion"></a>
# **createQuestion**
> QuestionResponse createQuestion(questionRequest)

Create a reusable question

Why this endpoint is needed: The question bank is the foundation of reusable survey content. Teams need a way to add standardized question assets once and reuse them across many surveys.  What this endpoint does: It creates a question with text, type, and scoring metadata and stores version context used by downstream snapshots.  How this endpoint does it: The service validates required fields and max score constraints, applies tenant scoping from auth context, persists the question entity, and returns a QuestionResponse containing metadata and version pointers. 

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.QuestionsApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    QuestionsApi apiInstance = new QuestionsApi(defaultClient);
    QuestionRequest questionRequest = new QuestionRequest(); // QuestionRequest | 
    try {
      QuestionResponse result = apiInstance.createQuestion(questionRequest);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling QuestionsApi#createQuestion");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **questionRequest** | [**QuestionRequest**](QuestionRequest.md)|  | |

### Return type

[**QuestionResponse**](QuestionResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **201** | Question created |  -  |
| **400** | Request validation failed or business rule validation failed |  -  |
| **401** | Authentication missing or invalid |  -  |

<a id="deactivateQuestion"></a>
# **deactivateQuestion**
> deactivateQuestion(id)

Deactivate a question

Why this endpoint is needed: Historical integrity is important, so hard-deletes are avoided while obsolete questions must be removed from active authoring lists.  What this endpoint does: It marks the target question as inactive.  How this endpoint does it: The service resolves the question by tenant context, updates active status, and returns no-content on success. 

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.QuestionsApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    QuestionsApi apiInstance = new QuestionsApi(defaultClient);
    UUID id = UUID.randomUUID(); // UUID | 
    try {
      apiInstance.deactivateQuestion(id);
    } catch (ApiException e) {
      System.err.println("Exception when calling QuestionsApi#deactivateQuestion");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **id** | **UUID**|  | |

### Return type

null (empty response body)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **204** | Question deactivated |  -  |
| **401** | Authentication missing or invalid |  -  |
| **404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

<a id="getQuestion"></a>
# **getQuestion**
> QuestionResponse getQuestion(id)

Get one question by id

Why this endpoint is needed: Detailed question retrieval is required for edit forms, review pages, and diagnostics.  What this endpoint does: It returns one question record by identifier under tenant access control.  How this endpoint does it: The service resolves the record by ID with tenant-scoped checks and maps the result to a QuestionResponse DTO. 

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.QuestionsApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    QuestionsApi apiInstance = new QuestionsApi(defaultClient);
    UUID id = UUID.randomUUID(); // UUID | 
    try {
      QuestionResponse result = apiInstance.getQuestion(id);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling QuestionsApi#getQuestion");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **id** | **UUID**|  | |

### Return type

[**QuestionResponse**](QuestionResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Question returned |  -  |
| **401** | Authentication missing or invalid |  -  |
| **404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

<a id="listQuestions"></a>
# **listQuestions**
> List&lt;QuestionResponse&gt; listQuestions()

List active questions for current tenant

Why this endpoint is needed: Authoring UIs require fast retrieval of available question inventory during survey design.  What this endpoint does: It returns active, tenant-scoped question records currently available for composition.  How this endpoint does it: The question service uses tenant-aware repository filtering and maps entity rows into response DTOs for list rendering. 

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.QuestionsApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    QuestionsApi apiInstance = new QuestionsApi(defaultClient);
    try {
      List<QuestionResponse> result = apiInstance.listQuestions();
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling QuestionsApi#listQuestions");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters
This endpoint does not need any parameter.

### Return type

[**List&lt;QuestionResponse&gt;**](QuestionResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: Not defined
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Active questions returned |  -  |
| **401** | Authentication missing or invalid |  -  |

<a id="updateQuestion"></a>
# **updateQuestion**
> QuestionResponse updateQuestion(id, questionRequest)

Update an existing question

Why this endpoint is needed: Content quality evolves over time and questions require controlled updates.  What this endpoint does: It updates question fields and maintains version continuity used by snapshot-based references.  How this endpoint does it: The service validates payload constraints, checks tenant ownership, persists changes, and returns updated question data. 

### Example
```java
// Import classes:
import org.openapitools.client.ApiClient;
import org.openapitools.client.ApiException;
import org.openapitools.client.Configuration;
import org.openapitools.client.auth.*;
import org.openapitools.client.models.*;
import org.openapitools.client.api.QuestionsApi;

public class Example {
  public static void main(String[] args) {
    ApiClient defaultClient = Configuration.getDefaultApiClient();
    defaultClient.setBasePath("http://localhost");
    
    // Configure HTTP bearer authorization: bearerAuth
    HttpBearerAuth bearerAuth = (HttpBearerAuth) defaultClient.getAuthentication("bearerAuth");
    bearerAuth.setBearerToken("BEARER TOKEN");

    QuestionsApi apiInstance = new QuestionsApi(defaultClient);
    UUID id = UUID.randomUUID(); // UUID | 
    QuestionRequest questionRequest = new QuestionRequest(); // QuestionRequest | 
    try {
      QuestionResponse result = apiInstance.updateQuestion(id, questionRequest);
      System.out.println(result);
    } catch (ApiException e) {
      System.err.println("Exception when calling QuestionsApi#updateQuestion");
      System.err.println("Status code: " + e.getCode());
      System.err.println("Reason: " + e.getResponseBody());
      System.err.println("Response headers: " + e.getResponseHeaders());
      e.printStackTrace();
    }
  }
}
```

### Parameters

| Name | Type | Description  | Notes |
|------------- | ------------- | ------------- | -------------|
| **id** | **UUID**|  | |
| **questionRequest** | [**QuestionRequest**](QuestionRequest.md)|  | |

### Return type

[**QuestionResponse**](QuestionResponse.md)

### Authorization

[bearerAuth](../README.md#bearerAuth)

### HTTP request headers

 - **Content-Type**: application/json
 - **Accept**: application/json

### HTTP response details
| Status code | Description | Response headers |
|-------------|-------------|------------------|
| **200** | Question updated |  -  |
| **400** | Request validation failed or business rule validation failed |  -  |
| **401** | Authentication missing or invalid |  -  |
| **404** | Referenced resource does not exist or is not visible in tenant scope |  -  |

