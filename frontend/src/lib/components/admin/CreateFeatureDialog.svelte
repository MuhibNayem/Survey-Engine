<script lang="ts">
	import { Button } from '$lib/components/ui/button';
	import * as Dialog from '$lib/components/ui/dialog';
	import { Input } from '$lib/components/ui/input';
	import { Label } from '$lib/components/ui/label';
	import * as Select from '$lib/components/ui/select';
	import { Switch } from '$lib/components/ui/switch';
	import { Textarea } from '$lib/components/ui/textarea';
	import { Badge } from '$lib/components/ui/badge';
	import * as Card from '$lib/components/ui/card';
	import { toast } from 'svelte-sonner';
	import api from '$lib/api';
	import FeatureMetadataEditor from './FeatureMetadataEditor.svelte';
	import type {
		FeatureType,
		FeatureMetadata,
		TourMetadata,
		TooltipMetadata,
		BannerMetadata,
		FeatureFlagMetadata,
		AnnouncementMetadata
	} from './types';

	interface Feature {
		id?: string;
		featureKey?: string;
		name?: string;
		featureType?: string;
		category?: string;
		enabled?: boolean;
		rolloutPercentage?: number;
		minPlan?: string;
		roles?: string[];
		platforms?: string[];
		description?: string;
		metadata?: Record<string, unknown>;
	}

	interface Props {
		feature?: Feature | null;
		onClose: () => void;
		onSave: () => void;
	}

	let { feature, onClose, onSave }: Props = $props();

	// Form state
	let featureKey = $state('');
	let name = $state('');
	let featureType = $state<FeatureType>('TOUR');
	let category = $state('GENERAL');
	let description = $state('');
	let enabled = $state(true);
	let rolloutPercentage = $state(100);
	let minPlan = $state('BASIC');
	let roles = $state<string[]>(['ADMIN', 'EDITOR', 'VIEWER']);
	let platforms = $state<string[]>(['WEB']);
	let isSaving = $state(false);

	// Metadata state
	let metadata = $state<FeatureMetadata | null>(null);
	let metadataDirty = $state(false);

	// Sync form state when feature prop changes
	$effect(() => {
		if (feature) {
			featureKey = feature.featureKey ?? '';
			name = feature.name ?? '';
			featureType = (feature.featureType as FeatureType) ?? 'TOUR';
			category = feature.category ?? 'GENERAL';
			description = feature.description ?? '';
			enabled = feature.enabled ?? true;
			rolloutPercentage = feature.rolloutPercentage ?? 100;
			minPlan = feature.minPlan ?? 'BASIC';
			roles = feature.roles ?? ['ADMIN', 'EDITOR', 'VIEWER'];
			platforms = feature.platforms ?? ['WEB'];
			metadata = (feature.metadata as FeatureMetadata | undefined) ?? null;
		} else {
			// Reset to defaults for create mode
			featureKey = '';
			name = '';
			featureType = 'TOUR';
			category = 'GENERAL';
			description = '';
			enabled = true;
			rolloutPercentage = 100;
			minPlan = 'BASIC';
			roles = ['ADMIN', 'EDITOR', 'VIEWER'];
			platforms = ['WEB'];
			metadata = null;
		}
		metadataDirty = false;
	});

	const featureTypes: FeatureType[] = ['TOUR', 'TOOLTIP', 'BANNER', 'FEATURE_FLAG', 'ANNOUNCEMENT'];
	const categories = [
		'GENERAL',
		'DASHBOARD',
		'SURVEYS',
		'CAMPAIGNS',
		'QUESTIONS',
		'ANALYTICS',
		'RESPONSES',
		'SETTINGS',
		'ADMIN'
	];
	const plans = ['BASIC', 'PRO', 'ENTERPRISE'];
	const availableRoles = ['SUPER_ADMIN', 'ADMIN', 'EDITOR', 'VIEWER'];
	const availablePlatforms = ['WEB', 'MOBILE', 'DESKTOP', 'API'];

	function toggleRole(role: string): void {
		if (roles.includes(role)) {
			roles = roles.filter((r) => r !== role);
		} else {
			roles = [...roles, role];
		}
	}

	function togglePlatform(platform: string): void {
		if (platforms.includes(platform)) {
			platforms = platforms.filter((p) => p !== platform);
		} else {
			platforms = [...platforms, platform];
		}
	}

	/**
	 * Handle metadata change from the editor
	 */
	function handleMetadataChange(newMetadata: FeatureMetadata): void {
		metadata = newMetadata;
		metadataDirty = true;
	}

	/**
	 * Validate the form
	 */
	function validateForm(): boolean {
		if (!featureKey || featureKey.trim() === '') {
			toast.error('Feature key is required');
			return false;
		}

		// Validate feature key format
		if (!/^[a-z0-9._-]+$/.test(featureKey)) {
			toast.error('Feature key must contain only lowercase letters, numbers, dots, underscores, and hyphens');
			return false;
		}

		if (!name || name.trim() === '') {
			toast.error('Feature name is required');
			return false;
		}

		if (!metadata) {
			toast.error('Feature metadata is required');
			return false;
		}

		return true;
	}

	/**
	 * Save the feature
	 */
	async function saveFeature(): Promise<void> {
		if (!validateForm()) {
			return;
		}

		isSaving = true;
		try {
			const payload = {
				featureKey,
				name,
				featureType,
				category,
				description,
				enabled,
				rolloutPercentage,
				minPlan,
				roles,
				platforms,
				metadata: metadata as unknown as Record<string, unknown>
			};

			if (feature?.id) {
				// Update existing
				await api.put(`/admin/features/${feature.featureKey}`, payload);
				toast.success('Feature updated successfully');
			} else {
				// Create new
				await api.post('/admin/features', payload);
				toast.success('Feature created successfully');
			}

			onSave();
			onClose();
		} catch (error: any) {
			const message = error.response?.data?.message || 'Failed to save feature';
			toast.error(message);
			console.error('Failed to save feature:', error);
		} finally {
			isSaving = false;
		}
	}

	/**
	 * Get default metadata for the selected feature type
	 */
	function getDefaultMetadata(type: FeatureType): FeatureMetadata {
		const base = {
			$schema: '1.0.0',
			lastModified: new Date().toISOString(),
			lastModifiedBy: 'system'
		};

		switch (type) {
			case 'TOUR':
				return {
					...base,
					featureType: 'TOUR',
					steps: [
						{
							id: 'step-1',
							title: 'Welcome',
							description: 'This is the first step of your tour.',
							targetSelector: '',
							placement: 'bottom',
							dismissible: true,
							showSkip: false,
							required: true
						}
					],
					config: {
						autoStart: true,
						showProgress: true,
						keyboardNavigation: true,
						scrollToElement: true,
						overlayColor: 'rgba(0, 0, 0, 0.5)',
						clickOverlayCloses: false,
						maxDuration: 0
					}
				} as TourMetadata;

			case 'TOOLTIP':
				return {
					...base,
					featureType: 'TOOLTIP',
					targetSelector: '',
					content: '',
					title: '',
					trigger: 'hover',
					placement: 'top',
					showDelay: 200,
					hideDelay: 100,
					dismissible: true,
					maxWidth: 300,
					customClass: ''
				} as TooltipMetadata;

			case 'BANNER':
				return {
					...base,
					featureType: 'BANNER',
					title: '',
					content: '',
					ctaText: '',
					ctaUrl: '',
					ctaNewTab: false,
					secondaryActionText: '',
					priority: 'medium',
					variant: 'info',
					startDate: '',
					endDate: '',
					dismissible: true,
					dismissKey: '',
					targetPages: [],
					targetRoles: [],
					displayFrequency: 'once'
				} as BannerMetadata;

			case 'FEATURE_FLAG':
				return {
					...base,
					featureType: 'FEATURE_FLAG',
					defaultValue: false,
					ruleGroups: [
						{
							id: 'group-1',
							logicalOperator: 'AND',
							rules: [
								{
									id: 'rule-1',
									attribute: 'user.role',
									operator: 'equals',
									value: 'ADMIN'
								}
							],
							groups: []
						}
					],
					rolloutPercentage: 100,
					rolloutStrategy: 'percentage',
					killSwitch: false,
					forceEnabledSegments: [],
					forceDisabledSegments: [],
					environmentOverrides: {}
				} as FeatureFlagMetadata;

			case 'ANNOUNCEMENT':
				return {
					...base,
					featureType: 'ANNOUNCEMENT',
					title: '',
					content: '',
					announcementType: 'info',
					icon: '',
					imageUrl: '',
					ctaText: '',
					ctaUrl: '',
					secondaryActionText: '',
					scheduledStart: '',
					scheduledEnd: '',
					dismissible: true,
					dismissPersistence: 'session',
					displayStyle: 'modal',
					targeting: {
						roles: [],
						plans: [],
						tenantIds: [],
						userIds: [],
						pages: []
					},
					priority: 5,
					firstLoginOnly: false,
					requireAcknowledgment: false
				} as AnnouncementMetadata;

			default:
				throw new Error(`Unknown feature type: ${type}`);
		}
	}

	/**
	 * Handle feature type change
	 */
	function handleFeatureTypeChange(newType: FeatureType): void {
		// Only reset metadata if changing type and no existing metadata
		if (featureType !== newType && !metadata) {
			metadata = getDefaultMetadata(newType);
			metadataDirty = true;
			toast.info(`Switched to ${newType} editor`);
		}
		featureType = newType;
	}
