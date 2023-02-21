if (!(Test-Path "env:IMAGEHIVE_PG_JDBC_URL") -and !(Test-Path "env:IMAGEHIVE_PG_USER") -and !(Test-Path "env:IMAGEHIVE_PG_PASSWORD")) {
    $env:IMAGEHIVE_PG_JDBC_URL=$args[0]
    $env:IMAGEHIVE_PG_USER=$args[1]
    $env:IMAGEHIVE_PG_PASSWORD=$args[2]
}
./mvnw.bat flyway:migrate
./mvnw.bat compile
npx @openapitools/openapi-generator-cli generate