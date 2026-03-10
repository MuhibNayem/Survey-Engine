<script lang="ts">
	import { page } from '$app/state';
	import api from '$lib/api';
	import FeatureTour, { type TourConfig } from './FeatureTour.svelte';
	import FeatureTooltip from './FeatureTooltip.svelte';
	import FeatureBanner, { type BannerConfig } from './FeatureBanner.svelte';

	type RuntimeFeature = {
		featureKey: string;
		name: string;
		description?: string;
		featureType: 'TOUR' | 'TOOLTIP' | 'BANNER' | 'ANNOUNCEMENT' | 'FEATURE_FLAG';
		metadata?: Record<string, any>;
	};

	let runtimeItems = $state<RuntimeFeature[]>([]);
	let loading = $state(false);
	let requestSeq = 0;

	async function loadRuntime(pathname: string) {
		const reqId = ++requestSeq;
		loading = true;
		try {
			const response = await api.get('/features/runtime', {
				params: {
					pagePath: pathname,
					platform: 'WEB',
					max: 20
				}
			});
			if (reqId !== requestSeq) return;
			runtimeItems = (response?.data?.items ?? []) as RuntimeFeature[];
		} catch (error) {
			if (reqId !== requestSeq) return;
			runtimeItems = [];
			console.error('Failed to load runtime features', error);
		} finally {
			if (reqId === requestSeq) {
				loading = false;
			}
		}
	}

	function toTour(item: RuntimeFeature): TourConfig | null {
		const stepsRaw = item.metadata?.steps;
		if (!Array.isArray(stepsRaw) || stepsRaw.length === 0) {
			return null;
		}
		const steps = stepsRaw
			.map((s: any, index: number) => ({
				id: s?.id ?? `step-${index + 1}`,
				title: s?.title ?? `Step ${index + 1}`,
				description: s?.description ?? '',
				content: s?.content,
				targetSelector: s?.targetSelector,
				placement: s?.placement ?? 'bottom'
			}))
			.filter((s: any) => !!s.targetSelector);

		if (steps.length === 0) return null;
		return {
			id: item.featureKey,
			title: item.metadata?.title ?? item.name,
			steps
		};
	}

	function tooltipIdFor(item: RuntimeFeature): string {
		const raw = item.metadata?.id ?? item.featureKey;
		return String(raw).replace(/[^a-zA-Z0-9_-]/g, '_');
	}

	const tours = $derived(
		runtimeItems
			.filter((i) => i.featureType === 'TOUR')
			.map((i) => toTour(i))
			.filter((i): i is TourConfig => i !== null)
	);

	const tooltips = $derived(
		runtimeItems.filter((i) =>
			i.featureType === 'TOOLTIP' &&
			typeof i.metadata?.targetSelector === 'string' &&
			typeof i.metadata?.content === 'string'
		)
	);

	const banners = $derived(
		runtimeItems.filter((i) => i.featureType === 'BANNER' || i.featureType === 'ANNOUNCEMENT')
	);

	$effect(() => {
		const pathname = page.url.pathname;
		const timeout = setTimeout(() => {
			void loadRuntime(pathname);
		}, 120);
		return () => clearTimeout(timeout);
	});
</script>

{#if !loading}
	{#if tours.length > 0}
		<FeatureTour tour={tours[0]} autoStart={true} />
	{/if}

	{#if banners.length > 0}
		{#key banners[0].featureKey}
			<FeatureBanner
				featureKey={banners[0].featureKey}
				banner={{
					id: banners[0].featureKey,
					title: banners[0].metadata?.title ?? banners[0].name,
					content: banners[0].metadata?.content ?? banners[0].description ?? '',
					priority: banners[0].metadata?.priority ?? 'medium',
					variant: banners[0].metadata?.variant ?? 'info',
					ctaText: banners[0].metadata?.ctaText,
					ctaUrl: banners[0].metadata?.ctaUrl,
					dismissible: banners[0].metadata?.dismissible ?? true,
					startDate: banners[0].metadata?.startAt,
					endDate: banners[0].metadata?.endAt
				} as BannerConfig}
			/>
		{/key}
	{/if}

	{#each tooltips as tooltip (tooltip.featureKey)}
		<FeatureTooltip
			featureKey={tooltip.featureKey}
			tooltipId={tooltipIdFor(tooltip)}
			title={tooltip.metadata?.title ?? tooltip.name}
			content={tooltip.metadata?.content ?? ''}
			targetSelector={tooltip.metadata?.targetSelector}
			placement={tooltip.metadata?.placement ?? 'top'}
			delay={tooltip.metadata?.delayMs ?? 600}
		/>
	{/each}
{/if}

