<script lang="ts">
    import api from "$lib/api";
    import { auth } from "$lib/stores/auth.svelte";
    import { onMount } from "svelte";
    import { goto } from "$app/navigation";
    import * as Card from "$lib/components/ui/card";
    import { Button } from "$lib/components/ui/button";
    import { Input } from "$lib/components/ui/input";
    import { Label } from "$lib/components/ui/label";
    import {
        ShieldAlert,
        Settings2,
        Pencil,
        AlertCircle,
        CheckCircle2,
    } from "lucide-svelte";
    import type { PlanDefinitionResponse } from "$lib/types";

    let plans = $state<PlanDefinitionResponse[]>([]);
    let loading = $state(true);
    let error = $state<string | null>(null);
    let successMessage = $state<string | null>(null);

    // Editor state
    let formOpen = $state(false);
    let editingPlan = $state<PlanDefinitionResponse | null>(null);
    let formLoading = $state(false);

    const isSuperAdmin = $derived(auth.user?.role === "SUPER_ADMIN");

    async function loadPlans() {
        loading = true;
        error = null;
        try {
            const { data } =
                await api.get<PlanDefinitionResponse[]>("/admin/plans");
            plans = data;
        } catch (err) {
            error = "Failed to load system plans. You may not have permission.";
        } finally {
            loading = false;
        }
    }

    function handleEdit(plan: PlanDefinitionResponse) {
        editingPlan = { ...plan }; // clone
        formOpen = true;
        successMessage = null;
        error = null;
    }

    async function handleSave(e: Event) {
        e.preventDefault();
        if (!editingPlan) return;
        formLoading = true;
        error = null;
        successMessage = null;

        try {
            await api.put("/admin/plans", editingPlan);
            successMessage = `Plan ${editingPlan.planCode} successfully updated.`;
            formOpen = false;
            await loadPlans();
        } catch (err: any) {
            error =
                err?.response?.data?.message || "Failed to update plan limits.";
        } finally {
            formLoading = false;
        }
    }

    onMount(() => {
        if (auth.initialized && !isSuperAdmin) {
            // Pre-emptive block if we already know state, but API will block anyway.
        }
        loadPlans();
    });
</script>

<svelte:head>
    <title>System Plans Admin — Survey Engine</title>
</svelte:head>

