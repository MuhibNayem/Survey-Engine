<script lang="ts">
	import { onMount, onDestroy } from 'svelte';
	import { 
		Search, 
		FileText, 
		BarChart3, 
		Mail, 
		Plus, 
		Settings, 
		HelpCircle, 
		ChevronRight,
		ArrowUp,
		ArrowDown,
		CornerDownRight,
		X
	} from 'lucide-svelte';
	import { goto } from '$app/navigation';

	export interface CommandItem {
		id: string;
		label: string;
		icon?: typeof Search;
		shortcuts?: string[];
		action: () => void;
		section?: string;
	}

	interface Props {
		open?: boolean;
		onOpenChange?: (open: boolean) => void;
	}

	let { open = $bindable(false), onOpenChange }: Props = $props();

	let query = $state('');
	let selectedId = $state<string | null>(null);
	let isClosing = $state(false);

	// Define all available commands
	const allCommands: CommandItem[] = [
		// Navigation
		{
			id: 'nav-dashboard',
			label: 'Go to Dashboard',
			icon: BarChart3,
			shortcuts: ['G', 'D'],
			action: () => goto('/dashboard'),
			section: 'Navigation'
		},
		{
			id: 'nav-surveys',
			label: 'Go to Surveys',
			icon: FileText,
			shortcuts: ['G', 'S'],
			action: () => goto('/surveys'),
			section: 'Navigation'
		},
		{
			id: 'nav-campaigns',
			label: 'Go to Campaigns',
			icon: Mail,
			shortcuts: ['G', 'C'],
			action: () => goto('/campaigns'),
			section: 'Navigation'
		},
		{
			id: 'nav-questions',
			label: 'Go to Questions',
			icon: HelpCircle,
			action: () => goto('/questions'),
			section: 'Navigation'
		},
		{
			id: 'nav-categories',
			label: 'Go to Categories',
			icon: FileText,
			action: () => goto('/categories'),
			section: 'Navigation'
		},
		{
			id: 'nav-responses',
			label: 'Go to Responses',
			icon: BarChart3,
			action: () => goto('/responses'),
			section: 'Navigation'
		},
		{
			id: 'nav-activity',
			label: 'Go to Activity',
			icon: FileText,
			action: () => goto('/activity'),
			section: 'Navigation'
		},
		{
			id: 'nav-settings',
			label: 'Go to Settings',
			icon: Settings,
			action: () => goto('/settings/auth'),
			section: 'Navigation'
		},

		// Actions - Navigate to page and trigger create dialog via URL hash
		{
			id: 'action-new-survey',
			label: 'Create New Survey',
			icon: Plus,
			shortcuts: ['N', 'S'],
			action: () => {
				goto('/surveys#new');
			},
			section: 'Actions'
		},
		{
			id: 'action-new-campaign',
			label: 'Create New Campaign',
			icon: Plus,
			shortcuts: ['N', 'C'],
			action: () => {
				goto('/campaigns#new');
			},
			section: 'Actions'
		},
		{
			id: 'action-new-question',
			label: 'Create New Question',
			icon: Plus,
			shortcuts: ['N', 'Q'],
			action: () => {
				goto('/questions#new');
			},
			section: 'Actions'
		}
	];

	// Filter commands based on query (computed, not state)
	function getFilteredItems(): CommandItem[] {
		if (!query.trim()) {
			return allCommands;
		}
		const q = query.toLowerCase();
		return allCommands
			.filter(cmd =>
				cmd.label.toLowerCase().includes(q) ||
				cmd.section?.toLowerCase().includes(q) ||
				cmd.shortcuts?.some(s => s.toLowerCase().includes(q))
			)
			.sort((a, b) => {
				const aLabelMatch = a.label.toLowerCase().indexOf(q);
				const bLabelMatch = b.label.toLowerCase().indexOf(q);
				if (aLabelMatch !== -1 && bLabelMatch === -1) return -1;
				if (aLabelMatch === -1 && bLabelMatch !== -1) return 1;
				return aLabelMatch - bLabelMatch;
			});
	}

	// Update selectedId when query or filtered items change
	$effect(() => {
		const filtered = getFilteredItems();
		selectedId = filtered[0]?.id ?? null;
	});

	let items = $derived(getFilteredItems());

	function handleKeyDown(e: KeyboardEvent) {
		if (e.key === 'ArrowDown') {
			e.preventDefault();
			const idx = items.findIndex(i => i.id === selectedId);
			const nextIdx = (idx + 1) % items.length;
			selectedId = items[nextIdx]?.id ?? null;
		} else if (e.key === 'ArrowUp') {
			e.preventDefault();
			const idx = items.findIndex(i => i.id === selectedId);
			const prevIdx = idx <= 0 ? items.length - 1 : idx - 1;
			selectedId = items[prevIdx]?.id ?? null;
		} else if (e.key === 'Enter' && selectedId) {
			e.preventDefault();
			const item = items.find(i => i.id === selectedId);
			item?.action();
			close();
		} else if (e.key === 'Escape') {
			close();
		}
	}

	function close() {
		isClosing = true;
		query = '';
		open = false;
		onOpenChange?.(false);
		setTimeout(() => {
			isClosing = false;
		}, 150);
	}

	function selectItem(item: CommandItem) {
		item.action();
		close();
	}

	function groupBySection(items: CommandItem[]): Record<string, CommandItem[]> {
		return items.reduce((acc, item) => {
			const section = item.section ?? 'Other';
			if (!acc[section]) acc[section] = [];
			acc[section].push(item);
			return acc;
		}, {} as Record<string, CommandItem[]>);
	}

	function handleGlobalKeydown(e: KeyboardEvent) {
		if ((e.metaKey || e.ctrlKey) && e.key === 'k') {
			e.preventDefault();
			open = true;
			onOpenChange?.(true);
		}
	}

	onMount(() => {
		window.addEventListener('keydown', handleGlobalKeydown);
	});

	onDestroy(() => {
		window.removeEventListener('keydown', handleGlobalKeydown);
	});
