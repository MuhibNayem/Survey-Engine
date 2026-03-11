export type SiteContentPageKey = "HOME" | "PRICING";

export interface SiteContentSeo {
    title: string;
    description: string;
}

export interface SiteAnnouncement {
    enabled: boolean;
    text: string;
    linkLabel: string;
    linkUrl: string;
}

export interface SiteHero {
    badge: string;
    title: string;
    highlight: string;
    description: string;
    primaryCtaLabel: string;
    primaryCtaUrl: string;
    secondaryCtaLabel: string;
    secondaryCtaUrl: string;
    tertiaryCtaLabel: string;
    tertiaryCtaUrl: string;
    footnote: string;
}

export interface LogoItem {
    label: string;
}

export interface FeatureItem {
    icon: string;
    title: string;
    description: string;
    color: string;
    bg: string;
}

export interface SiteCta {
    title: string;
    description: string;
    primaryCtaLabel: string;
    primaryCtaUrl: string;
    secondaryCtaLabel: string;
    secondaryCtaUrl: string;
    footnote: string;
}

export interface HomeSiteContent {
    seo: SiteContentSeo;
    announcement: SiteAnnouncement;
    hero: SiteHero;
    logoStrip: {
        title: string;
        logos: LogoItem[];
    };
    featureSection: {
        title: string;
        description: string;
        items: FeatureItem[];
    };
    pricingPreview: {
        title: string;
        description: string;
    };
    cta: SiteCta;
}

export interface PricingFaqItem {
    question: string;
    answer: string;
}

export interface PricingSiteContent {
    seo: SiteContentSeo;
    announcement: SiteAnnouncement;
    hero: {
        badge: string;
        title: string;
        highlight: string;
        description: string;
    };
    faq: {
        title: string;
        description: string;
        items: PricingFaqItem[];
    };
    cta: SiteCta;
}

function copy<T>(value: T): T {
    return JSON.parse(JSON.stringify(value));
}

const HOME_DEFAULT: HomeSiteContent = {
    seo: {
        title: "Survey Engine - Enterprise Survey Platform",
        description:
            "Build, deploy, and analyze surveys at scale with enterprise authentication, weighted scoring, and multi-tenant SaaS controls.",
    },
    announcement: {
        enabled: false,
        text: "Now with OIDC/PKCE authentication.",
        linkLabel: "Learn more",
        linkUrl: "/docs/api",
    },
    hero: {
        badge: "Now with OIDC/PKCE Authentication",
        title: "Surveys that",
        highlight: "scale",
        description:
            "Build, deploy, and analyze surveys with enterprise-grade authentication, weighted scoring, and multi-tenant isolation - all from a single platform.",
        primaryCtaLabel: "Start Free Trial",
        primaryCtaUrl: "/register",
        secondaryCtaLabel: "View Pricing",
        secondaryCtaUrl: "/pricing",
        tertiaryCtaLabel: "API Documentation",
        tertiaryCtaUrl: "/docs/api",
        footnote: "No credit card required - 14-day free trial - Cancel anytime",
    },
    logoStrip: {
        title: "Trusted by teams building high-accountability feedback programs",
        logos: [
            { label: "Global HR" },
            { label: "People Ops Co." },
            { label: "Insight Labs" },
            { label: "GovTech Unit" },
            { label: "EduScale" },
        ],
    },
    featureSection: {
        title: "Everything you need to run surveys at scale",
        description:
            "From question banks to weighted scoring - every feature is built for multi-tenant enterprise use.",
        items: [
            {
                icon: "FileText",
                title: "Smart Survey Builder",
                description:
                    "Create multi-page surveys with versioned questions, categories, and skip logic.",
                color: "text-blue-500",
                bg: "bg-blue-500/10",
            },
            {
                icon: "Shield",
                title: "Enterprise Authentication",
                description:
                    "SSO, OIDC/PKCE, signed tokens - support any identity provider out of the box.",
                color: "text-emerald-500",
                bg: "bg-emerald-500/10",
            },
            {
                icon: "Megaphone",
                title: "Campaign Management",
                description:
                    "Deploy surveys with quotas, IP restrictions, device dedup, and multi-channel distribution.",
                color: "text-orange-500",
                bg: "bg-orange-500/10",
            },
            {
                icon: "BarChart3",
                title: "Weighted Scoring Engine",
                description:
                    "Multi-dimensional scoring with category weights, normalization, and real-time analytics.",
                color: "text-purple-500",
                bg: "bg-purple-500/10",
            },
            {
                icon: "Users",
                title: "Multi-Tenant SaaS",
                description:
                    "Complete tenant isolation, subscription plans, and quota enforcement built in.",
                color: "text-pink-500",
                bg: "bg-pink-500/10",
            },
            {
                icon: "Zap",
                title: "Auto-Locking & Audit",
                description:
                    "Responses auto-lock on submit with audit coverage across the lifecycle.",
                color: "text-amber-500",
                bg: "bg-amber-500/10",
            },
        ],
    },
    pricingPreview: {
        title: "Simple, transparent pricing",
        description: "Start free, scale as you grow.",
    },
    cta: {
        title: "Ready to get started?",
        description: "Create your account in seconds. No credit card required.",
        primaryCtaLabel: "Start Your Free Trial",
        primaryCtaUrl: "/register",
        secondaryCtaLabel: "",
        secondaryCtaUrl: "",
        footnote: "",
    },
};

