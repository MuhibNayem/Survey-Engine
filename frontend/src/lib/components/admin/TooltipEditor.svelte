<script lang="ts">
	import { Button } from '$lib/components/ui/button';
	import { Input } from '$lib/components/ui/input';
	import { Label } from '$lib/components/ui/label';
	import { Textarea } from '$lib/components/ui/textarea';
	import * as Select from '$lib/components/ui/select';
	import { Switch } from '$lib/components/ui/switch';
	import * as Card from '$lib/components/ui/card';
	import { Alert, AlertDescription } from '$lib/components/ui/alert';
	import {
		MousePointer2,
		Target,
		Clock,
		AlertCircle,
		CheckCircle2,
		Eye
	} from 'lucide-svelte';
	import { cn } from '$lib/utils';
	import { toast } from 'svelte-sonner';
	import type { TooltipMetadata, TooltipTrigger, TourStepPlacement } from './types';

	interface Props {
		/** Tooltip metadata to edit */
		value: TooltipMetadata;
		/** Called when tooltip data changes */
		onChange?: (value: TooltipMetadata) => void;
		/** Whether editor is disabled */
		disabled?: boolean;
		/** Custom class names */
		className?: string;
	}

	let { value, onChange, disabled = false, className }: Props = $props();

	// Validation state
	let errors = $state<string[]>([]);
	// Preview mode
	let showPreview = $state(false);

	// Validate tooltip
	function validate(): boolean {
		errors = [];

		if (!value.targetSelector || value.targetSelector.trim() === '') {
			errors.push('Target selector is required');
		}

		if (!value.content || value.content.trim() === '') {
			errors.push('Content is required');
		}

		if (value.showDelay < 0) {
			errors.push('Show delay cannot be negative');
		}

		if (value.hideDelay < 0) {
			errors.push('Hide delay cannot be negative');
		}

		return errors.length === 0;
	}

	// Run validation on value change
	$effect(() => {
		validate();
	});

	/**
	 * Update a field in the tooltip metadata
	 */
	function updateField<K extends keyof TooltipMetadata>(field: K, newValue: TooltipMetadata[K]): void {
		const newValueObj: TooltipMetadata = {
			...value,
			[field]: newValue
		};
		onChange?.(newValueObj);
	}

	/**
	 * Test target selector
	 */
	function testTargetSelector(): void {
		if (!value.targetSelector || value.targetSelector.trim() === '') {
			toast.error('Please enter a selector first');
			return;
		}

		toast.success(`Selector "${value.targetSelector}" looks valid`, {
			description: 'Test it in the live preview to confirm'
		});
	}

	/**
	 * Get trigger badge color
	 */
	function getTriggerColor(trigger: TooltipTrigger): string {
		switch (trigger) {
			case 'hover':
				return 'bg-blue-100 text-blue-800 dark:bg-blue-900 dark:text-blue-300';
			case 'click':
				return 'bg-green-100 text-green-800 dark:bg-green-900 dark:text-green-300';
			case 'focus':
				return 'bg-yellow-100 text-yellow-800 dark:bg-yellow-900 dark:text-yellow-300';
			case 'manual':
				return 'bg-purple-100 text-purple-800 dark:bg-purple-900 dark:text-purple-300';
			default:
				return 'bg-gray-100 text-gray-800';
		}
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
				return 'bg-gray-100 text-gray-800';
		}
	}
</script>

