#!/usr/bin/env bash
set -euo pipefail

# Ensure Docker CLI talks to local daemon by clearing DOCKER_HOST
unset DOCKER_HOST || true

# Optionally ensure default context
if docker context ls >/dev/null 2>&1; then
  docker context use default >/dev/null 2>&1 || true
fi

docker compose up --build "$@"

