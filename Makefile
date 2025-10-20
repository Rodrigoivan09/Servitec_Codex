SHELL := /bin/bash

# -----------------------------------------------------------------------------
# Detección del stack/proyecto
# Permite forzar la detección con FORCE_STACK=<stack> make <target>
# -----------------------------------------------------------------------------
STACK ?= $(shell FORCE="$$FORCE_STACK"; \
	if [ -n "$$FORCE" ]; then \
		echo "$$FORCE"; \
	elif [ -f pom.xml ]; then \
		echo "java-maven"; \
	elif [ -f build.gradle ] || [ -f build.gradle.kts ]; then \
		echo "java-gradle"; \
	elif [ -f pnpm-lock.yaml ]; then \
		echo "node-pnpm"; \
	elif [ -f yarn.lock ]; then \
		echo "node-yarn"; \
	elif [ -f package-lock.json ]; then \
		echo "node-npm"; \
	elif [ -f package.json ]; then \
		echo "node-npm"; \
	elif [ -f poetry.lock ]; then \
		echo "python-poetry"; \
	elif [ -f pyproject.toml ] && grep -q "\[tool.poetry\]" pyproject.toml 2>/dev/null; then \
		echo "python-poetry"; \
	elif [ -f pyproject.toml ]; then \
		echo "python"; \
	elif [ -f requirements.txt ]; then \
		echo "python"; \
	elif [ -f go.mod ]; then \
		echo "go"; \
	elif [ -f Cargo.toml ]; then \
		echo "rust"; \
	elif [ -f composer.json ]; then \
		echo "php-composer"; \
	elif [ -f Gemfile ]; then \
		echo "ruby-bundler"; \
	elif ls *.sln >/dev/null 2>&1; then \
		echo "dotnet"; \
	elif [ -f mix.exs ]; then \
		echo "elixir-mix"; \
	elif [ -f CMakeLists.txt ]; then \
		echo "cpp-cmake"; \
	else \
		echo "unknown"; \
	fi)
STACK := $(strip $(STACK))
STACK_LABEL := $(if $(STACK),$(STACK),unknown)

$(info ➤ Stack detectado: $(STACK_LABEL))

STACK_NOTE := $(if $(FORCE_STACK),"(forzado por FORCE_STACK)","")

# JDK portátil para stacks Java cuando no se ha exportado JAVA_HOME
DEFAULT_JAVA_HOME := $(abspath vendor/amazon-corretto-21.0.8.9.1-linux-x64)
ifneq ($(wildcard $(DEFAULT_JAVA_HOME)/bin/java),)
  ifeq ($(origin JAVA_HOME), undefined)
    export JAVA_HOME := $(DEFAULT_JAVA_HOME)
  endif
endif
ifneq ($(strip $(JAVA_HOME)),)
  export PATH := $(JAVA_HOME)/bin:$(PATH)
endif

# Variables de entorno desde archivo (.env por defecto)
ENV_FILE ?= .env

# -----------------------------------------------------------------------------
# Wrappers de herramientas para soportar repos multi-plataforma
# -----------------------------------------------------------------------------
MVNW := $(if $(wildcard ./mvnw),./mvnw,mvn)
GRADLEW := $(if $(wildcard ./gradlew),./gradlew,gradle)
NPM_BIN ?= npm
PNPM_BIN ?= pnpm
YARN_BIN ?= yarn
POETRY_BIN ?= poetry
PIP_BIN ?= pip
GO_BIN ?= go
CARGO_BIN ?= cargo
COMPOSER_BIN ?= composer
BUNDLE_BIN ?= bundle
DOTNET_BIN ?= dotnet
MIX_BIN ?= mix
CMAKE_BIN ?= cmake

# Nivel de verbosidad (VERBOSE=1 muestra salida completa de herramientas)
VERBOSE ?= 0

ifeq ($(VERBOSE),1)
  MVNW_FLAGS :=
  GRADLEW_FLAGS :=
else
  MVNW_FLAGS := -q
  GRADLEW_FLAGS := --quiet
endif

