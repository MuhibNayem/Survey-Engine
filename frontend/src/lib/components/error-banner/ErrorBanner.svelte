<script lang="ts">
	import type { Snippet } from 'svelte';

	interface Props {
		/** Whether to show the error banner */
		show?: boolean;
		/** Error title */
		title?: string;
		/** Error message/description */
		message?: string;
		/** Error type for styling */
		type?: 'error' | 'failure' | 'warning' | 'info';
		/** Duration in milliseconds (0 = no auto-dismiss) */
		duration?: number;
		/** Callback when dismissed */
		onDismiss?: () => void;
		/** Show retry button */
		showRetry?: boolean;
		/** Retry callback */
		onRetry?: () => void;
		/** Custom action label */
		actionLabel?: string;
		/** Custom action callback */
		onAction?: () => void;
		/** Children slot for custom content */
		children?: Snippet;
	}

	let {
		show = false,
		title = 'Oops! Something went wrong',
		message,
		type = 'error',
		duration = 0, // No auto-dismiss by default
		onDismiss,
		showRetry = false,
		onRetry,
		actionLabel,
		onAction,
		children
	}: Props = $props();

	let visible = $state(false);

	$effect(() => {
		if (show) {
			visible = true;
			
			// Auto-dismiss if duration is set
			if (duration > 0) {
				const timer = setTimeout(() => {
					visible = false;
					onDismiss?.();
				}, duration);
				
				return () => clearTimeout(timer);
			}
		} else {
			visible = false;
		}
	});

	// Type-based styling
	const typeStyles = {
		error: {
			bg: 'from-red-50 via-white to-red-50 dark:from-gray-900 dark:via-red-950/20 dark:to-gray-900',
			border: 'border-red-200/50 dark:border-red-800/50',
			glow: 'from-red-500/30 via-rose-500/30 to-red-500/30',
			icon: 'bg-gradient-to-br from-red-500 to-rose-600',
			iconGlow: 'bg-red-500/30',
			text: 'text-red-900 dark:text-red-100',
			subtext: 'text-red-700 dark:text-red-300'
		},
		failure: {
			bg: 'from-orange-50 via-white to-orange-50 dark:from-gray-900 dark:via-orange-950/20 dark:to-gray-900',
			border: 'border-orange-200/50 dark:border-orange-800/50',
			glow: 'from-orange-500/30 via-amber-500/30 to-orange-500/30',
			icon: 'bg-gradient-to-br from-orange-500 to-amber-600',
			iconGlow: 'bg-orange-500/30',
			text: 'text-orange-900 dark:text-orange-100',
			subtext: 'text-orange-700 dark:text-orange-300'
		},
		warning: {
			bg: 'from-amber-50 via-white to-amber-50 dark:from-gray-900 dark:via-amber-950/20 dark:to-gray-900',
			border: 'border-amber-200/50 dark:border-amber-800/50',
			glow: 'from-amber-500/30 via-yellow-500/30 to-amber-500/30',
			icon: 'bg-gradient-to-br from-amber-500 to-yellow-600',
			iconGlow: 'bg-amber-500/30',
			text: 'text-amber-900 dark:text-amber-100',
			subtext: 'text-amber-700 dark:text-amber-300'
		},
		info: {
			bg: 'from-blue-50 via-white to-blue-50 dark:from-gray-900 dark:via-blue-950/20 dark:to-gray-900',
			border: 'border-blue-200/50 dark:border-blue-800/50',
			glow: 'from-blue-500/30 via-indigo-500/30 to-blue-500/30',
			icon: 'bg-gradient-to-br from-blue-500 to-indigo-600',
			iconGlow: 'bg-blue-500/30',
			text: 'text-blue-900 dark:text-blue-100',
			subtext: 'text-blue-700 dark:text-blue-300'
		}
	};

	const currentStyle = $derived(typeStyles[type]);

	function handleDismiss() {
		visible = false;
		setTimeout(() => {
			onDismiss?.();
		}, 300);
	}

	function handleRetry() {
		onRetry?.();
		handleDismiss();
	}
</script>

