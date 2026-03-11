<script lang="ts">
    import api from "$lib/api";
    import { page } from "$app/state";
    import { onMount } from "svelte";
    import * as Card from "$lib/components/ui/card";
    import { Button } from "$lib/components/ui/button";
    import { Input } from "$lib/components/ui/input";
    import { Label } from "$lib/components/ui/label";
    import { Switch } from "$lib/components/ui/switch";
    import { Textarea } from "$lib/components/ui/textarea";
    import { MultiSelect, type MultiSelectOption } from "$lib/components/ui/multi-select";
    import { Skeleton } from "$lib/components/ui/skeleton";
    import { toast } from "svelte-sonner";
    import { Check } from "lucide-svelte";
    import "$lib/styles/survey-theme.css";
    import type {
        CampaignSettingsRequest,
        CampaignSettingsResponse,
        SurveyThemeConfig,
    } from "$lib/types";

    const campaignId = $derived(page.params.id);

    // Settings form
    let settings = $state<CampaignSettingsRequest>({
        captchaEnabled: false,
        oneResponsePerDevice: false,
        ipRestrictionEnabled: false,
        emailRestrictionEnabled: false,
        sessionTimeoutMinutes: 30,
        showQuestionNumbers: true,
        showProgressIndicator: true,
        allowBackButton: true,
        collectName: false,
        collectEmail: false,
        collectPhone: false,
        collectAddress: false,
        dataCollectionFields: [],
    });
    let settingsLoading = $state(false);
    let settingsSaved = $state(false);
    let initialLoading = $state(true);
    let proThemeMode = $state(false);
    let fontStackSelection = $state<string[]>([]);
    let logoLoadFailed = $state(false);
    let activeStep = $state(0);

    const STUDIO_STEPS = [
        { title: "Direction", subtitle: "Template and brand base" },
        { title: "Palette", subtitle: "Color system and tokens" },
        { title: "Layout", subtitle: "Structure and motion" },
        { title: "Content", subtitle: "Header, footer, advanced" },
    ] as const;

    const isFirstStep = $derived(activeStep === 0);
    const isLastStep = $derived(activeStep === STUDIO_STEPS.length - 1);

    function goToStep(index: number) {
        activeStep = Math.max(0, Math.min(index, STUDIO_STEPS.length - 1));
    }

    function goNextStep() {
        goToStep(activeStep + 1);
    }

    function goPrevStep() {
        goToStep(activeStep - 1);
    }

    const THEME_TEMPLATES = [
        { key: "aurora-premium", label: "Aurora Premium" },
        { key: "executive-ink", label: "Executive Ink" },
        { key: "ivory-luxe", label: "Ivory Luxe" },
        { key: "campus-editorial", label: "Campus Editorial" },
    ] as const;

    const PALETTE_PRESETS: Record<string, SurveyThemeConfig["palette"]> = {
        "ocean-aurora": {
            background: "#f8fbfb",
            shell: "#ffffff",
            panel: "#e8f6f4",
            card: "#ffffff",
            border: "#9fd6cf",
            textPrimary: "#102a43",
            textSecondary: "#4e676c",
            primary: "#0f766e",
            primaryText: "#f8fffe",
            accent: "#14b8a6",
            accentSoft: "#d8f5f1",
            headerBackground: "#102a43",
            headerText: "#f8fffe",
            footerBackground: "#edf9f7",
            footerText: "#35545a",
        },
        "midnight-orchid": {
            background: "#f7f5fb",
            shell: "#ffffff",
            panel: "#f0eafb",
            card: "#ffffff",
            border: "#cab8eb",
            textPrimary: "#24173d",
            textSecondary: "#61557b",
            primary: "#5b21b6",
            primaryText: "#faf8ff",
            accent: "#c084fc",
            accentSoft: "#f3e8ff",
            headerBackground: "#24173d",
            headerText: "#faf8ff",
            footerBackground: "#f5efff",
            footerText: "#4c3f69",
        },
        "emerald-ivory": {
            background: "#fafbf7",
            shell: "#ffffff",
            panel: "#eef6ea",
            card: "#ffffff",
            border: "#bfd7c1",
            textPrimary: "#1f3527",
            textSecondary: "#5a7060",
            primary: "#166534",
            primaryText: "#f8fff9",
            accent: "#22c55e",
            accentSoft: "#dcfce7",
            headerBackground: "#1f3527",
            headerText: "#f8fff9",
            footerBackground: "#f1f8f1",
            footerText: "#45604b",
        },
        "sunset-royal": {
            background: "#fbf7f3",
            shell: "#ffffff",
            panel: "#faeee5",
            card: "#ffffff",
            border: "#e7c4ae",
            textPrimary: "#40231b",
            textSecondary: "#7f5a4e",
            primary: "#c2410c",
            primaryText: "#fff9f6",
            accent: "#f59e0b",
            accentSoft: "#ffedd5",
            headerBackground: "#40231b",
            headerText: "#fff9f6",
            footerBackground: "#fdf2eb",
            footerText: "#6f4b3f",
        },
    };

    const FONT_FAMILY_OPTIONS: MultiSelectOption[] = [
        { label: "Iowan Old Style", value: "\"Iowan Old Style\"" },
        { label: "Palatino Linotype", value: "\"Palatino Linotype\"" },
        { label: "Book Antiqua", value: "\"Book Antiqua\"" },
        { label: "Georgia", value: "Georgia" },
        { label: "Inter", value: "Inter" },
        { label: "Source Sans 3", value: "\"Source Sans 3\"" },
        { label: "Helvetica Neue", value: "\"Helvetica Neue\"" },
        { label: "Arial", value: "Arial" },
        { label: "system-ui", value: "system-ui" },
        { label: "serif", value: "serif" },
        { label: "sans-serif", value: "sans-serif" },
    ] as const;

    function cloneTheme<T>(value: T): T {
        return JSON.parse(JSON.stringify(value));
    }

    function createDefaultTheme(seed?: Partial<SurveyThemeConfig>): SurveyThemeConfig {
        return {
            templateKey: "aurora-premium",
            paletteKey: "ocean-aurora",
            palette: cloneTheme(PALETTE_PRESETS["ocean-aurora"]),
            branding: {
                brandLabel: "Confidential Evaluation Ledger",
                logoUrl: "",
                logoPosition: "left",
                fontFamily: "\"Iowan Old Style\", \"Palatino Linotype\", \"Book Antiqua\", Georgia, serif",
            },
            layout: {
                contentWidth: "standard",
                headerStyle: "hero",
                headerAlignment: "left",
                footerStyle: "support",
                footerAlignment: "left",
                sectionStyle: "panel",
                questionCardStyle: "soft",
                categorySeparatorStyle: "divider",
            },
            motion: {
                animationPreset: "subtle",
            },
            header: {
                enabled: true,
                eyebrow: "Confidential Evaluation Ledger",
                title: "Survey Experience",
                subtitle: "Share your feedback with clarity and confidence.",
                note: "",
            },
            footer: {
                enabled: true,
                line1: "Thank you for completing this survey.",
                line2: "Need assistance? Contact your survey administrator for support.",
                legal: "Responses are securely processed under your organization's data policy.",
            },
            advanced: {
                useCustomHeaderHtml: false,
                useCustomFooterHtml: false,
                customHeaderHtml: "",
                customFooterHtml: "",
                customCss: "",
            },
            ...seed,
        };
    }

    function ensureTheme(): SurveyThemeConfig {
        if (!settings.theme) {
            settings.theme = createDefaultTheme();
            syncFontStackSelection(settings.theme.branding.fontFamily);
        }
        return settings.theme;
    }

    function applyPalettePreset(key: string) {
        const theme = ensureTheme();
        theme.paletteKey = key;
        theme.palette = cloneTheme(PALETTE_PRESETS[key]);
    }

    function applyThemeTemplate(key: string) {
        const theme = ensureTheme();
        theme.templateKey = key;

        if (key === "executive-ink") {
            applyPalettePreset("midnight-orchid");
            theme.layout.headerStyle = "split";
            theme.layout.footerStyle = "minimal";
            theme.layout.questionCardStyle = "outlined";
            theme.motion.animationPreset = "subtle";
        } else if (key === "ivory-luxe") {
            applyPalettePreset("sunset-royal");
            theme.layout.headerStyle = "banner";
            theme.layout.footerStyle = "compliance";
            theme.layout.questionCardStyle = "soft";
            theme.motion.animationPreset = "none";
        } else if (key === "campus-editorial") {
            applyPalettePreset("emerald-ivory");
            theme.layout.headerStyle = "hero";
            theme.layout.footerStyle = "support";
            theme.layout.questionCardStyle = "soft";
            theme.motion.animationPreset = "subtle";
        } else {
            applyPalettePreset("ocean-aurora");
            theme.layout.headerStyle = "hero";
            theme.layout.footerStyle = "support";
            theme.layout.questionCardStyle = "soft";
            theme.motion.animationPreset = "subtle";
        }
    }

    function selectedFontFamilies(fontFamily?: string): string[] {
        if (!fontFamily || fontFamily.trim() === "") {
            return [];
        }
        return fontFamily
            .split(",")
            .map((token) => token.trim())
            .filter(Boolean);
    }

    function syncFontStackSelection(fontFamily?: string) {
        const nextSelection = selectedFontFamilies(fontFamily);
        if (JSON.stringify(fontStackSelection) !== JSON.stringify(nextSelection)) {
            fontStackSelection = nextSelection;
        }
    }

    $effect(() => {
        const theme = ensureTheme();
        const serialized = fontStackSelection.join(", ");
        if ((theme.branding.fontFamily ?? "") !== serialized) {
            theme.branding.fontFamily = serialized;
        }
    });

    function themePreviewStyle(theme: SurveyThemeConfig): string {
        return [
            `--preview-bg:${theme.palette.background}`,
            `--preview-shell:${theme.palette.shell}`,
            `--preview-panel:${theme.palette.panel}`,
            `--preview-card:${theme.palette.card}`,
            `--preview-border:${theme.palette.border}`,
            `--preview-text:${theme.palette.textPrimary}`,
            `--preview-muted:${theme.palette.textSecondary}`,
            `--preview-primary:${theme.palette.primary}`,
            `--preview-primary-text:${theme.palette.primaryText}`,
            `--preview-accent:${theme.palette.accent}`,
            `--preview-accent-soft:${theme.palette.accentSoft}`,
            `--preview-header-bg:${theme.palette.headerBackground}`,
            `--preview-header-text:${theme.palette.headerText}`,
            `--preview-footer-bg:${theme.palette.footerBackground}`,
            `--preview-footer-text:${theme.palette.footerText}`,
            `--preview-font:${theme.branding.fontFamily || "inherit"}`,
        ].join(";");
    }

    const currentTheme = $derived.by(() => ensureTheme());
    const activeLogoUrl = $derived(currentTheme.branding.logoUrl?.trim() || "");
    const shouldRenderLogo = $derived(Boolean(activeLogoUrl) && !logoLoadFailed);

    $effect(() => {
        activeLogoUrl;
        logoLoadFailed = false;
    });

    $effect(() => {
        const theme = ensureTheme();
        if (!theme.advanced.useCustomHeaderHtml) {
            theme.advanced.customHeaderHtml = "";
        }
        if (!theme.advanced.useCustomFooterHtml) {
            theme.advanced.customFooterHtml = "";
        }
    });

    function normalizeDateForInput(value?: string) {
        if (!value) {
            return undefined;
        }
        return value.includes("T") ? value.split("T")[0] : value;
    }

    function toNumberOrNull(value: unknown) {
        if (typeof value === "number" && Number.isFinite(value)) {
            return value;
        }
        if (typeof value === "string" && value.trim() !== "") {
            const parsed = Number(value);
            if (Number.isFinite(parsed)) {
                return parsed;
            }
        }
        return null;
    }

    function toIsoCloseDateOrNull(value?: string) {
        if (!value || value.trim() === "") {
            return null;
        }
        return `${value}T23:59:59Z`;
    }

    function buildSettingsPayload() {
        const theme = ensureTheme();
        return {
            ...settings,
            headerHtml: theme.advanced.useCustomHeaderHtml ? theme.advanced.customHeaderHtml : "",
            footerHtml: theme.advanced.useCustomFooterHtml ? theme.advanced.customFooterHtml : "",
            theme,
            closeDate: toIsoCloseDateOrNull(settings.closeDate),
            responseQuota: toNumberOrNull(settings.responseQuota),
            sessionTimeoutMinutes: toNumberOrNull(settings.sessionTimeoutMinutes) ?? 30,
        };
    }

    async function loadSettings() {
        initialLoading = true;
        try {
            const { data } = await api.get<CampaignSettingsResponse>(
                `/campaigns/${campaignId}/settings`,
            );
            settings = {
                password: data.password ?? "",
                captchaEnabled: data.captchaEnabled ?? false,
                oneResponsePerDevice: data.oneResponsePerDevice ?? false,
                ipRestrictionEnabled: data.ipRestrictionEnabled ?? false,
                emailRestrictionEnabled: data.emailRestrictionEnabled ?? false,
                responseQuota: data.responseQuota ?? undefined,
                closeDate: normalizeDateForInput(data.closeDate),
                sessionTimeoutMinutes: data.sessionTimeoutMinutes ?? 30,
                showQuestionNumbers: data.showQuestionNumbers ?? true,
                showProgressIndicator: data.showProgressIndicator ?? true,
                allowBackButton: data.allowBackButton ?? true,
                startMessage: data.startMessage ?? "",
                finishMessage: data.finishMessage ?? "",
                headerHtml: data.headerHtml ?? "",
                footerHtml: data.footerHtml ?? "",
                theme: data.theme
                    ? cloneTheme(data.theme)
                    : createDefaultTheme({
                          header: {
                              enabled: true,
                              eyebrow: "Confidential Evaluation Ledger",
                              title: data.campaignId ?? "Survey Experience",
                              subtitle: data.startMessage ?? "Share your feedback with clarity and confidence.",
                              note: "",
                          },
                          footer: {
                              enabled: true,
                              line1: data.finishMessage ?? "Thank you for completing this survey.",
                              line2: "Need assistance? Contact your survey administrator for support.",
                              legal: "Responses are securely processed under your organization's data policy.",
                          },
                          advanced: {
                              useCustomHeaderHtml: false,
                              useCustomFooterHtml: false,
                              customHeaderHtml: data.headerHtml ?? "",
                              customFooterHtml: data.footerHtml ?? "",
                              customCss: "",
                          },
                      }),
                collectName: data.collectName ?? false,
                collectEmail: data.collectEmail ?? false,
                collectPhone: data.collectPhone ?? false,
                collectAddress: data.collectAddress ?? false,
                dataCollectionFields: data.dataCollectionFields ?? [],
            };
            syncFontStackSelection(settings.theme?.branding.fontFamily);
        } catch {
            toast.error("Failed to load settings");
        } finally {
            initialLoading = false;
        }
    }

    async function saveSettings() {
        settingsLoading = true;
        settingsSaved = false;
        try {
            await api.put(`/campaigns/${campaignId}/settings`, buildSettingsPayload());
            await loadSettings();
            toast.success("Theme saved successfully");
            settingsSaved = true;
            setTimeout(() => (settingsSaved = false), 3000);
        } catch {
            toast.error("Failed to save theme");
        } finally {
            settingsLoading = false;
        }
    }

    onMount(loadSettings);
