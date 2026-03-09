<script lang="ts">
	interface Props {
		code: string;
		language?: 'typescript' | 'javascript' | 'json' | 'bash' | 'http' | 'curl';
		showLineNumbers?: boolean;
		copyable?: boolean;
		filename?: string;
	}

	let {
		code,
		language = 'typescript',
		showLineNumbers = false,
		copyable = true,
		filename
	}: Props = $props();

	let copied = $state(false);

	function copyCode() {
		navigator.clipboard.writeText(code);
		copied = true;
		setTimeout(() => (copied = false), 2000);
	}
</script>

<div class="relative rounded-lg overflow-hidden border border-border bg-[#0d1117]">
	{#if filename}
		<div class="flex items-center justify-between px-4 py-2 bg-[#161b22] border-b border-border">
			<span class="text-xs text-muted-foreground font-mono">{filename}</span>
			{#if copyable}
				<button
					type="button"
					onclick={copyCode}
					class="text-xs text-muted-foreground hover:text-foreground transition-colors"
				>
					{#if copied}
						Copied!
					{:else}
						Copy
					{/if}
				</button>
			{/if}
		</div>
	{:else if copyable}
		<button
			type="button"
			onclick={copyCode}
			class="absolute top-2 right-2 text-xs text-muted-foreground hover:text-foreground transition-colors px-2 py-1 rounded bg-[#161b22]/80"
		>
			{#if copied}
				Copied!
			{:else}
				Copy
			{/if}
		</button>
	{/if}

	<div class="overflow-x-auto">
		<pre class="p-4 text-sm font-mono text-slate-100"><code>{code}</code></pre>
	</div>

	{#if showLineNumbers}
		<div class="absolute left-0 top-0 bottom-0 w-12 bg-[#0d1117] border-r border-border text-right pr-2 pt-4 text-xs text-muted-foreground select-none">
			{#each code.split('\n').map((_, i) => i + 1) as line}
				<div class="leading-6">{line}</div>
			{/each}
		</div>
	{/if}
</div>
