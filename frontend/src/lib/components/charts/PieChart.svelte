<script lang="ts">
    import { pie, arc } from 'd3-shape';
    
    let { data = [] } = $props();
    const colors = ['#818cf8', '#34d399', '#fbbf24', '#f87171', '#a78bfa', '#f472b6', '#2dd4bf', '#fb923c'];

    let width = $state(400);
    let height = $state(300);

    const radius = $derived(Math.min(width * 0.5, height) / 2 - 10);
    const pieGen = $derived(pie<any, any>().value((d: any) => d.value).sort(null));
    const arcGen = $derived(arc<any, any>().innerRadius(radius * 0.4).outerRadius(radius));
    const arcs = $derived(pieGen(data));
</script>

<div class="w-full h-full min-h-[250px] flex flex-row items-center justify-center gap-4" bind:clientWidth={width} bind:clientHeight={height}>
    <div class="flex-shrink-0" style="width: {Math.min(width * 0.5, height)}px; height: {Math.min(width * 0.5, height)}px">
        <svg width="100%" height="100%" viewBox="0 0 {Math.min(width * 0.5, height)} {Math.min(width * 0.5, height)}">
            <g transform="translate({Math.min(width * 0.5, height) / 2}, {Math.min(width * 0.5, height) / 2})">
                {#each arcs as d, i}
                    <path
                        d={arcGen(d as any)}
                        fill={colors[i % colors.length]}
                        class="transition-all duration-300 hover:opacity-80 cursor-pointer stroke-background"
                        stroke-width="2"
                    >
                        <title>{d.data.label}: {d.data.value} ({d.data.percentage.toFixed(1)}%)</title>
                    </path>
                    {#if d.data.percentage > 5}
                        <text
                            transform="translate({arcGen.centroid(d as any)})"
                            text-anchor="middle"
                            class="text-[10px] fill-white font-semibold pointer-events-none"
                            dy=".32em"
                        >
                            {d.data.percentage.toFixed(0)}%
                        </text>
                    {/if}
                {/each}
            </g>
        </svg>
    </div>
    
    <!-- Legend -->
    <div class="flex flex-col gap-2 overflow-y-auto max-h-[80%] flex-1 pl-2">
        {#each data as d, i}
            <div class="flex items-center gap-2 text-xs">
                <div class="w-3 h-3 rounded-sm shrink-0" style="background-color: {colors[i % colors.length]}"></div>
                <span class="text-muted-foreground truncate" title={d.label}>{d.label}</span>
                <span class="font-medium ml-auto pl-2 shrink-0">{d.percentage.toFixed(1)}%</span>
            </div>
        {/each}
    </div>
</div>
