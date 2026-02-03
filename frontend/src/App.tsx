import "./App.css";
import LogIn from "./pages/logIn";
import { BrowserRouter, Navigate, Route, Routes } from "react-router";
import { isLoggedIn } from "./LocalStorage";
import MainPage from "./pages/mainPage";
import SingUp from "./pages/singUp";
import MyProfile from "./pages/myProfile";
import MyList from "./pages/myList";

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
              <MainPage />
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
        />
        <Route
          path="/my-list"
          element={
            <RequireAuth>
              <MyList />
            </RequireAuth>
          }
        />
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
