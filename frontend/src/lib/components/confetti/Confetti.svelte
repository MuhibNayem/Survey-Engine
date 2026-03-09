<script lang="ts">
	import { onMount } from 'svelte';
	import confetti from 'canvas-confetti';

	interface Props {
		/** Whether to fire the confetti */
		fire?: boolean;
		/** Number of particles */
		particleCount?: number;
		/** Spread angle in degrees */
		spread?: number;
		/** Start velocity */
		startVelocity?: number;
		/** Particle colors */
		colors?: string[];
		/** Origin position {x, y} (0-1) */
		origin?: { x?: number; y?: number };
		/** Decay rate (0-1) */
		decay?: number;
		/** Gravity */
		gravity?: number;
		/** Duration in milliseconds */
		duration?: number;
		/** Particle shapes: 'circle' | 'square' */
		shapes?: Array<'circle' | 'square'>;
		/** Callback when animation completes */
		onComplete?: () => void;
		/** Show celebration banner */
		showBanner?: boolean;
		/** Banner title */
		title?: string;
		/** Banner message */
		message?: string;
	}

	let {
		fire = false,
		particleCount = 100,
		spread = 70,
		startVelocity = 45,
		colors = ['#22c55e', '#3b82f6', '#a855f7', '#ec4899', '#f59e0b', '#14b8a6'],
		origin = { y: 0.6 },
		decay = 0.94,
		gravity = 1,
		duration = 3000,
		shapes = ['circle', 'square'],
		showBanner = true,
		title = '🎉 Success!',
		message,
		onComplete
	}: Props = $props();

	let canvas: HTMLCanvasElement | null = null;
	let animationFrame: number | null = null;
	let visible = $state(false);

	$effect(() => {
		if (fire) {
			visible = true;
			fireConfetti();
			
			// Auto-hide after duration
			const timer = setTimeout(() => {
				visible = false;
				onComplete?.();
			}, duration + 500);
			
			return () => clearTimeout(timer);
		}
	});

	function fireConfetti(): void {
		if (!canvas) return;
		const myConfetti = confetti.create(canvas, {
			resize: true,
			useWorker: true
		});

		const startTime = Date.now();
		const end = startTime + duration;

		// Fire multiple bursts for professional effect
		const burstTimes = [0, duration * 0.3, duration * 0.6];
		
		burstTimes.forEach((delay) => {
			setTimeout(() => {
				myConfetti({
					particleCount,
					spread,
					startVelocity,
					colors,
					origin: { x: Math.random() * 0.4 + 0.3, y: 0.5 },
					decay,
					gravity,
					shapes
				});
				
				// Side cannons
				myConfetti({
					particleCount: particleCount * 0.6,
					spread: 60,
					startVelocity: startVelocity * 0.8,
					colors,
					origin: { x: 0, y: 0.6 },
					decay,
					gravity,
					shapes
				});
				
				myConfetti({
					particleCount: particleCount * 0.6,
					spread: 60,
					startVelocity: startVelocity * 0.8,
					colors,
					origin: { x: 1, y: 0.6 },
					decay,
					gravity,
					shapes
				});
			}, delay);
		});
	}

	// Cleanup
	$effect(() => {
		return () => {
			if (animationFrame) {
				cancelAnimationFrame(animationFrame);
			}
		};
	});
</script>

<canvas bind:this={canvas} class="fixed inset-0 pointer-events-none z-50 w-full h-full"></canvas>

{#if showBanner && visible}
	<div 
		class="fixed inset-0 z-40 flex items-center justify-center pointer-events-none"
		aria-live="polite"
		aria-atomic="true"
	>
		<!-- Backdrop blur -->
		<div class="absolute inset-0 bg-black/20 backdrop-blur-sm animate-in fade-in duration-300"></div>
		
		<!-- Celebration Card -->
		<div 
			class="relative pointer-events-auto animate-in zoom-in-95 slide-in-from-bottom-8 duration-500 ease-out"
			role="alertdialog"
			aria-modal="true"
		>
			<!-- Glow effect -->
			<div class="absolute -inset-4 bg-gradient-to-r from-emerald-500/30 via-green-500/30 to-emerald-500/30 rounded-3xl blur-2xl animate-pulse"></div>
			
			<!-- Card -->
			<div class="relative bg-gradient-to-br from-white via-emerald-50 to-white dark:from-gray-900 dark:via-emerald-950 dark:to-gray-900 rounded-3xl shadow-2xl border border-emerald-200/50 dark:border-emerald-800/50 p-8 md:p-12 min-w-[320px] md:min-w-[480px]">
				<!-- Success Icon -->
				<div class="flex justify-center mb-6">
					<div class="relative">
						<div class="absolute inset-0 bg-emerald-500/30 rounded-full blur-xl animate-pulse"></div>
						<div class="relative flex items-center justify-center w-20 h-20 rounded-full bg-gradient-to-br from-emerald-500 to-green-600 shadow-lg">
							<svg 
								class="w-10 h-10 text-white" 
								fill="none" 
								stroke="currentColor" 
								viewBox="0 0 24 24"
								aria-hidden="true"
							>
								<path 
									stroke-linecap="round" 
									stroke-linejoin="round" 
									stroke-width="3" 
									d="M5 13l4 4L19 7"
								/>
							</svg>
						</div>
					</div>
				</div>
				
				<!-- Title -->
				<h2 class="text-2xl md:text-3xl font-bold text-center text-gray-900 dark:text-white mb-3 animate-in fade-in slide-in-from-bottom-4 duration-700">
					{title}
				</h2>
				
				<!-- Message -->
				{#if message}
					<p class="text-center text-gray-600 dark:text-gray-300 text-base md:text-lg animate-in fade-in slide-in-from-bottom-4 duration-700 delay-100">
						{message}
					</p>
				{/if}
				
				<!-- Progress bar -->
				<div class="mt-8 h-1 w-full bg-gray-200 dark:bg-gray-700 rounded-full overflow-hidden">
					<div 
						class="h-full bg-gradient-to-r from-emerald-500 via-green-500 to-emerald-500 rounded-full animate-in slide-in-from-left duration-1000 ease-out"
						style="animation: progress {duration}ms linear forwards;"
					></div>
				</div>
				
				<!-- Redirect notice -->
				<p class="mt-4 text-center text-sm text-gray-500 dark:text-gray-400 animate-in fade-in duration-700 delay-300">
					Redirecting you shortly...
				</p>
			</div>
		</div>
	</div>
{/if}

<style>
	@keyframes progress {
		from { width: 0%; }
		to { width: 100%; }
	}
</style>
