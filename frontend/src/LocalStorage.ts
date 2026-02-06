const AUTH_TOKEN_KEY = "authToken";

export const getAuthToken = (): string | null => {
	try {
		return localStorage.getItem(AUTH_TOKEN_KEY);
	} catch {
		return null;
	}
};

export const setAuthToken = (token: string): void => {
	localStorage.setItem(AUTH_TOKEN_KEY, token);
};

export const clearAuthToken = (): void => {
	localStorage.removeItem(AUTH_TOKEN_KEY);
};

export const isLoggedIn = (): boolean => {
	const token = getAuthToken();
	return typeof token === "string" && token.trim().length > 0;
};
