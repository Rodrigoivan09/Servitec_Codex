#!/usr/bin/env python3
"""Integra la información generada por auto_onboarding_generate en docs reales.

Usa los archivos bajo `Metodologia_Prompt_Mentor/generated/services/*.md` para
actualizar `docs/services/<servicio>.md`, insertando o reemplazando la sección
marcada entre `<!-- AUTO-ONBOARDING:START -->` y `<!-- AUTO-ONBOARDING:END -->`.

No modifica otros fragmentos de los documentos.
"""

from __future__ import annotations

import re
from dataclasses import dataclass
from pathlib import Path
from typing import List

GENERATED_DIR = Path("Metodologia_Prompt_Mentor/generated/services")
DOCS_DIR = Path("docs/services")
MARK_START = "<!-- AUTO-ONBOARDING:START -->"
MARK_END = "<!-- AUTO-ONBOARDING:END -->"


@dataclass
class Snapshot:
    service: str
    date: str
    tipos: List[str]
    archivos: List[str]
    puertos: List[str]
    source_path: Path


def parse_snapshot(path: Path) -> Snapshot:
    text = path.read_text(encoding="utf-8")
    lines = text.splitlines()
    header = lines[0]
    match = re.search(r"auto-onboarding\s+(\d{4}-\d{2}-\d{2})", header)
    date = match.group(1) if match else "(fecha desconocida)"

    def extract_section(title: str) -> List[str]:
        pattern = f"## {title}"
        if pattern not in lines:
            return []
        start = lines.index(pattern) + 1
        items = []
        for line in lines[start:]:
            if line.startswith("## "):
                break
            if line.strip().startswith("- "):
                item = line.strip()[2:].strip()
                if item.startswith("`") and item.endswith("`"):
                    item = item[1:-1]
                items.append(item)
        return items

    tipos = extract_section("Tipo detectado")
    archivos = extract_section("Archivos relevantes")
    puertos = extract_section("Puertos observados")

    service = path.stem
    return Snapshot(service=service, date=date, tipos=tipos, archivos=archivos, puertos=puertos, source_path=path)


def render_block(snapshot: Snapshot) -> str:
    tipos = "\n".join(f"- {item}" for item in snapshot.tipos) or "- (sin datos)"
    archivos = "\n".join(f"- `{item}`" for item in snapshot.archivos) or "- (sin datos)"
    puertos = "\n".join(f"- `{item}`" for item in snapshot.puertos) or "- (sin datos)"
    return (
        f"{MARK_START}\n"
        f"## Auto-onboarding snapshot ({snapshot.date})\n"
        f"_Origen: {snapshot.source_path}\n\n"
        f"### Tipo detectado\n{tipos}\n\n"
        f"### Archivos relevantes\n{archivos}\n\n"
        f"### Puertos observados\n{puertos}\n"
        f"{MARK_END}\n"
    )


def apply_snapshot(snapshot: Snapshot) -> None:
    target = DOCS_DIR / f"{snapshot.service}.md"
    if not target.exists():
        return
    original = target.read_text(encoding="utf-8")
    block = render_block(snapshot)
    if MARK_START in original and MARK_END in original:
        new_text = re.sub(
            rf"{re.escape(MARK_START)}.*?{re.escape(MARK_END)}\n?",
            block,
            original,
            flags=re.S,
        )
    else:
        if not original.endswith("\n"):
            original += "\n"
        new_text = original + "\n" + block
    target.write_text(new_text, encoding="utf-8")


def main() -> None:
    snapshots = [parse_snapshot(path) for path in GENERATED_DIR.glob("*.md")]
    for snapshot in snapshots:
        apply_snapshot(snapshot)
    print(f"Se actualizaron {len(snapshots)} bitácoras con snapshots auto-onboarding.")


if __name__ == "__main__":  # pragma: no cover
    main()
