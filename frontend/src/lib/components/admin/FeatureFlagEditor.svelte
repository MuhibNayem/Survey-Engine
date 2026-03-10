<script lang="ts">
	import { Button } from '$lib/components/ui/button';
	import { Input } from '$lib/components/ui/input';
	import { Label } from '$lib/components/ui/label';
	import * as Select from '$lib/components/ui/select';
	import { Switch } from '$lib/components/ui/switch';
	import * as Card from '$lib/components/ui/card';
	import { Alert, AlertDescription, AlertTitle } from '$lib/components/ui/alert';
	import { Badge } from '$lib/components/ui/badge';
	import {
		Flag,
		Plus,
		Trash2,
		Group,
		Settings2,
		AlertCircle,
		Shield,
		Clock,
		TrendingUp,
		 Zap
	} from 'lucide-svelte';
	import { cn } from '$lib/utils';
	import { toast } from 'svelte-sonner';
	import type {
		FeatureFlagMetadata,
		FeatureFlagRule,
		FeatureFlagRuleGroup,
		RuleOperator,
		LogicalOperator,
		RolloutStrategy
	} from './types';

	interface Props {
		/** Feature flag metadata to edit */
		value: FeatureFlagMetadata;
		/** Called when feature flag data changes */
		onChange?: (value: FeatureFlagMetadata) => void;
		/** Whether editor is disabled */
		disabled?: boolean;
		/** Custom class names */
		className?: string;
	}

	let { value, onChange, disabled = false, className }: Props = $props();

	// Validation state
	let errors = $state<string[]>([]);

	// Common attributes for rules
	const commonAttributes = [
		'user.id',
		'user.email',
		'user.role',
		'user.plan',
		'user.tenantId',
		'session.device',
		'session.browser',
		'session.os',
		'geo.country',
		'geo.region',
		'geo.city',
		'custom.attribute'
	];

	// Available operators
	const operators: { value: RuleOperator; label: string }[] = [
		{ value: 'equals', label: 'Equals' },
		{ value: 'not_equals', label: 'Not Equals' },
		{ value: 'contains', label: 'Contains' },
		{ value: 'not_contains', label: 'Not Contains' },
		{ value: 'greater_than', label: 'Greater Than' },
		{ value: 'less_than', label: 'Less Than' },
		{ value: 'greater_than_or_equal', label: 'Greater Than or Equal' },
		{ value: 'less_than_or_equal', label: 'Less Than or Equal' },
		{ value: 'in', label: 'In List' },
		{ value: 'not_in', label: 'Not In List' },
		{ value: 'regex', label: 'Matches Regex' },
		{ value: 'is_null', label: 'Is Null' },
		{ value: 'is_not_null', label: 'Is Not Null' }
	];

	// Rollout strategies
	const rolloutStrategies: { value: RolloutStrategy; label: string; icon: typeof Flag }[] = [
		{ value: 'percentage', label: 'Percentage', icon: Flag },
		{ value: 'gradual', label: 'Gradual', icon: TrendingUp },
		{ value: 'scheduled', label: 'Scheduled', icon: Clock },
		{ value: 'canary', label: 'Canary', icon: Zap }
	];

	// Validate feature flag
	function validate(): boolean {
		errors = [];

		if (value.rolloutPercentage < 0 || value.rolloutPercentage > 100) {
			errors.push('Rollout percentage must be between 0 and 100');
		}

		if (value.gradualRollout) {
			if (value.gradualRollout.startPercentage < 0 || value.gradualRollout.startPercentage > 100) {
				errors.push('Gradual rollout start percentage must be between 0 and 100');
			}
			if (value.gradualRollout.targetPercentage < 0 || value.gradualRollout.targetPercentage > 100) {
				errors.push('Gradual rollout target percentage must be between 0 and 100');
			}
		}

		return errors.length === 0;
	}

	// Run validation on value change
	$effect(() => {
		validate();
	});

	/**
	 * Generate a unique ID
	 */
	function generateId(): string {
		return `id-${Date.now()}-${Math.random().toString(36).substring(2, 9)}`;
	}

	/**
	 * Create a new rule
	 */
	function createRule(): FeatureFlagRule {
		return {
			id: generateId(),
			attribute: 'user.role',
			operator: 'equals',
			value: ''
		};
	}

	/**
	 * Create a new rule group
	 */
	function createRuleGroup(): FeatureFlagRuleGroup {
		return {
			id: generateId(),
			logicalOperator: 'AND',
			rules: [createRule()],
			groups: []
		};
	}

	/**
	 * Update the feature flag metadata
	 */
	function updateFlag(updates: Partial<FeatureFlagMetadata>): void {
		onChange?.({ ...value, ...updates });
	}

	/**
	 * Add a rule to a group
	 */
	function addRule(groupIndex: number, parentGroup?: FeatureFlagRuleGroup): void {
		const newGroups = [...value.ruleGroups];
		if (parentGroup) {
			const group = findGroup(newGroups, parentGroup.id);
			if (group) {
				group.rules.push(createRule());
			}
		} else if (groupIndex >= 0) {
			newGroups[groupIndex].rules.push(createRule());
		} else {
			// Create new group if none exists
			if (newGroups.length === 0) {
				newGroups.push(createRuleGroup());
			}
			newGroups[0].rules.push(createRule());
		}
		updateFlag({ ruleGroups: newGroups });
		toast.success('Rule added');
	}

	/**
	 * Remove a rule from a group
	 */
	function removeRule(groupIndex: number, ruleIndex: number): void {
		const newGroups = [...value.ruleGroups];
		if (newGroups[groupIndex]?.rules[ruleIndex]) {
			newGroups[groupIndex].rules.splice(ruleIndex, 1);
			updateFlag({ ruleGroups: newGroups });
			toast.success('Rule removed');
		}
	}

	/**
	 * Update a rule
	 */
	function updateRule(
		groupIndex: number,
		ruleIndex: number,
		updates: Partial<FeatureFlagRule>
	): void {
		const newGroups = [...value.ruleGroups];
		const rule = newGroups[groupIndex]?.rules[ruleIndex];
		if (rule) {
			Object.assign(rule, updates);
			updateFlag({ ruleGroups: newGroups });
		}
	}

	/**
	 * Add a new rule group
	 */
	function addRuleGroup(): void {
		const newGroups = [...value.ruleGroups, createRuleGroup()];
		updateFlag({ ruleGroups: newGroups });
		toast.success('Rule group added');
	}

	/**
	 * Remove a rule group
	 */
	function removeRuleGroup(groupIndex: number): void {
		const newGroups = value.ruleGroups.filter((_, i) => i !== groupIndex);
		updateFlag({ ruleGroups: newGroups.length > 0 ? newGroups : [createRuleGroup()] });
		toast.success('Rule group removed');
	}

	/**
	 * Update a rule group
	 */
	function updateRuleGroup(groupIndex: number, updates: Partial<FeatureFlagRuleGroup>): void {
		const newGroups = [...value.ruleGroups];
		if (newGroups[groupIndex]) {
			Object.assign(newGroups[groupIndex], updates);
			updateFlag({ ruleGroups: newGroups });
		}
	}

	/**
	 * Find a group by ID (for nested groups)
	 */
	function findGroup(groups: FeatureFlagRuleGroup[], id: string): FeatureFlagRuleGroup | null {
		for (const group of groups) {
			if (group.id === id) return group;
			if (group.groups) {
				const found = findGroup(group.groups, id);
				if (found) return found;
			}
		}
		return null;
	}

	/**
	 * Get operator label
	 */
	function getOperatorLabel(operator: RuleOperator): string {
		return operators.find((o) => o.value === operator)?.label || operator;
	}

	/**
	 * Check if operator requires a value
	 */
	function operatorRequiresValue(operator: RuleOperator): boolean {
		return operator !== 'is_null' && operator !== 'is_not_null';
	}

	/**
	 * Get rollout strategy icon
	 */
	function getRolloutIcon(strategy: RolloutStrategy): typeof Flag {
		return rolloutStrategies.find((s) => s.value === strategy)?.icon || Flag;
	}
