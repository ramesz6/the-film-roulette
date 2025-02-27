# Load environment variables from .env
include .env
export $(shell sed 's/=.*//' .env)

# Variables
BASE_DIR = backend/The-Film-Roulette
MVNW = $(BASE_DIR)/mvnw
JAR_FILE = $(BASE_DIR)/target/The-Film-Roulette-0.0.1-SNAPSHOT.jar
PORT = 8080

# Default target
.PHONY: all
all: clean build -DskipTests

# Build the project
.PHONY: build
build:
	$(MVNW) clean package -f $(BASE_DIR)/pom.xml -DskipTests

# Run the application
.PHONY: run
run: build
	docker compose up db -d
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
