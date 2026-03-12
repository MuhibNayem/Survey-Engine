<script lang="ts">
    import api from "$lib/api";
    import { page } from "$app/state";
    import { onMount } from "svelte";
    import * as Card from "$lib/components/ui/card";
    import { Button } from "$lib/components/ui/button";
    import { Badge } from "$lib/components/ui/badge";
    import { Skeleton } from "$lib/components/ui/skeleton";
    import { toast } from "svelte-sonner";
    import { Share2, Send, Copy, Check, Link2, Code2 } from "lucide-svelte";
    import type {
        CampaignResponse,
        DistributionChannelResponse,
    } from "$lib/types";

    const campaignId = $derived(page.params.id);

    let campaign = $state<CampaignResponse | null>(null);
    let channels = $state<DistributionChannelResponse[]>([]);
    let loading = $state(true);
    let copiedId = $state<string | null>(null);
    let distributeLoading = $state(false);

    async function loadData() {
        loading = true;
        try {
            const [campRes, chRes] = await Promise.all([
                api.get<CampaignResponse>(`/campaigns/${campaignId}`),
                api.get<DistributionChannelResponse[]>(`/campaigns/${campaignId}/channels`).catch(() => ({ data: [] }))
            ]);

            campaign = campRes.data;
            channels = chRes.data;
        } catch {
            toast.error("Failed to load distribution data");
        } finally {
            loading = false;
        }
    }

    async function distribute() {
        distributeLoading = true;
        try {
            const { data } = await api.post<DistributionChannelResponse[]>(`/campaigns/${campaignId}/distribute`);
            channels = data;
            toast.success("Channels generated successfully");
        } catch (err: any) {
            toast.error(err.response?.data?.message || "Failed to generate distribution channels.");
        } finally {
            distributeLoading = false;
        }
    }

    async function copyToClipboard(text: string, id: string) {
        try {
            if (navigator?.clipboard && navigator.clipboard.writeText) {
                await navigator.clipboard.writeText(text);
            } else {
                const textArea = document.createElement("textarea");
                textArea.value = text;
                textArea.style.position = "fixed";
                textArea.style.left = "-999999px";
                textArea.style.top = "-999999px";
                document.body.appendChild(textArea);
                textArea.focus();
                textArea.select();
                try {
                    document.execCommand("copy");
                } finally {
                    textArea.remove();
                }
            }
            copiedId = id;
            setTimeout(() => (copiedId = null), 2000);
        } catch (err) {
            console.error("Failed to copy:", err);
            toast.error("Failed to copy to clipboard");
        }
    }

    function copyLabel(id: string, fallback: string) {
        return copiedId === id ? "Copied" : fallback;
    }

    onMount(loadData);
</script>

