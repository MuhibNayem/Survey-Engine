<script lang="ts">
    import api from "$lib/api";
    import { onMount } from "svelte";
    import { goto } from "$app/navigation";
    import * as Card from "$lib/components/ui/card";
    import { Button } from "$lib/components/ui/button";
    import { Badge } from "$lib/components/ui/badge";
    import { ProgressBar } from "$lib/components/ui/progress-bar";
    import { Skeleton } from "$lib/components/ui/skeleton";
    import {
        ArrowLeft,
        CreditCard,
        CheckCircle2,
        Zap,
        ShieldCheck,
        AlertCircle,
        Sparkles,
        ChevronDown,
        ChevronUp,
    } from "lucide-svelte";
    import type {
        SubscriptionResponse,
        PlanDefinitionResponse,
        CampaignResponse,
        PageResponse,
    } from "$lib/types";

    let subscription = $state<SubscriptionResponse | null>(null);
    let plans = $state<PlanDefinitionResponse[]>([]);
    let campaigns = $state<CampaignResponse[]>([]);
    let activeCampaignCount = $state(0);
    let loading = $state(true);
    let error = $state<string | null>(null);
    let expandedPlanCode = $state<string | null>(null);

    const planBadgeVariant = $derived(
        subscription?.status === "ACTIVE"
            ? ("default" as const)
            : subscription?.status === "TRIAL"
              ? ("secondary" as const)
              : ("outline" as const),
    );

    function formatDate(iso: string | null | undefined) {
        if (!iso) return "—";
        return new Date(iso).toLocaleDateString("en-US", {
            month: "short",
            day: "numeric",
            year: "numeric",
        });
    }

    function isUnlimited(val: number | null | undefined) {
        return val == null || val < 0; // Backend may return null for infinite, or -1.
    }

    function isUpgradeRestrictedState() {
        return (
            subscription?.status === "ACTIVE" || subscription?.status === "TRIAL"
        );
    }

    function canCheckoutPlan(plan: PlanDefinitionResponse) {
        if (!subscription) return true;
        if (!isUpgradeRestrictedState()) return true;

        if (subscription.plan === plan.planCode) return false;
        return plan.price > subscription.planPrice;
    }

    function checkoutLabel(plan: PlanDefinitionResponse) {
        if (!subscription) return "Select Plan";
        if (isUpgradeRestrictedState()) {
            if (subscription.plan === plan.planCode) return "Current Plan";
            if (plan.price <= subscription.planPrice) return "Upgrade Only";
        }
        if (
            (subscription.status === "EXPIRED" ||
                subscription.status === "CANCELED") &&
            subscription.plan === plan.planCode
        ) {
            return "Renew Plan";
        }
        return "Select Plan";
    }

    async function loadData() {
        loading = true;
        error = null;
        try {
            // Use allSettled so that if a tenant doesn't have a subscription (404),
            // we can still load the available plans to render the upgrade UI.
            const [subRes, plansRes, campsRes] = await Promise.allSettled([
                api.get<SubscriptionResponse>("/admin/subscriptions/me"),
                api.get<PlanDefinitionResponse[]>("/admin/plans"),
                api.get<PageResponse<CampaignResponse> | CampaignResponse[]>(
                    "/campaigns?size=1",
                ),
            ]);

            if (subRes.status === "fulfilled") subscription = subRes.value.data;
            if (plansRes.status === "fulfilled") plans = plansRes.value.data;
            if (campsRes.status === "fulfilled") {
                const data = campsRes.value.data as
                    | PageResponse<CampaignResponse>
                    | CampaignResponse[];
                if (Array.isArray(data)) {
                    campaigns = data;
                    activeCampaignCount = data.length;
                } else {
                    campaigns = data.content || [];
                    activeCampaignCount =
                        typeof data.totalElements === "number"
                            ? data.totalElements
                            : campaigns.length;
                }
            }

            // If we failed to get plans, treat it as a hard failure since we can't render the grid.
            if (plansRes.status === "rejected") {
                error = "Failed to load pricing plans from the server.";
            }
        } catch (err) {
            error = "An error occurred loading subscription data.";
        } finally {
            loading = false;
        }
    }

    function initiateCheckout(planCode: string) {
        goto(`/payment/checkout?planCode=${planCode}&source=settings`);
    }

    function togglePlanDetails(planCode: string) {
        expandedPlanCode = expandedPlanCode === planCode ? null : planCode;
    }

    onMount(loadData);
