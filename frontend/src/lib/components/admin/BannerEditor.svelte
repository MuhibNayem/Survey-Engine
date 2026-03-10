<script lang="ts">
	import { Button } from '$lib/components/ui/button';
	import { Input } from '$lib/components/ui/input';
	import { Label } from '$lib/components/ui/label';
	import { Textarea } from '$lib/components/ui/textarea';
	import * as Select from '$lib/components/ui/select';
	import { Switch } from '$lib/components/ui/switch';
	import * as Card from '$lib/components/ui/card';
	import { Alert, AlertDescription } from '$lib/components/ui/alert';
	import { Badge } from '$lib/components/ui/badge';
	import {
		Megaphone,
		AlertCircle,
		Calendar,
		Link2,
		Palette,
		Flag,
		Eye
	} from 'lucide-svelte';
	import { cn } from '$lib/utils';
	import { toast } from 'svelte-sonner';
	import type { BannerMetadata, BannerPriority, BannerVariant } from './types';

	interface Props {
		/** Banner metadata to edit */
		value: BannerMetadata;
		/** Called when banner data changes */
		onChange?: (value: BannerMetadata) => void;
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

	// Validate banner
	function validate(): boolean {
		errors = [];

		if (!value.title || value.title.trim() === '') {
			errors.push('Title is required');
		}

		if (!value.content || value.content.trim() === '') {
			errors.push('Content is required');
		}

		if (value.startDate && value.endDate) {
			const start = new Date(value.startDate);
			const end = new Date(value.endDate);
			if (start > end) {
				errors.push('Start date must be before end date');
			}
		}

		if (value.ctaText && !value.ctaUrl) {
			errors.push('CTA URL is required when CTA text is provided');
		}

		return errors.length === 0;
	}

	// Run validation on value change
	$effect(() => {
		validate();
	});

	/**
	 * Update a field in the banner metadata
	 */
	function updateField<K extends keyof BannerMetadata>(field: K, newValue: BannerMetadata[K]): void {
		const newValueObj: BannerMetadata = {
			...value,
			[field]: newValue
		};
		onChange?.(newValueObj);
	}

	/**
	 * Get priority badge color
	 */
	function getPriorityColor(priority: BannerPriority): string {
		switch (priority) {
			case 'low':
				return 'bg-gray-100 text-gray-800 dark:bg-gray-900 dark:text-gray-300';
			case 'medium':
				return 'bg-blue-100 text-blue-800 dark:bg-blue-900 dark:text-blue-300';
			case 'high':
				return 'bg-orange-100 text-orange-800 dark:bg-orange-900 dark:text-orange-300';
			case 'urgent':
				return 'bg-red-100 text-red-800 dark:bg-red-900 dark:text-red-300';
			default:
				return 'bg-gray-100 text-gray-800';
		}
	}

	/**
	 * Get variant badge color
	 */
	function getVariantColor(variant: BannerVariant): string {
		switch (variant) {
			case 'default':
				return 'bg-gray-100 text-gray-800 dark:bg-gray-900 dark:text-gray-300';
			case 'info':
				return 'bg-blue-100 text-blue-800 dark:bg-blue-900 dark:text-blue-300';
			case 'success':
				return 'bg-green-100 text-green-800 dark:bg-green-900 dark:text-green-300';
			case 'warning':
				return 'bg-yellow-100 text-yellow-800 dark:bg-yellow-900 dark:text-yellow-300';
			case 'error':
				return 'bg-red-100 text-red-800 dark:bg-red-900 dark:text-red-300';
			default:
				return 'bg-gray-100 text-gray-800';
		}
	}

	/**
	 * Get variant border color for preview
	 */
	function getVariantBorderColor(variant: BannerVariant): string {
		switch (variant) {
			case 'info':
				return 'border-blue-500';
			case 'success':
				return 'border-green-500';
			case 'warning':
				return 'border-yellow-500';
			case 'error':
				return 'border-red-500';
			default:
				return 'border-gray-300 dark:border-gray-700';
		}
	}

	/**
	 * Get variant background color for preview
	 */
	function getVariantBgColor(variant: BannerVariant): string {
		switch (variant) {
			case 'info':
				return 'bg-blue-50 dark:bg-blue-950';
			case 'success':
				return 'bg-green-50 dark:bg-green-950';
			case 'warning':
				return 'bg-yellow-50 dark:bg-yellow-950';
			case 'error':
				return 'bg-red-50 dark:bg-red-950';
			default:
				return 'bg-gray-50 dark:bg-gray-900';
		}
	}
</script>

<div class={cn('flex flex-col gap-4', className)}>
	<!-- Header -->
	<div class="flex items-center justify-between">
		<div class="flex items-center gap-2">
			<Megaphone class="h-5 w-5 text-primary" />
			<h3 class="text-lg font-semibold">Banner Configuration</h3>
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

	<div class="grid gap-4 lg:grid-cols-3">
		<!-- Left Column - Content -->
		<div class="lg:col-span-2 space-y-4">
			<!-- Basic Info -->
			<Card.Root>
				<Card.Header class="py-3">
					<Card.Title class="text-sm font-medium">Content</Card.Title>
				</Card.Header>
				<Card.Content class="space-y-4">
					<div class="grid gap-4 md:grid-cols-2">
						<div class="space-y-2">
							<Label for="banner-title">Title *</Label>
							<Input
								id="banner-title"
								value={value.title}
								oninput={(e) => updateField('title', e.currentTarget.value)}
								disabled={disabled}
								placeholder="Banner title"
							/>
						</div>

						<div class="space-y-2">
							<Label for="banner-priority">Priority</Label>
							<Select.Root
								type="single"
								value={value.priority}
								onValueChange={(v) => updateField('priority', v as BannerPriority)}
							>
								<Select.Trigger class="w-full" disabled={disabled}>
									<div class="flex items-center gap-2">
										<Flag class="h-3.5 w-3.5" />
										<span>{value.priority}</span>
									</div>
								</Select.Trigger>
								<Select.Content>
									<Select.Item value="low">
										<div class="flex items-center gap-2">
											<span>Low</span>
											<Badge variant="secondary" class="ml-auto">low</Badge>
										</div>
									</Select.Item>
									<Select.Item value="medium">
										<div class="flex items-center gap-2">
											<span>Medium</span>
											<Badge variant="secondary" class="ml-auto">medium</Badge>
										</div>
									</Select.Item>
									<Select.Item value="high">
										<div class="flex items-center gap-2">
											<span>High</span>
											<Badge variant="secondary" class="ml-auto">high</Badge>
										</div>
									</Select.Item>
									<Select.Item value="urgent">
										<div class="flex items-center gap-2">
											<span>Urgent</span>
											<Badge variant="secondary" class="ml-auto">urgent</Badge>
										</div>
									</Select.Item>
								</Select.Content>
							</Select.Root>
						</div>
					</div>

					<div class="space-y-2">
						<Label for="banner-content">Content *</Label>
						<Textarea
							id="banner-content"
							value={value.content}
							oninput={(e) => updateField('content', e.currentTarget.value)}
							disabled={disabled}
							placeholder="Banner content (supports HTML)"
							rows={4}
						/>
						<p class="text-xs text-muted-foreground">HTML and markdown supported</p>
					</div>
				</Card.Content>
			</Card.Root>

			<!-- Call-to-Action -->
			<Card.Root>
				<Card.Header class="py-3">
					<div class="flex items-center gap-2">
						<Link2 class="h-4 w-4 text-muted-foreground" />
						<Card.Title class="text-sm font-medium">Call-to-Action</Card.Title>
					</div>
				</Card.Header>
				<Card.Content class="space-y-4">
					<div class="grid gap-4 md:grid-cols-2">
						<div class="space-y-2">
							<Label for="banner-cta-text">CTA Text</Label>
							<Input
								id="banner-cta-text"
								value={value.ctaText ?? ''}
								oninput={(e) => updateField('ctaText', e.currentTarget.value)}
								disabled={disabled}
								placeholder="Learn more"
							/>
						</div>

						<div class="space-y-2">
							<Label for="banner-cta-url">CTA URL</Label>
							<Input
								id="banner-cta-url"
								value={value.ctaUrl ?? ''}
								oninput={(e) => updateField('ctaUrl', e.currentTarget.value)}
								disabled={disabled}
								placeholder="https://..."
							/>
						</div>
					</div>

					<div class="flex items-center justify-between">
						<div>
							<Label for="banner-cta-new-tab" class="text-sm">Open in New Tab</Label>
							<p class="text-xs text-muted-foreground">Open CTA link in a new browser tab</p>
						</div>
						<Switch
							id="banner-cta-new-tab"
							checked={value.ctaNewTab ?? false}
							onCheckedChange={(checked) => updateField('ctaNewTab', checked)}
							disabled={disabled}
						/>
					</div>

					<div class="space-y-2">
						<Label for="banner-secondary-action">Secondary Action Text</Label>
						<Input
							id="banner-secondary-action"
							value={value.secondaryActionText ?? ''}
							oninput={(e) => updateField('secondaryActionText', e.currentTarget.value)}
							disabled={disabled}
							placeholder="Dismiss"
						/>
						<p class="text-xs text-muted-foreground">Optional secondary action button text</p>
					</div>
				</Card.Content>
			</Card.Root>

			<!-- Targeting -->
			<Card.Root>
				<Card.Header class="py-3">
					<Card.Title class="text-sm font-medium">Targeting & Display</Card.Title>
				</Card.Header>
				<Card.Content class="space-y-4">
					<div class="space-y-2">
						<Label for="banner-pages">Target Pages</Label>
						<Input
							id="banner-pages"
							value={(value.targetPages ?? []).join(', ')}
							oninput={(e) => {
								const pages = e.currentTarget.value
									.split(',')
									.map((p) => p.trim())
									.filter(Boolean);
								updateField('targetPages', pages);
							}}
							disabled={disabled}
							placeholder="/dashboard, /settings (comma-separated)"
						/>
						<p class="text-xs text-muted-foreground">Leave empty for all pages</p>
					</div>

					<div class="space-y-2">
						<Label for="banner-roles">Target Roles</Label>
						<Input
							id="banner-roles"
							value={(value.targetRoles ?? []).join(', ')}
							oninput={(e) => {
								const roles = e.currentTarget.value
									.split(',')
									.map((r) => r.trim())
									.filter(Boolean);
								updateField('targetRoles', roles);
							}}
							disabled={disabled}
							placeholder="ADMIN, EDITOR (comma-separated)"
						/>
						<p class="text-xs text-muted-foreground">Leave empty for all roles</p>
					</div>

					<div class="space-y-2">
						<Label for="banner-frequency">Display Frequency</Label>
						<Select.Root
							type="single"
							value={value.displayFrequency}
							onValueChange={(v) => updateField('displayFrequency', v as 'once' | 'daily' | 'always')}
						>
							<Select.Trigger class="w-full" disabled={disabled}>
								{value.displayFrequency}
							</Select.Trigger>
							<Select.Content>
								<Select.Item value="once">Once (per user)</Select.Item>
								<Select.Item value="daily">Daily</Select.Item>
								<Select.Item value="always">Always</Select.Item>
							</Select.Content>
						</Select.Root>
					</div>
				</Card.Content>
			</Card.Root>
		</div>

		<!-- Right Column - Appearance & Schedule -->
		<div class="space-y-4">
			<!-- Appearance -->
			<Card.Root>
				<Card.Header class="py-3">
					<div class="flex items-center gap-2">
						<Palette class="h-4 w-4 text-muted-foreground" />
						<Card.Title class="text-sm font-medium">Appearance</Card.Title>
					</div>
				</Card.Header>
				<Card.Content class="space-y-4">
					<div class="space-y-2">
						<Label for="banner-variant">Variant</Label>
						<Select.Root
							type="single"
							value={value.variant}
							onValueChange={(v) => updateField('variant', v as BannerVariant)}
						>
							<Select.Trigger class="w-full" disabled={disabled}>
								<div class="flex items-center gap-2">
									<span
										class={cn('w-3 h-3 rounded-full', {
											'bg-gray-400': value.variant === 'default',
											'bg-blue-500': value.variant === 'info',
											'bg-green-500': value.variant === 'success',
											'bg-yellow-500': value.variant === 'warning',
											'bg-red-500': value.variant === 'error'
										})}
									></span>
									<span>{value.variant}</span>
								</div>
							</Select.Trigger>
							<Select.Content>
								<Select.Item value="default">
									<div class="flex items-center gap-2">
										<span class="w-3 h-3 rounded-full bg-gray-400"></span>
										Default
									</div>
								</Select.Item>
								<Select.Item value="info">
									<div class="flex items-center gap-2">
										<span class="w-3 h-3 rounded-full bg-blue-500"></span>
										Info
									</div>
								</Select.Item>
								<Select.Item value="success">
									<div class="flex items-center gap-2">
										<span class="w-3 h-3 rounded-full bg-green-500"></span>
										Success
									</div>
								</Select.Item>
								<Select.Item value="warning">
									<div class="flex items-center gap-2">
										<span class="w-3 h-3 rounded-full bg-yellow-500"></span>
										Warning
									</div>
								</Select.Item>
								<Select.Item value="error">
									<div class="flex items-center gap-2">
										<span class="w-3 h-3 rounded-full bg-red-500"></span>
										Error
									</div>
								</Select.Item>
							</Select.Content>
						</Select.Root>
					</div>

					<div class="flex items-center justify-between">
						<div>
							<Label for="banner-dismissible" class="text-sm">Dismissible</Label>
							<p class="text-xs text-muted-foreground">Allow users to dismiss the banner</p>
						</div>
						<Switch
							id="banner-dismissible"
							checked={value.dismissible}
							onCheckedChange={(checked) => updateField('dismissible', checked)}
							disabled={disabled}
						/>
					</div>

					{#if value.dismissible}
						<div class="space-y-2">
							<Label for="banner-dismiss-key">Dismiss Key</Label>
							<Input
								id="banner-dismiss-key"
								value={value.dismissKey ?? ''}
								oninput={(e) => updateField('dismissKey', e.currentTarget.value)}
								disabled={disabled}
								placeholder="banner-my-feature-dismissed"
								class="font-mono text-xs"
							/>
							<p class="text-xs text-muted-foreground">localStorage key for dismiss state</p>
						</div>
					{/if}
				</Card.Content>
			</Card.Root>

			<!-- Schedule -->
			<Card.Root>
				<Card.Header class="py-3">
					<div class="flex items-center gap-2">
						<Calendar class="h-4 w-4 text-muted-foreground" />
						<Card.Title class="text-sm font-medium">Schedule</Card.Title>
					</div>
				</Card.Header>
				<Card.Content class="space-y-4">
					<div class="space-y-2">
						<Label for="banner-start-date">Start Date</Label>
						<Input
							id="banner-start-date"
							type="datetime-local"
							value={value.startDate ?? ''}
							oninput={(e) => updateField('startDate', e.currentTarget.value)}
							disabled={disabled}
						/>
						<p class="text-xs text-muted-foreground">Leave empty for immediate</p>
					</div>

					<div class="space-y-2">
						<Label for="banner-end-date">End Date</Label>
						<Input
							id="banner-end-date"
							type="datetime-local"
							value={value.endDate ?? ''}
							oninput={(e) => updateField('endDate', e.currentTarget.value)}
							disabled={disabled}
						/>
						<p class="text-xs text-muted-foreground">Leave empty for no end date</p>
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
						<div
							class={cn(
								'p-4 rounded-lg border-l-4',
								getVariantBorderColor(value.variant),
								getVariantBgColor(value.variant)
							)}
						>
							<div class="flex items-start justify-between gap-4">
								<div class="flex-1">
									<h4 class="font-semibold mb-1">{value.title || 'Banner Title'}</h4>
									<p class="text-sm text-muted-foreground">{value.content || 'Banner content...'}</p>
									{#if value.ctaText}
										<div class="mt-3 flex gap-2">
											<Button size="sm" class="h-8">
												{value.ctaText}
											</Button>
											{#if value.secondaryActionText}
												<Button size="sm" variant="ghost" class="h-8">
													{value.secondaryActionText}
												</Button>
											{/if}
										</div>
									{/if}
								</div>
								{#if value.dismissible}
									<button class="text-muted-foreground hover:text-foreground" aria-label="Dismiss">
										×
									</button>
								{/if}
							</div>
							<div class="flex gap-2 mt-3 pt-3 border-t">
								<Badge variant="secondary" class={cn('text-xs', getPriorityColor(value.priority))}>
									{value.priority}
								</Badge>
								<Badge variant="secondary" class={cn('text-xs', getVariantColor(value.variant))}>
									{value.variant}
								</Badge>
								<Badge variant="outline" class="text-xs">
									{value.displayFrequency}
								</Badge>
							</div>
						</div>
					</Card.Content>
				</Card.Root>
			{/if}
		</div>
	</div>
</div>
