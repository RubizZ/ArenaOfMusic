package es.ucm.fdi.iw.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * Non-authenticated requests only.
 */
@Controller
public class RootController {

    private static final Logger log = LogManager.getLogger(RootController.class);

    @ModelAttribute
    public void populateModel(HttpSession session, Model model) {
        for (String name : new String[] { "u", "url", "ws" }) {
            model.addAttribute(name, session.getAttribute(name));
        }
        List<String> adminPages = List.of("playlists", "shop", "users", "reports", "spectate", "stats");
        model.addAttribute("adminPages", adminPages);
    }

    @GetMapping("/perfil")
    public String perfil(Model model) {

        ObjectCard[] objects = {
                new ObjectCard("/img/default-obj.png", "Object 1", "Description 1"),
                new ObjectCard("/img/default-obj.png", "Object 2", "Description 2"),
                new ObjectCard("/img/default-obj.png", "Object 3", "Description 3")
        };

        model.addAttribute("objects", objects);

        Match[] matches = {
                new Match("Juan", 1),
                new Match("Maria", -1),
                new Match("Carlos", 0) };
        model.addAttribute("matches", matches);

        return "perfil";
    }

    public class ObjectCard {
        private String imageUrl;
        private String name;
        private String description;

        public ObjectCard(String imageUrl, String name, String description) {
            this.imageUrl = imageUrl;
            this.name = name;
            this.description = description;
        }

        // Getters e Setters
        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

    public class Match {
        private String opponent;
        private Integer result;

        public Match(String opponent, Integer result) {
            this.opponent = opponent;
            this.result = result;
        }

        public String getOpponent() {
            return opponent;
        }

        public Integer getResult() {
            return result;
        }
    }

    @GetMapping("/login")
    public String login(Model model, HttpServletRequest request) {
        boolean error = request.getQueryString() != null && request.getQueryString().indexOf("error") != -1;
        model.addAttribute("loginError", error);
        return "login";
    }

    @GetMapping("/")
    public String index(Model model) {
        return "index";
    }

    @GetMapping("/amigos")
    public String amigos(@RequestParam(name = "view", required = false) String viewType, Model model) {
        if (viewType == null || viewType.isBlank()) {
            viewType = "amigos";
        }

        // Lista de amigos
        List<Map<String, Object>> friends = Arrays.asList(
                Map.of("username", "Ava", "photoUrl", "img/logo.jpeg", "status", "Conectado"),
                Map.of("username", "Sam", "photoUrl", "img/logo.jpeg", "status", "Conectado"),
                Map.of("username", "Taylor", "photoUrl", "img/logo.jpeg", "status", "Desconectado"));

        // Lista de solicitudes
        List<Map<String, Object>> requests = Arrays.asList(
                Map.of("username", "Sam", "photoUrl", "img/logo.jpeg", "level", 4, "winRate", 85),
                Map.of("username", "Taylor", "photoUrl", "img/logo.jpeg", "level", 3, "winRate", 70));

        // Usuario seleccionado
        Map<String, Object> selectedUser = Map.of(
                "username", "Eric",
                "photoUrl", "img/logo.jpeg",
                "status", "Conectado",
                "victories", 7,
                "losses", 3,
                "draws", 12,
                "favoritePlaylists", Arrays.asList(
                        Map.of("title", "The Weeknd", "author", "ArenaOfMusic", "image", "img/logo.jpeg"),
                        Map.of("title", "Coldplay", "author", "ArenaOfMusic", "image", "img/logo.jpeg"),
                        Map.of("title", "Clásicos 80s", "author", "ArenaOfMusic", "image", "img/logo.jpeg")));

        model.addAttribute("friends", friends);
        model.addAttribute("requests", requests);
        model.addAttribute("selectedUser", selectedUser);
        model.addAttribute("viewType", viewType);

        return "amigos";
    }

