<script lang="ts">
    import api from "$lib/api";
    import { onMount } from "svelte";
    import * as Card from "$lib/components/ui/card";
    import { Button } from "$lib/components/ui/button";
    import { Input } from "$lib/components/ui/input";
    import { Label } from "$lib/components/ui/label";
    import { Badge } from "$lib/components/ui/badge";
    import * as Select from "$lib/components/ui/select";
    import { ConfirmDialog } from "$lib/components/ui/confirm-dialog";
    import PageHeader from "$lib/components/layout/PageHeader.svelte";
    import { Plus, Pencil, Trash2, Search, X, HelpCircle } from "lucide-svelte";
    import type { QuestionResponse, QuestionType } from "$lib/types";

    // --- State ---
    let questions = $state<QuestionResponse[]>([]);
    let loading = $state(true);
    let searchQuery = $state("");
    let typeFilter = $state<QuestionType | "">("");

    // Dialog state
    let dialogOpen = $state(false);
    let editingQuestion = $state<QuestionResponse | null>(null);
    let formText = $state("");
    let formType = $state<QuestionType>("RATING_SCALE");
    let formMaxScore = $state(5);
    let formLoading = $state(false);
    let formError = $state<string | null>(null);

    // Delete dialog
    let deleteTarget = $state<QuestionResponse | null>(null);
    let deleteLoading = $state(false);

    // --- Computed ---
    const filteredQuestions = $derived(
        questions.filter((q) => {
            const matchesSearch =
                !searchQuery ||
                q.text.toLowerCase().includes(searchQuery.toLowerCase());
            const matchesType = !typeFilter || q.type === typeFilter;
            return matchesSearch && matchesType;
        }),
    );

    const questionTypes: { value: QuestionType; label: string }[] = [
        { value: "RATING_SCALE", label: "Rating Scale" },
        { value: "SINGLE_CHOICE", label: "Single Choice" },
        { value: "MULTIPLE_CHOICE", label: "Multiple Choice" },
        { value: "RANK", label: "Rank" },
    ];

    // --- API ---
    async function loadQuestions() {
        loading = true;
        try {
            const { data } = await api.get<QuestionResponse[]>("/questions");
            questions = data;
        } catch {
            questions = [];
        } finally {
            loading = false;
        }
    }

    function openCreateDialog() {
        editingQuestion = null;
        formText = "";
        formType = "RATING_SCALE";
        formMaxScore = 5;
        formError = null;
        dialogOpen = true;
    }

    function openEditDialog(q: QuestionResponse) {
        editingQuestion = q;
        formText = q.text;
        formType = q.type;
        formMaxScore = q.maxScore;
        formError = null;
        dialogOpen = true;
    }

    async function handleSubmit(e: Event) {
        e.preventDefault();
        formLoading = true;
        formError = null;
        try {
            const payload = {
                text: formText,
                type: formType,
                maxScore: formMaxScore,
            };
            if (editingQuestion) {
                await api.put(`/questions/${editingQuestion.id}`, payload);
            } else {
                await api.post("/questions", payload);
            }
            dialogOpen = false;
            await loadQuestions();
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
            await api.delete(`/questions/${deleteTarget.id}`);
            deleteTarget = null;
            await loadQuestions();
        } catch {
            // Error is silently handled
        } finally {
            deleteLoading = false;
        }
    }

    function badgeVariant(type: QuestionType) {
        switch (type) {
            case "RATING_SCALE":
                return "default" as const;
            case "SINGLE_CHOICE":
                return "secondary" as const;
            case "MULTIPLE_CHOICE":
                return "outline" as const;
            case "RANK":
                return "secondary" as const;
            default:
                return "default" as const;
        }
    }

    function formatDate(iso: string) {
        return new Date(iso).toLocaleDateString("en-US", {
            month: "short",
            day: "numeric",
            year: "numeric",
        });
    }

    onMount(loadQuestions);
</script>

<svelte:head>
    <title>Questions — Survey Engine</title>
</svelte:head>

