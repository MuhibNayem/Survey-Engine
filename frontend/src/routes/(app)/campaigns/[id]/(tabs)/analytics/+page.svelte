<script lang="ts">
	import api from '$lib/api';
	import { page } from '$app/state';
	import { onMount } from 'svelte';
	import { goto } from '$app/navigation';
	import * as Card from '$lib/components/ui/card';
	import { Button } from '$lib/components/ui/button';
	import { Badge } from '$lib/components/ui/badge';
	import { Input } from '$lib/components/ui/input';
	import * as Select from '$lib/components/ui/select';
	import { ProgressBar } from '$lib/components/ui/progress-bar';
	import { Skeleton } from '$lib/components/ui/skeleton';
	import { DateRangePicker } from '$lib/components/date-range-picker';
	import {
		ArrowLeft,
		BarChart3,
		Users,
		Clock,
		CheckCircle2,
		Lock,
		MousePointerClick,
		FileDown,
		ArrowRight,
		Download,
		Calendar
	} from 'lucide-svelte';
	import * as DropdownMenu from '$lib/components/ui/dropdown-menu';
	import BarChartComponent from '$lib/components/charts/BarChart.svelte';
	import PieChartComponent from '$lib/components/charts/PieChart.svelte';
	import RadarChartComponent from '$lib/components/charts/RadarChart.svelte';
	import LineChartComponent from '$lib/components/charts/LineChart.svelte';
	import { Filter } from 'lucide-svelte';
	import { exportToCSV, createPrintableTable } from '$lib/utils/export';
	import type {
		CampaignResponse,
		AnalyticsResponse,
		ScoreDistributionResponse,
		QuestionAnalyticsResponse,
		CampaignPreviewResponse,
		TemporalAnalyticsResponse,
		FullCampaignAnalyticsResponse
	} from '$lib/types';

    let campaign = $state<CampaignResponse | null>(null);
    let campaignPreview = $state<CampaignPreviewResponse | null>(null);
    let analytics = $state<AnalyticsResponse | null>(null);
    let scoreDist = $state<ScoreDistributionResponse | null>(null);
    let trends = $state<TemporalAnalyticsResponse | null>(null);
    let qMap = $state<Record<string, QuestionAnalyticsResponse>>({});
    
    // Comparison State
    let viewMode = $state<'single' | 'compare'>('single');
    let comparisonData = $state<Record<string, FullCampaignAnalyticsResponse>>({});
    let segments = $state<Array<{id: string, name: string, filters: Record<string, string>, color: string}>>([
        { id: '1', name: 'Segment A', filters: {}, color: '#3b82f6' },
        { id: '2', name: 'Segment B', filters: {}, color: '#f59e0b' }
    ]);
    
    let loading = $state(true);
    let error = $state<string | null>(null);
    let metadataFilters = $state<Record<string, string>>({});
    let initialLoadComplete = $state(false);

    // Date Range State
    let startDate = $state(new Date(Date.now() - 30 * 24 * 60 * 60 * 1000).toISOString().split('T')[0]);
    let endDate = $state(new Date().toISOString().split('T')[0]);

    // Export Functions
    function handleExportSummary() {
        if (!analytics) return;
        const data = [{
            metric: 'Total Responses',
            value: analytics.totalResponses,
            completionRate: `${(analytics.completionRate * 100).toFixed(1)}%`,
            avgCompletionTime: 'N/A',
            avgScore: 'N/A'
        }];
        exportToCSV(
            data,
            `analytics-summary-${campaignId}`,
            [
                { key: 'metric', label: 'Metric' },
                { key: 'value', label: 'Value' },
                { key: 'completionRate', label: 'Completion Rate' },
                { key: 'avgCompletionTime', label: 'Avg Completion Time' },
                { key: 'avgScore', label: 'Average Score' }
            ]
        );
    }

    function handleExportQuestions() {
        if (!qMap || Object.keys(qMap).length === 0) return;
        const data = Object.values(qMap).map((q) => ({
            questionId: q.questionId,
            questionType: q.questionType,
            totalAnswers: q.totalAnswers,
            averageScore: q.averageScore?.toFixed(2) ?? 'N/A',
            completionRate: 'N/A'
        }));
        exportToCSV(
            data,
            `analytics-questions-${campaignId}`,
            [
                { key: 'questionId', label: 'Question ID' },
                { key: 'questionText', label: 'Question Text' },
                { key: 'totalResponses', label: 'Responses' },
                { key: 'averageScore', label: 'Avg Score' },
                { key: 'completionRate', label: 'Completion Rate' }
            ]
        );
    }

    function handleExportPDF() {
        if (!analytics || !campaign) return;
        const content = createPrintableTable(
            [{
                campaign: campaign.name,
                totalResponses: analytics.totalResponses,
                completionRate: `${(analytics.completionRate * 100).toFixed(1)}%`,
                avgScore: 'N/A',
                dateRange: `${startDate} - ${endDate}`
            }],
            [
                { key: 'campaign', label: 'Campaign' },
                { key: 'totalResponses', label: 'Total Responses' },
                { key: 'completionRate', label: 'Completion Rate' },
                { key: 'avgScore', label: 'Avg Score' },
                { key: 'dateRange', label: 'Date Range' }
            ],
            `Analytics Report - ${campaign.name}`
        );
        // PDF export - use browser print for now
        window.print();
    }

    const presetColors = ['#3b82f6', '#f59e0b', '#10b981', '#ef4444', '#8b5cf6'];

    const campaignId = $derived(page.params.id);

    async function refetchAnalytics() {
        if (!campaignId) return;
        loading = true;
        error = null;
        try {
            if (viewMode === 'single') {
                const params = new URLSearchParams();
                Object.entries(metadataFilters).forEach(([key, value]) => {
                    if (value && value.trim() !== '') {
                        params.append(`metadata.${key}`, value.trim());
                    }
                });
                const queryAppend = params.toString() ? '?' + params.toString() : '';
                
                const res = await api.get<FullCampaignAnalyticsResponse>(`/analytics/campaigns/${campaignId}/full-report${queryAppend}`);
                const report = res.data;
                
                analytics = report.summary as unknown as AnalyticsResponse;
                scoreDist = report.scoreDistribution;
                trends = report.temporalTrends;
                qMap = report.questionAnalytics || {};
            } else {
                // Comparison Mode Fetch
                const segmentPayloads = segments.map(s => {
                    // Extract only non-empty filters
                    const validFilters: Record<string, string> = {};
                    Object.entries(s.filters).forEach(([key, val]) => {
                        if (val && val.toString().trim() !== '') {
                            validFilters[key] = val.toString().trim();
                        }
                    });
                    
                    return {
                        name: s.name,
                        metadataFilters: validFilters
                    };
                }); 

                const hasFilters = segmentPayloads.some(s => Object.keys(s.metadataFilters).length > 0);
                if (!hasFilters) {
                    comparisonData = {};
                    analytics = null as any; 
                    return; // Skip API call if no segments have actual data filled
                }

                const payload = { segments: segmentPayloads.filter(s => Object.keys(s.metadataFilters).length > 0 || s.name) };
                const res = await api.post<any>(`/analytics/campaigns/${campaignId}/compare`, payload);
                comparisonData = res.data.segmentReports;
                
                // Set first segment as primary summary reference
                const primaryKey = Object.keys(comparisonData)[0];
                if (primaryKey) {
                    analytics = comparisonData[primaryKey].summary as unknown as AnalyticsResponse;
                }
            }
        } catch (err) {
            error = "Failed to load analytics data.";
        } finally {
            loading = false;
        }
    }

    async function loadAnalytics() {
        loading = true;
        error = null;
        try {
            const [cRes, previewRes] = await Promise.all([
                api.get<CampaignResponse>(`/campaigns/${campaignId}`),
                api.get<CampaignPreviewResponse>(`/campaigns/${campaignId}/preview`),
            ]);
            campaign = cRes.data;
            campaignPreview = previewRes.data;
            
            await refetchAnalytics();
            
            initialLoadComplete = true;
        } catch (err) {
            error = "Failed to load analytics data.";
        } finally {
            loading = false;
        }
    }

    onMount(loadAnalytics);

    function statusBadgeVariant(status: string) {
        switch (status) {
            case "ACTIVE":
                return "default" as const;
            case "COMPLETED":
                return "secondary" as const;
            default:
                return "outline" as const;
        }
    }
