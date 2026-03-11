<script lang="ts">
    import api from "$lib/api";
    import { auth } from "$lib/stores/auth.svelte";
    import { onMount } from "svelte";
    import { goto } from "$app/navigation";
    import * as Card from "$lib/components/ui/card";
    import { Button } from "$lib/components/ui/button";
    import { Badge } from "$lib/components/ui/badge";
    import { Skeleton } from "$lib/components/ui/skeleton";
    import { Confetti } from "$lib/components/confetti";
    import { ErrorBanner } from "$lib/components/error-banner";
    import { CheckCircle2, CreditCard, Sparkles } from "lucide-svelte";
    import type {
        PlanDefinitionResponse,
        SubscriptionResponse,
    } from "$lib/types";
    import { formatMoney } from "$lib/utils/currency";

    let loading = $state(true);
    let error = $state<string | null>(null);
    let plans = $state<PlanDefinitionResponse[]>([]);
    let subscription = $state<SubscriptionResponse | null>(null);

    // API Error Banner - only for 500-level errors
    type ApiErrorState = {
        show: boolean;
        type: 'error';
        title: string;
        message: string;
    };
    let apiError = $state<ApiErrorState>({ show: false, type: 'error', title: '', message: '' });

    // Confetti celebration
    let showConfetti = $state(false);
    let confettiTitle = $state('');
    let confettiMessage = $state('');

    function isUpgradeRestrictedState() {
        return (
            subscription?.status === "ACTIVE" || subscription?.status === "TRIAL"
        );
    }

    function canSelectPlan(plan: PlanDefinitionResponse) {
        if (!subscription) return true;
        if (!isUpgradeRestrictedState()) return true;

        if (subscription.plan === plan.planCode) return false;
        return plan.price > subscription.planPrice;
    }

    function selectLabel(plan: PlanDefinitionResponse) {
        if (!subscription) return "Select Plan";
        if (isUpgradeRestrictedState()) {
            if (subscription.plan === plan.planCode) return "Current Plan";
            if (plan.price <= subscription.planPrice) return "Upgrade Only";
        }
        return "Select Plan";
    }

    function startCheckout(planCode: string) {
        // 🎉 Celebrate plan selection only for first-time users
        if (auth.user?.firstLogin) {
            showConfetti = true;
            confettiTitle = '✨ Welcome Aboard!';
            confettiMessage = 'Your plan has been selected. Let\'s get started on your survey journey!';
            setTimeout(() => (showConfetti = false), 5000);
        }
        goto(`/payment/checkout?planCode=${planCode}&source=onboarding`);
    }

    onMount(async () => {
        if (!auth.isAuthenticated) {
            goto("/login");
            return;
        }

        loading = true;
        error = null;
        apiError = { show: false, type: 'error', title: '', message: '' };
        try {
            const [plansRes, subRes] = await Promise.allSettled([
                api.get<PlanDefinitionResponse[]>("/admin/plans"),
                api.get<SubscriptionResponse>("/admin/subscriptions/me"),
            ]);

            if (plansRes.status === "fulfilled") {
                plans = plansRes.value.data;
            } else {
                const status = plansRes.reason?.response?.status;
                // Show banner only for 500-level errors
                if (status && status >= 500) {
                    apiError = {
                        show: true,
                        type: 'error',
                        title: '🔴 Server Error',
                        message: 'Our servers are experiencing issues. Please try again later.'
                    };
                } else {
                    error = "Failed to load plans.";
                }
            }

            if (subRes.status === "fulfilled") {
                subscription = subRes.value.data;
            }
        } catch {
            error = "Failed to load onboarding data.";
        } finally {
            loading = false;
        }
    });
</script>

<svelte:head>
    <title>Select Plan — Survey Engine</title>
</svelte:head>

<div
    class="min-h-screen bg-gradient-to-br from-background via-background to-muted/30 p-4 md:p-8"
