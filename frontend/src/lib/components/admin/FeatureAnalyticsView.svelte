<script lang="ts">
	import { onMount } from 'svelte';
	import { Button } from '$lib/components/ui/button';
	import { Badge } from '$lib/components/ui/badge';
	import * as Card from '$lib/components/ui/card';
	import { ArrowLeft, Users, TrendingUp, Activity, Calendar } from 'lucide-svelte';
	import { goto } from '$app/navigation';
	import api from '$lib/api';
	import { toast } from 'svelte-sonner';

	interface Props {
		featureKey: string;
	}

	let { featureKey }: Props = $props();

	let analytics = $state<any>(null);
	let isLoading = $state(true);
	let feature = $state<any>(null);

	onMount(async () => {
		await loadAnalytics();
	});

	async function loadAnalytics(): Promise<void> {
		isLoading = true;
		try {
			const [analyticsRes, featureRes] = await Promise.all([
				api.get(`/admin/features/${featureKey}/analytics`),
				api.get(`/admin/features/${featureKey}`)
			]);
			analytics = analyticsRes.data;
			feature = featureRes.data;
		} catch (error) {
			toast.error('Failed to load analytics');
			console.error('Failed to load analytics:', error);
		} finally {
			isLoading = false;
		}
	}

	function formatNumber(num: number): string {
		if (num >= 1000000) return (num / 1000000).toFixed(1) + 'M';
		if (num >= 1000) return (num / 1000).toFixed(1) + 'K';
		return num.toString();
	}
</script>

