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

export function slugifyTag(tag: string): string {
  return tag.toLowerCase().replace(/[^a-z0-9]+/g, '-').replace(/(^-|-$)/g, '');
}

export function getOperationsByTagSlug(slug: string) {
  return uniqueById(allOpenApiOperations.filter(op => 
    op.tags.some(t => slugifyTag(t) === slug)
  ));
}

export function getTagBySlug(slug: string) {
  return openApiTags.find(t => slugifyTag(t.name) === slug);
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
