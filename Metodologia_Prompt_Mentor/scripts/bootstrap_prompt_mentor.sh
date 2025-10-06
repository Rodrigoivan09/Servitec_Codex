#!/usr/bin/env bash
# Bootstraps the Prompt Mentor documentación set into the target directory.
# Uso: ./scripts/bootstrap_prompt_mentor.sh [target_dir] [project_type]
#  - target_dir: ruta donde se copiarán los archivos (por defecto: directorio actual)
#  - project_type: dominio o stack detectado. Si se omite, el script intentará autodetectarlo.

set -euo pipefail

repo_root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
target_dir="${1:-$PWD}"
requested_type="${2:-}"

detect_project_type() {
  local dir="$1"

  # Infraestructura como código (Terraform, Ansible, Helm)
  if find "$dir" -maxdepth 2 -name 'terraform.tf' -o -name '*.tf' -o -name 'ansible.yml' -o -name 'helmfile.yaml' | grep -q .; then
    echo "iac"
    return
  fi

  # Mobile (React Native, Flutter, iOS/Android nativos)
  if find "$dir" -maxdepth 2 -name 'android' -o -name 'ios' -o -name '*.xcworkspace' -o -name 'pubspec.yaml' | grep -q .; then
    echo "mobile"
    return
  fi

  # Node / JS
  if [[ -f "$dir/package.json" ]]; then
    if grep -qi 'next\|nuxt\|vite\|astro\|remix' "$dir/package.json" || find "$dir" -maxdepth 2 -name 'next.config.*' -o -name 'vite.config.*' | grep -q .; then
      echo "frontend"
      return
    fi
    if grep -qi 'express\|fastify\|koa\|hapi' "$dir/package.json"; then
      echo "backend"
      return
    fi
  fi

  # Python indicators
  if find "$dir" -maxdepth 1 -name 'requirements.txt' -o -name 'pyproject.toml' -o -name 'Pipfile' | grep -q .; then
    if grep -Eiq 'selenium|playwright|beautifulsoup|scrapy' "$dir/requirements.txt" 2>/dev/null ||
       grep -Eiq 'selenium|playwright|beautifulsoup|scrapy' "$dir/pyproject.toml" 2>/dev/null; then
      echo "scrapper"
      return
    fi
    if grep -Eiq 'pandas|spark|dask|polars' "$dir/requirements.txt" 2>/dev/null ||
       grep -Eiq 'pandas|spark|dask|polars' "$dir/pyproject.toml" 2>/dev/null; then
      echo "data"
      return
    fi
    echo "backend"
    return
  fi

  # Go
  if [[ -f "$dir/go.mod" ]]; then
    echo "backend"
    return
  fi

  # Docker / Compose heavy repos default a backend (se ajustará manualmente si aplica)
  if find "$dir" -maxdepth 1 -name 'docker-compose.yml' -o -name 'docker-compose.yaml' | grep -q .; then
    echo "backend"
    return
  fi

  # Último recurso
  echo "backend"
}

if [[ -n "$requested_type" ]]; then
  note_type_lower="${requested_type,,}"
else
  note_type_lower="$(detect_project_type "$target_dir")"
  echo "[Prompt Mentor] Tipo de proyecto autodetectado: ${note_type_lower}" >&2
fi

note_type_title="${note_type_lower^}"

echo "[Prompt Mentor] Bootstrapping documentation into: ${target_dir}" >&2

mkdir -p "${target_dir}"

copy_file() {
  local source_path="$1"
  local destination_path="$2"
  local destination_dir
  destination_dir="$(dirname "${destination_path}")"
  mkdir -p "${destination_dir}"

  if [[ -e "${destination_path}" ]]; then
    echo "[Prompt Mentor] Skipping existing file: ${destination_path}" >&2
  else
    cp "${source_path}" "${destination_path}"
    echo "[Prompt Mentor] Created: ${destination_path}" >&2
  fi
}

files=(
  "STRATEGY_BITACORA_MENTOR.md"
  "CODEx_NOTES.md"
  "PATRONES_REFERENCIA.md"
  "CODEx_NOTES_template.md"
)

for file in "${files[@]}"; do
  copy_file "${repo_root}/${file}" "${target_dir}/${file}"
done

# Create specialized note if needed.
specialized_target="${target_dir}/CODEx_NOTES_${note_type_lower}.md"

if [[ -e "${specialized_target}" ]]; then
  echo "[Prompt Mentor] Specialized note already exists: ${specialized_target}" >&2
else
  if [[ "${note_type_lower}" == "backend" ]]; then
    copy_file "${repo_root}/CODEx_NOTES_backend.md" "${specialized_target}"
  else
    template="${repo_root}/CODEx_NOTES_template.md"
    if [[ -e "${template}" ]]; then
      mkdir -p "$(dirname "${specialized_target}")"
      sed "s/{{TYPE_TITLE}}/${note_type_title}/g; s/{{TYPE_KEY}}/${note_type_lower}/g" "${template}" > "${specialized_target}"
      echo "[Prompt Mentor] Created specialized note: ${specialized_target}" >&2
    else
      echo "[Prompt Mentor] Template not found: ${template}" >&2
    fi
  fi
fi

# Ensure docs/services directory exists even if empty.
mkdir -p "${target_dir}/docs/services"

cat <<'INFO' >&2
[Prompt Mentor] Ready.
- Revisa STRATEGY_BITACORA_MENTOR.md en el repositorio destino y actualiza los campos específicos.
- Revisa o ajusta la nota especializada generada (CODEx_NOTES_<tipo>.md). Para regenerarla con otro tipo, ejecuta de nuevo el script especificándolo.
- Ejecuta las bitácoras por servicio en docs/services/ conforme avances.
INFO
