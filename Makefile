# Load environment variables from .env (optional)
-include .env

# Export variables from .env if it exists
ifneq (,$(wildcard .env))
export $(shell sed 's/=.*//' .env)
endif

# Variables
BASE_DIR = backend/The-Film-Roulette
MVNW = $(BASE_DIR)/mvnw
JAR_FILE = $(BASE_DIR)/target/The-Film-Roulette-0.0.1-SNAPSHOT.jar
PORT = 8080
FRONTEND_PORT ?= 5173

# Default target
.PHONY: all
all: clean build

# Build the project
.PHONY: build
build:
	$(MVNW) clean package -f $(BASE_DIR)/pom.xml -DskipTests

# Run the application
.PHONY: run
run: build
	docker compose -f docker-compose.yaml up db -d
	java -jar $(JAR_FILE)

# Run the application in the background
.PHONY: start
start: build
	nohup java -jar $(JAR_FILE) > $(BASE_DIR)/app.log 2>&1 & echo $$! > $(BASE_DIR)/app.pid

# Stop the application
.PHONY: stop
stop:
	@echo "Stopping application..."
	@kill `cat $(BASE_DIR)/app.pid` || true
	@rm -f $(BASE_DIR)/app.pid

# Run tests
.PHONY: test
test:
	$(MVNW) test -f $(BASE_DIR)/pom.xml

# Clean project
.PHONY: clean
clean:
	$(MVNW) clean -f $(BASE_DIR)/pom.xml

# Check application logs
.PHONY: logs
logs:
	tail -f $(BASE_DIR)/app.log

# Docker Compose helpers
.PHONY: compose
compose:
	docker compose up -d --force-recreate --build frontend backend db
	@echo "Waiting for backend..."
	@for i in $$(seq 1 30); do curl -fsS http://localhost:8080/api/v1/movie/genres >/dev/null 2>&1 && break; sleep 1; done
	@echo "Waiting for frontend..."
	@for i in $$(seq 1 30); do curl -fsS http://localhost:$(FRONTEND_PORT)/ >/dev/null 2>&1 && break; sleep 1; done
	@echo "Frontend: http://localhost:$(FRONTEND_PORT)"
	@echo "Backend:  http://localhost:8080"

.PHONY: compose-down
compose-down:
	- docker compose down --remove-orphans

.PHONY: dev
dev: compose

.PHONY: prod
prod: compose
