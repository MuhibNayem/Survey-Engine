<script lang="ts">
    import { page } from "$app/state";
    import {
        LayoutDashboard,
        HelpCircle,
        FolderKanban,
        FileText,
        Megaphone,
        MessageSquareText,
        Settings,
        ChevronsLeft,
        ChevronsRight,
        ShieldAlert,
        Users,
    } from "lucide-svelte";
    import * as Tooltip from "$lib/components/ui/tooltip";
    import { auth } from "$lib/stores/auth.svelte";

    interface Props {
        collapsed?: boolean;
    }

    let { collapsed = $bindable(false) }: Props = $props();

    const navItems = [
        { href: "/dashboard", label: "Dashboard", icon: LayoutDashboard },
        { href: "/questions", label: "Questions", icon: HelpCircle },
        { href: "/categories", label: "Categories", icon: FolderKanban },
        { href: "/surveys", label: "Surveys", icon: FileText },
        { href: "/campaigns", label: "Campaigns", icon: Megaphone },
        { href: "/responses", label: "Responses", icon: MessageSquareText },
        { href: "/settings", label: "Settings", icon: Settings },
    ];

    const superAdminItems = [
        { href: "/admin/dashboard", label: "Overview", icon: LayoutDashboard },
        { href: "/admin/tenants", label: "Tenants", icon: Users },
        { href: "/admin/plans", label: "System Plans", icon: ShieldAlert },
    ];

    function isActive(href: string): boolean {
        return (
            page.url.pathname === href ||
            page.url.pathname.startsWith(href + "/")
        );
    }
</script>

<Tooltip.Provider>
    <aside
        class="flex h-full shrink-0 overflow-hidden flex-col border-r border-border bg-sidebar transition-[width] duration-300
            {collapsed ? 'w-16 min-w-16 max-w-16' : 'w-64 min-w-64 max-w-64'}"
    >
        <!-- Logo / Brand -->
        <div class="flex h-16 items-center border-b border-sidebar-border px-4">
            {#if !collapsed}
                <div class="flex items-center gap-2">
                    <div
                        class="flex h-8 w-8 items-center justify-center rounded-lg bg-primary text-primary-foreground text-sm font-bold"
                    >
                        SE
                    </div>
                    <span class="text-lg font-semibold text-sidebar-foreground"
                        >Survey Engine</span
                    >
                </div>
            {:else}
                <div
                    class="mx-auto flex h-8 w-8 items-center justify-center rounded-lg bg-primary text-primary-foreground text-sm font-bold"
                >
                    SE
                </div>
            {/if}
        </div>

        <!-- Navigation -->
        <nav class="flex-1 space-y-1 overflow-y-auto p-3">
            {#if auth.user?.role === "SUPER_ADMIN"}
                <div class="pb-2 px-3">
                    <p
                        class="text-xs font-semibold text-sidebar-foreground/50 uppercase tracking-widest {collapsed
                            ? 'text-center'
                            : ''}"
                    >
                        {collapsed ? "SA" : "Super Admin"}
                    </p>
                </div>
                {#each superAdminItems as item}
                    {@const active = isActive(item.href)}
                    {#if collapsed}
                        <Tooltip.Root>
                            <Tooltip.Trigger class="w-full focus:outline-none">
                                <a
                                    href={item.href}
                                    class="mx-auto flex h-10 w-10 items-center justify-center rounded-lg transition-colors
                                        {active
                                        ? 'bg-destructive/10 text-destructive'
                                        : 'text-destructive/70 hover:bg-destructive/10 hover:text-destructive'}"
                                >
                                    <item.icon class="h-5 w-5 shrink-0" />
                                </a>
                            </Tooltip.Trigger>
                            <Tooltip.Content side="right">
                                {item.label}
                            </Tooltip.Content>
                        </Tooltip.Root>
                    {:else}
                        <a
                            href={item.href}
                            class="flex items-center gap-3 rounded-lg px-3 py-2.5 text-sm font-medium transition-colors overflow-hidden
                                {active
                                ? 'bg-destructive/10 text-destructive'
                                : 'text-destructive/70 hover:bg-destructive/10 hover:text-destructive'}"
                        >
                            <item.icon class="h-5 w-5 shrink-0" />
                            <span class="whitespace-nowrap truncate"
                                >{item.label}</span
                            >
                        </a>
                    {/if}
                {/each}
            {:else}
                {#each navItems as item}
                    {@const active = isActive(item.href)}
                    {#if collapsed}
                        <Tooltip.Root>
                            <Tooltip.Trigger class="w-full focus:outline-none">
                                <a
                                    href={item.href}
                                    class="mx-auto flex h-10 w-10 items-center justify-center rounded-lg transition-colors
                                        {active
                                        ? 'bg-sidebar-accent text-sidebar-accent-foreground'
                                        : 'text-sidebar-foreground/70 hover:bg-sidebar-accent/50 hover:text-sidebar-accent-foreground'}"
                                >
                                    <item.icon class="h-5 w-5 shrink-0" />
                                </a>
                            </Tooltip.Trigger>
                            <Tooltip.Content side="right">
                                {item.label}
                            </Tooltip.Content>
                        </Tooltip.Root>
                    {:else}
                        <a
                            href={item.href}
                            class="flex items-center gap-3 rounded-lg px-3 py-2.5 text-sm font-medium transition-colors overflow-hidden
                                {active
                                ? 'bg-sidebar-accent text-sidebar-accent-foreground'
                                : 'text-sidebar-foreground/70 hover:bg-sidebar-accent/50 hover:text-sidebar-accent-foreground'}"
                        >
                            <item.icon class="h-5 w-5 shrink-0" />
                            <span class="whitespace-nowrap truncate"
                                >{item.label}</span
                            >
                        </a>
                    {/if}
                {/each}
            {/if}
        </nav>

        <!-- Collapse Toggle -->
        <div class="border-t border-sidebar-border p-3">
            <button
                onclick={() => (collapsed = !collapsed)}
                class="flex w-full items-center justify-center rounded-lg py-2 text-sidebar-foreground/50 hover:bg-sidebar-accent/50 hover:text-sidebar-foreground transition-colors"
            >
                {#if collapsed}
                    <ChevronsRight class="h-5 w-5" />
                {:else}
                    <ChevronsLeft class="h-5 w-5" />
                {/if}
            </button>
        </div>
    </aside>
</Tooltip.Provider>