    @GetMapping("/tienda")
    public String tienda(Model model) {
        List<Map<String, Object>> fotos = Arrays.asList(Map.of(
                "title", "Foto 1",
                "foto", "img/iconos/usuario.png"),
                Map.of(
                        "title", "Foto 2",
                        "foto", "/img/iconos/perro.png"),
                Map.of(
                        "title", "Foto 3",
                        "foto", "img/iconos/gato.png"),
                Map.of(
                        "title", "Foto 4",
                        "foto", "img/iconos/pez.png"),
                Map.of(
                        "title", "Foto 5",
                        "foto", "img/iconos/pajaro.png"),
                Map.of(
                        "title", "Foto 6",
                        "foto", "img/iconos/conejo.png"));

        List<Map<String, Object>> marcos = Arrays.asList(
                Map.of(
                        "title", "Marco 1",
                        "foto", "img/marco/marco1.png"),
                Map.of(
                        "title", "Marco 2",
                        "foto", "img/marco/marco2.png"),
                Map.of(
                        "title", "Marco 3",
                        "foto", "img/marco/marco3.png"),
                Map.of(
                        "title", "Marco 4",
                        "foto", "img/marco/marco4.png"),
                Map.of(
                        "title", "Marco 5",
                        "foto", "img/marco/marco5.png"),
                Map.of(
                        "title", "Marco 6",
                        "foto", "img/marco/marco6.png"));
        List<Map<String, Object>> banners = Arrays.asList(
                Map.of(
                        "title", "Banner 1",
                        "foto", "img/banners/banner1.png"),
                Map.of(
                        "title", "Banner 2",
                        "foto", "img/banners/banner2.png"),
                Map.of(
                        "title", "Banner 3",
                        "foto", "img/banners/banner3.png"),
                Map.of(
                        "title", "Banner 4",
                        "foto", "img/banners/banner4.png"),
                Map.of(
                        "title", "Banner 5",
                        "foto", "img/banners/banner5.png"),
                Map.of(
                        "title", "Banner 6",
                        "foto", "img/banners/banner6.png"));

        model.addAttribute("itemFotos", fotos);
        model.addAttribute("itemMarcos", marcos);
        model.addAttribute("itemBanners", banners);
        return "tienda";
    }

    @GetMapping("/configuracion-partida")
    public String configPartida(Model model) {
        return "configuracion-partida";
    }

    @GetMapping("/sala-espera")
    public String salaespera(Model model) {
        return "sala-espera";
    }

    @GetMapping("/partida")
    public String partida(Model model) {
        return "partida";
    }

    @GetMapping("/resultados")
    public String resultados(Model model) {
        model.addAttribute("position", "¡Has acabado en 1ª posición!");

        // Información de la playlist
        Map<String, Object> playlist = new HashMap<>();
        playlist.put("image", "img/logo.jpeg");
        playlist.put("name", "Top Hits 2010's");
        playlist.put("songs", 30);
        playlist.put("author", "ArenaOfMusic");

        // Canciones de la playlist
        List<Map<String, String>> canciones = Arrays.asList(
                Map.of("name", "Sorry", "artist", "Justin Bieber"),
                Map.of("name", "God's Plan", "artist", "Drake"),
                Map.of("name", "Memories", "artist", "David Guetta ft. Kid Cudi"),
                Map.of("name", "Good Feeling", "artist", "Flo Rida"),
                Map.of("name", "Can't Hold Us", "artist", "Macklemore ft. Ryan Lewis")
        );
        playlist.put("canciones", canciones);
        model.addAttribute("playlist", playlist);

        // Lista de participantes ordenados por puntuación
        List<Map<String, Object>> sortedParticipants = Arrays.asList(
            Map.of(
                "user", Map.of("username", "Eric", "photoUrl", "img/logo.jpeg"),
                "hits", 13,
                "score", 5750
            ),
            Map.of(
                "user", Map.of("username", "Ava", "photoUrl", "img/logo.jpeg"),
                "hits", 11,
                "score", 5200
            ),
            Map.of(
                "user", Map.of("username", "Sam", "photoUrl", "img/logo.jpeg"),
                "hits", 10,
                "score", 4850
            ),
            Map.of(
                "user", Map.of("username", "Taylor", "photoUrl", "img/logo.jpeg"),
                "hits", 6,
                "score", 2650
            )
        );
        model.addAttribute("sortedParticipants", sortedParticipants);

        // Resultados de cada canción
        List<Map<String, Object>> songResults = Arrays.asList(
            Map.of(
                "song", Map.of("name", "Sorry", "artist", "Justin Bieber"),
                "winnerName", "Sam",
                "time", 2.1
            ),
            Map.of(
                "song", Map.of("name", "God's Plan", "artist", "Drake"),
                "winnerName", "Eric",
                "time", 3.9
            ),
            Map.of(
                "song", Map.of("name", "Memories", "artist", "David Guetta ft. Kid Cudi"),
                "winnerName", "Eric",
                "time", 3.5
            ),
            Map.of(
                "song", Map.of("name", "Good Feeling", "artist", "Flo Rida"),
                "winnerName", "Sam",
                "time", 4.8
            ),
            Map.of(
                "song", Map.of("name", "Can't Hold Us", "artist", "Macklemore ft. Ryan Lewis"),
                "winnerName", "Ava",
                "time", 2.7
            )
        );
        model.addAttribute("songResults", songResults);

        return "resultados";
    }


}
