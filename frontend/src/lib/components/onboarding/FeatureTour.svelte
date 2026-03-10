<script lang="ts">
	import { onMount, onDestroy } from 'svelte';
	import { Button } from '$lib/components/ui/button';
	import * as Dialog from '$lib/components/ui/dialog';
	import * as Card from '$lib/components/ui/card';
	import { X, ChevronLeft, ChevronRight, Lightbulb, CheckCircle2 } from 'lucide-svelte';
	import { useFeatureFlag } from '$lib/hooks/useFeatureFlag.svelte';

	export interface TourStep {
		id: string;
		title: string;
		description: string;
		content?: string;
		targetSelector?: string;
		placement?: 'top' | 'bottom' | 'left' | 'right';
	}

	export interface TourConfig {
		id: string;
		title: string;
		steps: TourStep[];
		enabled?: boolean;
	}

	interface Props {
		tour: TourConfig;
		autoStart?: boolean;
		onComplete?: () => void;
		onSkip?: () => void;
	}

	let {
		tour,
		autoStart = true,
		onComplete,
		onSkip
	}: Props = $props();

	let currentStep = $state(0);
	let open = $state(false);
	let loading = $state(false);
	let isHighlighting = $state(false);
	let dontShowAgain = $state(false);

	// Use feature flag hook for backend integration
	// Tour ID already includes prefix (e.g., 'tour.dashboard'), so use it directly
	const {
		status,
		isCompleted,
		complete: completeFeature,
		reset: resetFeature
	} = useFeatureFlag(() => tour.id, {
		get autoCheck() { return autoStart; },
		autoRecordAccess: true
	});

	onMount(async () => {
		if (!autoStart) return;

		// Wait for status to load
		await new Promise(resolve => setTimeout(resolve, 100));

		// Check if tour should be shown
		if (status?.shouldShow && !isCompleted) {
			open = true;
			highlightElement(tour.steps[0]?.targetSelector);
		}
	});

	function next(): void {
		if (currentStep < tour.steps.length - 1) {
			currentStep++;
			highlightElement(tour.steps[currentStep].targetSelector);
		} else {
			complete();
		}
	}

	function prev(): void {
		if (currentStep > 0) {
			currentStep--;
			highlightElement(tour.steps[currentStep].targetSelector);
		}
	}

	async function complete(): Promise<void> {
		open = false;
		loading = true;

		try {
			await completeFeature();
		} catch (error) {
			console.error('Failed to save tour completion:', error);
		} finally {
			loading = false;
		}

		removeHighlight();
		onComplete?.();
	}

	async function skip(): Promise<void> {
		open = false;

		if (dontShowAgain) {
			loading = true;
			try {
				await completeFeature();
			} catch (error) {
				console.error('Failed to save tour skip:', error);
			} finally {
				loading = false;
			}
		}

		removeHighlight();
		onSkip?.();
	}

	function highlightElement(selector?: string): void {
		if (!selector || typeof window === 'undefined') return;

		removeHighlight();

		isHighlighting = true;
		const element = document.querySelector(selector);
		if (element) {
			element.scrollIntoView({ behavior: 'smooth', block: 'center' });
			element.classList.add('ring-4', 'ring-primary', 'ring-offset-2', 'transition-all', 'duration-300');
			element.setAttribute('data-tour-highlighted', 'true');
		}
	}

	function removeHighlight(): void {
		if (typeof window === 'undefined') return;

		const highlighted = document.querySelectorAll('[data-tour-highlighted]');
		highlighted.forEach(el => {
			el.classList.remove('ring-4', 'ring-primary', 'ring-offset-2', 'transition-all', 'duration-300');
			el.removeAttribute('data-tour-highlighted');
		});
		isHighlighting = false;
	}

	function goToStep(index: number): void {
		if (index >= 0 && index < tour.steps.length) {
			currentStep = index;
			highlightElement(tour.steps[index].targetSelector);
		}
	}

	onDestroy(() => {
		removeHighlight();
	});
</script>

<Dialog.Root bind:open onOpenChange={(newOpen) => {
	if (!newOpen) {
		removeHighlight();
	}
}}>
	<Dialog.Content class="sm:max-w-lg">
		<Dialog.Header>
			<div class="flex items-start justify-between gap-4">
				<div class="flex items-center gap-3">
					<div class="flex h-10 w-10 items-center justify-center rounded-full bg-primary/10">
						<Lightbulb class="h-5 w-5 text-primary" />
					</div>
					<div>
						<Dialog.Title class="text-lg">
							{tour.title}
						</Dialog.Title>
						<Dialog.Description class="text-sm">
							Step {currentStep + 1} of {tour.steps.length}
						</Dialog.Description>
					</div>
				</div>
				<Button
					variant="ghost"
					size="icon"
					onclick={skip}
					class="h-8 w-8 shrink-0"
					disabled={loading}
				>
					<X class="h-4 w-4" />
				</Button>
			</div>
		</Dialog.Header>

		<!-- Step Content -->
		<Card.Root class="mt-4 bg-gradient-to-br from-muted/50 to-muted/30 border-primary/20">
			<Card.Content class="py-4">
				<h3 class="font-semibold text-foreground mb-2">
					{tour.steps[currentStep].title}
				</h3>
				<p class="text-sm text-muted-foreground leading-relaxed">
					{tour.steps[currentStep].description}
				</p>
				{#if tour.steps[currentStep].content}
					<div class="mt-3 pt-3 border-t border-border/50">
						<p class="text-xs text-muted-foreground">
							{tour.steps[currentStep].content}
						</p>
					</div>
				{/if}
			</Card.Content>
		</Card.Root>

		<!-- Step Indicators -->
		<div class="flex justify-center gap-2 my-6">
			{#each tour.steps as _, i}
				<button
					type="button"
					onclick={() => goToStep(i)}
					class="h-2 rounded-full transition-all duration-300 {i === currentStep
						? 'w-8 bg-primary'
						: 'w-2 bg-muted hover:bg-muted/70'}"
					aria-label="Go to step {i + 1}"
					disabled={loading}
				></button>
			{/each}
		</div>

		<!-- Action Buttons with "Don't show again" -->
		<Dialog.Footer class="flex flex-col gap-3 sm:flex-row sm:items-center">
			<div class="flex-1">
				<label class="flex items-center gap-2 cursor-pointer select-none">
					<input
						type="checkbox"
						class="h-4 w-4 rounded border-gray-300 text-primary focus:ring-primary"
						bind:checked={dontShowAgain}
					/>
					<span class="text-xs text-muted-foreground">
						Don't show this tour again
					</span>
				</label>
			</div>

			<div class="flex gap-2">
				<Button
					variant="ghost"
					onclick={prev}
					disabled={currentStep === 0 || loading}
					class="shrink-0"
				>
					<ChevronLeft class="mr-1 h-4 w-4" />
					Previous
				</Button>
				<Button
					onclick={next}
					disabled={loading}
					class="shrink-0"
				>
					{currentStep === tour.steps.length - 1
						? 'Get Started'
						: 'Next'}
					{#if currentStep === tour.steps.length - 1}
						<CheckCircle2 class="ml-2 h-4 w-4" />
					{:else}
						<ChevronRight class="ml-1 h-4 w-4" />
					{/if}
				</Button>
			</div>
		</Dialog.Footer>
	</Dialog.Content>
</Dialog.Root>
