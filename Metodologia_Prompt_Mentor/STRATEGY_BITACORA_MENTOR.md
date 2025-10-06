# Estrategia Integral "Bitácora Mentor"

Documento de referencia exclusivo para repositorios en la cuenta de `rodrigo`. Explica las fuentes que inspiran nuestra forma de trabajo, las estrategias combinadas, cómo aplicarlas en cualquier sistema (nuevo o existente) y el prompt base que debe ejecutarse al iniciar una colaboración con Codex.

- **Autor**: Rodrigo Iván Olvera Martínez Polo.
- **Versión actual**: 1.3 (2025-09-24).

## 1. Marco conceptual y fuentes consultadas

### 1.1 Prácticas consultadas
- **Postmortem ágil (Agile Retrospective)**: se tomó de las guías de Atlassian y Scrum.org la idea de registrar qué salió bien/mal y los siguientes pasos inmediatos tras cada iteración.
- **Runbooks de SRE (Site Reliability Engineering de Google)**: de la documentación pública de Google SRE y del libro *Site Reliability Engineering*, adoptamos el uso de playbooks narrativos que explican cómo responder ante incidentes.
- **DocOps y documentación vivencial**: se combinó el enfoque de documentación “living docs” de GitLab con artículos de DocOps (por ejemplo, *DocOps: Applying DevOps to Documentation* de IBM) para mantener documentos que evolucionan junto con el sistema.
- **Captura de decisiones (Architectural Decision Records, ADRs)**: idea tomada de Michael Nygard y el repositorio *adr-tools* para documentar decisiones con contexto + consecuencias.
- **Pair Programming pedagógico**: prácticas de XP y pair coaching (Kent Beck, Martin Fowler) para narrar los pasos como si se estuviera enseñando al compañero.

### 1.2 Cómo se adaptaron
La estrategia "Bitácora Mentor" (en adelante, **Metodología Prompt Mentor**) mezcla estos enfoques con énfasis en:
1. **Narrativa pedagógica**: escribir como si se estuviera enseñando a un nuevo desarrollador qué se hizo y por qué.
2. **Bitácora viva**: cada servicio mantiene su historial de acciones, errores y learnings dentro del `.md` propio y en una guía global.
3. **Prompt persistente**: al ingresar a un proyecto, Codex lee este archivo para recuperar contexto y saber dónde documentar nuevos hallazgos.

### 1.5 Rutina obligatoria ante errores y éxitos (septiembre 2025)
1. Al detectar cualquier **error o excepción recurrente**, revisar inmediatamente `Metodologia_Prompt_Mentor/CODEx_NOTES.md` (bitácora global de errores/soluciones) y la nota especializada del dominio (`CODEx_NOTES_<tipo>.md`). Si el problema ya está registrado, aplicar la corrección documentada; si es un caso nuevo, resolverlo y añadir la entrada correspondiente con fecha, comandos y evidencia.
2. Después de corregir el error, actualizar también el archivo del servicio impactado en `docs/services/<servicio>.md` dentro de la sección "Bitácora". Incluye: descripción, comandos ejecutados, causa raíz, solución y validaciones.
3. Para cada **éxito o mejora** (scripts, pipelines, despliegues estables), documentar de forma inmediata en: (a) el `.md` del servicio o herramienta, (b) `CODEx_NOTES.md` en la raíz del repositorio y (c) `Metodologia_Prompt_Mentor/CODEx_NOTES.md` si la lección aplica a otros proyectos. Usa el repositorio como nuestra “base de datos” de conocimiento.
4. Durante el razonamiento de soluciones, Codex debe explicar en español el enfoque seguido (resumen de pensamiento) sin omitir la narrativa pedagógica. Registrar cualquier hipótesis o alternativa considerada dentro de la bitácora del servicio cuando ayude a reproducir la resolución.

