package es.ucm.fdi.iw.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class PartidaService {

    public List<Map<String, Object>> getPlaylists() {
        return Arrays.asList(
                Map.of("id", 1, "image", "img/default-profile.png", "title", "The Weeknd", "author", "ArenaOfMusic"),
                Map.of("id", 2, "image", "img/default-profile.png", "title", "ColdPlay", "author", "ArenaOfMusic"),
                Map.of("id", 3, "image", "img/default-profile.png", "title", "Clásicos 80's", "author", "ArenaOfMusic"),
                Map.of("id", 4, "image", "img/default-profile.png", "title", "Post Malone", "author", "ArenaOfMusic"),
                Map.of("id", 5, "image", "img/default-profile.png", "title", "Country", "author", "ArenaOfMusic"),
                Map.of("id", 6, "image", "img/default-profile.png", "title", "Ed Sheeran", "author", "ArenaOfMusic")
        );
    }

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
                        "song", Map.of("name", "Memories", "artist", "David Guetta ft. Kid Cudi"),
                        "winnerName", "Eric",
                        "time", 3.5),
                Map.of(
                        "song", Map.of("name", "Good Feeling", "artist", "Flo Rida"),
                        "winnerName", "Sam",
                        "time", 4.8),
                Map.of(
                        "song", Map.of("name", "Can't Hold Us", "artist", "Macklemore ft. Ryan Lewis"),
                        "winnerName", "Ava",
                        "time", 2.7));
        return songResults;

    }

    public Map<String, Object> getPlaylistInfo() {
        return Map.of(
            "title", "Top Hits 2010's",
            "image", "img/logo.jpeg"
        );
    }

    public List<Map<String, Object>> getPlayers() {
        return List.of(
            Map.of("username", "Ava", "photoUrl", "img/default-profile.png", "level", 4, "winRate", 85),
            Map.of("username", "Sam", "photoUrl", "img/default-profile.png", "level", 3, "winRate", 75),
            Map.of("username", "Taylor", "photoUrl", "img/default-profile.png", "level", 8, "winRate", 71)
        );
    }

    public Map<String, Object> getHost() {
        return Map.of("username", "Eric", "photoUrl", "img/default-profile.png", "level", 4, "winRate", 38);
    }

    public Map<String, Object> getGameInfo() {
        return Map.of(
            "code", "XT8V5L",
            "status", "waiting",
            "mode", "options",
            "rounds", 20,
            "songTime", 10
        );
    }
}
