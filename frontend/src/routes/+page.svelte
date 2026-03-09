<script lang="ts">
    import { auth } from "$lib/stores/auth.svelte";
    import { goto } from "$app/navigation";
    import { onMount } from "svelte";
    import { Button } from "$lib/components/ui/button";
    import { Badge } from "$lib/components/ui/badge";
    import * as Card from "$lib/components/ui/card";
    import {
        ArrowRight,
        CheckCircle2,
        BarChart3,
        Shield,
        Zap,
        Globe,
        Users,
        FileText,
        Megaphone,
        Star,
        ChevronRight,
    } from "lucide-svelte";

    import logo from "$lib/assets/logo.png";

    onMount(() => {
        if (auth.isAuthenticated) {
            if (auth.user?.role === "SUPER_ADMIN") {
                goto("/admin/dashboard");
            } else {
                goto("/dashboard");
            }
        }
    });

    const features = [
        {
            icon: FileText,
            title: "Smart Survey Builder",
            description:
                "Create multi-page surveys with versioned questions, categories, and skip logic.",
            color: "text-blue-500",
            bg: "bg-blue-500/10",
        },
        {
            icon: Shield,
            title: "Enterprise Authentication",
            description:
                "SSO, OIDC/PKCE, signed tokens — support any identity provider out of the box.",
            color: "text-emerald-500",
            bg: "bg-emerald-500/10",
        },
        {
            icon: Megaphone,
            title: "Campaign Management",
            description:
                "Deploy surveys with quotas, IP restrictions, device dedup, and 6 distribution channels.",
            color: "text-orange-500",
            bg: "bg-orange-500/10",
        },
        {
            icon: BarChart3,
            title: "Weighted Scoring Engine",
            description:
                "Multi-dimensional scoring with category weights, normalization, and real-time analytics.",
            color: "text-purple-500",
            bg: "bg-purple-500/10",
        },
        {
            icon: Users,
            title: "Multi-Tenant SaaS",
            description:
                "Complete tenant isolation, subscription plans, and quota enforcement built-in.",
            color: "text-pink-500",
            bg: "bg-pink-500/10",
        },
        {
            icon: Zap,
            title: "Auto-Locking & Audit",
            description:
                "Responses auto-lock on submit. Full lifecycle audit trail with reopen tracking.",
            color: "text-amber-500",
            bg: "bg-amber-500/10",
        },
    ];

    const plans = [
        {
            name: "Basic",
            price: "Free",
            period: "14-day trial",
            description: "Get started with core survey features",
            features: [
                "5 Active Campaigns",
                "100 Responses / Campaign",
                "2 Admin Users",
                "Public Surveys",
                "Basic Analytics",
            ],
            cta: "Start Free Trial",
            popular: false,
        },
        {
            name: "Pro",
            price: "$49",
            period: "/month",
            description: "For growing teams that need advanced features",
            features: [
                "25 Active Campaigns",
                "1,000 Responses / Campaign",
                "10 Admin Users",
                "Private + SSO Auth",
                "Weighted Scoring",
                "Custom Branding",
                "Priority Support",
            ],
            cta: "Get Started",
            popular: true,
        },
        {
            name: "Enterprise",
            price: "Custom",
            period: "contact us",
            description: "Unlimited scale with dedicated support",
            features: [
                "Unlimited Campaigns",
                "Unlimited Responses",
                "Unlimited Admin Users",
                "Custom SSO / OIDC",
                "Advanced Analytics",
                "SLA Guarantee",
                "Dedicated Account Manager",
            ],
            cta: "Contact Sales",
            popular: false,
        },
    ];
</script>

<svelte:head>
    <title>Survey Engine — Enterprise Survey Platform</title>
    <meta
        name="description"
        content="Build, deploy, and analyze surveys at scale. Multi-tenant SaaS with enterprise authentication, weighted scoring, and real-time analytics."
    />
</svelte:head>

