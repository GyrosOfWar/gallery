.\mvnw clean
if (Test-Path "imagehive-client") {
    Remove-Item "imagehive-client" -Recurse -Force -Confirm:$false
}
if (Test-Path "imagehive-frontend\node_modules") {
    Remove-Item "imagehive-frontend\node_modules"  -Recurse -Force -Confirm:$false
}
.\build.ps1
cd imagehive-client
npm run build
cd ..
cd imagehive-frontend
npm i
