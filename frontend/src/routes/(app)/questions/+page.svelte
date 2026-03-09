<script lang="ts">
	import api from '$lib/api';
	import { onMount } from 'svelte';
	import * as Card from '$lib/components/ui/card';
	import { Button } from '$lib/components/ui/button';
	import { Input } from '$lib/components/ui/input';
	import { Label } from '$lib/components/ui/label';
	import { Badge } from '$lib/components/ui/badge';
	import * as Select from '$lib/components/ui/select';
	import { ConfirmDialog } from '$lib/components/ui/confirm-dialog';
	import { EmptyState } from '$lib/components/empty-state';
	import * as DropdownMenu from '$lib/components/ui/dropdown-menu';
	import PageHeader from '$lib/components/layout/PageHeader.svelte';
	import Pagination from '$lib/components/ui/pagination/Pagination.svelte';
	import { Skeleton } from '$lib/components/ui/skeleton';
	import { useKeyboardShortcuts, commonShortcuts } from '$lib/hooks/useKeyboardShortcuts.svelte';
	import { Confetti } from '$lib/components/confetti';
	import { Plus, Pencil, Trash2, Search, X, HelpCircle, MoreHorizontal } from 'lucide-svelte';
	import type { QuestionResponse, QuestionType, PageResponse } from '$lib/types';

	// --- State ---
	let questions = $state<QuestionResponse[]>([]);
	let loading = $state(true);
	let searchQuery = $state('');
	let typeFilter = $state<QuestionType | ''>('');

	// Pagination specific state
	let currentPage = $state(0);
	let pageSize = $state(10);
	let totalElements = $state(0);
	let totalPages = $state(0);

	// Dialog state
	let dialogOpen = $state(false);
	let editingQuestion = $state<QuestionResponse | null>(null);
	let formText = $state('');
	let formType = $state<QuestionType>('RATING_SCALE');
	let formMaxScore = $state(5);
	type OptionRow = { id: number; value: string; score: string | number };
	let optionRowSeq = 1;
	let formOptions = $state<OptionRow[]>([]);
	let formLoading = $state(false);
	let formError = $state<string | null>(null);

	// Delete dialog
	let deleteTarget = $state<QuestionResponse | null>(null);
	let deleteLoading = $state(false);

	// Confetti celebration
	let showConfetti = $state(false);
	let confettiTitle = $state('');
	let confettiMessage = $state('');

	// --- Computed ---
	const filteredQuestions = $derived(
		questions.filter((q) => {
			const matchesSearch = !searchQuery || q.text.toLowerCase().includes(searchQuery.toLowerCase());
			const matchesType = !typeFilter || q.type === typeFilter;
			return matchesSearch && matchesType;
		})
	);

	const questionTypes: { value: QuestionType; label: string }[] = [
		{ value: 'RATING_SCALE', label: 'Rating Scale' },
		{ value: 'SINGLE_CHOICE', label: 'Single Choice' },
		{ value: 'MULTIPLE_CHOICE', label: 'Multiple Choice' },
		{ value: 'RANK', label: 'Rank' }
	];

	// --- API ---
	async function loadQuestions() {
		loading = true;
		try {
			const { data } = await api.get<PageResponse<QuestionResponse>>(
				`/questions?page=${currentPage}&size=${pageSize}`
			);
			const qData = data as any;
			questions = Array.isArray(qData) ? qData : (qData.content || []);
			totalElements = qData.totalElements || (Array.isArray(qData) ? qData.length : 0);
			totalPages = qData.totalPages || 1;
		} catch {
			questions = [];
			totalElements = 0;
			totalPages = 0;
		} finally {
			loading = false;
		}
	}

	function createOptionRow(value = '', score = ''): OptionRow {
		return { id: optionRowSeq++, value, score };
	}

	function addOptionRow() {
		formOptions = [...formOptions, createOptionRow()];
	}

	function removeOptionRow(id: number) {
		if (formOptions.length <= 1) return;
		formOptions = formOptions.filter((option) => option.id !== id);
	}

	function openCreateDialog() {
		editingQuestion = null;
		formText = '';
		formType = 'RATING_SCALE';
		formMaxScore = 5;
		formOptions = [];
		formError = null;
		dialogOpen = true;
	}

	function openEditDialog(q: QuestionResponse) {
		editingQuestion = q;
		formText = q.text;
		formType = q.type;
		formMaxScore = q.maxScore;
		formOptions = optionConfigToRows(q.optionConfig);
		formError = null;
		dialogOpen = true;
	}

	function requiresOptions(type: QuestionType): boolean {
		return type === 'SINGLE_CHOICE' || type === 'MULTIPLE_CHOICE' || type === 'RANK';
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
					if (typeof option === 'string') {
						const value = option.trim();
						if (!value) return null;
						return createOptionRow(value);
					}
					const value = option?.value?.trim() ?? '';
					if (!value) return null;
					const score = option.score != null ? String(option.score) : '';
					return createOptionRow(value, score);
				})
				.filter((option): option is OptionRow => option !== null);
		} catch {
			return [];
		}
	}

	function buildOptionConfigFromRows(rows: OptionRow[]): { optionConfig?: string; error?: string } {
		const options: Array<string | { value: string; score: number }> = [];
		for (const row of rows) {
			const valuePart = String(row.value ?? '').trim();
			const scorePart = row.score === null || row.score === undefined ? '' : String(row.score).trim();
			if (!valuePart && !scorePart) continue;
			if (!valuePart) {
				return { error: "Option value cannot be empty." };
			}
			if (scorePart) {
				const score = Number(scorePart);
				if (!Number.isFinite(score) || score < 0) {
					return { error: `Invalid score for option '${valuePart}'.` };
				}
				options.push({ value: valuePart, score });
			} else {
				options.push(valuePart);
			}
		}
		if (options.length === 0) {
			return { error: 'At least one option is required for this question type.' };
		}
		return { optionConfig: JSON.stringify({ options }) };
	}

	async function handleSubmit(e: Event) {
		e.preventDefault();
		formLoading = true;
		formError = null;
		try {
			let optionConfig: string | undefined;
			if (requiresOptions(formType)) {
				const parsed = buildOptionConfigFromRows(formOptions);
				if (parsed.error) {
					formError = parsed.error;
					formLoading = false;
					return;
				}
				optionConfig = parsed.optionConfig;
			}

			const payload = {
				text: formText,
				type: formType,
				maxScore: formMaxScore,
				optionConfig
			};
			if (editingQuestion) {
				await api.put(`/questions/${editingQuestion.id}`, payload);
			} else {
				await api.post('/questions', payload);
				// 🎉 Celebrate first question creation
				if (questions.length === 0) {
					showConfetti = true;
					confettiTitle = '❓ Question Created!';
					confettiMessage = 'Your first question has been added to the question bank.';
					setTimeout(() => (showConfetti = false), 3500);
				}
			}
			dialogOpen = false;
			await loadQuestions();
		} catch (err: unknown) {
			const axiosErr = err as { response?: { data?: { message?: string } } };
			formError = axiosErr?.response?.data?.message ?? 'Something went wrong.';
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
			case 'RATING_SCALE':
				return 'default' as const;
			case 'SINGLE_CHOICE':
				return 'secondary' as const;
			case 'MULTIPLE_CHOICE':
				return 'outline' as const;
			case 'RANK':
				return 'secondary' as const;
			default:
				return 'default' as const;
		}
	}

	function formatDate(iso: string) {
		return new Date(iso).toLocaleDateString('en-US', {
			month: 'short',
			day: 'numeric',
			year: 'numeric'
		});
	}

	$effect(() => {
		if (requiresOptions(formType) && formOptions.length === 0) {
			formOptions = [createOptionRow()];
			return;
		}
		if (!requiresOptions(formType) && formOptions.length > 0) {
			formOptions = [];
		}
	});

	// Keyboard shortcuts
	useKeyboardShortcuts([
		commonShortcuts.createNew(() => {
			if (!dialogOpen) openCreateDialog();
		}),
		commonShortcuts.search(() => {
			const input = document.querySelector('input[placeholder="Search questions..."]') as HTMLInputElement;
			input?.focus();
		})
	]);

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
			<Search class="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" aria-hidden="true" />
			<Input
				placeholder="Search questions..."
				bind:value={searchQuery}
				class="pl-9"
				aria-label="Search questions"
			/>
		</div>
		<Select.Root type="single" bind:value={typeFilter}>
			<Select.Trigger class="w-full sm:w-48" aria-label="Filter by type">
				{typeFilter ? questionTypes.find((t) => t.value === typeFilter)?.label : 'All Types'}
			</Select.Trigger>
			<Select.Content>
				<Select.Item value="">All Types</Select.Item>
				{#each questionTypes as qt}
					<Select.Item value={qt.value}>{qt.label}</Select.Item>
				{/each}
			</Select.Content>
		</Select.Root>
	</div>

	<!-- Aria-live region -->
	<div aria-live="polite" aria-atomic="true" class="sr-only">
		{#if loading}<span>Loading questions...</span>{/if}
		{#if formLoading}<span>Saving changes...</span>{/if}
	</div>

	<!-- Loading State -->
	{#if loading}
		<Card.Root>
			<div class="overflow-x-auto">
				<table class="w-full text-sm">
					<thead>
						<tr class="border-b border-border text-left text-muted-foreground">
							<th class="px-4 py-3 font-medium">Question</th>
							<th class="px-4 py-3 font-medium">Type</th>
							<th class="px-4 py-3 font-medium text-center">Max Score</th>
							<th class="px-4 py-3 font-medium">Created</th>
							<th class="px-4 py-3 font-medium text-right">Actions</th>
						</tr>
					</thead>
					<tbody>
						{#each Array(10) as _}
							<tr class="border-b border-border/50">
								<td class="px-4 py-3"><Skeleton class="h-4 w-[300px]" /></td>
								<td class="px-4 py-3"><Skeleton class="h-6 w-[100px]" /></td>
								<td class="px-4 py-3 text-center"><Skeleton class="h-4 w-[30px] mx-auto" /></td>
								<td class="px-4 py-3"><Skeleton class="h-4 w-[100px]" /></td>
								<td class="px-4 py-3 text-right">
									<div class="flex items-center justify-end gap-1">
										<Skeleton class="h-8 w-8" />
										<Skeleton class="h-8 w-8" />
									</div>
								</td>
							</tr>
						{/each}
					</tbody>
				</table>
			</div>
		</Card.Root>

	{:else if filteredQuestions.length === 0}
		<!-- Enhanced Empty State -->
		<EmptyState
			title={searchQuery || typeFilter ? 'No questions match your filters' : 'No questions yet'}
			description={
				searchQuery || typeFilter
					? 'Try adjusting your search or filter.'
					: 'Create your first question to build your question bank. Questions can be reused across multiple surveys.'
			}
			actionLabel="Create Question"
			onAction={openCreateDialog}
			illustration="question"
		/>

	{:else}
		<!-- Desktop Table -->
		<div class="hidden md:block">
			<Card.Root>
				<div class="overflow-x-auto">
					<table class="w-full text-sm">
						<thead>
							<tr class="border-b border-border text-left text-muted-foreground">
								<th class="px-4 py-3 font-medium">Question</th>
								<th class="px-4 py-3 font-medium">Type</th>
								<th class="px-4 py-3 font-medium text-center">Max Score</th>
								<th class="px-4 py-3 font-medium">Created</th>
								<th class="px-4 py-3 font-medium text-right">Actions</th>
							</tr>
						</thead>
						<tbody>
							{#each filteredQuestions as question (question.id)}
								<tr class="border-b border-border/50 transition-colors hover:bg-muted/30">
									<td class="max-w-md truncate px-4 py-3 font-medium text-foreground">
										{question.text}
									</td>
									<td class="px-4 py-3">
										<Badge variant={badgeVariant(question.type)}>
											{question.type.replace('_', ' ')}
										</Badge>
									</td>
									<td class="px-4 py-3 text-center text-foreground">{question.maxScore}</td>
									<td class="px-4 py-3 text-muted-foreground whitespace-nowrap">
										{formatDate(question.createdAt)}
									</td>
									<td class="px-4 py-3">
										<div class="flex items-center justify-end gap-1">
											<Button
												variant="ghost"
												size="sm"
												title="Edit Question"
												onclick={() => openEditDialog(question)}
											>
												<Pencil class="h-4 w-4" aria-hidden="true" />
											</Button>
											<Button
												variant="ghost"
												size="sm"
												title="Delete Question"
												onclick={() => (deleteTarget = question)}
											>
												<Trash2 class="h-4 w-4 text-destructive" aria-hidden="true" />
											</Button>
										</div>
									</td>
								</tr>
							{/each}
						</tbody>
					</table>
				</div>
				<div class="border-t border-border pt-2 pb-4"></div>
				<Pagination
					{currentPage}
					{totalPages}
					{totalElements}
					{pageSize}
					onPageChange={(p) => { currentPage = p; loadQuestions(); }}
					onSizeChange={(s) => { pageSize = s; currentPage = 0; loadQuestions(); }}
				/>
			</Card.Root>
		</div>

		<!-- Mobile Cards -->
		<div class="md:hidden grid gap-4">
			{#each filteredQuestions as question (question.id)}
				<Card.Root class="p-4">
					<div class="flex justify-between items-start">
						<div class="space-y-2 flex-1 min-w-0">
							<h3 class="font-semibold text-foreground truncate">{question.text}</h3>
							<div class="flex flex-wrap gap-2">
								<Badge variant={badgeVariant(question.type)}>
									{question.type.replace('_', ' ')}
								</Badge>
								<Badge variant="outline">Score: {question.maxScore}</Badge>
							</div>
							<p class="text-xs text-muted-foreground">Created: {formatDate(question.createdAt)}</p>
						</div>
						<DropdownMenu.Root>
							<DropdownMenu.Trigger>
								<Button variant="ghost" size="icon" aria-label="Question actions">
									<MoreHorizontal class="h-5 w-5" aria-hidden="true" />
								</Button>
							</DropdownMenu.Trigger>
							<DropdownMenu.Content align="end">
								<DropdownMenu.Item onclick={() => openEditDialog(question)}>
									<Pencil class="mr-2 h-4 w-4" aria-hidden="true" />
									Edit
								</DropdownMenu.Item>
								<DropdownMenu.Separator />
								<DropdownMenu.Item class="text-destructive" onclick={() => (deleteTarget = question)}>
									<Trash2 class="mr-2 h-4 w-4" aria-hidden="true" />
									Delete
								</DropdownMenu.Item>
							</DropdownMenu.Content>
						</DropdownMenu.Root>
					</div>
				</Card.Root>
			{/each}
		</div>

		<!-- Pagination for mobile -->
		<div class="md:hidden">
			<Pagination
				{currentPage}
				{totalPages}
				{totalElements}
				{pageSize}
				onPageChange={(p) => { currentPage = p; loadQuestions(); }}
				onSizeChange={(s) => { pageSize = s; currentPage = 0; loadQuestions(); }}
			/>
		</div>
	{/if}
</div>

<!-- Create/Edit Dialog -->
{#if dialogOpen}
	<div class="fixed inset-0 z-50 flex items-center justify-center" role="dialog" aria-modal="true">
		<button class="absolute inset-0 bg-black/50 backdrop-blur-sm" onclick={() => (dialogOpen = false)} aria-label="Close dialog"></button>
		<Card.Root class="relative z-10 w-full max-w-lg mx-4 shadow-2xl border-border">
			<Card.Header>
				<div class="flex items-center justify-between">
					<div>
						<Card.Title>{editingQuestion ? 'Edit Question' : 'New Question'}</Card.Title>
						<Card.Description>
							{editingQuestion ? 'Update the question details.' : 'Add a new question to your bank.'}
						</Card.Description>
					</div>
					<Button variant="ghost" size="sm" onclick={() => (dialogOpen = false)}>
						<X class="h-4 w-4" aria-hidden="true" />
					</Button>
				</div>
			</Card.Header>
			<Card.Content>
				<form onsubmit={handleSubmit} class="space-y-4">
					{#if formError}
						<div class="rounded-lg border border-destructive/30 bg-destructive/10 px-4 py-3 text-sm text-destructive" role="alert">
							{formError}
						</div>
					{/if}

					<div class="space-y-2">
						<Label for="q-text">Question Text</Label>
						<Input id="q-text" placeholder="e.g., How satisfied are you with..." bind:value={formText} required />
					</div>

					<div class="grid grid-cols-2 gap-4">
						<div class="space-y-2">
							<Label for="q-type">Type</Label>
							<Select.Root type="single" bind:value={formType} required>
								<Select.Trigger id="q-type">
									{questionTypes.find((t) => t.value === formType)?.label ?? 'Select type'}
								</Select.Trigger>
								<Select.Content>
									{#each questionTypes as qt}
										<Select.Item value={qt.value}>{qt.label}</Select.Item>
									{/each}
								</Select.Content>
							</Select.Root>
						</div>
						<div class="space-y-2">
							<Label for="q-score">Max Score</Label>
							<Input id="q-score" type="number" min="1" max="100" bind:value={formMaxScore} required />
						</div>
					</div>

					{#if requiresOptions(formType)}
						<div class="space-y-2">
							<div class="flex items-center justify-between">
								<Label>Options</Label>
								<Button type="button" variant="outline" size="sm" onclick={addOptionRow}>
									<Plus class="mr-1 h-3.5 w-3.5" aria-hidden="true" />
									Add Option
								</Button>
							</div>
							<p class="text-xs text-muted-foreground">Add one option per row. Score is optional.</p>
							<div class="space-y-2">
								{#each formOptions as option, idx (option.id)}
									<div class="grid grid-cols-[1fr_110px_auto] gap-2">
										<Input placeholder={`Option ${idx + 1}`} bind:value={option.value} />
										<Input type="number" min="0" step="1" placeholder="Score" bind:value={option.score} />
										<Button
											type="button"
											variant="ghost"
											size="icon"
											onclick={() => removeOptionRow(option.id)}
											disabled={formOptions.length <= 1}
											aria-label="Remove option"
										>
											<X class="h-4 w-4" aria-hidden="true" />
										</Button>
									</div>
								{/each}
							</div>
						</div>
					{/if}

					<div class="flex justify-end gap-2 pt-2">
						<Button variant="outline" type="button" onclick={() => (dialogOpen = false)}>Cancel</Button>
						<Button type="submit" disabled={formLoading}>
							{#if formLoading}
								<span class="mr-2 inline-block h-4 w-4 animate-spin rounded-full border-2 border-current border-t-transparent" aria-hidden="true"></span>
							{/if}
							{editingQuestion ? 'Save Changes' : 'Create'}
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

<!-- 🎉 Confetti Celebration -->
{#if showConfetti}
	<Confetti
		fire={showConfetti}
		showBanner={true}
		title={confettiTitle}
		message={confettiMessage}
		particleCount={120}
		spread={70}
		startVelocity={45}
		duration={3000}
		colors={['#10b981', '#3b82f6', '#f59e0b']}
		onComplete={() => (showConfetti = false)}
	/>
{/if}
