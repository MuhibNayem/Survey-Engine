<script lang="ts">
    import { auth } from "$lib/stores/auth.svelte";
    import { goto } from "$app/navigation";
    import { onMount } from "svelte";
    import * as Card from "$lib/components/ui/card";
    import { Badge } from "$lib/components/ui/badge";
    import { Button } from "$lib/components/ui/button";
    import { Skeleton } from "$lib/components/ui/skeleton";
    import {
        Users,
        Activity,
        CreditCard,
        TrendingUp,
        ShieldAlert,
        Clock3,
        Globe,
    } from "lucide-svelte";
    import api from "$lib/api";
    import type {
        SuperAdminMetricsResponse,
        AuditLogEntry,
        PaginatedResponse,
    } from "$lib/types";

    let liveMetrics = $state<SuperAdminMetricsResponse | null>(null);
    let recentActivity = $state<AuditLogEntry[]>([]);
    let error = $state<string | null>(null);

    function formatTime(iso: string) {
        return new Date(iso).toLocaleString("en-US", {
            month: "short",
            day: "numeric",
            hour: "2-digit",
            minute: "2-digit",
        });
    }

    onMount(async () => {
        if (!auth.isAuthenticated) {
            goto("/login");
            return;
        } else if (auth.user?.role !== "SUPER_ADMIN") {
            goto("/dashboard");
            return;
        }

        try {
            const [metricsRes, activityRes] = await Promise.all([
                api.get<SuperAdminMetricsResponse>(
                    "/admin/superadmin/tenants/metrics",
                ),
                api.get<PaginatedResponse<AuditLogEntry>>(
                    "/admin/superadmin/audit-logs?page=0&size=8",
                ),
            ]);
            liveMetrics = metricsRes.data;
            recentActivity = activityRes.data?.content ?? [];
        } catch (err) {
            console.error("Failed to load platform metrics", err);
            error = "Could not load real-time platform metrics and activity.";
        }
    });

    // Reactive platform metrics calculated dynamically from the API
    const metrics = $derived([
        {
            label: "Total Tenants",
            value: liveMetrics
                ? liveMetrics.totalTenants.toLocaleString()
                : "...",
            trend: "All instances",
            icon: Users,
            color: "text-blue-500",
            bg: "bg-blue-500/10",
        },
        {
            label: "Active Subscriptions",
            value: liveMetrics
                ? liveMetrics.activeSubscriptions.toLocaleString()
                : "...",
            trend: "Currently active",
            icon: Activity,
            color: "text-emerald-500",
            bg: "bg-emerald-500/10",
        },
        {
            label: "MRR / Value",
            value: "Enterprise", // Placeholder for actual billing logic
            trend: "SaaS Model",
            icon: CreditCard,
            color: "text-purple-500",
            bg: "bg-purple-500/10",
        },
        {
            label: "Global Responses",
            value: liveMetrics
                ? liveMetrics.totalPlatformUsage.toLocaleString()
                : "...",
            trend: "Across all surveys",
            icon: TrendingUp,
            color: "text-orange-500",
            bg: "bg-orange-500/10",
        },
    ]);
</script>

<svelte:head>
    <title>Super Admin Overview — Survey Engine</title>
</svelte:head>

