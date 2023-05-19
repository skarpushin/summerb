CREATE  TABLE forms_test_1 (
  id CHAR(36) NOT NULL ,
  env VARCHAR(45) NOT NULL ,
  active BOOLEAN NOT NULL ,
  major_version INT NOT NULL,
  minor_version INT NOT NULL,
  created_at BIGINT NOT NULL ,
  modified_at BIGINT NOT NULL ,
  created_by VARCHAR(36) NOT NULL ,
  modified_by VARCHAR(36) NOT NULL ,
  link_to_full_donwload VARCHAR(512) NOT NULL ,
  link_to_patch_to_next_version VARCHAR(512) NULL ,
  PRIMARY KEY (id) 
);

CREATE SEQUENCE forms_test_2_seq;

CREATE  TABLE forms_test_2 (
  id INT NOT NULL DEFAULT NEXTVAL ('forms_test_2_seq') ,
  env VARCHAR(45) NOT NULL ,
  active BOOLEAN NOT NULL ,
  major_version INT NOT NULL,
  minor_version INT NOT NULL,
  created_at BIGINT NOT NULL ,
  modified_at BIGINT NOT NULL ,
  created_by VARCHAR(36) NOT NULL ,
  modified_by VARCHAR(36) NOT NULL ,
  link_to_full_donwload VARCHAR(512) NOT NULL ,
  link_to_patch_to_next_version VARCHAR(512) NULL ,
  PRIMARY KEY (id) 
);

CREATE  TABLE forms_test_3 (
  id CHAR(36) NOT NULL ,
  link_To_Dto_One_Optional VARCHAR(36) NULL ,
  link_To_Dto_Two BIGINT NOT NULL ,
  link_To_Dto_Two_Optional BIGINT NULL ,
  link_To_Self_Optional VARCHAR(36) NULL ,
  PRIMARY KEY (id) 
);

CREATE SEQUENCE forms_mtom_seq;

CREATE  TABLE forms_mtom (
  id INT NOT NULL DEFAULT NEXTVAL ('forms_mtom_seq') ,
  src BIGINT NOT NULL ,
  dst VARCHAR(36) NOT NULL ,
  PRIMARY KEY (id) 
);
