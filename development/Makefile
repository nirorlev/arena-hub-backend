# Environment variables
export START_ENVIRONMENT=environmentVars
export FRONTENDPATH=arena-hub
export REDIS_HOST=127.0.0.1
export REDIS_PASS=kyle2020TBfIU584
export REDIS_PORT=6379
export REDIS_SSL=false
export MYSQL_HOST=127.0.0.1
export MYSQL_USER=user
export MYSQL_PASS=userpass240109
export MYSQL_DB_NAME=arena_hub
export MYSQL_PORT=3306
export MYSQL_SSL=false
export CHANNEL_SUBJECT_ID_CSV=1805,1806,1809,1832
export GUIDE_API_PORT=9999
export GUIDE_USER_STORE_CIDR=0.0.0.0/0
export PDP_ADDRESS=http://localhost:7000
# export AWS_STS_REGIONAL_ENDPOINTS=regional
# export AWS_DEFAULT_REGION=us-east-1
# export AWS_REGION=us-east-1
# export AWS_ROLE_ARN=arn:aws:iam::861419891964:role/Powtoon-dev-arena-role
# export AWS_ROLE_SESSION_NAME=S3Upload
# export AWS_S3_BUCKET=dev-arena-storage
# export AWS_S3_REGION=us-east-1
# export AWS_S3_SITE_BUCKET=dev-arena-site
# export GUIDE_USER_STORE_DOMAIN=storage.staging-arena.powtoon.com
# export POWTOON_BASE_HOST=https://arenaec.dev-public.k8s.powtoon.com/
# export SSO_TOKEN_HOST=arenaec.dev-public.k8s.powtoon.com

# Paths
PROJECT_PATH := ../guide-core-java/code/guidecore-server

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
	java -Xms256m -Xmx512m -jar -Dloader.path=.,config,lib guidecore.jar --spring.profiles.active=$(START_ENVIRONMENT)
