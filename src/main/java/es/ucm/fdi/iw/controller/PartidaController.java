package es.ucm.fdi.iw.controller;

import java.util.UUID;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import es.ucm.fdi.iw.dto.game.GameConfigDTO;
import es.ucm.fdi.iw.dto.game.GamePlayerDTO;
import es.ucm.fdi.iw.dto.game.GameRoundsDTO;
import es.ucm.fdi.iw.dto.game.RoundInfoDTO;
import es.ucm.fdi.iw.dto.game.RoundResponseDTO;
import es.ucm.fdi.iw.model.Game;
import es.ucm.fdi.iw.model.Playlist;
import es.ucm.fdi.iw.model.User;
import es.ucm.fdi.iw.service.PartidaService;
import es.ucm.fdi.iw.service.PlaylistService;
import es.ucm.fdi.iw.service.SongService;
import es.ucm.fdi.iw.util.FileGetter;
import es.ucm.fdi.iw.util.NoDataException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
public class PartidaController {

    @Autowired
    private PartidaService partidaService;

    @Autowired
    private SongService songService;

    @Autowired
    private PlaylistService playlistService;

    @ModelAttribute
    public void populateModel(HttpSession session, Model model) {
        for (String name : new String[] { "u", "url", "ws" }) {
            model.addAttribute(name, session.getAttribute(name));
        }
    }

