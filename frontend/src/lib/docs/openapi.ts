import { generatedOpenApi } from '$lib/docs/generated-openapi';

export type OpenApiProp = {
  name: string;
  type: string;
  required?: boolean;
  description?: string;
  defaultValue?: string;
};

export type OpenApiOperation = {
  id: string;
  method: 'GET' | 'POST' | 'PUT' | 'PATCH' | 'DELETE';
  endpoint: string;
  path: string;
  summary: string;
  description?: string;
  tags: string[];
  pathParams: OpenApiProp[];
  queryParams: OpenApiProp[];
  headers: OpenApiProp[];
  requestBody: OpenApiProp[];
  responseExample?: unknown;
};

export const openApiMeta = generatedOpenApi.info;
export const openApiTags = generatedOpenApi.tags;

export const allOpenApiOperations = generatedOpenApi.operations as unknown as OpenApiOperation[];

function byPrefix(prefixes: string[]) {
  return allOpenApiOperations.filter((op) => prefixes.some((prefix) => op.path.startsWith(prefix)));
}

function uniqueById(ops: OpenApiOperation[]) {
  return ops.filter((op, idx) => ops.findIndex((x) => x.id === op.id) === idx);
}

export function getAuthOperations() {
  return uniqueById(byPrefix(['/api/v1/admin/auth', '/api/v1/auth']));
}

export function getSurveyOperations() {
  return uniqueById(byPrefix(['/api/v1/surveys']));
}

export function getCampaignOperations() {
  return uniqueById(byPrefix(['/api/v1/campaigns', '/api/v1/public/campaigns']));
}

export function getResponseOperations() {
  return uniqueById(
    allOpenApiOperations.filter(
      (op) => op.path.startsWith('/api/v1/responses') && !op.path.startsWith('/api/v1/responses/analytics')
    )
  );
}

export function getAnalyticsOperations() {
  return uniqueById(
    allOpenApiOperations.filter(
      (op) =>
        op.path.startsWith('/api/v1/analytics') ||
        op.path.startsWith('/api/v1/responses/analytics')
    )
  );
}

export function getScoringOperations() {
  return uniqueById(byPrefix(['/api/v1/scoring']));
}

export function getSubscriptionOperations() {
  return uniqueById(
    allOpenApiOperations.filter(
      (op) =>
        op.path.startsWith('/api/v1/admin/subscriptions') ||
        op.path.startsWith('/api/v1/admin/plans') ||
        op.path.includes('/subscriptions/override')
    )
  );
}

export function toResponseExample(value: unknown): string | undefined {
  if (value == null) return undefined;
  try {
    if (typeof value === 'string') return value;
    return JSON.stringify(value, null, 2);
  } catch {
    return undefined;
  }
}