</script>

<svelte:head>
    <title>{campaign?.name ?? "Campaign"} Analytics — Survey Engine</title>
</svelte:head>

<div class="space-y-6">
    <!-- Controls Header -->
    <div
        class="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between"
    >
        <div>
            <h2 class="text-lg font-semibold tracking-tight flex items-center gap-2">
                <BarChart3 class="h-5 w-5 text-muted-foreground" />
                Analytics Overview
            </h2>
            <p class="text-sm text-muted-foreground">Analyze responses, filter data, and export reports.</p>
        </div>
        <div class="flex gap-2">
            <!-- Date Range Picker -->
            <DateRangePicker
                startDate={startDate}
                endDate={endDate}
                onChange={(range) => {
                    startDate = range.start;
                    endDate = range.end;
                    refetchAnalytics();
                }}
                size="sm"
            />

            <!-- Export Dropdown -->
            <DropdownMenu.Root>
                <DropdownMenu.Trigger>
                    <Button
                        variant="outline"
                        disabled={loading || !analytics || analytics.totalResponses === 0}
                    >
                        <Download class="mr-2 h-4 w-4" aria-hidden="true" />
                        Export
                    </Button>
                </DropdownMenu.Trigger>
                <DropdownMenu.Content align="end">
                    <DropdownMenu.Item onclick={handleExportSummary}>
                        <FileDown class="mr-2 h-4 w-4" aria-hidden="true" />
                        Export Summary (CSV)
                    </DropdownMenu.Item>
                    <DropdownMenu.Item onclick={handleExportQuestions}>
                        <FileDown class="mr-2 h-4 w-4" aria-hidden="true" />
                        Export Questions (CSV)
                    </DropdownMenu.Item>
                    <DropdownMenu.Separator />
                    <DropdownMenu.Item onclick={handleExportPDF}>
                        <BarChart3 class="mr-2 h-4 w-4" aria-hidden="true" />
                        Export Report (PDF)
                    </DropdownMenu.Item>
                </DropdownMenu.Content>
            </DropdownMenu.Root>
        </div>
    </div>

    <!-- Dynamic Metadata Filters / Segment Builder -->
    {#if campaign?.dataCollectionFields && campaign.dataCollectionFields.some((f) => f.enabled)}
        <div class="bg-card border rounded-lg p-4 space-y-4">
            <div class="flex items-center justify-between border-b pb-4">
                <div class="space-y-1">
                    <h3 class="font-medium leading-none">Analysis Mode</h3>
                    <p class="text-sm text-muted-foreground">Toggle between full campaign overview or cross-segment comparison.</p>
                </div>
                <!-- Mode Toggle -->
                <div class="flex items-center gap-1 bg-muted p-1 rounded-md">
                    <button 
                        class="px-4 py-1.5 text-sm font-medium rounded-sm transition-colors {viewMode === 'single' ? 'bg-background shadow-sm text-foreground' : 'text-muted-foreground hover:text-foreground'}"
                        onclick={() => { viewMode = 'single'; refetchAnalytics(); }}
                    >Overall View</button>
                    <button 
                        class="px-4 py-1.5 text-sm font-medium rounded-sm transition-colors {viewMode === 'compare' ? 'bg-background shadow-sm text-foreground' : 'text-muted-foreground hover:text-foreground'}"
                        onclick={() => { viewMode = 'compare'; refetchAnalytics(); }}
                    >Compare Segments</button>
                </div>
            </div>

            {#if viewMode === 'single'}
                <div class="flex flex-row flex-wrap gap-3 items-center pt-2">
                    <span class="text-xs font-semibold text-muted-foreground uppercase tracking-wider mr-2">Global Filter By:</span>
                    {#each campaign.dataCollectionFields as field}
                        <div class="flex items-center gap-2">
                            {#if field.enabled && (field.fieldType === 'TEXT' || field.fieldType === 'EMAIL' || field.fieldType === 'PHONE' || field.fieldType === 'NUMBER' || field.fieldType === 'TEXTAREA')}
                                <Input 
                                    type={field.fieldType.toLowerCase() === 'number' ? 'number' : 'text'}
                                    placeholder={`Filter by ${field.label}...`}
                                    bind:value={metadataFilters[field.fieldKey]}
                                    class="w-48 h-9 text-sm"
                                />
                            {/if}
                        </div>
                    {/each}
                    {#if Object.values(metadataFilters).some(v => v !== undefined && v.trim() !== '')}
                        <Button 
                            variant="ghost" 
                            size="sm" 
                            class="h-9 px-2 text-muted-foreground hover:text-foreground"
                            onclick={() => {
                                metadataFilters = {};
                                refetchAnalytics();
                            }}
                        >
                            Clear Filters
                        </Button>
                    {/if}
                </div>
            {:else}
                <div class="space-y-4 pt-2">
                    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
                        {#each segments as segment, i}
                            <div class="border rounded-md p-3 space-y-3 relative group">
                                <div class="flex items-center gap-2">
                                    <div class="w-3 h-3 rounded-full" style="background-color: {segment.color}"></div>
                                    <Input bind:value={segment.name} class="h-8 font-medium border-transparent hover:border-input focus-visible:ring-1" />
                                    {#if segments.length > 2}
                                        <button 
                                            class="absolute top-3 right-3 text-muted-foreground hover:text-destructive opacity-0 group-hover:opacity-100 transition-opacity"
                                            onclick={() => {
                                                segments = segments.filter(s => s.id !== segment.id);
                                                refetchAnalytics();
                                            }}
                                        >×</button>
                                    {/if}
                                </div>
                                <div class="space-y-2">
                                    {#each campaign.dataCollectionFields.filter((f) => f.enabled) as field}
                                        <div class="text-xs text-muted-foreground mb-1">{field.label}</div>
                                        <Input 
                                            type={field.fieldType.toLowerCase() === 'number' ? 'number' : 'text'}
                                            placeholder={`Any ${field.label}...`}
                                            bind:value={segment.filters[field.fieldKey]}
                                            class="w-full h-8 text-sm"
                                        />
                                    {/each}
                                </div>
                            </div>
                        {/each}
                        
                        {#if segments.length < 5}
                            <button 
                                class="border border-dashed rounded-md flex flex-col items-center justify-center text-muted-foreground hover:text-foreground hover:border-muted-foreground transition-colors p-4 min-h-[150px]"
                                onclick={() => {
                                    segments = [...segments, { 
                                        id: Date.now().toString(), 
                                        name: `Segment ${String.fromCharCode(65 + segments.length)}`, 
                                        filters: {}, 
                                        color: presetColors[segments.length % presetColors.length] 
                                    }];
                                }}
                            >
                                <span class="text-2xl mb-2">+</span>
                                <span class="text-sm font-medium">Add Segment</span>
                            </button>
                        {/if}
                    </div>
                </div>
            {/if}
            
            <div class="flex justify-end pt-2">
                <Button 
                    variant="default" 
                    size="sm" 
                    onclick={refetchAnalytics}
                    disabled={loading}
                >
                    Apply Analysis
                </Button>
            </div>
        </div>
    {/if}

    {#if loading}
        <div class="space-y-6">
            <div class="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
                <div class="flex items-center gap-3">
                    <Skeleton class="h-9 w-9" />
                    <div class="space-y-2">
                        <Skeleton class="h-8 w-[250px]" />
                        <Skeleton class="h-4 w-[150px]" />
                    </div>
                </div>
                <div class="flex gap-2">
                    <Skeleton class="h-9 w-[120px]" />
                    <Skeleton class="h-9 w-[140px]" />
                </div>
            </div>
            <div class="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
                {#each Array(4) as _}
                    <Card.Root>
                        <Card.Content class="pt-6">
                            <div class="flex items-center justify-between">
                                <div class="space-y-2">
                                    <Skeleton class="h-4 w-[100px]" />
                                    <Skeleton class="h-8 w-[60px]" />
                                </div>
                                <Skeleton class="h-12 w-12 rounded-xl" />
                            </div>
                        </Card.Content>
                    </Card.Root>
                {/each}
            </div>
            <div class="grid gap-6 lg:grid-cols-2">
                {#each Array(2) as _}
                    <Card.Root>
                        <Card.Header>
                            <div class="space-y-2">
                                <Skeleton class="h-6 w-[150px]" />
                                <Skeleton class="h-4 w-[200px]" />
                            </div>
                        </Card.Header>
                        <Card.Content>
                            <Skeleton class="h-[200px] w-full" />
                        </Card.Content>
                    </Card.Root>
                {/each}
            </div>
        </div>
    {:else if error}
        <Card.Root class="border-destructive/30 bg-destructive/10">
            <Card.Content class="py-10 text-center text-destructive">
                <p>{error}</p>
                <Button variant="outline" class="mt-4" onclick={loadAnalytics}
                    >Retry</Button
                >
            </Card.Content>
        </Card.Root>
    {:else if viewMode === 'compare' && Object.keys(comparisonData).length === 0}
        <Card.Root class="border-indigo-200 bg-indigo-50/50 mt-8 border-dashed border-2">
            <Card.Content class="py-16 flex flex-col items-center justify-center text-center">
                <div class="h-12 w-12 rounded-full bg-indigo-100 flex items-center justify-center mb-4">
                    <Filter class="h-6 w-6 text-indigo-600" />
                </div>
                <h3 class="text-xl font-semibold text-indigo-900 mb-2">Configure Segments to Compare</h3>
                <p class="text-indigo-700/70 max-w-md mx-auto text-sm">
                    Please use the segment builder above to select specific demographic filters for Segment A and Segment B. Click "Apply Analysis" when you're ready to see the comparison.
                </p>
            </Card.Content>
        </Card.Root>
    {:else if analytics}
        <!-- Quick Stats -->
        <div class="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
            <Card.Root>
                <Card.Content class="pt-6">
                    <div class="flex items-center justify-between">
                        <div>
                            <p
                                class="text-sm font-medium text-muted-foreground"
                            >
                                Total Responses
                            </p>
                            <p class="mt-1 text-3xl font-bold text-foreground">
                                {analytics.totalResponses}
                            </p>
                        </div>
                        <div
                            class="flex h-12 w-12 items-center justify-center rounded-xl bg-blue-500/10"
                        >
                            <Users class="h-6 w-6 text-blue-500" />
                        </div>
                    </div>
                </Card.Content>
            </Card.Root>

            <Card.Root>
                <Card.Content class="pt-6">
                    <div class="flex items-center justify-between">
                        <div>
                            <p
                                class="text-sm font-medium text-muted-foreground"
                            >
                                Completion Rate
                            </p>
                            <p class="mt-1 text-3xl font-bold text-foreground">
                                {(analytics.completionRate * 100).toFixed(1)}%
                            </p>
                        </div>
                        <div
                            class="flex h-12 w-12 items-center justify-center rounded-xl bg-emerald-500/10"
                        >
                            <CheckCircle2 class="h-6 w-6 text-emerald-500" />
                        </div>
                    </div>
                </Card.Content>
            </Card.Root>

            <Card.Root>
                <Card.Content class="pt-6">
                    <div class="flex items-center justify-between">
                        <div>
                            <p
                                class="text-sm font-medium text-muted-foreground"
                            >
                                In Progress
                            </p>
                            <p class="mt-1 text-3xl font-bold text-foreground">
                                {analytics.inProgressCount}
                            </p>
                        </div>
                        <div
                            class="flex h-12 w-12 items-center justify-center rounded-xl bg-amber-500/10"
                        >
                            <Clock class="h-6 w-6 text-amber-500" />
                        </div>
                    </div>
                </Card.Content>
            </Card.Root>

            <Card.Root>
                <Card.Content class="pt-6">
                    <div class="flex items-center justify-between">
                        <div>
                            <p
                                class="text-sm font-medium text-muted-foreground"
                            >
                                Locked
                            </p>
                            <p class="mt-1 text-3xl font-bold text-foreground">
                                {analytics.lockedCount}
                            </p>
                        </div>
                        <div
                            class="flex h-12 w-12 items-center justify-center rounded-xl bg-purple-500/10"
                        >
                            <Lock class="h-6 w-6 text-purple-500" />
                        </div>
                    </div>
                </Card.Content>
            </Card.Root>
        </div>

        {#if viewMode === 'single' && trends && trends.trendPoints.length > 0}
            <!-- Temporal Trend Analytics -->
            <div class="mt-8">
                <Card.Root class="shadow-md overflow-hidden">
                    <Card.Header class="bg-blue-50 dark:bg-blue-950/20 border-b border-blue-100 dark:border-blue-900/50">
                        <div class="flex justify-between items-center">
                            <div>
                                <Card.Title class="text-blue-900 dark:text-blue-200">Responses Over Time</Card.Title>
                                <Card.Description class="text-blue-700/70 dark:text-blue-300/60">
                                    Chronological submission volume based on current segments.
                                </Card.Description>
                            </div>
                            <div class="text-right">
                                <span class="text-sm text-blue-600 dark:text-blue-400 font-semibold">Peak Submissions</span>
                                <div class="text-3xl font-bold text-blue-700 dark:text-blue-300">
                                    {Math.max(...trends.trendPoints.map(t => t.count))}
                                </div>
                            </div>
                        </div>
                    </Card.Header>
                    <Card.Content class="h-[300px] pt-6">
                        <LineChartComponent data={trends.trendPoints} color="#3b82f6" />
                    </Card.Content>
                </Card.Root>
            </div>
        {:else if viewMode === 'compare' && Object.keys(comparisonData).length > 0}
            <!-- Temporal Trend Comparison -->
            <div class="mt-8">
                <Card.Root class="shadow-md overflow-hidden">
                    <Card.Header class="bg-blue-50 dark:bg-blue-950/20 border-b border-blue-100 dark:border-blue-900/50">
                        <Card.Title class="text-blue-900 dark:text-blue-200">Responses Over Time (Comparison)</Card.Title>
                        <Card.Description class="text-blue-700/70 dark:text-blue-300/60">
                            Comparative chronological submission volume between segments.
                        </Card.Description>
                    </Card.Header>
                    <Card.Content class="h-[300px] pt-6">
                        <LineChartComponent 
                            datasets={segments.filter(s => comparisonData[s.name]).map((s) => ({
                                label: s.name,
                                color: s.color,
                                data: comparisonData[s.name]?.temporalTrends?.trendPoints || []
                            }))} 
                        />
                    </Card.Content>
                </Card.Root>
            </div>
        {/if}

        <!-- Funnel & Details -->
        <div class="grid gap-6 lg:grid-cols-2 mt-8">
            <!-- Response Funnel -->
            <Card.Root>
                <Card.Header>
                    <Card.Title>Response Funnel</Card.Title>
                    <Card.Description
                        >Conversion from start to submission.</Card.Description
                    >
                </Card.Header>
                <Card.Content class="space-y-8">
                    {#if analytics.totalResponses === 0}
                        <div
                            class="flex h-32 items-center justify-center text-sm text-muted-foreground"
                        >
                            No data to display yet.
                        </div>
                    {:else}
                        {@const max = analytics.totalResponses}

                        <!-- Started -->
                        <div class="space-y-2 relative">
                            <div
                                class="flex items-center justify-between text-sm font-medium"
                            >
                                <span class="flex items-center gap-2"
                                    ><MousePointerClick
                                        class="h-4 w-4 text-muted-foreground"
                                    /> Total Started</span
                                >
                                <span>{max}</span>
                            </div>
                            <ProgressBar
                                value={max}
                                {max}
                                colorClass="bg-blue-500"
                            />
                            <p class="text-xs text-muted-foreground text-right">
                                100%
                            </p>
                        </div>

                        <!-- Submitted -->
                        <div class="space-y-2 relative">
                            <div
                                class="flex items-center justify-between text-sm font-medium"
                            >
                                <span class="flex items-center gap-2"
                                    ><CheckCircle2
                                        class="h-4 w-4 text-emerald-500"
                                    /> Completed & Submitted</span
                                >
                                <span
                                    >{analytics.submittedCount +
                                        analytics.lockedCount}</span
                                >
                            </div>
                            <ProgressBar
                                value={analytics.submittedCount +
                                    analytics.lockedCount}
                                {max}
                                colorClass="bg-emerald-500"
                            />
                            <p class="text-xs text-muted-foreground text-right">
                                {Math.round(
                                    ((analytics.submittedCount +
                                        analytics.lockedCount) /
                                        max) *
                                        100,
                                )}% of started
                            </p>
                        </div>

                        <!-- Locked -->
                        {#if analytics.lockedCount > 0}
                            <div
                                class="space-y-2 relative opacity-80 pl-6 border-l-2 border-border ml-2"
                            >
                                <div
                                    class="flex items-center justify-between text-sm font-medium"
                                >
                                    <span class="flex items-center gap-2"
                                        ><Lock
                                            class="h-3 w-3 text-purple-500"
                                        /> Locked (Finalized)</span
                                    >
                                    <span>{analytics.lockedCount}</span>
                                </div>
                                <ProgressBar
                                    value={analytics.lockedCount}
                                    max={analytics.submittedCount +
                                        analytics.lockedCount}
                                    colorClass="bg-purple-500"
                                />
                            </div>
                        {/if}

                        <!-- Drop-off -->
                        {#if analytics.inProgressCount > 0}
                            <div
                                class="space-y-2 relative pt-2 border-t border-border mt-4"
                            >
                                <div
                                    class="flex items-center justify-between text-sm font-medium text-amber-600 dark:text-amber-500"
                                >
                                    <span class="flex items-center gap-2"
                                        ><Clock class="h-4 w-4" /> Incomplete / Dropped
                                        Off</span
                                    >
                                    <span>{analytics.inProgressCount}</span>
                                </div>
                                <p class="text-xs text-muted-foreground">
                                    {Math.round(
                                        (analytics.inProgressCount / max) * 100,
                                    )}% of users started but haven't finished
                                    yet.
                                </p>
                            </div>
                        {/if}
                    {/if}
                </Card.Content>
            </Card.Root>

            <!-- Actionable Links -->
            <div class="space-y-4">
                <Card.Root>
                    <Card.Header>
                        <Card.Title>Individual Responses</Card.Title>
                        <Card.Description
                            >View row-by-row response data and lock status.</Card.Description
                        >
                    </Card.Header>
                    <Card.Content>
                        <p class="text-sm text-muted-foreground mb-4">
                            Navigate to the Response Management page to view
                            individual answers, search for specific respondents,
                            and review response status history.
                        </p>
                        <Button
                            class="w-full"
                            onclick={() => goto(`/campaigns/${campaignId}/responses`)}
                        >
                            View Responses Ledger
                            <ArrowRight class="ml-2 h-4 w-4" />
                        </Button>
                    </Card.Content>
                </Card.Root>

            </div>
        </div>

        <!-- Advanced Analytics: Score Distribution -->
        {#if viewMode === 'single' && scoreDist && scoreDist.totalScoredResponses > 0}
            <div class="mt-8">
                <Card.Root class="shadow-md overflow-hidden">
                    <Card.Header class="bg-indigo-50 dark:bg-indigo-950/20 border-b border-indigo-100 dark:border-indigo-900/50">
                        <div class="flex justify-between items-center">
                            <div>
                                <Card.Title class="text-indigo-900 dark:text-indigo-200">Score Distribution Curve</Card.Title>
                                <Card.Description class="text-indigo-700/70 dark:text-indigo-300/60">
                                    Histogram tracking the bell curve of respondent final scores. Median: {scoreDist.medianScore.toFixed(2)}
                                </Card.Description>
                            </div>
                            <div class="text-right">
                                <span class="text-sm text-indigo-600 dark:text-indigo-400 font-semibold">Avg Score</span>
                                <div class="text-3xl font-bold text-indigo-700 dark:text-indigo-300">
                                    {scoreDist.averageScore.toFixed(2)}
                                </div>
                            </div>
                        </div>
                    </Card.Header>
                    <Card.Content class="h-[400px] pt-6">
                        <BarChartComponent 
                            data={scoreDist.scoreBuckets.map((b: any) => ({
                                label: b.rangeLabel,
                                value: b.count,
                                percentage: b.percentage
                            }))}
                            color="#818cf8"
                        />
                    </Card.Content>
                </Card.Root>
            </div>
        {:else if viewMode === 'compare' && Object.keys(comparisonData).length > 0}
            <div class="mt-8">
                <Card.Root class="shadow-md overflow-hidden">
                    <Card.Header class="bg-indigo-50 dark:bg-indigo-950/20 border-b border-indigo-100 dark:border-indigo-900/50">
                        <Card.Title class="text-indigo-900 dark:text-indigo-200">Score Distribution (Comparison)</Card.Title>
                        <Card.Description class="text-indigo-700/70 dark:text-indigo-300/60">
                            Comparative bucket analysis of scores between segments.
                        </Card.Description>
                    </Card.Header>
                    <Card.Content class="h-[400px] pt-6">
                        <BarChartComponent 
                            datasets={segments.filter(s => comparisonData[s.name] && comparisonData[s.name].scoreDistribution?.scoreBuckets?.length > 0).map((s) => ({
                                label: s.name,
                                color: s.color,
                                data: comparisonData[s.name].scoreDistribution.scoreBuckets.map((b: any) => ({
                                    label: b.rangeLabel,
                                    value: b.count,
                                    percentage: b.percentage
                                }))
                            }))}
                        />
                    </Card.Content>
                </Card.Root>
            </div>
        {/if}

        <!-- Advanced Analytics: Question Frequencies -->
        {#if campaignPreview && (Object.keys(qMap).length > 0 || Object.keys(comparisonData).length > 0)}
            <div class="mt-8">
                <h2 class="text-xl font-semibold border-b pb-2 mb-6">Question Breakdown</h2>
                <div class="grid grid-cols-1 lg:grid-cols-2 gap-6">
                    {#each campaignPreview.pages.flatMap((p: any) => p.questions).filter((q: any) => q.type === 'SINGLE_CHOICE' || q.type === 'MULTIPLE_CHOICE' || q.type === 'RATING_SCALE') as question, i}
                        <Card.Root class="shadow-sm flex flex-col">
                            <Card.Header class="pb-2">
                                <div class="flex justify-between items-start gap-4">
                                    <div>
                                        <h3 class="font-medium text-sm text-muted-foreground mb-1">Question {i + 1}</h3>
                                        <Card.Title class="text-base leading-tight">
                                            {question.text}
                                        </Card.Title>
                                    </div>
                                    <Badge variant="secondary">{question.type.replace('_', ' ')}</Badge>
                                </div>
                            </Card.Header>
                            <Card.Content class="flex-1 mt-4">
                                {#if viewMode === 'single' && qMap[question.questionId]}
                                    {@const qData = qMap[question.questionId]}
                                    {@const chartData = qData.optionFrequencies.map((opt: any) => ({
                                        label: opt.optionValue.length > 30 ? opt.optionValue.substring(0,30) + '...' : opt.optionValue,
                                        value: opt.count,
                                        percentage: opt.percentage
                                    }))}
                                    
                                    {#if chartData.length > 0}
                                        <div class="h-[250px] mt-4">
                                            {#if question.type === 'SINGLE_CHOICE' && chartData.length <= 8}
                                                <PieChartComponent data={chartData} />
                                            {:else}
                                                <BarChartComponent data={chartData} />
                                            {/if}
                                        </div>
                                    {:else}
                                        <div class="h-[200px] flex items-center justify-center text-muted-foreground text-sm border-2 border-dashed rounded-lg bg-muted/20">
                                            No quantifiable data for this question format yet.
                                        </div>
                                    {/if}
                                    
                                {:else if viewMode === 'compare' && Object.keys(comparisonData).length > 0}
                                    {@const compareDatasets = segments.filter(s => comparisonData[s.name] && comparisonData[s.name].questionAnalytics[question.questionId]).map(s => ({
                                        label: s.name,
                                        color: s.color,
                                        data: comparisonData[s.name].questionAnalytics[question.questionId].optionFrequencies.map((opt: any) => ({
                                            label: opt.optionValue.length > 30 ? opt.optionValue.substring(0,30) + '...' : opt.optionValue,
                                            value: opt.count,
                                            percentage: opt.percentage
                                        }))
                                    }))}
                                    
                                    {#if compareDatasets.some((ds: any) => ds.data.length > 0)}
                                        <div class="h-[250px] mt-4">
                                            {#if question.type === 'SINGLE_CHOICE' && compareDatasets.every((ds: any) => ds.data.length <= 8)}
                                                <RadarChartComponent datasets={compareDatasets} />
                                            {:else}
                                                <BarChartComponent datasets={compareDatasets} />
                                            {/if}
                                        </div>
                                    {:else}
                                        <div class="h-[200px] flex items-center justify-center text-muted-foreground text-sm border-2 border-dashed rounded-lg bg-muted/20">
                                            No cross-segment data available for comparison.
                                        </div>
                                    {/if}
                                {/if}
                            </Card.Content>
                        </Card.Root>
                    {/each}
                </div>
            </div>
        {/if}

    {/if}
</div>
