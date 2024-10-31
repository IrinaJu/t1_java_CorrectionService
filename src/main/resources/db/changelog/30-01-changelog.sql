-- liquibase formatted sql

-- changeset e_cha:1727702549313-1
CREATE SEQUENCE IF NOT EXISTS role_seq START WITH 1 INCREMENT BY 50;

-- changeset e_cha:1727702549313-2
CREATE SEQUENCE IF NOT EXISTS transaction_seq START WITH 1 INCREMENT BY 50;

-- changeset e_cha:1727702549313-3
CREATE SEQUENCE IF NOT EXISTS users_seq START WITH 1 INCREMENT BY 50;

-- changeset e_cha:1727702549313-4
CREATE TABLE role
(
    id   BIGINT NOT NULL,
    name VARCHAR(20),
    CONSTRAINT pk_role PRIMARY KEY (id)
);

-- changeset e_cha:1727702549313-5
CREATE TABLE transaction
(
    id        BIGINT NOT NULL,
    amount    DECIMAL(19, 2),
    client_id BIGINT,
    CONSTRAINT pk_transaction PRIMARY KEY (id)
);

-- changeset e_cha:1727702549313-6
CREATE TABLE user_roles
(
    role_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    CONSTRAINT pk_user_roles PRIMARY KEY (role_id, user_id)
);

-- changeset e_cha:1727702549313-7
CREATE TABLE users
(
    id       BIGINT NOT NULL,
    login    VARCHAR(20),
    email    VARCHAR(50),
    password VARCHAR(120),
    CONSTRAINT pk_users PRIMARY KEY (id)
);

-- changeset e_cha:1727702549313-8
ALTER TABLE users
    ADD CONSTRAINT uc_74165e195b2f7b25de690d14a UNIQUE (email);

-- changeset e_cha:1727702549313-9
ALTER TABLE users
    ADD CONSTRAINT uc_f8d2576e807e2b20b506bf6a3 UNIQUE (login);

-- changeset e_cha:1727702549313-10
ALTER TABLE user_roles
    ADD CONSTRAINT fk_userol_on_role FOREIGN KEY (role_id) REFERENCES role (id);

-- changeset e_cha:1727702549313-11
ALTER TABLE user_roles
    ADD CONSTRAINT fk_userol_on_user FOREIGN KEY (user_id) REFERENCES users (id);

-- changeset e_cha:1727702549313-12
CREATE TABLE account
(
 id BIGINT NOT NULL,
 client_id BIGINT,
 account_type VARCHAR(10),
 balance DECIMAL(19, 2),
 CONSTRAINT pk_account PRIMARY KEY (id)

);

-- changeset e_cha:1727702549313-13
ALTER TABLE transaction
    ADD COLUMN account_id BIGINT,
    ADD CONSTRAINT fk_transaction_on_account FOREIGN KEY (account_id) REFERENCES account (id);

-- changeset e_cha:1727702549313-14
CREATE TABLE data_source_error_log
(
    id               BIGINT NOT NULL,
    stack_trace      TEXT,
    message           TEXT,
    method_signature VARCHAR(255),
    error_time        TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT pk_data_source_error_log PRIMARY KEY (id)
);

-- changeset e_cha:1727702549313-15
CREATE TABLE time_limit_exceed_log
(
    id                     BIGINT NOT NULL,
    method_signature       VARCHAR(255),
    execution_time         DECIMAL(10, 6),
    log_time               TIMESTAMP NOT NULL DEFAULT now(),
    CONSTRAINT pk_time_limit_exceed_log PRIMARY KEY (id)
);

