<script lang="ts">
  import SimpleCodeBlock from '$lib/components/ApiDocs/SimpleCodeBlock.svelte';
</script>

<section class="space-y-8">
  <header class="space-y-2">
    <p class="text-xs font-semibold uppercase tracking-wider text-primary">SDK</p>
    <h1 class="text-3xl font-bold tracking-tight">Survey Engine SDK Guide</h1>
    <p class="text-sm text-muted-foreground">
      Reference implementation patterns for building production-ready API clients with TypeScript.
    </p>
  </header>

  <div class="space-y-4 rounded-lg border border-border bg-card p-5">
    <h2 class="text-xl font-semibold">1. Install and bootstrap client</h2>
    <SimpleCodeBlock
      language="bash"
      filename="install.sh"
      code={`pnpm add axios zod`}
    />
    <SimpleCodeBlock
      language="typescript"
      filename="sdk/client.ts"
      code={`import axios from 'axios';

export const sdk = axios.create({
  baseURL: '/api/v1',
  withCredentials: true,
  headers: { 'Content-Type': 'application/json' }
});

export async function bootstrapCsrf() {
  const { data } = await sdk.get<{ token: string }>('/admin/auth/csrf');
  sdk.defaults.headers.common['X-XSRF-TOKEN'] = data.token;
}`}
    />
  </div>

  <div class="space-y-4 rounded-lg border border-border bg-card p-5">
    <h2 class="text-xl font-semibold">2. Authentication workflow</h2>
    <SimpleCodeBlock
      language="typescript"
      filename="sdk/auth.ts"
      code={`export async function login(email: string, password: string) {
  await bootstrapCsrf();
  await sdk.post('/admin/auth/login', { email, password });
  const me = await sdk.get('/admin/auth/me');
  return me.data;
}`}
    />
  </div>

  <div class="space-y-4 rounded-lg border border-border bg-card p-5">
    <h2 class="text-xl font-semibold">3. Campaign response query with metadata filters</h2>
    <SimpleCodeBlock
      language="typescript"
      filename="sdk/responses.ts"
      code={`interface ListResponsesInput {
  campaignId: string;
  page?: number;
  size?: number;
  metadata?: Record<string, string>;
}

export async function listResponses(input: ListResponsesInput) {
  const params = new URLSearchParams();
  params.set('page', String(input.page ?? 0));
  params.set('size', String(input.size ?? 20));

  Object.entries(input.metadata ?? {}).forEach(([k, v]) => {
    if (v?.trim()) params.append(\`metadata.\${k}\`, v.trim());
  });

  const { data } = await sdk.get(
    \`/responses/campaign/\${input.campaignId}?\${params.toString()}\`
  );

  return data;
}`}
    />
  </div>

  <div class="space-y-4 rounded-lg border border-border bg-card p-5">
    <h2 class="text-xl font-semibold">4. Production recommendations</h2>
    <ul class="list-disc space-y-2 pl-5 text-sm text-muted-foreground">
      <li>Centralize retry and timeout policies in the SDK client.</li>
      <li>Propagate request IDs for observability in every call.</li>
      <li>Validate response contracts with Zod before using data in UI.</li>
      <li>Use typed wrappers per domain: auth, surveys, campaigns, responses, analytics.</li>
    </ul>
  </div>
</section>
