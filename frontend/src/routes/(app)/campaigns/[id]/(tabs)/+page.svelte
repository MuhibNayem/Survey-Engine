<script lang="ts">
    import api from "$lib/api";
    import { page } from "$app/state";
    import { onMount } from "svelte";
    import * as Card from "$lib/components/ui/card";
    import { Badge } from "$lib/components/ui/badge";
    import { Skeleton } from "$lib/components/ui/skeleton";
    import type {
        CampaignResponse,
        DistributionChannelResponse,
        CampaignStatus,
    } from "$lib/types";

    const campaignId = $derived(page.params.id);

    // --- State ---
    let campaign = $state<CampaignResponse | null>(null);
    let channels = $state<DistributionChannelResponse[]>([]);
    let loading = $state(true);

    function statusBadgeVariant(status: CampaignStatus) {
        switch (status) {
            case "ACTIVE":
                return "default" as const;
            case "DRAFT":
                return "secondary" as const;
            default:
                return "outline" as const;
        }
    }

    function formatDate(iso: string) {
        return new Date(iso).toLocaleDateString("en-US", {
            month: "short",
            day: "numeric",
            year: "numeric",
            hour: "2-digit",
            minute: "2-digit",
        });
    }

    // --- API ---
    async function loadData() {
        loading = true;
        try {
            const { data: campaignData } = await api.get<CampaignResponse>(
                `/campaigns/${campaignId}`,
            );
            campaign = campaignData;

            // Load channels if active
            if (campaignData.status !== "DRAFT") {
                try {
                    const chRes = await api.get<DistributionChannelResponse[]>(
                        `/campaigns/${campaignId}/channels`,
                    );
                    channels = chRes.data;
                } catch {
                    channels = [];
                }
            }
        } catch {
            // silent fail
        } finally {
            loading = false;
        }
    }

    onMount(loadData);
</script>

{#if loading}
    <div class="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
        {#each Array(4) as _}
            <Card.Root>
                <Card.Header class="pb-2">
                    <Skeleton class="h-4 w-[100px]" />
                </Card.Header>
                <Card.Content>
                    <Skeleton class="h-6 w-[80px]" />
                </Card.Content>
            </Card.Root>
        {/each}
    </div>
{:else if campaign}
    <div class="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
        <Card.Root>
            <Card.Header class="pb-2">
                <Card.Description>Status</Card.Description>
            </Card.Header>
            <Card.Content>
                <Badge
                    variant={statusBadgeVariant(campaign.status)}
                    class="text-base"
                >
                    {campaign.status}
                </Badge>
            </Card.Content>
        </Card.Root>
        <Card.Root>
            <Card.Header class="pb-2">
                <Card.Description>Auth Mode</Card.Description>
            </Card.Header>
            <Card.Content>
                <Badge
                    variant={campaign.authMode === "PUBLIC"
                        ? "secondary"
                        : "outline"}
                    class="text-base"
                >
                    {campaign.authMode}
                </Badge>
            </Card.Content>
        </Card.Root>
        <Card.Root>
            <Card.Header class="pb-2">
                <Card.Description>Created</Card.Description>
            </Card.Header>
            <Card.Content>
                <p class="text-sm font-medium text-foreground">
                    {formatDate(campaign.createdAt)}
                </p>
            </Card.Content>
        </Card.Root>
        <Card.Root class="sm:col-span-2 lg:col-span-3">
            <Card.Header class="pb-2">
                <Card.Description>Distribution Channels</Card.Description>
            </Card.Header>
            <Card.Content>
                {#if channels.length > 0}
                    <p class="text-sm font-medium text-foreground">
                        {channels.length} channel{channels.length !== 1 ? "s" : ""} generated
                    </p>
                {:else}
                    <p class="text-sm text-muted-foreground">
                        {campaign.status === "DRAFT"
                            ? "Activate the campaign first, then generate channels."
                            : "No channels generated yet."}
                    </p>
                {/if}
            </Card.Content>
        </Card.Root>
    </div>
{/if}
