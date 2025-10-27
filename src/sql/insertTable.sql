START TRANSACTION;

--  Priority 1

INSERT INTO vetly.users (username, public_id, email, image, birth_date, uid, phone, roles)
VALUES
    ('Alice Smith',  '8b11690b-1ad8-45ab-b8a4-1c026361fbdb', 'alice@example.com', 'https://example.com/alice.jpg', '1990-05-15', '12345', '1234567890', ARRAY['VETERINARIAN']),
    ('Bob Johnson',  '726c6b2d-662b-4e9e-9e50-a26caadf8c2f','bob@example.com', 'https://example.com/bob.jpg', '1985-08-22', '123456789', '0987654321', ARRAY['ADMIN']),
('Admin',  '5023a81d-48b5-407e-a440-22a2ec4408a0','admin@gmail.com', 'https://example.com/bob.jpg', '1985-08-22', 'Nwcuqg4iJfgVTY34f0IAt6IT6yr2', '0988654321', ARRAY['ADMIN']);

INSERT INTO vetly.roles (id, role, description)
VALUES
    (1, 'ADMIN', 'System admin'),
    (2, 'VETERINARIAN', 'Veterinarian of a clinic');

INSERT INTO vetly.admins (id)
VALUES
    (2);

INSERT INTO vetly.veterinarians (n_register, id)
VALUES
    ('VET12345', 1);

INSERT INTO vetly.user_roles (user_id, role_id)
VALUES
    (1, 1),
    (1, 2),
    (3, 1);

INSERT INTO vetly.animals (name, microchip, species, birth_date, owner_id)
VALUES
   ('Amara', 'CHIP123', 'Beans', '2020-06-01', null),
   ('Carbono', 'CHIP456', 'Darkness', '2019-03-12', 2),
   ('Kiki', 'CHIP789', 'Feisty', '2019-06-01', null),
   ('Thor', 'CHIP012', 'God', '2018-06-01', null),
   ('Baby Grey', 'CHIP345', 'Zoomies', '2015-06-01', null),
   ('Lua', 'CHPI345', 'Astro', '2015-06-01', null),
   ('Atena', 'CHPI34', 'God', '2015-06-01', null),
   ('Midas', 'CPI345', 'God', '2015-06-01', null),
   ('Apollo', 'HPI345', 'God', '2015-06-01', null),
   ('Hera', 'CHP45', 'God', '2015-06-01', null),
   ('Dusty Fella', 'CHIP678', 'Sleepy', '2016-06-01', null);

INSERT INTO vetly.clinics (nif, name, address, longitude, latitude, phone, email)
VALUES
    ('123456789', 'VetCare Clinic', '123 Main St', -9.1399, 38.7169, '+351912345678', 'contact@vetcare.com'),
    ('987654321', 'Animal Health Center', '456 Elm St', -8.6121, 41.1496, '+351967891234', 'info@animalhealth.com');

INSERT INTO vetly.clinic_opening_hours (clinic_id, weekday, opens_at, closes_at)
VALUES
    (1, 0, '09:00', '18:00'),
    (1, 1, '09:00', '18:00'),
    (1, 2, '09:00', '18:00'),
    (1, 3, '09:00', '18:00'),
    (1, 4, '09:00', '18:00'),
    (2, 0, '09:00', '18:00'),
    (2, 1, '09:00', '18:00');

INSERT INTO vetly.clinic_services (clinic_id, service)
VALUES
    (1, 'VACCINATION'),
    (1, 'SURGERY'),
    (1, 'CHECKUP'),
    (1, 'EMERGENCY'),
    (2, 'VACCINATION'),
    (2, 'GROOMING'),
    (2, 'DENTISTRY');

END;

-- Priority 2

START TRANSACTION;

INSERT INTO vetly.clinic_memberships (left_in, veterinarian_id, clinic_id)
VALUES
    ('2023-01-01', 1, 1);

INSERT INTO vetly.checkups (title, description, date_time, animal_id, veterinarian_id, clinic_id)
VALUES
    ('Routine', 'Routine vaccination and checkup', '2024-02-15 10:00:00', 2,1, 1);


INSERT INTO vetly.guides (image_url, title, description, content, veterinarian_id)
VALUES
    ('https://example.com/guide1.jpg', 'Dog Care 101', 'Basic dog care tips', 'Make sure to feed your dog quality food...', 1);

END;

-- Priority 3

START TRANSACTION;

INSERT INTO vetly.medical_supplies (name, description, type)
VALUES
    ('Pain Relief Pills', 'Used for general pain relief.', 'PILL'),
    ('Antibiotic Liquid', 'Broad-spectrum antibiotic.', 'LIQUID'),
    ('Rabies Vaccine', 'Rabies vaccine for pets.', 'SHOT');

INSERT INTO vetly.pill_supplies (id, pills_per_box, mg_per_pill)
VALUES
    (1, 20, 500.00);

INSERT INTO vetly.liquid_supplies (id, ml_per_bottle, ml_dose_per_use)
VALUES
    (2, 100.00, 5.00);

INSERT INTO vetly.shot_supplies (id, vials_per_box, ml_per_vial)
VALUES
    (3, 5, 1.50);

INSERT INTO vetly.medical_supplies_clinics (clinic_id, medical_supply_id, price, quantity)
VALUES
    (1, 1, 9.99, 50);

INSERT INTO vetly.medical_supplies_clinics (clinic_id, medical_supply_id, price, quantity)
VALUES
    (1, 2, 15.50, 30),
    (2, 2, 16.00, 40);

INSERT INTO vetly.medical_supplies_clinics (clinic_id, medical_supply_id, price, quantity)
VALUES
    (2, 3, 25.00, 20);

END;

-- Priority 4

START TRANSACTION;
INSERT INTO vetly.files (raw_storage_path, file_name, mime_type, description, clinic_id, animal_id, checkup_id, request_id)
VALUES
    ('animals/amara.jpg', 'amara.jpg', 'image/jpeg', '', null, 1, null, null),
    ('animals/carbono.jpg', 'carbono.jpg', 'image/jpeg', '', null, 2, null, null),
    ('animals/kiki.jpg', 'kiki.jpg', 'image/jpeg', '', null, 3, null, null),
    ('animals/thor.jpg', 'thor.jpg', 'image/jpeg', '', null, 4, null, null),
    ('animals/baby_grey.jpg', 'baby_grey.jpg', 'image/jpeg', '', null, 5, null, null),
    ('animals/lua.jpg', 'lua.jpg', 'image/jpeg', '', null, 6, null, null),
    ('animals/atena.jpg', 'atena.jpg', 'image/jpeg', '', null, 7, null, null),
    ('animals/midas.jpg', 'midas.jpg', 'image/jpeg', '', null, 8, null, null),
    ('animals/apollo.jpg', 'apollo.jpg', 'image/jpeg', '', null, 9, null, null),
    ('animals/hera.jpg', 'hera.jpg', 'image/jpeg', '', null, 10, null, null),
    ('animals/dusty_fella.jpg', 'dusty_fella.jpg', 'image/jpeg', '', null, 11, null, null);
END;