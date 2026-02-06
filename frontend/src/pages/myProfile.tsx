import { useEffect, useState } from "react";
import { Link, NavLink, Outlet, useLocation, useNavigate } from "react-router";
import { clearAuthToken } from "../LocalStorage";

export default function MyProfile() {
	const navigate = useNavigate();
	const location = useLocation();
	const [notice, setNotice] = useState<string | null>(null);

	const logout = () => {
		clearAuthToken();
		navigate("/", { replace: true });
	};

	useEffect(() => {
		const state = (location.state ?? null) as { message?: string } | null;
		if (typeof state?.message === "string" && state.message.trim().length > 0) {
			setNotice(state.message);
		}
	}, [location.state]);

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
					<button className="btn btn-sm btn-outline" onClick={logout}>
						Logout
					</button>
				</div>
			</div>

			<div className="max-w-3xl mx-auto p-4">
				<h1 className="text-2xl font-bold mb-4">My Profile</h1>

				{notice && (
					<div className="alert alert-warning mb-3">
						<span>{notice}</span>
					</div>
				)}

				<div role="tablist" className="tabs tabs-boxed mb-4">
					<NavLink
						role="tab"
						to="preferences"
						end
						className={({ isActive }) => `tab ${isActive ? "tab-active" : ""}`}
					>
						Preferences
					</NavLink>
					<NavLink
						role="tab"
						to="list"
						className={({ isActive }) => `tab ${isActive ? "tab-active" : ""}`}
					>
						Lists
					</NavLink>
				</div>

				<Outlet />
			</div>
		</>
	);
}
