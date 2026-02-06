import { useCallback, useEffect, useMemo, useState } from "react";
import { Link, useNavigate } from "react-router";
import { apiClient } from "../api/client";
import { clearAuthToken } from "../LocalStorage";
import type { TitleDetails } from "../types/tmdb";
import {
	formatGenresLine,
	formatRuntime,
	posterUrl,
	tmdbTitleUrl,
} from "../utils/tmdb";

type ListStatus = "WATCH_LATER" | "SEEN" | "DISLIKED";

type ListEntry = {
	tmdbId: number;
	mediaType: "movie" | "tv";
	status: ListStatus;
};

// shared TMDB/formatting helpers live in ../utils/tmdb

function titleLabel(d: TitleDetails | undefined, entry: ListEntry) {
	if (!d) return `${entry.mediaType.toUpperCase()} #${entry.tmdbId}`;
	return (
		d.title || d.name || `${entry.mediaType.toUpperCase()} #${entry.tmdbId}`
	);
}

export default function MyList() {
	const navigate = useNavigate();
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
			setError("Failed to load list");
		} finally {
			setLoading(false);
		}
	}, []);

	useEffect(() => {
		load(active);
	}, [active, load]);

	const visible = useMemo(() => entries, [entries]);

	const remove = async (e: ListEntry) => {
		await apiClient.delete(`/api/v1/me/list/${e.mediaType}/${e.tmdbId}`);
		await load(active);
	};

	const moveTo = async (e: ListEntry, status: ListStatus) => {
		const url =
			status === "WATCH_LATER"
				? "/api/v1/me/list/watch-later"
				: status === "SEEN"
					? "/api/v1/me/list/seen"
					: "/api/v1/me/list/disliked";
		await apiClient.put(url, { tmdbId: e.tmdbId, mediaType: e.mediaType });
		await load(active);
	};

	const logout = () => {
		clearAuthToken();
		navigate("/", { replace: true });
	};

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
					<Link className="btn btn-sm" to="/profile">
						My Profile
					</Link>
					<button className="btn btn-sm btn-outline" onClick={logout}>
						Logout
					</button>
				</div>
			</div>

			<div className="max-w-3xl mx-auto p-4">
				<h1 className="text-2xl font-bold mb-4">My List</h1>

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
						Loading{" "}
						<span className="loading loading-infinity loading-md"></span>
					</p>
				) : visible.length === 0 ? (
					<p className="opacity-70">Empty</p>
				) : (
					<div className="grid grid-cols-1 gap-3">
						{visible.map((e) => {
							const key = `${e.mediaType}:${e.tmdbId}`;
							const d = details[key];
							const poster = posterUrl(d?.posterPath, "w185");
							const tmdbUrl = tmdbTitleUrl(e.mediaType, e.tmdbId);
							const genresLine = formatGenresLine(d?.genres);
							const runtimeLine =
								typeof d?.runtimeMinutes === "number" && d.runtimeMinutes > 0
									? formatRuntime(d.runtimeMinutes)
									: typeof d?.numberOfSeasons === "number" &&
											d.numberOfSeasons > 0
										? `${d.numberOfSeasons} seasons${
												typeof d.numberOfEpisodes === "number" &&
												d.numberOfEpisodes > 0
													? ` • ${d.numberOfEpisodes} episodes`
													: ""
											}`
										: null;
							const metaLine =
								genresLine && runtimeLine
									? `${genresLine} · ${runtimeLine}`
									: (genresLine ?? runtimeLine);
							return (
								<div key={key} className="card bg-base-100 shadow">
									<div className="card-body">
										<div className="flex justify-between gap-4 items-start">
											<div className="flex gap-4 items-start min-w-0">
												<div className="w-16 shrink-0">
													{poster ? (
														<img
															src={poster}
															alt={titleLabel(d, e)}
															className="w-16 h-24 object-cover rounded"
															loading="lazy"
														/>
													) : (
														<div className="w-16 h-24 rounded bg-base-200" />
													)}
												</div>

												<div className="min-w-0">
													<div className="flex items-baseline gap-2 min-w-0">
														<h2 className="card-title min-w-0">
															{titleLabel(d, e)}
														</h2>
														{typeof d?.userScore === "number" ? (
															<span className="badge badge-outline badge-sm shrink-0">
																{d.userScore.toFixed(1)}
															</span>
														) : null}
													</div>

													{metaLine ? (
														<p className="text-sm opacity-80">{metaLine}</p>
													) : null}
													{d?.overview && (
														<p className="text-sm opacity-80 line-clamp-3">
															{d.overview}
														</p>
													)}
													<p className="text-xs opacity-60 mt-1">
														{e.mediaType.toUpperCase()} •{" "}
														<a
															href={tmdbUrl}
															target="_blank"
															rel="noreferrer"
															className="link"
														>
															TMDB #{e.tmdbId}
														</a>
													</p>
												</div>
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
												) : (
													<>
														<button
															className="btn btn-sm w-full"
															onClick={() => moveTo(e, "WATCH_LATER")}
														>
															Like
														</button>
														<button
															className="btn btn-sm w-full"
															onClick={() => moveTo(e, "SEEN")}
														>
															Mark as watched
														</button>
													</>
												)}
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
		</>
	);
}
