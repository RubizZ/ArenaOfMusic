package es.ucm.fdi.iw.model;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "Players-Game")
public class PlayersGame implements Transferable<PlayersGame.Transfer> {

    @EmbeddedId
    private PlayerGameID id;

    @ManyToOne
    @MapsId("gameId")
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Getter
    @AllArgsConstructor
    public static class Transfer {
        private long gameId;
        private long userId;
    }

    @Override
    public Transfer toTransfer() {
        return new Transfer(
            id.getGameId(),
            id.getUserId()
        );
    }
}
