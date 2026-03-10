<script lang="ts">
	import { Input } from "$lib/components/ui/input";
	import * as Popover from "$lib/components/ui/popover";
	import { Button } from "$lib/components/ui/button";
	import { Label } from "$lib/components/ui/label";
	import { Check, ChevronDown, Search, X, ArrowUp, ArrowDown } from "lucide-svelte";
	import { cn } from "$lib/utils";

	export interface MultiSelectOption {
		value: string;
		label: string;
		description?: string;
		disabled?: boolean;
	}

	interface MultiSelectProps {
		options: MultiSelectOption[];
		selected?: string[];
		label?: string;
		placeholder?: string;
		searchPlaceholder?: string;
		emptyMessage?: string;
		disabled?: boolean;
		sortable?: boolean;
		showOrder?: boolean;
		selectedHelperText?: string;
		class?: string;
	}

	let {
		options,
		selected = $bindable([]),
		label,
		placeholder = "Select one or more options",
		searchPlaceholder = "Search options",
		emptyMessage = "No options found.",
		disabled = false,
		sortable = false,
		showOrder = false,
		selectedHelperText = "",
		class: className,
	}: MultiSelectProps = $props();

	let open = $state(false);
	let search = $state("");

	const selectedOptions = $derived.by(() =>
		selected
			.map((value) => options.find((option) => option.value === value))
			.filter((option): option is MultiSelectOption => Boolean(option)),
	);

	const filteredOptions = $derived.by(() => {
		const query = search.trim().toLowerCase();
		if (!query) {
			return options;
		}
		return options.filter((option) =>
			option.label.toLowerCase().includes(query) ||
			option.value.toLowerCase().includes(query) ||
			(option.description ?? "").toLowerCase().includes(query),
		);
	});

	function isSelected(value: string): boolean {
		return selected.includes(value);
	}

	function toggle(value: string): void {
		if (disabled) return;
		selected = isSelected(value)
			? selected.filter((item) => item !== value)
			: [...selected, value];
	}

	function remove(value: string): void {
		selected = selected.filter((item) => item !== value);
	}

	function move(value: string, direction: "up" | "down"): void {
		if (!sortable) return;
		const index = selected.indexOf(value);
		if (index < 0) return;
		const targetIndex = direction === "up" ? index - 1 : index + 1;
		if (targetIndex < 0 || targetIndex >= selected.length) return;

		const next = [...selected];
		const [item] = next.splice(index, 1);
		next.splice(targetIndex, 0, item);
		selected = next;
	}
</script>

