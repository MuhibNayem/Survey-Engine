/**
 * EditFeatureDialog - Reuses CreateFeatureDialog with feature prop
 *
 * This component provides a consistent editing experience by delegating
 * to CreateFeatureDialog which now contains the full metadata editor.
 */
<script lang="ts">
	import CreateFeatureDialog from './CreateFeatureDialog.svelte';

	interface Feature {
		id?: string;
		featureKey?: string;
		name?: string;
		featureType?: string;
		category?: string;
		enabled?: boolean;
		rolloutPercentage?: number;
		minPlan?: string;
		roles?: string[];
		platforms?: string[];
		description?: string;
		metadata?: Record<string, unknown>;
	}

	interface Props {
		feature: Feature;
		onClose: () => void;
		onSave: () => void;
	}

	export let feature: Feature;
	export let onClose: () => void;
	export let onSave: () => void;
</script>

<CreateFeatureDialog {feature} {onClose} {onSave} />
