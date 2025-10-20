#!/usr/bin/env bash
# Arranca la base de datos declarada en docker-compose.yml y espera a que pase a estado healthy.
set -euo pipefail

SERVICE_NAME="${1:-db}"
DOCKER_BIN="${DOCKER_BIN:-docker}"
COMPOSE_SUBCOMMAND="${COMPOSE_SUBCOMMAND:-compose}"
WAIT_TIMEOUT="${DB_WAIT_TIMEOUT:-120}"
WAIT_INTERVAL="${DB_WAIT_INTERVAL:-3}"

info() { printf "\033[1;34m[i]\033[0m %s\n" "$*"; }
error() { printf "\033[1;31m[x]\033[0m %s\n" "$*"; }

compose_cmd() {
	"${DOCKER_BIN}" "${COMPOSE_SUBCOMMAND}" "$@"
}

get_container_id() {
	compose_cmd ps -q "${SERVICE_NAME}"
}

health_status() {
	local container_id="${1}"
	"${DOCKER_BIN}" inspect --format='{{if .State.Health}}{{.State.Health.Status}}{{else}}{{.State.Status}}{{end}}' "${container_id}"
}

main() {
	info "Levantando servicio '${SERVICE_NAME}' con docker ${COMPOSE_SUBCOMMAND}..."
	compose_cmd up -d "${SERVICE_NAME}"

	local container_id
	container_id="$(get_container_id)"
	if [[ -z "${container_id}" ]]; then
		error "No se pudo obtener el contenedor para ${SERVICE_NAME}."
		exit 1
	fi

	info "Esperando a que ${SERVICE_NAME} esté listo (timeout: ${WAIT_TIMEOUT}s)..."
	local elapsed=0
	while true; do
		local status
		status="$(health_status "${container_id}")"
		if [[ "${status}" == "healthy" || "${status}" == "running" ]]; then
			info "Servicio ${SERVICE_NAME} listo (estado: ${status})."
			break
		fi
		if (( elapsed >= WAIT_TIMEOUT )); then
			error "Tiempo de espera agotado; estado actual: ${status}."
			exit 1
		fi
		sleep "${WAIT_INTERVAL}"
		elapsed=$((elapsed + WAIT_INTERVAL))
	done
}

main "$@"
