SHELL := /bin/bash

# Force local docker socket by clearing DOCKER_HOST for each target
DOCKER := DOCKER_HOST= docker

.PHONY: up up-fg down ps logs logs-app restart

up:
	$(DOCKER) compose up --build -d

up-fg:
	$(DOCKER) compose up --build

down:
	$(DOCKER) compose down -v

ps:
	$(DOCKER) compose ps

logs:
	$(DOCKER) compose logs -f --tail=200

logs-app:
	$(DOCKER) compose logs -f --tail=200 app

restart: down up

