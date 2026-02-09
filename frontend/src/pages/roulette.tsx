import { useCallback, useEffect, useRef, useState } from "react";
import "../App.css";
import { Link, useNavigate } from "react-router";
import { apiClient } from "../api/client";
import { clearAuthToken } from "../LocalStorage";
import type { TitleDetails } from "../types/tmdb";
import { getHttpStatus } from "../utils/getHttpStatus";
import {
	formatGenresLine,
	formatRuntime,
	posterUrl,
	tmdbTitleUrl,
} from "../utils/tmdb";

type Recommendation = {
	mediaType: "movie" | "tv";
	tmdbId: number;
	title: string;
	overview: string | null;
	posterPath: string | null;
	releaseDate: string | null;
	genreIds: number[];
};

// shared TMDB/formatting helpers live in ../utils/tmdb

const youtubeTrailerSearchUrl = (query: string) =>
	`https://www.youtube.com/results?search_query=${encodeURIComponent(
		`${query} trailer`,
	)}`;

const extractYouTubeKey = (url: string | undefined | null): string | null => {
	if (!url) return null;
	const trimmed = url.trim();
	if (!trimmed) return null;

	const playIdx = trimmed.indexOf("#play=");
	if (playIdx >= 0) {
		const key = trimmed.slice(playIdx + "#play=".length).split(/[&?#]/)[0];
		return key && key.length > 0 ? key : null;
	}

	try {
		const u = new URL(trimmed);
		const v = u.searchParams.get("v");
		if (v) return v;
		if (u.hostname === "youtu.be") {
			const key = u.pathname.replace(/^\//, "").split("/")[0];
			return key && key.length > 0 ? key : null;
		}
	} catch {
		// ignore
	}

	return null;
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

const RoulettePage = () => {
	const navigate = useNavigate();
	const [error, setError] = useState("");
	const [waitingForData, setWaitingForData] = useState(true);
	const [current, setCurrent] = useState<Recommendation | null>(null);
	const [currentDetails, setCurrentDetails] = useState<TitleDetails | null>(
		null,
	);
	const [detailsLoading, setDetailsLoading] = useState(false);
	const [detailsError, setDetailsError] = useState<string | null>(null);
	const [detailsVersion, setDetailsVersion] = useState(0);
	const [trailerModal, setTrailerModal] = useState<{
		title: string;
		youTubeKey: string;
	} | null>(null);
	const cardRef = useRef<HTMLDivElement | null>(null);

	const logout = () => {
		clearAuthToken();
		clearCurrentRecommendation();
		navigate("/", { replace: true });
	};

	const addToList = useCallback(
		async (
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
			} catch (err: unknown) {
				const statusCode = getHttpStatus(err);

				if (statusCode === 401 || statusCode === 403) {
					clearAuthToken();
					clearCurrentRecommendation();
					navigate("/", { replace: true });
					return false;
				}

				setError("Failed to add to list");
				return false;
			}
		},
		[navigate],
	);

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
				const status = getHttpStatus(err);
				if (status === 401 || status === 403) {
					clearAuthToken();
					clearCurrentRecommendation();
					navigate("/", { replace: true });
					return;
				}
				if (status === 428) {
					clearCurrentRecommendation();
					navigate("/profile/preferences", { replace: true });
					return;
				}
				if (status === 404) {
					setError(
						"No matching movies/series found for your profile. Try expanding your preferences.",
					);
					setCurrent(null);
					clearCurrentRecommendation();
					setWaitingForData(false);
					return;
				}
				setError("Server is unavailable");
				setWaitingForData(false);
				return;
			}
		}

		setError("Failed to find a new recommendation");
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

	useEffect(() => {
		let cancelled = false;

		const run = async () => {
			if (!current) {
				setCurrentDetails(null);
				setDetailsError(null);
				return;
			}

			setDetailsLoading(true);
			setDetailsError(null);
			try {
				const res = await apiClient.get<TitleDetails>(
					`/api/v1/movie/details/${current.mediaType}/${current.tmdbId}`,
				);
				if (!cancelled) setCurrentDetails(res.data);
			} catch (err: unknown) {
				const status = getHttpStatus(err);
				if (status === 401 || status === 403) {
					clearAuthToken();
					clearCurrentRecommendation();
					navigate("/", { replace: true });
					return;
				}
				if (!cancelled) {
					setCurrentDetails(null);
					setDetailsError("Failed to load details");
				}
			} finally {
				if (!cancelled) setDetailsLoading(false);
			}
		};

		run();
		return () => {
			cancelled = true;
		};
	}, [current, detailsVersion, navigate]);

	const dislike = useCallback(async () => {
		if (!current) return;

		const saved = await addToList(
			"disliked",
			current.mediaType,
			current.tmdbId,
		);
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
	}, [addToList, current, loadNext]);

	const seen = useCallback(async () => {
		if (!current) return;

		const saved = await addToList("seen", current.mediaType, current.tmdbId);
		if (!saved) {
			return;
		}

		clearCurrentRecommendation();
		await loadNext();
	}, [addToList, current, loadNext]);

	const watchLater = useCallback(async () => {
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
	}, [addToList, current, loadNext]);

	const watchNow = useCallback(async () => {
		if (!current) return;
		window.open(tmdbTitleUrl(current.mediaType, current.tmdbId), "_blank");
		clearCurrentRecommendation();
		await loadNext();
	}, [current, loadNext]);

	useEffect(() => {
		const onKeyDown = (e: KeyboardEvent) => {
			if (e.defaultPrevented) return;
			if (e.metaKey || e.ctrlKey || e.altKey) return;
			if (e.repeat) return;
			if (waitingForData) return;
			if (!current) return;
			if (trailerModal) return;

			const active = document.activeElement;
			const cardEl = cardRef.current;
			const focusIsBody =
				active === document.body || active === document.documentElement;
			const focusIsWithinCard =
				!!cardEl && !!active && (active === cardEl || cardEl.contains(active));
			if (!focusIsBody && !focusIsWithinCard) return;

			const isInteractive = (el: Element | null) => {
				if (!el) return false;
				if ((el as HTMLElement).isContentEditable) return true;
				return !!el.closest(
					"input, textarea, select, button, a, [role='button'], [role='link'], [contenteditable='true']",
				);
			};

			const targetEl = e.target instanceof Element ? e.target : null;
			if (isInteractive(targetEl) || isInteractive(active)) return;

			// Only override native Arrow-key behavior when the card itself is focused.
			// When focus is on <body>/<html>, we still allow the shortcut but avoid preventDefault
			// to not break scrolling or assistive-tech navigation.
			const shouldOverride = !!cardEl && active === cardEl;

			switch (e.key) {
				case "ArrowUp": {
					if (shouldOverride) e.preventDefault();
					void seen();
					break;
				}
				case "ArrowDown": {
					if (shouldOverride) e.preventDefault();
					void watchLater();
					break;
				}
				case "ArrowLeft": {
					if (shouldOverride) e.preventDefault();
					void watchNow();
					break;
				}
				case "ArrowRight": {
					if (shouldOverride) e.preventDefault();
					void dislike();
					break;
				}
				default:
			}
		};

		window.addEventListener("keydown", onKeyDown);
		return () => {
			window.removeEventListener("keydown", onKeyDown);
		};
	}, [
		current,
		dislike,
		seen,
		trailerModal,
		waitingForData,
		watchLater,
		watchNow,
	]);

	const playTrailer = () => {
		if (!current) return;
		const key = extractYouTubeKey(currentDetails?.trailerUrl);
		if (key) {
			setTrailerModal({ title: current.title, youTubeKey: key });
			return;
		}

		// fallback: open TMDB trailer page if available, otherwise YouTube search
		if (currentDetails?.trailerUrl) {
			window.open(currentDetails.trailerUrl, "_blank");
			return;
		}
		window.open(youtubeTrailerSearchUrl(current.title), "_blank");
	};

	const trailerButtonLabel = (() => {
		if (!current) return "Trailer";
		if (detailsLoading) return "Loading trailer…";

		const key = extractYouTubeKey(currentDetails?.trailerUrl);
		if (key) return "Play trailer";
		if (currentDetails?.trailerUrl) return "Open trailer";

		// No known trailer URL; fall back to YouTube search.
		return "Search trailer";
	})();

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
					Loading
					<span className="loading loading-infinity loading-lg"></span>
				</p>
			) : (
				<div className="flex flex-col items-center justify-center gap-4 px-4">
					{current ? (
						<div
							className="card w-full max-w-[26rem] bg-base-100 shadow-xl"
							ref={cardRef}
							tabIndex={0}
							onClick={(e) => {
								const t = e.target instanceof Element ? e.target : null;
								if (t?.closest("button, a, input, textarea, select")) return;
								cardRef.current?.focus();
							}}
						>
							<figure className="bg-base-200 p-2 flex items-center justify-center">
								{posterUrl(current.posterPath) ? (
									<img
										src={posterUrl(current.posterPath) ?? undefined}
										alt={current.title}
										className="w-full h-auto max-h-72 object-contain"
									/>
								) : (
									<div className="w-full h-56 flex items-center justify-center">
										<span className="opacity-60">No cover image</span>
									</div>
								)}
							</figure>
							<div className="card-body p-6">
								<div className="flex items-baseline justify-between gap-3">
									<div className="min-w-0 flex items-baseline gap-2">
										<h2 className="card-title text-lg break-words">
											{current.title}
										</h2>
										{typeof currentDetails?.userScore === "number" ? (
											<span className="badge badge-outline badge-sm shrink-0">
												{currentDetails.userScore.toFixed(1)}
												{typeof currentDetails.voteCount === "number" ? (
													<span className="opacity-60">
														&nbsp;({currentDetails.voteCount})
													</span>
												) : null}
											</span>
										) : null}
									</div>

									{current.releaseDate ? (
										<span className="badge badge-outline badge-sm">
											{current.releaseDate.slice(0, 4)}
										</span>
									) : null}
								</div>

								{(() => {
									const genresLine = formatGenresLine(currentDetails?.genres);
									const runtimeLine =
										typeof currentDetails?.runtimeMinutes === "number" &&
										currentDetails.runtimeMinutes > 0
											? formatRuntime(currentDetails.runtimeMinutes)
											: typeof currentDetails?.numberOfSeasons === "number" &&
													currentDetails.numberOfSeasons > 0
												? `${currentDetails.numberOfSeasons} seasons${
														typeof currentDetails.numberOfEpisodes ===
															"number" && currentDetails.numberOfEpisodes > 0
															? ` • ${currentDetails.numberOfEpisodes} episodes`
															: ""
													}`
												: null;

									const line =
										genresLine && runtimeLine
											? `${genresLine} · ${runtimeLine}`
											: (genresLine ?? runtimeLine);

									return line ? (
										<p className="text-sm opacity-80">{line}</p>
									) : null;
								})()}

								<div className="mt-1">
									{detailsLoading ? (
										<span className="badge badge-ghost badge-sm">
											Loading details…
										</span>
									) : detailsError ? (
										<div className="flex items-center gap-2">
											<span className="badge badge-warning badge-sm">
												Details unavailable
											</span>
											<button
												className="btn btn-xs"
												onClick={() => setDetailsVersion((v) => v + 1)}
											>
												Retry
											</button>
										</div>
									) : null}

									<div className="mt-2 flex flex-wrap gap-2 items-center" />
								</div>

								{current.overview ? (
									<p className="text-sm opacity-80 line-clamp-4">
										{current.overview}
									</p>
								) : null}

								<div className="card-actions justify-center gap-2 pt-2">
									<button
										className="btn btn-sm"
										onClick={playTrailer}
										disabled={detailsLoading}
									>
										{trailerButtonLabel}
									</button>
								</div>

								{currentDetails?.ottOffer ? (
									<div className="mt-2">
										<p className="text-xs opacity-70">
											Watch providers
											{currentDetails.ottOffer.region
												? ` (${currentDetails.ottOffer.region})`
												: ""}
										</p>
										<div className="mt-2 flex flex-wrap gap-2 items-center">
											{(currentDetails.ottOffer.flatrate ?? [])
												.slice(0, 8)
												.map((p, index) => (
													<span
														key={`f:${index}:${p.name ?? "unknown"}`}
														className="badge badge-ghost badge-sm gap-1"
														title={p.name ?? undefined}
													>
														{p.logoUrl ? (
															<img
																src={p.logoUrl ?? undefined}
																alt={p.name ?? "Provider"}
																className="w-4 h-4 rounded"
																loading="lazy"
															/>
														) : null}
														{p.name}
													</span>
												))}
										</div>
									</div>
								) : null}

								<div className="card-actions justify-center gap-2 pt-2">
									<button className="btn btn-success" onClick={watchNow}>
										<span className="inline-flex items-center gap-2">
											<span className="text-xs opacity-70" aria-hidden="true">
												←
											</span>
											<span>Watch now</span>
										</span>
									</button>
									<button className="btn btn-error" onClick={dislike}>
										<span className="inline-flex items-center gap-2">
											<span>Dislike</span>
											<span className="text-xs opacity-70" aria-hidden="true">
												→
											</span>
										</span>
									</button>
								</div>

								<div className="card-actions justify-center gap-2">
									<button className="btn btn-primary btn-sm" onClick={seen}>
										<span className="inline-flex items-center gap-2">
											<span className="text-xs opacity-70" aria-hidden="true">
												↑
											</span>
											<span>Watched</span>
										</span>
									</button>
									<button className="btn btn-sm" onClick={watchLater}>
										<span className="inline-flex items-center gap-2">
											<span>Watch later</span>
											<span className="text-xs opacity-70" aria-hidden="true">
												↓
											</span>
										</span>
									</button>
								</div>
							</div>
						</div>
					) : (
						<div className="text-center opacity-80">Nothing to show.</div>
					)}
				</div>
			)}

			{trailerModal ? (
				<div className="modal modal-open" role="dialog">
					<div className="modal-box w-11/12 max-w-4xl">
						<h3 className="font-bold text-lg">{trailerModal.title}</h3>
						<div className="mt-3 aspect-video w-full overflow-hidden rounded bg-black">
							<iframe
								className="w-full h-full"
								src={`https://www.youtube-nocookie.com/embed/${encodeURIComponent(
									trailerModal.youTubeKey,
								)}?autoplay=1`}
								title="Trailer"
								allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share"
								referrerPolicy="strict-origin-when-cross-origin"
								allowFullScreen
							/>
						</div>

						<div className="modal-action">
							<button className="btn" onClick={() => setTrailerModal(null)}>
								Close
							</button>
						</div>
					</div>

					<div className="modal-backdrop">
						<button onClick={() => setTrailerModal(null)}>close</button>
					</div>
				</div>
			) : null}
		</>
	);
};

export default RoulettePage;
