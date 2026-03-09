<script lang="ts">
	import { page } from '$app/state';
	import { ChevronRight, ChevronDown } from 'lucide-svelte';

	interface NavItem {
		title: string;
		href?: string;
		children?: NavItem[];
		icon?: string;
	}

	interface Props {
		items: NavItem[];
	}

	let { items }: Props = $props();
	let openSections = $state<Set<string>>(new Set(['api reference', 'sdk']));

	function toggleSection(id: string) {
		if (openSections.has(id)) {
			openSections.delete(id);
		} else {
			openSections.add(id);
		}
		openSections = new Set(openSections);
	}

	function isActive(href?: string): boolean {
		if (!href) return false;
		return page.url.pathname === href;
	}

	function hasActiveChild(children?: NavItem[]): boolean {
		if (!children) return false;
		return children.some((child) => isActive(child.href) || hasActiveChild(child.children));
	}
</script>

<nav class="space-y-1">
	{#each items as section (section.title)}
		<div class="mb-5">
			{#if section.children}
				<button
					type="button"
					onclick={() => toggleSection(section.title.toLowerCase())}
					class="w-full flex items-center gap-2 text-left mb-2 rounded-lg px-2 py-1.5 hover:bg-accent/50"
				>
					{#if openSections.has(section.title.toLowerCase())}
						<ChevronDown class="w-4 h-4 text-muted-foreground" />
					{:else}
						<ChevronRight class="w-4 h-4 text-muted-foreground" />
					{/if}
					<span class="text-[11px] font-semibold text-muted-foreground uppercase tracking-[0.1em]">
						{section.title}
					</span>
				</button>

				{#if openSections.has(section.title.toLowerCase())}
					<div class="ml-3 space-y-1.5">
						{#each section.children as item}
							{#if item.children}
								<div class="mt-2">
									<button
										type="button"
										onclick={() => toggleSection(item.title.toLowerCase())}
										class="w-full flex items-center gap-2 text-left py-1.5 px-2 rounded-lg hover:bg-accent/60 transition-colors"
									>
										{#if openSections.has(item.title.toLowerCase())}
											<ChevronDown class="w-3.5 h-3.5 text-muted-foreground" />
										{:else}
											<ChevronRight class="w-3.5 h-3.5 text-muted-foreground" />
										{/if}
										<span class="text-sm text-foreground">{item.title}</span>
									</button>
										{#if openSections.has(item.title.toLowerCase())}
											<div class="ml-4 mt-1 space-y-1.5">
											{#each item.children as subItem}
													<a
														href={subItem.href}
														class="block py-1.5 px-2 rounded-lg text-sm transition-colors {isActive(subItem.href)
															? 'bg-sky-500/10 text-sky-700 font-medium dark:text-sky-300'
															: 'text-muted-foreground hover:text-foreground hover:bg-accent/50'}"
													>
														{subItem.title}
													</a>
												{/each}
										</div>
									{/if}
								</div>
							{:else}
								<a
									href={item.href}
									class="block py-1.5 px-2 rounded-lg text-sm transition-colors {isActive(item.href)
										? 'bg-sky-500/10 text-sky-700 font-medium dark:text-sky-300'
										: 'text-muted-foreground hover:text-foreground hover:bg-accent/50'}"
								>
									{item.title}
								</a>
							{/if}
						{/each}
					</div>
				{/if}
			{:else}
				<a
					href={section.href}
					class="block py-1.5 px-2 rounded-lg text-sm transition-colors -mx-1 {isActive(section.href)
						? 'bg-sky-500/10 text-sky-700 font-medium dark:text-sky-300'
						: 'text-muted-foreground hover:text-foreground hover:bg-accent/50'}"
				>
					{section.title}
				</a>
			{/if}
		</div>
	{/each}
</nav>
