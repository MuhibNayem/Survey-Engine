import fs from 'node:fs/promises';
import path from 'node:path';
import { parse } from 'yaml';

const FRONTEND_ROOT = process.cwd();
const OPENAPI_PATH = path.resolve(FRONTEND_ROOT, '../src/main/resources/static/openapi.yaml');
const OUTPUT_PATH = path.resolve(FRONTEND_ROOT, 'src/lib/docs/generated-openapi.ts');

const METHOD_ORDER = ['GET', 'POST', 'PUT', 'PATCH', 'DELETE'];

function getByRef(spec, ref) {
  if (!ref || !ref.startsWith('#/')) return null;
  return ref
    .slice(2)
    .split('/')
    .reduce((acc, key) => (acc ? acc[key] : null), spec);
}

function normalizeType(schema, spec) {
  if (!schema) return 'unknown';
  if (schema.$ref) {
    const resolved = getByRef(spec, schema.$ref);
    if (!resolved) return 'object';
    return normalizeType(resolved, spec);
  }
  if (schema.type === 'array') {
    return `${normalizeType(schema.items, spec)}[]`;
  }
  if (Array.isArray(schema.enum) && schema.enum.length > 0) {
    return `${schema.type ?? 'string'}(${schema.enum.join('|')})`;
  }
  if (schema.type) return schema.type;
  if (schema.properties) return 'object';
  return 'unknown';
}

function extractPropsFromSchema(schema, spec, fallbackName = 'payload') {
  if (!schema) return [];
  if (schema.$ref) {
    const resolved = getByRef(spec, schema.$ref);
    return extractPropsFromSchema(resolved, spec, fallbackName);
  }
  if (schema.allOf && Array.isArray(schema.allOf)) {
    return schema.allOf.flatMap((s) => extractPropsFromSchema(s, spec, fallbackName));
  }
  if (schema.type === 'object' || schema.properties) {
    const requiredSet = new Set(schema.required || []);
    return Object.entries(schema.properties || {}).map(([name, propSchema]) => ({
      name,
      type: normalizeType(propSchema, spec),
      required: requiredSet.has(name),
      description: propSchema.description || undefined,
      defaultValue: propSchema.default != null ? String(propSchema.default) : undefined
    }));
  }
  return [
    {
      name: fallbackName,
      type: normalizeType(schema, spec),
      required: true,
      description: schema.description || undefined
    }
  ];
}

function pickResponseExample(op, spec) {
  const responses = op.responses || {};
  const sorted = Object.entries(responses).sort(([a], [b]) => Number(a) - Number(b));

  for (const [, response] of sorted) {
    const content = response?.content || {};
    const appJson = content['application/json'] || Object.values(content)[0];
    if (!appJson) continue;

    if (appJson.example != null) return appJson.example;

    if (appJson.examples && typeof appJson.examples === 'object') {
      const first = Object.values(appJson.examples)[0];
      if (first?.value != null) return first.value;
    }

    const schema = appJson.schema;
    if (schema?.example != null) return schema.example;

    if (schema?.$ref) {
      const resolved = getByRef(spec, schema.$ref);
      if (resolved?.example != null) return resolved.example;
    }
  }

  return undefined;
}

function collectParameters(op, pathItem) {
  const merged = [...(pathItem.parameters || []), ...(op.parameters || [])];
  const seen = new Set();

  return merged.filter((p) => {
    const key = `${p.in}:${p.name}`;
    if (seen.has(key)) return false;
    seen.add(key);
    return true;
  });
}

function ensurePathParams(pathPattern, pathParams) {
  const missing = [...pathPattern.matchAll(/\{([^}]+)\}/g)]
    .map((m) => m[1])
    .filter((name) => !pathParams.some((p) => p.name === name));

  for (const name of missing) {
    pathParams.push({
      name,
      type: 'string',
      required: true,
      description: 'Path parameter'
    });
  }

  return pathParams;
}

async function main() {
  const yamlRaw = await fs.readFile(OPENAPI_PATH, 'utf8');
  const spec = parse(yamlRaw);

  const operations = [];

  for (const [pathPattern, pathItem] of Object.entries(spec.paths || {})) {
    for (const [methodKey, op] of Object.entries(pathItem)) {
      const method = methodKey.toUpperCase();
      if (!METHOD_ORDER.includes(method)) continue;

      // Exclude super admin APIs completely from frontend docs
      if (pathPattern.includes('/superadmin/')) continue;
      
      const isSuperAdmin = (op.tags || []).some(t => 
        t.toLowerCase() === 'super admin'
      );
      if (isSuperAdmin) continue;

      const parameters = collectParameters(op, pathItem);

      const pathParams = ensurePathParams(
        pathPattern,
        parameters
          .filter((p) => p.in === 'path')
          .map((p) => ({
            name: p.name,
            type: normalizeType(p.schema, spec),
            required: p.required ?? true,
            description: p.description || undefined,
            defaultValue: p.schema?.default != null ? String(p.schema.default) : undefined
          }))
      );

      const queryParams = parameters
        .filter((p) => p.in === 'query')
        .map((p) => ({
          name: p.name,
          type: normalizeType(p.schema, spec),
          required: p.required ?? false,
          description: p.description || undefined,
          defaultValue: p.schema?.default != null ? String(p.schema.default) : undefined
        }));

      const headers = parameters
        .filter((p) => p.in === 'header')
        .map((p) => ({
          name: p.name,
          type: normalizeType(p.schema, spec),
          required: p.required ?? false,
          description: p.description || undefined,
          defaultValue: p.schema?.default != null ? String(p.schema.default) : undefined
        }));

      const requestBodySchema =
        op.requestBody?.content?.['application/json']?.schema ||
        Object.values(op.requestBody?.content || {})[0]?.schema;

      const requestBody = extractPropsFromSchema(requestBodySchema, spec, 'payload');

      operations.push({
        id: op.operationId || `${method.toLowerCase()}_${pathPattern.replace(/[^a-zA-Z0-9]+/g, '_')}`,
        method,
        endpoint: pathPattern.replace('/api/v1', ''),
        path: pathPattern,
        summary: op.summary || `${method} ${pathPattern}`,
        description: op.description || undefined,
        tags: op.tags || [],
        pathParams,
        queryParams,
        headers,
        requestBody,
        responseExample: pickResponseExample(op, spec)
      });
    }
  }

  operations.sort((a, b) => {
    if (a.path === b.path) {
      return METHOD_ORDER.indexOf(a.method) - METHOD_ORDER.indexOf(b.method);
    }
    return a.path.localeCompare(b.path);
  });

  const payload = {
    info: spec.info,
    tags: (spec.tags || []).filter(t => 
      t.name.toLowerCase() !== 'super admin'
    ),
    operations
  };

  const out = `/* AUTO-GENERATED FILE. DO NOT EDIT MANUALLY. */\n` +
    `export const generatedOpenApi = ${JSON.stringify(payload, null, 2)} as const;\n`;

  await fs.writeFile(OUTPUT_PATH, out, 'utf8');
  console.log(`Generated ${operations.length} operations -> ${path.relative(FRONTEND_ROOT, OUTPUT_PATH)}`);
}

main().catch((err) => {
  console.error(err);
  process.exit(1);
});
