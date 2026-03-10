<script lang="ts">
    import api from "$lib/api";
    import { page } from "$app/state";
    import { onMount } from "svelte";
    import { goto } from "$app/navigation";
    import * as Card from "$lib/components/ui/card";
    import { Button } from "$lib/components/ui/button";
    import { Badge } from "$lib/components/ui/badge";
    import { Textarea } from "$lib/components/ui/textarea";
    import { Input } from "$lib/components/ui/input";
    import { Label } from "$lib/components/ui/label";
    import { ConfirmDialog } from "$lib/components/ui/confirm-dialog";
    import { Skeleton } from "$lib/components/ui/skeleton";
    import {
        ArrowLeft,
        User,
        Clock,
        CheckCircle2,
        Lock,
        RotateCcw,
        Calendar,
        Save,
    } from "lucide-svelte";
    import type {
        SurveyResponseResponse,
        ResponseStatus,
        CampaignResponse,
    } from "$lib/types";

    let responseObj = $state<SurveyResponseResponse | null>(null);
    let campaign = $state<CampaignResponse | null>(null);
    let loading = $state(true);

    // Dialogs
    let confirmLock = $state(false);
    let lockLoading = $state(false);

    let reopenDialogOpen = $state(false);
    let reopenReason = $state("");
    let reopenLoading = $state(false);

    const responseId = $derived(page.params.id);

    const parsedMetadata = $derived.by(() => {
        if (!responseObj?.respondentMetadata) return null;
        try {
            return JSON.parse(responseObj.respondentMetadata) as Record<string, string>;
        } catch {
            return null;
        }
    });

    function statusBadgeVariant(status: ResponseStatus) {
        switch (status) {
            case "SUBMITTED":
                return "default" as const;
            case "LOCKED":
                return "secondary" as const;
            case "IN_PROGRESS":
                return "outline" as const;
            case "REOPENED":
                return "destructive" as const;
            default:
                return "secondary" as const;
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

    function formatDuration(start: string, end: string | null | undefined) {
        if (!end) return "In Progress";
        const ms = Math.max(0, new Date(end).getTime() - new Date(start).getTime());
        const mins = Math.floor(ms / 60000);
        const secs = Math.floor((ms % 60000) / 1000);
        if (mins > 0) return `${mins}m ${secs}s`;
        return `${secs}s`;
    }

    function formatAnswerValue(answer: SurveyResponseResponse["answers"][number]) {
        if (!answer.value) return "—";
        try {
            const parsed = JSON.parse(answer.value);
            if (Array.isArray(parsed)) {
                return parsed.map((item) => String(item)).join(", ");
            }
        } catch {
            // Keep raw value for scalar answers.
        }
        return answer.value;
    }

    async function loadData() {
        loading = true;
        try {
            const { data: resp } = await api.get<SurveyResponseResponse>(
                `/responses/${responseId}`,
            );
            responseObj = resp;

            const { data: camp } = await api.get<CampaignResponse>(
                `/campaigns/${resp.campaignId}`,
            );
            campaign = camp;
        } catch {
            goto("/campaigns");
        } finally {
            loading = false;
        }
    }

    async function handleLock() {
        lockLoading = true;
        try {
            const { data } = await api.post<SurveyResponseResponse>(
                `/responses/${responseId}/lock`,
            );
            responseObj = data;
            confirmLock = false;
        } catch {
            // error handled by api interceptor
        } finally {
            lockLoading = false;
        }
    }

    async function handleReopen(e: Event) {
        e.preventDefault();
        if (!reopenReason.trim()) return;

        reopenLoading = true;
        try {
            const { data } = await api.post<SurveyResponseResponse>(
                `/responses/${responseId}/reopen`,
                {
                    reason: reopenReason,
                },
            );
            responseObj = data;
            reopenDialogOpen = false;
            reopenReason = "";
        } catch {
            // error handled by api interceptor
        } finally {
            reopenLoading = false;
        }
    }

    onMount(loadData);
</script>

<svelte:head>
    <title>Response Viewer — Survey Engine</title>
</svelte:head>

<div class="space-y-6">
    <div
        class="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between"
    >
        <div class="flex items-center gap-3">
            <Button
                variant="ghost"
                size="sm"
                onclick={() => {
                    if (campaign) goto(`/campaigns/${campaign.id}/responses`);
                    else goto("/campaigns");
                }}
            >
                <ArrowLeft class="h-4 w-4" />
            </Button>
            <div>
                {#if loading}
                    <div class="h-8 w-64 animate-pulse rounded bg-muted"></div>
                {:else if responseObj}
                    <div class="flex items-center gap-2">
                        <h1
                            class="text-2xl font-bold tracking-tight text-foreground"
                        >
                            Response Details
                        </h1>
                        <Badge variant={statusBadgeVariant(responseObj.status)}>
                            {responseObj.status}
                        </Badge>
                    </div>
                    <p
                        class="mt-0.5 text-sm text-muted-foreground flex items-center gap-2"
                    >
                        {#if campaign}
                            <span class="font-medium text-foreground"
                                >{campaign.name}</span
                            > •
                        {/if}
                        <User class="h-3.5 w-3.5" />
                        {responseObj.respondentIdentifier}
                    </p>
                {/if}
            </div>
        </div>
        <div class="flex gap-2">
            {#if responseObj}
                {#if responseObj.status === "SUBMITTED" || responseObj.status === "REOPENED"}
                    <Button onclick={() => (confirmLock = true)}>
                        <Lock class="mr-2 h-4 w-4" />
                        Lock Response
                    </Button>
                {:else if responseObj.status === "LOCKED"}
                    <Button
                        variant="outline"
                        onclick={() => (reopenDialogOpen = true)}
                    >
                        <RotateCcw class="mr-2 h-4 w-4" />
                        Reopen Response
                    </Button>
                {/if}
            {/if}
        </div>
    </div>

    {#if loading}
        <div class="grid gap-6 md:grid-cols-3">
            <div class="space-y-6 md:col-span-1">
                <Card.Root>
                    <Card.Header>
                        <Skeleton class="h-6 w-[100px]" />
                    </Card.Header>
                    <Card.Content class="space-y-4">
                        {#each Array(4) as _}
                            <div class="flex justify-between">
                                <Skeleton class="h-4 w-[80px]" />
                                <Skeleton class="h-4 w-[100px]" />
                            </div>
                        {/each}
                    </Card.Content>
                </Card.Root>
            </div>
            <div class="md:col-span-2 space-y-6">
                <Card.Root>
                    <Card.Header>
                        <Skeleton class="h-6 w-[150px]" />
                    </Card.Header>
                    <Card.Content class="space-y-4">
                        {#each Array(5) as _}
                            <div class="space-y-2">
                                <Skeleton class="h-4 w-[200px]" />
                                <Skeleton class="h-10 w-full" />
                            </div>
                        {/each}
                    </Card.Content>
                </Card.Root>
            </div>
        </div>
    {:else if responseObj}
        <div class="grid gap-6 md:grid-cols-3">
            <!-- Metadata Sidebar -->
            <div class="space-y-6 md:col-span-1">
                <Card.Root>
                    <Card.Header>
                        <Card.Title class="text-base flex items-center gap-2">
                            <Clock class="h-4 w-4 text-muted-foreground" />
                            Timeline
                        </Card.Title>
                    </Card.Header>
                    <Card.Content class="space-y-4">
                        <div class="flex justify-between text-sm">
                            <span class="text-muted-foreground">Started</span>
                            <span class="font-medium text-foreground text-right"
                                >{formatDate(responseObj.startedAt)}</span
                            >
                        </div>
                        {#if responseObj.submittedAt}
                            <div class="flex justify-between text-sm">
                                <span
                                    class="text-muted-foreground flex items-center gap-1"
                                    ><CheckCircle2
                                        class="h-3.5 w-3.5 text-emerald-500"
                                    /> Submitted</span
                                >
                                <span
                                    class="font-medium text-foreground text-right"
                                    >{formatDate(responseObj.submittedAt)}</span
                                >
                            </div>
                            <div class="flex justify-between text-sm">
                                <span class="text-muted-foreground"
                                    >Time Taken</span
                                >
                                <span
                                    class="font-medium text-foreground text-right"
                                    >{formatDuration(
                                        responseObj.startedAt,
                                        responseObj.submittedAt,
                                    )}</span
                                >
                            </div>
                        {/if}
                        {#if responseObj.lockedAt}
                            <div
                                class="flex justify-between text-sm pt-4 border-t border-border"
                            >
                                <span
                                    class="text-muted-foreground flex items-center gap-1"
                                    ><Lock
                                        class="h-3.5 w-3.5 text-purple-500"
                                    /> Finalized</span
                                >
                                <span
                                    class="font-medium text-foreground text-right"
                                    >{formatDate(responseObj.lockedAt)}</span
                                >
                            </div>
                        {/if}
                    </Card.Content>
                </Card.Root>

                <Card.Root>
                    <Card.Header>
                        <Card.Title class="text-base flex items-center gap-2">
                            <User class="h-4 w-4 text-muted-foreground" />
                            Respondent
                        </Card.Title>
                    </Card.Header>
                    <Card.Content class="space-y-4">
                        <div class="space-y-1">
                            <p class="text-xs text-muted-foreground">
                                Identifier
                            </p>
                            <p
                                class="text-sm font-mono break-all bg-muted p-2 rounded-md"
                            >
                                {responseObj.respondentIdentifier}
                            </p>
                        </div>
                        <div class="space-y-1">
                            <p class="text-xs text-muted-foreground">
                                Response ID
                            </p>
                            <p
                                class="text-xs font-mono text-muted-foreground break-all"
                            >
                                {responseObj.id}
                            </p>
                        </div>
                        {#if parsedMetadata}
                            <div class="pt-4 border-t border-border space-y-3">
                                <h4 class="text-sm font-semibold text-foreground">Collected Fields</h4>
                                {#each Object.entries(parsedMetadata) as [key, value]}
                                    <div class="space-y-1">
                                        <p class="text-xs text-muted-foreground capitalize">
                                            {key.replace(/_/g, ' ')}
                                        </p>
                                        <p class="text-sm text-foreground break-all">
                                            {value || "—"}
                                        </p>
                                    </div>
                                {/each}
                            </div>
                        {/if}
                    </Card.Content>
                </Card.Root>
            </div>

            <!-- Answers List -->
            <div class="md:col-span-2 space-y-4">
                <h2
                    class="text-lg font-semibold text-foreground flex items-center gap-2"
                >
                    <Save class="h-5 w-5 text-muted-foreground" />
                    Recorded Answers
                </h2>

                {#if responseObj.answers.length === 0}
                    <Card.Root class="py-12 border-dashed">
                        <Card.Content
                            class="flex flex-col items-center justify-center text-center"
                        >
                            <p class="text-sm font-medium text-foreground">
                                No answers recorded yet
                            </p>
                            <p class="text-xs text-muted-foreground">
                                This response is in progress and has no saved
                                data.
                            </p>
                        </Card.Content>
                    </Card.Root>
                {:else}
                    {#each responseObj.answers as answer, i}
                        <Card.Root>
                            <Card.Header
                                class="pb-3 border-b border-border/50 bg-muted/20"
                            >
                                <div
                                    class="flex items-start justify-between gap-4"
                                >
                                    <p
                                        class="text-sm font-medium text-foreground leading-snug"
                                    >
                                        <span class="text-muted-foreground mr-1"
                                            >Q{i + 1}.</span
                                        >
                                        {answer.questionText || `Question ${i + 1}`}
                                    </p>
                                    {#if answer.score !== undefined && answer.score !== null}
                                        <Badge
                                            variant="secondary"
                                            class="font-mono"
                                            >{answer.score} pts</Badge
                                        >
                                    {/if}
                                </div>
                            </Card.Header>
                            <Card.Content class="pt-4">
                                <div class="mb-3 flex flex-wrap gap-2 text-xs text-muted-foreground">
                                    {#if answer.questionVersionNumber !== undefined}
                                        <span>Version {answer.questionVersionNumber}</span>
                                    {/if}
                                    {#if answer.questionType}
                                        <span>{answer.questionType}</span>
                                    {/if}
                                </div>
                                <div
                                    class="rounded-md border border-border bg-background p-3 text-sm text-foreground"
                                >
                                    {formatAnswerValue(answer)}
                                </div>
                                {#if answer.remark}
                                    <div class="mt-3 space-y-1">
                                        <p class="text-xs font-medium text-muted-foreground">Remark</p>
                                        <div class="rounded-md border border-border bg-muted/30 p-3 text-sm text-foreground whitespace-pre-wrap">
                                            {answer.remark}
                                        </div>
                                    </div>
                                {/if}
                            </Card.Content>
                        </Card.Root>
                    {/each}
                {/if}
            </div>
        </div>
    {/if}
</div>

<!-- Lock Dialog -->
<ConfirmDialog
    open={confirmLock}
    title="Lock Response"
    description="This marks the response as finalized. It cannot be edited by the respondent anymore and its data is ready for final scoring."
    confirmLabel="Lock Response"
    loading={lockLoading}
    onConfirm={handleLock}
    onCancel={() => (confirmLock = false)}
/>

<!-- Reopen Dialog -->
{#if reopenDialogOpen}
    <div class="fixed inset-0 z-50 flex items-center justify-center">
        <button
            class="absolute inset-0 bg-black/50 backdrop-blur-sm"
            onclick={() => (reopenDialogOpen = false)}
            aria-label="Close"
        ></button>
        <Card.Root
            class="relative z-10 w-full max-w-md mx-4 shadow-2xl border-border"
        >
            <Card.Header>
                <Card.Title>Reopen Response</Card.Title>
                <Card.Description>
                    Allow this response to be edited again. You must provide a
                    reason.
                </Card.Description>
            </Card.Header>
            <Card.Content>
                <form id="reopenForm" onsubmit={handleReopen} class="space-y-4">
                    <div class="space-y-2">
                        <Label for="r-reason">Reason</Label>
                        <Input
                            id="r-reason"
                            placeholder="e.g., User requested to fix typo in Q3"
                            bind:value={reopenReason}
                            required
                        />
                    </div>
                </form>
            </Card.Content>
            <Card.Footer class="flex justify-end gap-2">
                <Button
                    variant="outline"
                    onclick={() => (reopenDialogOpen = false)}>Cancel</Button
                >
                <Button
                    type="submit"
                    form="reopenForm"
                    disabled={reopenLoading || !reopenReason.trim()}
                    variant="destructive"
                >
                    {#if reopenLoading}
                        <span
                            class="mr-2 inline-block h-4 w-4 animate-spin rounded-full border-2 border-current border-t-transparent"
                        ></span>
                    {/if}
                    <RotateCcw class="mr-2 h-4 w-4" />
                    Reopen
                </Button>
            </Card.Footer>
        </Card.Root>
    </div>
{/if}
