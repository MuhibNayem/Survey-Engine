<script lang="ts">
    import api from "$lib/api";
    import { page } from "$app/state";
    import { onMount } from "svelte";
    import { goto } from "$app/navigation";
    import * as Card from "$lib/components/ui/card";
    import { Button } from "$lib/components/ui/button";
    import { Badge } from "$lib/components/ui/badge";
    import { ProgressBar } from "$lib/components/ui/progress-bar";
    import {
        ArrowLeft,
        BarChart3,
        Users,
        Clock,
        CheckCircle2,
        Lock,
        MousePointerClick,
        FileDown,
        ArrowRight,
        Sparkles,
    } from "lucide-svelte";
    import type { CampaignResponse, AnalyticsResponse } from "$lib/types";

    let campaign = $state<CampaignResponse | null>(null);
    let analytics = $state<AnalyticsResponse | null>(null);
    let loading = $state(true);
    let error = $state<string | null>(null);

    const campaignId = $derived(page.params.id);

    async function loadAnalytics() {
        loading = true;
        error = null;
        try {
            const [cRes, aRes] = await Promise.all([
                api.get<CampaignResponse>(`/campaigns/${campaignId}`),
                api.get<AnalyticsResponse>(
                    `/responses/analytics/${campaignId}`,
                ),
            ]);
            campaign = cRes.data;
            analytics = aRes.data;
        } catch (err) {
            error = "Failed to load analytics data.";
        } finally {
            loading = false;
        }
    }

    onMount(loadAnalytics);

    function statusBadgeVariant(status: string) {
        switch (status) {
            case "ACTIVE":
                return "default" as const;
            case "COMPLETED":
                return "secondary" as const;
            default:
                return "outline" as const;
        }
    }
</script>

<svelte:head>
    <title>{campaign?.name ?? "Campaign"} Analytics — Survey Engine</title>
</svelte:head>

