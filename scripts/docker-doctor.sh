#!/usr/bin/env bash
set -euo pipefail

say() { printf "[docker-doctor] %s\n" "$*"; }

say "Context actual: $(docker context show 2>/dev/null || echo 'desconocido')"
say "DOCKER_HOST actual: ${DOCKER_HOST-<no-definido>}"

if [[ "${DOCKER_HOST-}" =~ ^tcp://localhost:2375/?$ ]]; then
  say "Detectado DOCKER_HOST apuntando a tcp://localhost:2375; limpiando para usar el socket local..."
  unset DOCKER_HOST
fi

# Intenta usar el contexto default si existe
if docker context ls >/dev/null 2>&1; then
  docker context use default >/dev/null 2>&1 || true
fi

if ! docker info >/dev/null 2>&1; then
  say "No se puede hablar con el daemon aún. Intentando diagnosticar..."
  if command -v systemctl >/dev/null 2>&1; then
    say "En Linux puedes iniciar el daemon con: sudo systemctl start docker"
  else
    say "Si usas Docker Desktop, ábrelo y espera a 'Engine running'."
  fi
  exit 1
fi

say "Docker daemon accesible. Puedes levantar con: DOCKER_HOST= docker compose up --build -d"

