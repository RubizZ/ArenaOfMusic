package es.ucm.fdi.iw.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Service;

import es.ucm.fdi.iw.dto.GameConfigDTO;
import es.ucm.fdi.iw.model.Game;
import es.ucm.fdi.iw.model.Playlist;
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
    public String createPartida(GameConfigDTO gameConfig) {
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

            return game.getId().toString();
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

}