<!-- Hero Section -->
<div class="relative overflow-hidden">
    <!-- Background gradient -->
    <div
        class="absolute inset-0 bg-gradient-to-br from-primary/5 via-background to-purple-500/5"
    ></div>
    <div
        class="absolute top-0 left-1/2 -translate-x-1/2 w-[800px] h-[600px] bg-gradient-radial from-primary/10 to-transparent rounded-full blur-3xl"
    ></div>

    <!-- Nav -->
    <nav
        class="relative z-10 flex items-center justify-between px-6 py-4 max-w-7xl mx-auto lg:px-8"
    >
        <a href="/" class="flex items-center gap-2">
            <img src={logo} alt="SE" class="h-9 w-9 object-contain" />
            <span class="text-xl font-bold text-foreground">Survey Engine</span>
        </a>
        <div class="flex items-center gap-3">
            <Button variant="ghost" href="/docs/api">API Docs</Button>
            <Button variant="ghost" href="/login">Sign in</Button>
            <Button href="/register">
                Get Started
                <ArrowRight class="ml-1 h-4 w-4" />
            </Button>
        </div>
    </nav>

    <!-- Hero Content -->
    <div
        class="relative z-10 max-w-7xl mx-auto px-6 lg:px-8 pt-20 pb-32 text-center"
    >
        <Badge variant="secondary" class="mb-6 px-4 py-1.5 text-sm">
            <Star class="mr-1 h-3.5 w-3.5 text-amber-500" />
            Now with OIDC/PKCE Authentication
        </Badge>

        <h1
            class="text-5xl font-extrabold tracking-tight text-foreground sm:text-6xl lg:text-7xl"
        >
            Surveys that
            <span
                class="text-transparent bg-clip-text bg-gradient-to-r from-primary via-purple-500 to-pink-500"
            >
                scale
            </span>
        </h1>

        <p
            class="mt-6 max-w-2xl mx-auto text-lg text-muted-foreground leading-relaxed"
        >
            Build, deploy, and analyze surveys with enterprise-grade
            authentication, weighted scoring, and multi-tenant isolation — all
            from a single platform.
        </p>

        <div class="mt-10 flex items-center justify-center gap-4">
            <Button size="lg" class="px-8 py-6 text-base" href="/register">
                Start Free Trial
                <ArrowRight class="ml-2 h-5 w-5" />
            </Button>
            <Button
                variant="outline"
                size="lg"
                class="px-8 py-6 text-base"
                href="/pricing"
            >
                View Pricing
            </Button>
            <Button
                variant="outline"
                size="lg"
                class="px-8 py-6 text-base"
                href="/docs/api"
            >
                API Documentation
            </Button>
        </div>

        <p class="mt-4 text-sm text-muted-foreground">
            No credit card required · 14-day free trial · Cancel anytime
        </p>
    </div>
</div>

