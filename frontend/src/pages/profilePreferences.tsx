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
	const [movieGenres, setMovieGenres] = useState<Genre[]>([]);
	const [tvGenres, setTvGenres] = useState<Genre[]>([]);
	const [prefs, setPrefs] = useState<Preferences | null>(null);
	const [savedPrefs, setSavedPrefs] = useState<Preferences | null>(null);
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

				setMovieGenres((genresRes.data.movie ?? []).slice());
				setTvGenres((genresRes.data.tv ?? []).slice());

				const p = prefsRes.data;
				const normalized: Preferences = {
					...p,
					yearFrom: p.yearFrom ?? MIN_YEAR,
					yearTo: p.yearTo ?? MAX_YEAR,
					likedGenreIds: Array.from(new Set(p.likedGenreIds ?? [])),
				};
				setPrefs(normalized);
				setSavedPrefs(normalized);
			} catch {
				setError("Failed to load profile preferences");
			}
		};

		load();
	}, []);

	const isDirty = useMemo(() => {
		if (!prefs || !savedPrefs) return false;
		if (prefs.includeMovies !== savedPrefs.includeMovies) return true;
		if (prefs.includeSeries !== savedPrefs.includeSeries) return true;
		if ((prefs.yearFrom ?? MIN_YEAR) !== (savedPrefs.yearFrom ?? MIN_YEAR))
			return true;
		if ((prefs.yearTo ?? MAX_YEAR) !== (savedPrefs.yearTo ?? MAX_YEAR))
			return true;
		const a = Array.from(new Set(prefs.likedGenreIds ?? [])).sort(
			(x, y) => x - y,
		);
		const b = Array.from(new Set(savedPrefs.likedGenreIds ?? [])).sort(
			(x, y) => x - y,
		);
		if (a.length !== b.length) return true;
		for (let i = 0; i < a.length; i++) {
			if (a[i] !== b[i]) return true;
		}
		return false;
	}, [prefs, savedPrefs]);

	const allowedGenreIds = useMemo(() => {
		const allow = new Set<number>();
		if (!prefs) return allow;
		if (prefs.includeMovies) {
			for (const g of movieGenres) allow.add(g.id);
		}
		if (prefs.includeSeries) {
			for (const g of tvGenres) allow.add(g.id);
		}
		return allow;
	}, [prefs, movieGenres, tvGenres]);

	const visibleGenres = useMemo(() => {
		if (!prefs) return [] as Array<{ id: number; label: string }>;

		if (prefs.includeMovies && prefs.includeSeries) {
			const combined: Array<{ id: number; label: string }> = [];
			for (const g of movieGenres)
				combined.push({ id: g.id, label: `${g.name} (Movie)` });
			for (const g of tvGenres)
				combined.push({ id: g.id, label: `${g.name} (TV)` });
			return combined.sort((a, b) => a.label.localeCompare(b.label));
		}

		const src = prefs.includeSeries ? tvGenres : movieGenres;
		return src
			.map((g) => ({ id: g.id, label: g.name }))
			.sort((a, b) => a.label.localeCompare(b.label));
	}, [prefs, movieGenres, tvGenres]);

	const save = async () => {
		if (!prefs) return;
		setIsSaving(true);
		setError(null);

		const filtered = {
			...prefs,
			likedGenreIds: prefs.likedGenreIds.filter((id) =>
				allowedGenreIds.has(id),
			),
		};

		try {
			const putRes = await apiClient.put<Preferences>(
				"/api/v1/me/preferences",
				filtered,
			);
			// Verify persistence (prevents "reroll" if backend silently ignores changes)
			const verifyRes = await apiClient.get<Preferences>(
				"/api/v1/me/preferences",
			);
			const persisted = verifyRes.data;
			const persistedNormalized: Preferences = {
				...persisted,
				yearFrom: persisted.yearFrom ?? MIN_YEAR,
				yearTo: persisted.yearTo ?? MAX_YEAR,
				likedGenreIds: Array.from(new Set(persisted.likedGenreIds ?? [])),
			};

			// If the server didn't persist what it claimed, don't clear roulette cache / navigate.
			const claimed = putRes.data;
			const claimedNormalized: Preferences = {
				...claimed,
				yearFrom: claimed.yearFrom ?? MIN_YEAR,
				yearTo: claimed.yearTo ?? MAX_YEAR,
				likedGenreIds: Array.from(new Set(claimed.likedGenreIds ?? [])),
			};
			const sameYearFrom =
				(persistedNormalized.yearFrom ?? MIN_YEAR) ===
				(claimedNormalized.yearFrom ?? MIN_YEAR);
			const sameYearTo =
				(persistedNormalized.yearTo ?? MAX_YEAR) ===
				(claimedNormalized.yearTo ?? MAX_YEAR);
			const sameMovies =
				persistedNormalized.includeMovies === claimedNormalized.includeMovies;
			const sameSeries =
				persistedNormalized.includeSeries === claimedNormalized.includeSeries;
			const pa = (persistedNormalized.likedGenreIds ?? [])
				.slice()
				.sort((x, y) => x - y);
			const pb = (claimedNormalized.likedGenreIds ?? [])
				.slice()
				.sort((x, y) => x - y);
			const sameGenres =
				pa.length === pb.length && pa.every((v, i) => v === pb[i]);
			if (
				!(sameYearFrom && sameYearTo && sameMovies && sameSeries && sameGenres)
			) {
				setPrefs(persistedNormalized);
				setSavedPrefs(persistedNormalized);
				setError("Save did not persist. Please try again.");
				return;
			}

			setPrefs(persistedNormalized);
			setSavedPrefs(persistedNormalized);

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
							className="dropdown-content z-[1] p-2 shadow bg-base-100 rounded-box w-80 max-w-[calc(100vw-2rem)] max-h-80 overflow-auto"
						>
							{visibleGenres.length === 0 ? (
								<p className="p-2 text-sm opacity-70">No genres available</p>
							) : (
								<ul className="menu">
									{visibleGenres.map((g) => (
										<li key={g.id}>
											<label className="label cursor-pointer justify-start gap-3">
												<input
													type="checkbox"
													className="checkbox"
													checked={likedSet.has(g.id)}
													onChange={() => toggleGenre(g.id)}
												/>
												<span className="label-text">{g.label}</span>
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
								onChange={(e) => {
									const checked = e.target.checked;
									setPrefs((prev) => {
										if (!prev) return prev;
										const next = { ...prev, includeMovies: checked };
										const nextAllowed = new Set<number>();
										if (next.includeMovies)
											for (const g of movieGenres) nextAllowed.add(g.id);
										if (next.includeSeries)
											for (const g of tvGenres) nextAllowed.add(g.id);
										next.likedGenreIds = next.likedGenreIds.filter((id) =>
											nextAllowed.has(id),
										);
										return next;
									});
								}}
							/>
							<span className="label-text">Movies</span>
						</label>
						<label className="label cursor-pointer justify-start gap-3">
							<input
								type="checkbox"
								className="checkbox"
								checked={prefs.includeSeries}
								onChange={(e) => {
									const checked = e.target.checked;
									setPrefs((prev) => {
										if (!prev) return prev;
										const next = { ...prev, includeSeries: checked };
										const nextAllowed = new Set<number>();
										if (next.includeMovies)
											for (const g of movieGenres) nextAllowed.add(g.id);
										if (next.includeSeries)
											for (const g of tvGenres) nextAllowed.add(g.id);
										next.likedGenreIds = next.likedGenreIds.filter((id) =>
											nextAllowed.has(id),
										);
										return next;
									});
								}}
							/>
							<span className="label-text">Series</span>
						</label>
					</div>
				</div>

				<div className="card-actions justify-end">
					<button
						className="btn btn-primary"
						disabled={isSaving || !isDirty}
						onClick={save}
					>
						{isSaving ? "Saving..." : "Save"}
					</button>
				</div>
			</div>
		</div>
	);
}
