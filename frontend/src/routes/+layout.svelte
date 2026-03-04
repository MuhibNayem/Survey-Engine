<script lang="ts">
	import '../app.css';
	import { Toaster } from '$lib/components/ui/sonner';
	let { children } = $props();
</script>

<svelte:head>
	<link rel="preconnect" href="https://fonts.googleapis.com" />
	<link rel="preconnect" href="https://fonts.gstatic.com" crossorigin="anonymous" />
	<link
		href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700;800&display=swap"
		rel="stylesheet"
	/>
</svelte:head>

<div class="font-sans antialiased" style="font-family: 'Inter', sans-serif;">
	{@render children()}
</div>
<Toaster richColors />
