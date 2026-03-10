<script lang="ts">
	import { onMount } from 'svelte';
	import { Button } from '$lib/components/ui/button';
	import { X, Lightbulb, CheckCircle } from 'lucide-svelte';
	import { useFeatureFlag } from '$lib/hooks/useFeatureFlag.svelte';

	interface Props {
		featureKey?: string;
		tooltipId: string;
		title: string;
		content: string;
		targetSelector: string;
		placement?: 'top' | 'bottom' | 'left' | 'right';
		delay?: number;
		onDismiss?: () => void;
	}

	let {
		featureKey,
		tooltipId,
		title,
		content,
		targetSelector,
		placement = 'top',
		delay = 1000,
		onDismiss
	}: Props = $props();

	let visible = $state(false);
	let loading = $state(false);
	let position = $state({ top: 0, left: 0 });

	// Use feature flag hook for backend integration
	const {
		status,
		isCompleted,
		complete: completeFeature
	} = useFeatureFlag(() => featureKey ?? `tooltip.${tooltipId}`, {
		autoCheck: true,
		autoRecordAccess: true
	});

	onMount(async () => {
		// Wait for status to load
		await new Promise(resolve => setTimeout(resolve, 100));

		// Check if tooltip should be shown
		if (!status?.shouldShow || isCompleted) {
			return;
		}

		// Show tooltip after delay
		setTimeout(() => {
			positionTooltip();
			visible = true;
		}, delay);
	});

	function positionTooltip(): void {
		if (typeof window === 'undefined') return;

		const target = document.querySelector(targetSelector);
		if (!target) return;

		const targetRect = target.getBoundingClientRect();
		const tooltipElement = document.getElementById(`tooltip-${tooltipId}`);

		if (!tooltipElement) return;

		const tooltipRect = tooltipElement.getBoundingClientRect();

		let top = 0;
		let left = 0;

		switch (placement) {
			case 'top':
				top = targetRect.top - tooltipRect.height - 10;
				left = targetRect.left + (targetRect.width / 2) - (tooltipRect.width / 2);
				break;
			case 'bottom':
				top = targetRect.bottom + 10;
				left = targetRect.left + (targetRect.width / 2) - (tooltipRect.width / 2);
				break;
			case 'left':
				top = targetRect.top + (targetRect.height / 2) - (tooltipRect.height / 2);
				left = targetRect.left - tooltipRect.width - 10;
				break;
			case 'right':
				top = targetRect.top + (targetRect.height / 2) - (tooltipRect.height / 2);
				left = targetRect.right + 10;
				break;
		}

		position = { top, left };
	}

	async function dismiss(): Promise<void> {
		visible = false;
		loading = true;

		try {
			// Mark as completed
			await completeFeature();
		} catch (error) {
			console.error('Failed to save tooltip dismissal:', error);
		} finally {
			loading = false;
		}

		onDismiss?.();
	}

	function close(): void {
		visible = false;
	}
</script>

{#if visible}
	<div
		id="tooltip-{tooltipId}"
		class="fixed z-50 w-80 animate-in fade-in zoom-in-95 duration-200"
		style="top: {position.top}px; left: {position.left}px;"
		role="tooltip"
	>
		<!-- Tooltip Card -->
		<div class="overflow-hidden rounded-lg border border-border bg-popover shadow-lg">
			<!-- Header -->
			<div class="flex items-start justify-between gap-2 border-b border-border bg-gradient-to-r from-primary/5 to-transparent px-4 py-3">
				<div class="flex items-center gap-2">
					<Lightbulb class="h-4 w-4 text-primary" />
					<h4 class="font-semibold text-sm text-foreground">
						{title}
					</h4>
				</div>
				<Button
					variant="ghost"
					size="icon"
					class="h-6 w-6 shrink-0"
					onclick={close}
					disabled={loading}
				>
					<X class="h-3 w-3" />
				</Button>
			</div>

			<!-- Content -->
			<div class="px-4 py-3">
				<p class="text-sm text-muted-foreground leading-relaxed">
					{content}
				</p>
			</div>

			<!-- Footer with Don't Show Again -->
			<div class="flex items-center justify-between gap-2 border-t border-border bg-muted/30 px-4 py-2">
				<label class="flex items-center gap-2 cursor-pointer select-none">
					<input
						type="checkbox"
						class="h-3.5 w-3.5 rounded border-gray-300 text-primary focus:ring-primary"
						onchange={dismiss}
						disabled={loading}
					/>
					<span class="text-xs text-muted-foreground">
						Don't show again
					</span>
				</label>

				{#if loading}
					<span class="text-xs text-muted-foreground">Saving...</span>
				{:else}
					<CheckCircle class="h-3.5 w-3.5 text-muted-foreground/50" />
				{/if}
			</div>
		</div>

		<!-- Arrow/Pointer -->
		<div
			class="absolute h-3 w-3 rotate-45 border-r border-b border-border bg-popover shadow-sm"
			style={placement === 'top'
				? 'bottom: -6px; left: 50%; transform: translateX(-50%) rotate(45deg);'
				: placement === 'bottom'
				? 'top: -6px; left: 50%; transform: translateX(-50%) rotate(45deg);'
				: placement === 'left'
				? 'right: -6px; top: 50%; transform: translateY(-50%) rotate(45deg);'
				: 'left: -6px; top: 50%; transform: translateY(-50%) rotate(45deg);'
			}
		></div>
	</div>
{/if}
