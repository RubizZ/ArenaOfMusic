package es.ucm.fdi.iw.model;

import java.sql.Date;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "reports")
public class Report implements Transferable<Report.Transfer> {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gen")
    @SequenceGenerator(name = "gen", sequenceName = "gen")
    private long id;

    @ManyToOne
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter;

    @ManyToOne
    @JoinColumn(name = "reported_id", nullable = false)
    private User reported;

    @Column(nullable = false)
    private int reason;

    @Column
    // TODO @JoinColumn()
    private int partida;

    @Column(nullable = false)
    private boolean solved = false;

    @Column
    private boolean banned = false;

    @ManyToOne
    @JoinColumn(name = "admin_id")
    private User admin;

    @Column(nullable = false)
    private LocalDateTime creationDate = LocalDateTime.now();

    @Column
    private LocalDateTime resolutionDate;

    @Getter
    @AllArgsConstructor
    public static class Transfer {
        private long id;
        private long reporter;
        private long reported;
        private int reason;
        private int partida;
        private boolean solved;
        private boolean banned;
        private long admin;
        private String creationDate;
        private String resolutionDate;
    }

    @Override
    public Transfer toTransfer() {
        return new Transfer(id, reporter.getId(), reported.getId(), reason, partida, solved,
                banned, admin.getId(),
                DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(creationDate),
                resolutionDate == null ? null : DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(resolutionDate));
    }

}
