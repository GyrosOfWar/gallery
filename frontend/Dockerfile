# base node image
FROM node:lts as base

# set for base and all layer that inherit from it
ENV NODE_ENV production

# Install all node_modules, including dev dependencies
FROM base as deps

WORKDIR /imagehive

ADD package.json ./
RUN npm install --include=dev

# Setup production node_modules
FROM base as production-deps

WORKDIR /imagehive

COPY --from=deps /imagehive/node_modules /imagehive/node_modules
ADD package.json ./
RUN npm prune --omit=dev

# Build the app
FROM base as build

WORKDIR /imagehive

COPY --from=deps /imagehive/node_modules /imagehive/node_modules

ADD . .
RUN npm run build

# Finally, build the production image with minimal footprint
FROM base

ENV DATABASE_URL=file:/data/sqlite.db
ENV PORT="8080"
ENV NODE_ENV="production"

# add shortcut for connecting to database CLI
RUN echo "#!/bin/sh\nset -x\nsqlite3 \$DATABASE_URL" > /usr/local/bin/database-cli && chmod +x /usr/local/bin/database-cli

WORKDIR /imagehive

COPY --from=production-deps /imagehive/node_modules /imagehive/node_modules

COPY --from=build /imagehive/build /imagehive/build
COPY --from=build /imagehive/public /imagehive/public
COPY --from=build /imagehive/package.json /imagehive/package.json
COPY --from=build /imagehive/start.sh /imagehive/start.sh

ENTRYPOINT [ "./start.sh" ]