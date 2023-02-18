#!/usr/bin/env sh
set -e

export IMAGEHIVE_PG_JDBC_URL=jdbc:postgresql://localhost:5432/$1
export IMAGEHIVE_PG_USER=$2
export IMAGEHIVE_PG_PASSWORD=$3

./mvnw flyway:migrate
./mvnw compile
npx @openapitools/openapi-generator-cli generate