import axios from "axios";
import { getAuthToken } from "../LocalStorage";
import { getApiBaseUrl } from "../runtimeEnv";

const apiBaseUrl = getApiBaseUrl();

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
