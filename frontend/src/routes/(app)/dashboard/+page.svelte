<script lang="ts">
    import { auth } from "$lib/stores/auth.svelte";
    import api from "$lib/api";
    import { onMount } from "svelte";
    import * as Card from "$lib/components/ui/card";
    import { Badge } from "$lib/components/ui/badge";
    import { Button } from "$lib/components/ui/button";
    import {
        HelpCircle,
        FolderKanban,
        FileText,
        Megaphone,
        MessageSquareText,
        ArrowRight,
        CreditCard,
        Sparkles,
    } from "lucide-svelte";
    import type { SubscriptionResponse } from "$lib/types";

    let subscription = $state<SubscriptionResponse | null>(null);
    let stats = $state({
        questions: "—",
        categories: "—",
        surveys: "—",
        campaigns: "—",
        responses: "—",
    });

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

    onMount(async () => {
        // Load stats in parallel
        const [questions, categories, surveys, campaigns] =
            await Promise.allSettled([
                api.get<unknown[]>("/questions"),
                api.get<unknown[]>("/categories"),
                api.get<unknown[]>("/surveys"),
                api.get<unknown[]>("/campaigns"),
            ]);

        if (questions.status === "fulfilled")
            stats.questions = String(questions.value.data.length);
        if (categories.status === "fulfilled")
            stats.categories = String(categories.value.data.length);
        if (surveys.status === "fulfilled")
            stats.surveys = String(surveys.value.data.length);
        if (campaigns.status === "fulfilled")
            stats.campaigns = String(campaigns.value.data.length);

        try {
            const { data } = await api.get<SubscriptionResponse>(
                "/admin/subscriptions/me",
            );
            subscription = data;
        } catch {
            // Subscription fetch may fail if not set up yet
        }
    });

    const planBadgeVariant = $derived(
        subscription?.status === "ACTIVE"
            ? ("default" as const)
            : ("secondary" as const),
    );
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

    <!-- Quick Actions -->
    <div>
        <h2 class="mb-4 text-lg font-semibold text-foreground">
            Quick Actions
        </h2>
        <div class="grid gap-3 sm:grid-cols-2 lg:grid-cols-4">
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
                    <p class="text-xs text-muted-foreground">Design a survey</p>
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
            <Button
                variant="outline"
                class="h-auto justify-start gap-3 p-4"
                href="/scoring"
            >
                <div
                    class="flex h-9 w-9 items-center justify-center rounded-lg bg-amber-500/10"
                >
                    <Sparkles class="h-5 w-5 text-amber-500" />
                </div>
                <div class="text-left">
                    <p class="font-medium">Score Results</p>
                    <p class="text-xs text-muted-foreground">Analyze scores</p>
                </div>
            </Button>
        </div>
    </div>
</div>
