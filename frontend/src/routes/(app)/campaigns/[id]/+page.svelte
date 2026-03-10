<script lang="ts">
    import api from "$lib/api";
    import { page } from "$app/state";
    import { onMount } from "svelte";
    import { goto } from "$app/navigation";
    import * as Card from "$lib/components/ui/card";
    import { Button } from "$lib/components/ui/button";
    import { Input } from "$lib/components/ui/input";
    import { Label } from "$lib/components/ui/label";
    import { Badge } from "$lib/components/ui/badge";
    import { Switch } from "$lib/components/ui/switch";
    import { Textarea } from "$lib/components/ui/textarea";
    import { MultiSelect, type MultiSelectOption } from "$lib/components/ui/multi-select";
    import { toast } from "svelte-sonner";
    import { Skeleton } from "$lib/components/ui/skeleton";
    import { Confetti } from "$lib/components/confetti";
    import { ErrorBanner } from "$lib/components/error-banner";
    import "$lib/styles/survey-theme.css";
    import {
        ArrowLeft,
        Settings,
        Palette,
        Share2,
        Info,
        Play,
        Copy,
        Check,
        Send,
        Eye,
        CalendarDays,
        X,
        BarChart3,
        Plus,
        Trash2,
        GripVertical
    } from "lucide-svelte";
    import type {
        CampaignResponse,
        CampaignSettingsRequest,
        CampaignSettingsResponse,
        DistributionChannelResponse,
        CampaignStatus,
        SurveyThemeConfig,
    } from "$lib/types";

    // --- State ---
    let campaign = $state<CampaignResponse | null>(null);
    let channels = $state<DistributionChannelResponse[]>([]);
    let loading = $state(true);
    let activeTab = $state<"overview" | "settings" | "theme" | "distribution">(
        "overview",
    );
    let copiedId = $state<string | null>(null);

    // Confetti celebration
    let showConfetti = $state(false);
    let confettiTitle = $state('');
    let confettiMessage = $state('');

    // API Error Banner - only for 500-level errors
    type ApiErrorState = {
        show: boolean;
        type: 'error';
        title: string;
        message: string;
    };
    let apiError = $state<ApiErrorState>({ show: false, type: 'error', title: '', message: '' });

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
    let proThemeMode = $state(false);
    let fontStackSelection = $state<string[]>([]);
    let logoLoadFailed = $state(false);
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
                title: campaign?.name ?? "Survey Experience",
                subtitle: campaign?.description ?? "Share your feedback with clarity and confidence.",
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

    // Distribute
    let distributeLoading = $state(false);

    // Activate
    let activateLoading = $state(false);

    const campaignId = $derived(page.params.id);
    const currentTheme = $derived.by(() => ensureTheme());
    const activeLogoUrl = $derived(currentTheme.branding.logoUrl?.trim() || "");
    const shouldRenderLogo = $derived(Boolean(activeLogoUrl) && !logoLoadFailed);

    $effect(() => {
        activeLogoUrl;
        logoLoadFailed = false;
    });

    function statusBadgeVariant(status: CampaignStatus) {
        switch (status) {
            case "ACTIVE":
                return "default" as const;
            case "DRAFT":
                return "secondary" as const;
            default:
                return "outline" as const;
        }
    }

    // --- API ---
    async function loadCampaign() {
        loading = true;
        try {
            const { data } = await api.get<CampaignResponse>(
                `/campaigns/${campaignId}`,
            );
            campaign = data;
            await loadSettings();

            // Load channels if active
            if (data.status !== "DRAFT") {
                try {
                    const chRes = await api.get<DistributionChannelResponse[]>(
                        `/campaigns/${campaignId}/channels`,
                    );
                    channels = chRes.data;
                } catch {
                    channels = [];
                }
            }
        } catch {
            goto("/campaigns");
        } finally {
            loading = false;
        }
    }

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
        // Send full ISO instant to satisfy backend Instant parsing.
        return `${value}T23:59:59Z`;
    }

    function openCloseDatePicker() {
        const input = document.getElementById("s-close") as HTMLInputElement | null;
        input?.showPicker?.();
        input?.focus();
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
            sessionTimeoutMinutes:
                toNumberOrNull(settings.sessionTimeoutMinutes) ?? 30,
        };
    }

    async function loadSettings() {
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
                              title: campaign?.name ?? data.campaignId ?? "Survey Experience",
                              subtitle: campaign?.description ?? data.startMessage ?? "Share your feedback with clarity and confidence.",
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
            // keep defaults
        }
    }

    function addDataCollectionField() {
        if (!settings.dataCollectionFields) {
            settings.dataCollectionFields = [];
        }
        settings.dataCollectionFields.push({
            fieldKey: "new_field",
            label: "New Field",
            fieldType: "TEXT",
            required: false,
            sortOrder: settings.dataCollectionFields.length,
            enabled: true,
        });
    }

    function removeDataCollectionField(index: number) {
        if (!settings.dataCollectionFields) return;
        settings.dataCollectionFields.splice(index, 1);
    }

    async function saveSettings() {
        settingsLoading = true;
        settingsSaved = false;
        try {
            await api.put(`/campaigns/${campaignId}/settings`, buildSettingsPayload());
            await loadSettings();
            settingsSaved = true;
            setTimeout(() => (settingsSaved = false), 3000);
        } catch {
            // silent
        } finally {
            settingsLoading = false;
        }
    }

    function escapeHtml(value: string) {
        return value
            .replaceAll("&", "&amp;")
            .replaceAll("<", "&lt;")
            .replaceAll(">", "&gt;")
            .replaceAll('"', "&quot;")
            .replaceAll("'", "&#39;");
    }

    $effect(() => {
        const theme = ensureTheme();
        if (!theme.advanced.useCustomHeaderHtml) {
            theme.advanced.customHeaderHtml = "";
        }
        if (!theme.advanced.useCustomFooterHtml) {
            theme.advanced.customFooterHtml = "";
        }
    });

    async function activate() {
        activateLoading = true;
        apiError = { show: false, type: 'error', title: '', message: '' };
        try {
            await api.post(`/campaigns/${campaignId}/activate`);
            // 🎉 Celebrate campaign activation
            showConfetti = true;
            confettiTitle = '🚀 Campaign Activated!';
            confettiMessage = 'Your campaign is now live and collecting responses.';
            setTimeout(() => (showConfetti = false), 4500);
            await loadCampaign();
        } catch (err: any) {
            const status = err?.response?.status;
            const message = err?.response?.data?.message || 'Failed to activate campaign.';
            
            // Show banner only for 500-level errors
            if (status && status >= 500) {
                apiError = {
                    show: true,
                    type: 'error',
                    title: '🔴 Server Error',
                    message: 'Our servers are experiencing issues. Please try again later.'
                };
            }
        } finally {
            activateLoading = false;
        }
    }

    async function distribute() {
        distributeLoading = true;
        try {
            const { data } = await api.post<DistributionChannelResponse[]>(
                `/campaigns/${campaignId}/distribute`,
            );
            channels = data;
            activeTab = "distribution";
            toast.success("Channels generated successfully");
        } catch (err: any) {
            toast.error(
                err.response?.data?.message ||
                    "Failed to generate distribution channels.",
            );
        } finally {
            distributeLoading = false;
        }
    }

    async function copyToClipboard(text: string, id: string) {
        try {
            if (navigator?.clipboard && navigator.clipboard.writeText) {
                await navigator.clipboard.writeText(text);
            } else {
                const textArea = document.createElement("textarea");
                textArea.value = text;
                textArea.style.position = "fixed";
                textArea.style.left = "-999999px";
                textArea.style.top = "-999999px";
                document.body.appendChild(textArea);
                textArea.focus();
                textArea.select();
                try {
                    document.execCommand('copy');
                } finally {
                    textArea.remove();
                }
            }
            copiedId = id;
            setTimeout(() => (copiedId = null), 2000);
        } catch (err) {
            console.error("Failed to copy:", err);
            toast.error("Failed to copy to clipboard");
        }
    }

    function formatDate(iso: string) {
        return new Date(iso).toLocaleDateString("en-US", {
            month: "short",
            day: "numeric",
            year: "numeric",
            hour: "2-digit",
            minute: "2-digit",
        });
    }

    function channelLabel(type: string): string {
        return type.replace(/_/g, " ");
    }

    const tabs = [
        { id: "overview" as const, label: "Overview", icon: Info },
        { id: "settings" as const, label: "Settings", icon: Settings },
        { id: "theme" as const, label: "Theme Studio", icon: Palette },
        {
            id: "distribution" as const,
            label: "Distribution",
            icon: Share2,
        },
    ];

    onMount(loadCampaign);
