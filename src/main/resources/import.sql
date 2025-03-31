-- Inserimento di un utente admin con username 'a' e password 'aa' (bcrypt)
INSERT INTO IWUser (id, username, password, roles, email, enabled, EXP_total, EXP, creation_Date_Time, banned) 
VALUES (1, 'a', '{bcrypt}$2a$10$2BpNTbrsarbHjNsUWgzfNubJqBRf.0Vz9924nRSHBqlbPKerkgX.W', 
        'ADMIN,USER', 'a@example.com', TRUE, 0, 0, CURRENT_TIMESTAMP, FALSE);

-- Inserimento di un utente normale con username 'b' e password 'bb' (bcrypt)
INSERT INTO IWUser (id, username, password, roles, email, enabled, EXP_total, EXP, creation_Date_Time, banned) 
VALUES (2, 'b', '{bcrypt}$2a$10$2BpNTbrsarbHjNsUWgzfNubJqBRf.0Vz9924nRSHBqlbPKerkgX.W', 
        'USER', 'b@example.com', TRUE, 0, 0, CURRENT_TIMESTAMP, FALSE);

INSERT INTO Song (ACTIVE, ID, ALBUM, ARTISTS, NAME) VALUES
(TRUE, 975, 'Fantastic Magic', '["TK"]', 'Unravel'),
(TRUE, 976, 'Who Made Who', '["AC/DC"]', 'Hells Bells'),
(TRUE, 977, 'Back In Black', '["AC/DC"]', 'Back In Black'),
(TRUE, 978, 'Hablarán las Calles', '["Boikot","Ciudad Jara","ZOO","Aspencat","Los Chikos del Maíz"]', 'Hablarán las Calles'),
(TRUE, 979, 'Highway to Hell', '["AC/DC"]', 'Highway to Hell'),
(TRUE, 980, 'The Razors Edge', '["AC/DC"]', 'Thunderstruck'),
(TRUE, 981, 'Back In Black', '["AC/DC"]', 'You Shook Me All Night Long');

INSERT INTO PLAYLIST(ACTIVE, ID, DESCRIPTION, NAME) VALUES(TRUE, 975, 'Lorem ipsum', 'Antes de Cristo, Después de Cristo');

INSERT INTO PLAYLIST_SONG(PLAYLIST_ID, SONG_ID) VALUES(975, 976), (975, 977), (975, 979), (975, 980), (975, 981);

-- Reset della sequenza per evitare conflitti con gli ID
ALTER SEQUENCE gen RESTART WITH 1024;