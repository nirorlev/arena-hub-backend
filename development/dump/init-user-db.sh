#!/bin/bash
set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
	CREATE USER arena_user WITH PASSWORD '$POSTGRES_PASS';
  CREATE DATABASE arena_hub WITH TEMPLATE = template0 ENCODING = 'UTF8' LOCALE_PROVIDER = libc LOCALE = 'en_US.utf8';
  ALTER DATABASE arena_hub OWNER TO postgres;
  ALTER DATABASE arena_hub SET search_path TO 'public', 'arena_hub';
	GRANT ALL PRIVILEGES ON DATABASE arena_hub TO arena_user;
	\c arena_hub
  CREATE SCHEMA arena_hub;
  ALTER SCHEMA arena_hub OWNER TO postgres;
EOSQL