<div class={cn("space-y-3", className)}>
	{#if label}
		<Label>{label}</Label>
	{/if}

	<Popover.Root bind:open>
		<Popover.Trigger>
			<button
				type="button"
				class={cn(
					"flex w-full items-start justify-between gap-3 rounded-2xl border border-border/80 bg-background px-4 py-3 text-left shadow-[0_18px_50px_-36px_rgba(15,23,42,0.35)] transition hover:border-primary/35 hover:bg-accent/30 disabled:pointer-events-none disabled:opacity-60 dark:border-input dark:bg-input/30 dark:hover:bg-input/50",
				)}
				aria-expanded={open}
				disabled={disabled}
			>
				<div class="min-w-0 flex-1 space-y-2">
					<div class="text-sm font-semibold text-foreground">Selected Options</div>
					{#if selectedOptions.length > 0}
						<div class="flex flex-wrap gap-2.5">
							{#each selectedOptions as option, index}
								<span
									class="inline-flex max-w-full items-center gap-2.5 rounded-full border border-border/80 bg-background px-3 py-1.5 text-xs font-medium text-foreground shadow-sm dark:border-input dark:bg-input/40"
								>
									{#if showOrder}
										<span class="inline-flex h-5 min-w-5 items-center justify-center rounded-full bg-primary/10 px-1 text-[10px] font-semibold text-primary">
											{index + 1}
										</span>
									{/if}
									<span class="min-w-0 truncate">{option.label}</span>
								</span>
							{/each}
						</div>
					{:else}
						<div class="text-sm text-muted-foreground">{placeholder}</div>
					{/if}
				</div>
				<ChevronDown class={cn("mt-1 h-4 w-4 shrink-0 text-muted-foreground transition-transform", open && "rotate-180")} />
			</button>
		</Popover.Trigger>

		<Popover.Content
			class="w-[460px] max-w-[calc(100vw-2rem)] rounded-2xl border border-border/80 bg-popover p-0 shadow-[0_30px_80px_-40px_rgba(15,23,42,0.45)] backdrop-blur-xl dark:border-input dark:bg-popover"
			align="start"
		>
			<div class="border-b border-border/70 p-3">
				<div class="relative">
					<Search class="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
					<Input
						bind:value={search}
						class="h-11 rounded-xl border-border/70 bg-muted/30 pl-10 pr-3 shadow-none transition focus-visible:border-primary/45 focus-visible:ring-2 focus-visible:ring-primary/15 dark:border-input dark:bg-input/30 dark:focus-visible:border-ring dark:focus-visible:ring-ring/30"
						placeholder={searchPlaceholder}
					/>
				</div>
			</div>
			<div class="flex max-h-80 flex-col gap-2.5 overflow-y-auto p-2">
				{#if filteredOptions.length > 0}
					{#each filteredOptions as option}
						<button
							type="button"
							class={cn(
								"flex w-full items-start justify-between gap-3 rounded-xl px-3 py-3 text-left transition",
								isSelected(option.value)
									? "bg-primary/8 text-foreground dark:bg-primary/18"
									: "hover:bg-muted/50 dark:hover:bg-input/45",
								option.disabled && "pointer-events-none opacity-50",
							)}
							onclick={() => toggle(option.value)}
						>
							<div class="min-w-0">
								<div class="text-sm font-medium">{option.label}</div>
								{#if option.description}
									<div class="mt-0.5 text-xs leading-5 text-muted-foreground">{option.description}</div>
								{/if}
							</div>
							{#if isSelected(option.value)}
								<span class="inline-flex h-6 w-6 shrink-0 items-center justify-center rounded-full bg-primary text-primary-foreground">
									<Check class="h-3.5 w-3.5" />
								</span>
							{/if}
						</button>
					{/each}
				{:else}
					<div class="px-3 py-6 text-center text-sm text-muted-foreground">{emptyMessage}</div>
				{/if}
			</div>
		</Popover.Content>
	</Popover.Root>

	{#if selectedOptions.length > 0}
		<div class="space-y-3 rounded-2xl border border-border/70 bg-muted/20 p-3">
			{#if selectedHelperText}
				<p class="text-xs leading-5 text-muted-foreground">{selectedHelperText}</p>
			{/if}
			<div class="space-y-2.5">
				{#each selectedOptions as option, index}
					<div class="flex min-w-0 items-center gap-3 rounded-2xl border border-border/80 bg-background px-3 py-2.5 shadow-sm dark:border-input dark:bg-input/30">
						<div class="flex min-w-0 flex-1 items-center gap-2.5">
							{#if showOrder}
								<div class="inline-flex h-6 min-w-6 shrink-0 items-center justify-center rounded-full bg-primary/10 px-1 text-[10px] font-semibold text-primary">
									{index + 1}
								</div>
							{/if}
							<div class="min-w-0 flex-1">
								<div class="truncate text-sm font-medium">{option.label}</div>
								{#if option.description}
									<div class="truncate pt-0.5 text-xs text-muted-foreground">{option.description}</div>
								{/if}
							</div>
						</div>
						<div class="flex shrink-0 items-center gap-1 rounded-full bg-muted/35 p-1 dark:bg-input/60">
							{#if sortable}
								<Button
									type="button"
									size="sm"
									variant="ghost"
									class="h-8 w-8 rounded-full p-0"
									disabled={index === 0}
									onclick={(event) => {
										event.stopPropagation();
										move(option.value, "up");
									}}
								>
									<ArrowUp class="h-3.5 w-3.5" />
								</Button>
								<Button
									type="button"
									size="sm"
									variant="ghost"
									class="h-8 w-8 rounded-full p-0"
									disabled={index === selectedOptions.length - 1}
									onclick={(event) => {
										event.stopPropagation();
										move(option.value, "down");
									}}
								>
									<ArrowDown class="h-3.5 w-3.5" />
								</Button>
							{/if}
							<Button
								type="button"
								size="sm"
								variant="ghost"
								class="h-8 w-8 rounded-full p-0 text-muted-foreground hover:text-destructive"
								onclick={(event) => {
									event.stopPropagation();
									remove(option.value);
								}}
							>
								<X class="h-3.5 w-3.5" />
							</Button>
						</div>
					</div>
				{/each}
			</div>
		</div>
	{/if}
</div>