{#if visible}
	<div 
		class="fixed inset-0 z-50 flex items-center justify-center p-4"
		role="alertdialog"
		aria-modal="true"
		aria-labelledby="error-banner-title"
		aria-describedby="error-banner-message"
	>
		<!-- Backdrop blur -->
		<div 
			class="absolute inset-0 bg-black/30 backdrop-blur-sm animate-in fade-in duration-300"
			onclick={handleDismiss}
			onkeydown={(e) => { if (e.key === 'Enter' || e.key === ' ') handleDismiss(); }}
			role="button"
			tabindex="0"
			aria-label="Dismiss error banner"
		></div>
		
		<!-- Error Card -->
		<div 
			class="relative w-full max-w-md animate-in zoom-in-95 slide-in-from-bottom-8 duration-300 ease-out"
		>
			<!-- Glow effect -->
			<div class="absolute -inset-4 bg-gradient-to-r {currentStyle.glow} rounded-3xl blur-2xl animate-pulse"></div>
			
			<!-- Card -->
			<div class="relative bg-gradient-to-br {currentStyle.bg} rounded-3xl shadow-2xl border {currentStyle.border} p-6 md:p-8">
				<!-- Error Icon -->
				<div class="flex justify-center mb-6">
					<div class="relative">
						<div class="absolute inset-0 {currentStyle.iconGlow} rounded-full blur-xl animate-pulse"></div>
						<div class="relative flex items-center justify-center w-16 h-16 rounded-full {currentStyle.icon} shadow-lg">
							{#if type === 'error' || type === 'failure'}
								<svg 
									class="w-8 h-8 text-white" 
									fill="none" 
									stroke="currentColor" 
									viewBox="0 0 24 24"
									aria-hidden="true"
								>
									<path 
										stroke-linecap="round" 
										stroke-linejoin="round" 
										stroke-width="2.5" 
										d="M6 18L18 6M6 6l12 12"
									/>
								</svg>
							{:else if type === 'warning'}
								<svg 
									class="w-8 h-8 text-white" 
									fill="none" 
									stroke="currentColor" 
									viewBox="0 0 24 24"
									aria-hidden="true"
								>
									<path 
										stroke-linecap="round" 
										stroke-linejoin="round" 
										stroke-width="2.5" 
										d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"
									/>
								</svg>
							{:else}
								<svg 
									class="w-8 h-8 text-white" 
									fill="none" 
									stroke="currentColor" 
									viewBox="0 0 24 24"
									aria-hidden="true"
								>
									<path 
										stroke-linecap="round" 
										stroke-linejoin="round" 
										stroke-width="2.5" 
										d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"
									/>
								</svg>
							{/if}
						</div>
					</div>
				</div>
				
				<!-- Title -->
				<h2 
					id="error-banner-title"
					class="text-xl md:text-2xl font-bold text-center {currentStyle.text} mb-3 animate-in fade-in slide-in-from-bottom-4 duration-500"
				>
					{title}
				</h2>
				
				<!-- Message -->
				{#if message}
					<p 
						id="error-banner-message"
						class="text-center {currentStyle.subtext} text-sm md:text-base mb-6 animate-in fade-in slide-in-from-bottom-4 duration-500 delay-100"
					>
						{message}
					</p>
				{/if}
				
				<!-- Children Slot -->
				{#if children}
					<div class="mb-6 animate-in fade-in duration-500 delay-200">
						{@render children()}
					</div>
				{/if}
				
				<!-- Action Buttons -->
				<div class="flex gap-3 animate-in fade-in slide-in-from-bottom-4 duration-500 delay-300">
					{#if showRetry && onRetry}
						<button
							onclick={handleRetry}
							class="flex-1 px-4 py-2.5 bg-gradient-to-r from-red-500 to-rose-600 text-white font-semibold rounded-xl shadow-lg hover:shadow-xl hover:scale-105 transition-all duration-200 focus:outline-none focus:ring-2 focus:ring-red-500 focus:ring-offset-2"
						>
							Try Again
						</button>
					{/if}
					
					{#if actionLabel && onAction}
						<button
							onclick={onAction}
							class="flex-1 px-4 py-2.5 bg-gradient-to-r from-blue-500 to-indigo-600 text-white font-semibold rounded-xl shadow-lg hover:shadow-xl hover:scale-105 transition-all duration-200 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2"
						>
							{actionLabel}
						</button>
					{/if}
					
					<button
						onclick={handleDismiss}
						class="flex-1 px-4 py-2.5 bg-gray-100 dark:bg-gray-800 text-gray-700 dark:text-gray-300 font-semibold rounded-xl shadow hover:shadow-md hover:bg-gray-200 dark:hover:bg-gray-700 transition-all duration-200 focus:outline-none focus:ring-2 focus:ring-gray-400 focus:ring-offset-2"
					>
						{#if !showRetry && !actionLabel}Close{:else}Dismiss{/if}
					</button>
				</div>
				
				<!-- Error Code (optional) -->
				{#if type === 'error'}
					<div class="mt-4 pt-4 border-t border-red-200 dark:border-red-800">
						<p class="text-xs text-center text-red-600 dark:text-red-400">
							Error ID: {Math.floor(Math.random() * 1000000).toString().padStart(6, '0')}
						</p>
					</div>
				{/if}
			</div>
		</div>
	</div>
{/if}
