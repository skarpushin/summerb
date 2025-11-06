CREATE TABLE users_table
(
    id          CHAR(36)     NOT NULL,

    name        VARCHAR(45)  NOT NULL,
    about       VARCHAR(45)  NULL,
    active      BIT          NOT NULL,
    karma       INT          NOT NULL,
    status      VARCHAR(45)  NULL,

    created_at  BIGINT       NOT NULL,
    modified_at BIGINT       NOT NULL,
    created_by  VARCHAR(255) NOT NULL,
    modified_by VARCHAR(255) NOT NULL,

    PRIMARY KEY (id)
);

CREATE TABLE posts
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    title       VARCHAR(255) NOT NULL,
    body        VARCHAR(255),
    likes       INT          NOT NULL DEFAULT 0,
    dislikes    INT          NOT NULL DEFAULT 0,
    author_id   VARCHAR(255) NOT NULL,
    pinned_by   VARCHAR(255) NULL,

    created_at  BIGINT       NOT NULL,
    modified_at BIGINT       NOT NULL,
    created_by  VARCHAR(255) NOT NULL,
    modified_by VARCHAR(255) NOT NULL
) ENGINE=InnoDB;

CREATE TABLE comments
(
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,

    post_id     BIGINT       NOT NULL,
    author_id   VARCHAR(255) NOT NULL,
    comment     VARCHAR(255) NOT NULL,

    created_at  BIGINT       NOT NULL,
    modified_at BIGINT       NOT NULL,
    created_by  VARCHAR(255) NOT NULL,
    modified_by VARCHAR(255) NOT NULL
) ENGINE=InnoDB;

CREATE TABLE forms_mtom
(
    id  INT         NOT NULL AUTO_INCREMENT,
    src BIGINT      NOT NULL,
    dst VARCHAR(36) NOT NULL,
    PRIMARY KEY (id)
);
