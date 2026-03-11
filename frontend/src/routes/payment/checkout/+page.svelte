<script lang="ts">
    import api from "$lib/api";
    import { auth } from "$lib/stores/auth.svelte";
    import { onMount } from "svelte";
    import { goto } from "$app/navigation";
    import * as Card from "$lib/components/ui/card";
    import { Button } from "$lib/components/ui/button";
    import { Skeleton } from "$lib/components/ui/skeleton";
    import { Confetti } from "$lib/components/confetti";
    import { ErrorBanner } from "$lib/components/error-banner";
    import {
        ReceiptText,
        CheckCircle2,
        ArrowLeft,
        LoaderCircle,
    } from "lucide-svelte";
    import type {
        CheckoutSessionResponse,
        PlanDefinitionResponse,
        SubscriptionPlan,
    } from "$lib/types";

    let loading = $state(true);
    let paying = $state(false);
    let error = $state<string | null>(null);
    let serverError = $state(false);
    let plan = $state<PlanDefinitionResponse | null>(null);
    let source = $state<"settings" | "onboarding">("settings");
    let paymentStatus = $state<"idle" | "success" | "failed" | "canceled">("idle");
    let gatewayReference = $state<string | null>(null);
    let showConfetti = $state(false);

    function redirectBack() {
        if (source === "onboarding") {
            goto("/onboarding/plan");
            return;
        }
        goto("/settings/subscription");
    }

    function continueAfterSuccess() {
        if (source === "onboarding") {
            if (auth.user?.role === "SUPER_ADMIN") {
                goto("/admin/dashboard");
            } else {
                goto("/dashboard");
            }
            return;
        }
        goto("/settings/subscription");
    }

    function statusMessage() {
        if (!plan) return "";
        switch (paymentStatus) {
            case "success":
                return `${plan.displayName} has been activated for your tenant.`;
            case "failed":
                return `Payment could not be completed for ${plan.displayName}. Please try again.`;
            case "canceled":
                return `Payment for ${plan.displayName} was canceled.`;
            default:
                return `You will be redirected to SSLCommerz to pay for ${plan.displayName}.`;
        }
    }

    async function payAndUpgrade() {
        if (!plan) return;

        paying = true;
        error = null;
        serverError = false;
        try {
            const { data } = await api.post<CheckoutSessionResponse>(
                "/admin/subscriptions/checkout",
                {
                    planCode: plan.planCode,
                    source,
                },
            );
            gatewayReference = data.gatewayReference;
            if (!data.paymentUrl) {
                error = "Payment session created, but no redirect URL was returned.";
                return;
            }
            window.location.href = data.paymentUrl;
        } catch (err: any) {
            const status = err?.response?.status;
            if (status >= 500) {
                serverError = true;
            } else {
                error = err?.response?.data?.message || "Unable to start payment. Please try again.";
            }
        } finally {
            paying = false;
        }
    }

    onMount(async () => {
        if (!auth.isAuthenticated) {
            goto("/login");
            return;
        }

        loading = true;
        error = null;
        try {
            const params = new URLSearchParams(window.location.search);
            const selectedPlanCode = params.get("planCode") as SubscriptionPlan | null;
            source = params.get("source") === "onboarding" ? "onboarding" : "settings";
            const statusParam = params.get("paymentStatus");
            gatewayReference = params.get("gatewayReference");

            if (statusParam === "success" || statusParam === "failed" || statusParam === "canceled") {
                paymentStatus = statusParam;
                if (statusParam === "success") {
                    showConfetti = true;
                }
            }

            if (!selectedPlanCode) {
                error = "No plan selected for checkout.";
                return;
            }

            const { data } = await api.get<PlanDefinitionResponse[]>("/admin/plans");
            plan = data.find((p) => p.planCode === selectedPlanCode) ?? null;
            if (!plan) {
                error = "Selected plan is unavailable.";
            }
        } catch {
            error = "Failed to load checkout details.";
        } finally {
            loading = false;
        }
    });
</script>

<svelte:head>
    <title>Payment Checkout - Survey Engine</title>
</svelte:head>

