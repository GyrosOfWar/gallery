CREATE TABLE "user"
(
    id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    email         VARCHAR     NOT NULL,
    password_hash VARCHAR     NOT NULL,
    username      VARCHAR     NOT NULL,
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
    metadata        JSONB            NOT NULL
);
