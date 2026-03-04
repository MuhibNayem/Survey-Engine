<script lang="ts">
    import api from "$lib/api";
    import { page } from "$app/state";
    import { onMount } from "svelte";
    import { goto } from "$app/navigation";
    import * as Card from "$lib/components/ui/card";
    import { Button } from "$lib/components/ui/button";
    import { Badge } from "$lib/components/ui/badge";
    import { Input } from "$lib/components/ui/input";
    import { Label } from "$lib/components/ui/label";
    import { ConfirmDialog } from "$lib/components/ui/confirm-dialog";
    import {
        ArrowLeft,
        Sparkles,
        Plus,
        Calculator,
        CheckCircle2,
        XCircle,
        Trash2,
        Pencil,
        Percent,
    } from "lucide-svelte";
    import type {
        CampaignResponse,
        WeightProfileResponse,
        CategoryResponse,
    } from "$lib/types";

    let campaign = $state<CampaignResponse | null>(null);
    let profiles = $state<WeightProfileResponse[]>([]);
    let categories = $state<CategoryResponse[]>([]);
    let loading = $state(true);

    const campaignId = $derived(page.params.id);

    // Editor State
    let formOpen = $state(false);
    let editingId = $state<string | null>(null);
    let formName = $state("");
    let formWeights = $state<
        { categoryId: string; weightPercentage: number }[]
    >([]);
    let formLoading = $state(false);
    let formError = $state<string | null>(null);
    let currentValidation = $state<{ valid: boolean; message: string } | null>(
        null,
    );

    // Delete State
    let deleteTarget = $state<WeightProfileResponse | null>(null);
    let deleteLoading = $state(false);

    const totalWeight = $derived(
        formWeights.reduce(
            (sum, w) => sum + (Number(w.weightPercentage) || 0),
            0,
        ),
    );

    const is100Percent = $derived(totalWeight === 100);

    function getCategoryName(id: string) {
        return categories.find((c) => c.id === id)?.name ?? "Unknown Category";
    }

    async function loadData() {
        loading = true;
        try {
            const [cRes, pRes, catRes] = await Promise.all([
                api.get<CampaignResponse>(`/campaigns/${campaignId}`),
                api.get<WeightProfileResponse[]>(
                    `/scoring/profiles/campaign/${campaignId}`,
                ),
                api.get<CategoryResponse[]>("/categories"),
            ]);
            campaign = cRes.data;
            profiles = pRes.data;
            categories = catRes.data;
        } catch {
            goto("/campaigns");
        } finally {
            loading = false;
        }
    }

    function handleCreate() {
        editingId = null;
        formName = "";
        formError = null;
        currentValidation = null;

        // Auto-populate with active categories, splitting 100 evenly if possible
        const activeCats = categories.filter((c) => c.active);
        if (activeCats.length > 0) {
            const split = Math.floor(100 / activeCats.length);
            // Give the remainder to the first one just to make it cleanly 100 if we want, or leave it to the user.
            // Actually just default 0 and let user fill it to enforce manual review.
            formWeights = activeCats.map((c) => ({
                categoryId: c.id,
                weightPercentage: 0,
            }));
        }

        formOpen = true;
    }

    function handleEdit(profile: WeightProfileResponse) {
        editingId = profile.id;
        formName = profile.name;
        formError = null;
        currentValidation = null;
        // Clone weights
        formWeights = profile.categoryWeights.map((cw) => ({ ...cw }));
        formOpen = true;
    }

    async function handleValidate() {
        if (!editingId && formWeights.length > 0) {
            // Fake validation if not saved yet (API needs profile ID).
            // We can just rely on frontend UI to block save if not 100%.
            currentValidation = {
                valid: is100Percent,
                message: is100Percent
                    ? "Weights sum to exactly 100%."
                    : `Weights sum to ${totalWeight}%, but must be exactly 100%.`,
            };
            return;
        } else if (editingId) {
            try {
                await api.post(`/scoring/profiles/${editingId}/validate`);
                currentValidation = {
                    valid: true,
                    message: "Profile weight distribution is valid.",
                };
            } catch {
                currentValidation = {
                    valid: false,
                    message: "Validation check failed.",
                };
            }
        }
    }

    async function handleSave(e: Event) {
        e.preventDefault();
        if (!is100Percent) {
            formError = `Total weight must be exactly 100%. Current: ${totalWeight}%`;
            return;
        }

        formLoading = true;
        formError = null;

        const payload = {
            name: formName,
            campaignId: campaignId,
            categoryWeights: formWeights.map((w) => ({
                categoryId: w.categoryId,
                weightPercentage: Number(w.weightPercentage),
            })),
        };

        try {
            if (editingId) {
                await api.put(`/scoring/profiles/${editingId}`, payload);
            } else {
                await api.post("/scoring/profiles", payload);
            }
            formOpen = false;
            await loadData();
        } catch (err: any) {
            formError =
                err?.response?.data?.message || "Failed to save profile.";
        } finally {
            formLoading = false;
        }
    }

    async function handleDelete() {
        if (!deleteTarget) return;
        deleteLoading = true;
        try {
            await api.delete(`/scoring/profiles/${deleteTarget.id}`);
            deleteTarget = null;
            await loadData();
        } catch {
            // silent
        } finally {
            deleteLoading = false;
        }
    }

    onMount(loadData);
