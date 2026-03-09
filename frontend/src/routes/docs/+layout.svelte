<script lang="ts">
  import DocsSidebar from '$lib/components/ApiDocs/DocsSidebar.svelte';
  import { docsNavItems } from '$lib/docs/navigation';
  import { page } from '$app/state';

  let { children } = $props();

  // Determine if we are on the SDK page to adjust layout for 3-column view
  let isSdkPage = $derived(page.url.pathname === '/docs/sdk');
</script>

<div class="min-h-screen bg-gradient-to-b from-background via-background to-sky-50/40 text-foreground dark:to-background">
  <div class="mx-auto grid px-4 py-6 md:px-6 transition-all duration-300 {isSdkPage ? 'max-w-[1400px] grid-cols-1 md:grid-cols-[280px_minmax(0,1fr)]' : 'max-w-7xl grid-cols-1 gap-6 md:grid-cols-[290px_minmax(0,1fr)] lg:gap-8'}">
    <aside class="md:sticky md:top-4 md:h-[calc(100vh-2rem)] md:overflow-y-auto {isSdkPage ? 'border-r border-border/50 pr-4' : ''}">
      <div class="mb-4 rounded-xl border border-slate-200/80 bg-card/90 p-4 shadow-sm backdrop-blur dark:border-border">
        <p class="text-[11px] font-semibold uppercase tracking-[0.12em] text-muted-foreground">Survey Engine Docs</p>
        <h2 class="mt-1 text-lg font-semibold leading-tight">API Reference & SDK</h2>
        <p class="mt-2 text-xs text-muted-foreground">
          Production integration docs for platform, tenant, and responder flows.
        </p>
      </div>
      <div class="rounded-xl border border-slate-200/80 bg-card/90 p-3 shadow-sm backdrop-blur dark:border-border">
        <DocsSidebar items={docsNavItems} />
      </div>
    </aside>

    <main class="min-w-0 {isSdkPage ? 'pl-0' : 'rounded-2xl border border-slate-200/70 bg-card/70 p-5 shadow-sm backdrop-blur-sm dark:border-border md:p-7'}">
      {@render children?.()}
    </main>
  </div>
</div>
