# Environment variables
include .local.env
export $(shell sed 's/=.*//' .local.env)

export START_ENVIRONMENT=environmentVars,local,swagger
export REDIS_HOST=127.0.0.1
export REDIS_PORT=6379
export REDIS_SSL=false
export POSTGRES_HOST=127.0.0.1
export POSTGRES_USER=arena_user
export POSTGRES_DB_NAME=arena_hub
export POSTGRES_PORT=5432
export GUIDE_API_PORT=9999
export PDP_ADDRESS=http://pdp:7000
export AWS_ROLE_SESSION_NAME=S3Upload
export AWS_S3_BUCKET=bucket name
export AWS_S3_REGION=eu-west-2
export GUIDE_USER_STORE_DOMAIN=storage-arena.dev.k8s.powtoon.com
export AWS_IAM_ROLE=iam role
export BI_TRACKING_HOST=https://example.com
export SENTRY_DSN=https://examplePublicKey@o0.ingest.sentry.io/0
export APP_ENV=local
export SENTRY_AUTO_INIT=false
export OTEL_TRACES_EXPORTER=none
export OTEL_METRICS_EXPORTER=none

# Paths
PROJECT_PATH := ../guide-core-java/guidecore-server

.PHONY: pull_sql_dump build run

pull_sql_dump:
	@wget https://powtoon-dev-develop-static.s3.amazonaws.com/arena-hub-be-db-dump/dump.sql.tar.gz -O dump.sql.tar.gz
	tar -xzvf dump.sql.tar.gz
	rm dump.sql.tar.gz
	mv dump.sql dump/
	
build:
	@cd $(PROJECT_PATH) && \
	mvn -Dmaven.compiler.source=15 -Dmaven.compiler.target=15 clean package -Dmaven.test.skip=true

run:
	@cd $(PROJECT_PATH)/target && \
	java -Xms256m -Xmx512m -jar "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005" -Dloader.path=.,config,lib guidecore.jar --spring.profiles.active=$(START_ENVIRONMENT)
