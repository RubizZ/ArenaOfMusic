package es.ucm.fdi.iw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

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
        model.addAttribute("playlist", partidaService.getPlaylist());
        model.addAttribute("sortedParticipants", partidaService.getSortedParticipants());
        model.addAttribute("songResults", partidaService.getSongResults());

        return "resultados";
    }

}
