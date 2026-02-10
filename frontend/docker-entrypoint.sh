#!/bin/sh
set -eu

PORT_VALUE="${PORT:-8080}"

# Write runtime env vars for the SPA (loaded by index.html before the app).
cat > /usr/share/nginx/html/__env.js <<EOF
window.__ENV__ = {
  VITE_API_BASE_URL: "${VITE_API_BASE_URL:-}",
  VITE_GOOGLE_CLIENT_ID: "${VITE_GOOGLE_CLIENT_ID:-}"
};
EOF

# Render nginx config with the correct port.
sed "s/__PORT__/${PORT_VALUE}/g" /etc/nginx/conf.d/default.conf.template > /etc/nginx/conf.d/default.conf

exec nginx -g 'daemon off;'
