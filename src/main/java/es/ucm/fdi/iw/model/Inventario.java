package es.ucm.fdi.iw.model;

import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.GenerationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Inventario implements Transferable<Inventario.Transfer> {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gen")
    @SequenceGenerator(name = "gen", sequenceName = "gen")
    private long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "object_id", nullable = false)
    private Objeto object;

    @Column
    private LocalDateTime purchaseDate;



    @Data
    @AllArgsConstructor
    public class Transfer {
        private long id;
        private long idUser;
        private long idObject;
        private String purchaseDate;
    }

    @Override
    public Transfer toTransfer() {
        return new Transfer(id, user.getId(), object.getId(), 
        purchaseDate == null ? null : DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(purchaseDate));
    }
    
}
