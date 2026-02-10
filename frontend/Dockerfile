# Production build for Railway (or any Docker host)
# Builds the Vite app and serves it via nginx.

FROM node:20-alpine AS builder
WORKDIR /app

COPY package.json package-lock.json* ./
RUN npm ci || npm install

COPY . .

RUN npm run build

FROM nginx:1.27-alpine

# Use a template so we can bind to $PORT at runtime.
COPY ./nginx.conf /etc/nginx/conf.d/default.conf.template

# Generate runtime env file and finalize nginx config before start.
COPY ./docker-entrypoint.sh /docker-entrypoint.sh
RUN chmod +x /docker-entrypoint.sh

COPY --from=builder /app/dist /usr/share/nginx/html

EXPOSE 8080
ENTRYPOINT ["/docker-entrypoint.sh"]
