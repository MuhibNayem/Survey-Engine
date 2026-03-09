<script lang="ts">
    import { onMount, onDestroy } from 'svelte';
    import Chart from 'chart.js/auto';

    let { 
        data = [], 
        datasets = [], // Supports [{ label, data: [{value, label}], color }]
        xKey = 'value', 
        yKey = 'label', 
        color = '#3b82f6' 
    } = $props();

    let canvas: HTMLCanvasElement;
    let chartInstance: Chart<'bar'> | null = null;

    function buildDatasets() {
        if (datasets && datasets.length > 0) {
            return datasets.map((ds: any) => ({
                label: ds.label,
                data: ds.data.map((d: any) => Number(d[xKey]) || 0),
                backgroundColor: ds.color || color,
                borderRadius: 4,
            }));
        } else {
            return [{
                label: 'Count',
                data: data.map((d: any) => Number(d[xKey]) || 0),
                backgroundColor: color,
                borderRadius: 4,
            }];
        }
    }

    function buildLabels() {
        const primaryData = datasets && datasets.length > 0 ? datasets[0].data : data;
        return primaryData.map((d: any) => d[yKey]);
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
            type: 'bar',
            data: {
                labels: buildLabels(),
                datasets: buildDatasets()
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                indexAxis: 'y', // Horizontal
                plugins: {
                    legend: { display: datasets && datasets.length > 0 },
                    tooltip: {
                        callbacks: {
                            label: (context) => {
                                let label = context.dataset.label || '';
                                if (label) label += ': ';
                                if (context.parsed.x !== null) {
                                    label += context.parsed.x;
                                    const idx = context.dataIndex;
                                    // Retrieve correct percentage mapped array
                                    let activeDataTarget = data;
                                    if (datasets && datasets.length > 0 && datasets[context.datasetIndex]) {
                                        activeDataTarget = datasets[context.datasetIndex].data;
                                    }
                                    if (activeDataTarget[idx] && activeDataTarget[idx].percentage !== undefined) {
                                        label += ` (${Number(activeDataTarget[idx].percentage).toFixed(1)}%)`;
                                    }
                                }
                                return label;
                            }
                        }
                    }
                },
                scales: {
                    x: { beginAtZero: true }
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

<div class="w-full h-full relative">
    <canvas bind:this={canvas}></canvas>
</div>
