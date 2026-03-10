<script lang="ts">
	import { Accordion } from "bits-ui";
	import { cn } from "$lib/utils";
	import { ChevronDown } from "lucide-svelte";
	import type { Snippet } from "svelte";

	interface Props {
		class?: string;
		children?: Snippet;
	}

	let { class: className, children }: Props = $props();
</script>

<Accordion.Header class="flex">
	<Accordion.Trigger
		class={cn(
			"flex flex-1 items-center justify-between py-4 font-medium transition-all hover:underline [&[data-state=open]>svg]:rotate-180",
			className
		)}
	>
		{@render children?.()}
		<ChevronDown
			class="h-4 w-4 shrink-0 transition-transform duration-200"
			aria-hidden="true"
		/>
	</Accordion.Trigger>
</Accordion.Header>
