package es.ucm.fdi.iw.controller;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.ucm.fdi.iw.model.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/**
 * Site administration.
 *
 * Access to this end-point is authenticated - see SecurityConfig
 */
@Controller
@RequestMapping("admin")
public class AdminController {

    @ModelAttribute
    public void populateModel(HttpSession session, Model model) {
        for (String name : new String[] { "u", "url", "ws" }) {
            model.addAttribute(name, session.getAttribute(name));
        }
        List<String> adminPages = List.of("playlists", "shop", "users", "reports", "spectate", "stats");
        model.addAttribute("adminPages", adminPages);
    }

    private static final Logger log = LogManager.getLogger(AdminController.class);

    @GetMapping({ "", "/" })
    public String index(Model model) {
        log.info("Admin acaba de entrar");
        return "admin";
    }

    @GetMapping({ "/playlists", "/playlists/" })
    public String playlists(Model model) {
        return "admin/playlists";
    }

    @GetMapping({ "/shop", "/shop/" })
    public String shop(Model model) {
        return "admin/shop";
    }

    @GetMapping({ "/users", "users/" })
    public String users(Model model) {
        return "admin/users";
    }

    @GetMapping({ "/reports", "/reports/" })
    public String reports(Model model) {
        return "admin/reports";
    }

    @GetMapping({ "/spectate", "/spectate/" })
    public String spectate(Model model) {
        return "admin/spectate";
    }

    @GetMapping({ "/stats", "/stats/" })
    public String stats(Model model) {
        return "admin/stats";
    }
}
