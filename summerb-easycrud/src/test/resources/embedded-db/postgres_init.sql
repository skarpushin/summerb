CREATE TABLE users_table
(
    id          CHAR(36)    NOT NULL,

    name        VARCHAR(45) NOT NULL,
    about       VARCHAR(45) NULL,
    active      BOOLEAN     NOT NULL,
    karma       INT         NOT NULL,
    status      VARCHAR(45) NULL,

    created_at  BIGINT      NOT NULL,
    modified_at BIGINT      NOT NULL,
    created_by  VARCHAR     NOT NULL,
    modified_by VARCHAR     NOT NULL,

    PRIMARY KEY (id)
);

CREATE TABLE posts
(
    id          BIGSERIAL PRIMARY KEY,
    title       VARCHAR NOT NULL,
    body        VARCHAR,
    likes       INTEGER NOT NULL DEFAULT 0,
    dislikes    INTEGER NOT NULL DEFAULT 0,
    author_id   VARCHAR NOT NULL,
    pinned_by   VARCHAR NULL,

    created_at  BIGINT  NOT NULL,
    modified_at BIGINT  NOT NULL,
    created_by  VARCHAR NOT NULL,
    modified_by VARCHAR NOT NULL
);

CREATE TABLE comments
(
    id          BIGSERIAL PRIMARY KEY,
    post_id     BIGINT  NOT NULL,
    author_id   VARCHAR NOT NULL,
    comment     VARCHAR NOT NULL,

    created_at  BIGINT  NOT NULL,
    modified_at BIGINT  NOT NULL,
    created_by  VARCHAR NOT NULL,
    modified_by VARCHAR NOT NULL
);

CREATE SEQUENCE forms_mtom_seq;
CREATE TABLE forms_mtom
(
    id  INT         NOT NULL DEFAULT NEXTVAL('forms_mtom_seq'),
    src BIGINT      NOT NULL,
    dst VARCHAR(36) NOT NULL,
    PRIMARY KEY (id)
);
