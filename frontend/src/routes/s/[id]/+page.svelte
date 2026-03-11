<script lang="ts">
    import api from "$lib/api";
    import { onMount } from "svelte";
    import { page } from "$app/state";
    import { goto, replaceState } from "$app/navigation";
    import * as Card from "$lib/components/ui/card";
    import { Button } from "$lib/components/ui/button";
    import { Input } from "$lib/components/ui/input";
    import { Label } from "$lib/components/ui/label";
    import { Textarea } from "$lib/components/ui/textarea";
    import * as StarRating from "$lib/components/ui/star-rating";
    import { Skeleton } from "$lib/components/ui/skeleton";
    import { ChevronLeft, ChevronRight, LogOut, Play, Save } from "lucide-svelte";
    import "$lib/styles/survey-theme.css";
    import type {
        CampaignPreviewResponse,
        QuestionType,
        SurveyResponseResponse,
        SurveyThemeConfig,
    } from "$lib/types";

    type AnswerValue = string | number | string[] | null | undefined;

    type PreviewQuestion = {
        id: string;
        questionId: string;
        questionVersionId: string;
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
        categories: PreviewCategory[];
        questions: PreviewQuestion[];
    };

    type PreviewCategory = {
        categoryVersionId: string;
        versionNumber?: number;
        name: string;
        description?: string;
        weightPercentage?: number;
        sortOrder?: number;
        questions: PreviewQuestion[];
    };

    type OidcStartResponse = {
        authorizationUrl: string;
        state: string;
        expiresAt: string;
    };

    type DraftSession = {
        responseId: string;
        pageIndex: number;
    };

    type ResponderSessionStatusResponse = {
        authenticated: boolean;
        email?: string | null;
    };

    const campaignId = $derived(page.params.id);

    let loading = $state(true);
    let error = $state<string | null>(null);
    let stage = $state<"intro" | "form" | "complete">("intro");
    let submitLoading = $state(false);
    let submitError = $state<string | null>(null);
    let authLoading = $state(false);
    let authError = $state<string | null>(null);
    let logoutLoading = $state(false);
    let currentPageIndex = $state(0);
    let campaign = $state<CampaignPreviewResponse | null>(null);
    let draftResponseId = $state<string | null>(null);
    let draftSaving = $state(false);
    let draftMessage = $state<string | null>(null);
    let responderToken = $state<string | null>(null);
    let responderAccessCode = $state<string | null>(null);
    let privateSessionAuthenticated = $state(false);
    let logoLoadFailed = $state(false);
    let answers = $state<Record<string, AnswerValue>>({});
    let remarks = $state<Record<string, string>>({});
    let errors = $state<Record<string, string>>({});
    let respondentMetadata = $state<Record<string, string | number | null | undefined>>({});

    const resolvedPages = $derived(
        campaign
            ? [...campaign.pages]
                  .sort((a, b) => a.sortOrder - b.sortOrder)
                  .map((p) => ({
                      id: p.id,
                      title: p.title,
                      sortOrder: p.sortOrder,
                      categories: [...(p.categories ?? [])]
                          .sort((a, b) => (a.sortOrder ?? 0) - (b.sortOrder ?? 0))
                          .map((category) => ({
                              categoryVersionId: category.categoryVersionId,
                              versionNumber: category.versionNumber,
                              name: category.name,
                              description: category.description,
                              weightPercentage: category.weightPercentage,
                              sortOrder: category.sortOrder,
                              questions: [...category.questions]
                                  .sort((a, b) => a.sortOrder - b.sortOrder)
                                  .map((q) => ({
                                      id: q.id,
                                      questionId: q.questionId,
                                      questionVersionId: q.questionVersionId,
                                      text: q.text ?? `Question ${q.questionId}`,
                                      type: q.type ?? "RATING_SCALE",
                                      mandatory: q.mandatory,
                                      sortOrder: q.sortOrder,
                                      optionConfig: q.optionConfig,
                                      answerConfig: q.answerConfig,
                                      maxScore: q.maxScore ?? 5,
                                      categoryVersionId: q.categoryVersionId,
                                  })),
                          })),
                      questions: [...p.questions]
                          .sort((a, b) => a.sortOrder - b.sortOrder)
                          .map((q) => ({
                              id: q.id,
                              questionId: q.questionId,
                              questionVersionId: q.questionVersionId,
                              categoryVersionId: q.categoryVersionId,
                              text: q.text ?? `Question ${q.questionId}`,
                              type: q.type ?? "RATING_SCALE",
                              mandatory: q.mandatory,
                              sortOrder: q.sortOrder,
                              optionConfig: q.optionConfig,
                              answerConfig: q.answerConfig,
                              maxScore: q.maxScore ?? 5,
                          })),
                  }))
            : [],
    );

    const currentPage = $derived(resolvedPages[currentPageIndex] ?? null);
    const totalPages = $derived(resolvedPages.length);
    const isPrivateCampaign = $derived(campaign?.authMode === "PRIVATE");
    const hasPrivateCredential = $derived(
        Boolean(responderToken?.trim() || responderAccessCode?.trim()),
    );
    const hasPrivateAccess = $derived(
        Boolean(privateSessionAuthenticated || responderToken?.trim() || responderAccessCode?.trim()),
    );
    
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

    function readAuthParamsFromUrl() {
        if (typeof window === "undefined") return;
        const url = new URL(window.location.href);
        const authCode = url.searchParams.get("auth_code")?.trim();
        const tokenFromQuery =
            url.searchParams.get("token")?.trim() ||
            url.searchParams.get("responderToken")?.trim();

        if (authCode) {
            responderAccessCode = authCode;
            authError = null;
        }
        if (tokenFromQuery && isLikelyJwtToken(tokenFromQuery)) {
            responderToken = tokenFromQuery;
            authError = null;
        }
    }

    function readMetadataParamsFromUrl() {
        if (typeof window === "undefined") return;
        const url = new URL(window.location.href);
        url.searchParams.forEach((value, key) => {
            if (key.startsWith("metadata.")) {
                const cleanKey = key.replace("metadata.", "");
                // The URL value might already be mostly decoded, but we decodeURIComponent just in case.
                let finalVal = value;
                try {
                    finalVal = decodeURIComponent(value);
                } catch {
                    // ignore malformed URI component
                }
                respondentMetadata[cleanKey] = finalVal;
            }
        });
    }

    function isLikelyJwtToken(token: string): boolean {
        const parts = token.split(".");
        return parts.length === 3 && parts.every((p) => p.length > 0);
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

    const activeLogoUrl = $derived(getTheme().branding.logoUrl?.trim() || "");
    const shouldRenderLogo = $derived(Boolean(activeLogoUrl) && !logoLoadFailed);

    $effect(() => {
        activeLogoUrl;
        logoLoadFailed = false;
    });

    function getRatingValues(question: PreviewQuestion): number[] {
        const config = parseAnswerConfig(question.answerConfig);
        const min = Number(config.min ?? 1);
        const max = Number(config.max ?? question.maxScore ?? 5);
        const step = Number(config.step ?? 1);
        if (
            !Number.isFinite(min) ||
            !Number.isFinite(max) ||
            !Number.isFinite(step) ||
            step <= 0 ||
            max < min
        ) {
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

    function getRemarkValue(questionId: string): string {
        return remarks[questionId] ?? "";
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
                ? "survey-option-card survey-option-card--selected shadow-sm"
                : "survey-option-card"
        }`;
    }

    function radioDotClass(selected: boolean): string {
        return `inline-flex h-5 w-5 shrink-0 items-center justify-center rounded-full border ${
            selected
                ? "survey-radio-dot survey-radio-dot--selected"
                : "survey-radio-dot"
        }`;
    }

    function checkBoxClass(selected: boolean): string {
        return `inline-flex h-5 w-5 shrink-0 items-center justify-center rounded-md border text-xs font-bold ${
            selected
                ? "survey-checkbox survey-checkbox--selected"
                : "survey-checkbox"
        }`;
    }

    function ratingButtonClass(selected: boolean): string {
        return `inline-flex h-9 min-w-10 items-center justify-center rounded-lg border px-3 text-sm font-medium transition ${
            selected
                ? "survey-rating-button survey-rating-button--selected"
                : "survey-rating-button"
        }`;
    }

    function moveRankOption(
        question: PreviewQuestion,
        index: number,
        direction: -1 | 1,
    ) {
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

    function questionSerialForQuestion(pageIdx: number, questionId: string): number {
        const pageData = resolvedPages[pageIdx];
        const questionIdx = pageData?.questions.findIndex((question) => question.questionId === questionId) ?? -1;
        return questionSerial(pageIdx, Math.max(questionIdx, 0));
    }

    function pageQuestionGroups(pageData: PreviewPage): { key: string; title?: string; description?: string; weightPercentage?: number; questions: PreviewQuestion[] }[] {
        const groups: { key: string; title?: string; description?: string; weightPercentage?: number; questions: PreviewQuestion[] }[] = [];
        for (const category of pageData.categories) {
            groups.push({
                key: category.categoryVersionId,
                title: category.name,
                description: category.description,
                weightPercentage: category.weightPercentage,
                questions: category.questions,
            });
        }

        const categorizedQuestionIds = new Set(
            pageData.categories.flatMap((category) => category.questions.map((question) => question.questionId)),
        );
        const uncategorizedQuestions = pageData.questions.filter(
            (question) => !categorizedQuestionIds.has(question.questionId),
        );
        if (uncategorizedQuestions.length > 0) {
            groups.push({
                key: "uncategorized",
                title: pageData.categories.length > 0 ? "Other Questions" : undefined,
                questions: uncategorizedQuestions,
            });
        }

        return groups;
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

    function getTheme(): SurveyThemeConfig {
        const theme = campaign?.theme;
        if (!theme) {
            return {
                ...DEFAULT_THEME,
                header: {
                    ...DEFAULT_THEME.header,
                    title: campaign?.campaignName ?? "Survey Experience",
                    subtitle: campaign?.surveyTitle ?? "Share your feedback with clarity and confidence.",
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
                title: campaign?.campaignName ?? "Survey Experience",
                subtitle: campaign?.surveyTitle ?? "Share your feedback with clarity and confidence.",
                ...theme.header,
            },
            footer: { ...DEFAULT_THEME.footer, ...theme.footer },
            advanced: { ...DEFAULT_THEME.advanced, ...theme.advanced },
        };
    }

    function surveyThemeStyle(): string {
        const theme = getTheme();
        return [
            `--survey-bg:${theme.palette.background}`,
            `--survey-shell:${theme.palette.shell}`,
            `--survey-panel:${theme.palette.panel}`,
            `--survey-card:${theme.palette.card}`,
            `--survey-border:${theme.palette.border}`,
            `--survey-text:${theme.palette.textPrimary}`,
            `--survey-muted:${theme.palette.textSecondary}`,
            `--survey-primary:${theme.palette.primary}`,
            `--survey-primary-text:${theme.palette.primaryText}`,
            `--survey-accent:${theme.palette.accent}`,
            `--survey-accent-soft:${theme.palette.accentSoft}`,
            `--survey-header-bg:${theme.palette.headerBackground}`,
            `--survey-header-text:${theme.palette.headerText}`,
            `--survey-footer-bg:${theme.palette.footerBackground}`,
            `--survey-footer-text:${theme.palette.footerText}`,
            `--survey-font:${theme.branding.fontFamily || DEFAULT_THEME.branding.fontFamily}`,
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

    function surveyShellClass(): string {
        const theme = getTheme();
        return `theme-studio-preview theme-studio-preview--live theme-studio-preview--${theme.layout.contentWidth} theme-studio-preview--motion-${theme.motion.animationPreset}`;
    }

    function surveyHeaderClass(): string {
        return `theme-studio-preview__header theme-studio-preview__header--${getTheme().layout.headerStyle}`;
    }

    function surveyPanelClass(): string {
        return `theme-studio-preview__panel theme-studio-preview__panel--${getTheme().layout.sectionStyle}`;
    }

    function surveyCardVariantClass(): string {
        return `theme-studio-preview__card theme-studio-preview__card--${getTheme().layout.questionCardStyle}`;
    }

    function surveyDividerClass(): string {
        return `theme-studio-preview__divider theme-studio-preview__divider--${getTheme().layout.categorySeparatorStyle}`;
    }

    function surveyFooterClass(): string {
        return `theme-studio-preview__footer theme-studio-preview__footer--${getTheme().layout.footerStyle}`;
    }

    function contentWidthClass(): string {
        const width = getTheme().layout.contentWidth;
        if (width === "narrow") return "max-w-4xl";
        if (width === "wide") return "max-w-6xl";
        return "max-w-5xl";
    }

    function hasCustomHeader(): boolean {
        const theme = getTheme();
        return Boolean(theme.advanced.useCustomHeaderHtml && theme.advanced.customHeaderHtml?.trim());
    }

    function hasCustomFooter(): boolean {
        const theme = getTheme();
        return Boolean(theme.advanced.useCustomFooterHtml && theme.advanced.customFooterHtml?.trim());
    }

    function customHeaderHtml(): string {
        const theme = getTheme();
        return theme.advanced.customHeaderHtml || campaign?.headerHtml || "";
    }

    function customFooterHtml(): string {
        const theme = getTheme();
        return theme.advanced.customFooterHtml || campaign?.footerHtml || "";
    }

    function headerAlignmentClass(): string {
        const align = getTheme().layout.headerAlignment;
        if (align === "center") return "items-center text-center";
        if (align === "right") return "items-end text-right";
        return "items-start text-left";
    }

    function getCollapsedCategory(pageData: PreviewPage) {
        const groups = pageQuestionGroups(pageData);
        if (groups.length !== 1) return null;
        const onlyGroup = groups[0];
        if (!onlyGroup.title) return null;
        return normalizeUiLabel(pageData.title) === normalizeUiLabel(onlyGroup.title)
            ? onlyGroup
            : null;
    }

    function shouldShowPageTitle(pageData: PreviewPage): boolean {
        return !getCollapsedCategory(pageData);
    }

    function shouldShowCategoryHeader(pageData: PreviewPage, groupKey: string): boolean {
        const collapsed = getCollapsedCategory(pageData);
        return !collapsed || collapsed.key !== groupKey;
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

    function buildRespondentIdentifier(): string | undefined {
        if (!campaign?.dataCollectionFields || campaign.dataCollectionFields.length === 0) return undefined;
        
        // Try to find a reasonable identifier for the admin UI to display.
        // Prefer email, then phone, then name.
        const email = getRespondentMetadataValue("email");
        if (email) return email;
        const phone = getRespondentMetadataValue("phone");
        if (phone) return phone;
        const name = getRespondentMetadataValue("name");
        if (name) return name;
        
        // Fallback: just return the first collected field value that exists
        for (const field of campaign.dataCollectionFields) {
            const value = getRespondentMetadataValue(field.fieldKey);
            if (field.enabled && value) {
                return value;
            }
        }
        
        return undefined;
    }

    function serializeAnswerValue(value: AnswerValue): string | null {
        if (Array.isArray(value)) {
            if (value.length === 0) return null;
            return JSON.stringify(value.map((v) => String(v)));
        }
        if (typeof value === "number") return String(value);
        if (typeof value === "string") {
            const trimmed = value.trim();
            return trimmed.length > 0 ? trimmed : null;
        }
        return null;
    }

    function getDraftStorageKey(): string {
        return `survey-draft:${campaignId}`;
    }

    function readDraftSession(): DraftSession | null {
        if (typeof window === "undefined") return null;
        const raw = window.localStorage.getItem(getDraftStorageKey());
        if (!raw) return null;
        try {
            const parsed = JSON.parse(raw) as DraftSession;
            return parsed?.responseId ? parsed : null;
        } catch {
            return null;
        }
    }

    function writeDraftSession(responseId: string, pageIndex: number): void {
        if (typeof window === "undefined") return;
        window.localStorage.setItem(
            getDraftStorageKey(),
            JSON.stringify({ responseId, pageIndex }),
        );
    }

    function clearConsumedAccessCode(): void {
        responderAccessCode = null;
        if (typeof window === "undefined") return;
        const url = new URL(window.location.href);
        if (url.searchParams.has("auth_code")) {
            url.searchParams.delete("auth_code");
            replaceState(url, page.state);
        }
    }

    function clearConsumedResponderToken(): void {
        responderToken = null;
        if (typeof window === "undefined") return;
        const url = new URL(window.location.href);
        let updated = false;
        if (url.searchParams.has("token")) {
            url.searchParams.delete("token");
            updated = true;
        }
        if (url.searchParams.has("responderToken")) {
            url.searchParams.delete("responderToken");
            updated = true;
        }
        if (updated) {
            replaceState(url, page.state);
        }
    }

    function clearDraftSession(): void {
        if (typeof window === "undefined") return;
        window.localStorage.removeItem(getDraftStorageKey());
    }

    function updateDraftPageIndex(): void {
        if (!draftResponseId) return;
        writeDraftSession(draftResponseId, currentPageIndex);
    }

    function deserializeAnswerValue(
        question: PreviewQuestion,
        rawValue: string,
    ): AnswerValue {
        if (
            question.type === "MULTIPLE_CHOICE" ||
            question.type === "RANK"
        ) {
            try {
                const parsed = JSON.parse(rawValue);
                return Array.isArray(parsed)
                    ? parsed.map((item) => String(item))
                    : [];
            } catch {
                return rawValue ? [rawValue] : [];
            }
        }

        if (question.type === "RATING_SCALE") {
            const numeric = Number(rawValue);
            return Number.isFinite(numeric) ? numeric : rawValue;
        }

        return rawValue;
    }

    function hydrateDraft(
        response: SurveyResponseResponse,
        pageIndex = 0,
    ): void {
        draftResponseId = response.id;

        const questionMap = new Map<string, PreviewQuestion>();
        for (const previewPage of resolvedPages) {
            for (const question of previewPage.questions) {
                questionMap.set(question.questionId, question);
            }
        }

        const restoredAnswers: Record<string, AnswerValue> = {};
        const restoredRemarks: Record<string, string> = {};
        for (const answer of response.answers ?? []) {
            const question = questionMap.get(answer.questionId);
            if (!question) continue;
            restoredAnswers[answer.questionId] = deserializeAnswerValue(
                question,
                answer.value,
            );
            if (answer.remark) {
                restoredRemarks[answer.questionId] = answer.remark;
            }
        }
        answers = restoredAnswers;
        remarks = restoredRemarks;

        if (response.respondentMetadata) {
            try {
                const parsed = JSON.parse(response.respondentMetadata) as Record<
                    string,
                    string | number | null
                >;
                respondentMetadata = parsed ?? {};
            } catch {
                respondentMetadata = {};
            }
        }

        currentPageIndex = Math.min(
            Math.max(pageIndex, 0),
            Math.max(totalPages - 1, 0),
        );
        stage = "form";
        draftMessage = "Draft restored.";
    }

    function buildPayloadAnswers(): {
        questionId: string;
        questionVersionId: string;
        value: string;
        remark?: string;
    }[] {
        const payloadAnswers: {
            questionId: string;
            questionVersionId: string;
            value: string;
            remark?: string;
        }[] = [];

        for (const surveyPage of resolvedPages) {
            for (const question of surveyPage.questions) {
                const serializedValue = serializeAnswerValue(
                    answers[question.questionId],
                );
                if (!serializedValue) continue;
                payloadAnswers.push({
                    questionId: question.questionId,
                    questionVersionId: question.questionVersionId,
                    value: serializedValue,
                    remark: getRemarkValue(question.questionId).trim() || undefined,
                });
            }
        }

        return payloadAnswers;
    }

    async function restoreDraftIfAvailable(): Promise<void> {
        const draftSession = readDraftSession();
        if (!campaign) return;
        if (campaign.authMode === "PRIVATE" && !hasPrivateAccess) return;

        const respondentIdentifier = buildRespondentIdentifier();
        if (!draftSession && !respondentIdentifier && campaign.authMode !== "PRIVATE") {
            return;
        }

        try {
            const { data } = await api.post<SurveyResponseResponse>(
                `/public/campaigns/${campaignId}/responses/draft/load`,
                {
                    responseId: draftSession?.responseId || undefined,
                    respondentIdentifier: respondentIdentifier || undefined,
                    responderToken:
                        campaign.authMode === "PRIVATE"
                            ? responderToken || undefined
                            : undefined,
                    responderAccessCode:
                        campaign.authMode === "PRIVATE"
                            ? responderAccessCode || undefined
                            : undefined,
                },
            );
            hydrateDraft(data, draftSession?.pageIndex ?? 0);
            writeDraftSession(data.id, draftSession?.pageIndex ?? 0);
            if (campaign.authMode === "PRIVATE" && responderAccessCode) {
                clearConsumedAccessCode();
            }
        } catch {
            if (draftSession) {
                clearDraftSession();
                draftResponseId = null;
            }
        }
    }

    async function loadPrivateSessionStatus(): Promise<void> {
        if (!campaign || campaign.authMode !== "PRIVATE") {
            privateSessionAuthenticated = false;
            return;
        }

        try {
            const { data } = await api.get<ResponderSessionStatusResponse>(
                `/public/campaigns/${campaignId}/auth/session`,
            );
            privateSessionAuthenticated = Boolean(data?.authenticated);
        } catch {
            privateSessionAuthenticated = false;
        }
    }

    async function exchangePrivateCredentialForSession(): Promise<void> {
        if (!campaign || campaign.authMode !== "PRIVATE") return;
        if (privateSessionAuthenticated) return;

        const token = responderToken?.trim() || undefined;
        const accessCode = responderAccessCode?.trim() || undefined;
        if (!token && !accessCode) return;

        try {
            const { data } = await api.post<ResponderSessionStatusResponse>(
                `/public/campaigns/${campaignId}/auth/exchange`,
                {
                    responderToken: token,
                    responderAccessCode: accessCode,
                },
            );
            privateSessionAuthenticated = Boolean(data?.authenticated);
            if (privateSessionAuthenticated) {
                if (accessCode) {
                    clearConsumedAccessCode();
                }
                if (token) {
                    clearConsumedResponderToken();
                }
            }
        } catch {
            // Keep legacy credential path as fallback.
        }
    }

    async function startPrivateAuth(): Promise<boolean> {
        if (!campaign || campaign.authMode !== "PRIVATE") return true;
        if (hasPrivateAccess) return true;
        if (!campaign.tenantId) {
            authError = "Unable to start private authentication.";
            return false;
        }

        authLoading = true;
        authError = null;
        try {
            const { data } = await api.post<OidcStartResponse>(
                "/auth/respondent/oidc/start",
                {
                    tenantId: campaign.tenantId,
                    campaignId: campaign.campaignId,
                    returnPath: `/s/${campaignId}`,
                },
            );
            if (!data.authorizationUrl) {
                authError =
                    "Authentication service did not return a login URL.";
                return false;
            }
            if (typeof window !== "undefined") {
                window.location.href = data.authorizationUrl;
            }
            return false;
        } catch (err: unknown) {
            const axiosErr = err as {
                response?: { data?: { message?: string } };
            };
            authError =
                axiosErr?.response?.data?.message ??
                "Failed to start private sign-in.";
            return false;
        } finally {
            authLoading = false;
        }
    }

    async function logoutPrivateSession(): Promise<void> {
        if (!campaign || campaign.authMode !== "PRIVATE" || !privateSessionAuthenticated) {
            return;
        }

        logoutLoading = true;
        authError = null;
        submitError = null;
        draftMessage = null;
        try {
            await api.post(`/public/campaigns/${campaignId}/auth/logout`);
        } catch (err: unknown) {
            const axiosErr = err as {
                response?: { data?: { message?: string } };
            };
            authError =
                axiosErr?.response?.data?.message ??
                "Failed to end the private survey session.";
        } finally {
            privateSessionAuthenticated = false;
            responderAccessCode = null;
            responderToken = null;
            clearConsumedAccessCode();
            stage = "intro";
            logoutLoading = false;
        }
    }

    async function handleStartSurvey() {
        submitError = null;
        if (campaign?.authMode === "PRIVATE" && !hasPrivateAccess) {
            const authReady = await startPrivateAuth();
            if (!authReady) return;
        }
        stage = "form";
    }

    async function submitResponse(): Promise<boolean> {
        if (!campaign) return false;
        if (campaign.authMode === "PRIVATE" && !hasPrivateAccess) {
            submitError =
                "Private authentication is required before submission.";
            return false;
        }
        const payloadAnswers = buildPayloadAnswers();

        submitLoading = true;
        submitError = null;
        try {
            await api.post("/responses", {
                responseId: draftResponseId || undefined,
                campaignId: campaign.campaignId,
                respondentIdentifier: buildRespondentIdentifier(),
                respondentMetadata: respondentMetadata,
                responderToken:
                    campaign.authMode === "PRIVATE"
                        ? responderToken || undefined
                        : undefined,
                responderAccessCode:
                    campaign.authMode === "PRIVATE"
                        ? responderAccessCode || undefined
                        : undefined,
                answers: payloadAnswers,
            });
            clearDraftSession();
            draftResponseId = null;
            draftMessage = null;
            remarks = {};
            return true;
        } catch (err: unknown) {
            const axiosErr = err as {
                response?: { data?: { message?: string } };
            };
            submitError =
                axiosErr?.response?.data?.message ??
                "Failed to submit response.";
            return false;
        } finally {
            submitLoading = false;
        }
    }

    async function saveDraft(): Promise<void> {
        if (!campaign) return;
        if (campaign.authMode === "PRIVATE" && !hasPrivateAccess) {
            const authReady = await startPrivateAuth();
            if (!authReady) return;
        }

        draftSaving = true;
        draftMessage = null;
        submitError = null;
        try {
            const { data } = await api.post<SurveyResponseResponse>(
                `/public/campaigns/${campaignId}/responses/draft`,
                {
                    responseId: draftResponseId || undefined,
                    campaignId: campaign.campaignId,
                    respondentIdentifier: buildRespondentIdentifier(),
                    respondentMetadata: respondentMetadata,
                    responderToken:
                        campaign.authMode === "PRIVATE"
                            ? responderToken || undefined
                            : undefined,
                    responderAccessCode:
                        campaign.authMode === "PRIVATE"
                            ? responderAccessCode || undefined
                            : undefined,
                    answers: buildPayloadAnswers(),
                },
            );
            draftResponseId = data.id;
            writeDraftSession(data.id, currentPageIndex);
            if (campaign.authMode === "PRIVATE" && responderAccessCode) {
                clearConsumedAccessCode();
            }
            draftMessage = "Draft saved.";
        } catch (err: unknown) {
            const axiosErr = err as {
                response?: { data?: { message?: string } };
            };
            submitError =
                axiosErr?.response?.data?.message ??
                "Failed to save draft.";
        } finally {
            draftSaving = false;
        }
    }

    async function goNext() {
        errors = {}; // Clear errors before running validations
        const respondentValid = validateRespondentFields();
        const pageValid = validateCurrentPage();
        if (!respondentValid || !pageValid) return;
        if (currentPageIndex >= totalPages - 1) {
            const submitted = await submitResponse();
            if (submitted) {
                stage = "complete";
                errors = {};
            }
            return;
        }
        currentPageIndex += 1;
        updateDraftPageIndex();
        submitError = null;
    }

    function goBack() {
        if (currentPageIndex <= 0) return;
        currentPageIndex -= 1;
        updateDraftPageIndex();
        errors = {};
        submitError = null;
    }

    async function load() {
        loading = true;
        error = null;
        authError = null;
        try {
            readAuthParamsFromUrl();
            readMetadataParamsFromUrl();

            const previewRes = await api.get<CampaignPreviewResponse>(
                `/public/campaigns/${campaignId}/preview`,
            );
            campaign = previewRes.data;
            await loadPrivateSessionStatus();
            await exchangePrivateCredentialForSession();
            if (campaign.authMode === "PRIVATE" && privateSessionAuthenticated && responderAccessCode) {
                clearConsumedAccessCode();
            }
            await restoreDraftIfAvailable();
            if (stage === "intro" && campaign.authMode === "PRIVATE" && hasPrivateAccess) {
                stage = "form";
            }
        } catch (err: unknown) {
            const axiosErr = err as {
                response?: { status?: number; data?: { message?: string } };
            };
            if (axiosErr?.response?.status === 404) {
                error = "Survey not found or not active";
            } else {
                error =
                    axiosErr?.response?.data?.message ??
                    "Failed to load survey";
            }
        } finally {
            loading = false;
        }
    }

    onMount(load);
</script>

<svelte:head>
    <title>{campaign?.campaignName ?? "Survey"} — Respond</title>
</svelte:head>

<div class="survey-page min-h-screen py-8 sm:py-10" style={surveyThemeStyle()}>
    <div class="survey-page__ambient survey-page__ambient--left"></div>
    <div class="survey-page__ambient survey-page__ambient--right"></div>
    <div class="survey-page__mesh"></div>
    <div class={`relative mx-auto w-full ${contentWidthClass()} px-4 sm:px-6`}>
        {#if loading}
            <Card.Root class="mx-auto max-w-2xl">
                <Card.Header>
                    <div class="space-y-2">
                        <Skeleton class="h-8 w-[250px]" />
                        <Skeleton class="h-4 w-[350px]" />
                    </div>
                </Card.Header>
                <Card.Content class="space-y-6">
                    <div class="space-y-4">
                        {#each Array(5) as _}
                            <div class="space-y-2">
                                <Skeleton class="h-4 w-[200px]" />
                                <Skeleton class="h-10 w-full" />
                            </div>
                        {/each}
                    </div>
                    <div class="flex justify-end gap-2">
                        <Skeleton class="h-10 w-[120px]" />
                        <Skeleton class="h-10 w-[100px]" />
                    </div>
                </Card.Content>
            </Card.Root>
        {:else if error || !campaign}
            <Card.Root class="mx-auto max-w-2xl">
                <Card.Header>
                    <Card.Title>Survey Unavailable</Card.Title>
                    <Card.Description
                        >{error ??
                            "Unable to open this survey link."}</Card.Description
                    >
                </Card.Header>
                <Card.Content>
                    <Button variant="outline" onclick={() => goto("/")}
                        >Back to Home</Button
                    >
                </Card.Content>
            </Card.Root>
        {:else}
            <div
                class={`survey-shell ${surveyShellClass()} overflow-hidden border border-white/60 bg-[linear-gradient(180deg,rgba(255,255,255,0.96),rgba(250,246,239,0.92))] shadow-[0_40px_120px_-55px_rgba(15,23,42,0.6)] backdrop-blur-2xl transition-all duration-300`}
            >
                {#if hasCustomHeader()}
                    <section
                        class="theme-studio-preview__custom-html theme-studio-preview__custom-html--header campaign-branding-shell campaign-branding-shell-header campaign-branding-shell-header--flush relative z-10 w-full"
                    >
                        <div class="campaign-branding-content campaign-branding-content--flush campaign-branding-content--edge">
                            {@html customHeaderHtml()}
                        </div>
                    </section>
                {/if}

                {#if !hasCustomHeader() || (stage === "form" && campaign.showProgressIndicator)}
                    <section
                    class={`${surveyHeaderClass()} survey-hero relative z-0 space-y-4 pb-8 pt-8 sm:pb-10 ${hasCustomHeader() ? 'survey-hero--with-custom-header' : ''} ${headerAlignmentClass()}`}
                    data-align={getTheme().layout.headerAlignment}
                    data-logo-position={getTheme().branding.logoPosition}
                    >
                        {#if !hasCustomHeader()}
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
                            <div class="theme-studio-preview__eyebrow">
                                {getTheme().header.eyebrow || getTheme().branding.brandLabel}
                            </div>
                            <Card.Title
                            class="survey-title text-3xl sm:text-5xl"
                                >{getTheme().header.title || campaign.campaignName}</Card.Title
                            >
                            <Card.Description
                                class="max-w-2xl text-base font-medium leading-7 survey-hero-description"
                                >{getTheme().header.subtitle || campaign.surveyTitle}</Card.Description
                            >
                            {#if getTheme().header.note}
                                <p class="survey-hero-note">{getTheme().header.note}</p>
                            {/if}
                        {/if}

                        {#if stage === "form" && campaign.showProgressIndicator}
                            <div class={`survey-progress-block space-y-2 pt-3 ${hasCustomHeader() ? 'survey-progress-panel' : ''}`}>
                                <div
                                class="h-2 w-full overflow-hidden rounded-full survey-progress-track shadow-inner backdrop-blur-sm"
                                >
                                    <div
                                    class="h-full rounded-full survey-progress-fill transition-all duration-700 ease-out shadow-sm"
                                        style={`width:${progressPercent}%`}
                                    ></div>
                                </div>
                                <p
                                class="text-[11px] font-bold uppercase tracking-[0.24em] survey-progress-copy"
                                >
                                    Progress: {Math.round(progressPercent)}% &bull;
                                    Page {currentPageIndex + 1} of {totalPages}
                                </p>
                            </div>
                        {/if}
                    </section>
                {/if}

                {#if stage === "intro"}
                            <div class={`${surveyCardVariantClass()} survey-intro-card m-6 space-y-4 sm:m-8`}>
                                <div class="survey-section-chip">
                                    Invitation
                                </div>
                                {#if campaign.startMessage}
                                    <p
                                        class="text-sm leading-7 text-foreground whitespace-pre-wrap sm:text-base"
                                    >
                                        {campaign.startMessage}
                                    </p>
                                {:else}
                                    <p class="text-sm leading-7 survey-copy-muted sm:text-base">
                                        This evaluation flow is designed as a structured review. Complete each section with the same care you would expect from a premium assessment experience.
                                    </p>
                                {/if}
                                {#if campaign.authMode === "PRIVATE"}
                                    <p class="text-sm leading-6 survey-copy-muted">
                                        This is a private survey. You must
                                        complete sign-in before answering.
                                    </p>
                                {/if}
                                {#if authError}
                                    <p class="text-sm text-destructive">
                                        {authError}
                                    </p>
                                {/if}
                                <Button
                                    class="survey-primary-button"
                                    onclick={handleStartSurvey}
                                    disabled={authLoading}
                                >
                                    <Play class="mr-2 h-4 w-4" />
                                    {#if authLoading}
                                        Redirecting to sign-in...
                                    {:else}
                                        Start Survey
                                    {/if}
                                </Button>
                            </div>
                        {:else if stage === "complete"}
                            <div class={`${surveyCardVariantClass()} survey-intro-card m-6 space-y-4 sm:m-8`}>
                                <div class="survey-section-chip">
                                    Submission Received
                                </div>
                                <h3 class="survey-title text-2xl sm:text-4xl">Thank You</h3>
                                {#if campaign.finishMessage}
                                    <p
                                        class="text-sm leading-7 text-foreground whitespace-pre-wrap sm:text-base"
                                    >
                                        {campaign.finishMessage}
                                    </p>
                                {:else}
                                    <p class="text-sm leading-7 survey-copy-muted sm:text-base">
                                        Your response has been captured successfully. The evaluation record now includes section context, scoring data, and any remarks you provided.
                                    </p>
                                {/if}
                            </div>
                        {:else if isPrivateCampaign && !hasPrivateAccess}
                            <div class={`${surveyCardVariantClass()} survey-intro-card m-6 space-y-4 sm:m-8`}>
                                <div class="survey-section-chip">
                                    Authentication Required
                                </div>
                                <p class="text-sm leading-7 text-[#446066] sm:text-base">
                                    Private authentication is required before
                                    filling this survey.
                                </p>
                                {#if authError}
                                    <p class="text-sm text-destructive">
                                        {authError}
                                    </p>
                                {/if}
                                <Button
                                    class="survey-primary-button"
                                    onclick={startPrivateAuth}
                                    disabled={authLoading}
                                >
                                    {#if authLoading}
                                        Redirecting to sign-in...
                                    {:else}
                                        Continue with Sign-In
                                    {/if}
                                </Button>
                            </div>
                        {:else if currentPage}
                            {#if campaign.dataCollectionFields?.some(f => f.enabled)}
                                <section class={`${surveyPanelClass()} survey-panel mx-6 mt-6 sm:mx-8 sm:mt-8`}>
                                    <div class="theme-studio-preview__section-title">Respondent Metadata</div>
                                    <div class={surveyDividerClass()}></div>
                                    <div class="pb-3">
                                        <div class="survey-panel-title text-base uppercase tracking-[0.18em] font-semibold">Your Information</div>
                                        <p class="survey-panel-description text-sm">
                                            Respondent metadata is collected before the evaluation sections.
                                        </p>
                                    </div>
                                    <div class="grid gap-4 pb-2 sm:grid-cols-2">
                                        {#each campaign.dataCollectionFields as field}
                                            {#if field.enabled}
                                                <div class={field.fieldType === 'TEXTAREA' ? "space-y-1 sm:col-span-2" : "space-y-1"}>
                                                    <Label for="meta-{field.fieldKey}">
                                                        {field.label} {#if field.required}<span class="text-destructive">*</span>{/if}
                                                    </Label>
                                                    
                                                    {#if field.fieldType === 'TEXTAREA'}
                                                        <Textarea
                                                            class="survey-input"
                                                            id="meta-{field.fieldKey}"
                                                            bind:value={respondentMetadata[field.fieldKey]}
                                                        />
                                                    {:else if field.fieldType === 'NUMBER'}
                                                        <Input
                                                            class="survey-input"
                                                            id="meta-{field.fieldKey}"
                                                            type="number"
                                                            bind:value={respondentMetadata[field.fieldKey]}
                                                        />
                                                    {:else if field.fieldType === 'PHONE'}
                                                        <Input
                                                            class="survey-input"
                                                            id="meta-{field.fieldKey}"
                                                            type="tel"
                                                            bind:value={respondentMetadata[field.fieldKey]}
                                                        />
                                                    {:else if field.fieldType === 'EMAIL'}
                                                        <Input
                                                            class="survey-input"
                                                            id="meta-{field.fieldKey}"
                                                            type="email"
                                                            bind:value={respondentMetadata[field.fieldKey]}
                                                        />
                                                    {:else}
                                                        <Input
                                                            class="survey-input"
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

                            <div class="mx-6 mt-6 space-y-8 sm:mx-8 sm:mt-8">
                                <div class="theme-studio-preview__section-title">Evaluation Section</div>
                                <div class={surveyDividerClass()}></div>
                                <div class="pb-3">
                                    {#if shouldShowPageTitle(currentPage)}
                                        <div class="survey-panel-title text-base uppercase tracking-[0.18em] font-semibold">{currentPage.title}</div>
                                    {:else}
                                        <div class="survey-panel-title text-base uppercase tracking-[0.18em] font-semibold">
                                            {getCollapsedCategory(currentPage)?.title}
                                        </div>
                                    {/if}
                                    <p class="survey-panel-description text-sm">
                                        {#if getCollapsedCategory(currentPage)?.description}
                                            {getCollapsedCategory(currentPage)?.description}
                                        {:else}
                                            Provide your evaluation chapter by chapter. Optional remarks add nuance where a score alone is not enough.
                                        {/if}
                                    </p>
                                    {#if getCollapsedCategory(currentPage)?.weightPercentage != null || (getCollapsedCategory(currentPage)?.key !== undefined && currentPage.categories.find((category) => category.categoryVersionId === getCollapsedCategory(currentPage)?.key)?.versionNumber)}
                                        <div class="flex flex-wrap items-center gap-2 pt-1">
                                            {#if currentPage.categories.find((category) => category.categoryVersionId === getCollapsedCategory(currentPage)?.key)?.versionNumber}
                                                <span class="survey-meta-pill">
                                                    Version v{currentPage.categories.find((category) => category.categoryVersionId === getCollapsedCategory(currentPage)?.key)?.versionNumber}
                                                </span>
                                            {/if}
                                            {#if getCollapsedCategory(currentPage)?.weightPercentage != null}
                                                <span class="survey-meta-pill">
                                                    Weight {getCollapsedCategory(currentPage)?.weightPercentage}%
                                                </span>
                                            {/if}
                                        </div>
                                    {/if}
                                </div>
                                <div class="space-y-8 pb-2">
                                    {#each pageQuestionGroups(currentPage) as group, groupIndex}
                                        <section class="survey-category-block space-y-5">
                                            {#if groupIndex > 0}
                                                <div class={`${surveyDividerClass()} survey-category-divider`} aria-hidden="true"></div>
                                            {/if}
                                            {#if group.title && shouldShowCategoryHeader(currentPage, group.key)}
                                                <div class="survey-category-header">
                                                    <div class="flex flex-wrap items-start justify-between gap-3">
                                                        <div class="space-y-1">
                                                            <h3 class="survey-category-title">
                                                                {group.title}
                                                                {#if group.key !== "uncategorized" && currentPage.categories.find((category) => category.categoryVersionId === group.key)?.versionNumber}
                                                                    <span class="ml-2 text-xs font-medium text-[#5e7075]">
                                                                        v{currentPage.categories.find((category) => category.categoryVersionId === group.key)?.versionNumber}
                                                                    </span>
                                                                {/if}
                                                            </h3>
                                                            {#if group.description}
                                                                <p class="survey-panel-description text-sm leading-6">
                                                                    {group.description}
                                                                </p>
                                                            {/if}
                                                        </div>
                                                        {#if group.weightPercentage != null}
                                                            <div class="survey-weight-pill">
                                                                Weight {group.weightPercentage}%
                                                            </div>
                                                        {/if}
                                                    </div>
                                                </div>
                                            {/if}

                                            {#each group.questions as question}
                                                <div class={`${surveyCardVariantClass()} survey-question-card space-y-4`}>
                                                    <p
                                                        class="survey-question-text"
                                                    >
                                                        {#if campaign.showQuestionNumbers}
                                                            <span
                                                                class="survey-question-index"
                                                                >{questionSerialForQuestion(
                                                                    currentPageIndex,
                                                                    question.questionId,
                                                                )}</span
                                                            >
                                                        {/if}
                                                        {question.text}
                                                        {#if question.mandatory}<span
                                                                class="text-destructive ml-1"
                                                                >*</span
                                                            >{/if}
                                                    </p>

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
                                                                    <span
                                                                        class="survey-option-text"
                                                                        >{option}</span
                                                                    >
                                                                    <span
                                                                        class={radioDotClass(
                                                                            isSingleSelected(
                                                                                question.questionId,
                                                                                option,
                                                                            ),
                                                                        )}
                                                                    >
                                                                        {#if isSingleSelected(question.questionId, option)}
                                                                            <span
                                                                                class="h-2.5 w-2.5 rounded-full survey-radio-dot-inner"
                                                                            ></span>
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
                                                                    <span
                                                                        class="survey-option-text"
                                                                        >{option}</span
                                                                    >
                                                                    <span
                                                                        class={checkBoxClass(
                                                                            isMultiSelected(
                                                                                question.questionId,
                                                                                option,
                                                                            ),
                                                                        )}>✓</span
                                                                    >
                                                                </button>
                                                            {/each}
                                                        </div>
                                                    {:else if question.type === "RANK"}
                                                        <div class="space-y-2">
                                                            {#each ensureRankOrder(question) as option, idx}
                                                                <div
                                                                    class="flex items-center justify-between rounded-md border border-border px-3 py-2"
                                                                >
                                                                    <div
                                                                        class="survey-rank-option-text"
                                                                    >
                                                                        {idx + 1}. {option}
                                                                    </div>
                                                                    <div
                                                                        class="flex gap-1"
                                                                    >
                                                                        <Button
                                                                            type="button"
                                                                            variant="outline"
                                                                            size="sm"
                                                                            disabled={idx ===
                                                                                0}
                                                                            onclick={() =>
                                                                                moveRankOption(
                                                                                    question,
                                                                                    idx,
                                                                                    -1,
                                                                                )}
                                                                        >
                                                                            <ChevronLeft
                                                                                class="h-4 w-4"
                                                                            />
                                                                        </Button>
                                                                        <button
                                                                            type="button"
                                                                            onclick={() =>
                                                                                moveRankOption(
                                                                                    question,
                                                                                    idx,
                                                                                    1,
                                                                                )}
                                                                            disabled={idx ===
                                                                                ensureRankOrder(
                                                                                    question,
                                                                                )
                                                                                    .length -
                                                                                    1}
                                                                        >
                                                                            <ChevronRight
                                                                                class="h-4 w-4"
                                                                            />
                                                                        </button>
                                                                    </div>
                                                                </div>
                                                            {/each}
                                                        </div>
                                                    {:else}
                                                        <div
                                                            class="flex flex-wrap gap-2"
                                                        >
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
                                                                                question
                                                                                    .questionId
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
                                                        <Label for={`remark-${question.questionId}`}>
                                                            Remarks <span class="text-muted-foreground">(optional)</span>
                                                        </Label>
                                                        <Textarea
                                                            class="survey-input survey-remark"
                                                            id={`remark-${question.questionId}`}
                                                            rows={3}
                                                            placeholder="Add an optional comment for this question"
                                                            bind:value={remarks[question.questionId]}
                                                        />
                                                    </div>

                                                    {#if errors[question.questionId]}
                                                        <p
                                                            class="text-xs text-destructive"
                                                        >
                                                            {errors[
                                                                question.questionId
                                                            ]}
                                                        </p>
                                                    {/if}
                                                </div>
                                            {/each}
                                        </section>
                                    {/each}
                                </div>
                            </div>

                            <div class="mx-6 mt-6 mb-6 flex items-center justify-between gap-4 sm:mx-8 sm:mb-8">
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
                                <div class="flex items-center gap-2">
                                    {#if isPrivateCampaign && privateSessionAuthenticated}
                                        <Button
                                            type="button"
                                            variant="outline"
                                            class="survey-secondary-button survey-logout-button"
                                            onclick={logoutPrivateSession}
                                            disabled={logoutLoading || submitLoading || draftSaving}
                                        >
                                            <LogOut class="mr-2 h-4 w-4" />
                                            {logoutLoading ? "Signing out..." : "Sign out"}
                                        </Button>
                                    {/if}
                                    <Button
                                        type="button"
                                        variant="outline"
                                        class="survey-secondary-button"
                                        onclick={saveDraft}
                                        disabled={draftSaving || submitLoading}
                                    >
                                        <Save class="mr-2 h-4 w-4" />
                                        {draftSaving ? "Saving..." : "Save Draft"}
                                    </Button>
                                    <Button
                                        type="button"
                                        class="survey-primary-button"
                                        onclick={goNext}
                                        disabled={submitLoading || draftSaving}
                                    >
                                        {#if currentPageIndex === totalPages - 1 && submitLoading}
                                            Submitting...
                                        {:else}
                                            {currentPageIndex === totalPages - 1
                                                ? "Finish"
                                                : "Next"}
                                        {/if}
                                    </Button>
                                </div>
                            </div>
                            {#if draftMessage}
                                <p class="mx-6 mt-3 text-sm text-emerald-600 sm:mx-8">
                                    {draftMessage}
                                </p>
                            {/if}
                            {#if submitError}
                                <p class="mx-6 mt-3 text-sm text-destructive sm:mx-8">
                                    {submitError}
                                </p>
                            {/if}
                        {/if}

                    {#if stage === "form" && currentPage}
                        {#if hasCustomFooter()}
                            <section class="theme-studio-preview__custom-html theme-studio-preview__custom-html--footer theme-studio-preview__custom-html--footer-edge campaign-branding-shell campaign-branding-shell-footer">
                                <div class={`campaign-branding-content ${hasCustomFooter() ? 'campaign-branding-content--edge' : ''}`}>
                                    {@html customFooterHtml()}
                                </div>
                            </section>
                        {:else if getTheme().footer.enabled}
                            <section class={`${surveyFooterClass()} theme-studio-preview__footer--edge`} data-align={getTheme().layout.footerAlignment}>
                                <p class="theme-studio-preview__footer-line1">{getTheme().footer.line1}</p>
                                {#if getTheme().footer.line2}
                                    <p class="theme-studio-preview__footer-line2">{getTheme().footer.line2}</p>
                                {/if}
                                {#if getTheme().footer.legal}
                                    <p class="theme-studio-preview__footer-legal">{getTheme().footer.legal}</p>
                                {/if}
                            </section>
                        {/if}
                    {/if}
            </div>
        {/if}
    </div>
</div>

<style>
    .survey-page {
        position: relative;
        overflow: hidden;
        background: var(--survey-bg);
        background:
            radial-gradient(circle at top left, color-mix(in srgb, var(--survey-accent) 22%, transparent), transparent 30%),
            radial-gradient(circle at bottom right, color-mix(in srgb, var(--survey-primary) 18%, transparent), transparent 34%),
            linear-gradient(180deg, color-mix(in srgb, var(--survey-bg) 92%, #fff6ef) 0%, color-mix(in srgb, var(--survey-bg) 94%, #eef5f4) 36%, var(--survey-bg) 100%);
        color: var(--survey-text);
    }

    .survey-page__ambient {
        position: absolute;
        inset: auto;
        border-radius: 9999px;
        filter: blur(70px);
        opacity: 0.6;
        pointer-events: none;
    }

    :global(.survey-logout-button) {
        color: #fff !important;
        border-color: #b91c1c !important;
        background: linear-gradient(135deg, #dc2626 0%, #b91c1c 100%) !important;
    }

    :global(.survey-logout-button:hover:not(:disabled)) {
        color: #fff !important;
        border-color: #991b1b !important;
        background: linear-gradient(135deg, #b91c1c 0%, #991b1b 100%) !important;
    }

    .survey-page__ambient--left {
        top: 3rem;
        left: -6rem;
        width: 18rem;
        height: 18rem;
        background: color-mix(in srgb, var(--survey-accent) 24%, transparent);
    }

    .survey-page__ambient--right {
        right: -5rem;
        bottom: 6rem;
        width: 16rem;
        height: 16rem;
        background: color-mix(in srgb, var(--survey-primary) 16%, transparent);
    }

    .survey-page__mesh {
        position: absolute;
        inset: 0;
        pointer-events: none;
        background-image:
            linear-gradient(color-mix(in srgb, var(--survey-muted) 10%, transparent) 1px, transparent 1px),
            linear-gradient(90deg, color-mix(in srgb, var(--survey-muted) 10%, transparent) 1px, transparent 1px);
        background-size: 30px 30px;
        mask-image: linear-gradient(180deg, rgba(0, 0, 0, 0.4), transparent 78%);
    }

    .survey-shell {
        position: relative;
    }

    .survey-hero::after {
        content: "";
        position: absolute;
        inset: auto 2rem 0;
        height: 1px;
        background: linear-gradient(90deg, transparent, color-mix(in srgb, var(--survey-accent) 46%, transparent), transparent);
    }

    .survey-hero--with-custom-header {
        padding-top: 0.85rem;
        padding-bottom: 1rem;
        background: linear-gradient(180deg, color-mix(in srgb, var(--survey-shell) 94%, white), color-mix(in srgb, var(--survey-panel) 82%, white));
    }

    .survey-hero--with-custom-header::before {
        content: "";
        position: absolute;
        inset: 0 0 auto 0;
        height: 10px;
        background: linear-gradient(180deg, color-mix(in srgb, var(--survey-primary) 8%, transparent), transparent);
        pointer-events: none;
    }

    .survey-title {
        font-family: "Iowan Old Style", "Palatino Linotype", "Book Antiqua", Georgia, serif;
        letter-spacing: -0.04em;
        line-height: 1;
        color: inherit;
        font-family: var(--survey-font);
        text-wrap: balance;
    }

    .survey-intro-card,
    .survey-question-card {
        border-radius: 1.5rem;
        border: 1px solid color-mix(in srgb, var(--survey-border) 38%, transparent);
        background: linear-gradient(180deg, color-mix(in srgb, var(--survey-card) 96%, white), color-mix(in srgb, var(--survey-card) 90%, var(--survey-accent-soft)));
        box-shadow: 0 24px 60px -45px rgba(16, 42, 67, 0.32);
    }

    .survey-panel {
        border-radius: 1.5rem;
        border: 1px solid color-mix(in srgb, var(--survey-border) 28%, transparent);
        background: linear-gradient(180deg, color-mix(in srgb, var(--survey-panel) 66%, transparent), color-mix(in srgb, var(--survey-panel) 42%, transparent));
        box-shadow: none;
    }

    .survey-intro-card,
    .survey-question-card {
        padding: 1.4rem;
    }

    .survey-panel :global(.card-header),
    .survey-panel :global(.card-content) {
        position: relative;
    }

    .survey-panel :global(.card-header) {
        border-bottom: 1px solid color-mix(in srgb, var(--survey-border) 44%, transparent);
    }

    .survey-section-chip {
        display: inline-flex;
        align-items: center;
        border-radius: 9999px;
        background: var(--survey-accent-soft);
        background: color-mix(in srgb, var(--survey-accent) 12%, transparent);
        color: var(--survey-primary);
        padding: 0.45rem 0.8rem;
        font-size: 0.72rem;
        font-weight: 700;
        letter-spacing: 0.16em;
        text-transform: uppercase;
    }

    .survey-category-block {
        position: relative;
    }

    .survey-category-divider {
        height: 1px;
        width: 100%;
        background: linear-gradient(90deg, transparent, color-mix(in srgb, var(--survey-accent) 50%, transparent), transparent);
        margin: 0.25rem 0 1.4rem;
    }

    .survey-category-header {
        padding: 0 0 0.35rem;
        border: 0;
        background: transparent;
        box-shadow: none;
        border-radius: 0;
    }

    .survey-category-title {
        font-family: "Iowan Old Style", "Palatino Linotype", "Book Antiqua", Georgia, serif;
        font-size: 1rem;
        font-weight: 700;
        letter-spacing: 0.01em;
        color: var(--survey-text);
    }

    .survey-panel-title {
        color: var(--survey-primary);
    }

    .survey-panel-description {
        color: var(--survey-muted);
    }

    .survey-weight-pill {
        border-radius: 9999px;
        border: 1px solid var(--survey-accent);
        border: 1px solid color-mix(in srgb, var(--survey-accent) 32%, transparent);
        background: var(--survey-accent-soft);
        background: color-mix(in srgb, var(--survey-accent-soft) 88%, white);
        padding: 0.45rem 0.85rem;
        font-size: 0.72rem;
        font-weight: 700;
        letter-spacing: 0.12em;
        text-transform: uppercase;
        color: var(--survey-primary);
    }

    .survey-meta-pill {
        display: inline-flex;
        align-items: center;
        border-radius: 9999px;
        border: 1px solid var(--survey-accent);
        border: 1px solid color-mix(in srgb, var(--survey-accent) 24%, transparent);
        background: var(--survey-accent-soft);
        background: color-mix(in srgb, var(--survey-accent-soft) 92%, white);
        padding: 0.35rem 0.7rem;
        font-size: 0.72rem;
        font-weight: 700;
        letter-spacing: 0.08em;
        text-transform: uppercase;
        color: var(--survey-primary);
    }

    .survey-question-index {
        display: inline-flex;
        min-width: 1.75rem;
        justify-content: center;
        margin-right: 0.55rem;
        border-radius: 9999px;
        background: color-mix(in srgb, var(--survey-primary) 8%, transparent);
        color: var(--survey-primary);
        font-size: 0.82rem;
        font-weight: 700;
    }

    .survey-question-text {
        font-family: "Iowan Old Style", "Palatino Linotype", "Book Antiqua", Georgia, serif;
        font-size: 1.08rem;
        line-height: 1.7;
        letter-spacing: 0.002em;
        font-weight: 650;
        color: color-mix(in srgb, var(--survey-text) 94%, #111827);
        text-wrap: pretty;
    }

    .survey-option-text {
        padding-right: 0.75rem;
        font-size: 0.97rem;
        line-height: 1.55;
        font-weight: 600;
        letter-spacing: 0.004em;
        color: color-mix(in srgb, var(--survey-text) 88%, #1f2937);
    }

    .survey-rank-option-text {
        font-size: 0.95rem;
        line-height: 1.5;
        font-weight: 600;
        color: color-mix(in srgb, var(--survey-text) 88%, #1f2937);
    }

    :global(.survey-input) {
        border-color: color-mix(in srgb, var(--survey-border) 48%, transparent);
        background: rgba(255, 255, 255, 0.9);
        box-shadow: inset 0 1px 2px rgba(16, 42, 67, 0.04);
    }

    :global(.survey-input:focus-visible) {
        outline: 2px solid var(--survey-accent);
        outline-offset: 1px;
        box-shadow: 0 0 0 3px color-mix(in srgb, var(--survey-accent) 18%, transparent);
    }

    :global(.survey-remark) {
        min-height: 6rem;
    }

    .survey-option-card {
        border-color: color-mix(in srgb, var(--survey-border) 56%, transparent);
        background: color-mix(in srgb, var(--survey-card) 92%, white);
        color: var(--survey-text);
    }

    .survey-option-card:hover {
        border-color: color-mix(in srgb, var(--survey-primary) 40%, transparent);
        background: color-mix(in srgb, var(--survey-accent-soft) 60%, var(--survey-card));
    }

    .survey-option-card--selected {
        border-color: var(--survey-primary);
        background: color-mix(in srgb, var(--survey-primary) 10%, var(--survey-card));
        color: var(--survey-primary);
    }

    .survey-radio-dot {
        border-color: color-mix(in srgb, var(--survey-border) 65%, transparent);
        background: color-mix(in srgb, var(--survey-card) 96%, white);
    }

    .survey-radio-dot--selected {
        border-color: var(--survey-primary);
        background: var(--survey-primary);
    }

    .survey-radio-dot-inner {
        background: var(--survey-primary-text);
    }

    .survey-checkbox {
        border-color: color-mix(in srgb, var(--survey-border) 65%, transparent);
        background: color-mix(in srgb, var(--survey-card) 96%, white);
        color: transparent;
    }

    .survey-checkbox--selected {
        border-color: var(--survey-primary);
        background: var(--survey-primary);
        color: var(--survey-primary-text);
    }

    .survey-rating-button {
        border-color: color-mix(in srgb, var(--survey-border) 56%, transparent);
        background: color-mix(in srgb, var(--survey-card) 92%, white);
        color: var(--survey-text);
    }

    .survey-rating-button:hover {
        border-color: color-mix(in srgb, var(--survey-primary) 40%, transparent);
        background: color-mix(in srgb, var(--survey-accent-soft) 60%, var(--survey-card));
    }

    .survey-rating-button--selected {
        border-color: var(--survey-primary);
        background: var(--survey-primary);
        color: var(--survey-primary-text);
    }

    :global(.survey-primary-button) {
        border: 1px solid #166534 !important;
        background: linear-gradient(135deg, #15803d 0%, #166534 100%) !important;
        color: #f8fafc !important;
        box-shadow: 0 14px 30px -24px rgba(22, 101, 52, 0.42);
    }

    :global(.survey-primary-button:hover) {
        border-color: #14532d !important;
        background: linear-gradient(135deg, #166534 0%, #14532d 100%) !important;
        color: #f8fafc !important;
        transform: translateY(-1px);
    }

    :global(.survey-secondary-button) {
        border-color: #6b7280 !important;
        background: linear-gradient(135deg, #f3f4f6 0%, #e5e7eb 100%) !important;
        color: #1f2937 !important;
    }

    :global(.survey-secondary-button:hover) {
        border-color: #4b5563 !important;
        background: linear-gradient(135deg, #e5e7eb 0%, #d1d5db 100%) !important;
        color: #111827 !important;
    }

    .campaign-branding-shell-header {
        position: relative;
        border-bottom: 1px solid color-mix(in srgb, var(--survey-accent) 22%, transparent);
        background:
            linear-gradient(180deg, color-mix(in srgb, var(--survey-shell) 96%, white), color-mix(in srgb, var(--survey-panel) 90%, white)),
            radial-gradient(circle at top left, color-mix(in srgb, var(--survey-accent) 14%, transparent), transparent 42%);
        backdrop-filter: blur(18px);
        box-shadow: inset 0 -1px 0 color-mix(in srgb, var(--survey-accent) 12%, transparent);
    }

    .campaign-branding-shell-header--flush {
        border-bottom: 0;
        background: transparent;
        box-shadow: none;
    }

    .campaign-branding-shell-header--flush::before,
    .campaign-branding-shell-header--flush::after,
    .campaign-branding-shell-footer::before,
    .campaign-branding-shell-footer::after {
        display: none;
    }

    .campaign-branding-shell-footer {
        position: relative;
        border-top: 1px solid color-mix(in srgb, var(--survey-accent) 18%, transparent);
        background:
            linear-gradient(180deg, color-mix(in srgb, var(--survey-footer-bg) 78%, white), color-mix(in srgb, var(--survey-footer-bg) 92%, var(--survey-shell))),
            radial-gradient(circle at bottom right, color-mix(in srgb, var(--survey-accent) 10%, transparent), transparent 40%);
        backdrop-filter: blur(14px);
    }

    .campaign-branding-content {
        position: relative;
        padding: 1.25rem 1.5rem;
    }

    .campaign-branding-content--flush {
        padding: 0;
    }

    .campaign-branding-content--edge {
        padding: 0;
        margin: 0;
    }

    .campaign-branding-content--flush :global(section:first-child),
    .campaign-branding-content--flush :global(div:first-child),
    .campaign-branding-content--flush :global(header:first-child),
    .campaign-branding-content--flush :global(footer:first-child) {
        width: 100%;
        margin: 0;
        border-radius: 0;
    }

    .campaign-branding-content--edge :global(*) {
        margin-top: 0;
    }

    .campaign-branding-content--edge :global(section),
    .campaign-branding-content--edge :global(div),
    .campaign-branding-content--edge :global(header),
    .campaign-branding-content--edge :global(footer) {
        max-width: 100%;
    }

    .campaign-branding-content--edge :global(section:first-child),
    .campaign-branding-content--edge :global(div:first-child),
    .campaign-branding-content--edge :global(header:first-child),
    .campaign-branding-content--edge :global(footer:first-child) {
        width: 100%;
        margin: 0;
        border: 0 !important;
        border-radius: 0 !important;
        box-shadow: none !important;
    }

    .campaign-branding-shell-footer:has(.campaign-branding-content--edge) {
        border-top: 0;
        background: transparent;
        box-shadow: none;
    }

    .campaign-branding-shell-header .campaign-branding-content:not(.campaign-branding-content--flush) {
        padding-bottom: 1.5rem;
    }

    .campaign-branding-shell-header .campaign-branding-content:not(.campaign-branding-content--flush)::after {
        content: "";
        position: absolute;
        inset: auto 1.5rem 0;
        height: 1px;
        background: linear-gradient(90deg, transparent, color-mix(in srgb, var(--survey-accent) 42%, transparent), transparent);
        pointer-events: none;
    }

    .campaign-branding-shell-footer .campaign-branding-content {
        padding-top: 1.5rem;
    }

    .campaign-branding-shell-footer .campaign-branding-content.campaign-branding-content--edge {
        padding-top: 0;
    }

    .survey-progress-panel {
        margin-top: 0.25rem;
        border-radius: 1rem;
        border: 1px solid color-mix(in srgb, var(--survey-accent) 18%, transparent);
        background: color-mix(in srgb, var(--survey-shell) 78%, var(--survey-accent-soft));
        padding: 0.85rem 1rem 0.9rem;
        box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.4);
    }

    .survey-progress-block {
        width: 100%;
        max-width: none;
        align-self: stretch;
    }

    .campaign-branding-content :global(h1),
    .campaign-branding-content :global(h2),
    .campaign-branding-content :global(h3),
    .campaign-branding-content :global(h4),
    .campaign-branding-content :global(h5),
    .campaign-branding-content :global(h6) {
        margin: 0;
        color: inherit;
        letter-spacing: -0.02em;
        font-weight: 700;
    }

    .campaign-branding-content :global(p) {
        margin: 0.5rem 0 0;
        color: inherit;
        line-height: 1.6;
    }

    .survey-copy-muted {
        color: var(--survey-muted);
    }

    .survey-progress-copy {
        color: color-mix(in srgb, var(--survey-header-text) 84%, var(--survey-primary));
    }

    .survey-progress-track {
        background: color-mix(in srgb, var(--survey-header-text) 20%, transparent);
    }

    .survey-progress-fill {
        background: linear-gradient(90deg, var(--survey-primary) 0%, var(--survey-accent) 52%, color-mix(in srgb, var(--survey-header-text) 70%, var(--survey-accent)) 100%);
    }

    .theme-studio-preview__header .survey-hero-description,
    .theme-studio-preview__header .survey-hero-note {
        color: inherit;
        opacity: 0.92;
    }

    .survey-structured-footer {
        margin: 0 1.5rem 1.5rem;
        padding: 1rem 1.25rem;
        border: 1px solid color-mix(in srgb, var(--survey-border) 42%, transparent);
        border-radius: 1rem;
        background: var(--survey-footer-bg);
        color: var(--survey-footer-text);
    }

    .survey-structured-footer__line1 {
        margin: 0;
        font-weight: 700;
    }

    .survey-structured-footer__line2,
    .survey-structured-footer__legal {
        margin: 0.4rem 0 0;
        line-height: 1.55;
    }

    @media (min-width: 768px) {
        .survey-intro-card {
            padding: 2rem;
        }

        .survey-question-card {
            padding: 1.55rem;
        }
    }
</style>
