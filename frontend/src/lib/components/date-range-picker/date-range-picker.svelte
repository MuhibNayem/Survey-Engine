<script lang="ts">
	import { Calendar } from 'lucide-svelte';
	import { Button } from '$lib/components/ui/button';
	import * as Dialog from '$lib/components/ui/dialog';
	import { Label } from '$lib/components/ui/label';
	import { Input } from '$lib/components/ui/input';

	interface Preset {
		label: string;
		days: number;
	}

	interface Props {
		startDate?: string;
		endDate?: string;
		onChange?: (range: { start: string; end: string }) => void;
		presets?: Preset[];
		size?: 'sm' | 'md' | 'lg';
	}

	let {
		startDate: startDateProp = new Date(Date.now() - 30 * 24 * 60 * 60 * 1000).toISOString().split('T')[0],
		endDate: endDateProp = new Date().toISOString().split('T')[0],
		onChange = () => {},
		presets = [
			{ label: 'Last 7 days', days: 7 },
			{ label: 'Last 30 days', days: 30 },
			{ label: 'Last 90 days', days: 90 }
		],
		size = 'md'
	}: Props = $props();

	let open = $state(false);
	let localStart = $state<string>(startDateProp);
	let localEnd = $state<string>(endDateProp);

	// Sync props to local state when they change from parent
	$effect.pre(() => {
		localStart = startDateProp;
		localEnd = endDateProp;
	});

	function applyPreset(days: number) {
		const end = new Date();
		const start = new Date(Date.now() - days * 24 * 60 * 60 * 1000);
		localStart = start.toISOString().split('T')[0];
		localEnd = end.toISOString().split('T')[0];
		onChange({
			start: localStart,
			end: localEnd
		});
	}

	function applyDates() {
		onChange({
			start: localStart,
			end: localEnd
		});
		open = false;
	}

	const buttonSize = $derived(size === 'sm' ? 'sm' : size === 'lg' ? 'lg' : 'default');
</script>

<Dialog.Root bind:open>
	<Button onclick={() => (open = true)} variant="outline" size={buttonSize} class="justify-start text-left font-normal">
		<Calendar class="mr-2 h-4 w-4" aria-hidden="true" />
		{startDateProp} - {endDateProp}
	</Button>
	<Dialog.Content class="w-auto sm:max-w-md">
		<Dialog.Header>
			<Dialog.Title>Select Date Range</Dialog.Title>
			<Dialog.Description>
				Choose a preset or select custom dates
			</Dialog.Description>
		</Dialog.Header>
		<div class="space-y-4 py-4">
			<!-- Presets -->
			<div class="flex flex-wrap gap-2">
				{#each presets as preset}
					<Button
						variant="ghost"
						size="sm"
						onclick={() => applyPreset(preset.days)}
						class="text-xs"
					>
						{preset.label}
					</Button>
				{/each}
			</div>

			<!-- Custom Dates -->
			<div class="grid gap-4 sm:grid-cols-2">
				<div class="space-y-2">
					<Label for="start-date">Start Date</Label>
					<Input
						id="start-date"
						type="date"
						bind:value={localStart}
					/>
				</div>
				<div class="space-y-2">
					<Label for="end-date">End Date</Label>
					<Input
						id="end-date"
						type="date"
						bind:value={localEnd}
					/>
				</div>
			</div>

			<!-- Actions -->
			<div class="flex justify-end gap-2 pt-4">
				<Button variant="outline" onclick={() => (open = false)}>
					Cancel
				</Button>
				<Button onclick={applyDates}>
					Apply
				</Button>
			</div>
		</div>
	</Dialog.Content>
</Dialog.Root>
