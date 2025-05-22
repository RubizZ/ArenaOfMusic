-- Inserimento di un utente admin con username 'a' e password 'aa' (bcrypt)
INSERT INTO IWUser (id, username, password, roles, email, enabled, EXP_total, EXP, creation_Date_Time, banned) 
VALUES (1, 'a', '{bcrypt}$2a$10$2BpNTbrsarbHjNsUWgzfNubJqBRf.0Vz9924nRSHBqlbPKerkgX.W', 
        'ADMIN,USER', 'a@example.com', TRUE, 0, 0, CURRENT_TIMESTAMP, FALSE);

-- Inserimento di un utente normale con username 'b' e password 'bb' (bcrypt)
INSERT INTO IWUser (id, username, password, roles, email, enabled, EXP_total, EXP, creation_Date_Time, banned) 
VALUES (2, 'b', '{bcrypt}$2a$10$2BpNTbrsarbHjNsUWgzfNubJqBRf.0Vz9924nRSHBqlbPKerkgX.W', 
        'USER', 'b@example.com', TRUE, 0, 0, CURRENT_TIMESTAMP, FALSE);


INSERT INTO report (id, reporter_id, reported_id, reason, game_id, solved, banned, admin_id, creation_date, resolution_date)
VALUES (nextval('gen'), 1, 2, 1, NULL, FALSE, FALSE, NULL, '2025-05-04T10:00:00', NULL);


INSERT INTO song (active, id, album, artists, name) VALUES
(TRUE, 1025, 'Pájaros En La Cabeza', '["Amaral"]', 'Días de verano'),
(TRUE, 1026, 'Lo que hemos vivido', '["Despistaos"]', 'Física o química'),
(TRUE, 1027, 'Pájaros En La Cabeza', '["Amaral"]', 'Marta, Sebas, Guille y los demás'),
(TRUE, 1028, 'La taberna del Buda', '["Café Quijano"]', 'La taberna del Buda'),
(TRUE, 1029, '40:04', '["Efecto Mariposa"]', 'Por quererte'),
(TRUE, 1030, 'We Broke The Rules', '["Aventura/Judy Santos"]', 'Obsesion'),
(TRUE, 1031, 'Zapatillas', '["El Canto Del Loco"]', 'Besos'),
(TRUE, 1032, 'Estados de Ánimo', '["El Canto Del Loco"]', 'Insoportable'),
(TRUE, 1033, 'Zapatillas', '["El Canto Del Loco"]', 'Zapatillas'),
(TRUE, 1034, 'El sueño de Morfeo (reedicion)', '["El Sueño de Morfeo"]', 'Nunca volverá'),
(TRUE, 1035, 'Estopa', '["Estopa"]', 'Por la Raja de Tu Falda'),
(TRUE, 1036, 'Destrangis', '["Estopa"]', 'Vino Tinto'),
(TRUE, 1037, 'Por la boca vive el pez', '["Fito y Fitipaldis"]', 'Por la boca vive el pez'),
(TRUE, 1038, 'Lo mas lejos a tu lado', '["Fito y Fitipaldis"]', 'Soldadito marinero'),
(TRUE, 1039, 'Las Cartas Sobre la Mesa', '["Fondo Flamenco"]', 'Mi Estrella Blanca'),
(TRUE, 1040, 'La Chica de la habitacion de al lado', '["Fran Perea"]', 'Uno más uno son 7'),
(TRUE, 1041, 'A las 12', '["La Fuga"]', 'Paquí pallá'),
(TRUE, 1042, 'Lo Que Te Conte Mientras Te Hacias La Dormida', '["La Oreja de Van Gogh"]', 'Rosas'),
(TRUE, 1043, 'Feo, Fuerte y Formal', '["Loquillo Y Los Trogloditas"]', 'Feo Fuerte y Formal');


INSERT INTO playlist (active, id, description, name) VALUES
(TRUE, 975, 'La mejor música de la primera década del 2000 en España', 'Lo mejor de los 2000 en España');

INSERT INTO playlist_song (playlist_id, song_id) VALUES
(975, 1025),
(975, 1026),
(975, 1027),
(975, 1028),
(975, 1029),
(975, 1030),
(975, 1031),
(975, 1032),
(975, 1033),
(975, 1034),
(975, 1035),
(975, 1036),
(975, 1037),
(975, 1038),
(975, 1039),
(975, 1040),
(975, 1041),
(975, 1042),
(975, 1043);


-- Reset della sequenza per evitare conflitti con gli ID
ALTER SEQUENCE gen RESTART WITH 1024;