<div class="space-y-6">
    <PageHeader
        title="Questions"
        description="Manage your reusable question bank"
        actionLabel="New Question"
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
                placeholder="Search questions..."
                bind:value={searchQuery}
                class="pl-9"
            />
        </div>
        <Select.Root type="single" bind:value={typeFilter}>
            <Select.Trigger class="w-full sm:w-48">
                {typeFilter
                    ? questionTypes.find((t) => t.value === typeFilter)?.label
                    : "All Types"}
            </Select.Trigger>
            <Select.Content>
                <Select.Item value="">All Types</Select.Item>
                {#each questionTypes as qt}
                    <Select.Item value={qt.value}>{qt.label}</Select.Item>
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
    {:else if filteredQuestions.length === 0}
        <Card.Root
            class="flex flex-col items-center justify-center py-16 text-center"
        >
            <Card.Content>
                <div
                    class="mx-auto mb-4 flex h-16 w-16 items-center justify-center rounded-2xl bg-muted"
                >
                    <HelpCircle class="h-8 w-8 text-muted-foreground" />
                </div>
                <h3 class="text-lg font-semibold text-foreground">
                    {searchQuery || typeFilter
                        ? "No questions match your filters"
                        : "No questions yet"}
                </h3>
                <p class="mt-1 text-sm text-muted-foreground">
                    {searchQuery || typeFilter
                        ? "Try adjusting your search or filter."
                        : "Create your first question to get started."}
                </p>
                {#if !searchQuery && !typeFilter}
                    <Button class="mt-4" onclick={openCreateDialog}>
                        <Plus class="mr-2 h-4 w-4" />
                        Create Question
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
                            <th class="px-4 py-3 font-medium">Question</th>
                            <th class="px-4 py-3 font-medium">Type</th>
                            <th class="px-4 py-3 font-medium text-center">
                                Max Score
                            </th>
                            <th class="px-4 py-3 font-medium">Created</th>
                            <th class="px-4 py-3 font-medium text-right"
                                >Actions</th
                            >
                        </tr>
                    </thead>
                    <tbody>
                        {#each filteredQuestions as question (question.id)}
                            <tr
                                class="border-b border-border/50 transition-colors hover:bg-muted/30"
                            >
                                <td
                                    class="max-w-md truncate px-4 py-3 font-medium text-foreground"
                                >
                                    {question.text}
                                </td>
                                <td class="px-4 py-3">
                                    <Badge
                                        variant={badgeVariant(question.type)}
                                    >
                                        {question.type.replace("_", " ")}
                                    </Badge>
                                </td>
                                <td
                                    class="px-4 py-3 text-center text-foreground"
                                >
                                    {question.maxScore}
                                </td>
                                <td
                                    class="px-4 py-3 text-muted-foreground whitespace-nowrap"
                                >
                                    {formatDate(question.createdAt)}
                                </td>
                                <td class="px-4 py-3">
                                    <div
                                        class="flex items-center justify-end gap-1"
                                    >
                                        <Button
                                            variant="ghost"
                                            size="sm"
                                            onclick={() =>
                                                openEditDialog(question)}
                                        >
                                            <Pencil class="h-4 w-4" />
                                        </Button>
                                        <Button
                                            variant="ghost"
                                            size="sm"
                                            onclick={() =>
                                                (deleteTarget = question)}
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
                    {filteredQuestions.length} of {questions.length} questions
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
            class="relative z-10 w-full max-w-lg mx-4 shadow-2xl border-border"
        >
            <Card.Header>
                <div class="flex items-center justify-between">
                    <div>
                        <Card.Title>
                            {editingQuestion ? "Edit Question" : "New Question"}
                        </Card.Title>
                        <Card.Description>
                            {editingQuestion
                                ? "Update the question details."
                                : "Add a new question to your bank."}
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
                <form onsubmit={handleSubmit} class="space-y-4">
                    {#if formError}
                        <div
                            class="rounded-lg border border-destructive/30 bg-destructive/10 px-4 py-3 text-sm text-destructive"
                        >
                            {formError}
                        </div>
                    {/if}

                    <div class="space-y-2">
                        <Label for="q-text">Question Text</Label>
                        <Input
                            id="q-text"
                            placeholder="e.g., How satisfied are you with..."
                            bind:value={formText}
                            required
                        />
                    </div>

                    <div class="grid grid-cols-2 gap-4">
                        <div class="space-y-2">
                            <Label for="q-type">Type</Label>
                            <Select.Root
                                type="single"
                                bind:value={formType}
                                required
                            >
                                <Select.Trigger id="q-type">
                                    {questionTypes.find(
                                        (t) => t.value === formType,
                                    )?.label ?? "Select type"}
                                </Select.Trigger>
                                <Select.Content>
                                    {#each questionTypes as qt}
                                        <Select.Item value={qt.value}
                                            >{qt.label}</Select.Item
                                        >
                                    {/each}
                                </Select.Content>
                            </Select.Root>
                        </div>
                        <div class="space-y-2">
                            <Label for="q-score">Max Score</Label>
                            <Input
                                id="q-score"
                                type="number"
                                min="1"
                                max="100"
                                bind:value={formMaxScore}
                                required
                            />
                        </div>
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
                            {editingQuestion ? "Save Changes" : "Create"}
                        </Button>
                    </div>
                </form>
            </Card.Content>
        </Card.Root>
    </div>
{/if}

<!-- Delete Dialog -->
<ConfirmDialog
    open={!!deleteTarget}
    title="Delete Question"
    description="This will deactivate the question. It won't appear in future surveys but existing data is preserved."
    confirmLabel="Delete"
    loading={deleteLoading}
    onConfirm={handleDelete}
    onCancel={() => (deleteTarget = null)}
/>
