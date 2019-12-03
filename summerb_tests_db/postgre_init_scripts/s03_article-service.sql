CREATE SEQUENCE articles_seqa;

CREATE  TABLE "articles" (
  "id" INT CHECK ("id" > 0) NOT NULL DEFAULT NEXTVAL ('articles_seqa') ,
  "article_key" VARCHAR(255) NOT NULL ,
  "article_group" VARCHAR(255) NULL ,
  "lang" VARCHAR(2) NOT NULL ,
  "title" VARCHAR(512) NULL ,
  "annotation" TEXT NULL ,
  "content" TEXT NOT NULL ,
  "created_at" BIGINT NOT NULL ,
  "modified_at" BIGINT NOT NULL ,
  "created_by" CHAR(36) NOT NULL ,
  "modified_by" CHAR(36) NOT NULL ,
  PRIMARY KEY ("id") ,
  CONSTRAINT "articles_UNIQUE" UNIQUE  ("id") ,
  CONSTRAINT "article_key_UNIQUE" UNIQUE  ("article_key", "lang")
);

CREATE INDEX "idx_group" ON "articles" ("article_group");

-- CREATE  TABLE "articles_attachments" (
--  "id" INT UNSIGNED NOT NULL AUTO_INCREMENT ,
--  "name" VARCHAR(200) NOT NULL ,
--  "size" BIGINT NOT NULL ,
--  "article_id" INT UNSIGNED NOT NULL ,
--  "contents" LONGBLOB NULL ,
--  PRIMARY KEY ("id") ,
--  UNIQUE INDEX "id_UNIQUE" ("id") ,
--  UNIQUE INDEX "unique_file" ("article_id", "name"),
--  INDEX "articles_attachments_FK_articles_idx" ("article_id") ,
--  CONSTRAINT "articles_attachments_FK_articles" FOREIGN KEY ("article_id" ) REFERENCES "articles" ("id" ) ON DELETE CASCADE ON UPDATE CASCADE
-- );

CREATE SEQUENCE articles_attachments_seq;

CREATE  TABLE "articles_attachments" (
  "id" INT CHECK ("id" > 0) NOT NULL DEFAULT NEXTVAL ('articles_attachments_seq') ,
  "name" VARCHAR(200) NOT NULL ,
  "size" BIGINT NOT NULL ,
  "article_id" INT CHECK ("article_id" > 0) NOT NULL ,
  PRIMARY KEY ("id") ,
  CONSTRAINT "articles_attachments_UNIQUE" UNIQUE  ("id") ,
  CONSTRAINT "unique_file" UNIQUE  ("article_id", "name")
  ,
  CONSTRAINT "articles_attachments_FK_articles" FOREIGN KEY ("article_id" ) REFERENCES "articles" ("id" ) ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE INDEX "articles_attachments_FK_articles_idx" ON "articles_attachments" ("article_id");

