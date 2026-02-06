export type MediaType = "movie" | "tv";

export type OttProvider = {
	name: string | null;
	logoUrl: string | null;
};

export type OttOffer = {
	region: string | null;
	link: string | null;
	flatrate: OttProvider[] | null;
	rent: OttProvider[] | null;
	buy: OttProvider[] | null;
};

export type TitleDetails = {
	id: number;
	title: string | null;
	name: string | null;
	overview: string | null;
	posterPath: string | null;
	releaseDate: string | null;
	firstAirDate: string | null;
	genreIds: number[] | null;
	genres: string[] | null;
	userScore: number | null;
	voteCount: number | null;
	runtimeMinutes: number | null;
	numberOfSeasons: number | null;
	numberOfEpisodes: number | null;
	trailerUrl: string | null;
	ottOffer: OttOffer | null;
};
