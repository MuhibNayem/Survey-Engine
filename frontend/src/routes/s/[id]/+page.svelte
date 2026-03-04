<script lang="ts">
    import api from "$lib/api";
    import { onMount } from "svelte";
    import { page } from "$app/state";
    import { goto } from "$app/navigation";
    import * as Card from "$lib/components/ui/card";
    import { Button } from "$lib/components/ui/button";
    import { Input } from "$lib/components/ui/input";
    import { Label } from "$lib/components/ui/label";
    import { ChevronLeft, ChevronRight, Play } from "lucide-svelte";
    import type { CampaignPreviewResponse, QuestionType } from "$lib/types";

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
        questions: PreviewQuestion[];
    };

    type OidcStartResponse = {
        authorizationUrl: string;
        state: string;
        expiresAt: string;
    };

    const campaignId = $derived(page.params.id);

    let loading = $state(true);
    let error = $state<string | null>(null);
    let stage = $state<"intro" | "form" | "complete">("intro");
    let submitLoading = $state(false);
    let submitError = $state<string | null>(null);
    let authLoading = $state(false);
    let authError = $state<string | null>(null);
    let currentPageIndex = $state(0);
    let campaign = $state<CampaignPreviewResponse | null>(null);
    let responderToken = $state<string | null>(null);
    let responderAccessCode = $state<string | null>(null);
    let answers = $state<Record<string, AnswerValue>>({});
    let errors = $state<Record<string, string>>({});
    let respondent = $state({
        name: "",
        email: "",
        phone: "",
        address: "",
    });

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
    const progressPercent = $derived(
        totalPages <= 0
            ? 0
            : Math.round(((currentPageIndex + 1) / totalPages) * 100),
    );

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

    function getOptions(question: PreviewQuestion): string[] {
        const config = parseAnswerConfig(question.optionConfig);
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

    function validateRespondentFields(): boolean {
        const nextErrors: Record<string, string> = {};
        if (campaign?.collectName && respondent.name.trim().length === 0)
            nextErrors["meta.name"] = "Name is required";
        if (campaign?.collectEmail) {
            const email = respondent.email.trim();
            if (email.length === 0)
                nextErrors["meta.email"] = "Email is required";
            else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email))
                nextErrors["meta.email"] = "Enter a valid email";
        }
        if (campaign?.collectPhone && respondent.phone.trim().length === 0)
            nextErrors["meta.phone"] = "Phone is required";
        if (campaign?.collectAddress && respondent.address.trim().length === 0)
            nextErrors["meta.address"] = "Address is required";
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
        errors = nextErrors;
        return Object.keys(nextErrors).length === 0;
    }

    function buildRespondentIdentifier(): string | undefined {
        const email = respondent.email.trim();
        if (email) return email;
        const name = respondent.name.trim();
        if (name) return name;
        const phone = respondent.phone.trim();
        if (phone) return phone;
        const address = respondent.address.trim();
        if (address) return address;
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

    async function startPrivateAuth(): Promise<boolean> {
        if (!campaign || campaign.authMode !== "PRIVATE") return true;
        if (hasPrivateCredential) return true;
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

    async function handleStartSurvey() {
        submitError = null;
        if (campaign?.authMode === "PRIVATE" && !hasPrivateCredential) {
            const authReady = await startPrivateAuth();
            if (!authReady) return;
        }
        stage = "form";
    }

    async function submitResponse(): Promise<boolean> {
        if (!campaign) return false;
        if (campaign.authMode === "PRIVATE" && !hasPrivateCredential) {
            submitError =
                "Private authentication is required before submission.";
            return false;
        }

        const payloadAnswers: {
            questionId: string;
            questionVersionId: string;
            value: string;
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
                });
            }
        }

        submitLoading = true;
        submitError = null;
        try {
            await api.post("/responses", {
                campaignId: campaign.campaignId,
                respondentIdentifier: buildRespondentIdentifier(),
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

    async function goNext() {
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
        errors = {};
        submitError = null;
    }

    function goBack() {
        if (currentPageIndex <= 0) return;
        currentPageIndex -= 1;
        errors = {};
        submitError = null;
    }

    async function load() {
        loading = true;
        error = null;
        authError = null;
        try {
            const previewRes = await api.get<CampaignPreviewResponse>(
                `/public/campaigns/${campaignId}/preview`,
            );
            campaign = previewRes.data;
            readAuthParamsFromUrl();
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

<div class="min-h-screen bg-gradient-to-b from-slate-50 to-white py-8">
    <div class="mx-auto w-full max-w-4xl px-4">
        {#if loading}
            <div class="flex items-center justify-center py-16">
                <span
                    class="inline-block h-8 w-8 animate-spin rounded-full border-4 border-primary border-t-transparent"
                ></span>
            </div>
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
            <Card.Root
                class="border border-white/40 shadow-xl overflow-hidden bg-white/60 backdrop-blur-2xl transition-all duration-300"
            >
                {#if campaign.headerHtml}
                    <section
                        class="campaign-branding-shell campaign-branding-shell-header relative z-10 w-full"
                    >
                        <div class="campaign-branding-content">
                            {@html campaign.headerHtml}
                        </div>
                    </section>
                {/if}

                <Card.Header
                    class="relative space-y-4 border-b border-border/30 bg-gradient-to-br from-primary/5 via-transparent to-muted/10 pb-7 pt-8 z-0"
                >
                    <Card.Title
                        class="text-3xl font-extrabold tracking-tight bg-gradient-to-r from-foreground to-foreground/70 bg-clip-text text-transparent"
                        >{campaign.campaignName}</Card.Title
                    >
                    <Card.Description
                        class="text-base font-medium text-muted-foreground/90"
                        >{campaign.surveyTitle}</Card.Description
                    >

                    {#if stage === "form" && campaign.showProgressIndicator}
                        <div class="space-y-2 pt-3">
                            <div
                                class="h-1.5 w-full overflow-hidden rounded-full bg-black/5 shadow-inner backdrop-blur-sm"
                            >
                                <div
                                    class="h-full rounded-full bg-gradient-to-r from-primary/80 to-primary transition-all duration-700 ease-out shadow-sm"
                                    style={`width:${progressPercent}%`}
                                ></div>
                            </div>
                            <p
                                class="text-[11px] font-bold tracking-wider text-primary/70 uppercase"
                            >
                                Progress: {Math.round(progressPercent)}% &bull;
                                Page {currentPageIndex + 1} of {totalPages}
                            </p>
                        </div>
                    {/if}
                </Card.Header>

                <Card.Content class="p-0 relative z-0">
                    <section class="space-y-8 p-6 sm:p-8">
                        {#if stage === "intro"}
                            <div class="space-y-4">
                                {#if campaign.startMessage}
                                    <p
                                        class="text-sm leading-6 text-foreground whitespace-pre-wrap"
                                    >
                                        {campaign.startMessage}
                                    </p>
                                {/if}
                                {#if campaign.authMode === "PRIVATE"}
                                    <p class="text-sm text-muted-foreground">
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
                            <div class="space-y-4">
                                <h3 class="text-lg font-semibold">Thank You</h3>
                                {#if campaign.finishMessage}
                                    <p
                                        class="text-sm leading-6 text-foreground whitespace-pre-wrap"
                                    >
                                        {campaign.finishMessage}
                                    </p>
                                {/if}
                            </div>
                        {:else if isPrivateCampaign && !hasPrivateCredential}
                            <div class="space-y-4">
                                <p class="text-sm text-muted-foreground">
                                    Private authentication is required before
                                    filling this survey.
                                </p>
                                {#if authError}
                                    <p class="text-sm text-destructive">
                                        {authError}
                                    </p>
                                {/if}
                                <Button
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
                            <div class="space-y-6">
                                {#if campaign.collectName || campaign.collectEmail || campaign.collectPhone || campaign.collectAddress}
                                    <Card.Root class="border border-border/70">
                                        <Card.Header class="pb-3">
                                            <Card.Title class="text-base"
                                                >Your Information</Card.Title
                                            >
                                        </Card.Header>
                                        <Card.Content
                                            class="grid gap-3 sm:grid-cols-2"
                                        >
                                            {#if campaign.collectName}
                                                <div class="space-y-1">
                                                    <Label for="meta-name"
                                                        >Name</Label
                                                    >
                                                    <Input
                                                        id="meta-name"
                                                        bind:value={
                                                            respondent.name
                                                        }
                                                    />
                                                    {#if errors["meta.name"]}<p
                                                            class="text-xs text-destructive"
                                                        >
                                                            {errors[
                                                                "meta.name"
                                                            ]}
                                                        </p>{/if}
                                                </div>
                                            {/if}
                                            {#if campaign.collectEmail}
                                                <div class="space-y-1">
                                                    <Label for="meta-email"
                                                        >Email</Label
                                                    >
                                                    <Input
                                                        id="meta-email"
                                                        bind:value={
                                                            respondent.email
                                                        }
                                                    />
                                                    {#if errors["meta.email"]}<p
                                                            class="text-xs text-destructive"
                                                        >
                                                            {errors[
                                                                "meta.email"
                                                            ]}
                                                        </p>{/if}
                                                </div>
                                            {/if}
                                            {#if campaign.collectPhone}
                                                <div class="space-y-1">
                                                    <Label for="meta-phone"
                                                        >Phone</Label
                                                    >
                                                    <Input
                                                        id="meta-phone"
                                                        bind:value={
                                                            respondent.phone
                                                        }
                                                    />
                                                    {#if errors["meta.phone"]}<p
                                                            class="text-xs text-destructive"
                                                        >
                                                            {errors[
                                                                "meta.phone"
                                                            ]}
                                                        </p>{/if}
                                                </div>
                                            {/if}
                                            {#if campaign.collectAddress}
                                                <div
                                                    class="space-y-1 sm:col-span-2"
                                                >
                                                    <Label for="meta-address"
                                                        >Address</Label
                                                    >
                                                    <Input
                                                        id="meta-address"
                                                        bind:value={
                                                            respondent.address
                                                        }
                                                    />
                                                    {#if errors["meta.address"]}<p
                                                            class="text-xs text-destructive"
                                                        >
                                                            {errors[
                                                                "meta.address"
                                                            ]}
                                                        </p>{/if}
                                                </div>
                                            {/if}
                                        </Card.Content>
                                    </Card.Root>
                                {/if}

                                <Card.Root class="border border-border/70">
                                    <Card.Header class="pb-3">
                                        <Card.Title class="text-base"
                                            >{currentPage.title}</Card.Title
                                        >
                                    </Card.Header>
                                    <Card.Content class="space-y-6">
                                        {#each currentPage.questions as question, qIdx}
                                            <div class="space-y-3">
                                                <p
                                                    class="font-medium leading-6"
                                                >
                                                    {#if campaign.showQuestionNumbers}
                                                        <span
                                                            class="text-muted-foreground mr-1"
                                                            >{questionSerial(
                                                                currentPageIndex,
                                                                qIdx,
                                                            )}.</span
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
                                                                    class="pr-3 font-medium"
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
                                                                            class="h-2.5 w-2.5 rounded-full bg-primary-foreground"
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
                                                                    class="pr-3 font-medium"
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
                                                                    class="text-sm"
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
                                                                    <Button
                                                                        type="button"
                                                                        variant="outline"
                                                                        size="sm"
                                                                        disabled={idx ===
                                                                            ensureRankOrder(
                                                                                question,
                                                                            )
                                                                                .length -
                                                                                1}
                                                                        onclick={() =>
                                                                            moveRankOption(
                                                                                question,
                                                                                idx,
                                                                                1,
                                                                            )}
                                                                    >
                                                                        <ChevronRight
                                                                            class="h-4 w-4"
                                                                        />
                                                                    </Button>
                                                                </div>
                                                            </div>
                                                        {/each}
                                                    </div>
                                                {:else}
                                                    <div
                                                        class="flex flex-wrap gap-2"
                                                    >
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
                                                    </div>
                                                {/if}

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
                                    </Card.Content>
                                </Card.Root>

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

                                    <Button
                                        type="button"
                                        onclick={goNext}
                                        disabled={submitLoading}
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
                                {#if submitError}
                                    <p class="text-sm text-destructive">
                                        {submitError}
                                    </p>
                                {/if}
                            </div>
                        {/if}
                    </section>

                    {#if campaign.footerHtml}
                        <section
                            class="campaign-branding-shell campaign-branding-shell-footer"
                        >
                            <div class="campaign-branding-content">
                                {@html campaign.footerHtml}
                            </div>
                        </section>
                    {/if}
                </Card.Content>
            </Card.Root>
        {/if}
    </div>
</div>

<style>
    .campaign-branding-shell {
        /* Base styles if needed by dynamically injected elements */
    }

    .campaign-branding-shell-header {
        border-bottom: 1px solid hsla(var(--border) / 0.3);
        background: linear-gradient(
            135deg,
            hsla(var(--primary) / 0.15),
            hsla(var(--primary) / 0.05)
        );
        backdrop-filter: blur(16px);
        box-shadow: 0 4px 24px -12px hsla(var(--primary) / 0.3);
    }

    .campaign-branding-shell-footer {
        border-top: 1px solid hsla(var(--border) / 0.3);
        background: linear-gradient(
            180deg,
            hsla(var(--background) / 0.1),
            hsla(var(--muted) / 0.5)
        );
        backdrop-filter: blur(12px);
    }

    .campaign-branding-content {
        padding: 1.5rem 2rem;
    }

    .campaign-branding-content :global(h1),
    .campaign-branding-content :global(h2),
    .campaign-branding-content :global(h3),
    .campaign-branding-content :global(h4),
    .campaign-branding-content :global(h5),
    .campaign-branding-content :global(h6) {
        margin: 0;
        color: hsl(var(--foreground));
        letter-spacing: -0.02em;
        font-weight: 700;
    }

    .campaign-branding-content :global(p) {
        margin: 0.5rem 0 0;
        color: hsl(var(--muted-foreground));
        line-height: 1.6;
    }
</style>
