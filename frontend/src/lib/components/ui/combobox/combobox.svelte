<script lang="ts">
	import { onMount } from 'svelte';
	import { Input } from '$lib/components/ui/input';
	import { Label } from '$lib/components/ui/label';
	import { Button } from '$lib/components/ui/button';
	import * as Popover from '$lib/components/ui/popover';
	import { Check, ChevronsUpDown, Search, X, Loader2 } from 'lucide-svelte';
	import { cn } from '$lib/utils';
	import api from '$lib/api';

	export interface ComboboxOption {
		value: string;
		label: string;
		description?: string;
		disabled?: boolean;
		[key: string]: unknown;
	}

	export interface ComboboxProps<T extends ComboboxOption> {
		value?: string | null;
		options?: T[];
		placeholder?: string;
		label?: string;
		searchPlaceholder?: string;
		required?: boolean;
		disabled?: boolean;
		emptyMessage?: string;
		searchEndpoint?: string;
		searchParam?: string;
		pageSize?: number;
		minSearchLength?: number;
		searchDebounce?: number;
		onValueChange?: (value: string | null) => void;
		onSearch?: (query: string, page: number) => Promise<{ options: T[]; hasMore: boolean; total?: number }>;
		optionRenderer?: (option: T) => string;
	}

	let {
		value = $bindable(null),
		options = [],
		placeholder = 'Select...',
		searchPlaceholder = 'Search...',
		label = 'Select',
		required = false,
		disabled = false,
		emptyMessage = 'No results found.',
		searchEndpoint,
		searchParam = 'search',
		pageSize = 20,
		minSearchLength = 2,
		searchDebounce = 300,
		onValueChange,
		onSearch,
		optionRenderer
	}: ComboboxProps<ComboboxOption> = $props();

	let displayOptions = $state<ComboboxOption[]>([]);
	
	// Sync options prop to displayOptions
	$effect(() => {
		if (options.length > 0 && !searchEndpoint && !onSearch) {
			displayOptions = options;
		}
	});
	let open = $state(false);
	let searchQuery = $state('');
	let isLoading = $state(false);
	let isLoadingMore = $state(false);
	let hasMore = $state(true);
	let currentPage = $state(0);
	let selectedOption = $state<ComboboxOption | null>(null);
	let debounceTimer: ReturnType<typeof setTimeout> | null = null;

	onMount(async () => {
		if (options.length > 0) {
			displayOptions = options;
		} else if (searchEndpoint || onSearch) {
			await loadOptions('', 0);
		}
	});

	// Load options when popover opens
	$effect(() => {
		if (open && (searchEndpoint || onSearch) && displayOptions.length === 0) {
			loadOptions('', 0);
		}
	});

	// Watch for value changes and update selected option
	$effect(() => {
		if (value && displayOptions.length > 0) {
			selectedOption = displayOptions.find(o => o.value === value) || null;
		} else {
			selectedOption = null;
		}
	});

	// Watch for selected option changes and update value
	$effect(() => {
		if (selectedOption) {
			value = selectedOption.value;
			onValueChange?.(selectedOption.value);
		}
	});

	// Watch for options prop changes
	$effect(() => {
		if (options.length > 0 && !searchEndpoint && !onSearch) {
			displayOptions = options;
		}
	});

	async function loadOptions(query: string, page: number, append = false): Promise<void> {
		if (isLoadingMore && page > 0) return;
		
		if (page === 0) {
			isLoading = true;
		} else {
			isLoadingMore = true;
		}

		try {
			let result: { options: ComboboxOption[]; hasMore: boolean };

			if (onSearch) {
				// Use custom search function
				const searchResult = await onSearch(query, page);
				result = {
					options: searchResult.options,
					hasMore: searchResult.hasMore
				};
			} else if (searchEndpoint) {
				// Use API endpoint
				const params = new URLSearchParams();
				params.set(searchParam, query);
				params.set('page', page.toString());
				params.set('size', pageSize.toString());

				const response = await api.get(`${searchEndpoint}?${params.toString()}`);
				result = {
					options: response.data.content || response.data,
					hasMore: response.data.totalPages ? page < response.data.totalPages - 1 : false
				};
			} else {
				// Filter local options
				result = {
					options: options.filter(o => 
						o.label.toLowerCase().includes(query.toLowerCase())
					),
					hasMore: false
				};
			}

			if (append && page > 0) {
				displayOptions = [...displayOptions, ...result.options];
			} else {
				displayOptions = result.options;
			}

			hasMore = result.hasMore;
			currentPage = page;
		} catch (error) {
			console.error('Failed to load options:', error);
			displayOptions = [];
			hasMore = false;
		} finally {
			isLoading = false;
			isLoadingMore = false;
		}
	}

	function handleSearch(event: Event): void {
		const target = event.target as HTMLInputElement;
		searchQuery = target.value;

		// Clear existing debounce
		if (debounceTimer) {
			clearTimeout(debounceTimer);
		}

		// Debounce search
		debounceTimer = setTimeout(() => {
			if (searchQuery.length >= minSearchLength) {
				loadOptions(searchQuery, 0, false);
			} else if (searchQuery.length === 0) {
				loadOptions('', 0, false);
			}
		}, searchDebounce);
	}

	function clearSearch(): void {
		searchQuery = '';
		if (debounceTimer) {
			clearTimeout(debounceTimer);
		}
		loadOptions('', 0, false);
	}

	function selectOption(option: ComboboxOption): void {
		if (option.disabled) return;
		selectedOption = option;
		open = false;
		searchQuery = '';
	}

	function clearSelection(): void {
		selectedOption = null;
		value = null;
		onValueChange?.(null);
		searchQuery = '';
		open = false;
	}

	function handleScroll(event: Event): void {
		const target = event.target as HTMLElement;
		const scrollTop = target.scrollTop;
		const scrollHeight = target.scrollHeight;
		const clientHeight = target.clientHeight;

		// Load more when scrolled to bottom (within 50px)
		if (scrollTop + clientHeight >= scrollHeight - 50 && hasMore && !isLoadingMore) {
			loadOptions(searchQuery, currentPage + 1, true);
		}
	}

	function getOptionLabel(option: ComboboxOption): string {
		if (optionRenderer) {
			return optionRenderer(option);
		}
		return option.label;
	}
