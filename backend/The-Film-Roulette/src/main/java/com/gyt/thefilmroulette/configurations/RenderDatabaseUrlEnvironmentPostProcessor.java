package com.gyt.thefilmroulette.configurations;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

/**
 * Render commonly provides a single DATABASE_URL (e.g. postgres://user:pass@host:5432/db)
 * instead of Spring's SPRING_DATASOURCE_* variables.
 *
 * <p>This post-processor maps DATABASE_URL to SPRING_DATASOURCE_URL/USERNAME/PASSWORD early
 * enough that placeholders in application.yaml can resolve.
 */
public class RenderDatabaseUrlEnvironmentPostProcessor
        implements EnvironmentPostProcessor, Ordered {

    private static final String DATABASE_URL = "DATABASE_URL";
    private static final String SPRING_DATASOURCE_URL = "SPRING_DATASOURCE_URL";
    private static final String SPRING_DATASOURCE_USERNAME = "SPRING_DATASOURCE_USERNAME";
    private static final String SPRING_DATASOURCE_PASSWORD = "SPRING_DATASOURCE_PASSWORD";

    @Override
    public void postProcessEnvironment(
            ConfigurableEnvironment environment,
            SpringApplication application) {
        String existingUrl = environment.getProperty(SPRING_DATASOURCE_URL);
        if (isUsable(existingUrl)) {
            return;
        }

        String rawDatabaseUrl = environment.getProperty(DATABASE_URL);
        if (!isUsable(rawDatabaseUrl)) {
            return;
        }

        DatabaseUrlParts parts;
        try {
            parts = parseDatabaseUrl(rawDatabaseUrl);
        } catch (IllegalArgumentException ex) {
            // If DATABASE_URL is present but not parseable, do not guess.
            return;
        }

        Map<String, Object> mapped = new HashMap<>();
        mapped.put(SPRING_DATASOURCE_URL, parts.jdbcUrl());
        if (isUsable(environment.getProperty(SPRING_DATASOURCE_USERNAME))
                || parts.username() == null
                || parts.username().isBlank()) {
            // keep existing username
        } else {
            mapped.put(SPRING_DATASOURCE_USERNAME, parts.username());
        }

        if (isUsable(environment.getProperty(SPRING_DATASOURCE_PASSWORD))
                || parts.password() == null) {
            // keep existing password
        } else {
            mapped.put(SPRING_DATASOURCE_PASSWORD, parts.password());
        }

        if (!mapped.isEmpty()) {
            environment
                    .getPropertySources()
                    .addFirst(new MapPropertySource("renderDatabaseUrl", mapped));
        }
    }

    @Override
    public int getOrder() {
        // Run early.
        return Ordered.HIGHEST_PRECEDENCE;
    }

    private static boolean isUsable(String value) {
        if (value == null) {
            return false;
        }

        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            return false;
        }

        // Guard against accidentally setting the literal placeholder as an env var.
        if (trimmed.contains("${") && trimmed.contains("}")) {
            return false;
        }

        return true;
    }

    private static DatabaseUrlParts parseDatabaseUrl(String raw) {
        URI uri;
        try {
            uri = new URI(raw);
        } catch (URISyntaxException ex) {
            throw new IllegalArgumentException("Invalid DATABASE_URL", ex);
        }

        String scheme = uri.getScheme();
        if (scheme == null) {
            throw new IllegalArgumentException("DATABASE_URL scheme is missing");
        }

        String normalizedScheme;
        if (scheme.equalsIgnoreCase("postgres")
                || scheme.equalsIgnoreCase("postgresql")) {
            normalizedScheme = "postgresql";
        } else {
            throw new IllegalArgumentException("Unsupported DATABASE_URL scheme: " + scheme);
        }

        String host = uri.getHost();
        int port = uri.getPort();
        String path = uri.getPath();

        if (host == null || host.isBlank()) {
            throw new IllegalArgumentException("DATABASE_URL host is missing");
        }
        if (path == null || path.isBlank() || path.equals("/")) {
            throw new IllegalArgumentException("DATABASE_URL database name is missing");
        }

        String dbName = path.startsWith("/") ? path.substring(1) : path;

        String userInfo = uri.getUserInfo();
        String username = null;
        String password = null;
        if (userInfo != null && !userInfo.isBlank()) {
            int colon = userInfo.indexOf(':');
            if (colon >= 0) {
                username = userInfo.substring(0, colon);
                password = userInfo.substring(colon + 1);
            } else {
                username = userInfo;
            }
        }

        StringBuilder jdbcUrl = new StringBuilder();
        jdbcUrl.append("jdbc:")
                .append(normalizedScheme)
                .append("://")
                .append(host);
        if (port > 0) {
            jdbcUrl.append(':').append(port);
        }
        jdbcUrl.append('/').append(dbName);

        return new DatabaseUrlParts(jdbcUrl.toString(), username, password);
    }

    private record DatabaseUrlParts(String jdbcUrl, String username, String password) {}
}