# -----------------------------------------------------------------------------
# Comandos por stack (se pueden sobreescribir vía variables de entorno)
# -----------------------------------------------------------------------------

ifeq ($(STACK_LABEL),java-maven)
  TOOL_LABEL := Maven/Java
  JAVA_BOOTSTRAP ?= ./scripts/setup_java.sh
  SETUP_CMD ?= $(JAVA_BOOTSTRAP) && $(MVNW) $(MVNW_FLAGS) -DskipTests dependency:go-offline
  BUILD_CMD ?= $(JAVA_BOOTSTRAP) && $(MVNW) $(MVNW_FLAGS) -DskipTests compile
  LINT_CMD ?= $(JAVA_BOOTSTRAP) && $(MVNW) $(MVNW_FLAGS) -DskipTests verify
  TEST_CMD ?= $(JAVA_BOOTSTRAP) && $(MVNW) $(MVNW_FLAGS) test
  RUN_CMD ?= $(JAVA_BOOTSTRAP) && $(MVNW) spring-boot:run
  CLEAN_CMD ?= $(MVNW) $(MVNW_FLAGS) clean
endif

ifeq ($(STACK_LABEL),java-gradle)
  TOOL_LABEL := Gradle/Java
  SETUP_CMD ?= $(GRADLEW) $(GRADLEW_FLAGS) dependencies
  BUILD_CMD ?= $(GRADLEW) $(GRADLEW_FLAGS) build -x test
  LINT_CMD ?= $(GRADLEW) $(GRADLEW_FLAGS) check
  TEST_CMD ?= $(GRADLEW) $(GRADLEW_FLAGS) test
  RUN_CMD ?= $(GRADLEW) bootRun
  CLEAN_CMD ?= $(GRADLEW) $(GRADLEW_FLAGS) clean
endif

ifeq ($(STACK_LABEL),node-pnpm)
  TOOL_LABEL := PNPM/Node.js
  SETUP_CMD ?= $(PNPM_BIN) install
  BUILD_CMD ?= $(PNPM_BIN) run build --if-present
  LINT_CMD ?= $(PNPM_BIN) run lint --if-present
  TEST_CMD ?= $(PNPM_BIN) run test --if-present
  RUN_CMD ?= $(PNPM_BIN) run start --if-present
  FORMAT_CMD ?= $(PNPM_BIN) run format --if-present
endif

ifeq ($(STACK_LABEL),node-yarn)
  TOOL_LABEL := Yarn/Node.js
  SETUP_CMD ?= $(YARN_BIN) install --frozen-lockfile
  BUILD_CMD ?= if $(YARN_BIN) run --silent build >/dev/null 2>&1; then $(YARN_BIN) run build; else echo "Script build no definido"; fi
  LINT_CMD ?= if $(YARN_BIN) run --silent lint >/dev/null 2>&1; then $(YARN_BIN) run lint; else echo "Script lint no definido"; fi
  TEST_CMD ?= if $(YARN_BIN) run --silent test >/dev/null 2>&1; then $(YARN_BIN) run test; else echo "Script test no definido"; fi
  RUN_CMD ?= if $(YARN_BIN) run --silent start >/dev/null 2>&1; then $(YARN_BIN) run start; else echo "Script start no definido"; fi
  FORMAT_CMD ?= if $(YARN_BIN) run --silent format >/dev/null 2>&1; then $(YARN_BIN) run format; else echo "Script format no definido"; fi
endif

ifeq ($(STACK_LABEL),node-npm)
  TOOL_LABEL := NPM/Node.js
  SETUP_CMD ?= $(NPM_BIN) install
  BUILD_CMD ?= $(NPM_BIN) run build --if-present
  LINT_CMD ?= $(NPM_BIN) run lint --if-present
  TEST_CMD ?= $(NPM_BIN) run test --if-present
  RUN_CMD ?= $(NPM_BIN) run start --if-present
  FORMAT_CMD ?= $(NPM_BIN) run format --if-present
endif

