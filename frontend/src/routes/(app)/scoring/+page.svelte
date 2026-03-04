<script lang="ts">
    import api from "$lib/api";
    import { onMount } from "svelte";
    import { goto } from "$app/navigation";
    import * as Card from "$lib/components/ui/card";
    import { Button } from "$lib/components/ui/button";
    import { Sparkles, Megaphone, ArrowRight } from "lucide-svelte";
    import type { CampaignResponse } from "$lib/types";

    let campaigns = $state<CampaignResponse[]>([]);
    let loading = $state(true);

    onMount(async () => {
        try {
            const { data } = await api.get<CampaignResponse[]>("/campaigns");
            campaigns = data;
        } catch {
            // skip
        } finally {
            loading = false;
        }
    });
</script>

<svelte:head>
    <title>Select Campaign Scoring — Survey Engine</title>
</svelte:head>

<div class="space-y-6 max-w-4xl mx-auto">
    <div>
        <h1
            class="text-2xl font-bold tracking-tight text-foreground flex items-center gap-2"
        >
            <Sparkles class="h-6 w-6 text-amber-500" />
            Scoring Engine
        </h1>
        <p class="mt-1 text-sm text-muted-foreground">
            Select a campaign to manage its weight profiles and calculate
            scores.
        </p>
    </div>

    {#if loading}
        <div class="flex items-center justify-center py-16">
            <span
                class="inline-block h-8 w-8 animate-spin rounded-full border-4 border-amber-500 border-t-transparent"
            ></span>
        </div>
    {:else if campaigns.length === 0}
        <Card.Root class="py-16 text-center">
            <Card.Content>
                <Megaphone
                    class="mx-auto h-12 w-12 text-muted-foreground/30 mb-4"
                />
                <h3 class="text-lg font-medium">No campaigns found</h3>
                <p class="text-sm text-muted-foreground mt-1">
                    Create and activate a campaign to assign scoring equations.
                </p>
                <Button class="mt-4" onclick={() => goto("/campaigns")}
                    >Go to Campaigns</Button
                >
            </Card.Content>
        </Card.Root>
    {:else}
        <div class="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
            {#each campaigns as campaign}
                <Card.Root
                    class="hover:border-amber-500/50 transition-colors cursor-pointer flex flex-col justify-between"
                    onclick={() => goto(`/campaigns/${campaign.id}/scoring`)}
                >
                    <Card.Header>
                        <Card.Title class="text-base"
                            >{campaign.name}</Card.Title
                        >
                        <Card.Description
                            >Status: {campaign.status}</Card.Description
                        >
                    </Card.Header>
                    <Card.Content class="flex justify-end pb-4 pt-1">
                        <Button
                            variant="ghost"
                            size="sm"
                            class="gap-1 bg-amber-500/10 text-amber-700 dark:text-amber-500 hover:bg-amber-500/20"
                        >
                            Manage Scoring <ArrowRight class="h-4 w-4" />
                        </Button>
                    </Card.Content>
                </Card.Root>
            {/each}
        </div>
    {/if}
</div>
