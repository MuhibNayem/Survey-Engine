<script lang="ts">
	import { Accordion } from "bits-ui";
	import { cn } from "$lib/utils";
	import type { Snippet } from "svelte";

	interface Props {
		class?: string;
		children?: Snippet;
	}

	let { class: className, children }: Props = $props();
</script>

<Accordion.Header
	class={cn("flex", className)}
>
	{@render children?.()}
</Accordion.Header>
