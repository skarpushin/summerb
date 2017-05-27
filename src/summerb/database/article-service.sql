CREATE  TABLE `articles` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT ,
  `article_key` VARCHAR(255) NOT NULL ,
  `article_group` VARCHAR(255) NULL ,
  `lang` VARCHAR(2) NOT NULL ,
  `title` VARCHAR(512) NULL ,
  `annotation` TEXT NULL ,
  `content` MEDIUMTEXT NOT NULL ,
  `created_at` BIGINT NOT NULL ,
  `modified_at` BIGINT NOT NULL ,
  `created_by` CHAR(36) NOT NULL ,
  `modified_by` CHAR(36) NOT NULL ,
  PRIMARY KEY (`id`) ,
  UNIQUE INDEX `id_UNIQUE` (`id` ASC) ,
  UNIQUE INDEX `article_key_UNIQUE` (`article_key` ASC, `lang` ASC),
  INDEX `idx_group` (`article_group` ASC)
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COLLATE = utf8_general_ci;

-- CREATE  TABLE `articles_attachments` (
--  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT ,
--  `name` VARCHAR(200) NOT NULL ,
--  `size` BIGINT NOT NULL ,
--  `article_id` INT UNSIGNED NOT NULL ,
--  `contents` LONGBLOB NULL ,
--  PRIMARY KEY (`id`) ,
--  UNIQUE INDEX `id_UNIQUE` (`id` ASC) ,
--  UNIQUE INDEX `unique_file` (`article_id` ASC, `name` ASC),
--  INDEX `articles_attachments_FK_articles_idx` (`article_id` ASC) ,
--  CONSTRAINT `articles_attachments_FK_articles` FOREIGN KEY (`article_id` ) REFERENCES `articles` (`id` ) ON DELETE CASCADE ON UPDATE CASCADE
-- ) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COLLATE = utf8_general_ci;

CREATE  TABLE `articles_attachments` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT ,
  `name` VARCHAR(200) NOT NULL ,
  `size` BIGINT NOT NULL ,
  `article_id` INT UNSIGNED NOT NULL ,
  PRIMARY KEY (`id`) ,
  UNIQUE INDEX `id_UNIQUE` (`id` ASC) ,
  UNIQUE INDEX `unique_file` (`article_id` ASC, `name` ASC),
  INDEX `articles_attachments_FK_articles_idx` (`article_id` ASC) ,
  CONSTRAINT `articles_attachments_FK_articles` FOREIGN KEY (`article_id` ) REFERENCES `articles` (`id` ) ON UPDATE NO ACTION ON DELETE NO ACTION
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COLLATE = utf8_general_ci;

