<script lang="ts">
	import { page } from '$app/state';
	import TenantFeatureConfigurator from '$lib/components/admin/TenantFeatureConfigurator.svelte';

	let featureKey = $derived(page.params.featureKey ?? '');
</script>

<TenantFeatureConfigurator featureKey={featureKey} />