    @GetMapping("/partida/configuracion-partida/{modo}")
    public String configPartida(Model model, @PathVariable String modo, RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("playlists", partidaService.getActivePlaylists());
            model.addAttribute("modo", modo);
            return "configuracion-partida";
        } catch (RuntimeException e) {
            String reason = "Error al acceder a la configuración de partida: " + e.getMessage();
            return redireccion(redirectAttributes, reason);
        }
    }

    @PostMapping("/partida/crear-partida")
    public String crearPartida(Model model,
            @RequestParam Long playlistId,
            @RequestParam int rondas,
            @RequestParam int tiempo,
            @RequestParam String modoJuego,
            @RequestParam int maxPlayers, // Se añadirá cuando se implemente el modo multijugador
            RedirectAttributes redirectAttributes,
            HttpSession session) {

        User creator = (User) session.getAttribute("u");
        GameConfigDTO gameConfig = new GameConfigDTO(playlistId, modoJuego, rondas, tiempo, creator.getId(), maxPlayers,
                0, maxPlayers > 1);
        try {
            UUID gameId = partidaService.createGame(gameConfig);
            return "redirect:/partida/sala-espera/" + gameId.toString();
        } catch (RuntimeException rte) {
            String reason = "Error al acceder a la partida: " + rte.getMessage();
            return redireccion(redirectAttributes, reason);
        }
    }

    @GetMapping("/partida/sala-espera/{gameId}")
    public String salaEspera(@PathVariable UUID gameId, Model model, HttpSession session,
            RedirectAttributes redirectAttributes) {
        try {
            Game game = validarEstadoPartida(gameId, Game.GameState.WAITING);

            // Ingresar jugador a la partida
            User creator = (User) session.getAttribute("u");
            if (!partidaService.isPlayerInGame(creator.getId(), gameId)) {
                partidaService.playerEntersTheGame(gameId, creator.getId());
            }

            // Obtener Configuracion de la Partida
            String gameConfigString = game.getConfigJson();
            GameConfigDTO gameConfig = new GameConfigDTO();
            gameConfig.parseGameConfigDTO(gameConfigString);

            // Obtener jugadores de la partida
            Set<GamePlayerDTO> players = partidaService.getGamePlayers(gameId);

            // Obtener información de la playlist
            Playlist playlist = game.getPlaylist();

            // Agregar datos al modelo
            model.addAttribute("gameId", game.getId().toString());
            model.addAttribute("players", players);
            model.addAttribute("gameConfig", gameConfig);
            model.addAttribute("playlist", playlist);
            return "sala-espera";
        } catch (ResponseStatusException e) {
            String reason = "Error al acceder a la sala de espera: " + e.getReason();
            return redireccion(redirectAttributes, reason);
        } catch (RuntimeException e) {
            String reason = "Error al acceder a la sala de espera: " + e.getMessage();
            return redireccion(redirectAttributes, reason);
        }
    }

    @PostMapping("/partida/abandonar/{gameId}")
    public ResponseEntity<String> abandonarPartida(@PathVariable UUID gameId, HttpServletResponse response,
            HttpSession session) {
        try {
            playerEnPartida(gameId, ((User) session.getAttribute("u")).getId());
            Game game = partidaService.getGameById(gameId);
            if (game == null || !game.getActive()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "La partida no existe.");
            }
            if (game.getGameState().equals(Game.GameState.FINISHED)
                    || game.getGameState().equals(Game.GameState.ABANDONED)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "La partida no se puede abandonar.");
            }
            partidaService.leaveGame(game);

            ResponseCookie cookie = ResponseCookie.from("partidaAbandonada" + gameId, "true")
                    .path("/")
                    .maxAge(10)
                    .sameSite("Lax")
                    .httpOnly(false)
                    .build();

            response.addHeader("Set-Cookie", cookie.toString());
            return ResponseEntity.ok("Partida abandonada con éxito.");
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/partida/iniciar/{gameId}")
    public String iniciarPartida(@PathVariable UUID gameId, RedirectAttributes redirectAttributes,
            HttpSession session) {
        try {
            playerEnPartida(gameId, ((User) session.getAttribute("u")).getId());

            Game game = validarEstadoPartida(gameId, Game.GameState.WAITING);
            partidaService.startGame(game);
            return "redirect:/partida/" + gameId.toString();
        } catch (ResponseStatusException e) {
            String reason = "Error al iniciar la partida: " + e.getReason();
            return redireccion(redirectAttributes, reason);
        } catch (Exception e) {
            String reason = "Error al iniciar la partida: " + e.getMessage();
            return redireccion(redirectAttributes, reason);
        }
    }

    @GetMapping("/partida/{gameId}")
    public String partida(@PathVariable UUID gameId, Model model, RedirectAttributes redirectAttributes,
            HttpSession session) {
        try {
            playerEnPartida(gameId, ((User) session.getAttribute("u")).getId());

            Game game = validarEstadoPartida(gameId, Game.GameState.PLAYING);

            // Obtener Configuracion de la Partida
            String gameConfigString = game.getConfigJson();
            GameConfigDTO gameConfig = new GameConfigDTO();
            gameConfig.parseGameConfigDTO(gameConfigString);
            // Obtener jugadores de la partida
            Set<GamePlayerDTO> players = partidaService.getGamePlayers(gameId);
            // Agregar datos al modelo
            model.addAttribute("gameId", game.getId().toString());
            model.addAttribute("players", players);
            model.addAttribute("gameConfig", gameConfig);

            return "partida";
        } catch (ResponseStatusException e) {
            String reason = "Error al acceder a la partida: " + e.getReason();
            return redireccion(redirectAttributes, reason);
        } catch (RuntimeException e) {
            String reason = "Error inesperado al intentar cargar la partida: " + e.getMessage();
            return redireccion(redirectAttributes, reason);
        }
    }

    @GetMapping("/partida/obtenerTitulos")
    public ResponseEntity<List<String>> obtenerTitulos() {
        try {
            List<String> titles = partidaService.getTitles();
            return ResponseEntity.ok(titles);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/partida/inicioRonda/{gameId}")
    public ResponseEntity<RoundInfoDTO> inicioRonda(@PathVariable UUID gameId, RedirectAttributes redirectAttributes,
            HttpSession session) {
        try {
            playerEnPartida(gameId, ((User) session.getAttribute("u")).getId());

            Game game = validarEstadoPartida(gameId, Game.GameState.PLAYING);

            RoundInfoDTO response = partidaService.startRound(game);
            return ResponseEntity.ok(response);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/partida/song/{id}/cover")
    public ResponseEntity<byte[]> getSongCover(@PathVariable Long id) {
        return responseEntityFromFileGetter(() -> songService.getSongCover(id));
    }

    @GetMapping("/partida/song/{id}/audio/{gameId}")
    public ResponseEntity<byte[]> getSongAudio(@PathVariable Long id, @PathVariable UUID gameId, HttpSession session) {
        try {
            playerEnPartida(gameId, ((User) session.getAttribute("u")).getId());

            Game game = validarEstadoPartida(gameId, Game.GameState.PLAYING);

            GameConfigDTO gameConfig = new GameConfigDTO();
            gameConfig.parseGameConfigDTO(game.getConfigJson());
            int fragmentDuration = gameConfig.getFragmentDuration();

            File audioFile = songService.getSongAudio(id); // debe devolver File
            if (audioFile == null || !audioFile.exists()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            File fragment = partidaService.generateFragment(audioFile, fragmentDuration); // lo escribimos abajo

            byte[] bytes = Files.readAllBytes(fragment.toPath());

            fragment.delete();

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("audio/mpeg"))
                    .contentLength(bytes.length)
                    .body(bytes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/partida/playlist/{id}/cover")
    public ResponseEntity<byte[]> getPlaylistCover(@PathVariable Long id, HttpSession session) {
        return responseEntityFromFileGetter(() -> playlistService.getPlaylistCover(id));
    }

    @PostMapping("/partida/finRonda/{gameId}")
    public ResponseEntity<RoundResponseDTO> finRonda(@PathVariable UUID gameId, @RequestBody Map<Long, String> body,
            RedirectAttributes redirectAttributes, HttpSession session) {
        try {
            playerEnPartida(gameId, ((User) session.getAttribute("u")).getId());

            Game game = validarEstadoPartida(gameId, Game.GameState.PLAYING);

            RoundResponseDTO response = partidaService.endRound(game, body);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/partida/finalizar/{gameId}")
    public ResponseEntity<Void> finalizarPartida(@PathVariable UUID gameId, RedirectAttributes redirectAttributes,
            HttpSession session) {
        try {
            playerEnPartida(gameId, ((User) session.getAttribute("u")).getId());

            Game game = validarEstadoPartida(gameId, Game.GameState.PLAYING);

            partidaService.endGame(game);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/partida/resultados/{gameId}")
    public String resultados(Model model, @PathVariable UUID gameId, HttpSession session,
            RedirectAttributes redirectAttributes) {
        try {
            playerEnPartida(gameId, ((User) session.getAttribute("u")).getId());
            Game game = validarEstadoPartida(gameId, Game.GameState.FINISHED);

            User creator = (User) session.getAttribute("u");
            int position = partidaService.getPosition(game, creator.getId());

            GameConfigDTO gameConfig = new GameConfigDTO();
            gameConfig.parseGameConfigDTO(game.getConfigJson());

            GameRoundsDTO gameRounds = new GameRoundsDTO();
            gameRounds = gameRounds.parse(game.getRoundJson());

            model.addAttribute("position", position);
            model.addAttribute("playlist", partidaService.getPlaylist(game));
            model.addAttribute("sortedParticipants", partidaService.getSortedParticipants(game));
            model.addAttribute("gameResults", partidaService.getGameResults(gameRounds));
            model.addAttribute("gameConfig", gameConfig);

            return "resultados";
        } catch (ResponseStatusException e) {
            String reason = "Error al acceder a los resultados: " + e.getReason();
            return redireccion(redirectAttributes, reason);
        } catch (RuntimeException e) {
            String reason = "Error inesperado al intentar cargar los resultados: " + e.getMessage();
            return redireccion(redirectAttributes, reason);
        }
    }

    // Métodos auxiliares

    private void playerEnPartida(UUID gameId, Long userId) throws ResponseStatusException {
        // Verificar si el jugador está en la partida
        if (partidaService.isPlayerInGame(userId, gameId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No estás en la partida.");
        }
    }

    private Game validarEstadoPartida(UUID gameId, Game.GameState estadoEsperado) throws ResponseStatusException {
        // Validar el estado de la partida
        // Si la partida no existe o no está activa, lanzar una excepción
        Game game;
        try {
            game = partidaService.getGameById(gameId);
        } catch (Exception e) {
            game = null;
        }
        if (game == null || !game.getActive()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "La partida no existe.");
        }
        // Si la partida no está en el estado esperado, lanzar una excepción
        if (!game.getGameState().equals(estadoEsperado)) {
            switch (game.getGameState()) {
                case FINISHED ->
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "La partida ya ha finalizado.");
                case ABANDONED -> throw new ResponseStatusException(HttpStatus.FORBIDDEN, "La partida fue abandonada.");
                case WAITING -> throw new ResponseStatusException(HttpStatus.FORBIDDEN, "La partida no ha comenzado.");
                case PLAYING -> throw new ResponseStatusException(HttpStatus.FORBIDDEN, "La partida está en curso.");
            }
        }

        return game;
    }

    public String redireccion(RedirectAttributes redirectAttributes, String reason) {
        // Guardar el mensaje de error en los atributos de redirección y redirigir a la
        // página de inicio para que muestre el mensaje de error
        redirectAttributes.addFlashAttribute("error", reason);
        return "redirect:/";
    }

    private ResponseEntity<byte[]> responseEntityFromFileGetter(FileGetter fileGetter) {
        // Devolver un ResponseEntity con el contenido del archivo(imagen o audio)
        try {
            File file = fileGetter.get();

            byte[] fileContent = Files.readAllBytes(file.toPath());
            String contentType = Files.probeContentType(file.toPath());

            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.valueOf(contentType))
                    .body(fileContent);

        } catch (IOException e) {
            return ResponseEntity.status(500).body(new byte[0]);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(new byte[0]);
        } catch (NoDataException e) {
            return ResponseEntity.status(410).body(new byte[0]);
        }
    }
}
