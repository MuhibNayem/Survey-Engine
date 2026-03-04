<script lang="ts">
    import { auth } from "$lib/stores/auth.svelte";
    import api from "$lib/api";
    import { onMount } from "svelte";
    import * as Card from "$lib/components/ui/card";
    import { Badge } from "$lib/components/ui/badge";
    import { Button } from "$lib/components/ui/button";
    import { Separator } from "$lib/components/ui/separator";
    import {
        CreditCard,
        Crown,
        CheckCircle2,
        ArrowUpRight,
        Calendar,
        Users,
        Megaphone,
        MessageSquareText,
        Shield,
        AlertCircle,
    } from "lucide-svelte";
    import type {
        SubscriptionResponse,
        PlanDefinitionResponse,
    } from "$lib/types";
    import { toast } from "svelte-sonner";

    let subscription = $state<SubscriptionResponse | null>(null);
    let availablePlans = $state<PlanDefinitionResponse[]>([]);
    let loading = $state(true);
    let upgrading = $state<string | null>(null);

    onMount(async () => {
        try {
            const [subRes, plansRes] = await Promise.allSettled([
                api.get<SubscriptionResponse>("/admin/subscriptions/me"),
                api.get<PlanDefinitionResponse[]>("/admin/plans"),
            ]);
            if (subRes.status === "fulfilled") subscription = subRes.value.data;
            if (plansRes.status === "fulfilled")
                availablePlans = plansRes.value.data;
        } catch {
            // Will show empty state
        } finally {
            loading = false;
        }
    });

    async function handleUpgrade(planCode: string) {
        upgrading = planCode;
        try {
            const { data } = await api.post<SubscriptionResponse>(
                "/admin/subscriptions/checkout",
                {
                    plan: planCode,
                },
            );
            subscription = data;
            toast.success("Plan upgraded successfully!");
        } catch (err: any) {
            const msg =
                err?.response?.data?.message ||
                "Upgrade failed. Please try again.";
            toast.error(msg);
        } finally {
            upgrading = null;
        }
    }

    function formatDate(iso: string | null): string {
        if (!iso) return "—";
        return new Date(iso).toLocaleDateString("en-US", {
            year: "numeric",
            month: "long",
            day: "numeric",
        });
    }

    function formatLimit(val: number | null): string {
        if (!val) return "Unlimited";
        return val.toLocaleString();
    }

    const statusColor = $derived(() => {
        switch (subscription?.status) {
            case "ACTIVE":
                return "text-emerald-500";
            case "TRIAL":
                return "text-blue-500";
            case "CANCELED":
                return "text-orange-500";
            case "EXPIRED":
                return "text-red-500";
            default:
                return "text-muted-foreground";
        }
    });

    const statusBadge = $derived(() => {
        switch (subscription?.status) {
            case "ACTIVE":
                return "default" as const;
            case "TRIAL":
                return "secondary" as const;
            default:
                return "destructive" as const;
        }
    });
</script>

<svelte:head>
    <title>Settings — Survey Engine</title>
</svelte:head>

