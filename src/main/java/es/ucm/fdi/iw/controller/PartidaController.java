package es.ucm.fdi.iw.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.ucm.fdi.iw.dto.GameConfigDTO;
import es.ucm.fdi.iw.service.PartidaService;
import jakarta.servlet.http.HttpSession;

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
    public ResponseEntity<?> crearPartida(Model model,
            @RequestParam Long playlistId,
            @RequestParam int rondas,
            @RequestParam int tiempo,
            @RequestParam String modoJuego) {
        GameConfigDTO gameConfig = new GameConfigDTO(playlistId, modoJuego, rondas, tiempo);
        try {
            partidaService.createPartida(gameConfig);
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("playlists", partidaService.getActivePlaylists());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch(Exception e)
        {
            model.addAttribute("error", "Error al crear la partida: " + e.getMessage());
            model.addAttribute("playlists", partidaService.getActivePlaylists());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/partida/sala-espera")
    public String salaespera(Model model) {
        return "sala-espera";
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
