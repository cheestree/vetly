CREATE SCHEMA IF NOT EXISTS vetly;

CREATE TYPE vetly.supply_type AS ENUM ('PILL', 'LIQUID', 'SHOT', 'MISC');
CREATE TYPE vetly.service_type AS ENUM (
    'VACCINATION',
    'SURGERY',
    'DENTISTRY',
    'GROOMING',
    'CHECKUP',
    'EMERGENCY'
);

CREATE TABLE vetly.base_table (
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL
);

CREATE TABLE vetly.users (
    id SERIAL PRIMARY KEY,
    public_id UUID UNIQUE NOT NULL,
    uid VARCHAR(64) NOT NULL UNIQUE,
    username VARCHAR(64) NOT NULL,
    email VARCHAR(128) NOT NULL UNIQUE,
    image TEXT,
    phone VARCHAR(20) UNIQUE,
    roles TEXT[],
    birth_date TIMESTAMP
) INHERITS (vetly.base_table);

CREATE TABLE vetly.requests (
    id UUID PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES vetly.users(id),
    action VARCHAR(50) NOT NULL,
    target VARCHAR(50) NOT NULL,
    justification TEXT,
    status VARCHAR(32) CHECK (status IN ('APPROVED', 'REJECTED', 'PENDING')) DEFAULT 'PENDING',
    extra_data JSONB
) INHERITS (vetly.base_table);

CREATE TABLE vetly.roles (
    id INT PRIMARY KEY,
    role VARCHAR(32) NOT NULL UNIQUE,
    description TEXT NULL
);

CREATE TABLE vetly.user_roles (
    user_id INT NOT NULL,
    role_id INT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES vetly.users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES vetly.roles(id) ON DELETE CASCADE
) INHERITS (vetly.base_table);

CREATE TABLE vetly.admins (
    id INT PRIMARY KEY,
    FOREIGN KEY (id) REFERENCES vetly.roles(id) ON DELETE CASCADE
);

CREATE TABLE vetly.veterinarians (
    id INT PRIMARY KEY,
    n_register VARCHAR(16) UNIQUE NOT NULL,
    FOREIGN KEY (id) REFERENCES vetly.roles(id) ON DELETE CASCADE
);

CREATE TABLE vetly.clinics (
    id SERIAL PRIMARY KEY,
    nif VARCHAR(16) UNIQUE NOT NULL,
    name VARCHAR(32) NOT NULL,
    address VARCHAR(128) NOT NULL,
    longitude DECIMAL(9,6) NOT NULL,
    latitude DECIMAL(9,6) NOT NULL,
    phone VARCHAR(16) NOT NULL,
    email VARCHAR(128) NOT NULL UNIQUE,
    is_active BOOLEAN DEFAULT TRUE,
    owner_id INT REFERENCES vetly.users(id) NULL
) INHERITS (vetly.base_table);

CREATE TABLE vetly.clinic_services (
    clinic_id INT REFERENCES vetly.clinics(id) ON DELETE CASCADE,
    service vetly.service_type NOT NULL,
    PRIMARY KEY (clinic_id, service)
);

CREATE TABLE vetly.clinic_opening_hours (
    id SERIAL PRIMARY KEY,
    clinic_id INT REFERENCES vetly.clinics(id) ON DELETE CASCADE,
    weekday SMALLINT NOT NULL CHECK (weekday BETWEEN 0 AND 6),
    opens_at TIME NOT NULL,
    closes_at TIME NOT NULL,
    UNIQUE (clinic_id, weekday, opens_at, closes_at)
);

CREATE TABLE vetly.animals (
    id SERIAL PRIMARY KEY,
    name VARCHAR(32) NULL,
    microchip VARCHAR(32) UNIQUE NULL,
    sex VARCHAR(32) CHECK (sex IN ('MALE', 'FEMALE', 'UNKNOWN')) DEFAULT 'UNKNOWN',
    sterilized BOOLEAN DEFAULT FALSE,
    species VARCHAR(32) NULL,
    birth_date TIMESTAMP NULL,
    is_active BOOLEAN DEFAULT TRUE,
    owner_id INT REFERENCES vetly.users(id)
) INHERITS (vetly.base_table);

