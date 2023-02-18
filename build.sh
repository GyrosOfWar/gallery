#!/usr/bin/env sh
set -e

./mvnw flyway:migrate
./mvnw compile
npx @openapitools/openapi-generator-cli generate