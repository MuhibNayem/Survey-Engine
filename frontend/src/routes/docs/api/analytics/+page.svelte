<script lang="ts">
  import ApiEndpoint from '$lib/components/ApiDocs/ApiEndpoint.svelte';
  import { getAnalyticsOperations, toResponseExample } from '$lib/docs/openapi';

  const operations = $derived(getAnalyticsOperations());
</script>

<section class="space-y-6">
  <header class="space-y-2">
    <p class="text-xs font-semibold uppercase tracking-wider text-primary">Analytics</p>
    <h1 class="text-3xl font-bold tracking-tight">Analytics & Reporting APIs</h1>
    <p class="text-sm text-muted-foreground">Auto-generated analytics and segment comparison endpoints.</p>
  </header>

  <div class="space-y-3">
    {#each operations as op (op.id)}
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