const PRICING_DEFAULT: PricingSiteContent = {
    seo: {
        title: "Pricing - Survey Engine",
        description: "Simple, transparent pricing. Start free and scale with your team.",
    },
    announcement: {
        enabled: false,
        text: "Annual contracts and enterprise rollouts available.",
        linkLabel: "Contact sales",
        linkUrl: "/contact",
    },
    hero: {
        badge: "14-day free trial - No credit card required",
        title: "Plans tailored for",
        highlight: "every team",
        description:
            "Start free, securely gather insights, and scale with a plan designed to grow with your organization.",
    },
    faq: {
        title: "Frequently asked questions",
        description: "Answers to the common questions buyers ask before rollout.",
        items: [
            {
                question: "Can we start on a lower plan and upgrade later?",
                answer: "Yes. Tenants can start small and move to higher tiers as usage and admin needs grow.",
            },
            {
                question: "Do enterprise plans support custom rollout help?",
                answer: "Yes. Enterprise packaging is designed for larger deployments, change management, and dedicated support needs.",
            },
            {
                question: "Is branding available on every plan?",
                answer: "Custom branding and other advanced controls depend on the plan definition configured by the platform.",
            },
        ],
    },
    cta: {
        title: "Ready to transform your data collection?",
        description:
            "Join organizations building high-conversion surveys with Survey Engine.",
        primaryCtaLabel: "Start Your Free Trial",
        primaryCtaUrl: "/register",
        secondaryCtaLabel: "Contact Sales",
        secondaryCtaUrl: "/contact",
        footnote: "All plans include SSL encryption, daily backups, and a 99.9% uptime SLA.",
    },
};

export function defaultHomeSiteContent(): HomeSiteContent {
    return copy(HOME_DEFAULT);
}

export function defaultPricingSiteContent(): PricingSiteContent {
    return copy(PRICING_DEFAULT);
}

export function normalizeHomeSiteContent(raw: unknown): HomeSiteContent {
    return {
        ...defaultHomeSiteContent(),
        ...(raw as Partial<HomeSiteContent> || {}),
        announcement: {
            ...HOME_DEFAULT.announcement,
            ...((raw as Partial<HomeSiteContent> | undefined)?.announcement || {}),
        },
        hero: {
            ...HOME_DEFAULT.hero,
            ...((raw as Partial<HomeSiteContent> | undefined)?.hero || {}),
        },
        logoStrip: {
            ...HOME_DEFAULT.logoStrip,
            ...((raw as Partial<HomeSiteContent> | undefined)?.logoStrip || {}),
            logos: Array.isArray((raw as Partial<HomeSiteContent> | undefined)?.logoStrip?.logos)
                ? (raw as Partial<HomeSiteContent>).logoStrip!.logos!
                : copy(HOME_DEFAULT.logoStrip.logos),
        },
        featureSection: {
            ...HOME_DEFAULT.featureSection,
            ...((raw as Partial<HomeSiteContent> | undefined)?.featureSection || {}),
            items: Array.isArray((raw as Partial<HomeSiteContent> | undefined)?.featureSection?.items)
                ? (raw as Partial<HomeSiteContent>).featureSection!.items!
                : copy(HOME_DEFAULT.featureSection.items),
        },
        pricingPreview: {
            ...HOME_DEFAULT.pricingPreview,
            ...((raw as Partial<HomeSiteContent> | undefined)?.pricingPreview || {}),
        },
        cta: {
            ...HOME_DEFAULT.cta,
            ...((raw as Partial<HomeSiteContent> | undefined)?.cta || {}),
        },
        seo: {
            ...HOME_DEFAULT.seo,
            ...((raw as Partial<HomeSiteContent> | undefined)?.seo || {}),
        },
    };
}

export function normalizePricingSiteContent(raw: unknown): PricingSiteContent {
    return {
        ...defaultPricingSiteContent(),
        ...(raw as Partial<PricingSiteContent> || {}),
        announcement: {
            ...PRICING_DEFAULT.announcement,
            ...((raw as Partial<PricingSiteContent> | undefined)?.announcement || {}),
        },
        hero: {
            ...PRICING_DEFAULT.hero,
            ...((raw as Partial<PricingSiteContent> | undefined)?.hero || {}),
        },
        faq: {
            ...PRICING_DEFAULT.faq,
            ...((raw as Partial<PricingSiteContent> | undefined)?.faq || {}),
            items: Array.isArray((raw as Partial<PricingSiteContent> | undefined)?.faq?.items)
                ? (raw as Partial<PricingSiteContent>).faq!.items!
                : copy(PRICING_DEFAULT.faq.items),
        },
        cta: {
            ...PRICING_DEFAULT.cta,
            ...((raw as Partial<PricingSiteContent> | undefined)?.cta || {}),
        },
        seo: {
            ...PRICING_DEFAULT.seo,
            ...((raw as Partial<PricingSiteContent> | undefined)?.seo || {}),
        },
    };
}
