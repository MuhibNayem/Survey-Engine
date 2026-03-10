<script lang="ts">
  import ApiEndpoint from '$lib/components/ApiDocs/ApiEndpoint.svelte';
  import { toResponseExample } from '$lib/docs/openapi';
  import { reveal } from '$lib/docs/intersectionReveal';
  import type { PageData } from './$types';

  let { data }: { data: PageData } = $props();

  const tag = $derived(data.tag);
  const operations = $derived(data.operations);
</script>

<svelte:head>
  <title>{tag.name} - API Reference | Survey Engine MVP</title>
</svelte:head>

<section class="space-y-6">
  <header class="space-y-2" use:reveal={{ y: 16, duration: 700 }}>
    <p class="text-xs font-semibold uppercase tracking-wider text-primary">{tag.name}</p>
    <h1 class="text-3xl font-bold tracking-tight">{tag.name} APIs</h1>
    {#if tag.description}
      <p class="text-sm text-muted-foreground">{tag.description}</p>
    {/if}
  </header>

  <div class="space-y-3" use:reveal={{ y: 20, duration: 820, delay: 110 }}>
    {#if operations.length === 0}
      <div class="rounded-lg border border-border bg-card p-6 text-center">
        <p class="text-sm text-muted-foreground">No endpoints documented for this tag yet.</p>
      </div>
    {:else}
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
    {/if}
  </div>
</section>
