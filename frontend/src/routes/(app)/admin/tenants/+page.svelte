<script lang="ts">
    import { onMount } from "svelte";
    import * as DropdownMenu from "$lib/components/ui/dropdown-menu";
    import { Button } from "$lib/components/ui/button";
    import { Badge } from "$lib/components/ui/badge";
    import { Input } from "$lib/components/ui/input";
    import {
        Search,
        MoreHorizontal,
        ShieldAlert,
        LogIn,
        Edit,
        Ban,
        CheckCircle2,
    } from "lucide-svelte";
    import api from "$lib/api";
    import { auth } from "$lib/stores/auth.svelte";
    import { goto } from "$app/navigation";
    import type {
        TenantOverviewResponse,
        SubscriptionPlan,
        AuthUserResponse,
    } from "$lib/types";

    let tenants = $state<TenantOverviewResponse[]>([]);
    let loading = $state(true);
    let error = $state<string | null>(null);
    let actionLoading = $state(false);

    // Override Subscription Modal State
    let overrideModalOpen = $state(false);
    let overrideTargetTenant = $state<TenantOverviewResponse | null>(null);
    let overridePlan = $state<
        { label: string; value: SubscriptionPlan } | undefined
    >(undefined);

    const plans: { label: string; value: SubscriptionPlan }[] = [
        { label: "BASIC", value: "BASIC" },
        { label: "PRO", value: "PRO" },
        { label: "ENTERPRISE", value: "ENTERPRISE" },
    ];

    onMount(async () => {
        if (!auth.isAuthenticated || auth.user?.role !== "SUPER_ADMIN") {
            goto("/dashboard");
            return;
        }
        await loadTenants();
    });

    async function loadTenants() {
        loading = true;
        error = null;
        try {
            const { data } = await api.get<TenantOverviewResponse[]>(
                "/admin/superadmin/tenants",
            );
            tenants = data;
        } catch (err: any) {
            error = "Failed to load tenants data.";
            console.error(err);
        } finally {
            loading = false;
        }
    }

    async function toggleTenantStatus(tenant: TenantOverviewResponse) {
        actionLoading = true;
        try {
            if (tenant.active) {
                await api.put(
                    `/admin/superadmin/tenants/${tenant.tenantId}/suspend`,
                );
            } else {
                await api.put(
                    `/admin/superadmin/tenants/${tenant.tenantId}/activate`,
                );
            }
            await loadTenants();
        } catch (err: any) {
            alert("Failed to update tenant status.");
        } finally {
            actionLoading = false;
        }
    }

    async function impersonateTenant(tenant: TenantOverviewResponse) {
        actionLoading = true;
        try {
            const { data } = await api.post<AuthUserResponse>(
                `/admin/superadmin/tenants/${tenant.tenantId}/impersonate`,
            );
            window.location.href = "/dashboard";
        } catch (err: any) {
            alert(
                "Failed to impersonate tenant. They might not have an ADMIN user.",
            );
            actionLoading = false;
        }
    }

    function openOverrideModal(tenant: TenantOverviewResponse) {
        overrideTargetTenant = tenant;
        overridePlan = plans.find((p) => p.value === tenant.plan) || plans[0];
        overrideModalOpen = true;
    }

    async function overrideSubscription() {
        if (!overrideTargetTenant || !overridePlan) return;

        actionLoading = true;
        try {
            await api.post(
                `/admin/superadmin/tenants/${overrideTargetTenant.tenantId}/subscriptions/override`,
                {
                    plan: overridePlan.value,
                },
            );
            overrideModalOpen = false;
            await loadTenants();
        } catch (err: any) {
            alert("Failed to override subscription.");
        } finally {
            actionLoading = false;
        }
    }

    function getPlanBadgeColor(plan: SubscriptionPlan | null) {
        if (!plan) return "bg-gray-100 text-gray-800";
        switch (plan) {
            case "ENTERPRISE":
                return "bg-purple-100 text-purple-800 border-purple-200";
            case "PRO":
                return "bg-blue-100 text-blue-800 border-blue-200";
            case "BASIC":
                return "bg-green-100 text-green-800 border-green-200";
            default:
                return "bg-gray-100 text-gray-800";
        }
    }

    function formatDate(dateStr: string) {
        return new Date(dateStr).toLocaleDateString(undefined, {
            year: "numeric",
            month: "short",
            day: "numeric",
        });
    }
</script>

<svelte:head>
    <title>Tenant Management — Super Admin</title>
</svelte:head>

