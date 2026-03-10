<script lang="ts">
	import { Button } from '$lib/components/ui/button';
	import * as Tabs from '$lib/components/ui/tabs';
	import { Badge } from '$lib/components/ui/badge';
	import { Alert, AlertDescription, AlertTitle } from '$lib/components/ui/alert';
	import * as Card from '$lib/components/ui/card';
	import {
		LayoutTemplate,
		MousePointer2,
		Megaphone,
		Flag,
		Bell,
		Code2,
		Save,
		X,
		RotateCcw,
		History,
		AlertCircle,
		CheckCircle2,
		Clock
	} from 'lucide-svelte';
	import { cn } from '$lib/utils';
	import { toast } from 'svelte-sonner';
	import TourStepEditor from './TourStepEditor.svelte';
	import TooltipEditor from './TooltipEditor.svelte';
	import BannerEditor from './BannerEditor.svelte';
	import FeatureFlagEditor from './FeatureFlagEditor.svelte';
	import AnnouncementEditor from './AnnouncementEditor.svelte';
	import JsonEditor from './JsonEditor.svelte';
	import type {
		FeatureMetadata,
		FeatureType,
		TourMetadata,
		TooltipMetadata,
		BannerMetadata,
		FeatureFlagMetadata,
		AnnouncementMetadata,
		EditorState,
		ValidationError
	} from './types';

	interface Props {
		/** Feature type */
		featureType: FeatureType;
		/** Initial metadata value */
		value: FeatureMetadata | null;
		/** Called when metadata changes */
		onChange?: (value: FeatureMetadata) => void;
		/** Called when save is requested */
		onSave?: (value: FeatureMetadata) => Promise<void>;
		/** Called when cancel is requested */
		onCancel?: () => void;
		/** Whether editor is disabled */
		disabled?: boolean;
		/** Whether to show action buttons */
		showActions?: boolean;
		/** Custom class names */
		className?: string;
		/** Whether to enable JSON mode toggle */
		enableJsonMode?: boolean;
	}

	let {
		featureType,
		value,
		onChange,
		onSave,
		onCancel,
		disabled = false,
		showActions = true,
		className,
		enableJsonMode = true
	}: Props = $props();

	// Current active tab
	let activeTab = $state<'editor' | 'json'>('editor');
	// Editor state for undo/redo
	let editorState = $state<EditorState<FeatureMetadata>>({
		original: null,
		current: getDefaultValue(featureType),
		isDirty: false,
		isSaving: false,
		errors: [],
		readOnly: false,
		history: [],
		historyIndex: -1
	});
	// Last save time
	let lastSaveTime = $state<Date | null>(null);
	// Auto-save timeout
	let autoSaveTimeout: ReturnType<typeof setTimeout> | null = null;

	// Initialize editor state when value changes
	$effect(() => {
		if (value) {
			editorState.original = value;
			editorState.current = value;
			editorState.history = [value];
			editorState.historyIndex = 0;
			editorState.isDirty = false;
		} else {
			const defaultValue = getDefaultValue(featureType);
			editorState.original = null;
			editorState.current = defaultValue;
			editorState.history = [defaultValue];
			editorState.historyIndex = 0;
			editorState.isDirty = false;
		}
	});

	// Track dirty state
	function checkDirty(): void {
		editorState.isDirty = JSON.stringify(editorState.current) !== JSON.stringify(editorState.original);
	}

	// Get default metadata for feature type
	function getDefaultValue(type: FeatureType): FeatureMetadata {
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

	// Handle metadata change from child editors
	function handleMetadataChange(newMetadata: FeatureMetadata): void {
		editorState.current = newMetadata;
		checkDirty();

		// Add to history for undo
		const newHistory = editorState.history.slice(0, editorState.historyIndex + 1);
		newHistory.push(newMetadata);
		editorState.history = newHistory.slice(-50); // Keep last 50 states
		editorState.historyIndex = editorState.history.length - 1;

		// Update last modified
		editorState.current.lastModified = new Date().toISOString();

		// Notify parent
		onChange?.(editorState.current);

		// Auto-save after delay
		if (autoSaveTimeout) {
			clearTimeout(autoSaveTimeout);
		}
		autoSaveTimeout = setTimeout(() => {
			if (editorState.isDirty) {
				handleSave();
			}
		}, 5000);
	}

	// Handle save
	async function handleSave(): Promise<void> {
		if (!editorState.isDirty) {
			toast.info('No changes to save');
			return;
		}

		editorState.isSaving = true;
		try {
			await onSave?.(editorState.current);
			editorState.original = editorState.current;
			editorState.isDirty = false;
			lastSaveTime = new Date();
			toast.success('Changes saved');
		} catch (error) {
			toast.error('Failed to save changes');
			console.error('Failed to save:', error);
		} finally {
			editorState.isSaving = false;
		}
	}

	// Handle cancel
	function handleCancel(): void {
		if (editorState.isDirty) {
			// Confirm discard
			if (!confirm('You have unsaved changes. Are you sure you want to discard them?')) {
				return;
			}
		}
		onCancel?.();
	}

	// Handle undo
	function handleUndo(): void {
		if (editorState.historyIndex > 0) {
			editorState.historyIndex--;
			editorState.current = editorState.history[editorState.historyIndex];
			checkDirty();
			onChange?.(editorState.current);
			toast.info('Undone');
		}
	}

	// Handle redo
	function handleRedo(): void {
		if (editorState.historyIndex < editorState.history.length - 1) {
			editorState.historyIndex++;
			editorState.current = editorState.history[editorState.historyIndex];
			checkDirty();
			onChange?.(editorState.current);
			toast.info('Redone');
		}
	}

	// Handle reset
	function handleReset(): void {
		if (editorState.original) {
			editorState.current = editorState.original;
			editorState.history = [editorState.original];
			editorState.historyIndex = 0;
			checkDirty();
			onChange?.(editorState.current);
			toast.info('Reset to original');
		}
	}

	// Get feature type icon
	function getFeatureTypeIcon(type: FeatureType): typeof LayoutTemplate {
		switch (type) {
			case 'TOUR':
				return LayoutTemplate;
			case 'TOOLTIP':
				return MousePointer2;
			case 'BANNER':
				return Megaphone;
			case 'FEATURE_FLAG':
				return Flag;
			case 'ANNOUNCEMENT':
				return Bell;
			default:
				return LayoutTemplate;
		}
	}

	// Get feature type label
	function getFeatureTypeLabel(type: FeatureType): string {
		switch (type) {
			case 'TOUR':
				return 'Tour';
			case 'TOOLTIP':
				return 'Tooltip';
			case 'BANNER':
				return 'Banner';
			case 'FEATURE_FLAG':
				return 'Feature Flag';
			case 'ANNOUNCEMENT':
				return 'Announcement';
			default:
				return type;
		}
	}

	// Keyboard shortcuts - attach to window
	$effect(() => {
		function handleKeydown(event: KeyboardEvent): void {
			if ((event.ctrlKey || event.metaKey) && event.key === 's') {
				event.preventDefault();
				handleSave();
			}
			if ((event.ctrlKey || event.metaKey) && event.key === 'z') {
				event.preventDefault();
				if (event.shiftKey) {
					handleRedo();
				} else {
					handleUndo();
				}
			}
			if (event.key === 'Escape') {
				handleCancel();
			}
		}

		window.addEventListener('keydown', handleKeydown);
		return () => window.removeEventListener('keydown', handleKeydown);
	});

	// Format last save time
	function formatLastSaveTime(): string {
		if (!lastSaveTime) return 'Never';
		const now = new Date();
		const diff = now.getTime() - lastSaveTime.getTime();
		const minutes = Math.floor(diff / 60000);
		if (minutes < 1) return 'Just now';
		if (minutes === 1) return '1 minute ago';
		if (minutes < 60) return `${minutes} minutes ago`;
		const hours = Math.floor(minutes / 60);
		if (hours === 1) return '1 hour ago';
		return `${hours} hours ago`;
	}
</script>

<div class={cn('flex flex-col h-full gap-4', className)}>
	<!-- Header -->
	<div class="flex items-center justify-between">
		<div class="flex items-center gap-3">
			{#if true}
				{@const TypeIcon = getFeatureTypeIcon(featureType)}
				<TypeIcon class="h-6 w-6 text-primary" />
			{/if}
			<div>
				<h2 class="text-lg font-semibold">{getFeatureTypeLabel(featureType)} Editor</h2>
				<div class="flex items-center gap-2 text-xs text-muted-foreground">
					{#if editorState.isDirty}
						<span class="flex items-center gap-1 text-amber-600">
							<AlertCircle class="h-3 w-3" />
							Unsaved changes
						</span>
					{:else}
						<span class="flex items-center gap-1 text-green-600">
							<CheckCircle2 class="h-3 w-3" />
							Saved
						</span>
					{/if}
					{#if lastSaveTime}
						<span class="flex items-center gap-1">
							<Clock class="h-3 w-3" />
							{formatLastSaveTime()}
						</span>
					{/if}
				</div>
			</div>
		</div>

		<div class="flex items-center gap-2">
			<!-- Undo/Redo -->
			<Button
				variant="ghost"
				size="icon"
				onclick={handleUndo}
				disabled={disabled || editorState.historyIndex <= 0}
				aria-label="Undo"
				title="Undo (Ctrl+Z)"
			>
				<RotateCcw class="h-4 w-4" />
			</Button>
			<Button
				variant="ghost"
				size="icon"
				onclick={handleRedo}
				disabled={disabled || editorState.historyIndex >= editorState.history.length - 1}
				aria-label="Redo"
				title="Redo (Ctrl+Shift+Z)"
			>
				<History class="h-4 w-4" />
			</Button>

			<!-- JSON Mode Toggle -->
			{#if enableJsonMode}
				<Button
					variant={activeTab === 'json' ? 'default' : 'outline'}
					size="sm"
					onclick={() => (activeTab = 'json')}
					disabled={disabled}
				>
					<Code2 class="h-4 w-4 mr-1" />
					JSON
				</Button>
			{/if}

			<!-- Action Buttons -->
			{#if showActions}
				<div class="flex items-center gap-2 ml-4 pl-4 border-l">
					<Button variant="outline" onclick={handleCancel} disabled={disabled || editorState.isSaving}>
						<X class="h-4 w-4 mr-1" />
						Cancel
					</Button>
					<Button
						variant="default"
						onclick={handleSave}
						disabled={disabled || editorState.isSaving || !editorState.isDirty}
					>
						{#if editorState.isSaving}
							<Clock class="h-4 w-4 mr-1 animate-spin" />
							Saving...
						{:else}
							<Save class="h-4 w-4 mr-1" />
							Save
						{/if}
					</Button>
				</div>
			{/if}
		</div>
	</div>

	<!-- Main Content -->
	{#if enableJsonMode}
		<Tabs.Root value={activeTab} onValueChange={(v) => (activeTab = v as 'editor' | 'json')} class="flex-1 flex flex-col min-h-0">
			<Tabs.List class="grid w-full grid-cols-2 mb-4">
				<Tabs.Trigger value="editor" class={disabled ? 'opacity-50 pointer-events-none' : ''}>
					<div class="flex items-center gap-2">
						{#if true}
							{@const TypeIcon = getFeatureTypeIcon(featureType)}
							<TypeIcon class="h-4 w-4" />
						{/if}
						Editor
					</div>
				</Tabs.Trigger>
				<Tabs.Trigger value="json" class={disabled ? 'opacity-50 pointer-events-none' : ''}>
					<div class="flex items-center gap-2">
						<Code2 class="h-4 w-4" />
						JSON
					</div>
				</Tabs.Trigger>
			</Tabs.List>

			<Tabs.Content value="editor" class="flex-1 overflow-auto">
				<!-- Type-specific editors -->
				{#if featureType === 'TOUR' && editorState.current.featureType === 'TOUR'}
					<TourStepEditor
						value={editorState.current}
						onChange={handleMetadataChange}
						disabled={disabled}
					/>
				{:else if featureType === 'TOOLTIP' && editorState.current.featureType === 'TOOLTIP'}
					<TooltipEditor
						value={editorState.current}
						onChange={handleMetadataChange}
						disabled={disabled}
					/>
				{:else if featureType === 'BANNER' && editorState.current.featureType === 'BANNER'}
					<BannerEditor
						value={editorState.current}
						onChange={handleMetadataChange}
						disabled={disabled}
					/>
				{:else if featureType === 'FEATURE_FLAG' && editorState.current.featureType === 'FEATURE_FLAG'}
					<FeatureFlagEditor
						value={editorState.current}
						onChange={handleMetadataChange}
						disabled={disabled}
					/>
				{:else if featureType === 'ANNOUNCEMENT' && editorState.current.featureType === 'ANNOUNCEMENT'}
					<AnnouncementEditor
						value={editorState.current}
						onChange={handleMetadataChange}
						disabled={disabled}
					/>
				{:else}
					<Card.Root>
						<Card.Content class="py-8 text-center text-muted-foreground">
							<AlertCircle class="h-8 w-8 mx-auto mb-2 opacity-50" />
							<p>No editor available for this feature type</p>
						</Card.Content>
					</Card.Root>
				{/if}
			</Tabs.Content>

			<Tabs.Content value="json" class="flex-1 overflow-auto">
				<JsonEditor
					value={editorState.current as unknown as Record<string, unknown>}
					onChange={(jsonValue) => handleMetadataChange(jsonValue as unknown as FeatureMetadata)}
					disabled={disabled}
				/>
			</Tabs.Content>
		</Tabs.Root>
	{:else}
		<div class="flex-1 overflow-auto">
			<!-- Type-specific editors (no tabs) -->
			{#if featureType === 'TOUR' && editorState.current.featureType === 'TOUR'}
				<TourStepEditor
					value={editorState.current}
					onChange={handleMetadataChange}
					disabled={disabled}
				/>
			{:else if featureType === 'TOOLTIP' && editorState.current.featureType === 'TOOLTIP'}
				<TooltipEditor
					value={editorState.current}
					onChange={handleMetadataChange}
					disabled={disabled}
				/>
			{:else if featureType === 'BANNER' && editorState.current.featureType === 'BANNER'}
				<BannerEditor
					value={editorState.current}
					onChange={handleMetadataChange}
					disabled={disabled}
				/>
			{:else if featureType === 'FEATURE_FLAG' && editorState.current.featureType === 'FEATURE_FLAG'}
				<FeatureFlagEditor
					value={editorState.current}
					onChange={handleMetadataChange}
					disabled={disabled}
				/>
			{:else if featureType === 'ANNOUNCEMENT' && editorState.current.featureType === 'ANNOUNCEMENT'}
				<AnnouncementEditor
					value={editorState.current}
					onChange={handleMetadataChange}
					disabled={disabled}
				/>
			{:else}
				<Card.Root>
					<Card.Content class="py-8 text-center text-muted-foreground">
						<AlertCircle class="h-8 w-8 mx-auto mb-2 opacity-50" />
						<p>No editor available for this feature type</p>
					</Card.Content>
				</Card.Root>
			{/if}
		</div>
	{/if}

	<!-- Footer Info -->
	<div class="flex items-center justify-between text-xs text-muted-foreground pt-2 border-t">
		<div class="flex items-center gap-4">
			<span>Keyboard: Ctrl+S to save, Ctrl+Z to undo, Esc to cancel</span>
		</div>
		<div class="flex items-center gap-2">
			{#if editorState.isDirty}
				<Badge variant="secondary" class="bg-amber-100 text-amber-800 dark:bg-amber-900 dark:text-amber-300">
					Unsaved changes
				</Badge>
			{/if}
		</div>
	</div>
</div>
