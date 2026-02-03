import "./App.css";
import LogIn from "./pages/logIn";
import { BrowserRouter, Navigate, Route, Routes } from "react-router";
import { isLoggedIn } from "./LocalStorage";
import MainPage from "./pages/mainPage";
import SingUp from "./pages/singUp";

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
          element={isLoggedIn() ? <Navigate to="/" replace /> : <LogIn />}
        />
        <Route path="/singup" element={<SingUp />} />
        <Route path="/signup" element={<Navigate to="/singup" replace />} />
        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