</script>

<div class="space-y-2">
	{#if label}
		<Label for="combobox-input">
			{label}
			{#if required}
				<span class="text-destructive">*</span>
			{/if}
		</Label>
	{/if}

	<Popover.Root bind:open>
		<Popover.Trigger>
			<Button
				variant="outline"
				role="combobox"
				aria-expanded={open}
				class="w-full justify-between h-10"
				disabled={disabled || isLoading}
			>
				<span class="truncate">
					{#if selectedOption}
						{getOptionLabel(selectedOption)}
					{:else}
						<span class="text-muted-foreground">{placeholder}</span>
					{/if}
				</span>
				<ChevronsUpDown class="ml-2 h-4 w-4 shrink-0 opacity-50" />
			</Button>
		</Popover.Trigger>
		<Popover.Content class="w-[--radix-popover-trigger-width] p-0" align="start">
			<div class="space-y-2 p-2">
				<div class="relative">
					<Search class="absolute left-2 top-2.5 h-4 w-4 text-muted-foreground" />
					<Input
						id="combobox-input"
						placeholder={searchPlaceholder}
						value={searchQuery}
						oninput={handleSearch}
						class="pl-8 h-9"
					/>
					{#if searchQuery}
						<button
							class="absolute right-2 top-2 text-muted-foreground hover:text-foreground"
							onclick={clearSearch}
							type="button"
						>
							<X class="h-4 w-4" />
						</button>
					{/if}
				</div>

				<div 
					class="max-h-64 overflow-y-auto space-y-1"
					onscroll={handleScroll}
				>
					{#if isLoading && currentPage === 0}
						<div class="flex items-center justify-center py-8 text-sm text-muted-foreground">
							<Loader2 class="h-4 w-4 animate-spin mr-2" />
							Loading...
						</div>
					{:else if displayOptions.length === 0}
						<div class="flex items-center justify-center py-8 text-sm text-muted-foreground">
							{searchQuery ? emptyMessage : 'No options available'}
						</div>
					{:else}
						{#each displayOptions as option (option.value)}
							<button
								class={cn(
									"w-full flex items-center justify-between px-2 py-1.5 text-sm rounded-md hover:bg-accent hover:text-accent-foreground transition-colors",
									selectedOption?.value === option.value ? 'bg-accent text-accent-foreground' : '',
									option.disabled ? 'opacity-50 cursor-not-allowed' : 'cursor-pointer'
								)}
								onclick={() => selectOption(option)}
								disabled={option.disabled}
								type="button"
							>
								<div class="flex items-center gap-2 flex-1 min-w-0">
									<span class="truncate">{getOptionLabel(option)}</span>
									{#if option.description}
										<span class="text-xs text-muted-foreground truncate hidden sm:inline">
											{option.description}
										</span>
									{/if}
								</div>
								{#if selectedOption?.value === option.value}
									<Check class="h-4 w-4 shrink-0" />
								{/if}
							</button>
						{/each}

						{#if isLoadingMore}
							<div class="flex items-center justify-center py-4 text-sm text-muted-foreground">
								<Loader2 class="h-4 w-4 animate-spin mr-2" />
								Loading more...
							</div>
						{:else if hasMore}
							<div class="text-center py-2 text-xs text-muted-foreground">
								Scroll for more
							</div>
						{/if}
					{/if}
				</div>
			</div>
		</Popover.Content>
	</Popover.Root>

	{#if selectedOption}
		<div class="flex items-center gap-2">
			<p class="text-xs text-muted-foreground">
				Selected: <span class="font-medium">{getOptionLabel(selectedOption)}</span>
			</p>
			<Button
				variant="ghost"
				size="sm"
				class="h-6 px-2 text-xs"
				onclick={clearSelection}
				type="button"
			>
				Clear
			</Button>
		</div>
	{/if}
</div>
