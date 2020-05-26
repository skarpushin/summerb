
-- some comment, followed by multi-line statement
CREATE  TABLE `upg_pet` (
-- another comment within statement
  `field1` VARCHAR(35) NOT NULL -- another comment after the statement, followed by empty line

) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8 COLLATE = utf8_general_ci;

/* multiline comment in single line */
INSERT INTO `upg_pet` (`field1`) VALUES ('1 --' /*embedded comment*/ );
-- sholud be no problem with next line
INSERT INTO `upg_pet` (`field1`) VALUES ('2 /*asd*/' /*embedded comment*/ );

/* multiline comment 
    in 
    multiple
    lines */
INSERT INTO `upg_pet` (`field1`) VALUES ('3 /* -- */' /*embedded comment*/ );
INSERT INTO `upg_pet` (`field1`) VALUES ('4 /* \' */' /* -- */ );

