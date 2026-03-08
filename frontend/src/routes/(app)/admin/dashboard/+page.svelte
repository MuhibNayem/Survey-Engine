<script lang="ts">
    import { auth } from "$lib/stores/auth.svelte";
    import { goto } from "$app/navigation";
    import { onMount } from "svelte";
    import * as Card from "$lib/components/ui/card";
    import { Badge } from "$lib/components/ui/badge";
    import { Button } from "$lib/components/ui/button";
    import {
        Users,
        Activity,
        CreditCard,
        TrendingUp,
        ShieldAlert,
    } from "lucide-svelte";

    onMount(() => {
        if (!auth.isAuthenticated) {
            goto("/login");
        } else if (auth.user?.role !== "SUPER_ADMIN") {
            goto("/dashboard");
        }
    });

    // Mock platform metrics for the Super Admin
    const metrics = [
        {
            label: "Total Tenants",
            value: "1,204",
            trend: "+12% this month",
            icon: Users,
            color: "text-blue-500",
            bg: "bg-blue-500/10",
        },
        {
            label: "Active Subscriptions",
            value: "842",
            trend: "+5% this month",
            icon: Activity,
            color: "text-emerald-500",
            bg: "bg-emerald-500/10",
        },
        {
            label: "MRR",
            value: "$42,500",
            trend: "+18% this month",
            icon: CreditCard,
            color: "text-purple-500",
            bg: "bg-purple-500/10",
        },
        {
            label: "Platform Usage",
            value: "1.2M",
            trend: "Responses collected",
            icon: TrendingUp,
            color: "text-orange-500",
            bg: "bg-orange-500/10",
        },
    ];
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
            </div>
        </div>

        <!-- Recent System Activity Placeholder -->
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
                            Platform audit logs and significant event tracking
                            will appear here once the backend metrics endpoint
                            is fully implemented.
                        </p>
                    </div>
                </Card.Content>
            </Card.Root>
        </div>
    </div>
</div>
