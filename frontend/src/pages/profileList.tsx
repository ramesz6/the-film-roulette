import { useCallback, useEffect, useMemo, useState } from "react";
import { apiClient } from "../api/client";

type ListStatus = "WATCH_LATER" | "SEEN" | "DISLIKED";

type ListEntry = {
  tmdbId: number;
  mediaType: "movie" | "tv";
  status: ListStatus;
};

type TitleDetails = {
  id: number;
  title?: string;
  name?: string;
  overview?: string;
  posterPath?: string;
  releaseDate?: string;
  firstAirDate?: string;
  genreIds?: number[];
};

function titleLabel(d: TitleDetails | undefined, entry: ListEntry) {
  if (!d) return `${entry.mediaType.toUpperCase()} #${entry.tmdbId}`;
  return (
    d.title || d.name || `${entry.mediaType.toUpperCase()} #${entry.tmdbId}`
  );
}

export default function ProfileList() {
  const [active, setActive] = useState<ListStatus>("WATCH_LATER");
  const [entries, setEntries] = useState<ListEntry[]>([]);
  const [details, setDetails] = useState<
    Record<string, TitleDetails | undefined>
  >({});
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(true);

  const load = useCallback(async (status: ListStatus) => {
    setLoading(true);
    setError(null);

    try {
      const url =
        status === "WATCH_LATER"
          ? "/api/v1/me/list/watch-later"
          : status === "SEEN"
            ? "/api/v1/me/list/seen"
            : "/api/v1/me/list/disliked";
      const res = await apiClient.get<ListEntry[]>(url);
      setEntries(res.data);

      const nextDetails: Record<string, TitleDetails | undefined> = {};
      await Promise.all(
        res.data.map(async (e) => {
          const key = `${e.mediaType}:${e.tmdbId}`;
          try {
            const d = await apiClient.get<TitleDetails>(
              `/api/v1/movie/details/${e.mediaType}/${e.tmdbId}`,
            );
            nextDetails[key] = d.data;
          } catch {
            nextDetails[key] = undefined;
          }
        }),
      );
      setDetails(nextDetails);
    } catch {
      setError("Nem sikerült betölteni a listát");
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    load(active);
  }, [active, load]);

  const visible = useMemo(() => entries, [entries]);

  const remove = async (e: ListEntry) => {
    setError(null);
    try {
      await apiClient.delete(`/api/v1/me/list/${e.mediaType}/${e.tmdbId}`);
      await load(active);
    } catch {
      setError("Nem sikerült törölni a listából");
    }
  };

  const moveTo = async (e: ListEntry, status: ListStatus) => {
    const url =
      status === "WATCH_LATER"
        ? "/api/v1/me/list/watch-later"
        : status === "SEEN"
          ? "/api/v1/me/list/seen"
          : "/api/v1/me/list/disliked";
    setError(null);
    try {
      await apiClient.put(url, { tmdbId: e.tmdbId, mediaType: e.mediaType });
      await load(active);
    } catch {
      setError("Nem sikerült frissíteni a státuszt");
    }
  };

  return (
    <div className="card bg-base-100 shadow-xl">
      <div className="card-body">
        <h2 className="text-xl font-semibold mb-2">My List</h2>

        <div role="tablist" className="tabs tabs-boxed mb-4">
          <button
            role="tab"
            className={`tab ${active === "WATCH_LATER" ? "tab-active" : ""}`}
            onClick={() => setActive("WATCH_LATER")}
          >
            Like
          </button>
          <button
            role="tab"
            className={`tab ${active === "SEEN" ? "tab-active" : ""}`}
            onClick={() => setActive("SEEN")}
          >
            Watched
          </button>
          <button
            role="tab"
            className={`tab ${active === "DISLIKED" ? "tab-active" : ""}`}
            onClick={() => setActive("DISLIKED")}
          >
            Disliked
          </button>
        </div>

        {error && <p className="text-red-500 mb-3">{error}</p>}

        {loading ? (
          <p>
            Betöltés{" "}
            <span className="loading loading-infinity loading-md"></span>
          </p>
        ) : visible.length === 0 ? (
          <p className="opacity-70">Üres</p>
        ) : (
          <div className="grid grid-cols-1 gap-3">
            {visible.map((e) => {
              const key = `${e.mediaType}:${e.tmdbId}`;
              const d = details[key];
              return (
                <div key={key} className="card bg-base-100 shadow">
                  <div className="card-body">
                    <div className="flex justify-between gap-3 items-start">
                      <div>
                        <h3 className="card-title">{titleLabel(d, e)}</h3>
                        {d?.overview && (
                          <p className="text-sm opacity-80 line-clamp-3">
                            {d.overview}
                          </p>
                        )}
                        <p className="text-xs opacity-60 mt-1">
                          {e.mediaType.toUpperCase()} • TMDB #{e.tmdbId}
                        </p>
                      </div>
                      <div className="flex flex-col gap-2 items-stretch">
                        {active === "WATCH_LATER" ? (
                          <button
                            className="btn btn-sm w-full"
                            onClick={() => moveTo(e, "SEEN")}
                          >
                            Mark as watched
                          </button>
                        ) : active === "SEEN" ? (
                          <button
                            className="btn btn-sm w-full"
                            onClick={() => moveTo(e, "WATCH_LATER")}
                          >
                            Like
                          </button>
                        ) : null}

                        <button
                          className="btn btn-sm btn-outline w-full"
                          onClick={() => remove(e)}
                        >
                          Remove
                        </button>
                      </div>
                    </div>
                  </div>
                </div>
              );
            })}
          </div>
        )}
      </div>
    </div>
  );
}