<!-- Features Grid -->
<section class="py-24 bg-muted/30">
    <div class="max-w-7xl mx-auto px-6 lg:px-8">
        <div class="text-center mb-16">
            <h2 class="text-3xl font-bold text-foreground sm:text-4xl">
                Everything you need to run surveys at scale
            </h2>
            <p class="mt-4 text-lg text-muted-foreground max-w-2xl mx-auto">
                From question banks to weighted scoring — every feature is built
                for multi-tenant enterprise use.
            </p>
        </div>

        <div class="grid gap-8 sm:grid-cols-2 lg:grid-cols-3">
            {#each features as feature}
                <Card.Root
                    class="border-border/50 transition-all duration-300 hover:shadow-lg hover:border-primary/20 hover:-translate-y-1"
                >
                    <Card.Content class="pt-6">
                        <div
                            class="flex h-12 w-12 items-center justify-center rounded-xl {feature.bg} mb-4"
                        >
                            <feature.icon class="h-6 w-6 {feature.color}" />
                        </div>
                        <h3 class="text-lg font-semibold text-foreground mb-2">
                            {feature.title}
                        </h3>
                        <p
                            class="text-sm text-muted-foreground leading-relaxed"
                        >
                            {feature.description}
                        </p>
                    </Card.Content>
                </Card.Root>
            {/each}
        </div>
    </div>
</section>

<!-- Pricing Preview -->
<section class="py-24" id="pricing">
    <div class="max-w-7xl mx-auto px-6 lg:px-8">
        <div class="text-center mb-16">
            <h2 class="text-3xl font-bold text-foreground sm:text-4xl">
                Simple, transparent pricing
            </h2>
            <p class="mt-4 text-lg text-muted-foreground">
                Start free, scale as you grow.
            </p>
        </div>

        <div class="grid gap-8 lg:grid-cols-3 max-w-5xl mx-auto">
            {#each plans as plan}
                <Card.Root
                    class="relative {plan.popular
                        ? 'border-primary shadow-xl shadow-primary/10'
                        : 'border-border/50'}"
                >
                    {#if plan.popular}
                        <div class="absolute -top-3 left-1/2 -translate-x-1/2">
                            <Badge class="px-3 py-1">Most Popular</Badge>
                        </div>
                    {/if}
                    <Card.Header class="text-center pb-2">
                        <Card.Title class="text-xl">{plan.name}</Card.Title>
                        <Card.Description>{plan.description}</Card.Description>
                    </Card.Header>
                    <Card.Content class="text-center pb-4">
                        <div class="mt-2 mb-6">
                            <span class="text-4xl font-bold text-foreground"
                                >{plan.price}</span
                            >
                            <span class="text-muted-foreground text-sm ml-1"
                                >{plan.period}</span
                            >
                        </div>
                        <ul class="space-y-3 text-left">
                            {#each plan.features as feat}
                                <li class="flex items-center gap-2 text-sm">
                                    <CheckCircle2
                                        class="h-4 w-4 text-emerald-500 shrink-0"
                                    />
                                    <span class="text-muted-foreground"
                                        >{feat}</span
                                    >
                                </li>
                            {/each}
                        </ul>
                    </Card.Content>
                    <Card.Footer>
                        <Button
                            variant={plan.popular ? "default" : "outline"}
                            class="w-full"
                            href="/register"
                        >
                            {plan.cta}
                            <ChevronRight class="ml-1 h-4 w-4" />
                        </Button>
                    </Card.Footer>
                </Card.Root>
            {/each}
        </div>
    </div>
</section>

<!-- CTA Section -->
<section
    class="py-20 bg-gradient-to-r from-primary/5 via-purple-500/5 to-pink-500/5"
>
    <div class="max-w-3xl mx-auto px-6 text-center">
        <h2 class="text-3xl font-bold text-foreground sm:text-4xl">
            Ready to get started?
        </h2>
        <p class="mt-4 text-lg text-muted-foreground">
            Create your account in seconds. No credit card required.
        </p>
        <div class="mt-8">
            <Button size="lg" class="px-10 py-6 text-base" href="/register">
                Start Your Free Trial
                <ArrowRight class="ml-2 h-5 w-5" />
            </Button>
        </div>
    </div>
</section>

<!-- Footer -->
<footer class="border-t border-border bg-background py-12">
    <div class="max-w-7xl mx-auto px-6 lg:px-8">
        <div
            class="flex flex-col md:flex-row items-center justify-between gap-4"
        >
            <div class="flex items-center gap-2">
                <img src={logo} alt="SE" class="h-7 w-7 object-contain" />
                <span class="font-semibold text-foreground">Survey Engine</span>
            </div>
            <div class="flex items-center gap-6 text-sm text-muted-foreground">
                <a
                    href="/pricing"
                    class="hover:text-foreground transition-colors">Pricing</a
                >
                <a href="/login" class="hover:text-foreground transition-colors"
                    >Sign in</a
                >
                <a
                    href="/register"
                    class="hover:text-foreground transition-colors"
                    >Get Started</a
                >
            </div>
            <p class="text-sm text-muted-foreground">
                © 2026 Survey Engine. All rights reserved.
            </p>
        </div>
    </div>
</footer>