</script>

{#if open}
	<!-- Backdrop -->
	<button
		type="button"
		class="fixed inset-0 bg-black/80 backdrop-blur-sm z-50 transition-opacity duration-200 w-full h-full"
		onclick={close}
		aria-label="Close command palette"
		tabindex="-1"
	/>

	<!-- Command Palette Modal -->
	<div 
		class="fixed left-1/2 top-[20vh] w-[90vw] sm:w-[600px] -translate-x-1/2 z-50"
		role="dialog"
		aria-modal="true"
		aria-label="Command palette"
	>
		<!-- Animated container -->
		<div class="animate-in fade-in zoom-in-95 duration-200">
			<!-- Main card with shadow and border -->
			<div class="overflow-hidden rounded-xl bg-white dark:bg-zinc-900 shadow-2xl border border-zinc-200 dark:border-zinc-800">
				<!-- Search Header -->
				<div class="flex items-center gap-3 px-4 py-3 border-b border-zinc-200 dark:border-zinc-800">
					<Search class="h-5 w-5 text-zinc-400" />
					<input
						bind:value={query}
						placeholder="Type a command or search..."
						class="flex-1 bg-transparent border-0 outline-none text-zinc-900 dark:text-zinc-100 placeholder-zinc-400 text-sm"
						onkeydown={handleKeyDown}
						autoFocus
						autoComplete="off"
						autoCapitalize="off"
						spellCheck="false"
					/>
					{#if query}
						<button
							onclick={() => query = ''}
							class="p-1 hover:bg-zinc-100 dark:hover:bg-zinc-800 rounded transition-colors"
							aria-label="Clear search"
						>
							<X class="h-4 w-4 text-zinc-400" />
						</button>
					{/if}
					<kbd class="hidden sm:inline-flex h-6 items-center gap-1 rounded border border-zinc-200 dark:border-zinc-700 bg-zinc-50 dark:bg-zinc-800 px-2 font-mono text-xs font-medium text-zinc-500">
						ESC
					</kbd>
				</div>

				<!-- Results List -->
				<div class="max-h-[420px] overflow-y-auto p-2">
					{#if items.length === 0}
						<div class="flex flex-col items-center justify-center py-12 text-center">
							<div class="h-12 w-12 rounded-full bg-zinc-100 dark:bg-zinc-800 flex items-center justify-center mb-3">
								<Search class="h-6 w-6 text-zinc-400" />
							</div>
							<p class="text-zinc-900 dark:text-zinc-100 font-medium text-sm">No results found</p>
							<p class="text-zinc-500 text-xs mt-1">Try searching for a different command</p>
						</div>
					{:else}
						{#each Object.entries(groupBySection(items)) as [section, sectionItems]}
							<div class="mb-3 last:mb-0">
								<!-- Section Header -->
								<div class="flex items-center gap-2 px-3 py-2">
									<span class="text-xs font-semibold text-zinc-500 uppercase tracking-wider">
										{section}
									</span>
									<div class="flex-1 h-px bg-zinc-200 dark:bg-zinc-800"></div>
								</div>

								<!-- Commands -->
								{#each sectionItems as item}
									<button
										class="w-full flex items-center justify-between px-3 py-2.5 rounded-lg transition-all duration-150 group
											{selectedId === item.id 
												? 'bg-zinc-900 dark:bg-zinc-100 text-white dark:text-zinc-900' 
												: 'hover:bg-zinc-100 dark:hover:bg-zinc-800 text-zinc-700 dark:text-zinc-300'}"
										onclick={() => selectItem(item)}
										onmouseenter={() => selectedId = item.id}
										role="option"
										aria-selected={selectedId === item.id}
									>
										<div class="flex items-center gap-3">
											{#if item.icon}
												<item.icon 
													class="h-4 w-4 {selectedId === item.id ? 'text-white dark:text-zinc-900' : 'text-zinc-400 group-hover:text-zinc-600 dark:group-hover:text-zinc-300'} transition-colors" 
												/>
											{/if}
											<span class="text-sm font-medium">{item.label}</span>
										</div>
										{#if item.shortcuts}
											<div class="flex items-center gap-1.5">
												{#each item.shortcuts as shortcut}
													<kbd 
														class="hidden sm:inline-flex h-5 items-center justify-center rounded-md bg-zinc-100 dark:bg-zinc-800 px-1.5 
															{selectedId === item.id 
																? 'text-zinc-600 dark:text-zinc-300' 
																: 'text-zinc-500'} 
															text-xs font-mono font-medium transition-colors"
													>
														{shortcut}
													</kbd>
												{/each}
											</div>
										{/if}
									</button>
								{/each}
							</div>
						{/each}
					{/if}
				</div>

				<!-- Footer with hints -->
				<div class="flex items-center justify-between px-4 py-2.5 border-t border-zinc-200 dark:border-zinc-800 bg-zinc-50 dark:bg-zinc-900/50">
					<div class="flex items-center gap-4">
						<span class="flex items-center gap-1.5 text-xs text-zinc-500">
							<div class="flex items-center gap-0.5">
								<ArrowUp class="h-3 w-3" />
								<ArrowDown class="h-3 w-3" />
							</div>
							Navigate
						</span>
						<span class="flex items-center gap-1.5 text-xs text-zinc-500">
							<CornerDownRight class="h-3 w-3" />
							Select
						</span>
					</div>
					<div class="flex items-center gap-2 text-xs text-zinc-400">
						<span>Survey Engine</span>
						<span>•</span>
						<span>Cmd+K</span>
					</div>
				</div>
			</div>
		</div>
	</div>
{/if}
