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

<div class="min-h-screen bg-background selection:bg-primary/20">
    <!-- Nav (Glassmorphism) -->
    <nav
        class="sticky top-0 z-50 bg-background/60 backdrop-blur-xl border-b border-white/5 shadow-sm"
    >
        <div
            class="flex items-center justify-between px-6 py-4 max-w-7xl mx-auto"
        >
            <div class="flex items-center gap-4">
                <Button
                    variant="ghost"
                    size="icon"
                    href="/"
                    class="hover:bg-white/5 transition-colors"
                >
                    <ArrowLeft class="h-5 w-5" />
                </Button>
                <a href="/" class="flex items-center gap-3 group">
                    <div
                        class="flex h-9 w-9 items-center justify-center rounded-xl bg-gradient-to-br from-primary to-purple-600 shadow-lg shadow-primary/20 text-primary-foreground text-sm font-bold transition-transform group-hover:scale-105 duration-300"
                    >
                        SE
                    </div>
                    <span
                        class="text-xl font-bold bg-clip-text text-transparent bg-gradient-to-r from-foreground to-foreground/70 hidden sm:block tracking-tight"
                        >Survey Engine</span
                    >
                </a>
            </div>
            <div class="flex items-center gap-4">
                <Button
                    variant="ghost"
                    href="/login"
                    class="font-medium hover:bg-white/5">Sign in</Button
                >
                <Button
                    href="/register"
                    class="font-medium shadow-md hover:shadow-lg transition-all"
                    >Get Started</Button
                >
            </div>
        </div>
    </nav>

    <!-- Header Hero -->
    <section class="relative pt-24 pb-16 px-6 text-center overflow-hidden">
        <!-- Decorative background glow -->
        <div
            class="absolute top-0 left-1/2 -translate-x-1/2 w-[600px] h-[300px] bg-primary/20 blur-[120px] rounded-full pointer-events-none"
        ></div>

        <div
            class="relative z-10 animate-in fade-in slide-in-from-bottom-4 duration-700 ease-out"
        >
            <Badge
                variant="outline"
                class="mb-6 px-4 py-1.5 bg-background/50 backdrop-blur-sm border-primary/20 text-primary shadow-sm hover:bg-primary/5 transition-colors cursor-default"
            >
                <Sparkles
                    class="mr-2 h-3.5 w-3.5 text-purple-500 animate-pulse"
                />
                <span class="font-medium"
                    >14-day free trial · No credit card required</span
                >
            </Badge>
            <h1
                class="text-5xl font-extrabold text-foreground sm:text-7xl tracking-tighter max-w-3xl mx-auto leading-tight"
            >
                Plans tailored for <br /><span
                    class="text-transparent bg-clip-text bg-gradient-to-r from-primary via-purple-500 to-pink-500"
                    >every team</span
                >
            </h1>
            <p
                class="mt-6 text-xl text-muted-foreground max-w-2xl mx-auto leading-relaxed font-medium"
            >
                Start free, securely gather insights, and scale seamlessly with
                a plan designed to grow with your organization.
            </p>
        </div>
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
                class="grid grid-cols-1 md:grid-cols-3 gap-6 max-w-6xl mx-auto animate-in fade-in slide-in-from-bottom-8 duration-700 delay-150 fill-mode-both"
            >
                {#each plans as plan, i}
                    <div
                        class="group relative flex flex-col rounded-3xl border bg-card/50 backdrop-blur-xl transition-all duration-300 hover:shadow-2xl hover:-translate-y-2
                            {plan.highlight
                            ? 'border-primary/50 shadow-xl shadow-primary/10 scale-105 z-10 bg-card'
                            : 'border-white/10 hover:border-white/20'}"
                    >
                        {#if plan.highlight}
                            <div
                                class="absolute -top-px left-8 right-8 h-[2px] bg-gradient-to-r from-transparent via-primary to-transparent"
                            ></div>
                            <div
                                class="absolute -inset-0.5 rounded-3xl bg-gradient-to-b from-primary/20 to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-500 pointer-events-none"
                            ></div>
                        {/if}

                        <div
                            class="p-8 pb-10 flex-1 flex flex-col relative z-20"
                        >
                            <!-- Plan Header -->
                            <div class="mb-8 relative">
                                {#if plan.highlight}
                                    <div class="absolute -top-4 right-0">
                                        <Badge
                                            class="bg-gradient-to-r from-primary to-purple-500 text-white shadow-md border-0 uppercase tracking-widest text-[10px] py-1"
                                            >Most Popular</Badge
                                        >
                                    </div>
                                {/if}
                                <h2
                                    class="text-2xl font-bold text-foreground tracking-tight"
                                >
                                    {plan.name}
                                </h2>
                                <p
                                    class="mt-3 text-sm text-muted-foreground leading-relaxed h-10"
                                >
                                    {plan.description}
                                </p>
                            </div>

                            <!-- Price -->
                            <div class="mb-8">
                                <div
                                    class="flex items-baseline gap-1.5 border-b border-border/40 pb-6"
                                >
                                    <span
                                        class="text-6xl font-extrabold tracking-tighter text-foreground"
                                        >{plan.price}</span
                                    >
                                    {#if plan.price !== "Free" && plan.price !== "Custom"}
                                        <span
                                            class="text-base font-medium text-muted-foreground"
                                            >/{plan.period.replace(
                                                "per ",
                                                "",
                                            )}</span
                                        >
                                    {/if}
                                </div>
                                {#if plan.price === "Free"}
                                    <p
                                        class="text-sm font-medium text-muted-foreground mt-4"
                                    >
                                        {plan.period}
                                    </p>
                                {:else if plan.price === "Custom"}
                                    <p
                                        class="text-sm font-medium text-muted-foreground mt-4"
                                    >
                                        Tailored to your exact scale
                                    </p>
                                {:else}
                                    <p
                                        class="text-sm font-medium text-muted-foreground mt-4"
                                    >
                                        Billed {plan.period}
                                    </p>
                                {/if}
                            </div>

                            <!-- CTA -->
                            <Button
                                variant={plan.highlight
                                    ? "default"
                                    : "secondary"}
                                class="w-full mb-8 py-6 rounded-xl text-base font-semibold shadow-sm group-hover:shadow-md transition-all
                                    {plan.highlight
                                    ? 'bg-primary hover:bg-primary/90'
                                    : 'bg-secondary hover:bg-secondary/80'}"
                                href="/register"
                            >
                                {plan.cta}
                                <ArrowRight
                                    class="ml-2 h-4 w-4 transition-transform group-hover:translate-x-1"
                                />
                            </Button>

                            <!-- Limits -->
                            <div class="space-y-4 text-sm mt-auto">
                                <p
                                    class="font-semibold text-foreground text-xs uppercase tracking-widest mb-4"
                                >
                                    Plan Limits
                                </p>
                                <div class="flex justify-between items-center">
                                    <span
                                        class="text-muted-foreground flex items-center gap-2"
                                    >
                                        <div
                                            class="h-1.5 w-1.5 rounded-full bg-primary/40"
                                        ></div>
                                        Active Campaigns
                                    </span>
                                    <span class="font-bold text-foreground"
                                        >{plan.limits.campaigns}</span
                                    >
                                </div>
                                <div class="flex justify-between items-center">
                                    <span
                                        class="text-muted-foreground flex items-center gap-2"
                                    >
                                        <div
                                            class="h-1.5 w-1.5 rounded-full bg-primary/40"
                                        ></div>
                                        Responses / Campaign
                                    </span>
                                    <span class="font-bold text-foreground"
                                        >{plan.limits.responses}</span
                                    >
                                </div>
                                <div class="flex justify-between items-center">
                                    <span
                                        class="text-muted-foreground flex items-center gap-2"
                                    >
                                        <div
                                            class="h-1.5 w-1.5 rounded-full bg-primary/40"
                                        ></div>
                                        Admin Users
                                    </span>
                                    <span class="font-bold text-foreground"
                                        >{plan.limits.admins}</span
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
    <section class="max-w-5xl mx-auto px-6 pb-24 relative z-10">
        <div
            class="text-center mb-16 animate-in fade-in slide-in-from-bottom-8 duration-700 delay-300 fill-mode-both"
        >
            <h2 class="text-3xl font-extrabold text-foreground tracking-tight">
                Compare all features
            </h2>
            <p class="mt-4 text-lg text-muted-foreground font-medium">
                See exactly what's included in each plan.
            </p>
        </div>

        <div
            class="rounded-3xl border border-border/60 bg-card/30 backdrop-blur-md overflow-hidden shadow-2xl animate-in fade-in slide-in-from-bottom-8 duration-700 delay-500 fill-mode-both"
        >
            <!-- Table Header -->
            <div
                class="grid grid-cols-4 bg-muted/40 text-sm font-semibold border-b border-border/60"
            >
                <div class="p-6 text-muted-foreground font-medium text-base">
                    Feature
                </div>
                <div
                    class="p-6 text-center text-foreground font-bold text-base"
                >
                    Basic
                </div>
                <div
                    class="p-6 text-center text-primary font-bold text-base bg-primary/[0.04] relative"
                >
                    <div
                        class="absolute top-0 left-0 right-0 h-1 bg-gradient-to-r from-primary to-purple-500"
                    ></div>
                    Pro
                </div>
                <div
                    class="p-6 text-center text-foreground font-bold text-base"
                >
                    Enterprise
                </div>
            </div>

            {#each featureCategories as category, ci}
                <!-- Category Header -->
                <div
                    class="grid grid-cols-4 bg-muted/10 border-b border-border/40"
                >
                    <div
                        class="p-4 px-6 text-xs font-bold uppercase tracking-widest text-foreground col-span-4"
                    >
                        {category.name}
                    </div>
                </div>

                <!-- Feature Rows -->
                {#each category.features as feature}
                    <div
                        class="grid grid-cols-4 border-b border-border/30 text-sm hover:bg-muted/30 transition-colors duration-200"
                    >
                        <div
                            class="p-4 px-6 text-muted-foreground font-medium flex items-center"
                        >
                            {feature.name}
                        </div>
                        <div class="p-4 flex items-center justify-center">
                            {#if feature.basic}
                                <div
                                    class="flex h-6 w-6 items-center justify-center rounded-full bg-emerald-500/10 text-emerald-500"
                                >
                                    <Check class="h-4 w-4" strokeWidth={3} />
                                </div>
                            {:else}
                                <X class="h-4 w-4 text-muted-foreground/30" />
                            {/if}
                        </div>
                        <div
                            class="p-4 flex items-center justify-center bg-primary/[0.02]"
                        >
                            {#if feature.pro}
                                <div
                                    class="flex h-6 w-6 items-center justify-center rounded-full bg-primary/10 text-primary"
                                >
                                    <Check class="h-4 w-4" strokeWidth={3} />
                                </div>
                            {:else}
                                <X class="h-4 w-4 text-muted-foreground/30" />
                            {/if}
                        </div>
                        <div class="p-4 flex items-center justify-center">
                            {#if feature.enterprise}
                                <div
                                    class="flex h-6 w-6 items-center justify-center rounded-full bg-emerald-500/10 text-emerald-500"
                                >
                                    <Check class="h-4 w-4" strokeWidth={3} />
                                </div>
                            {:else}
                                <X class="h-4 w-4 text-muted-foreground/30" />
                            {/if}
                        </div>
                    </div>
                {/each}
            {/each}
        </div>

        <!-- Bottom CTA -->
        <div class="mt-24 text-center relative">
            <div
                class="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-[400px] h-[200px] bg-primary/10 blur-[100px] rounded-full pointer-events-none"
            ></div>
            <h2
                class="text-3xl font-bold text-foreground mb-6 relative z-10 tracking-tight"
            >
                Ready to transform your data collection?
            </h2>
            <p
                class="text-lg text-muted-foreground mb-10 max-w-xl mx-auto font-medium relative z-10"
            >
                Join thousands of organizations building high-conversion surveys
                with Survey Engine.
            </p>
            <div
                class="flex flex-col sm:flex-row items-center justify-center gap-4 relative z-10"
            >
                <Button
                    size="lg"
                    class="px-10 py-6 text-base font-semibold rounded-xl shadow-lg shadow-primary/25 hover:shadow-xl hover:shadow-primary/30 transition-all hover:-translate-y-1"
                    href="/register"
                >
                    Start Your Free Trial
                    <ArrowRight class="ml-2 h-5 w-5" />
                </Button>
                <Button
                    size="lg"
                    variant="outline"
                    class="px-10 py-6 text-base font-semibold rounded-xl"
                    href="/contact"
                >
                    Contact Sales
                </Button>
            </div>
            <p class="text-muted-foreground text-sm mt-8 opacity-70">
                All plans include SSL encryption, daily backups, and a 99.9%
                uptime SLA.
            </p>
        </div>
    </section>
</div>
