package es.ucm.fdi.iw.controller;

import java.security.Principal;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import es.ucm.fdi.iw.model.User;
import es.ucm.fdi.iw.service.MessageService;
import es.ucm.fdi.iw.service.UserService;
import jakarta.servlet.http.HttpSession;

/**
 * Non-authenticated requests only.
 */
@Controller
public class RootController {

        private static final Logger log = LogManager.getLogger(RootController.class);

        @Autowired
        private UserService userService;

        @Autowired
        private MessageService messageService;

        @ModelAttribute
        public void populateModel(HttpSession session, Model model, Principal principal) {
                for (String name : new String[] { "u", "url", "ws" }) {
                        model.addAttribute(name, session.getAttribute(name));
                }
        }

        @GetMapping("/")
        public String index(Model model, HttpSession session, Principal principal) {
                if (principal != null) {
                        model.addAttribute("am", messageService
                                        .countUnreadMessages(userService.findByUsername(principal.getName()))
                                        .get((long) 1));
                }

                return "index";
        }

}
