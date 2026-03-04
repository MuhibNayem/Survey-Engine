<script lang="ts">
    import api from "$lib/api";
    import { onMount } from "svelte";
    import { page } from "$app/state";
    import { goto } from "$app/navigation";
    import * as Card from "$lib/components/ui/card";
    import { Button } from "$lib/components/ui/button";
    import { Input } from "$lib/components/ui/input";
    import { Label } from "$lib/components/ui/label";
    import { Badge } from "$lib/components/ui/badge";
    import { ArrowLeft, RefreshCw, Play, ChevronLeft, ChevronRight } from "lucide-svelte";
    import type {
        CampaignPreviewResponse,
        QuestionType,
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
    const progressPercent = $derived(
        totalPages <= 0 ? 0 : Math.round(((currentPageIndex + 1) / totalPages) * 100),
    );

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

    function validateRespondentFields(): boolean {
        const nextErrors: Record<string, string> = {};
        if (campaign?.collectName && respondent.name.trim().length === 0) {
            nextErrors["meta.name"] = "Name is required";
        }
        if (campaign?.collectEmail) {
            const email = respondent.email.trim();
            if (email.length === 0) {
                nextErrors["meta.email"] = "Email is required";
            } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
                nextErrors["meta.email"] = "Enter a valid email";
            }
        }
        if (campaign?.collectPhone && respondent.phone.trim().length === 0) {
            nextErrors["meta.phone"] = "Phone is required";
        }
        if (campaign?.collectAddress && respondent.address.trim().length === 0) {
            nextErrors["meta.address"] = "Address is required";
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
        errors = nextErrors;
        return Object.keys(nextErrors).length === 0;
    }

    function goNext() {
        const respondentValid = validateRespondentFields();
        const pageValid = validateCurrentPage();
        if (!respondentValid || !pageValid) return;
        if (currentPageIndex >= totalPages - 1) {
            stage = "complete";
            return;
        }
        currentPageIndex += 1;
        errors = {};
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
        respondent = { name: "", email: "", phone: "", address: "" };
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
    <div class="flex items-center justify-center py-16">
        <span class="inline-block h-8 w-8 animate-spin rounded-full border-4 border-primary border-t-transparent"></span>
    </div>
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
    <div class="space-y-6">
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

        <Card.Root class="border-2">
            <Card.Header class="space-y-3">
                <div class="flex items-center justify-between gap-4">
                    <div>
                        <Card.Title>{campaign.campaignName}</Card.Title>
                        <Card.Description>{campaign.surveyTitle}</Card.Description>
                    </div>
                    {#if stage === "form" && campaign.showProgressIndicator}
                        <Badge variant="outline">Page {currentPageIndex + 1} / {totalPages}</Badge>
                    {/if}
                </div>

                {#if stage === "form" && campaign.showProgressIndicator}
                    <div class="space-y-1">
                        <div class="h-2 w-full overflow-hidden rounded-full bg-muted">
                            <div class="h-full rounded-full bg-primary transition-all" style={`width:${progressPercent}%`}></div>
                        </div>
                        <p class="text-xs text-muted-foreground">{progressPercent}% completed</p>
                    </div>
                {/if}
            </Card.Header>

            <Card.Content class="p-0">
                {#if campaign.headerHtml}
                    <section class="campaign-branding-shell campaign-branding-shell-header">
                        <div class="campaign-branding-content">{@html campaign.headerHtml}</div>
                    </section>
                {/if}

                <section class="space-y-6 p-5 sm:p-7">
                    {#if stage === "intro"}
                    <div class="space-y-4">
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
                    <div class="space-y-4">
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
                        {#if campaign.collectName || campaign.collectEmail || campaign.collectPhone || campaign.collectAddress}
                            <Card.Root class="border border-border/70">
                                <Card.Header class="pb-3">
                                    <Card.Title class="text-base">Responder Information</Card.Title>
                                    <Card.Description>Visible because data collection is enabled in campaign settings</Card.Description>
                                </Card.Header>
                                <Card.Content class="grid gap-3 sm:grid-cols-2">
                                    {#if campaign.collectName}
                                        <div class="space-y-1">
                                            <Label for="meta-name">Name</Label>
                                            <Input id="meta-name" bind:value={respondent.name} />
                                            {#if errors["meta.name"]}<p class="text-xs text-destructive">{errors["meta.name"]}</p>{/if}
                                        </div>
                                    {/if}
                                    {#if campaign.collectEmail}
                                        <div class="space-y-1">
                                            <Label for="meta-email">Email</Label>
                                            <Input id="meta-email" bind:value={respondent.email} />
                                            {#if errors["meta.email"]}<p class="text-xs text-destructive">{errors["meta.email"]}</p>{/if}
                                        </div>
                                    {/if}
                                    {#if campaign.collectPhone}
                                        <div class="space-y-1">
                                            <Label for="meta-phone">Phone</Label>
                                            <Input id="meta-phone" bind:value={respondent.phone} />
                                            {#if errors["meta.phone"]}<p class="text-xs text-destructive">{errors["meta.phone"]}</p>{/if}
                                        </div>
                                    {/if}
                                    {#if campaign.collectAddress}
                                        <div class="space-y-1 sm:col-span-2">
                                            <Label for="meta-address">Address</Label>
                                            <Input id="meta-address" bind:value={respondent.address} />
                                            {#if errors["meta.address"]}<p class="text-xs text-destructive">{errors["meta.address"]}</p>{/if}
                                        </div>
                                    {/if}
                                </Card.Content>
                            </Card.Root>
                        {/if}

                        <Card.Root class="border border-border/70">
                            <Card.Header class="pb-3">
                                <Card.Title class="text-base">{currentPage.title}</Card.Title>
                                <Card.Description>{currentPage.questions.length} question(s) on this page</Card.Description>
                            </Card.Header>
                            <Card.Content class="space-y-6">
                                {#each currentPage.questions as question, qIdx}
                                    <div class="space-y-3">
                                        <div class="space-y-1">
                                            <p class="font-medium leading-6">
                                                {#if campaign.showQuestionNumbers}
                                                    <span class="text-muted-foreground mr-1">
                                                        {questionSerial(currentPageIndex, qIdx)}.
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
                                            </div>
                                        {/if}

                                        {#if errors[question.questionId]}
                                            <p class="text-xs text-destructive">{errors[question.questionId]}</p>
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

                            <Button type="button" onclick={goNext}>
                                {currentPageIndex === totalPages - 1 ? "Finish Preview" : "Next"}
                            </Button>
                        </div>
                    </div>
                    {/if}
                </section>

                {#if campaign.footerHtml}
                    <section class="campaign-branding-shell campaign-branding-shell-footer">
                        <div class="campaign-branding-content">{@html campaign.footerHtml}</div>
                    </section>
                {/if}
            </Card.Content>
        </Card.Root>
    </div>
{/if}

<style>
    .campaign-branding-shell {
        border-top: 1px solid hsl(var(--border));
        background: linear-gradient(180deg, hsl(var(--muted) / 0.3), hsl(var(--background)));
    }

    .campaign-branding-shell-footer {
        border-top: 1px solid hsl(var(--border));
        background: linear-gradient(180deg, hsl(var(--background)), hsl(var(--muted) / 0.35));
    }

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
        color: hsl(var(--foreground));
        letter-spacing: -0.01em;
    }

    .campaign-branding-content :global(p) {
        margin: 0.45rem 0 0;
        color: hsl(var(--muted-foreground));
        line-height: 1.55;
    }
</style>
