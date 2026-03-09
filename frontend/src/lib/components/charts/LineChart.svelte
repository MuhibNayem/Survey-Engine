<script lang="ts">
    import { line, curveMonotoneX, area } from 'd3-shape';
    import { scaleTime, scaleLinear } from 'd3-scale';
    import { max, extent } from 'd3-array';

    let { data = [], color = '#818cf8' } = $props();

    let width = $state(600);
    let height = $state(300);
    const margin = { top: 20, right: 20, bottom: 30, left: 40 };

    const innerWidth = $derived(Math.max(0, width - margin.left - margin.right));
    const innerHeight = $derived(Math.max(0, height - margin.top - margin.bottom));

    const parsedData = $derived(data.map((d: any) => ({
        date: new Date(d.date),
        count: d.count
    })));

    const xScale = $derived(scaleTime()
        .domain(extent(parsedData, (d: any) => d.date) as [Date, Date] || [new Date(), new Date()])
        .range([0, innerWidth]));

    const yScale = $derived(scaleLinear()
        .domain([0, (max(parsedData, (d: any) => d.count) || 10) * 1.2]) 
        .range([innerHeight, 0]));

    const lineGen = $derived(line<any>()
        .x(d => xScale(d.date))
        .y(d => yScale(d.count))
        .curve(curveMonotoneX));

    const areaGen = $derived(area<any>()
        .x(d => xScale(d.date))
        .y0(innerHeight)
        .y1(d => yScale(d.count))
        .curve(curveMonotoneX));
</script>

<div class="w-full h-full min-h-[300px] relative" bind:clientWidth={width} bind:clientHeight={height}>
    <svg {width} {height}>
        <g transform="translate({margin.left}, {margin.top})">
            <!-- Grid Lines -->
            {#each yScale.ticks(5) as tick}
                <g transform="translate(0, {yScale(tick)})">
                    <line x2={innerWidth} stroke="#e2e8f0" class="dark:stroke-slate-800" stroke-width="1" stroke-dasharray="4" />
                    <text x="-10" dy=".32em" text-anchor="end" class="text-xs fill-muted-foreground">{tick}</text>
                </g>
            {/each}

            {#if parsedData.length > 0}
                <!-- Area -->
                <path
                    d={areaGen(parsedData)}
                    fill={color}
                    fill-opacity="0.2"
                />
                
                <!-- Line -->
                <path
                    d={lineGen(parsedData)}
                    fill="none"
                    stroke={color}
                    stroke-width="3"
                />

                <!-- Points -->
                {#each parsedData as d}
                    <circle
                        cx={xScale(d.date)}
                        cy={yScale(d.count)}
                        r="5"
                        class="fill-background"
                        stroke={color}
                        stroke-width="2"
                    >
                        <title>{d.date.toLocaleDateString()}: {d.count} responses</title>
                    </circle>
                {/each}
            {/if}

            <!-- X Axis Base -->
            <g transform="translate(0, {innerHeight})">
                <line x2={innerWidth} stroke="#cbd5e1" class="dark:stroke-slate-700" stroke-width="1" />
                {#if parsedData.length > 0}
                    <text x={0} y="20" text-anchor="start" class="text-xs fill-muted-foreground">
                        {parsedData[0].date.toLocaleDateString()}
                    </text>
                    {#if parsedData.length > 1}
                        <text x={innerWidth} y="20" text-anchor="end" class="text-xs fill-muted-foreground">
                            {parsedData[parsedData.length - 1].date.toLocaleDateString()}
                        </text>
                    {/if}
                {/if}
            </g>
        </g>
    </svg>
</div>
