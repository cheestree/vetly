START TRANSACTION;

INSERT INTO vetly.account (name, email, image_url, birth, auth, auth_id, hash, phone)
VALUES
    ('Alice Smith', 'alice@example.com', 'https://example.com/alice.jpg', '1990-05-15', 'google', 'auth_google_1', NULL, '1234567890'),
    ('Bob Johnson', 'bob@example.com', 'https://example.com/bob.jpg', '1985-08-22', 'password', 'auth_pw_1', 'hashed_pw_1', '0987654321');

INSERT INTO vetly.member (id)
VALUES
    (1),
    (2);

INSERT INTO vetly.admin (id)
VALUES
    (2);

INSERT INTO vetly.veterinarian (n_register, id)
VALUES
    ('VET12345', 1);

INSERT INTO vetly.animal (image_url, chip, breed, birth, owner_id)
VALUES
   ('https://example.com/dog1.jpg', 'CHIP123', 'Labrador', '2020-06-01', 1),
   ('https://example.com/cat1.jpg', 'CHIP456', 'Siamese', '2019-03-12', 2);


INSERT INTO vetly.clinic (nif, image_url, name, address, long, lat, phone, email, owner_id)
VALUES
    ('CLINIC001', 'https://example.com/clinic1.jpg', 'Happy Paws Vet', '123 Pet St, Petville', -75.123456, 40.654321, '5551112233', 'clinic@example.com', 2);

INSERT INTO vetly.part_of (joined_in, vet_id, clinic_id)
VALUES
    ('2023-01-01', 'VET12345', 'CLINIC001');

INSERT INTO vetly.checkup (description, date_time, vet_id, clinic_id)
VALUES
    ('Routine vaccination and checkup', '2024-02-15 10:00:00', 'VET12345', 'CLINIC001');

INSERT INTO vetly.guide (image_url, name, description, text, vet_id)
VALUES
    ('https://example.com/guide1.jpg', 'Dog Care 101', 'Basic dog care tips', 'Make sure to feed your dog quality food...', 'VET12345');

END;