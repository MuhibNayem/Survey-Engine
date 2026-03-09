<script lang="ts">
    import api from "$lib/api";
    import { onMount } from "svelte";
    import { goto } from "$app/navigation";
    import * as Card from "$lib/components/ui/card";
    import { Button } from "$lib/components/ui/button";
    import { Input } from "$lib/components/ui/input";
    import { Label } from "$lib/components/ui/label";
    import { Badge } from "$lib/components/ui/badge";
    import * as Select from "$lib/components/ui/select";
    import { Textarea } from "$lib/components/ui/textarea";
    import { ConfirmDialog } from "$lib/components/ui/confirm-dialog";
    import PageHeader from "$lib/components/layout/PageHeader.svelte";
    import Pagination from "$lib/components/ui/pagination/Pagination.svelte";
    import { Skeleton } from "$lib/components/ui/skeleton";
    import {
        Plus,
        Pencil,
        Trash2,
        Search,
        X,
        Megaphone,
        Eye,
        Play,
        BarChart,
    } from "lucide-svelte";
    import type {
        CampaignResponse,
        CampaignStatus,
        AuthMode,
        SurveyResponse,
        PageResponse
    } from "$lib/types";

    // --- State ---
    let campaigns = $state<CampaignResponse[]>([]);
    let surveys = $state<SurveyResponse[]>([]);
    let loading = $state(true);
    let searchQuery = $state("");
    let statusFilter = $state<CampaignStatus | "">("");

    // Pagination specific state
    let currentPage = $state(0);
    let pageSize = $state(10);
    let totalElements = $state(0);
    let totalPages = $state(0);

    // Create/Edit Dialog
    let dialogOpen = $state(false);
    let editingCampaign = $state<CampaignResponse | null>(null);
    let formName = $state("");
    let formDescription = $state("");
    let formSurveyId = $state("");
    let formAuthMode = $state<AuthMode>("PUBLIC");
    let formLoading = $state(false);
    let formError = $state<string | null>(null);

    // Activate
    let activateTarget = $state<CampaignResponse | null>(null);
    let activateLoading = $state(false);

    // Delete
    let deleteTarget = $state<CampaignResponse | null>(null);
    let deleteLoading = $state(false);

    // --- Computed ---
    const filteredCampaigns = $derived(
        campaigns.filter((c) => {
            const matchesSearch =
                !searchQuery ||
                c.name.toLowerCase().includes(searchQuery.toLowerCase());
            const matchesStatus = !statusFilter || c.status === statusFilter;
            return matchesSearch && matchesStatus;
        }),
    );

    const publishedSurveys = $derived(
        surveys.filter((s) => s.lifecycleState === "PUBLISHED"),
    );

    const statuses: CampaignStatus[] = [
        "DRAFT",
        "ACTIVE",
        "PAUSED",
        "COMPLETED",
        "ARCHIVED",
    ];

    function statusBadgeVariant(status: CampaignStatus) {
        switch (status) {
            case "ACTIVE":
                return "default" as const;
            case "DRAFT":
                return "secondary" as const;
            case "PAUSED":
                return "outline" as const;
            case "COMPLETED":
                return "secondary" as const;
            case "ARCHIVED":
                return "outline" as const;
            default:
                return "secondary" as const;
        }
    }

    // --- API ---
    async function loadData() {
        loading = true;
        try {
            const [cRes, sRes] = await Promise.all([
                api.get<PageResponse<CampaignResponse>>(`/campaigns?page=${currentPage}&size=${pageSize}`),
                api.get<PageResponse<SurveyResponse>>("/surveys?size=1000"),
            ]);
            const cData = cRes.data as any;
            campaigns = Array.isArray(cData) ? cData : (cData.content || []);
            totalElements = cData.totalElements || (Array.isArray(cData) ? cData.length : 0);
            totalPages = cData.totalPages || 1;
            
            const sData = sRes.data as any;
            surveys = Array.isArray(sData) ? sData : (sData.content || []);
        } catch {
            campaigns = [];
            totalElements = 0;
            totalPages = 0;
            surveys = [];
        } finally {
            loading = false;
        }
    }

    function openCreateDialog() {
        editingCampaign = null;
        formName = "";
        formDescription = "";
        formSurveyId = "";
        formAuthMode = "PUBLIC";
        formError = null;
        dialogOpen = true;
    }

    function openEditDialog(c: CampaignResponse) {
        editingCampaign = c;
        formName = c.name;
        formDescription = c.description;
        formSurveyId = c.surveyId;
        formAuthMode = c.authMode;
        formError = null;
        dialogOpen = true;
    }

    async function handleSubmit(e: Event) {
        e.preventDefault();
        formLoading = true;
        formError = null;
        try {
            const payload = {
                name: formName,
                description: formDescription,
                surveyId: formSurveyId,
                authMode: formAuthMode,
            };
            if (editingCampaign) {
                await api.put(`/campaigns/${editingCampaign.id}`, payload);
            } else {
                await api.post("/campaigns", payload);
            }
            dialogOpen = false;
            await loadData();
        } catch (err: unknown) {
            const axiosErr = err as {
                response?: { data?: { message?: string } };
            };
            formError =
                axiosErr?.response?.data?.message ?? "Something went wrong.";
        } finally {
            formLoading = false;
        }
    }

    async function handleActivate() {
        if (!activateTarget) return;
        activateLoading = true;
        try {
            await api.post(`/campaigns/${activateTarget.id}/activate`);
            activateTarget = null;
            await loadData();
        } catch {
            // silent
        } finally {
            activateLoading = false;
        }
    }

    async function handleDelete() {
        if (!deleteTarget) return;
        deleteLoading = true;
        try {
            await api.delete(`/campaigns/${deleteTarget.id}`);
            deleteTarget = null;
            await loadData();
        } catch {
            // silent
        } finally {
            deleteLoading = false;
        }
    }

    function getSurveyTitle(id: string): string {
        return surveys.find((s) => s.id === id)?.title ?? "—";
    }

    function formatDate(iso: string) {
        return new Date(iso).toLocaleDateString("en-US", {
            month: "short",
            day: "numeric",
            year: "numeric",
        });
    }

    onMount(loadData);
