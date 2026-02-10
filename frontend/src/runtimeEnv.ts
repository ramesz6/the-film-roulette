type RuntimeEnv = {
	VITE_API_BASE_URL?: string;
	VITE_GOOGLE_CLIENT_ID?: string;
};

declare global {
	interface Window {
		__ENV__?: RuntimeEnv;
	}
}

export const getRuntimeEnv = (): RuntimeEnv => {
	return window.__ENV__ ?? {};
};

export const getApiBaseUrl = (): string => {
	const runtime = getRuntimeEnv().VITE_API_BASE_URL;
	return runtime ?? import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8080";
};

export const getGoogleClientId = (): string | undefined => {
	const runtime = getRuntimeEnv().VITE_GOOGLE_CLIENT_ID;
	return runtime ?? import.meta.env.VITE_GOOGLE_CLIENT_ID;
};
