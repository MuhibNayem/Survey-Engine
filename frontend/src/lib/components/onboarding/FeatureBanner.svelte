<script lang="ts">
	import { onMount } from 'svelte';
	import { Button } from '$lib/components/ui/button';
	import * as Alert from '$lib/components/ui/alert';
	import { X, Megaphone, ExternalLink, CheckCircle } from 'lucide-svelte';
	import { useFeatureFlag } from '$lib/hooks/useFeatureFlag.svelte';

	export interface BannerConfig {
		id: string;
		title: string;
		content: string;
		priority?: 'low' | 'medium' | 'high' | 'urgent';
		variant?: 'default' | 'destructive' | 'success' | 'warning' | 'info';
		ctaText?: string;
		ctaUrl?: string;
		dismissible?: boolean;
		startDate?: string;
		endDate?: string;
	}

	interface Props {
		banner: BannerConfig;
		position?: 'top' | 'bottom';
		onDismiss?: () => void;
		onCtaClick?: () => void;
	}

	let {
		banner,
		position = 'top',
		onDismiss,
		onCtaClick
	}: Props = $props();

	let visible = $state(false);
	let loading = $state(false);

	// Use feature flag hook for backend integration
	const {
		status,
		isCompleted,
		complete: completeFeature
	} = useFeatureFlag(() => `announcement.${banner.id}`, {
		autoCheck: true,
		autoRecordAccess: true
	});

	onMount(async () => {
		// Wait for status to load
		await new Promise(resolve => setTimeout(resolve, 100));

		// Check if banner should be shown
		if (!status?.shouldShow || isCompleted) {
			return;
		}

		// Check date range
		const now = new Date();
		if (banner.startDate) {
			const startDate = new Date(banner.startDate);
			if (now < startDate) {
				return;
			}
		}
		if (banner.endDate) {
			const endDate = new Date(banner.endDate);
			if (now > endDate) {
				return;
			}
		}

		visible = true;
	});

	async function dismiss(): Promise<void> {
		if (!banner.dismissible) return;

		visible = false;
		loading = true;

		try {
			await completeFeature();
		} catch (error) {
			console.error('Failed to dismiss banner:', error);
		} finally {
			loading = false;
		}

		onDismiss?.();
	}

	function handleCtaClick(): void {
		if (banner.ctaUrl) {
			window.open(banner.ctaUrl, '_blank');
		}
		onCtaClick?.();
	}

	function getVariantClasses(): string {
		switch (banner.variant) {
			case 'destructive':
				return 'bg-destructive/10 border-destructive/30';
			case 'success':
				return 'bg-green-500/10 border-green-500/30';
			case 'warning':
				return 'bg-yellow-500/10 border-yellow-500/30';
			case 'info':
				return 'bg-blue-500/10 border-blue-500/30';
			default:
				return 'bg-primary/5 border-primary/20';
		}
	}

	function getPriorityIcon(): any {
		switch (banner.priority) {
			case 'urgent':
				return '🔴';
			case 'high':
				return '🟠';
			case 'medium':
				return '🟡';
			default:
				return Megaphone;
		}
	}
</script>

{#if visible}
	<div
		class="fixed z-50 left-0 right-0 {position === 'top' ? 'top-0' : 'bottom-0'}"
		role="alert"
	>
		<Alert.Root class="m-0 rounded-none border-b {getVariantClasses()}">
			<div class="flex items-start justify-between gap-4 w-full">
				<div class="flex items-start gap-3 flex-1">
					<!-- Priority Indicator -->
					<div class="flex h-8 w-8 shrink-0 items-center justify-center rounded-full bg-background/50">
						{#if typeof getPriorityIcon() === 'string'}
							<span class="text-sm">{getPriorityIcon()}</span>
						{:else}
							<svelte:component this={getPriorityIcon()} class="h-4 w-4" />
						{/if}
					</div>

					<!-- Content -->
					<div class="flex-1 space-y-1">
						<Alert.Title class="flex items-center gap-2 text-foreground">
							{banner.title}
						</Alert.Title>
						<Alert.Description class="text-muted-foreground">
							{banner.content}
						</Alert.Description>

						<!-- CTA Button -->
						{#if banner.ctaText}
							<div class="pt-2">
								<Button
									size="sm"
									variant={banner.variant === 'destructive' ? 'destructive' : 'default'}
									onclick={handleCtaClick}
									class="gap-2"
								>
									{banner.ctaText}
									<ExternalLink class="h-3.5 w-3.5" />
								</Button>
							</div>
						{/if}
					</div>
				</div>

				<!-- Dismiss Button -->
				{#if banner.dismissible}
					<Button
						variant="ghost"
						size="icon"
						class="h-8 w-8 shrink-0 -mt-1 -mr-1"
						onclick={dismiss}
						disabled={loading}
					>
						<X class="h-4 w-4" />
					</Button>
				{/if}
			</div>
		</Alert.Root>
	</div>

	<!-- Spacer to prevent content from being hidden behind banner -->
	<div class="h-16"></div>
{/if}