ifeq ($(STACK_LABEL),python-poetry)
  TOOL_LABEL := Poetry/Python
  SETUP_CMD ?= $(POETRY_BIN) install --no-root
  BUILD_CMD ?= $(POETRY_BIN) build
  LINT_CMD ?= if $(POETRY_BIN) run flake8 --version >/dev/null 2>&1; then $(POETRY_BIN) run flake8; \
	elif $(POETRY_BIN) run ruff --version >/dev/null 2>&1; then $(POETRY_BIN) run ruff .; \
	else echo "Sin linter configurado (flake8/ruff)"; fi
  TEST_CMD ?= if $(POETRY_BIN) run pytest --version >/dev/null 2>&1; then $(POETRY_BIN) run pytest; else $(POETRY_BIN) run python -m unittest discover; fi
  RUN_CMD ?= $(POETRY_BIN) run python main.py
  FORMAT_CMD ?= if $(POETRY_BIN) run black --version >/dev/null 2>&1; then $(POETRY_BIN) run black .; else echo "Black no disponible"; fi
  CLEAN_CMD ?= $(POETRY_BIN) cache clear --all pypi || true
endif

ifeq ($(STACK_LABEL),python)
  TOOL_LABEL := Pip/Python
  SETUP_CMD ?= if [ -f requirements.txt ]; then $(PIP_BIN) install -r requirements.txt; fi
  BUILD_CMD ?= python -m compileall src || true
  LINT_CMD ?= if command -v flake8 >/dev/null 2>&1; then flake8; \
	elif command -v ruff >/dev/null 2>&1; then ruff .; \
	else echo "Instala flake8 o ruff para lint"; fi
  TEST_CMD ?= if command -v pytest >/dev/null 2>&1; then pytest; else python -m unittest discover; fi
  RUN_CMD ?= python main.py
  FORMAT_CMD ?= if command -v black >/dev/null 2>&1; then black .; else echo "Black no disponible"; fi
endif

ifeq ($(STACK_LABEL),go)
  TOOL_LABEL := Go
  SETUP_CMD ?= $(GO_BIN) mod tidy
  BUILD_CMD ?= $(GO_BIN) build ./...
  LINT_CMD ?= if command -v golangci-lint >/dev/null 2>&1; then golangci-lint run ./...; else echo "Instala golangci-lint"; fi
  TEST_CMD ?= $(GO_BIN) test ./...
  RUN_CMD ?= $(GO_BIN) run ./...
  FORMAT_CMD ?= $(GO_BIN) fmt ./...
  CLEAN_CMD ?= $(GO_BIN) clean -cache -modcache
endif

ifeq ($(STACK_LABEL),rust)
  TOOL_LABEL := Rust/Cargo
  SETUP_CMD ?= $(CARGO_BIN) fetch
  BUILD_CMD ?= $(CARGO_BIN) build
  LINT_CMD ?= if command -v cargo-clippy >/dev/null 2>&1; then $(CARGO_BIN) clippy --all-targets --all-features; else echo "Instala cargo-clippy"; fi
  TEST_CMD ?= $(CARGO_BIN) test
  RUN_CMD ?= $(CARGO_BIN) run
  FORMAT_CMD ?= $(CARGO_BIN) fmt
  CLEAN_CMD ?= $(CARGO_BIN) clean
endif

ifeq ($(STACK_LABEL),php-composer)
  TOOL_LABEL := PHP/Composer
  SETUP_CMD ?= $(COMPOSER_BIN) install
  BUILD_CMD ?= $(COMPOSER_BIN) dump-autoload
  LINT_CMD ?= $(COMPOSER_BIN) run-script lint || echo "Script lint no definido"
  TEST_CMD ?= $(COMPOSER_BIN) run-script test || echo "Script test no definido"
  RUN_CMD ?= php artisan serve || php -S localhost:8000 -t public
endif

ifeq ($(STACK_LABEL),ruby-bundler)
  TOOL_LABEL := Ruby/Bundler
  SETUP_CMD ?= $(BUNDLE_BIN) install
  BUILD_CMD ?= $(BUNDLE_BIN) exec rake build
  LINT_CMD ?= $(BUNDLE_BIN) exec rubocop || echo "Rubocop no disponible"
  TEST_CMD ?= $(BUNDLE_BIN) exec rspec || $(BUNDLE_BIN) exec rake test
  RUN_CMD ?= $(BUNDLE_BIN) exec rails server || $(BUNDLE_BIN) exec ruby main.rb
