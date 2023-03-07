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
0. Prerequisites: Install Java 17+, NodeJS 18+ and PostgreSQL 15+
1. Install Typescript globally (`npm i -g typescript`)
2. Create a new database (`createdb imagehive`)
3. Set the required environment variables (`IMAGEHIVE_PG_JDBC_URL`, `IMAGEHIVE_PG_USER` and `IMAGEHIVE_PG_PASSWORD`)
4. Run the `regenerate-code` script in the main directory (`.ps1` on windows, `.sh` on *nix)
5. Install the frontend dependencies (`npm i`)
6. Start the backend (in the `backend` folder, run `./mvnw mn:run`)
7. Start the frontend (in the `frontend` folder, run `npm run dev`)
