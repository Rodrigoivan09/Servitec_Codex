# Servicio `security` (auto-onboarding 2025-09-25)

## Tipo detectado
- fastapi

## Archivos relevantes
- `scrapper_data/.env10/lib/python3.10/site-packages/fastapi/security/api_key.py`
- `scrapper_data/.env10/lib/python3.10/site-packages/fastapi/security/http.py`
- `scrapper_data/.env10/lib/python3.10/site-packages/fastapi/security/oauth2.py`
- `server_data/infrastructure/service/fastapi/security/upload_documentation.py`

## Puertos observados
- (sin puertos detectados)

## Despliegue manual sugerido
- Revisar Dockerfile asociado (si existe) y el compose: 
- (no se detectaron definiciones docker-compose)
- Para ejecución directa, validar el comando del framework: 
- `scrapper_data/.env10/lib/python3.10/site-packages/fastapi/security/oauth2.py` utiliza `fastapi`
- `scrapper_data/.env10/lib/python3.10/site-packages/fastapi/security/http.py` utiliza `fastapi`
- `scrapper_data/.env10/lib/python3.10/site-packages/fastapi/security/api_key.py` utiliza `fastapi`
- `server_data/infrastructure/service/fastapi/security/upload_documentation.py` utiliza `fastapi`

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
