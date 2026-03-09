<script lang="ts">
	import { auth } from '$lib/stores/auth.svelte';
	import api from '$lib/api';
	import { onMount } from 'svelte';
	import { goto } from '$app/navigation';
	import * as Card from '$lib/components/ui/card';
	import { Badge } from '$lib/components/ui/badge';
	import { Button } from '$lib/components/ui/button';
	import { ProgressBar } from '$lib/components/ui/progress-bar';
	import { Skeleton } from '$lib/components/ui/skeleton';
	import { EmptyState } from '$lib/components/empty-state';
	import { Confetti } from '$lib/components/confetti';
	import {
		HelpCircle,
		FolderKanban,
		FileText,
		Megaphone,
		MessageSquareText,
		ArrowRight,
		CreditCard,
		BarChart3
	} from 'lucide-svelte';
	import type { SubscriptionResponse, CampaignResponse, AnalyticsResponse } from '$lib/types';

    type CampaignWithAnalytics = CampaignResponse & {
        analytics?: AnalyticsResponse;
    };

    let subscription = $state<SubscriptionResponse | null>(null);
    let recentCampaigns = $state<CampaignWithAnalytics[]>([]);
    let stats = $state({
        questions: "—",
        categories: "—",
        surveys: "—",
        campaigns: "—",
        responses: "—",
    });
    let loadingCampaigns = $state(true);

    // Confetti celebration for milestones
    let showConfetti = $state(false);
    let confettiTitle = $state('');
    let confettiMessage = $state('');
    let previousResponses = $state(0);

    const statCards = $derived([
        {
            label: "Questions",
            value: stats.questions,
            icon: HelpCircle,
            href: "/questions",
            color: "text-blue-500",
        },
        {
            label: "Categories",
            value: stats.categories,
            icon: FolderKanban,
            href: "/categories",
            color: "text-purple-500",
        },
        {
            label: "Surveys",
            value: stats.surveys,
            icon: FileText,
            href: "/surveys",
            color: "text-emerald-500",
        },
        {
            label: "Campaigns",
            value: stats.campaigns,
            icon: Megaphone,
            href: "/campaigns",
            color: "text-orange-500",
        },
        {
            label: "Responses",
            value: stats.responses,
            icon: MessageSquareText,
            href: "/responses",
            color: "text-pink-500",
        },
    ]);

    // Helper to extract content array and total from a Page or plain array response
    function unwrapPage<T>(data: any): { items: T[]; total: number } {
        if (!data) {
            return { items: [], total: 0 };
        }
        if (Array.isArray(data)) {
            return { items: data, total: data.length };
        }
        const content = Array.isArray(data.content) ? data.content : [];
        return {
            items: content,
            total: data.totalElements ?? content.length,
        };
    }

    onMount(async () => {
        // Load stats in parallel
        const [questions, categories, surveys, campaigns, sub] =
            await Promise.allSettled([
                api.get<unknown>("/questions?size=1"),
                api.get<unknown>("/categories?size=1"),
                api.get<unknown>("/surveys?size=1"),
                api.get<unknown>("/campaigns?size=5&sort=createdAt,desc"),
                api.get<SubscriptionResponse>("/admin/subscriptions/me"),
            ]);

        if (questions.status === "fulfilled") {
            const { total } = unwrapPage(questions.value.data);
            stats.questions = String(total);
        }
        if (categories.status === "fulfilled") {
            const { total } = unwrapPage(categories.value.data);
            stats.categories = String(total);
        }
        if (surveys.status === "fulfilled") {
            const { total } = unwrapPage(surveys.value.data);
            stats.surveys = String(total);
        }

        if (campaigns.status === "fulfilled") {
            const { items: camps, total } = unwrapPage<CampaignResponse>(
                campaigns.value.data,
            );
            stats.campaigns = String(total);

            // Already sorted by createdAt desc from the API, take up to 5
            const recent = camps.slice(0, 5);

            // Fetch analytics for recent active/completed campaigns
            const campaignsWithAnalytics = await Promise.all(
                recent.map(async (c): Promise<CampaignWithAnalytics> => {
                    if (c.status !== "DRAFT") {
                        try {
                            const { data } = await api.get<AnalyticsResponse>(
                                `/responses/analytics/${c.id}`,
                            );
                            return { ...c, analytics: data };
                        } catch {
                            return c;
                        }
                    }
                    return c;
                }),
            );
            recentCampaigns = campaignsWithAnalytics;

            // Estimate total responses (very rough, relies only on recent fetched analytics)
            let totalRes = 0;
            for (const c of campaignsWithAnalytics) {
                if (c.analytics) totalRes += c.analytics.totalResponses;
            }
            stats.responses = totalRes > 0 ? `${totalRes}+` : "0";

            // 🎉 Celebrate milestone reached (100 responses)
            const currentResponses = totalRes;
            if (previousResponses < 100 && currentResponses >= 100) {
                showConfetti = true;
                confettiTitle = '🎊 Milestone Reached!';
                confettiMessage = "You've collected 100 responses. Amazing work!";
                setTimeout(() => (showConfetti = false), 5500);
            }
            previousResponses = currentResponses;
        }

        loadingCampaigns = false;

        if (sub.status === "fulfilled") {
            subscription = sub.value.data;
        }
    });

    const planBadgeVariant = $derived(
        subscription?.status === "ACTIVE"
            ? ("default" as const)
            : ("secondary" as const),
    );

    function statusBadgeVariant(status: string) {
        switch (status) {
            case "ACTIVE":
                return "default" as const;
            case "DRAFT":
                return "secondary" as const;
            default:
                return "outline" as const;
        }
    }
