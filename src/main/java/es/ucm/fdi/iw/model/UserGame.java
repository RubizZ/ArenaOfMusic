package es.ucm.fdi.iw.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "partida_usu")
public class UserGame implements Transferable<UserGame.Transfer> {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "partida_usu_gen")
    @SequenceGenerator(name = "partida_usu_gen", sequenceName = "partida_usu_seq")
    private long id;

    @ManyToOne
    @JoinColumn(name = "partida_id", nullable = false)
    private Game game;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private User user;

    @Getter
    @AllArgsConstructor
    public static class Transfer {
        private long id;
        private long partidaId;
        private long userId;
    }

    @Override
    public Transfer toTransfer() {
        return new Transfer(
            id,
            game.getId(),
            user.getId()
        );
    }
}
