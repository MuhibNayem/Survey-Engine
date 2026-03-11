export function currencySymbol(currency: string | null | undefined): string {
    const normalizedCurrency = (currency || "USD").toUpperCase();
    return normalizedCurrency === "BDT" ? "৳" : normalizedCurrency;
}

export function formatAmount(
    amount: number | null | undefined,
    currency: string | null | undefined,
): string {
    if (amount == null) return "—";

    const normalizedCurrency = (currency || "USD").toUpperCase();
    const locale = normalizedCurrency === "BDT" ? "en-BD" : "en-US";

    return amount.toLocaleString(locale, {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2,
    });
}

export function formatMoney(
    amount: number | null | undefined,
    currency: string | null | undefined,
): string {
    if (amount == null) return "—";
    return `${currencySymbol(currency)}${formatAmount(amount, currency)}`;
}
