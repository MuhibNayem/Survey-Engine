<script lang="ts">
	import { onMount } from 'svelte';
	import { Button } from '$lib/components/ui/button';
	import { Badge } from '$lib/components/ui/badge';
	import * as Card from '$lib/components/ui/card';
	import { Switch } from '$lib/components/ui/switch';
	import { Label } from '$lib/components/ui/label';
	import { Input } from '$lib/components/ui/input';
	import { ArrowLeft, Save, Trash2 } from 'lucide-svelte';
	import { goto } from '$app/navigation';
	import api from '$lib/api';
	import { toast } from 'svelte-sonner';
	import { TenantCombobox } from '$lib/components/ui/combobox';

	interface Props {
		featureKey: string;
	}

	let { featureKey }: Props = $props();

	let feature = $state<any>(null);
	let configurations = $state<any[]>([]);
	let isLoading = $state(true);
	let isSaving = $state(false);

	// New configuration form
	let newTenantId = $state<string | null>(null);
	let newTenantName = $state('');
	let newEnabled = $state(true);
	let newRolloutPercentage = $state<number | null>(null);

	onMount(async () => {
		await loadData();
	});

	async function loadData(): Promise<void> {
		isLoading = true;
		try {
			const [featureRes, configsRes] = await Promise.all([
				api.get(`/admin/features/${featureKey}`),
				api.get(`/admin/features/${featureKey}/tenants`)
			]);
			feature = featureRes.data;
			configurations = configsRes.data;
		} catch (error) {
			toast.error('Failed to load data');
			console.error('Failed to load data:', error);
		} finally {
			isLoading = false;
		}
	}

	async function saveConfiguration(tenantId: string, config: any): Promise<void> {
		isSaving = true;
		try {
			await api.post(`/admin/features/${featureKey}/tenants/${tenantId}/configure`, config);
			toast.success('Configuration saved');
			await loadData();
		} catch (error: any) {
			const message = error.response?.data?.message || 'Failed to save configuration';
			toast.error(message);
		} finally {
			isSaving = false;
		}
	}

	async function deleteConfiguration(tenantId: string): Promise<void> {
		try {
			await api.delete(`/admin/features/${featureKey}/tenants/${tenantId}/configure`);
			toast.success('Configuration removed');
			await loadData();
		} catch (error) {
			toast.error('Failed to remove configuration');
			console.error('Failed to remove configuration:', error);
		}
	}

	async function addConfiguration(): Promise<void> {
		if (!newTenantId) {
			toast.error('Please select a tenant');
			return;
		}

		await saveConfiguration(newTenantId, {
			enabled: newEnabled,
			rolloutPercentage: newRolloutPercentage,
			customMetadata: {}
		});

		// Reset form
		newTenantId = null;
		newTenantName = '';
		newEnabled = true;
		newRolloutPercentage = null;
	}

	function handleTenantChange(tenantId: string | null): void {
		newTenantId = tenantId;
	}
</script>

