#!/usr/bin/env python3
"""Escáner inicial para el flujo de auto-onboarding de la Metodología Prompt Mentor.

Produce un resumen en JSON con lenguajes detectados, frameworks probables,
servicios (FastAPI/Express, etc.), puertos y archivos relevantes (Docker, Nginx).

Uso:
    python Metodologia_Prompt_Mentor/scripts/auto_onboarding_scan.py [ruta_del_repo]

El objetivo es ofrecer un punto de partida. No modifica archivos y puede
extenderse con más detectores a futuro.
"""

from __future__ import annotations

import argparse
import json
import re
from collections import defaultdict
from dataclasses import dataclass, asdict
from pathlib import Path
from typing import Dict, Iterable, List, Optional, Set


# --- Modelos ---------------------------------------------------------------


EXCLUDE_DIR_NAMES = {
    ".git",
    "node_modules",
    "__pycache__",
    "venv",
    ".venv",
    "dist",
    "build",
    ".tox",
    "PaddleOCR",
    "Metodologia_Prompt_Mentor/.cache",
}


@dataclass
class ServiceInfo:
    name: str
    kind: str
    source: str
    ports: List[int]
    notes: List[str]


@dataclass
class ScanReport:
    root: str
    languages: Set[str]
    frameworks: Set[str]
    services: List[ServiceInfo]
    docker: Dict[str, List[str]]
    reverse_proxies: List[str]
    notes: List[str]

    def to_dict(self) -> Dict:
        return {
            "root": self.root,
            "languages": sorted(self.languages),
            "frameworks": sorted(self.frameworks),
            "services": [asdict(s) for s in self.services],
            "docker": self.docker,
            "reverse_proxies": self.reverse_proxies,
            "notes": self.notes,
        }


# --- Utilidades -----------------------------------------------------------


def read_text(path: Path) -> str:
    try:
        return path.read_text(encoding="utf-8", errors="ignore")
    except Exception:
        return ""


def glob_exists(root: Path, patterns: Iterable[str]) -> bool:
    for pattern in patterns:
        for path in root.glob(pattern):
            if not is_excluded(path):
                return True
    return False


def is_excluded(path: Path) -> bool:
    return any(part in EXCLUDE_DIR_NAMES for part in path.parts)


def detect_languages(root: Path) -> Set[str]:
    langs: Set[str] = set()
    if glob_exists(root, ["**/requirements.txt", "**/pyproject.toml", "**/Pipfile"]):
        langs.add("python")
    if glob_exists(root, ["**/package.json"]):
        langs.add("javascript")
    if glob_exists(root, ["**/*.ts", "**/*.tsx"]):
        langs.add("typescript")
    if glob_exists(root, ["**/go.mod"]):
        langs.add("go")
    if glob_exists(root, ["**/pom.xml", "**/build.gradle", "**/build.gradle.kts"]):
        langs.add("java")
    if glob_exists(root, ["**/*.kt"]):
        langs.add("kotlin")
    if glob_exists(root, ["**/pubspec.yaml"]):
        langs.add("dart")
    if glob_exists(root, ["**/*.tf", "**/terraform.tfstate"]):
        langs.add("iac")
    return langs


def detect_python_frameworks(root: Path, frameworks: Set[str], services: List[ServiceInfo]):
    fastapi_regex = re.compile(r"FastAPI\s*\(")
    flask_regex = re.compile(r"Flask\s*\(")
    django_regex = re.compile(r"django\.setup\(|from django|")
    uvicorn_regex = re.compile(r"uvicorn\.run\((.+)\)")

    for path in root.rglob("*.py"):
        if is_excluded(path.relative_to(root)):
            continue
        text = read_text(path)
        if "FastAPI(" in text:
            frameworks.add("fastapi")
            ports = extract_ports_from_text(text)
            services.append(ServiceInfo(
                name=derive_service_name(path),
                kind="fastapi",
                source=str(path.relative_to(root)),
                ports=ports,
                notes=[]
            ))
            continue
        if "Flask(" in text or "from flask" in text:
            frameworks.add("flask")
            ports = extract_ports_from_text(text)
            services.append(ServiceInfo(
                name=derive_service_name(path),
                kind="flask",
                source=str(path.relative_to(root)),
                ports=ports,
                notes=[]
            ))
            continue
        if "django" in text.lower():
            frameworks.add("django")


def extract_ports_from_text(text: str) -> List[int]:
    ports: Set[int] = set()
    # uvicorn.run(..., port=8040)
    for match in re.finditer(r"port\s*=\s*(\d{2,5})", text):
        ports.add(int(match.group(1)))
    # CLI commands (uvicorn main:app --port 8040)
    for match in re.finditer(r"--port\s+(\d{2,5})", text):
        ports.add(int(match.group(1)))
    # app.run(port=5000)
    return sorted(ports)


