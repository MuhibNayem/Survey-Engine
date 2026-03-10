<script lang="ts">
	import { Accordion } from "bits-ui";
	import { cn } from "$lib/utils";
	import type { Snippet } from "svelte";

	interface Props {
		type?: "single" | "multiple";
		value?: string | string[];
		onValueChange?: (value: string | string[]) => void;
		collapsible?: boolean;
		class?: string;
		children?: Snippet;
	}

	let {
		type = "single",
		value,
		onValueChange,
		collapsible = false,
		class: className,
		children
	}: Props = $props();
</script>

<Accordion.Root
	{type}
	{value}
	{onValueChange}
	{collapsible}
	class={cn("w-full", className)}
>
	{@render children?.()}
</Accordion.Root>
