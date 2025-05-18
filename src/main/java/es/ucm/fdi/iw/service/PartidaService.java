package es.ucm.fdi.iw.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import es.ucm.fdi.iw.dto.game.GameConfigDTO;
import es.ucm.fdi.iw.dto.game.GamePlayerDTO;
import es.ucm.fdi.iw.dto.game.GameRoundsDTO;
import es.ucm.fdi.iw.dto.game.RoundInfoDTO;
import es.ucm.fdi.iw.dto.game.RoundResponseDTO;
import es.ucm.fdi.iw.model.Game;
import es.ucm.fdi.iw.model.PlayerGame;
import es.ucm.fdi.iw.model.PlayerGameId;
import es.ucm.fdi.iw.model.Playlist;
import es.ucm.fdi.iw.model.Song;
import es.ucm.fdi.iw.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PartidaService {

    @PersistenceContext
    private EntityManager entityManager;

    public Map<String, Object> getPlaylist(Game game) {
        GameConfigDTO gameConfig = new GameConfigDTO();
        gameConfig.parseGameConfigDTO(game.getConfigJson());
        GameRoundsDTO rounds = new GameRoundsDTO();
        rounds = rounds.parse(game.getRoundJson());

        Playlist playlist = entityManager.find(Playlist.class, gameConfig.getPlaylistId());
        Map<String, Object> GamePlaylist = new HashMap<>();
        GamePlaylist.put("name", playlist.getName());
        GamePlaylist.put("songs", gameConfig.getRounds());

        List<Song> canciones = getSongsByGame(rounds.getSongsIds());
        GamePlaylist.put("canciones", canciones);

        return GamePlaylist;
    }

    private List<Song> getSongsByGame(List<Long> ids) {
        return entityManager.createNamedQuery("Song.getSongsOfList", Song.class)
                .setParameter("ids", ids)
                .getResultList();
    }

    public PriorityQueue<PlayerGame> getSortedParticipants(Game game) {
        List<PlayerGame> players = game.getParticipants();
        PriorityQueue<PlayerGame> sortedParticipants = new PriorityQueue<>(
                Comparator.comparingInt(PlayerGame::getScore).reversed());

        sortedParticipants.addAll(players);

        return sortedParticipants;

    }

    public List<Map<String, Object>> getSongResults() {
        List<Map<String, Object>> songResults = Arrays.asList(
                Map.of(
                        "song", Map.of("name", "Sorry", "artist", "Justin Bieber"),
                        "winnerName", "Sam",
                        "time", 2.1),
                Map.of(
                        "song", Map.of("name", "God's Plan", "artist", "Drake"),
                        "winnerName", "Eric",
                        "time", 3.9),
                Map.of(
                        "song",
                        Map.of("name", "Memories", "artist", "David Guetta ft. Kid Cudi"),
                        "winnerName", "Eric",
                        "time", 3.5),
                Map.of(
                        "song", Map.of("name", "Good Feeling", "artist", "Flo Rida"),
                        "winnerName", "Sam",
                        "time", 4.8),
                Map.of(
                        "song",
                        Map.of("name", "Can't Hold Us", "artist", "Macklemore ft. Ryan Lewis"),
                        "winnerName", "Ava",
                        "time", 2.7));
        return songResults;

    }

    public List<Playlist> getActivePlaylists() {
        TypedQuery<Playlist> playlists = entityManager.createNamedQuery("Playlist.active", Playlist.class);
        return playlists.getResultList();
    }

    @Transactional
    public UUID createPartida(GameConfigDTO gameConfig) {
        try {
            Playlist playlist = entityManager.find(Playlist.class, gameConfig.getPlaylistId());
            if (playlist == null || !playlist.isActive()) {
                throw new IllegalArgumentException(
                        "La playlist seleccionada no existe o no se encuentra disponible.");
            }

            Game game = new Game();
            game.setConfigJson(gameConfig.toString());
            game.setRoundJson("[]");
            game.setGameState(Game.GameState.WAITING);
            game.setPlaylist(playlist);

            entityManager.persist(game);

            return game.getId();
        } catch (Exception e) {
            System.err.println("Error al crear la partida: " + e.getMessage());
            throw new RuntimeException("No se pudo crear la partida, intenta nuevamente.");
        }
    }

    public Game getGameById(UUID gameId) {
        Game game = entityManager.find(Game.class, gameId);
        if (game == null || !game.getActive()) {
            throw new IllegalArgumentException("La partida no existe.");
        }
        return game;
    }

    @Transactional
    public PlayerGame addPlayerIntoGame(long userId, UUID gameId) {
        try {
            Game game = entityManager.find(Game.class, gameId);
            if (game == null) {
                throw new IllegalArgumentException("La partida con ID " + gameId + " no existe.");
            }

            User user = entityManager.find(User.class, userId);
            if (user == null) {
                throw new IllegalArgumentException("El usuario con ID " + userId + " no existe.");
            }

            PlayerGameId checkId = new PlayerGameId(gameId, userId);
            if (entityManager.find(PlayerGame.class, checkId) != null) {
                System.out.println("El usuario " + userId + " ya está en la partida " + gameId);
                throw new IllegalStateException("El usuario ya está en esta partida.");
            }

            PlayerGame playerGame = new PlayerGame();

            playerGame.setGame(game);
            playerGame.setUser(user);

            PlayerGameId playerGameId = new PlayerGameId(game.getId(), user.getId());
            playerGame.setId(playerGameId);

            playerGame.setScore(0);
            playerGame.setPosition(0);

            entityManager.persist(playerGame);

            GameConfigDTO gameConfig = new GameConfigDTO();
            gameConfig.parseGameConfigDTO(game.getConfigJson());
            gameConfig.setNumPlayers(gameConfig.getNumPlayers() + 1);
            game.setConfigJson(gameConfig.toString());

            return playerGame;
        } catch (IllegalArgumentException e) {
            System.out.println("Argumento invalido: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.out.println(
                    "Error inesperado al agregar jugador " + userId + " a partida " + gameId + "\n" + e.getMessage());
            throw new RuntimeException("Error inesperado en el servidor al agregar el jugador al juego.", e);
        }

    }

    @Transactional
    public void addPlayerGameToUser(long userId, PlayerGame pg) {
        User user = entityManager.find(User.class, userId);
        if (user == null) {
            throw new IllegalArgumentException("El usuario con ID " + userId + " no existe.");
        }
        user.addPlayerGame(pg);
    }

    @Transactional
    public void addPlayerGameToGame(UUID gameId, PlayerGame pg) {
        Game game = entityManager.find(Game.class, gameId);
        if (game == null) {
            throw new IllegalArgumentException("La partida con ID " + gameId + " no existe.");
        }
        game.addPlayerGame(pg);
    }

    @Transactional
    public void accederPartida(UUID gameId, long playerId) {
        try {
            addPlayerToGame(gameId, playerId);
        } catch (Exception e) {
            System.err.println("Error al crear partida o vincular host con partida: " + e.getMessage());
            throw new RuntimeException("No se pudo crear partida o vincular host a partida, intenta nuevamente.");
        }
    }

    @Transactional
    public void addPlayerToGame(UUID gameId, long userId) {
        try {
            Game game = entityManager.find(Game.class, gameId);
            if (game == null) {
                throw new IllegalArgumentException("La partida con ID " + gameId + " no existe.");
            }

            User user = entityManager.find(User.class, userId);
            if (user == null) {
                throw new IllegalArgumentException("El usuario con ID " + userId + " no existe.");
            }

            PlayerGame pg = addPlayerIntoGame(userId, gameId);
            addPlayerGameToUser(userId, pg);
            addPlayerGameToGame(gameId, pg);
        } catch (IllegalArgumentException e) {
            System.out.println("Argumento invalido: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error inesperado al agregar jugador a la partida: " + e.getMessage());
        }
    }

    public Set<GamePlayerDTO> getGamePlayers(UUID gameId) {
        Game game = entityManager.find(Game.class, gameId);
        if (game == null) {
            throw new IllegalArgumentException("La partida no existe.");
        }
        List<PlayerGame> gamePlayers = game.getParticipants();
        Set<GamePlayerDTO> players = new HashSet<>();

        for (PlayerGame playerGame : gamePlayers) {
            User player = entityManager.find(User.class, playerGame.getUser().getId());
            GamePlayerDTO playerDTO = new GamePlayerDTO();
            playerDTO.setId(player.getId());
            playerDTO.setUsername(player.getUsername());
            playerDTO.setEXP_total(player.getEXP_total());
            playerDTO.setTotalWins(entityManager.createNamedQuery("PlayerGame.countWinsByUser", Long.class)
                    .setParameter("userId", player.getId())
                    .getSingleResult());
            playerDTO.setProfileImage(player.getProfileImage());
            players.add(playerDTO);
        }

        return players;
    }

    public List<Song> getSongsByPlaylistId(long playlistId) {
        return entityManager.createNamedQuery("Song.findByPlaylistId", Song.class)
                .setParameter("playlistId", playlistId)
                .getResultList();
    }

    @Transactional
    public void cargarCanciones(Game game) {
        GameConfigDTO gameConfigDTO = new GameConfigDTO();
        gameConfigDTO.parseGameConfigDTO(game.getConfigJson());

        Playlist playlist = game.getPlaylist();
        if (playlist == null || !playlist.isActive()) {
            throw new IllegalArgumentException("La playlist no existe o no está activa.");
        }

        List<Song> cancionesAleatorias = getSongsByPlaylistId(playlist.getId());

        // Verificar si hay suficientes canciones para el número de rondas
        if (cancionesAleatorias.size() < gameConfigDTO.getRounds()) {
            throw new IllegalArgumentException(
                    "No hay suficientes canciones en la playlist para el número de rondas configurado.");
        }

        // Mezclar las canciones aleatoriamente
        Collections.shuffle(cancionesAleatorias);

        // Seleccionar las primeras `gameConfigDTO.getRounds()` canciones
        cancionesAleatorias = cancionesAleatorias.subList(0, gameConfigDTO.getRounds());

        GameRoundsDTO gameRoundsDTO = new GameRoundsDTO();
        gameRoundsDTO.setSongsIds(cancionesAleatorias.stream()
                .map(Song::getId)
                .collect(Collectors.toList()));

        gameRoundsDTO.setRoundNumber(0);

        game.setRoundJson(gameRoundsDTO.toString());

    }

    @Transactional
    public void startGame(Game game) {
        try {
            if (game == null) {
                throw new IllegalArgumentException("La partida no existe.");
            }
            if (!game.getGameState().equals(Game.GameState.WAITING)) {
                throw new IllegalStateException("La partida ya ha comenzado o ha finalizado.");
            }
            cargarCanciones(game);
            game.setGameState(Game.GameState.PLAYING);
        } catch (IllegalArgumentException e) {
            System.out.println("Argumento invalido: " + e.getMessage());
        } catch (IllegalStateException e) {
            System.out.println("Invalid state: " + e.getMessage());
        }

    }

    @Transactional
    public RoundInfoDTO iniciarRonda(Game game) {
        GameRoundsDTO gameRoundsDTO = new GameRoundsDTO();
        RoundInfoDTO roundInfo = new RoundInfoDTO();

        try {
            if (game == null) {
                throw new IllegalArgumentException("La partida no existe.");
            }
            if (!game.getGameState().equals(Game.GameState.PLAYING)) {
                throw new IllegalStateException("La partida no se está jugando.");
            }
            gameRoundsDTO = gameRoundsDTO.parse(game.getRoundJson());
            Long songId = gameRoundsDTO.getSong(gameRoundsDTO.getRoundNumber());
            Song song = entityManager.find(Song.class, songId);

            roundInfo.setRoundNumber(gameRoundsDTO.getRoundNumber() + 1);
            roundInfo.setSongId(song.getId());
            // roundInfo.setSongName(song.getName());
            gameRoundsDTO.addRound(roundInfo);
            game.setRoundJson(gameRoundsDTO.toString());
            entityManager.persist(game);

        } catch (IllegalArgumentException e) {
            System.out.println("Argumento invalido: " + e.getMessage());
        } catch (IllegalStateException e) {
            System.out.println("Invalid state: " + e.getMessage());
        }

        return roundInfo;

    }

    @Transactional
    public RoundResponseDTO finalizarRonda(Game game, Map<Long, String> userAnswers) {
        GameRoundsDTO gameRoundsDTO = new GameRoundsDTO();
        RoundInfoDTO roundInfo = new RoundInfoDTO();
        RoundResponseDTO roundResponse = new RoundResponseDTO();
        try {
            if (game == null) {
                throw new IllegalArgumentException("La partida no existe.");
            }
            if (!game.getGameState().equals(Game.GameState.PLAYING)) {
                throw new IllegalStateException("La partida no se está jugando.");
            }
            gameRoundsDTO = gameRoundsDTO.parse(game.getRoundJson());
            roundInfo = gameRoundsDTO.getRound(gameRoundsDTO.getRoundNumber() - 1);
            gameRoundsDTO.setRound(gameRoundsDTO.getRoundNumber() - 1, roundInfo);
            Song song = entityManager.find(Song.class, gameRoundsDTO.getSong(gameRoundsDTO.getRoundNumber() - 1));
            roundResponse.setSongId(song.getId());
            roundResponse.setSongName(song.getName());

            Map<Long, Boolean> userTry = new HashMap<>();
            userAnswers.forEach((key, value) -> {
                PlayerGame playerGame = entityManager.find(PlayerGame.class,
                        new PlayerGameId(game.getId(), key));
                int score = 0;
                if (value.equalsIgnoreCase(song.getName())) {
                    score += 10;
                    userTry.put(key, true); // Guardar el intento correcto
                } else {
                    userTry.put(key, false); // Guardar el intento incorrecto
                }
                if (playerGame != null) {
                    playerGame.setScore(playerGame.getScore() + score); // Incrementar el puntaje del jugador
                    entityManager.persist(playerGame); // Persistir el cambio en la base de datos
                }
                roundResponse.getResult().put(key, score); // Guardar el puntaje del jugador en el resultado
            });
            roundInfo.setUserAnswers(userTry);
            game.setRoundJson(roundInfo.toString());

            GameConfigDTO gameConfig = new GameConfigDTO();
            gameConfig.parseGameConfigDTO(game.getConfigJson());
            game.setRoundJson(gameRoundsDTO.toString());

        } catch (IllegalArgumentException e) {
            System.out.println("Argumento invalido: " + e.getMessage());
        } catch (IllegalStateException e) {
            System.out.println("Invalid state: " + e.getMessage());
        }

        return roundResponse;
    }

    @Transactional
    public void finalizarPartida(Game game) {
        try {
            if (game == null) {
                throw new IllegalArgumentException("La partida no existe.");
            }
            if (!game.getGameState().equals(Game.GameState.PLAYING)) {
                throw new IllegalStateException("La partida no ha comenzado o ya ha finalizado.");
            }

            game.setGameState(Game.GameState.FINISHED);

            List<PlayerGame> players = game.getParticipants();
            PriorityQueue<PlayerGame> priorityQueue = new PriorityQueue<>(
                    Comparator.comparingInt(PlayerGame::getScore).reversed());

            priorityQueue.addAll(players);
            int position = 1;
            while (!priorityQueue.isEmpty()) {
                PlayerGame playerGame = priorityQueue.poll();
                User user = entityManager.find(User.class, playerGame.getUser().getId());
                if (user != null) {
                    user.setEXP(user.getEXP() + playerGame.getScore());
                    user.setEXP_total(user.getEXP_total() + playerGame.getScore());
                    System.out.println("El jugador " + user.getUsername() + " ha ganado " + playerGame.getScore()
                            + " puntos de EXP.");
                }
                playerGame.setPosition(position++);
                //position++;
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Argumento invalido: " + e.getMessage());
        } catch (IllegalStateException e) {
            System.out.println("Invalid state: " + e.getMessage());
        }
    }

    public List<String> getTitles() {
        List<String> titulos = new ArrayList<>();
        try {
            titulos = entityManager.createNamedQuery("Song.getActiveSongsTitles", String.class)
                    .getResultList();
        } catch (IllegalArgumentException e) {
            System.out.println("Argumento invalido: " + e.getMessage());
        } catch (IllegalStateException e) {
            System.out.println("Invalid state: " + e.getMessage());
        }
        return titulos;
    }

    public int getPosition(Game game, long id) {

        PlayerGame playerGame = entityManager.find(PlayerGame.class,
                new PlayerGameId(game.getId(), id));
        if (playerGame != null) {
            return playerGame.getPosition();
        } else {
            throw new IllegalArgumentException("El jugador no está en la partida.");
        }
    }

    @Transactional
    public void abandonarPartida(Game game) {
        try {
            if (game == null) {
                throw new IllegalArgumentException("La partida no existe.");
            }
            if (game.getGameState().equals(Game.GameState.FINISHED)
                    || game.getGameState().equals(Game.GameState.ABANDONED)) {
                throw new IllegalStateException("La partida no se puede abandonar.");
            }
            game.setGameState(Game.GameState.ABANDONED);
        } catch (IllegalArgumentException e) {
            System.out.println("Argumento invalido: " + e.getMessage());
        } catch (IllegalStateException e) {
            System.out.println("Invalid state: " + e.getMessage());
        }
    }

}
