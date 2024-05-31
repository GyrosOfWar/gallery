# Imagehive

A self-hostable photo gallery. Very early, very work-in-progress and not ready for use yet. Licensed under GPLv3.

## Targeted features

- Powerful user management (photos are by default private, can be shared with other users in the app, or users outside it)
- A photo feed similar to GPhotos or Apple Photos, searchable.
- Album management: Allow creating albums with images and share them with others
- Automatic tagging of images
- Easy deployment with a `docker-compose.yaml` file. (potentially also provide a Kubernetes deployment setup)

## Non-features

- Support for databases other than PostgreSQL. We use PostgreSQL specific features and support for MySQL etc. is not planned.
- Image editing support. We want to keep the scope reasonable (this is a side project).

## Developer setup

0. Prerequisites: Install Java 21, NodeJS 18+ and PostgreSQL 16 (plus `nushell` and `just` if you want to use the `justfile` shortcuts)
1. Create a new database (`createdb imagehive`)
2. Set the required environment variables (`IMAGEHIVE_PG_JDBC_URL`, `IMAGEHIVE_PG_USER` and `IMAGEHIVE_PG_PASSWORD`)
3. Run `just generate-client` and wait for it to succeed.
4. To start the different apps, do `just backend start`, `just frontend start` and `just ai start` if you have `just` and `nushell` installed, otherwise
   look in the `justfile` for the respective projects to see what commands they run.
