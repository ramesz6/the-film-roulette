import axios from "axios";
import { useState } from "react";
import { Link, useNavigate } from "react-router";
import { setAuthToken } from "../LocalStorage";

const apiBaseUrl = import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8080";

const LogIn = () => {
	const navigate = useNavigate();
	const [email, setEmail] = useState("");
	const [password, setPassword] = useState("");
	const [error, setError] = useState<string | null>(null);
	const [isSubmitting, setIsSubmitting] = useState(false);

	const handleSubmit = async () => {
		setError(null);
		setIsSubmitting(true);
		try {
			const response = await axios.post(`${apiBaseUrl}/api/v1/auth/login`, {
				email,
				password,
			});

			const token = response?.data?.token;
			if (typeof token !== "string" || token.trim().length === 0) {
				setError("Login failed: missing token");
				return;
			}

			setAuthToken(token);
			navigate("/profile", { replace: true });
		} catch {
			setError("Invalid email or password");
		} finally {
			setIsSubmitting(false);
		}
	};

	const onFormSubmit: React.FormEventHandler<HTMLFormElement> = (e) => {
		e.preventDefault();
		if (isSubmitting) return;
		void handleSubmit();
	};

	return (
		<>
			<div className="flex justify-center items-center">
				<div className="card bg-base-100 w-96 shadow-xl">
					<form className="card-body" onSubmit={onFormSubmit}>
						<h2 className="card-title justify-center">Login</h2>
						{error && <p className="text-center text-red-500">{error}</p>}
						<label className="input input-bordered flex items-center gap-2">
							<svg
								xmlns="http://www.w3.org/2000/svg"
								viewBox="0 0 16 16"
								fill="currentColor"
								className="h-4 w-4 opacity-70"
							>
								<path d="M2.5 3A1.5 1.5 0 0 0 1 4.5v.793c.026.009.051.02.076.032L7.674 8.51c.206.1.446.1.652 0l6.598-3.185A.755.755 0 0 1 15 5.293V4.5A1.5 1.5 0 0 0 13.5 3h-11Z" />
								<path d="M15 6.954 8.978 9.86a2.25 2.25 0 0 1-1.956 0L1 6.954V11.5A1.5 1.5 0 0 0 2.5 13h11a1.5 1.5 0 0 0 1.5-1.5V6.954Z" />
							</svg>
							<input
								type="email"
								className="grow"
								placeholder="Email"
								value={email}
								onChange={(e) => setEmail(e.target.value)}
								autoComplete="email"
							/>
						</label>
						<label className="input input-bordered flex items-center gap-2">
							<svg
								xmlns="http://www.w3.org/2000/svg"
								viewBox="0 0 16 16"
								fill="currentColor"
								className="h-4 w-4 opacity-70"
							>
								<path
									fillRule="evenodd"
									d="M14 6a4 4 0 0 1-4.899 3.899l-1.955 1.955a.5.5 0 0 1-.353.146H5v1.5a.5.5 0 0 1-.5.5h-2a.5.5 0 0 1-.5-.5v-2.293a.5.5 0 0 1 .146-.353l3.955-3.955A4 4 0 1 1 14 6Zm-4-2a.75.75 0 0 0 0 1.5.5.5 0 0 1 .5.5.75.75 0 0 0 1.5 0 2 2 0 0 0-2-2Z"
									clipRule="evenodd"
								/>
							</svg>
							<input
								type="password"
								className="grow"
								placeholder="Password"
								value={password}
								onChange={(e) => setPassword(e.target.value)}
								autoComplete="current-password"
							/>
						</label>
						<div className="card-actions justify-center">
							<button
								className="btn btn-primary"
								type="submit"
								disabled={isSubmitting}
							>
								{isSubmitting ? "Logging in..." : "Log in"}
							</button>
						</div>
						<p className="text-center text-sm">
							Don't have an account?{" "}
							<Link className="link link-primary" to="/singup">
								Sign up
							</Link>
						</p>
					</form>
				</div>
			</div>
		</>
	);
};

export default LogIn;
