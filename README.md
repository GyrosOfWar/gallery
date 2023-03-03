# Imagehive

A self-hostable photo gallery. Very early, very work-in-progress and not ready for use yet. Licensed under GPLv3.

## Targeted features
- Powerful user management (photos are by default private, can be shared with other users in the app, or users outside it)
- A photo feed similar to GPhotos or Apple Photos, searchable.
- Album management: Allow creating albums with images and share them with others
- Automatic tagging of images
- Easy deployment with a `docker-compose.yaml` file. (potentially also )

## Non-features
- Support for databases other than PostgreSQL. We use PostgreSQL specific features and support for MySQL etc. is not planned.
- Image editing support. We want to keep the scope reasonable (this is a side project).
