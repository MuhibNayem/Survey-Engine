<script lang="ts">
    import { onMount } from "svelte";
    import api from "$lib/api";
    import PageHeader from "$lib/components/layout/PageHeader.svelte";
    import * as Card from "$lib/components/ui/card";
    import { Button } from "$lib/components/ui/button";
    import { Input } from "$lib/components/ui/input";
    import { Badge } from "$lib/components/ui/badge";
    import {
        Activity,
        Search,
        RefreshCw,
        ChevronLeft,
        ChevronRight,
    } from "lucide-svelte";
    import type { AuditLogEntry, PaginatedResponse } from "$lib/types";

    let loading = $state(true);
    let error = $state<string | null>(null);
    let logsData = $state<PaginatedResponse<AuditLogEntry> | null>(null);

    // Filters & Pagination
    let searchQuery = $state("");
    let actionFilter = $state<string>("");
    let pageNumber = $state(0);
    const pageSize = 15;

    async function loadLogs() {
        loading = true;
        error = null;
        try {
            const params = new URLSearchParams();
            if (searchQuery) params.append("actor", searchQuery);
            if (actionFilter) params.append("action", actionFilter);
            params.append("page", pageNumber.toString());
            params.append("size", pageSize.toString());

            const response = await api.get<PaginatedResponse<AuditLogEntry>>(
                `/audit-logs?${params.toString()}`,
            );
            logsData = response.data;
        } catch (err: unknown) {
            const ae = err as { response?: { data?: { message?: string } } };
            error =
                ae?.response?.data?.message || "Failed to load activity logs";
        } finally {
            loading = false;
        }
    }

    function handleSearch() {
        pageNumber = 0;
        loadLogs();
    }

    function clearFilters() {
        searchQuery = "";
        actionFilter = "";
        pageNumber = 0;
        loadLogs();
    }

    function nextPage() {
        if (logsData && !logsData.last) {
            pageNumber++;
            loadLogs();
        }
    }

    function prevPage() {
        if (pageNumber > 0) {
            pageNumber--;
            loadLogs();
        }
    }

    function formatDate(iso: string) {
        return new Date(iso).toLocaleString("en-US", {
            month: "short",
            day: "numeric",
            year: "numeric",
            hour: "numeric",
            minute: "2-digit",
            second: "2-digit",
        });
    }

    onMount(loadLogs);
</script>

<svelte:head>
    <title>Activity Log — Survey Engine</title>
</svelte:head>

