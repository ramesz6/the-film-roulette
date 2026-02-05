# The Film Roulette

## Project Overview
The Film Roulette is a full-stack application built with a **React (Vite, TypeScript) frontend** and a **Spring Boot backend**. The application fetches movie-related data from an external API and provides users with a film selection experience.

## Technologies Used
### Frontend:
- React (Vite, TypeScript)
- React Router
- Axios (for API requests)
- Tailwind CSS (for styling)

### Backend:
- Java Spring Boot
- Spring Security
- Spring Data JPA
- PostgreSQL (as the database)
- JSON Web Tokens (JWT) for authentication
- OpenAPI (Swagger) for API documentation

### DevOps & Deployment:
- Docker (for containerization)
- Docker Compose (for managing multiple containers)
- GitHub Actions (for CI/CD)

---

## Installation and Setup

### Prerequisites:
- Docker & Docker Compose

Optional (only if you want to run services outside Docker):
- Node.js (frontend)
- Java 21+ (backend)
- PostgreSQL

### Environment Variables
To keep sensitive data secure, environment variables should be stored in `.env` files (and **must not** be committed).

Create a local `.env` from the sample file:
```sh
cp .env.sample .env
```

#### **Frontend `.env` Example:**
```
VITE_API_BASE_URL=http://localhost:8080
```

#### **Backend `.env` Example:**
```
POSTGRES_PASSWORD=<your-password>
JWT_SECRET=your_jwt_secret_key_here
TMDB_API_KEY=your_tmdb_api_key_here

# Optional
# CORS_URLS=http://localhost:5173
# FRONTEND_PORT=5173
```

---

## Running the Project

### Docker Compose (recommended)

Start everything (frontend + backend + db):
```sh
cp .env.sample .env
make compose
```

Stop everything:
```sh
make compose-down
```

URLs:
- Frontend: `http://localhost:5173` (override with `FRONTEND_PORT=5173`)
- Backend: `http://localhost:8080`

---

## API Documentation
The backend provides a Swagger UI for API documentation and testing.
- After running the backend, visit: `http://localhost:8080/swagger-ui/index.html`

---

## Endpoints
| Method | Endpoint             | Description                          |
| ------ | -------------------- | ------------------------------------ |
| `GET`  | `/api/v1/movie/discover` | Discover movies via TMDB         |
| `GET`  | `/api/v1/movie/genres`   | Fetch TMDB genres                |
| `GET`  | `/api/v1/movie/details/{mediaType}/{id}` | Details for a title |
| `POST` | `/api/v1/auth/login`     | Authenticate and return a JWT    |
| `POST` | `/api/v1/auth/register`  | Register a new user              |

---

## Contribution Guide
1. Fork the repository
2. Clone your forked repository:
```sh
git clone https://github.com/your-username/the-film-roulette.git
```
3. Create a new branch:
```sh
git checkout -b feature-branch-name
```
4. Make your changes and commit them:
```sh
git commit -m "Added new feature"
```
5. Push your changes:
```sh
git push origin feature-branch-name
```
6. Create a pull request to merge your changes

---

## License
This project is licensed under the MIT License. See the `LICENSE` file for details.

---

## Contact
For any questions or contributions, contact the project maintainers at:
- **GitHub:** [github.com/ramesz6](https://github.com/ramesz6)
- **Email:** ramesz6@me.com