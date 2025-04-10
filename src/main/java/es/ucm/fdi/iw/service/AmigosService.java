package es.ucm.fdi.iw.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
public class AmigosService {

    private List<Map<String, Object>> friends;
    private List<Map<String, Object>> requests;

    public AmigosService() {
        friends = new ArrayList<>();
        friends.add(Map.of("username", "Ava", "photoUrl", "img/logo.jpeg", "level", 4, "winRate", 85, "status", "Conectado"));
        friends.add(Map.of("username", "Sam", "photoUrl", "img/logo.jpeg", "level", 3, "winRate", 70, "status", "Conectado"));
        friends.add(Map.of("username", "Taylor", "photoUrl", "img/logo.jpeg", "level", 3, "winRate", 57, "status", "Desconectado"));

        requests = new ArrayList<>();
        requests.add(Map.of("username", "Nicole", "photoUrl", "img/logo.jpeg", "level", 6, "winRate", 61, "status", "Desconectado"));
        requests.add(Map.of("username", "Oliver", "photoUrl", "img/logo.jpeg", "level", 4, "winRate", 77, "status", "Desconectado"));
    }

    public List<Map<String, Object>> getFriends() {
        return friends;
    }

    public List<Map<String, Object>> getRequests() {
        return requests;
    }

    public Map<String, Object> getSelectedUser() {
        return Map.of(
                "username", "Eric",
                "photoUrl", "img/logo.jpeg",
                "status", "Conectado",
                "victories", 7,
                "defeats", 3,
                "draws", 12,
                "favoritePlaylists", List.of(
                        Map.of("title", "The Weeknd", "author", "ArenaOfMusic", "image", "img/logo.jpeg"),
                        Map.of("title", "Coldplay", "author", "ArenaOfMusic", "image", "img/logo.jpeg"),
                        Map.of("title", "Clásicos 80s", "author", "ArenaOfMusic", "image", "img/logo.jpeg")));
    }

    // Aceptar solicitud de amistad
    public boolean acceptRequest(Map<String, Object> requestData) {
        String username = (String) requestData.get("username");
        System.out.println("Solicitud aceptada para: " + username);

        Optional<Map<String, Object>> request = requests.stream()
            .filter(item -> item.get("username").equals(username))
            .findFirst();

        if(request.isPresent()) {
            // Eliminar usuario de la lista de solicitudes
            requests.remove(request.get());

            // Añadir usuario aceptado a la lista de amigos
            boolean currentFriend = friends.stream().anyMatch(item -> item.get("username").equals(username));
            if(!currentFriend) {
                friends.add(Map.of(
                    "username", username,
                    "photoUrl", request.get().get("photoUrl"),
                    "level", request.get().get("level"),
                    "winRate", request.get().get("winRate"),
                    "status", request.get().get("status")
                ));
            }
            return true;
        }
        return false;
    }

    // Rechazar solicitud de amistad
    public boolean rejectRequest(Map<String, Object> requestData) {
        String username = (String) requestData.get("username");
        System.out.println("Solicitud rechazada para: " + username);

        // Eliminar usuario de la lista de solicitudes
        return requests.removeIf(item -> item.get("username").equals(username));
    }

    // Obtener detalles del perfil de un amigo
    public Map<String, Object> getFriendProfile(Map<String, Object> friendData) {
        return Map.of(
            "username", friendData.get("username"),
            "photoUrl", friendData.get("photoUrl"),
            "status", friendData.get("status"),
            "victories", 7,
            "defeats", 3,
            "draws", 12,
            "favoritePlaylists", List.of(
                Map.of("title", "The Weeknd", "author", "ArenaOfMusic", "image", "img/logo.jpeg"),
                Map.of("title", "Coldplay", "author", "ArenaOfMusic", "image", "img/logo.jpeg"),
                Map.of("title", "Clásicos 80s", "author", "ArenaOfMusic", "image", "img/logo.jpeg")));
    }
}
