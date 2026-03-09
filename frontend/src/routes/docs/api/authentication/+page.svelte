<script lang="ts">
  import ApiEndpoint from '$lib/components/ApiDocs/ApiEndpoint.svelte';
  import { getAuthOperations, toResponseExample } from '$lib/docs/openapi';

  const operations = $derived(getAuthOperations());
</script>

<section class="space-y-6">
  <header class="space-y-2">
    <p class="text-xs font-semibold uppercase tracking-wider text-primary">Authentication</p>
    <h1 class="text-3xl font-bold tracking-tight">Auth & Session APIs</h1>
    <p class="text-sm text-muted-foreground">
      Auto-generated from OpenAPI: admin auth flows, token mode, responder auth profile and OIDC responder endpoints.
    </p>
  </header>

  {#if operations.length === 0}
    <p class="text-sm text-muted-foreground">No auth operations found in OpenAPI spec.</p>
  {:else}
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
  {/if}
</section>
