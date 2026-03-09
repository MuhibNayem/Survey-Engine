<script lang="ts">
  import logo from '$lib/assets/logo.png';
  import { Button } from '$lib/components/ui/button';
  import DocsSidebar from '$lib/components/ApiDocs/DocsSidebar.svelte';
  import { docsNavItems } from '$lib/docs/navigation';
  import { page } from '$app/state';

  let { children } = $props();

  // Determine if we are on the SDK page to adjust layout for 3-column view
  let isSdkPage = $derived(page.url.pathname === '/docs/sdk');
</script>

<div class="min-h-screen bg-gradient-to-b from-background via-background to-sky-50/40 text-foreground dark:to-background">
  <nav class="relative z-10 flex items-center justify-between px-6 py-4 max-w-7xl mx-auto lg:px-8">
    <a href="/" class="flex items-center gap-2">
      <img src={logo} alt="SE" class="h-9 w-9 object-contain" />
      <span class="text-xl font-bold text-foreground">Survey Engine</span>
    </a>
    <div class="flex items-center gap-3">
      <Button variant="ghost" href="/docs/api">API Docs</Button>
      <Button variant="ghost" href="/pricing">Pricing</Button>
      <Button variant="ghost" href="/login">Sign in</Button>
      <Button href="/register">Get Started</Button>
    </div>
  </nav>

  <div class="mx-auto grid px-4 py-6 md:px-6 transition-all duration-300 {isSdkPage ? 'max-w-[1400px] grid-cols-1 md:grid-cols-[280px_minmax(0,1fr)]' : 'max-w-7xl grid-cols-1 gap-6 md:grid-cols-[290px_minmax(0,1fr)] lg:gap-8'}">
    <aside class="md:sticky md:top-4 md:h-[calc(100vh-2rem)] md:overflow-y-auto {isSdkPage ? 'border-r border-border/50 pr-4' : ''}">
      <div class="mb-4 rounded-xl border border-slate-200/80 bg-card/90 p-4 shadow-sm backdrop-blur dark:border-border">
        <p class="text-[11px] font-semibold uppercase tracking-[0.12em] text-muted-foreground">Survey Engine Docs</p>
        <h2 class="mt-1 text-lg font-semibold leading-tight">API Reference & SDK</h2>
        <p class="mt-2 text-xs text-muted-foreground">
          Production integration docs for platform, tenant, and responder flows.
        </p>
        <div class="mt-3 flex items-center gap-2">
          <a
            href="/"
            class="inline-flex items-center rounded-md border border-border px-2.5 py-1.5 text-xs font-medium text-muted-foreground transition-colors hover:bg-accent hover:text-foreground"
          >
            Back to Landing
          </a>
          <a
            href="/docs/api"
            class="inline-flex items-center rounded-md border border-border px-2.5 py-1.5 text-xs font-medium text-muted-foreground transition-colors hover:bg-accent hover:text-foreground"
          >
            API Reference
          </a>
        </div>
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
