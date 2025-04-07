package es.ucm.fdi.iw.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import es.ucm.fdi.iw.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class PerfilService {

    public class ObjectCard {
        private String imageUrl;
        private String name;
        private String description;

        public ObjectCard(String imageUrl, String name, String description) {
            this.imageUrl = imageUrl;
            this.name = name;
            this.description = description;
        }

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

    public List<ObjectCard> getObjectCards() {
        return List.of(
                new ObjectCard("/img/default-obj.png", "Object 1", "Description 1"),
                new ObjectCard("/img/default-obj.png", "Object 2", "Description 2"),
                new ObjectCard("/img/default-obj.png", "Object 3", "Description 3"));
    }

    public List<Match> getMatches() {
        return List.of(
                new Match("Juan", 1),
                new Match("Maria", -1),
                new Match("Carlos", 0));
    }

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void actualizarPerfil(User user, String username, String email, String description, String oldPassword,
            String newPassword) {

        // Check for duplicate username
        if (!user.getUsername().equals(username) && usernameExists(username)) {
            throw new IllegalArgumentException("Username is already in use.");
        }

        // Check for duplicate email
        if (!user.getEmail().equals(email) && emailExists(email)) {
            throw new IllegalArgumentException("Email is already in use.");
        }

        // Change password if requested
        if (newPassword != null && !newPassword.isBlank()) {
            if (oldPassword == null || !passwordEncoder.matches(oldPassword, user.getPassword())) {
                throw new IllegalArgumentException("Old password is incorrect.");
            }
            user.setPassword(passwordEncoder.encode(newPassword));
        }

        user.setUsername(username);
        user.setEmail(email);
        user.setDescription(description);

        entityManager.merge(user); // update the record in the database
    }

    private boolean usernameExists(String username) {
        try {
            entityManager.createQuery("SELECT u FROM User u WHERE u.username = :username", User.class)
                    .setParameter("username", username)
                    .getSingleResult();
            return true;
        } catch (NoResultException e) {
            return false;
        }
    }

    private boolean emailExists(String email) {
        try {
            entityManager.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
                    .setParameter("email", email)
                    .getSingleResult();
            return true;
        } catch (NoResultException e) {
            return false;
        }
    }

    public User findById(long id) {
        User u = entityManager.find(User.class, id);
        if (u == null) {
            throw new IllegalArgumentException("User not found");
        }
        return u;
    }
}
