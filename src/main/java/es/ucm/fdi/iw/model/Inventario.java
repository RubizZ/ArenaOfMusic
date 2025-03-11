package es.ucm.fdi.iw.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Inventario implements Transferable<Inventario.Transfer> {

    @EmbeddedId
    private InventarioId id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne
    @MapsId("objectId")
    @JoinColumn(name = "object_id", nullable = false)
    private Objeto object;

    @Column(nullable = false)
    private LocalDateTime purchaseDate = LocalDateTime.now();


    @Data
    @AllArgsConstructor
    public class Transfer {
        private long idUser;
        private long idObject;
        private String purchaseDate;
    }

    @Override
    public Transfer toTransfer() {
        return new Transfer(id.getUserId(), id.getObjectId(), 
        purchaseDate == null ? null : DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(purchaseDate));
    }
    
}
