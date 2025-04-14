package es.ucm.fdi.iw;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import es.ucm.fdi.iw.model.User;
import jakarta.persistence.EntityManager;

@Service
public class UserService {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public boolean usernameExists(String username) { // Check if the username already exists
        Long count = entityManager.createQuery(
                "SELECT COUNT(u) FROM User u WHERE u.username = :username", Long.class)
                .setParameter("username", username)
                .getSingleResult();
        return count > 0;
    }

    public void registerUser(String username, String password, String firstName, String lastName, String email) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password)); // Hash of password
        user.setRoles(User.Role.USER.toString()); // Set the user role
        entityManager.persist(user);
    }
}
