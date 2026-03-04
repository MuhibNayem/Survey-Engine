<script lang="ts">
    import { Button } from "$lib/components/ui/button";
    import { Badge } from "$lib/components/ui/badge";
    import { Separator } from "$lib/components/ui/separator";
    import { Check, X, ArrowLeft, Sparkles, ArrowRight } from "lucide-svelte";
    import api from "$lib/api";
    import type { PlanDefinitionResponse } from "$lib/types";
    import { onMount } from "svelte";

    let apiPlans = $state<PlanDefinitionResponse[]>([]);
    let loading = $state(true);

    onMount(async () => {
        try {
            const { data } =
                await api.get<PlanDefinitionResponse[]>("/admin/plans");
            if (data?.length) apiPlans = data;
        } catch {
            /* use defaults */
        } finally {
            loading = false;
        }
    });

    interface PlanDisplay {
        code: string;
        name: string;
        description: string;
        price: string;
        period: string;
        highlight: boolean;
        cta: string;
        limits: {
            campaigns: string;
            responses: string;
            admins: string;
            trial: string;
        };
    }

    function buildPlans(api: PlanDefinitionResponse[]): PlanDisplay[] {
        const defaults: PlanDisplay[] = [
            {
                code: "BASIC",
                name: "Basic",
                description:
                    "Perfect for individuals and small teams getting started.",
                price: "Free",
                period: "14-day trial",
                highlight: false,
                cta: "Start Free Trial",
                limits: {
                    campaigns: "5",
                    responses: "100",
                    admins: "2",
                    trial: "14 days",
                },
            },
            {
                code: "PRO",
                name: "Pro",
                description:
                    "For growing organizations that need advanced features.",
                price: "$49",
                period: "per month",
                highlight: true,
                cta: "Get Started",
                limits: {
                    campaigns: "25",
                    responses: "1,000",
                    admins: "10",
                    trial: "14 days",
                },
            },
            {
                code: "ENTERPRISE",
                name: "Enterprise",
                description: "Unlimited scale with dedicated support and SLA.",
                price: "Custom",
                period: "contact us",
                highlight: false,
                cta: "Contact Sales",
                limits: {
                    campaigns: "Unlimited",
                    responses: "Unlimited",
                    admins: "Unlimited",
                    trial: "30 days",
                },
            },
        ];

        if (!api.length) return defaults;

        return api.map((p) => {
            const fallback =
                defaults.find((d) => d.code === p.planCode) ?? defaults[0];
            const fmt = (v: number | null) =>
                v == null ? "Unlimited" : v.toLocaleString();
            const fmtPrice = (v: number) => (v === 0 ? "Free" : `$${v}`);
            return {
                ...fallback,
                code: p.planCode,
                name: p.displayName || fallback.name,
                price: fmtPrice(p.price),
                period:
                    p.price === 0 ? `${p.trialDays}-day trial` : "per month",
                limits: {
                    campaigns: fmt(p.maxCampaigns),
                    responses: fmt(p.maxResponsesPerCampaign),
                    admins: fmt(p.maxAdminUsers),
                    trial: `${p.trialDays} days`,
                },
            };
        });
    }

    const plans = $derived(buildPlans(apiPlans));

    // Feature comparison table
    const featureCategories = [
        {
            name: "Survey & Content",
            features: [
                {
                    name: "Question Bank (CRUD + Versioning)",
                    basic: true,
                    pro: true,
                    enterprise: true,
                },
                {
                    name: "Categories with Weighted Mappings",
                    basic: true,
                    pro: true,
                    enterprise: true,
                },
                {
                    name: "Multi-Page Survey Builder",
                    basic: true,
                    pro: true,
                    enterprise: true,
                },
                {
                    name: "Survey Snapshots on Publish",
                    basic: true,
                    pro: true,
                    enterprise: true,
                },
                {
                    name: "Custom Branding & Themes",
                    basic: false,
                    pro: true,
                    enterprise: true,
                },
            ],
        },
        {
            name: "Campaigns & Distribution",
            features: [
                {
                    name: "Public Campaigns",
                    basic: true,
                    pro: true,
                    enterprise: true,
                },
                {
                    name: "Private Campaigns (Token Auth)",
                    basic: false,
                    pro: true,
                    enterprise: true,
                },
                {
                    name: "6 Distribution Channels",
                    basic: true,
                    pro: true,
                    enterprise: true,
                },
                {
                    name: "Response Quotas & IP Restrictions",
                    basic: true,
                    pro: true,
                    enterprise: true,
                },
                {
                    name: "Device Fingerprint Dedup",
                    basic: false,
                    pro: true,
                    enterprise: true,
                },
            ],
        },
        {
            name: "Authentication",
            features: [
                {
                    name: "Admin JWT Auth",
                    basic: true,
                    pro: true,
                    enterprise: true,
                },
                {
                    name: "OIDC / PKCE Respondent Auth",
                    basic: false,
                    pro: true,
                    enterprise: true,
                },
                {
                    name: "Custom SSO Integration",
                    basic: false,
                    pro: false,
                    enterprise: true,
                },
                {
                    name: "Auth Provider Templates (Okta, Auth0)",
                    basic: false,
                    pro: true,
                    enterprise: true,
                },
            ],
        },
        {
            name: "Analytics & Scoring",
            features: [
                {
                    name: "Basic Campaign Analytics",
                    basic: true,
                    pro: true,
                    enterprise: true,
                },
                {
                    name: "Weighted Scoring Engine",
                    basic: false,
                    pro: true,
                    enterprise: true,
                },
                {
                    name: "Response Locking & Reopen Audit",
                    basic: true,
                    pro: true,
                    enterprise: true,
                },
            ],
        },
        {
            name: "Support",
            features: [
                {
                    name: "Email Support",
                    basic: true,
                    pro: true,
                    enterprise: true,
                },
                {
                    name: "Priority Support",
                    basic: false,
                    pro: true,
                    enterprise: true,
                },
                {
                    name: "Dedicated Account Manager",
                    basic: false,
                    pro: false,
                    enterprise: true,
                },
                {
                    name: "SLA Guarantee",
                    basic: false,
                    pro: false,
                    enterprise: true,
                },
            ],
        },
    ];
