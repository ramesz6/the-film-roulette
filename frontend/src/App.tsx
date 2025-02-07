import { useState, useEffect } from "react";
import "./App.css";
import { Movie } from "./api/getRandomMovie";
import { loadMovies } from "./api/getRandomMovie";

function App() {
  const [movieDatas, setMovieDatas] = useState<Movie | null>(null);

  function generateRandomNumber(): string {
    let result = Math.floor(Math.random() * 996) + 1;
    return "" + result;
  }

  const getMovie = async (id: string) => {
    try {
      const response = await loadMovies(id);
      if (response.success && response.data) {
        setMovieDatas(response.data);
      } else if (response.status === 404) {
        getMovie(generateRandomNumber());
      } else {
        console.error("Failed to fetch movie data");
      }
    } catch (error) {
      console.error("Error fetching movie:", error);
    }
  };

  useEffect(() => {
    const randomId = generateRandomNumber();
    getMovie(randomId);
  }, []);

  return (
    <>
      {movieDatas ? (
        <div className="flex flex-col justify-center items-center min-h-screen">
          <div className="card bg-base-100 w-96 shadow-xl">
            <figure className="px-10 pt-10">
              <img
                src={`https://image.tmdb.org/t/p/w500${movieDatas.poster_path}`}
                alt="Movie Poster"
                className="rounded-xl"
              />
            </figure>
            <div className="card-body items-center text-center">
              <h2 className="card-title">{movieDatas.original_title}</h2>
              <p>{`${movieDatas.release_date?.split("-").join(".")}.`}</p>
              <div className="flex flex-row flex-wrap gap-2">
                {movieDatas.genres && movieDatas.genres.length > 0 ? (
                  movieDatas.genres.map((genre) => (
                    <p key={genre.id} className="px-2 py-1 rounded">
                      {genre.name}
                    </p>
                  ))
                ) : (
                  <p>No genres available</p>
                )}
              </div>
              <div className="card-actions">
                <button
                  className="btn btn-primary"
                  onClick={() => {
                    getMovie(generateRandomNumber());
                  }}
                >
                  Randomized
                </button>
              </div>
            </div>
          </div>
        </div>
      ) : (
        <p>Loading...</p>
      )}
    </>
  );
}

export default App;
