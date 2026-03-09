<script lang="ts">
	import { Button } from '$lib/components/ui/button';
	import * as Dialog from '$lib/components/ui/dialog';
	import { X, ChevronLeft, ChevronRight } from 'lucide-svelte';
	import * as Card from '$lib/components/ui/card';

	export interface Step {
		title: string;
		description: string;
		targetSelector?: string;
		content?: string;
	}

	interface Props {
		steps: Step[];
		onComplete?: () => void;
		onSkip?: () => void;
		autoStart?: boolean;
		storageKey?: string;
	}

	let {
		steps,
		onComplete,
		onSkip,
		autoStart = true,
		storageKey = 'onboarding-complete'
	}: Props = $props();

	let currentStep = $state(0);
	let open = $state(false);
	let isHighlighting = $state(false);

	// Check if onboarding was completed
	$effect(() => {
		if (typeof window !== 'undefined' && autoStart) {
			const completed = localStorage.getItem(storageKey);
			if (!completed) {
				open = true;
			}
		}
	});

	function next(): void {
		if (currentStep < steps.length - 1) {
			currentStep++;
			highlightElement(steps[currentStep].targetSelector);
		} else {
			complete();
		}
	}

	function prev(): void {
		if (currentStep > 0) {
			currentStep--;
			highlightElement(steps[currentStep].targetSelector);
		}
	}

	function complete(): void {
		open = false;
		if (typeof window !== 'undefined') {
			localStorage.setItem(storageKey, 'true');
		}
		onComplete?.();
	}

	function skip(): void {
		open = false;
		if (typeof window !== 'undefined') {
			localStorage.setItem(storageKey, 'true');
		}
		onSkip?.();
	}

	function highlightElement(selector?: string): void {
		if (!selector || typeof window === 'undefined') return;

		isHighlighting = true;
		const element = document.querySelector(selector);
		if (element) {
			element.scrollIntoView({ behavior: 'smooth', block: 'center' });
			element.classList.add('ring-4', 'ring-primary', 'ring-offset-2');
			setTimeout(() => {
				element.classList.remove('ring-4', 'ring-primary', 'ring-offset-2');
				isHighlighting = false;
			}, 2000);
		}
	}

	function goToStep(index: number): void {
		if (index >= 0 && index < steps.length) {
			currentStep = index;
			highlightElement(steps[index].targetSelector);
		}
	}
</script>

<Dialog.Root bind:open>
	<Dialog.Content class="sm:max-w-md">
		<Dialog.Header>
			<div class="flex items-center justify-between">
				<Dialog.Title>{steps[currentStep].title}</Dialog.Title>
				<Button variant="ghost" size="icon" onclick={skip} class="h-6 w-6">
					<X class="h-4 w-4" />
				</Button>
			</div>
			<Dialog.Description>
				{steps[currentStep].description}
			</Dialog.Description>
		</Dialog.Header>

		{#if steps[currentStep].content}
			<Card.Root class="mt-4 bg-muted/50">
				<Card.Content class="py-4">
					<p class="text-sm text-muted-foreground">
						{steps[currentStep].content}
					</p>
				</Card.Content>
			</Card.Root>
		{/if}

		<!-- Step Indicators -->
		<div class="flex justify-center gap-2 my-4">
			{#each steps as _, i}
				<button
					type="button"
					onclick={() => goToStep(i)}
					class="h-2 rounded-full transition-all {i === currentStep
						? 'w-8 bg-primary'
						: 'w-2 bg-muted'}"
					aria-label="Go to step {i + 1}"
				></button>
			{/each}
		</div>

		<Dialog.Footer>
			<Button
				variant="ghost"
				onclick={prev}
				disabled={currentStep === 0}
			>
				<ChevronLeft class="mr-1 h-4 w-4" />
				Previous
			</Button>
			<Button onclick={next}>
				{currentStep === steps.length - 1 ? 'Get Started' : 'Next'}
				{#if currentStep < steps.length - 1}
					<ChevronRight class="ml-1 h-4 w-4" />
				{/if}
			</Button>
		</Dialog.Footer>
	</Dialog.Content>
</Dialog.Root>
