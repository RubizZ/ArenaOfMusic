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
@Table(name = "block")
public class Block implements Transferable<Block.Transfer> {

    @EmbeddedId
    private BlockId id;

    @ManyToOne
    @MapsId("blockerId")
    @JoinColumn(name = "blocker_id", nullable = false)
    private User blocker;

    @ManyToOne
    @MapsId("blockedId")
    @JoinColumn(name = "blocked_id", nullable = false)
    private User blocked;

    @Column(nullable = false)
    private LocalDateTime blockDate = LocalDateTime.now();

    @Getter
    @AllArgsConstructor
    public static class Transfer {
        private long blocker;
        private long blocked;
        private String blockDate;
    }

    @Override
    public Transfer toTransfer() {
        return new Transfer(id.getBlockerId(), id.getBlockedId(),
                DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(blockDate));
    }
}
