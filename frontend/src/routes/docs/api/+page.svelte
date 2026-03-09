<script lang="ts">
  import ApiEndpoint from '$lib/components/ApiDocs/ApiEndpoint.svelte';
  import SimpleCodeBlock from '$lib/components/ApiDocs/SimpleCodeBlock.svelte';
  import { allOpenApiOperations, openApiMeta, openApiTags, toResponseExample } from '$lib/docs/openapi';

  const SECURITY_MARKER = /security model summary\s*:/i;
  const CONTRACT_MARKER = /every endpoint description intentionally includes\s*:/i;

  function normalizeWhitespace(input: string): string {
    return input.replace(/\s+/g, ' ').trim();
  }

  function splitOnMarker(input: string, marker: RegExp): { before: string; after: string } {
    const match = marker.exec(input);
    if (!match || match.index < 0) {
      return { before: input, after: '' };
    }
    const markerText = match[0];
    const before = input.slice(0, match.index).trim();
    const after = input.slice(match.index + markerText.length).trim();
    return { before, after };
  }

  function parseNumberedPoints(input: string): string[] {
    if (!input) return [];
    return input
      .split(/\s*\d+\)\s+/)
      .map((item) => item.trim())
      .filter(Boolean);
  }

  function parseBulletPoints(input: string): string[] {
    if (!input) return [];

    const normalized = normalizeWhitespace(input)
      // Turn inline " - " bullets into line bullets.
      .replace(/\s+-\s+/g, '\n- ')
      // If content begins without a bullet marker but has bullet-like clauses, prefix first bullet.
      .replace(/^(?!- )(Admin APIs|Headless clients|Admin sessions|Browser cookie mode|Responder submission endpoint)/i, '- $1');

    return normalized
      .split('\n')
      .map((line) => line.trim())
      .filter((line) => line.startsWith('-'))
      .map((line) => line.replace(/^-+\s*/, '').trim())
      .filter(Boolean);
  }

  const highlighted = $derived(allOpenApiOperations.slice(0, 8));
  const rawDescription = $derived(normalizeWhitespace(String(openApiMeta.description ?? '')));
  const descriptionSections = $derived(splitOnMarker(rawDescription, SECURITY_MARKER));
  const securityModel = $derived(parseBulletPoints(descriptionSections.after));

  const introSections = $derived(splitOnMarker(descriptionSections.before, CONTRACT_MARKER));
  const contractStylePoints = $derived(parseNumberedPoints(introSections.after));

  const introSummary = $derived(
    introSections.before || 'Comprehensive API contract for product, implementation, and integration teams.'
  );
</script>

<section class="space-y-8">
  <header class="space-y-3">
    <p class="text-xs font-semibold uppercase tracking-wider text-primary">API Reference</p>
    <h1 class="text-3xl font-bold tracking-tight">{openApiMeta.title}</h1>
    <p class="max-w-3xl text-sm text-muted-foreground">
      {introSummary}
    </p>
  </header>

  <div class="grid gap-4 md:grid-cols-2">
    <div class="rounded-lg border border-border bg-card p-4">
      <p class="text-xs font-semibold uppercase tracking-wider text-muted-foreground">Contract Style</p>
      <ul class="mt-3 list-disc space-y-1.5 pl-5 text-sm text-muted-foreground">
        {#if contractStylePoints.length > 0}
          {#each contractStylePoints as point}
            <li>{point}</li>
          {/each}
        {:else}
          <li>Why the endpoint exists</li>
          <li>What the endpoint does</li>
          <li>How it works in platform lifecycle</li>
        {/if}
      </ul>
    </div>
    <div class="rounded-lg border border-border bg-card p-4">
      <p class="text-xs font-semibold uppercase tracking-wider text-muted-foreground">Security Model</p>
      {#if securityModel.length > 0}
        <ul class="mt-3 list-disc space-y-1.5 pl-5 text-sm text-muted-foreground">
          {#each securityModel as item}
            <li>{item}</li>
          {/each}
        </ul>
      {:else}
        <p class="mt-3 text-sm text-muted-foreground">See auth section for cookie, bearer, and CSRF details.</p>
      {/if}
    </div>
  </div>

  <div class="grid gap-4 md:grid-cols-4">
    <div class="rounded-lg border border-border bg-card p-4">
      <p class="text-xs uppercase tracking-wider text-muted-foreground">Version</p>
      <p class="mt-2 font-mono text-sm">{openApiMeta.version}</p>
    </div>
    <div class="rounded-lg border border-border bg-card p-4">
      <p class="text-xs uppercase tracking-wider text-muted-foreground">Total Endpoints</p>
      <p class="mt-2 text-sm font-semibold">{allOpenApiOperations.length}</p>
    </div>
    <div class="rounded-lg border border-border bg-card p-4">
      <p class="text-xs uppercase tracking-wider text-muted-foreground">Tag Groups</p>
      <p class="mt-2 text-sm font-semibold">{openApiTags.length}</p>
    </div>
    <div class="rounded-lg border border-border bg-card p-4">
      <p class="text-xs uppercase tracking-wider text-muted-foreground">Base Prefix</p>
      <p class="mt-2 font-mono text-sm">/api/v1</p>
    </div>
  </div>

  <div class="space-y-4 rounded-lg border border-border bg-card p-5">
    <h2 class="text-xl font-semibold">OpenAPI-driven Sync</h2>
    <p class="text-sm text-muted-foreground">
      Endpoint sections below are generated from <code>src/main/resources/static/openapi.yaml</code>.
      Update the spec and rerun frontend scripts to refresh docs automatically.
    </p>
    <SimpleCodeBlock
      language="bash"
      filename="sync-docs.sh"
      code={`# regenerate docs from backend OpenAPI spec
pnpm -C frontend docs:generate

# run type checks
pnpm -C frontend check`}
    />
  </div>

  <div class="space-y-4">
    <h2 class="text-xl font-semibold">Sample Operations</h2>
    {#each highlighted as op (op.id)}
      <ApiEndpoint
        method={op.method}
        endpoint={op.endpoint}
        description={op.description || op.summary}
        pathParams={op.pathParams}
        queryParams={op.queryParams}
        headers={op.headers}
        requestBody={op.requestBody}
        responseExample={toResponseExample(op.responseExample)}
        collapsed={true}
      />
    {/each}
  </div>
</section>
