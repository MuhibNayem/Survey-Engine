<script lang="ts">
    import api from "$lib/api";
    import { page } from "$app/state";
    import { onMount } from "svelte";
    import { goto } from "$app/navigation";
    import * as Card from "$lib/components/ui/card";
    import { Button } from "$lib/components/ui/button";
    import { Input } from "$lib/components/ui/input";
    import { Badge } from "$lib/components/ui/badge";
    import * as Select from "$lib/components/ui/select";
    import {
        ArrowLeft,
        Search,
        MessageSquareText,
        Eye,
        Clock,
        CheckCircle2,
        Lock,
        RotateCcw,
    } from "lucide-svelte";
    import type {
        CampaignResponse,
        SurveyResponseResponse,
        ResponseStatus,
        DataCollectionFieldType
    } from "$lib/types";

    let campaign = $state<CampaignResponse | null>(null);
    let responses = $state<SurveyResponseResponse[]>([]);
    let loading = $state(true);
    let searchQuery = $state("");
    let statusFilter = $state<ResponseStatus | "">("");
    let metadataFilters = $state<Record<string, string>>({});
    let initialLoadComplete = $state(false);

    const campaignId = $derived(page.params.id);

    const filteredResponses = $derived(
        responses.filter((r) => {
            const matchesSearch =
                !searchQuery ||
                r.respondentIdentifier
                    .toLowerCase()
                    .includes(searchQuery.toLowerCase());
            const matchesStatus = !statusFilter || r.status === statusFilter;
            return matchesSearch && matchesStatus;
        }),
    );

    const statuses: ResponseStatus[] = [
        "IN_PROGRESS",
        "SUBMITTED",
        "LOCKED",
        "REOPENED",
    ];

    function statusBadgeVariant(status: ResponseStatus) {
        switch (status) {
            case "SUBMITTED":
                return "default" as const;
            case "LOCKED":
                return "secondary" as const; // Or a specific color
            case "IN_PROGRESS":
                return "outline" as const;
            case "REOPENED":
                return "destructive" as const;
            default:
                return "secondary" as const;
        }
    }

    function statusIcon(status: ResponseStatus) {
        switch (status) {
            case "SUBMITTED":
                return CheckCircle2;
            case "LOCKED":
                return Lock;
            case "IN_PROGRESS":
                return Clock;
            case "REOPENED":
                return RotateCcw;
            default:
                return MessageSquareText;
        }
    }

    function formatDate(iso: string | null | undefined) {
        if (!iso) return "—";
        return new Date(iso).toLocaleDateString("en-US", {
            month: "short",
            day: "numeric",
            year: "numeric",
            hour: "2-digit",
            minute: "2-digit",
        });
    }

    async function loadResponses() {
        if (!campaignId) return;
        loading = true;
        
        try {
            // Build query params
            const params = new URLSearchParams();
            Object.entries(metadataFilters).forEach(([key, value]) => {
                if (value && value.trim() !== '') {
                    params.append(`metadata.${key}`, value.trim());
                }
            });

            const rRes = await api.get<SurveyResponseResponse[]>(`/responses/campaign/${campaignId}?${params.toString()}`);
            responses = rRes.data;
        } catch {
            responses = [];
        } finally {
            loading = false;
        }
    }

    async function loadData() {
        loading = true;
        try {
            const [cRes, rRes] = await Promise.all([
                api.get<CampaignResponse>(`/campaigns/${campaignId}`),
                api.get<SurveyResponseResponse[]>(`/responses/campaign/${campaignId}`),
            ]);
            campaign = cRes.data;
            responses = rRes.data;
            initialLoadComplete = true;
        } catch {
            goto("/campaigns");
        } finally {
            loading = false;
        }
    }

    onMount(loadData);
    
    // Watch for metadata filter changes after initial data is loaded
    $effect(() => {
        if (initialLoadComplete) {
            // Re-run whenever metadataFilters change deep
            const serializedFilters = JSON.stringify(metadataFilters);
            // using serialized value to trigger derived effect reactively
            if (serializedFilters) {
                // Introduce a tiny debounce naturally by awaiting the load
                loadResponses();
            }
        }
    });
</script>