>
    <div class="mx-auto max-w-5xl space-y-6">
        <div class="text-center space-y-2">
            <div
                class="mx-auto flex h-12 w-12 items-center justify-center rounded-2xl bg-primary/10 text-primary"
            >
                <Sparkles class="h-6 w-6" />
            </div>
            <h1 class="text-2xl font-bold text-foreground">Choose Your Plan</h1>
            <p class="text-sm text-muted-foreground">
                Continue with trial or select a paid plan to proceed to payment.
            </p>
        </div>

        <ErrorBanner
            show={apiError.show || error !== null}
            type="failure"
            title={apiError.show ? apiError.title : "⚠️ Plan Selection Failed"}
            message={apiError.show ? apiError.message : (error || 'Failed to load plan information.')}
            onDismiss={() => { apiError = { show: false, type: 'error', title: '', message: '' }; error = null; }}
        />

        {#if loading}
            <div class="grid gap-4 md:grid-cols-3">
                {#each Array(3) as _}
                    <Card.Root>
                        <Card.Header>
                            <div class="space-y-2">
                                <Skeleton class="h-6 w-[120px]" />
                                <Skeleton class="h-4 w-[80px]" />
                            </div>
                        </Card.Header>
                        <Card.Content class="space-y-4">
                            {#each Array(3) as _}
                                <div class="flex items-start gap-2">
                                    <Skeleton class="h-4 w-4 rounded-full mt-0.5" />
                                    <Skeleton class="h-4 w-[150px]" />
                                </div>
                            {/each}
                            <Skeleton class="h-10 w-full" />
                        </Card.Content>
                    </Card.Root>
                {/each}
            </div>
        {:else}
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
                                >{formatMoney(plan.price, plan.currency)}</Card.Description
                            >
                        </Card.Header>
                        <Card.Content class="space-y-4">
                            <ul class="space-y-2 text-sm text-muted-foreground">
                                <li class="flex items-start gap-2">
                                    <CheckCircle2
                                        class="h-4 w-4 text-emerald-500 shrink-0 mt-0.5"
                                    />
                                    <span>{plan.maxCampaigns} campaigns</span>
                                </li>
                                <li class="flex items-start gap-2">
                                    <CheckCircle2
                                        class="h-4 w-4 text-emerald-500 shrink-0 mt-0.5"
                                    />
                                    <span
                                        >{plan.maxResponsesPerCampaign} responses /
                                        campaign</span
                                    >
                                </li>
                                <li class="flex items-start gap-2">
                                    <CheckCircle2
                                        class="h-4 w-4 text-emerald-500 shrink-0 mt-0.5"
                                    />
                                    <span>{plan.maxAdminUsers} admin users</span>
                                </li>
                            </ul>

                            <Button
                                class="w-full"
                                variant={subscription?.plan === plan.planCode &&
                                isUpgradeRestrictedState()
                                    ? "secondary"
                                    : "default"}
                                disabled={!canSelectPlan(plan)}
                                onclick={() => startCheckout(plan.planCode)}
                            >
                                <CreditCard class="mr-2 h-4 w-4" />
                                {selectLabel(plan)}
                            </Button>
                        </Card.Content>
                    </Card.Root>
                {/each}
            </div>

            <div class="flex justify-center pt-2">
                <Button variant="outline" onclick={() => goto("/dashboard")}>
                    Continue with Trial
                </Button>
            </div>
        {/if}
    </div>
</div>

<!-- 🎉 Confetti Celebration -->
{#if showConfetti}
    <Confetti
        fire={showConfetti}
        showBanner={true}
        title={confettiTitle}
        message={confettiMessage}
        particleCount={180}
        spread={90}
        startVelocity={55}
        duration={4500}
        colors={['#FFD700', '#10b981', '#3b82f6', '#8b5cf6']}
        onComplete={() => (showConfetti = false)}
    />
{/if}
