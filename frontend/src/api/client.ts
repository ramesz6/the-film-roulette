import axios from "axios";
import { getAuthToken } from "../LocalStorage";

const apiBaseUrl = import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8080";

export const apiClient = axios.create({
	baseURL: apiBaseUrl,
});

apiClient.interceptors.request.use((config) => {
	const token = getAuthToken();
	if (token) {
		config.headers = config.headers ?? {};
		config.headers.Authorization = `Bearer ${token}`;
	}
	return config;
});
