import "./App.css";
import LogIn from "./pages/logIn";
import { BrowserRouter, Route, Routes } from "react-router";
import SingUp from "./pages/singUp";

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/login" element={<LogIn />} />
        <Route path="/singup" element={<SingUp />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
