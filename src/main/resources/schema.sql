CREATE TABLE IF NOT EXISTS users
(
    id    INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
    name  VARCHAR(255)                             NOT NULL,
    email VARCHAR(255)                             NOT NULL
);

CREATE TABLE IF NOT EXISTS items
(
    id          INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
    name        VARCHAR(255)                             NOT NULL,
    description VARCHAR(500),
    owner       INTEGER REFERENCES users (id),
    available   BOOLEAN                                  NOT NULL
);

CREATE TABLE IF NOT EXISTS booking
(
    id     INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
    start_date   TIMESTAMP WITHOUT TIME ZONE,
    end_date     TIMESTAMP WITHOUT TIME ZONE,
    item   INTEGER REFERENCES items (id),
    booker INTEGER REFERENCES users (id),
    status VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS comments
(
    id        INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
    text      VARCHAR(255),
    item_id   INTEGER REFERENCES items (id),
    author_id INTEGER REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS requests
(
    id           INTEGER GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY NOT NULL,
    description  VARCHAR(255),
    requestor_id INTEGER REFERENCES users (id),
    created      TIMESTAMP WITHOUT TIME ZONE
);