<div class="space-y-6 max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-6">
    <div
        class="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-4"
    >
        <div>
            <h1
                class="text-2xl sm:text-3xl font-bold tracking-tight text-foreground"
            >
                Tenant Management
            </h1>
            <p class="text-sm text-muted-foreground mt-1">
                View, manage, and impersonate customer instances across the
                platform.
            </p>
        </div>
        <Badge
            variant="outline"
            class="w-fit border-indigo-200 bg-indigo-50 text-indigo-700 font-medium px-3 py-1 shrink-0"
        >
            <ShieldAlert class="mr-1.5 h-3.5 w-3.5" />
            Enterprise Admin Tool
        </Badge>
    </div>

    {#if error}
        <div
            class="bg-destructive/10 border-l-4 border-destructive text-destructive p-4 rounded-md"
        >
            {error}
        </div>
    {/if}

    <div
        class="rounded-xl border border-border/60 bg-card shadow-sm overflow-hidden"
    >
        <div
            class="p-4 border-b border-border/50 flex flex-col sm:flex-row justify-between items-start sm:items-center bg-muted/20 gap-4"
        >
            <div
                class="flex items-center w-full sm:w-72 h-10 rounded-md border border-input bg-background px-3 focus-within:ring-2 focus-within:ring-ring focus-within:ring-offset-2 transition-shadow shadow-sm"
            >
                <Search class="mr-2 h-4 w-4 shrink-0 text-muted-foreground" />
                <input
                    type="text"
                    placeholder="Search tenants..."
                    class="flex h-full w-full rounded-md bg-transparent text-sm md:text-sm placeholder:text-muted-foreground outline-none disabled:cursor-not-allowed disabled:opacity-50 min-w-0"
                />
            </div>
            <div class="text-sm text-muted-foreground whitespace-nowrap">
                {tenants.length} Total Tenants
            </div>
        </div>

        <div class="relative w-full overflow-x-auto">
            <table class="w-full caption-bottom text-sm whitespace-nowrap">
                <thead class="[&_tr]:border-b">
                    <tr
                        class="border-b transition-colors bg-muted/30 hover:bg-muted/30 data-[state=selected]:bg-muted"
                    >
                        <th
                            class="h-12 px-4 text-left align-middle font-medium text-muted-foreground"
                            >Tenant / ID</th
                        >
                        <th
                            class="h-12 px-4 text-left align-middle font-medium text-muted-foreground"
                            >Primary Contact</th
                        >
                        <th
                            class="h-12 px-4 text-left align-middle font-medium text-muted-foreground"
                            >Subscription</th
                        >
                        <th
                            class="h-12 px-4 text-left align-middle font-medium text-muted-foreground"
                            >Status</th
                        >
                        <th
                            class="h-12 px-4 text-left align-middle font-medium text-muted-foreground"
                            >Created</th
                        >
                        <th
                            class="h-12 px-4 text-right align-middle font-medium text-muted-foreground"
                            >Actions</th
                        >
                    </tr>
                </thead>
                <tbody class="[&_tr:last-child]:border-0">
                    {#if loading}
                        <tr
                            class="border-b transition-colors hover:bg-muted/50 data-[state=selected]:bg-muted"
                        >
                            <td
                                colspan={6}
                                class="p-4 align-middle h-32 text-center text-muted-foreground"
                            >
                                <span
                                    class="inline-block h-5 w-5 animate-spin rounded-full border-2 border-primary border-t-transparent mr-2 align-middle"
                                ></span>
                                Loading tenants data...
                            </td>
                        </tr>
                    {:else if tenants.length === 0}
                        <tr
                            class="border-b transition-colors hover:bg-muted/50 data-[state=selected]:bg-muted"
                        >
                            <td
                                colspan={6}
                                class="p-4 align-middle h-32 text-center text-muted-foreground"
                            >
                                No tenants found on the platform.
                            </td>
                        </tr>
                    {:else}
                        {#each tenants as tenant}
                            <tr
                                class="border-b transition-colors hover:bg-muted/50 data-[state=selected]:bg-muted"
                            >
                                <td class="p-4 align-middle">
                                    <div class="font-medium text-foreground">
                                        {tenant.name || "Unnamed"}
                                    </div>
                                    <div
                                        class="text-xs text-muted-foreground font-mono mt-0.5"
                                    >
                                        {tenant.tenantId}
                                    </div>
                                </td>
                                <td class="p-4 align-middle text-sm">
                                    {tenant.primaryEmail}
                                </td>
                                <td class="p-4 align-middle">
                                    {#if tenant.plan}
                                        <span
                                            class="inline-flex items-center px-2 py-0.5 rounded text-xs font-semibold border {getPlanBadgeColor(
                                                tenant.plan,
                                            )}"
                                        >
                                            {tenant.plan}
                                        </span>
                                        <div
                                            class="text-xs text-muted-foreground mt-1 ml-1"
                                        >
                                            {tenant.subscriptionStatus}
                                        </div>
                                    {:else}
                                        <span
                                            class="text-muted-foreground text-xs italic"
                                            >No Subscription</span
                                        >
                                    {/if}
                                </td>
                                <td class="p-4 align-middle">
                                    {#if tenant.active}
                                        <Badge
                                            variant="outline"
                                            class="border-emerald-200 bg-emerald-50 text-emerald-700 hover:bg-emerald-50"
                                            >Active</Badge
                                        >
                                    {:else}
                                        <Badge
                                            variant="secondary"
                                            class="bg-muted text-muted-foreground"
                                            >Suspended</Badge
                                        >
                                    {/if}
                                </td>
                                <td
                                    class="p-4 align-middle text-sm text-muted-foreground"
                                >
                                    {formatDate(tenant.createdAt)}
                                </td>
                                <td class="p-4 align-middle text-right">
                                    <DropdownMenu.Root>
                                        <DropdownMenu.Trigger
                                            disabled={actionLoading}
                                        >
                                            <Button
                                                variant="ghost"
                                                size="icon"
                                                class="h-8 w-8 text-muted-foreground hover:text-foreground"
                                            >
                                                <MoreHorizontal
                                                    class="h-4 w-4"
                                                />
                                            </Button>
                                        </DropdownMenu.Trigger>
                                        <DropdownMenu.Content
                                            align="end"
                                            class="w-48"
                                        >
                                            <DropdownMenu.Label
                                                class="text-xs font-semibold uppercase text-muted-foreground"
                                                >Admin Actions</DropdownMenu.Label
                                            >
                                            <DropdownMenu.Separator />
                                            <DropdownMenu.Item
                                                onclick={() =>
                                                    impersonateTenant(tenant)}
                                                class="cursor-pointer text-blue-600 focus:text-blue-700"
                                            >
                                                <LogIn class="mr-2 h-4 w-4" />
                                                <span
                                                    >Impersonate (Login As)</span
                                                >
                                            </DropdownMenu.Item>
                                            <DropdownMenu.Item
                                                onclick={() =>
                                                    openOverrideModal(tenant)}
                                                class="cursor-pointer"
                                            >
                                                <Edit class="mr-2 h-4 w-4" />
                                                <span
                                                    >Override Subscription</span
                                                >
                                            </DropdownMenu.Item>
                                            <DropdownMenu.Separator />
                                            {#if tenant.active}
                                                <DropdownMenu.Item
                                                    onclick={() =>
                                                        toggleTenantStatus(
                                                            tenant,
                                                        )}
                                                    class="cursor-pointer text-destructive focus:text-destructive"
                                                >
                                                    <Ban class="mr-2 h-4 w-4" />
                                                    <span>Suspend Tenant</span>
                                                </DropdownMenu.Item>
                                            {:else}
                                                <DropdownMenu.Item
                                                    onclick={() =>
                                                        toggleTenantStatus(
                                                            tenant,
                                                        )}
                                                    class="cursor-pointer text-emerald-600 focus:text-emerald-600"
                                                >
                                                    <CheckCircle2
                                                        class="mr-2 h-4 w-4"
                                                    />
                                                    <span>Activate Tenant</span>
                                                </DropdownMenu.Item>
                                            {/if}
                                        </DropdownMenu.Content>
                                    </DropdownMenu.Root>
                                </td>
                            </tr>
                        {/each}
                    {/if}
                </tbody>
            </table>
        </div>
    </div>
</div>

{#if overrideModalOpen}
    <div class="fixed inset-0 z-50 bg-background/80 backdrop-blur-sm"></div>
    <div
        class="fixed left-1/2 top-1/2 z-50 w-full max-w-lg -translate-x-1/2 -translate-y-1/2 rounded-xl border bg-card p-6 shadow-lg sm:rounded-lg"
    >
        <div class="mb-4">
            <h2 class="text-lg font-semibold tracking-tight">
                Override Subscription Plan
            </h2>
            <p class="text-sm text-muted-foreground mt-1">
                Manually grant or upgrade a subscription for <span
                    class="font-medium text-foreground"
                    >{overrideTargetTenant?.name ||
                        overrideTargetTenant?.tenantId}</span
                >. This bypasses payment requirements.
            </p>
        </div>

        <div class="grid gap-4 py-4">
            <div class="grid gap-2">
                <label for="plan-select" class="text-sm font-medium"
                    >Select Plan</label
                >
                <select
                    id="plan-select"
                    class="flex h-10 w-full items-center justify-between rounded-md border border-input bg-background px-3 py-2 text-sm focus:outline-none focus:ring-2 focus:ring-ring focus:ring-offset-2"
                    bind:value={overridePlan}
                >
                    {#each plans as plan}
                        <option value={plan}>{plan.label}</option>
                    {/each}
                </select>
            </div>

            <div
                class="bg-amber-50 text-amber-800 text-xs p-3 rounded-md border border-amber-200 flex gap-2"
            >
                <ShieldAlert class="h-4 w-4 shrink-0 mt-0.5" />
                <p>
                    This action creates or updates an active subscription valid
                    for 10 years and bypasses billing. Use only for internal or
                    VIP accounts.
                </p>
            </div>
        </div>

        <div
            class="mt-2 flex flex-col-reverse sm:flex-row sm:justify-end sm:space-x-2"
        >
            <Button
                variant="outline"
                onclick={() => (overrideModalOpen = false)}
                disabled={actionLoading}>Cancel</Button
            >
            <Button
                onclick={overrideSubscription}
                disabled={actionLoading || !overridePlan}
            >
                {#if actionLoading}
                    <span
                        class="mr-2 inline-block h-4 w-4 animate-spin rounded-full border-2 border-current border-t-transparent"
                    ></span>
                    Applying...
                {:else}
                    Force Apply Plan
                {/if}
            </Button>
        </div>
    </div>
{/if}
