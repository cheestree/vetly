START TRANSACTION;

INSERT INTO vetly."user" (name, email, imageUrl, birth, uid, phone)
VALUES
    ('Alice Smith', 'alice@example.com', 'https://example.com/alice.jpg', '1990-05-15', '12345', '1234567890'),
    ('Bob Johnson', 'bob@example.com', 'https://example.com/bob.jpg', '1985-08-22', '123456789', '0987654321');

INSERT INTO vetly.member (id)
VALUES
    (1),
    (2);

INSERT INTO vetly.admin (id)
VALUES
    (2);

INSERT INTO vetly.veterinarian (nregister, id)
VALUES
    ('VET12345', 1);

INSERT INTO vetly.animal (imageUrl, chip, breed, birth)
VALUES
   ('https://example.com/dog1.jpg', 'CHIP123', 'Labrador', '2020-06-01'),
   ('https://example.com/cat1.jpg', 'CHIP456', 'Siamese', '2019-03-12');

INSERT INTO vetly.pet (id, ownerid)
VALUES
    (2, 2);

INSERT INTO vetly.clinic (nif, imageurl, name, address, long, lat, phone, email, ownerid)
VALUES
    ('CLINIC001', 'https://example.com/clinic1.jpg', 'Happy Paws Vet', '123 Pet St, Petville', -75.123456, 40.654321, '5551112233', 'clinic@example.com', 2);

END;

START TRANSACTION;

INSERT INTO vetly.part_of (joinedin, vetid, clinicid)
VALUES
    ('2023-01-01', 1, 1);

INSERT INTO vetly.checkup (description, datetime, animalid, vetid, clinicid)
VALUES
    ('Routine vaccination and checkup', '2024-02-15 10:00:00', 2,1, 1);

INSERT INTO vetly.guide (imageUrl, name, description, text, vetid)
VALUES
    ('https://example.com/guide1.jpg', 'Dog Care 101', 'Basic dog care tips', 'Make sure to feed your dog quality food...', 1);

END;