</script>

<svelte:head>
    <title>Scoring Profiles — Survey Engine</title>
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
                        <Sparkles class="h-3.5 w-3.5" /> Scoring Engine Profiles
                    </p>
                {/if}
            </div>
        </div>
        <div class="flex gap-2">
            <Button onclick={handleCreate}>
                <Plus class="mr-2 h-4 w-4" />
                New Profile
            </Button>
        </div>
    </div>

    {#if loading}
        <div class="flex items-center justify-center py-16">
            <span
                class="inline-block h-8 w-8 animate-spin rounded-full border-4 border-primary border-t-transparent"
            ></span>
        </div>
    {:else if profiles.length === 0}
        <Card.Root
            class="flex flex-col items-center justify-center py-16 text-center"
        >
            <Card.Content>
                <div
                    class="mx-auto mb-4 flex h-16 w-16 items-center justify-center rounded-2xl bg-muted"
                >
                    <Sparkles class="h-8 w-8 text-amber-500/50" />
                </div>
                <h3 class="text-lg font-semibold text-foreground">
                    No scoring profiles
                </h3>
                <p class="mt-1 text-sm text-muted-foreground max-w-sm">
                    Define mathematical weight mapping profiles to calculate
                    final weighted scores from survey results.
                </p>
                <Button class="mt-4" onclick={handleCreate}>
                    <Plus class="mr-2 h-4 w-4" />
                    Create Profile
                </Button>
            </Card.Content>
        </Card.Root>
    {:else}
        <div class="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
            {#each profiles as profile}
                <Card.Root class={!profile.active ? "opacity-60" : ""}>
                    <Card.Header class="pb-3 border-b border-border/50">
                        <div class="flex items-start justify-between">
                            <div>
                                <Card.Title class="text-base"
                                    >{profile.name}</Card.Title
                                >
                                {#if !profile.active}
                                    <Badge variant="outline" class="mt-1"
                                        >Inactive</Badge
                                    >
                                {/if}
                            </div>
                            <div class="flex gap-1">
                                <Button
                                    variant="ghost"
                                    size="icon"
                                    class="h-8 w-8"
                                    onclick={() =>
                                        goto(
                                            `/campaigns/${campaignId}/scoring/${profile.id}/calculate`,
                                        )}
                                    title="Test Calculation"
                                >
                                    <Calculator class="h-4 w-4 text-primary" />
                                </Button>
                                <Button
                                    variant="ghost"
                                    size="icon"
                                    class="h-8 w-8"
                                    onclick={() => handleEdit(profile)}
                                >
                                    <Pencil class="h-4 w-4" />
                                </Button>
                                <Button
                                    variant="ghost"
                                    size="icon"
                                    class="h-8 w-8"
                                    onclick={() => (deleteTarget = profile)}
                                >
                                    <Trash2 class="h-4 w-4 text-destructive" />
                                </Button>
                            </div>
                        </div>
                    </Card.Header>
                    <Card.Content class="pt-4">
                        <div class="space-y-3">
                            <p
                                class="text-xs font-semibold text-muted-foreground uppercase tracking-wider"
                            >
                                Weight Distribution
                            </p>
                            {#each profile.categoryWeights as cw}
                                <div
                                    class="flex items-center justify-between text-sm"
                                >
                                    <span class="truncate pr-4"
                                        >{getCategoryName(cw.categoryId)}</span
                                    >
                                    <span
                                        class="font-mono bg-muted px-1.5 rounded"
                                        >{cw.weightPercentage}%</span
                                    >
                                </div>
                            {/each}
                        </div>
                    </Card.Content>
                </Card.Root>
            {/each}
        </div>
    {/if}
</div>

<!-- Editor Dialog -->
{#if formOpen}
    <div class="fixed inset-0 z-50 flex items-center justify-center">
        <button
            class="absolute inset-0 bg-black/50 backdrop-blur-sm"
            onclick={() => (formOpen = false)}
            aria-label="Close"
        ></button>
        <Card.Root
            class="relative z-10 w-full max-w-lg mx-4 shadow-2xl border-border max-h-[90vh] flex flex-col"
        >
            <Card.Header>
                <Card.Title
                    >{editingId
                        ? "Edit Weight Profile"
                        : "New Weight Profile"}</Card.Title
                >
                <Card.Description>
                    Assign percentages to categories. The sum must equal exactly
                    100%.
                </Card.Description>
            </Card.Header>
            <div class="overflow-y-auto px-6 py-2 flex-1">
                <form
                    id="profileForm"
                    onsubmit={handleSave}
                    class="space-y-6 pb-4"
                >
                    {#if formError}
                        <div
                            class="rounded-lg border border-destructive/30 bg-destructive/10 px-4 py-3 text-sm text-destructive"
                        >
                            {formError}
                        </div>
                    {/if}

                    <div class="space-y-2">
                        <Label for="p-name">Profile Name</Label>
                        <Input
                            id="p-name"
                            placeholder="e.g., Q3 Bonus Weighting Model"
                            bind:value={formName}
                            required
                        />
                    </div>

                    <div class="space-y-4">
                        <div class="flex items-center justify-between">
                            <Label>Category Weights</Label>
                            <div
                                class={is100Percent
                                    ? "text-emerald-500 font-bold"
                                    : "text-amber-500 font-bold"}
                            >
                                Total: {totalWeight}%
                            </div>
                        </div>

                        <div
                            class="bg-muted/30 p-4 rounded-lg space-y-3 border border-border"
                        >
                            {#each formWeights as cw, i}
                                <div class="flex items-center gap-3">
                                    <div class="flex-1 min-w-0">
                                        <p class="text-sm font-medium truncate">
                                            {getCategoryName(cw.categoryId)}
                                        </p>
                                    </div>
                                    <div class="w-24 relative">
                                        <Input
                                            type="number"
                                            min="0"
                                            max="100"
                                            bind:value={cw.weightPercentage}
                                            class="pr-7 text-right"
                                            required
                                        />
                                        <Percent
                                            class="absolute right-2.5 top-1/2 -translate-y-1/2 h-3.5 w-3.5 text-muted-foreground pointer-events-none"
                                        />
                                    </div>
                                </div>
                            {/each}
                        </div>

                        <div
                            class="flex items-center justify-between rounded-md border p-3"
                        >
                            <div class="flex items-center gap-2">
                                {#if currentValidation}
                                    {#if currentValidation.valid}
                                        <CheckCircle2
                                            class="h-5 w-5 text-emerald-500"
                                        />
                                        <span
                                            class="text-sm text-emerald-500 font-medium"
                                            >{currentValidation.message}</span
                                        >
                                    {:else}
                                        <XCircle
                                            class="h-5 w-5 text-destructive"
                                        />
                                        <span
                                            class="text-sm text-destructive font-medium"
                                            >{currentValidation.message}</span
                                        >
                                    {/if}
                                {:else if is100Percent}
                                    <CheckCircle2
                                        class="h-5 w-5 text-emerald-500"
                                    />
                                    <span
                                        class="text-sm text-emerald-500 font-medium"
                                        >Sum strictly equals 100%</span
                                    >
                                {:else}
                                    <XCircle class="h-5 w-5 text-amber-500" />
                                    <span
                                        class="text-sm text-amber-500 font-medium"
                                        >Sum must equal 100% (currently {totalWeight}%)</span
                                    >
                                {/if}
                            </div>
                            <Button
                                variant="outline"
                                size="sm"
                                type="button"
                                onclick={handleValidate}
                            >
                                Verify Logic
                            </Button>
                        </div>
                    </div>
                </form>
            </div>
            <Card.Footer class="flex justify-end gap-2 border-t pt-4">
                <Button variant="outline" onclick={() => (formOpen = false)}
                    >Cancel</Button
                >
                <Button
                    type="submit"
                    form="profileForm"
                    disabled={formLoading || !is100Percent}
                >
                    {#if formLoading}
                        <span
                            class="mr-2 inline-block h-4 w-4 animate-spin rounded-full border-2 border-current border-t-transparent"
                        ></span>
                    {/if}
                    {editingId ? "Save Changes" : "Create Profile"}
                </Button>
            </Card.Footer>
        </Card.Root>
    </div>
{/if}

<ConfirmDialog
    open={!!deleteTarget}
    title="Delete Weight Profile"
    description="Are you sure you want to logically delete this scoring profile?"
    confirmLabel="Delete"
    loading={deleteLoading}
    onConfirm={handleDelete}
    onCancel={() => (deleteTarget = null)}
/>
