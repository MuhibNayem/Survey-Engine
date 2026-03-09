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
        UserX,
        Activity,
    } from "lucide-svelte";
    import * as Tooltip from "$lib/components/ui/tooltip";
    import { auth } from "$lib/stores/auth.svelte";

    import logo from "$lib/assets/logo.png";

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
        { href: "/activity", label: "Activity Log", icon: Activity },
        { href: "/settings", label: "Settings", icon: Settings },
    ];

    const superAdminItems = [
        { href: "/admin/dashboard", label: "Overview", icon: LayoutDashboard },
        { href: "/admin/tenants", label: "Tenants", icon: Users },
        { href: "/admin/plans", label: "System Plans", icon: ShieldAlert },
        { href: "/admin/audit-logs", label: "Audit Logs", icon: Activity },
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
                <a
                    href={auth.user?.role === "SUPER_ADMIN"
                        ? "/admin/dashboard"
                        : "/dashboard"}
                    class="flex items-center gap-2"
                >
                    <img src={logo} alt="Survey Engine Logo" class="h-8 w-8 object-contain" />
                    <span class="text-lg font-semibold text-sidebar-foreground"
                        >Survey Engine</span
                     >
                </a>
            {:else}
                <a
                    href={auth.user?.role === "SUPER_ADMIN"
                        ? "/admin/dashboard"
                        : "/dashboard"}
                    class="mx-auto"
                    aria-label="Go to dashboard"
                >
                    <img src={logo} alt="SE" class="h-8 w-8 object-contain" />
                </a>
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

        <!-- Impersonation Reversion -->
        {#if auth.isImpersonating}
            <div class={collapsed ? "px-2 pb-4" : "px-4 pb-4"}>
                {#if collapsed}
                    <Tooltip.Root>
                        <Tooltip.Trigger class="w-full focus:outline-none">
                            <button
                                onclick={() => auth.revertImpersonation()}
                                class="mx-auto flex h-10 w-10 items-center justify-center rounded-lg bg-orange-500/10 text-orange-600 hover:bg-orange-500/20 transition-all border border-orange-500/20"
                                disabled={auth.isLoading}
                            >
                                {#if auth.isLoading}
                                    <span
                                        class="h-4 w-4 animate-spin rounded-full border-2 border-orange-600 border-t-transparent"
                                    ></span>
                                {:else}
                                    <UserX class="h-5 w-5 shrink-0" />
                                {/if}
                            </button>
                        </Tooltip.Trigger>
                        <Tooltip.Content side="right">
                            Revert Impersonation
                        </Tooltip.Content>
                    </Tooltip.Root>
                {:else}
                    <button
                        onclick={() => auth.revertImpersonation()}
                        class="flex w-full items-center justify-center gap-2 rounded-lg bg-orange-500/10 py-2.5 px-3 text-sm font-semibold text-orange-600 hover:bg-orange-500/20 transition-all border border-orange-500/20"
                        disabled={auth.isLoading}
                    >
                        {#if auth.isLoading}
                            <span
                                class="inline-block h-4 w-4 animate-spin rounded-full border-2 border-orange-600 border-t-transparent"
                            ></span>
                            <span>Reverting...</span>
                        {:else}
                            <UserX class="h-4 w-4 shrink-0" />
                            <span class="truncate">Exit Proxy Session</span>
                        {/if}
                    </button>
                {/if}
            </div>
        {/if}

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
