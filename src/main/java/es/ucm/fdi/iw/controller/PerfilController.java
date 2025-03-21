package es.ucm.fdi.iw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import es.ucm.fdi.iw.service.PerfilService;
import jakarta.servlet.http.HttpSession;

@Controller
public class PerfilController {
    @ModelAttribute
    public void populateModel(HttpSession session, Model model) {
        for (String name : new String[] { "u", "url", "ws" }) {
            model.addAttribute(name, session.getAttribute(name));
        }
    }

    @Autowired
    private PerfilService perfilService;

    @GetMapping("/perfil")
    public String perfil(Model model) {
        model.addAttribute("objects", perfilService.getObjectCards());
        model.addAttribute("matches", perfilService.getMatches());
        return "perfil";
    }

}
