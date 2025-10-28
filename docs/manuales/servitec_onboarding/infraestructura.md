# Fase 1 · Infraestructura base (Google Cloud)

**Propósito**: dejar listo el proyecto de GCP con repositorio Docker y recursos mínimos para alojar la aplicación. Todos los comandos se prueban en Cloud Shell (o cualquier terminal con `gcloud` autenticado).

## Checklist rápido
- [ ] Variables de entorno exportadas
- [ ] APIs habilitadas
- [ ] Artifact Registry creado
- [ ] Cuenta de servicio con roles mínimos
- [ ] Llave JSON guardada para GitHub
- [ ] VM validada con Docker instalado
- [ ] Regla de firewall para el puerto de la app

## 1. Variables base
```bash
export PROJECT_ID=servitec-476518
export REGION=us-central1
export REPO_ID=servitec-app
export SA_NAME=github-actions-servitec
```
> Ajusta `PROJECT_ID`, `REGION` y `REPO_ID` si clonas el proyecto en otra cuenta o región.

## 2. APIs requeridas
```bash
gcloud config set project $PROJECT_ID
gcloud services enable artifactregistry.googleapis.com compute.googleapis.com
```

## 3. Artifact Registry
```bash
gcloud artifacts repositories create $REPO_ID \
  --repository-format=docker \
  --location=$REGION \
  --description="Imágenes Docker Servitec" \
  --async

gcloud artifacts repositories describe $REPO_ID --location=$REGION
```
> Guardar la ruta de imagen estándar: `us-central1-docker.pkg.dev/$PROJECT_ID/$REPO_ID/servitec:latest`.

## 4. Cuenta de servicio para CI/CD
```bash
gcloud iam service-accounts create $SA_NAME \
  --description="CI/CD desde GitHub Actions para Servitec" \
  --display-name="GitHub Actions Servitec"

gcloud projects add-iam-policy-binding $PROJECT_ID \
  --member="serviceAccount:${SA_NAME}@${PROJECT_ID}.iam.gserviceaccount.com" \
  --role="roles/artifactregistry.writer"

gcloud projects add-iam-policy-binding $PROJECT_ID \
  --member="serviceAccount:${SA_NAME}@${PROJECT_ID}.iam.gserviceaccount.com" \
  --role="roles/compute.instanceAdmin.v1"

gcloud projects add-iam-policy-binding $PROJECT_ID \
  --member="serviceAccount:${SA_NAME}@${PROJECT_ID}.iam.gserviceaccount.com" \
  --role="roles/iam.serviceAccountUser"
```

### Llave JSON
```bash
gcloud iam service-accounts keys create ~/github-actions-servitec-key.json \
  --iam-account="${SA_NAME}@${PROJECT_ID}.iam.gserviceaccount.com"
```
Sube el contenido a GitHub como secreto `GCP_SA_KEY`.

## 5. VM `rodev`
1. Crear la instancia (ejemplo): `gcloud compute instances create rodev --machine-type=e2-custom-6-16384 --zone=us-central1-a --image-family=debian-12 --image-project=debian-cloud`.
2. Instalar Docker:
   ```bash
   sudo apt-get update && sudo apt-get install -y docker.io
   sudo systemctl enable docker && sudo systemctl start docker
   sudo usermod -aG docker $USER
   ```
3. Verificar:
   ```bash
   docker --version
   docker run hello-world
   ```
4. Configurar acceso Artifact Registry una vez autenticado:
   ```bash
   gcloud auth configure-docker ${REGION}-docker.pkg.dev
   gcloud auth print-access-token | sudo docker login -u oauth2accesstoken --password-stdin https://${REGION}-docker.pkg.dev
   ```

## 6. Firewall del puerto de la aplicación
```bash
gcloud compute firewall-rules create allow-servitec-8090 \
  --allow tcp:8090 \
  --target-tags servitec-app \
  --network default \
  --project $PROJECT_ID

gcloud compute instances add-tags rodev \
  --zone us-central1-a \
  --tags servitec-app
```

## 7. Validación final
- `gcloud compute instances list --project $PROJECT_ID` muestra la IP externa.
- Desde la VM: `curl http://127.0.0.1:8090/health` (una vez desplegada la app).
- Documenta en `docs/services/servitec.md` la fecha, comandos ejecutados y cualquier incidencia.
