import { useCallback, useEffect, useState } from "react";
import "../App.css";
import { Link, useNavigate } from "react-router";
import { clearAuthToken } from "../LocalStorage";
import { apiClient } from "../api/client";

type Recommendation = {
  mediaType: "movie" | "tv";
  tmdbId: number;
  title: string;
  overview: string | null;
  posterPath: string | null;
  releaseDate: string | null;
  genreIds: number[];
};

const posterUrl = (posterPath: string | null): string | null => {
  if (!posterPath) return null;
  if (posterPath.startsWith("http://") || posterPath.startsWith("https://")) {
    return posterPath;
  }
  return `https://image.tmdb.org/t/p/w500${posterPath}`;
};

const tmdbUrl = (mediaType: "movie" | "tv", id: number): string => {
  return mediaType === "movie"
    ? `https://www.themoviedb.org/movie/${id}`
    : `https://www.themoviedb.org/tv/${id}`;
};

const DISLIKED_KEY = "dislikedTitles";
const CURRENT_RECOMMENDATION_KEY = "currentRecommendation";

const isValidRecommendation = (value: unknown): value is Recommendation => {
  if (!value || typeof value !== "object") return false;
  const rec = value as Partial<Recommendation>;
  if (rec.mediaType !== "movie" && rec.mediaType !== "tv") return false;
  if (typeof rec.tmdbId !== "number" || !Number.isFinite(rec.tmdbId))
    return false;
  if (typeof rec.title !== "string") return false;
  if (rec.overview != null && typeof rec.overview !== "string") return false;
  if (rec.posterPath != null && typeof rec.posterPath !== "string")
    return false;
  if (rec.releaseDate != null && typeof rec.releaseDate !== "string")
    return false;
  if (
    rec.genreIds != null &&
    (!Array.isArray(rec.genreIds) ||
      !rec.genreIds.every((g) => typeof g === "number"))
  ) {
    return false;
  }
  return true;
};

const loadCurrentRecommendation = (): Recommendation | null => {
  try {
    const raw = localStorage.getItem(CURRENT_RECOMMENDATION_KEY);
    if (!raw) return null;
    const parsed = JSON.parse(raw);
    if (!isValidRecommendation(parsed)) return null;
    return {
      mediaType: parsed.mediaType,
      tmdbId: parsed.tmdbId,
      title: parsed.title,
      overview: parsed.overview ?? null,
      posterPath: parsed.posterPath ?? null,
      releaseDate: parsed.releaseDate ?? null,
      genreIds: Array.isArray(parsed.genreIds) ? parsed.genreIds : [],
    };
  } catch {
    return null;
  }
};

const saveCurrentRecommendation = (rec: Recommendation) => {
  try {
    localStorage.setItem(CURRENT_RECOMMENDATION_KEY, JSON.stringify(rec));
  } catch {
    // ignore
  }
};

const clearCurrentRecommendation = () => {
  try {
    localStorage.removeItem(CURRENT_RECOMMENDATION_KEY);
  } catch {
    // ignore
  }
};

const loadDisliked = (): string[] => {
  try {
    const raw = localStorage.getItem(DISLIKED_KEY);
    if (!raw) return [];
    const parsed = JSON.parse(raw);
    return Array.isArray(parsed)
      ? parsed.filter((x) => typeof x === "string")
      : [];
  } catch {
    return [];
  }
};

const saveDisliked = (items: string[]) => {
  try {
    localStorage.setItem(DISLIKED_KEY, JSON.stringify(items.slice(-500)));
  } catch {
    // ignore
  }
};

