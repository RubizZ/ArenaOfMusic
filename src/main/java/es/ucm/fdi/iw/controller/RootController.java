package es.ucm.fdi.iw.controller;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.RequestParam;


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

    @GetMapping("/profile")
    public String profile(Model model) {
         
        ObjectCard[] objects = {
                new ObjectCard("/img/default-obj.png", "Object 1", "Description 1"),
                new ObjectCard("/img/default-obj.png", "Object 2", "Description 2"),
                new ObjectCard("/img/default-obj.png", "Object 3", "Description 3")
        };

         
        model.addAttribute("objects", objects);

        Match[] matches = {
                new Match("Juan", 1),
                new Match("Maria", -1),
                new Match("Carlos", 0) };
        model.addAttribute("matches", matches);

        return "profile";
    }

    public class ObjectCard {
        private String imageUrl;
        private String name;
        private String description;

        
        public ObjectCard(String imageUrl, String name, String description) {
            this.imageUrl = imageUrl;
            this.name = name;
            this.description = description;
        }

        // Getters e Setters
        public String getImageUrl() {
            return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }

    public class Match {
        private String opponent;
        private Integer result;

        public Match(String opponent, Integer result) {
            this.opponent = opponent;
            this.result = result;
        }

        public String getOpponent() {
            return opponent;
        }

        public Integer getResult() {
            return result;
        }
    }
 
    @GetMapping("/login")
    public String login(Model model, HttpServletRequest request) {
        boolean error = request.getQueryString() != null && request.getQueryString().indexOf("error") != -1;
        model.addAttribute("loginError", error);
        return "login";
    }

    @GetMapping("/")
    public String index(Model model) {
        return "index";
    }

    @GetMapping("/principal")
    public String principal(Model model) {
        return "principal";
    }
    
}
