<script lang="ts">
	import { Check, Copy, ChevronRight, ChevronDown } from 'lucide-svelte';

	interface Prop {
		name: string;
		type: string;
		required?: boolean;
		description?: string;
		defaultValue?: string;
		children?: Prop[];
	}

	interface Props {
		endpoint: string;
		method: 'GET' | 'POST' | 'PUT' | 'DELETE' | 'PATCH';
		description?: string;
		requestBody?: Prop[];
		responseExample?: string;
		pathParams?: Prop[];
		queryParams?: Prop[];
		headers?: Prop[];
		collapsed?: boolean;
	}

	let {
		endpoint,
		method,
		description,
		requestBody,
		responseExample,
		pathParams,
		queryParams,
		headers,
		collapsed = false
	}: Props = $props();

	let isExpanded = $state(true);
	let copied = $state(false);

	type LifecycleSection = {
		title: string;
		content: string;
	};

	$effect(() => {
		isExpanded = !collapsed;
	});

	const methodColors: Record<string, string> = {
		GET: 'bg-green-500/10 text-green-400 border-green-500/20',
		POST: 'bg-blue-500/10 text-blue-400 border-blue-500/20',
		PUT: 'bg-yellow-500/10 text-yellow-400 border-yellow-500/20',
		DELETE: 'bg-red-500/10 text-red-400 border-red-500/20',
		PATCH: 'bg-orange-500/10 text-orange-400 border-orange-500/20'
	};

	function copyEndpoint() {
		navigator.clipboard.writeText(`${method} ${endpoint}`);
		copied = true;
		setTimeout(() => (copied = false), 2000);
	}

	function toggleExpand() {
		isExpanded = !isExpanded;
	}

	function normalizeWhitespace(input: string): string {
		return input.replace(/\s+/g, ' ').trim();
	}

	function extractSection(
		text: string,
		startMarker: RegExp,
		nextMarkers: RegExp[]
	): { value: string; index: number } {
		const start = startMarker.exec(text);
		if (!start || start.index < 0) return { value: '', index: -1 };

		const startIndex = start.index + start[0].length;
		let endIndex = text.length;

		for (const marker of nextMarkers) {
			const next = marker.exec(text.slice(startIndex));
			if (next && next.index >= 0) {
				endIndex = Math.min(endIndex, startIndex + next.index);
			}
		}

		return {
			value: text.slice(startIndex, endIndex).trim().replace(/\s*[.;]\s*$/, ''),
			index: start.index
		};
	}

	function parseLifecycleDescription(input?: string): LifecycleSection[] {
		if (!input) return [];
		const text = normalizeWhitespace(input);

		const whyMarker = /why this endpoint is needed\s*:/i;
		const whatMarker = /what this endpoint does\s*:/i;
		const howMarker = /how this endpoint does it\s*:/i;

		const why = extractSection(text, whyMarker, [whatMarker, howMarker]);
		const what = extractSection(text, whatMarker, [howMarker]);
		const how = extractSection(text, howMarker, []);

		if (!why.value && !what.value && !how.value) return [];

		const sections: LifecycleSection[] = [];
		if (why.value) sections.push({ title: 'Why', content: why.value });
		if (what.value) sections.push({ title: 'What', content: what.value });
		if (how.value) sections.push({ title: 'How', content: how.value });
		return sections;
	}

	const lifecycleSections = $derived(parseLifecycleDescription(description));
</script>

