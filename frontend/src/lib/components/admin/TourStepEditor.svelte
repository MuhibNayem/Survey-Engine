<script lang="ts">
	import { Button } from '$lib/components/ui/button';
	import { Input } from '$lib/components/ui/input';
	import { Label } from '$lib/components/ui/label';
	import { Textarea } from '$lib/components/ui/textarea';
	import * as Select from '$lib/components/ui/select';
	import { Switch } from '$lib/components/ui/switch';
	import { Badge } from '$lib/components/ui/badge';
	import { Alert, AlertDescription } from '$lib/components/ui/alert';
	import * as Card from '$lib/components/ui/card';
	import * as Accordion from '$lib/components/ui/accordion';
	import {
			Plus,
			Trash2,
			GripVertical,
			ChevronRight,
			ChevronDown,
			Target,
			LayoutTemplate,
			Settings2,
			AlertTriangle,
			CheckCircle,
			HelpCircle,
			ArrowUp,
			ArrowDown,
			Copy,
			Sparkles
		} from 'lucide-svelte';
	import { cn } from '$lib/utils';
	import { toast } from 'svelte-sonner';
	import type { TourMetadata, TourStep, TourStepPlacement, StepValidationResult } from './types';

	interface Props {
		/** Tour metadata to edit */
		value: TourMetadata;
		/** Called when tour data changes */
		onChange?: (value: TourMetadata) => void;
		/** Whether editor is disabled */
		disabled?: boolean;
		/** Custom class names */
		className?: string;
	}

	let { value, onChange, disabled = false, className }: Props = $props();

	// Currently selected step index
	let selectedStepIndex = $state<number>(0);
	// Expanded step IDs for accordion
	let expandedSteps = $state<Set<string>>(new Set());
	// Drag state
	let draggedIndex = $state<number | null>(null);
	// Target picker active state
	let targetPickerActive = $state(false);
	// Validation results per step
	let stepValidations = $state<Map<string, StepValidationResult>>(new Map());

	// Get current steps array
	let steps = $derived(value.steps);

	// Get selected step
	let selectedStep = $derived(steps[selectedStepIndex] ?? null);

	// Check if there are validation errors
	let hasErrors = $derived(steps.some((_, i) => !validateStep(i).isValid));

	// Initialize expanded steps
	$effect(() => {
		steps.forEach((step) => {
			if (!expandedSteps.has(step.id)) {
				expandedSteps.add(step.id);
			}
		});
	});

	/**
	 * Generate a unique step ID
	 */
	function generateStepId(): string {
		return `step-${Date.now()}-${Math.random().toString(36).substring(2, 9)}`;
	}

	/**
	 * Create a new tour step with default values
	 */
	function createNewStep(): TourStep {
		return {
			id: generateStepId(),
			title: 'New Step',
			description: 'Describe what this step highlights...',
			targetSelector: '',
			placement: 'bottom',
			dismissible: true,
			showSkip: false,
			required: true
		};
	}

	/**
	 * Add a new step at the specified index
	 */
	function addStep(index?: number): void {
		const newStep = createNewStep();
		const newSteps = [...steps];

		if (index !== undefined) {
			newSteps.splice(index + 1, 0, newStep);
			selectedStepIndex = index + 1;
		} else {
			newSteps.push(newStep);
			selectedStepIndex = newSteps.length - 1;
		}

		updateSteps(newSteps);
		expandedSteps.add(newStep.id);
		toast.success('Step added');
	}

	/**
	 * Remove a step at the specified index
	 */
	function removeStep(index: number): void {
		if (steps.length <= 1) {
			toast.error('Cannot remove the last step');
			return;
		}

		const newSteps = steps.filter((_, i) => i !== index);
		updateSteps(newSteps);

		if (selectedStepIndex >= newSteps.length) {
			selectedStepIndex = newSteps.length - 1;
		}

		toast.success('Step removed');
	}

	/**
	 * Duplicate a step
	 */
	function duplicateStep(index: number): void {
		const stepToDuplicate = steps[index];
		const newStep: TourStep = {
			...stepToDuplicate,
			id: generateStepId(),
			title: `${stepToDuplicate.title} (Copy)`
		};

		const newSteps = [...steps];
		newSteps.splice(index + 1, 0, newStep);
		updateSteps(newSteps);
		selectedStepIndex = index + 1;
		expandedSteps.add(newStep.id);
		toast.success('Step duplicated');
	}

	/**
	 * Move a step up in the list
	 */
	function moveStepUp(index: number): void {
		if (index === 0) return;

		const newSteps = [...steps];
		[newSteps[index - 1], newSteps[index]] = [newSteps[index], newSteps[index - 1]];
		updateSteps(newSteps);
		selectedStepIndex = index - 1;
	}

	/**
	 * Move a step down in the list
	 */
	function moveStepDown(index: number): void {
		if (index === steps.length - 1) return;

		const newSteps = [...steps];
		[newSteps[index], newSteps[index + 1]] = [newSteps[index + 1], newSteps[index]];
		updateSteps(newSteps);
		selectedStepIndex = index + 1;
	}

	/**
	 * Update a specific step
	 */
	function updateStep(index: number, updates: Partial<TourStep>): void {
		const newSteps = [...steps];
		newSteps[index] = { ...newSteps[index], ...updates };
		updateSteps(newSteps);
	}

	/**
	 * Update the steps array and notify parent
	 */
	function updateSteps(newSteps: TourStep[]): void {
		const newValue: TourMetadata = {
			...value,
			steps: newSteps
		};
		onChange?.(newValue);
	}

	/**
	 * Update tour configuration
	 */
	function updateConfig(updates: Partial<TourMetadata['config']>): void {
		const newValue: TourMetadata = {
			...value,
			config: {
				...value.config,
				...updates
			}
		};
		onChange?.(newValue);
	}

	/**
	 * Validate a step
	 */
	function validateStep(index: number): StepValidationResult {
		const step = steps[index];
		const errors: string[] = [];

		if (!step.title || step.title.trim() === '') {
			errors.push('Title is required');
		}

		if (!step.description || step.description.trim() === '') {
			errors.push('Description is required');
		}

		if (!step.targetSelector || step.targetSelector.trim() === '') {
			errors.push('Target selector is required');
		}

		const result: StepValidationResult = {
			isValid: errors.length === 0,
			errors,
			targetValid: step.targetSelector.trim() !== ''
		};

		stepValidations.set(step.id, result);
		return result;
	}

	/**
	 * Test if target selector works (simulated)
	 */
	function testTargetSelector(selector: string): void {
		if (!selector || selector.trim() === '') {
			toast.error('Please enter a selector first');
			return;
		}

		// In a real implementation, this would query the DOM
		// For now, we'll do basic validation
		try {
			// Basic selector validation
			if (selector.startsWith('.') || selector.startsWith('#') || /^[a-z]/i.test(selector)) {
				toast.success(`Selector "${selector}" looks valid`, {
					description: 'Test it in the live preview to confirm'
				});
			} else {
				toast.warning('Selector may be invalid', {
					description: 'Make sure it follows CSS selector syntax'
				});
			}
		} catch (e) {
			toast.error('Invalid selector syntax');
		}
	}

	/**
	 * Handle drag start
	 */
	function handleDragStart(index: number): void {
		draggedIndex = index;
	}

	/**
	 * Handle drag over
	 */
	function handleDragOver(event: DragEvent, index: number): void {
		event.preventDefault();
	}

	/**
	 * Handle drop
	 */
	function handleDrop(event: DragEvent, dropIndex: number): void {
		event.preventDefault();

		if (draggedIndex === null || draggedIndex === dropIndex) return;

		const newSteps = [...steps];
		const [draggedStep] = newSteps.splice(draggedIndex, 1);
		newSteps.splice(dropIndex, 0, draggedStep);

		updateSteps(newSteps);
		selectedStepIndex = dropIndex;
		draggedIndex = null;
		toast.success('Step reordered');
	}

	/**
	 * Handle drag end
	 */
	function handleDragEnd(): void {
		draggedIndex = null;
	}

	/**
	 * Get placement badge color
	 */
	function getPlacementColor(placement: TourStepPlacement): string {
		switch (placement) {
			case 'top':
				return 'bg-blue-100 text-blue-800 dark:bg-blue-900 dark:text-blue-300';
			case 'bottom':
				return 'bg-green-100 text-green-800 dark:bg-green-900 dark:text-green-300';
			case 'left':
				return 'bg-yellow-100 text-yellow-800 dark:bg-yellow-900 dark:text-yellow-300';
			case 'right':
				return 'bg-purple-100 text-purple-800 dark:bg-purple-900 dark:text-purple-300';
			default:
				return 'bg-gray-100 text-gray-800 dark:bg-gray-900 dark:text-gray-300';
		}
	}

	// Keyboard shortcuts - attach to window
	$effect(() => {
		function handleKeydown(event: KeyboardEvent): void {
			if ((event.ctrlKey || event.metaKey) && event.key === 's') {
				event.preventDefault();
				toast.success('Tour saved');
			}
		}

		window.addEventListener('keydown', handleKeydown);
		return () => window.removeEventListener('keydown', handleKeydown);
	});
