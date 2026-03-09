/**
 * Export utilities for CSV and PDF export functionality
 */

/**
 * Exports data to CSV format and triggers download
 * @param data - Array of data objects to export
 * @param filename - Base filename (without extension)
 * @param columns - Column definitions with key and label
 */
export function exportToCSV<T extends Record<string, any>>(
	data: T[],
	filename: string,
	columns: Array<{ key: keyof T | string; label: string }>
): void {
	if (!data || data.length === 0) {
		console.warn('No data to export');
		return;
	}

	// Create CSV header
	const headers = columns.map((c) => escapeCsvValue(c.label)).join(',');

	// Create CSV rows
	const rows = data.map((row) =>
		columns
			.map((c) => {
				const value = row[c.key as keyof T] ?? '';
				return escapeCsvValue(String(value));
			})
			.join(',')
	);

	const csv = [headers, ...rows].join('\n');
	downloadFile(csv, `${filename}-${new Date().toISOString().split('T')[0]}.csv`, 'text/csv;charset=utf-8;');
}

/**
 * Escapes a value for CSV format
 */
function escapeCsvValue(value: string): string {
	const escaped = String(value ?? '').replace(/"/g, '""');
	return escaped.includes(',') || escaped.includes('"') || escaped.includes('\n')
		? `"${escaped}"`
		: escaped;
}

/**
 * Exports content to PDF using browser print dialog
 * For production, consider integrating with pdfmake or jsPDF
 * @param content - HTML content to print
 * @param filename - Document title
 * @param options - Optional print configuration
 */
export async function exportToPDF(
	content: string,
	filename: string,
	options?: {
		orientation?: 'portrait' | 'landscape';
		includeStyles?: boolean;
	}
): Promise<void> {
	const { orientation = 'portrait', includeStyles = true } = options ?? {};

	// Clone current document styles if requested
	const styles = includeStyles
		? Array.from(document.querySelectorAll('style, link[rel="stylesheet"]'))
				.map((el) => el.outerHTML)
				.join('\n')
		: '';

	const printContent = `
		<!DOCTYPE html>
		<html lang="en">
			<head>
				<meta charset="UTF-8">
				<meta name="viewport" content="width=device-width, initial-scale=1.0">
				<title>${filename}</title>
				${styles}
				<style>
					@media print {
						@page {
							size: ${orientation};
							margin: 20mm;
						}
						body {
							font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
						}
					}
				</style>
			</head>
			<body>
				${content}
			</body>
		</html>
	`;

	const printWindow = window.open('', '_blank');
	if (!printWindow) {
		console.error('Failed to open print window');
		return;
	}

	printWindow.document.write(printContent);
	printWindow.document.close();

	// Wait for content to load before printing
	printWindow.onload = () => {
		printWindow.print();
	};
}

/**
 * Exports data to JSON format and triggers download
 * @param data - Data to export
 * @param filename - Base filename (without extension)
 */
export function exportToJSON<T>(data: T, filename: string): void {
	const json = JSON.stringify(data, null, 2);
	downloadFile(json, `${filename}-${new Date().toISOString().split('T')[0]}.json`, 'application/json');
}

/**
 * Helper function to trigger file download
 */
function downloadFile(content: string, filename: string, mimeType: string): void {
	const blob = new Blob([content], { type: mimeType });
	const link = document.createElement('a');
	link.href = URL.createObjectURL(blob);
	link.download = filename;
	document.body.appendChild(link);
	link.click();
	document.body.removeChild(link);
	URL.revokeObjectURL(link.href);
}

/**
 * Creates a printable table from data
 * @param data - Array of data objects
 * @param columns - Column definitions
 * @param title - Optional table title
 */
export function createPrintableTable<T extends Record<string, any>>(
	data: T[],
	columns: Array<{ key: keyof T | string; label: string }>,
	title?: string
): string {
	const headers = columns.map((c) => `<th class="px-4 py-2 text-left border">${c.label}</th>`).join('');
	const rows = data
		.map(
			(row) =>
				`<tr>${columns.map((c) => `<td class="px-4 py-2 border">${row[c.key as keyof T] ?? ''}</td>`).join('')}</tr>`
		)
		.join('');

	return `
		<div class="printable-table">
			${title ? `<h2 class="text-xl font-bold mb-4">${title}</h2>` : ''}
			<table class="w-full border-collapse">
				<thead>
					<tr class="bg-muted">${headers}</tr>
				</thead>
				<tbody>
					${rows}
				</tbody>
			</table>
		</div>
	`;
}