</script>

<div class={cn('flex flex-col gap-4', className)}>
	<!-- Header -->
	<div class="flex items-center justify-between">
		<div class="flex items-center gap-2">
			<Flag class="h-5 w-5 text-primary" />
			<h3 class="text-lg font-semibold">Feature Flag Rules</h3>
		</div>
		<div class="flex items-center gap-2">
			<Badge variant={value.killSwitch ? 'destructive' : 'secondary'}>
				{value.killSwitch ? 'Kill Switch Active' : 'Active'}
			</Badge>
		</div>
	</div>

	<!-- Validation errors -->
	{#if errors.length > 0}
		<Alert variant="destructive">
			<AlertCircle class="h-4 w-4" />
			<AlertDescription class="flex flex-col gap-1">
				{#each errors as error}
					<span class="text-sm">{error}</span>
				{/each}
			</AlertDescription>
		</Alert>
	{/if}

	<!-- Kill Switch -->
	<Card.Root class="border-destructive/50">
		<Card.Header class="py-3">
			<div class="flex items-center gap-2">
				<Shield class="h-4 w-4 text-destructive" />
				<Card.Title class="text-sm font-medium text-destructive">Kill Switch</Card.Title>
			</div>
		</Card.Header>
		<Card.Content>
			<div class="flex items-center justify-between">
				<div>
					<Label for="kill-switch" class="text-sm font-medium">Disable for Everyone</Label>
					<p class="text-xs text-muted-foreground">
						When enabled, this feature will be disabled for all users regardless of rules
					</p>
				</div>
				<Switch
					id="kill-switch"
					checked={value.killSwitch}
					onCheckedChange={(checked) => updateFlag({ killSwitch: checked })}
					disabled={disabled}
				/>
			</div>
		</Card.Content>
	</Card.Root>

	<!-- Default Value -->
	<Card.Root>
		<Card.Header class="py-3">
			<Card.Title class="text-sm font-medium">Default Behavior</Card.Title>
		</Card.Header>
		<Card.Content>
			<div class="flex items-center justify-between">
				<div>
					<Label for="default-value" class="text-sm">Default Value</Label>
					<p class="text-xs text-muted-foreground">Value when no rules match</p>
				</div>
				<Switch
					id="default-value"
					checked={value.defaultValue}
					onCheckedChange={(checked) => updateFlag({ defaultValue: checked })}
					disabled={disabled || value.killSwitch}
				/>
			</div>
		</Card.Content>
	</Card.Root>

	<!-- Rollout Settings -->
	<Card.Root>
		<Card.Header class="py-3">
			<div class="flex items-center gap-2">
				<TrendingUp class="h-4 w-4 text-muted-foreground" />
				<Card.Title class="text-sm font-medium">Rollout Configuration</Card.Title>
			</div>
		</Card.Header>
		<Card.Content class="space-y-4">
			<div class="grid gap-4 md:grid-cols-2">
				<div class="space-y-2">
					<Label for="rollout-percentage">Rollout Percentage: {value.rolloutPercentage}%</Label>
					<Input
						id="rollout-percentage"
						type="range"
						min="0"
						max="100"
						value={value.rolloutPercentage}
						oninput={(e) => updateFlag({ rolloutPercentage: parseInt(e.currentTarget.value) })}
						disabled={disabled || value.killSwitch}
						class="w-full"
					/>
					<div class="flex justify-between text-xs text-muted-foreground">
						<span>0%</span>
						<span>50%</span>
						<span>100%</span>
					</div>
				</div>

				<div class="space-y-2">
					<Label for="rollout-strategy">Rollout Strategy</Label>
					<Select.Root
						type="single"
						value={value.rolloutStrategy}
						onValueChange={(v) => updateFlag({ rolloutStrategy: v as RolloutStrategy })}
					>
						<Select.Trigger class="w-full" disabled={disabled || value.killSwitch}>
							<div class="flex items-center gap-2">
								{#if true}
									{@const Icon = getRolloutIcon(value.rolloutStrategy)}
									<Icon class="h-4 w-4" />
								{/if}
								<span>{value.rolloutStrategy}</span>
							</div>
						</Select.Trigger>
						<Select.Content>
							{#each rolloutStrategies as strategy}
								<Select.Item value={strategy.value}>
									<div class="flex items-center gap-2">
										{#if true}
											{@const Icon = strategy.icon}
											<Icon class="h-4 w-4" />
										{/if}
										<span>{strategy.label}</span>
									</div>
								</Select.Item>
							{/each}
						</Select.Content>
					</Select.Root>
				</div>
			</div>

			<!-- Gradual Rollout Config -->
			{#if value.rolloutStrategy === 'gradual'}
				<div class="p-4 border rounded-lg bg-muted/50 space-y-3">
					<Label class="text-sm font-medium">Gradual Rollout Settings</Label>
					<div class="grid gap-3 md:grid-cols-3">
						<div class="space-y-1">
							<Label for="gradual-start" class="text-xs">Start %</Label>
							<Input
								id="gradual-start"
								type="number"
								min="0"
								max="100"
								value={value.gradualRollout?.startPercentage ?? 0}
								oninput={(e) =>
									updateFlag({
										gradualRollout: {
											startPercentage: parseInt(e.currentTarget.value) || 0,
											targetPercentage: value.gradualRollout?.targetPercentage ?? 100,
											incrementStep: value.gradualRollout?.incrementStep ?? 10,
											intervalHours: value.gradualRollout?.intervalHours ?? 24
										}
									})}
								disabled={disabled}
							/>
						</div>
						<div class="space-y-1">
							<Label for="gradual-target" class="text-xs">Target %</Label>
							<Input
								id="gradual-target"
								type="number"
								min="0"
								max="100"
								value={value.gradualRollout?.targetPercentage ?? 100}
								oninput={(e) =>
									updateFlag({
										gradualRollout: {
											startPercentage: value.gradualRollout?.startPercentage ?? 0,
											targetPercentage: parseInt(e.currentTarget.value) || 100,
											incrementStep: value.gradualRollout?.incrementStep ?? 10,
											intervalHours: value.gradualRollout?.intervalHours ?? 24
										}
									})}
								disabled={disabled}
							/>
						</div>
						<div class="space-y-1">
							<Label for="gradual-interval" class="text-xs">Interval (hours)</Label>
							<Input
								id="gradual-interval"
								type="number"
								min="1"
								value={value.gradualRollout?.intervalHours ?? 24}
								oninput={(e) =>
									updateFlag({
										gradualRollout: {
											startPercentage: value.gradualRollout?.startPercentage ?? 0,
											targetPercentage: value.gradualRollout?.targetPercentage ?? 100,
											incrementStep: value.gradualRollout?.incrementStep ?? 10,
											intervalHours: parseInt(e.currentTarget.value) || 24
										}
									})}
								disabled={disabled}
							/>
						</div>
					</div>
				</div>
			{/if}

			<!-- Environment Overrides -->
			<div class="space-y-2">
				<Label class="text-sm font-medium">Environment Overrides</Label>
				<div class="flex flex-wrap gap-2">
					{#each ['production', 'staging', 'development'] as env}
						<div class="flex items-center gap-2 p-2 border rounded-lg">
							<Switch
								id={`env-${env}`}
								checked={value.environmentOverrides?.[env] ?? false}
								onCheckedChange={(checked) =>
									updateFlag({
										environmentOverrides: {
											...value.environmentOverrides,
											[env]: checked
										}
									})}
								disabled={disabled}
							/>
							<Label for={`env-${env}`} class="text-sm capitalize">
								{env}
							</Label>
						</div>
					{/each}
				</div>
			</div>
		</Card.Content>
	</Card.Root>

	<!-- Rule Groups -->
	<Card.Root>
		<Card.Header class="py-3">
			<div class="flex items-center justify-between">
				<div class="flex items-center gap-2">
					<Group class="h-4 w-4 text-muted-foreground" />
					<Card.Title class="text-sm font-medium">Rule Groups</Card.Title>
				</div>
				<Button variant="outline" size="sm" onclick={addRuleGroup} disabled={disabled || value.killSwitch}>
					<Plus class="h-4 w-4 mr-1" />
					Add Group
				</Button>
			</div>
		</Card.Header>
		<Card.Content class="space-y-4">
			{#if value.ruleGroups.length === 0}
				<div class="text-center py-8 text-muted-foreground">
					<Group class="h-8 w-8 mx-auto mb-2 opacity-50" />
					<p class="text-sm">No rule groups yet</p>
					<p class="text-xs">Add a rule group to define conditions</p>
				</div>
			{:else}
				{#each value.ruleGroups as group, groupIndex (group.id)}
					<div class="p-4 border rounded-lg space-y-3">
						<!-- Group Header -->
						<div class="flex items-center justify-between">
							<div class="flex items-center gap-2">
								<Badge variant="outline">Group {groupIndex + 1}</Badge>
								<Select.Root
									type="single"
									value={group.logicalOperator}
									onValueChange={(v) =>
										updateRuleGroup(groupIndex, { logicalOperator: v as LogicalOperator })
									}
								>
									<Select.Trigger class="w-32 h-8" disabled={disabled || value.killSwitch}>
										{group.logicalOperator}
									</Select.Trigger>
									<Select.Content>
										<Select.Item value="AND">AND (all match)</Select.Item>
										<Select.Item value="OR">OR (any match)</Select.Item>
									</Select.Content>
								</Select.Root>
							</div>
							<div class="flex items-center gap-2">
								<Button
									variant="ghost"
									size="sm"
									onclick={() => addRule(groupIndex)}
									disabled={disabled || value.killSwitch}
								>
									<Plus class="h-4 w-4 mr-1" />
									Add Rule
								</Button>
								<Button
									variant="ghost"
									size="icon"
									onclick={() => removeRuleGroup(groupIndex)}
									disabled={disabled || value.killSwitch || value.ruleGroups.length === 1}
									aria-label="Remove group"
								>
									<Trash2 class="h-4 w-4 text-destructive" />
								</Button>
							</div>
						</div>

						<!-- Rules -->
						<div class="space-y-2">
							{#each group.rules as rule, ruleIndex (rule.id)}
								<div class="flex items-center gap-2 p-2 bg-muted/50 rounded">
									<!-- Attribute -->
									<Select.Root
										type="single"
										value={rule.attribute}
										onValueChange={(v) => updateRule(groupIndex, ruleIndex, { attribute: v })}
									>
										<Select.Trigger class="w-40 h-9" disabled={disabled || value.killSwitch}>
											{rule.attribute}
										</Select.Trigger>
										<Select.Content>
											{#each commonAttributes as attr}
												<Select.Item value={attr}>{attr}</Select.Item>
											{/each}
										</Select.Content>
									</Select.Root>

									<!-- Operator -->
									<Select.Root
										type="single"
										value={rule.operator}
										onValueChange={(v) =>
											updateRule(groupIndex, ruleIndex, { operator: v as RuleOperator })
										}
									>
										<Select.Trigger class="w-40 h-9" disabled={disabled || value.killSwitch}>
											{getOperatorLabel(rule.operator)}
										</Select.Trigger>
										<Select.Content>
											{#each operators as op}
												<Select.Item value={op.value}>{op.label}</Select.Item>
											{/each}
										</Select.Content>
									</Select.Root>

									<!-- Value -->
									{#if operatorRequiresValue(rule.operator)}
										<Input
											value={typeof rule.value === 'string' ? rule.value : ''}
											oninput={(e) => updateRule(groupIndex, ruleIndex, { value: e.currentTarget.value })}
											disabled={disabled || value.killSwitch}
											placeholder="Value"
											class="flex-1 h-9"
										/>
									{:else}
										<span class="text-sm text-muted-foreground italic flex-1">
											No value needed
										</span>
									{/if}

									<!-- Remove -->
									<Button
										variant="ghost"
										size="icon"
										onclick={() => removeRule(groupIndex, ruleIndex)}
										disabled={disabled || value.killSwitch}
										aria-label="Remove rule"
									>
										<Trash2 class="h-4 w-4 text-destructive" />
									</Button>
								</div>
							{/each}

							{#if group.rules.length === 0}
								<p class="text-sm text-muted-foreground italic">No rules in this group</p>
							{/if}
						</div>
					</div>
				{/each}
			{/if}
		</Card.Content>
	</Card.Root>

	<!-- Force Enabled/Disabled Segments -->
	<div class="grid gap-4 md:grid-cols-2">
		<Card.Root>
			<Card.Header class="py-3">
				<Card.Title class="text-sm font-medium">Force Enabled Segments</Card.Title>
			</Card.Header>
			<Card.Content>
				<div class="space-y-2">
					<Input
						value={(value.forceEnabledSegments ?? []).join(', ')}
						oninput={(e) => {
							const segments = e.currentTarget.value
								.split(',')
								.map((s) => s.trim())
								.filter(Boolean);
							updateFlag({ forceEnabledSegments: segments });
						}}
						disabled={disabled || value.killSwitch}
						placeholder="segment1, segment2"
					/>
					<p class="text-xs text-muted-foreground">Comma-separated segment IDs</p>
				</div>
			</Card.Content>
		</Card.Root>

		<Card.Root>
			<Card.Header class="py-3">
				<Card.Title class="text-sm font-medium">Force Disabled Segments</Card.Title>
			</Card.Header>
			<Card.Content>
				<div class="space-y-2">
					<Input
						value={(value.forceDisabledSegments ?? []).join(', ')}
						oninput={(e) => {
							const segments = e.currentTarget.value
								.split(',')
								.map((s) => s.trim())
								.filter(Boolean);
							updateFlag({ forceDisabledSegments: segments });
						}}
						disabled={disabled || value.killSwitch}
						placeholder="segment1, segment2"
					/>
					<p class="text-xs text-muted-foreground">Comma-separated segment IDs</p>
				</div>
			</Card.Content>
		</Card.Root>
	</div>

	<!-- Info Alert -->
	<Alert class="bg-blue-50 dark:bg-blue-950 border-blue-200 dark:border-blue-800">
		<Settings2 class="h-4 w-4 text-blue-600 dark:text-blue-400" />
		<AlertTitle class="text-blue-800 dark:text-blue-200">Rule Evaluation</AlertTitle>
		<AlertDescription class="text-blue-700 dark:text-blue-300">
			Rules are evaluated in order. The first matching rule group determines the feature state.
			Groups are combined with OR logic, rules within groups use the selected operator (AND/OR).
		</AlertDescription>
	</Alert>
</div>
