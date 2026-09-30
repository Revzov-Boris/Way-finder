CREATE TABLE cities (
    id          SERIAL      PRIMARY KEY,
    name        VARCHAR(255),
    address     VARCHAR(255) UNIQUE,
    time_zone   INTEGER     NOT NULL
);

CREATE TABLE routs (
    id               BIGSERIAL    PRIMARY KEY,
    type_transport   VARCHAR(255),
    type_distance    VARCHAR(255)
);

CREATE TABLE halts (
    id        BIGSERIAL     PRIMARY KEY,
    route_id  BIGINT        REFERENCES routs(id),
    city_id   INTEGER       REFERENCES cities(id),
    time      TIMESTAMP
);

CREATE INDEX idx_halts_city_id ON halsts(city_id);
CREATE INDEX idx_halts_city_id ON halsts(route_id);

