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

<Accordion.Content
	class={cn(
		"overflow-hidden text-sm transition-all data-[state=closed]:animate-accordion-up data-[state=open]:animate-accordion-down",
		className
	)}
>
	<div class="pb-4 pt-0">
		{@render children?.()}
	</div>
</Accordion.Content>
