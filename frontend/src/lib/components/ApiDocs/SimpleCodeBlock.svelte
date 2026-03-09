<script lang="ts">
	import { Check, Copy } from 'lucide-svelte';

	interface Props {
		code: string;
		language?: 'typescript' | 'javascript' | 'json' | 'bash' | 'http';
		filename?: string;
	}

	let { code, language = 'typescript', filename }: Props = $props();
	let copied = $state(false);

	function copyCode() {
		navigator.clipboard.writeText(code);
		copied = true;
		setTimeout(() => (copied = false), 2000);
	}

	const getLanguageLabel = (lang: string) => {
		const labels: Record<string, string> = {
			typescript: 'TypeScript',
			javascript: 'JavaScript',
			json: 'JSON',
			bash: 'Bash',
			http: 'HTTP'
		};
		return labels[lang] || lang;
	};
</script>

<div class="group relative rounded-lg overflow-hidden border border-border bg-[#0d1117]">
	{#if filename || language}
		<div class="flex items-center justify-between px-4 py-2 bg-[#161b22] border-b border-border">
			<div class="flex items-center gap-2">
				{#if filename}
					<span class="text-xs text-muted-foreground font-mono">{filename}</span>
				{/if}
				{#if language}
					<span class="text-xs px-2 py-0.5 rounded-full bg-blue-500/10 text-blue-400">
						{getLanguageLabel(language)}
					</span>
				{/if}
			</div>
			<button
				type="button"
				onclick={copyCode}
				class="flex items-center gap-1.5 text-xs text-muted-foreground hover:text-foreground transition-colors"
			>
				{#if copied}
					<Check class="w-3.5 h-3.5" />
					<span>Copied!</span>
				{:else}
					<Copy class="w-3.5 h-3.5" />
					<span>Copy</span>
				{/if}
			</button>
		</div>
	{/if}

	<div class="overflow-x-auto p-4">
		<pre class="text-sm font-mono leading-relaxed text-slate-100"><code>{code}</code></pre>
	</div>
</div>
