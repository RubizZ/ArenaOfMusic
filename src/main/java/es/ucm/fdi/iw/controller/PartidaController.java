package es.ucm.fdi.iw.controller;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import es.ucm.fdi.iw.service.PartidaService;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class PartidaController {

    @Autowired
    private PartidaService partidaService;

    private static final Logger log = LogManager.getLogger(PartidaController.class);

    @ModelAttribute
    public void populateModel(HttpSession session, Model model) {
        for (String name : new String[] { "u", "url", "ws" }) {
            model.addAttribute(name, session.getAttribute(name));
        }
    }

    @GetMapping("/configuracion-partida")
    public String configPartida(Model model) {
        // Playlists disponibles
        model.addAttribute("playlists", partidaService.getPlaylists());

        return "configuracion-partida";
    }

    @GetMapping("/sala-espera")
    public String salaespera(Model model, HttpSession session)
    {
        // Configuración de la sesión
        model.addAttribute("selectedPlaylist", session.getAttribute("selectedPlaylist"));
        model.addAttribute("rounds", session.getAttribute("rounds"));
        model.addAttribute("time", session.getAttribute("time"));
        model.addAttribute("mode", session.getAttribute("mode"));

        // Obtener información adicional de la partida
        Map<String, Object> playlist = partidaService.getPlaylistInfo();
        List<Map<String, Object>> players = partidaService.getPlayers();
        Map<String, Object> host = partidaService.getHost();
        Map<String, Object> game = partidaService.getGameInfo();
        boolean isHost = true;

        model.addAttribute("playlist", playlist);
        model.addAttribute("players", players);
        model.addAttribute("host", host);
        model.addAttribute("game", game);
        model.addAttribute("isHost", isHost);

        log.info(
            "Sala de espera con código: {}, modo: {}",
            game.get("code"),
            session.getAttribute("mode") != null ? session.getAttribute("mode") : "options"
        );

        return "sala-espera";
    }

    @GetMapping("/partida")
    public String partida(Model model) {
        return "partida";
    }

    @GetMapping("/resultados")
    public String resultados(Model model) {
        model.addAttribute("position", "¡Has acabado en 1ª posición!");
        model.addAttribute("playlist", partidaService.getPlaylist());
        model.addAttribute("sortedParticipants", partidaService.getSortedParticipants());
        model.addAttribute("songResults", partidaService.getSongResults());

        return "resultados";
    }

    @PostMapping("/guardar-configuracion")
    public String guardarConfiguracion(
        @RequestParam("selectedPlaylist") String selectedPlaylist,
        @RequestParam("rounds") int rounds,
        @RequestParam("time") int time,
        @RequestParam("mode") String mode,
        HttpSession session)
    {
        session.setAttribute("selectedPlaylist", selectedPlaylist);
        session.setAttribute("rounds", rounds);
        session.setAttribute("time", time);
        session.setAttribute("mode", mode);

        return "redirect:/sala-espera";
    }
}
