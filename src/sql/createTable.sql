START TRANSACTION;

CREATE SCHEMA IF NOT EXISTS vetly;

CREATE TYPE vetly.supply_type_enum AS ENUM ('pill', 'liquid', 'shot', 'misc');

CREATE TABLE vetly.users (
    id SERIAL PRIMARY KEY,
    uuid UUID UNIQUE NOT NULL,
    uid VARCHAR(64) NOT NULL UNIQUE,
    username VARCHAR(64) NOT NULL,
    email VARCHAR(128) NOT NULL UNIQUE,
    image_url TEXT,
    phone INT UNIQUE,
    roles TEXT[],
    birth TIMESTAMP
);

CREATE TABLE vetly.admin (
    id INT PRIMARY KEY REFERENCES vetly.users(id) ON DELETE CASCADE
);

CREATE TABLE vetly.veterinarian (
    n_register VARCHAR(16) UNIQUE NOT NULL,
    id INT PRIMARY KEY REFERENCES vetly.users(id) ON DELETE CASCADE
);

CREATE TABLE vetly.animal (
    id SERIAL PRIMARY KEY,
    name VARCHAR(32) NULL,
    chip VARCHAR(32) UNIQUE NULL,
    breed VARCHAR(32) NULL,
    birth TIMESTAMP NULL,
    image_url TEXT NULL,
    owner_id INT REFERENCES vetly.users(id)
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
    joined_in TIMESTAMP WITH TIME ZONE NOT NULL,
    left_in TIMESTAMP WITH TIME ZONE NULL,
    veterinarian_id INT REFERENCES vetly.veterinarian(id) ON DELETE CASCADE NOT NULL,
    clinic_id INT REFERENCES vetly.clinic(id) ON DELETE CASCADE NOT NULL,
    PRIMARY KEY (veterinarian_id, clinic_id)
);

CREATE TABLE vetly.checkup (
    id SERIAL PRIMARY KEY,
    uuid UUID UNIQUE NOT NULL,
    description VARCHAR(512) NOT NULL,
    date_time TIMESTAMP WITH TIME ZONE NOT NULL,
    missed BOOLEAN DEFAULT FALSE,
    animal_id INT REFERENCES vetly.animal(id) ON DELETE CASCADE NOT NULL,
    veterinarian_id INT REFERENCES vetly.veterinarian(id) ON DELETE CASCADE NOT NULL,
    clinic_id INT REFERENCES vetly.clinic(id) ON DELETE CASCADE NOT NULL
);

CREATE TABLE vetly.checkup_files (
    id SERIAL PRIMARY KEY,
    uuid UUID NOT NULL UNIQUE,
    url TEXT NOT NULL,
    description TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT now(),
    checkup_id INT REFERENCES vetly.checkup(id) ON DELETE CASCADE
);

CREATE TABLE vetly.guide (
    id SERIAL PRIMARY KEY,
    image_url TEXT,
    title VARCHAR(32) NOT NULL,
    description VARCHAR(256) NOT NULL,
    text TEXT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT now(),
    modified_at TIMESTAMP DEFAULT NULL,
    veterinarian_id INT REFERENCES vetly.veterinarian(id) ON DELETE CASCADE NOT NULL
);

CREATE TABLE vetly.medical_supply (
    id SERIAL PRIMARY KEY,
    uuid UUID UNIQUE NOT NULL DEFAULT gen_random_uuid(),
    name VARCHAR(64) NOT NULL,
    description TEXT,
    image_url TEXT,
    supply_type vetly.supply_type_enum NOT NULL
);

CREATE TABLE vetly.medical_supply_clinic (
    clinic_id INT REFERENCES vetly.clinic(id) ON DELETE CASCADE,
    medical_supply_id INT REFERENCES vetly.medical_supply(id) ON DELETE CASCADE,
    price DECIMAL(10,2) NOT NULL,
    count INT NOT NULL,
    PRIMARY KEY (clinic_id, medical_supply_id)
);

CREATE TABLE vetly.pill_supply (
    id INT PRIMARY KEY REFERENCES vetly.medical_supply(id) ON DELETE CASCADE,
    pills_per_box INT NOT NULL,
    mg_per_pill DECIMAL(5,2) NOT NULL
);

CREATE TABLE vetly.liquid_supply (
    id INT PRIMARY KEY REFERENCES vetly.medical_supply(id) ON DELETE CASCADE,
    ml_per_bottle DECIMAL(6,2) NOT NULL,
    ml_dose_per_use DECIMAL(6,2) NOT NULL
);

CREATE TABLE vetly.shot_supply (
    id INT PRIMARY KEY REFERENCES vetly.medical_supply(id) ON DELETE CASCADE,
    vials_per_box INT NOT NULL,
    ml_per_vial DECIMAL(6,2) NOT NULL
);

COMMIT;