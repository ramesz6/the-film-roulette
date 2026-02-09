import React from "react";
import ReactDOM from "react-dom/client";
import { GoogleOAuthProvider } from "@react-oauth/google";
import App from "./App.tsx";
import "./index.css";

const googleClientId = import.meta.env.VITE_GOOGLE_CLIENT_ID as
	| string
	| undefined;

const app =
	typeof googleClientId === "string" && googleClientId.trim().length > 0 ? (
		<GoogleOAuthProvider clientId={googleClientId.trim()}>
			<App />
		</GoogleOAuthProvider>
	) : (
		<App />
	);

ReactDOM.createRoot(document.getElementById("root")!).render(
	<React.StrictMode>{app}</React.StrictMode>,
);