### 1.3 Marco científico (Design Science Research)
Adoptamos las siete guías de Hevner et al. para garantizar rigor científico en cada iteración:
- **G1 Artefacto**: toda intervención debe producir un artefacto útil (software, proceso, documentación).
- **G2 Relevancia del problema**: validar que el objetivo atiende una necesidad organizacional o de usuario concreta.
- **G3 Evaluación del diseño**: definir métricas, experimentos o simulaciones que evidencien desempeño.
- **G4 Contribución a la investigación**: registrar el conocimiento nuevo (patrones, métricas, lecciones).
- **G5 Rigor en la investigación**: sustentar decisiones con fundamentos teóricos y metodológicos (citando normas y literatura).
- **G6 Diseño como proceso**: iterar entre construcción y evaluación, manteniendo trazabilidad.
- **G7 Comunicación**: compartir resultados con perfiles técnicos y de negocio usando la narrativa Prompt Mentor.

**Template breve de evaluación DSR**
- *Objetivo/hipótesis*: ¿Qué se espera lograr con el artefacto?
- *Diseño/implementación*: componentes creados o modificados.
- *Métricas de evaluación*: KPIs cuantitativos/cualitativos (ligados a ISO 25010 cuando aplique).
- *Amenazas a la validez*: interna, externa, constructo y conclusión; mitigaciones propuestas.

### 1.4 Evidencia y lecciones (EBSE)
Cada iteración debe registrar evidencia siguiendo la ingeniería de software basada en evidencia (EBSE):
- **Pregunta**: qué se investigó o validó.
- **Contexto**: entorno, herramientas, restricciones.
- **Evidencia**: datos recolectados, enlaces a scripts y outputs.
- **Interpretación**: conclusiones, impacto en el proyecto.
- **Lecciones aprendidas**: recomendaciones reutilizables, vinculadas a `PATRONES_REFERENCIA.md` o a las bitácoras de servicio.

## 2. Componentes de la estrategia

### 2.1 Guía global (`CODEx_NOTES.md`)
- **Objetivo**: entregar a Codex (y a cualquier persona) un panorama del repositorio, estructura de carpetas y bitácora global.
- **Aplicación**: se actualiza cuando cambian los cimientos del proyecto o al cerrar una sesión importante.
- **Impacto**: reduce el tiempo de onboarding y evita buscar contexto repetidamente.

### 2.2 Análisis automático al detectar este archivo
Al abrir un repositorio que contenga `STRATEGY_BITACORA_MENTOR.md`, Codex debe ejecutar el siguiente flujo sin esperar instrucciones adicionales:
1. **Escanear la estructura del proyecto**: listar carpetas principales, detectar lenguajes dominantes (extensiones) y frameworks relevantes.
2. **Crear la guía global si falta**: generar `CODEx_NOTES.md` con la estructura base descrita en este documento (propósito, servicios, bitácora, éxitos, notas especializadas).
3. **Clasificar el tipo de proyecto**: backend, frontend, scrapper, IA, etc. Crear o actualizar el archivo `CODEx_NOTES_<tipo>.md` correspondiente con detalles específicos (lenguaje, comandos, errores comunes, pipeline de despliegue).
4. **Verificar documentación por servicio**: si no existe `docs/services/<servicio>.md` para un componente identificado, crearlo y registrar contexto inicial + bitácora.
5. **Registrar metodología y lenguaje preferente**: dejar explícito en las notas cuál es el idioma de trabajo y que la estrategia activa es **Metodología Prompt Mentor** (ver sección 3).
6. **Configurar patrón de diseño orientado a prompts/IA**: iniciar la sección “Patrones de diseño orientados a prompts” (en la guía global o especializada) donde se resuma cómo se automatizan tareas con IA en el proyecto.
7. **Actualizar bitácoras**: documentar en `CODEx_NOTES.md` y en este archivo que se ejecutó el análisis inicial o cualquier actualización relevante.
8. **Ejecutar bootstrap opcional**: desde esta carpeta, correr `./scripts/bootstrap_prompt_mentor.sh <ruta_destino>` para copiar rápidamente la estrategia y notas asociadas a otro repositorio. Si se ejecuta desde la raíz del repo, usar `Metodologia_Prompt_Mentor/scripts/bootstrap_prompt_mentor.sh <ruta_destino>`.
9. **Consultar y actualizar el catálogo de patrones**: consulta `PATRONES_REFERENCIA.md` antes de elegir una arquitectura o patrón, y actualízalo con cualquier patrón nuevo o combinación exitosa detectada durante el proyecto (incluye fecha y referencia en la bitácora del servicio).

