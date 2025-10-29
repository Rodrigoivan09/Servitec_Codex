# Fase 3 · Automatización CI/CD (GitHub Actions)

**Propósito**: automatizar la construcción y despliegue de la imagen Servitec hacia la VM `rodev` usando GitHub Actions. Esta fase asume que completaste la infraestructura y el acceso seguro.

## Checklist rápido
- [ ] Secreto `GCP_SA_KEY` cargado
- [ ] Secretos SSH (`SERVITEC_SSH_*`) activos
- [ ] Workflow `deploy-servitec.yml` creado
- [ ] Imagen construida y publicada en Artifact Registry
- [ ] Despliegue en la VM verificado (`docker ps`, `curl`)

## 1. Secretos requeridos (GitHub → Settings → Secrets and variables → Actions)
| Clave | Contenido |
|-------|-----------|
| `GCP_SA_KEY` | JSON generado en Fase 1 |
| `SERVITEC_SSH_KEY` | Llave privada `servitec_rodev` |
| `SERVITEC_SSH_USER` | `rodrigo` |
| `SERVITEC_VM_HOST` | `35.192.59.158` |

Añade extras según variables de entorno de la app (`SERVITEC_DB_PASS`, etc.).

## 2. Workflow base (referencia)
```yaml
name: Deploy Servitec

on:
  push:
    branches: [main]
    paths:
      - 'src/**'
      - 'Dockerfile'
      - 'docker-compose.yml'
      - '.github/workflows/deploy-servitec.yml'
  workflow_dispatch:

env:
  IMAGE_NAME: us-central1-docker.pkg.dev/servitec-476518/servitec-app/servitec:latest
  CONTAINER_NAME: servitec_app
  VM_PORT_MAPPING: 127.0.0.1:8090:8090

jobs:
  build-and-push:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4
      - uses: google-github-actions/auth@v2
        with:
          credentials_json: ${{ secrets.GCP_SA_KEY }}
      - uses: google-github-actions/setup-gcloud@v2
        with:
          project_id: servitec-476518
      - run: gcloud auth configure-docker us-central1-docker.pkg.dev --quiet
      - run: docker build -t $IMAGE_NAME .
      - run: docker push $IMAGE_NAME

  deploy:
    runs-on: ubuntu-latest
    needs: build-and-push
    steps:
      - uses: google-github-actions/auth@v2
        with:
          credentials_json: ${{ secrets.GCP_SA_KEY }}
      - uses: google-github-actions/setup-gcloud@v2
        with:
          project_id: servitec-476518
      - name: Generate access token
        id: gcp_token
        run: echo "token=$(gcloud auth print-access-token)" >> "$GITHUB_OUTPUT"
      - name: Prepare SSH key
        run: |
          echo "${{ secrets.SERVITEC_SSH_KEY }}" > key.pem
          chmod 600 key.pem
          mkdir -p ~/.ssh
          ssh-keyscan -H ${{ secrets.SERVITEC_VM_HOST }} >> ~/.ssh/known_hosts
      - name: Deploy on rodev
        env:
          ACCESS_TOKEN: ${{ steps.gcp_token.outputs.token }}
        run: |
          ssh -i key.pem ${{ secrets.SERVITEC_SSH_USER }}@${{ secrets.SERVITEC_VM_HOST }} \
            ACCESS_TOKEN="$ACCESS_TOKEN" \
            IMAGE_NAME="$IMAGE_NAME" \
            CONTAINER_NAME="$CONTAINER_NAME" \
            VM_PORT_MAPPING="$VM_PORT_MAPPING" \
            'bash -s' <<'EOF'
          set -euo pipefail
          echo "$ACCESS_TOKEN" | sudo docker login --username oauth2accesstoken --password-stdin https://us-central1-docker.pkg.dev
          sudo docker stop "$CONTAINER_NAME" || true
          sudo docker rm "$CONTAINER_NAME" || true
          sudo docker system prune -af || true
          sudo docker pull "$IMAGE_NAME"
          sudo docker run -d --name "$CONTAINER_NAME" --restart unless-stopped \
            -p "$VM_PORT_MAPPING" \
            -e SPRING_PROFILES_ACTIVE=docker \
            "$IMAGE_NAME"
          EOF
```
> Ajusta variables y `env` según la versión final del workflow que uses.

## 3. Validaciones posteriores al deploy
En la VM:
```bash
docker ps --filter name=servitec_app
curl -I http://127.0.0.1:8090/login
tail -n 50 /var/log/syslog | grep Servitec
```

## 4. Documentación y bitácoras
- Anota los resultados en `docs/services/servitec.md` (fecha, run de GitHub, comprobaciones).
- Si introduces cambios en el workflow, enlaza las líneas relevantes (`.github/workflows/deploy-servitec.yml:<línea>`) en la bitácora.
- Resume patrones nuevos en `Metodologia_Prompt_Mentor/CODEx_NOTES_backend.md`.

## 5. Troubleshooting rápido
- **`docker login` falla**: revisa que la cuenta de servicio tenga rol `artifactregistry.writer` y que el token se genera con `gcloud auth print-access-token`.
- **`Permission denied (publickey)` durante SSH**: secreta `SERVITEC_SSH_KEY` con formato incorrecto o usuario distinto; vuelve a registrar la clave pública.
- **`no space left on device`**: mantén `docker system prune` y ajusta el tamaño del disco si la app crece.
- **Rollback**: vuelve a ejecutar el deploy apuntando a una etiqueta previa (`IMAGE_NAME=...:tag-anterior`) o usa `docker run` con la versión anterior guardada en la VM.