<div class="space-y-6">
    <PageHeader
        title="Activity Log"
        description="Track all actions and changes within your tenant workspace"
        actionLabel="Refresh"
        actionIcon={RefreshCw}
        onAction={loadLogs}
    />

    <!-- Filters -->
    <div class="flex flex-col gap-3 sm:flex-row sm:items-center">
        <div class="relative flex-1">
            <Search
                class="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground"
            />
            <Input
                placeholder="Search by actor..."
                bind:value={searchQuery}
                onkeydown={(e) => e.key === "Enter" && handleSearch()}
                class="pl-9"
            />
        </div>
        <div class="flex space-x-2">
            <Input
                placeholder="Filter action (e.g., LOGIN)..."
                bind:value={actionFilter}
                onkeydown={(e) => e.key === "Enter" && handleSearch()}
                class="w-full sm:w-64"
            />
            <Button variant="secondary" onclick={handleSearch}>Filter</Button>
            {#if searchQuery || actionFilter}
                <Button variant="ghost" onclick={clearFilters}>Clear</Button>
            {/if}
        </div>
    </div>

    {#if loading && !logsData}
        <div class="flex items-center justify-center py-16">
            <span
                class="inline-block h-8 w-8 animate-spin rounded-full border-4 border-primary border-t-transparent"
            ></span>
        </div>
    {:else if error}
        <Card.Root class="border-destructive/50 bg-destructive/5">
            <Card.Content class="py-6 text-center text-destructive">
                <p>{error}</p>
                <Button variant="outline" class="mt-4" onclick={loadLogs}
                    >Try Again</Button
                >
            </Card.Content>
        </Card.Root>
    {:else if !logsData || logsData.content.length === 0}
        <Card.Root
            class="flex flex-col items-center justify-center py-16 text-center"
        >
            <Card.Content>
                <div
                    class="mx-auto mb-4 flex h-16 w-16 items-center justify-center rounded-2xl bg-muted"
                >
                    <Activity class="h-8 w-8 text-muted-foreground" />
                </div>
                <h3 class="text-lg font-semibold text-foreground">
                    No activity logs found
                </h3>
                <p class="mt-1 text-sm text-muted-foreground">
                    {searchQuery || actionFilter
                        ? "Try adjusting your filters."
                        : "Actions taken by users will appear here."}
                </p>
            </Card.Content>
        </Card.Root>
    {:else}
        <Card.Root>
            <div class="overflow-x-auto">
                <table class="w-full text-sm">
                    <thead>
                        <tr
                            class="border-b border-border text-left text-muted-foreground bg-muted/50"
                        >
                            <th class="px-4 py-3 font-medium">Timestamp</th>
                            <th class="px-4 py-3 font-medium">Actor</th>
                            <th class="px-4 py-3 font-medium">Action</th>
                            <th class="px-4 py-3 font-medium">Entity</th>
                            <th class="px-4 py-3 font-medium">Details</th>
                        </tr>
                    </thead>
                    <tbody class="divide-y divide-border/50">
                        {#each logsData.content as log (log.id)}
                            <tr class="transition-colors hover:bg-muted/30">
                                <td
                                    class="px-4 py-3 whitespace-nowrap text-muted-foreground font-mono text-xs"
                                >
                                    {formatDate(log.timestamp)}
                                </td>
                                <td class="px-4 py-3">
                                    <div class="font-medium text-foreground">
                                        {log.actor}
                                    </div>
                                    {#if log.ipAddress}
                                        <div
                                            class="text-xs text-muted-foreground mt-0.5"
                                        >
                                            {log.ipAddress}
                                        </div>
                                    {/if}
                                </td>
                                <td class="px-4 py-3">
                                    <Badge
                                        variant="outline"
                                        class="font-mono text-[10px] tracking-wider bg-primary/10 text-primary border-primary/20"
                                    >
                                        {log.action}
                                    </Badge>
                                </td>
                                <td class="px-4 py-3">
                                    <div class="text-xs">
                                        <span
                                            class="font-medium text-foreground"
                                            >{log.entityType}</span
                                        >
                                        <span
                                            class="text-muted-foreground ml-1 break-all"
                                            >{log.entityId}</span
                                        >
                                    </div>
                                </td>
                                <td
                                    class="px-4 py-3 text-muted-foreground text-xs max-w-xs truncate"
                                    title={log.reason || ""}
                                >
                                    {log.reason || "—"}
                                </td>
                            </tr>
                        {/each}
                    </tbody>
                </table>
            </div>

            <div
                class="flex items-center justify-between border-t border-border px-4 py-3 bg-muted/20"
            >
                <div class="text-xs text-muted-foreground">
                    Page {logsData.number + 1} of {logsData.totalPages} ({logsData.totalElements}
                    records)
                </div>
                <div class="flex items-center gap-2">
                    <Button
                        variant="outline"
                        size="sm"
                        disabled={logsData.first || loading}
                        onclick={prevPage}
                    >
                        <ChevronLeft class="h-4 w-4 mr-1" /> Prev
                    </Button>
                    <Button
                        variant="outline"
                        size="sm"
                        disabled={logsData.last || loading}
                        onclick={nextPage}
                    >
                        Next <ChevronRight class="h-4 w-4 ml-1" />
                    </Button>
                </div>
            </div>
        </Card.Root>
    {/if}
</div>
