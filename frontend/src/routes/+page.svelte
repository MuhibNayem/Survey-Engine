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
    import api from "$lib/api";
    import type { PlanDefinitionResponse } from "$lib/types";
    import { currencySymbol, formatAmount } from "$lib/utils/currency";
    import { Skeleton } from "$lib/components/ui/skeleton";
    import Autoplay from "embla-carousel-autoplay";
    import * as Carousel from "$lib/components/ui/carousel/index.js";
    import {
        defaultHomeSiteContent,
        normalizeHomeSiteContent,
        type HomeSiteContent,
    } from "$lib/site-content";

    let landingRoot: HTMLDivElement | null = null;
    let apiPlans = $state<PlanDefinitionResponse[]>([]);
    let loadingPlans = $state(true);
    let homeContent = $state<HomeSiteContent>(defaultHomeSiteContent());
    let carouselPlugin: any = null;

    onMount(() => {
        api.get<PlanDefinitionResponse[]>("/admin/plans")
            .then(({ data }) => {
                if (data?.length) apiPlans = data;
            })
            .catch(() => {})
            .finally(() => {
                loadingPlans = false;
            });

        api.get("/public/site-content/HOME")
            .then(({ data }) => {
                if (data?.published && data?.content) {
                    const parsed = normalizeHomeSiteContent(data.content);
                    const secs = parsed.carousel?.autoPlaySeconds;
                    if (secs && secs > 0) {
                        carouselPlugin = Autoplay({
                            delay: Math.max(secs * 1000, 1000),
                            stopOnInteraction: true,
                        });
                    }
                    homeContent = parsed;
                }
            })
            .catch(() => {});

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
                        (entry.target as HTMLElement).classList.add(
                            "ag-visible",
                        );
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
            card.style.setProperty(
                "--ag-card-delay",
                `${180 + (idx % 6) * 70}ms`,
            );
            const onMove = (event: PointerEvent) => {
                const rect = card.getBoundingClientRect();
                const x = (event.clientX - rect.left) / rect.width;
                const y = (event.clientY - rect.top) / rect.height;
                card.style.setProperty(
                    "--ag-rx",
                    `${((0.5 - y) * 9).toFixed(2)}deg`,
                );
                card.style.setProperty(
                    "--ag-ry",
                    `${((x - 0.5) * 11).toFixed(2)}deg`,
                );
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

        window.addEventListener("pointermove", onPointerMoveRoot, {
            passive: true,
        });
        cleanups.push(() => {
            window.removeEventListener("pointermove", onPointerMoveRoot);
            if (pointerRaf) window.cancelAnimationFrame(pointerRaf);
        });

        return () => {
            cleanups.forEach((fn) => fn());
        };
    });

    const iconMap = {
        FileText,
        Shield,
        Megaphone,
        BarChart3,
        Users,
        Zap,
        Globe,
    } as const;

    const features = $derived(
        homeContent.featureSection.items.map((feature) => ({
            ...feature,
            icon: iconMap[feature.icon as keyof typeof iconMap] ?? FileText,
        })),
    );

    interface PlanDisplay {
        code: string;
        name: string;
        description: string;
        price: string;
        priceSymbol?: string;
        priceAmount?: string;
        period: string;
        popular: boolean;
        cta: string;
        features: string[];
    }

    function buildPlans(apiData: PlanDefinitionResponse[]): PlanDisplay[] {
        const defaults: PlanDisplay[] = [
            {
                code: "BASIC",
                name: "Basic",
                price: "Free",
                priceSymbol: "",
                priceAmount: "Free",
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
                code: "PRO",
                name: "Pro",
                price: "$49",
                priceSymbol: "$",
                priceAmount: "49",
                period: "per month",
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
                code: "ENTERPRISE",
                name: "Enterprise",
                price: "Custom",
                priceSymbol: "",
                priceAmount: "Custom",
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

        if (!apiData.length) return defaults;

        return apiData.map((p) => {
            const fallback =
                defaults.find((d) => d.code === p.planCode) ?? defaults[0];

            const pSymbol = p.price === 0 ? "" : currencySymbol(p.currency);
            const pAmount =
                p.price === 0 ? "Free" : formatAmount(p.price, p.currency);
            const fmtPrice = (v: number, currency: string) =>
                v === 0
                    ? "Free"
                    : `${currencySymbol(currency)}${formatAmount(v, currency)}`;

            const formatLimit = (v: number | null) =>
                v == null ? "Unlimited" : v.toLocaleString();

            // Override the first 3 lines with actual limits
            const displayFeatures = [
                `${formatLimit(p.maxCampaigns)} Active Campaigns`,
                `${formatLimit(p.maxResponsesPerCampaign)} Responses / Campaign`,
                `${formatLimit(p.maxAdminUsers)} Admin Users`,
                ...fallback.features.slice(3),
            ];

            return {
                ...fallback,
                code: p.planCode,
                name: p.displayName || fallback.name,
                price: fmtPrice(p.price, p.currency),
                priceSymbol: pSymbol,
                priceAmount: pAmount,
                period:
                    p.price === 0 ? `${p.trialDays}-day trial` : "per month",
                features: displayFeatures,
            };
        });
    }

    const plans = $derived(buildPlans(apiPlans));
</script>

<svelte:head>
    <title>{homeContent.seo.title}</title>
    <meta name="description" content={homeContent.seo.description} />
</svelte:head>

<div bind:this={landingRoot} class="ag-root relative overflow-x-clip">
    <div class="ag-space" aria-hidden="true">
        <div class="ag-grid"></div>
        <div class="ag-blob ag-blob-a"></div>
        <div class="ag-blob ag-blob-b"></div>
        <div class="ag-blob ag-blob-c"></div>
        <div class="ag-spotlight"></div>
    </div>

    {#if homeContent.announcement.enabled && homeContent.announcement.text}
        <div
            class="relative z-20 border-b border-border/60 bg-background/90 backdrop-blur"
        >
            <div
                class="mx-auto flex max-w-7xl items-center justify-center gap-3 px-6 py-3 text-sm text-foreground"
            >
                <span>{homeContent.announcement.text}</span>
                {#if homeContent.announcement.linkLabel && homeContent.announcement.linkUrl}
                    <a
                        class="font-medium text-primary hover:underline"
                        href={homeContent.announcement.linkUrl}
                    >
                        {homeContent.announcement.linkLabel}
                    </a>
                {/if}
            </div>
        </div>
    {/if}

    <!-- ═══════════════════════════════════════════════════════════════════════
     HERO — Premium Enterprise Layout
════════════════════════════════════════════════════════════════════════ -->
    <div class="agent-hero relative overflow-hidden" data-ag-reveal>
        <!-- Ambient blobs -->
        <div class="hero-blob hero-blob-a" aria-hidden="true"></div>
        <div class="hero-blob hero-blob-b" aria-hidden="true"></div>
        <div class="hero-grid-lines" aria-hidden="true"></div>

        <!-- Nav -->
        <nav class="hero-nav">
            <a href="/" class="flex items-center gap-2.5 ag-brand">
                <img src={logo} alt="SE" class="h-9 w-9 object-contain" />
                <span class="text-xl font-bold text-foreground tracking-tight"
                    >Survey Engine</span
                >
            </a>
            <div class="flex items-center gap-2">
                <Button variant="ghost" size="sm" href="/docs/api">Docs</Button>
                <Button variant="ghost" size="sm" href="/pricing"
                    >Pricing</Button
                >
                {#if auth.isAuthenticated}
                    <Button
                        size="sm"
                        href={auth.user?.role === "SUPER_ADMIN"
                            ? "/admin/dashboard"
                            : "/dashboard"}
                        class="hero-cta-nav"
                    >
                        Dashboard <ArrowRight class="ml-1 h-3.5 w-3.5" />
                    </Button>
                {:else}
                    <Button variant="ghost" size="sm" href="/login"
                        >Sign in</Button
                    >
                    <Button size="sm" href="/register" class="hero-cta-nav">
                        Get Started <ArrowRight class="ml-1 h-3.5 w-3.5" />
                    </Button>
                {/if}
            </div>
        </nav>

        <!-- Two-column hero body -->
        <div class="hero-body">
            <!-- LEFT: copy -->
            <div class="hero-left">
                <!-- Eyebrow badge -->
                <div class="hero-badge-wrap">
                    <span class="hero-badge">
                        <span class="hero-badge-dot"></span>
                        {homeContent.hero.badge}
                    </span>
                </div>

                <!-- Headline -->
                <h1 class="hero-headline">
                    {homeContent.hero.title}
                    <span class="hero-highlight"
                        >{homeContent.hero.highlight}</span
                    >
                </h1>

                <!-- Description -->
                <p class="hero-description">{homeContent.hero.description}</p>

                <!-- CTA row -->
                <div class="hero-cta-row">
                    {#if auth.isAuthenticated}
                        <a
                            href={auth.user?.role === "SUPER_ADMIN"
                                ? "/admin/dashboard"
                                : "/dashboard"}
                            class="hero-btn-primary"
                        >
                            Go to Dashboard
                            <svg
                                class="hero-btn-icon"
                                xmlns="http://www.w3.org/2000/svg"
                                width="16"
                                height="16"
                                viewBox="0 0 24 24"
                                fill="none"
                                stroke="currentColor"
                                stroke-width="2.5"
                                stroke-linecap="round"
                                stroke-linejoin="round"
                                ><path d="M5 12h14" /><path
                                    d="m12 5 7 7-7 7"
                                /></svg
                            >
                        </a>
                    {:else}
                        <a
                            href={homeContent.hero.primaryCtaUrl || "/register"}
                            class="hero-btn-primary"
                        >
                            {homeContent.hero.primaryCtaLabel ||
                                "Start Free Trial"}
                            <svg
                                class="hero-btn-icon"
                                xmlns="http://www.w3.org/2000/svg"
                                width="16"
                                height="16"
                                viewBox="0 0 24 24"
                                fill="none"
                                stroke="currentColor"
                                stroke-width="2.5"
                                stroke-linecap="round"
                                stroke-linejoin="round"
                                ><path d="M5 12h14" /><path
                                    d="m12 5 7 7-7 7"
                                /></svg
                            >
                        </a>
                    {/if}
                    {#if homeContent.hero.secondaryCtaLabel}
                        <a
                            href={homeContent.hero.secondaryCtaUrl ||
                                "/pricing"}
                            class="hero-btn-secondary"
                        >
                            {homeContent.hero.secondaryCtaLabel}
                        </a>
                    {/if}
                </div>

                <!-- Footnote: social signals -->
                {#if homeContent.hero.footnote}
                    <p class="hero-footnote">
                        <CheckCircle2
                            class="h-3.5 w-3.5 text-emerald-500 shrink-0"
                        />
                        {homeContent.hero.footnote}
                    </p>
                {/if}
            </div>

            <!-- RIGHT: floating stat cards -->
            <div class="hero-right" aria-hidden="true">
                <div class="hero-card-grid">
                    <div class="hero-stat-card hero-stat-card-a ag-card">
                        <div class="hero-stat-icon hero-stat-icon-purple">
                            <BarChart3 class="h-5 w-5" />
                        </div>
                        <div class="hero-stat-val">99.9%</div>
                        <div class="hero-stat-label">Uptime SLA</div>
                    </div>
                    <div class="hero-stat-card hero-stat-card-b ag-card">
                        <div class="hero-stat-icon hero-stat-icon-blue">
                            <Shield class="h-5 w-5" />
                        </div>
                        <div class="hero-stat-val">SOC 2</div>
                        <div class="hero-stat-label">Compliant</div>
                    </div>
                    <div class="hero-stat-card hero-stat-card-c ag-card">
                        <div class="hero-stat-icon hero-stat-icon-emerald">
                            <Users class="h-5 w-5" />
                        </div>
                        <div class="hero-stat-val">Multi-Tenant</div>
                        <div class="hero-stat-label">Isolated</div>
                    </div>
                    <div class="hero-stat-card hero-stat-card-d ag-card">
                        <div class="hero-stat-icon hero-stat-icon-amber">
                            <Zap class="h-5 w-5" />
                        </div>
                        <div class="hero-stat-val">&lt; 200ms</div>
                        <div class="hero-stat-label">Response time</div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Logo strip -->
    <section class="logo-strip ag-layer" data-ag-reveal>
        <div class="max-w-6xl mx-auto px-6 lg:px-8">
            <p class="logo-strip-label">{homeContent.logoStrip.title}</p>
            <div class="logo-strip-logos">
                {#each homeContent.logoStrip.logos as logoItem}
                    <div class="logo-chip">{logoItem.label}</div>
                {/each}
            </div>
        </div>
    </section>

    <!-- ═══════════════════════════════════════════════════════════════════════
     CAROUSEL — only rendered when enabled by Super Admin
════════════════════════════════════════════════════════════════════════ -->
    {#if homeContent.carousel?.enabled && homeContent.carousel.slides?.length}
        <section class="carousel-section ag-layer" data-ag-reveal>
            {#snippet carouselBlock()}
                {@const slides = homeContent.carousel.slides}
                <div class="carousel-wrap">
                    <div class="carousel-header">
                        <h2 class="carousel-title">
                            {homeContent.carousel.title}
                        </h2>
                        {#if homeContent.carousel.description}
                            <p class="carousel-desc">
                                {homeContent.carousel.description}
                            </p>
                        {/if}
                    </div>

                    <!-- Slides -->
                    <Carousel.Root
                        plugins={carouselPlugin ? [carouselPlugin] : []}
                        class="w-full relative"
                        onmouseenter={() => carouselPlugin?.stop?.()}
                        onmouseleave={() => carouselPlugin?.reset?.()}
                    >
                        <Carousel.Content class="-ml-4">
                            {#each slides as slide, i}
                                <Carousel.Item class="pl-4">
                                    <div
                                        class="carousel-slide"
                                        style="--slide-accent:{slide.accentColor ||
                                            'hsl(263 70% 55%)'}"
                                    >
                                        <div class="slide-inner">
                                            {#if slide.tag}
                                                <span class="slide-tag"
                                                    >{slide.tag}</span
                                                >
                                            {/if}
                                            <h3 class="slide-headline">
                                                {slide.headline}
                                            </h3>
                                            {#if slide.subheadline}
                                                <p class="slide-sub">
                                                    {slide.subheadline}
                                                </p>
                                            {/if}
                                            {#if slide.description}
                                                <p class="slide-desc">
                                                    {slide.description}
                                                </p>
                                            {/if}
                                            <div class="slide-ctas">
                                                {#if slide.ctaLabel}
                                                    <a
                                                        href={slide.ctaUrl ||
                                                            "#"}
                                                        class="slide-cta-primary"
                                                        >{slide.ctaLabel}
                                                        <svg
                                                            class="hero-btn-icon"
                                                            xmlns="http://www.w3.org/2000/svg"
                                                            width="16"
                                                            height="16"
                                                            viewBox="0 0 24 24"
                                                            fill="none"
                                                            stroke="currentColor"
                                                            stroke-width="2.5"
                                                            stroke-linecap="round"
                                                            stroke-linejoin="round"
                                                            ><path
                                                                d="M5 12h14"
                                                            /><path
                                                                d="m12 5 7 7-7 7"
                                                            /></svg
                                                        >
                                                    </a>
                                                {/if}
                                                {#if slide.secondaryCtaLabel}
                                                    <a
                                                        href={slide.secondaryCtaUrl ||
                                                            "#"}
                                                        class="slide-cta-secondary"
                                                        >{slide.secondaryCtaLabel}</a
                                                    >
                                                {/if}
                                            </div>
                                        </div>
                                        <div class="slide-accent-stripe"></div>
                                    </div>
                                </Carousel.Item>
                            {/each}
                        </Carousel.Content>

                        <div
                            class="flex items-center justify-center gap-4 mt-8"
                        >
                            <Carousel.Previous class="static translate-y-0" />
                            <Carousel.Next class="static translate-y-0" />
                        </div>
                    </Carousel.Root>
                </div>
            {/snippet}
            {@render carouselBlock()}
        </section>
    {/if}

    <!-- ═══ TESTIMONIALS — conditionally shown ════════════════════════════ -->
    {#if homeContent.testimonials?.enabled && homeContent.testimonials.items?.length}
        <section class="testimonials-section ag-layer" data-ag-reveal>
            <div class="max-w-7xl mx-auto px-6 lg:px-8">
                <div class="testimonials-header">
                    <h2 class="testimonials-title">
                        {homeContent.testimonials.title}
                    </h2>
                    {#if homeContent.testimonials.description}
                        <p class="testimonials-desc">
                            {homeContent.testimonials.description}
                        </p>
                    {/if}
                </div>
                <div class="testimonials-grid">
                    {#each homeContent.testimonials.items as t}
                        <div class="testimonial-card ag-card">
                            <svg
                                class="t-quote-mark"
                                xmlns="http://www.w3.org/2000/svg"
                                width="28"
                                height="28"
                                viewBox="0 0 24 24"
                                fill="currentColor"
                                aria-hidden="true"
                                ><path
                                    d="M4.583 17.321C3.553 16.227 3 15 3 13.011c0-3.5 2.457-6.637 6.03-8.188l.893 1.378c-3.335 1.804-3.987 4.145-4.247 5.621.537-.278 1.24-.375 1.929-.311 1.804.167 3.226 1.648 3.226 3.489a3.5 3.5 0 0 1-3.5 3.5c-1.073 0-2.099-.49-2.748-1.179zm10 0C13.553 16.227 13 15 13 13.011c0-3.5 2.457-6.637 6.03-8.188l.893 1.378c-3.335 1.804-3.987 4.145-4.247 5.621.537-.278 1.24-.375 1.929-.311 1.804.167 3.226 1.648 3.226 3.489a3.5 3.5 0 0 1-3.5 3.5c-1.073 0-2.099-.49-2.748-1.179z"
                                /></svg
                            >
                            <blockquote class="t-quote">{t.quote}</blockquote>
                            <div class="t-author">
                                <div
                                    class="t-avatar"
                                    style="--av-hue:{t.avatarHue ?? 263}"
                                >
                                    {t.authorInitials ||
                                        t.authorName
                                            ?.slice(0, 2)
                                            ?.toUpperCase()}
                                </div>
                                <div class="t-meta">
                                    <span class="t-name">{t.authorName}</span>
                                    <span class="t-role"
                                        >{t.authorRole}{t.authorCompany
                                            ? ` · ${t.authorCompany}`
                                            : ""}</span
                                    >
                                </div>
                            </div>
                        </div>
                    {/each}
                </div>
            </div>
        </section>
    {/if}

    <!-- Features Grid -->
    <section class="py-24 bg-muted/30 ag-layer" data-ag-reveal>
        <div class="max-w-7xl mx-auto px-6 lg:px-8">
            <div class="text-center mb-16">
                <h2 class="text-3xl font-bold text-foreground sm:text-4xl">
                    {homeContent.featureSection.title}
                </h2>
                <p class="mt-4 text-lg text-muted-foreground max-w-2xl mx-auto">
                    {homeContent.featureSection.description}
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
                            <h3
                                class="text-lg font-semibold text-foreground mb-2"
                            >
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
                    {homeContent.pricingPreview.title}
                </h2>
                <p class="mt-4 text-lg text-muted-foreground">
                    {homeContent.pricingPreview.description}
                </p>
            </div>

            <div class="grid gap-8 lg:grid-cols-3 max-w-5xl mx-auto">
                {#each plans as plan}
                    <Card.Root
                        class="ag-card flex flex-col relative {plan.popular
                            ? 'border-primary shadow-xl shadow-primary/10 bg-card z-10 scale-[1.02]'
                            : 'border-border/50 bg-card/50'}"
                    >
                        {#if plan.popular}
                            <div
                                class="absolute -top-[14px] left-0 right-0 flex justify-center z-20"
                            >
                                <Badge
                                    class="bg-gradient-to-r from-primary to-purple-500 text-white shadow-md border-0 px-3 py-1 uppercase tracking-widest text-[10px]"
                                    >Most Popular</Badge
                                >
                            </div>
                        {/if}
                        <Card.Header class="text-center pb-2 pt-8">
                            <Card.Title class="text-xl">{plan.name}</Card.Title>
                            <Card.Description
                                >{plan.description}</Card.Description
                            >
                        </Card.Header>
                        <Card.Content class="text-center pb-8 flex-1">
                            <div
                                class="mt-2 mb-6 flex items-baseline justify-center gap-1 flex-wrap"
                            >
                                {#if plan.priceSymbol}
                                    <span
                                        class="text-2xl font-bold text-muted-foreground"
                                        >{plan.priceSymbol}</span
                                    >
                                    <span
                                        class="text-4xl font-extrabold tracking-tighter text-foreground leading-none"
                                        >{plan.priceAmount}</span
                                    >
                                {:else}
                                    <span
                                        class="text-4xl font-extrabold tracking-tighter text-foreground leading-none"
                                        >{plan.priceAmount || plan.price}</span
                                    >
                                {/if}
                                {#if plan.price !== "Free" && plan.price !== "Custom"}
                                    <span
                                        class="text-sm font-medium text-muted-foreground whitespace-nowrap"
                                        >/{plan.period.replace(
                                            "per ",
                                            "",
                                        )}</span
                                    >
                                {/if}
                            </div>
                            <ul class="space-y-3 text-left">
                                {#each plan.features as feat}
                                    <li class="flex items-start gap-2 text-sm">
                                        <CheckCircle2
                                            class="h-4 w-4 text-emerald-500 shrink-0 mt-0.5"
                                        />
                                        <span class="text-muted-foreground"
                                            >{feat}</span
                                        >
                                    </li>
                                {/each}
                            </ul>
                        </Card.Content>
                        <Card.Footer class="mt-auto pb-8">
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
                {homeContent.cta.title}
            </h2>
            <p class="mt-4 text-lg text-muted-foreground">
                {homeContent.cta.description}
            </p>
            <div class="mt-8">
                <Button
                    size="lg"
                    class="px-10 py-6 text-base"
                    href={homeContent.cta.primaryCtaUrl}
                >
                    {homeContent.cta.primaryCtaLabel}
                    <ArrowRight class="ml-2 h-5 w-5" />
                </Button>
            </div>
        </div>
    </section>

    <!-- Footer -->
    <footer
        class="border-t border-border bg-background py-12 ag-layer"
        data-ag-reveal
    >
        <div class="max-w-7xl mx-auto px-6 lg:px-8">
            <div
                class="flex flex-col md:flex-row items-center justify-between gap-4"
            >
                <div class="flex items-center gap-2">
                    <img src={logo} alt="SE" class="h-7 w-7 object-contain" />
                    <span class="font-semibold text-foreground"
                        >Survey Engine</span
                    >
                </div>
                <div
                    class="flex items-center gap-6 text-sm text-muted-foreground"
                >
                    <a
                        href="/pricing"
                        class="hover:text-foreground transition-colors"
                        >Pricing</a
                    >
                    <a
                        href="/login"
                        class="hover:text-foreground transition-colors"
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
        background-image: linear-gradient(
                to right,
                color-mix(in oklab, var(--foreground) 8%, transparent) 1px,
                transparent 1px
            ),
            linear-gradient(
                to bottom,
                color-mix(in oklab, var(--foreground) 8%, transparent) 1px,
                transparent 1px
            );
        background-size: 72px 72px;
        opacity: 0.2;
        mask-image: radial-gradient(
            85% 70% at 50% 10%,
            black 30%,
            transparent 100%
        );
        transform: translate3d(0, calc(var(--ag-scroll) * -0.08), 0);
        animation: agGridFloat 20s linear infinite;
    }

    .ag-spotlight {
        position: absolute;
        inset: -20%;
        background: radial-gradient(
                460px circle at var(--ag-px) var(--ag-py),
                color-mix(in oklab, var(--primary) 24%, transparent) 0%,
                transparent 55%
            ),
            radial-gradient(
                380px circle at calc(var(--ag-px) + 14%)
                    calc(var(--ag-py) + 18%),
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
        background: radial-gradient(
            circle at 40% 35%,
            #22d3ee 0%,
            transparent 68%
        );
        transform: translate3d(0, calc(var(--ag-scroll) * -0.18), 0);
        animation: agFloatA 17s ease-in-out infinite;
    }

    .ag-blob-b {
        width: min(42vw, 620px);
        height: min(42vw, 620px);
        right: -8%;
        top: 12%;
        background: radial-gradient(
            circle at 38% 35%,
            #3b82f6 0%,
            transparent 68%
        );
        transform: translate3d(0, calc(var(--ag-scroll) * -0.24), 0);
        animation: agFloatB 21s ease-in-out infinite;
    }

    .ag-blob-c {
        width: min(48vw, 740px);
        height: min(48vw, 740px);
        left: 28%;
        bottom: -24%;
        background: radial-gradient(
            circle at 45% 35%,
            #f97316 0%,
            transparent 68%
        );
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
        transform-style: preserve-3d;
        transform: perspective(1200px) rotateX(var(--ag-rx))
            rotateY(var(--ag-ry));
        transition:
            transform 0.3s cubic-bezier(0.22, 0.61, 0.36, 1),
            box-shadow 0.32s ease,
            border-color 0.32s ease,
            background-color 0.32s ease;
        animation: agCardIn 850ms cubic-bezier(0.16, 1, 0.3, 1) both;
        animation-delay: var(--ag-card-delay, 0ms);
        box-shadow: 0 26px 60px -46px color-mix(in oklab, var(--foreground)
                    36%, transparent);
    }

    :global(.ag-card)::after {
        content: "";
        position: absolute;
        inset: -1px;
        pointer-events: none;
        border-radius: inherit;
        background: radial-gradient(
            320px circle at var(--ag-mx) var(--ag-my),
            color-mix(in oklab, var(--primary) 25%, transparent) 0%,
            transparent 46%
        );
        opacity: 0;
        transition: opacity 0.24s ease;
    }

    :global(.ag-card:hover)::after {
        opacity: 1;
    }

    @keyframes agGridFloat {
        0%,
        100% {
            transform: translate3d(0, calc(var(--ag-scroll) * -0.08), 0);
        }
        50% {
            transform: translate3d(
                    -1.8%,
                    calc(var(--ag-scroll) * -0.08 - 6px),
                    0
                )
                scale(1.02);
        }
    }

    @keyframes agFloatA {
        0%,
        100% {
            transform: translate3d(0, calc(var(--ag-scroll) * -0.18), 0)
                scale(1);
        }
        50% {
            transform: translate3d(5%, calc(var(--ag-scroll) * -0.18 - 10px), 0)
                scale(1.08);
        }
    }

    @keyframes agFloatB {
        0%,
        100% {
            transform: translate3d(0, calc(var(--ag-scroll) * -0.24), 0)
                scale(1);
        }
        50% {
            transform: translate3d(-6%, calc(var(--ag-scroll) * -0.24 + 8px), 0)
                scale(1.1);
        }
    }

    @keyframes agFloatC {
        0%,
        100% {
            transform: translate3d(0, calc(var(--ag-scroll) * -0.14), 0)
                scale(1);
        }
        50% {
            transform: translate3d(3%, calc(var(--ag-scroll) * -0.14 - 10px), 0)
                scale(1.07);
        }
    }

    @keyframes agHaloPulse {
        0%,
        100% {
            opacity: 0.8;
        }
        50% {
            opacity: 1;
        }
    }

    @keyframes agSpotPulse {
        0%,
        100% {
            opacity: 0.42;
        }
        50% {
            opacity: 0.62;
        }
    }

    @keyframes agCardIn {
        from {
            opacity: 0;
            transform: perspective(1200px) translateY(18px) scale(0.985);
        }
        to {
            opacity: 1;
            transform: perspective(1200px) translateY(0) scale(1);
        }
    }

    @keyframes agHeadline {
        from {
            opacity: 0;
            transform: translate3d(0, 24px, 0) scale(0.98);
            filter: blur(8px);
        }
        to {
            opacity: 1;
            transform: translate3d(0, 0, 0) scale(1);
            filter: blur(0);
        }
    }

    @keyframes agBrand {
        from {
            opacity: 0;
            transform: translate3d(0, -10px, 0);
        }
        to {
            opacity: 1;
            transform: translate3d(0, 0, 0);
        }
    }

    @media (prefers-reduced-motion: reduce) {
        .ag-grid,
        .ag-blob,
        .ag-hero-halo,
        .ag-spotlight,
        .ag-headline,
        .ag-brand,
        :global(.ag-card),
        :global(.ag-reveal) {
            animation: none !important;
            transition: none !important;
            transform: none !important;
            filter: none !important;
            opacity: 1 !important;
        }
    }

    /* ─────────────────────────── PREMIUM HERO ──────────────────────────── */
    .agent-hero {
        background: var(--background);
        border-bottom: 1px solid color-mix(in oklab, var(--border) 40%, transparent);
        position: relative;
        min-height: 100vh;
        display: flex;
        flex-direction: column;
    }
    .hero-blob {
        position: absolute;
        border-radius: 50%;
        filter: blur(80px);
        pointer-events: none;
        z-index: 0;
    }
    .hero-blob-a {
        width: 720px;
        height: 720px;
        top: -200px;
        left: -180px;
        background: radial-gradient(
            circle,
            hsl(263 70% 55% / 0.18) 0%,
            transparent 70%
        );
        animation: agFloatA 18s ease-in-out infinite;
    }
    .hero-blob-b {
        width: 600px;
        height: 600px;
        bottom: -100px;
        right: -120px;
        background: radial-gradient(
            circle,
            hsl(220 80% 60% / 0.14) 0%,
            transparent 70%
        );
        animation: agFloatB 22s ease-in-out infinite;
    }
    .hero-grid-lines {
        position: absolute;
        inset: 0;
        z-index: 0;
        background-image: linear-gradient(
                color-mix(in oklab, var(--border) 8%, transparent) 1px,
                transparent 1px
            ),
            linear-gradient(
                90deg,
                color-mix(in oklab, var(--border) 8%, transparent) 1px,
                transparent 1px
            );
        background-size: 64px 64px;
        mask-image: radial-gradient(
            ellipse 80% 60% at 50% 0%,
            black 40%,
            transparent 100%
        );
    }
    .hero-nav {
        position: relative;
        z-index: 20;
        display: flex;
        align-items: center;
        justify-content: space-between;
        padding: 1.25rem 2rem;
        max-width: 76rem;
        margin: 0 auto;
        width: 100%;
    }
    .hero-cta-nav {
        background: var(--primary);
        color: white;
    }
    .hero-body {
        position: relative;
        z-index: 10;
        display: grid;
        grid-template-columns: 1fr 1fr;
        gap: 4rem;
        align-items: center;
        max-width: 76rem;
        margin: 0 auto;
        padding: 4rem 2rem 6rem;
        width: 100%;
        flex: 1;
    }
    @media (max-width: 900px) {
        .hero-body {
            grid-template-columns: 1fr;
        }
        .hero-right {
            display: none;
        }
    }

    /* Badge */
    .hero-badge-wrap {
        margin-bottom: 1.5rem;
        text-align: left;
        display: flex;
        justify-content: flex-start;
    }
    .hero-badge {
        display: inline-flex;
        align-items: center;
        gap: 0.5rem;
        padding: 0.375rem 0.875rem;
        background: color-mix(in oklab, var(--primary) 8%, transparent);
        border: 1px solid color-mix(in oklab, var(--primary) 20%, transparent);
        border-radius: 999px;
        font-size: 0.8125rem;
        font-weight: 600;
        color: var(--primary);
    }
    .hero-badge-dot {
        width: 0.5rem;
        height: 0.5rem;
        border-radius: 50%;
        background: var(--primary);
        box-shadow: 0 0 8px color-mix(in oklab, var(--primary) 60%, transparent);
        animation: pulse 2s ease-in-out infinite;
    }
    @keyframes pulse {
        0%,
        100% {
            box-shadow: 0 0 6px color-mix(in oklab, var(--primary) 40%, transparent);
        }
        50% {
            box-shadow: 0 0 14px color-mix(in oklab, var(--primary) 80%, transparent);
        }
    }

    /* Headline */
    .hero-headline {
        font-size: clamp(2.5rem, 5vw, 4rem);
        font-weight: 800;
        line-height: 1.1;
        letter-spacing: -0.025em;
        color: var(--foreground);
        margin: 0 0 1.25rem;
    }
    .hero-highlight {
        display: block;
        background: linear-gradient(
            135deg,
            hsl(263 70% 55%),
            hsl(220 80% 65%),
            hsl(300 70% 60%)
        );
        -webkit-background-clip: text;
        background-clip: text;
        -webkit-text-fill-color: transparent;
        text-fill-color: transparent;
    }
    .hero-description {
        font-size: 1.125rem;
        line-height: 1.65;
        color: var(--muted-foreground);
        max-width: 38rem;
        margin: 0 0 2rem;
    }

    /* CTA row */
    .hero-cta-row {
        display: flex;
        flex-wrap: wrap;
        gap: 0.75rem;
        margin-bottom: 1.25rem;
    }
    .hero-btn-primary {
        display: inline-flex;
        align-items: center;
        gap: 0.5rem;
        padding: 0.8125rem 1.75rem;
        background: linear-gradient(
            135deg,
            var(--primary),
            hsl(263 70% 50%)
        );
        color: white;
        font-size: 0.9375rem;
        font-weight: 600;
        border-radius: 0.625rem;
        box-shadow:
            0 4px 20px color-mix(in oklab, var(--primary) 35%, transparent),
            0 1px 0 hsl(0 0% 100% / 0.1) inset;
        transition: all 0.2s ease;
        text-decoration: none;
    }
    .hero-btn-primary:hover {
        transform: translateY(-1px);
        box-shadow: 0 8px 28px color-mix(in oklab, var(--primary) 45%, transparent);
    }
    .hero-btn-secondary {
        display: inline-flex;
        align-items: center;
        gap: 0.5rem;
        padding: 0.8125rem 1.75rem;
        background: var(--background);
        color: var(--foreground);
        font-size: 0.9375rem;
        font-weight: 600;
        border-radius: 0.625rem;
        border: 1px solid var(--border);
        transition: all 0.2s ease;
        text-decoration: none;
    }
    .hero-btn-secondary:hover {
        background: var(--accent);
        border-color: color-mix(in oklab, var(--border) 80%, transparent);
    }
    .hero-footnote {
        display: flex;
        align-items: center;
        gap: 0.375rem;
        font-size: 0.8125rem;
        color: var(--muted-foreground);
        margin: 0;
    }

    /* Right panel — stat cards */
    .hero-right {
        position: relative;
        height: 440px;
        display: flex;
        align-items: center;
        justify-content: center;
    }
    .hero-card-grid {
        position: relative;
        width: 440px;
        height: 440px;
        max-width: 100%;
    }
    .hero-stat-card {
        position: absolute;
        background: var(--card);
        border: 1px solid color-mix(in oklab, var(--border) 60%, transparent);
        border-radius: 0.875rem;
        padding: 1rem 1.25rem;
        display: flex;
        flex-direction: column;
        align-items: flex-start;
        gap: 0.375rem;
        backdrop-filter: blur(12px);
        box-shadow:
            0 8px 32px hsl(0 0% 0% / 0.06),
            0 1px 0 hsl(0 0% 100% / 0.05) inset;
        min-width: 9.5rem;
        animation: agCardIn 0.6s cubic-bezier(0.22, 1, 0.36, 1) both;
        animation-delay: var(--ag-card-delay, 0ms);
        z-index: 5;
    }
    /* Fixed absolute corners of a 440x440 grid */
    .hero-stat-card-a {
        top: 0;
        left: 0;
    }
    .hero-stat-card-b {
        top: 0;
        right: 0;
    }
    .hero-stat-card-c {
        bottom: 0;
        left: 0;
    }
    .hero-stat-card-d {
        bottom: 0;
        right: 0;
    }

    .hero-stat-icon {
        width: 2rem;
        height: 2rem;
        border-radius: 0.5rem;
        display: flex;
        align-items: center;
        justify-content: center;
    }
    .hero-stat-icon-purple {
        background: hsl(263 70% 55% / 0.12);
        color: hsl(263 70% 60%);
    }
    .hero-stat-icon-blue {
        background: hsl(220 80% 60% / 0.12);
        color: hsl(220 80% 60%);
    }
    .hero-stat-icon-emerald {
        background: hsl(142 71% 45% / 0.12);
        color: hsl(142 71% 45%);
    }
    .hero-stat-icon-amber {
        background: hsl(38 92% 50% / 0.12);
        color: hsl(38 92% 50%);
    }

    .hero-stat-val {
        font-size: 1.125rem;
        font-weight: 700;
        color: var(--foreground);
    }
    .hero-stat-label {
        font-size: 0.75rem;
        color: var(--muted-foreground);
    }

    /* Center ring */
    .hero-center-ring {
        position: absolute;
        top: 50%;
        left: 50%;
        transform: translate(-50%, -50%);
        width: 8.5rem;
        height: 8.5rem;
        border-radius: 50%;
        border: 1.5px solid color-mix(in oklab, var(--primary) 30%, transparent);
        box-shadow:
            0 0 0 12px color-mix(in oklab, var(--primary) 4%, transparent),
            0 0 0 24px color-mix(in oklab, var(--primary) 2%, transparent);
        background: var(--card);
        display: flex;
        align-items: center;
        justify-content: center;
        animation: agCardIn 0.7s cubic-bezier(0.22, 1, 0.36, 1) both;
    }
    .hero-center-inner {
        display: flex;
        flex-direction: column;
        align-items: center;
        justify-content: center;
        gap: 0.5rem;
        text-align: center;
    }
    .hero-center-icon {
        width: 2.5rem;
        height: 2.5rem;
        border-radius: 999px;
        display: flex;
        align-items: center;
        justify-content: center;
        background: color-mix(in oklab, var(--primary) 8%, transparent);
        border: 1px solid color-mix(in oklab, var(--primary) 16%, transparent);
        flex-shrink: 0;
    }
    .hero-center-label {
        display: flex;
        flex-direction: column;
        align-items: center;
        gap: 0.1rem;
        font-size: 0.75rem;
        font-weight: 700;
        line-height: 1.1;
        letter-spacing: 0.03em;
        color: var(--foreground);
    }

    /* Logo strip */
    .logo-strip {
        padding: 2.5rem 0;
        border-top: 1px solid color-mix(in oklab, var(--border) 40%, transparent);
        border-bottom: 1px solid color-mix(in oklab, var(--border) 40%, transparent);
    }
    .logo-strip-label {
        text-align: center;
        font-size: 0.6875rem;
        font-weight: 700;
        letter-spacing: 0.18em;
        text-transform: uppercase;
        color: color-mix(in oklab, var(--muted-foreground) 70%, transparent);
        margin-bottom: 1.25rem;
    }
    .logo-strip-logos {
        display: flex;
        flex-wrap: wrap;
        justify-content: center;
        gap: 0.625rem;
    }
    .logo-chip {
        border-radius: 999px;
        border: 1px solid color-mix(in oklab, var(--border) 60%, transparent);
        background: var(--background);
        padding: 0.375rem 1rem;
        font-size: 0.875rem;
        font-weight: 500;
        color: var(--muted-foreground);
    }

    /* ─────────────────────────── CAROUSEL ──────────────────────────────── */
    .carousel-section {
        padding: 4rem 0;
        background: color-mix(in oklab, var(--muted) 20%, transparent);
    }
    .carousel-wrap {
        max-width: 56rem;
        margin: 0 auto;
        padding: 0 2rem;
    }
    .carousel-header {
        text-align: center;
        margin-bottom: 2rem;
    }
    .carousel-title {
        font-size: 1.5rem;
        font-weight: 700;
        color: var(--foreground);
        margin: 0 0 0.5rem;
    }
    .carousel-desc {
        font-size: 0.9375rem;
        color: var(--muted-foreground);
        margin: 0;
    }

    .carousel-slide {
        position: relative;
        background: var(--card);
        border: 1px solid color-mix(in oklab, var(--border) 60%, transparent);
        border-radius: 1.25rem;
        overflow: hidden;
        box-shadow: 0 8px 40px hsl(0 0% 0% / 0.06);
    }

    .slide-inner {
        padding: 2.5rem 2rem 2rem;
        position: relative;
        z-index: 1;
    }

    .slide-accent-stripe {
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        height: 3px;
        background: var(--slide-accent, hsl(263 70% 55%));
        opacity: 0.9;
    }
    .slide-tag {
        display: inline-block;
        background: var(--slide-accent, hsl(263 70% 55%) / 0.1);
        border: 1px solid var(--border);
        color: var(--foreground);
        font-size: 0.75rem;
        font-weight: 700;
        letter-spacing: 0.06em;
        text-transform: uppercase;
        padding: 0.25rem 0.75rem;
        border-radius: 999px;
        margin-bottom: 1rem;
    }
    .slide-headline {
        font-size: 1.75rem;
        font-weight: 800;
        color: var(--foreground);
        margin: 0 0 0.5rem;
        line-height: 1.2;
    }
    .slide-sub {
        font-size: 1rem;
        font-weight: 600;
        color: var(--primary);
        margin: 0 0 0.875rem;
    }
    .slide-desc {
        font-size: 0.9375rem;
        color: var(--muted-foreground);
        margin: 0 0 1.5rem;
        line-height: 1.6;
        max-width: 42rem;
    }
    .slide-ctas {
        display: flex;
        flex-wrap: wrap;
        gap: 0.75rem;
    }
    .slide-cta-primary {
        display: inline-flex;
        align-items: center;
        gap: 0.5rem;
        padding: 0.625rem 1.375rem;
        background: var(--slide-accent, var(--primary));
        color: white;
        font-size: 0.875rem;
        font-weight: 600;
        border-radius: 0.5rem;
        text-decoration: none;
        transition: opacity 0.15s;
    }
    .slide-cta-primary:hover {
        opacity: 0.88;
    }
    .slide-cta-secondary {
        display: inline-flex;
        align-items: center;
        padding: 0.625rem 1.375rem;
        background: transparent;
        border: 1px solid var(--border);
        color: var(--foreground);
        font-size: 0.875rem;
        font-weight: 600;
        border-radius: 0.5rem;
        text-decoration: none;
        transition: background 0.15s;
    }
    .slide-cta-secondary:hover {
        background: var(--accent);
    }

    /* ── Hero button icon ─────────────────────────────────────────────────── */
    .hero-btn-icon {
        width: 1rem;
        height: 1rem;
        flex-shrink: 0;
        display: inline-block;
        vertical-align: middle;
    }

    /* ── Testimonials ─────────────────────────────────────────────────────── */
    .testimonials-section {
        padding: 5rem 0;
    }
    .testimonials-header {
        text-align: center;
        margin-bottom: 3rem;
    }
    .testimonials-title {
        font-size: 2rem;
        font-weight: 800;
        color: var(--foreground);
        margin: 0 0 0.75rem;
        letter-spacing: -0.02em;
    }
    .testimonials-desc {
        font-size: 1rem;
        color: var(--muted-foreground);
        max-width: 42rem;
        margin: 0 auto;
        line-height: 1.6;
    }

    .testimonials-grid {
        display: grid;
        grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
        gap: 1.5rem;
    }
    .testimonial-card {
        background: var(--card);
        border: 1px solid color-mix(in oklab, var(--border) 60%, transparent);
        border-radius: 1rem;
        padding: 1.75rem;
        display: flex;
        flex-direction: column;
        transition:
            box-shadow 0.2s,
            border-color 0.2s;
    }
    .testimonial-card:hover {
        box-shadow: 0 8px 32px color-mix(in oklab, var(--primary) 8%, transparent);
        border-color: color-mix(in oklab, var(--primary) 20%, transparent);
    }
    .t-quote-mark {
        color: color-mix(in oklab, var(--primary) 30%, transparent);
        margin-bottom: 0.75rem;
        flex-shrink: 0;
    }
    .t-quote {
        font-size: 0.9375rem;
        line-height: 1.65;
        color: color-mix(in oklab, var(--foreground) 85%, transparent);
        flex: 1;
        margin: 0 0 1.5rem;
        font-style: italic;
    }
    .t-author {
        display: flex;
        align-items: center;
        gap: 0.875rem;
    }
    .t-avatar {
        width: 2.5rem;
        height: 2.5rem;
        flex-shrink: 0;
        border-radius: 50%;
        background: hsl(var(--av-hue, 263) 55% 55% / 0.15);
        border: 1.5px solid hsl(var(--av-hue, 263) 55% 55% / 0.25);
        color: hsl(var(--av-hue, 263) 55% 45%);
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 0.75rem;
        font-weight: 800;
        letter-spacing: 0.03em;
    }
    .t-meta {
        display: flex;
        flex-direction: column;
        gap: 0.1rem;
        overflow: hidden;
    }
    .t-name {
        font-size: 0.875rem;
        font-weight: 700;
        color: var(--foreground);
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
    }
    .t-role {
        font-size: 0.75rem;
        color: var(--muted-foreground);
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
    }
</style>