<div class="space-y-6 max-w-6xl mx-auto">
    <div class="flex items-center justify-between">
        <div>
            <h1
                class="text-2xl font-bold tracking-tight text-foreground flex items-center gap-2"
            >
                System Plans Admin
                {#if isSuperAdmin}
                    <span
                        class="bg-primary/10 text-primary text-[10px] font-bold px-2 py-0.5 rounded-full uppercase tracking-wider"
                        >Super Admin</span
                    >
                {/if}
            </h1>
            <p
                class="mt-0.5 text-sm text-muted-foreground flex items-center gap-2"
            >
                <Settings2 class="h-3.5 w-3.5" /> Define global subscription tiers,
                pricing, and quotas
            </p>
        </div>
    </div>

    {#if !loading && !isSuperAdmin}
        <Card.Root class="border-destructive/30 bg-destructive/10">
            <Card.Content class="py-10 text-center flex flex-col items-center">
                <ShieldAlert class="h-12 w-12 text-destructive mb-4" />
                <h2 class="text-xl font-bold text-destructive mb-2">
                    Access Denied
                </h2>
                <p class="text-sm text-muted-foreground max-w-sm">
                    You must have the SUPER_ADMIN system role to access and
                    modify global billing definitions.
                </p>
                <Button
                    variant="outline"
                    class="mt-6"
                    onclick={() => goto("/dashboard")}
                >
                    Return to Dashboard
                </Button>
            </Card.Content>
        </Card.Root>
    {:else if loading}
        <div class="flex items-center justify-center py-16">
            <span
                class="inline-block h-8 w-8 animate-spin rounded-full border-4 border-primary border-t-transparent"
            ></span>
        </div>
    {:else}
        {#if error && !formOpen}
            <div
                class="flex items-start gap-2 rounded-lg border border-destructive/30 bg-destructive/10 p-3 text-sm text-destructive"
            >
                <AlertCircle class="h-4 w-4 shrink-0 mt-0.5" />
                <p>{error}</p>
            </div>
        {/if}

        {#if successMessage && !formOpen}
            <div
                class="flex items-start gap-2 rounded-lg border border-emerald-500/30 bg-emerald-500/10 p-3 text-sm text-emerald-600 dark:text-emerald-400"
            >
                <CheckCircle2 class="h-4 w-4 shrink-0 mt-0.5" />
                <p>{successMessage}</p>
            </div>
        {/if}

        <div class="grid gap-6 md:grid-cols-2 lg:grid-cols-3">
            {#each plans as plan}
                <Card.Root>
                    <Card.Header class="pb-3 border-b border-border/50">
                        <div class="flex items-start justify-between">
                            <div>
                                <Card.Title class="text-lg"
                                    >{plan.displayName}</Card.Title
                                >
                                <Card.Description class="font-mono text-xs mt-1"
                                    >{plan.planCode}</Card.Description
                                >
                            </div>
                            <Button
                                variant="ghost"
                                size="icon"
                                onclick={() => handleEdit(plan)}
                            >
                                <Pencil class="h-4 w-4" />
                            </Button>
                        </div>
                    </Card.Header>
                    <Card.Content class="pt-4 space-y-4">
                        <div class="flex items-center justify-between text-sm">
                            <span class="text-muted-foreground">Price</span>
                            <span class="font-medium"
                                >{plan.currency} {plan.price}</span
                            >
                        </div>
                        <div class="flex items-center justify-between text-sm">
                            <span class="text-muted-foreground"
                                >Billing Cycle</span
                            >
                            <span class="font-medium"
                                >{plan.billingCycleDays} days</span
                            >
                        </div>
                        <div class="flex items-center justify-between text-sm">
                            <span class="text-muted-foreground">Free Trial</span
                            >
                            <span class="font-medium"
                                >{plan.trialDays} days</span
                            >
                        </div>

                        <div class="pt-4 border-t border-border/50 space-y-2">
                            <p
                                class="text-xs font-semibold text-muted-foreground uppercase tracking-wider mb-2"
                            >
                                Resource Quotas
                            </p>

                            <div
                                class="flex justify-between text-sm items-center"
                            >
                                <span class="text-muted-foreground"
                                    >Max Campaigns</span
                                >
                                <span class="font-mono bg-muted px-1.5 rounded"
                                    >{plan.maxCampaigns ?? "Unlimited"}</span
                                >
                            </div>
                            <div
                                class="flex justify-between text-sm items-center"
                            >
                                <span class="text-muted-foreground"
                                    >Responses/Campaign</span
                                >
                                <span class="font-mono bg-muted px-1.5 rounded"
                                    >{plan.maxResponsesPerCampaign ??
                                        "Unlimited"}</span
                                >
                            </div>
                            <div
                                class="flex justify-between text-sm items-center"
                            >
                                <span class="text-muted-foreground"
                                    >Admin Users</span
                                >
                                <span class="font-mono bg-muted px-1.5 rounded"
                                    >{plan.maxAdminUsers ?? "Unlimited"}</span
                                >
                            </div>
                        </div>
                        <p
                            class="text-[10px] text-muted-foreground text-center pt-2"
                        >
                            -1 indicates an unlimited quota.
                        </p>
                    </Card.Content>
                </Card.Root>
            {/each}
        </div>
    {/if}
</div>

{#if formOpen && editingPlan}
    <div class="fixed inset-0 z-50 flex items-center justify-center">
        <button
            class="absolute inset-0 bg-black/50 backdrop-blur-sm"
            onclick={() => (formOpen = false)}
            aria-label="Close"
        ></button>
        <Card.Root
            class="relative z-10 w-full max-w-2xl mx-4 shadow-2xl border-border max-h-[90vh] flex flex-col"
        >
            <Card.Header>
                <Card.Title>Edit Plan: {editingPlan.displayName}</Card.Title>
                <Card.Description>
                    Adjust pricing and quota limits for the {editingPlan.planCode}
                    tier. These changes affect all new subscriptions.
                </Card.Description>
            </Card.Header>
            <div class="overflow-y-auto px-6 py-2 flex-1">
                <form
                    id="planForm"
                    onsubmit={handleSave}
                    class="space-y-6 pb-4"
                >
                    {#if error}
                        <div
                            class="rounded-lg border border-destructive/30 bg-destructive/10 px-4 py-3 text-sm text-destructive"
                        >
                            {error}
                        </div>
                    {/if}

                    <div class="grid sm:grid-cols-2 gap-4">
                        <div class="space-y-2">
                            <Label for="pl-display">Display Name</Label>
                            <Input
                                id="pl-display"
                                bind:value={editingPlan.displayName}
                                required
                            />
                        </div>
                        <div class="space-y-2">
                            <Label for="pl-code">Plan Code (Constant)</Label>
                            <Input
                                id="pl-code"
                                value={editingPlan.planCode}
                                disabled
                                class="bg-muted font-mono"
                            />
                        </div>
                    </div>

                    <div class="grid sm:grid-cols-3 gap-4">
                        <div class="space-y-2">
                            <Label for="pl-price">Price</Label>
                            <Input
                                id="pl-price"
                                type="number"
                                step="0.01"
                                bind:value={editingPlan.price}
                                required
                            />
                        </div>
                        <div class="space-y-2">
                            <Label for="pl-currency">Currency (ISO)</Label>
                            <Input
                                id="pl-currency"
                                bind:value={editingPlan.currency}
                                required
                            />
                        </div>
                        <div class="space-y-2">
                            <Label for="pl-cycle">Billing Cycle (Days)</Label>
                            <Input
                                id="pl-cycle"
                                type="number"
                                bind:value={editingPlan.billingCycleDays}
                                required
                            />
                        </div>
                    </div>

                    <div class="space-y-2">
                        <Label for="pl-trial">Trial Days</Label>
                        <Input
                            id="pl-trial"
                            type="number"
                            bind:value={editingPlan.trialDays}
                            required
                        />
                    </div>

                    <div class="pt-4 border-t border-border space-y-4">
                        <h3 class="text-sm font-semibold text-foreground">
                            Resource Quotas
                        </h3>
                        <p class="text-xs text-muted-foreground mb-4">
                            Leave blank or set to -1 for unlimited usage.
                        </p>

                        <div class="grid sm:grid-cols-3 gap-4">
                            <div class="space-y-2">
                                <Label for="q-camps">Max Campaigns</Label>
                                <Input
                                    id="q-camps"
                                    type="number"
                                    bind:value={editingPlan.maxCampaigns}
                                />
                            </div>
                            <div class="space-y-2">
                                <Label for="q-resp"
                                    >Max Responses/Campaign</Label
                                >
                                <Input
                                    id="q-resp"
                                    type="number"
                                    bind:value={
                                        editingPlan.maxResponsesPerCampaign
                                    }
                                />
                            </div>
                            <div class="space-y-2">
                                <Label for="q-users">Max Admin Users</Label>
                                <Input
                                    id="q-users"
                                    type="number"
                                    bind:value={editingPlan.maxAdminUsers}
                                />
                            </div>
                        </div>
                    </div>
                </form>
            </div>
            <Card.Footer class="flex justify-end gap-2 border-t pt-4">
                <Button variant="outline" onclick={() => (formOpen = false)}
                    >Cancel</Button
                >
                <Button type="submit" form="planForm" disabled={formLoading}>
                    {#if formLoading}
                        <span
                            class="mr-2 inline-block h-4 w-4 animate-spin rounded-full border-2 border-current border-t-transparent"
                        ></span>
                    {/if}
                    Save Plan Definition
                </Button>
            </Card.Footer>
        </Card.Root>
    </div>
{/if}
