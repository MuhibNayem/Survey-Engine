<script lang="ts">
	import { Tabs } from "bits-ui";
	import { cn } from "$lib/utils";
	import type { Snippet } from "svelte";

	interface Props {
		value?: string;
		onValueChange?: (value: string) => void;
		orientation?: "horizontal" | "vertical";
		dir?: "ltr" | "rtl";
		activationMode?: "automatic" | "manual";
		class?: string;
		children?: Snippet;
	}

	let {
		value,
		onValueChange,
		orientation = "horizontal",
		dir = "ltr",
		activationMode = "automatic",
		class: className,
		children
	}: Props = $props();
</script>

<Tabs.Root
	{value}
	{onValueChange}
	{orientation}
	{dir}
	{activationMode}
	class={cn("w-full", className)}
>
	{@render children?.()}
</Tabs.Root>
