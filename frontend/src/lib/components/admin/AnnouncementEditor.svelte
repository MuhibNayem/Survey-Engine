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
		Users,
		Bell,
		MessageSquare,
		Star,
		Wrench,
		Gift,
		Eye,
		Clock
	} from 'lucide-svelte';
	import { cn } from '$lib/utils';
	import { toast } from 'svelte-sonner';
	import type {
		AnnouncementMetadata,
		AnnouncementType,
		AnnouncementTargeting
	} from './types';

	interface Props {
		/** Announcement metadata to edit */
		value: AnnouncementMetadata;
		/** Called when announcement data changes */
		onChange?: (value: AnnouncementMetadata) => void;
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

	// Announcement type options
	const announcementTypes: {
		value: AnnouncementType;
		label: string;
		icon: typeof Megaphone;
		color: string;
	}[] = [
		{ value: 'info', label: 'Information', icon: Bell, color: 'bg-blue-500' },
		{ value: 'feature', label: 'New Feature', icon: Star, color: 'bg-green-500' },
		{ value: 'update', label: 'Update', icon: MessageSquare, color: 'bg-purple-500' },
		{ value: 'maintenance', label: 'Maintenance', icon: Wrench, color: 'bg-orange-500' },
		{ value: 'promotion', label: 'Promotion', icon: Gift, color: 'bg-pink-500' }
	];

	// Display style options
	const displayStyles: { value: 'modal' | 'banner' | 'toast' | 'inline'; label: string }[] = [
		{ value: 'modal', label: 'Modal Dialog' },
		{ value: 'banner', label: 'Top Banner' },
		{ value: 'toast', label: 'Toast Notification' },
		{ value: 'inline', label: 'Inline Content' }
	];

	// Validate announcement
	function validate(): boolean {
		errors = [];

		if (!value.title || value.title.trim() === '') {
			errors.push('Title is required');
		}

		if (!value.content || value.content.trim() === '') {
			errors.push('Content is required');
		}

		if (value.scheduledStart && value.scheduledEnd) {
			const start = new Date(value.scheduledStart);
			const end = new Date(value.scheduledEnd);
			if (start > end) {
				errors.push('Start date must be before end date');
			}
		}

		if (value.ctaText && !value.ctaUrl) {
			errors.push('CTA URL is required when CTA text is provided');
		}

		if (value.priority < 1 || value.priority > 10) {
			errors.push('Priority must be between 1 and 10');
		}

		return errors.length === 0;
	}

	// Run validation on value change
	$effect(() => {
		validate();
	});

	/**
	 * Update a field in the announcement metadata
	 */
	function updateField<K extends keyof AnnouncementMetadata>(
		field: K,
		newValue: AnnouncementMetadata[K]
	): void {
		const newValueObj: AnnouncementMetadata = {
			...value,
			[field]: newValue
		};
		onChange?.(newValueObj);
	}

	/**
	 * Update targeting field
	 */
	function updateTargeting<K extends keyof AnnouncementTargeting>(
		field: K,
		newValue: AnnouncementTargeting[K]
	): void {
		const newValueObj: AnnouncementMetadata = {
			...value,
			targeting: {
				...value.targeting,
				[field]: newValue
			}
		};
		onChange?.(newValueObj);
	}

	/**
	 * Get type icon
	 */
	function getTypeIcon(type: AnnouncementType): typeof Megaphone {
		return announcementTypes.find((t) => t.value === type)?.icon || Megaphone;
	}

	/**
	 * Get type color
	 */
	function getTypeColor(type: AnnouncementType): string {
		return announcementTypes.find((t) => t.value === type)?.color || 'bg-gray-500';
	}

	/**
	 * Get display style preview
	 */
	function getDisplayStylePreview(): string {
		switch (value.displayStyle) {
			case 'modal':
				return 'Centered overlay with backdrop';
			case 'banner':
				return 'Full-width bar at top of page';
			case 'toast':
				return 'Small popup in corner';
			case 'inline':
				return 'Embedded in page content';
			default:
				return '';
		}
	}
</script>

<div class={cn('flex flex-col gap-4', className)}>
	<!-- Header -->
	<div class="flex items-center justify-between">
		<div class="flex items-center gap-2">
			<Megaphone class="h-5 w-5 text-primary" />
			<h3 class="text-lg font-semibold">Announcement Configuration</h3>
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
							<Label for="announcement-title">Title *</Label>
							<Input
								id="announcement-title"
								value={value.title}
								oninput={(e) => updateField('title', e.currentTarget.value)}
								disabled={disabled}
								placeholder="Announcement title"
							/>
						</div>

						<div class="space-y-2">
							<Label for="announcement-type">Type</Label>
							<Select.Root
								type="single"
								value={value.announcementType}
								onValueChange={(v) => updateField('announcementType', v as AnnouncementType)}
							>
								<Select.Trigger class="w-full" disabled={disabled}>
									<div class="flex items-center gap-2">
										{#if true}
											{@const TypeIcon = getTypeIcon(value.announcementType)}
											<TypeIcon class="h-4 w-4" />
										{/if}
										<span>
											{announcementTypes.find((t) => t.value === value.announcementType)?.label ||
												value.announcementType}
										</span>
									</div>
								</Select.Trigger>
								<Select.Content>
									{#each announcementTypes as type}
										<Select.Item value={type.value}>
											<div class="flex items-center gap-2">
												{#if true}
													{@const Icon = type.icon}
													<Icon class="h-4 w-4" />
												{/if}
												<span>{type.label}</span>
											</div>
										</Select.Item>
									{/each}
								</Select.Content>
							</Select.Root>
						</div>
					</div>

					<div class="space-y-2">
						<Label for="announcement-content">Content *</Label>
						<Textarea
							id="announcement-content"
							value={value.content}
							oninput={(e) => updateField('content', e.currentTarget.value)}
							disabled={disabled}
							placeholder="Announcement content (supports HTML/markdown)"
							rows={5}
						/>
						<p class="text-xs text-muted-foreground">HTML and markdown supported</p>
					</div>

					<div class="space-y-2">
						<Label for="announcement-icon">Icon (Optional)</Label>
						<Input
							id="announcement-icon"
							value={value.icon ?? ''}
							oninput={(e) => updateField('icon', e.currentTarget.value)}
							disabled={disabled}
							placeholder="lucide-icon-name"
							class="font-mono text-xs"
						/>
						<p class="text-xs text-muted-foreground">Lucide icon name (e.g., "bell", "star")</p>
					</div>

					<div class="space-y-2">
						<Label for="announcement-image">Image URL (Optional)</Label>
						<Input
							id="announcement-image"
							value={value.imageUrl ?? ''}
							oninput={(e) => updateField('imageUrl', e.currentTarget.value)}
							disabled={disabled}
							placeholder="https://..."
						/>
						<p class="text-xs text-muted-foreground">Optional hero image for the announcement</p>
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
							<Label for="announcement-cta-text">CTA Text</Label>
							<Input
								id="announcement-cta-text"
								value={value.ctaText ?? ''}
								oninput={(e) => updateField('ctaText', e.currentTarget.value)}
								disabled={disabled}
								placeholder="Learn more"
							/>
						</div>

						<div class="space-y-2">
							<Label for="announcement-cta-url">CTA URL</Label>
							<Input
								id="announcement-cta-url"
								value={value.ctaUrl ?? ''}
								oninput={(e) => updateField('ctaUrl', e.currentTarget.value)}
								disabled={disabled}
								placeholder="https://..."
							/>
						</div>
					</div>

					<div class="space-y-2">
						<Label for="announcement-secondary-action">Secondary Action Text</Label>
						<Input
							id="announcement-secondary-action"
							value={value.secondaryActionText ?? ''}
							oninput={(e) => updateField('secondaryActionText', e.currentTarget.value)}
							disabled={disabled}
							placeholder="Maybe later"
						/>
					</div>
				</Card.Content>
			</Card.Root>

			<!-- Targeting -->
			<Card.Root>
				<Card.Header class="py-3">
					<div class="flex items-center gap-2">
						<Users class="h-4 w-4 text-muted-foreground" />
						<Card.Title class="text-sm font-medium">Targeting</Card.Title>
					</div>
				</Card.Header>
				<Card.Content class="space-y-4">
					<div class="space-y-2">
						<Label for="announcement-roles">Target Roles</Label>
						<Input
							id="announcement-roles"
							value={(value.targeting.roles ?? []).join(', ')}
							oninput={(e) => {
								const roles = e.currentTarget.value
									.split(',')
									.map((r) => r.trim())
									.filter(Boolean);
								updateTargeting('roles', roles);
							}}
							disabled={disabled}
							placeholder="ADMIN, EDITOR, VIEWER (comma-separated)"
						/>
						<p class="text-xs text-muted-foreground">Leave empty for all roles</p>
					</div>

					<div class="space-y-2">
						<Label for="announcement-plans">Target Plans</Label>
						<Input
							id="announcement-plans"
							value={(value.targeting.plans ?? []).join(', ')}
							oninput={(e) => {
								const plans = e.currentTarget.value
									.split(',')
									.map((p) => p.trim())
									.filter(Boolean);
								updateTargeting('plans', plans);
							}}
							disabled={disabled}
							placeholder="BASIC, PRO, ENTERPRISE (comma-separated)"
						/>
						<p class="text-xs text-muted-foreground">Leave empty for all plans</p>
					</div>

					<div class="space-y-2">
						<Label for="announcement-pages">Target Pages</Label>
						<Input
							id="announcement-pages"
							value={(value.targeting.pages ?? []).join(', ')}
							oninput={(e) => {
								const pages = e.currentTarget.value
									.split(',')
									.map((p) => p.trim())
									.filter(Boolean);
								updateTargeting('pages', pages);
							}}
							disabled={disabled}
							placeholder="/dashboard, /settings (comma-separated)"
						/>
						<p class="text-xs text-muted-foreground">Leave empty for all pages</p>
					</div>

					<div class="space-y-2">
						<Label for="announcement-tenant-ids">Target Tenant IDs</Label>
						<Input
							id="announcement-tenant-ids"
							value={(value.targeting.tenantIds ?? []).join(', ')}
							oninput={(e) => {
								const ids = e.currentTarget.value
									.split(',')
									.map((id) => id.trim())
									.filter(Boolean);
								updateTargeting('tenantIds', ids);
							}}
							disabled={disabled}
							placeholder="tenant-1, tenant-2 (comma-separated)"
							class="font-mono text-xs"
						/>
						<p class="text-xs text-muted-foreground">Leave empty for all tenants</p>
					</div>
				</Card.Content>
			</Card.Root>
		</div>

		<!-- Right Column - Display & Schedule -->
		<div class="space-y-4">
			<!-- Display Settings -->
			<Card.Root>
				<Card.Header class="py-3">
					<Card.Title class="text-sm font-medium">Display Settings</Card.Title>
				</Card.Header>
				<Card.Content class="space-y-4">
					<div class="space-y-2">
						<Label for="announcement-display-style">Display Style</Label>
						<Select.Root
							type="single"
							value={value.displayStyle}
							onValueChange={(v) =>
								updateField('displayStyle', v as 'modal' | 'banner' | 'toast' | 'inline')
							}
						>
							<Select.Trigger class="w-full" disabled={disabled}>
								{displayStyles.find((s) => s.value === value.displayStyle)?.label || value.displayStyle}
							</Select.Trigger>
							<Select.Content>
								{#each displayStyles as style}
									<Select.Item value={style.value}>{style.label}</Select.Item>
								{/each}
							</Select.Content>
						</Select.Root>
						<p class="text-xs text-muted-foreground">{getDisplayStylePreview()}</p>
					</div>

					<div class="space-y-2">
						<Label for="announcement-priority">Priority (1-10)</Label>
						<Input
							id="announcement-priority"
							type="number"
							min="1"
							max="10"
							value={value.priority}
							oninput={(e) => updateField('priority', parseInt(e.currentTarget.value) || 1)}
							disabled={disabled}
						/>
						<p class="text-xs text-muted-foreground">Higher priority announcements show first</p>
					</div>

					<div class="flex items-center justify-between">
						<div>
							<Label for="announcement-dismissible" class="text-sm">Dismissible</Label>
							<p class="text-xs text-muted-foreground">Allow users to dismiss the announcement</p>
						</div>
						<Switch
							id="announcement-dismissible"
							checked={value.dismissible}
							onCheckedChange={(checked) => updateField('dismissible', checked)}
							disabled={disabled}
						/>
					</div>

					{#if value.dismissible}
						<div class="space-y-2">
							<Label for="announcement-dismiss-persistence">Dismiss Persistence</Label>
							<Select.Root
								type="single"
								value={value.dismissPersistence}
								onValueChange={(v) =>
									updateField('dismissPersistence', v as 'session' | 'local' | 'never')
								}
							>
								<Select.Trigger class="w-full" disabled={disabled}>
									{value.dismissPersistence}
								</Select.Trigger>
								<Select.Content>
									<Select.Item value="session">Session (until browser closes)</Select.Item>
									<Select.Item value="local">Local Storage (permanent)</Select.Item>
									<Select.Item value="never">Never Persist</Select.Item>
								</Select.Content>
							</Select.Root>
						</div>
					{/if}

					<div class="flex items-center justify-between">
						<div>
							<Label for="announcement-first-login" class="text-sm">First Login Only</Label>
							<p class="text-xs text-muted-foreground">Show only on user's first login</p>
						</div>
						<Switch
							id="announcement-first-login"
							checked={value.firstLoginOnly}
							onCheckedChange={(checked) => updateField('firstLoginOnly', checked)}
							disabled={disabled}
						/>
					</div>

					<div class="flex items-center justify-between">
						<div>
							<Label for="announcement-require-ack" class="text-sm">Require Acknowledgment</Label>
							<p class="text-xs text-muted-foreground">User must explicitly acknowledge</p>
						</div>
						<Switch
							id="announcement-require-ack"
							checked={value.requireAcknowledgment}
							onCheckedChange={(checked) => updateField('requireAcknowledgment', checked)}
							disabled={disabled}
						/>
					</div>
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
						<Label for="announcement-start">Scheduled Start</Label>
						<Input
							id="announcement-start"
							type="datetime-local"
							value={value.scheduledStart ?? ''}
							oninput={(e) => updateField('scheduledStart', e.currentTarget.value)}
							disabled={disabled}
						/>
						<p class="text-xs text-muted-foreground">Leave empty for immediate</p>
					</div>

					<div class="space-y-2">
						<Label for="announcement-end">Scheduled End</Label>
						<Input
							id="announcement-end"
							type="datetime-local"
							value={value.scheduledEnd ?? ''}
							oninput={(e) => updateField('scheduledEnd', e.currentTarget.value)}
							disabled={disabled}
						/>
						<p class="text-xs text-muted-foreground">Leave empty for no end date</p>
					</div>

					{#if value.scheduledStart || value.scheduledEnd}
						<div class="p-3 bg-muted/50 rounded-lg">
							<div class="flex items-center gap-2 text-sm">
								<Clock class="h-4 w-4 text-muted-foreground" />
								<span class="text-muted-foreground">
									{value.scheduledStart
										? `Starts: ${new Date(value.scheduledStart).toLocaleString()}`
										: 'Starts immediately'}
								</span>
							</div>
							{#if value.scheduledEnd}
								<div class="flex items-center gap-2 text-sm mt-1">
									<Clock class="h-4 w-4 text-muted-foreground" />
									<span class="text-muted-foreground">
										Ends: {new Date(value.scheduledEnd).toLocaleString()}
									</span>
								</div>
							{/if}
						</div>
					{/if}
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
								'p-4 rounded-lg border',
								value.displayStyle === 'modal' && 'shadow-lg max-w-md mx-auto',
								value.displayStyle === 'banner' && 'w-full',
								value.displayStyle === 'toast' && 'shadow-md max-w-sm',
								value.displayStyle === 'inline' && 'bg-muted/50'
							)}
						>
							<!-- Type Badge -->
							<div class="flex items-center gap-2 mb-3">
								<span class={cn('w-2 h-2 rounded-full', getTypeColor(value.announcementType))}></span>
								<Badge variant="secondary" class="text-xs">
									{announcementTypes.find((t) => t.value === value.announcementType)?.label}
								</Badge>
								<span class="text-xs text-muted-foreground">Priority: {value.priority}</span>
							</div>

							<!-- Content -->
							<h4 class="font-semibold mb-2">{value.title || 'Announcement Title'}</h4>
							<p class="text-sm text-muted-foreground mb-3">{value.content || 'Announcement content...'}</p>

							<!-- Image -->
							{#if value.imageUrl}
								<div class="mb-3 rounded overflow-hidden">
									<div class="w-full h-32 bg-muted flex items-center justify-center text-muted-foreground text-xs">
										Image Preview
									</div>
								</div>
							{/if}

							<!-- Actions -->
							{#if value.ctaText || value.secondaryActionText}
								<div class="flex gap-2 pt-3 border-t">
									{#if value.ctaText}
										<Button size="sm" class="flex-1">
											{value.ctaText}
										</Button>
									{/if}
									{#if value.secondaryActionText}
										<Button size="sm" variant="ghost">
											{value.secondaryActionText}
										</Button>
									{/if}
								</div>
							{/if}

							<!-- Dismiss -->
							{#if value.dismissible}
								<div class="mt-3 pt-3 border-t flex justify-end">
									<Button size="sm" variant="ghost" class="h-7 text-xs">
										{value.dismissPersistence === 'never' ? 'Close' : "Don't show again"}
									</Button>
								</div>
							{/if}
						</div>
					</Card.Content>
				</Card.Root>
			{/if}
		</div>
	</div>
</div>