{#if loading}
    <div class="space-y-3">
        {#each Array(3) as _}
            <Card.Root>
                <Card.Content class="flex items-center justify-between gap-4 py-4">
                    <div class="space-y-2 flex-1">
                        <Skeleton class="h-5 w-[100px]" />
                        <Skeleton class="h-4 w-full max-w-[400px]" />
                    </div>
                    <Skeleton class="h-9 w-[80px]" />
                </Card.Content>
            </Card.Root>
        {/each}
    </div>
{:else if campaign}
    {#if channels.length === 0}
        <Card.Root class="flex flex-col items-center justify-center py-16 text-center">
            <Card.Content>
                <div class="mx-auto mb-4 flex h-16 w-16 items-center justify-center rounded-2xl bg-muted">
                    <Share2 class="h-8 w-8 text-muted-foreground" />
                </div>
                <h3 class="text-lg font-semibold text-foreground">No distribution generated</h3>
                <p class="mt-1 text-sm text-muted-foreground">
                    {campaign.status === "DRAFT"
                        ? "Activate the campaign first to generate distribution details."
                        : 'Click "Generate Channels" to create the distribution details.'}
                </p>
                {#if campaign.status === "ACTIVE"}
                    <Button class="mt-4" onclick={distribute} disabled={distributeLoading}>
                        {#if distributeLoading}
                            <span class="mr-2 inline-block h-4 w-4 animate-spin rounded-full border-2 border-current border-t-transparent"></span>
                        {/if}
                        <Send class="mr-2 h-4 w-4" />
                        Generate Channels
                    </Button>
                {/if}
            </Card.Content>
        </Card.Root>
    {:else}
        <div class="space-y-4">
            {#each channels as channel (channel.id)}
                <Card.Root>
                    <Card.Header>
                        <div class="flex items-start justify-between gap-4">
                            <div>
                                <Card.Title class="flex items-center gap-2 text-base">
                                    {#if channel.requestPayloadExample}
                                        <Code2 class="h-4 w-4" />
                                    {:else}
                                        <Link2 class="h-4 w-4" />
                                    {/if}
                                    {channel.channelTitle || channel.channelType.replace(/_/g, ' ')}
                                </Card.Title>
                                {#if channel.channelNote}
                                    <Card.Description class="mt-2 max-w-3xl leading-6">
                                        {channel.channelNote}
                                    </Card.Description>
                                {/if}
                            </div>
                            <Badge variant="secondary">{channel.channelType.replace(/_/g, ' ')}</Badge>
                        </div>
                    </Card.Header>
                    <Card.Content class="space-y-4">
                        <div class="rounded-md border bg-muted/20 p-3">
                            <div class="mb-2 text-xs font-semibold uppercase tracking-[0.16em] text-muted-foreground">
                                {channel.requestPayloadExample ? 'Launch Endpoint' : 'Survey URL'}
                            </div>
                            <p class="break-all font-mono text-xs text-foreground">{channel.channelValue}</p>
                        </div>

                        <div class="flex flex-wrap gap-2">
                            <Button variant="outline" size="sm" onclick={() => copyToClipboard(channel.channelValue, channel.id)}>
                                {#if copiedId === channel.id}
                                    <Check class="mr-1 h-4 w-4 text-emerald-500" />
                                {:else}
                                    <Copy class="mr-1 h-4 w-4" />
                                {/if}
                                {copyLabel(channel.id, channel.requestPayloadExample ? 'Copy Endpoint' : 'Copy Link')}
                            </Button>
                            {#if channel.requestPayloadExample}
                                <Button variant="outline" size="sm" onclick={() => copyToClipboard(channel.requestPayloadExample ?? '', `${channel.id}-payload`)}>
                                    {#if copiedId === `${channel.id}-payload`}
                                        <Check class="mr-1 h-4 w-4 text-emerald-500" />
                                    {:else}
                                        <Copy class="mr-1 h-4 w-4" />
                                    {/if}
                                    {copyLabel(`${channel.id}-payload`, 'Copy Payload')}
                                </Button>
                            {/if}
                            {#if channel.frontendExample}
                                <Button variant="outline" size="sm" onclick={() => copyToClipboard(channel.frontendExample ?? '', `${channel.id}-frontend`)}>
                                    {#if copiedId === `${channel.id}-frontend`}
                                        <Check class="mr-1 h-4 w-4 text-emerald-500" />
                                    {:else}
                                        <Copy class="mr-1 h-4 w-4" />
                                    {/if}
                                    {copyLabel(`${channel.id}-frontend`, 'Copy Frontend Sample')}
                                </Button>
                            {/if}
                            {#if channel.backendExample}
                                <Button variant="outline" size="sm" onclick={() => copyToClipboard(channel.backendExample ?? '', `${channel.id}-backend`)}>
                                    {#if copiedId === `${channel.id}-backend`}
                                        <Check class="mr-1 h-4 w-4 text-emerald-500" />
                                    {:else}
                                        <Copy class="mr-1 h-4 w-4" />
                                    {/if}
                                    {copyLabel(`${channel.id}-backend`, 'Copy Backend Sample')}
                                </Button>
                            {/if}
                        </div>

                        {#if channel.requestPayloadExample}
                            <div class="grid gap-4 xl:grid-cols-3">
                                <div class="space-y-2 xl:col-span-1">
                                    <div class="text-xs font-semibold uppercase tracking-[0.16em] text-muted-foreground">Sample Payload</div>
                                    <pre class="overflow-x-auto rounded-md border bg-muted/30 p-3 text-xs leading-6 text-foreground"><code>{channel.requestPayloadExample}</code></pre>
                                </div>
                                <div class="space-y-2 xl:col-span-1">
                                    <div class="text-xs font-semibold uppercase tracking-[0.16em] text-muted-foreground">Frontend Sample</div>
                                    <pre class="overflow-x-auto rounded-md border bg-muted/30 p-3 text-xs leading-6 text-foreground"><code>{channel.frontendExample}</code></pre>
                                </div>
                                <div class="space-y-2 xl:col-span-1">
                                    <div class="text-xs font-semibold uppercase tracking-[0.16em] text-muted-foreground">Backend Sample</div>
                                    <pre class="overflow-x-auto rounded-md border bg-muted/30 p-3 text-xs leading-6 text-foreground"><code>{channel.backendExample}</code></pre>
                                </div>
                            </div>
                        {/if}
                    </Card.Content>
                </Card.Root>
            {/each}
        </div>
    {/if}
{/if}
