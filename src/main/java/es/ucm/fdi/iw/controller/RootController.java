package es.ucm.fdi.iw.controller;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import jakarta.servlet.http.HttpSession;

/**
 * Non-authenticated requests only.
 */
@Controller
public class RootController {

        private static final Logger log = LogManager.getLogger(RootController.class);

        @ModelAttribute
        public void populateModel(HttpSession session, Model model) {
                for (String name : new String[] { "u", "url", "ws" }) {
                        model.addAttribute(name, session.getAttribute(name));
                }
                List<String> adminPages = List.of("playlists", "shop", "users", "reports", "spectate", "stats");
                model.addAttribute("adminPages", adminPages);
        }

        @GetMapping("/")
        public String index(Model model) {
                return "index";
        }
}
