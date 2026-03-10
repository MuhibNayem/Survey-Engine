<script lang="ts">
	import { onMount } from 'svelte';
	import { goto } from '$app/navigation';
	import { Button } from '$lib/components/ui/button';
	import { Input } from '$lib/components/ui/input';
	import { Badge } from '$lib/components/ui/badge';
	import * as Card from '$lib/components/ui/card';
	import * as Dialog from '$lib/components/ui/dialog';
	import * as DropdownMenu from '$lib/components/ui/dropdown-menu';
	import {
		Search,
		Plus,
		MoreVertical,
		Edit,
		Trash2,
		BarChart3,
		Settings,
		ToggleLeft,
		ToggleRight,
		Users,
		RefreshCw,
		Filter,
		Download
	} from 'lucide-svelte';
	import api from '$lib/api';
	import { toast } from 'svelte-sonner';
	import CreateFeatureDialog from './CreateFeatureDialog.svelte';
	import EditFeatureDialog from './EditFeatureDialog.svelte';

	interface Feature {
		id: string;
		featureKey: string;
		name: string;
		featureType: string;
		category: string;
		enabled: boolean;
		rolloutPercentage: number;
		minPlan: string;
		roles: string[];
		totalAccessed?: number;
		totalCompleted?: number;
		completionRate?: number;
	}

	let features = $state<Feature[]>([]);
	let isLoading = $state(true);
	let searchQuery = $state('');
	let selectedCategory = $state<string>('ALL');
	let selectedType = $state<string>('ALL');
	let selectedPlan = $state<string>('ALL');
	let editingFeature = $state<Feature | null>(null);
	let showCreateDialog = $state(false);
	let showDeleteDialog = $state(false);
	let featureToDelete = $state<string | null>(null);

	const categories = ['ALL', 'GENERAL', 'DASHBOARD', 'SURVEYS', 'CAMPAIGNS', 'QUESTIONS', 'ANALYTICS', 'RESPONSES', 'SETTINGS', 'ADMIN'];
	const types = ['ALL', 'TOUR', 'TOOLTIP', 'BANNER', 'FEATURE_FLAG', 'ANNOUNCEMENT'];
	const plans = ['ALL', 'BASIC', 'PRO', 'ENTERPRISE'];

	onMount(async () => {
		await loadFeatures();
	});

	async function loadFeatures(): Promise<void> {
		isLoading = true;
		try {
			const params = new URLSearchParams();
			if (selectedCategory !== 'ALL') params.set('category', selectedCategory);
			if (selectedType !== 'ALL') params.set('type', selectedType);
			
			const response = await api.get(`/admin/features?${params.toString()}`);
			features = response.data.content || response.data;
		} catch (error) {
			toast.error('Failed to load features');
			console.error('Failed to load features:', error);
		} finally {
			isLoading = false;
		}
	}

	function filteredFeatures(): Feature[] {
		return features.filter(f => {
			const matchesSearch = searchQuery === '' ||
				f.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
				f.featureKey.toLowerCase().includes(searchQuery.toLowerCase());
			
			const matchesCategory = selectedCategory === 'ALL' || f.category === selectedCategory;
			const matchesType = selectedType === 'ALL' || f.featureType === selectedType;
			const matchesPlan = selectedPlan === 'ALL' || f.minPlan === selectedPlan;
			
			return matchesSearch && matchesCategory && matchesType && matchesPlan;
		});
	}

	function openCreateDialog(): void {
		editingFeature = null;
		showCreateDialog = true;
	}

	function openEditDialog(feature: Feature): void {
		editingFeature = feature;
		showCreateDialog = true;
	}

	function confirmDelete(featureKey: string): void {
		featureToDelete = featureKey;
		showDeleteDialog = true;
	}

	async function deleteFeature(): Promise<void> {
		if (!featureToDelete) return;
		
		try {
			await api.delete(`/admin/features/${featureToDelete}`);
			toast.success('Feature deleted successfully');
			await loadFeatures();
		} catch (error) {
			toast.error('Failed to delete feature');
			console.error('Failed to delete feature:', error);
		} finally {
			showDeleteDialog = false;
			featureToDelete = null;
		}
	}

	async function toggleFeature(feature: Feature): Promise<void> {
		try {
			await api.put(`/admin/features/${feature.featureKey}`, {
				enabled: !feature.enabled
			});
			toast.success(`Feature ${feature.enabled ? 'disabled' : 'enabled'}`);
			await loadFeatures();
		} catch (error) {
			toast.error('Failed to update feature');
			console.error('Failed to toggle feature:', error);
		}
	}

	function viewAnalytics(featureKey: string): void {
		goto(`/admin/features/${featureKey}/analytics`);
	}

	function configureTenant(featureKey: string): void {
		goto(`/admin/features/${featureKey}/tenants`);
	}

	function getPlanColor(plan: string): string {
		switch (plan) {
			case 'BASIC': return 'bg-gray-100 text-gray-800 dark:bg-gray-800 dark:text-gray-300';
			case 'PRO': return 'bg-blue-100 text-blue-800 dark:bg-blue-900 dark:text-blue-300';
			case 'ENTERPRISE': return 'bg-purple-100 text-purple-800 dark:bg-purple-900 dark:text-purple-300';
			default: return 'bg-gray-100 text-gray-800';
		}
	}

	function getTypeColor(type: string): string {
		switch (type) {
			case 'TOUR': return 'bg-green-100 text-green-800 dark:bg-green-900 dark:text-green-300';
			case 'TOOLTIP': return 'bg-yellow-100 text-yellow-800 dark:bg-yellow-900 dark:text-yellow-300';
			case 'BANNER': return 'bg-red-100 text-red-800 dark:bg-red-900 dark:text-red-300';
			case 'FEATURE_FLAG': return 'bg-blue-100 text-blue-800 dark:bg-blue-900 dark:text-blue-300';
			case 'ANNOUNCEMENT': return 'bg-purple-100 text-purple-800 dark:bg-purple-900 dark:text-purple-300';
			default: return 'bg-gray-100 text-gray-800';
		}
	}
