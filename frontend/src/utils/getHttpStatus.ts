export function getHttpStatus(err: unknown): number | undefined {
	if (typeof err !== "object" || err === null) return undefined;
	if (!("response" in err)) return undefined;

	const response = (err as { response?: unknown }).response;
	if (typeof response !== "object" || response === null) return undefined;
	if (!("status" in response)) return undefined;

	const value = (response as { status?: unknown }).status;
	return typeof value === "number" ? value : undefined;
}
