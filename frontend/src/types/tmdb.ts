export type MediaType = "movie" | "tv";

export type TitleDetails = {
  id: number;
  title?: string;
  name?: string;
  overview?: string;
  posterPath?: string;
  releaseDate?: string;
  firstAirDate?: string;
  genreIds?: number[];
  genres?: string[];
  userScore?: number;
  voteCount?: number;
  runtimeMinutes?: number;
  numberOfSeasons?: number;
  numberOfEpisodes?: number;
  trailerUrl?: string;
  ottOffer?: {
    region?: string;
    link?: string;
    flatrate?: { name?: string; logoUrl?: string | null }[];
    rent?: { name?: string; logoUrl?: string | null }[];
    buy?: { name?: string; logoUrl?: string | null }[];
  };
};
