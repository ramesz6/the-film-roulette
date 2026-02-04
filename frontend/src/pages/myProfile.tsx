import { useEffect, useMemo, useState } from "react";
import { apiClient } from "../api/client";
import { Link, useLocation, useNavigate } from "react-router";
import { clearAuthToken } from "../LocalStorage";

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

const currentYear = new Date().getFullYear();

export default function MyProfile() {
  const navigate = useNavigate();
  const location = useLocation();
  const [genres, setGenres] = useState<Genre[]>([]);
  const [prefs, setPrefs] = useState<Preferences | null>(null);
  const [notice, setNotice] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);
  const [isSaving, setIsSaving] = useState(false);

  const logout = () => {
    clearAuthToken();
    navigate("/login", { replace: true });
  };

  const minYear = 1900;
  const maxYear = currentYear;

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
          yearFrom: p.yearFrom ?? minYear,
          yearTo: p.yearTo ?? maxYear,
        });
      } catch {
        setError("Nem sikerült betölteni a profil beállításokat");
      }
    };

    load();
  }, []);

  useEffect(() => {
    const state = (location.state ?? null) as { message?: string } | null;
    if (typeof state?.message === "string" && state.message.trim().length > 0) {
      setNotice(state.message);
    }
  }, [location.state]);

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
      setError("Mentés sikertelen");
    } finally {
      setIsSaving(false);
    }
  };

  if (!prefs) {
    return (
      <div className="flex justify-center items-center">
        <p>
          Betöltés <span className="loading loading-infinity loading-md"></span>
        </p>
      </div>
    );
  }

  const yearFrom = prefs.yearFrom ?? minYear;
  const yearTo = prefs.yearTo ?? maxYear;

  return (
    <>
      <div className="navbar bg-base-100 shadow mb-4">
        <div className="flex-1">
          <Link className="btn btn-ghost text-xl" to="/">
            Roulette
          </Link>
        </div>
        <div className="flex-none gap-2">
          <Link className="btn btn-sm" to="/">
            Roulette
          </Link>
          <Link className="btn btn-sm" to="/my-list">
            My List
          </Link>
          <button className="btn btn-sm btn-outline" onClick={logout}>
            Logout
          </button>
        </div>
      </div>

      <div className="max-w-2xl mx-auto p-4">
        <h1 className="text-2xl font-bold mb-4">My Profile</h1>
        {notice && (
          <div className="alert alert-warning mb-3">
            <span>{notice}</span>
          </div>
        )}
        {error && <p className="text-red-500 mb-3">{error}</p>}

        <div className="card bg-base-100 shadow-xl">
          <div className="card-body gap-4">
            <div>
              <div className="font-semibold mb-2">Liked genres</div>
              <div className="dropdown">
                <div tabIndex={0} role="button" className="btn">
                  Zsánerek kiválasztása ({prefs.likedGenreIds.length})
                </div>
                <div
                  tabIndex={0}
                  className="dropdown-content z-[1] p-2 shadow bg-base-100 rounded-box w-80 max-h-80 overflow-auto"
                >
                  {genres.length === 0 ? (
                    <p className="p-2 text-sm opacity-70">
                      Nincs elérhető genre lista
                    </p>
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
              <div className="grid grid-cols-1 gap-3">
                <div>
                  <div className="text-sm opacity-70 mb-1">
                    From: {yearFrom}
                  </div>
                  <input
                    type="range"
                    min={minYear}
                    max={maxYear}
                    value={yearFrom}
                    className="range"
                    onChange={(e) => {
                      const value = Number(e.target.value);
                      setPrefs((prev) =>
                        prev
                          ? {
                              ...prev,
                              yearFrom: value,
                              yearTo: Math.max(prev.yearTo ?? maxYear, value),
                            }
                          : prev,
                      );
                    }}
                  />
                </div>
                <div>
                  <div className="text-sm opacity-70 mb-1">To: {yearTo}</div>
                  <input
                    type="range"
                    min={minYear}
                    max={maxYear}
                    value={yearTo}
                    className="range"
                    onChange={(e) => {
                      const value = Number(e.target.value);
                      setPrefs((prev) =>
                        prev
                          ? {
                              ...prev,
                              yearTo: value,
                              yearFrom: Math.min(
                                prev.yearFrom ?? minYear,
                                value,
                              ),
                            }
                          : prev,
                      );
                    }}
                  />
                </div>
              </div>
            </div>

            <div>
              <div className="font-semibold mb-2">Content type</div>
              <label className="label cursor-pointer justify-start gap-3">
                <input
                  type="checkbox"
                  className="checkbox"
                  checked={prefs.includeMovies}
                  onChange={(e) =>
                    setPrefs({ ...prefs, includeMovies: e.target.checked })
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
                    setPrefs({ ...prefs, includeSeries: e.target.checked })
                  }
                />
                <span className="label-text">Series</span>
              </label>
            </div>

            <div className="card-actions justify-end">
              <button
                className="btn btn-primary"
                onClick={save}
                disabled={isSaving}
              >
                {isSaving ? "Mentés..." : "Mentés"}
              </button>
            </div>
          </div>
        </div>
      </div>
    </>
  );
}
