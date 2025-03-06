package es.ucm.fdi.iw.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "friendships", uniqueConstraints = @UniqueConstraint(columnNames = { "user1_id", "user2_id" })) // UniqueConstraints para evitar duplicidades entre usuarios
public class Friendship implements Transferable<Friendship.Transfer> {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "friendship_gen")
    @SequenceGenerator(name = "friendship_gen", sequenceName = "friendship_gen")
    private long id;

    @ManyToOne
    @JoinColumn(name = "user1_id", nullable = false)
    private User user1; 

    @ManyToOne
    @JoinColumn(name = "user2_id", nullable = false)
    private User user2;

    @Column(nullable = false)
    private boolean accepted = false; 

    @Column
    private LocalDateTime friendshipDate; 

    @Getter
    @AllArgsConstructor
    public static class Transfer {
        private long id;
        private long user1;
        private long user2;
        private boolean accepted;
        private String friendshipDate;
    }

    @Override
    public Transfer toTransfer() {
        return new Transfer(id, user1.getId(), user2.getId(), accepted,
                friendshipDate == null ? null : DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(friendshipDate));
    }
}
