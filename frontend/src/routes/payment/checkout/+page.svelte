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
    import { CreditCard, ShieldCheck } from "lucide-svelte";
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

    let cardholderName = $state("");
    let cardNumber = $state("");
    let expiry = $state("");
    let cvv = $state("");

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

            if (source === "onboarding") {
                if (auth.user?.role === "SUPER_ADMIN") {
                    goto("/admin/dashboard");
                } else {
                    goto("/dashboard");
                }
            } else {
                goto("/settings/subscription");
            }
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

<div
    class="min-h-screen bg-gradient-to-br from-background via-background to-muted/30 p-4 md:p-8"
>
    <div class="mx-auto max-w-3xl space-y-6">
        <div class="flex items-center justify-between">
            <div>
                <h1 class="text-2xl font-bold text-foreground">
                    Payment Checkout
                </h1>
                <p class="text-sm text-muted-foreground">
                    Complete payment to activate the selected plan.
                </p>
            </div>
            <Badge variant="secondary">Mock Gateway</Badge>
        </div>

        {#if error}
            <div
                class="rounded-lg border border-destructive/30 bg-destructive/10 p-3 text-sm text-destructive"
            >
                {error}
            </div>
        {/if}

        {#if loading}
            <div class="flex items-center justify-center py-16">
                <span
                    class="inline-block h-8 w-8 animate-spin rounded-full border-4 border-primary border-t-transparent"
                ></span>
            </div>
        {:else if plan}
            <div class="grid gap-6 md:grid-cols-2">
                <Card.Root>
                    <Card.Header>
                        <Card.Title class="flex items-center gap-2">
                            <ShieldCheck class="h-5 w-5 text-primary" />
                            Order Summary
                        </Card.Title>
                    </Card.Header>
                    <Card.Content class="space-y-4 text-sm">
                        <div class="flex justify-between">
                            <span class="text-muted-foreground">Plan</span>
                            <span class="font-medium">{plan.displayName}</span>
                        </div>
                        <div class="flex justify-between">
                            <span class="text-muted-foreground">Price</span>
                            <span class="font-medium"
                                >{plan.currency} {plan.price}</span
                            >
                        </div>
                        <div class="flex justify-between">
                            <span class="text-muted-foreground"
                                >Billing cycle</span
                            >
                            <span class="font-medium"
                                >{plan.billingCycleDays} days</span
                            >
                        </div>
                        <div class="pt-2 text-xs text-muted-foreground">
                            This checkout uses a mock payment gateway in MVP
                            mode.
                        </div>
                    </Card.Content>
                </Card.Root>

                <Card.Root>
                    <Card.Header>
                        <Card.Title class="flex items-center gap-2">
                            <CreditCard class="h-5 w-5 text-primary" />
                            Payment Details
                        </Card.Title>
                    </Card.Header>
                    <Card.Content class="space-y-3">
                        <div class="space-y-2">
                            <Label for="cardholder">Cardholder Name</Label>
                            <Input
                                id="cardholder"
                                bind:value={cardholderName}
                                placeholder="John Doe"
                            />
                        </div>
                        <div class="space-y-2">
                            <Label for="cardnum">Card Number</Label>
                            <Input
                                id="cardnum"
                                bind:value={cardNumber}
                                placeholder="4242 4242 4242 4242"
                            />
                        </div>
                        <div class="grid grid-cols-2 gap-3">
                            <div class="space-y-2">
                                <Label for="exp">Expiry (MM/YY)</Label>
                                <Input
                                    id="exp"
                                    bind:value={expiry}
                                    placeholder="12/30"
                                />
                            </div>
                            <div class="space-y-2">
                                <Label for="cvv">CVV</Label>
                                <Input
                                    id="cvv"
                                    bind:value={cvv}
                                    placeholder="123"
                                />
                            </div>
                        </div>
                    </Card.Content>
                    <Card.Footer class="flex justify-end gap-2">
                        <Button
                            variant="outline"
                            onclick={redirectBack}
                            disabled={paying}
                        >
                            Cancel
                        </Button>
                        <Button onclick={payAndUpgrade} disabled={paying}>
                            {#if paying}
                                <span
                                    class="mr-2 inline-block h-4 w-4 animate-spin rounded-full border-2 border-current border-t-transparent"
                                ></span>
                                Processing...
                            {:else}
                                Pay & Upgrade
                            {/if}
                        </Button>
                    </Card.Footer>
                </Card.Root>
            </div>
        {/if}
    </div>
</div>