</script>

<svelte:head>
    <title>{campaign?.name ?? "Campaign"} — Survey Engine</title>
</svelte:head>

{#if loading}
    <div class="space-y-6">
        <div class="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
            <div class="flex items-center gap-3">
                <Skeleton class="h-9 w-9" />
                <div class="space-y-2">
                    <Skeleton class="h-8 w-[250px]" />
                    <Skeleton class="h-4 w-[180px]" />
                </div>
            </div>
            <div class="flex gap-2">
                <Skeleton class="h-9 w-[100px]" />
                <Skeleton class="h-9 w-[100px]" />
            </div>
        </div>
        <div class="flex gap-1 border-b border-border">
            {#each Array(3) as _}
                <Skeleton class="h-10 w-[120px]" />
            {/each}
        </div>
        <div class="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
            {#each Array(6) as _}
                <Card.Root>
                    <Card.Header class="pb-2">
                        <Skeleton class="h-4 w-[100px]" />
                    </Card.Header>
                    <Card.Content>
                        <Skeleton class="h-6 w-[80px]" />
                    </Card.Content>
                </Card.Root>
            {/each}
        </div>
    </div>
{:else if campaign}
    <div class="space-y-6">
        <ErrorBanner
            show={apiError.show}
            type="failure"
            title={apiError.title}
            message={apiError.message}
            showRetry={true}
            onRetry={activate}
            onDismiss={() => (apiError = { show: false, type: 'error', title: '', message: '' })}
        />

        <!-- Header -->
        <div
            class="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between"
        >
            <div class="flex items-center gap-3">
                <Button
                    variant="ghost"
                    size="sm"
                    onclick={() => goto("/campaigns")}
                >
                    <ArrowLeft class="h-4 w-4" />
                </Button>
                <div>
                    <div class="flex items-center gap-2">
                        <h1
                            class="text-2xl font-bold tracking-tight text-foreground"
                        >
                            {campaign.name}
                        </h1>
                        <Badge variant={statusBadgeVariant(campaign.status)}>
                            {campaign.status}
                        </Badge>
                    </div>
                    {#if campaign.description}
                        <p class="mt-0.5 text-sm text-muted-foreground">
                            {campaign.description}
                        </p>
                    {/if}
                </div>
            </div>
            <div class="flex gap-2">
                <Button
                    variant="outline"
                    onclick={() => goto(`/campaigns/${campaignId}/preview`)}
                >
                    <Eye class="mr-2 h-4 w-4" />
                    Preview
                </Button>
                {#if campaign.status === "DRAFT"}
                    <Button onclick={activate} disabled={activateLoading}>
                        {#if activateLoading}
                            <span
                                class="mr-2 inline-block h-4 w-4 animate-spin rounded-full border-2 border-current border-t-transparent"
                            ></span>
                        {/if}
                        <Play class="mr-2 h-4 w-4" />
                        Activate
                    </Button>
                {/if}
                {#if campaign.status === "ACTIVE" && channels.length === 0}
                    <Button onclick={distribute} disabled={distributeLoading}>
                        {#if distributeLoading}
                            <span
                                class="mr-2 inline-block h-4 w-4 animate-spin rounded-full border-2 border-current border-t-transparent"
                            ></span>
                        {/if}
                        <Send class="mr-2 h-4 w-4" />
                        Generate Channels
                    </Button>
                {/if}
            </div>
        </div>

        <!-- Tabs -->
        <div class="flex gap-1 border-b border-border overflow-x-auto whitespace-nowrap pb-px no-scrollbar">
            {#each tabs as tab}
                <button
                    class="flex items-center gap-2 px-4 py-2.5 text-sm font-medium transition-colors border-b-2 -mb-[2px] {activeTab ===
                    tab.id
                        ? 'border-primary text-primary'
                        : 'border-transparent text-muted-foreground hover:text-foreground hover:border-border'}"
                    onclick={() => (activeTab = tab.id)}
                >
                    <tab.icon class="h-4 w-4" />
                    {tab.label}
                </button>
            {/each}
            <button
                class="flex items-center gap-2 px-4 py-2.5 text-sm font-medium transition-colors border-b-2 -mb-[2px] border-transparent text-muted-foreground hover:text-foreground hover:border-border"
                onclick={() => goto(`/campaigns/${campaignId}/analytics`)}
            >
                <BarChart3 class="h-4 w-4" />
                Analytics
            </button>
        </div>

        <!-- Tab Content -->
        {#if activeTab === "overview"}
            <div class="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">
                <Card.Root>
                    <Card.Header class="pb-2">
                        <Card.Description>Status</Card.Description>
                    </Card.Header>
                    <Card.Content>
                        <Badge
                            variant={statusBadgeVariant(campaign.status)}
                            class="text-base"
                        >
                            {campaign.status}
                        </Badge>
                    </Card.Content>
                </Card.Root>
                <Card.Root>
                    <Card.Header class="pb-2">
                        <Card.Description>Auth Mode</Card.Description>
                    </Card.Header>
                    <Card.Content>
                        <Badge
                            variant={campaign.authMode === "PUBLIC"
                                ? "secondary"
                                : "outline"}
                            class="text-base"
                        >
                            {campaign.authMode}
                        </Badge>
                    </Card.Content>
                </Card.Root>
                <Card.Root>
                    <Card.Header class="pb-2">
                        <Card.Description>Created</Card.Description>
                    </Card.Header>
                    <Card.Content>
                        <p class="text-sm font-medium text-foreground">
                            {formatDate(campaign.createdAt)}
                        </p>
                    </Card.Content>
                </Card.Root>
                <Card.Root class="sm:col-span-2 lg:col-span-3">
                    <Card.Header class="pb-2">
                        <Card.Description
                            >Distribution Channels</Card.Description
                        >
                    </Card.Header>
                    <Card.Content>
                        {#if channels.length > 0}
                            <p class="text-sm font-medium text-foreground">
                                {channels.length} channel{channels.length !== 1
                                    ? "s"
                                    : ""} generated
                            </p>
                        {:else}
                            <p class="text-sm text-muted-foreground">
                                {campaign.status === "DRAFT"
                                    ? "Activate the campaign first, then generate channels."
                                    : "No channels generated yet."}
                            </p>
                        {/if}
                    </Card.Content>
                </Card.Root>
            </div>
        {:else if activeTab === "settings"}
            <form
                onsubmit={(e) => {
                    e.preventDefault();
                    saveSettings();
                }}
                class="space-y-6"
            >
                <div class="grid gap-6 md:grid-cols-2">
                    <!-- Security -->
                    <Card.Root>
                        <Card.Header>
                            <Card.Title class="text-base">Security</Card.Title>
                            <Card.Description
                                >Access control and rate limiting</Card.Description
                            >
                        </Card.Header>
                        <Card.Content class="space-y-4">
                            <div class="space-y-2">
                                <Label for="s-password">Password</Label>
                                <Input
                                    id="s-password"
                                    type="password"
                                    placeholder="Optional"
                                    bind:value={settings.password}
                                />
                            </div>
                            <div class="space-y-2">
                                <Label for="s-timeout"
                                    >Session Timeout (min)</Label
                                >
                                <Input
                                    id="s-timeout"
                                    type="number"
                                    min="1"
                                    bind:value={settings.sessionTimeoutMinutes}
                                />
                            </div>

                            <div class="space-y-3 pt-2">
                                <div class="flex items-start justify-between gap-3">
                                    <Label class="flex-1 space-y-1 pr-2">
                                        <span class="block text-sm font-medium leading-5">
                                            Captcha
                                        </span>
                                        <span class="block text-xs font-normal leading-4 text-muted-foreground">
                                            Require human verification
                                        </span>
                                    </Label>
                                    <Switch
                                        class="mt-0.5 shrink-0"
                                        bind:checked={settings.captchaEnabled}
                                    />
                                </div>
                                <div class="flex items-start justify-between gap-3">
                                    <Label class="flex-1 space-y-1 pr-2">
                                        <span class="block text-sm font-medium leading-5">
                                            One response per device
                                        </span>
                                        <span class="block text-xs font-normal leading-4 text-muted-foreground">
                                            Prevent duplicate submissions via cookies
                                        </span>
                                    </Label>
                                    <Switch
                                        class="mt-0.5 shrink-0"
                                        bind:checked={
                                            settings.oneResponsePerDevice
                                        }
                                    />
                                </div>
                                <div class="flex items-start justify-between gap-3">
                                    <Label class="flex-1 space-y-1 pr-2">
                                        <span class="block text-sm font-medium leading-5">
                                            IP Restriction
                                        </span>
                                        <span class="block text-xs font-normal leading-4 text-muted-foreground">
                                            Block repeated IPs
                                        </span>
                                    </Label>
                                    <Switch
                                        class="mt-0.5 shrink-0"
                                        bind:checked={
                                            settings.ipRestrictionEnabled
                                        }
                                    />
                                </div>
                                <div class="flex items-start justify-between gap-3">
                                    <Label class="flex-1 space-y-1 pr-2">
                                        <span class="block text-sm font-medium leading-5">
                                            Email Restriction
                                        </span>
                                        <span class="block text-xs font-normal leading-4 text-muted-foreground">
                                            Require unique email addresses
                                        </span>
                                    </Label>
                                    <Switch
                                        class="mt-0.5 shrink-0"
                                        bind:checked={
                                            settings.emailRestrictionEnabled
                                        }
                                    />
                                </div>
                            </div>
                        </Card.Content>
                    </Card.Root>

                    <!-- User Experience & Limits -->
                    <div class="space-y-6">
                        <Card.Root>
                            <Card.Header>
                                <Card.Title class="text-base"
                                    >User Experience</Card.Title
                                >
                                <Card.Description
                                    >Configure how respondents see the survey</Card.Description
                                >
                            </Card.Header>
                            <Card.Content class="space-y-3">
                                <div class="flex items-center justify-between">
                                    <Label class="leading-none"
                                        >Show Question Numbers</Label
                                    >
                                    <Switch
                                        bind:checked={
                                            settings.showQuestionNumbers
                                        }
                                    />
                                </div>
                                <div class="flex items-center justify-between">
                                    <Label class="leading-none"
                                        >Show Progress Indicator</Label
                                    >
                                    <Switch
                                        bind:checked={
                                            settings.showProgressIndicator
                                        }
                                    />
                                </div>
                                <div class="flex items-center justify-between">
                                    <Label class="leading-none"
                                        >Allow Back Button</Label
                                    >
                                    <Switch
                                        bind:checked={settings.allowBackButton}
                                    />
                                </div>
                            </Card.Content>
                        </Card.Root>

                        <Card.Root>
                            <Card.Header>
                                <Card.Title class="text-base">Limits</Card.Title
                                >
                                <Card.Description
                                    >Time and capacity boundaries</Card.Description
                                >
                            </Card.Header>
                            <Card.Content class="grid gap-3 sm:grid-cols-2">
                                <div class="space-y-2">
                                    <Label for="s-quota">Response Quota</Label>
                                    <Input
                                        id="s-quota"
                                        type="number"
                                        min="0"
                                        placeholder="Unlimited"
                                        bind:value={settings.responseQuota}
                                    />
                                </div>
                                <div class="space-y-2">
                                    <Label for="s-close">Close Date</Label>
                                    <div class="relative">
                                        <input
                                            id="s-close"
                                            type="date"
                                            class="h-10 w-full rounded-lg border border-border bg-background pl-10 pr-20 text-sm outline-none ring-offset-background transition-colors focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2"
                                            bind:value={settings.closeDate}
                                        />
                                        <CalendarDays
                                            class="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground"
                                        />
                                        <div
                                            class="absolute right-2 top-1/2 flex -translate-y-1/2 items-center gap-1"
                                        >
                                            {#if settings.closeDate}
                                                <Button
                                                    type="button"
                                                    size="sm"
                                                    variant="ghost"
                                                    class="h-7 px-2 text-xs"
                                                    onclick={() =>
                                                        (settings.closeDate =
                                                            undefined)}
                                                >
                                                    <X class="h-3.5 w-3.5" />
                                                </Button>
                                            {/if}
                                            <Button
                                                type="button"
                                                size="sm"
                                                variant="outline"
                                                class="h-7 px-2 text-xs"
                                                onclick={openCloseDatePicker}
                                            >
                                                Pick
                                            </Button>
                                        </div>
                                    </div>
                                    <p class="text-xs text-muted-foreground">
                                        Stored as end-of-day UTC for reliable backend parsing.
                                    </p>
                                </div>
                            </Card.Content>
                        </Card.Root>
                    </div>

                    <!-- Data Collection Fields -->
                    <Card.Root class="md:col-span-2">
                        <Card.Header>
                            <div class="flex items-center justify-between">
                                <div>
                                    <Card.Title class="text-base">Data Collection</Card.Title>
                                    <Card.Description>Configure what information respondents must provide before starting the survey.</Card.Description>
                                </div>
                                <Button size="sm" variant="outline" onclick={addDataCollectionField} class="h-8">
                                    <Plus class="mr-2 h-4 w-4" /> Add Field
                                </Button>
                            </div>
                        </Card.Header>
                        <Card.Content>
                            {#if !settings.dataCollectionFields || settings.dataCollectionFields.length === 0}
                                <div class="flex flex-col items-center justify-center rounded-md border border-dashed p-6 text-center text-sm text-muted-foreground">
                                    No data collection fields configured.
                                    <button type="button" class="mt-1 font-medium text-primary hover:underline" onclick={addDataCollectionField}>
                                        Add your first field
                                    </button>
                                </div>
                            {:else}
                                <div class="space-y-4">
                                    {#each settings.dataCollectionFields as field, i}
                                        <div class="flex flex-col gap-4 rounded-md border bg-muted/40 p-4 sm:flex-row sm:items-start">
                                            <div class="pt-2 text-muted-foreground hidden sm:block shrink-0">
                                                <GripVertical class="h-5 w-5" />
                                            </div>
                                            <div class="grid flex-1 gap-4 sm:grid-cols-2 lg:grid-cols-4">
                                                <!-- Field Key -->
                                                <div class="space-y-2">
                                                    <div class="flex items-center gap-1.5">
                                                        <Label for="key-{i}">Key</Label>
                                                        <div 
                                                            title="Internal variable name used for data segregation and analytics. Must be alphanumeric (e.g., student_id, age)." 
                                                            class="text-muted-foreground hover:text-foreground cursor-help"
                                                        >
                                                            <Info class="h-3.5 w-3.5" />
                                                        </div>
                                                    </div>
                                                    <Input
                                                        id="key-{i}"
                                                        bind:value={field.fieldKey}
                                                        placeholder="e.g., student_id"
                                                    />
                                                    <p class="text-[10px] text-muted-foreground">Internal identifier</p>
                                                </div>
                                                
                                                <!-- Display Label -->
                                                <div class="space-y-2">
                                                    <Label for="label-{i}">Display Label</Label>
                                                    <Input
                                                        id="label-{i}"
                                                        bind:value={field.label}
                                                        placeholder="e.g., Your Full Name"
                                                    />
                                                </div>

                                                <!-- Type -->
                                                <div class="space-y-2">
                                                    <Label for="type-{i}">Input Type</Label>
                                                    <select
                                                        id="type-{i}"
                                                        bind:value={field.fieldType}
                                                        class="flex h-9 w-full rounded-md border border-input bg-transparent px-3 py-1 text-sm shadow-sm transition-colors file:border-0 file:bg-transparent file:text-sm file:font-medium placeholder:text-muted-foreground focus-visible:outline-none focus-visible:ring-1 focus-visible:ring-ring disabled:cursor-not-allowed disabled:opacity-50"
                                                    >
                                                        <option value="TEXT">Short Text</option>
                                                        <option value="EMAIL">Email Address</option>
                                                        <option value="PHONE">Phone Number</option>
                                                        <option value="NUMBER">Number</option>
                                                        <option value="TEXTAREA">Long Text (Paragraph)</option>
                                                    </select>
                                                </div>

                                                <!-- Toggles -->
                                                <div class="space-y-2 flex flex-col">
                                                    <Label class="text-transparent hidden sm:block">&nbsp;</Label>
                                                    <div class="flex items-center space-x-2 h-9">
                                                        <Switch id="req-{i}" bind:checked={field.required} />
                                                        <Label for="req-{i}" class="text-sm font-normal cursor-pointer leading-none">Required</Label>
                                                    </div>
                                                </div>
                                            </div>
                                            <div class="flex justify-end sm:flex-col sm:space-y-2">
                                                <Label class="text-transparent hidden sm:block">&nbsp;</Label>
                                                <Button
                                                    variant="ghost"
                                                    size="icon"
                                                    class="h-9 w-9 text-muted-foreground hover:bg-destructive/10 hover:text-destructive shrink-0"
                                                    onclick={() => removeDataCollectionField(i)}
                                                    type="button"
                                                    title="Remove field"
                                                >
                                                    <Trash2 class="h-4 w-4" />
                                                    <span class="sr-only">Remove field</span>
                                                </Button>
                                            </div>
                                        </div>
                                    {/each}
                                </div>
                            {/if}
                        </Card.Content>
                    </Card.Root>

                    <!-- Messages -->
                    <Card.Root class="md:col-span-2">
                        <Card.Header>
                            <Card.Title class="text-base">Messages</Card.Title>
                            <Card.Description
                                >Custom text displayed to respondents</Card.Description
                            >
                        </Card.Header>
                        <Card.Content class="space-y-4">
                            <div class="space-y-2">
                                <Label for="s-start">Start Message</Label>
                                <Input
                                    id="s-start"
                                    placeholder="Welcome to our survey..."
                                    bind:value={settings.startMessage}
                                />
                            </div>
                            <div class="space-y-2">
                                <Label for="s-finish">Finish Message</Label>
                                <Input
                                    id="s-finish"
                                    placeholder="Thank you for participating..."
                                    bind:value={settings.finishMessage}
                                />
                            </div>
                        </Card.Content>
                    </Card.Root>

                </div>

                <div
                    class="flex items-center justify-end gap-3 pt-6 border-t border-border"
                >
                    {#if settingsSaved}
                        <span
                            class="flex items-center gap-1 text-sm text-emerald-500 font-medium"
                        >
                            <Check class="h-4 w-4" />
                            Saved successfully
                        </span>
                    {/if}
                    <Button
                        type="submit"
                        disabled={settingsLoading}
                        class="w-[140px]"
                    >
                        {#if settingsLoading}
                            <span
                                class="mr-2 inline-block h-4 w-4 animate-spin rounded-full border-2 border-current border-t-transparent"
                            ></span>
                        {/if}
                        Save Settings
                    </Button>
                </div>
            </form>
        {:else if activeTab === "theme"}
            <form
                onsubmit={(e) => {
                    e.preventDefault();
                    saveSettings();
                }}
                class="space-y-6"
            >
                <Card.Root>
                    <Card.Header>
                        <Card.Title class="text-base">Theme Studio</Card.Title>
                        <Card.Description>
                            Configure the full survey experience: palette, branding, layout, motion, and advanced overrides.
                        </Card.Description>
                    </Card.Header>
                    <Card.Content class="space-y-6">
                        <div class="rounded-lg border border-border bg-muted/30 p-3 text-xs text-muted-foreground">
                            Start with a template, then enable Pro Theme Mode only when you need manual token editing. Use Campaign Preview after saving to verify the live runtime view.
                        </div>

                        <div class="grid gap-4 lg:grid-cols-2">
                            <div class="space-y-4 rounded-lg border border-border p-4">
                                <div class="space-y-2">
                                    <Label class="text-sm font-semibold">Theme Template</Label>
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
                                    <Label class="text-sm font-semibold">Palette Presets</Label>
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
                                            selectedHelperText="The first font is preferred. The next fonts are used only when the earlier one is unavailable."
                                        />
                                    </div>
                                </div>
                            </div>

                            <div class="space-y-4 rounded-lg border border-border p-4">
                                <div class="flex items-center justify-between rounded-lg border border-border/70 bg-muted/30 px-3 py-2">
                                    <div>
                                        <Label class="text-sm font-semibold">Pro Theme Mode</Label>
                                        <p class="text-xs text-muted-foreground">Unlock manual color token editing and advanced fine-tuning.</p>
                                    </div>
                                    <Switch bind:checked={proThemeMode} />
                                </div>

                                <Label class="text-sm font-semibold">Color Tokens</Label>
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
                                    <p class="text-xs text-muted-foreground">Palette presets remain editable. Turn on Pro Theme Mode to edit raw color tokens.</p>
                                {/if}
                            </div>
                        </div>

                        <div class="grid gap-4 lg:grid-cols-2">
                            <div class="space-y-4 rounded-lg border border-border p-4">
                                <Label class="text-sm font-semibold">Layout & Motion</Label>
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
                            </div>

                            <div class="space-y-4 rounded-lg border border-border p-4">
                                <Label class="text-sm font-semibold">Live Theme Snapshot</Label>
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
                            </div>
                        </div>

                        <div class="grid gap-4 lg:grid-cols-2">
                            <div class="space-y-4 rounded-lg border border-border p-4">
                                <div class="flex items-center justify-between">
                                    <div>
                                        <Label class="text-sm font-semibold">Header Content</Label>
                                        <p class="text-xs text-muted-foreground">Structured branding content shown above the survey.</p>
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
                                        <p class="text-xs text-muted-foreground">Use only when the structured header is not enough.</p>
                                    </div>
                                    <Switch bind:checked={currentTheme.advanced.useCustomHeaderHtml} />
                                </div>
                                {#if currentTheme.advanced.useCustomHeaderHtml}
                                    <Textarea rows={8} class="font-mono text-xs" bind:value={currentTheme.advanced.customHeaderHtml} />
                                {/if}
                            </div>

                            <div class="space-y-4 rounded-lg border border-border p-4">
                                <div class="flex items-center justify-between">
                                    <div>
                                        <Label class="text-sm font-semibold">Footer Content</Label>
                                        <p class="text-xs text-muted-foreground">Structured messaging shown after the survey content.</p>
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
                                        <p class="text-xs text-muted-foreground">Use only when the structured footer is not enough.</p>
                                    </div>
                                    <Switch bind:checked={currentTheme.advanced.useCustomFooterHtml} />
                                </div>
                                {#if currentTheme.advanced.useCustomFooterHtml}
                                    <Textarea rows={8} class="font-mono text-xs" bind:value={currentTheme.advanced.customFooterHtml} />
                                {/if}
                            </div>
                        </div>

                        <div class="space-y-2 rounded-lg border border-border p-4">
                            <Label class="text-sm font-semibold">Advanced CSS Override</Label>
                            <Textarea rows={6} class="font-mono text-xs" placeholder=".survey-shell styles" bind:value={currentTheme.advanced.customCss} />
                        </div>
                    </Card.Content>
                </Card.Root>

                <div class="flex items-center justify-end gap-3 border-t border-border pt-6">
                    {#if settingsSaved}
                        <span class="flex items-center gap-1 text-sm font-medium text-emerald-500">
                            <Check class="h-4 w-4" />
                            Saved successfully
                        </span>
                    {/if}
                    <Button type="submit" disabled={settingsLoading} class="w-[140px]">
                        {#if settingsLoading}
                            <span class="mr-2 inline-block h-4 w-4 animate-spin rounded-full border-2 border-current border-t-transparent"></span>
                        {/if}
                        Save Theme
                    </Button>
                </div>
            </form>
        {:else if activeTab === "distribution"}
            {#if channels.length === 0}
                <Card.Root
                    class="flex flex-col items-center justify-center py-16 text-center"
                >
                    <Card.Content>
                        <div
                            class="mx-auto mb-4 flex h-16 w-16 items-center justify-center rounded-2xl bg-muted"
                        >
                            <Share2 class="h-8 w-8 text-muted-foreground" />
                        </div>
                        <h3 class="text-lg font-semibold text-foreground">
                            No channels generated
                        </h3>
                        <p class="mt-1 text-sm text-muted-foreground">
                            {campaign.status === "DRAFT"
                                ? "Activate the campaign first to generate distribution channels."
                                : 'Click "Generate Channels" to create distribution links.'}
                        </p>
                        {#if campaign.status === "ACTIVE"}
                            <Button
                                class="mt-4"
                                onclick={distribute}
                                disabled={distributeLoading}
                            >
                                <Send class="mr-2 h-4 w-4" />
                                Generate Channels
                            </Button>
                        {/if}
                    </Card.Content>
                </Card.Root>
            {:else}
                <div class="space-y-3">
                    {#each channels as channel (channel.id)}
                        <Card.Root>
                            <Card.Content
                                class="flex items-center justify-between gap-4 py-4"
                            >
                                <div class="min-w-0 flex-1">
                                    <Badge variant="secondary" class="mb-1">
                                        {channelLabel(channel.channelType)}
                                    </Badge>
                                    <p
                                        class="mt-1 truncate font-mono text-xs text-muted-foreground"
                                    >
                                        {channel.channelValue}
                                    </p>
                                </div>
                                <Button
                                    variant="outline"
                                    size="sm"
                                    onclick={() =>
                                        copyToClipboard(
                                            channel.channelValue,
                                            channel.id,
                                        )}
                                >
                                    {#if copiedId === channel.id}
                                        <Check
                                            class="mr-1 h-4 w-4 text-emerald-500"
                                        />
                                        Copied
                                    {:else}
                                        <Copy class="mr-1 h-4 w-4" />
                                        Copy
                                    {/if}
                                </Button>
                            </Card.Content>
                        </Card.Root>
                    {/each}
                </div>
            {/if}
        {/if}
    </div>
{/if}

<style>
    .no-scrollbar::-webkit-scrollbar {
        display: none;
    }
    .no-scrollbar {
        -ms-overflow-style: none;
        scrollbar-width: none;
    }

</style>

<!-- 🎉 Confetti Celebration -->
{#if showConfetti}
    <Confetti
        fire={showConfetti}
        showBanner={true}
        title={confettiTitle}
        message={confettiMessage}
        particleCount={150}
        spread={80}
        startVelocity={50}
        duration={4000}
        colors={['#3b82f6', '#8b5cf6', '#ec4899', '#f59e0b']}
        onComplete={() => (showConfetti = false)}
    />
{/if}
