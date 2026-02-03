import { useState, useEffect } from "react";
import "../App.css";
import { loadMovie } from "../api/getMovie";
import { Movies } from "../api/getMovie";
import { Link, useNavigate } from "react-router";
import { clearAuthToken } from "../LocalStorage";
import { apiClient } from "../api/client";

const MainPage = () => {
  const navigate = useNavigate();
  const [error, setError] = useState("");
  const [movieDatas, setMovieDatas] = useState<Movies[] | null>(null);
  const [waitingForData, setWaitingForData] = useState(true);

  const logout = () => {
    clearAuthToken();
    navigate("/login", { replace: true });
  };

  const addToList = async (status: "watch-later" | "seen", tmdbId: number) => {
    try {
      const url =
        status === "watch-later"
          ? "/api/v1/me/list/watch-later"
          : "/api/v1/me/list/seen";
      await apiClient.put(url, { tmdbId, mediaType: "movie" });
    } catch {
      setError("Nem sikerült hozzáadni a listához");
    }
  };

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
      <div className="navbar bg-base-100 shadow mb-4">
        <div className="flex-1">
          <span className="btn btn-ghost text-xl">The Film Roulette</span>
        </div>
        <div className="flex-none gap-2">
          <Link className="btn btn-sm" to="/profile">
            My Profile
          </Link>
          <Link className="btn btn-sm" to="/my-list">
            My List
          </Link>
          <button className="btn btn-sm btn-outline" onClick={logout}>
            Logout
          </button>
        </div>
      </div>
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
              <div className="card-actions justify-end p-3 gap-2">
                <button
                  className="btn btn-sm"
                  onClick={() => addToList("watch-later", movie.id)}
                >
                  Watch later
                </button>
                <button
                  className="btn btn-sm btn-primary"
                  onClick={() => addToList("seen", movie.id)}
                >
                  Seen
                </button>
              </div>
            </div>
          ))}
        </div>
      )}
    </>
  );
};

export default MainPage;