const MainPage = () => {
  const navigate = useNavigate();
  const [error, setError] = useState("");
  const [waitingForData, setWaitingForData] = useState(true);
  const [current, setCurrent] = useState<Recommendation | null>(null);

  const logout = () => {
    clearAuthToken();
    clearCurrentRecommendation();
    navigate("/login", { replace: true });
  };

  const addToList = async (
    status: "watch-later" | "seen" | "disliked",
    mediaType: "movie" | "tv",
    tmdbId: number,
  ) => {
    try {
      const url =
        status === "watch-later"
          ? "/api/v1/me/list/watch-later"
          : status === "seen"
            ? "/api/v1/me/list/seen"
            : "/api/v1/me/list/disliked";
      await apiClient.put(url, { tmdbId, mediaType });
      return true;
    } catch {
      setError("Nem sikerült hozzáadni a listához");
      return false;
    }
  };

  const loadNext = useCallback(async () => {
    setWaitingForData(true);
    setError("");

    const excluded = loadDisliked();
    for (let i = 0; i < 8; i++) {
      try {
        const params = new URLSearchParams();
        for (const item of excluded) {
          params.append("exclude", item);
        }
        const res = await apiClient.get<Recommendation>(
          "/api/v1/me/recommendation/next",
          {
            params,
          },
        );
        const rec = res.data;
        const key = `${rec.mediaType}:${rec.tmdbId}`;
        if (!excluded.includes(key)) {
          setCurrent(rec);
          saveCurrentRecommendation(rec);
          setWaitingForData(false);
          return;
        }
      } catch (err: unknown) {
        const status = (() => {
          if (typeof err !== "object" || err === null) return undefined;
          if (!("response" in err)) return undefined;
          const response = (err as { response?: unknown }).response;
          if (typeof response !== "object" || response === null)
            return undefined;
          if (!("status" in response)) return undefined;
          const value = (response as { status?: unknown }).status;
          return typeof value === "number" ? value : undefined;
        })();
        if (status === 428) {
          clearCurrentRecommendation();
          navigate("/profile/preferences", { replace: true });
          return;
        }
        if (status === 404) {
          setError(
            "Nem találtam a profilodhoz illő filmet/sorozatot. Próbáld bővíteni a preferenciákat.",
          );
          setCurrent(null);
          clearCurrentRecommendation();
          setWaitingForData(false);
          return;
        }
        setError("A szerver nem elérhető");
        setWaitingForData(false);
        return;
      }
    }

    setError("Nem sikerült új ajánlást találni");
    setWaitingForData(false);
  }, [navigate]);

  useEffect(() => {
    const cached = loadCurrentRecommendation();
    if (cached) {
      setCurrent(cached);
      setWaitingForData(false);
      return;
    }
    loadNext();
  }, [loadNext]);

  const dislike = async () => {
    if (!current) return;

    const saved = await addToList("disliked", current.mediaType, current.tmdbId);
    if (!saved) {
      return;
    }

    const existing = loadDisliked();
    const key = `${current.mediaType}:${current.tmdbId}`;
    if (!existing.includes(key)) {
      saveDisliked([...existing, key]);
    }

    clearCurrentRecommendation();
    await loadNext();
  };

  const seen = async () => {
    if (!current) return;

    const saved = await addToList("seen", current.mediaType, current.tmdbId);
    if (!saved) {
      return;
    }

    clearCurrentRecommendation();
    await loadNext();
  };

  const watchLater = async () => {
    if (!current) return;

    const saved = await addToList(
      "watch-later",
      current.mediaType,
      current.tmdbId,
    );
    if (!saved) {
      return;
    }

    clearCurrentRecommendation();
    await loadNext();
  };

  const watchNow = async () => {
    if (!current) return;
    window.open(tmdbUrl(current.mediaType, current.tmdbId), "_blank");
    clearCurrentRecommendation();
    await loadNext();
  };

  return (
    <>
      <div className="navbar bg-base-100 shadow mb-4">
        <div className="flex-1">
          <span className="btn btn-ghost text-xl">Roulette</span>
        </div>
        <div className="flex-none gap-2">
          <Link className="btn btn-sm" to="/profile">
            My Profile
          </Link>
          <Link className="btn btn-sm" to="/profile/list">
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
        <div className="flex flex-col items-center justify-center gap-4 px-4">
          {current ? (
            <div className="card w-full max-w-[26rem] bg-base-100 shadow-xl">
              <figure className="bg-base-200 p-2 flex items-center justify-center">
                {posterUrl(current.posterPath) ? (
                  <img
                    src={posterUrl(current.posterPath) ?? undefined}
                    alt={current.title}
                    className="w-full h-auto max-h-80 object-contain"
                  />
                ) : (
                  <div className="w-full h-56 flex items-center justify-center">
                    <span className="opacity-60">Nincs borítókép</span>
                  </div>
                )}
              </figure>
              <div className="card-body p-6">
                <div className="flex items-baseline justify-between gap-3">
                  <h2 className="card-title text-lg break-words">{current.title}</h2>
                  {current.releaseDate ? (
                    <span className="badge badge-outline badge-sm">
                      {current.releaseDate.slice(0, 4)}
                    </span>
                  ) : null}
                </div>
                {current.overview ? (
                  <p className="text-sm opacity-80 line-clamp-4">
                    {current.overview}
                  </p>
                ) : null}

                <div className="card-actions justify-center gap-2 pt-2">
                  <button className="btn btn-success" onClick={watchNow}>
                    MEGNÉZEM MOST
                  </button>
                  <button className="btn btn-error" onClick={dislike}>
                    NEM TETSZIK
                  </button>
                </div>

                <div className="card-actions justify-center gap-2">
                  <button className="btn btn-primary btn-sm" onClick={seen}>
                    LÁTTAM
                  </button>
                  <button className="btn btn-sm" onClick={watchLater}>
                    MEGNÉZEM KÉSŐBB
                  </button>
                </div>
              </div>
            </div>
          ) : (
            <div className="text-center opacity-80">
              Nincs megjeleníthető cím.
            </div>
          )}
        </div>
      )}
    </>
  );
};

export default MainPage;
