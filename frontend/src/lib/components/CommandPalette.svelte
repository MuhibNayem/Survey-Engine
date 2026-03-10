<script lang="ts">
	import { onMount, onDestroy, tick } from 'svelte';
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
	import api from '$lib/api';

	export interface CommandItem {
		id: string;
		label: string;
		icon?: typeof Search;
		shortcuts?: string[];
		action: () => void;
		section?: string;
		snippet?: string;
		entityType?: string;
		remote?: boolean;
	}

	interface Props {
		open?: boolean;
		onOpenChange?: (open: boolean) => void;
	}

	let { open = $bindable(false), onOpenChange }: Props = $props();

	let query = $state('');
	let selectedId = $state<string | null>(null);
	let isClosing = $state(false);
	let remoteCommands = $state<CommandItem[]>([]);
	let searchingRemote = $state(false);
	let remoteError = $state<string | null>(null);
	let searchInputEl = $state<HTMLInputElement | null>(null);

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

	function getLocalFilteredItems(): CommandItem[] {
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

	function toRemoteCommand(item: any): CommandItem {
		const iconByType: Record<string, typeof Search> = {
			campaign: Mail,
			survey: FileText,
			question: HelpCircle,
			category: FileText
		};
		return {
			id: `remote-${item.type}-${item.id}`,
			label: item.title,
			icon: iconByType[item.type] ?? Search,
			section: 'Search Results',
			entityType: item.type,
			snippet: item.snippet,
			remote: true,
			action: () => goto(item.route)
		};
	}

	function mergeItems(local: CommandItem[], remote: CommandItem[]): CommandItem[] {
		if (!query.trim()) return local;
		const seen = new Set(local.map((i) => i.id));
		const dedupedRemote = remote.filter((i) => !seen.has(i.id));
		return [...dedupedRemote, ...local];
	}

	function splitHighlighted(text: string): Array<{ text: string; marked: boolean }> {
		if (!text) return [];
		const parts: Array<{ text: string; marked: boolean }> = [];
		let cursor = 0;
		while (cursor < text.length) {
			const start = text.indexOf('[[[', cursor);
			if (start < 0) {
				parts.push({ text: text.slice(cursor), marked: false });
				break;
			}
			if (start > cursor) {
				parts.push({ text: text.slice(cursor, start), marked: false });
			}
			const end = text.indexOf(']]]', start + 3);
			if (end < 0) {
				parts.push({ text: text.slice(start + 3), marked: true });
				break;
			}
			parts.push({ text: text.slice(start + 3, end), marked: true });
			cursor = end + 3;
		}
		return parts.filter((p) => p.text.length > 0);
	}

	$effect(() => {
		const q = query.trim();
		remoteError = null;
		if (q.length < 2) {
			remoteCommands = [];
			searchingRemote = false;
			return;
		}

		searchingRemote = true;
		const timeout = setTimeout(async () => {
			try {
				const response = await api.get('/search/global', {
					params: { q, limit: 12 }
				});
				const items = response?.data?.items ?? [];
				remoteCommands = items.map(toRemoteCommand);
			} catch (error) {
				console.error('Global search failed', error);
				remoteCommands = [];
				remoteError = 'Search is temporarily unavailable';
			} finally {
				searchingRemote = false;
			}
		}, 180);

		return () => clearTimeout(timeout);
	});

	let items = $derived(mergeItems(getLocalFilteredItems(), remoteCommands));

	$effect(() => {
		selectedId = items[0]?.id ?? null;
	});

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
			void focusSearchInput();
		}
	}

	async function focusSearchInput() {
		await tick();
		requestAnimationFrame(() => {
			searchInputEl?.focus();
		});
	}

	$effect(() => {
		if (open) {
			void focusSearchInput();
		}
	});

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
	></button>

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
				<div class="overflow-hidden rounded-2xl bg-white/95 dark:bg-zinc-900/95 shadow-2xl ring-1 ring-zinc-200/80 dark:ring-zinc-800/90 backdrop-blur-xl">
					<!-- Search Header -->
					<div class="border-b border-zinc-200/70 dark:border-zinc-800/80 bg-gradient-to-r from-zinc-50/80 via-white to-zinc-50/80 dark:from-zinc-900 dark:via-zinc-900 dark:to-zinc-950 px-4 py-3.5">
						<div class="group flex items-center gap-2 rounded-xl bg-zinc-100/85 dark:bg-zinc-800/60 px-3 py-2.5 shadow-sm ring-1 ring-zinc-200/70 dark:ring-zinc-700/70 transition-all focus-within:ring-primary/45 focus-within:shadow-[0_0_0_3px_rgba(59,130,246,0.16)]">
							<div class="flex h-7 w-7 items-center justify-center rounded-md bg-zinc-100 dark:bg-zinc-800">
								<Search class="h-4 w-4 text-zinc-500 dark:text-zinc-300" />
							</div>
								<input
									bind:this={searchInputEl}
									bind:value={query}
									placeholder="Search commands, campaigns, surveys, questions..."
								class="cmd-search-input flex-1 bg-transparent text-zinc-900 dark:text-zinc-100 placeholder-zinc-400 text-sm font-medium caret-primary"
									onkeydown={handleKeyDown}
								autocomplete="off"
								autocapitalize="off"
								spellcheck="false"
							/>
							{#if query}
								<button
									onclick={() => query = ''}
									class="p-1.5 hover:bg-zinc-100 dark:hover:bg-zinc-800 rounded-md transition-colors"
									aria-label="Clear search"
								>
									<X class="h-3.5 w-3.5 text-zinc-400" />
								</button>
							{/if}
								<kbd class="hidden sm:inline-flex h-6 items-center gap-1 rounded-md bg-white/70 dark:bg-zinc-900/80 px-2 font-mono text-[10px] font-semibold text-zinc-500 ring-1 ring-zinc-200/70 dark:ring-zinc-700/70">
								ESC
							</kbd>
						</div>
					</div>

				<!-- Results List -->
				<div class="cmd-results max-h-[420px] overflow-y-auto p-2">
					{#if searchingRemote}
						<div class="px-3 py-2 text-xs text-zinc-500">Searching everything...</div>
					{/if}
					{#if remoteError}
						<div class="px-3 py-2 text-xs text-amber-600 dark:text-amber-400">{remoteError}</div>
					{/if}
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
										<div class="flex min-w-0 items-center gap-3">
											{#if item.icon}
												<item.icon 
													class="h-4 w-4 {selectedId === item.id ? 'text-white dark:text-zinc-900' : 'text-zinc-400 group-hover:text-zinc-600 dark:group-hover:text-zinc-300'} transition-colors" 
												/>
											{/if}
											<div class="min-w-0 text-left">
												<div class="flex items-center gap-2">
													<span class="truncate text-sm font-medium">{item.label}</span>
													{#if item.remote && item.entityType}
														<span class="rounded-full border border-zinc-300 dark:border-zinc-700 px-1.5 py-0.5 text-[10px] uppercase tracking-wide text-zinc-500 dark:text-zinc-400">
															{item.entityType}
														</span>
													{/if}
												</div>
												{#if item.snippet}
													<p class="mt-0.5 max-w-[380px] text-xs text-zinc-500 dark:text-zinc-400">
														{#each splitHighlighted(item.snippet) as segment}
															{#if segment.marked}
																<mark class="bg-yellow-200/70 px-0.5 text-zinc-900 dark:bg-yellow-300/40 dark:text-zinc-100">{segment.text}</mark>
															{:else}
																{segment.text}
															{/if}
														{/each}
													</p>
												{/if}
											</div>
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

<style>
  /* ── Nuke every possible native-browser focus artefact on the search input ─── */
  :global(.cmd-search-input),
  :global(.cmd-search-input:focus),
  :global(.cmd-search-input:focus-visible),
  :global(.cmd-search-input:focus-within),
  :global(.cmd-search-input:active) {
    outline: none !important;
    outline-offset: 0 !important;
    box-shadow: none !important;
    border: none !important;
    -webkit-appearance: none;
    -moz-appearance: none;
    appearance: none;
  }

  /* Remove Firefox's inner focus blue border */
  :global(.cmd-search-input:-moz-focusring) {
    color: transparent;
    text-shadow: none;
  }

  /* Smooth, branded caret */
  :global(.cmd-search-input) {
    caret-color: hsl(var(--primary));
    caret-shape: bar;
  }

  /* Premium text selection highlight */
  :global(.cmd-search-input::selection) {
    background-color: hsl(var(--primary) / 0.25);
    color: inherit;
  }
  :global(.cmd-search-input::-moz-selection) {
    background-color: hsl(var(--primary) / 0.25);
    color: inherit;
  }

  /* Hide browser's native clear button (x) on webkit */
  :global(.cmd-search-input::-webkit-search-cancel-button),
  :global(.cmd-search-input::-webkit-search-decoration) {
    -webkit-appearance: none;
    display: none;
  }

  /* ── Result list smooth scroll ─────────────────────────────────────── */
  :global(.cmd-results) {
    scroll-behavior: smooth;
    scrollbar-width: thin;
    scrollbar-color: hsl(var(--border)) transparent;
  }
  :global(.cmd-results::-webkit-scrollbar) {
    width: 4px;
  }
  :global(.cmd-results::-webkit-scrollbar-thumb) {
    background: hsl(var(--border));
    border-radius: 4px;
  }
</style>
