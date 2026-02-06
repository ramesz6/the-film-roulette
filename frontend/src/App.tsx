import "./App.css";
import { BrowserRouter, Navigate, Route, Routes } from "react-router";
import RequireProfile from "./components/RequireProfile";
import { isLoggedIn } from "./LocalStorage";
import LogIn from "./pages/logIn";
import MyProfile from "./pages/myProfile";
import ProfileList from "./pages/profileList";
import ProfilePreferences from "./pages/profilePreferences";
import RoulettePage from "./pages/roulette";
import SingUp from "./pages/singUp";

function RequireAuth({ children }: { children: React.ReactNode }) {
	if (!isLoggedIn()) {
		return <Navigate to="/login" replace />;
	}
	return children;
}

function Home() {
	if (!isLoggedIn()) {
		return <Navigate to="/login" replace />;
	}

	return (
		<RequireProfile>
			<RoulettePage />
		</RequireProfile>
	);
}

function App() {
	return (
		<BrowserRouter>
			<Routes>
				<Route path="/" element={<Home />} />
				<Route
					path="/roulette"
					element={
						<RequireAuth>
							<RequireProfile>
								<RoulettePage />
							</RequireProfile>
						</RequireAuth>
					}
				/>
				<Route
					path="/login"
					element={
						isLoggedIn() ? <Navigate to="/profile" replace /> : <LogIn />
					}
				/>
				<Route path="/singup" element={<SingUp />} />
				<Route path="/signup" element={<Navigate to="/singup" replace />} />
				<Route
					path="/profile"
					element={
						<RequireAuth>
							<MyProfile />
						</RequireAuth>
					}
				>
					<Route index element={<Navigate to="preferences" replace />} />
					<Route path="preferences" element={<ProfilePreferences />} />
					<Route path="list" element={<ProfileList />} />
				</Route>

				<Route
					path="/my-list"
					element={<Navigate to="/profile/list" replace />}
				/>
				<Route path="*" element={<Navigate to="/" replace />} />
			</Routes>
		</BrowserRouter>
	);
}

export default App;
