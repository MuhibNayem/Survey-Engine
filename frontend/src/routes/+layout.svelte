<script lang="ts">
	import "../app.css";
	import { Toaster } from "$lib/components/ui/sonner";
	let { children } = $props();
</script>

<svelte:head>
	<link rel="preconnect" href="https://fonts.googleapis.com" />
	<link
		rel="preconnect"
		href="https://fonts.gstatic.com"
		crossorigin="anonymous"
	/>
	<link
		href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700;800&display=swap"
		rel="stylesheet"
	/>
</svelte:head>

<div class="font-sans antialiased" style="font-family: 'Inter', sans-serif;">
	<!-- Skip Link for Accessibility -->
	<a
		href="#main-content"
		class="sr-only focus:not-sr-only focus:fixed focus:top-4 focus:left-4 focus:z-50 focus:px-4 focus:py-2 focus:bg-primary focus:text-primary-foreground focus:rounded-md focus:shadow-lg focus:outline-none focus:ring-2 focus:ring-ring focus:ring-offset-2"
	>
		Skip to main content
	</a>

	<div id="main-content">
		{@render children()}
	</div>
</div>
<Toaster richColors />
