package es.ucm.fdi.iw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import es.ucm.fdi.iw.service.AmigosService;
import jakarta.servlet.http.HttpSession;

@Controller
public class AmigosController {

    @Autowired
    private AmigosService amigosService;

    @ModelAttribute
    public void populateModel(HttpSession session, Model model) {
        for (String name : new String[] { "u", "url", "ws" }) {
            model.addAttribute(name, session.getAttribute(name));
        }
    }

    @GetMapping("/amigos")
    public String amigos(@RequestParam(name = "view", required = false) String viewType, Model model) {
        if (viewType == null || viewType.isBlank()) {
            viewType = "amigos";
        }

        model.addAttribute("friends", amigosService.getFriends());
        model.addAttribute("requests", amigosService.getRequests());
        model.addAttribute("selectedUser", amigosService.getSelectedUser());
        model.addAttribute("viewType", viewType);
        return "amigos";
    }

}
