<script lang="ts">
    // Native HTML/CSS Bar Chart for pixel-perfect typography alignment

    export type ChartData = {
        label: string;
        value: number;
        percentage: number;
        [key: string]: string | number; // Index signature for dynamic D3 rendering
    };

    let { 
        data = [], 
        xKey = 'value', 
        yKey = 'label', 
        color = '#3b82f6' 
    }: {
        data?: ChartData[];
        xKey?: string;
        yKey?: string;
        color?: string;
    } = $props();
</script>

<div class="w-full flex-1 flex flex-col justify-center min-h-[250px] relative font-sans text-sm">
    {#if data && data.length > 0}
        {@const maxValue = Math.max(...data.map(d => Number(d[xKey]) || 0))}
        
        <div class="space-y-4 px-2 w-full max-h-[300px] overflow-y-auto">
            {#each data as d}
                {@const val = Number(d[xKey]) || 0}
                {@const percentWidth = maxValue > 0 ? (val / maxValue) * 100 : 0}
                {@const actualPercent = typeof d.percentage === 'number' ? d.percentage : (percentWidth)}
                
                <div class="flex flex-col gap-1 w-full">
                    <div class="flex justify-between items-end">
                        <span class="text-sm font-medium text-foreground truncate pr-4" title={String(d[yKey])}>
                            {d[yKey]}
                        </span>
                        <span class="text-xs font-semibold text-muted-foreground whitespace-nowrap">
                            {val} ({actualPercent.toFixed(1)}%)
                        </span>
                    </div>
                    
                    <div class="w-full h-4 bg-muted/30 rounded-full overflow-hidden flex items-center shadow-inner">
                        <div 
                            class="h-full rounded-full transition-all duration-700 ease-out"
                            style="width: {percentWidth}%; background-color: {color};"
                            role="progressbar"
                            aria-valuenow={val}
                            aria-valuemin={0}
                            aria-valuemax={maxValue}
                        ></div>
                    </div>
                </div>
            {/each}
        </div>
    {:else}
        <div class="flex items-center justify-center h-full w-full text-muted-foreground border-2 border-dashed rounded-lg bg-muted/10 opacity-70">
            No data available
        </div>
    {/if}
</div>
