#!/usr/bin/env python3
"""Genera documentación base a partir del escaneo de auto-onboarding.

Produce archivos en `Metodologia_Prompt_Mentor/generated/` para revisión manual:
- `CODEx_NOTES_update.md`: resumen global (lenguajes, frameworks, ideas de próximos pasos).
- `services/<servicio>.md`: plantilla de bitácora por servicio con la metadata detectada.

No sobrescribe documentos existentes del proyecto; la intención es proveer borradores
que luego se integren (copiar/sustituir) siguiendo la Metodología Prompt Mentor.
"""

from __future__ import annotations

import json
from collections import defaultdict
from datetime import datetime, timezone
from pathlib import Path
from typing import Dict, Iterable, List, Set

import sys

SCRIPT_DIR = Path(__file__).resolve().parent
if str(SCRIPT_DIR) not in sys.path:
    sys.path.insert(0, str(SCRIPT_DIR))

from auto_onboarding_scan import run_scan  # type: ignore


GENERATED_ROOT = Path("Metodologia_Prompt_Mentor/generated")
SERVICES_DIR = GENERATED_ROOT / "services"


def ensure_dirs() -> None:
    SERVICES_DIR.mkdir(parents=True, exist_ok=True)


def render_global_report(report_dict: Dict) -> str:
    today = datetime.now(timezone.utc).strftime("%Y-%m-%d")
    languages = ", ".join(report_dict.get("languages", [])) or "(sin detectar)"
    frameworks = ", ".join(report_dict.get("frameworks", [])) or "(sin detectar)"
    dockerfiles = "\n".join(f"- `{path}`" for path in report_dict.get("docker", {}).get("dockerfiles", [])) or "- (ninguno)"
    compose = "\n".join(f"- `{path}`" for path in report_dict.get("docker", {}).get("compose", [])) or "- (ninguno)"
    reverse = "\n".join(f"- `{path}`" for path in report_dict.get("reverse_proxies", [])) or "- (ninguno)"

    return f"""# Actualización auto-onboarding — {today}

- **Lenguajes detectados**: {languages}
- **Frameworks detectados**: {frameworks}
- **Total de servicios**: {len(report_dict.get('services', []))}
- **Notas del escáner**: {', '.join(report_dict.get('notes', []) or ['(sin advertencias)'])}

## Artefactos relevantes
### Dockerfiles
{dockerfiles}

### docker-compose
{compose}

### Reverse proxies / ingress
{reverse}

## Recomendaciones inmediatas
- Validar si los puertos listados coinciden con la configuración en producción.
- Completar las bitácoras generadas para cada servicio en `docs/services/`.
- Revisar variables de entorno necesarias y plasmarlas en `.env.example`.
- Si no existen pruebas automatizadas, agregar TODO en la guía global.

## Próximos pasos sugeridos
1. Integrar las plantillas de servicio generadas en el repositorio real.
2. Ejecutar `make lint` / `make test` (crear Makefile si aún no está disponible).
3. Configurar workflow de CI mínimo (`.github/workflows/prompt_mentor_ci.yml`).

> Documento generado automáticamente. Ajusta la redacción conforme avances.
"""


def group_services(services: List[Dict]) -> Dict[str, List[Dict]]:
    grouped: Dict[str, List[Dict]] = defaultdict(list)
    for svc in services:
        grouped[svc["name"]].append(svc)
    return grouped


def render_service_doc(name: str, entries: List[Dict]) -> str:
    today = datetime.now(timezone.utc).strftime("%Y-%m-%d")
    kinds = sorted({entry["kind"] for entry in entries})
    ports = sorted({port for entry in entries for port in entry.get("ports", [])})
    sources = sorted({entry["source"] for entry in entries})
    compose_notes = [entry for entry in entries if entry["kind"] == "compose"]
    app_notes = [entry for entry in entries if entry["kind"] != "compose"]

    ports_list = "\n".join(f"- `{port}`" for port in ports) or "- (sin puertos detectados)"
    sources_list = "\n".join(f"- `{src}`" for src in sources)

    compose_hint = "\n".join(
        f"- `{item['source']}` expone {', '.join(map(str, item.get('ports', [])) or ['(sin puertos definidos)'])}"
        for item in compose_notes
    ) or "- (no se detectaron definiciones docker-compose)"

    runtime_hint = "\n".join(
        f"- `{item['source']}` utiliza `{item['kind']}`"
        for item in app_notes
    ) or "- (no se identificó código de aplicación)"

    return f"""# Servicio `{name}` (auto-onboarding {today})

## Tipo detectado
- {', '.join(kinds)}

## Archivos relevantes
{sources_list}

## Puertos observados
{ports_list}

## Despliegue manual sugerido
- Revisar Dockerfile asociado (si existe) y el compose: 
{compose_hint}
- Para ejecución directa, validar el comando del framework: 
{runtime_hint}

## Bitácora automática
1. [ ] Completar pasos reales de despliegue (llenar con comandos exactos).
2. [ ] Documentar dependencias externas (bases de datos, colas, secretos).
3. [ ] Registrar validaciones manuales o salud (`/docs`, ping, etc.).

## Errores comunes / TODOs
- [ ] Añadir escenarios conocidos una vez validados.

## Validaciones pendientes
- [ ] ¿Existen pruebas automatizadas? Documentar resultado de `make test` o equivalente.
- [ ] Añadir sección de endpoints (OpenAPI) cuando se revise la app.

> Plantilla generada automáticamente. Ajusta el contenido según lo aprendido en la bitácora real.
"""


def write_file(path: Path, content: str) -> None:
    path.write_text(content, encoding="utf-8")


def main() -> None:
    ensure_dirs()
    report = run_scan(Path("."))
    report_dict = report.to_dict()

    # Guardar resumen global
    global_doc = render_global_report(report_dict)
    write_file(GENERATED_ROOT / "CODEx_NOTES_update.md", global_doc)

    # Generar docs por servicio
    grouped = group_services(report_dict.get("services", []))
    for name, entries in grouped.items():
        content = render_service_doc(name, entries)
        write_file(SERVICES_DIR / f"{name}.md", content)

    output_index = {
        "generated_on": datetime.now(timezone.utc).isoformat(),
        "services": sorted(grouped.keys()),
        "global_doc": str((GENERATED_ROOT / "CODEx_NOTES_update.md").resolve()),
    }
    write_file(GENERATED_ROOT / "index.json", json.dumps(output_index, indent=2))

    print("Documentación generada en `Metodologia_Prompt_Mentor/generated/`.")


if __name__ == "__main__":  # pragma: no cover
    main()
