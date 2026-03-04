<script lang="ts">
    import api from "$lib/api";
    import { page } from "$app/state";
    import { onMount } from "svelte";
    import { goto } from "$app/navigation";
    import * as Card from "$lib/components/ui/card";
    import { Button } from "$lib/components/ui/button";
    import { Input } from "$lib/components/ui/input";
    import { Label } from "$lib/components/ui/label";
    import { Badge } from "$lib/components/ui/badge";
    import {
        ArrowLeft,
        Calculator,
        Sparkles,
        CheckCircle2,
        AlertCircle,
    } from "lucide-svelte";
    import type {
        CampaignResponse,
        WeightProfileResponse,
        CategoryResponse,
        ScoreResult,
    } from "$lib/types";

    let campaign = $state<CampaignResponse | null>(null);
    let profile = $state<WeightProfileResponse | null>(null);
    let categories = $state<CategoryResponse[]>([]);
    let loading = $state(true);

    const campaignId = $derived(page.params.id);
    const profileId = $derived(page.params.profileId);

    // Calculation State
    let rawScores = $state<Record<string, number>>({});
    let calculating = $state(false);
    let result = $state<ScoreResult | null>(null);
    let calcError = $state<string | null>(null);

    function getCategoryName(id: string) {
        return categories.find((c) => c.id === id)?.name ?? "Unknown Category";
    }

    async function loadData() {
        loading = true;
        try {
            const [cRes, pRes, catRes] = await Promise.all([
                api.get<CampaignResponse>(`/campaigns/${campaignId}`),
                api.get<WeightProfileResponse>(
                    `/scoring/profiles/${profileId}`,
                ),
                api.get<CategoryResponse[]>("/categories"),
            ]);
            campaign = cRes.data;
            profile = pRes.data;
            categories = catRes.data;

            // Initialize rawScores object for all categories present in the profile
            const initScores: Record<string, number> = {};
            for (const cw of profile.categoryWeights) {
                initScores[cw.categoryId] = 0;
            }
            rawScores = initScores;
        } catch {
            goto(`/campaigns/${campaignId}/scoring`);
        } finally {
            loading = false;
        }
    }

    async function handleCalculate(e: Event) {
        e.preventDefault();
        calculating = true;
        calcError = null;
        result = null;

        try {
            // Send the Map<UUID, BigDecimal> directly
            const { data } = await api.post<ScoreResult>(
                `/scoring/calculate/${profileId}`,
                rawScores,
            );
            result = data;
        } catch (err: any) {
            calcError = err?.response?.data?.message || "Calculation failed.";
        } finally {
            calculating = false;
        }
    }

    onMount(loadData);
</script>

