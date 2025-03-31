package es.ucm.fdi.iw.controller;

import java.util.UUID;
import java.util.HashSet;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import es.ucm.fdi.iw.dto.GameConfigDTO;
import es.ucm.fdi.iw.dto.GamePlayerDTO;
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

    @GetMapping("/partida")
    public String partida(Model model) {
        return "partida";
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

        GameConfigDTO gameConfig = new GameConfigDTO(playlistId, modoJuego, rondas, tiempo);
        try {
            UUID gameId = partidaService.createPartida(gameConfig);
            PlayerGame pg = partidaService.addPlayerToGame(creator.getId(), gameId);
            partidaService.addPlayerGameToUser(creator.getId(), pg);
            partidaService.addPlayerGameToGame(gameId, pg);
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
    public String salaEspera(@PathVariable UUID gameId, Model model) {
        try {
            Game game = partidaService.getGameById(gameId);

            if (game == null || !game.getActive()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "La partida no existe.");
            }

            if (game.getGameState().equals("FINISHED")) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "La partida ya ha finalizado.");
            }

            if (!game.getGameState().equals("WAITING")) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "La partida ya ha comenzado.");
            }

            
            //Obtener Jugadores de la partida
            Set<GamePlayerDTO> players = partidaService.getGamePlayers(gameId);

            //Obtener Configuracion de la Partida
            String gameConfigString = game.getConfigJson();
            GameConfigDTO gameConfig = new GameConfigDTO();
            gameConfig.parseGameConfigDTO(gameConfigString);

            // Obtener información de la playlist
            Playlist playlist = game.getPlaylist();

            // Agregar datos al modelo
            model.addAttribute("game", game);
            model.addAttribute("gameConfig", gameConfig);
            model.addAttribute("players", players);
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

    @GetMapping("/partida/resultados")
    public String resultados(Model model) {
        model.addAttribute("position", "¡Has acabado en 1ª posición!");
        model.addAttribute("playlist", partidaService.getPlaylist());
        model.addAttribute("sortedParticipants", partidaService.getSortedParticipants());
        model.addAttribute("songResults", partidaService.getSongResults());

        return "resultados";
    }

}
