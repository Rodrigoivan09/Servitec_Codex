#!/usr/bin/env bash
# Automatiza la instalación local de Amazon Corretto 21 dentro de vendor/
# para evitar depender del JDK embebido en extensiones o instalaciones manuales.
set -euo pipefail

JDK_VERSION="21.0.8.9.1"
ARCHIVE_NAME="amazon-corretto-${JDK_VERSION}-linux-x64.tar.gz"
ALT_ARCHIVE_NAME="amazon-corretto-21-x64-linux-jdk.tar.gz"
DOWNLOAD_URL="https://corretto.aws/downloads/resources/${JDK_VERSION}/${ARCHIVE_NAME}"
VENDOR_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)/vendor"
INSTALL_DIR="${VENDOR_DIR}/amazon-corretto-${JDK_VERSION}-linux-x64"
ENV_FILE="${ENV_FILE:-.env}"

info() { printf "\033[1;34m[i]\033[0m %s\n" "$*"; }
warn() { printf "\033[1;33m[!]\033[0m %s\n" "$*"; }
error() { printf "\033[1;31m[x]\033[0m %s\n" "$*"; }

ensure_vendor_dir() {
	if [[ ! -d "${VENDOR_DIR}" ]]; then
		info "Creando directorio vendor/..."
		mkdir -p "${VENDOR_DIR}"
	fi
}

download_archive() {
	local target="${1}"
	if [[ -f "${target}" ]]; then
		info "Archivo ${target} ya disponible; se reutiliza."
		return
	fi

	if [[ -f "${ALT_ARCHIVE_NAME}" ]]; then
		info "Se reutiliza archivo existente ${ALT_ARCHIVE_NAME}."
		mv "${ALT_ARCHIVE_NAME}" "${target}"
		return
	fi

	info "Descargando Amazon Corretto ${JDK_VERSION}..."
	curl -L "${DOWNLOAD_URL}" -o "${target}"
}

extract_jdk() {
	local archive_path="${1}"
	info "Extrayendo ${archive_path} en ${VENDOR_DIR}..."
	tar -xzf "${archive_path}" -C "${VENDOR_DIR}"
}

ensure_env_java_home() {
	local java_home_value="${1}"
	if [[ ! -f "${ENV_FILE}" ]]; then
		warn "Archivo ${ENV_FILE} no encontrado; se crea nuevo archivo .env."
		printf "JAVA_HOME=%s\n" "${java_home_value}" > "${ENV_FILE}"
		return
	fi

	if grep -q '^JAVA_HOME=' "${ENV_FILE}"; then
		info "JAVA_HOME ya definido en ${ENV_FILE}; no se modifica."
	else
		info "Registrando JAVA_HOME en ${ENV_FILE}."
		printf "\nJAVA_HOME=%s\n" "${java_home_value}" >> "${ENV_FILE}"
	fi
}

main() {
	if [[ -x "${INSTALL_DIR}/bin/java" ]]; then
		info "JDK ya instalado en ${INSTALL_DIR}."
		ensure_env_java_home "${INSTALL_DIR}"
		return
	fi

	ensure_vendor_dir

	local archive_path="./${ARCHIVE_NAME}"
	download_archive "${archive_path}"

	extract_jdk "${archive_path}"

	if [[ ! -x "${INSTALL_DIR}/bin/java" ]]; then
		error "No se encontró bin/java tras la extracción en ${INSTALL_DIR}."
		exit 1
	fi

	info "JDK instalado en ${INSTALL_DIR}."
	ensure_env_java_home "${INSTALL_DIR}"
}

main "$@"
