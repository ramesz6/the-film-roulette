import "./App.css";
import LogIn from "./pages/logIn";
import { BrowserRouter, Navigate, Route, Routes } from "react-router";
import { isLoggedIn } from "./LocalStorage";
import RoulettePage from "./pages/roulette";
import SingUp from "./pages/singUp";
import MyProfile from "./pages/myProfile";
import RequireProfile from "./components/RequireProfile";
import ProfilePreferences from "./pages/profilePreferences";
import ProfileList from "./pages/profileList";

function RequireAuth({ children }: { children: React.ReactNode }) {
  if (!isLoggedIn()) {
    return <Navigate to="/login" replace />;
  }
  return children;
}

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route
          path="/"
          element={
            <RequireAuth>
              <RequireProfile>
                <RoulettePage />
              </RequireProfile>
            </RequireAuth>
          }
        />
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