</script>

<div class={cn('flex flex-col h-full gap-4', className)}>
	<!-- Header -->
	<div class="flex items-center justify-between">
		<div class="flex items-center gap-2">
			<LayoutTemplate class="h-5 w-5 text-primary" />
			<h3 class="text-lg font-semibold">Tour Steps</h3>
			<Badge variant="secondary">{steps.length} steps</Badge>
		</div>
		<Button onclick={() => addStep()} disabled={disabled} size="sm">
			<Plus class="h-4 w-4 mr-1" />
			Add Step
		</Button>
	</div>

	<!-- Main content - two panel layout -->
	<div class="flex flex-1 gap-4 min-h-0">
		<!-- Left Panel - Step List -->
		<Card.Root class="flex-1 flex flex-col overflow-hidden">
			<Card.Header class="py-3 border-b">
				<Card.Title class="text-sm font-medium">Step List</Card.Title>
				<Card.Description class="text-xs">
					Drag to reorder • Click to edit
				</Card.Description>
			</Card.Header>
			<Card.Content class="flex-1 overflow-y-auto p-2 space-y-2">
				{#each steps as step, index (step.id)}
					{@const validation = validateStep(index)}
					<div
						draggable={!disabled}
						ondragstart={() => handleDragStart(index)}
						ondragover={(e) => handleDragOver(e, index)}
						ondrop={(e) => handleDrop(e, index)}
						ondragend={handleDragEnd}
						onclick={() => (selectedStepIndex = index)}
						class={cn(
							'group flex items-center gap-2 p-3 rounded-lg border cursor-pointer transition-all',
							selectedStepIndex === index
								? 'border-primary bg-primary/5 shadow-sm'
								: 'border-border hover:border-primary/50 hover:bg-muted/50',
							draggedIndex === index && 'opacity-50',
							!validation.isValid && 'border-destructive/50 bg-destructive/5'
						)}
						role="tab"
						tabindex="0"
						aria-selected={selectedStepIndex === index}
						onkeydown={(e) => e.key === 'Enter' && (selectedStepIndex = index)}
					>
						<!-- Drag handle -->
						<button
							onclick={(e) => e.stopPropagation()}
							class="cursor-grab active:cursor-grabbing text-muted-foreground hover:text-foreground"
							disabled={disabled}
							aria-label="Drag to reorder"
						>
							<GripVertical class="h-4 w-4" />
						</button>

						<!-- Step number -->
						<div
							class={cn(
								'flex h-6 w-6 items-center justify-center rounded-full text-xs font-medium',
								selectedStepIndex === index
									? 'bg-primary text-primary-foreground'
									: 'bg-muted text-muted-foreground'
							)}
						>
							{index + 1}
						</div>

						<!-- Step info -->
						<div class="flex-1 min-w-0">
							<div class="flex items-center gap-2">
								<span class="text-sm font-medium truncate">{step.title || 'Untitled'}</span>
								{#if !validation.isValid}
									<AlertTriangle class="h-3.5 w-3.5 text-destructive flex-shrink-0" />
								{:else}
									<CheckCircle class="h-3.5 w-3.5 text-green-600 flex-shrink-0" />
								{/if}
							</div>
							<div class="flex items-center gap-2 mt-0.5">
								<span class="text-xs text-muted-foreground truncate max-w-[150px]">
									{step.targetSelector || 'No target'}
								</span>
								<Badge variant="secondary" class={cn('h-4 text-[10px]', getPlacementColor(step.placement))}>
									{step.placement}
								</Badge>
							</div>
						</div>

						<!-- Quick actions -->
						<div class="flex items-center gap-1 opacity-0 group-hover:opacity-100 transition-opacity">
							<button
								onclick={(e) => {
									e.stopPropagation();
									moveStepUp(index);
								}}
								class="p-1 hover:bg-muted rounded"
								disabled={index === 0 || disabled}
								aria-label="Move up"
							>
								<ArrowUp class="h-3.5 w-3.5" />
							</button>
							<button
								onclick={(e) => {
									e.stopPropagation();
									moveStepDown(index);
								}}
								class="p-1 hover:bg-muted rounded"
								disabled={index === steps.length - 1 || disabled}
								aria-label="Move down"
							>
								<ArrowDown class="h-3.5 w-3.5" />
							</button>
							<button
								onclick={(e) => {
									e.stopPropagation();
									duplicateStep(index);
								}}
								class="p-1 hover:bg-muted rounded"
								disabled={disabled}
								aria-label="Duplicate"
							>
								<Copy class="h-3.5 w-3.5" />
							</button>
							<button
								onclick={(e) => {
									e.stopPropagation();
									removeStep(index);
								}}
								class="p-1 hover:bg-destructive/10 text-destructive rounded"
								disabled={disabled}
								aria-label="Delete"
							>
								<Trash2 class="h-3.5 w-3.5" />
							</button>
						</div>
					</div>
				{/each}

				{#if steps.length === 0}
					<div class="flex flex-col items-center justify-center py-8 text-center text-muted-foreground">
						<LayoutTemplate class="h-8 w-8 mb-2 opacity-50" />
						<p class="text-sm">No steps yet</p>
						<p class="text-xs">Click "Add Step" to create your first step</p>
					</div>
				{/if}
			</Card.Content>
		</Card.Root>

		<!-- Right Panel - Step Editor -->
		<Card.Root class="flex-[1.5] flex flex-col overflow-hidden">
			<Card.Header class="py-3 border-b">
				<div class="flex items-center justify-between">
					<div>
						<Card.Title class="text-sm font-medium">
							{selectedStep ? `Step ${selectedStepIndex + 1}: ${selectedStep.title}` : 'No Step Selected'}
						</Card.Title>
						<Card.Description class="text-xs">Configure step properties</Card.Description>
					</div>
					{#if selectedStep}
						<div class="flex items-center gap-2">
							<Badge variant={validateStep(selectedStepIndex).isValid ? 'secondary' : 'destructive'}>
								{validateStep(selectedStepIndex).isValid ? 'Valid' : 'Invalid'}
							</Badge>
						</div>
					{/if}
				</div>
			</Card.Header>
			<Card.Content class="flex-1 overflow-y-auto p-4">
				{#if selectedStep}
					<div class="space-y-4">
						<!-- Step ID -->
						<div class="space-y-2">
							<Label for="step-id">Step ID</Label>
							<div class="flex gap-2">
								<Input
									id="step-id"
									value={selectedStep.id}
									oninput={(e) => updateStep(selectedStepIndex, { id: e.currentTarget.value })}
									disabled={disabled}
									class="font-mono text-xs"
									placeholder="step-unique-id"
								/>
								<Button
									variant="outline"
									size="icon"
									onclick={() => updateStep(selectedStepIndex, { id: generateStepId() })}
									disabled={disabled}
									aria-label="Generate new ID"
								>
									<Sparkles class="h-4 w-4" />
								</Button>
							</div>
							<p class="text-xs text-muted-foreground">Unique identifier for this step</p>
						</div>

						<!-- Title -->
						<div class="space-y-2">
							<Label for="step-title">Title *</Label>
							<Input
								id="step-title"
								value={selectedStep.title}
								oninput={(e) => updateStep(selectedStepIndex, { title: e.currentTarget.value })}
								disabled={disabled}
								placeholder="Enter step title"
							/>
						</div>

						<!-- Description -->
						<div class="space-y-2">
							<Label for="step-description">Description *</Label>
							<Textarea
								id="step-description"
								value={selectedStep.description}
								oninput={(e) => updateStep(selectedStepIndex, { description: e.currentTarget.value })}
								disabled={disabled}
								placeholder="Describe what this step highlights..."
								rows={3}
							/>
						</div>

						<!-- Target Selector -->
						<div class="space-y-2">
							<div class="flex items-center justify-between">
								<Label for="target-selector">Target Selector *</Label>
								<Button
									variant="ghost"
									size="sm"
									onclick={() => testTargetSelector(selectedStep.targetSelector)}
									disabled={disabled || !selectedStep.targetSelector}
									class="h-6 text-xs"
								>
									<Target class="h-3 w-3 mr-1" />
									Test
								</Button>
							</div>
							<div class="flex gap-2">
								<Input
									id="target-selector"
									value={selectedStep.targetSelector}
									oninput={(e) => updateStep(selectedStepIndex, { targetSelector: e.currentTarget.value })}
									disabled={disabled}
									placeholder="e.g., #my-element, .my-class, button"
									class="font-mono text-xs"
								/>
								<Button
									variant="outline"
									size="sm"
									onclick={() => {
										targetPickerActive = !targetPickerActive;
										toast.info('Target picker activated', {
											description: 'Click on an element in your app to select it'
										});
									}}
									disabled={disabled || targetPickerActive}
									aria-label="Element picker"
								>
									<HelpCircle class="h-4 w-4" />
								</Button>
							</div>
							<p class="text-xs text-muted-foreground">
								CSS selector for the element to highlight
							</p>
							{#if targetPickerActive}
								<Alert class="bg-blue-50 dark:bg-blue-950 border-blue-200 dark:border-blue-800 py-2">
									<AlertDescription class="text-sm text-blue-700 dark:text-blue-300">
										🎯 Target picker active: Click on any element in your application to select it as the target.
										Press Escape to cancel.
									</AlertDescription>
								</Alert>
							{/if}
						</div>

						<!-- Placement -->
						<div class="space-y-2">
							<Label for="placement">Placement</Label>
							<Select.Root
								type="single"
								value={selectedStep.placement}
								onValueChange={(v) => updateStep(selectedStepIndex, { placement: v as TourStepPlacement })}
							>
								<Select.Trigger class="w-full" disabled={disabled}>
									{selectedStep.placement || 'Select placement'}
								</Select.Trigger>
								<Select.Content>
									<Select.Item value="top">Top</Select.Item>
									<Select.Item value="bottom">Bottom</Select.Item>
									<Select.Item value="left">Left</Select.Item>
									<Select.Item value="right">Right</Select.Item>
									<Select.Item value="auto">Auto</Select.Item>
								</Select.Content>
							</Select.Root>
							<p class="text-xs text-muted-foreground">Where to position the tooltip</p>
						</div>

						<!-- Options -->
						<div class="space-y-3 pt-2 border-t">
							<Label class="text-xs font-medium">Step Options</Label>
							<div class="space-y-3">
								<div class="flex items-center justify-between">
									<div>
										<Label for="dismissible" class="text-sm">Dismissible</Label>
										<p class="text-xs text-muted-foreground">Allow clicking outside to close</p>
									</div>
									<Switch
										id="dismissible"
										checked={selectedStep.dismissible ?? true}
										onCheckedChange={(checked) => updateStep(selectedStepIndex, { dismissible: checked })}
										disabled={disabled}
									/>
								</div>
								<div class="flex items-center justify-between">
									<div>
										<Label for="show-skip" class="text-sm">Show Skip Button</Label>
										<p class="text-xs text-muted-foreground">Display a skip button on this step</p>
									</div>
									<Switch
										id="show-skip"
										checked={selectedStep.showSkip ?? false}
										onCheckedChange={(checked) => updateStep(selectedStepIndex, { showSkip: checked })}
										disabled={disabled}
									/>
								</div>
								<div class="flex items-center justify-between">
									<div>
										<Label for="step-required" class="text-sm">Required</Label>
										<p class="text-xs text-muted-foreground">Step must be completed</p>
									</div>
									<Switch
										id="step-required"
										checked={selectedStep.required ?? true}
										onCheckedChange={(checked) => updateStep(selectedStepIndex, { required: checked })}
										disabled={disabled}
									/>
								</div>
							</div>
						</div>

						<!-- Custom Class -->
						<div class="space-y-2">
							<Label for="custom-class">Custom CSS Class</Label>
							<Input
								id="custom-class"
								value={selectedStep.customClass ?? ''}
								oninput={(e) => updateStep(selectedStepIndex, { customClass: e.currentTarget.value })}
								disabled={disabled}
								placeholder="my-custom-class"
								class="font-mono text-xs"
							/>
						</div>
					</div>
				{:else}
					<div class="flex flex-col items-center justify-center h-full text-center text-muted-foreground">
						<LayoutTemplate class="h-12 w-12 mb-4 opacity-50" />
						<p class="text-sm">Select a step to edit</p>
						<p class="text-xs">or add a new step to get started</p>
					</div>
				{/if}
			</Card.Content>
		</Card.Root>
	</div>

	<!-- Tour Configuration -->
	<Card.Root>
		<Card.Header class="py-3">
			<div class="flex items-center gap-2">
				<Settings2 class="h-4 w-4 text-muted-foreground" />
				<Card.Title class="text-sm font-medium">Tour Configuration</Card.Title>
			</div>
		</Card.Header>
		<Card.Content class="py-3">
			<div class="grid grid-cols-2 md:grid-cols-4 gap-4">
				<div class="flex items-center justify-between">
					<div>
						<Label for="auto-start" class="text-sm">Auto Start</Label>
						<p class="text-xs text-muted-foreground">Start on page load</p>
					</div>
					<Switch
						id="auto-start"
						checked={value.config.autoStart}
						onCheckedChange={(checked) => updateConfig({ autoStart: checked })}
						disabled={disabled}
					/>
				</div>
				<div class="flex items-center justify-between">
					<div>
						<Label for="show-progress" class="text-sm">Show Progress</Label>
						<p class="text-xs text-muted-foreground">Display step counter</p>
					</div>
					<Switch
						id="show-progress"
						checked={value.config.showProgress}
						onCheckedChange={(checked) => updateConfig({ showProgress: checked })}
						disabled={disabled}
					/>
				</div>
				<div class="flex items-center justify-between">
					<div>
						<Label for="keyboard-nav" class="text-sm">Keyboard Nav</Label>
						<p class="text-xs text-muted-foreground">Arrow key navigation</p>
					</div>
					<Switch
						id="keyboard-nav"
						checked={value.config.keyboardNavigation}
						onCheckedChange={(checked) => updateConfig({ keyboardNavigation: checked })}
						disabled={disabled}
					/>
				</div>
				<div class="flex items-center justify-between">
					<div>
						<Label for="scroll-to-element" class="text-sm">Scroll To Element</Label>
						<p class="text-xs text-muted-foreground">Auto-scroll to target</p>
					</div>
					<Switch
						id="scroll-to-element"
						checked={value.config.scrollToElement}
						onCheckedChange={(checked) => updateConfig({ scrollToElement: checked })}
						disabled={disabled}
					/>
				</div>
			</div>
		</Card.Content>
	</Card.Root>

	<!-- Validation Summary -->
	{#if hasErrors}
		<Alert variant="destructive">
			<AlertTriangle class="h-4 w-4" />
			<AlertDescription>
				{steps.filter((_, i) => !validateStep(i).isValid).length} step(s) have validation errors.
				Please fix them before saving.
			</AlertDescription>
		</Alert>
	{/if}
</div>
