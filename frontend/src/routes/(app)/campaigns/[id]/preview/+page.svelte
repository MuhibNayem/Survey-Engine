<script lang="ts">
    import api from "$lib/api";
    import { onMount } from "svelte";
    import { page } from "$app/state";
    import { goto } from "$app/navigation";
    import * as Card from "$lib/components/ui/card";
    import { Button } from "$lib/components/ui/button";
    import { Input } from "$lib/components/ui/input";
    import { Label } from "$lib/components/ui/label";
    import { Textarea } from "$lib/components/ui/textarea";
    import * as StarRating from "$lib/components/ui/star-rating";
    import { Badge } from "$lib/components/ui/badge";
    import { Skeleton } from "$lib/components/ui/skeleton";
    import { ArrowLeft, RefreshCw, Play, ChevronLeft, ChevronRight } from "lucide-svelte";
    import "$lib/styles/survey-theme.css";
    import type {
        CampaignPreviewResponse,
        QuestionType,
        SurveyThemeConfig,
    } from "$lib/types";

    type AnswerValue = string | number | string[] | null | undefined;

    type PreviewQuestion = {
        id: string;
        questionId: string;
        text: string;
        type: QuestionType;
        mandatory: boolean;
        sortOrder: number;
        optionConfig?: string;
        answerConfig?: string;
        maxScore: number;
    };

    type PreviewPage = {
        id: string;
        title: string;
        sortOrder: number;
        questions: PreviewQuestion[];
    };

    const campaignId = $derived(page.params.id);

    let loading = $state(true);
    let error = $state<string | null>(null);
    let stage = $state<"intro" | "form" | "complete">("intro");
    let currentPageIndex = $state(0);
    let campaign = $state<CampaignPreviewResponse | null>(null);
    let answers = $state<Record<string, AnswerValue>>({});
    let remarks = $state<Record<string, string>>({});
    let errors = $state<Record<string, string>>({});
    let respondentMetadata = $state<Record<string, string | number | null | undefined>>({});
    let logoLoadFailed = $state(false);

    const resolvedPages = $derived(
        campaign
            ? [...campaign.pages]
                  .sort((a, b) => a.sortOrder - b.sortOrder)
                  .map((p) => ({
                      id: p.id,
                      title: p.title,
                      sortOrder: p.sortOrder,
                      questions: [...p.questions]
                          .sort((a, b) => a.sortOrder - b.sortOrder)
                          .map((q) => {
                              return {
                                  id: q.id,
                                  questionId: q.questionId,
                                  text: q.text ?? `Question ${q.questionId}`,
                                  type: q.type ?? "RATING_SCALE",
                                  mandatory: q.mandatory,
                                  sortOrder: q.sortOrder,
                                  optionConfig: q.optionConfig,
                                  answerConfig: q.answerConfig,
                                  maxScore: q.maxScore ?? 5,
                              } satisfies PreviewQuestion;
                          }),
                  } satisfies PreviewPage))
            : [],
    );

    const currentPage = $derived(resolvedPages[currentPageIndex] ?? null);
    const totalPages = $derived(resolvedPages.length);
    
    const totalQuestions = $derived(
        resolvedPages.reduce((sum, p) => sum + p.questions.length, 0)
    );
    const answeredQuestions = $derived(
        resolvedPages.reduce(
            (sum, p) => sum + p.questions.filter((q) => isAnswered(q)).length, 
            0
        )
    );
    const progressPercent = $derived(
        totalQuestions === 0 
            ? 0 
            : Math.round((answeredQuestions / totalQuestions) * 100),
    );

    const DEFAULT_THEME: SurveyThemeConfig = {
        templateKey: "aurora-premium",
        paletteKey: "ocean-aurora",
        palette: {
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
        motion: { animationPreset: "subtle" },
        header: {
            enabled: true,
            eyebrow: "Confidential Evaluation Ledger",
            title: "",
            subtitle: "",
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
    };

    function getTheme(): SurveyThemeConfig {
        const theme = campaign?.theme;
        if (!theme) {
            return {
                ...DEFAULT_THEME,
                header: {
                    ...DEFAULT_THEME.header,
                    title: campaign?.campaignName ?? "Survey Preview",
                    subtitle: campaign?.surveyTitle ?? "Share your feedback",
                },
            };
        }
        return {
            ...DEFAULT_THEME,
            ...theme,
            palette: { ...DEFAULT_THEME.palette, ...theme.palette },
            branding: { ...DEFAULT_THEME.branding, ...theme.branding },
            layout: { ...DEFAULT_THEME.layout, ...theme.layout },
            motion: { ...DEFAULT_THEME.motion, ...theme.motion },
            header: {
                ...DEFAULT_THEME.header,
                title: campaign?.campaignName ?? "Survey Preview",
                subtitle: campaign?.surveyTitle ?? "Share your feedback",
                ...theme.header,
            },
            footer: { ...DEFAULT_THEME.footer, ...theme.footer },
            advanced: { ...DEFAULT_THEME.advanced, ...theme.advanced },
        };
    }

    function previewThemeStyle(): string {
        const theme = getTheme();
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
            `--preview-header-bg:${theme.palette.headerBackground}`,
            `--preview-header-text:${theme.palette.headerText}`,
            `--preview-footer-bg:${theme.palette.footerBackground}`,
            `--preview-footer-text:${theme.palette.footerText}`,
            `--preview-font:${theme.branding.fontFamily || DEFAULT_THEME.branding.fontFamily}`,
        ].join(";");
    }

    const activeLogoUrl = $derived(getTheme().branding.logoUrl?.trim() || "");
    const shouldRenderLogo = $derived(Boolean(activeLogoUrl) && !logoLoadFailed);

    $effect(() => {
        activeLogoUrl;
        logoLoadFailed = false;
    });

    function previewShellClass(): string {
        const theme = getTheme();
        return `theme-studio-preview theme-studio-preview--live theme-studio-preview--${theme.layout.contentWidth} theme-studio-preview--motion-${theme.motion.animationPreset}`;
    }

    function previewHeaderClass(): string {
        return `theme-studio-preview__header theme-studio-preview__header--${getTheme().layout.headerStyle}`;
    }

    function previewPanelClass(): string {
        return `theme-studio-preview__panel theme-studio-preview__panel--${getTheme().layout.sectionStyle}`;
    }

    function previewCardClass(): string {
        return `theme-studio-preview__card theme-studio-preview__card--${getTheme().layout.questionCardStyle}`;
    }

    function previewDividerClass(): string {
        return `theme-studio-preview__divider theme-studio-preview__divider--${getTheme().layout.categorySeparatorStyle}`;
    }

    function previewFooterClass(): string {
        return `theme-studio-preview__footer theme-studio-preview__footer--${getTheme().layout.footerStyle}`;
    }

    function parseAnswerConfig(raw?: string): Record<string, unknown> {
        if (!raw) return {};
        try {
            const parsed = JSON.parse(raw);
            return parsed && typeof parsed === "object" ? parsed : {};
        } catch {
            return {};
        }
    }

    function parseOptionConfig(raw?: string): Record<string, unknown> {
        if (!raw) return {};
        try {
            const parsed = JSON.parse(raw);
            return parsed && typeof parsed === "object" ? parsed : {};
        } catch {
            return {};
        }
    }

    function getOptions(question: PreviewQuestion): string[] {
        const config = parseOptionConfig(question.optionConfig);
        const rawOptions = config.options;
        if (!Array.isArray(rawOptions)) return [];
        return rawOptions
            .map((v) =>
                typeof v === "string"
                    ? v.trim()
                    : String(v?.value ?? "").trim(),
            )
            .filter((v) => v.length > 0);
    }

    function getRatingValues(question: PreviewQuestion): number[] {
        const config = parseAnswerConfig(question.answerConfig);
        const min = Number(config.min ?? 1);
        const max = Number(config.max ?? question.maxScore ?? 5);
        const step = Number(config.step ?? 1);
        if (!Number.isFinite(min) || !Number.isFinite(max) || !Number.isFinite(step) || step <= 0 || max < min) {
            return [1, 2, 3, 4, 5];
        }
        const values: number[] = [];
        let current = min;
        let guard = 0;
        while (current <= max + 1e-9 && guard < 100) {
            values.push(Number(current.toFixed(2)));
            current += step;
            guard += 1;
        }
        return values.length > 0 ? values : [1, 2, 3, 4, 5];
    }

    function getRatingDisplayMode(question: PreviewQuestion): "NUMBERS" | "STARS" {
        const config = parseOptionConfig(question.optionConfig);
        const rawMode = String(config.displayMode ?? "NUMBERS").trim().toUpperCase();
        return rawMode === "STARS" ? "STARS" : "NUMBERS";
    }

    function canUseStarRating(question: PreviewQuestion): boolean {
        const config = parseAnswerConfig(question.answerConfig);
        const min = Number(config.min ?? 1);
        const max = Number(config.max ?? question.maxScore ?? 5);
        const step = Number(config.step ?? 1);
        return Number.isInteger(min) && Number.isInteger(max) && Number.isInteger(step) && min === 1 && step === 1;
    }

    function getNumericAnswer(questionId: string): number | undefined {
        const value = answers[questionId];
        return typeof value === "number" ? value : undefined;
    }

    function setRatingAnswer(questionId: string, value: number): void {
        answers[questionId] = value;
    }

    function isAnswered(question: PreviewQuestion): boolean {
        const value = answers[question.questionId];
        if (question.type === "MULTIPLE_CHOICE" || question.type === "RANK") {
            return Array.isArray(value) && value.length > 0;
        }
        if (typeof value === "number") return true;
        if (typeof value === "string") return value.trim().length > 0;
        return false;
    }

    function ensureRankOrder(question: PreviewQuestion): string[] {
        const options = getOptions(question);
        const existing = answers[question.questionId];
        if (Array.isArray(existing) && existing.length > 0) {
            return existing.map((x) => String(x));
        }
        answers[question.questionId] = [...options];
        return options;
    }

    function getArrayAnswer(questionId: string): string[] {
        const existing = answers[questionId];
        return Array.isArray(existing) ? existing.map((v) => String(v)) : [];
    }

    function isSingleSelected(questionId: string, option: string): boolean {
        return answers[questionId] === option;
    }

    function setSingleChoice(questionId: string, option: string) {
        answers[questionId] = option;
    }

    function isMultiSelected(questionId: string, option: string): boolean {
        return getArrayAnswer(questionId).includes(option);
    }

    function toggleMultiChoice(questionId: string, option: string) {
        const current = getArrayAnswer(questionId);
        answers[questionId] = current.includes(option)
            ? current.filter((v) => v !== option)
            : [...current, option];
    }

    function optionCardClass(selected: boolean): string {
        return `group flex w-full items-center justify-between rounded-xl border px-4 py-3 text-left text-sm transition-all ${
            selected
                ? "border-primary bg-primary/10 text-primary shadow-sm"
                : "border-border bg-background text-foreground hover:border-primary/40 hover:bg-primary/5"
        }`;
    }

    function radioDotClass(selected: boolean): string {
        return `inline-flex h-5 w-5 shrink-0 items-center justify-center rounded-full border ${
            selected
                ? "border-primary bg-primary"
                : "border-border bg-background"
        }`;
    }

    function checkBoxClass(selected: boolean): string {
        return `inline-flex h-5 w-5 shrink-0 items-center justify-center rounded-md border text-xs font-bold ${
            selected
                ? "border-primary bg-primary text-primary-foreground"
                : "border-border bg-background text-transparent"
        }`;
    }

    function ratingButtonClass(selected: boolean): string {
        return `inline-flex h-9 min-w-10 items-center justify-center rounded-lg border px-3 text-sm font-medium transition ${
            selected
                ? "border-primary bg-primary text-primary-foreground"
                : "border-border bg-background text-foreground hover:border-primary/40 hover:bg-primary/5"
        }`;
    }

    function moveRankOption(question: PreviewQuestion, index: number, direction: -1 | 1) {
        const ordered = [...ensureRankOrder(question)];
        const newIndex = index + direction;
        if (newIndex < 0 || newIndex >= ordered.length) return;
        const tmp = ordered[index];
        ordered[index] = ordered[newIndex];
        ordered[newIndex] = tmp;
        answers[question.questionId] = ordered;
    }

    function questionSerial(pageIdx: number, questionIdx: number): number {
        const before = resolvedPages
            .slice(0, pageIdx)
            .reduce((sum, p) => sum + p.questions.length, 0);
        return before + questionIdx + 1;
    }

    function normalizeUiLabel(value?: string): string {
        return (value ?? "")
            .trim()
            .toLowerCase()
            .replace(/\bv\d+\b/g, "")
            .replace(/[^a-z0-9]+/g, " ")
            .replace(/\s+/g, " ")
            .trim();
    }

    function hasCustomHeader(): boolean {
        return Boolean(getTheme().advanced.useCustomHeaderHtml && getTheme().advanced.customHeaderHtml?.trim());
    }

    function hasCustomFooter(): boolean {
        return Boolean(getTheme().advanced.useCustomFooterHtml && getTheme().advanced.customFooterHtml?.trim());
    }

    function customHeaderHtml(): string {
        return getTheme().advanced.customHeaderHtml || campaign?.headerHtml || "";
    }

    function customFooterHtml(): string {
        return getTheme().advanced.customFooterHtml || campaign?.footerHtml || "";
    }

    function getRespondentMetadataValue(fieldKey: string): string {
        const value = respondentMetadata[fieldKey];
        if (value == null) return "";
        return String(value).trim();
    }

    function validateRespondentFields(): boolean {
        const nextErrors: Record<string, string> = {};
        if (campaign?.dataCollectionFields) {
            for (const field of campaign.dataCollectionFields) {
                if (!field.enabled) continue;
                
                const val = getRespondentMetadataValue(field.fieldKey);
                if (field.required && val.length === 0) {
                    nextErrors["meta." + field.fieldKey] = `${field.label} is required`;
                } else if (val.length > 0 && field.fieldType === 'EMAIL') {
                    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(val)) {
                        nextErrors["meta." + field.fieldKey] = "Enter a valid email";
                    }
                }
            }
        }
        
        errors = { ...errors, ...nextErrors };
        return Object.keys(nextErrors).length === 0;
    }

    function validateCurrentPage(): boolean {
        if (!currentPage) return false;
        const nextErrors: Record<string, string> = {};
        for (const question of currentPage.questions) {
            if (question.mandatory && !isAnswered(question)) {
                nextErrors[question.questionId] = "This question is required";
            }
        }
        errors = { ...errors, ...nextErrors };
        return Object.keys(nextErrors).length === 0;
    }

    function goNext() {
        errors = {}; // clear global errors before validation
        const respondentValid = validateRespondentFields();
        const pageValid = validateCurrentPage();
        if (!respondentValid || !pageValid) return;
        if (currentPageIndex >= totalPages - 1) {
            stage = "complete";
            return;
        }
        currentPageIndex += 1;
    }

    function goBack() {
        if (currentPageIndex <= 0) return;
        currentPageIndex -= 1;
        errors = {};
    }

    function restartPreview() {
        stage = "intro";
        currentPageIndex = 0;
        answers = {};
        errors = {};
        respondentMetadata = {};
        remarks = {};
    }

    async function load() {
        loading = true;
        error = null;
        try {
            const previewRes = await api.get<CampaignPreviewResponse>(`/campaigns/${campaignId}/preview`);
            campaign = previewRes.data;
        } catch (err: unknown) {
            const axiosErr = err as { response?: { data?: { message?: string } } };
            error = axiosErr?.response?.data?.message ?? "Failed to load campaign preview";
        } finally {
            loading = false;
        }
    }

    onMount(load);
