package es.ucm.fdi.iw.controller;

import java.util.UUID;
import java.io.File;
import java.io.IOException;
import java.net.http.HttpHeaders;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.http.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import es.ucm.fdi.iw.dto.game.GameConfigDTO;
import es.ucm.fdi.iw.dto.game.GamePlayerDTO;
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
import jakarta.annotation.Resource;
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

    @GetMapping("/partida/configuracion-partida")
    public String configPartida(Model model) {
        model.addAttribute("playlists", partidaService.getActivePlaylists());
        return "configuracion-partida";
    }

    @PostMapping("/partida/crear-partida")
    public String crearPartida(Model model,
            @RequestParam Long playlistId,
            @RequestParam int rondas,
            @RequestParam int tiempo,
            @RequestParam String modoJuego,
            RedirectAttributes redirectAttributes,
            HttpSession session) {
        User creator = (User) session.getAttribute("u");
        GameConfigDTO gameConfig = new GameConfigDTO(playlistId, modoJuego, rondas, tiempo, creator.getId(), 1);
        try {
            UUID gameId = partidaService.crearPartida(gameConfig, creator.getId());
            return "redirect:/partida/sala-espera/" + gameId.toString();
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/error";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al crear la partida.");
            return "redirect:/error";
        }
    }

    @GetMapping("/partida/sala-espera/{gameId}")
    public String salaEspera(@PathVariable UUID gameId, Model model, HttpSession session) {
        try {
            Game game = partidaService.getGameById(gameId);

            if (game == null || !game.getActive()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "La partida no existe.");
            }

            if (game.getGameState().equals(Game.GameState.FINISHED)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "La partida ya ha finalizado.");
            }

            if (!game.getGameState().equals(Game.GameState.WAITING)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "La partida ya ha comenzado.");
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
            model.addAttribute("msg", "Error al acceder a la sala de espera: " + e.getReason());
            model.addAttribute("status", e.getStatusCode().value());
            return "error";
        } catch (Exception e) {
            model.addAttribute("msg", "Error al acceder a la sala de espera: " + e.getMessage());
            return "error";
        }
    }

    @PostMapping("/partida/iniciar/{gameId}")
    public String iniciarPartida(@PathVariable UUID gameId, RedirectAttributes redirectAttributes) {
        try {

            Game game = partidaService.getGameById(gameId);

            if (game == null || !game.getActive()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "La partida no existe.");
            }

            if (game.getGameState().equals(Game.GameState.FINISHED)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "La partida ya ha finalizado.");
            }

            if (!game.getGameState().equals(Game.GameState.WAITING)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "La partida ya ha comenzado.");
            }

            partidaService.startGame(game);
            return "redirect:/partida/" + gameId.toString();
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/error";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al iniciar la partida.");
            return "redirect:/error";
        }
    }

    @GetMapping("/partida/{gameId}")
    public String partida(@PathVariable UUID gameId, Model model) {
        try {
            Game game = partidaService.getGameById(gameId);
            if (game == null || !game.getActive()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "La partida no existe.");
            }
            if (game.getGameState().equals(Game.GameState.FINISHED)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "La partida ya ha finalizado.");
            }
            if (game.getGameState().equals(Game.GameState.WAITING)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "La partida no ha comenzado.");
            }

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
            model.addAttribute("msg", "Error al acceder a la sala de espera: " + e.getReason());
            model.addAttribute("status", e.getStatusCode().value());
            return "error";
        }
    }

    @GetMapping("/partida/obtenerTitulos")
    public ResponseEntity<List<String>> obtenerTitulos() {
        try {
            List<String> titles = partidaService.getTitles();
            return ResponseEntity.ok(titles);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/partida/ronda/info/{gameId}")
    public ResponseEntity<RoundInfoDTO> obtenerInfoRonda(@PathVariable UUID gameId) {
        try {
            Game game = partidaService.getGameById(gameId);
            if (game == null || !game.getActive()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "La partida no existe.");
            }
            if (!game.getGameState().equals(Game.GameState.PLAYING)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "La partida no está activa.");
            }

            RoundInfoDTO response = partidaService.obtenerInfoRonda(game);
            return ResponseEntity.ok(response);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/partida/ronda/avanzar/{gameId}")
    public ResponseEntity<RoundInfoDTO> avanzarRonda(@PathVariable UUID gameId) {
        try {
            Game game = partidaService.getGameById(gameId);
            if (game == null || !game.getActive()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "La partida no existe.");
            }
            if (!game.getGameState().equals(Game.GameState.PLAYING)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "La partida no está activa.");
            }

            RoundInfoDTO response = partidaService.avanzarRonda(game);
            return ResponseEntity.ok(response);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{gameId}/estado-respuestas")
    public ResponseEntity<Boolean> obtenerEstadoRespuestas(@PathVariable UUID gameId) {
        Game game = partidaService.getGameById(gameId);
        if (game == null || !game.getActive()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "La partida no existe.");
        }
        if (!game.getGameState().equals(Game.GameState.PLAYING)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "La partida no está activa.");
        }

        int jugadores = partidaService.getGamePlayers(gameId).size();
        int respuestasProcesadas = 0;// game.getRoundJson(). (coger las respuestas de la última ronda)
        return ResponseEntity.ok(jugadores == respuestasProcesadas);
    }

    @PostMapping("/partida/ronda/respuestas/{gameId}")
    public ResponseEntity<Void> enviarRespuestas(@PathVariable UUID gameId, @RequestBody Map<Long, String> body) {
        try {
            // Asegúrate de que solo haya una entrada en el body
            if (body.size() != 1) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Se debe enviar solo una respuesta por solicitud.");
            }

            // Obtener el juego
            Game game = partidaService.getGameById(gameId);
            if (game == null || !game.getActive()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "La partida no existe.");
            }
            if (!game.getGameState().equals(Game.GameState.PLAYING)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "La partida no está activa.");
            }

            // Obtener el ID del jugador y la respuesta
            Map.Entry<Long, String> entry = body.entrySet().iterator().next();
            Long playerId = entry.getKey();
            String playerAnswer = entry.getValue();

            // Procesar la respuesta de un solo jugador
            partidaService.procesarRespuestaJugador(game, playerId, playerAnswer);

            return ResponseEntity.ok().build();
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/partida/ronda/resultados/{gameId}")
    public ResponseEntity<RoundResponseDTO> obtenerRespuestas(@PathVariable UUID gameId) {
        try {
            Game game = partidaService.getGameById(gameId);
            if (game == null || !game.getActive()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "La partida no existe.");
            }
            if (!game.getGameState().equals(Game.GameState.PLAYING)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "La partida no está activa.");
            }

            RoundResponseDTO response = partidaService.obtenerRespuestas(game);
            return ResponseEntity.ok(response);
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // @PostMapping("/partida/inicioRonda/{gameId}")
    // public ResponseEntity<RoundInfoDTO> inicioRonda(@PathVariable UUID gameId) {
    // try {
    // Game game = partidaService.getGameById(gameId);
    // if (game == null || !game.getActive()) {
    // throw new ResponseStatusException(HttpStatus.NOT_FOUND, "La partida no
    // existe.");
    // }
    // if (game.getGameState().equals(Game.GameState.FINISHED)) {
    // throw new ResponseStatusException(HttpStatus.FORBIDDEN, "La partida ya ha
    // finalizado.");
    // }
    // if (game.getGameState().equals(Game.GameState.WAITING)) {
    // throw new ResponseStatusException(HttpStatus.FORBIDDEN, "La partida no ha
    // comenzado.");
    // }
    // RoundInfoDTO response = partidaService.iniciarRonda(game);
    // return ResponseEntity.ok(response);
    // } catch (ResponseStatusException e) {
    // return ResponseEntity.status(e.getStatusCode()).build();
    // } catch (Exception e) {
    // return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    // }
    // }

    // @PostMapping("/partida/finRonda/{gameId}")
    // public ResponseEntity<RoundResponseDTO> finRonda(@PathVariable UUID gameId,
    // @RequestBody Map<Long, String> body) {
    // try {
    // Game game = partidaService.getGameById(gameId);
    // if (game == null || !game.getActive()) {
    // throw new ResponseStatusException(HttpStatus.NOT_FOUND, "La partida no
    // existe.");
    // }
    // if (game.getGameState().equals(Game.GameState.FINISHED)) {
    // throw new ResponseStatusException(HttpStatus.FORBIDDEN, "La partida ya ha
    // finalizado.");
    // }
    // if (game.getGameState().equals(Game.GameState.WAITING)) {
    // throw new ResponseStatusException(HttpStatus.FORBIDDEN, "La partida no ha
    // comenzado.");
    // }
    // RoundResponseDTO response = partidaService.finalizarRonda(game, body);
    // return ResponseEntity.ok(response);
    // } catch (ResponseStatusException e) {
    // return ResponseEntity.status(e.getStatusCode()).build();
    // } catch (Exception e) {
    // return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    // }
    // }

    @PostMapping("/partida/finalizar/{gameId}")
    public ResponseEntity<Void> finalizarPartida(@PathVariable UUID gameId) {
        try {
            Game game = partidaService.getGameById(gameId);
            if (game == null || !game.getActive()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "La partida no existe.");
            }
            if (game.getGameState().equals(Game.GameState.FINISHED)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "La partida ya ha finalizado.");
            }
            if (game.getGameState().equals(Game.GameState.WAITING)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "La partida no ha comenzado.");
            }
            partidaService.finalizarPartida(game);
            return ResponseEntity.ok().build();
        } catch (ResponseStatusException e) {
            return ResponseEntity.status(e.getStatusCode()).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/partida/resultados/{gameId}")
    public String resultados(Model model, @PathVariable UUID gameId, HttpSession session) {
        Game game;
        try {
            game = partidaService.getGameById(gameId);
            if (game == null || !game.getActive()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "La partida no existe.");
            }
            if (game.getGameState().equals(Game.GameState.PLAYING)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "La partida se está jugando.");
            }
            if (game.getGameState().equals(Game.GameState.WAITING)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "La partida no ha comenzado.");
            }
        } catch (ResponseStatusException e) {
            model.addAttribute("msg", "Error al acceder a la sala de espera: " + e.getReason());
            model.addAttribute("status", e.getStatusCode().value());
            return "error";
        } catch (Exception e) {
            model.addAttribute("msg", "Error al acceder a la sala de espera: " + e.getMessage());
            return "error";
        }
        User creator = (User) session.getAttribute("u");
        int position = partidaService.getPosition(game, creator.getId());
        model.addAttribute("position", position);
        model.addAttribute("playlist", partidaService.getPlaylist(game));
        model.addAttribute("sortedParticipants", partidaService.getSortedParticipants(game));
        model.addAttribute("songResults", partidaService.getSongResults());

        return "resultados";
    }

    @GetMapping("/partida/song/{id}/cover")
    public ResponseEntity<byte[]> getSongCover(@PathVariable Long id) {
        return responseEntityFromFileGetter(() -> songService.getSongCover(id));
    }

    @GetMapping("/partida/song/{id}/audio")
    public ResponseEntity<byte[]> getSongAudio(@PathVariable Long id) {
        return responseEntityFromFileGetter(() -> songService.getSongAudio(id));
    }

    @GetMapping("/partida/playlist/{id}/cover")
    public ResponseEntity<byte[]> getPlaylistCover(@PathVariable Long id) {
        return responseEntityFromFileGetter(() -> playlistService.getPlaylistCover(id));
    }

    private ResponseEntity<byte[]> responseEntityFromFileGetter(FileGetter fileGetter) {
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
