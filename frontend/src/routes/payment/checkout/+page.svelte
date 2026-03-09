<script lang="ts">
    import api from "$lib/api";
    import { auth } from "$lib/stores/auth.svelte";
    import { onMount } from "svelte";
    import { goto } from "$app/navigation";
    import * as Card from "$lib/components/ui/card";
    import { Button } from "$lib/components/ui/button";
    import { Input } from "$lib/components/ui/input";
    import { Label } from "$lib/components/ui/label";
    import { Badge } from "$lib/components/ui/badge";
    import { Skeleton } from "$lib/components/ui/skeleton";
    import { Confetti } from "$lib/components/confetti";
    import {
        CreditCard,
        ShieldCheck,
        LockKeyhole,
        ReceiptText,
        CheckCircle2,
        ArrowLeft,
    } from "lucide-svelte";
    import type {
        PlanDefinitionResponse,
        SubscriptionPlan,
        SubscriptionResponse,
    } from "$lib/types";

    let loading = $state(true);
    let paying = $state(false);
    let error = $state<string | null>(null);
    let plan = $state<PlanDefinitionResponse | null>(null);
    let source = $state<"settings" | "onboarding">("settings");
    
    // Confetti celebration
    let showConfetti = $state(false);

    let cardholderName = $state("");
    let cardNumber = $state("");
    let expiry = $state("");
    let cvv = $state("");

    function formatCardNumberInput(raw: string) {
        const digitsOnly = raw.replace(/\D+/g, "").slice(0, 19);
        return digitsOnly.replace(/(\d{4})(?=\d)/g, "$1 ").trim();
    }

    function formatExpiryInput(raw: string) {
        const digitsOnly = raw.replace(/\D+/g, "").slice(0, 4);
        if (digitsOnly.length <= 2) return digitsOnly;
        return `${digitsOnly.slice(0, 2)}/${digitsOnly.slice(2)}`;
    }

    function formatCvvInput(raw: string) {
        return raw.replace(/\D+/g, "").slice(0, 4);
    }

    function redirectBack() {
        if (source === "onboarding") {
            goto("/onboarding/plan");
            return;
        }
        goto("/settings/subscription");
    }

    function isCardInputValid() {
        const digits = cardNumber.replace(/\s+/g, "");
        const expiryRegex = /^(0[1-9]|1[0-2])\/\d{2}$/;
        const cvvRegex = /^\d{3,4}$/;
        return (
            cardholderName.trim().length >= 3 &&
            digits.length >= 12 &&
            expiryRegex.test(expiry.trim()) &&
            cvvRegex.test(cvv.trim())
        );
    }

    async function payAndUpgrade() {
        if (!plan) return;
        if (!isCardInputValid()) {
            error = "Please provide valid payment details.";
            return;
        }

        paying = true;
        error = null;
        try {
            await api.post<SubscriptionResponse>(
                "/admin/subscriptions/checkout",
                {
                    planCode: plan.planCode,
                },
            );

            // 🎉 Celebrate successful payment!
            showConfetti = true;
            
            // Wait for confetti animation before redirecting
            setTimeout(() => {
                if (source === "onboarding") {
                    if (auth.user?.role === "SUPER_ADMIN") {
                        goto("/admin/dashboard");
                    } else {
                        goto("/dashboard");
                    }
                } else {
                    goto("/settings/subscription");
                }
            }, 4500);
        } catch (err: any) {
            error = err?.response?.data?.message || "Payment failed.";
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
            const selectedPlanCode = params.get(
                "planCode",
            ) as SubscriptionPlan | null;
            source =
                params.get("source") === "onboarding"
                    ? "onboarding"
                    : "settings";

            if (!selectedPlanCode) {
                error = "No plan selected for checkout.";
                return;
            }

            const { data } =
                await api.get<PlanDefinitionResponse[]>("/admin/plans");
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
    <title>Payment Checkout — Survey Engine</title>
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
                    <p class="text-sm text-muted-foreground">
                        Review plan details and complete your payment.
                    </p>
                </div>
            </div>
            <Badge variant="secondary" class="w-fit">MVP Mock Gateway</Badge>
        </div>

        {#if error}
            <div class="rounded-lg border border-destructive/30 bg-destructive/10 p-3 text-sm text-destructive">
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
                        <Card.Title class="flex items-center gap-2">
                            <CreditCard class="h-5 w-5 text-primary" />
                            Payment Details
                        </Card.Title>
                        <Card.Description>
                            Enter card details to complete checkout for <span class="font-medium text-foreground">{plan.displayName}</span>.
                        </Card.Description>
                    </Card.Header>
                    <Card.Content class="space-y-5 pt-5">
                        <div class="space-y-2">
                            <Label for="cardholder">Cardholder Name</Label>
                            <Input
                                id="cardholder"
                                bind:value={cardholderName}
                                placeholder="John Doe"
                                autocomplete="cc-name"
                            />
                        </div>
                        <div class="space-y-2">
                            <Label for="cardnum">Card Number</Label>
                            <Input
                                id="cardnum"
                                bind:value={cardNumber}
                                placeholder="4242 4242 4242 4242"
                                inputmode="numeric"
                                maxlength={23}
                                autocomplete="cc-number"
                                oninput={(e) => {
                                    const target = e.currentTarget as HTMLInputElement;
                                    cardNumber = formatCardNumberInput(target.value);
                                }}
                            />
                        </div>
                        <div class="grid grid-cols-2 gap-3">
                            <div class="space-y-2">
                                <Label for="exp">Expiry (MM/YY)</Label>
                                <Input
                                    id="exp"
                                    bind:value={expiry}
                                    placeholder="12/30"
                                    inputmode="numeric"
                                    maxlength={5}
                                    autocomplete="cc-exp"
                                    oninput={(e) => {
                                        const target = e.currentTarget as HTMLInputElement;
                                        expiry = formatExpiryInput(target.value);
                                    }}
                                />
                            </div>
                            <div class="space-y-2">
                                <Label for="cvv">CVV</Label>
                                <Input
                                    id="cvv"
                                    bind:value={cvv}
                                    placeholder="123"
                                    inputmode="numeric"
                                    maxlength={4}
                                    autocomplete="cc-csc"
                                    oninput={(e) => {
                                        const target = e.currentTarget as HTMLInputElement;
                                        cvv = formatCvvInput(target.value);
                                    }}
                                />
                            </div>
                        </div>

                        <div class="rounded-lg border border-border/60 bg-muted/20 p-3 text-xs text-muted-foreground">
                            <div class="flex items-center gap-2 text-foreground">
                                <LockKeyhole class="h-3.5 w-3.5 text-emerald-600" />
                                <span class="font-medium">Secure simulation mode</span>
                            </div>
                            <p class="mt-1">
                                No real card processing occurs in MVP. Payment data is validated client-side for demo flow only.
                            </p>
                        </div>
                    </Card.Content>
                    <Card.Footer class="flex flex-col-reverse gap-2 border-t border-border/60 pt-4 sm:flex-row sm:justify-end">
                        <Button variant="outline" onclick={redirectBack} disabled={paying}>
                            Cancel
                        </Button>
                        <Button onclick={payAndUpgrade} disabled={paying}>
                            {#if paying}
                                <span class="mr-2 inline-block h-4 w-4 animate-spin rounded-full border-2 border-current border-t-transparent"></span>
                                Processing...
                            {:else}
                                Pay {plan.currency} {plan.price} & Activate
                            {/if}
                        </Button>
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
                                <span class="text-muted-foreground">Tax</span>
                                <span>{plan.currency} 0.00</span>
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

                    <div class="rounded-lg border border-border/60 bg-muted/10 px-3 py-2 text-xs text-muted-foreground">
                        <div class="flex items-center gap-2 text-foreground">
                            <ShieldCheck class="h-3.5 w-3.5 text-emerald-600" />
                            <span class="font-medium">Protected checkout</span>
                        </div>
                        <p class="mt-1">
                            Session is authenticated and plan checkout is scoped to your tenant.
                        </p>
                    </div>
                </div>
            </div>
        {/if}
    </div>

    <!-- 🎉 Confetti Celebration for Successful Payment -->
    {#if showConfetti}
        <Confetti
            fire={showConfetti}
            particleCount={150}
            spread={100}
            startVelocity={55}
            duration={4000}
            colors={['#10b981', '#3b82f6', '#8b5cf6', '#f59e0b', '#FFD700', '#06b6d4']}
            showBanner={true}
            title="🎉 Payment Successful!"
            message="Your subscription has been activated. Welcome to the premium experience!"
            gravity={0.9}
            onComplete={() => (showConfetti = false)}
        />
    {/if}
</div>
