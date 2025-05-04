-- Inserimento di un utente admin con username 'a' e password 'aa' (bcrypt)
INSERT INTO IWUser (id, username, password, roles, email, enabled, EXP_total, EXP, creation_Date_Time, banned) 
VALUES (1, 'a', '{bcrypt}$2a$10$2BpNTbrsarbHjNsUWgzfNubJqBRf.0Vz9924nRSHBqlbPKerkgX.W', 
        'ADMIN,USER', 'a@example.com', TRUE, 0, 0, CURRENT_TIMESTAMP, FALSE);

-- Inserimento di un utente normale con username 'b' e password 'bb' (bcrypt)
INSERT INTO IWUser (id, username, password, roles, email, enabled, EXP_total, EXP, creation_Date_Time, banned) 
VALUES (2, 'b', '{bcrypt}$2a$10$2BpNTbrsarbHjNsUWgzfNubJqBRf.0Vz9924nRSHBqlbPKerkgX.W', 
        'USER', 'b@example.com', TRUE, 0, 0, CURRENT_TIMESTAMP, FALSE);

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
(TRUE, 1043, 'Feo, Fuerte y Formal', '["Loquillo Y Los Trogloditas"]', 'Feo Fuerte y Formal'),
(TRUE, 1044, 'Ultrasónica', '["Los Piratas"]', 'Años 80'),
(TRUE, 1045, '4 Canciones', '["Los Ronaldos"]', 'No Puedo Vivir Sin Ti'),
(TRUE, 1046, 'Es un Secreto...No Se Lo Digas a Nadie', '["Maldita Nerea"]', 'El Secreto de las Tortugas'),
(TRUE, 1047, 'Sin Enchufe', '["M-Clan"]', 'Carolina'),
(TRUE, 1048, 'Que El Cielo Espere Sentao..', '["Melendi"]', 'Caminando por la vida'),
(TRUE, 1049, 'Mientras No Cueste Trabajo', '["Melendi"]', 'Kisiera yo saber'),
(TRUE, 1050, 'Relatos De Carnaval', '["Nena Daconte"]', 'Tenía Tanto Que Darte'),
(TRUE, 1051, 'Aviones', '["Pereza"]', 'Lady Madrid'),
(TRUE, 1052, 'Algo Para Cantar', '["Pereza"]', 'Pienso en Aquella Tarde'),
(TRUE, 1053, 'Aviones', '["Pereza"]', 'Princesas'),
(TRUE, 1054, 'Algo Zero', '["Pereza"]', 'Todo Me Da Igual'),
(TRUE, 1055, 'Fantastic Magic', '["TK"]', '2'),
(TRUE, 1056, 'Fantastic Magic', '["TK"]', '3'),
(TRUE, 1057, 'Fantastic Magic', '["TK"]', '5'),
(TRUE, 1058, 'Fantastic Magic', '["TK"]', '1'),
(TRUE, 1059, 'Fantastic Magic', '["TK"]', '4');


INSERT INTO playlist (active, id, description, name) VALUES
(TRUE, 975, 'La mejor música de la primera década del 2000 en España', 'Lo mejor de los 2000 en España'),
(TRUE, 976, 'Playlist for testing', 'Test Unravel Fragments');


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
(975, 1043),
(975, 1044),
(975, 1045),
(975, 1046),
(975, 1047),
(975, 1048),
(975, 1049),
(975, 1050),
(975, 1051),
(975, 1052),
(975, 1053),
(975, 1054),
(976, 1055),
(976, 1056),
(976, 1057),
(976, 1058),
(976, 1059);

-- Reset della sequenza per evitare conflitti con gli ID
ALTER SEQUENCE gen RESTART WITH 1024;