<div class="border border-border rounded-lg overflow-hidden bg-card">
	<div
		role="button"
		tabindex="0"
		onclick={toggleExpand}
		onkeydown={(e) => {
			if (e.key === 'Enter' || e.key === ' ') {
				e.preventDefault();
				toggleExpand();
			}
		}}
		class="w-full flex items-center gap-4 p-4 hover:bg-accent/50 transition-colors text-left"
	>
		<div class="flex items-center gap-2 flex-shrink-0">
			{#if isExpanded}
				<ChevronDown class="w-5 h-5 text-muted-foreground" />
			{:else}
				<ChevronRight class="w-5 h-5 text-muted-foreground" />
			{/if}
		</div>

		<span
			class="px-2.5 py-1 rounded-md text-xs font-semibold font-mono border {methodColors[method]}"
		>
			{method}
		</span>

		<code class="flex-1 text-sm font-mono text-foreground truncate">{endpoint}</code>

		<button
			type="button"
			onclick={(e) => {
				e.stopPropagation();
				copyEndpoint();
			}}
			class="flex items-center gap-1.5 text-xs text-muted-foreground hover:text-foreground transition-colors px-2 py-1 rounded hover:bg-accent"
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

	{#if isExpanded}
		<div class="border-t border-border">
			{#if description}
				<div class="p-4 border-b border-border">
					{#if lifecycleSections.length > 0}
						<div class="grid gap-3 md:grid-cols-3">
							{#each lifecycleSections as section}
								<div class="rounded-lg border border-border bg-background/40 p-3">
									<p class="text-[11px] font-semibold uppercase tracking-wide text-primary">{section.title}</p>
									<p class="mt-1.5 text-sm leading-relaxed text-muted-foreground">{section.content}</p>
								</div>
							{/each}
						</div>
					{:else}
						<p class="text-sm text-muted-foreground leading-relaxed">{description}</p>
					{/if}
				</div>
			{/if}

			{#if pathParams && pathParams.length > 0}
				<div class="p-4 border-b border-border">
					<h4 class="text-sm font-semibold mb-3">Path Parameters</h4>
					<div class="space-y-3">
						{#each pathParams as param}
							<div class="grid grid-cols-12 gap-2 text-sm">
								<div class="col-span-3">
									<code class="text-xs font-mono text-blue-400">{param.name}</code>
									{#if param.required}
										<span class="text-xs text-red-400 ml-1">required</span>
									{/if}
								</div>
								<div class="col-span-3">
									<span class="text-xs font-mono text-muted-foreground">{param.type}</span>
								</div>
								<div class="col-span-6">
									<p class="text-xs text-muted-foreground">{param.description}</p>
									{#if param.defaultValue}
										<p class="text-xs text-muted-foreground mt-1">
											Default: <code class="font-mono">{param.defaultValue}</code>
										</p>
									{/if}
								</div>
							</div>
						{/each}
					</div>
				</div>
			{/if}

			{#if queryParams && queryParams.length > 0}
				<div class="p-4 border-b border-border">
					<h4 class="text-sm font-semibold mb-3">Query Parameters</h4>
					<div class="space-y-3">
						{#each queryParams as param}
							<div class="grid grid-cols-12 gap-2 text-sm">
								<div class="col-span-3">
									<code class="text-xs font-mono text-blue-400">{param.name}</code>
									{#if param.required}
										<span class="text-xs text-red-400 ml-1">required</span>
									{/if}
								</div>
								<div class="col-span-3">
									<span class="text-xs font-mono text-muted-foreground">{param.type}</span>
								</div>
								<div class="col-span-6">
									<p class="text-xs text-muted-foreground">{param.description}</p>
									{#if param.defaultValue}
										<p class="text-xs text-muted-foreground mt-1">
											Default: <code class="font-mono">{param.defaultValue}</code>
										</p>
									{/if}
								</div>
							</div>
						{/each}
					</div>
				</div>
			{/if}

			{#if requestBody && requestBody.length > 0}
				<div class="p-4 border-b border-border">
					<h4 class="text-sm font-semibold mb-3">Request Body</h4>
					<div class="space-y-3">
						{#each requestBody as param}
							<div class="grid grid-cols-12 gap-2 text-sm">
								<div class="col-span-3">
									<code class="text-xs font-mono text-blue-400">{param.name}</code>
									{#if param.required}
										<span class="text-xs text-red-400 ml-1">required</span>
									{/if}
								</div>
								<div class="col-span-3">
									<span class="text-xs font-mono text-muted-foreground">{param.type}</span>
								</div>
								<div class="col-span-6">
									<p class="text-xs text-muted-foreground">{param.description}</p>
									{#if param.defaultValue}
										<p class="text-xs text-muted-foreground mt-1">
											Default: <code class="font-mono">{param.defaultValue}</code>
										</p>
									{/if}
								</div>
							</div>
						{/each}
					</div>
				</div>
			{/if}

			{#if responseExample}
				<div class="p-4">
					<h4 class="text-sm font-semibold mb-3">Response Example</h4>
					<div class="rounded-lg overflow-hidden border border-border bg-[#0d1117]">
						<pre class="p-4 overflow-x-auto text-sm font-mono text-foreground"><code>{responseExample}</code></pre>
					</div>
				</div>
			{/if}
		</div>
	{/if}
</div>
