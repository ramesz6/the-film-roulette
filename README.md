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
- Node.js (for frontend development)
- Java 17+ (for backend development)
- Docker & Docker Compose (for running services locally)
- PostgreSQL (if running without Docker)

### Environment Variables
To keep sensitive data secure, environment variables should be stored in `.env` files.

#### **Frontend `.env` Example:**
```
VITE_API_BASE_URL=http://localhost:8080/api
VITE_PUBLIC_MOVIE_API_KEY=your_public_api_key_here
```

#### **Backend `.env` Example:**
```
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/filmroulette
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres
JWT_SECRET=your_jwt_secret_key_here
EXTERNAL_MOVIE_API_KEY=your_movie_api_key_here
```

---

## Running the Project

### 1. Run the Backend
```sh
cd backend
./mvnw spring-boot:run
```
OR with Docker:
```sh
cd 
```

### 2. Run the Frontend
```sh
cd frontend
npm install
npm run dev
```

---

## API Documentation
The backend provides a Swagger UI for API documentation and testing.
- After running the backend, visit: `http://localhost:8080/swagger-ui/index.html`

---

## Endpoints
| Method | Endpoint             | Description                          |
| ------ | -------------------- | ------------------------------------ |
| `GET`  | `/api/movies`        | Fetch all available movies           |
| `GET`  | `/api/movies/{id}`   | Get details of a specific movie      |
| `POST` | `/api/auth/login`    | Authenticate a user and return a JWT |
| `POST` | `/api/auth/register` | Register a new user                  |

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