</script>

{#if initialLoading}
    <div class="space-y-6">
        <Skeleton class="h-10 w-full mb-4" />
        <div class="grid gap-4 lg:grid-cols-2">
            <Skeleton class="h-64 w-full" />
            <Skeleton class="h-64 w-full" />
        </div>
        <div class="grid gap-4 lg:grid-cols-2">
            <Skeleton class="h-64 w-full" />
            <Skeleton class="h-64 w-full" />
        </div>
    </div>
{:else}
    <form
        onsubmit={(e) => {
            e.preventDefault();
            saveSettings();
        }}
        class="space-y-6"
    >
        <Card.Root class="overflow-hidden border-border/70 bg-gradient-to-br from-card via-card to-muted/20">
            <Card.Content class="space-y-6 p-5 sm:p-6 lg:p-8">
                <div class="rounded-2xl border border-border/70 bg-background/70 p-5 backdrop-blur-sm">
                    <p class="text-[11px] font-semibold uppercase tracking-[0.24em] text-primary/80">Theme Studio</p>
                    <h2 class="mt-2 text-2xl font-semibold tracking-tight text-foreground">Build a premium responder experience</h2>
                    <p class="mt-2 max-w-3xl text-sm leading-6 text-muted-foreground">
                        Start from a design direction, shape brand and layout semantics, then finalize structured content.
                        Keep Pro Theme Mode off for guided control. Enable it only when you need token-level precision.
                    </p>
                    <div class="mt-4 flex flex-wrap gap-2">
                        {#each STUDIO_STEPS as step, index}
                            <button
                                type="button"
                                onclick={() => goToStep(index)}
                                class={`rounded-full border px-3 py-1 text-[11px] font-semibold uppercase tracking-wide transition ${
                                    activeStep === index
                                        ? "border-primary bg-primary text-primary-foreground"
                                        : "border-border bg-muted/40 text-muted-foreground hover:bg-muted/70"
                                }`}
                            >
                                {index + 1}. {step.title}
                            </button>
                        {/each}
                    </div>
                </div>

                <div class="grid gap-6 xl:grid-cols-[minmax(0,1fr)_390px]">
                    <div class="space-y-6">
                        {#if activeStep === 0}
                        <section class="space-y-4 rounded-2xl border border-border/70 bg-background/80 p-5 shadow-sm">
                            <div class="flex items-start justify-between gap-4">
                                <div>
                                    <h3 class="text-sm font-semibold uppercase tracking-[0.16em] text-foreground/90">Design Direction</h3>
                                    <p class="mt-1 text-xs text-muted-foreground">Pick an intent first, then iterate details.</p>
                                </div>
                                <div class="flex items-center gap-2 rounded-xl border border-border/70 bg-muted/30 px-3 py-2">
                                    <div>
                                        <p class="text-[11px] font-semibold uppercase tracking-[0.14em] text-foreground/85">Pro Theme Mode</p>
                                        <p class="text-[11px] text-muted-foreground">Manual token editing</p>
                                    </div>
                                    <Switch bind:checked={proThemeMode} />
                                </div>
                            </div>

                            <div class="space-y-2">
                                <Label class="text-xs font-semibold uppercase tracking-[0.12em] text-muted-foreground">Theme Templates</Label>
                                <div class="flex flex-wrap gap-2">
                                    {#each THEME_TEMPLATES as template}
                                        <Button
                                            type="button"
                                            size="sm"
                                            variant={currentTheme.templateKey === template.key ? "default" : "outline"}
                                            onclick={() => applyThemeTemplate(template.key)}
                                        >
                                            {template.label}
                                        </Button>
                                    {/each}
                                </div>
                            </div>

                            <div class="space-y-2">
                                <Label class="text-xs font-semibold uppercase tracking-[0.12em] text-muted-foreground">Palette Presets</Label>
                                <div class="flex flex-wrap gap-2">
                                    {#each Object.keys(PALETTE_PRESETS) as paletteKey}
                                        <Button
                                            type="button"
                                            size="sm"
                                            variant={currentTheme.paletteKey === paletteKey ? "default" : "outline"}
                                            onclick={() => applyPalettePreset(paletteKey)}
                                        >
                                            {paletteKey}
                                        </Button>
                                    {/each}
                                </div>
                            </div>
                        </section>

                        <section class="space-y-4 rounded-2xl border border-border/70 bg-background/80 p-5 shadow-sm">
                            <h3 class="text-sm font-semibold uppercase tracking-[0.16em] text-foreground/90">Brand Identity</h3>
                            <div class="grid gap-3 md:grid-cols-2">
                                <div class="space-y-2">
                                    <Label for="theme-brand-label">Brand Label</Label>
                                    <Input id="theme-brand-label" bind:value={currentTheme.branding.brandLabel} />
                                </div>
                                <div class="space-y-2">
                                    <Label for="theme-logo-url">Logo URL</Label>
                                    <Input id="theme-logo-url" placeholder="https://..." bind:value={currentTheme.branding.logoUrl} />
                                </div>
                                <div class="space-y-2">
                                    <Label for="theme-logo-position">Logo Position</Label>
                                    <select
                                        id="theme-logo-position"
                                        bind:value={currentTheme.branding.logoPosition}
                                        class="flex h-9 w-full rounded-md border border-input bg-transparent px-3 py-1 text-sm shadow-sm transition-colors focus-visible:outline-none focus-visible:ring-1 focus-visible:ring-ring"
                                    >
                                        <option value="left">Left</option>
                                        <option value="center">Center</option>
                                        <option value="right">Right</option>
                                    </select>
                                </div>
                                <div class="space-y-2">
                                    <Label>Font Family Stack</Label>
                                    <MultiSelect
                                        options={FONT_FAMILY_OPTIONS}
                                        bind:selected={fontStackSelection}
                                        placeholder="Select the fonts you want in the survey stack"
                                        searchPlaceholder="Search fonts"
                                        emptyMessage="No fonts match this search."
                                        sortable={true}
                                        showOrder={true}
                                        selectedHelperText="First font is primary. Others are graceful fallbacks."
                                    />
                                </div>
                            </div>
                        </section>
                        {:else if activeStep === 1}

                        <section class="space-y-4 rounded-2xl border border-border/70 bg-background/80 p-5 shadow-sm">
                            <h3 class="text-sm font-semibold uppercase tracking-[0.16em] text-foreground/90">Color System</h3>
                            <div class="grid gap-3 md:grid-cols-2">
                                <div class="space-y-2">
                                    <Label for="theme-primary">Primary</Label>
                                    <Input id="theme-primary" bind:value={currentTheme.palette.primary} disabled={!proThemeMode} />
                                </div>
                                <div class="space-y-2">
                                    <Label for="theme-accent">Accent</Label>
                                    <Input id="theme-accent" bind:value={currentTheme.palette.accent} disabled={!proThemeMode} />
                                </div>
                                <div class="space-y-2">
                                    <Label for="theme-background">Background</Label>
                                    <Input id="theme-background" bind:value={currentTheme.palette.background} disabled={!proThemeMode} />
                                </div>
                                <div class="space-y-2">
                                    <Label for="theme-panel">Panel</Label>
                                    <Input id="theme-panel" bind:value={currentTheme.palette.panel} disabled={!proThemeMode} />
                                </div>
                                <div class="space-y-2">
                                    <Label for="theme-text-primary">Text Primary</Label>
                                    <Input id="theme-text-primary" bind:value={currentTheme.palette.textPrimary} disabled={!proThemeMode} />
                                </div>
                                <div class="space-y-2">
                                    <Label for="theme-text-secondary">Text Secondary</Label>
                                    <Input id="theme-text-secondary" bind:value={currentTheme.palette.textSecondary} disabled={!proThemeMode} />
                                </div>
                                <div class="space-y-2">
                                    <Label for="theme-header-bg">Header Background</Label>
                                    <Input id="theme-header-bg" bind:value={currentTheme.palette.headerBackground} disabled={!proThemeMode} />
                                </div>
                                <div class="space-y-2">
                                    <Label for="theme-footer-bg">Footer Background</Label>
                                    <Input id="theme-footer-bg" bind:value={currentTheme.palette.footerBackground} disabled={!proThemeMode} />
                                </div>
                            </div>
                            {#if !proThemeMode}
                                <p class="text-xs text-muted-foreground">Guided mode active. Enable Pro Theme Mode for raw token editing.</p>
                            {/if}
                        </section>
                        {:else if activeStep === 2}

                        <section class="space-y-4 rounded-2xl border border-border/70 bg-background/80 p-5 shadow-sm">
                            <h3 class="text-sm font-semibold uppercase tracking-[0.16em] text-foreground/90">Layout & Motion</h3>
                            <div class="grid gap-3 md:grid-cols-2">
                                <div class="space-y-2">
                                    <Label for="theme-content-width">Content Width</Label>
                                    <select id="theme-content-width" bind:value={currentTheme.layout.contentWidth} class="flex h-9 w-full rounded-md border border-input bg-transparent px-3 py-1 text-sm shadow-sm transition-colors focus-visible:outline-none focus-visible:ring-1 focus-visible:ring-ring">
                                        <option value="narrow">Narrow</option>
                                        <option value="standard">Standard</option>
                                        <option value="wide">Wide</option>
                                    </select>
                                </div>
                                <div class="space-y-2">
                                    <Label for="theme-header-style">Header Style</Label>
                                    <select id="theme-header-style" bind:value={currentTheme.layout.headerStyle} class="flex h-9 w-full rounded-md border border-input bg-transparent px-3 py-1 text-sm shadow-sm transition-colors focus-visible:outline-none focus-visible:ring-1 focus-visible:ring-ring">
                                        <option value="hero">Hero</option>
                                        <option value="banner">Banner</option>
                                        <option value="split">Split</option>
                                        <option value="minimal">Minimal</option>
                                    </select>
                                </div>
                                <div class="space-y-2">
                                    <Label for="theme-header-align">Header Alignment</Label>
                                    <select id="theme-header-align" bind:value={currentTheme.layout.headerAlignment} class="flex h-9 w-full rounded-md border border-input bg-transparent px-3 py-1 text-sm shadow-sm transition-colors focus-visible:outline-none focus-visible:ring-1 focus-visible:ring-ring">
                                        <option value="left">Left</option>
                                        <option value="center">Center</option>
                                        <option value="right">Right</option>
                                    </select>
                                </div>
                                <div class="space-y-2">
                                    <Label for="theme-footer-style">Footer Style</Label>
                                    <select id="theme-footer-style" bind:value={currentTheme.layout.footerStyle} class="flex h-9 w-full rounded-md border border-input bg-transparent px-3 py-1 text-sm shadow-sm transition-colors focus-visible:outline-none focus-visible:ring-1 focus-visible:ring-ring">
                                        <option value="minimal">Minimal</option>
                                        <option value="support">Support</option>
                                        <option value="compliance">Compliance</option>
                                        <option value="branded">Branded</option>
                                    </select>
                                </div>
                                <div class="space-y-2">
                                    <Label for="theme-question-style">Question Card Style</Label>
                                    <select id="theme-question-style" bind:value={currentTheme.layout.questionCardStyle} class="flex h-9 w-full rounded-md border border-input bg-transparent px-3 py-1 text-sm shadow-sm transition-colors focus-visible:outline-none focus-visible:ring-1 focus-visible:ring-ring">
                                        <option value="soft">Soft</option>
                                        <option value="outlined">Outlined</option>
                                        <option value="flat">Flat</option>
                                    </select>
                                </div>
                                <div class="space-y-2">
                                    <Label for="theme-motion">Animation Preset</Label>
                                    <select id="theme-motion" bind:value={currentTheme.motion.animationPreset} class="flex h-9 w-full rounded-md border border-input bg-transparent px-3 py-1 text-sm shadow-sm transition-colors focus-visible:outline-none focus-visible:ring-1 focus-visible:ring-ring">
                                        <option value="none">None</option>
                                        <option value="subtle">Subtle</option>
                                        <option value="elevated">Elevated</option>
                                    </select>
                                </div>
                            </div>
                        </section>
                        {:else}

                        <div class="grid gap-4 lg:grid-cols-2">
                            <section class="space-y-4 rounded-2xl border border-border/70 bg-background/80 p-5 shadow-sm">
                                <div class="flex items-center justify-between">
                                    <div>
                                        <h3 class="text-sm font-semibold uppercase tracking-[0.16em] text-foreground/90">Header Content</h3>
                                        <p class="mt-1 text-xs text-muted-foreground">Structured branding area above the survey.</p>
                                    </div>
                                    <Switch bind:checked={currentTheme.header.enabled} />
                                </div>
                                <div class="grid gap-3 md:grid-cols-2">
                                    <div class="space-y-2">
                                        <Label for="theme-header-eyebrow">Eyebrow</Label>
                                        <Input id="theme-header-eyebrow" bind:value={currentTheme.header.eyebrow} />
                                    </div>
                                    <div class="space-y-2">
                                        <Label for="theme-header-title">Title</Label>
                                        <Input id="theme-header-title" bind:value={currentTheme.header.title} />
                                    </div>
                                    <div class="space-y-2 md:col-span-2">
                                        <Label for="theme-header-subtitle">Subtitle</Label>
                                        <Input id="theme-header-subtitle" bind:value={currentTheme.header.subtitle} />
                                    </div>
                                    <div class="space-y-2 md:col-span-2">
                                        <Label for="theme-header-note">Optional Note</Label>
                                        <Input id="theme-header-note" bind:value={currentTheme.header.note} />
                                    </div>
                                </div>
                                <div class="flex items-center justify-between">
                                    <div>
                                        <Label class="text-sm font-semibold">Advanced Header HTML</Label>
                                        <p class="text-xs text-muted-foreground">Use only when structured content is not enough.</p>
                                    </div>
                                    <Switch bind:checked={currentTheme.advanced.useCustomHeaderHtml} />
                                </div>
                                {#if currentTheme.advanced.useCustomHeaderHtml}
                                    <Textarea rows={8} class="font-mono text-xs" bind:value={currentTheme.advanced.customHeaderHtml} />
                                {/if}
                            </section>

                            <section class="space-y-4 rounded-2xl border border-border/70 bg-background/80 p-5 shadow-sm">
                                <div class="flex items-center justify-between">
                                    <div>
                                        <h3 class="text-sm font-semibold uppercase tracking-[0.16em] text-foreground/90">Footer Content</h3>
                                        <p class="mt-1 text-xs text-muted-foreground">Structured messaging after the survey.</p>
                                    </div>
                                    <Switch bind:checked={currentTheme.footer.enabled} />
                                </div>
                                <div class="grid gap-3 md:grid-cols-2">
                                    <div class="space-y-2">
                                        <Label for="theme-footer-line1">Primary Line</Label>
                                        <Input id="theme-footer-line1" bind:value={currentTheme.footer.line1} />
                                    </div>
                                    <div class="space-y-2">
                                        <Label for="theme-footer-line2">Secondary Line</Label>
                                        <Input id="theme-footer-line2" bind:value={currentTheme.footer.line2} />
                                    </div>
                                    <div class="space-y-2 md:col-span-2">
                                        <Label for="theme-footer-legal">Legal / Compliance Note</Label>
                                        <Input id="theme-footer-legal" bind:value={currentTheme.footer.legal} />
                                    </div>
                                </div>
                                <div class="flex items-center justify-between">
                                    <div>
                                        <Label class="text-sm font-semibold">Advanced Footer HTML</Label>
                                        <p class="text-xs text-muted-foreground">Use only when structured content is not enough.</p>
                                    </div>
                                    <Switch bind:checked={currentTheme.advanced.useCustomFooterHtml} />
                                </div>
                                {#if currentTheme.advanced.useCustomFooterHtml}
                                    <Textarea rows={8} class="font-mono text-xs" bind:value={currentTheme.advanced.customFooterHtml} />
                                {/if}
                            </section>
                        </div>

                        <section class="space-y-2 rounded-2xl border border-border/70 bg-background/80 p-5 shadow-sm">
                            <h3 class="text-sm font-semibold uppercase tracking-[0.16em] text-foreground/90">Advanced CSS Override</h3>
                            <Textarea rows={6} class="font-mono text-xs" placeholder=".survey-shell styles" bind:value={currentTheme.advanced.customCss} />
                        </section>
                        {/if}

                        <div class="flex items-center justify-between rounded-2xl border border-border/70 bg-background/80 p-4">
                            <div>
                                <p class="text-[11px] font-semibold uppercase tracking-[0.14em] text-muted-foreground">Step {activeStep + 1} of {STUDIO_STEPS.length}</p>
                                <p class="text-sm font-medium text-foreground">{STUDIO_STEPS[activeStep].subtitle}</p>
                            </div>
                            <div class="flex gap-2">
                                <Button type="button" variant="outline" onclick={goPrevStep} disabled={isFirstStep}>
                                    Previous
                                </Button>
                                <Button type="button" onclick={goNextStep} disabled={isLastStep}>
                                    Next
                                </Button>
                            </div>
                        </div>
                    </div>

                    <aside class="xl:sticky xl:top-20 xl:self-start">
                        <div class="space-y-4 rounded-2xl border border-border/70 bg-background/90 p-4 shadow-sm backdrop-blur-sm">
                            <div class="flex items-center justify-between">
                                <div>
                                    <p class="text-[11px] font-semibold uppercase tracking-[0.18em] text-muted-foreground">Live Snapshot</p>
                                    <p class="text-xs text-muted-foreground">Desktop responder preview</p>
                                </div>
                                <span class="rounded-full border border-border bg-muted/40 px-2.5 py-1 text-[10px] font-semibold uppercase tracking-wide text-muted-foreground">
                                    {currentTheme.layout.contentWidth}
                                </span>
                            </div>

                            <div class={`theme-studio-preview theme-studio-preview--${currentTheme.layout.contentWidth} theme-studio-preview--motion-${currentTheme.motion.animationPreset}`} style={themePreviewStyle(currentTheme)}>
                                {#if currentTheme.advanced.useCustomHeaderHtml && currentTheme.advanced.customHeaderHtml}
                                    <div class="theme-studio-preview__custom-html theme-studio-preview__custom-html--header">
                                        {@html currentTheme.advanced.customHeaderHtml}
                                    </div>
                                {:else if currentTheme.header.enabled}
                                    <div class={`theme-studio-preview__header theme-studio-preview__header--${currentTheme.layout.headerStyle}`} data-align={currentTheme.layout.headerAlignment} data-logo-position={currentTheme.branding.logoPosition}>
                                        {#if shouldRenderLogo}
                                            <img
                                                src={activeLogoUrl}
                                                alt="Brand logo"
                                                class="theme-studio-preview__logo"
                                                onerror={() => {
                                                    logoLoadFailed = true;
                                                }}
                                            />
                                        {/if}
                                        <div class="theme-studio-preview__eyebrow">{currentTheme.header.eyebrow || currentTheme.branding.brandLabel}</div>
                                        <h3>{currentTheme.header.title}</h3>
                                        <p>{currentTheme.header.subtitle}</p>
                                        {#if currentTheme.header.note}
                                            <div class="theme-studio-preview__note">{currentTheme.header.note}</div>
                                        {/if}
                                    </div>
                                {/if}
                                <div class={`theme-studio-preview__panel theme-studio-preview__panel--${currentTheme.layout.sectionStyle}`}>
                                    <div class="theme-studio-preview__section-title">Question Panel</div>
                                    <div class={`theme-studio-preview__divider theme-studio-preview__divider--${currentTheme.layout.categorySeparatorStyle}`}></div>
                                    <div class={`theme-studio-preview__card theme-studio-preview__card--${currentTheme.layout.questionCardStyle}`}>Sample rating question card</div>
                                </div>
                                {#if currentTheme.advanced.useCustomFooterHtml && currentTheme.advanced.customFooterHtml}
                                    <div class="theme-studio-preview__custom-html theme-studio-preview__custom-html--footer theme-studio-preview__custom-html--footer-edge">
                                        {@html currentTheme.advanced.customFooterHtml}
                                    </div>
                                {:else if currentTheme.footer.enabled}
                                    <div class={`theme-studio-preview__footer theme-studio-preview__footer--${currentTheme.layout.footerStyle} theme-studio-preview__footer--edge`}>
                                        <div class="theme-studio-preview__footer-line1">{currentTheme.footer.line1}</div>
                                        {#if currentTheme.footer.line2}
                                            <div class="theme-studio-preview__footer-line2">{currentTheme.footer.line2}</div>
                                        {/if}
                                        {#if currentTheme.footer.legal}
                                            <div class="theme-studio-preview__footer-legal">{currentTheme.footer.legal}</div>
                                        {/if}
                                    </div>
                                {/if}
                            </div>

                            <div class="rounded-xl border border-border/70 bg-muted/25 p-3">
                                <p class="text-[11px] font-semibold uppercase tracking-[0.14em] text-muted-foreground">Quality Rails</p>
                                <ul class="mt-2 space-y-1 text-xs text-muted-foreground">
                                    <li>Keep body text contrast above 4.5:1.</li>
                                    <li>Prefer one primary accent and one support accent.</li>
                                    <li>Reserve custom HTML for true edge cases.</li>
                                </ul>
                            </div>

                            <div class="flex items-center justify-between gap-3 border-t border-border pt-3">
                                {#if settingsSaved}
                                    <span class="flex items-center gap-1 text-sm font-medium text-emerald-500">
                                        <Check class="h-4 w-4" />
                                        Saved
                                    </span>
                                {:else}
                                    <span class="text-xs text-muted-foreground">Draft changes are local until saved</span>
                                {/if}
                                <Button type="submit" disabled={settingsLoading} class="min-w-[130px]">
                                    {#if settingsLoading}
                                        <span class="mr-2 inline-block h-4 w-4 animate-spin rounded-full border-2 border-current border-t-transparent"></span>
                                    {/if}
                                    Save Theme
                                </Button>
                            </div>
                        </div>
                    </aside>
                </div>
            </Card.Content>
        </Card.Root>
    </form>
{/if}
