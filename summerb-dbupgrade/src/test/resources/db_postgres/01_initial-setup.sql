
-- some comment, followed by multi-line statement
CREATE  TABLE upg_pet (
-- another comment within statement
  field1 VARCHAR(35) NOT NULL -- another comment after the statement, followed by empty line

);

/* multiline comment in single line */
INSERT INTO upg_pet (field1) VALUES ('1 --' /*embedded comment*/ );
-- should be no problem with next line
INSERT INTO upg_pet (field1) VALUES ('2 /*asd*/' /*embedded comment*/ );

/* multiline comment 
    in 
    multiple
    lines */
INSERT INTO upg_pet (field1) VALUES ('3 /* -- */' /*embedded comment*/ );

-- Note that in Postgress string literal marker is escaped by duplicating string literal marker
INSERT INTO upg_pet (field1) VALUES ('4 /* '' */' /* -- */ );

