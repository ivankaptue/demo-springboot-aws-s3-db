.DEFAULT_GOAL := help
.PHONY: help
help: ## Affiche cette aide
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | sort | awk 'BEGIN {FS = ":.*?## "}; {printf "\033[36m%-30s\033[0m %s\n", $$1, $$2}'

.PHONY: start-db
start-db: docker-compose-postgres.yml ## Démarre la bd en local et lance les migrations flyway
	docker-compose -f docker-compose-postgres.yml up

.PHONY: destroy-db
destroy-db: docker-compose-postgres.yml ## Stop le container de la bd en cours d'exécution
	docker-compose -f docker-compose-postgres.yml down -v

