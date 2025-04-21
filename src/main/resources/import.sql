-- Inserimento di un utente admin con username 'a' e password 'aa' (bcrypt)
INSERT INTO IWUser (id, username, password, roles, email, enabled, EXP_total, EXP, creation_Date_Time, banned) 
VALUES (1, 'a', '{bcrypt}$2a$10$2BpNTbrsarbHjNsUWgzfNubJqBRf.0Vz9924nRSHBqlbPKerkgX.W', 
        'ADMIN,USER', 'a@example.com', TRUE, 0, 0, CURRENT_TIMESTAMP, FALSE);

-- Inserimento di un utente normale con username 'b' e password 'bb' (bcrypt)
INSERT INTO IWUser (id, username, password, roles, email, enabled, EXP_total, EXP, creation_Date_Time, banned) 
VALUES (2, 'b', '{bcrypt}$2a$10$2BpNTbrsarbHjNsUWgzfNubJqBRf.0Vz9924nRSHBqlbPKerkgX.W', 
        'USER', 'b@example.com', TRUE, 0, 0, CURRENT_TIMESTAMP, FALSE);

INSERT INTO SONG (ACTIVE, ID, ALBUM, ARTISTS, NAME) VALUES
(TRUE, 975, 'Fantastic Magic', '["TK"]', 'Unravel'),
(TRUE, 976, 'Who Made Who', '["AC/DC"]', 'Hells Bells'),
(TRUE, 977, 'Back In Black', '["AC/DC"]', 'Back In Black'),
(TRUE, 978, 'Hablarán las Calles', '["Boikot","Ciudad Jara","ZOO","Aspencat","Los Chikos del Maíz"]', 'Hablarán las Calles'),
(TRUE, 979, 'Highway to Hell', '["AC/DC"]', 'Highway to Hell'),
(TRUE, 980, 'The Razors Edge', '["AC/DC"]', 'Thunderstruck'),
(TRUE, 981, 'Back In Black', '["AC/DC"]', 'You Shook Me All Night Long'),
(TRUE, 1025, 'Fantastic Magic', '["TK"]', '1'),
(TRUE, 1026, 'Fantastic Magic', '["TK"]', '2'),
(TRUE, 1027, 'Fantastic Magic', '["TK"]', '3'),
(TRUE, 1028, 'Fantastic Magic', '["TK"]', '4'),
(TRUE, 1029, 'Fantastic Magic', '["TK"]', '5');


INSERT INTO PLAYLIST (ACTIVE, ID, DESCRIPTION, NAME) VALUES
(TRUE, 976, 'Lorem ipsum', 'Antes de Cristo, Después de Cristo'),
(TRUE, 977, 'test with unravel in fragments', 'a');

INSERT INTO PLAYLIST_SONG (PLAYLIST_ID, SONG_ID) VALUES
(976, 976),
(976, 977),
(976, 979),
(976, 980),
(976, 981),
(977, 1025),
(977, 1026),
(977, 1027),
(977, 1028),
(977, 1029);

-- Reset della sequenza per evitare conflitti con gli ID
ALTER SEQUENCE gen RESTART WITH 1024;