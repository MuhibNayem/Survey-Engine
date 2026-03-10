<script lang="ts">
    import api from "$lib/api";
    import { page } from "$app/state";
    import { onMount } from "svelte";
    import * as Card from "$lib/components/ui/card";
    import { Button } from "$lib/components/ui/button";
    import { Badge } from "$lib/components/ui/badge";
    import { Skeleton } from "$lib/components/ui/skeleton";
    import { toast } from "svelte-sonner";
    import { Share2, Send, Copy, Check } from "lucide-svelte";
    import type {
        CampaignResponse,
        DistributionChannelResponse,
    } from "$lib/types";

    const campaignId = $derived(page.params.id);

    // --- State ---
    let campaign = $state<CampaignResponse | null>(null);
    let channels = $state<DistributionChannelResponse[]>([]);
    let loading = $state(true);
    let copiedId = $state<string | null>(null);

    // Distribute
    let distributeLoading = $state(false);

    // --- API ---
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
            const { data } = await api.post<DistributionChannelResponse[]>(
                `/campaigns/${campaignId}/distribute`,
            );
            channels = data;
            toast.success("Channels generated successfully");
        } catch (err: any) {
            toast.error(
                err.response?.data?.message ||
                    "Failed to generate distribution channels.",
            );
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
                    document.execCommand('copy');
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

    function channelLabel(type: string): string {
        return type.replace(/_/g, " ");
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
        <Card.Root
            class="flex flex-col items-center justify-center py-16 text-center"
        >
            <Card.Content>
                <div
                    class="mx-auto mb-4 flex h-16 w-16 items-center justify-center rounded-2xl bg-muted"
                >
                    <Share2 class="h-8 w-8 text-muted-foreground" />
                </div>
                <h3 class="text-lg font-semibold text-foreground">
                    No channels generated
                </h3>
                <p class="mt-1 text-sm text-muted-foreground">
                    {campaign.status === "DRAFT"
                        ? "Activate the campaign first to generate distribution channels."
                        : 'Click "Generate Channels" to create distribution links.'}
                </p>
                {#if campaign.status === "ACTIVE"}
                    <Button
                        class="mt-4"
                        onclick={distribute}
                        disabled={distributeLoading}
                    >
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
        <div class="space-y-3">
            {#each channels as channel (channel.id)}
                <Card.Root>
                    <Card.Content
                        class="flex items-center justify-between gap-4 py-4"
                    >
                        <div class="min-w-0 flex-1">
                            <Badge variant="secondary" class="mb-1">
                                {channelLabel(channel.channelType)}
                            </Badge>
                            <p
                                class="mt-1 truncate font-mono text-xs text-muted-foreground"
                            >
                                {channel.channelValue}
                            </p>
                        </div>
                        <Button
                            variant="outline"
                            size="sm"
                            onclick={() =>
                                copyToClipboard(
                                    channel.channelValue,
                                    channel.id,
                                )}
                        >
                            {#if copiedId === channel.id}
                                <Check
                                    class="mr-1 h-4 w-4 text-emerald-500"
                                />
                                Copied
                            {:else}
                                <Copy class="mr-1 h-4 w-4" />
                                Copy
                            {/if}
                        </Button>
                    </Card.Content>
                </Card.Root>
            {/each}
        </div>
    {/if}
{/if}