<div class={cn('flex flex-col gap-4', className)}>
	<!-- Header -->
	<div class="flex items-center justify-between">
		<div class="flex items-center gap-2">
			<MousePointer2 class="h-5 w-5 text-primary" />
			<h3 class="text-lg font-semibold">Tooltip Configuration</h3>
		</div>
		<Button variant="outline" size="sm" onclick={() => (showPreview = !showPreview)} disabled={disabled}>
			<Eye class="h-4 w-4 mr-1" />
			{showPreview ? 'Hide' : 'Show'} Preview
		</Button>
	</div>

	<!-- Validation errors -->
	{#if errors.length > 0}
		<Alert variant="destructive">
			<AlertCircle class="h-4 w-4" />
			<AlertDescription class="flex flex-col gap-1">
				{#each errors as error}
					<span class="text-sm">{error}</span>
				{/each}
			</AlertDescription>
		</Alert>
	{/if}

	<div class="grid gap-4 md:grid-cols-2">
		<!-- Left Column -->
		<div class="space-y-4">
			<!-- Target Selector -->
			<Card.Root>
				<Card.Header class="py-3">
					<div class="flex items-center gap-2">
						<Target class="h-4 w-4 text-muted-foreground" />
						<Card.Title class="text-sm font-medium">Target</Card.Title>
					</div>
				</Card.Header>
				<Card.Content class="space-y-3">
					<div class="space-y-2">
						<Label for="tooltip-target">Target Selector *</Label>
						<div class="flex gap-2">
							<Input
								id="tooltip-target"
								value={value.targetSelector}
								oninput={(e) => updateField('targetSelector', e.currentTarget.value)}
								disabled={disabled}
								placeholder="e.g., #help-button, .info-icon"
								class="font-mono text-xs"
							/>
							<Button
								variant="outline"
								size="icon"
								onclick={testTargetSelector}
								disabled={disabled || !value.targetSelector}
								aria-label="Test selector"
							>
								<Target class="h-4 w-4" />
							</Button>
						</div>
						<p class="text-xs text-muted-foreground">CSS selector for the element to attach the tooltip</p>
					</div>

					<div class="space-y-2">
						<Label for="tooltip-placement">Placement</Label>
						<Select.Root
							type="single"
							value={value.placement}
							onValueChange={(v) => updateField('placement', v as TourStepPlacement)}
						>
							<Select.Trigger class="w-full" disabled={disabled}>
								{value.placement || 'Select placement'}
							</Select.Trigger>
							<Select.Content>
								<Select.Item value="top">Top</Select.Item>
								<Select.Item value="bottom">Bottom</Select.Item>
								<Select.Item value="left">Left</Select.Item>
								<Select.Item value="right">Right</Select.Item>
								<Select.Item value="auto">Auto</Select.Item>
							</Select.Content>
						</Select.Root>
					</div>
				</Card.Content>
			</Card.Root>

			<!-- Content -->
			<Card.Root>
				<Card.Header class="py-3">
					<Card.Title class="text-sm font-medium">Content</Card.Title>
				</Card.Header>
				<Card.Content class="space-y-3">
					<div class="space-y-2">
						<Label for="tooltip-title">Title (Optional)</Label>
						<Input
							id="tooltip-title"
							value={value.title ?? ''}
							oninput={(e) => updateField('title', e.currentTarget.value)}
							disabled={disabled}
							placeholder="Tooltip title"
						/>
					</div>

					<div class="space-y-2">
						<Label for="tooltip-content">Content *</Label>
						<Textarea
							id="tooltip-content"
							value={value.content}
							oninput={(e) => updateField('content', e.currentTarget.value)}
							disabled={disabled}
							placeholder="Tooltip content (supports HTML)"
							rows={4}
						/>
						<p class="text-xs text-muted-foreground">HTML and markdown supported</p>
					</div>

					<div class="space-y-2">
						<Label for="tooltip-class">Custom CSS Class</Label>
						<Input
							id="tooltip-class"
							value={value.customClass ?? ''}
							oninput={(e) => updateField('customClass', e.currentTarget.value)}
							disabled={disabled}
							placeholder="my-tooltip-class"
							class="font-mono text-xs"
						/>
					</div>
				</Card.Content>
			</Card.Root>
		</div>

		<!-- Right Column -->
		<div class="space-y-4">
			<!-- Trigger Settings -->
			<Card.Root>
				<Card.Header class="py-3">
					<div class="flex items-center gap-2">
						<MousePointer2 class="h-4 w-4 text-muted-foreground" />
						<Card.Title class="text-sm font-medium">Trigger Settings</Card.Title>
					</div>
				</Card.Header>
				<Card.Content class="space-y-3">
					<div class="space-y-2">
						<Label for="tooltip-trigger">Trigger Type</Label>
						<Select.Root
							type="single"
							value={value.trigger}
							onValueChange={(v) => updateField('trigger', v as TooltipTrigger)}
						>
							<Select.Trigger class="w-full" disabled={disabled}>
								<div class="flex items-center gap-2">
									<span>{value.trigger}</span>
									<span class={cn('ml-auto px-2 py-0.5 rounded-full text-xs', getTriggerColor(value.trigger))}>
										{value.trigger}
									</span>
								</div>
							</Select.Trigger>
							<Select.Content>
								<Select.Item value="hover">Hover</Select.Item>
								<Select.Item value="click">Click</Select.Item>
								<Select.Item value="focus">Focus</Select.Item>
								<Select.Item value="manual">Manual</Select.Item>
							</Select.Content>
						</Select.Root>
						<p class="text-xs text-muted-foreground">How the tooltip is triggered</p>
					</div>

					<div class="flex items-center justify-between">
						<div>
							<Label for="tooltip-dismissible" class="text-sm">Dismissible</Label>
							<p class="text-xs text-muted-foreground">Allow users to dismiss the tooltip</p>
						</div>
						<Switch
							id="tooltip-dismissible"
							checked={value.dismissible}
							onCheckedChange={(checked) => updateField('dismissible', checked)}
							disabled={disabled}
						/>
					</div>
				</Card.Content>
			</Card.Root>

			<!-- Timing Settings -->
			<Card.Root>
				<Card.Header class="py-3">
					<div class="flex items-center gap-2">
						<Clock class="h-4 w-4 text-muted-foreground" />
						<Card.Title class="text-sm font-medium">Timing</Card.Title>
					</div>
				</Card.Header>
				<Card.Content class="space-y-3">
					<div class="space-y-2">
						<Label for="show-delay">Show Delay (ms)</Label>
						<Input
							id="show-delay"
							type="number"
							min="0"
							value={value.showDelay}
							oninput={(e) => updateField('showDelay', parseInt(e.currentTarget.value) || 0)}
							disabled={disabled}
						/>
						<p class="text-xs text-muted-foreground">Delay before showing tooltip</p>
					</div>

					<div class="space-y-2">
						<Label for="hide-delay">Hide Delay (ms)</Label>
						<Input
							id="hide-delay"
							type="number"
							min="0"
							value={value.hideDelay}
							oninput={(e) => updateField('hideDelay', parseInt(e.currentTarget.value) || 0)}
							disabled={disabled}
						/>
						<p class="text-xs text-muted-foreground">Delay before hiding tooltip</p>
					</div>

					<div class="space-y-2">
						<Label for="max-width">Max Width (px)</Label>
						<Input
							id="max-width"
							type="number"
							min="100"
							value={value.maxWidth ?? 300}
							oninput={(e) => updateField('maxWidth', parseInt(e.currentTarget.value) || 300)}
							disabled={disabled}
						/>
						<p class="text-xs text-muted-foreground">Maximum tooltip width</p>
					</div>
				</Card.Content>
			</Card.Root>

			<!-- Preview -->
			{#if showPreview}
				<Card.Root>
					<Card.Header class="py-3">
						<Card.Title class="text-sm font-medium">Preview</Card.Title>
					</Card.Header>
					<Card.Content>
						<div class="p-4 border rounded-lg bg-muted/50">
							<div class="text-center">
								<div class="inline-block p-2 bg-primary text-primary-foreground rounded mb-2">
									Target Element
								</div>
								<div
									class="mt-2 p-3 bg-background border rounded-lg shadow-lg text-sm max-w-[200px]"
									style="position: relative;"
								>
									{#if value.title}
										<div class="font-semibold mb-1">{value.title}</div>
									{/if}
									<div class="text-muted-foreground">{value.content || 'No content'}</div>
									<div class="absolute -bottom-2 left-1/2 -translate-x-1/2 w-0 h-0 border-l-8 border-r-8 border-t-8 border-l-transparent border-r-transparent border-t-background"></div>
								</div>
								<p class="text-xs text-muted-foreground mt-2">
									Placement: <span class="font-mono">{value.placement}</span>
								</p>
							</div>
						</div>
					</Card.Content>
				</Card.Root>
			{/if}
		</div>
	</div>
</div>