<div class="min-h-screen bg-gradient-to-b from-background via-background to-muted/20 p-4 md:p-8">
    <div class="mx-auto max-w-6xl space-y-6">
        <div class="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
            <div class="flex items-center gap-3">
                <Button variant="outline" size="sm" onclick={redirectBack} disabled={paying}>
                    <ArrowLeft class="h-4 w-4" />
                </Button>
                <div>
                    <h1 class="text-2xl font-bold text-foreground">Secure Checkout</h1>
                </div>
            </div>
        </div>

        <ErrorBanner
            show={serverError}
            type="failure"
            title="Server Error"
            message="The payment session could not be created. Please try again later."
            showRetry={true}
            onRetry={payAndUpgrade}
            onDismiss={() => (serverError = false)}
        />

        {#if error && !serverError}
            <div class="rounded-lg border border-destructive/30 bg-destructive/10 px-4 py-3 text-sm text-destructive">
                {error}
            </div>
        {/if}

        {#if loading}
            <div class="grid gap-6 lg:grid-cols-[1.2fr_0.8fr]">
                <Card.Root class="order-2 lg:order-1 border-border/70">
                    <Card.Header class="border-b border-border/60 pb-4">
                        <div class="space-y-2">
                            <Skeleton class="h-6 w-[180px]" />
                            <Skeleton class="h-4 w-[300px]" />
                        </div>
                    </Card.Header>
                    <Card.Content class="space-y-5 pt-5">
                        {#each Array(4) as _}
                            <div class="space-y-2">
                                <Skeleton class="h-4 w-[120px]" />
                                <Skeleton class="h-10 w-full" />
                            </div>
                        {/each}
                    </Card.Content>
                </Card.Root>
                <Card.Root class="order-1 lg:order-2 border-primary/20 bg-gradient-to-br from-primary/5 via-transparent to-transparent">
                    <Card.Header class="border-b border-border/60 pb-4">
                        <div class="space-y-2">
                            <Skeleton class="h-6 w-[150px]" />
                            <Skeleton class="h-4 w-[250px]" />
                        </div>
                    </Card.Header>
                    <Card.Content class="space-y-4 pt-5">
                        {#each Array(5) as _}
                            <div class="flex items-center justify-between">
                                <Skeleton class="h-4 w-[100px]" />
                                <Skeleton class="h-4 w-[60px]" />
                            </div>
                        {/each}
                    </Card.Content>
                </Card.Root>
            </div>
        {:else if plan}
            <div class="grid gap-6 lg:grid-cols-[1.2fr_0.8fr]">
                <Card.Root class="order-2 lg:order-1 border-border/70">
                    <Card.Header class="border-b border-border/60 pb-4">
                        <Card.Title>Checkout</Card.Title>
                        {#if paymentStatus !== "idle"}
                            <Card.Description>{statusMessage()}</Card.Description>
                        {/if}
                    </Card.Header>
                    <Card.Content class="space-y-5 pt-5">
                        <div class="rounded-lg border border-border/60 p-4">
                            <p class="text-lg font-semibold text-foreground">{plan.displayName}</p>
                            <p class="text-sm text-muted-foreground">{plan.currency} {plan.price} every {plan.billingCycleDays} days</p>
                        </div>

                        {#if paymentStatus === "success"}
                            <div class="rounded-lg border border-emerald-500/30 bg-emerald-500/10 p-4 text-sm text-emerald-700">
                                Your subscription is active now. Continue to your workspace.
                            </div>
                        {:else if paymentStatus === "failed" || paymentStatus === "canceled"}
                            <div class="rounded-lg border border-destructive/30 bg-destructive/10 p-4 text-sm text-destructive">
                                The plan was not activated. Please try again.
                            </div>
                        {/if}
                    </Card.Content>
                    <Card.Footer class="flex flex-col-reverse gap-2 border-t border-border/60 pt-4 sm:flex-row sm:justify-end">
                        <Button variant="outline" onclick={redirectBack} disabled={paying}>
                            Back
                        </Button>
                        {#if paymentStatus === "success"}
                            <Button onclick={continueAfterSuccess}>
                                Continue
                            </Button>
                        {:else}
                            <Button onclick={payAndUpgrade} disabled={paying}>
                                {#if paying}
                                    <LoaderCircle class="mr-2 h-4 w-4 animate-spin" />
                                    Redirecting...
                                {:else}
                                    Pay
                                {/if}
                            </Button>
                        {/if}
                    </Card.Footer>
                </Card.Root>

                <div class="order-1 space-y-4 lg:order-2">
                    <Card.Root class="border-border/70 lg:sticky lg:top-6">
                        <Card.Header class="border-b border-border/60 pb-4">
                            <Card.Title class="flex items-center gap-2">
                                <ReceiptText class="h-5 w-5 text-primary" />
                                Order Summary
                            </Card.Title>
                            <Card.Description>{plan.displayName} plan</Card.Description>
                        </Card.Header>
                        <Card.Content class="space-y-4 pt-5 text-sm">
                            <div class="flex items-center justify-between">
                                <span class="text-muted-foreground">Base plan</span>
                                <span class="font-medium">{plan.currency} {plan.price}</span>
                            </div>
                            <div class="flex items-center justify-between">
                                <span class="text-muted-foreground">Billing cycle</span>
                                <span>{plan.billingCycleDays} days</span>
                            </div>
                            <div class="flex items-center justify-between">
                                <span class="text-muted-foreground">Gateway</span>
                                <span>SSLCommerz sandbox</span>
                            </div>
                            <div class="border-t border-border/70 pt-3">
                                <div class="flex items-center justify-between text-base font-semibold text-foreground">
                                    <span>Due today</span>
                                    <span>{plan.currency} {plan.price}</span>
                                </div>
                            </div>

                            <div class="rounded-lg border border-border/60 bg-muted/10 p-3">
                                <p class="mb-2 text-xs font-semibold uppercase tracking-wide text-muted-foreground">
                                    Included highlights
                                </p>
                                <ul class="space-y-1.5">
                                    <li class="flex items-center justify-between text-xs">
                                        <span class="text-muted-foreground">Campaign quota</span>
                                        <span class="font-medium text-foreground">{plan.maxCampaigns < 0 ? "Unlimited" : plan.maxCampaigns}</span>
                                    </li>
                                    <li class="flex items-center justify-between text-xs">
                                        <span class="text-muted-foreground">Responses / campaign</span>
                                        <span class="font-medium text-foreground">{plan.maxResponsesPerCampaign < 0 ? "Unlimited" : plan.maxResponsesPerCampaign}</span>
                                    </li>
                                    <li class="flex items-center justify-between text-xs">
                                        <span class="text-muted-foreground">Admin users</span>
                                        <span class="font-medium text-foreground">{plan.maxAdminUsers < 0 ? "Unlimited" : plan.maxAdminUsers}</span>
                                    </li>
                                    <li class="flex items-center justify-between text-xs">
                                        <span class="text-muted-foreground">Trial</span>
                                        <span class="font-medium text-foreground">{plan.trialDays} days</span>
                                    </li>
                                </ul>
                            </div>

                            {#if plan.features && plan.features.length > 0}
                                <div>
                                    <p class="mb-2 text-xs font-semibold uppercase tracking-wide text-muted-foreground">
                                        Feature access
                                    </p>
                                    <ul class="space-y-1.5">
                                        {#each plan.features.slice(0, 4) as feature}
                                            <li class="flex items-start gap-2 text-xs text-muted-foreground">
                                                <CheckCircle2 class="mt-0.5 h-3.5 w-3.5 shrink-0 text-emerald-600" />
                                                <span>{feature.name}</span>
                                            </li>
                                        {/each}
                                    </ul>
                                </div>
                            {/if}
                        </Card.Content>
                    </Card.Root>
                </div>
            </div>
        {/if}
    </div>

    {#if showConfetti}
        <Confetti
            fire={showConfetti}
            particleCount={150}
            spread={100}
            startVelocity={55}
            duration={4000}
            colors={['#10b981', '#3b82f6', '#f59e0b', '#06b6d4']}
            showBanner={true}
            title="Payment Successful!"
            message="Your subscription has been activated."
            gravity={0.9}
            onComplete={() => (showConfetti = false)}
        />
    {/if}
</div>
