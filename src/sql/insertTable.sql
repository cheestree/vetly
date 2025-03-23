START TRANSACTION;

INSERT INTO vetly.users (name, uuid, email, image_url, birth, uid, phone, roles)
VALUES
    ('Alice Smith',  '8b11690b-1ad8-45ab-b8a4-1c026361fbdb', 'alice@example.com', 'https://example.com/alice.jpg', '1990-05-15', '12345', '1234567890', ARRAY['VETERINARIAN']),
    ('Bob Johnson',  '726c6b2d-662b-4e9e-9e50-a26caadf8c2f','bob@example.com', 'https://example.com/bob.jpg', '1985-08-22', '123456789', '0987654321', ARRAY['ADMIN']);

INSERT INTO vetly.admin (id)
VALUES
    (2);

INSERT INTO vetly.veterinarian (n_register, id)
VALUES
    ('VET12345', 1);

INSERT INTO vetly.animal (name, image_url, chip, breed, birth, owner_id)
VALUES
   ('Spaghetti', 'https://example.com/dog1.jpg', 'CHIP123', 'Labrador', '2020-06-01', null),
   ('Fettuccine', 'https://example.com/cat1.jpg', 'CHIP456', 'Siamese', '2019-03-12', 2);

INSERT INTO vetly.clinic (nif, uuid, image_url, name, address, long, lat, phone, email, owner_id)
VALUES
    ('CLINIC001',  '8f485104-9699-4871-a1cf-8db93d8400dc','https://example.com/clinic1.jpg', 'Happy Paws Vet', '123 Pet St, Petville', -75.123456, 40.654321, '5551112233', 'clinic@example.com', 2);

END;

START TRANSACTION;

INSERT INTO vetly.part_of (joined_in, veterinarian_id, clinic_id)
VALUES
    ('2023-01-01', 1, 1);

INSERT INTO vetly.checkup (description, uuid, date_time, animal_id, veterinarian_id, clinic_id)
VALUES
    ('Routine vaccination and checkup', '0d6ed34c-a2dc-4935-867c-c829d8168e4d', '2024-02-15 10:00:00', 2,1, 1);

INSERT INTO vetly.guide (image_url, name, description, text, veterinarian_id)
VALUES
    ('https://example.com/guide1.jpg', 'Dog Care 101', 'Basic dog care tips', 'Make sure to feed your dog quality food...', 1);

END;