<div class="container mx-auto py-6 space-y-6">
	<!-- Header -->
	<div class="flex items-center gap-4">
		<Button variant="ghost" size="icon" onclick={() => goto('/admin/features')}>
			<ArrowLeft class="h-4 w-4" />
		</Button>
		<div>
			<h1 class="text-3xl font-bold tracking-tight">Tenant Configuration</h1>
			<p class="text-muted-foreground">{feature?.name ?? featureKey}</p>
		</div>
	</div>

	{#if isLoading}
		<div class="flex items-center justify-center h-64">
			<div class="text-center">
				<div class="animate-spin h-8 w-8 border-4 border-primary border-t-transparent rounded-full mx-auto"></div>
				<p class="mt-4 text-muted-foreground">Loading...</p>
			</div>
		</div>
	{:else}
		<!-- Add New Configuration -->
		<Card.Root>
			<Card.Header>
				<Card.Title>Add Tenant Configuration</Card.Title>
				<Card.Description>Override feature settings for a specific tenant</Card.Description>
			</Card.Header>
			<Card.Content>
				<div class="grid gap-4 md:grid-cols-5 items-end">
					<div class="md:col-span-2 space-y-2">
						<TenantCombobox 
							value={newTenantId}
							placeholder="Search and select tenant..."
							label="Tenant"
							onValueChange={handleTenantChange}
						/>
					</div>
					<div class="space-y-2">
						<Label for="configEnabled">Enabled</Label>
						<div class="flex items-center gap-2 h-10">
							<Switch id="configEnabled" bind:checked={newEnabled} />
							<span class="text-sm">{newEnabled ? 'Yes' : 'No'}</span>
						</div>
					</div>
					<div class="space-y-2">
						<Label for="configRollout">Rollout %</Label>
						<Input
							id="configRollout"
							type="number"
							min="0"
							max="100"
							placeholder="Default"
							bind:value={newRolloutPercentage}
						/>
					</div>
					<Button onclick={addConfiguration} disabled={isSaving || !newTenantId}>
						<Save class="mr-2 h-4 w-4" />
						Save
					</Button>
				</div>
			</Card.Content>
		</Card.Root>

		<!-- Existing Configurations -->
		<Card.Root>
			<Card.Header>
				<Card.Title>Tenant Configurations</Card.Title>
				<Card.Description>
					{configurations.length} {configurations.length === 1 ? 'tenant' : 'tenants'} configured
				</Card.Description>
			</Card.Header>
			<Card.Content>
				{#if configurations.length === 0}
					<div class="text-center py-8 text-muted-foreground">
						No tenant-specific configurations. All tenants use global settings.
					</div>
				{:else}
					<div class="space-y-4">
						{#each configurations as config}
							<div class="flex items-center justify-between p-4 border rounded-lg">
								<div class="space-y-1">
									<div class="font-medium">{config.tenantId}</div>
									<div class="text-sm text-muted-foreground">
										Enabled: {config.enabled ? 'Yes' : 'No'} | 
										Rollout: {config.rolloutPercentage ?? 'Default'}%
									</div>
								</div>
								<div class="flex items-center gap-2">
									<Badge variant={config.effectiveEnabled ? 'default' : 'secondary'}>
										{config.effectiveEnabled ? 'Active' : 'Disabled'}
									</Badge>
									<Button
										variant="ghost"
										size="icon"
										onclick={() => deleteConfiguration(config.tenantId)}
									>
										<Trash2 class="h-4 w-4 text-red-600" />
									</Button>
								</div>
							</div>
						{/each}
					</div>
				{/if}
			</Card.Content>
		</Card.Root>

		<!-- Global Settings Reference -->
		<Card.Root>
			<Card.Header>
				<Card.Title>Global Settings</Card.Title>
				<Card.Description>Current global feature configuration</Card.Description>
			</Card.Header>
			<Card.Content>
				<dl class="grid grid-cols-2 gap-4">
					<div>
						<dt class="text-sm font-medium text-muted-foreground">Enabled</dt>
						<dd class="text-sm">
							<Badge variant={feature.enabled ? 'default' : 'secondary'}>
								{feature.enabled ? 'Yes' : 'No'}
							</Badge>
						</dd>
					</div>
					<div>
						<dt class="text-sm font-medium text-muted-foreground">Rollout Percentage</dt>
						<dd class="text-sm">{feature.rolloutPercentage}%</dd>
					</div>
					<div>
						<dt class="text-sm font-medium text-muted-foreground">Minimum Plan</dt>
						<dd class="text-sm">
							<Badge variant="secondary">{feature.minPlan}</Badge>
						</dd>
					</div>
					<div>
						<dt class="text-sm font-medium text-muted-foreground">Allowed Roles</dt>
						<dd class="text-sm">{feature.roles?.join(', ')}</dd>
					</div>
				</dl>
			</Card.Content>
		</Card.Root>
	{/if}
</div>
