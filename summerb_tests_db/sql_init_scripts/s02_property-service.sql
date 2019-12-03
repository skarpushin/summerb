CREATE TABLE `props_alias_app` (
  `alias` INT NOT NULL AUTO_INCREMENT ,
  `alias_name` VARCHAR(128) NOT NULL ,
  PRIMARY KEY (`alias`) ,
  UNIQUE INDEX `alias_name_UNIQUE` (`alias_name` ASC) 
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COLLATE = utf8_general_ci;

CREATE TABLE `props_alias_domain` (
  `alias` INT NOT NULL AUTO_INCREMENT ,
  `alias_name` VARCHAR(128) NOT NULL ,
  PRIMARY KEY (`alias`) ,
  UNIQUE INDEX `alias_name_UNIQUE` (`alias_name` ASC) 
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COLLATE = utf8_general_ci;

CREATE TABLE `props_alias_name` (
  `alias` INT NOT NULL AUTO_INCREMENT ,
  `alias_name` VARCHAR(255) NOT NULL ,
  PRIMARY KEY (`alias`) ,
  UNIQUE INDEX `alias_name_UNIQUE` (`alias_name` ASC) 
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COLLATE = utf8_general_ci;

CREATE  TABLE `props_values` (
  `app_id` INT NOT NULL ,
  `domain_id` INT NOT NULL ,
  `subject_id` VARCHAR(45) NOT NULL ,
  `name_id` INT NOT NULL ,
  `value` VARCHAR(255) NULL ,
  PRIMARY KEY (`app_id`, `domain_id`, `subject_id`, `name_id`) 
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COLLATE = utf8_general_ci;

ALTER TABLE `props_values` 
  ADD CONSTRAINT `fk_prop_appId` FOREIGN KEY (`app_id` ) REFERENCES `props_alias_app` (`alias` ) ON DELETE CASCADE ON UPDATE CASCADE, 
  ADD CONSTRAINT `fk_prop_domainId` FOREIGN KEY (`domain_id` ) REFERENCES `props_alias_domain` (`alias` ) ON DELETE CASCADE ON UPDATE CASCADE, 
  ADD CONSTRAINT `fk_prop_propName` FOREIGN KEY (`name_id` ) REFERENCES `props_alias_name` (`alias` ) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD INDEX `fk_prop_appId` (`app_id` ASC), 
  ADD INDEX `fk_prop_domainId` (`domain_id` ASC), 
  ADD INDEX `fk_prop_propName` (`name_id` ASC) ;


