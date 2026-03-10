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
    let landingRoot: HTMLDivElement | null = null;

    onMount(() => {
        if (auth.isAuthenticated) {
            if (auth.user?.role === "SUPER_ADMIN") {
                goto("/admin/dashboard");
            } else {
                goto("/dashboard");
            }
            return;
        }

        if (!landingRoot) return;
        const prefersReducedMotion = window.matchMedia(
            "(prefers-reduced-motion: reduce)",
        ).matches;
        if (prefersReducedMotion) return;

        const cleanups: Array<() => void> = [];
        let rafId = 0;
        let pointerRaf = 0;

        const setScrollVars = () => {
            rafId = 0;
            const y = window.scrollY || window.pageYOffset || 0;
            landingRoot?.style.setProperty("--ag-scroll", `${y.toFixed(2)}px`);
        };

        const onScroll = () => {
            if (rafId) return;
            rafId = window.requestAnimationFrame(setScrollVars);
        };

        setScrollVars();
        window.addEventListener("scroll", onScroll, { passive: true });
        cleanups.push(() => {
            window.removeEventListener("scroll", onScroll);
            if (rafId) window.cancelAnimationFrame(rafId);
        });

        const revealEls = Array.from(
            landingRoot.querySelectorAll<HTMLElement>("[data-ag-reveal]"),
        );
        revealEls.forEach((el, i) => {
            el.classList.add("ag-reveal");
            el.style.setProperty("--ag-delay", `${Math.min(i * 90, 720)}ms`);
        });

        const io = new IntersectionObserver(
            (entries) => {
                for (const entry of entries) {
                    if (entry.isIntersecting) {
                        (entry.target as HTMLElement).classList.add("ag-visible");
                    }
                }
            },
            { threshold: 0.14, rootMargin: "0px 0px -8% 0px" },
        );

        revealEls.forEach((el) => io.observe(el));
        cleanups.push(() => io.disconnect());

        const cards = Array.from(
            landingRoot.querySelectorAll<HTMLElement>(".ag-card"),
        );

        cards.forEach((card, idx) => {
            card.style.setProperty("--ag-card-delay", `${180 + (idx % 6) * 70}ms`);
            const onMove = (event: PointerEvent) => {
                const rect = card.getBoundingClientRect();
                const x = (event.clientX - rect.left) / rect.width;
                const y = (event.clientY - rect.top) / rect.height;
                card.style.setProperty("--ag-rx", `${((0.5 - y) * 9).toFixed(2)}deg`);
                card.style.setProperty("--ag-ry", `${((x - 0.5) * 11).toFixed(2)}deg`);
                card.style.setProperty("--ag-mx", `${(x * 100).toFixed(2)}%`);
                card.style.setProperty("--ag-my", `${(y * 100).toFixed(2)}%`);
            };

            const onLeave = () => {
                card.style.setProperty("--ag-rx", "0deg");
                card.style.setProperty("--ag-ry", "0deg");
            };

            card.addEventListener("pointermove", onMove);
            card.addEventListener("pointerleave", onLeave);
            cleanups.push(() => {
                card.removeEventListener("pointermove", onMove);
                card.removeEventListener("pointerleave", onLeave);
            });
        });

        const onPointerMoveRoot = (event: PointerEvent) => {
            if (pointerRaf) return;
            pointerRaf = window.requestAnimationFrame(() => {
                pointerRaf = 0;
                const x = (event.clientX / window.innerWidth) * 100;
                const y = (event.clientY / window.innerHeight) * 100;
                landingRoot?.style.setProperty("--ag-px", `${x.toFixed(2)}%`);
                landingRoot?.style.setProperty("--ag-py", `${y.toFixed(2)}%`);
            });
        };

        window.addEventListener("pointermove", onPointerMoveRoot, { passive: true });
        cleanups.push(() => {
            window.removeEventListener("pointermove", onPointerMoveRoot);
            if (pointerRaf) window.cancelAnimationFrame(pointerRaf);
        });

        return () => {
            cleanups.forEach((fn) => fn());
        };
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

<div bind:this={landingRoot} class="ag-root relative overflow-x-clip">
    <div class="ag-space" aria-hidden="true">
        <div class="ag-grid"></div>
        <div class="ag-blob ag-blob-a"></div>
        <div class="ag-blob ag-blob-b"></div>
        <div class="ag-blob ag-blob-c"></div>
        <div class="ag-spotlight"></div>
    </div>

<!-- Hero Section -->
<div class="relative overflow-hidden ag-layer" data-ag-reveal>
    <!-- Background gradient -->
    <div
        class="ag-hero-bg absolute inset-0 bg-gradient-to-br from-primary/5 via-background to-purple-500/5"
    ></div>
    <div
        class="ag-hero-halo absolute top-0 left-1/2 -translate-x-1/2 w-[800px] h-[600px] bg-gradient-radial from-primary/10 to-transparent rounded-full blur-3xl"
    ></div>

    <!-- Nav -->
    <nav
        class="relative z-10 flex items-center justify-between px-6 py-4 max-w-7xl mx-auto lg:px-8"
    >
        <a href="/" class="flex items-center gap-2 ag-brand">
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
            class="ag-headline text-5xl font-extrabold tracking-tight text-foreground sm:text-6xl lg:text-7xl"
        >
            Surveys that
            <span
                class="text-transparent bg-clip-text bg-gradient-to-r from-primary via-purple-500 to-pink-500"
            >
                scale
            </span>
        </h1>

        <p class="mt-6 max-w-2xl mx-auto text-lg text-muted-foreground leading-relaxed">
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
<section class="py-24 bg-muted/30 ag-layer" data-ag-reveal>
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
                    class="ag-card border-border/50 transition-all duration-300 hover:shadow-lg hover:border-primary/20 hover:-translate-y-1"
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
<section class="py-24 ag-layer" id="pricing" data-ag-reveal>
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
                    class="ag-card relative {plan.popular
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
    class="py-20 bg-gradient-to-r from-primary/5 via-purple-500/5 to-pink-500/5 ag-layer"
    data-ag-reveal
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
<footer class="border-t border-border bg-background py-12 ag-layer" data-ag-reveal>
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
</div>

<style>
    .ag-root {
        --ag-scroll: 0px;
        --ag-px: 50%;
        --ag-py: 18%;
    }

    .ag-space {
        position: fixed;
        inset: 0;
        pointer-events: none;
        z-index: 0;
        overflow: hidden;
    }

    .ag-layer {
        position: relative;
        z-index: 1;
    }

    .ag-grid {
        position: absolute;
        inset: -12% -8%;
        background-image:
            linear-gradient(to right, color-mix(in oklab, hsl(var(--foreground)) 8%, transparent) 1px, transparent 1px),
            linear-gradient(to bottom, color-mix(in oklab, hsl(var(--foreground)) 8%, transparent) 1px, transparent 1px);
        background-size: 72px 72px;
        opacity: 0.2;
        mask-image: radial-gradient(85% 70% at 50% 10%, black 30%, transparent 100%);
        transform: translate3d(0, calc(var(--ag-scroll) * -0.08), 0);
        animation: agGridFloat 20s linear infinite;
    }

    .ag-spotlight {
        position: absolute;
        inset: -20%;
        background:
            radial-gradient(
                460px circle at var(--ag-px) var(--ag-py),
                color-mix(in oklab, hsl(var(--primary)) 24%, transparent) 0%,
                transparent 55%
            ),
            radial-gradient(
                380px circle at calc(var(--ag-px) + 14%) calc(var(--ag-py) + 18%),
                color-mix(in oklab, #22d3ee 20%, transparent) 0%,
                transparent 60%
            );
        opacity: 0.55;
        transition: opacity 0.3s ease;
        animation: agSpotPulse 9s ease-in-out infinite;
    }

    .ag-blob {
        position: absolute;
        border-radius: 999px;
        filter: blur(58px);
        opacity: 0.24;
        mix-blend-mode: screen;
    }

    .ag-blob-a {
        width: min(45vw, 680px);
        height: min(45vw, 680px);
        left: -12%;
        top: -6%;
        background: radial-gradient(circle at 40% 35%, #22d3ee 0%, transparent 68%);
        transform: translate3d(0, calc(var(--ag-scroll) * -0.18), 0);
        animation: agFloatA 17s ease-in-out infinite;
    }

    .ag-blob-b {
        width: min(42vw, 620px);
        height: min(42vw, 620px);
        right: -8%;
        top: 12%;
        background: radial-gradient(circle at 38% 35%, #3b82f6 0%, transparent 68%);
        transform: translate3d(0, calc(var(--ag-scroll) * -0.24), 0);
        animation: agFloatB 21s ease-in-out infinite;
    }

    .ag-blob-c {
        width: min(48vw, 740px);
        height: min(48vw, 740px);
        left: 28%;
        bottom: -24%;
        background: radial-gradient(circle at 45% 35%, #f97316 0%, transparent 68%);
        transform: translate3d(0, calc(var(--ag-scroll) * -0.14), 0);
        animation: agFloatC 24s ease-in-out infinite;
    }

    .ag-hero-bg {
        transform: translate3d(0, calc(var(--ag-scroll) * -0.12), 0);
    }

    .ag-hero-halo {
        transform: translate3d(-50%, calc(var(--ag-scroll) * -0.2), 0);
        animation: agHaloPulse 10s ease-in-out infinite;
    }

    .ag-headline {
        animation: agHeadline 900ms cubic-bezier(0.16, 1, 0.3, 1) both;
    }

    .ag-brand {
        animation: agBrand 800ms cubic-bezier(0.22, 1, 0.36, 1) both;
    }

    :global(.ag-reveal) {
        opacity: 0;
        transform: translateY(42px) scale(0.985);
        filter: blur(8px);
        transition:
            opacity 0.95s cubic-bezier(0.16, 1, 0.3, 1) var(--ag-delay, 0ms),
            transform 1s cubic-bezier(0.16, 1, 0.3, 1) var(--ag-delay, 0ms),
            filter 0.9s cubic-bezier(0.16, 1, 0.3, 1) var(--ag-delay, 0ms);
    }

    :global(.ag-reveal.ag-visible) {
        opacity: 1;
        transform: translateY(0) scale(1);
        filter: blur(0);
    }

    :global(.ag-card) {
        --ag-rx: 0deg;
        --ag-ry: 0deg;
        --ag-mx: 50%;
        --ag-my: 50%;
        position: relative;
        overflow: hidden;
        transform-style: preserve-3d;
        transform: perspective(1200px) rotateX(var(--ag-rx)) rotateY(var(--ag-ry));
        transition:
            transform 0.3s cubic-bezier(0.22, 0.61, 0.36, 1),
            box-shadow 0.32s ease,
            border-color 0.32s ease,
            background-color 0.32s ease;
        animation: agCardIn 850ms cubic-bezier(0.16, 1, 0.3, 1) both;
        animation-delay: var(--ag-card-delay, 0ms);
        box-shadow: 0 26px 60px -46px color-mix(in oklab, hsl(var(--foreground)) 36%, transparent);
    }

    :global(.ag-card)::after {
        content: "";
        position: absolute;
        inset: -1px;
        pointer-events: none;
        border-radius: inherit;
        background: radial-gradient(320px circle at var(--ag-mx) var(--ag-my), color-mix(in oklab, hsl(var(--primary)) 25%, transparent) 0%, transparent 46%);
        opacity: 0;
        transition: opacity 0.24s ease;
    }

    :global(.ag-card:hover)::after {
        opacity: 1;
    }

    @keyframes agGridFloat {
        0%, 100% { transform: translate3d(0, calc(var(--ag-scroll) * -0.08), 0); }
        50% { transform: translate3d(-1.8%, calc(var(--ag-scroll) * -0.08 - 6px), 0) scale(1.02); }
    }

    @keyframes agFloatA {
        0%, 100% { transform: translate3d(0, calc(var(--ag-scroll) * -0.18), 0) scale(1); }
        50% { transform: translate3d(5%, calc(var(--ag-scroll) * -0.18 - 10px), 0) scale(1.08); }
    }

    @keyframes agFloatB {
        0%, 100% { transform: translate3d(0, calc(var(--ag-scroll) * -0.24), 0) scale(1); }
        50% { transform: translate3d(-6%, calc(var(--ag-scroll) * -0.24 + 8px), 0) scale(1.1); }
    }

    @keyframes agFloatC {
        0%, 100% { transform: translate3d(0, calc(var(--ag-scroll) * -0.14), 0) scale(1); }
        50% { transform: translate3d(3%, calc(var(--ag-scroll) * -0.14 - 10px), 0) scale(1.07); }
    }

    @keyframes agHaloPulse {
        0%, 100% { opacity: 0.8; }
        50% { opacity: 1; }
    }

    @keyframes agSpotPulse {
        0%, 100% { opacity: 0.42; }
        50% { opacity: 0.62; }
    }

    @keyframes agCardIn {
        from { opacity: 0; transform: perspective(1200px) translateY(18px) scale(0.985); }
        to { opacity: 1; transform: perspective(1200px) translateY(0) scale(1); }
    }

    @keyframes agHeadline {
        from { opacity: 0; transform: translate3d(0, 24px, 0) scale(0.98); filter: blur(8px); }
        to { opacity: 1; transform: translate3d(0, 0, 0) scale(1); filter: blur(0); }
    }

    @keyframes agBrand {
        from { opacity: 0; transform: translate3d(0, -10px, 0); }
        to { opacity: 1; transform: translate3d(0, 0, 0); }
    }

    @media (prefers-reduced-motion: reduce) {
        .ag-grid, .ag-blob, .ag-hero-halo, .ag-spotlight, .ag-headline, .ag-brand, :global(.ag-card), :global(.ag-reveal) {
            animation: none !important;
            transition: none !important;
            transform: none !important;
            filter: none !important;
            opacity: 1 !important;
        }
    }
</style>
