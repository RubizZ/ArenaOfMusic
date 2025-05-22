package es.ucm.fdi.iw.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.ucm.fdi.iw.model.Friendship;
import es.ucm.fdi.iw.model.FriendshipId;
import es.ucm.fdi.iw.model.PlayerGame;
import es.ucm.fdi.iw.model.User;
import jakarta.persistence.EntityManager;

@Service
@Transactional
public class AmigosService {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private UserService userService;

    @Autowired
    private BlockService blockService;

    @Autowired
    private MessageService messageService;

    
    public User findUser(String name) {
        return userService.findByUsername(name);
    }

    // Devuelve la lista de amigos
    public List<Map<String, Object>> getFriends(String username, String search)
    {
        User me = userService.findByUsername(username);
        Long id = me.getId();

        Map<Long, Long> unreadMessages = messageService.countUnreadMessages(me);

        // Obtener amigos aceptados
        List<Friendship> friendShips = entityManager.createQuery(
                "SELECT f FROM Friendship f " +
                "WHERE f.accepted = true AND (f.user1.id = :id OR f.user2.id = :id)",
                Friendship.class)
                .setParameter("id", id)
                .getResultList();

        return friendShips.stream()
                .map(f -> {
                    User friend = (f.getUser1().getId() == id) ? f.getUser2() : f.getUser1();
                    if(search != null && !search.isBlank() &&
                        !friend.getUsername().toLowerCase().contains(search.toLowerCase()))
                    {
                        return null;
                    }

                    String photoUrl = friend.getProfileImage() != null ? friend.getProfileImage() : "/img/default-profile.png";
                    long unread = unreadMessages.getOrDefault(friend.getId(), 0L);

                    return Map.<String, Object> of(
                        "username", friend.getUsername(),
                        "photoUrl", photoUrl,
                        "level", friend.getEXP_total(),
                        "unread", unread
                    );
                })
                .filter(Objects::nonNull)
                .filter(m -> {
                    // Filtrar amigos bloqueados
                    Long blockId = userService.findByUsername((String)m.get("username")).getId();
                    return !blockService.isBlocked(me.getId(), blockId);
                })
                .collect(Collectors.toList());
    }

    // Devuelve la lista de solicitudes de amistad
    public List<Map<String, Object>> getRequests(String username, String search)
    {
        User me = userService.findByUsername(username);
        Long id = me.getId();

        // Obtener solicitudes de amistad
        List<Friendship> friendShip = entityManager.createQuery(
                "SELECT f FROM Friendship f " +
                "WHERE f.accepted = false AND f.user2.id = :id",
                Friendship.class)
                .setParameter("id", id)
                .getResultList();

        return friendShip.stream()
                .map(f -> {
                    User requestUser = f.getUser1();
                    if(search != null && !search.isBlank()) {
                        if (!requestUser.getUsername().toLowerCase().contains(search.toLowerCase())) {
                            return null;
                        }
                    }

                    String status = getLastLogin(requestUser.getLastLogin());
                    String photoUrl = requestUser.getProfileImage() != null ? requestUser.getProfileImage() : "/img/default-profile.png";

                    return Map.<String, Object> of(
                        "username", requestUser.getUsername(),
                        "photoUrl", photoUrl,
                        "level", requestUser.getEXP_total(),
                        "status", status);
                })
                .filter(Objects::nonNull)
                .filter(m -> {
                    // Filtrar amigos bloqueados
                    Long blockId = userService.findByUsername((String)m.get("username")).getId();
                    return !blockService.isBlocked(me.getId(), blockId);
                })
                .collect(Collectors.toList());
    }

    // Aceptar solicitud de amistad
    public boolean acceptRequest(String username, String requestUsername)
    {
        User me = userService.findByUsername(username);
        User requestUser = userService.findByUsername(requestUsername);

        Friendship friendship = entityManager.createQuery(
            "SELECT f FROM Friendship f " +
            "WHERE f.user1.id = :requestUser AND f.user2.id = :me " +
            "OR (f.user1.id = :me AND f.user2.id = :requestUser)",
            Friendship.class)
            .setParameter("requestUser", requestUser.getId())
            .setParameter("me", me.getId())
            .getSingleResult();

        if (friendship != null && !friendship.isAccepted()) {
            friendship.setAccepted(true);
            friendship.setFriendshipDate(LocalDateTime.now());
            entityManager.merge(friendship);
            return true;
        }

        return false;
    }

