if (!(Test-Path "env:IMAGEHIVE_PG_JDBC_URL") -and !(Test-Path "env:IMAGEHIVE_PG_USER") -and !(Test-Path "env:IMAGEHIVE_PG_PASSWORD")) {
    $env:IMAGEHIVE_PG_JDBC_URL = $args[0]
    $env:IMAGEHIVE_PG_USER = $args[1]
    $env:IMAGEHIVE_PG_PASSWORD = $args[2]
}
if (Test-Path -Path .\JAVA_HOME -PathType Leaf) {
    $env:JAVA_HOME = Get-Content -Path .\JAVA_HOME
}
Set-Location backend
./mvnw.bat flyway:migrate
./mvnw.bat compile
Set-Location ..
npx @openapitools/openapi-generator-cli generate