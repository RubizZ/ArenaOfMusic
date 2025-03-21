package es.ucm.fdi.iw.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * An authorized user of the system.
 */
@Entity
@Data
@NoArgsConstructor
@NamedQueries({
        @NamedQuery(name = "User.byUsername", query = "SELECT u FROM User u "
                + "WHERE u.username = :username AND u.enabled = TRUE"),
        @NamedQuery(name = "User.hasUsername", query = "SELECT COUNT(u) "
                + "FROM User u "
                + "WHERE u.username = :username")
})
@Table(name = "IWUser")
public class User implements Transferable<User.Transfer> {

    public enum Role {
        USER, // normal users
        ADMIN, // admin users
    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gen")
    @SequenceGenerator(name = "gen", sequenceName = "gen")
    private long id;

    @Column(nullable = false, unique = true)
    private String username;
    @Column(nullable = false)
    private String password;

    private String firstName;
    private String lastName;

    @Column(nullable = false)
    private boolean enabled = true;

    private String roles; // split by ',' to separate roles

    @Column(nullable = false, unique = true)
    private String email;

    private String description;

    @Column(nullable = false)
    private Integer EXP_totale = 0;

    @Column(nullable = false)
    private Integer EXP = 0;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date creationDateTime = new Date();

    private Date lastLogin;

    @Column(nullable = false)
    private boolean banned = false;

    @OneToMany
    @JoinColumn(name = "sender_id")
    private List<Message> sent = new ArrayList<>();

    @OneToMany
    @JoinColumn(name = "recipient_id")
    private List<Message> received = new ArrayList<>();

    @OneToMany(mappedBy = "reporter")
    private List<Report> reportsMade;

    @OneToMany(mappedBy = "reported")
    private List<Report> reportsReceived;

    // Faltan las relaciones con amigos, bloqueos e Inventario
    // -> faltan las clases de amigos, bloqueos e inventario en model

    /**
     * Checks whether this user has a given role.
     * 
     * @param role to check
     * @return true iff this user has that role.
     */
    public boolean hasRole(Role role) {
        String roleName = role.name();
        return Arrays.asList(roles.split(",")).contains(roleName);
    }

    @Getter
    @AllArgsConstructor
    public static class Transfer {
        private long id;
        private String username;
        private String email;
        private boolean enabled;
        private boolean banned;
        private int EXP_totale;
        private int EXP;
        private Date creationDateTime;
        private Date lastLogin;
        private int totalReceived;
        private int totalSent;
    }

    @Override
    public Transfer toTransfer() {
        return new Transfer(
                id,
                username,
                email,
                enabled,
                banned,
                EXP_totale,
                EXP,
                creationDateTime,
                lastLogin,
                received.size(),
                sent.size());
    }

    @Override
    public String toString() {
        return toTransfer().toString();
    }

}
