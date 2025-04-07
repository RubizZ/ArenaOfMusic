package es.ucm.fdi.iw.controller;

import java.util.UUID;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import es.ucm.fdi.iw.dto.game.GameConfigDTO;
import es.ucm.fdi.iw.dto.game.GamePlayerDTO;
import es.ucm.fdi.iw.model.Game;
import es.ucm.fdi.iw.model.PlayerGame;
import es.ucm.fdi.iw.model.Playlist;
import es.ucm.fdi.iw.model.User;
import es.ucm.fdi.iw.service.PartidaService;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

@Controller
public class PartidaController {

    @Autowired
    private PartidaService partidaService;

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
            RedirectAttributes redirectAttributes) {

        GameConfigDTO gameConfig = new GameConfigDTO(playlistId, modoJuego, rondas, tiempo);
        try {
            UUID gameId = partidaService.crearPartida(gameConfig);
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
        User creator = (User) session.getAttribute("u");
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

            // Obtener información de la playlist
            Playlist playlist = game.getPlaylist();

            // Agregar datos al modelo
            model.addAttribute("gameId", game.getId().toString());
            model.addAttribute("userId", creator.getId());
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
    @ResponseBody
    public ResponseEntity<?> iniciarPartida(@PathVariable UUID gameId) {
        try {
            partidaService.startGame(gameId);
            return ResponseEntity.ok(Map.of(
                    "message", "Partida iniciada correctamente",
                    "redirectUrl", "/partida/partida/" + gameId.toString()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Error al iniciar la partida"));
        }
    }

    @GetMapping("/partida/partida/{gameId}")
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
            return "partida";

        } catch (ResponseStatusException e) {
            model.addAttribute("msg", "Error al acceder a la sala de espera: " + e.getReason());
            model.addAttribute("status", e.getStatusCode().value());
            return "error";
        }
    }

    @GetMapping("/partida/resultados")
    public String resultados(Model model) {
        model.addAttribute("position", "¡Has acabado en 1ª posición!");
        model.addAttribute("playlist", partidaService.getPlaylist());
        model.addAttribute("sortedParticipants", partidaService.getSortedParticipants());
        model.addAttribute("songResults", partidaService.getSongResults());

        return "resultados";
    }

}