<div class="space-y-8 max-w-4xl">
    <div>
        <h1 class="text-3xl font-bold tracking-tight text-foreground">
            Settings
        </h1>
        <p class="mt-1 text-muted-foreground">
            Manage your subscription and account settings.
        </p>
    </div>

    {#if loading}
        <div class="flex justify-center py-20">
            <div
                class="h-8 w-8 animate-spin rounded-full border-4 border-primary border-t-transparent"
            ></div>
        </div>
    {:else}
        <!-- Current Subscription -->
        <Card.Root>
            <Card.Header>
                <div class="flex items-center justify-between">
                    <div class="flex items-center gap-3">
                        <div
                            class="flex h-10 w-10 items-center justify-center rounded-xl bg-primary/10"
                        >
                            <Crown class="h-5 w-5 text-primary" />
                        </div>
                        <div>
                            <Card.Title>Current Plan</Card.Title>
                            <Card.Description
                                >Your subscription details</Card.Description
                            >
                        </div>
                    </div>
                    {#if subscription}
                        <Badge variant={statusBadge()}>
                            {subscription.status}
                        </Badge>
                    {/if}
                </div>
            </Card.Header>
            <Card.Content>
                {#if subscription}
                    <div class="grid gap-6 sm:grid-cols-2 lg:grid-cols-4">
                        <div class="space-y-1">
                            <p
                                class="text-sm text-muted-foreground flex items-center gap-1.5"
                            >
                                <CreditCard class="h-3.5 w-3.5" />
                                Plan
                            </p>
                            <p class="text-lg font-semibold text-foreground">
                                {subscription.plan}
                            </p>
                        </div>
                        <div class="space-y-1">
                            <p
                                class="text-sm text-muted-foreground flex items-center gap-1.5"
                            >
                                <Calendar class="h-3.5 w-3.5" />
                                Billing Period
                            </p>
                            <p class="text-sm text-foreground">
                                {formatDate(subscription.currentPeriodStart)} — {formatDate(
                                    subscription.currentPeriodEnd,
                                )}
                            </p>
                        </div>
                        <div class="space-y-1">
                            <p
                                class="text-sm text-muted-foreground flex items-center gap-1.5"
                            >
                                <CreditCard class="h-3.5 w-3.5" />
                                Price
                            </p>
                            <p class="text-lg font-semibold text-foreground">
                                {subscription.planPrice > 0
                                    ? new Intl.NumberFormat("en-US", {
                                          style: "currency",
                                          currency: subscription.currency,
                                          minimumFractionDigits: 0,
                                      }).format(subscription.planPrice)
                                    : "Free"}
                                {#if subscription.planPrice > 0}
                                    <span
                                        class="text-sm font-normal text-muted-foreground"
                                        >/mo</span
                                    >
                                {/if}
                            </p>
                        </div>
                        <div class="space-y-1">
                            <p class="text-sm text-muted-foreground">
                                Payment Ref
                            </p>
                            <p class="text-xs font-mono text-muted-foreground">
                                {subscription.lastPaymentGatewayReference ??
                                    "—"}
                            </p>
                        </div>
                    </div>

                    <Separator class="my-6" />

                    <!-- Quota Usage -->
                    <div>
                        <h3 class="text-sm font-semibold text-foreground mb-4">
                            Plan Limits
                        </h3>
                        <div class="grid gap-4 sm:grid-cols-3">
                            <div
                                class="flex items-center gap-3 rounded-lg border border-border p-4"
                            >
                                <Megaphone class="h-5 w-5 text-orange-500" />
                                <div>
                                    <p
                                        class="text-sm font-medium text-foreground"
                                    >
                                        {formatLimit(subscription.maxCampaigns)}
                                    </p>
                                    <p class="text-xs text-muted-foreground">
                                        Active Campaigns
                                    </p>
                                </div>
                            </div>
                            <div
                                class="flex items-center gap-3 rounded-lg border border-border p-4"
                            >
                                <MessageSquareText
                                    class="h-5 w-5 text-pink-500"
                                />
                                <div>
                                    <p
                                        class="text-sm font-medium text-foreground"
                                    >
                                        {formatLimit(
                                            subscription.maxResponsesPerCampaign,
                                        )}
                                    </p>
                                    <p class="text-xs text-muted-foreground">
                                        Responses / Campaign
                                    </p>
                                </div>
                            </div>
                            <div
                                class="flex items-center gap-3 rounded-lg border border-border p-4"
                            >
                                <Users class="h-5 w-5 text-blue-500" />
                                <div>
                                    <p
                                        class="text-sm font-medium text-foreground"
                                    >
                                        {formatLimit(
                                            subscription.maxAdminUsers,
                                        )}
                                    </p>
                                    <p class="text-xs text-muted-foreground">
                                        Admin Users
                                    </p>
                                </div>
                            </div>
                        </div>
                    </div>
                {:else}
                    <div class="flex flex-col items-center justify-center py-8">
                        <AlertCircle
                            class="h-10 w-10 text-muted-foreground/50 mb-3"
                        />
                        <p class="text-muted-foreground">
                            No subscription found. Start a free trial to get
                            started.
                        </p>
                        <Button class="mt-4" href="/pricing">
                            View Plans
                            <ArrowUpRight class="ml-1 h-4 w-4" />
                        </Button>
                    </div>
                {/if}
            </Card.Content>
        </Card.Root>

        <!-- Upgrade Plans -->
        {#if availablePlans.length > 0}
            <Card.Root>
                <Card.Header>
                    <Card.Title>Upgrade or Change Plan</Card.Title>
                    <Card.Description
                        >Choose a plan that fits your needs.</Card.Description
                    >
                </Card.Header>
                <Card.Content>
                    <div class="grid gap-4 sm:grid-cols-3">
                        {#each availablePlans as plan}
                            {@const isCurrentPlan =
                                subscription?.plan === plan.planCode}
                            <div
                                class="rounded-xl border {isCurrentPlan
                                    ? 'border-primary bg-primary/5'
                                    : 'border-border'} p-5 space-y-4"
                            >
                                <div class="flex items-center justify-between">
                                    <h3 class="font-semibold text-foreground">
                                        {plan.displayName}
                                    </h3>
                                    {#if isCurrentPlan}
                                        <Badge variant="outline">Current</Badge>
                                    {/if}
                                </div>
                                <p class="text-2xl font-bold text-foreground">
                                    {plan.price > 0
                                        ? new Intl.NumberFormat("en-US", {
                                              style: "currency",
                                              currency: plan.currency,
                                              minimumFractionDigits: 0,
                                          }).format(plan.price)
                                        : "Free"}
                                    {#if plan.price > 0}
                                        <span
                                            class="text-sm font-normal text-muted-foreground"
                                            >/mo</span
                                        >
                                    {/if}
                                </p>
                                <ul
                                    class="space-y-1.5 text-sm text-muted-foreground"
                                >
                                    <li class="flex items-center gap-1.5">
                                        <CheckCircle2
                                            class="h-3.5 w-3.5 text-emerald-500"
                                        />
                                        {formatLimit(plan.maxCampaigns)} campaigns
                                    </li>
                                    <li class="flex items-center gap-1.5">
                                        <CheckCircle2
                                            class="h-3.5 w-3.5 text-emerald-500"
                                        />
                                        {formatLimit(
                                            plan.maxResponsesPerCampaign,
                                        )} responses
                                    </li>
                                    <li class="flex items-center gap-1.5">
                                        <CheckCircle2
                                            class="h-3.5 w-3.5 text-emerald-500"
                                        />
                                        {formatLimit(plan.maxAdminUsers)} admin users
                                    </li>
                                </ul>
                                <Button
                                    variant={isCurrentPlan
                                        ? "outline"
                                        : "default"}
                                    class="w-full"
                                    disabled={isCurrentPlan ||
                                        upgrading !== null}
                                    onclick={() => handleUpgrade(plan.planCode)}
                                >
                                    {#if upgrading === plan.planCode}
                                        <span
                                            class="mr-2 inline-block h-4 w-4 animate-spin rounded-full border-2 border-current border-t-transparent"
                                        ></span>
                                        Processing...
                                    {:else if isCurrentPlan}
                                        Current Plan
                                    {:else}
                                        Upgrade
                                    {/if}
                                </Button>
                            </div>
                        {/each}
                    </div>
                </Card.Content>
            </Card.Root>
        {/if}

        <!-- Account Info -->
        <Card.Root>
            <Card.Header>
                <div class="flex items-center gap-3">
                    <div
                        class="flex h-10 w-10 items-center justify-center rounded-xl bg-muted"
                    >
                        <Shield class="h-5 w-5 text-muted-foreground" />
                    </div>
                    <div>
                        <Card.Title>Account</Card.Title>
                        <Card.Description
                            >Your account information</Card.Description
                        >
                    </div>
                </div>
            </Card.Header>
            <Card.Content>
                <div class="grid gap-4 sm:grid-cols-2">
                    <div class="space-y-1">
                        <p class="text-sm text-muted-foreground">Email</p>
                        <p class="font-medium text-foreground">
                            {auth.user?.email ?? "—"}
                        </p>
                    </div>
                    <div class="space-y-1">
                        <p class="text-sm text-muted-foreground">Tenant ID</p>
                        <p class="font-mono text-sm text-muted-foreground">
                            {auth.user?.tenantId ?? "—"}
                        </p>
                    </div>
                </div>
            </Card.Content>
        </Card.Root>
    {/if}
</div>
