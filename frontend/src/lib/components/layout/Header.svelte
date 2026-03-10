<script lang="ts">
    import { auth } from "$lib/stores/auth.svelte";
    import { themePreferences } from "$lib/stores/theme.svelte";
    import {
        Menu,
        Moon,
        Sun,
        LogOut,
        ChevronsLeft,
        ChevronsRight,
    } from "lucide-svelte";
    import { Button } from "$lib/components/ui/button";
    import * as DropdownMenu from "$lib/components/ui/dropdown-menu";

    interface Props {
        onToggleMobileSidebar: () => void;
        onToggleDesktopSidebar?: () => void;
        desktopSidebarHidden?: boolean;
    }

    let {
        onToggleMobileSidebar,
        onToggleDesktopSidebar = () => {},
        desktopSidebarHidden = false,
    }: Props = $props();
</script>

<header
    class="flex h-16 shrink-0 items-center justify-between border-b border-border bg-background px-4 md:px-6"
>
    <!-- Left side: mobile menu + breadcrumb -->
    <div class="flex items-center gap-3">
        <Button
            variant="ghost"
            size="icon"
            class="md:hidden"
            onclick={onToggleMobileSidebar}
        >
            <Menu class="h-5 w-5" />
        </Button>
        <Button
            variant="ghost"
            size="icon"
            class="hidden md:inline-flex"
            onclick={onToggleDesktopSidebar}
            aria-label={desktopSidebarHidden
                ? "Expand sidebar"
                : "Collapse sidebar"}
        >
            {#if desktopSidebarHidden}
                <ChevronsRight class="h-5 w-5" />
            {:else}
                <ChevronsLeft class="h-5 w-5" />
            {/if}
        </Button>
        <a
            href={auth.user?.role === "SUPER_ADMIN"
                ? "/admin/dashboard"
                : "/dashboard"}
            class="hidden md:block"
        >
            <h2 class="text-lg font-semibold text-foreground hover:text-primary transition-colors">
                Survey Engine
            </h2>
        </a>
    </div>

    <!-- Right side: theme toggle + user menu -->
    <div class="flex items-center gap-2">
        <Button variant="ghost" size="icon" onclick={themePreferences.toggleDarkMode}>
            {#if themePreferences.isDark}
                <Sun class="h-5 w-5" />
            {:else}
                <Moon class="h-5 w-5" />
            {/if}
        </Button>

        <DropdownMenu.Root>
            <DropdownMenu.Trigger>
                <Button variant="ghost" size="icon" class="rounded-full">
                    <div
                        class="flex h-8 w-8 items-center justify-center rounded-full bg-primary text-primary-foreground text-xs font-semibold"
                    >
                        {auth.user?.email?.charAt(0).toUpperCase() ?? "U"}
                    </div>
                </Button>
            </DropdownMenu.Trigger>
            <DropdownMenu.Content align="end" class="w-56">
                <DropdownMenu.Label>
                    <div class="flex flex-col gap-1">
                        <span class="text-sm font-medium"
                            >{auth.user?.email ?? "User"}</span
                        >
                        <span class="text-xs text-muted-foreground"
                            >Tenant: {auth.user?.tenantId ?? "—"}</span
                        >
                    </div>
                </DropdownMenu.Label>
                <DropdownMenu.Separator />
                <DropdownMenu.Item onclick={() => auth.logout()}>
                    <LogOut class="mr-2 h-4 w-4" />
                    Log out
                </DropdownMenu.Item>
            </DropdownMenu.Content>
        </DropdownMenu.Root>
    </div>
</header>
