package es.ucm.fdi.iw.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import es.ucm.fdi.iw.model.User;
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

    @PostMapping("/perfil/editar")
    @ResponseBody
    public ResponseEntity<?> editarPerfilJson(
            @RequestBody Map<String, String> data,
            HttpSession session) {
        User user = (User) session.getAttribute("u");

        try {
            perfilService.actualizarPerfil(
                    user,
                    data.get("username"),
                    data.get("email"),
                    data.get("description"),
                    data.get("oldPassword"),
                    data.get("password"));
            session.setAttribute("u", perfilService.findById(user.getId())); // aggiorna sessione
            return ResponseEntity.ok(Map.of("message", "Perfil actualizado correctamente"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
