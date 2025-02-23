import axios, { AxiosError, AxiosResponse } from "axios";
import { z } from "zod";

const Movie = axios.create({
  baseURL: "http://localhost:8080" 
});

const getMovie = async (): Promise<AxiosResponse | null> => {
  try {
    const response = await Movie.get("/api/v1/movie/discover");
    console.log(response.data)
    return response;
  } catch (error) {
    return (error as AxiosError).response || null;
  }
};

const Moviechema = z.object({
    adult: z.boolean(),
    backdropPath: z.string(),
    genreIds: z.array(z.number().int()),
    id: z.number().int(),
    originalLanguage: z.string(),
    originalTitle: z.string(),
    overview: z.string(),
    popularity: z.number(),
    posterPath: z.string(),
    releaseDate: z.string(),
    title: z.string(),
    video: z.boolean(),
    voteAverage: z.number(),
    voteCount: z.number().int().array(),
});

export type Movies = z.infer<typeof Moviechema>;

const validateMovie = (response: AxiosResponse): Movies [] | null => {
  const result = Moviechema.array().safeParse(response.data);
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

export const loadMovie = async (): Promise<Response<Movies []>> => {
  const response = await getMovie();
  if (!response) return { success: false, status: 0 };
  if (response.status !== 200)
    return { success: false, status: response.status };
  const data = validateMovie(response);
  if (!data) return { success: false, status: response.status };
  return { success: true, status: response.status, data };
};
