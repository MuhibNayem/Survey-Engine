<script lang="ts">
    import api from "$lib/api";
    import { onMount } from "svelte";
    import { goto } from "$app/navigation";
    import * as Card from "$lib/components/ui/card";
    import { Button } from "$lib/components/ui/button";
    import { Skeleton } from "$lib/components/ui/skeleton";
    import { MessageSquareText, Megaphone, ArrowRight } from "lucide-svelte";
    import Pagination from "$lib/components/ui/pagination/Pagination.svelte";
    import type { CampaignResponse, PageResponse } from "$lib/types";

    let campaigns = $state<CampaignResponse[]>([]);
    let loading = $state(true);

    // Pagination specific state
    let currentPage = $state(0);
    let pageSize = $state(12); // Use 12 for grid
    let totalElements = $state(0);
    let totalPages = $state(0);

    async function loadData() {
        loading = true;
        try {
            const { data } = await api.get<PageResponse<CampaignResponse>>(`/campaigns?page=${currentPage}&size=${pageSize}`);
            const cData = data as any;
            campaigns = Array.isArray(cData) ? cData : (cData.content || []);
            totalElements = cData.totalElements || (Array.isArray(cData) ? cData.length : 0);
            totalPages = cData.totalPages || 1;
        } catch {
            campaigns = [];
            totalElements = 0;
            totalPages = 0;
        } finally {
            loading = false;
        }
    }

    onMount(loadData);
</script>

<svelte:head>
    <title>Select Campaign Responses — Survey Engine</title>
</svelte:head>

<div class="space-y-6 max-w-4xl mx-auto">
    <div>
        <h1
            class="text-2xl font-bold tracking-tight text-foreground flex items-center gap-2"
        >
            <MessageSquareText class="h-6 w-6" />
            Responses
        </h1>
        <p class="mt-1 text-sm text-muted-foreground">
            Select a campaign to view its collected respondents and their
            individual answers.
        </p>
    </div>

    {#if loading}
        <div class="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
            {#each Array(9) as _}
                <Card.Root>
                    <Card.Header>
                        <div class="space-y-2">
                            <Skeleton class="h-5 w-[180px]" />
                            <Skeleton class="h-4 w-[120px]" />
                        </div>
                    </Card.Header>
                    <Card.Content class="flex justify-end pb-4 pt-1">
                        <Skeleton class="h-9 w-[120px]" />
                    </Card.Content>
                </Card.Root>
            {/each}
        </div>
    {:else if campaigns.length === 0}
        <Card.Root class="py-16 text-center">
            <Card.Content>
                <Megaphone
                    class="mx-auto h-12 w-12 text-muted-foreground/30 mb-4"
                />
                <h3 class="text-lg font-medium">No campaigns found</h3>
                <p class="text-sm text-muted-foreground mt-1">
                    Create and activate a campaign in order to collect
                    responses.
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
                    class="hover:border-primary/50 transition-colors cursor-pointer flex flex-col justify-between"
                    onclick={() => goto(`/campaigns/${campaign.id}/responses`)}
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
                            class="gap-1 bg-muted/30"
                        >
                            View Responses <ArrowRight class="h-4 w-4" />
                        </Button>
                    </Card.Content>
                </Card.Root>
            {/each}
        </div>
        <div class="mt-6 flex justify-center">
            <Pagination
                {currentPage}
                {totalPages}
                {totalElements}
                {pageSize}
                onPageChange={(p) => { currentPage = p; loadData(); }}
                onSizeChange={(s) => { pageSize = s; currentPage = 0; loadData(); }}
            />
        </div>
    {/if}
</div>
