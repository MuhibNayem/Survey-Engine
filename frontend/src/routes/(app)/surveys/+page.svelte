<script lang="ts">
    import api from "$lib/api";
    import { onMount } from "svelte";
    import * as Card from "$lib/components/ui/card";
    import { Button } from "$lib/components/ui/button";
    import { Input } from "$lib/components/ui/input";
    import { Label } from "$lib/components/ui/label";
    import { Badge } from "$lib/components/ui/badge";
    import { Textarea } from "$lib/components/ui/textarea";
    import * as Select from "$lib/components/ui/select";
    import { ConfirmDialog } from "$lib/components/ui/confirm-dialog";
    import PageHeader from "$lib/components/layout/PageHeader.svelte";
    import {
        Plus,
        Pencil,
        Trash2,
        Search,
        X,
        Settings,
        FileText,
        ChevronRight,
        ArrowRightCircle,
    } from "lucide-svelte";
    import type {
        SurveyResponse,
        SurveyLifecycleState,
        QuestionResponse,
        CategoryResponse,
        SurveyPageRequest,
        SurveyQuestionRequest,
    } from "$lib/types";

    // --- State ---
    let surveys = $state<SurveyResponse[]>([]);
    let allQuestions = $state<QuestionResponse[]>([]);
    let allCategories = $state<CategoryResponse[]>([]);
    let loading = $state(true);
    let searchQuery = $state("");
    let stateFilter = $state<SurveyLifecycleState | "">("");

    // Create/Edit Dialog
    let dialogOpen = $state(false);
    let editingSurvey = $state<SurveyResponse | null>(null);
    let formTitle = $state("");
    let formDescription = $state("");
    let formPages = $state<
        {
            title: string;
            sortOrder: number;
            questions: {
                questionId: string;
                categoryId?: string;
                sortOrder: number;
                mandatory: boolean;
                parsedConfig: any;
                showConfig?: boolean;
            }[];
        }[]
    >([]);
    let formLoading = $state(false);
    let formError = $state<string | null>(null);

    // Delete
    let deleteTarget = $state<SurveyResponse | null>(null);
    let deleteLoading = $state(false);

    // Lifecycle
    let lifecycleTarget = $state<SurveyResponse | null>(null);
    let lifecycleState = $state<SurveyLifecycleState>("PUBLISHED");
    let lifecycleLoading = $state(false);

    // --- Computed ---
    const filteredSurveys = $derived(
        surveys.filter((s) => {
            const matchesSearch =
                !searchQuery ||
                s.title.toLowerCase().includes(searchQuery.toLowerCase());
            const matchesState =
                !stateFilter || s.lifecycleState === stateFilter;
            return matchesSearch && matchesState;
        }),
    );

    const lifecycleStates: SurveyLifecycleState[] = [
        "DRAFT",
        "PUBLISHED",
        "CLOSED",
        "RESULTS_PUBLISHED",
        "ARCHIVED",
    ];

    // Valid transitions from a given state
    function validTransitions(
        current: SurveyLifecycleState,
    ): SurveyLifecycleState[] {
        switch (current) {
            case "DRAFT":
                return ["PUBLISHED"];
            case "PUBLISHED":
                return ["CLOSED"];
            case "CLOSED":
                return ["RESULTS_PUBLISHED", "PUBLISHED"];
            case "RESULTS_PUBLISHED":
                return ["ARCHIVED"];
            case "ARCHIVED":
                return [];
            default:
                return [];
        }
    }

    function stateBadgeVariant(state: SurveyLifecycleState) {
        switch (state) {
            case "DRAFT":
                return "secondary" as const;
            case "PUBLISHED":
                return "default" as const;
            case "CLOSED":
                return "outline" as const;
            case "RESULTS_PUBLISHED":
                return "default" as const;
            case "ARCHIVED":
                return "secondary" as const;
            default:
                return "secondary" as const;
        }
    }

    // --- API ---
    async function loadData() {
        loading = true;
        try {
            const [sRes, qRes, cRes] = await Promise.all([
                api.get<SurveyResponse[]>("/surveys"),
                api.get<QuestionResponse[]>("/questions"),
                api.get<CategoryResponse[]>("/categories"),
            ]);
            surveys = sRes.data;
            allQuestions = qRes.data;
            allCategories = cRes.data;
        } catch {
            surveys = [];
        } finally {
            loading = false;
        }
    }

    function openCreateDialog() {
        editingSurvey = null;
        formTitle = "";
        formDescription = "";
        formPages = [{ title: "Page 1", sortOrder: 1, questions: [] }];
        formError = null;
        dialogOpen = true;
    }

    function openEditDialog(s: SurveyResponse) {
        editingSurvey = s;
        formTitle = s.title;
        formDescription = s.description;
        formPages = s.pages.map((p) => ({
            title: p.title,
            sortOrder: p.sortOrder,
            questions: p.questions.map((q) => {
                let parsedConfig = {};
                if (q.answerConfig) {
                    try {
                        parsedConfig = JSON.parse(q.answerConfig);
                    } catch (e) {
                        // ignore
                    }
                }
                return {
                    questionId: q.questionId,
                    categoryId: q.categoryId,
                    sortOrder: q.sortOrder,
                    mandatory: q.mandatory,
                    parsedConfig,
                    showConfig: false,
                };
            }),
        }));
        formError = null;
        dialogOpen = true;
    }

    function addPage() {
        formPages = [
            ...formPages,
            {
                title: `Page ${formPages.length + 1}`,
                sortOrder: formPages.length + 1,
                questions: [],
            },
        ];
    }

    function removePage(pIdx: number) {
        formPages = formPages.filter((_, i) => i !== pIdx);
        formPages = formPages.map((p, i) => ({
            ...p,
            sortOrder: i + 1,
        }));
    }

    function addQuestionToPage(pIdx: number) {
        const page = formPages[pIdx];
        page.questions = [
            ...page.questions,
            {
                questionId: "",
                sortOrder: page.questions.length + 1,
                mandatory: true,
                parsedConfig: {},
                showConfig: true,
            },
        ];
        formPages = [...formPages];
    }

    function removeQuestionFromPage(pIdx: number, qIdx: number) {
        const page = formPages[pIdx];
        page.questions = page.questions.filter((_, i) => i !== qIdx);
        page.questions = page.questions.map((q, i) => ({
            ...q,
            sortOrder: i + 1,
        }));
        formPages = [...formPages];
    }

    function addCategoryToPage(pIdx: number, categoryId: string) {
        const category = allCategories.find((c) => c.id === categoryId);
        if (!category) return;

        const page = formPages[pIdx];
        const nextSortOrder =
            page.questions.length > 0
                ? Math.max(...page.questions.map((q) => q.sortOrder)) + 1
                : 1;

        const newQuestions = category.questionMappings
            .sort((a, b) => a.sortOrder - b.sortOrder)
            .map((mapping, i) => ({
                questionId: mapping.questionId,
                categoryId: category.id,
                sortOrder: nextSortOrder + i,
                mandatory: true,
                parsedConfig: {},
                showConfig: false,
            }));

        page.questions = [...page.questions, ...newQuestions];
        formPages = [...formPages];
    }

    function getCategoryName(id: string): string {
        return allCategories.find((c) => c.id === id)?.name ?? "Misc";
    }

    function getQuestionText(id: string): string {
        const q = allQuestions.find((q) => q.id === id);
        return q?.text ?? "";
    }

    async function handleSubmit(e: Event) {
        e.preventDefault();
        formLoading = true;
        formError = null;
        try {
            const payload = {
                title: formTitle,
                description: formDescription,
                pages: formPages.map((p) => ({
                    title: p.title,
                    sortOrder: p.sortOrder,
                    questions: p.questions.map((q) => {
                        const answerConfigStr =
                            q.parsedConfig &&
                            Object.keys(q.parsedConfig).length > 0
                                ? JSON.stringify(q.parsedConfig)
                                : undefined;
                        return {
                            questionId: q.questionId,
                            categoryId: q.categoryId,
                            sortOrder: q.sortOrder,
                            mandatory: q.mandatory,
                            answerConfig: answerConfigStr,
                        } as SurveyQuestionRequest;
                    }),
                })) as SurveyPageRequest[],
            };
            if (editingSurvey) {
                await api.put(`/surveys/${editingSurvey.id}`, payload);
            } else {
                await api.post("/surveys", payload);
            }
            dialogOpen = false;
            await loadData();
        } catch (err: unknown) {
            const axiosErr = err as {
                response?: { data?: { message?: string } };
            };
            formError =
                axiosErr?.response?.data?.message ?? "Something went wrong.";
        } finally {
            formLoading = false;
        }
    }

    async function handleDelete() {
        if (!deleteTarget) return;
        deleteLoading = true;
        try {
            await api.delete(`/surveys/${deleteTarget.id}`);
            deleteTarget = null;
            await loadData();
        } catch {
            // silent
        } finally {
            deleteLoading = false;
        }
    }

    function openLifecycleDialog(s: SurveyResponse) {
        lifecycleTarget = s;
        const transitions = validTransitions(s.lifecycleState);
        lifecycleState = transitions[0] ?? "PUBLISHED";
    }

    async function handleLifecycleTransition() {
        if (!lifecycleTarget) return;
        lifecycleLoading = true;
        try {
            await api.post(`/surveys/${lifecycleTarget.id}/lifecycle`, {
                targetState: lifecycleState,
            });
            lifecycleTarget = null;
            await loadData();
        } catch {
            // silent
        } finally {
            lifecycleLoading = false;
        }
    }

    function formatDate(iso: string) {
        return new Date(iso).toLocaleDateString("en-US", {
            month: "short",
            day: "numeric",
            year: "numeric",
        });
    }

    onMount(loadData);
