import axios, { AxiosError, AxiosResponse } from "axios";
import { z } from "zod";
import { api_key } from "../apiKey/themoviedb";

const client = axios.create({
  baseURL: "https://api.themoviedb.org",
});

const getMovie = async (id: string): Promise<AxiosResponse | null> => {
  try {
    const response = await client.get("/3/movie/" + id, {
      params: { api_key: api_key },
    });
    return response;
  } catch (error) {
    return (error as AxiosError).response || null;
  }
};

const belongsToCollectionSchema = z.object({
  id: z.number(),
  name: z.string(),
  poster_path: z.string().nullable(),
  backdrop_path: z.string().nullable(),
});

const genreSchema = z.object({
  id: z.number(),
  name: z.string(),
});

const productionCompanySchema = z.object({
  id: z.number(),
  logo_path: z.string().nullable(),
  name: z.string(),
  origin_country: z.string(),
});

const productionCountrySchema = z.object({
  iso_3166_1: z.string(),
  name: z.string(),
});

const spokenLanguageSchema = z.object({
  english_name: z.string(),
  iso_639_1: z.string(),
  name: z.string(),
});

const MovieApiSchema = z.object({
  adult: z.boolean(),
  backdrop_path: z.string().nullable(),
  belongs_to_collection: belongsToCollectionSchema.nullable(),
  budget: z.number(),
  genres: z.array(genreSchema),
  homepage: z.string().nullable(),
  id: z.number(),
  imdb_id: z.string().nullable(),
  origin_country: z.array(z.string()),
  original_language: z.string(),
  original_title: z.string(),
  overview: z.string().nullable(),
  popularity: z.number(),
  poster_path: z.string().nullable(),
  production_companies: z.array(productionCompanySchema),
  production_countries: z.array(productionCountrySchema),
  release_date: z.string(),
  revenue: z.number(),
  runtime: z.number().nullable(),
  spoken_languages: z.array(spokenLanguageSchema),
  status: z.string(),
  tagline: z.string().nullable(),
  title: z.string(),
  video: z.boolean(),
  vote_average: z.number(),
  vote_count: z.number(),
});

export type Movie = z.infer<typeof MovieApiSchema>;

const validateMovie = (response: AxiosResponse): Movie | null => {
  const result = MovieApiSchema.safeParse(response.data);
  if (!result.success) {
    console.log(result.error.issues);
    return null;
  }
  return result.data;
};

type Response<Type> =
  | {
      data: Type;
      status: number;
      success: true;
    }
  | {
      status: number;
      success: false;
    };

export const loadMovies = async (id?: string): Promise<Response<Movie>> => {
  const response = await getMovie(id!);
  if (!response) return { success: false, status: 0 };
  if (response.status !== 200)
    return { success: false, status: response.status };
  const data = validateMovie(response);
  if (!data) return { success: false, status: response.status };
  return { success: true, status: response.status, data };
};
