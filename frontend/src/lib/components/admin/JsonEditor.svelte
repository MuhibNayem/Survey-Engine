<script lang="ts">
	import { Button } from '$lib/components/ui/button';
	import { Textarea } from '$lib/components/ui/textarea';
	import { Badge } from '$lib/components/ui/badge';
	import { Alert, AlertDescription, AlertTitle } from '$lib/components/ui/alert';
	import { Code2, Eye, Pencil, Copy, Check, AlertTriangle, Info } from 'lucide-svelte';
	import { cn } from '$lib/utils';
	import { toast } from 'svelte-sonner';

	interface Props {
		/** JSON value to edit */
		value: Record<string, unknown>;
		/** Called when JSON changes */
		onChange?: (value: Record<string, unknown>) => void;
		/** Whether editor is disabled */
		disabled?: boolean;
		/** Custom class names */
		className?: string;
		/** Whether to show validation errors */
		showValidation?: boolean;
		/** Whether to show format/copy buttons */
		showActions?: boolean;
		/** Minimum height for textarea */
		minHeight?: string;
	}

	let {
		value,
		onChange,
		disabled = false,
		className,
		showValidation = true,
		showActions = true,
		minHeight = '300px'
	}: Props = $props();

	// Editor mode: 'edit' or 'preview'
	let mode = $state<'edit' | 'preview'>('edit');
	// Raw JSON string for editing
	let jsonString = $state('');
	// Validation errors
	let errors = $state<string[]>([]);
	// Whether JSON is valid
	let isValid = $state(true);
	// Whether copy was successful
	let copied = $state(false);

	// Initialize JSON string from value
	$effect(() => {
		try {
			jsonString = JSON.stringify(value, null, 2);
			errors = [];
			isValid = true;
		} catch (e) {
			errors = ['Failed to stringify JSON'];
			isValid = false;
		}
	});

	// Parse JSON and notify changes
	function parseAndNotify(): void {
		try {
			const parsed = JSON.parse(jsonString);
			if (typeof parsed !== 'object' || parsed === null || Array.isArray(parsed)) {
				throw new Error('JSON must be an object');
			}
			errors = [];
			isValid = true;
			onChange?.(parsed as Record<string, unknown>);
		} catch (e) {
			isValid = false;
			errors = [(e as Error).message];
		}
	}

	// Format JSON nicely
	function formatJson(): void {
		try {
			const parsed = JSON.parse(jsonString);
			jsonString = JSON.stringify(parsed, null, 2);
			parseAndNotify();
			toast.success('JSON formatted');
		} catch (e) {
			toast.error('Invalid JSON - cannot format');
		}
	}

	// Minify JSON
	function minifyJson(): void {
		try {
			const parsed = JSON.parse(jsonString);
			jsonString = JSON.stringify(parsed);
			parseAndNotify();
			toast.success('JSON minified');
		} catch (e) {
			toast.error('Invalid JSON - cannot minify');
		}
	}

	// Copy to clipboard
	async function copyToClipboard(): Promise<void> {
		try {
			await navigator.clipboard.writeText(jsonString);
			copied = true;
			toast.success('Copied to clipboard');
			setTimeout(() => (copied = false), 2000);
		} catch (e) {
			toast.error('Failed to copy');
		}
	}

	// Reset to original value
	function reset(): void {
		jsonString = JSON.stringify(value, null, 2);
		errors = [];
		isValid = true;
		toast.info('Reset to original value');
	}

	// Toggle between edit and preview mode
	function toggleMode(): void {
		if (mode === 'edit') {
			// Validate before switching to preview
			parseAndNotify();
			if (isValid) {
				mode = 'preview';
			}
		} else {
			mode = 'edit';
		}
	}

	// Handle textarea input
	function handleInput(): void {
		if (mode === 'edit') {
			parseAndNotify();
		}
	}

	// Keyboard shortcut for save (Ctrl+S)
	function handleKeydown(event: KeyboardEvent): void {
		if ((event.ctrlKey || event.metaKey) && event.key === 's') {
			event.preventDefault();
			formatJson();
		}
		if (event.key === 'Escape') {
			reset();
		}
	}
