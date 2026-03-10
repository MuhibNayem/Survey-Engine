<script lang="ts">
	import { Accordion } from "bits-ui";
	import { cn } from "$lib/utils";
	import type { Snippet } from "svelte";

	interface Props {
		value: string;
		class?: string;
		children?: Snippet;
	}

	let { value, class: className, children }: Props = $props();
</script>

<Accordion.Item
	{value}
	class={cn("border-b", className)}
>
	{@render children?.()}
</Accordion.Item>
