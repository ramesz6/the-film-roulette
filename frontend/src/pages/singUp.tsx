import axios, { type AxiosError } from "axios";
import { useState } from "react";
import { Link, useNavigate } from "react-router";

interface UserModel {
	email: string;
	password: string;
	confirmPassword: string;
}

const apiBaseUrl = import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8080";

const EMAIL_REGEX = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
const MIN_PASSWORD_LENGTH = 8;

type FieldErrors = Partial<Record<keyof UserModel, string>>;

const validate = (data: UserModel): FieldErrors => {
	const errors: FieldErrors = {};

	const email = data.email.trim();
	const password = data.password;
	const confirmPassword = data.confirmPassword;

	if (!EMAIL_REGEX.test(email)) {
		errors.email = "Invalid email format";
	}

	const missing: string[] = [];
	if (password.length < MIN_PASSWORD_LENGTH)
		missing.push(`at least ${MIN_PASSWORD_LENGTH} characters`);
	if (!/[a-z]/.test(password)) missing.push("lowercase letter");
	if (!/[A-Z]/.test(password)) missing.push("uppercase letter");
	if (!/[0-9]/.test(password)) missing.push("number");

	if (missing.length > 0) {
		errors.password = `Password requirements: ${missing.join(", ")}`;
	}

	if (confirmPassword.length === 0) {
		errors.confirmPassword = "Please re-enter your password";
	} else if (password !== confirmPassword) {
		errors.confirmPassword = "Passwords do not match";
	}

	return errors;
};

const errorMessageFromAxios = (err: unknown): string => {
	if (!axios.isAxiosError(err)) return "Sign up failed";

	const axiosErr = err as AxiosError;
	const status = axiosErr.response?.status;
	const data = axiosErr.response?.data;

	if (status === 400) {
		if (typeof data === "string") {
			if (data.toLowerCase().includes("already exists")) {
				return "Email is already in use";
			}
			if (data.toLowerCase().includes("invalid")) {
				return "Invalid data";
			}
			return data;
		}
		return "Invalid data";
	}

	if (status === 0 || status === undefined) return "Server is unavailable";
	if (status === 401 || status === 403) return "Not authorized";
	if (status >= 500) return "Server error";

	return "Sign up failed";
};

const SingUp = () => {
	const navigate = useNavigate();
	const [data, setData] = useState<UserModel>({
		email: "",
		password: "",
		confirmPassword: "",
	});
	const [error, setError] = useState<string | null>(null);
	const [touched, setTouched] = useState<Record<keyof UserModel, boolean>>({
		email: false,
		password: false,
		confirmPassword: false,
	});
	const [isSubmitting, setIsSubmitting] = useState(false);

	const fieldErrors = validate(data);
	const hasErrors = Object.keys(fieldErrors).length > 0;

	const onSubmit = async (event: React.FormEvent) => {
		event.preventDefault();
		setError(null);

		setTouched({ email: true, password: true, confirmPassword: true });
		const currentErrors = validate(data);
		if (Object.keys(currentErrors).length > 0) {
			return;
		}

		setIsSubmitting(true);

		try {
			await axios.post(`${apiBaseUrl}/api/v1/auth/register`, {
				email: data.email.trim(),
				password: data.password,
			});

			navigate("/login", { replace: true });
		} catch (err) {
			setError(errorMessageFromAxios(err));
		} finally {
			setIsSubmitting(false);
		}
	};

	return (
		<>
			<div className="flex justify-center items-center p-4">
				<div className="card bg-base-100 w-full max-w-sm shadow-xl">
					<form className="card-body" onSubmit={onSubmit}>
						<h2 className="card-title justify-center">Sign Up</h2>
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
								type="text"
								className="grow"
								placeholder="Email"
								id="email"
								autoComplete="email"
								value={data.email}
								onChange={(e) =>
									setData((prev) => ({ ...prev, email: e.target.value }))
								}
								onBlur={() => setTouched((p) => ({ ...p, email: true }))}
							/>
						</label>
						{touched.email && fieldErrors.email && (
							<p className="text-sm text-red-500">{fieldErrors.email}</p>
						)}
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
								id="password"
								autoComplete="new-password"
								value={data.password}
								onChange={(e) =>
									setData((prev) => ({ ...prev, password: e.target.value }))
								}
								onBlur={() => setTouched((p) => ({ ...p, password: true }))}
							/>
						</label>
						<p className="text-xs opacity-70">
							Min. {MIN_PASSWORD_LENGTH} characters, include a lowercase letter,
							an uppercase letter and a number.
						</p>
						{touched.password && fieldErrors.password && (
							<p className="text-sm text-red-500">{fieldErrors.password}</p>
						)}
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
								placeholder="Re-enter password"
								id="confirmPassword"
								autoComplete="new-password"
								value={data.confirmPassword}
								onChange={(e) =>
									setData((prev) => ({
										...prev,
										confirmPassword: e.target.value,
									}))
								}
								onBlur={() =>
									setTouched((p) => ({ ...p, confirmPassword: true }))
								}
							/>
						</label>
						{touched.confirmPassword && fieldErrors.confirmPassword && (
							<p className="text-sm text-red-500">
								{fieldErrors.confirmPassword}
							</p>
						)}
						<div className="card-actions justify-center">
							<button
								className="btn btn-primary"
								type="submit"
								disabled={isSubmitting || hasErrors}
							>
								{isSubmitting ? "Signing up..." : "Sign up"}
							</button>
						</div>
						<p className="text-center text-sm">
							Already have an account?{" "}
							<Link className="link link-primary" to="/login">
								Login
							</Link>
						</p>
					</form>
				</div>
			</div>
		</>
	);
};

export default SingUp;
