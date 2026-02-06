export type MediaType = "movie" | "tv";

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

export const formatGenresLine = (genres: string[] | undefined): string | null => {
  if (!genres || genres.length === 0) return null;
  if (genres.length === 1) return genres[0] ?? null;
  if (genres.length === 2) return `${genres[0]} and ${genres[1]}`;
  const head = genres.slice(0, -1).join(", ");
  const last = genres[genres.length - 1];
  return `${head}, and ${last}`;
};

export const formatRuntime = (minutes: number): string => {
  if (!Number.isFinite(minutes) || minutes <= 0) return "";
  const h = Math.floor(minutes / 60);
  const m = minutes % 60;
  return h > 0 ? `${h}h ${m}m` : `${m}m`;
};

export const tmdbTitleUrl = (mediaType: MediaType, tmdbId: number) =>
  `https://www.themoviedb.org/${mediaType === "tv" ? "tv" : "movie"}/${tmdbId}`;
