<script lang="ts">
	import { Button } from '$lib/components/ui/button';
	import * as Card from '$lib/components/ui/card';
	import type { Component, Snippet } from 'svelte';
	import { Megaphone, HelpCircle, FolderKanban, FileText, MessageSquareText, ChartBar } from 'lucide-svelte';

	interface Props {
		icon?: Component<{ class?: string; 'aria-hidden'?: boolean }>;
		title: string;
		description?: string;
		actionLabel?: string;
		onAction?: () => void;
		secondaryActionLabel?: string;
		onSecondaryAction?: () => void;
		illustration?: 'campaign' | 'survey' | 'response' | 'question' | 'category' | 'default';
		children?: Snippet;
	}

	let {
		icon: Icon,
		title,
		description,
		actionLabel,
		onAction,
		secondaryActionLabel,
		onSecondaryAction,
		illustration = 'default',
		children
	}: Props = $props();

	// Map illustration types to default icons
	const iconMap = {
		campaign: Megaphone,
		survey: FileText,
		response: MessageSquareText,
		question: HelpCircle,
		category: FolderKanban,
		default: ChartBar
	};

	// Use provided icon or get default from illustration type
	const DisplayIcon = $derived(Icon || iconMap[illustration]);
</script>

<Card.Root
	class="flex flex-col items-center justify-center text-center"
>
	<Card.Content class="flex flex-col items-center justify-center py-8 px-4">
		<!-- Illustration Container -->
		<div class="mb-6 relative">
			<div
				class="absolute inset-0 bg-gradient-to-br from-primary/20 to-purple-500/20 rounded-full blur-2xl"
			></div>
			<div
				class="relative flex h-24 w-24 items-center justify-center rounded-2xl bg-gradient-to-br from-primary/10 to-purple-500/10 border border-primary/20"
			>
				{#if DisplayIcon}
					<DisplayIcon class="h-12 w-12 text-primary" />
				{/if}
			</div>
		</div>

		<!-- Title -->
		<h3 class="text-lg font-semibold text-foreground">{title}</h3>

		<!-- Description -->
		{#if description}
			<p class="mt-2 text-sm text-muted-foreground max-w-md">
				{description}
			</p>
		{/if}

		<!-- Actions -->
		{#if actionLabel}
			<div class="mt-6 flex gap-3">
				<Button onclick={onAction}>
					{actionLabel}
				</Button>
				{#if secondaryActionLabel}
					<Button variant="outline" onclick={onSecondaryAction}>
						{secondaryActionLabel}
					</Button>
				{/if}
			</div>
		{/if}

		<!-- Children Slot -->
		{#if children}
			<div class="mt-6">
				{@render children()}
			</div>
		{/if}
	</Card.Content>
</Card.Root>
