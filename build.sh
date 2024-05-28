#!/usr/bin/env sh
set -e

cd backend
./mvnw compile test-compile
cd ..
npx @openapitools/openapi-generator-cli generate