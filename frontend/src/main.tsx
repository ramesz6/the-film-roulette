import React from "react";
import ReactDOM from "react-dom/client";
import { GoogleOAuthProvider } from "@react-oauth/google";
import App from "./App.tsx";
import "./index.css";
import { getGoogleClientId } from "./runtimeEnv";

const googleClientId = getGoogleClientId();

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
