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
        FolderKanban,
        GripVertical,
    } from "lucide-svelte";
    import type {
        CategoryResponse,
        QuestionResponse,
        CategoryQuestionMappingRequest,
    } from "$lib/types";

    // --- State ---
    let categories = $state<CategoryResponse[]>([]);
    let allQuestions = $state<QuestionResponse[]>([]);
    let loading = $state(true);
    let searchQuery = $state("");

    // Dialog
    let dialogOpen = $state(false);
    let editingCategory = $state<CategoryResponse | null>(null);
    let formName = $state("");
    let formDescription = $state("");
    let formMappings = $state<
        { questionId: string; sortOrder: number; weight: number }[]
    >([]);
    let formLoading = $state(false);
    let formError = $state<string | null>(null);

    // Delete
    let deleteTarget = $state<CategoryResponse | null>(null);
    let deleteLoading = $state(false);

    // --- Computed ---
    const filteredCategories = $derived(
        categories.filter(
            (c) =>
                !searchQuery ||
                c.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
                c.description
                    ?.toLowerCase()
                    .includes(searchQuery.toLowerCase()),
        ),
    );

    // --- API ---
    async function loadData() {
        loading = true;
        try {
            const [catRes, qRes] = await Promise.all([
                api.get<CategoryResponse[]>("/categories"),
                api.get<QuestionResponse[]>("/questions"),
            ]);
            categories = catRes.data;
            allQuestions = qRes.data;
        } catch {
            categories = [];
        } finally {
            loading = false;
        }
    }

    function openCreateDialog() {
        editingCategory = null;
        formName = "";
        formDescription = "";
        formMappings = [];
        formError = null;
        dialogOpen = true;
    }

    function openEditDialog(cat: CategoryResponse) {
        editingCategory = cat;
        formName = cat.name;
        formDescription = cat.description;
        formMappings = cat.questionMappings.map((m) => ({
            questionId: m.questionId,
            sortOrder: m.sortOrder,
            weight: m.weight,
        }));
        formError = null;
        dialogOpen = true;
    }

    function addMapping() {
        formMappings = [
            ...formMappings,
            {
                questionId: "",
                sortOrder: formMappings.length + 1,
                weight: 1,
            },
        ];
    }

    function removeMapping(index: number) {
        formMappings = formMappings.filter((_, i) => i !== index);
        // Re-number sort orders
        formMappings = formMappings.map((m, i) => ({
            ...m,
            sortOrder: i + 1,
        }));
    }

    function getQuestionText(id: string): string {
        const q = allQuestions.find((q) => q.id === id);
        return q?.text ?? "Unknown question";
    }

    async function handleSubmit(e: Event) {
        e.preventDefault();
        formLoading = true;
        formError = null;

        // Validate mappings have question IDs
        const invalidMappings = formMappings.filter((m) => !m.questionId);
        if (invalidMappings.length > 0) {
            formError = "Please select a question for each mapping.";
            formLoading = false;
            return;
        }

        try {
            const payload = {
                name: formName,
                description: formDescription,
                questionMappings:
                    formMappings as CategoryQuestionMappingRequest[],
            };
            if (editingCategory) {
                await api.put(`/categories/${editingCategory.id}`, payload);
            } else {
                await api.post("/categories", payload);
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
            await api.delete(`/categories/${deleteTarget.id}`);
            deleteTarget = null;
            await loadData();
        } catch {
            // silent
        } finally {
            deleteLoading = false;
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
    <title>Categories — Survey Engine</title>
</svelte:head>

<div class="space-y-6">
    <PageHeader
        title="Categories"
        description="Group questions into scored dimensions"
        actionLabel="New Category"
        actionIcon={Plus}
        onAction={openCreateDialog}
    />

    <!-- Search -->
    <div class="relative max-w-md">
        <Search
            class="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground"
        />
        <Input
            placeholder="Search categories..."
            bind:value={searchQuery}
            class="pl-9"
        />
    </div>

    <!-- Table -->
    {#if loading}
        <div class="flex items-center justify-center py-16">
            <span
                class="inline-block h-8 w-8 animate-spin rounded-full border-4 border-primary border-t-transparent"
            ></span>
        </div>
    {:else if filteredCategories.length === 0}
        <Card.Root
            class="flex flex-col items-center justify-center py-16 text-center"
        >
            <Card.Content>
                <div
                    class="mx-auto mb-4 flex h-16 w-16 items-center justify-center rounded-2xl bg-muted"
                >
                    <FolderKanban class="h-8 w-8 text-muted-foreground" />
                </div>
                <h3 class="text-lg font-semibold text-foreground">
                    {searchQuery
                        ? "No categories match your search"
                        : "No categories yet"}
                </h3>
                <p class="mt-1 text-sm text-muted-foreground">
                    {searchQuery
                        ? "Try adjusting your search."
                        : "Create categories to group and weight questions."}
                </p>
                {#if !searchQuery}
                    <Button class="mt-4" onclick={openCreateDialog}>
                        <Plus class="mr-2 h-4 w-4" />
                        Create Category
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
                            <th class="px-4 py-3 font-medium">Name</th>
                            <th class="px-4 py-3 font-medium">Description</th>
                            <th class="px-4 py-3 font-medium text-center"
                                >Questions</th
                            >
                            <th class="px-4 py-3 font-medium">Created</th>
                            <th class="px-4 py-3 font-medium text-right"
                                >Actions</th
                            >
                        </tr>
                    </thead>
                    <tbody>
                        {#each filteredCategories as cat (cat.id)}
                            <tr
                                class="border-b border-border/50 transition-colors hover:bg-muted/30"
                            >
                                <td
                                    class="px-4 py-3 font-medium text-foreground"
                                >
                                    {cat.name}
                                </td>
                                <td
                                    class="max-w-xs truncate px-4 py-3 text-muted-foreground"
                                >
                                    {cat.description || "—"}
                                </td>
                                <td class="px-4 py-3 text-center">
                                    <Badge variant="secondary">
                                        {cat.questionMappings.length}
                                    </Badge>
                                </td>
                                <td
                                    class="px-4 py-3 text-muted-foreground whitespace-nowrap"
                                >
                                    {formatDate(cat.createdAt)}
                                </td>
                                <td class="px-4 py-3">
                                    <div
                                        class="flex items-center justify-end gap-1"
                                    >
                                        <Button
                                            variant="ghost"
                                            size="sm"
                                            onclick={() => openEditDialog(cat)}
                                        >
                                            <Pencil class="h-4 w-4" />
                                        </Button>
                                        <Button
                                            variant="ghost"
                                            size="sm"
                                            onclick={() => (deleteTarget = cat)}
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
                    {filteredCategories.length} of {categories.length} categories
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
            class="relative z-10 w-full max-w-2xl mx-4 max-h-[90vh] overflow-y-auto shadow-2xl border-border"
        >
            <Card.Header>
                <div class="flex items-center justify-between">
                    <div>
                        <Card.Title>
                            {editingCategory ? "Edit Category" : "New Category"}
                        </Card.Title>
                        <Card.Description>
                            {editingCategory
                                ? "Update category details and question mappings."
                                : "Create a new category and assign questions."}
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

                    <div class="space-y-4">
                        <div class="space-y-2">
                            <Label for="cat-name">Name</Label>
                            <Input
                                id="cat-name"
                                placeholder="e.g., Communication Skills"
                                bind:value={formName}
                                required
                            />
                        </div>
                        <div class="space-y-2">
                            <Label for="cat-desc">Description</Label>
                            <Textarea
                                id="cat-desc"
                                placeholder="Describe this category..."
                                bind:value={formDescription}
                            />
                        </div>
                    </div>

                    <!-- Mappings -->
                    <div class="space-y-3">
                        <div class="flex items-center justify-between">
                            <Label>Question Mappings</Label>
                            <Button
                                variant="outline"
                                size="sm"
                                type="button"
                                onclick={addMapping}
                            >
                                <Plus class="mr-1 h-3 w-3" />
                                Add
                            </Button>
                        </div>

                        {#if formMappings.length === 0}
                            <div
                                class="rounded-lg border-2 border-dashed border-border px-4 py-8 text-center text-sm text-muted-foreground"
                            >
                                No questions mapped yet. Click "Add" to map
                                questions.
                            </div>
                        {:else}
                            <div class="space-y-2">
                                {#each formMappings as mapping, i}
                                    <div
                                        class="flex items-center gap-2 rounded-lg border border-border bg-muted/20 p-3"
                                    >
                                        <GripVertical
                                            class="h-4 w-4 shrink-0 text-muted-foreground"
                                        />
                                        <span
                                            class="shrink-0 text-xs font-medium text-muted-foreground w-6"
                                        >
                                            #{mapping.sortOrder}
                                        </span>
                                        <Select.Root
                                            type="single"
                                            bind:value={
                                                formMappings[i].questionId
                                            }
                                        >
                                            <Select.Trigger class="flex-1">
                                                {formMappings[i].questionId
                                                    ? allQuestions.find(
                                                          (q) =>
                                                              q.id ===
                                                              formMappings[i]
                                                                  .questionId,
                                                      )?.text
                                                    : "Select question"}
                                            </Select.Trigger>
                                            <Select.Content>
                                                {#each allQuestions as q}
                                                    <Select.Item value={q.id}
                                                        >{q.text}</Select.Item
                                                    >
                                                {/each}
                                            </Select.Content>
                                        </Select.Root>
                                        <div
                                            class="flex items-center gap-1 shrink-0"
                                        >
                                            <Label
                                                for="w-{i}"
                                                class="text-xs text-muted-foreground"
                                                >W:</Label
                                            >
                                            <Input
                                                id="w-{i}"
                                                type="number"
                                                min="0"
                                                step="0.1"
                                                bind:value={
                                                    formMappings[i].weight
                                                }
                                                class="w-16"
                                                required
                                            />
                                        </div>
                                        <Button
                                            variant="ghost"
                                            size="sm"
                                            type="button"
                                            onclick={() => removeMapping(i)}
                                        >
                                            <Trash2
                                                class="h-4 w-4 text-destructive"
                                            />
                                        </Button>
                                    </div>
                                {/each}
                            </div>
                        {/if}
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
                            {editingCategory ? "Save Changes" : "Create"}
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
    title="Delete Category"
    description="This will deactivate the category. Existing survey data is preserved."
    confirmLabel="Delete"
    loading={deleteLoading}
    onConfirm={handleDelete}
    onCancel={() => (deleteTarget = null)}
/>
