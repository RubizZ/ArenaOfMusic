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
@Table(name = "friendship")
public class Friendship implements Transferable<Friendship.Transfer> {

    @EmbeddedId
    private FriendshipId id;

    @Column(nullable = false)
    private boolean accepted = false;

    @Column
    private LocalDateTime friendshipDate;

    @ManyToOne
    @MapsId("user1Id")
    @JoinColumn(name = "user1_id", nullable = false)
    private User user1;

    @ManyToOne
    @MapsId("user2Id")
    @JoinColumn(name = "user2_id", nullable = false)
    private User user2;

    @Getter
    @AllArgsConstructor
    public static class Transfer {
        private long user1;
        private long user2;
        private boolean accepted;
        private String friendshipDate;
    }

    @Override
    public Transfer toTransfer() {
        return new Transfer(id.getUser1Id(), id.getUser2Id(), accepted,
                friendshipDate == null ? null : DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(friendshipDate));
    }
}