endif

ifeq ($(STACK_LABEL),dotnet)
  TOOL_LABEL := .NET CLI
  SETUP_CMD ?= $(DOTNET_BIN) restore
  BUILD_CMD ?= $(DOTNET_BIN) build
  LINT_CMD ?= $(DOTNET_BIN) format style --verify-no-changes || echo "dotnet format style"
  TEST_CMD ?= $(DOTNET_BIN) test
  RUN_CMD ?= $(DOTNET_BIN) run
  CLEAN_CMD ?= $(DOTNET_BIN) clean
endif

ifeq ($(STACK_LABEL),elixir-mix)
  TOOL_LABEL := Elixir/Mix
  SETUP_CMD ?= $(MIX_BIN) deps.get
  BUILD_CMD ?= $(MIX_BIN) compile
  LINT_CMD ?= $(MIX_BIN) credo --strict || echo "Credo no configurado"
  TEST_CMD ?= $(MIX_BIN) test
  RUN_CMD ?= $(MIX_BIN) phx.server
endif

ifeq ($(STACK_LABEL),cpp-cmake)
  TOOL_LABEL := CMake/C++
  SETUP_CMD ?= $(CMAKE_BIN) -S . -B build
  BUILD_CMD ?= $(CMAKE_BIN) --build build
  TEST_CMD ?= ctest --test-dir build || echo "ctest no configurado"
  CLEAN_CMD ?= rm -rf build
endif

ifeq ($(STACK_LABEL),unknown)
  TOOL_LABEL := Stack no identificado
endif

STACK_SUMMARY := $(STACK_LABEL) $(STACK_NOTE)

# -----------------------------------------------------------------------------
# Utilidades
# -----------------------------------------------------------------------------
# Determina el puerto interno por stack cuando SERVER_PORT no está definido
APP_PORT_INIT = APP_PORT=$${SERVER_PORT:-}; \
	if [ -z "$$APP_PORT" ]; then \
		case "$(STACK_LABEL)" in \
			node-npm|node-yarn|node-pnpm) APP_PORT=3000 ;; \
			python-poetry|python) APP_PORT=8000 ;; \
			go|rust) APP_PORT=8080 ;; \
			*) APP_PORT=8090 ;; \
		esac; \
	fi

define run-or-warn
	@if [ -n "$(strip $(1))" ]; then \
		echo "▶ $(2) :: $(STACK_SUMMARY)"; \
		if [ -f "$(ENV_FILE)" ]; then \
			while IFS= read -r line; do \
				case $$line in \
					''|\#*) ;; \
					*) export $$line ;; \
				esac; \
			done < "$(ENV_FILE)"; \
		fi; \
		$(1); \
	else \
		echo "⚠ $(2) no está definido para el stack '$(STACK_LABEL)'."; \
		echo "  Sugerencias:"; \
		echo "    - Exporta FORCE_STACK=<stack> para forzar la detección."; \
		echo "    - Define la variable $(3)_CMD para personalizar el comando."; \
		exit 1; \
	fi
endef

# -----------------------------------------------------------------------------
# Objetivos principales (agnósticos al stack)
# -----------------------------------------------------------------------------
.PHONY: help context setup build lint test run format clean ensure-db

help:
	@echo "Objetivos principales:" \
	&& echo "  make context     -> Mostrar stack detectado" \
	&& echo "  make setup       -> Instalar dependencias / preparación" \
	&& echo "  make build       -> Compilación/empacado" \
	&& echo "  make lint        -> Lint estático" \
	&& echo "  make test        -> Pruebas unitarias/integración" \
	&& echo "  make run         -> Ejecutar aplicación (si procede)" \
	&& echo "  make format      -> Formateo de código" \
	&& echo "  make clean       -> Limpieza de artefactos" \
	&& echo "  make compose-up  -> Reiniciar Docker Compose (down -v + up)" \
	&& echo "  make dev-up      -> Levantar perfil dev (si existe)" \
	&& echo "" \
	&& echo "Opciones:" \
	&& echo "  VERBOSE=1 make <target>  -> Ejecuta sin flags de silencio (útil para depurar)"

