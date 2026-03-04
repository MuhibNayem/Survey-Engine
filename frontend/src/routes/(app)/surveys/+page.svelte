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
        Eye,
        Trash2,
        Search,
        X,
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
        QuestionType,
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
    let dialogReadOnly = $state(false);
    let formTitle = $state("");
    let formDescription = $state("");
    let formPages = $state<
        {
            title: string;
            sortOrder: number;
            questions: {
                questionId: string;
                categoryId?: string;
                categoryWeightPercentage?: number;
                sortOrder: number;
                mandatory: boolean;
                pinnedQuestionText?: string;
                pinnedQuestionType?: QuestionType;
                pinnedQuestionMaxScore?: number;
                pinnedQuestionOptionConfig?: string;
                pinnedCategoryName?: string;
                pinnedCategoryDescription?: string;
            }[];
        }[]
    >([]);
    let addFromCategoryState = $state<
        Record<number, { categoryId: string; weightPercentage: number }>
    >({});
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
        dialogReadOnly = false;
        formTitle = "";
        formDescription = "";
        formPages = [{ title: "Page 1", sortOrder: 1, questions: [] }];
        addFromCategoryState = {};
        formError = null;
        dialogOpen = true;
    }

    function mapSurveyToFormPages(s: SurveyResponse) {
        return s.pages.map((p) => ({
            title: p.title,
            sortOrder: p.sortOrder,
            questions: p.questions.map((q) => ({
                questionId: q.questionId,
                categoryId: q.categoryId,
                categoryWeightPercentage: q.categoryWeightPercentage,
                sortOrder: q.sortOrder,
                mandatory: q.mandatory,
                pinnedQuestionText: q.pinnedQuestionText,
                pinnedQuestionType: q.pinnedQuestionType,
                pinnedQuestionMaxScore: q.pinnedQuestionMaxScore,
                pinnedQuestionOptionConfig: q.pinnedQuestionOptionConfig,
                pinnedCategoryName: q.pinnedCategoryName,
                pinnedCategoryDescription: q.pinnedCategoryDescription,
            })),
        }));
    }

    function openEditDialog(s: SurveyResponse) {
        editingSurvey = s;
        dialogReadOnly = false;
        formTitle = s.title;
        formDescription = s.description;
        formPages = mapSurveyToFormPages(s);
        addFromCategoryState = {};
        formError = null;
        dialogOpen = true;
    }

    function openViewDialog(s: SurveyResponse) {
        editingSurvey = s;
        dialogReadOnly = true;
        formTitle = s.title;
        formDescription = s.description;
        formPages = mapSurveyToFormPages(s);
        addFromCategoryState = {};
        formError = null;
        dialogOpen = true;
    }

    function isOptionQuestion(type?: QuestionType): boolean {
        return (
            type === "SINGLE_CHOICE" ||
            type === "MULTIPLE_CHOICE" ||
            type === "RANK"
        );
    }

    type OptionRow = { value: string; score: string };

    function getPinnedQuestionDisplayText(pIdx: number, qIdx: number): string {
        const question = formPages[pIdx].questions[qIdx];
        if (question.pinnedQuestionText?.trim()) {
            return question.pinnedQuestionText;
        }
        return question.questionId ? "Pinned text not set" : "Select question";
    }

    function optionConfigToRows(optionConfig?: string): OptionRow[] {
        if (!optionConfig) return [];
        try {
            const parsed = JSON.parse(optionConfig) as {
                options?: Array<string | { value?: string; score?: number }>;
            };
            if (!Array.isArray(parsed?.options)) return [];
            return parsed.options
                .map((option) => {
                    if (typeof option === "string") {
                        const value = option.trim();
                        if (!value) return null;
                        return { value, score: "" };
                    }
                    const value = option?.value?.trim() ?? "";
                    if (!value) return null;
                    return {
                        value,
                        score:
                            option.score !== undefined && option.score !== null
                                ? String(option.score)
                                : "",
                    };
                })
                .filter((row): row is OptionRow => row !== null);
        } catch {
            return [];
        }
    }

    function buildOptionConfigFromRows(rows: OptionRow[]): string {
        const options: Array<string | { value: string; score: number }> = [];
        for (const row of rows) {
            const valuePart = row.value.trim();
            const scorePart = row.score.trim();
            if (!valuePart && !scorePart) continue;
            if (!valuePart) continue;
            if (!scorePart) {
                options.push(valuePart);
                continue;
            }
            const score = Number(scorePart);
            if (Number.isFinite(score) && score >= 0) {
                options.push({ value: valuePart, score });
            } else {
                options.push(valuePart);
            }
        }
        return JSON.stringify({ options });
    }

    function getPinnedOptionRows(pIdx: number, qIdx: number): OptionRow[] {
        const rows = optionConfigToRows(
            formPages[pIdx].questions[qIdx].pinnedQuestionOptionConfig,
        );
        return rows.length > 0 ? rows : [{ value: "", score: "" }];
    }

    function updatePinnedOptionRow(
        pIdx: number,
        qIdx: number,
        rowIdx: number,
        field: "value" | "score",
        next: string,
    ) {
        const rows = getPinnedOptionRows(pIdx, qIdx);
        if (!rows[rowIdx]) return;
        rows[rowIdx][field] = next;
        formPages[pIdx].questions[qIdx].pinnedQuestionOptionConfig =
            buildOptionConfigFromRows(rows);
        formPages = [...formPages];
    }

    function addPinnedOptionRow(pIdx: number, qIdx: number) {
        const rows = getPinnedOptionRows(pIdx, qIdx);
        rows.push({ value: "", score: "" });
        formPages[pIdx].questions[qIdx].pinnedQuestionOptionConfig =
            buildOptionConfigFromRows(rows);
        formPages = [...formPages];
    }

    function removePinnedOptionRow(pIdx: number, qIdx: number, rowIdx: number) {
        const rows = getPinnedOptionRows(pIdx, qIdx);
        if (rows.length <= 1) return;
        rows.splice(rowIdx, 1);
        formPages[pIdx].questions[qIdx].pinnedQuestionOptionConfig =
            buildOptionConfigFromRows(rows);
        formPages = [...formPages];
    }

    function setQuestionForPage(pIdx: number, qIdx: number, questionId: string) {
        const page = formPages[pIdx];
        const question = page.questions[qIdx];
        const questionBankItem = allQuestions.find((q) => q.id === questionId);
        question.questionId = questionId;
        question.pinnedQuestionText = questionBankItem?.text ?? "";
        question.pinnedQuestionType = questionBankItem?.type;
        question.pinnedQuestionMaxScore = questionBankItem?.maxScore;
        question.pinnedQuestionOptionConfig = questionBankItem?.optionConfig;
        formPages = [...formPages];
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
        addFromCategoryState = {};
    }

    function removePage(pIdx: number) {
        formPages = formPages.filter((_, i) => i !== pIdx);
        formPages = formPages.map((p, i) => ({
            ...p,
            sortOrder: i + 1,
        }));
        addFromCategoryState = {};
    }

    function addQuestionToPage(pIdx: number) {
        const page = formPages[pIdx];
        page.questions = [
            ...page.questions,
            {
                questionId: "",
                sortOrder: page.questions.length + 1,
                mandatory: true,
                pinnedQuestionText: "",
                pinnedQuestionMaxScore: 1,
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

    function removeCategoryFromPage(pIdx: number, categoryId: string) {
        const page = formPages[pIdx];
        page.questions = page.questions
            .filter((q) => q.categoryId !== categoryId)
            .map((q, i) => ({
                ...q,
                sortOrder: i + 1,
            }));
        formPages = [...formPages];
    }

    function setAddFromCategorySelection(pIdx: number, categoryId: string) {
        const current = addFromCategoryState[pIdx] ?? {
            categoryId: "",
            weightPercentage: 0,
        };
        addFromCategoryState = {
            ...addFromCategoryState,
            [pIdx]: { ...current, categoryId },
        };
    }

    function setAddFromCategoryWeight(pIdx: number, weightPercentage: number) {
        const current = addFromCategoryState[pIdx] ?? {
            categoryId: "",
            weightPercentage: 0,
        };
        addFromCategoryState = {
            ...addFromCategoryState,
            [pIdx]: { ...current, weightPercentage },
        };
    }

    function setCategoryWeightForAllQuestions(
        categoryId: string,
        weightPercentage: number,
    ) {
        for (const page of formPages) {
            for (const question of page.questions) {
                if (question.categoryId === categoryId) {
                    question.categoryWeightPercentage = weightPercentage;
                }
            }
        }
        formPages = [...formPages];
    }

    function setCategoryNameForAllQuestions(
        categoryId: string,
        categoryName: string,
    ) {
        for (const page of formPages) {
            for (const question of page.questions) {
                if (question.categoryId === categoryId) {
                    question.pinnedCategoryName = categoryName;
                }
            }
        }
        formPages = [...formPages];
    }

    function setCategoryDescriptionForAllQuestions(
        categoryId: string,
        categoryDescription: string,
    ) {
        for (const page of formPages) {
            for (const question of page.questions) {
                if (question.categoryId === categoryId) {
                    question.pinnedCategoryDescription = categoryDescription;
                }
            }
        }
        formPages = [...formPages];
    }

    function addCategoryToPage(
        pIdx: number,
        categoryId: string,
        categoryWeightPercentage: number,
    ) {
        const category = allCategories.find((c) => c.id === categoryId);
        if (!category) return;

        const page = formPages[pIdx];
        const nextSortOrder =
            page.questions.length > 0
                ? Math.max(...page.questions.map((q) => q.sortOrder)) + 1
                : 1;

        const newQuestions = category.questionMappings
            .sort((a, b) => a.sortOrder - b.sortOrder)
            .map((mapping, i) => {
                const questionBankItem = allQuestions.find(
                    (q) => q.id === mapping.questionId,
                );
                return {
                    questionId: mapping.questionId,
                    categoryId: category.id,
                    categoryWeightPercentage,
                    sortOrder: nextSortOrder + i,
                    mandatory: true,
                    pinnedQuestionText: questionBankItem?.text ?? "",
                    pinnedQuestionType: questionBankItem?.type,
                    pinnedQuestionMaxScore: questionBankItem?.maxScore ?? 1,
                    pinnedQuestionOptionConfig:
                        questionBankItem?.optionConfig ?? "",
                    pinnedCategoryName: category.name,
                    pinnedCategoryDescription: category.description,
                };
            });

        page.questions = [...page.questions, ...newQuestions];
        formPages = [...formPages];
    }

    function addSelectedCategoryToPage(pIdx: number) {
        const state = addFromCategoryState[pIdx];
        if (!state?.categoryId) {
            formError = "Select a category to add.";
            return;
        }
        if (
            !Number.isFinite(state.weightPercentage) ||
            state.weightPercentage <= 0 ||
            state.weightPercentage > 100
        ) {
            formError = "Category weight must be > 0 and <= 100.";
            return;
        }
        const normalizedWeight = Number(state.weightPercentage.toFixed(2));
        addCategoryToPage(pIdx, state.categoryId, normalizedWeight);
        setCategoryWeightForAllQuestions(state.categoryId, normalizedWeight);
        addFromCategoryState = {
            ...addFromCategoryState,
            [pIdx]: { categoryId: "", weightPercentage: 0 },
        };
        formError = null;
    }

    function getCategoryName(id: string): string {
        return allCategories.find((c) => c.id === id)?.name ?? "Misc";
    }

    function getQuestionText(id: string): string {
        const q = allQuestions.find((q) => q.id === id);
        return q?.text ?? "";
    }

    function getPageCategoryIds(
        page: (typeof formPages)[number],
    ): string[] {
        const ids: string[] = [];
        for (const question of page.questions) {
            if (!question.categoryId) continue;
            if (!ids.includes(question.categoryId)) {
                ids.push(question.categoryId);
            }
        }
        return ids;
    }

    function getPageQuestionsByCategory(
        page: (typeof formPages)[number],
        categoryId: string,
    ): { question: (typeof formPages)[number]["questions"][number]; index: number }[] {
        return page.questions
            .map((question, index) => ({ question, index }))
            .filter(({ question }) => question.categoryId === categoryId);
    }

    function getPageUncategorizedQuestions(
        page: (typeof formPages)[number],
    ): { question: (typeof formPages)[number]["questions"][number]; index: number }[] {
        return page.questions
            .map((question, index) => ({ question, index }))
            .filter(({ question }) => !question.categoryId);
    }

    function getCategoryWeightForPage(
        page: (typeof formPages)[number],
        categoryId: string,
    ): number {
        const question = page.questions.find((q) => q.categoryId === categoryId);
        return Number(question?.categoryWeightPercentage ?? 0);
    }

    function getCategoryPinnedNameForPage(
        page: (typeof formPages)[number],
        categoryId: string,
    ): string {
        const question = page.questions.find((q) => q.categoryId === categoryId);
        return question?.pinnedCategoryName ?? getCategoryName(categoryId);
    }

    function getCategoryPinnedDescriptionForPage(
        page: (typeof formPages)[number],
        categoryId: string,
    ): string {
        const question = page.questions.find((q) => q.categoryId === categoryId);
        return question?.pinnedCategoryDescription ?? "";
    }

    function validateCategoryWeights(): string | null {
        const categoryWeights = new Map<string, number>();
        for (const page of formPages) {
            for (const question of page.questions) {
                if (!question.categoryId) continue;
                const weight = Number(question.categoryWeightPercentage);
                if (!Number.isFinite(weight) || weight <= 0 || weight > 100) {
                    return `Category '${getCategoryName(question.categoryId)}' has invalid weight.`;
                }
                const previous = categoryWeights.get(question.categoryId);
                const normalized = Number(weight.toFixed(2));
                if (
                    previous !== undefined &&
                    Math.abs(previous - normalized) > 0.001
                ) {
                    return `Category '${getCategoryName(question.categoryId)}' must use the same weight across all its questions.`;
                }
                categoryWeights.set(question.categoryId, normalized);
            }
        }

        if (categoryWeights.size > 0) {
            const total = Array.from(categoryWeights.values()).reduce(
                (sum, w) => sum + w,
                0,
            );
            if (Math.abs(total - 100) > 0.001) {
                return `Category weights must sum to 100%. Current total: ${total.toFixed(2)}%.`;
            }
        }
        return null;
    }

    async function handleSubmit(e: Event) {
        e.preventDefault();
        formLoading = true;
        formError = null;
        try {
            const weightError = validateCategoryWeights();
            if (weightError) {
                formError = weightError;
                formLoading = false;
                return;
            }
            const payload = {
                title: formTitle,
                description: formDescription,
                pages: formPages.map((p) => ({
                    title: p.title,
                    sortOrder: p.sortOrder,
                    questions: p.questions.map((q) => ({
                        questionId: q.questionId,
                        categoryId: q.categoryId,
                        categoryWeightPercentage: q.categoryWeightPercentage,
                        sortOrder: q.sortOrder,
                        mandatory: q.mandatory,
                        pinnedQuestionText: q.pinnedQuestionText,
                        pinnedQuestionMaxScore: q.pinnedQuestionMaxScore,
                        pinnedQuestionOptionConfig:
                            q.pinnedQuestionOptionConfig,
                        pinnedCategoryName: q.pinnedCategoryName,
                        pinnedCategoryDescription: q.pinnedCategoryDescription,
                    })) as SurveyQuestionRequest[],
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
                                        <Button
                                            variant="ghost"
                                            size="sm"
                                            onclick={() => openViewDialog(survey)}
                                        >
                                            <Eye class="h-4 w-4" />
                                        </Button>
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
                            {#if dialogReadOnly}
                                Survey Details
                            {:else}
                                {editingSurvey ? "Edit Survey" : "New Survey"}
                            {/if}
                        </Card.Title>
                        <Card.Description>
                            {#if dialogReadOnly}
                                Read-only view of pinned question/category copies. Editing is disabled after publish.
                            {:else}
                                {editingSurvey
                                    ? "Update survey structure and pinned copies."
                                    : "Create a new survey with pages and questions."}
                            {/if}
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

                    <div class={dialogReadOnly
                        ? "space-y-6 pointer-events-none opacity-80"
                        : "space-y-6"}
                    >
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
                            {#if !dialogReadOnly}
                                <Button
                                    variant="outline"
                                    size="sm"
                                    type="button"
                                    onclick={addPage}
                                >
                                    <Plus class="mr-1 h-3 w-3" />
                                    Add Page
                                </Button>
                            {/if}
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
                                        {#if !dialogReadOnly && formPages.length > 1}
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
                                        {@const categoryIds =
                                            getPageCategoryIds(page)}
                                        {#if categoryIds.length > 0}
                                            <div class="space-y-3">
                                                {#each categoryIds as categoryId}
                                                    <div
                                                        class="rounded-lg border border-border bg-muted/20 p-3"
                                                    >
                                                        <div
                                                            class="mb-3 flex flex-wrap items-center justify-between gap-3"
                                                        >
                                                            <div
                                                                class="flex items-center gap-2"
                                                            >
                                                                <Input
                                                                    class="h-8 w-52 text-xs"
                                                                    value={getCategoryPinnedNameForPage(
                                                                        page,
                                                                        categoryId,
                                                                    )}
                                                                    oninput={(e) =>
                                                                        setCategoryNameForAllQuestions(
                                                                            categoryId,
                                                                            (
                                                                                e.currentTarget as HTMLInputElement
                                                                            ).value,
                                                                        )}
                                                                    placeholder="Pinned category name"
                                                                />
                                                                <span
                                                                    class="text-xs text-muted-foreground"
                                                                >
                                                                    {getPageQuestionsByCategory(
                                                                        page,
                                                                        categoryId,
                                                                    ).length}
                                                                    questions
                                                                </span>
                                                            </div>
                                                            <div
                                                                class="flex items-center gap-2"
                                                            >
                                                                <span
                                                                    class="text-xs text-muted-foreground"
                                                                    >Weight</span
                                                                >
                                                                <Input
                                                                    type="number"
                                                                    min="0.01"
                                                                    max="100"
                                                                    step="0.01"
                                                                    class="h-8 w-24 text-xs"
                                                                    value={getCategoryWeightForPage(
                                                                        page,
                                                                        categoryId,
                                                                    )}
                                                                    oninput={(e) => {
                                                                        const v = Number(
                                                                            (
                                                                                e.currentTarget as HTMLInputElement
                                                                            ).value,
                                                                        );
                                                                        if (
                                                                            Number.isFinite(
                                                                                v,
                                                                            )
                                                                        ) {
                                                                            setCategoryWeightForAllQuestions(
                                                                                categoryId,
                                                                                Number(
                                                                                    v.toFixed(
                                                                                        2,
                                                                                    ),
                                                                                ),
                                                                            );
                                                                        }
                                                                    }}
                                                                />
                                                                <span
                                                                    class="text-xs text-muted-foreground"
                                                                    >%</span
                                                                >
                                                                {#if !dialogReadOnly}
                                                                    <Button
                                                                        variant="ghost"
                                                                        size="sm"
                                                                        type="button"
                                                                        onclick={() =>
                                                                            removeCategoryFromPage(
                                                                                pIdx,
                                                                                categoryId,
                                                                            )}
                                                                    >
                                                                        <Trash2
                                                                            class="h-3 w-3 text-destructive"
                                                                        />
                                                                    </Button>
                                                                {/if}
                                                            </div>
                                                        </div>
                                                        <div class="mb-3">
                                                            <Input
                                                                class="h-8 text-xs"
                                                                value={getCategoryPinnedDescriptionForPage(
                                                                    page,
                                                                    categoryId,
                                                                )}
                                                                oninput={(e) =>
                                                                    setCategoryDescriptionForAllQuestions(
                                                                        categoryId,
                                                                        (
                                                                            e.currentTarget as HTMLInputElement
                                                                        ).value,
                                                                    )}
                                                                placeholder="Pinned category description (optional)"
                                                            />
                                                        </div>
                                                        <div class="space-y-2">
                                                            {#each getPageQuestionsByCategory(page, categoryId) as item (item.index)}
                                                                <div
                                                                    class="flex items-center gap-2 rounded-md border border-border bg-background p-2"
                                                                >
                                                                    <span
                                                                        class="shrink-0 text-xs font-medium text-muted-foreground w-6"
                                                                    >
                                                                        #{item.question
                                                                            .sortOrder}
                                                                    </span>
                                                                    <Select.Root
                                                                        type="single"
                                                                        value={formPages[
                                                                            pIdx
                                                                        ]
                                                                            .questions[
                                                                            item
                                                                                .index
                                                                        ]
                                                                            .questionId}
                                                                        onValueChange={(v) =>
                                                                            setQuestionForPage(
                                                                                pIdx,
                                                                                item.index,
                                                                                v,
                                                                            )}
                                                                    >
                                                                        <Select.Trigger
                                                                            class="flex-1 h-8 text-xs"
                                                                        >
                                                                            {getPinnedQuestionDisplayText(
                                                                                pIdx,
                                                                                item.index,
                                                                            )}
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
                                                                    <Input
                                                                        class="h-8 text-xs flex-[1.2]"
                                                                        placeholder="Pinned question text"
                                                                        bind:value={formPages[
                                                                            pIdx
                                                                        ]
                                                                            .questions[
                                                                            item
                                                                                .index
                                                                        ]
                                                                            .pinnedQuestionText}
                                                                    />
                                                                    <Input
                                                                        type="number"
                                                                        min="0.01"
                                                                        step="0.01"
                                                                        class="h-8 w-24 text-xs"
                                                                        placeholder="Max"
                                                                        bind:value={formPages[
                                                                            pIdx
                                                                        ]
                                                                            .questions[
                                                                            item
                                                                                .index
                                                                        ]
                                                                            .pinnedQuestionMaxScore}
                                                                    />
                                                                    <label
                                                                        class="flex items-center gap-1 shrink-0 text-xs text-muted-foreground"
                                                                    >
                                                                        <input
                                                                            type="checkbox"
                                                                            bind:checked={
                                                                                formPages[
                                                                                    pIdx
                                                                                ]
                                                                                    .questions[
                                                                                    item
                                                                                        .index
                                                                                ]
                                                                                    .mandatory
                                                                            }
                                                                            class="h-3.5 w-3.5 rounded border-border"
                                                                        />
                                                                        Required
                                                                    </label>
                                                                    {#if !dialogReadOnly}
                                                                        <Button
                                                                            variant="ghost"
                                                                            size="sm"
                                                                            type="button"
                                                                            onclick={() =>
                                                                                removeQuestionFromPage(
                                                                                    pIdx,
                                                                                    item.index,
                                                                                )}
                                                                        >
                                                                            <Trash2
                                                                                class="h-3 w-3 text-destructive"
                                                                            />
                                                                        </Button>
                                                                    {/if}
                                                                </div>
                                                                {#if isOptionQuestion(formPages[pIdx].questions[item.index].pinnedQuestionType)}
                                                                    <div class="mt-2 rounded-md border border-border p-2 space-y-2">
                                                                        <div class="flex items-center justify-between">
                                                                            <span class="text-xs font-medium text-muted-foreground">Pinned options</span>
                                                                            {#if !dialogReadOnly}
                                                                                <Button
                                                                                    variant="outline"
                                                                                    size="sm"
                                                                                    type="button"
                                                                                    class="h-7 text-xs"
                                                                                    onclick={() =>
                                                                                        addPinnedOptionRow(
                                                                                            pIdx,
                                                                                            item.index,
                                                                                        )}
                                                                                >
                                                                                    <Plus class="mr-1 h-3 w-3" />
                                                                                    Add Option
                                                                                </Button>
                                                                            {/if}
                                                                        </div>
                                                                        {#each getPinnedOptionRows(pIdx, item.index) as row, rowIdx}
                                                                            <div class="flex items-center gap-2">
                                                                                <Input
                                                                                    class="h-8 text-xs flex-1"
                                                                                    placeholder="Option value"
                                                                                    value={row.value}
                                                                                    oninput={(e) =>
                                                                                        updatePinnedOptionRow(
                                                                                            pIdx,
                                                                                            item.index,
                                                                                            rowIdx,
                                                                                            "value",
                                                                                            (e.currentTarget as HTMLInputElement).value,
                                                                                        )}
                                                                                />
                                                                                <Input
                                                                                    class="h-8 w-24 text-xs"
                                                                                    type="number"
                                                                                    min="0"
                                                                                    step="0.01"
                                                                                    placeholder="Score"
                                                                                    value={row.score}
                                                                                    oninput={(e) =>
                                                                                        updatePinnedOptionRow(
                                                                                            pIdx,
                                                                                            item.index,
                                                                                            rowIdx,
                                                                                            "score",
                                                                                            (e.currentTarget as HTMLInputElement).value,
                                                                                        )}
                                                                                />
                                                                                {#if !dialogReadOnly}
                                                                                    <Button
                                                                                        variant="ghost"
                                                                                        size="sm"
                                                                                        type="button"
                                                                                        onclick={() =>
                                                                                            removePinnedOptionRow(
                                                                                                pIdx,
                                                                                                item.index,
                                                                                                rowIdx,
                                                                                            )}
                                                                                    >
                                                                                        <Trash2 class="h-3 w-3 text-destructive" />
                                                                                    </Button>
                                                                                {/if}
                                                                            </div>
                                                                        {/each}
                                                                    </div>
                                                                {/if}
                                                            {/each}
                                                        </div>
                                                    </div>
                                                {/each}
                                            </div>
                                        {/if}

                                        {@const uncategorizedQuestions =
                                            getPageUncategorizedQuestions(page)}
                                        {#if uncategorizedQuestions.length > 0}
                                            <div class="space-y-2">
                                                <p
                                                    class="text-[11px] font-medium uppercase tracking-wide text-muted-foreground"
                                                >
                                                    Independent Questions
                                                </p>
                                                {#each uncategorizedQuestions as item (item.index)}
                                                    <div class="space-y-2">
                                                        <div
                                                            class="flex items-center gap-2 rounded-md border border-border bg-muted/20 p-2"
                                                        >
                                                            <span
                                                                class="shrink-0 text-xs font-medium text-muted-foreground w-6"
                                                            >
                                                                #{item.question
                                                                    .sortOrder}
                                                            </span>
                                                            <Select.Root
                                                                type="single"
                                                                value={formPages[
                                                                    pIdx
                                                                ]
                                                                    .questions[
                                                                    item.index
                                                                ].questionId}
                                                                onValueChange={(v) =>
                                                                    setQuestionForPage(
                                                                        pIdx,
                                                                        item.index,
                                                                        v,
                                                                    )}
                                                            >
                                                                <Select.Trigger
                                                                    class="flex-1 h-8 text-xs"
                                                                >
                                                                    {getPinnedQuestionDisplayText(
                                                                        pIdx,
                                                                        item.index,
                                                                    )}
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
                                                            <Input
                                                                class="h-8 text-xs flex-[1.2]"
                                                                placeholder="Pinned question text"
                                                                bind:value={formPages[
                                                                    pIdx
                                                                ]
                                                                    .questions[
                                                                    item.index
                                                                ]
                                                                    .pinnedQuestionText}
                                                            />
                                                            <Input
                                                                type="number"
                                                                min="0.01"
                                                                step="0.01"
                                                                class="h-8 w-24 text-xs"
                                                                placeholder="Max"
                                                                bind:value={formPages[
                                                                    pIdx
                                                                ]
                                                                    .questions[
                                                                    item.index
                                                                ]
                                                                    .pinnedQuestionMaxScore}
                                                            />
                                                            <label
                                                                class="flex items-center gap-1 shrink-0 text-xs text-muted-foreground"
                                                            >
                                                                <input
                                                                    type="checkbox"
                                                                    bind:checked={
                                                                        formPages[
                                                                            pIdx
                                                                        ]
                                                                            .questions[
                                                                            item
                                                                                .index
                                                                        ]
                                                                            .mandatory
                                                                    }
                                                                    class="h-3.5 w-3.5 rounded border-border"
                                                                />
                                                                Required
                                                            </label>
                                                            {#if !dialogReadOnly}
                                                                <Button
                                                                    variant="ghost"
                                                                    size="sm"
                                                                    type="button"
                                                                    onclick={() =>
                                                                        removeQuestionFromPage(
                                                                            pIdx,
                                                                            item.index,
                                                                        )}
                                                                >
                                                                    <Trash2
                                                                        class="h-3 w-3 text-destructive"
                                                                    />
                                                                </Button>
                                                            {/if}
                                                        </div>
                                                        {#if isOptionQuestion(formPages[pIdx].questions[item.index].pinnedQuestionType)}
                                                            <div class="mt-2 rounded-md border border-border p-2 space-y-2">
                                                                <div class="flex items-center justify-between">
                                                                    <span class="text-xs font-medium text-muted-foreground">Pinned options</span>
                                                                    {#if !dialogReadOnly}
                                                                        <Button
                                                                            variant="outline"
                                                                            size="sm"
                                                                            type="button"
                                                                            class="h-7 text-xs"
                                                                            onclick={() =>
                                                                                addPinnedOptionRow(
                                                                                    pIdx,
                                                                                    item.index,
                                                                                )}
                                                                        >
                                                                            <Plus class="mr-1 h-3 w-3" />
                                                                            Add Option
                                                                        </Button>
                                                                    {/if}
                                                                </div>
                                                                {#each getPinnedOptionRows(pIdx, item.index) as row, rowIdx}
                                                                    <div class="flex items-center gap-2">
                                                                        <Input
                                                                            class="h-8 text-xs flex-1"
                                                                            placeholder="Option value"
                                                                            value={row.value}
                                                                            oninput={(e) =>
                                                                                updatePinnedOptionRow(
                                                                                    pIdx,
                                                                                    item.index,
                                                                                    rowIdx,
                                                                                    "value",
                                                                                    (e.currentTarget as HTMLInputElement).value,
                                                                                )}
                                                                        />
                                                                        <Input
                                                                            class="h-8 w-24 text-xs"
                                                                            type="number"
                                                                            min="0"
                                                                            step="0.01"
                                                                            placeholder="Score"
                                                                            value={row.score}
                                                                            oninput={(e) =>
                                                                                updatePinnedOptionRow(
                                                                                    pIdx,
                                                                                    item.index,
                                                                                    rowIdx,
                                                                                    "score",
                                                                                    (e.currentTarget as HTMLInputElement).value,
                                                                                )}
                                                                        />
                                                                        {#if !dialogReadOnly}
                                                                            <Button
                                                                                variant="ghost"
                                                                                size="sm"
                                                                                type="button"
                                                                                onclick={() =>
                                                                                    removePinnedOptionRow(
                                                                                        pIdx,
                                                                                        item.index,
                                                                                        rowIdx,
                                                                                    )}
                                                                            >
                                                                                <Trash2 class="h-3 w-3 text-destructive" />
                                                                            </Button>
                                                                        {/if}
                                                                    </div>
                                                                {/each}
                                                            </div>
                                                        {/if}
                                                    </div>
                                                {/each}
                                            </div>
                                        {/if}
                                    {/if}
                                    {#if !dialogReadOnly}
                                        <div class="flex items-center gap-2 mt-4">
                                            <Select.Root
                                                type="single"
                                                value={addFromCategoryState[pIdx]
                                                    ?.categoryId ?? ""}
                                                onValueChange={(v) =>
                                                    setAddFromCategorySelection(
                                                        pIdx,
                                                        v,
                                                    )}
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
                                            <Input
                                                type="number"
                                                min="0.01"
                                                max="100"
                                                step="0.01"
                                                class="w-28 h-8 text-xs"
                                                placeholder="Weight %"
                                                value={addFromCategoryState[pIdx]
                                                    ?.weightPercentage ?? 0}
                                                oninput={(e) =>
                                                    setAddFromCategoryWeight(
                                                        pIdx,
                                                        Number(
                                                            (
                                                                e.currentTarget as HTMLInputElement
                                                            ).value,
                                                        ),
                                                    )}
                                            />
                                            <Button
                                                variant="outline"
                                                size="sm"
                                                type="button"
                                                class="text-xs h-8"
                                                onclick={() =>
                                                    addSelectedCategoryToPage(
                                                        pIdx,
                                                    )}
                                            >
                                                <Plus class="mr-1 h-3 w-3" />
                                                Add Category
                                            </Button>
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
                                    {/if}
                                </Card.Content>
                            </Card.Root>
                        {/each}
                    </div>
                    </div>

                    <div class="flex justify-end gap-2 pt-2">
                        {#if dialogReadOnly}
                            <Button
                                variant="outline"
                                type="button"
                                onclick={() => (dialogOpen = false)}
                            >
                                Close
                            </Button>
                        {:else}
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
                        {/if}
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
