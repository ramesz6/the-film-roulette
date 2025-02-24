import axios, { AxiosError, AxiosResponse } from "axios"

const client = axios.create({
    baseURL: "http://localhost:8080"
})

export const postRegister = async (email: string, username: string, password: string): Promise<AxiosResponse | null> => {
    try {
        const response = await client.post("/api/v1/auth/", { "username": username, "email": email, "password": password })
        return response
    } catch (error) {
        return (error as AxiosError).response || null
    }
}