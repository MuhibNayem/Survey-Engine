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
        ShieldAlert,
    } from "lucide-svelte";
    import type { AuditLogEntry, PaginatedResponse } from "$lib/types";

    let loading = $state(true);
    let error = $state<string | null>(null);
    let logsData = $state<PaginatedResponse<AuditLogEntry> | null>(null);

    // Filters & Pagination
    let searchQuery = $state("");
    let actionFilter = $state<string>("");
    let tenantFilter = $state<string>("");
    let pageNumber = $state(0);
    const pageSize = 20;

    async function loadLogs() {
        loading = true;
        error = null;
        try {
            const params = new URLSearchParams();
            if (searchQuery) params.append("actor", searchQuery);
            if (actionFilter) params.append("action", actionFilter);
            if (tenantFilter) params.append("tenantId", tenantFilter);
            params.append("page", pageNumber.toString());
            params.append("size", pageSize.toString());

            const response = await api.get<PaginatedResponse<AuditLogEntry>>(
                `/admin/superadmin/audit-logs?${params.toString()}`,
            );
            logsData = response.data;
        } catch (err: unknown) {
            const ae = err as { response?: { data?: { message?: string } } };
            error =
                ae?.response?.data?.message ||
                "Failed to load platform audit logs";
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
        tenantFilter = "";
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
    <title>Platform Audit Logs — Admin</title>
</svelte:head>

<div class="space-y-6">
    <PageHeader
        title="Platform Audit Logs"
        description="Global system audit trail across all tenants and users"
        actionLabel="Refresh Data"
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
                placeholder="Tenant ID..."
                bind:value={tenantFilter}
                onkeydown={(e) => e.key === "Enter" && handleSearch()}
                class="w-full sm:w-48"
            />
            <Input
                placeholder="Action (e.g., TENANT_SUSPENDED)..."
                bind:value={actionFilter}
                onkeydown={(e) => e.key === "Enter" && handleSearch()}
                class="w-full sm:w-64"
            />
            <Button variant="secondary" onclick={handleSearch}>Apply</Button>
            {#if searchQuery || actionFilter || tenantFilter}
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
                <ShieldAlert class="h-8 w-8 mx-auto mb-2 opacity-50" />
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
                    <ShieldAlert class="h-8 w-8 text-muted-foreground" />
                </div>
                <h3 class="text-lg font-semibold text-foreground">
                    No audit logs found
                </h3>
                <p class="mt-1 text-sm text-muted-foreground">
                    Try adjusting your filters to find specific events.
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
                            <th class="px-4 py-3 font-medium">Tenant</th>
                            <th class="px-4 py-3 font-medium">Actor</th>
                            <th class="px-4 py-3 font-medium">Action</th>
                            <th class="px-4 py-3 font-medium">Entity</th>
                            <th class="px-4 py-3 font-medium">Reason/Details</th
                            >
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
                                    {#if log.tenantId}
                                        <Badge
                                            variant="secondary"
                                            class="font-mono text-[10px] tracking-wider truncate max-w-[120px]"
                                            title={log.tenantId}
                                        >
                                            {log.tenantId.split("-")[0]}..
                                        </Badge>
                                    {:else}
                                        <span
                                            class="text-muted-foreground text-xs italic"
                                            >System</span
                                        >
                                    {/if}
                                </td>
                                <td class="px-4 py-3">
                                    <div class="font-medium text-foreground">
                                        {log.actor}
                                    </div>
                                    {#if log.ipAddress}
                                        <div
                                            class="text-[10px] text-muted-foreground mt-0.5"
                                        >
                                            {log.ipAddress}
                                        </div>
                                    {/if}
                                </td>
                                <td class="px-4 py-3">
                                    <Badge
                                        variant="outline"
                                        class="font-mono text-[10px] tracking-wider {log.tenantId
                                            ? 'bg-primary/10 text-primary border-primary/20'
                                            : 'bg-destructive/10 text-destructive border-destructive/20'}"
                                    >
                                        {log.action}
                                    </Badge>
                                </td>
                                <td class="px-4 py-3">
                                    <div class="text-xs">
                                        <div
                                            class="font-medium text-foreground"
                                        >
                                            {log.entityType}
                                        </div>
                                        <div
                                            class="text-muted-foreground break-all"
                                            title={log.entityId}
                                        >
                                            {log.entityId.substring(0, 8)}..
                                        </div>
                                    </div>
                                </td>
                                <td
                                    class="px-4 py-3 text-muted-foreground text-xs"
                                >
                                    <div
                                        class="max-w-xs truncate"
                                        title={log.reason || ""}
                                    >
                                        {log.reason || "—"}
                                    </div>
                                    {#if log.beforeValue || log.afterValue}
                                        <div class="mt-1 flex gap-1">
                                            {#if log.beforeValue}
                                                <Badge
                                                    variant="outline"
                                                    class="text-[9px] bg-muted/50 border-dashed"
                                                    title={log.beforeValue}
                                                    >Before</Badge
                                                >
                                            {/if}
                                            {#if log.afterValue}
                                                <Badge
                                                    variant="outline"
                                                    class="text-[9px] bg-muted/50 border-dashed"
                                                    title={log.afterValue}
                                                    >After</Badge
                                                >
                                            {/if}
                                        </div>
                                    {/if}
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
