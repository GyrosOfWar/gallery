#!/usr/bin/env sh
set -e

cd backend
./mvnw flyway:migrate
./mvnw compile
cd ..
npx @openapitools/openapi-generator-cli generate