CREATE TABLE vetly.clinic_memberships (
    left_in TIMESTAMP WITH TIME ZONE NULL,
    veterinarian_id INT,
    clinic_id INT,
    FOREIGN KEY (veterinarian_id) REFERENCES vetly.users(id),
    FOREIGN KEY (clinic_id) REFERENCES vetly.clinics(id),
    PRIMARY KEY (veterinarian_id, clinic_id)
) INHERITS (vetly.base_table);

CREATE TABLE vetly.checkups (
    id SERIAL PRIMARY KEY,
    title VARCHAR(64) NOT NULL,
    description VARCHAR(128) NOT NULL,
    date_time TIMESTAMP WITH TIME ZONE NOT NULL,
    status VARCHAR(32) CHECK (status IN ('SCHEDULED', 'COMPLETED', 'MISSED', 'CANCELED')) DEFAULT 'SCHEDULED',
    notes VARCHAR(512),
    animal_id INT REFERENCES vetly.animals(id) ON DELETE CASCADE NOT NULL,
    veterinarian_id INT REFERENCES vetly.users(id) ON DELETE CASCADE NOT NULL,
    clinic_id INT REFERENCES vetly.clinics(id) ON DELETE CASCADE NOT NULL
) INHERITS (vetly.base_table);

CREATE TABLE vetly.guides (
    id SERIAL PRIMARY KEY,
    image_url TEXT,
    title VARCHAR(32) NOT NULL,
    description VARCHAR(256) NOT NULL,
    content TEXT NOT NULL,
    veterinarian_id INT REFERENCES vetly.users(id) ON DELETE CASCADE NOT NULL
) INHERITS (vetly.base_table);

CREATE TABLE vetly.medical_supplies (
    id SERIAL PRIMARY KEY,
    uuid UUID UNIQUE NOT NULL DEFAULT gen_random_uuid(),
    name VARCHAR(64) NOT NULL,
    description TEXT,
    type vetly.supply_type DEFAULT 'MISC'
);

CREATE TABLE vetly.medical_supplies_clinics (
    clinic_id INT REFERENCES vetly.clinics(id) ON DELETE CASCADE,
    medical_supply_id INT REFERENCES vetly.medical_supplies(id) ON DELETE CASCADE,
    price DECIMAL(10,2) NOT NULL,
    quantity INT NOT NULL,
    PRIMARY KEY (clinic_id, medical_supply_id)
) INHERITS (vetly.base_table);

CREATE TABLE vetly.pill_supplies (
    id INT PRIMARY KEY REFERENCES vetly.medical_supplies(id) ON DELETE CASCADE,
    pills_per_box INT NOT NULL,
    mg_per_pill DECIMAL(5,2) NOT NULL
);

CREATE TABLE vetly.liquid_supplies (
    id INT PRIMARY KEY REFERENCES vetly.medical_supplies(id) ON DELETE CASCADE,
    ml_per_bottle DECIMAL(6,2) NOT NULL,
    ml_dose_per_use DECIMAL(6,2) NOT NULL
);

CREATE TABLE vetly.shot_supplies (
    id INT PRIMARY KEY REFERENCES vetly.medical_supplies(id) ON DELETE CASCADE,
    vials_per_box INT NOT NULL,
    ml_per_vial DECIMAL(6,2) NOT NULL
);

CREATE TABLE vetly.files (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    raw_storage_path VARCHAR(50) NOT NULL,
    file_name VARCHAR(50) NOT NULL,
    mime_type VARCHAR(20) NOT NULL,
    description VARCHAR(100),
    clinic_id INT REFERENCES vetly.clinics(id),
    animal_id INT REFERENCES vetly.animals(id),
    checkup_id INT REFERENCES vetly.checkups(id),
    request_id UUID REFERENCES vetly.requests(id)
) INHERITS (vetly.base_table);