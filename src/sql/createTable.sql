START TRANSACTION;

CREATE SCHEMA IF NOT EXISTS vetly;

CREATE TABLE vetly.users (
    id SERIAL PRIMARY KEY,
    uuid UUID UNIQUE NOT NULL,
    uid VARCHAR(64) NOT NULL UNIQUE,
    name VARCHAR(64) NOT NULL,
    email VARCHAR(128) NOT NULL UNIQUE,
    image_url TEXT,
    phone INT UNIQUE,
    birth TIMESTAMP,
    role VARCHAR(16) NOT NULL
);

CREATE TABLE vetly.member (
    id INT PRIMARY KEY REFERENCES vetly.users(id) ON DELETE CASCADE
);

CREATE TABLE vetly.admin (
    id INT PRIMARY KEY REFERENCES vetly.users(id) ON DELETE CASCADE
);

CREATE TABLE vetly.veterinarian (
    n_register VARCHAR(16) UNIQUE NOT NULL,
    id INT PRIMARY KEY REFERENCES vetly.member(id) ON DELETE CASCADE
);

CREATE TABLE vetly.animal (
    id SERIAL PRIMARY KEY,
    name VARCHAR(32) NULL,
    chip VARCHAR(32) UNIQUE NULL,
    breed VARCHAR(32) NULL,
    birth TIMESTAMP NULL,
    image_url TEXT
);

CREATE TABLE vetly.pet (
    id INT PRIMARY KEY REFERENCES vetly.animal(id),
    owner_id INT REFERENCES vetly.member(id) NOT NULL
);

CREATE TABLE vetly.clinic (
    id SERIAL PRIMARY KEY,
    uuid UUID UNIQUE NOT NULL,
    nif VARCHAR(16) UNIQUE NOT NULL,
    name VARCHAR(32) NOT NULL,
    address VARCHAR(128) NOT NULL,
    long DECIMAL(9,6) NOT NULL,
    lat DECIMAL(9,6) NOT NULL,
    phone VARCHAR(16) NOT NULL,
    email VARCHAR(128) NOT NULL UNIQUE,
    image_url TEXT,
    owner_id INT REFERENCES vetly.users(id) NULL
);
CREATE TABLE vetly.part_of (
    joined_in TIMESTAMP NOT NULL,
    left_in TIMESTAMP NULL,
    veterinarian_id INT REFERENCES vetly.veterinarian(id) ON DELETE CASCADE NOT NULL,
    clinic_id INT REFERENCES vetly.clinic(id) ON DELETE CASCADE NOT NULL,
    PRIMARY KEY (veterinarian_id, clinic_id)
);

CREATE TABLE vetly.checkup (
    id SERIAL PRIMARY KEY,
    uuid UUID UNIQUE NOT NULL,
    description VARCHAR(512) NOT NULL,
    date_time TIMESTAMP NOT NULL,
    missed BOOLEAN DEFAULT FALSE,
    animal_id INT REFERENCES vetly.animal(id) ON DELETE CASCADE NOT NULL,
    veterinarian_id INT REFERENCES vetly.veterinarian(id) ON DELETE CASCADE NOT NULL,
    clinic_id INT REFERENCES vetly.clinic(id) ON DELETE CASCADE NOT NULL
);

CREATE TABLE vetly.guide (
    id SERIAL PRIMARY KEY,
    image_url TEXT,
    name VARCHAR(32) NOT NULL,
    description VARCHAR(256) NOT NULL,
    text TEXT NOT NULL,
    veterinarian_id INT REFERENCES vetly.veterinarian(id) ON DELETE CASCADE NOT NULL
);

COMMIT;