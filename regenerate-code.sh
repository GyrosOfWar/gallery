#!/usr/bin/env bash
set -e

cd backend
./mvnw clean
cd ..

rm -rf imagehive-client
. build.sh
cd imagehive-client
npm run build
cd ..
cd frontend
npm i
