DROP TABLE IF EXISTS customer CASCADE;

CREATE TABLE customer (
    keycloak_id UUID PRIMARY KEY,
    email VARCHAR(255) UNIQUE NOT NULL,
    address TEXT,
    name VARCHAR(255) NOT NULL
);