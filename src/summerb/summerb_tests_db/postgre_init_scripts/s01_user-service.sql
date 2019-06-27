-- CREATE SCHEMA `summerb_tests` DEFAULT CHARACTER SET utf8 ;
-- Migration from MySQL hints: https://en.wikibooks.org/wiki/Converting_MySQL_to_PostgreSQL

CREATE  TABLE users (
  uuid CHAR(36) NOT NULL ,
  display_name VARCHAR(45) NOT NULL ,
  email VARCHAR(45) NOT NULL ,
  time_zone VARCHAR(20) NOT NULL DEFAULT 'GMT+0' ,
  locale VARCHAR(8) NOT NULL DEFAULT 'en_US' ,
  registered_at BIGINT NOT NULL ,
  is_blocked BOOLEAN NOT NULL DEFAULT false ,
  integration_data VARCHAR(45) NULL ,
  PRIMARY KEY (uuid) ,
  CONSTRAINT users_uuid_UNIQUE UNIQUE  (uuid) ,
  CONSTRAINT email_UNIQUE UNIQUE  (email)
);

CREATE INDEX idxDisplayName ON users (display_name);

CREATE  TABLE users_passwords (
  user_uuid CHAR(36) NOT NULL ,
  password_hash VARCHAR(160) NULL ,
  restoration_token CHAR(36) NULL ,
  PRIMARY KEY (user_uuid) ,
  CONSTRAINT users_passwords_uuid_UNIQUE UNIQUE  (user_uuid) 
);

CREATE  TABLE users_auth_tokens (
  uuid VARCHAR(36) NOT NULL ,
  token_value VARCHAR(36) NOT NULL ,
  user_uuid CHAR(36) NOT NULL ,
  created_at BIGINT NOT NULL ,
  expires_at BIGINT NOT NULL ,
  last_verified_at BIGINT NOT NULL ,
  client_ip VARCHAR(39) NULL ,
  PRIMARY KEY (uuid) ,
  CONSTRAINT users_auth_tokens_UNIQUE UNIQUE  (uuid)
);

CREATE INDEX idx_expires_at ON users_auth_tokens (expires_at);
CREATE INDEX idx_user ON users_auth_tokens (user_uuid);

CREATE  TABLE users_permissions (
  domain_name VARCHAR(45) NOT NULL ,
  subject_id VARCHAR(45) NOT NULL ,
  user_uuid VARCHAR(45) NOT NULL ,
  permission_key VARCHAR(45) NOT NULL ,
  PRIMARY KEY (domain_name, subject_id, user_uuid, permission_key) 
);