</script>

<svelte:head>
    <title>Subscription & Billing — Survey Engine</title>
</svelte:head>

<div class="space-y-6 max-w-5xl mx-auto">
    <div class="flex items-center gap-3">
        <Button variant="ghost" size="sm" onclick={() => goto("/dashboard")}>
            <ArrowLeft class="h-4 w-4" />
        </Button>
        <div>
            <h1 class="text-2xl font-bold tracking-tight text-foreground">
                Subscription & Billing
            </h1>
            <p
                class="mt-0.5 text-sm text-muted-foreground flex items-center gap-2"
            >
                <CreditCard class="h-3.5 w-3.5" /> Manage your tenant plan and limits
            </p>
        </div>
    </div>

    {#if error}
        <div
            class="flex items-start gap-2 rounded-lg border border-destructive/30 bg-destructive/10 p-3 text-sm text-destructive"
        >
            <AlertCircle class="h-4 w-4 shrink-0 mt-0.5" />
            <p>{error}</p>
        </div>
    {/if}

    {#if loading}
        <div class="space-y-6">
            <div class="grid gap-6 md:grid-cols-2">
                {#each Array(2) as _}
                    <Card.Root>
                        <Card.Header>
                            <div class="space-y-2">
                                <Skeleton class="h-6 w-[180px]" />
                                <Skeleton class="h-4 w-[120px]" />
                            </div>
                        </Card.Header>
                        <Card.Content class="space-y-4">
                            {#each Array(5) as _}
                                <div class="flex items-center justify-between">
                                    <Skeleton class="h-4 w-[100px]" />
                                    <Skeleton class="h-4 w-[60px]" />
                                </div>
                            {/each}
                        </Card.Content>
                    </Card.Root>
                {/each}
            </div>
            <div class="grid gap-4 md:grid-cols-3">
                {#each Array(3) as _}
                    <Card.Root>
                        <Card.Header>
                            <div class="space-y-2">
                                <Skeleton class="h-5 w-[120px]" />
                                <Skeleton class="h-4 w-[80px]" />
                            </div>
                        </Card.Header>
                        <Card.Content class="space-y-3">
                            {#each Array(4) as _}
                                <Skeleton class="h-4 w-full" />
                            {/each}
                        </Card.Content>
                    </Card.Root>
                {/each}
            </div>
        </div>
    {:else}
        {#if subscription}
            <div class="grid gap-6 md:grid-cols-2">
                <!-- Current Plan Card -->
                <Card.Root
                    class="border-primary/20 bg-gradient-to-br from-primary/5 via-transparent to-transparent"
                >
                    <Card.Header>
                        <Card.Title class="flex items-center gap-2">
                            <ShieldCheck class="h-5 w-5 text-primary" />
                            Current Plan
                        </Card.Title>
                    </Card.Header>
                    <Card.Content class="space-y-6">
                        <div>
                            <div class="flex items-center gap-3 mb-1">
                                <span class="text-3xl font-bold text-foreground"
                                    >{subscription.plan}</span
                                >
                                <Badge variant={planBadgeVariant} class="mt-1"
                                    >{subscription.status}</Badge
                                >
                            </div>
                            <p class="text-sm text-muted-foreground">
                                {subscription.currency}
                                {subscription.planPrice} / cycle
                            </p>
                        </div>

                        <div
                            class="space-y-2 text-sm pt-4 border-t border-border/50"
                        >
                            <div class="flex justify-between">
                                <span class="text-muted-foreground"
                                    >Period Start</span
                                >
                                <span class="font-medium text-foreground"
                                    >{formatDate(
                                        subscription.currentPeriodStart,
                                    )}</span
                                >
                            </div>
                            <div class="flex justify-between">
                                <span class="text-muted-foreground"
                                    >Period End</span
                                >
                                <span class="font-medium text-foreground"
                                    >{formatDate(
                                        subscription.currentPeriodEnd,
                                    )}</span
                                >
                            </div>
                        </div>
                    </Card.Content>
                </Card.Root>

                <!-- Quotas/Usage -->
                <Card.Root>
                    <Card.Header>
                        <Card.Title class="flex items-center gap-2">
                            <Zap class="h-5 w-5 text-amber-500" />
                            Resource Usage
                        </Card.Title>
                        <Card.Description
                            >Based on your {subscription.plan} quota</Card.Description
                        >
                    </Card.Header>
                    <Card.Content class="space-y-6">
                        <div class="space-y-2">
                            <div
                                class="flex items-center justify-between text-sm font-medium"
                            >
                                <span>Active Campaigns</span>
                                <span class="text-muted-foreground font-normal">
                                    {activeCampaignCount} / {isUnlimited(
                                        subscription.maxCampaigns,
                                    )
                                        ? "Unlimited"
                                        : subscription.maxCampaigns}
                                </span>
                            </div>
                            {#if !isUnlimited(subscription.maxCampaigns)}
                                <ProgressBar
                                    value={activeCampaignCount}
                                    max={subscription.maxCampaigns}
                                    colorClass={activeCampaignCount >=
                                    subscription.maxCampaigns
                                        ? "bg-destructive"
                                        : "bg-primary"}
                                />
                            {:else}
                                <ProgressBar
                                    value={100}
                                    max={100}
                                    colorClass="bg-emerald-500"
                                />
                            {/if}
                        </div>

                        <div class="space-y-2 opacity-80">
                            <div
                                class="flex items-center justify-between text-sm font-medium"
                            >
                                <span>Responses per Campaign</span>
                                <span class="text-muted-foreground font-normal">
                                    Limit: {isUnlimited(
                                        subscription.maxResponsesPerCampaign,
                                    )
                                        ? "Unlimited"
                                        : subscription.maxResponsesPerCampaign}
                                </span>
                            </div>
                            <p class="text-xs text-muted-foreground">
                                If a campaign reaches this limit, it will
                                automatically pause accepting new responses.
                            </p>
                        </div>
                    </Card.Content>
                </Card.Root>
            </div>
        {:else}
            <Card.Root class="py-12 border-dashed bg-muted/10">
                <Card.Content
                    class="flex flex-col items-center justify-center text-center space-y-2"
                >
                    <CreditCard class="h-10 w-10 text-muted-foreground/50" />
                    <h2 class="text-lg font-semibold text-foreground">
                        No Active Subscription
                    </h2>
                    <p class="text-sm text-muted-foreground max-w-sm">
                        You do not have a registered subscription plan. Please
                        select one below to bind your tenant and unlock limits.
                    </p>
                </Card.Content>
            </Card.Root>
        {/if}

        <!-- Upgrade Options -->
        <div class="pt-6 border-t border-border mt-8">
            <h2
                class="text-xl font-semibold text-foreground mb-4 flex items-center gap-2"
            >
                <Sparkles class="h-5 w-5 text-purple-500" /> Available Plans
            </h2>

            <div class="grid gap-4 md:grid-cols-3">
                {#each plans as plan}
                    <Card.Root
                        class={subscription?.plan === plan.planCode
                            ? "border-primary shadow-sm relative overflow-hidden"
                            : ""}
                    >
                        {#if subscription?.plan === plan.planCode}
                            <div
                                class="absolute top-0 right-0 bg-primary text-primary-foreground text-[10px] font-bold px-2 py-0.5 rounded-bl-lg uppercase tracking-wider"
                            >
                                Current
                            </div>
                        {/if}
                        <Card.Header>
                            <Card.Title>{plan.displayName}</Card.Title>
                            <Card.Description
                                >{plan.currency} {plan.price}</Card.Description
                            >
                        </Card.Header>
                        <Card.Content class="space-y-4">
                            <ul class="space-y-2 text-sm text-muted-foreground">
                                <li class="flex items-start gap-2">
                                    <CheckCircle2
                                        class="h-4 w-4 text-emerald-500 shrink-0 mt-0.5"
                                    />
                                    <span>
                                        {isUnlimited(plan.maxCampaigns)
                                            ? "Unlimited"
                                            : plan.maxCampaigns} active campaigns
                                    </span>
                                </li>
                                <li class="flex items-start gap-2">
                                    <CheckCircle2
                                        class="h-4 w-4 text-emerald-500 shrink-0 mt-0.5"
                                    />
                                    <span>
                                        {isUnlimited(
                                            plan.maxResponsesPerCampaign,
                                        )
                                            ? "Unlimited"
                                            : plan.maxResponsesPerCampaign} responses
                                        / campaign
                                    </span>
                                </li>
                                <li class="flex items-start gap-2">
                                    <CheckCircle2
                                        class="h-4 w-4 text-emerald-500 shrink-0 mt-0.5"
                                    />
                                    <span>
                                        {isUnlimited(plan.maxAdminUsers)
                                            ? "Unlimited"
                                            : plan.maxAdminUsers} admin users
                                    </span>
                                </li>
                                <li class="flex items-start gap-2">
                                    <CheckCircle2
                                        class="h-4 w-4 text-emerald-500 shrink-0 mt-0.5"
                                    />
                                    <span>{plan.trialDays} days free trial</span
                                    >
                                </li>
                            </ul>

                            <Button
                                type="button"
                                variant="ghost"
                                size="sm"
                                class="w-full justify-between px-2"
                                onclick={() => togglePlanDetails(plan.planCode)}
                            >
                                <span>Plan details</span>
                                {#if expandedPlanCode === plan.planCode}
                                    <ChevronUp class="h-4 w-4" />
                                {:else}
                                    <ChevronDown class="h-4 w-4" />
                                {/if}
                            </Button>

                            {#if expandedPlanCode === plan.planCode}
                                <div class="rounded-lg border border-border/60 bg-muted/20 p-3 space-y-3">
                                    <div class="grid grid-cols-2 gap-2 text-xs text-muted-foreground">
                                        <div>Billing cycle</div>
                                        <div class="text-right text-foreground">{plan.billingCycleDays} days</div>
                                        <div>API access</div>
                                        <div class="text-right text-foreground">{plan.apiAccessEnabled ? "Enabled" : "Disabled"}</div>
                                        <div>SSO</div>
                                        <div class="text-right text-foreground">{plan.ssoEnabled ? "Enabled" : "Disabled"}</div>
                                        <div>Custom branding</div>
                                        <div class="text-right text-foreground">{plan.customBrandingEnabled ? "Enabled" : "Disabled"}</div>
                                        <div>Signed token</div>
                                        <div class="text-right text-foreground">{plan.signedTokenEnabled ? "Enabled" : "Disabled"}</div>
                                        <div>Weight profiles</div>
                                        <div class="text-right text-foreground">{plan.weightProfilesEnabled ? "Enabled" : "Disabled"}</div>
                                    </div>

                                    {#if plan.features && plan.features.length > 0}
                                        <div class="border-t border-border/60 pt-3">
                                            <p class="text-xs font-semibold text-foreground mb-2">Included features</p>
                                            <ul class="space-y-1.5">
                                                {#each plan.features as feature}
                                                    <li class="text-xs text-muted-foreground">
                                                        <span class="text-foreground font-medium">{feature.name}</span>
                                                        {#if feature.description}
                                                            <span>: {feature.description}</span>
                                                        {/if}
                                                    </li>
                                                {/each}
                                            </ul>
                                        </div>
                                    {/if}
                                </div>
                            {/if}

                            <Button
                                class="w-full mt-4"
                                variant={subscription?.plan === plan.planCode &&
                                isUpgradeRestrictedState()
                                    ? "secondary"
                                    : "default"}
                                disabled={!canCheckoutPlan(plan)}
                                onclick={() => initiateCheckout(plan.planCode)}
                            >
                                {checkoutLabel(plan)}
                            </Button>
                        </Card.Content>
                    </Card.Root>
                {/each}
            </div>
        </div>
    {/if}
</div>