El objetivo es que cualquier repositorio adopte de inmediato esta metodología sin depender de memoria humana.

### 2.3 Bitácora por servicio (`docs/services/<servicio>.md`)
- **Formato**: sección "Despliegue manual" o "Bitácora de trabajo" redactada como lección paso a paso.
- **Contenido mínimo**:
  - Comandos ejecutados (con explicación).
  - Errores encontrados.
  - Cómo se resolvieron y qué se aprendió.
- **Impacto**: facilita replicar despliegues, diagnosticar regresiones y entrenar nuevos integrantes.

### 2.4 Registro de incidentes recurrentes
- **Ubicación**: subsección "Errores frecuentes y soluciones" tanto en la guía global como en cada servicio.
- **Aplicación**: cada vez que se repite un problema (permisos, dependencias, configuraciones), se documenta la solución definitiva.
- **Impacto**: convierte la experiencia en un manual de operación y reduce fallos futuros.

### 2.5 Prompt de arranque
- **Descripción**: un bloque reutilizable que Codex debe ejecutar mentalmente cada vez que ingresa a un repositorio de la cuenta `rodrigo`.
- **Propósito**: garantizar que se configure el contexto (bitácora, guías, estilo narrativo) antes de modificar o revisar cualquier archivo.

### 2.6 Clasificación y nomenclaturas nuevas
- Cuando un flujo, patrón o solución salga de lo convencional pero funcione correctamente, asígnale un nombre propio y documenta su alcance para reconocer la innovación.
- Si es necesario separar por categoría, ámbito o campo (por ejemplo, "Mentoría Legal", "Mentoría IA"), crea archivos `CODEx_NOTES_<categoria>.md` y enlázalos desde la guía global.
- Incluye en esta estrategia un resumen del nuevo nombre o categoría y el motivo por el cual se creó.
- Solo generar nuevas clasificaciones cuando mejoren la comprensión y lectura del repositorio.

## 3. Metodología "Prompt Mentor" orientada a IA

> **Nombre oficial**: *Metodología Prompt Mentor*, designado por Rodrigo Iván Olvera Martínez Polo para resaltar la fusión entre acompañamiento pedagógico (mentor) y automatización impulsada por prompts/IA (prompt). El objetivo es que cada iteración funcione como una mentoría guiada por inteligencia artificial, dejando un rastro documental replicable.

Este proyecto adopta la **Metodología Prompt Mentor**, un patrón de diseño orientado a prompts e inteligencia artificial que guía cómo Codex y el equipo colaboran. Principios clave:

1. **Contexto narrativo primero**: antes de escribir código, documentar intención, lenguaje y dependencias en la bitácora correspondiente.
2. **Prompts reproducibles**: registrar los comandos y mensajes utilizados para resolver tareas, de modo que futuras sesiones (humanas o de IA) puedan replicarlos.
3. **Validación asistida por IA**: cada cambio relevante debe considerar cómo una IA puede verificarlo (pruebas automatizadas, linters, descripciones claras).
4. **Retroalimentación continua**: los éxitos se convierten en plantillas o patrones que se propagan a `CODEx_NOTES_<tipo>.md` y a esta estrategia.
5. **Idioma declarado**: especificar en las notas si se trabajará en español, inglés u otro idioma; mantener coherencia en los prompts.
6. **Mentoría reusable**: capturar cada detalle (comandos, decisiones, guías) en un formato reutilizable para que pueda aplicarse en cualquier ámbito o proyecto futuro.

### 3.1 Sección "Patrones de diseño orientados a prompts"
- Crear/actualizar esta sección en `CODEx_NOTES.md` y en cada nota especializada.
- Documentar flujos exitosos (por ejemplo, despliegues Docker, migraciones de base de datos) describiendo el prompt inicial, la secuencia de comandos y la validación final.
- Indicar cómo estos patrones se aplicarían en proyectos futuros (scripts reutilizables, automatizaciones previstas, checklist IA).

