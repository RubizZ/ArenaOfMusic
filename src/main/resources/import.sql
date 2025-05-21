-- Inserimento di un utente admin con username 'a' e password 'aa' (bcrypt)
INSERT INTO IWUser (id, username, password, roles, email, enabled, EXP_total, EXP, creation_Date_Time, banned) 
VALUES (1, 'a', '{bcrypt}$2a$10$2BpNTbrsarbHjNsUWgzfNubJqBRf.0Vz9924nRSHBqlbPKerkgX.W', 
        'ADMIN,USER', 'a@example.com', TRUE, 0, 0, CURRENT_TIMESTAMP, FALSE);

-- Inserimento di un utente normale con username 'b' e password 'bb' (bcrypt)
INSERT INTO IWUser (id, username, password, roles, email, enabled, EXP_total, EXP, creation_Date_Time, banned) 
VALUES (2, 'b', '{bcrypt}$2a$10$2BpNTbrsarbHjNsUWgzfNubJqBRf.0Vz9924nRSHBqlbPKerkgX.W', 
        'USER', 'b@example.com', TRUE, 0, 0, CURRENT_TIMESTAMP, FALSE);

-- Usuario 'c' con contraseña 'cc'
INSERT INTO IWUser (id, username, password, roles, email, enabled, EXP_total, EXP, creation_Date_Time, banned) 
VALUES (3, 'c', '{bcrypt}$2a$10$OcK2BEMH/NmjR3eNN82C/O.IUchq9bo4OaUP8XAp4A4qYarWjHAbC', 
        'USER', 'c@example.com', TRUE, 0, 0, CURRENT_TIMESTAMP, FALSE);

-- Usuario 'd' con contraseña 'dd'
INSERT INTO IWUser (id, username, password, roles, email, enabled, EXP_total, EXP, creation_Date_Time, banned) 
VALUES (4, 'd', '{bcrypt}$2a$10$tLnY1Bp9eu2OzOQbKAVjAe2oNexk2E/v78o4K/Pq7gLoI2n7tD5rS', 
        'USER', 'd@example.com', TRUE, 0, 0, CURRENT_TIMESTAMP, FALSE);


INSERT INTO report (id, reporter_id, reported_id, reason, game_id, solved, banned, admin_id, creation_date, resolution_date)
VALUES (nextval('gen'), 2, 3, 1, NULL, FALSE, FALSE, NULL, '2025-05-04T10:00:00', NULL);

INSERT INTO report (id, reporter_id, reported_id, reason, game_id, solved, banned, admin_id, creation_date, resolution_date)
VALUES (nextval('gen'), 3, 4, 2, NULL, FALSE, FALSE, NULL, '2025-05-04T11:00:00', NULL);



-- Reset della sequenza per evitare conflitti con gli ID
ALTER SEQUENCE gen RESTART WITH 1024;
