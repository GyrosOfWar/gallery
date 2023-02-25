if (!(Test-Path "env:IMAGEHIVE_PG_JDBC_URL") -and !(Test-Path "env:IMAGEHIVE_PG_USER") -and !(Test-Path "env:IMAGEHIVE_PG_PASSWORD")) {
    $env:IMAGEHIVE_PG_JDBC_URL=$args[0]
    $env:IMAGEHIVE_PG_USER=$args[1]
    $env:IMAGEHIVE_PG_PASSWORD=$args[2]
}
Set-Location backend
.\mvnw clean
Set-Location ..

if (Test-Path "imagehive-client") {
    Remove-Item "imagehive-client" -Recurse -Force -Confirm:$false
}

.\build.ps1 $env:IMAGEHIVE_PG_JDBC_URL $env:IMAGEHIVE_PG_USER $env:IMAGEHIVE_PG_PASSWORD
Set-Location imagehive-client
npm run build
Set-Location ..
Set-Location frontend
npm i
Set-Location ..
