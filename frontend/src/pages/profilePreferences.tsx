import { useEffect, useMemo, useState } from "react";
import { useNavigate } from "react-router";
import { apiClient } from "../api/client";

type Genre = { id: number; name: string };

type GenresResponse = {
  movie: Genre[];
  tv: Genre[];
};

type Preferences = {
  likedGenreIds: number[];
  yearFrom: number | null;
  yearTo: number | null;
  includeMovies: boolean;
  includeSeries: boolean;
};

const MIN_YEAR = 1900;
const MAX_YEAR = new Date().getFullYear();

export default function ProfilePreferences() {
  const navigate = useNavigate();
  const [genres, setGenres] = useState<Genre[]>([]);
  const [prefs, setPrefs] = useState<Preferences | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [isSaving, setIsSaving] = useState(false);

  const likedSet = useMemo(() => new Set(prefs?.likedGenreIds ?? []), [prefs]);

  const toggleGenre = (genreId: number) => {
    setPrefs((prev) => {
      if (!prev) return prev;
      const next = new Set(prev.likedGenreIds);
      if (next.has(genreId)) next.delete(genreId);
      else next.add(genreId);
      return { ...prev, likedGenreIds: Array.from(next) };
    });
  };

  useEffect(() => {
    const load = async () => {
      setError(null);
      try {
        const [genresRes, prefsRes] = await Promise.all([
          apiClient.get<GenresResponse>("/api/v1/movie/genres"),
          apiClient.get<Preferences>("/api/v1/me/preferences"),
        ]);

        const combined = [
          ...(genresRes.data.movie ?? []),
          ...(genresRes.data.tv ?? []),
        ];
        const byId = new Map<number, Genre>();
        for (const g of combined) byId.set(g.id, g);
        setGenres(
          Array.from(byId.values()).sort((a, b) =>
            a.name.localeCompare(b.name),
          ),
        );

        const p = prefsRes.data;
        setPrefs({
          ...p,
          yearFrom: p.yearFrom ?? MIN_YEAR,
          yearTo: p.yearTo ?? MAX_YEAR,
        });
      } catch {
        setError("Failed to load profile preferences");
      }
    };

    load();
  }, []);

  const save = async () => {
    if (!prefs) return;
    setIsSaving(true);
    setError(null);

    try {
      await apiClient.put("/api/v1/me/preferences", prefs);

      try {
        localStorage.removeItem("currentRecommendation");
      } catch {
        // ignore
      }

      navigate("/", { replace: true });
      return;
    } catch {
      setError("Save failed");
    } finally {
      setIsSaving(false);
    }
  };

  if (!prefs) {
    return (
      <div className="flex justify-center items-center">
        <p>
          Loading...{" "}
          <span className="loading loading-infinity loading-md"></span>
        </p>
      </div>
    );
  }

  const yearFrom = prefs.yearFrom ?? MIN_YEAR;
  const yearTo = prefs.yearTo ?? MAX_YEAR;
  const span = Math.max(1, MAX_YEAR - MIN_YEAR);
  const fromPct = ((yearFrom - MIN_YEAR) / span) * 100;
  const toPct = ((yearTo - MIN_YEAR) / span) * 100;

  return (
    <div className="card bg-base-100 shadow-xl">
      <div className="card-body gap-4">
        <h2 className="text-xl font-semibold">My Preferences</h2>
        {error && <p className="text-red-500">{error}</p>}

        <div>
          <div className="font-semibold mb-2">Liked genres</div>
          <div className="dropdown">
            <div tabIndex={0} role="button" className="btn">
              Select genres ({prefs.likedGenreIds.length})
            </div>
            <div
              tabIndex={0}
              className="dropdown-content z-[1] p-2 shadow bg-base-100 rounded-box w-80 max-h-80 overflow-auto"
            >
              {genres.length === 0 ? (
                <p className="p-2 text-sm opacity-70">No genres available</p>
              ) : (
                <ul className="menu">
                  {genres.map((g) => (
                    <li key={g.id}>
                      <label className="label cursor-pointer justify-start gap-3">
                        <input
                          type="checkbox"
                          className="checkbox"
                          checked={likedSet.has(g.id)}
                          onChange={() => toggleGenre(g.id)}
                        />
                        <span className="label-text">{g.name}</span>
                      </label>
                    </li>
                  ))}
                </ul>
              )}
            </div>
          </div>
        </div>

        <div>
          <div className="font-semibold mb-2">Year range</div>
          <div className="flex items-end gap-3 mb-3 flex-wrap">
            <label className="form-control">
              <div className="label py-0">
                <span className="label-text text-sm">From</span>
              </div>
              <input
                type="number"
                className="input input-bordered input-sm w-28"
                min={MIN_YEAR}
                max={MAX_YEAR}
                value={yearFrom}
                placeholder={`${MIN_YEAR}`}
                onChange={(e) => {
                  const raw = e.target.value;
                  const parsed = raw === "" ? MIN_YEAR : Number(raw);
                  if (!Number.isFinite(parsed)) return;
                  const nextValue = Math.max(
                    MIN_YEAR,
                    Math.min(MAX_YEAR, Math.trunc(parsed)),
                  );

                  setPrefs((prev) =>
                    prev
                      ? {
                          ...prev,
                          yearFrom: nextValue,
                          yearTo: Math.max(prev.yearTo ?? MAX_YEAR, nextValue),
                        }
                      : prev,
                  );
                }}
              />
            </label>

            <label className="form-control">
              <div className="label py-0">
                <span className="label-text text-sm">To</span>
              </div>
              <input
                type="number"
                className="input input-bordered input-sm w-28"
                min={MIN_YEAR}
                max={MAX_YEAR}
                value={yearTo}
                placeholder={`${MAX_YEAR}`}
                onChange={(e) => {
                  const raw = e.target.value;
                  const parsed = raw === "" ? MAX_YEAR : Number(raw);
                  if (!Number.isFinite(parsed)) return;
                  const nextValue = Math.max(
                    MIN_YEAR,
                    Math.min(MAX_YEAR, Math.trunc(parsed)),
                  );

                  setPrefs((prev) =>
                    prev
                      ? {
                          ...prev,
                          yearTo: nextValue,
                          yearFrom: Math.min(
                            prev.yearFrom ?? MIN_YEAR,
                            nextValue,
                          ),
                        }
                      : prev,
                  );
                }}
              />
            </label>
          </div>

          <div className="relative h-6">
            <div className="absolute left-0 right-0 top-1/2 h-2 -translate-y-1/2 rounded bg-base-300" />
            <div
              className="absolute top-1/2 h-2 -translate-y-1/2 rounded bg-primary"
              style={{ left: `${fromPct}%`, right: `${100 - toPct}%` }}
            />

            <input
              type="range"
              min={MIN_YEAR}
              max={MAX_YEAR}
              value={yearFrom}
              className="dual-range absolute inset-0"
              aria-label="Year from"
              style={{ zIndex: yearFrom > MAX_YEAR - 2 ? 4 : 3 }}
              onChange={(e) => {
                const value = Number(e.target.value);
                setPrefs((prev) =>
                  prev
                    ? {
                        ...prev,
                        yearFrom: value,
                        yearTo: Math.max(prev.yearTo ?? MAX_YEAR, value),
                      }
                    : prev,
                );
              }}
            />
            <input
              type="range"
              min={MIN_YEAR}
              max={MAX_YEAR}
              value={yearTo}
              className="dual-range absolute inset-0"
              aria-label="Year to"
              style={{ zIndex: 4 }}
              onChange={(e) => {
                const value = Number(e.target.value);
                setPrefs((prev) =>
                  prev
                    ? {
                        ...prev,
                        yearTo: value,
                        yearFrom: Math.min(prev.yearFrom ?? MIN_YEAR, value),
                      }
                    : prev,
                );
              }}
            />
          </div>
        </div>

        <div>
          <div className="font-semibold mb-2">Media types</div>
          <div className="flex flex-row flex-wrap gap-6">
            <label className="label cursor-pointer justify-start gap-3">
              <input
                type="checkbox"
                className="checkbox"
                checked={prefs.includeMovies}
                onChange={(e) =>
                  setPrefs((prev) =>
                    prev ? { ...prev, includeMovies: e.target.checked } : prev,
                  )
                }
              />
              <span className="label-text">Movies</span>
            </label>
            <label className="label cursor-pointer justify-start gap-3">
              <input
                type="checkbox"
                className="checkbox"
                checked={prefs.includeSeries}
                onChange={(e) =>
                  setPrefs((prev) =>
                    prev ? { ...prev, includeSeries: e.target.checked } : prev,
                  )
                }
              />
              <span className="label-text">Series</span>
            </label>
          </div>
        </div>

        <div className="card-actions justify-end">
          <button
            className="btn btn-primary"
            disabled={isSaving}
            onClick={save}
          >
            {isSaving ? "Saving..." : "Save"}
          </button>
        </div>
      </div>
    </div>
  );
}
