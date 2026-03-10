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

{#if type === "single"}
	{@const Root = Accordion.Root as any}
	<!-- @ts-expect-error -->
	<Root
		type="single"
		value={value as string | undefined}
		onValueChange={onValueChange as any}
		{collapsible}
		class={cn("w-full", className)}
	>
		{@render children?.()}
	</Root>
{:else}
	{@const Root = Accordion.Root as any}
	<Root
		type="multiple"
		value={value as string[] | undefined}
		onValueChange={onValueChange as any}
		class={cn("w-full", className)}
	>
		{@render children?.()}
	</Root>
{/if}
