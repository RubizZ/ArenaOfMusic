package es.ucm.fdi.iw.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import es.ucm.fdi.iw.model.User;
import es.ucm.fdi.iw.service.UserService;
import jakarta.servlet.http.HttpSession;

@ControllerAdvice
public class GlobalModelAdvice {

    @Autowired
    private UserService userService;

    @ModelAttribute
    public void addUserToModel(Principal principal, Model model, HttpSession session) {
        if (principal != null) {
            String username = principal.getName();
            User user = userService.findByUsername(username);
            session.setAttribute("u", user);
            model.addAttribute("u", user);
        }
    }
}
