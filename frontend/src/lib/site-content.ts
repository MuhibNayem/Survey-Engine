export type SiteContentPageKey = "HOME" | "PRICING";

export interface CarouselSlide {
    /** Short eyebrow/tag above the headline */
    tag: string;
    headline: string;
    subheadline: string;
    description: string;
    ctaLabel: string;
    ctaUrl: string;
    /** Optional secondary CTA */
    secondaryCtaLabel: string;
    secondaryCtaUrl: string;
    /** Accent color for the slide highlight (HSL CSS string or Tailwind token) */
    accentColor: string;
}

export interface SiteCarousel {
    enabled: boolean;
    /** Section heading shown above the carousel */
    title: string;
    description: string;
    autoPlaySeconds: number;
    slides: CarouselSlide[];
}

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

export interface TestimonialItem {
    quote: string;
    authorName: string;
    authorRole: string;
    authorCompany: string;
    /** Initials to show in the avatar circle when no image is provided */
    authorInitials: string;
    /** Accent hue for the avatar background (HSL hue number 0-360) */
    avatarHue: number;
}

export interface SiteTestimonials {
    enabled: boolean;
    title: string;
    description: string;
    items: TestimonialItem[];
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
    carousel: SiteCarousel;
    logoStrip: {
        title: string;
        logos: LogoItem[];
    };
    featureSection: {
        title: string;
        description: string;
        items: FeatureItem[];
    };
    testimonials: SiteTestimonials;
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

const TESTIMONIALS_DEFAULT: SiteTestimonials = {
    enabled: true,
    title: "Trusted by teams that care about quality feedback",
    description: "See how organizations use Survey Engine to capture high-accountability insights at scale.",
    items: [
        {
            quote: "Survey Engine transformed how we collect employee feedback. The weighted scoring and private auth flows let us run sensitive engagement surveys with full confidence.",
            authorName: "Sarah Chen",
            authorRole: "Head of People Operations",
            authorCompany: "Nexus Labs",
            authorInitials: "SC",
            avatarHue: 263,
        },
        {
            quote: "The multi-tenant isolation means each of our business units sees only their own data. We went from spreadsheets to enterprise-grade analytics in under a week.",
            authorName: "Marcus Okonkwo",
            authorRole: "VP of Engineering",
            authorCompany: "Meridian Health",
            authorInitials: "MO",
            avatarHue: 220,
        },
        {
            quote: "The OIDC/PKCE campaign auth is a game changer — our respondents authenticate through their company SSO before accessing any survey. Zero friction, full security.",
            authorName: "Priya Nair",
            authorRole: "Director of Compliance",
            authorCompany: "Finvault Group",
            authorInitials: "PN",
            avatarHue: 142,
        },
    ],
};

const CAROUSEL_DEFAULT: SiteCarousel = {
    enabled: false,
    title: "Upcoming Events & Announcements",
    description: "Stay up to date with the latest from Survey Engine.",
    autoPlaySeconds: 5,
    slides: [
        {
            tag: "Webinar",
            headline: "Enterprise Survey Best Practices",
            subheadline: "Join our live session with industry experts",
            description: "Learn how leading organizations use Survey Engine to collect high-accountability feedback at scale.",
            ctaLabel: "Register Now",
            ctaUrl: "/register",
            secondaryCtaLabel: "Learn More",
            secondaryCtaUrl: "/docs",
            accentColor: "hsl(263 70% 55%)",
        },
    ],
};

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
    carousel: CAROUSEL_DEFAULT,
    testimonials: TESTIMONIALS_DEFAULT,
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
    const r = raw as Partial<HomeSiteContent> | undefined;
    return {
        ...defaultHomeSiteContent(),
        ...(r || {}),
        seo: { ...HOME_DEFAULT.seo, ...(r?.seo || {}) },
        announcement: { ...HOME_DEFAULT.announcement, ...(r?.announcement || {}) },
        hero: { ...HOME_DEFAULT.hero, ...(r?.hero || {}) },
        carousel: {
            ...CAROUSEL_DEFAULT,
            ...(r?.carousel || {}),
            slides: Array.isArray(r?.carousel?.slides) ? r!.carousel!.slides! : copy(CAROUSEL_DEFAULT.slides),
        },
        logoStrip: {
            ...HOME_DEFAULT.logoStrip,
            ...(r?.logoStrip || {}),
            logos: Array.isArray(r?.logoStrip?.logos) ? r!.logoStrip!.logos! : copy(HOME_DEFAULT.logoStrip.logos),
        },
        featureSection: {
            ...HOME_DEFAULT.featureSection,
            ...(r?.featureSection || {}),
            items: Array.isArray(r?.featureSection?.items) ? r!.featureSection!.items! : copy(HOME_DEFAULT.featureSection.items),
        },
        testimonials: {
            ...TESTIMONIALS_DEFAULT,
            ...(r?.testimonials || {}),
            items: Array.isArray(r?.testimonials?.items) ? r!.testimonials!.items! : copy(TESTIMONIALS_DEFAULT.items),
        },
        pricingPreview: { ...HOME_DEFAULT.pricingPreview, ...(r?.pricingPreview || {}) },
        cta: { ...HOME_DEFAULT.cta, ...(r?.cta || {}) },
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