<svelte:head>
    <title>Score Calculator — Survey Engine</title>
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
                onclick={() => goto(`/campaigns/${campaignId}/scoring`)}
            >
                <ArrowLeft class="h-4 w-4" />
            </Button>
            <div>
                {#if loading}
                    <div class="h-8 w-48 animate-pulse rounded bg-muted"></div>
                {:else if campaign && profile}
                    <div class="flex items-center gap-2">
                        <h1
                            class="text-2xl font-bold tracking-tight text-foreground"
                        >
                            Score Calculator
                        </h1>
                    </div>
                    <p
                        class="mt-0.5 text-sm text-muted-foreground flex items-center gap-2"
                    >
                        <Sparkles class="h-3.5 w-3.5" />
                        {campaign.name} • {profile.name}
                    </p>
                {/if}
            </div>
        </div>
    </div>

    {#if loading}
        <div class="flex items-center justify-center py-16">
            <span
                class="inline-block h-8 w-8 animate-spin rounded-full border-4 border-primary border-t-transparent"
            ></span>
        </div>
    {:else if profile}
        <div class="grid gap-6 lg:grid-cols-12 border-t border-border pt-6">
            <!-- Input Form -->
            <div class="lg:col-span-4 space-y-4">
                <h2
                    class="text-lg font-semibold text-foreground flex items-center gap-2"
                >
                    <Calculator class="h-5 w-5 text-muted-foreground" />
                    Test Inputs
                </h2>
                <p class="text-xs text-muted-foreground">
                    Enter hypothetical raw scores for each category to see how
                    the engine calculates the final weighted score.
                </p>

                <form
                    id="calcForm"
                    onsubmit={handleCalculate}
                    class="space-y-4 pt-2"
                >
                    {#each profile.categoryWeights as cw}
                        <div
                            class="space-y-1.5 focus-within:text-primary transition-colors"
                        >
                            <Label for={`cat-${cw.categoryId}`} class="text-sm">
                                {getCategoryName(cw.categoryId)}
                                <span
                                    class="text-xs font-mono text-muted-foreground ml-1 font-normal"
                                    >({cw.weightPercentage}% w)</span
                                >
                            </Label>
                            <Input
                                id={`cat-${cw.categoryId}`}
                                type="number"
                                step="0.01"
                                min="0"
                                bind:value={rawScores[cw.categoryId]}
                                required
                                placeholder="Raw Score"
                            />
                        </div>
                    {/each}

                    <Button
                        type="submit"
                        class="w-full mt-2"
                        disabled={calculating}
                    >
                        {#if calculating}
                            <span
                                class="mr-2 inline-block h-4 w-4 animate-spin rounded-full border-2 border-current border-t-transparent"
                            ></span>
                        {:else}
                            <Calculator class="mr-2 h-4 w-4" />
                        {/if}
                        Calculate Score
                    </Button>
                </form>

                {#if calcError}
                    <div
                        class="flex items-start gap-2 rounded-lg border border-destructive/30 bg-destructive/10 p-3 text-sm text-destructive"
                    >
                        <AlertCircle class="h-4 w-4 shrink-0 mt-0.5" />
                        <p>{calcError}</p>
                    </div>
                {/if}
            </div>

            <!-- Output Result -->
            <div class="lg:col-span-8">
                {#if result}
                    <div class="space-y-6">
                        <Card.Root
                            class="border-amber-500/30 bg-amber-500/5 shadow-inner"
                        >
                            <Card.Content class="pt-6">
                                <div
                                    class="flex flex-col sm:flex-row items-center justify-between text-center sm:text-left gap-4"
                                >
                                    <div>
                                        <p
                                            class="text-sm font-semibold text-amber-600 dark:text-amber-500 uppercase tracking-wider mb-1"
                                        >
                                            Total Weighted Score
                                        </p>
                                        <p
                                            class="text-xs text-muted-foreground"
                                        >
                                            Aggregated across {result
                                                .categoryScores.length} categories.
                                        </p>
                                    </div>
                                    <div
                                        class="text-5xl font-black text-amber-500 tabular-nums"
                                    >
                                        {result.totalScore.toFixed(2)}
                                    </div>
                                </div>
                            </Card.Content>
                        </Card.Root>

                        <Card.Root>
                            <Card.Header>
                                <Card.Title>Calculation Breakdown</Card.Title>
                                <Card.Description
                                    >Row by row mathematics according to the
                                    active profile.</Card.Description
                                >
                            </Card.Header>
                            <Card.Content>
                                <div class="overflow-x-auto">
                                    <table class="w-full text-sm">
                                        <thead>
                                            <tr
                                                class="border-b border-border text-left text-muted-foreground"
                                            >
                                                <th
                                                    class="px-3 py-2 font-medium"
                                                    >Category</th
                                                >
                                                <th
                                                    class="px-3 py-2 font-medium text-right"
                                                    >Raw</th
                                                >
                                                <th
                                                    class="px-3 py-2 font-medium text-right"
                                                    >Max</th
                                                >
                                                <th
                                                    class="px-3 py-2 font-medium text-right border-x border-border/50"
                                                    >Norm</th
                                                >
                                                <th
                                                    class="px-3 py-2 font-medium text-right"
                                                    >Weight</th
                                                >
                                                <th
                                                    class="px-3 py-2 font-medium text-right text-foreground"
                                                    >Weighted</th
                                                >
                                            </tr>
                                        </thead>
                                        <tbody>
                                            {#each result.categoryScores as cs}
                                                <tr
                                                    class="border-b border-border/50 transition-colors hover:bg-muted/30"
                                                >
                                                    <td
                                                        class="px-3 py-2 font-medium text-foreground"
                                                    >
                                                        {getCategoryName(
                                                            cs.categoryId,
                                                        )}
                                                    </td>
                                                    <td
                                                        class="px-3 py-2 text-right tabular-nums"
                                                    >
                                                        {cs.rawScore.toFixed(2)}
                                                    </td>
                                                    <td
                                                        class="px-3 py-2 text-right tabular-nums text-muted-foreground"
                                                    >
                                                        {cs.maxScore.toFixed(2)}
                                                    </td>
                                                    <td
                                                        class="px-3 py-2 text-right tabular-nums border-x border-border/50 bg-muted/10 font-mono text-xs"
                                                    >
                                                        {(
                                                            cs.normalizedScore *
                                                            100
                                                        ).toFixed(1)}%
                                                    </td>
                                                    <td
                                                        class="px-3 py-2 text-right tabular-nums"
                                                    >
                                                        {cs.weightPercentage.toFixed(
                                                            1,
                                                        )}%
                                                    </td>
                                                    <td
                                                        class="px-3 py-2 text-right tabular-nums font-bold text-amber-600 dark:text-amber-500"
                                                    >
                                                        {cs.weightedScore.toFixed(
                                                            2,
                                                        )}
                                                    </td>
                                                </tr>
                                            {/each}
                                        </tbody>
                                    </table>
                                </div>
                            </Card.Content>
                        </Card.Root>
                    </div>
                {:else}
                    <div
                        class="flex h-full min-h-[400px] flex-col items-center justify-center rounded-lg border border-dashed border-border p-8 text-center bg-muted/20"
                    >
                        <Calculator
                            class="mb-4 h-12 w-12 text-muted-foreground/30"
                        />
                        <p class="text-lg font-medium text-foreground">
                            Awaiting Output
                        </p>
                        <p class="text-sm text-muted-foreground max-w-sm mt-1">
                            Fill in the raw scores on the left and click
                            calculate to view the math breakdown.
                        </p>
                    </div>
                {/if}
            </div>
        </div>
    {/if}
</div>
