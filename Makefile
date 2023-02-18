.DEFAULT_GOAL := help
.PHONY: help
help: ## Affiche cette aide
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-30s\033[0m %s\n", $$1, $$2}'

.PHONY: start
start: docker-compose-postgres.yml ## Démarre la BD en local et lance les migrations flyway
	docker-compose -f docker-compose-postgres.yml up

.PHONY: run-db
run-db: docker-compose-postgres.yml ## Démarre la BD en local
	docker-compose -f docker-compose-postgres.yml up postgres

.PHONY: migrate
migrate: docker-compose-postgres.yml ## Lance les migrations flyway
	docker-compose -f docker-compose-postgres.yml up flyway-migrate

.PHONY: clean
clean: docker-compose-postgres.yml ## Lance le clean de la BD
	docker-compose -f docker-compose-postgres.yml up flyway-clean

.PHONY: stop
stop: docker-compose-postgres.yml ## Stop le container de la bd en cours d'exécution
	docker-compose -f docker-compose-postgres.yml down

.PHONY: destroy
destroy: docker-compose-postgres.yml ## Stop le container de la bd en cours d'exécution et détruit le volume monté
	docker-compose -f docker-compose-postgres.yml down -v