    // Rechazar solicitud de amistad
    public boolean rejectRequest(String username, String requestUsername)
    {
        User me = userService.findByUsername(username);
        User requestUser = userService.findByUsername(requestUsername);

        List<Friendship> friendship = entityManager.createQuery(
            "SELECT f FROM Friendship f " +
            "WHERE f.accepted = false " +
            "AND ((f.user1.id = :requestUser AND f.user2.id = :me )" +
            "OR (f.user1.id = :me AND f.user2.id = :requestUser))",
            Friendship.class)
            .setParameter("requestUser", requestUser.getId())
            .setParameter("me", me.getId())
            .getResultList();

        if(!friendship.isEmpty()) {
            entityManager.remove(friendship.get(0));
            return true;
        }

        return false;
    }

    // Eliminar amigo
    public boolean deleteFriend(String username, String friendUsername)
    {
        User me = userService.findByUsername(username);
        User friend = userService.findByUsername(friendUsername);

        Friendship friendship = entityManager.find(Friendship.class, new FriendshipId(me.getId(), friend.getId()));
        if (friendship == null) {
            friendship = entityManager.find(Friendship.class, new FriendshipId(friend.getId(), me.getId()));
        }
        if (friendship != null && friendship.isAccepted()) {
            entityManager.remove(friendship);
            return true;
        }

        return false;
    }

    // Obtener detalles del perfil de un amigo
    public Map<String, Object> getFriendProfile(String username, String friendUsername)
    {
        User friend = userService.findByUsername(friendUsername);

        int totalGames = friend.getPartidas().size();
        long wins = friend.getPartidas().stream()
            .filter(p -> p.getPosition() == 1)
            .count();
        double averageScore = friend.getPartidas().stream()
            .mapToInt(PlayerGame::getScore)
            .average()
            .orElse(0);
        int maxScore = friend.getPartidas().stream()
            .mapToInt(PlayerGame::getScore)
            .max()
            .orElse(0);

        String status = getLastLogin(friend.getLastLogin());
        String photoUrl = friend.getProfileImage() != null ? friend.getProfileImage() : "/img/default-profile.png";
        String description = friend.getDescription() != null ? friend.getDescription() : "";

        List<Map<String, Object>> recentPlaylists = friend.getPartidas().stream()
            .map(PlayerGame::getGame)
            .map(game -> game.getPlaylist())
            .distinct()
            .limit(3)
            .map(playlist -> Map.<String, Object> of(
                "id", playlist.getId(),
                "title", playlist.getName(),
                "description", playlist.getDescription()
            ))
            .collect(Collectors.toList());

        return Map.<String, Object> of(
            "username", friend.getUsername(),
            "photoUrl", photoUrl,
            "description", description,
            "level", friend.getEXP_total(),
            "status", status,
            "wins", wins,
            "averageScore", String.format("%.1f", averageScore),
            "maxScore", maxScore,
            "recentPlaylists", recentPlaylists
        );
    }

    // Enviar una solicitud de amistad
    public boolean sendRequest(String username, String toUsername)
    {
        if(username.equalsIgnoreCase(toUsername)) {
            return false;
        }

        User me = userService.findByUsername(username);
        User other = userService.findByUsername(toUsername);
        if(other == null) {
            return false;
        }

        // Comprobar si la solicitud ya existe
        List<Friendship> existingFriendship = entityManager.createQuery(
            "SELECT f FROM Friendship f " +
            "WHERE (f.user1.id = :me AND f.user2.id = :other) " +
            "OR (f.user1.id = :other AND f.user2.id = :me)",
            Friendship.class)
            .setParameter("me", me.getId())
            .setParameter("other", other.getId())
            .getResultList();

        if (!existingFriendship.isEmpty()) {
            return false;
        }

        // Comprobar posibles bloqueos
        if(blockService.isBlocked(me.getId(), other.getId()) ||
           blockService.isBlocked(other.getId(), me.getId())) {
            return false;
        }

        // Crear la solicitud de amistad
        Friendship friendship = new Friendship();
        FriendshipId friendshipId = new FriendshipId(me.getId(), other.getId());

        friendship.setId(friendshipId);
        friendship.setUser1(me);
        friendship.setUser2(other);
        friendship.setAccepted(false);
        entityManager.persist(friendship);

        return true;
    }

    // Obtener el estado de conexión del usuario
    private String getLastLogin(Date loginDate)
    {
        if(loginDate == null) {
            return "Desconocido";
        }

        LocalDateTime last = LocalDateTime.ofInstant(loginDate.toInstant(), ZoneId.systemDefault());
        long seconds = Duration.between(last, LocalDateTime.now()).getSeconds();
        if (seconds < 60) {
            return "Conectado";
        } else if (seconds < 3600) {
            long minutes = seconds / 60;
            return "Última conexión hace " + minutes + " minuto" + (minutes > 1 ? "s" : "");
        } else if (seconds < 86400) {
            long hours = seconds / 3600;
            return "Última conexión hace " + hours + " hora" + (hours > 1 ? "s" : "");
        } else {
            long days = seconds / 86400;
            return "Última conexión hace " + days + " día" + (days > 1 ? "s" : "");
        }
    }
}
