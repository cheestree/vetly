START TRANSACTION;

CREATE SCHEMA vetly;

CREATE TABLE vetly.account (
    id SERIAL PRIMARY KEY,
    name VARCHAR(64) NOT NULL,
    email VARCHAR(128) NOT NULL UNIQUE,
    image_url TEXT,
    birth TIMESTAMP,
    auth VARCHAR(16) NOT NULL,
    auth_id VARCHAR(64) NOT NULL UNIQUE,
    hash VARCHAR(256),
    phone VARCHAR(16) UNIQUE
);

CREATE TABLE vetly.member (
    id INT PRIMARY KEY REFERENCES vetly.account(id) ON DELETE CASCADE
);

CREATE TABLE vetly.admin (
    id INT PRIMARY KEY REFERENCES vetly.account(id) ON DELETE CASCADE
);

CREATE TABLE vetly.veterinarian (
    n_register VARCHAR(16) UNIQUE NOT NULL,
    id INT PRIMARY KEY REFERENCES vetly.member(id) ON DELETE CASCADE
);

CREATE TABLE vetly.animal (
    id SERIAL PRIMARY KEY,
    image_url TEXT,
    chip VARCHAR(32) UNIQUE NULL,
    breed VARCHAR(32) NULL,
    birth TIMESTAMP NULL,
    owner_id INT REFERENCES vetly.member(id) NULL
);

CREATE TABLE vetly.clinic (
    nif VARCHAR(16) PRIMARY KEY,
    image_url TEXT,
    name VARCHAR(32) NOT NULL,
    address VARCHAR(128) NOT NULL,
    long DECIMAL(9,6) NOT NULL,
    lat DECIMAL(9,6) NOT NULL,
    phone VARCHAR(16) NOT NULL,
    email VARCHAR(128) NOT NULL UNIQUE,
    owner_id INT REFERENCES vetly.account(id) NULL
);

CREATE TABLE vetly.part_of (
    joined_in TIMESTAMP NOT NULL,
    left_in TIMESTAMP NULL,
    vet_id VARCHAR(16) REFERENCES vetly.veterinarian(n_register) ON DELETE CASCADE NOT NULL,
    clinic_id VARCHAR(16) REFERENCES vetly.clinic(nif) ON DELETE CASCADE NOT NULL,
    PRIMARY KEY (vet_id, clinic_id)
);

CREATE TABLE vetly.checkup (
    id SERIAL PRIMARY KEY,
    description VARCHAR(512) NOT NULL,
    date_time TIMESTAMP NOT NULL,
    vet_id VARCHAR(16) REFERENCES vetly.veterinarian(n_register) ON DELETE CASCADE NOT NULL,
    clinic_id VARCHAR(16) REFERENCES vetly.clinic(nif) ON DELETE CASCADE NOT NULL
);

CREATE TABLE vetly.guide (
    id SERIAL PRIMARY KEY,
    image_url TEXT,
    name VARCHAR(32) NOT NULL,
    description VARCHAR(256) NOT NULL,
    text TEXT NOT NULL,
    vet_id VARCHAR(16) REFERENCES vetly.veterinarian(n_register) ON DELETE CASCADE NOT NULL
);

COMMIT ;