context:
	@echo "Stack detectado: $(STACK_SUMMARY)"
	@echo "Herramientas: $(if $(TOOL_LABEL),$(TOOL_LABEL),no definidas)"

setup:
	$(call run-or-warn,$(SETUP_CMD),Preparación inicial,SETUP)

build:
	$(call run-or-warn,$(BUILD_CMD),Compilación/Build,BUILD)

lint:
	$(call run-or-warn,$(LINT_CMD),Linter estático,LINT)

test:
	$(call run-or-warn,$(TEST_CMD),Ejecución de pruebas,TEST)

run: ensure-db
	$(call run-or-warn,$(RUN_CMD),Ejecución de la aplicación,RUN)

format:
	$(call run-or-warn,$(FORMAT_CMD),Formateo de código,FORMAT)

clean:
	$(call run-or-warn,$(CLEAN_CMD),Limpieza,CLEAN)

ensure-db:
	@echo "🗄  Verificando estado de la base de datos (docker compose)..."
	@DOCKER_HOST= DOCKER_BIN=docker COMPOSE_SUBCOMMAND=compose ./scripts/ensure_db.sh db

# -----------------------------------------------------------------------------
# Docker Compose (se reinicia siempre para evitar estados sucios)
# -----------------------------------------------------------------------------
DOCKER ?= DOCKER_HOST= docker
COMPOSE := $(DOCKER) compose

.PHONY: compose-up compose-up-fg compose-down compose-ps compose-logs compose-logs-app compose-restart compose-watch compose-dev-up compose-dev-up-fg compose-dev-logs

compose-up:
	@echo "🧹 Reiniciando contenedores (down -v --remove-orphans)"
	$(COMPOSE) down -v --remove-orphans
	$(COMPOSE) up --build -d

compose-up-fg:
	@echo "🧹 Reiniciando contenedores (interactivo)"
	$(COMPOSE) down -v --remove-orphans
	$(COMPOSE) up --build

compose-down:
	$(COMPOSE) down -v --remove-orphans

compose-ps:
	$(COMPOSE) ps

compose-logs:
	$(COMPOSE) logs -f --tail=200

compose-logs-app:
	$(COMPOSE) logs -f --tail=200 app

compose-restart: compose-down compose-up

compose-watch:
	@echo "# Requiere Docker Compose v2.22+"
	$(COMPOSE) watch

compose-dev-up:
	@echo "🧹 Reiniciando perfil dev"
	$(COMPOSE) --profile dev down -v --remove-orphans
	$(COMPOSE) --profile dev up --build -d app-dev db

compose-dev-up-fg:
	@echo "🧹 Reiniciando perfil dev (interactivo)"
	$(COMPOSE) --profile dev down -v --remove-orphans
	$(COMPOSE) --profile dev up --build app-dev db

compose-dev-logs:
	$(COMPOSE) --profile dev logs -f --tail=200 app-dev

# Alias retrocompatibles ------------------------------------------------------
.PHONY: up up-fg down ps logs logs-app restart dev-up dev-up-fg dev-logs watch

up: compose-up

up-fg: compose-up-fg

down: compose-down

ps: compose-ps

logs: compose-logs

logs-app: compose-logs-app

restart: compose-restart

dev-up: compose-dev-up

dev-up-fg: compose-dev-up-fg

dev-logs: compose-dev-logs

watch: compose-watch

# -----------------------------------------------------------------------------
# Docker (imagen standalone)
# -----------------------------------------------------------------------------
APP_IMAGE ?= servitec_app:latest
APP_CONTAINER ?= servitec_app
APP_DOCKERFILE ?=
APP_DOCKER_RUN_ARGS ?=
APP_DOCKER_DB_URL ?= jdbc:mariadb://servitec_db:3306/servitecdb
APP_DOCKER_PROFILE ?= docker

