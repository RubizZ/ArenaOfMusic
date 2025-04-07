package es.ucm.fdi.iw.controller;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import es.ucm.fdi.iw.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

/**
 * Site administration.
 *
 * Access to this end-point is authenticated - see SecurityConfig
 */
@Controller
@RequestMapping("admin")
public class AdminController {

    @Autowired
    private EntityManager entityManager;

    @ModelAttribute
    public void populateModel(HttpSession session, Model model) {
        for (String name : new String[] { "u", "url", "ws" }) {
            model.addAttribute(name, session.getAttribute(name));
        }
    }

    private static final Logger log = LogManager.getLogger(AdminController.class);

    @GetMapping({ "", "/" })
    public String index(Model model) {
        log.info("Admin acaba de entrar");
        return "admin";
    }

    @GetMapping({ "/shop", "/shop/" })
    public String shop(Model model) {
        return "admin/shop";
    }

    @GetMapping({ "/users", "users/" })
    public String users(Model model) {
        return "admin/users";
    }

    public static class UserFilterRequest {
        private Long id;
        private String username;
        private String email;
        private List<String> roles;
        private int orderBy = 1;
        private String direction = "asc";

        // Getter e Setter

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public List<String> getRoles() {
            return roles;
        }

        public void setRoles(List<String> roles) {
            this.roles = roles;
        }

        public int getOrderBy() {
            return orderBy;
        }

        public void setOrderBy(int orderBy) {
            this.orderBy = orderBy;
        }

        public String getDirection() {
            return direction;
        }

        public void setDirection(String direction) {
            this.direction = direction;
        }
    }

    @PostMapping("/users/list")
    @ResponseBody
    @Transactional
    public String list(@RequestBody UserFilterRequest filter) {

        StringBuilder query = new StringBuilder("SELECT u FROM User u WHERE 1=1");

        System.out.println("=== Filtro ricevuto ===");
        System.out.println("ID: " + filter.getId());
        System.out.println("Username: " + filter.getUsername());
        System.out.println("Email: " + filter.getEmail());
        System.out.println("Roles: " + filter.getRoles());
        System.out.println("OrderBy: " + filter.getOrderBy());
        System.out.println("Direction: " + filter.getDirection());

        if (filter.getId() != null)
            query.append(" AND u.id = :id");
        if (filter.getUsername() != null && !filter.getUsername().isBlank())
            query.append(" AND u.username LIKE :username");
        if (filter.getEmail() != null && !filter.getEmail().isBlank())
            query.append(" AND u.email LIKE :email");
        if (filter.getRoles() != null && !filter.getRoles().isEmpty()) {
            query.append(" AND (");
            for (int i = 0; i < filter.getRoles().size(); i++) {
                if (i > 0)
                    query.append(" AND ");
                query.append(" u.roles LIKE :role" + i);
            }
            query.append(" )");
        }

        switch (filter.getOrderBy()) {
            case 1 -> query.append(" ORDER BY u.id");
            case 2 -> query.append(" ORDER BY u.creationDateTime");
            case 3 -> query.append(" ORDER BY u.lastLogin");
            default -> query.append(" ORDER BY u.id");
        }

        if ("desc".equalsIgnoreCase(filter.getDirection())) {
            query.append(" DESC");
        } else {
            query.append(" ASC");
        }

        TypedQuery<User> q = entityManager.createQuery(query.toString(), User.class);

        if (filter.getId() != null)
            q.setParameter("id", filter.getId());
        if (filter.getUsername() != null && !filter.getUsername().isBlank())
            q.setParameter("username", "%" + filter.getUsername() + "%");
        if (filter.getEmail() != null && !filter.getEmail().isBlank())
            q.setParameter("email", "%" + filter.getEmail() + "%");
        if (filter.getRoles() != null && !filter.getRoles().isEmpty()) {
            for (int i = 0; i < filter.getRoles().size(); i++) {
                q.setParameter("role" + i, "%" + filter.getRoles().get(i) + "%");
            }
        }

        ObjectMapper mapper = new ObjectMapper();
        try {
            List<User> results = q.getResultList();
            System.out.println("Utenti trovati: " + results.size());
            return mapper.writeValueAsString(results);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "{}";
        }
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
