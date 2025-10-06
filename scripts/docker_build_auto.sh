#!/usr/bin/env bash
set -euo pipefail

fail() {
  echo "❌ $*" >&2
  exit 1
}

STACK=${STACK_LABEL:-}
[[ -z "$STACK" ]] && fail "STACK_LABEL no está definido"

APP_IMAGE=${APP_IMAGE:-}
[[ -z "$APP_IMAGE" ]] && fail "APP_IMAGE no está definido"

DOCKER_CMD=${DOCKER_CMD:-docker}
APP_DOCKERFILE_ENV=${APP_DOCKERFILE:-}
ENV_FILE_PATH=${ENV_FILE:-.env}
APP_PORT_OVERRIDE=${APP_PORT_OVERRIDE:-}

temp_file=""
cleanup() {
  if [[ -n "$temp_file" && -f "$temp_file" ]]; then
    rm -f "$temp_file"
  fi
}
trap cleanup EXIT

if [[ -n "$ENV_FILE_PATH" && -f "$ENV_FILE_PATH" ]]; then
  # shellcheck disable=SC1090
  set -a
  . "$ENV_FILE_PATH"
  set +a
fi

compute_port() {
  local stack="$1"
  local candidate="${APP_PORT_OVERRIDE:-}"
  if [[ -n "$candidate" ]]; then
    echo "$candidate"
    return
  fi
  local fallback=""
  case "$stack" in
    node-npm|node-yarn|node-pnpm)
      fallback=${SERVER_PORT:-3000}
      ;;
    python-poetry|python)
      fallback=${SERVER_PORT:-8000}
      ;;
    go|rust)
      fallback=${SERVER_PORT:-8080}
      ;;
    *)
      fallback=${SERVER_PORT:-8090}
      ;;
  esac
  if [[ -z "$fallback" ]]; then
    fallback=8090
  fi
  echo "$fallback"
}

APP_PORT=$(compute_port "$STACK")

create_template() {
  local stack="$1"
  temp_file=$(mktemp)
  case "$stack" in
    java-maven)
      cat <<'DOCKER' >"$temp_file"
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /workspace
COPY pom.xml ./
COPY src ./src
RUN mvn -B -DskipTests package

FROM eclipse-temurin:17-jre
WORKDIR /app
ENV TZ=UTC
COPY --from=build /workspace/target/*.jar /app/app.jar
EXPOSE __APP_PORT__
ENTRYPOINT ["java","-jar","/app/app.jar"]
DOCKER
      ;;
    node-npm)
      cat <<'DOCKER' >"$temp_file"
FROM node:20-alpine
WORKDIR /app
COPY . .
RUN npm install --omit=dev || npm install
RUN npm run build --if-present
ENV NODE_ENV=production \
    PORT=__APP_PORT__
EXPOSE __APP_PORT__
CMD ["npm","run","start"]
DOCKER
      ;;
    node-yarn)
      cat <<'DOCKER' >"$temp_file"
FROM node:20-alpine
WORKDIR /app
COPY . .
RUN yarn install --production --frozen-lockfile || yarn install --production
RUN yarn build || true
ENV NODE_ENV=production \
    PORT=__APP_PORT__
EXPOSE __APP_PORT__
CMD ["yarn","start"]
DOCKER
      ;;
    node-pnpm)
      cat <<'DOCKER' >"$temp_file"
FROM node:20-alpine
RUN npm install -g pnpm
WORKDIR /app
COPY . .
RUN pnpm install --frozen-lockfile --prod || pnpm install --prod
RUN pnpm run build || true
ENV NODE_ENV=production \
    PORT=__APP_PORT__
EXPOSE __APP_PORT__
CMD ["pnpm","start"]
DOCKER
      ;;
    python-poetry)
      cat <<'DOCKER' >"$temp_file"
FROM python:3.11-slim
WORKDIR /app
ENV PYTHONDONTWRITEBYTECODE=1 \
    PYTHONUNBUFFERED=1 \
    POETRY_VIRTUALENVS_IN_PROJECT=1 \
    POETRY_NO_INTERACTION=1
RUN pip install --no-cache-dir poetry
COPY . .
RUN if [ -f poetry.lock ]; then poetry install --only main --no-root; else poetry install --only main --no-root; fi
EXPOSE __APP_PORT__
CMD ["poetry","run","python","main.py"]
DOCKER
      ;;
    python)
      cat <<'DOCKER' >"$temp_file"
FROM python:3.11-slim
WORKDIR /app
ENV PYTHONDONTWRITEBYTECODE=1 \
    PYTHONUNBUFFERED=1
COPY . .
RUN if [ -f requirements.txt ]; then pip install --no-cache-dir -r requirements.txt; fi
EXPOSE __APP_PORT__
CMD ["python","main.py"]
DOCKER
      ;;
    go)
      cat <<'DOCKER' >"$temp_file"
FROM golang:1.22 AS build
WORKDIR /workspace
COPY . .
RUN if [ -f go.mod ]; then go mod download; fi
RUN go build -o app ./...

FROM debian:bookworm-slim
RUN apt-get update && apt-get install -y --no-install-recommends ca-certificates && rm -rf /var/lib/apt/lists/*
WORKDIR /app
COPY --from=build /workspace/app /app/app
EXPOSE __APP_PORT__
CMD ["/app/app"]
DOCKER
      ;;
    rust)
      cat <<'DOCKER' >"$temp_file"
FROM rust:1.77 AS build
WORKDIR /workspace
COPY . .
RUN cargo build --release

FROM debian:bookworm-slim
RUN apt-get update && apt-get install -y --no-install-recommends ca-certificates && rm -rf /var/lib/apt/lists/*
WORKDIR /app
COPY --from=build /workspace/target/release /tmp/release
RUN APP_BINARY=$(find /tmp/release -maxdepth 1 -type f -executable | head -n1); \
    if [ -z "$APP_BINARY" ]; then echo "No se encontró binario compilado en target/release" && exit 1; fi; \
    mv "$APP_BINARY" /app/app && rm -rf /tmp/release
EXPOSE __APP_PORT__
CMD ["/app/app"]
DOCKER
      ;;
    *)
      rm -f "$temp_file"
      temp_file=""
      fail "No existe plantilla Docker automática para el stack $stack. Define APP_DOCKERFILE o crea un Dockerfile."
      ;;
  esac
  if [[ -n "$temp_file" ]]; then
    sed -i.bak "s/__APP_PORT__/$APP_PORT/g" "$temp_file"
    rm -f "$temp_file.bak"
  fi
}

select_dockerfile() {
  if [[ -n "$APP_DOCKERFILE_ENV" ]]; then
    [[ -f "$APP_DOCKERFILE_ENV" ]] || fail "APP_DOCKERFILE='$APP_DOCKERFILE_ENV' no existe"
    echo "$APP_DOCKERFILE_ENV"
    return
  fi
  if [[ -f Dockerfile ]]; then
    echo "Dockerfile"
    return
  fi
  if [[ -f Dockerfile."$STACK" ]]; then
    echo "Dockerfile.$STACK"
    return
  fi
  if [[ -f docker/Dockerfile ]]; then
    echo "docker/Dockerfile"
    return
  fi
  create_template "$STACK"
  echo "$temp_file"
}

DOCKERFILE_PATH=$(select_dockerfile)

echo "🔨 Construyendo imagen $APP_IMAGE"
BUILD_CMD="$DOCKER_CMD build -f \"$DOCKERFILE_PATH\" -t \"$APP_IMAGE\" ."
eval "$BUILD_CMD"
