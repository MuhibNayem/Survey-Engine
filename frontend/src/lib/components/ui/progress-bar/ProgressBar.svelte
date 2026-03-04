<script lang="ts">
    import { cn } from "$lib/utils.js";

    interface Props {
        value?: number;
        max?: number;
        class?: string;
        colorClass?: string;
        showLabel?: boolean;
    }

    let {
        value = 0,
        max = 100,
        class: className,
        colorClass = "bg-primary",
        showLabel = false,
    }: Props = $props();

    const percentage = $derived(
        max > 0 ? Math.min(100, Math.max(0, (value / max) * 100)) : 0,
    );
</script>

<div class={cn("w-full", className)}>
    <div
        class="flex items-center justify-between mb-1.5 text-xs text-muted-foreground"
    >
        {#if showLabel}
            <span>{value.toLocaleString()} / {max.toLocaleString()}</span>
            <span class="font-medium text-foreground"
                >{Math.round(percentage)}%</span
            >
        {/if}
    </div>
    <div class="relative h-2 w-full overflow-hidden rounded-full bg-secondary">
        <div
            class={cn(
                "h-full w-full flex-1 transition-all duration-500 ease-in-out",
                colorClass,
            )}
            style="transform: translateX(-{100 - (percentage || 0)}%)"
        ></div>
    </div>
</div>