</script>

<Dialog.Root open={true} onOpenChange={(open) => !open && onClose()}>
	<Dialog.Content class="sm:max-w-5xl max-h-[90vh] overflow-hidden flex flex-col">
		<Dialog.Header class="border-b pb-4">
			<div class="flex items-center justify-between">
				<div>
					<Dialog.Title>{feature?.id ? 'Edit Feature' : 'Create New Feature'}</Dialog.Title>
					<Dialog.Description>
						{feature?.id ? 'Update feature configuration' : 'Define a new feature, tour, or tooltip'}
					</Dialog.Description>
				</div>
				{#if metadataDirty}
					<Badge variant="secondary" class="bg-amber-100 text-amber-800 dark:bg-amber-900 dark:text-amber-300">
						Unsaved metadata changes
					</Badge>
				{/if}
			</div>
		</Dialog.Header>

		<div class="flex-1 overflow-y-auto py-4">
			<div class="space-y-6">
				<!-- Basic Info Section -->
				<Card.Root>
					<Card.Header>
						<Card.Title>Basic Information</Card.Title>
						<Card.Description>Define the core feature properties</Card.Description>
					</Card.Header>
					<Card.Content class="space-y-4">
						<div class="grid grid-cols-2 gap-4">
							<div class="space-y-2">
								<Label for="featureKey">Feature Key *</Label>
								<Input
									id="featureKey"
									placeholder="e.g., tour.dashboard"
									bind:value={featureKey}
									disabled={!!feature?.id}
								/>
								<p class="text-xs text-muted-foreground">Lowercase, dots, underscores, hyphens only</p>
							</div>
							<div class="space-y-2">
								<Label for="name">Name *</Label>
								<Input
									id="name"
									placeholder="e.g., Dashboard Tour"
									bind:value={name}
								/>
							</div>
						</div>

						<div class="grid grid-cols-2 gap-4">
							<div class="space-y-2">
								<Label for="type">Type</Label>
								<Select.Root
									type="single"
									value={featureType}
									onValueChange={(v) => handleFeatureTypeChange(v as FeatureType)}
								>
									<Select.Trigger class="w-full">
										{featureType || 'Select type'}
									</Select.Trigger>
									<Select.Content>
										{#each featureTypes as type}
											<Select.Item value={type}>{type}</Select.Item>
										{/each}
									</Select.Content>
								</Select.Root>
							</div>
							<div class="space-y-2">
								<Label for="category">Category</Label>
								<Select.Root type="single" bind:value={category}>
									<Select.Trigger class="w-full">
										{category || 'Select category'}
									</Select.Trigger>
									<Select.Content>
										{#each categories as cat}
											<Select.Item value={cat}>{cat}</Select.Item>
										{/each}
									</Select.Content>
								</Select.Root>
							</div>
						</div>

						<div class="space-y-2">
							<Label for="description">Description</Label>
							<Textarea
								id="description"
								placeholder="Describe the feature..."
								bind:value={description}
								rows={3}
							/>
						</div>
					</Card.Content>
				</Card.Root>

				<!-- Metadata Editor Section -->
				<Card.Root>
					<Card.Header>
						<div class="flex items-center justify-between">
							<div>
								<Card.Title>Feature Configuration</Card.Title>
								<Card.Description>
									Configure {featureType.toLowerCase()} specific settings
								</Card.Description>
							</div>
							{#if metadataDirty}
								<Badge variant="outline" class="text-amber-600 border-amber-600">
									Unsaved changes
								</Badge>
							{/if}
						</div>
					</Card.Header>
					<Card.Content>
						<FeatureMetadataEditor
							featureType={featureType}
							value={metadata}
							onChange={handleMetadataChange}
							disabled={isSaving}
							showActions={false}
							enableJsonMode={true}
							className="min-h-[500px]"
						/>
					</Card.Content>
				</Card.Root>

				<!-- Settings Section -->
				<Card.Root>
					<Card.Header>
						<Card.Title>Access & Rollout Settings</Card.Title>
						<Card.Description>Control who can access this feature</Card.Description>
					</Card.Header>
					<Card.Content class="space-y-4">
						<div class="grid grid-cols-3 gap-4">
							<div class="space-y-2">
								<Label for="plan">Minimum Plan</Label>
								<Select.Root type="single" bind:value={minPlan}>
									<Select.Trigger class="w-full">
										{minPlan || 'Select plan'}
									</Select.Trigger>
									<Select.Content>
										{#each plans as plan}
											<Select.Item value={plan}>{plan}</Select.Item>
										{/each}
									</Select.Content>
								</Select.Root>
							</div>
							<div class="space-y-2">
								<Label for="rollout">Rollout Percentage: {rolloutPercentage}%</Label>
								<Input
									id="rollout"
									type="number"
									min="0"
									max="100"
									bind:value={rolloutPercentage}
								/>
							</div>
							<div class="flex items-end pb-2">
								<div class="flex items-center gap-2">
									<Switch id="enabled" bind:checked={enabled} />
									<Label for="enabled">Enabled</Label>
								</div>
							</div>
						</div>

						<!-- Roles -->
						<div class="space-y-2">
							<Label>Allowed Roles</Label>
							<div class="flex flex-wrap gap-2">
								{#each availableRoles as role}
									<Button
										type="button"
										variant={roles.includes(role) ? 'default' : 'outline'}
										size="sm"
										onclick={() => toggleRole(role)}
									>
										{role}
									</Button>
								{/each}
							</div>
						</div>

						<!-- Platforms -->
						<div class="space-y-2">
							<Label>Platforms</Label>
							<div class="flex flex-wrap gap-2">
								{#each availablePlatforms as platform}
									<Button
										type="button"
										variant={platforms.includes(platform) ? 'default' : 'outline'}
										size="sm"
										onclick={() => togglePlatform(platform)}
									>
										{platform}
									</Button>
								{/each}
							</div>
						</div>
					</Card.Content>
				</Card.Root>
			</div>
		</div>

		<Dialog.Footer class="border-t pt-4">
			<div class="flex items-center justify-between">
				<div class="text-sm text-muted-foreground">
					{#if feature?.id}
						Created: {new Date().toLocaleDateString()}
					{:else}
						Press Ctrl+S to save
					{/if}
				</div>
				<div class="flex items-center gap-2">
					<Button variant="outline" onclick={onClose} disabled={isSaving}>
						Cancel
					</Button>
					<Button onclick={saveFeature} disabled={isSaving}>
						{#if isSaving}
							Saving...
						{:else}
							{feature?.id ? 'Update' : 'Create'} Feature
						{/if}
					</Button>
				</div>
			</div>
		</Dialog.Footer>
	</Dialog.Content>
</Dialog.Root>