</script>

<svelte:head>
    <title>Campaigns — Survey Engine</title>
</svelte:head>

<div class="space-y-6">
    <PageHeader
        title="Campaigns"
        description="Create and manage survey deployment campaigns"
        actionLabel="New Campaign"
        actionIcon={Plus}
        onAction={openCreateDialog}
    />

    <!-- Filters -->
    <div class="flex flex-col gap-3 sm:flex-row sm:items-center">
        <div class="relative flex-1">
            <Search
                class="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground"
            />
            <Input
                placeholder="Search campaigns..."
                bind:value={searchQuery}
                class="pl-9"
            />
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

    <!-- Table -->
    {#if loading}
        <Card.Root>
            <div class="overflow-x-auto">
                <table class="w-full text-sm">
                    <thead>
                        <tr class="border-b border-border text-left text-muted-foreground">
                            <th class="px-4 py-3 font-medium">Campaign</th>
                            <th class="px-4 py-3 font-medium">Survey</th>
                            <th class="px-4 py-3 font-medium">Status</th>
                            <th class="px-4 py-3 font-medium">Auth</th>
                            <th class="px-4 py-3 font-medium">Created</th>
                            <th class="px-4 py-3 font-medium text-right">Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        {#each Array(10) as _}
                            <tr class="border-b border-border/50">
                                <td class="px-4 py-3">
                                    <div class="space-y-2">
                                        <Skeleton class="h-4 w-[200px]" />
                                        <Skeleton class="h-3 w-[150px]" />
                                    </div>
                                </td>
                                <td class="px-4 py-3"><Skeleton class="h-4 w-[120px]" /></td>
                                <td class="px-4 py-3"><Skeleton class="h-6 w-[60px]" /></td>
                                <td class="px-4 py-3"><Skeleton class="h-6 w-[50px]" /></td>
                                <td class="px-4 py-3"><Skeleton class="h-4 w-[100px]" /></td>
                                <td class="px-4 py-3 text-right">
                                    <div class="flex items-center justify-end gap-1">
                                        <Skeleton class="h-8 w-8" />
                                        <Skeleton class="h-8 w-8" />
                                        <Skeleton class="h-8 w-8" />
                                    </div>
                                </td>
                            </tr>
                        {/each}
                    </tbody>
                </table>
            </div>
        </Card.Root>
    {:else if filteredCampaigns.length === 0}
        <Card.Root
            class="flex flex-col items-center justify-center py-16 text-center"
        >
            <Card.Content>
                <div
                    class="mx-auto mb-4 flex h-16 w-16 items-center justify-center rounded-2xl bg-muted"
                >
                    <Megaphone class="h-8 w-8 text-muted-foreground" />
                </div>
                <h3 class="text-lg font-semibold text-foreground">
                    {searchQuery || statusFilter
                        ? "No campaigns match your filters"
                        : "No campaigns yet"}
                </h3>
                <p class="mt-1 text-sm text-muted-foreground">
                    {searchQuery || statusFilter
                        ? "Try adjusting your search or filter."
                        : "Create a campaign to start collecting responses."}
                </p>
                {#if !searchQuery && !statusFilter}
                    <Button class="mt-4" onclick={openCreateDialog}>
                        <Plus class="mr-2 h-4 w-4" />
                        Create Campaign
                    </Button>
                {/if}
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
                            <th class="px-4 py-3 font-medium">Campaign</th>
                            <th class="px-4 py-3 font-medium">Survey</th>
                            <th class="px-4 py-3 font-medium">Status</th>
                            <th class="px-4 py-3 font-medium">Auth</th>
                            <th class="px-4 py-3 font-medium">Created</th>
                            <th class="px-4 py-3 font-medium text-right"
                                >Actions</th
                            >
                        </tr>
                    </thead>
                    <tbody>
                        {#each filteredCampaigns as campaign (campaign.id)}
                            <tr
                                class="border-b border-border/50 transition-colors hover:bg-muted/30"
                            >
                                <td class="px-4 py-3">
                                    <div>
                                        <p
                                            class="font-medium text-foreground truncate max-w-xs"
                                        >
                                            {campaign.name}
                                        </p>
                                        {#if campaign.description}
                                            <p
                                                class="text-xs text-muted-foreground truncate max-w-xs mt-0.5"
                                            >
                                                {campaign.description}
                                            </p>
                                        {/if}
                                    </div>
                                </td>
                                <td
                                    class="px-4 py-3 text-muted-foreground truncate max-w-[160px]"
                                >
                                    {getSurveyTitle(campaign.surveyId)}
                                </td>
                                <td class="px-4 py-3">
                                    <Badge
                                        variant={statusBadgeVariant(
                                            campaign.status,
                                        )}
                                    >
                                        {campaign.status}
                                    </Badge>
                                </td>
                                <td class="px-4 py-3">
                                    <Badge
                                        variant={campaign.authMode === "PUBLIC"
                                            ? "secondary"
                                            : "outline"}
                                    >
                                        {campaign.authMode}
                                    </Badge>
                                </td>
                                <td
                                    class="px-4 py-3 text-muted-foreground whitespace-nowrap"
                                >
                                    {formatDate(campaign.createdAt)}
                                </td>
                                <td class="px-4 py-3">
                                    <div
                                        class="flex items-center justify-end gap-1"
                                    >
                                        <Button
                                            variant="ghost"
                                            size="sm"
                                            title="View Details"
                                            onclick={() =>
                                                goto(
                                                    `/campaigns/${campaign.id}`,
                                                )}
                                        >
                                            <Eye class="h-4 w-4" />
                                        </Button>
                                        <Button
                                            variant="ghost"
                                            size="sm"
                                            title="Campaign Analytics"
                                            onclick={() =>
                                                goto(
                                                    `/campaigns/${campaign.id}/analytics`,
                                                )}
                                        >
                                            <BarChart class="h-4 w-4 text-indigo-500" />
                                        </Button>
                                        {#if campaign.status === "DRAFT"}
                                            <Button
                                                variant="ghost"
                                                size="sm"
                                                onclick={() =>
                                                    (activateTarget = campaign)}
                                            >
                                                <Play
                                                    class="h-4 w-4 text-primary"
                                                />
                                            </Button>
                                            <Button
                                                variant="ghost"
                                                size="sm"
                                                onclick={() =>
                                                    openEditDialog(campaign)}
                                            >
                                                <Pencil class="h-4 w-4" />
                                            </Button>
                                        {/if}
                                        <Button
                                            variant="ghost"
                                            size="sm"
                                            onclick={() =>
                                                (deleteTarget = campaign)}
                                        >
                                            <Trash2
                                                class="h-4 w-4 text-destructive"
                                            />
                                        </Button>
                                    </div>
                                </td>
                            </tr>
                        {/each}
                    </tbody>
                </table>
            </div>
            <div class="border-t border-border pt-2 pb-4"></div>
            <Pagination
                {currentPage}
                {totalPages}
                {totalElements}
                {pageSize}
                onPageChange={(p) => { currentPage = p; loadData(); }}
                onSizeChange={(s) => { pageSize = s; currentPage = 0; loadData(); }}
            />
        </Card.Root>
    {/if}
</div>

<!-- Create/Edit Dialog -->
{#if dialogOpen}
    <div class="fixed inset-0 z-50 flex items-center justify-center">
        <button
            class="absolute inset-0 bg-black/50 backdrop-blur-sm"
            onclick={() => (dialogOpen = false)}
            aria-label="Close dialog"
        ></button>
        <Card.Root
            class="relative z-10 w-full max-w-lg mx-4 shadow-2xl border-border"
        >
            <Card.Header>
                <div class="flex items-center justify-between">
                    <div>
                        <Card.Title>
                            {editingCampaign ? "Edit Campaign" : "New Campaign"}
                        </Card.Title>
                        <Card.Description>
                            {editingCampaign
                                ? "Update campaign details."
                                : "Link a published survey and set auth mode."}
                        </Card.Description>
                    </div>
                    <Button
                        variant="ghost"
                        size="sm"
                        onclick={() => (dialogOpen = false)}
                    >
                        <X class="h-4 w-4" />
                    </Button>
                </div>
            </Card.Header>
            <Card.Content>
                <form onsubmit={handleSubmit} class="space-y-4">
                    {#if formError}
                        <div
                            class="rounded-lg border border-destructive/30 bg-destructive/10 px-4 py-3 text-sm text-destructive"
                        >
                            {formError}
                        </div>
                    {/if}

                    <div class="space-y-2">
                        <Label for="c-name">Name</Label>
                        <Input
                            id="c-name"
                            placeholder="e.g., Q1 Employee Survey"
                            bind:value={formName}
                            required
                        />
                    </div>

                    <div class="space-y-2">
                        <Label for="c-desc">Description</Label>
                        <Textarea
                            id="c-desc"
                            placeholder="Describe this campaign..."
                            bind:value={formDescription}
                        />
                    </div>

                    <div class="grid grid-cols-2 gap-4">
                        <div class="space-y-2">
                            <Label for="c-survey">Survey</Label>
                            <Select.Root
                                type="single"
                                bind:value={formSurveyId}
                                required
                            >
                                <Select.Trigger id="c-survey">
                                    {formSurveyId
                                        ? publishedSurveys.find(
                                              (s) => s.id === formSurveyId,
                                          )?.title
                                        : "Select survey"}
                                </Select.Trigger>
                                <Select.Content>
                                    {#each publishedSurveys as s}
                                        <Select.Item value={s.id}
                                            >{s.title}</Select.Item
                                        >
                                    {/each}
                                </Select.Content>
                            </Select.Root>
                            {#if publishedSurveys.length === 0}
                                <p class="text-xs text-muted-foreground">
                                    No published surveys available. Publish a
                                    survey first.
                                </p>
                            {/if}
                        </div>
                        <div class="space-y-2">
                            <Label for="c-auth">Auth Mode</Label>
                            <Select.Root
                                type="single"
                                bind:value={formAuthMode}
                                required
                            >
                                <Select.Trigger id="c-auth">
                                    {formAuthMode === "PUBLIC"
                                        ? "Public"
                                        : "Private"}
                                </Select.Trigger>
                                <Select.Content>
                                    <Select.Item value="PUBLIC"
                                        >Public</Select.Item
                                    >
                                    <Select.Item value="PRIVATE"
                                        >Private</Select.Item
                                    >
                                </Select.Content>
                            </Select.Root>
                        </div>
                    </div>

                    <div class="flex justify-end gap-2 pt-2">
                        <Button
                            variant="outline"
                            type="button"
                            onclick={() => (dialogOpen = false)}
                        >
                            Cancel
                        </Button>
                        <Button type="submit" disabled={formLoading}>
                            {#if formLoading}
                                <span
                                    class="mr-2 inline-block h-4 w-4 animate-spin rounded-full border-2 border-current border-t-transparent"
                                ></span>
                            {/if}
                            {editingCampaign ? "Save Changes" : "Create"}
                        </Button>
                    </div>
                </form>
            </Card.Content>
        </Card.Root>
    </div>
{/if}

<!-- Activate Confirmation -->
{#if activateTarget}
    <div class="fixed inset-0 z-50 flex items-center justify-center">
        <button
            class="absolute inset-0 bg-black/50 backdrop-blur-sm"
            onclick={() => (activateTarget = null)}
            aria-label="Close"
        ></button>
        <Card.Root
            class="relative z-10 w-full max-w-md mx-4 shadow-2xl border-border"
        >
            <Card.Header>
                <Card.Title>Activate Campaign</Card.Title>
                <Card.Description>
                    Activate <span class="font-medium text-foreground"
                        >{activateTarget.name}</span
                    >? This will snapshot the linked survey and make the
                    campaign live.
                </Card.Description>
            </Card.Header>
            <Card.Footer class="flex justify-end gap-2">
                <Button
                    variant="outline"
                    onclick={() => (activateTarget = null)}>Cancel</Button
                >
                <Button onclick={handleActivate} disabled={activateLoading}>
                    {#if activateLoading}
                        <span
                            class="mr-2 inline-block h-4 w-4 animate-spin rounded-full border-2 border-current border-t-transparent"
                        ></span>
                    {/if}
                    <Play class="mr-2 h-4 w-4" />
                    Activate
                </Button>
            </Card.Footer>
        </Card.Root>
    </div>
{/if}

<!-- Delete Dialog -->
<ConfirmDialog
    open={!!deleteTarget}
    title="Delete Campaign"
    description="This will deactivate the campaign. Response data is preserved."
    confirmLabel="Delete"
    loading={deleteLoading}
    onConfirm={handleDelete}
    onCancel={() => (deleteTarget = null)}
/>