</script>

<div class="container mx-auto py-6 space-y-6">
	<!-- Header -->
	<div class="flex items-center justify-between">
		<div>
			<h1 class="text-3xl font-bold tracking-tight">Feature Management</h1>
			<p class="text-muted-foreground">Manage feature flags, tours, tooltips, and announcements</p>
		</div>
		<Button onclick={openCreateDialog}>
			<Plus class="mr-2 h-4 w-4" />
			New Feature
		</Button>
	</div>

	<!-- Filters -->
	<Card.Root>
		<Card.Content class="py-4">
			<div class="flex flex-wrap gap-4">
				<div class="flex-1 min-w-64">
					<div class="relative">
						<Search class="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
						<Input
							placeholder="Search features..."
							class="pl-9"
							bind:value={searchQuery}
						/>
					</div>
				</div>
				
				<div class="flex items-center gap-2">
					<Filter class="h-4 w-4 text-muted-foreground" />
					<select
						class="h-10 rounded-md border border-input bg-background px-3 py-2 text-sm"
						value={selectedCategory}
						oninput={(e) => selectedCategory = e.currentTarget.value}
					>
						{#each categories as cat}
							<option value={cat}>{cat}</option>
						{/each}
					</select>

					<select
						class="h-10 rounded-md border border-input bg-background px-3 py-2 text-sm"
						value={selectedType}
						oninput={(e) => selectedType = e.currentTarget.value}
					>
						{#each types as type}
							<option value={type}>{type}</option>
						{/each}
					</select>

					<select
						class="h-10 rounded-md border border-input bg-background px-3 py-2 text-sm"
						value={selectedPlan}
						oninput={(e) => selectedPlan = e.currentTarget.value}
					>
						{#each plans as plan}
							<option value={plan}>{plan}</option>
						{/each}
					</select>

					<Button variant="outline" size="icon" onclick={loadFeatures}>
						<RefreshCw class="h-4 w-4" />
					</Button>
				</div>
			</div>
		</Card.Content>
	</Card.Root>

	<!-- Features Table -->
	<Card.Root>
		<Card.Content class="p-0">
			<div class="relative w-full overflow-auto">
				<table class="w-full caption-bottom text-sm">
					<thead class="[&_tr]:border-b">
						<tr class="border-b transition-colors hover:bg-muted/50">
							<th class="h-12 px-4 text-left align-middle font-medium text-muted-foreground">Feature</th>
							<th class="h-12 px-4 text-left align-middle font-medium text-muted-foreground">Type</th>
							<th class="h-12 px-4 text-left align-middle font-medium text-muted-foreground">Category</th>
							<th class="h-12 px-4 text-left align-middle font-medium text-muted-foreground">Plan</th>
							<th class="h-12 px-4 text-left align-middle font-medium text-muted-foreground">Rollout</th>
							<th class="h-12 px-4 text-left align-middle font-medium text-muted-foreground">Status</th>
							<th class="h-12 px-4 text-left align-middle font-medium text-muted-foreground">Usage</th>
							<th class="h-12 px-4 text-right align-middle font-medium text-muted-foreground">Actions</th>
						</tr>
					</thead>
					<tbody class="[&_tr:last-child]:border-0">
						{#if isLoading}
							<tr>
								<td colspan="8" class="h-24 text-center">
									<div class="flex items-center justify-center gap-2">
										<RefreshCw class="h-4 w-4 animate-spin" />
										<span>Loading features...</span>
									</div>
								</td>
							</tr>
						{:else if filteredFeatures().length === 0}
							<tr>
								<td colspan="8" class="h-24 text-center text-muted-foreground">
									No features found
								</td>
							</tr>
						{:else}
							{#each filteredFeatures() as feature}
								<tr class="border-b transition-colors hover:bg-muted/50">
									<td class="p-4 align-middle">
										<div>
											<div class="font-medium">{feature.name}</div>
											<div class="text-xs text-muted-foreground">{feature.featureKey}</div>
										</div>
									</td>
									<td class="p-4 align-middle">
										<Badge variant="secondary" class={getTypeColor(feature.featureType)}>
											{feature.featureType}
										</Badge>
									</td>
									<td class="p-4 align-middle">
										{feature.category}
									</td>
									<td class="p-4 align-middle">
										<Badge variant="secondary" class={getPlanColor(feature.minPlan)}>
											{feature.minPlan}
										</Badge>
									</td>
									<td class="p-4 align-middle">
										<div class="flex items-center gap-2">
											<div class="w-24 h-2 bg-muted rounded-full overflow-hidden">
												<div 
													class="h-full bg-primary transition-all"
													style="width: {feature.rolloutPercentage}%"
												></div>
											</div>
											<span class="text-xs">{feature.rolloutPercentage}%</span>
										</div>
									</td>
									<td class="p-4 align-middle">
										<button
											onclick={() => toggleFeature(feature)}
											class="flex items-center gap-1 hover:opacity-80"
										>
											{#if feature.enabled}
												<ToggleRight class="h-6 w-6 text-green-600" />
												<span class="text-xs text-green-600">Active</span>
											{:else}
												<ToggleLeft class="h-6 w-6 text-gray-400" />
												<span class="text-xs text-gray-400">Disabled</span>
											{/if}
										</button>
									</td>
									<td class="p-4 align-middle">
										<div class="flex items-center gap-1 text-xs">
											<Users class="h-3 w-3" />
											{feature.totalAccessed ?? 0} / {feature.totalCompleted ?? 0}
										</div>
									</td>
									<td class="p-4 align-middle text-right">
										<DropdownMenu.Root>
											<DropdownMenu.Trigger>
												<Button variant="ghost" size="icon">
													<MoreVertical class="h-4 w-4" />
												</Button>
											</DropdownMenu.Trigger>
											<DropdownMenu.Content align="end">
												<DropdownMenu.Item onclick={() => openEditDialog(feature)}>
													<Edit class="mr-2 h-4 w-4" />
													Edit
												</DropdownMenu.Item>
												<DropdownMenu.Item onclick={() => viewAnalytics(feature.featureKey)}>
													<BarChart3 class="mr-2 h-4 w-4" />
													Analytics
												</DropdownMenu.Item>
												<DropdownMenu.Item onclick={() => configureTenant(feature.featureKey)}>
													<Settings class="mr-2 h-4 w-4" />
													Tenant Config
												</DropdownMenu.Item>
												<DropdownMenu.Separator />
												<DropdownMenu.Item 
													onclick={() => confirmDelete(feature.featureKey)}
													class="text-red-600"
												>
													<Trash2 class="mr-2 h-4 w-4" />
													Delete
												</DropdownMenu.Item>
											</DropdownMenu.Content>
										</DropdownMenu.Root>
									</td>
								</tr>
							{/each}
						{/if}
					</tbody>
				</table>
			</div>
		</Card.Content>
	</Card.Root>
</div>

<!-- Create/Edit Dialog -->
{#if showCreateDialog}
	{#if editingFeature}
		<EditFeatureDialog
			feature={editingFeature}
			onClose={() => showCreateDialog = false}
			onSave={loadFeatures}
		/>
	{:else}
		<CreateFeatureDialog
			onClose={() => showCreateDialog = false}
			onSave={loadFeatures}
		/>
	{/if}
{/if}

<!-- Delete Confirmation Dialog -->
{#if showDeleteDialog}
	<Dialog.Root bind:open={showDeleteDialog}>
		<Dialog.Content>
			<Dialog.Header>
				<Dialog.Title>Delete Feature</Dialog.Title>
				<Dialog.Description>
					Are you sure you want to delete this feature? This action cannot be undone.
				</Dialog.Description>
			</Dialog.Header>
			<Dialog.Footer class="sm:justify-end">
				<Button variant="outline" onclick={() => showDeleteDialog = false}>
					Cancel
				</Button>
				<Button variant="destructive" onclick={deleteFeature}>
					Delete
				</Button>
			</Dialog.Footer>
		</Dialog.Content>
	</Dialog.Root>
{/if}
