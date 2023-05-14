
CREATE TABLE `props_alias_app` (
  `alias` int(11) NOT NULL AUTO_INCREMENT,
  `alias_name` varchar(128) NOT NULL,
  PRIMARY KEY (`alias`),
  UNIQUE KEY `alias_name_UNIQUE` (`alias_name`)
) ENGINE=InnoDB AUTO_INCREMENT=464 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;

CREATE TABLE `props_alias_domain` (
  `alias` int(11) NOT NULL AUTO_INCREMENT,
  `alias_name` varchar(128) NOT NULL,
  PRIMARY KEY (`alias`),
  UNIQUE KEY `alias_name_UNIQUE` (`alias_name`)
) ENGINE=InnoDB AUTO_INCREMENT=603 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;

CREATE TABLE `props_alias_name` (
  `alias` int(11) NOT NULL AUTO_INCREMENT,
  `alias_name` varchar(255) NOT NULL,
  PRIMARY KEY (`alias`),
  UNIQUE KEY `alias_name_UNIQUE` (`alias_name`)
) ENGINE=InnoDB AUTO_INCREMENT=2940 DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;

CREATE TABLE `props_values` (
  `app_id` int(11) NOT NULL,
  `domain_id` int(11) NOT NULL,
  `subject_id` varchar(45) NOT NULL,
  `name_id` int(11) NOT NULL,
  `value` varchar(1024) DEFAULT NULL,
  PRIMARY KEY (`app_id`,`domain_id`,`subject_id`,`name_id`),
  KEY `fk_prop_appId` (`app_id`),
  KEY `fk_prop_domainId` (`domain_id`),
  KEY `fk_prop_propName` (`name_id`),
  CONSTRAINT `fk_prop_appId` FOREIGN KEY (`app_id`) REFERENCES `props_alias_app` (`alias`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_prop_domainId` FOREIGN KEY (`domain_id`) REFERENCES `props_alias_domain` (`alias`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_prop_propName` FOREIGN KEY (`name_id`) REFERENCES `props_alias_name` (`alias`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3 COLLATE=utf8mb3_general_ci;