<div class="space-y-8">
    <div
        class="flex flex-col md:flex-row md:items-center justify-between gap-4"
    >
        <div>
            <h1 class="text-3xl font-bold tracking-tight text-foreground">
                Platform Overview
            </h1>
            <p class="mt-1 text-muted-foreground">
                Welcome to the Super Admin control center, <span
                    class="font-medium text-foreground"
                    >{auth.user?.fullName ?? "Admin"}</span
                >
            </p>
        </div>
        <Badge
            variant="outline"
            class="w-fit border-primary/20 bg-primary/5 text-primary"
        >
            <ShieldAlert class="mr-1 h-3 w-3" />
            Super Admin Access
        </Badge>
    </div>

    <!-- Metrics Grid -->
    {#if !liveMetrics}
        <div class="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
            {#each Array(4) as _}
                <Card.Root>
                    <Card.Content class="p-6">
                        <div class="flex items-center justify-between mb-4">
                            <Skeleton class="h-4 w-[100px]" />
                            <Skeleton class="h-10 w-10 rounded-xl" />
                        </div>
                        <div class="space-y-2">
                            <Skeleton class="h-8 w-[80px]" />
                            <Skeleton class="h-3 w-[120px]" />
                        </div>
                    </Card.Content>
                </Card.Root>
            {/each}
        </div>
    {:else}
        <div class="grid gap-4 sm:grid-cols-2 lg:grid-cols-4">
            {#each metrics as metric}
                <Card.Root
                    class="transition-all duration-200 hover:shadow-md border-border/50 bg-gradient-to-br from-background to-muted/20"
                >
                    <Card.Content class="p-6">
                        <div class="flex items-center justify-between mb-4">
                            <p class="text-sm font-medium text-muted-foreground">
                                {metric.label}
                            </p>
                            <div
                                class="flex h-10 w-10 items-center justify-center rounded-xl {metric.bg}"
                            >
                                <metric.icon class="h-5 w-5 {metric.color}" />
                            </div>
                        </div>
                        <div>
                            <p class="text-3xl font-bold text-foreground">
                                {metric.value}
                            </p>
                            <p class="mt-1 text-xs text-muted-foreground">
                                {metric.trend}
                            </p>
                        </div>
                    </Card.Content>
                </Card.Root>
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
                    class="h-auto justify-start gap-4 p-4 border-border/50 hover:border-primary/50 transition-colors"
                    href="/admin/tenants"
                >
                    <div
                        class="flex h-10 w-10 items-center justify-center rounded-lg bg-indigo-500/10 shrink-0"
                    >
                        <Users class="h-5 w-5 text-indigo-500" />
                    </div>
                    <div class="text-left">
                        <p class="font-medium text-foreground">
                            Manage Tenants
                        </p>
                        <p class="text-xs text-muted-foreground">
                            View and manage platform customers
                        </p>
                    </div>
                </Button>

                <Button
                    variant="outline"
                    class="h-auto justify-start gap-4 p-4 border-border/50 hover:border-primary/50 transition-colors"
                    href="/admin/plans"
                >
                    <div
                        class="flex h-10 w-10 items-center justify-center rounded-lg bg-rose-500/10 shrink-0"
                    >
                        <ShieldAlert class="h-5 w-5 text-rose-500" />
                    </div>
                    <div class="text-left">
                        <p class="font-medium text-foreground">System Plans</p>
                        <p class="text-xs text-muted-foreground">
                            Configure pricing and feature toggles
                        </p>
                    </div>
                </Button>

                <Button
                    variant="outline"
                    class="h-auto justify-start gap-4 p-4 border-border/50 hover:border-primary/50 transition-colors"
                    href="/admin/site-content"
                >
                    <div
                        class="flex h-10 w-10 items-center justify-center rounded-lg bg-amber-500/10 shrink-0"
                    >
                        <Globe class="h-5 w-5 text-amber-500" />
                    </div>
                    <div class="text-left">
                        <p class="font-medium text-foreground">Site Content</p>
                        <p class="text-xs text-muted-foreground">
                            Edit landing page, pricing, and public announcements
                        </p>
                    </div>
                </Button>
            </div>
        </div>

        <!-- Recent Platform Activity -->
        <div class="md:col-span-1 lg:col-span-2">
            <Card.Root
                class="h-full border-border/50 bg-gradient-to-br from-background to-muted/20"
            >
                <Card.Header>
                    <Card.Title>Recent Platform Activity</Card.Title>
                    <Card.Description
                        >Latest significant events across all instances</Card.Description
                    >
                </Card.Header>
                <Card.Content>
                    {#if !liveMetrics}
                        <div class="space-y-3">
                            {#each Array(5) as _}
                                <div class="rounded-lg border border-border/60 bg-muted/20 p-3">
                                    <div class="flex items-start justify-between gap-3">
                                        <div class="space-y-2 flex-1">
                                            <Skeleton class="h-4 w-[150px]" />
                                            <Skeleton class="h-3 w-[200px]" />
                                        </div>
                                        <Skeleton class="h-3 w-[80px]" />
                                    </div>
                                </div>
                            {/each}
                        </div>
                    {:else if recentActivity.length === 0}
                        <div
                            class="flex flex-col items-center justify-center py-10 text-center"
                        >
                            <Activity
                                class="mb-4 h-10 w-10 text-muted-foreground/30"
                            />
                            <p class="text-sm font-medium text-foreground">
                                No recent activity
                            </p>
                            <p class="text-xs text-muted-foreground mt-1 max-w-sm">
                                No platform audit events were returned in the latest query.
                            </p>
                        </div>
                    {:else}
                        <div class="space-y-3">
                            {#each recentActivity as item (item.id)}
                                <div class="rounded-lg border border-border/60 bg-muted/20 p-3">
                                    <div class="flex items-start justify-between gap-3">
                                        <div class="space-y-1">
                                            <p class="text-sm font-medium text-foreground">
                                                {item.action}
                                            </p>
                                            <p class="text-xs text-muted-foreground">
                                                {item.entityType} · {item.tenantId ?? "platform"} · {item.actor}
                                            </p>
                                        </div>
                                        <span class="inline-flex items-center gap-1 text-xs text-muted-foreground">
                                            <Clock3 class="h-3.5 w-3.5" />
                                            {formatTime(item.createdAt)}
                                        </span>
                                    </div>
                                </div>
                            {/each}
                        </div>
                    {/if}
                </Card.Content>
            </Card.Root>
        </div>
    </div>
</div>
