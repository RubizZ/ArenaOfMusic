package es.ucm.fdi.iw.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class AmigosService {

    public List<Map<String, Object>> getFriends() {
        return List.of(
                Map.of("username", "Ava", "photoUrl", "img/logo.jpeg", "status", "Conectado"),
                Map.of("username", "Sam", "photoUrl", "img/logo.jpeg", "status", "Conectado"),
                Map.of("username", "Taylor", "photoUrl", "img/logo.jpeg", "status", "Desconectado"));
    }

    public List<Map<String, Object>> getRequests() {
        return List.of(
                Map.of("username", "Sam", "photoUrl", "img/logo.jpeg", "level", 4, "winRate", 85),
                Map.of("username", "Taylor", "photoUrl", "img/logo.jpeg", "level", 3, "winRate", 70));
    }

    public Map<String, Object> getSelectedUser() {
        return Map.of(
                "username", "Eric",
                "photoUrl", "img/logo.jpeg",
                "status", "Conectado",
                "victories", 7,
                "losses", 3,
                "draws", 12,
                "favoritePlaylists", List.of(
                        Map.of("title", "The Weeknd", "author", "ArenaOfMusic", "image", "img/logo.jpeg"),
                        Map.of("title", "Coldplay", "author", "ArenaOfMusic", "image", "img/logo.jpeg"),
                        Map.of("title", "Clásicos 80s", "author", "ArenaOfMusic", "image", "img/logo.jpeg")));
    }
}
