$env:IMAGEHIVE_PG_JDBC_URL=$args[0]
$env:IMAGEHIVE_PG_USER=$args[1]
$env:IMAGEHIVE_PG_PASSWORD=$args[2]

.\mvnw clean
if (Test-Path "imagehive-client") {
    Remove-Item "imagehive-client" -Recurse -Force -Confirm:$false
}
if (Test-Path "imagehive-frontend\node_modules") {
    Remove-Item "imagehive-frontend\node_modules"  -Recurse -Force -Confirm:$false
}
.\build.ps1 $args[0] $args[1] $args[2]
cd imagehive-client
npm run build
cd ..
cd imagehive-frontend
npm i
