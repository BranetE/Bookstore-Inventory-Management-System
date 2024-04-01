-- liquibase formatted sql

-- changeset OlehKulbaba:1
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS books
(
    id uuid not null primary key default gen_random_uuid(),
    title    varchar(255),
    quantity integer,
    isbn     varchar(255),
    author varchar(255)
);
-- rollback DROP TABLE books