CREATE TABLE "user"
(
    id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    username      VARCHAR     NOT NULL,
    email         VARCHAR     NOT NULL,
    password_hash VARCHAR     NOT NULL,
    admin         BOOLEAN     NOT NULL,
    totp_secret   VARCHAR,
    created_on    TIMESTAMPTZ NOT NULL
);

CREATE TABLE image
(
    id              UUID PRIMARY KEY NOT NULL,
    created_on      TIMESTAMPTZ      NOT NULL,
    owner_id        BIGINT REFERENCES "user" (id),
    height          INT              NOT NULL,
    width           INT              NOT NULL,
    geo_coordinates POINT,
    metadata        JSONB            NOT NULL,
    tags            VARCHAR[]        NOT NULL
);

CREATE TABLE album
(
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    "name"     VARCHAR     NOT NULL,
    created_on TIMESTAMPTZ NOT NULL,
    owner_id   BIGINT REFERENCES "user" (id),
    tags       VARCHAR[]   NOT NULL
);

CREATE TABLE album_image
(
    album_id BIGINT REFERENCES album (id) NOT NULL,
    image_id UUID REFERENCES image (id)   NOT NULL,
    UNIQUE (album_id, image_id)
);
