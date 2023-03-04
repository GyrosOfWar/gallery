CREATE OR REPLACE FUNCTION immutable_array_to_string(VARCHAR[])
  RETURNS text LANGUAGE sql IMMUTABLE AS $$SELECT array_to_string($1, ' ')$$;

CREATE TABLE "user"
(
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    username        VARCHAR       NOT NULL,
    email           VARCHAR       NOT NULL,
    password_hash   VARCHAR       NOT NULL,
    admin           BOOLEAN       NOT NULL,
    totp_secret     VARCHAR,
    user_settings   JSONB         NOT NULL,
    created_on      TIMESTAMPTZ   NOT NULL,
    last_login      TIMESTAMPTZ
);

CREATE TABLE image
(
    id              UUID PRIMARY KEY NOT NULL,
    title           VARCHAR,
    description     VARCHAR,
    created_on      TIMESTAMPTZ      NOT NULL,
    captured_on     TIMESTAMPTZ,
    owner_id        BIGINT REFERENCES "user" (id),
    width           INT              NOT NULL,
    height          INT              NOT NULL,
    gps_latitude    DOUBLE PRECISION,
    gps_longitude   DOUBLE PRECISION,
    metadata        JSONB            NOT NULL,
    tags            VARCHAR[]        NOT NULL,
    favorite        BOOLEAN          NOT NULL DEFAULT false,
    file_path       VARCHAR          NOT NULL,
    ts_vec          tsvector         NOT NULL GENERATED ALWAYS AS (
        to_tsvector('english', coalesce(title, '') || ' ' || coalesce(description, '') || ' ' || immutable_array_to_string(tags))) STORED
);

CREATE INDEX fulltext_image_idx ON image USING GIN (ts_vec);

CREATE TABLE album
(
    id              BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    "name"          VARCHAR     NOT NULL,
    description     VARCHAR,
    created_on      TIMESTAMPTZ NOT NULL,
    owner_id        BIGINT      REFERENCES "user" (id),
    thumbnail_id    UUID        REFERENCES image (id),
    tags            VARCHAR[]   NOT NULL,
    ts_vec          tsvector    NOT NULL GENERATED ALWAYS AS (
        to_tsvector('english', "name" || ' ' || coalesce(description, '') || ' ' || immutable_array_to_string(tags))) STORED
);

CREATE INDEX fulltext_album_idx ON album USING GIN (ts_vec);

CREATE TABLE album_image
(
    album_id    BIGINT  REFERENCES album (id) NOT NULL,
    image_id    UUID    REFERENCES image (id) NOT NULL,
    UNIQUE (album_id, image_id)
);