</script>

<svelte:head>
    <title>Campaign Preview — Survey Engine</title>
</svelte:head>

{#if loading}
    <Card.Root class="max-w-3xl mx-auto mt-8">
        <Card.Header>
            <div class="space-y-2">
                <Skeleton class="h-8 w-[200px]" />
                <Skeleton class="h-4 w-[300px]" />
            </div>
        </Card.Header>
        <Card.Content class="space-y-6">
            <div class="space-y-4">
                {#each Array(4) as _}
                    <div class="space-y-2">
                        <Skeleton class="h-4 w-[180px]" />
                        <Skeleton class="h-10 w-full" />
                    </div>
                {/each}
            </div>
            <div class="flex justify-end gap-2">
                <Skeleton class="h-10 w-[100px]" />
                <Skeleton class="h-10 w-[80px]" />
            </div>
        </Card.Content>
    </Card.Root>
{:else if error || !campaign}
    <Card.Root class="max-w-3xl mx-auto mt-8">
        <Card.Header>
            <Card.Title>Preview Unavailable</Card.Title>
            <Card.Description>{error ?? "Unable to render preview for this campaign."}</Card.Description>
        </Card.Header>
        <Card.Content>
            <Button variant="outline" onclick={() => goto(`/campaigns/${campaignId}`)}>
                <ArrowLeft class="mr-2 h-4 w-4" />
                Back to Campaign
            </Button>
        </Card.Content>
    </Card.Root>
{:else}
    <div class="space-y-6" style={previewThemeStyle()}>
        <div class="flex flex-col gap-3 sm:flex-row sm:items-center sm:justify-between">
            <div class="flex items-center gap-2">
                <Button variant="ghost" size="sm" onclick={() => goto(`/campaigns/${campaignId}`)}>
                    <ArrowLeft class="h-4 w-4" />
                </Button>
                <div>
                    <h1 class="text-2xl font-bold tracking-tight">Campaign Preview</h1>
                    <p class="text-sm text-muted-foreground">Admin-only simulation of responder experience</p>
                </div>
            </div>
            <div class="flex gap-2">
                <Badge variant="secondary">Preview Mode</Badge>
                <Button variant="outline" size="sm" onclick={restartPreview}>
                    <RefreshCw class="mr-2 h-4 w-4" />
                    Restart
                </Button>
            </div>
        </div>

        <div class={previewShellClass()} style={previewThemeStyle()}>
            {#if !hasCustomHeader()}
                <section
                    class={previewHeaderClass()}
                    data-align={getTheme().layout.headerAlignment}
                    data-logo-position={getTheme().branding.logoPosition}
                >
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
                    <div class="theme-studio-preview__eyebrow">{getTheme().header.eyebrow || getTheme().branding.brandLabel}</div>
                    <h3>{getTheme().header.title || campaign.campaignName}</h3>
                    <p>{getTheme().header.subtitle || campaign.surveyTitle}</p>
                    {#if getTheme().header.note}
                        <div class="theme-studio-preview__note">{getTheme().header.note}</div>
                    {/if}
                    {#if stage === "form" && campaign.showProgressIndicator}
                        <div class="mt-4 space-y-1">
                            <div class="h-2 w-full overflow-hidden rounded-full bg-white/20">
                                <div class="h-full rounded-full transition-all" style={`width:${progressPercent}%;background:linear-gradient(90deg,var(--preview-primary-text),color-mix(in srgb, var(--preview-primary-text) 78%, var(--preview-accent)))`}></div>
                            </div>
                            <p class="text-xs opacity-90">{progressPercent}% completed • Page {currentPageIndex + 1} / {totalPages}</p>
                        </div>
                    {/if}
                </section>
            {/if}

            <div>
                {#if hasCustomHeader()}
                    <section class="theme-studio-preview__custom-html theme-studio-preview__custom-html--header">
                        <div class="campaign-branding-content">{@html customHeaderHtml()}</div>
                    </section>
                {/if}

                <section class="space-y-6 p-5 sm:p-7">
                    {#if stage === "intro"}
                    <div class={previewCardClass()}>
                        <div class="theme-studio-preview__section-title">Invitation</div>
                        {#if campaign.startMessage}
                            <p class="text-sm leading-6 text-foreground whitespace-pre-wrap">{campaign.startMessage}</p>
                        {:else}
                            <p class="text-sm leading-6 text-muted-foreground">No custom start message configured.</p>
                        {/if}
                        <Button onclick={() => (stage = "form")}>
                            <Play class="mr-2 h-4 w-4" />
                            Start Preview
                        </Button>
                    </div>
                    {:else if stage === "complete"}
                    <div class={previewCardClass()}>
                        <div class="theme-studio-preview__section-title">Submission Received</div>
                        <h3 class="text-lg font-semibold">Submission Complete (Preview)</h3>
                        {#if campaign.finishMessage}
                            <p class="text-sm leading-6 text-foreground whitespace-pre-wrap">{campaign.finishMessage}</p>
                        {:else}
                            <p class="text-sm leading-6 text-muted-foreground">No custom finish message configured.</p>
                        {/if}
                        <div class="flex gap-2">
                            <Button variant="outline" onclick={restartPreview}>Preview Again</Button>
                            <Button onclick={() => goto(`/campaigns/${campaignId}`)}>Back to Campaign</Button>
                        </div>
                    </div>
                    {:else if currentPage}
                    <div class="space-y-6">
                        {#if campaign.dataCollectionFields?.some(f => f.enabled)}
                            <section class={previewPanelClass()}>
                                <div class="theme-studio-preview__section-title">Responder Information</div>
                                <div class={previewDividerClass()}></div>
                                <div class="grid gap-3 sm:grid-cols-2">
                                    {#each campaign.dataCollectionFields as field}
                                        {#if field.enabled}
                                            <div class={field.fieldType === 'TEXTAREA' ? "space-y-1 sm:col-span-2" : "space-y-1"}>
                                                <Label for="meta-{field.fieldKey}">
                                                    {field.label} {#if field.required}<span class="text-destructive">*</span>{/if}
                                                </Label>
                                                
                                                {#if field.fieldType === 'TEXTAREA'}
                                                    <Input
                                                        id="meta-{field.fieldKey}"
                                                        bind:value={respondentMetadata[field.fieldKey]}
                                                    />
                                                {:else if field.fieldType === 'NUMBER'}
                                                    <Input
                                                        id="meta-{field.fieldKey}"
                                                        type="number"
                                                        bind:value={respondentMetadata[field.fieldKey]}
                                                    />
                                                {:else if field.fieldType === 'PHONE'}
                                                    <Input
                                                        id="meta-{field.fieldKey}"
                                                        type="tel"
                                                        bind:value={respondentMetadata[field.fieldKey]}
                                                    />
                                                {:else if field.fieldType === 'EMAIL'}
                                                    <Input
                                                        id="meta-{field.fieldKey}"
                                                        type="email"
                                                        bind:value={respondentMetadata[field.fieldKey]}
                                                    />
                                                {:else}
                                                    <Input
                                                        id="meta-{field.fieldKey}"
                                                        type="text"
                                                        bind:value={respondentMetadata[field.fieldKey]}
                                                    />
                                                {/if}
                                                
                                                {#if errors["meta." + field.fieldKey]}
                                                    <p class="text-xs text-destructive">
                                                        {errors["meta." + field.fieldKey]}
                                                    </p>
                                                {/if}
                                            </div>
                                        {/if}
                                    {/each}
                                </div>
                            </section>
                        {/if}

                        <section class={previewPanelClass()}>
                            <div class="theme-studio-preview__section-title">{currentPage.title}</div>
                            <div class={previewDividerClass()}></div>
                            <div class="space-y-6">
                                {#each currentPage.questions as question, qIdx}
                                    <div class={`${previewCardClass()} space-y-3`}>
                                        <div class="space-y-1">
                                            <p class="font-medium leading-6">
                                                {#if campaign.showQuestionNumbers}
                                                    <span class="text-muted-foreground mr-1">
                                                        {questionSerial(currentPageIndex, qIdx)}
                                                    </span>
                                                {/if}
                                                {question.text}
                                                {#if question.mandatory}
                                                    <span class="text-destructive ml-1">*</span>
                                                {/if}
                                            </p>
                                            <p class="text-xs text-muted-foreground">Type: {question.type.replace("_", " ")}</p>
                                        </div>

                                        {#if question.type === "SINGLE_CHOICE"}
                                            <div class="space-y-2">
                                                {#each getOptions(question) as option}
                                                    <button
                                                        type="button"
                                                        class={optionCardClass(
                                                            isSingleSelected(
                                                                question.questionId,
                                                                option,
                                                            ),
                                                        )}
                                                        onclick={() =>
                                                            setSingleChoice(
                                                                question.questionId,
                                                                option,
                                                            )}
                                                    >
                                                        <span class="pr-3 font-medium">
                                                            {option}
                                                        </span>
                                                        <span class={radioDotClass(
                                                            isSingleSelected(
                                                                question.questionId,
                                                                option,
                                                            ),
                                                        )}>
                                                            {#if isSingleSelected(question.questionId, option)}
                                                                <span class="h-2.5 w-2.5 rounded-full bg-primary-foreground"></span>
                                                            {/if}
                                                        </span>
                                                    </button>
                                                {/each}
                                            </div>
                                        {:else if question.type === "MULTIPLE_CHOICE"}
                                            <div class="space-y-2">
                                                {#each getOptions(question) as option}
                                                    <button
                                                        type="button"
                                                        class={optionCardClass(
                                                            isMultiSelected(
                                                                question.questionId,
                                                                option,
                                                            ),
                                                        )}
                                                        onclick={() =>
                                                            toggleMultiChoice(
                                                                question.questionId,
                                                                option,
                                                            )}
                                                    >
                                                        <span class="pr-3 font-medium">
                                                            {option}
                                                        </span>
                                                        <span class={checkBoxClass(
                                                            isMultiSelected(
                                                                question.questionId,
                                                                option,
                                                            ),
                                                        )}>
                                                            ✓
                                                        </span>
                                                    </button>
                                                {/each}
                                            </div>
                                        {:else if question.type === "RANK"}
                                            <div class="space-y-2">
                                                {#each ensureRankOrder(question) as option, idx}
                                                    <div class="flex items-center justify-between rounded-md border border-border px-3 py-2">
                                                        <div class="text-sm">{idx + 1}. {option}</div>
                                                        <div class="flex gap-1">
                                                            <Button
                                                                type="button"
                                                                variant="outline"
                                                                size="sm"
                                                                disabled={idx === 0}
                                                                onclick={() => moveRankOption(question, idx, -1)}
                                                            >
                                                                <ChevronLeft class="h-4 w-4" />
                                                            </Button>
                                                            <Button
                                                                type="button"
                                                                variant="outline"
                                                                size="sm"
                                                                disabled={idx === ensureRankOrder(question).length - 1}
                                                                onclick={() => moveRankOption(question, idx, 1)}
                                                            >
                                                                <ChevronRight class="h-4 w-4" />
                                                            </Button>
                                                        </div>
                                                    </div>
                                                {/each}
                                            </div>
                                        {:else}
                                            <div class="flex flex-wrap gap-2">
                                                {#if getRatingDisplayMode(question) === "STARS" && canUseStarRating(question)}
                                                    <StarRating.Root
                                                        value={getNumericAnswer(question.questionId)}
                                                        onValueChange={(value) => setRatingAnswer(question.questionId, value)}
                                                        max={getRatingValues(question).length}
                                                    >
                                                        {#snippet children({ items })}
                                                            {#each items as item (item.index)}
                                                                <StarRating.Star {...item} class="text-yellow-400" />
                                                            {/each}
                                                        {/snippet}
                                                    </StarRating.Root>
                                                {:else}
                                                    {#each getRatingValues(question) as value}
                                                        <button
                                                            type="button"
                                                            class={ratingButtonClass(
                                                                answers[
                                                                    question.questionId
                                                                ] === value,
                                                            )}
                                                            onclick={() =>
                                                                (answers[
                                                                    question.questionId
                                                                ] = value)}
                                                        >
                                                            {value}
                                                        </button>
                                                    {/each}
                                                {/if}
                                            </div>
                                        {/if}

                                        <div class="space-y-1">
                                            <Label for={`preview-remark-${question.questionId}`}>
                                                Remarks <span class="text-muted-foreground">(optional)</span>
                                            </Label>
                                            <Textarea
                                                id={`preview-remark-${question.questionId}`}
                                                rows={3}
                                                placeholder="Preview optional respondent comment"
                                                bind:value={remarks[question.questionId]}
                                            />
                                        </div>

                                        {#if errors[question.questionId]}
                                            <p class="text-xs text-destructive">{errors[question.questionId]}</p>
                                        {/if}
                                    </div>
                                {/each}
                            </div>
                        </section>

                        <div class="flex items-center justify-between">
                            {#if campaign.allowBackButton}
                                <Button
                                    type="button"
                                    variant="outline"
                                    disabled={currentPageIndex === 0}
                                    onclick={goBack}
                                >
                                    Back
                                </Button>
                            {:else}
                                <span></span>
                            {/if}

                            <Button type="button" onclick={goNext}>
                                {currentPageIndex === totalPages - 1 ? "Finish Preview" : "Next"}
                            </Button>
                        </div>
                        {#if hasCustomFooter()}
                            <section class="theme-studio-preview__custom-html theme-studio-preview__custom-html--footer theme-studio-preview__custom-html--footer-edge">
                                <div class="campaign-branding-content">{@html customFooterHtml()}</div>
                            </section>
                        {:else if getTheme().footer.enabled}
                            <section class={`${previewFooterClass()} theme-studio-preview__footer--edge`} data-align={getTheme().layout.footerAlignment}>
                                <p class="theme-studio-preview__footer-line1">{getTheme().footer.line1}</p>
                                {#if getTheme().footer.line2}
                                    <p class="theme-studio-preview__footer-line2">{getTheme().footer.line2}</p>
                                {/if}
                                {#if getTheme().footer.legal}
                                    <p class="theme-studio-preview__footer-legal">{getTheme().footer.legal}</p>
                                {/if}
                            </section>
                        {/if}
                    </div>
                    {/if}
                </section>

            </div>
        </div>
    </div>
{/if}

<style>
    .campaign-branding-content {
        padding: 1.25rem 1.5rem;
    }

    .campaign-branding-content :global(h1),
    .campaign-branding-content :global(h2),
    .campaign-branding-content :global(h3),
    .campaign-branding-content :global(h4),
    .campaign-branding-content :global(h5),
    .campaign-branding-content :global(h6) {
        margin: 0;
        color: inherit;
        letter-spacing: -0.01em;
    }

    .campaign-branding-content :global(p) {
        margin: 0.45rem 0 0;
        color: inherit;
        line-height: 1.55;
    }
</style>