</script>

<svelte:head>
    <title>Pricing — Survey Engine</title>
    <meta
        name="description"
        content="Simple, transparent pricing. Start free, upgrade as you grow."
    />
</svelte:head>

<div class="min-h-screen bg-background">
    <!-- Nav -->
    <nav
        class="sticky top-0 z-50 backdrop-blur-md bg-background/80 border-b border-border"
    >
        <div
            class="flex items-center justify-between px-6 py-3 max-w-7xl mx-auto"
        >
            <div class="flex items-center gap-4">
                <Button variant="ghost" size="icon" href="/">
                    <ArrowLeft class="h-5 w-5" />
                </Button>
                <a href="/" class="flex items-center gap-2">
                    <div
                        class="flex h-8 w-8 items-center justify-center rounded-lg bg-primary text-primary-foreground text-sm font-bold"
                    >
                        SE
                    </div>
                    <span
                        class="text-lg font-bold text-foreground hidden sm:block"
                        >Survey Engine</span
                    >
                </a>
            </div>
            <div class="flex items-center gap-3">
                <Button variant="ghost" href="/login">Sign in</Button>
                <Button href="/register">Get Started</Button>
            </div>
        </div>
    </nav>

    <!-- Header -->
    <section class="pt-16 pb-4 px-6 text-center">
        <Badge variant="secondary" class="mb-5 px-4 py-1.5">
            <Sparkles class="mr-1.5 h-3.5 w-3.5 text-purple-500" />
            14-day free trial · No credit card required
        </Badge>
        <h1
            class="text-4xl font-bold text-foreground sm:text-5xl tracking-tight"
        >
            Plans for every team
        </h1>
        <p class="mt-4 text-lg text-muted-foreground max-w-lg mx-auto">
            Start free, then scale with a plan that fits your needs.
        </p>
    </section>

    <!-- Plan Cards -->
    <section class="max-w-5xl mx-auto px-6 py-12">
        {#if loading}
            <div class="flex justify-center py-16">
                <div
                    class="h-8 w-8 animate-spin rounded-full border-4 border-primary border-t-transparent"
                ></div>
            </div>
        {:else}
            <div
                class="grid grid-cols-1 md:grid-cols-3 gap-0 rounded-2xl border border-border overflow-hidden"
            >
                {#each plans as plan, i}
                    <div
                        class="relative flex flex-col {plan.highlight
                            ? 'bg-primary/[0.03] border-x border-primary/20 md:border-x'
                            : ''} {i === 0
                            ? ''
                            : 'border-t md:border-t-0 md:border-l'} border-border"
                    >
                        {#if plan.highlight}
                            <div
                                class="absolute -top-px left-0 right-0 h-[3px] bg-gradient-to-r from-primary via-purple-500 to-pink-500 rounded-t-2xl"
                            ></div>
                        {/if}

                        <div class="p-6 lg:p-8 flex-1 flex flex-col">
                            <!-- Plan Header -->
                            <div class="mb-6">
                                {#if plan.highlight}
                                    <Badge class="mb-3">Most Popular</Badge>
                                {/if}
                                <h2 class="text-xl font-bold text-foreground">
                                    {plan.name}
                                </h2>
                                <p
                                    class="mt-1 text-sm text-muted-foreground leading-relaxed"
                                >
                                    {plan.description}
                                </p>
                            </div>

                            <!-- Price -->
                            <div class="mb-6">
                                <div class="flex items-baseline gap-1">
                                    <span
                                        class="text-4xl font-extrabold text-foreground"
                                        >{plan.price}</span
                                    >
                                    {#if plan.price !== "Free" && plan.price !== "Custom"}
                                        <span
                                            class="text-sm text-muted-foreground"
                                            >/{plan.period.replace(
                                                "per ",
                                                "",
                                            )}</span
                                        >
                                    {/if}
                                </div>
                                {#if plan.price === "Free"}
                                    <p
                                        class="text-sm text-muted-foreground mt-1"
                                    >
                                        {plan.period}
                                    </p>
                                {:else if plan.price === "Custom"}
                                    <p
                                        class="text-sm text-muted-foreground mt-1"
                                    >
                                        Tailored to your needs
                                    </p>
                                {/if}
                            </div>

                            <!-- CTA -->
                            <Button
                                variant={plan.highlight ? "default" : "outline"}
                                class="w-full mb-6 py-5"
                                href="/register"
                            >
                                {plan.cta}
                                <ArrowRight class="ml-1.5 h-4 w-4" />
                            </Button>

                            <!-- Limits -->
                            <div class="space-y-3 text-sm">
                                <p
                                    class="font-medium text-foreground text-xs uppercase tracking-wider"
                                >
                                    Includes
                                </p>
                                <div
                                    class="flex justify-between py-2 border-b border-border/50"
                                >
                                    <span class="text-muted-foreground"
                                        >Active Campaigns</span
                                    >
                                    <span class="font-semibold text-foreground"
                                        >{plan.limits.campaigns}</span
                                    >
                                </div>
                                <div
                                    class="flex justify-between py-2 border-b border-border/50"
                                >
                                    <span class="text-muted-foreground"
                                        >Responses / Campaign</span
                                    >
                                    <span class="font-semibold text-foreground"
                                        >{plan.limits.responses}</span
                                    >
                                </div>
                                <div
                                    class="flex justify-between py-2 border-b border-border/50"
                                >
                                    <span class="text-muted-foreground"
                                        >Admin Users</span
                                    >
                                    <span class="font-semibold text-foreground"
                                        >{plan.limits.admins}</span
                                    >
                                </div>
                                <div class="flex justify-between py-2">
                                    <span class="text-muted-foreground"
                                        >Free Trial</span
                                    >
                                    <span class="font-semibold text-foreground"
                                        >{plan.limits.trial}</span
                                    >
                                </div>
                            </div>
                        </div>
                    </div>
                {/each}
            </div>
        {/if}
    </section>

    <!-- Feature Comparison Table -->
    <section class="max-w-5xl mx-auto px-6 pb-24">
        <div class="text-center mb-10">
            <h2 class="text-2xl font-bold text-foreground">
                Compare all features
            </h2>
            <p class="mt-2 text-muted-foreground">
                See exactly what's included in each plan.
            </p>
        </div>

        <div class="rounded-2xl border border-border overflow-hidden">
            <!-- Table Header -->
            <div class="grid grid-cols-4 bg-muted/50 text-sm font-semibold">
                <div class="p-4 text-muted-foreground">Feature</div>
                <div class="p-4 text-center text-foreground">Basic</div>
                <div class="p-4 text-center text-foreground bg-primary/[0.03]">
                    Pro
                </div>
                <div class="p-4 text-center text-foreground">Enterprise</div>
            </div>

            {#each featureCategories as category, ci}
                <!-- Category Header -->
                <div
                    class="grid grid-cols-4 bg-muted/30 border-t border-border"
                >
                    <div
                        class="p-3 px-4 text-xs font-bold uppercase tracking-wider text-muted-foreground col-span-4"
                    >
                        {category.name}
                    </div>
                </div>

                <!-- Feature Rows -->
                {#each category.features as feature}
                    <div
                        class="grid grid-cols-4 border-t border-border/50 text-sm hover:bg-muted/20 transition-colors"
                    >
                        <div class="p-3 px-4 text-muted-foreground">
                            {feature.name}
                        </div>
                        <div class="p-3 flex items-center justify-center">
                            {#if feature.basic}
                                <Check class="h-4 w-4 text-emerald-500" />
                            {:else}
                                <X class="h-4 w-4 text-muted-foreground/30" />
                            {/if}
                        </div>
                        <div
                            class="p-3 flex items-center justify-center bg-primary/[0.02]"
                        >
                            {#if feature.pro}
                                <Check class="h-4 w-4 text-emerald-500" />
                            {:else}
                                <X class="h-4 w-4 text-muted-foreground/30" />
                            {/if}
                        </div>
                        <div class="p-3 flex items-center justify-center">
                            {#if feature.enterprise}
                                <Check class="h-4 w-4 text-emerald-500" />
                            {:else}
                                <X class="h-4 w-4 text-muted-foreground/30" />
                            {/if}
                        </div>
                    </div>
                {/each}
            {/each}
        </div>

        <!-- Bottom CTA -->
        <div class="mt-12 text-center">
            <p class="text-muted-foreground mb-4">
                All plans include SSL, daily backups, and 99.9% uptime.
            </p>
            <Button size="lg" class="px-8 py-5" href="/register">
                Start Your Free Trial
                <ArrowRight class="ml-2 h-4 w-4" />
            </Button>
        </div>
    </section>
</div>
