import type { MediaType } from "../types/tmdb";

export type PosterSize = "w185" | "w500";

export const posterUrl = (
	posterPath: string | null | undefined,
	size: PosterSize = "w500",
): string | null => {
	if (!posterPath) return null;
	if (posterPath.startsWith("http://") || posterPath.startsWith("https://")) {
		return posterPath;
	}
	return `https://image.tmdb.org/t/p/${size}${posterPath}`;
};

export const normalizeGenreLabel = (label: string): string => {
	const trimmed = label.trim();
	// TMDB TV genre 10759 is named "Action & Adventure"; show a consistent label.
	if (/^Action\s*&\s*Adventure$/i.test(trimmed)) return "Action/Adventure";
	return label;
};

export const formatGenresLine = (
	genres: string[] | null | undefined,
): string | null => {
	if (!genres || genres.length === 0) return null;
	const normalized = genres.map((g) => (g ? normalizeGenreLabel(g) : g));
	if (normalized.length === 1) return normalized[0] ?? null;
	if (normalized.length === 2) return `${normalized[0]} and ${normalized[1]}`;
	const head = normalized.slice(0, -1).join(", ");
	const last = normalized[normalized.length - 1];
	return `${head}, and ${last}`;
};

export const formatRuntime = (minutes: number | null | undefined): string => {
	if (minutes == null || !Number.isFinite(minutes) || minutes <= 0) return "";
	const h = Math.floor(minutes / 60);
	const m = minutes % 60;
	return h > 0 ? `${h}h ${m}m` : `${m}m`;
};

export const tmdbTitleUrl = (mediaType: MediaType, tmdbId: number) =>
	`https://www.themoviedb.org/${mediaType === "tv" ? "tv" : "movie"}/${tmdbId}`;
