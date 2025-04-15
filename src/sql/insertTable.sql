START TRANSACTION;

--  Priority 1

INSERT INTO vetly.users (username, uuid, email, image_url, birth_date, uid, phone, roles)
VALUES
    ('Alice Smith',  '8b11690b-1ad8-45ab-b8a4-1c026361fbdb', 'alice@example.com', 'https://example.com/alice.jpg', '1990-05-15', '12345', '1234567890', ARRAY['VETERINARIAN']),
    ('Bob Johnson',  '726c6b2d-662b-4e9e-9e50-a26caadf8c2f','bob@example.com', 'https://example.com/bob.jpg', '1985-08-22', '123456789', '0987654321', ARRAY['ADMIN']);

INSERT INTO vetly.roles (id, role, description)
VALUES
    (1, 'ADMIN', 'System admin'),
    (2, 'VETERINARIAN', 'Veterinarian of a clinic');

INSERT INTO vetly.admin (id)
VALUES
    (2);

INSERT INTO vetly.veterinarian (n_register, id)
VALUES
    ('VET12345', 1);

INSERT INTO vetly.user_roles (user_id, role_id)
VALUES
    (1, 1),
    (1, 2);

INSERT INTO vetly.animal (name, image_url, microchip, species, birth_date, owner_id)
VALUES
   ('Spaghetti', 'https://example.com/dog1.jpg', 'CHIP123', 'Labrador', '2020-06-01', null),
   ('Fettuccine', 'https://example.com/cat1.jpg', 'CHIP456', 'Siamese', '2019-03-12', 2);

INSERT INTO vetly.clinic (nif, name, address, lng, lat, phone, email)
VALUES
    ('123456789', 'VetCare Clinic', '123 Main St', -9.1399, 38.7169, '+351912345678', 'contact@vetcare.com'),
    ('987654321', 'Animal Health Center', '456 Elm St', -8.6121, 41.1496, '+351967891234', 'info@animalhealth.com');

END;

-- Priority 2

START TRANSACTION;

INSERT INTO vetly.clinic_membership (joined_in, veterinarian_id, clinic_id)
VALUES
    ('2023-01-01', 1, 1);

INSERT INTO vetly.checkup (description, uuid, date_time, animal_id, veterinarian_id, clinic_id)
VALUES
    ('Routine vaccination and checkup', '0d6ed34c-a2dc-4935-867c-c829d8168e4d', '2024-02-15 10:00:00', 2,1, 1);

INSERT INTO vetly.checkup_files (uuid, url, description, checkup_id)
VALUES
    ('0d3ed34c-a2dc-4935-867c-c829d8168e4d', 'https://example.com/file1.pdf', 'Test file 1', 1),
    ('0d3ed34c-a2dc-4935-867c-c829d8168e4e', 'https://example.com/file2.pdf', 'Test file 2', 1);

INSERT INTO vetly.guide (image_url, title, description, text, veterinarian_id)
VALUES
    ('https://example.com/guide1.jpg', 'Dog Care 101', 'Basic dog care tips', 'Make sure to feed your dog quality food...', 1);

END;

-- Priority 3

START TRANSACTION;

INSERT INTO vetly.medical_supply (name, description, image_url, supply_type)
VALUES
    ('Pain Relief Pills', 'Used for general pain relief.', 'https://example.com/pain_pills.jpg', 'pill'),
    ('Antibiotic Liquid', 'Broad-spectrum antibiotic.', 'https://example.com/antibiotic.jpg', 'liquid'),
    ('Rabies Vaccine', 'Rabies vaccine for pets.', 'https://example.com/rabies_vaccine.jpg', 'shot');

INSERT INTO vetly.pill_supply (id, pills_per_box, mg_per_pill)
VALUES
    (1, 20, 500.00);

INSERT INTO vetly.liquid_supply (id, ml_per_bottle, ml_dose_per_use)
VALUES
    (2, 100.00, 5.00);

INSERT INTO vetly.shot_supply (id, vials_per_box, ml_per_vial)
VALUES
    (3, 5, 1.50);

INSERT INTO vetly.medical_supply_clinic (clinic_id, medical_supply_id, price, quantity)
VALUES
    (1, 1, 9.99, 50);

INSERT INTO vetly.medical_supply_clinic (clinic_id, medical_supply_id, price, quantity)
VALUES
    (1, 2, 15.50, 30),
    (2, 2, 16.00, 40);

INSERT INTO vetly.medical_supply_clinic (clinic_id, medical_supply_id, price, quantity)
VALUES
    (2, 3, 25.00, 20);

END;