<script lang="ts">
	import { page } from '$app/state';
	import FeatureAnalyticsView from '$lib/components/admin/FeatureAnalyticsView.svelte';

	let featureKey = $derived(page.params.featureKey ?? '');
</script>

<FeatureAnalyticsView featureKey={featureKey} />