### 3.2 Actualización recursiva de la metodología
- Cada vez que surja un nuevo patrón efectivo (ej. automatización de pipelines, estrategia de pruebas), agregarlo aquí y enlazarlo desde las notas correspondientes.
- Si se detecta la necesidad de ajustar el lenguaje o enfoque, actualizar esta sección y anunciarlo en la bitácora global.
- Mantener esta sección como referencia maestra para replicar la metodología en cualquier sistema futuro dentro de la cuenta.

## 4. Guía de aplicación paso a paso en un proyecto nuevo

1. **Crear guía global**:
   - Archivo `CODEx_NOTES.md` con estructura mínima: propósito, servicios, estrategia, bitácora global, próximos pasos.
   - Añadir mención explícita a la Estrategia Bitácora Mentor.
   - Si no existe, copiar también `Metodologia_Prompt_Mentor/PROMPT_INICIALIZACION.md` y usarlo como punto de partida en cada sesión.
2. **Crear documentación por servicio**:
   - Ubicarla en `docs/services/<servicio>.md`.
   - Redactar una sección de bitácora inicial que cubra el setup y cualquier despliegue base.
3. **Registrar primer despliegue**:
   - Documentar build, push, despliegue y errores.
   - Verificar que se explique qué problema se resolvió y cómo se verificó el resultado.
4. **Configurar seguimiento continuo**:
   - Cada vez que haya un cambio relevante, actualizar la guía global y el `.md` del servicio.
   - Señalar si hay pasos pendientes (por ejemplo, recargar un servicio) y quién debe ejecutarlos.
5. **Difusión interna**:
   - Compartir este archivo con el equipo para que todos adopten la misma narrativa.
   - Fomentar que Pull Requests referencien la bitácora cuando introduzcan cambios complejos.

## 5. Impacto esperado en un sistema complejo

| Aspecto | Impacto directo |
|---------|-----------------|
| Onboarding | Reducción sustancial del tiempo de entrada de nuevos desarrolladores (se entrega contexto narrado). |
| Operación | Los procedimientos repetitivos (despliegues, arreglos de permisos, configuraciones) quedan documentados y son fáciles de replicar. |
| Fiabilidad | Al registrar incidentes y soluciones, se disminuye la tasa de errores recurrentes y se fortalecen las prácticas SRE. |
| Comunicación | El tono pedagógico fomenta que cualquier integrante entienda la razón de cada decisión y pueda enseñarla a otros. |
| Escalabilidad | Las bitácoras permiten planear automatizaciones futuras, pues se conoce exactamente qué pasos son manuales y por qué. |

## 6. Prompt de arranque para Codex

> **Prompt base (copiar mentalmente al iniciar sesión)** — consulta `Metodologia_Prompt_Mentor/PROMPT_INICIALIZACION.md` para la versión vigente.
>
> "Estás trabajando con la Estrategia Bitácora Mentor. Lee `CODEx_NOTES.md` para obtener contexto global y verificar acuerdos vigentes. Cada vez que ejecutes comandos relevantes o soluciones un problema, documenta el paso a paso y la justificación en el `.md` del servicio implicado y en la bitácora global. Escribe siempre con tono pedagógico, explicando qué aprendimos. Si descubres nuevos errores o decisiones, añade secciones correspondientes."

> **Recordatorio automático**: antes de responder cualquier mensaje en un repositorio de la cuenta `rodrigo`, Codex debe leer este archivo `STRATEGY_BITACORA_MENTOR.md` y seguir sus instrucciones sin que el usuario lo solicite. Tras cada cambio o éxito, volver a este archivo y actualizarlo (y a las notas asociadas) si corresponde.

### 6.1 Checklist inicial
- [ ] Abriste y leíste `CODEx_NOTES.md`.
- [ ] Identificaste el archivo `.md` del servicio con el que trabajarás.
- [ ] Confirmaste si hay pendientes de la sesión anterior.
- [ ] Preparaste la sección de bitácora para registrar los nuevos pasos.

