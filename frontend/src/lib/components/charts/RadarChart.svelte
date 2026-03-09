<script lang="ts">
    import { onMount, onDestroy } from 'svelte';
    import Chart from 'chart.js/auto';

    let { 
        data = [], 
        datasets = [], // Supports [{ label, data: [{value, label}], color }]
        xKey = 'value', 
        yKey = 'label', 
        color = '#3b82f6' 
    } = $props<{
        data?: any[];
        datasets?: any[];
        xKey?: string;
        yKey?: string;
        color?: string;
    }>();

    let canvas: HTMLCanvasElement;
    let chartInstance: Chart<'radar'> | null = null;

    function buildLabels() {
        if (datasets && datasets.length > 0) {
            const allLabels = new Set<string>();
            datasets.forEach((ds: any) => {
                ds.data.forEach((d: any) => allLabels.add(d[yKey]));
            });
            return Array.from(allLabels);
        } else {
            return data.map((d: any) => d[yKey]);
        }
    }

    function buildDatasets() {
        if (datasets && datasets.length > 0) {
            const labels = buildLabels();
            return datasets.map((ds: any) => {
                const alignedData = labels.map((label: string) => {
                    const found = ds.data.find((d: any) => d[yKey] === label);
                    return found ? (Number(found[xKey]) || 0) : 0;
                });
                
                // Add some transparency to the radar fill area for better comparative readability
                const hexToRgba = (hex: string, alpha: number) => {
                    let c: any;
                    if(/^#([A-Fa-f0-9]{3}){1,2}$/.test(hex)){
                        c= hex.substring(1).split('');
                        if(c.length== 3){
                            c= [c[0], c[0], c[1], c[1], c[2], c[2]];
                        }
                        const numColor = parseInt(c.join(''), 16);
                        return 'rgba(' + [(numColor >> 16) & 255, (numColor >> 8) & 255, numColor & 255].join(',') + ',' + alpha + ')';
                    }
                    return hex; // fallback if not hex
                }

                const baseColor = ds.color || color;

                return {
                    label: ds.label,
                    data: alignedData,
                    borderColor: baseColor,
                    backgroundColor: hexToRgba(baseColor, 0.2), // Transparent fill
                    pointBackgroundColor: baseColor,
                    pointBorderColor: '#fff',
                    pointHoverBackgroundColor: '#fff',
                    pointHoverBorderColor: baseColor,
                };
            });
        } else {
            return [{
                label: 'Count',
                data: data.map((d: any) => Number(d[xKey]) || 0),
                borderColor: color,
                backgroundColor: color + '33', // rough 20% opacity using hex
                pointBackgroundColor: color,
            }];
        }
    }

    $effect(() => {
        if (chartInstance && (data.length > 0 || datasets.length > 0)) {
            chartInstance.data.labels = buildLabels();
            chartInstance.data.datasets = buildDatasets();
            chartInstance.update();
        }
    });

    onMount(() => {
        const ctx = canvas.getContext('2d');
        if (!ctx) return;

        chartInstance = new Chart(ctx, {
            type: 'radar',
            data: {
                labels: buildLabels(),
                datasets: buildDatasets()
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: { display: datasets && datasets.length > 0 },
                    tooltip: {
                        callbacks: {
                            label: (context: any) => {
                                let label = context.dataset.label || '';
                                if (label) label += ': ';
                                if (context.parsed.r !== null) {
                                    label += context.parsed.r;
                                    
                                    const targetDatasets = datasets && datasets.length > 0 ? datasets : [{ data }];
                                    const dsData = targetDatasets[context.datasetIndex]?.data || [];
                                    const labelName = context.label;
                                    const item = dsData.find((d: any) => d[yKey] === labelName);

                                    if (item && item.percentage !== undefined && item.percentage !== null) {
                                        label += ` (${Number(item.percentage).toFixed(1)}%)`;
                                    }
                                }
                                return label;
                            }
                        }
                    }
                },
                scales: {
                    r: { 
                        beginAtZero: true,
                        ticks: { precision: 0 } // whole integers only for counts
                    }
                }
            }
        });
    });

    onDestroy(() => {
        if (chartInstance) {
            chartInstance.destroy();
        }
    });
</script>

<div class="relative w-full h-full">
    <canvas bind:this={canvas}></canvas>
</div>