<div class="space-y-6">
    <!-- Header -->
    <div
        class="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between"
    >
        <div class="flex items-center gap-3">
            <Button
                variant="ghost"
                size="sm"
                onclick={() => goto("/campaigns")}
            >
                <ArrowLeft class="h-4 w-4" />
            </Button>
            <div>
                {#if loading}
                    <div class="h-8 w-48 animate-pulse rounded bg-muted"></div>
                    <div
                        class="mt-1 h-4 w-32 animate-pulse rounded bg-muted"
                    ></div>
                {:else if campaign}
                    <div class="flex items-center gap-2">
                        <h1
                            class="text-2xl font-bold tracking-tight text-foreground"
                        >
                            {campaign.name}
                        </h1>
                        <Badge variant={statusBadgeVariant(campaign.status)}>
                            {campaign.status}
                        </Badge>
                    </div>
                    <p
                        class="mt-0.5 text-sm text-muted-foreground flex items-center gap-2"
                    >
                        <BarChart3 class="h-3.5 w-3.5" /> Analytics Overview
                    </p>
                {/if}
            </div>
        </div>
        <div class="flex gap-2">
            <Button
                variant="outline"
                disabled={loading ||
                    !analytics ||
                    analytics.totalResponses === 0}
            >
                <FileDown class="mr-2 h-4 w-4" />
                Export Data
            </Button>
            {#if campaign}
                <Button
                    variant="secondary"
                    onclick={() =>
                        campaign && goto(`/campaigns/${campaign.id}`)}
                >
                    Campaign Settings
                </Button>
            {/if}
        </div>
    </div>

    {#if loading}
        <div class="flex items-center justify-center py-16">
            <span
                class="inline-block h-8 w-8 animate-spin rounded-full border-4 border-primary border-t-transparent"
            ></span>
        </div>
    {:else if error}
        <Card.Root class="border-destructive/30 bg-destructive/10">
            <Card.Content class="py-10 text-center text-destructive">
                <p>{error}</p>
                <Button variant="outline" class="mt-4" onclick={loadAnalytics}
                    >Retry</Button
                >
            </Card.Content>
        </Card.Root>
    {:else if analytics}
        <!-- Quick Stats -->
        <div class="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
            <Card.Root>
                <Card.Content class="pt-6">
                    <div class="flex items-center justify-between">
                        <div>
                            <p
                                class="text-sm font-medium text-muted-foreground"
                            >
                                Total Responses
                            </p>
                            <p class="mt-1 text-3xl font-bold text-foreground">
                                {analytics.totalResponses}
                            </p>
                        </div>
                        <div
                            class="flex h-12 w-12 items-center justify-center rounded-xl bg-blue-500/10"
                        >
                            <Users class="h-6 w-6 text-blue-500" />
                        </div>
                    </div>
                </Card.Content>
            </Card.Root>

            <Card.Root>
                <Card.Content class="pt-6">
                    <div class="flex items-center justify-between">
                        <div>
                            <p
                                class="text-sm font-medium text-muted-foreground"
                            >
                                Completion Rate
                            </p>
                            <p class="mt-1 text-3xl font-bold text-foreground">
                                {(analytics.completionRate * 100).toFixed(1)}%
                            </p>
                        </div>
                        <div
                            class="flex h-12 w-12 items-center justify-center rounded-xl bg-emerald-500/10"
                        >
                            <CheckCircle2 class="h-6 w-6 text-emerald-500" />
                        </div>
                    </div>
                </Card.Content>
            </Card.Root>

            <Card.Root>
                <Card.Content class="pt-6">
                    <div class="flex items-center justify-between">
                        <div>
                            <p
                                class="text-sm font-medium text-muted-foreground"
                            >
                                In Progress
                            </p>
                            <p class="mt-1 text-3xl font-bold text-foreground">
                                {analytics.inProgressCount}
                            </p>
                        </div>
                        <div
                            class="flex h-12 w-12 items-center justify-center rounded-xl bg-amber-500/10"
                        >
                            <Clock class="h-6 w-6 text-amber-500" />
                        </div>
                    </div>
                </Card.Content>
            </Card.Root>

            <Card.Root>
                <Card.Content class="pt-6">
                    <div class="flex items-center justify-between">
                        <div>
                            <p
                                class="text-sm font-medium text-muted-foreground"
                            >
                                Locked
                            </p>
                            <p class="mt-1 text-3xl font-bold text-foreground">
                                {analytics.lockedCount}
                            </p>
                        </div>
                        <div
                            class="flex h-12 w-12 items-center justify-center rounded-xl bg-purple-500/10"
                        >
                            <Lock class="h-6 w-6 text-purple-500" />
                        </div>
                    </div>
                </Card.Content>
            </Card.Root>
        </div>

        <!-- Funnel & Details -->
        <div class="grid gap-6 lg:grid-cols-2">
            <!-- Response Funnel -->
            <Card.Root>
                <Card.Header>
                    <Card.Title>Response Funnel</Card.Title>
                    <Card.Description
                        >Conversion from start to submission.</Card.Description
                    >
                </Card.Header>
                <Card.Content class="space-y-8">
                    {#if analytics.totalResponses === 0}
                        <div
                            class="flex h-32 items-center justify-center text-sm text-muted-foreground"
                        >
                            No data to display yet.
                        </div>
                    {:else}
                        {@const max = analytics.totalResponses}

                        <!-- Started -->
                        <div class="space-y-2 relative">
                            <div
                                class="flex items-center justify-between text-sm font-medium"
                            >
                                <span class="flex items-center gap-2"
                                    ><MousePointerClick
                                        class="h-4 w-4 text-muted-foreground"
                                    /> Total Started</span
                                >
                                <span>{max}</span>
                            </div>
                            <ProgressBar
                                value={max}
                                {max}
                                colorClass="bg-blue-500"
                            />
                            <p class="text-xs text-muted-foreground text-right">
                                100%
                            </p>
                        </div>

                        <!-- Submitted -->
                        <div class="space-y-2 relative">
                            <div
                                class="flex items-center justify-between text-sm font-medium"
                            >
                                <span class="flex items-center gap-2"
                                    ><CheckCircle2
                                        class="h-4 w-4 text-emerald-500"
                                    /> Completed & Submitted</span
                                >
                                <span
                                    >{analytics.submittedCount +
                                        analytics.lockedCount}</span
                                >
                            </div>
                            <ProgressBar
                                value={analytics.submittedCount +
                                    analytics.lockedCount}
                                {max}
                                colorClass="bg-emerald-500"
                            />
                            <p class="text-xs text-muted-foreground text-right">
                                {Math.round(
                                    ((analytics.submittedCount +
                                        analytics.lockedCount) /
                                        max) *
                                        100,
                                )}% of started
                            </p>
                        </div>

                        <!-- Locked -->
                        {#if analytics.lockedCount > 0}
                            <div
                                class="space-y-2 relative opacity-80 pl-6 border-l-2 border-border ml-2"
                            >
                                <div
                                    class="flex items-center justify-between text-sm font-medium"
                                >
                                    <span class="flex items-center gap-2"
                                        ><Lock
                                            class="h-3 w-3 text-purple-500"
                                        /> Locked (Finalized)</span
                                    >
                                    <span>{analytics.lockedCount}</span>
                                </div>
                                <ProgressBar
                                    value={analytics.lockedCount}
                                    max={analytics.submittedCount +
                                        analytics.lockedCount}
                                    colorClass="bg-purple-500"
                                />
                            </div>
                        {/if}

                        <!-- Drop-off -->
                        {#if analytics.inProgressCount > 0}
                            <div
                                class="space-y-2 relative pt-2 border-t border-border mt-4"
                            >
                                <div
                                    class="flex items-center justify-between text-sm font-medium text-amber-600 dark:text-amber-500"
                                >
                                    <span class="flex items-center gap-2"
                                        ><Clock class="h-4 w-4" /> Incomplete / Dropped
                                        Off</span
                                    >
                                    <span>{analytics.inProgressCount}</span>
                                </div>
                                <p class="text-xs text-muted-foreground">
                                    {Math.round(
                                        (analytics.inProgressCount / max) * 100,
                                    )}% of users started but haven't finished
                                    yet.
                                </p>
                            </div>
                        {/if}
                    {/if}
                </Card.Content>
            </Card.Root>

            <!-- Actionable Links -->
            <div class="space-y-4">
                <Card.Root>
                    <Card.Header>
                        <Card.Title>Individual Responses</Card.Title>
                        <Card.Description
                            >View row-by-row response data and lock status.</Card.Description
                        >
                    </Card.Header>
                    <Card.Content>
                        <p class="text-sm text-muted-foreground mb-4">
                            Navigate to the Response Management page to view
                            individual answers, search for specific respondents,
                            and lock final data for scoring.
                        </p>
                        <Button class="w-full" disabled>
                            View Responses Ledger (Phase 4)
                            <ArrowRight class="ml-2 h-4 w-4" />
                        </Button>
                    </Card.Content>
                </Card.Root>

                <!-- Scoring teaser -->
                <Card.Root>
                    <Card.Header>
                        <Card.Title>Scoring & Weights</Card.Title>
                        <Card.Description
                            >Calculate weighted scores for submitted data.</Card.Description
                        >
                    </Card.Header>
                    <Card.Content>
                        <div
                            class="flex items-center gap-3 mb-4 rounded-lg border border-amber-500/20 bg-amber-500/10 p-3 text-sm text-amber-600 dark:text-amber-400"
                        >
                            <Sparkles class="h-5 w-5 shrink-0" />
                            <p>
                                Lock responses in the ledger before computing
                                final weighted scores.
                            </p>
                        </div>
                        <Button variant="outline" class="w-full" disabled>
                            Go to Scoring Engine (Phase 5)
                            <ArrowRight class="ml-2 h-4 w-4" />
                        </Button>
                    </Card.Content>
                </Card.Root>
            </div>
        </div>
    {/if}
</div>
