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

import es.ucm.fdi.iw.dto.GameConfigDTO;
import es.ucm.fdi.iw.dto.GamePlayerDTO;
import es.ucm.fdi.iw.model.Game;
import es.ucm.fdi.iw.model.PlayerGame;
import es.ucm.fdi.iw.model.PlayerGameId;
import es.ucm.fdi.iw.model.Playlist;
import es.ucm.fdi.iw.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

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
            game.setGameState("WAITING");
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
    public PlayerGame addPlayerToGame(long id, UUID gameId) {
        try {
            // Obtener las entidades Game y User
            Game game = entityManager.find(Game.class, gameId);
            if (game == null) {
                throw new IllegalArgumentException("La partida no existe.");
            }

            User user = entityManager.find(User.class, id);
            if (user == null) {
                throw new IllegalArgumentException("El usuario no existe.");
            }

            // Crear una nueva instancia de PlayerGame
            PlayerGame playerGame = new PlayerGame();
            PlayerGameId playerGameId = new PlayerGameId();

            playerGameId.setGameId(gameId);
            playerGameId.setUserId(id);

            playerGame.setId(playerGameId);
            playerGame.setGame(game);
            playerGame.setUser(user);
            playerGame.setScore(0); // inicializar el puntaje
            playerGame.setPosition(0); // inicializar la posición

            // Persistir el PlayerGame
            entityManager.persist(playerGame);

            return playerGame;

        } catch (Exception e) {
            System.err.println("Error al vincular host con partida: " + e.getMessage());
            throw new RuntimeException("No se pudo vincular host a partida, intenta nuevamente.");
        }
    }

    @Transactional
    private void vinculatePlayerWhitGame(User creator, UUID gameId, PlayerGame pg) {
        try {
            User aux = entityManager.find(User.class, creator.getId());
            if (aux == null) {
                throw new IllegalArgumentException("El usuario no existe.");
            }
            aux.addPlayerGame(pg);
            System.out.println("patata");
        } catch (Exception e) {
            System.out.println("patata");
        }

    }

    @Transactional
    public UUID crearPartidaYVincular(GameConfigDTO gameConfig, User creator) {
        try {
            UUID gameId = createPartida(gameConfig);
            PlayerGame pg = addPlayerToGame(creator.getId(), gameId);
            vinculatePlayerWhitGame(creator, gameId, pg);
            return gameId;
        } catch (Exception e) {
            System.err.println("Error al crear partida o vincular host con partida: " + e.getMessage());
            throw new RuntimeException("No se pudo crear partida o vincular host a partida, intenta nuevamente.");
        }

    }

    public Set<GamePlayerDTO> getGamePlayers(UUID gameId) {
        Game game = entityManager.find(Game.class, gameId);
        if (game == null) {
            throw new IllegalArgumentException("La partida no existe.");
        }
        Set<PlayerGame> gamePlayers = game.getParticipants();
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

}
