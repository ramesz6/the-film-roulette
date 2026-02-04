import { useEffect, useState } from "react";
import { Navigate, useLocation, useNavigate } from "react-router";
import { apiClient } from "../api/client";

type PreferencesResponse = {
  likedGenreIds: number[];
  yearFrom: number | null;
  yearTo: number | null;
  includeMovies: boolean;
  includeSeries: boolean;
};

const isConfigured = (prefs: PreferencesResponse): boolean => {
  const hasGenres =
    Array.isArray(prefs.likedGenreIds) && prefs.likedGenreIds.length > 0;
  const hasYearRange =
    typeof prefs.yearFrom === "number" && typeof prefs.yearTo === "number";
  const hasType = prefs.includeMovies || prefs.includeSeries;
  return hasGenres && hasYearRange && hasType;
};

const buildMissingProfileMessage = (prefs: PreferencesResponse): string => {
  const missing: string[] = [];

  if (!Array.isArray(prefs.likedGenreIds) || prefs.likedGenreIds.length === 0) {
    missing.push("válassz legalább 1 zsánert");
  }
  if (typeof prefs.yearFrom !== "number" || typeof prefs.yearTo !== "number") {
    missing.push("állíts be évszám tartományt");
  }
  if (!prefs.includeMovies && !prefs.includeSeries) {
    missing.push("jelöld be a Film vagy Sorozat opciót");
  }

  if (missing.length === 0) {
    return "A ROULETTE használatához töltsd ki a profilt.";
  }

  return `A ROULETTE használatához töltsd ki a profilt: ${missing.join(", ")}.`;
};

export default function RequireProfile({
  children,
}: {
  children: React.ReactNode;
}) {
  const [loading, setLoading] = useState(true);
  const [allowed, setAllowed] = useState(false);
  const navigate = useNavigate();
  const location = useLocation();

  useEffect(() => {
    let active = true;
    apiClient
      .get<PreferencesResponse>("/api/v1/me/preferences")
      .then((res) => {
        if (!active) return;
        if (isConfigured(res.data)) {
          setAllowed(true);
          setLoading(false);
          return;
        }
        setAllowed(false);
        setLoading(false);
        navigate("/profile", {
          replace: true,
          state: {
            from: location.pathname,
            message: buildMissingProfileMessage(res.data),
          },
        });
      })
      .catch(() => {
        if (!active) return;
        setAllowed(false);
        setLoading(false);
        navigate("/profile", {
          replace: true,
          state: {
            from: location.pathname,
            message: "Nem sikerült ellenőrizni a profilt. Próbáld újra.",
          },
        });
      });

    return () => {
      active = false;
    };
  }, [location.pathname, navigate]);

  if (loading) {
    return (
      <div className="flex justify-center items-center min-h-[50vh]">
        <span className="loading loading-infinity loading-lg"></span>
      </div>
    );
  }

  if (!allowed) {
    return (
      <Navigate
        to="/profile"
        replace
        state={{
          from: location.pathname,
          message: "A ROULETTE használatához töltsd ki a profilt.",
        }}
      />
    );
  }

  return children;
}