def derive_service_name(path: Path) -> str:
    # Example: validator_employee/main.py -> validator_employee
    parts = list(path.parts)
    if len(parts) >= 2:
        return parts[-2]
    return path.stem


def detect_node_services(root: Path, frameworks: Set[str], services: List[ServiceInfo]):
    for path in root.rglob("*.js"):
        if is_excluded(path.relative_to(root)):
            continue
        text = read_text(path)
        if "express()" in text:
            frameworks.add("express")
            ports = set()
            for match in re.finditer(r"listen\s*\(\s*(\d{2,5})", text):
                ports.add(int(match.group(1)))
            services.append(ServiceInfo(
                name=derive_service_name(path),
                kind="express",
                source=str(path.relative_to(root)),
                ports=sorted(ports),
                notes=[]
            ))


def detect_docker_assets(root: Path) -> Dict[str, List[str]]:
    dockerfiles = [str(p.relative_to(root)) for p in root.glob("**/Dockerfile*") if not is_excluded(p.relative_to(root))]
    compose_files = [str(p.relative_to(root)) for p in root.glob("**/docker-compose.y*ml") if not is_excluded(p.relative_to(root))]
    k8s_files = [str(p.relative_to(root)) for p in root.glob("**/*.k8s.yml") if not is_excluded(p.relative_to(root))]
    return {
        "dockerfiles": dockerfiles,
        "compose": compose_files,
        "kubernetes": k8s_files,
    }


def detect_reverse_proxies(root: Path) -> List[str]:
    patterns = ["**/nginx.conf", "**/nginx/*.conf", "**/traefik*.yml", "**/traefik/*.yml"]
    return [str(p.relative_to(root)) for pat in patterns for p in root.glob(pat) if not is_excluded(p.relative_to(root))]


def detect_services_from_compose(root: Path, services: List[ServiceInfo]):
    import yaml  # type: ignore

    for compose in root.glob("**/docker-compose.y*ml"):
        if is_excluded(compose.relative_to(root)):
            continue
        text = read_text(compose)
        try:
            data = yaml.safe_load(text)
        except Exception:
            continue
        if not isinstance(data, dict):
            continue
        svcs = data.get("services", {})
        if not isinstance(svcs, dict):
            continue
        for name, cfg in svcs.items():
            ports: List[int] = []
            if isinstance(cfg, dict):
                for entry in cfg.get("ports", []) or []:
                    if isinstance(entry, str):
                        left = entry.split(":", 1)[0]
                        if left.isdigit():
                            ports.append(int(left))
                    elif isinstance(entry, int):
                        ports.append(int(entry))
            services.append(ServiceInfo(
                name=name,
                kind="compose",
                source=str(compose.relative_to(root)),
                ports=sorted(ports),
                notes=["definido en docker-compose"]
            ))


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(description="Escáner para auto-onboarding Prompt Mentor")
    parser.add_argument("path", nargs="?", default=".", help="Ruta del repositorio a analizar")
    return parser.parse_args()


def run_scan(root: Path) -> ScanReport:
    root = root.resolve()
    services: List[ServiceInfo] = []
    languages = detect_languages(root)
    frameworks: Set[str] = set()
    notes: List[str] = []

    if "python" in languages:
        detect_python_frameworks(root, frameworks, services)
    if "javascript" in languages or "typescript" in languages:
        detect_node_services(root, frameworks, services)

    try:
        detect_services_from_compose(root, services)
    except ImportError:
        notes.append("PyYAML no disponible; se omitió el parseo detallado de docker-compose")

    docker_assets = detect_docker_assets(root)
    reverse_proxies = detect_reverse_proxies(root)

    return ScanReport(
        root=str(root),
        languages=languages,
        frameworks=frameworks,
        services=merge_duplicate_services(services),
        docker=docker_assets,
        reverse_proxies=reverse_proxies,
        notes=notes,
    )


def main() -> None:
    args = parse_args()
    report = run_scan(Path(args.path))
    print(json.dumps(report.to_dict(), indent=2, ensure_ascii=False))


def merge_duplicate_services(services: List[ServiceInfo]) -> List[ServiceInfo]:
    merged: Dict[tuple, ServiceInfo] = {}
    for svc in services:
        key = (svc.name, svc.kind, svc.source)
        if key not in merged:
            merged[key] = svc
        else:
            existing = merged[key]
            existing.ports = sorted(set(existing.ports) | set(svc.ports))
            existing.notes = sorted(set(existing.notes + svc.notes))
    return list(merged.values())


if __name__ == "__main__":  # pragma: no cover
    main()