.PHONY: docker-redeploy docker-remove docker-build docker-run

docker-redeploy:
	$(if $(strip $(BUILD_CMD)),$(call run-or-warn,$(BUILD_CMD),Validación build previa a Docker,BUILD))
	$(if $(strip $(TEST_CMD)),$(call run-or-warn,$(TEST_CMD),Validación tests previa a Docker,TEST))
	@set -euo pipefail; \
	$(APP_PORT_INIT); \
	APP_PORT_VALUE=$$APP_PORT; \
	DOCKER_CMD='$(DOCKER)' STACK_LABEL="$(STACK_LABEL)" APP_IMAGE="$(APP_IMAGE)" APP_DOCKERFILE="$(APP_DOCKERFILE)" ENV_FILE="$(ENV_FILE)" APP_PORT_OVERRIDE="$$APP_PORT_VALUE" ./scripts/docker_build_auto.sh
	@set -euo pipefail; \
	if [ -f "$(ENV_FILE)" ]; then ENV_FLAG="--env-file $(ENV_FILE)"; set -a; . "$(ENV_FILE)"; set +a; else ENV_FLAG=""; fi; \
	$(APP_PORT_INIT); \
	HOST_PORT=$$APP_PORT; \
	UPLOADS_DIR=$$(pwd)/uploads; \
	mkdir -p "$$UPLOADS_DIR"; \
	echo "🧹 Eliminando contenedor existente (si aplica)"; \
	$(DOCKER) rm -f $(APP_CONTAINER) >/dev/null 2>&1 || true; \
	echo "🚀 Levantando contenedor $(APP_CONTAINER) en puerto $$HOST_PORT"; \
	$(DOCKER) run -d --name $(APP_CONTAINER) $$ENV_FLAG -p $$HOST_PORT:$$APP_PORT -v "$$UPLOADS_DIR:/app/uploads" $(APP_DOCKER_RUN_ARGS) \
		-e SPRING_PROFILES_ACTIVE=$(APP_DOCKER_PROFILE) \
		-e SPRING_DATASOURCE_URL=$(APP_DOCKER_DB_URL) \
		$(APP_IMAGE)

docker-remove:
	$(DOCKER) rm -f $(APP_CONTAINER) >/dev/null 2>&1 || true

docker-build:
	@set -euo pipefail; \
	$(APP_PORT_INIT); \
	APP_PORT_VALUE=$$APP_PORT; \
	DOCKER_CMD='$(DOCKER)' STACK_LABEL="$(STACK_LABEL)" APP_IMAGE="$(APP_IMAGE)" APP_DOCKERFILE="$(APP_DOCKERFILE)" ENV_FILE="$(ENV_FILE)" APP_PORT_OVERRIDE="$$APP_PORT_VALUE" ./scripts/docker_build_auto.sh

docker-run:
	@set -euo pipefail; \
	if [ -f "$(ENV_FILE)" ]; then ENV_FLAG="--env-file $(ENV_FILE)"; set -a; . "$(ENV_FILE)"; set +a; else ENV_FLAG=""; fi; \
	$(APP_PORT_INIT); \
	HOST_PORT=$$APP_PORT; \
	UPLOADS_DIR=$$(pwd)/uploads; \
	mkdir -p "$$UPLOADS_DIR"; \
	$(DOCKER) rm -f $(APP_CONTAINER) >/dev/null 2>&1 || true; \
	echo "🚀 Levantando contenedor $(APP_CONTAINER) en puerto $$HOST_PORT"; \
	$(DOCKER) run -d --name $(APP_CONTAINER) $$ENV_FLAG -p $$HOST_PORT:$$APP_PORT -v "$$UPLOADS_DIR:/app/uploads" $(APP_DOCKER_RUN_ARGS) \
		-e SPRING_PROFILES_ACTIVE=$(APP_DOCKER_PROFILE) \
		-e SPRING_DATASOURCE_URL=$(APP_DOCKER_DB_URL) \
		$(APP_IMAGE)
