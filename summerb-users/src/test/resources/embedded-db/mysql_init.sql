-- CREATE SCHEMA `summerb_tests` DEFAULT CHARACTER SET utf8 ;

CREATE  TABLE `users` (
  `uuid` CHAR(36) NOT NULL ,
  `display_name` VARCHAR(45) NOT NULL ,
  `email` VARCHAR(45) NOT NULL ,
  `time_zone` VARCHAR(20) NOT NULL DEFAULT 'GMT+0' ,
  `locale` CHAR(8) NOT NULL DEFAULT 'en_US' ,
  `registered_at` BIGINT NOT NULL ,
  `is_blocked` TINYINT(1) NOT NULL DEFAULT 0 ,
  `integration_data` VARCHAR(45) NULL ,
  PRIMARY KEY (`uuid`) ,
  UNIQUE INDEX `uuid_UNIQUE` (`uuid` ASC) ,
  UNIQUE INDEX `email_UNIQUE` (`email` ASC) ,
  INDEX `idxDisplayName` (`display_name` ASC) 
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COLLATE = utf8_general_ci;

CREATE  TABLE `users_passwords` (
  `user_uuid` CHAR(36) NOT NULL ,
  `password_hash` VARCHAR(160) NULL ,
  `restoration_token` CHAR(36) NULL ,
  PRIMARY KEY (`user_uuid`) ,
  UNIQUE INDEX `user_uuid_UNIQUE` (`user_uuid` ASC) 
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COLLATE = utf8_general_ci;

CREATE  TABLE `users_auth_tokens` (
  `uuid` VARCHAR(36) NOT NULL ,
  `token_value` VARCHAR(36) NOT NULL ,
  `user_uuid` CHAR(36) NOT NULL ,
  `created_at` BIGINT NOT NULL ,
  `expires_at` BIGINT NOT NULL ,
  `last_verified_at` BIGINT NOT NULL ,
  `client_ip` VARCHAR(39) NULL ,
  PRIMARY KEY (`uuid`) ,
  UNIQUE INDEX `uuid_UNIQUE` (`uuid` ASC) ,
  INDEX `idx_expires_at` (`expires_at` ASC) ,
  INDEX `idx_user` (`user_uuid` ASC) 
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COLLATE = utf8_general_ci;

CREATE  TABLE `users_permissions` (
  `domain_name` CHAR(45) NOT NULL ,
  `subject_id` CHAR(45) NOT NULL ,
  `user_uuid` CHAR(45) NOT NULL ,
  `permission_key` CHAR(45) NOT NULL ,
  PRIMARY KEY (`domain_name`, `subject_id`, `user_uuid`, `permission_key`) 
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COLLATE = utf8_general_ci;

