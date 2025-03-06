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
@Table(name = "blocks")
public class Block implements Transferable<Block.Transfer> {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "block_gen")
    @SequenceGenerator(name = "block_gen", sequenceName = "block_gen")
    private long id;

    @ManyToOne
    @JoinColumn(name = "blocker_id", nullable = false)
    private User blocker;

    @ManyToOne
    @JoinColumn(name = "blocked_id", nullable = false)
    private User blocked;

    @Column(nullable = false)
    private LocalDateTime creationDate = LocalDateTime.now();

    @Getter
    @AllArgsConstructor
    public static class Transfer {
        private long id;
        private long blocker;
        private long blocked;
        private String creationDate;
    }

    @Override
    public Transfer toTransfer() {
        return new Transfer(id, blocker.getId(), blocked.getId(),
                DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(creationDate));
    }
}
