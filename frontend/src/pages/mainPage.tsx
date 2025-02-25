import { useState, useEffect } from "react";
import "./App.css";
import { loadMovie } from "../api/getMovie";
import { Movies } from "../api/getMovie";

const MainPage = () => {
  const [error, setError] = useState("");
  const [movieDatas, setMovieDatas] = useState<Movies[] | null>(null);
  const [waitingForData, setWaitingForData] = useState(true);

  const getMovies = async () => {
    const response = await loadMovie();

    if (!response.success) {
      setError("A szerver nem elérhető");
      setWaitingForData(false);
    } else {
      setMovieDatas(response.data);
      setWaitingForData(false);
    }
  };

  useEffect(() => {
    getMovies();
  }, []);

  return (
    <>
      {error && (
        <p className="flex justify-center items-center text-center text-red-500">
          {error}
        </p>
      )}
      {waitingForData ? (
        <p className="flex justify-center items-center text-center">
          Adatok betöltése
          <span className="loading loading-infinity loading-lg"></span>
        </p>
      ) : (
        <div className="flex flex-wrap flex-row justify-center items-center">
          {movieDatas?.map((movie) => (
            <div
              key={movie.title}
              className="card w-96 bg-base-100 shadow-xl m-5"
            >
              <h1 className="card-title">{movie.title}</h1>
              <img src={movie.posterPath} alt={movie.title} />
            </div>
          ))}
        </div>
      )}
    </>
  );
};

export default MainPage;
