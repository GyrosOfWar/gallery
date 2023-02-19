#!/usr/bin/env bash
set -e

./mvnw clean
rm -rf imagehive-client
rm -rf imagehive-frontend/node_modules
. build.sh
cd imagehive-client
npm run build
cd ..
cd imagehive-frontend
npm i