</script>

<svelte:head>
    <title>Dashboard — Survey Engine</title>
</svelte:head>

<div class="space-y-8">
    <!-- Welcome -->
    <div>
        <h1 class="text-3xl font-bold tracking-tight text-foreground">
            Dashboard
        </h1>
        <p class="mt-1 text-muted-foreground">
            Welcome back, <span class="font-medium text-foreground"
                >{auth.user?.email ?? "Admin"}</span
            >
        </p>
    </div>

    <!-- Subscription Banner -->
    {#if subscription}
        <Card.Root
            class="border-primary/20 bg-gradient-to-r from-primary/5 via-transparent to-primary/5"
        >
            <Card.Content class="flex items-center justify-between py-4">
                <div class="flex items-center gap-3">
                    <div
                        class="flex h-10 w-10 items-center justify-center rounded-lg bg-primary/10"
                    >
                        <CreditCard class="h-5 w-5 text-primary" />
                    </div>
                    <div>
                        <div class="flex items-center gap-2">
                            <span class="font-semibold text-foreground"
                                >{subscription.plan} Plan</span
                            >
                            <Badge variant={planBadgeVariant}>
                                {subscription.status}
                            </Badge>
                        </div>
                        <p class="text-xs text-muted-foreground">
                            {subscription.maxCampaigns ?? "∞"} campaigns · {subscription.maxResponsesPerCampaign ??
                                "∞"} responses/campaign
                        </p>
                    </div>
                </div>
                <Button variant="outline" size="sm" href="/settings">
                    Manage
                    <ArrowRight class="ml-1 h-4 w-4" />
                </Button>
            </Card.Content>
        </Card.Root>
    {/if}

    <!-- Stat Cards -->
    {#if loadingCampaigns}
        <div class="grid gap-4 sm:grid-cols-2 lg:grid-cols-5">
            {#each Array(5) as _}
                <Card.Root>
                    <Card.Content class="pt-6">
                        <div class="flex items-center justify-between">
                            <div class="space-y-2">
                                <Skeleton class="h-4 w-[80px]" />
                                <Skeleton class="h-8 w-[60px]" />
                            </div>
                            <Skeleton class="h-12 w-12 rounded-xl" />
                        </div>
                    </Card.Content>
                </Card.Root>
            {/each}
        </div>
    {:else}
        <div class="grid gap-4 sm:grid-cols-2 lg:grid-cols-5">
            {#each statCards as card}
                <a href={card.href} class="group">
                    <Card.Root
                        class="transition-all duration-200 hover:shadow-md hover:border-primary/30 group-hover:-translate-y-0.5"
                    >
                        <Card.Content class="pt-6">
                            <div class="flex items-center justify-between">
                                <div>
                                    <p
                                        class="text-sm font-medium text-muted-foreground"
                                    >
                                        {card.label}
                                    </p>
                                    <p
                                        class="mt-1 text-3xl font-bold text-foreground"
                                    >
                                        {card.value}
                                    </p>
                                </div>
                                <div
                                    class="flex h-12 w-12 items-center justify-center rounded-xl bg-muted/50"
                                >
                                    <card.icon class="h-6 w-6 {card.color}" />
                                </div>
                            </div>
                        </Card.Content>
                    </Card.Root>
                </a>
            {/each}
        </div>
    {/if}

    <div class="grid gap-6 md:grid-cols-2 lg:grid-cols-3">
        <!-- Quick Actions -->
        <div class="lg:col-span-1">
            <h2 class="mb-4 text-lg font-semibold text-foreground">
                Quick Actions
            </h2>
            <div class="grid gap-3 sm:grid-cols-2 lg:grid-cols-1">
                <Button
                    variant="outline"
                    class="h-auto justify-start gap-3 p-4"
                    href="/questions"
                >
                    <div
                        class="flex h-9 w-9 items-center justify-center rounded-lg bg-blue-500/10"
                    >
                        <HelpCircle class="h-5 w-5 text-blue-500" />
                    </div>
                    <div class="text-left">
                        <p class="font-medium">New Question</p>
                        <p class="text-xs text-muted-foreground">
                            Add to question bank
                        </p>
                    </div>
                </Button>
                <Button
                    variant="outline"
                    class="h-auto justify-start gap-3 p-4"
                    href="/surveys"
                >
                    <div
                        class="flex h-9 w-9 items-center justify-center rounded-lg bg-emerald-500/10"
                    >
                        <FileText class="h-5 w-5 text-emerald-500" />
                    </div>
                    <div class="text-left">
                        <p class="font-medium">New Survey</p>
                        <p class="text-xs text-muted-foreground">
                            Design a survey
                        </p>
                    </div>
                </Button>
                <Button
                    variant="outline"
                    class="h-auto justify-start gap-3 p-4"
                    href="/campaigns"
                >
                    <div
                        class="flex h-9 w-9 items-center justify-center rounded-lg bg-orange-500/10"
                    >
                        <Megaphone class="h-5 w-5 text-orange-500" />
                    </div>
                    <div class="text-left">
                        <p class="font-medium">New Campaign</p>
                        <p class="text-xs text-muted-foreground">
                            Start collecting
                        </p>
                    </div>
                </Button>
            </div>
        </div>

        <!-- Recent Campaigns -->
        <div class="md:col-span-1 lg:col-span-2">
            <div class="flex items-center justify-between mb-4">
                <h2 class="text-lg font-semibold text-foreground">
                    Recent Campaigns
                </h2>
                <Button
                    variant="ghost"
                    size="sm"
                    href="/campaigns"
                    class="text-muted-foreground"
                >
                    View All <ArrowRight class="ml-1 h-4 w-4" />
                </Button>
            </div>

            {#if loadingCampaigns}
                <Card.Root>
                    <div class="divide-y divide-border">
                        {#each Array(5) as _}
                            <div class="flex flex-col sm:flex-row sm:items-center justify-between gap-4 p-4">
                                <div class="min-w-0 flex-1 space-y-2">
                                    <div class="flex items-center gap-2">
                                        <Skeleton class="h-5 w-[180px]" />
                                        <Skeleton class="h-4 w-[50px]" />
                                    </div>
                                    <Skeleton class="h-4 w-[250px]" />
                                </div>
                                <div class="flex items-center gap-6 sm:w-1/2 justify-end">
                                    <Skeleton class="h-8 w-24" />
                                    <Skeleton class="h-8 w-8" />
                                </div>
                            </div>
                        {/each}
                    </div>
                </Card.Root>
            			{:else if recentCampaigns.length === 0}
				<!-- Enhanced Empty State -->
				<EmptyState
					title="No campaigns yet"
					description="Create your first campaign to start collecting responses and viewing analytics here."
					actionLabel="Create Campaign"
					onAction={() => goto('/campaigns')}
					illustration="campaign"
				/>
            {:else}
                <Card.Root>
                    <div class="divide-y divide-border">
                        {#each recentCampaigns as campaign (campaign.id)}
                            <div
                                class="flex flex-col sm:flex-row sm:items-center justify-between gap-4 p-4 hover:bg-muted/30 transition-colors"
                            >
                                <div class="min-w-0 flex-1">
                                    <div class="flex items-center gap-2 mb-1">
                                        <p
                                            class="font-medium text-foreground truncate"
                                        >
                                            {campaign.name}
                                        </p>
                                        <Badge
                                            variant={statusBadgeVariant(
                                                campaign.status,
                                            )}
                                            class="text-[10px] px-1.5 py-0 h-4"
                                        >
                                            {campaign.status}
                                        </Badge>
                                    </div>
                                    <p
                                        class="text-xs text-muted-foreground truncate max-w-sm"
                                    >
                                        {campaign.description ||
                                            "No description"}
                                    </p>
                                </div>

                                <div
                                    class="flex items-center gap-6 sm:w-1/2 justify-end"
                                >
                                    {#if campaign.analytics}
                                        <div class="w-24 shrink-0">
                                            <div
                                                class="flex justify-between text-[10px] text-muted-foreground mb-1"
                                            >
                                                <span
                                                    >{campaign.analytics
                                                        .totalResponses} resp</span
                                                >
                                                <span
                                                    >{Math.round(
                                                        campaign.analytics
                                                            .completionRate *
                                                            100,
                                                    )}%</span
                                                >
                                            </div>
                                            <ProgressBar
                                                value={campaign.analytics
                                                    .completionRate * 100}
                                                max={100}
                                                class="!mt-0"
                                                colorClass="bg-blue-500"
                                            />
                                        </div>
                                    {:else if campaign.status !== "DRAFT"}
                                        <span
                                            class="text-xs text-muted-foreground"
                                            >No data</span
                                        >
                                    {/if}

                                    <Button
                                        variant="ghost"
                                        size="icon"
                                        onclick={() =>
                                            goto(
                                                campaign.status === "ACTIVE" ||
                                                    campaign.status ===
                                                        "COMPLETED"
                                                    ? `/campaigns/${campaign.id}/analytics`
                                                    : `/campaigns/${campaign.id}`,
                                            )}
                                    >
                                        {#if campaign.status === "ACTIVE" || campaign.status === "COMPLETED"}
                                            <BarChart3 class="h-4 w-4" />
                                        {:else}
                                            <ArrowRight class="h-4 w-4" />
                                        {/if}
                                    </Button>
                                </div>
                            </div>
                        {/each}
                    </div>
                </Card.Root>
            {/if}
        </div>
    </div>
</div>

<!-- 🎉 Confetti Celebration -->
{#if showConfetti}
    <Confetti
        fire={showConfetti}
        showBanner={true}
        title={confettiTitle}
        message={confettiMessage}
        particleCount={200}
        spread={100}
        startVelocity={60}
        duration={5000}
        colors={['#3b82f6', '#8b5cf6', '#ec4899', '#f59e0b', '#10b981']}
        onComplete={() => (showConfetti = false)}
    />
{/if}