</script>

<div class={cn('flex flex-col gap-4', className)}>
	<!-- Header with actions -->
	{#if showActions}
		<div class="flex items-center justify-between">
			<div class="flex items-center gap-2">
				<Code2 class="h-4 w-4 text-muted-foreground" />
				<span class="text-sm font-medium">JSON Editor</span>
				{#if isValid}
					<Badge variant="secondary" class="bg-green-100 text-green-800 dark:bg-green-900 dark:text-green-300">
						Valid JSON
					</Badge>
				{:else}
					<Badge variant="secondary" class="bg-red-100 text-red-800 dark:bg-red-900 dark:text-red-300">
						Invalid JSON
					</Badge>
				{/if}
			</div>
			<div class="flex items-center gap-2">
				<Button variant="outline" size="sm" onclick={toggleMode} disabled={disabled}>
					{#if mode === 'edit'}
						<Eye class="h-3.5 w-3.5 mr-1.5" />
						Preview
					{:else}
						<Pencil class="h-3.5 w-3.5 mr-1.5" />
						Edit
					{/if}
				</Button>
				<Button variant="outline" size="sm" onclick={formatJson} disabled={disabled || mode !== 'edit'}>
					Format
				</Button>
				<Button variant="outline" size="sm" onclick={minifyJson} disabled={disabled || mode !== 'edit'}>
					Minify
				</Button>
				<Button variant="outline" size="sm" onclick={copyToClipboard} disabled={disabled}>
					{#if copied}
						<Check class="h-3.5 w-3.5 mr-1.5" />
						Copied
					{:else}
						<Copy class="h-3.5 w-3.5 mr-1.5" />
						Copy
					{/if}
				</Button>
				<Button variant="outline" size="sm" onclick={reset} disabled={disabled}>
					Reset
				</Button>
			</div>
		</div>
	{/if}

	<!-- Validation errors -->
	{#if showValidation && !isValid && errors.length > 0}
		<Alert variant="destructive">
			<AlertTriangle class="h-4 w-4" />
			<AlertTitle>Invalid JSON</AlertTitle>
			<AlertDescription class="flex flex-col gap-1">
				{#each errors as error}
					<span class="text-sm">{error}</span>
				{/each}
			</AlertDescription>
		</Alert>
	{/if}

	<!-- Info alert for advanced users -->
	{#if showValidation && isValid}
		<Alert class="bg-blue-50 dark:bg-blue-950 border-blue-200 dark:border-blue-800">
			<Info class="h-4 w-4 text-blue-600 dark:text-blue-400" />
			<AlertTitle class="text-blue-800 dark:text-blue-200">Advanced Mode</AlertTitle>
			<AlertDescription class="text-blue-700 dark:text-blue-300">
				Edit raw JSON directly. Use Ctrl+S to format, Escape to reset.
			</AlertDescription>
		</Alert>
	{/if}

	<!-- Editor/Preview area -->
	<div class="relative">
		{#if mode === 'edit'}
			<Textarea
				value={jsonString}
				oninput={handleInput}
				onkeydown={handleKeydown}
				disabled={disabled}
				class={cn(
					'font-mono text-xs leading-relaxed',
					!isValid && 'border-destructive focus-visible:ring-destructive'
				)}
				style="min-height: {minHeight}; resize: vertical;"
				aria-label="JSON editor"
				aria-invalid={!isValid}
				aria-describedby={!isValid ? 'json-error' : undefined}
			/>
		{:else}
			<pre
				class="p-4 rounded-md border bg-muted font-mono text-xs overflow-auto max-h-[600px]"
				style="min-height: {minHeight}"
			>
				<code>{jsonString}</code>
			</pre>
		{/if}
	</div>

	<!-- Hidden error element for aria-describedby -->
	{#if !isValid}
		<div id="json-error" class="sr-only">
			{errors.join('. ')}
		</div>
	{/if}
</div>
