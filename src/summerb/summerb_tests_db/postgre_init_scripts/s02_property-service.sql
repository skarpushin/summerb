CREATE SEQUENCE props_alias_app_seq;

CREATE TABLE "props_alias_app" (
  "alias" INT NOT NULL DEFAULT NEXTVAL ('props_alias_app_seq') ,
  "alias_name" VARCHAR(128) NOT NULL ,
  PRIMARY KEY ("alias") ,
  CONSTRAINT "props_alias_app_UNIQUE" UNIQUE  ("alias_name") 
);

CREATE SEQUENCE props_alias_domain_seq;

CREATE TABLE "props_alias_domain" (
  "alias" INT NOT NULL DEFAULT NEXTVAL ('props_alias_domain_seq') ,
  "alias_name" VARCHAR(128) NOT NULL ,
  PRIMARY KEY ("alias") ,
  CONSTRAINT "props_alias_domain_UNIQUE" UNIQUE  ("alias_name") 
);

CREATE SEQUENCE props_alias_name_seqe;

CREATE TABLE "props_alias_name" (
  "alias" INT NOT NULL DEFAULT NEXTVAL ('props_alias_name_seqe') ,
  "alias_name" VARCHAR(255) NOT NULL ,
  PRIMARY KEY ("alias") ,
  CONSTRAINT "props_alias_name_UNIQUE" UNIQUE  ("alias_name") 
);

CREATE  TABLE "props_values" (
  "app_id" INT NOT NULL ,
  "domain_id" INT NOT NULL ,
  "subject_id" VARCHAR(45) NOT NULL ,
  "name_id" INT NOT NULL ,
  "value" VARCHAR(255) NULL ,
  PRIMARY KEY ("app_id", "domain_id", "subject_id", "name_id") 
);

CREATE INDEX "fk_prop_appId" ON "props_values" ("app_id");
CREATE INDEX "fk_prop_domainId" ON "props_values" ("domain_id");
CREATE INDEX "fk_prop_propName" ON "props_values" ("name_id");

ALTER TABLE "props_values" 
  ADD CONSTRAINT "fk_prop_appId" FOREIGN KEY ("app_id" ) REFERENCES "props_alias_app" ("alias" ) on delete cascade, 
  ADD CONSTRAINT "fk_prop_domainId" FOREIGN KEY ("domain_id" ) REFERENCES "props_alias_domain" ("alias" ) on delete cascade, 
  ADD CONSTRAINT "fk_prop_propName" FOREIGN KEY ("name_id" ) REFERENCES "props_alias_name" ("alias" ) on delete cascade;




