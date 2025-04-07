package es.ucm.fdi.iw.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import es.ucm.fdi.iw.dto.game.GameConfigDTO;
import es.ucm.fdi.iw.dto.game.GamePlayerDTO;
import es.ucm.fdi.iw.model.Game;
import es.ucm.fdi.iw.model.PlayerGame;
import es.ucm.fdi.iw.model.PlayerGameId;
import es.ucm.fdi.iw.model.Playlist;
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

    public Map<String, Object> getPlaylist() {
        Map<String, Object> playlist = new HashMap<>();
        playlist.put("image", "img/logo.jpeg");
        playlist.put("name", "Top Hits 2010's");
        playlist.put("songs", 30);
        playlist.put("author", "ArenaOfMusic");
        List<Map<String, String>> canciones = Arrays.asList(
                Map.of("name", "Sorry", "artist", "Justin Bieber"),
                Map.of("name", "God's Plan", "artist", "Drake"),
                Map.of("name", "Memories", "artist", "David Guetta ft. Kid Cudi"),
                Map.of("name", "Good Feeling", "artist", "Flo Rida"),
                Map.of("name", "Can't Hold Us", "artist", "Macklemore ft. Ryan Lewis"));
        playlist.put("canciones", canciones);

        return playlist;
    }

    public List<Map<String, Object>> getSortedParticipants() {
        List<Map<String, Object>> sortedParticipants = Arrays.asList(
                Map.of(
                        "user", Map.of("username", "Eric", "photoUrl", "img/logo.jpeg"),
                        "hits", 13,
                        "score", 5750),
                Map.of(
                        "user", Map.of("username", "Ava", "photoUrl", "img/logo.jpeg"),
                        "hits", 11,
                        "score", 5200),
                Map.of(
                        "user", Map.of("username", "Sam", "photoUrl", "img/logo.jpeg"),
                        "hits", 10,
                        "score", 4850),
                Map.of(
                        "user", Map.of("username", "Taylor", "photoUrl", "img/logo.jpeg"),
                        "hits", 6,
                        "score", 2650));
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
    public UUID crearPartida(GameConfigDTO gameConfig) {
        try {
            UUID gameId = createPartida(gameConfig);
            return gameId;
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
            players.add(playerDTO);
        }

        return players;
    }

    @Transactional
    public void startGame(UUID gameId) {
        try {
            Game game = entityManager.find(Game.class, gameId);
            if (game == null) {
                throw new IllegalArgumentException("La partida no existe.");
            }
            if (!game.getGameState().equals(Game.GameState.WAITING)) {
                throw new IllegalStateException("La partida ya ha comenzado o ha finalizado.");
            }
            game.setGameState(Game.GameState.PLAYING);
        } catch (IllegalArgumentException e) {
            System.out.println("Argumento invalido: " + e.getMessage());
        } catch (IllegalStateException e) {
            System.out.println("Invalid state: " + e.getMessage());
        }

    }

}