<div class="container mx-auto py-6 space-y-6">
	<!-- Header -->
	<div class="flex items-center gap-4">
		<Button variant="ghost" size="icon" onclick={() => goto('/admin/features')}>
			<ArrowLeft class="h-4 w-4" />
		</Button>
		<div>
			<h1 class="text-3xl font-bold tracking-tight">Feature Analytics</h1>
			<p class="text-muted-foreground">{feature?.name ?? featureKey}</p>
		</div>
	</div>

	{#if isLoading}
		<div class="flex items-center justify-center h-64">
			<div class="text-center">
				<div class="animate-spin h-8 w-8 border-4 border-primary border-t-transparent rounded-full mx-auto"></div>
				<p class="mt-4 text-muted-foreground">Loading analytics...</p>
			</div>
		</div>
	{:else if !analytics}
		<div class="flex items-center justify-center h-64">
			<p class="text-muted-foreground">No analytics available</p>
		</div>
	{:else}
		<!-- Stats Grid -->
		<div class="grid gap-4 md:grid-cols-2 lg:grid-cols-4">
			<Card.Root>
				<Card.Header class="flex flex-row items-center justify-between space-y-0 pb-2">
					<Card.Title class="text-sm font-medium">Total Accessed</Card.Title>
					<Users class="h-4 w-4 text-muted-foreground" />
				</Card.Header>
				<Card.Content>
					<div class="text-2xl font-bold">{formatNumber(analytics.totalAccessed ?? 0)}</div>
					<p class="text-xs text-muted-foreground">Users who saw this feature</p>
				</Card.Content>
			</Card.Root>

			<Card.Root>
				<Card.Header class="flex flex-row items-center justify-between space-y-0 pb-2">
					<Card.Title class="text-sm font-medium">Total Completed</Card.Title>
					<TrendingUp class="h-4 w-4 text-muted-foreground" />
				</Card.Header>
				<Card.Content>
					<div class="text-2xl font-bold">{formatNumber(analytics.totalCompleted ?? 0)}</div>
					<p class="text-xs text-muted-foreground">Users who completed</p>
				</Card.Content>
			</Card.Root>

			<Card.Root>
				<Card.Header class="flex flex-row items-center justify-between space-y-0 pb-2">
					<Card.Title class="text-sm font-medium">Completion Rate</Card.Title>
					<Activity class="h-4 w-4 text-muted-foreground" />
				</Card.Header>
				<Card.Content>
					<div class="text-2xl font-bold">{(analytics.completionRate ?? 0).toFixed(1)}%</div>
					<p class="text-xs text-muted-foreground">Of accessed users</p>
				</Card.Content>
			</Card.Root>

			<Card.Root>
				<Card.Header class="flex flex-row items-center justify-between space-y-0 pb-2">
					<Card.Title class="text-sm font-medium">Unique Tenants</Card.Title>
					<Calendar class="h-4 w-4 text-muted-foreground" />
				</Card.Header>
				<Card.Content>
					<div class="text-2xl font-bold">{formatNumber(analytics.uniqueTenants ?? 0)}</div>
					<p class="text-xs text-muted-foreground">Organizations using</p>
				</Card.Content>
			</Card.Root>
		</div>

		<!-- Details -->
		<Card.Root>
			<Card.Header>
				<Card.Title>Feature Details</Card.Title>
			</Card.Header>
			<Card.Content>
				<dl class="grid grid-cols-2 gap-4">
					<div>
						<dt class="text-sm font-medium text-muted-foreground">Feature Key</dt>
						<dd class="text-sm">{analytics.featureKey}</dd>
					</div>
					<div>
						<dt class="text-sm font-medium text-muted-foreground">Type</dt>
						<dd class="text-sm">
							<Badge variant="secondary">{analytics.featureType}</Badge>
						</dd>
					</div>
					<div>
						<dt class="text-sm font-medium text-muted-foreground">Category</dt>
						<dd class="text-sm">{analytics.category}</dd>
					</div>
					<div>
						<dt class="text-sm font-medium text-muted-foreground">Minimum Plan</dt>
						<dd class="text-sm">
							<Badge variant="secondary">{analytics.minPlan}</Badge>
						</dd>
					</div>
					<div>
						<dt class="text-sm font-medium text-muted-foreground">Status</dt>
						<dd class="text-sm">
							<Badge variant={analytics.enabled ? 'default' : 'secondary'}>
								{analytics.enabled ? 'Enabled' : 'Disabled'}
							</Badge>
						</dd>
					</div>
					<div>
						<dt class="text-sm font-medium text-muted-foreground">Rollout</dt>
						<dd class="text-sm">{analytics.rolloutPercentage}%</dd>
					</div>
					{#if analytics.firstAccessedAt}
						<div>
							<dt class="text-sm font-medium text-muted-foreground">First Accessed</dt>
							<dd class="text-sm">{new Date(analytics.firstAccessedAt).toLocaleDateString()}</dd>
						</div>
					{/if}
					{#if analytics.lastAccessedAt}
						<div>
							<dt class="text-sm font-medium text-muted-foreground">Last Accessed</dt>
							<dd class="text-sm">{new Date(analytics.lastAccessedAt).toLocaleDateString()}</dd>
						</div>
					{/if}
				</dl>
			</Card.Content>
		</Card.Root>

		<!-- Usage Breakdown -->
		<Card.Root>
			<Card.Header>
				<Card.Title>Usage Breakdown</Card.Title>
			</Card.Header>
			<Card.Content>
				<div class="space-y-4">
					<div>
						<div class="flex justify-between text-sm mb-2">
							<span>Accessed</span>
							<span>{analytics.totalAccessed ?? 0} users</span>
						</div>
						<div class="w-full h-2 bg-muted rounded-full overflow-hidden">
							<div 
								class="h-full bg-blue-500"
								style="width: {analytics.totalAccessed ?? 0}px"
							></div>
						</div>
					</div>
					<div>
						<div class="flex justify-between text-sm mb-2">
							<span>Completed</span>
							<span>{analytics.totalCompleted ?? 0} users</span>
						</div>
						<div class="w-full h-2 bg-muted rounded-full overflow-hidden">
							<div 
								class="h-full bg-green-500"
								style="width: {analytics.totalCompleted ?? 0}px"
							></div>
						</div>
					</div>
					<div>
						<div class="flex justify-between text-sm mb-2">
							<span>Not Accessed</span>
							<span>{analytics.totalNotAccessed ?? 0} users</span>
						</div>
						<div class="w-full h-2 bg-muted rounded-full overflow-hidden">
							<div 
								class="h-full bg-gray-400"
								style="width: {analytics.totalNotAccessed ?? 0}px"
							></div>
						</div>
					</div>
				</div>
			</Card.Content>
		</Card.Root>
	{/if}
</div>
