./mvnw.bat flyway:migrate
./mvnw.bat compile
npx @openapitools/openapi-generator-cli generate