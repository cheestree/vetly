START TRANSACTION;

CREATE SCHEMA IF NOT EXISTS vetly;

CREATE TABLE vetly.user (
    id SERIAL PRIMARY KEY,
    uid VARCHAR(64) NOT NULL UNIQUE,
    name VARCHAR(64) NOT NULL,
    email VARCHAR(128) NOT NULL UNIQUE,
    imageUrl TEXT,
    phone INT UNIQUE,
    birth TIMESTAMP
);

CREATE TABLE vetly.member (
    id INT PRIMARY KEY REFERENCES vetly.user(id) ON DELETE CASCADE
);

CREATE TABLE vetly.admin (
    id INT PRIMARY KEY REFERENCES vetly.user(id) ON DELETE CASCADE
);

CREATE TABLE vetly.veterinarian (
    nRegister VARCHAR(16) UNIQUE NOT NULL,
    id INT PRIMARY KEY REFERENCES vetly.member(id) ON DELETE CASCADE
);

CREATE TABLE vetly.animal (
    id SERIAL PRIMARY KEY,
    name VARCHAR(32) NULL,
    chip VARCHAR(32) UNIQUE NULL,
    breed VARCHAR(32) NULL,
    birth TIMESTAMP NULL,
    imageUrl TEXT
);

CREATE TABLE vetly.pet (
    id INT PRIMARY KEY REFERENCES vetly.animal(id),
    ownerId INT REFERENCES vetly.member(id) NOT NULL
);

CREATE TABLE vetly.clinic (
    id SERIAL PRIMARY KEY,
    nif VARCHAR(16) UNIQUE NOT NULL,
    name VARCHAR(32) NOT NULL,
    address VARCHAR(128) NOT NULL,
    long DECIMAL(9,6) NOT NULL,
    lat DECIMAL(9,6) NOT NULL,
    phone VARCHAR(16) NOT NULL,
    email VARCHAR(128) NOT NULL UNIQUE,
    imageUrl TEXT,
    ownerId INT REFERENCES vetly.user(id) NULL
);
CREATE TABLE vetly.part_of (
    joinedIn TIMESTAMP NOT NULL,
    leftIn TIMESTAMP NULL,
    vetId INT REFERENCES vetly.veterinarian(id) ON DELETE CASCADE NOT NULL,
    clinicId INT REFERENCES vetly.clinic(id) ON DELETE CASCADE NOT NULL,
    PRIMARY KEY (vetId, clinicId)
);

CREATE TABLE vetly.checkup (
    id SERIAL PRIMARY KEY,
    description VARCHAR(512) NOT NULL,
    dateTime TIMESTAMP NOT NULL,
    missed BOOLEAN DEFAULT FALSE,
    animalId INT REFERENCES vetly.animal(id) ON DELETE CASCADE NOT NULL,
    vetId INT REFERENCES vetly.veterinarian(id) ON DELETE CASCADE NOT NULL,
    clinicId INT REFERENCES vetly.clinic(id) ON DELETE CASCADE NOT NULL
);

CREATE TABLE vetly.guide (
    id SERIAL PRIMARY KEY,
    imageUrl TEXT,
    name VARCHAR(32) NOT NULL,
    description VARCHAR(256) NOT NULL,
    text TEXT NOT NULL,
    vetId INT REFERENCES vetly.veterinarian(id) ON DELETE CASCADE NOT NULL
);

COMMIT;