<svelte:head>
    <title>{campaign?.name ?? "Campaign"} Responses — Survey Engine</title>
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
                onclick={() => goto(`/campaigns/${campaignId}`)}
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
                    </div>
                    <p
                        class="mt-0.5 text-sm text-muted-foreground flex items-center gap-2"
                    >
                        <MessageSquareText class="h-3.5 w-3.5" /> Responses Ledger
                    </p>
                {/if}
            </div>
        </div>
        <div class="flex gap-2">
            <Button
                variant="outline"
                onclick={() => goto(`/campaigns/${campaignId}/analytics`)}
            >
                Analytics
            </Button>
        </div>
    </div>

    <!-- Filters -->
    <div class="flex flex-col gap-3">
        <div class="flex flex-col gap-3 sm:flex-row sm:items-center">
            <div class="relative flex-1">
                <Search class="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
                <Input placeholder="Search by respondent identifier..." bind:value={searchQuery} class="pl-9" />
            </div>
            <Select.Root type="single" bind:value={statusFilter}>
                <Select.Trigger class="w-full sm:w-48">
                    {statusFilter ? statusFilter : "All Statuses"}
                </Select.Trigger>
                <Select.Content>
                    <Select.Item value="">All Statuses</Select.Item>
                    {#each statuses as s}
                        <Select.Item value={s}>{s}</Select.Item>
                    {/each}
                </Select.Content>
            </Select.Root>
        </div>

        <!-- Dynamic Metadata Filters -->
        {#if campaign?.dataCollectionFields && campaign.dataCollectionFields.length > 0}
            <div class="flex flex-row flex-wrap gap-3 items-center pt-2 border-t border-border mt-1">
                <span class="text-xs font-semibold text-muted-foreground uppercase tracking-wider mr-2">Custom Filters:</span>
                {#each campaign.dataCollectionFields as field}
                    <div class="flex items-center gap-2">
                        {#if field.fieldType === 'TEXT' || field.fieldType === 'EMAIL' || field.fieldType === 'PHONE' || field.fieldType === 'NUMBER'}
                            <Input 
                                type={field.fieldType.toLowerCase() === 'number' ? 'number' : 'text'}
                                placeholder={`Filter by ${field.label}...`}
                                bind:value={metadataFilters[field.fieldKey]}
                                class="w-48 h-9 text-sm"
                            />
                        {/if}
                    </div>
                {/each}
                {#if Object.values(metadataFilters).some(v => v !== undefined && v.trim() !== '')}
                    <Button 
                        variant="ghost" 
                        size="sm" 
                        class="h-9 px-2 text-muted-foreground hover:text-foreground"
                        onclick={() => {
                            metadataFilters = {};
                        }}
                    >
                        Clear Filters
                    </Button>
                {/if}
            </div>
        {/if}
    </div>

    <!-- Table -->
    {#if loading}
        <div class="flex items-center justify-center py-16">
            <span
                class="inline-block h-8 w-8 animate-spin rounded-full border-4 border-primary border-t-transparent"
            ></span>
        </div>
    {:else if filteredResponses.length === 0}
        <Card.Root
            class="flex flex-col items-center justify-center py-16 text-center"
        >
            <Card.Content>
                <div
                    class="mx-auto mb-4 flex h-16 w-16 items-center justify-center rounded-2xl bg-muted"
                >
                    <MessageSquareText class="h-8 w-8 text-muted-foreground" />
                </div>
                <h3 class="text-lg font-semibold text-foreground">
                    {searchQuery || statusFilter
                        ? "No responses match your filters"
                        : "No responses yet"}
                </h3>
                <p class="mt-1 text-sm text-muted-foreground">
                    {searchQuery || statusFilter
                        ? "Try adjusting your search or filter."
                        : "When respondents submit the survey, their data will appear here."}
                </p>
            </Card.Content>
        </Card.Root>
    {:else}
        <Card.Root>
            <div class="overflow-x-auto">
                <table class="w-full text-sm">
                    <thead>
                        <tr
                            class="border-b border-border text-left text-muted-foreground"
                        >
                            <th class="px-4 py-3 font-medium">Respondent</th>
                            <th class="px-4 py-3 font-medium">Status</th>
                            <th class="px-4 py-3 font-medium">Started</th>
                            <th class="px-4 py-3 font-medium">Finalized</th>
                            <th class="px-4 py-3 font-medium text-right"
                                >Actions</th
                            >
                        </tr>
                    </thead>
                    <tbody>
                        {#each filteredResponses as response (response.id)}
                            <tr
                                class="border-b border-border/50 transition-colors hover:bg-muted/30"
                            >
                                <td
                                    class="px-4 py-3 font-mono text-xs text-foreground"
                                >
                                    {response.respondentIdentifier}
                                </td>
                                <td class="px-4 py-3">
                                    <Badge
                                        variant={statusBadgeVariant(
                                            response.status,
                                        )}
                                        class="flex w-fit items-center gap-1.5"
                                    >
                                        {#if response.status === "SUBMITTED"}
                                            <CheckCircle2 class="h-3 w-3" />
                                        {:else if response.status === "LOCKED"}
                                            <Lock class="h-3 w-3" />
                                        {:else if response.status === "IN_PROGRESS"}
                                            <Clock class="h-3 w-3" />
                                        {:else if response.status === "REOPENED"}
                                            <RotateCcw class="h-3 w-3" />
                                        {/if}
                                        {response.status}
                                    </Badge>
                                </td>
                                <td
                                    class="px-4 py-3 text-muted-foreground whitespace-nowrap"
                                >
                                    {formatDate(response.startedAt)}
                                </td>
                                <td
                                    class="px-4 py-3 text-muted-foreground whitespace-nowrap"
                                >
                                    {#if response.status === "LOCKED"}
                                        <span
                                            class="text-foreground font-medium"
                                            >{formatDate(
                                                response.lockedAt,
                                            )}</span
                                        >
                                    {:else if response.status === "SUBMITTED"}
                                        {formatDate(response.submittedAt)}
                                    {:else}
                                        —
                                    {/if}
                                </td>
                                <td class="px-4 py-3">
                                    <div
                                        class="flex items-center justify-end gap-1"
                                    >
                                        <Button
                                            variant="ghost"
                                            size="sm"
                                            onclick={() =>
                                                goto(
                                                    `/responses/${response.id}`,
                                                )}
                                        >
                                            <Eye class="h-4 w-4 mr-2" />
                                            View
                                        </Button>
                                    </div>
                                </td>
                            </tr>
                        {/each}
                    </tbody>
                </table>
            </div>
            <div class="border-t border-border px-4 py-3">
                <p class="text-xs text-muted-foreground">
                    {filteredResponses.length} of {responses.length} responses
                </p>
            </div>
        </Card.Root>
    {/if}
</div>
