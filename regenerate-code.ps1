if (!(Test-Path "env:IMAGEHIVE_PG_JDBC_URL") -and !(Test-Path "env:IMAGEHIVE_PG_USER") -and !(Test-Path "env:IMAGEHIVE_PG_PASSWORD")) {
    $env:IMAGEHIVE_PG_JDBC_URL=$args[0]
    $env:IMAGEHIVE_PG_USER=$args[1]
    $env:IMAGEHIVE_PG_PASSWORD=$args[2]
}
.\mvnw clean
if (Test-Path "imagehive-client") {
    Remove-Item "imagehive-client" -Recurse -Force -Confirm:$false
}
if (Test-Path "imagehive-frontend\node_modules") {
    Remove-Item "imagehive-frontend\node_modules"  -Recurse -Force -Confirm:$false
}
.\build.ps1 $env:IMAGEHIVE_PG_JDBC_URL $env:IMAGEHIVE_PG_USER $env:IMAGEHIVE_PG_PASSWORD
Set-Location imagehive-client
npm run build
Set-Location ..
Set-Location imagehive-frontend
npm i