## 7. Ejemplo de aplicación (ya implementado en este repo)

- **Guía global**: `CODEx_NOTES.md` describe la estructura del backend y resume el despliegue de `validator_employee` a GCP.
- **Documentación por servicio**: `docs/services/validator_employee.md` contiene una bitácora de septiembre 2025 con comandos, errores (Docker daemon, permisos Artifact Registry) y soluciones.
- **Registro de errores recurrentes**: problemas de `DOCKER_HOST`, falta de servicio Docker y credenciales se documentaron tanto en la guía global como en el `.md` del servicio.
- **Narrativa pedagógica**: cada paso explica qué salió mal, cómo se diagnosticó y cómo se corrigió para que cualquiera lo replique.

## 8. Recomendaciones finales

1. **Extender a todos los servicios**: gradualmente documentar `auth_user`, `server_file`, `server_data`, etc., con la misma estructura.
2. **Automatizar**: cuando los pasos sean estables, crear scripts o pipelines y documentar cómo reemplazan los comandos manuales.
3. **Revisar trimestralmente**: hacer auditoría de la guía para depurar pasos obsoletos y registrar nuevas lecciones aprendidas.
4. **Aplicación multi-repo**: cada vez que Codex abra un nuevo repositorio de la cuenta, generar una versión inicial de este archivo y adaptarlo según el contexto del proyecto.

## 9. Actualización recursiva y notas especializadas

Para cumplir con la evolución continua de la Estrategia Bitácora Mentor:

- **Registro de éxitos**: cada logro, mejora de proceso o hallazgo significativo debe registrarse aquí mismo. Añade fecha, descripción del éxito y cómo afecta a futuras iteraciones.
- **Registro de éxitos recientes (2025-09-24)**:
  - **Automatización CI/CD en GitHub Actions**: se configuraron los secretos `GCP_SA_KEY`, `LIBERSALUS_SSH_USER` y `LIBERSALUS_SSH_KEY` para permitir que el workflow `deploy-validator` construya y publique imágenes en Artifact Registry y reinicie el contenedor en la VM `liber-salus` sin intervención manual. La guía incluye pasos para regenerar llaves comprometidas y validar permisos de la cuenta de servicio.
  - **Onboarding seguro de claves**: se documentó el procedimiento para crear llaves ed25519 dedicadas, distribuir la clave pública mediante `authorized_keys`, asegurar permisos (`chmod 700 ~/.ssh`, `chmod 600 ~/.ssh/authorized_keys`) y registrar la clave privada como secreto en GitHub.
  - **Transición a usuario `Timoris`**: se migró el despliegue automatizado para usar la cuenta `Timoris`, reutilizando la llave existente (`~/.ssh/libersalus_ci`), copiándola con `gcloud compute scp`, actualizando los secretos (`LIBERSALUS_SSH_USER`, `LIBERSALUS_SSH_KEY`) y validando el acceso SSH antes de ejecutar el pipeline.
- **Generación de `CODEx_NOTES` especializado**: al evaluar el tipo de proyecto (por ejemplo, backend FastAPI, microservicios de datos, scrapers, etc.), crea o actualiza un archivo `CODEx_NOTES_<tipo>.md` que contenga instrucciones específicas para ese dominio. Este archivo complementa al `CODEx_NOTES.md` general.
- **Sincronización con `CODEx_NOTES.md`**: el `CODEx_NOTES.md` principal debe enlazar o referenciar el archivo especializado pertinente y mantenerse al día con cualquier ajuste descrito en esta estrategia.
- **Bitácora recursiva**: cuando un servicio reciba actualizaciones relevantes, documenta en su `.md` respectivo y verifica si el logro o la mejora merece añadirse a esta estrategia (por ejemplo, nuevos patrones de despliegue exitosos).
- **Memoria conversacional**: durante cualquier sesión con Codex, recuerda consultar y actualizar tanto la estrategia como las notas especializadas cada vez que se descubra una práctica valiosa. Esta dinámica es recursiva: los cambios registrados aquí alimentan a los `CODEx_NOTES`, y viceversa.