</script>

<svelte:head>
    <title>Surveys — Survey Engine</title>
</svelte:head>

<div class="space-y-6">
    <PageHeader
        title="Surveys"
        description="Design and manage your survey instruments"
        actionLabel="New Survey"
        actionIcon={Plus}
        onAction={openCreateDialog}
    />

    <!-- Filters -->
    <div class="flex flex-col gap-3 sm:flex-row sm:items-center">
        <div class="relative flex-1">
            <Search
                class="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground"
            />
            <Input
                placeholder="Search surveys..."
                bind:value={searchQuery}
                class="pl-9"
            />
        </div>
        <Select.Root type="single" bind:value={stateFilter}>
            <Select.Trigger class="w-full sm:w-48">
                {stateFilter ? stateFilter.replace("_", " ") : "All States"}
            </Select.Trigger>
            <Select.Content>
                <Select.Item value="">All States</Select.Item>
                {#each lifecycleStates as s}
                    <Select.Item value={s}>{s.replace("_", " ")}</Select.Item>
                {/each}
            </Select.Content>
        </Select.Root>
    </div>

    <!-- Table -->
    {#if loading}
        <div class="flex items-center justify-center py-16">
            <span
                class="inline-block h-8 w-8 animate-spin rounded-full border-4 border-primary border-t-transparent"
            ></span>
        </div>
    {:else if filteredSurveys.length === 0}
        <Card.Root
            class="flex flex-col items-center justify-center py-16 text-center"
        >
            <Card.Content>
                <div
                    class="mx-auto mb-4 flex h-16 w-16 items-center justify-center rounded-2xl bg-muted"
                >
                    <FileText class="h-8 w-8 text-muted-foreground" />
                </div>
                <h3 class="text-lg font-semibold text-foreground">
                    {searchQuery || stateFilter
                        ? "No surveys match your filters"
                        : "No surveys yet"}
                </h3>
                <p class="mt-1 text-sm text-muted-foreground">
                    {searchQuery || stateFilter
                        ? "Try adjusting your search or filter."
                        : "Create your first survey to start collecting data."}
                </p>
                {#if !searchQuery && !stateFilter}
                    <Button class="mt-4" onclick={openCreateDialog}>
                        <Plus class="mr-2 h-4 w-4" />
                        Create Survey
                    </Button>
                {/if}
            </Card.Content>
        </Card.Root>
    {:else}
        <Card.Root>
            <div class="overflow-x-auto">
                <table class="w-full text-sm">
                    <thead>
                        <tr
                            class="border-b border-border text-left text-muted-foreground"
                        >
                            <th class="px-4 py-3 font-medium">Title</th>
                            <th class="px-4 py-3 font-medium">State</th>
                            <th class="px-4 py-3 font-medium text-center"
                                >Pages</th
                            >
                            <th class="px-4 py-3 font-medium">Created</th>
                            <th class="px-4 py-3 font-medium text-right"
                                >Actions</th
                            >
                        </tr>
                    </thead>
                    <tbody>
                        {#each filteredSurveys as survey (survey.id)}
                            <tr
                                class="border-b border-border/50 transition-colors hover:bg-muted/30"
                            >
                                <td class="px-4 py-3">
                                    <div>
                                        <p
                                            class="font-medium text-foreground truncate max-w-xs"
                                        >
                                            {survey.title}
                                        </p>
                                        {#if survey.description}
                                            <p
                                                class="text-xs text-muted-foreground truncate max-w-xs mt-0.5"
                                            >
                                                {survey.description}
                                            </p>
                                        {/if}
                                    </div>
                                </td>
                                <td class="px-4 py-3">
                                    <Badge
                                        variant={stateBadgeVariant(
                                            survey.lifecycleState,
                                        )}
                                    >
                                        {survey.lifecycleState.replace(
                                            "_",
                                            " ",
                                        )}
                                    </Badge>
                                </td>
                                <td class="px-4 py-3 text-center">
                                    <Badge variant="secondary">
                                        {survey.pages.length}
                                    </Badge>
                                </td>
                                <td
                                    class="px-4 py-3 text-muted-foreground whitespace-nowrap"
                                >
                                    {formatDate(survey.createdAt)}
                                </td>
                                <td class="px-4 py-3">
                                    <div
                                        class="flex items-center justify-end gap-1"
                                    >
                                        {#if validTransitions(survey.lifecycleState).length > 0}
                                            <Button
                                                variant="ghost"
                                                size="sm"
                                                onclick={() =>
                                                    openLifecycleDialog(survey)}
                                            >
                                                <ArrowRightCircle
                                                    class="h-4 w-4 text-primary"
                                                />
                                            </Button>
                                        {/if}
                                        {#if survey.lifecycleState === "DRAFT"}
                                            <Button
                                                variant="ghost"
                                                size="sm"
                                                onclick={() =>
                                                    openEditDialog(survey)}
                                            >
                                                <Pencil class="h-4 w-4" />
                                            </Button>
                                        {/if}
                                        <Button
                                            variant="ghost"
                                            size="sm"
                                            onclick={() =>
                                                (deleteTarget = survey)}
                                        >
                                            <Trash2
                                                class="h-4 w-4 text-destructive"
                                            />
                                        </Button>
                                    </div>
                                </td>
                            </tr>
                        {/each}
                    </tbody>
                </table>
            </div>
            <div class="border-t border-border px-4 py-3">
                <p class="text-xs text-muted-foreground">
                    {filteredSurveys.length} of {surveys.length} surveys
                </p>
            </div>
        </Card.Root>
    {/if}
</div>

<!-- Create/Edit Dialog -->
{#if dialogOpen}
    <div class="fixed inset-0 z-50 flex items-center justify-center">
        <button
            class="absolute inset-0 bg-black/50 backdrop-blur-sm"
            onclick={() => (dialogOpen = false)}
            aria-label="Close dialog"
        ></button>
        <Card.Root
            class="relative z-10 w-full max-w-3xl mx-4 max-h-[90vh] overflow-y-auto shadow-2xl border-border"
        >
            <Card.Header>
                <div class="flex items-center justify-between">
                    <div>
                        <Card.Title>
                            {editingSurvey ? "Edit Survey" : "New Survey"}
                        </Card.Title>
                        <Card.Description>
                            {editingSurvey
                                ? "Update survey structure and questions."
                                : "Create a new survey with pages and questions."}
                        </Card.Description>
                    </div>
                    <Button
                        variant="ghost"
                        size="sm"
                        onclick={() => (dialogOpen = false)}
                    >
                        <X class="h-4 w-4" />
                    </Button>
                </div>
            </Card.Header>
            <Card.Content>
                <form onsubmit={handleSubmit} class="space-y-6">
                    {#if formError}
                        <div
                            class="rounded-lg border border-destructive/30 bg-destructive/10 px-4 py-3 text-sm text-destructive"
                        >
                            {formError}
                        </div>
                    {/if}

                    <!-- Basic Info -->
                    <div class="space-y-4">
                        <div class="space-y-2">
                            <Label for="s-title">Title</Label>
                            <Input
                                id="s-title"
                                placeholder="e.g., Employee Satisfaction Survey"
                                bind:value={formTitle}
                                required
                            />
                        </div>
                        <div class="space-y-2">
                            <Label for="s-desc">Description</Label>
                            <Textarea
                                id="s-desc"
                                placeholder="What"
                                bind:value={formDescription}
                            />
                        </div>
                    </div>

                    <!-- Pages -->
                    <div class="space-y-4">
                        <div class="flex items-center justify-between">
                            <Label class="text-base font-semibold"
                                >Survey Pages</Label
                            >
                            <Button
                                variant="outline"
                                size="sm"
                                type="button"
                                onclick={addPage}
                            >
                                <Plus class="mr-1 h-3 w-3" />
                                Add Page
                            </Button>
                        </div>

                        {#each formPages as page, pIdx}
                            <Card.Root class="border-border/70">
                                <Card.Header class="py-3 px-4">
                                    <div
                                        class="flex items-center justify-between"
                                    >
                                        <div
                                            class="flex items-center gap-2 flex-1"
                                        >
                                            <Badge variant="secondary"
                                                >Page {page.sortOrder}</Badge
                                            >
                                            <Input
                                                placeholder="Page title"
                                                bind:value={
                                                    formPages[pIdx].title
                                                }
                                                class="h-8"
                                                required
                                            />
                                        </div>
                                        {#if formPages.length > 1}
                                            <Button
                                                variant="ghost"
                                                size="sm"
                                                type="button"
                                                onclick={() => removePage(pIdx)}
                                            >
                                                <X
                                                    class="h-4 w-4 text-destructive"
                                                />
                                            </Button>
                                        {/if}
                                    </div>
                                </Card.Header>
                                <Card.Content class="pt-0 space-y-2">
                                    {#if page.questions.length === 0}
                                        <div
                                            class="rounded-lg border-2 border-dashed border-border px-4 py-4 text-center text-xs text-muted-foreground"
                                        >
                                            No questions on this page yet.
                                        </div>
                                    {:else}
                                        {#each page.questions as q, qIdx}
                                            <div
                                                class="flex flex-col gap-2 rounded-md border border-border bg-muted/20 p-2"
                                            >
                                                <div
                                                    class="flex items-center gap-2"
                                                >
                                                    <span
                                                        class="shrink-0 text-xs font-medium text-muted-foreground w-6"
                                                    >
                                                        #{q.sortOrder}
                                                    </span>
                                                    {#if q.categoryId}
                                                        <Badge
                                                            variant="outline"
                                                            class="mr-2 text-[10px] py-0 h-5 shrink-0 uppercase"
                                                            >{getCategoryName(
                                                                q.categoryId,
                                                            )}</Badge
                                                        >
                                                    {/if}
                                                    <Select.Root
                                                        type="single"
                                                        bind:value={
                                                            formPages[pIdx]
                                                                .questions[qIdx]
                                                                .questionId
                                                        }
                                                    >
                                                        <Select.Trigger
                                                            class="flex-1 h-8 text-xs"
                                                        >
                                                            {formPages[pIdx]
                                                                .questions[qIdx]
                                                                .questionId
                                                                ? allQuestions.find(
                                                                      (aq) =>
                                                                          aq.id ===
                                                                          formPages[
                                                                              pIdx
                                                                          ]
                                                                              .questions[
                                                                              qIdx
                                                                          ]
                                                                              .questionId,
                                                                  )?.text
                                                                : "Select question"}
                                                        </Select.Trigger>
                                                        <Select.Content>
                                                            {#each allQuestions as aq}
                                                                <Select.Item
                                                                    value={aq.id}
                                                                    >{aq.text}</Select.Item
                                                                >
                                                            {/each}
                                                        </Select.Content>
                                                    </Select.Root>
                                                    <label
                                                        class="flex items-center gap-1 shrink-0 text-xs text-muted-foreground"
                                                    >
                                                        <input
                                                            type="checkbox"
                                                            bind:checked={
                                                                formPages[pIdx]
                                                                    .questions[
                                                                    qIdx
                                                                ].mandatory
                                                            }
                                                            class="h-3.5 w-3.5 rounded border-border"
                                                        />
                                                        Required
                                                    </label>
                                                    <Button
                                                        variant="ghost"
                                                        size="sm"
                                                        type="button"
                                                        onclick={() =>
                                                            (formPages[
                                                                pIdx
                                                            ].questions[
                                                                qIdx
                                                            ].showConfig =
                                                                !formPages[pIdx]
                                                                    .questions[
                                                                    qIdx
                                                                ].showConfig)}
                                                    >
                                                        <Settings
                                                            class="h-4 w-4 text-muted-foreground"
                                                        />
                                                    </Button>
                                                    <Button
                                                        variant="ghost"
                                                        size="sm"
                                                        type="button"
                                                        onclick={() =>
                                                            removeQuestionFromPage(
                                                                pIdx,
                                                                qIdx,
                                                            )}
                                                    >
                                                        <Trash2
                                                            class="h-3 w-3 text-destructive"
                                                        />
                                                    </Button>
                                                </div>

                                                <!-- Configuration Panel -->
                                                {#if q.showConfig}
                                                    {@const qType =
                                                        allQuestions.find(
                                                            (aq) =>
                                                                aq.id ===
                                                                q.questionId,
                                                        )?.type}
                                                    <div
                                                        class="mt-2 pl-8 border-t border-border/50 pt-4 space-y-4"
                                                    >
                                                        {#if !qType}
                                                            <p
                                                                class="text-xs text-muted-foreground"
                                                            >
                                                                Please select a
                                                                question first.
                                                            </p>
                                                        {:else if qType === "RATING_SCALE"}
                                                            <div
                                                                class="flex items-center gap-4"
                                                            >
                                                                <div
                                                                    class="space-y-1"
                                                                >
                                                                    <Label
                                                                        class="text-xs"
                                                                        >Min</Label
                                                                    >
                                                                    <Input
                                                                        type="number"
                                                                        bind:value={
                                                                            formPages[
                                                                                pIdx
                                                                            ]
                                                                                .questions[
                                                                                qIdx
                                                                            ]
                                                                                .parsedConfig
                                                                                .min
                                                                        }
                                                                        class="h-8 w-24 text-xs"
                                                                        placeholder="1"
                                                                    />
                                                                </div>
                                                                <div
                                                                    class="space-y-1"
                                                                >
                                                                    <Label
                                                                        class="text-xs"
                                                                        >Max</Label
                                                                    >
                                                                    <Input
                                                                        type="number"
                                                                        bind:value={
                                                                            formPages[
                                                                                pIdx
                                                                            ]
                                                                                .questions[
                                                                                qIdx
                                                                            ]
                                                                                .parsedConfig
                                                                                .max
                                                                        }
                                                                        class="h-8 w-24 text-xs"
                                                                    />
                                                                </div>
                                                                <div
                                                                    class="space-y-1"
                                                                >
                                                                    <Label
                                                                        class="text-xs"
                                                                        >Step</Label
                                                                    >
                                                                    <Input
                                                                        type="number"
                                                                        step="0.5"
                                                                        bind:value={
                                                                            formPages[
                                                                                pIdx
                                                                            ]
                                                                                .questions[
                                                                                qIdx
                                                                            ]
                                                                                .parsedConfig
                                                                                .step
                                                                        }
                                                                        class="h-8 w-24 text-xs"
                                                                        placeholder="0.5"
                                                                    />
                                                                </div>
                                                            </div>
                                                        {:else if qType === "SINGLE_CHOICE" || qType === "MULTIPLE_CHOICE" || qType === "RANK"}
                                                            <div
                                                                class="space-y-2"
                                                            >
                                                                <Label
                                                                    class="text-xs font-semibold"
                                                                    >Options</Label
                                                                >
                                                                {#if q.parsedConfig.options}
                                                                    <div
                                                                        class="space-y-2"
                                                                    >
                                                                        {#each q.parsedConfig.options as opt, optIdx}
                                                                            <div
                                                                                class="flex items-center gap-2"
                                                                            >
                                                                                <Input
                                                                                    bind:value={
                                                                                        formPages[
                                                                                            pIdx
                                                                                        ]
                                                                                            .questions[
                                                                                            qIdx
                                                                                        ]
                                                                                            .parsedConfig
                                                                                            .options[
                                                                                            optIdx
                                                                                        ]
                                                                                    }
                                                                                    class="h-8 flex-1 text-xs"
                                                                                    placeholder={`Option ${optIdx + 1}`}
                                                                                />
                                                                                <Button
                                                                                    variant="ghost"
                                                                                    size="sm"
                                                                                    type="button"
                                                                                    class="h-8 px-2"
                                                                                    onclick={() => {
                                                                                        formPages[
                                                                                            pIdx
                                                                                        ].questions[
                                                                                            qIdx
                                                                                        ].parsedConfig.options =
                                                                                            formPages[
                                                                                                pIdx
                                                                                            ].questions[
                                                                                                qIdx
                                                                                            ].parsedConfig.options.filter(
                                                                                                (
                                                                                                    _: any,
                                                                                                    i: number,
                                                                                                ) =>
                                                                                                    i !==
                                                                                                    optIdx,
                                                                                            );
                                                                                    }}
                                                                                >
                                                                                    <X
                                                                                        class="h-3 w-3 text-destructive"
                                                                                    />
                                                                                </Button>
                                                                            </div>
                                                                        {/each}
                                                                    </div>
                                                                {/if}
                                                                <Button
                                                                    variant="outline"
                                                                    size="sm"
                                                                    type="button"
                                                                    class="text-[10px] h-6 px-2 mt-1"
                                                                    onclick={() => {
                                                                        if (
                                                                            !formPages[
                                                                                pIdx
                                                                            ]
                                                                                .questions[
                                                                                qIdx
                                                                            ]
                                                                                .parsedConfig
                                                                                .options
                                                                        )
                                                                            formPages[
                                                                                pIdx
                                                                            ].questions[
                                                                                qIdx
                                                                            ].parsedConfig.options =
                                                                                [];
                                                                        formPages[
                                                                            pIdx
                                                                        ].questions[
                                                                            qIdx
                                                                        ].parsedConfig.options =
                                                                            [
                                                                                ...formPages[
                                                                                    pIdx
                                                                                ]
                                                                                    .questions[
                                                                                    qIdx
                                                                                ]
                                                                                    .parsedConfig
                                                                                    .options,
                                                                                "",
                                                                            ];
                                                                    }}
                                                                >
                                                                    <Plus
                                                                        class="mr-1 h-3 w-3"
                                                                    /> Add Option
                                                                </Button>
                                                            </div>

                                                            {#if qType === "MULTIPLE_CHOICE"}
                                                                <div
                                                                    class="flex items-center gap-4 pt-2 border-t border-border/50"
                                                                >
                                                                    <div
                                                                        class="space-y-1 flex-1 max-w-[120px]"
                                                                    >
                                                                        <Label
                                                                            class="text-xs"
                                                                            >Min
                                                                            Selections</Label
                                                                        >
                                                                        <Input
                                                                            type="number"
                                                                            bind:value={
                                                                                formPages[
                                                                                    pIdx
                                                                                ]
                                                                                    .questions[
                                                                                    qIdx
                                                                                ]
                                                                                    .parsedConfig
                                                                                    .minSelections
                                                                            }
                                                                            class="h-8 text-xs"
                                                                            placeholder="1"
                                                                        />
                                                                    </div>
                                                                    <div
                                                                        class="space-y-1 flex-1 max-w-[120px]"
                                                                    >
                                                                        <Label
                                                                            class="text-xs"
                                                                            >Max
                                                                            Selections</Label
                                                                        >
                                                                        <Input
                                                                            type="number"
                                                                            bind:value={
                                                                                formPages[
                                                                                    pIdx
                                                                                ]
                                                                                    .questions[
                                                                                    qIdx
                                                                                ]
                                                                                    .parsedConfig
                                                                                    .maxSelections
                                                                            }
                                                                            class="h-8 text-xs"
                                                                            placeholder="All"
                                                                        />
                                                                    </div>
                                                                </div>
                                                            {/if}
                                                        {/if}
                                                    </div>
                                                {/if}
                                            </div>
                                        {/each}
                                    {/if}
                                    <div class="flex items-center gap-2 mt-4">
                                        <Select.Root
                                            type="single"
                                            onValueChange={(v) => {
                                                if (v)
                                                    addCategoryToPage(pIdx, v);
                                            }}
                                        >
                                            <Select.Trigger
                                                class="w-[200px] h-8 text-xs"
                                            >
                                                <Plus class="mr-1 h-3 w-3" />
                                                Add from Category
                                            </Select.Trigger>
                                            <Select.Content>
                                                {#each allCategories as c}
                                                    <Select.Item value={c.id}
                                                        >{c.name}</Select.Item
                                                    >
                                                {/each}
                                            </Select.Content>
                                        </Select.Root>
                                        <Button
                                            variant="outline"
                                            size="sm"
                                            type="button"
                                            class="text-xs h-8"
                                            onclick={() =>
                                                addQuestionToPage(pIdx)}
                                        >
                                            <Plus class="mr-1 h-3 w-3" />
                                            Add Empty Question
                                        </Button>
                                    </div>
                                </Card.Content>
                            </Card.Root>
                        {/each}
                    </div>

                    <div class="flex justify-end gap-2 pt-2">
                        <Button
                            variant="outline"
                            type="button"
                            onclick={() => (dialogOpen = false)}
                        >
                            Cancel
                        </Button>
                        <Button type="submit" disabled={formLoading}>
                            {#if formLoading}
                                <span
                                    class="mr-2 inline-block h-4 w-4 animate-spin rounded-full border-2 border-current border-t-transparent"
                                ></span>
                            {/if}
                            {editingSurvey ? "Save Changes" : "Create"}
                        </Button>
                    </div>
                </form>
            </Card.Content>
        </Card.Root>
    </div>
{/if}

<!-- Lifecycle Transition Dialog -->
{#if lifecycleTarget}
    <div class="fixed inset-0 z-50 flex items-center justify-center">
        <button
            class="absolute inset-0 bg-black/50 backdrop-blur-sm"
            onclick={() => (lifecycleTarget = null)}
            aria-label="Close dialog"
        ></button>
        <Card.Root
            class="relative z-10 w-full max-w-md mx-4 shadow-2xl border-border"
        >
            <Card.Header>
                <Card.Title>Transition Survey State</Card.Title>
                <Card.Description>
                    <span class="font-medium text-foreground"
                        >{lifecycleTarget.title}</span
                    >
                    is currently
                    <Badge
                        variant={stateBadgeVariant(
                            lifecycleTarget.lifecycleState,
                        )}
                        class="mx-1"
                    >
                        {lifecycleTarget.lifecycleState.replace("_", " ")}
                    </Badge>
                </Card.Description>
            </Card.Header>
            <Card.Content class="space-y-4">
                <div class="flex items-center gap-3 text-sm">
                    <Badge
                        variant={stateBadgeVariant(
                            lifecycleTarget.lifecycleState,
                        )}
                    >
                        {lifecycleTarget.lifecycleState.replace("_", " ")}
                    </Badge>
                    <ChevronRight class="h-4 w-4 text-muted-foreground" />
                    <Select.Root type="single" bind:value={lifecycleState}>
                        <Select.Trigger class="w-48">
                            {lifecycleState.replace("_", " ")}
                        </Select.Trigger>
                        <Select.Content>
                            {#each validTransitions(lifecycleTarget.lifecycleState) as target}
                                <Select.Item value={target}
                                    >{target.replace("_", " ")}</Select.Item
                                >
                            {/each}
                        </Select.Content>
                    </Select.Root>
                </div>
            </Card.Content>
            <Card.Footer class="flex justify-end gap-2">
                <Button
                    variant="outline"
                    onclick={() => (lifecycleTarget = null)}
                >
                    Cancel
                </Button>
                <Button
                    onclick={handleLifecycleTransition}
                    disabled={lifecycleLoading}
                >
                    {#if lifecycleLoading}
                        <span
                            class="mr-2 inline-block h-4 w-4 animate-spin rounded-full border-2 border-current border-t-transparent"
                        ></span>
                    {/if}
                    <ArrowRightCircle class="mr-2 h-4 w-4" />
                    Transition
                </Button>
            </Card.Footer>
        </Card.Root>
    </div>
{/if}

<!-- Delete Dialog -->
<ConfirmDialog
    open={!!deleteTarget}
    title="Delete Survey"
    description="This will deactivate the survey. Existing campaign data is preserved."
    confirmLabel="Delete"
    loading={deleteLoading}
    onConfirm={handleDelete}
    onCancel={() => (deleteTarget = null)}
/>