- **Creación de notas especializadas dinámicas**: se proporcionó `CODEx_NOTES_backend.md` como ejemplo y se habilitó la generación automática de `CODEx_NOTES_<tipo>.md` mediante el script de bootstrap. Úsalo para crear guías adaptadas a cada dominio.
- **Auto-onboarding soportado**: los scripts `auto_onboarding_scan.py`, `auto_onboarding_generate.py` y `auto_onboarding_apply.py` permiten inventariar servicios, generar plantillas y actualizar bitácoras con snapshots.
- **Marco científico integrado**: se alineó la metodología con DSR (Hevner), EBSE y normas ISO/IEC (12207, 15288, 25010, 22989, 23894) para garantizar trazabilidad, métricas y gestión de riesgo IA.

## 10. Historial de versiones

| Versión | Fecha | Cambios clave |
|---------|-------|---------------|
| 1.3 | 2025-09-24 | Integración de despliegue automático vía GitHub Actions: manejo seguro de llaves, secretos para Artifact Registry y playbook de validación del pipeline `deploy-validator`. |
| 1.2 | 2025-09-23 | Auto-adaptación universal: scripts de escaneo/generación/aplicación, integración de snapshots en bitácoras y creación de estándares (Makefile, AGENTS.md, CI, etc.). |
| 1.1 | 2025-09-22 | Reglas de clasificación y nomenclaturas, principio de mentoría reusable, referencia al script `Metodologia_Prompt_Mentor/scripts/bootstrap_prompt_mentor.sh` y formalización del flujo de bootstrap automatizado. |
| 1.0 | 2025-09-22 | Versión inicial de la estrategia Bitácora Mentor con enfoque Prompt Mentor, bitácoras por servicio y lineamientos recursivos. |

- Se añadió el playbook para regenerar secretos comprometidos (revocar clave en GCP, crear nueva, registrar secretos en GitHub y validar conexión SSH) y se ligó al workflow `deploy-validator` como referencia principal de despliegue.
- Se añadieron los scripts de auto-onboarding (`scan`, `generate`, `apply`) y la carpeta `generated/`.
- Se crearon los estándares operativos mínimos: `AGENTS.md`, `Makefile`, `scripts/codex_bootstrap_env.sh`, `.env.example`, `docs/services/_template.md`, `.github/workflows/prompt_mentor_ci.yml`, `SECURITY.md`, `CODEOWNERS`, `ARTIFACTS.md` y `model_card.md`.
- Las bitácoras de servicios incluyen ahora snapshots automáticos para acelerar la documentación.
- Se incorporó el marco científico (DSR/EBSE) y la trazabilidad a ISO/IEC 12207, 15288, 25010, 22989 y 23894, con KPIs y políticas de riesgo IA documentadas.

**Notas detalladas de la versión 1.1**
- Se añadió la sección 2.6 para nombrar innovaciones no convencionales y crear `CODEx_NOTES_<categoria>.md` cuando sea necesario.
- Se incorporó el principio 3.6 "Mentoría reusable" para asegurar que cada hallazgo pueda replicarse en otros proyectos.
- Se documentó el uso del script `Metodologia_Prompt_Mentor/scripts/bootstrap_prompt_mentor.sh` como flujo rápido de bootstrap en 2.2.
- Se estableció la regla de mantener `PATRONES_REFERENCIA.md` actualizado con nuevas combinaciones exitosas.
- Se creó este historial de versiones para dejar rastro cronológico.

**Notas de la versión 1.0**
- Publicación inicial de la estrategia con enfoque Prompt Mentor.
- Definición de bitácoras por servicio, sincronización recursiva y prompt de arranque obligatorio.

Con este documento, la estrategia queda institucionalizada. Cualquier colaborador (incluyendo Codex) puede entender qué fuentes la respaldan, cómo aplicarla desde cero y qué beneficios aporta al sistema.
