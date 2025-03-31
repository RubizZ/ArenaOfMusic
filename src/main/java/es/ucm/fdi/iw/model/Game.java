package es.ucm.fdi.iw.model;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "Game")
public class Game implements Transferable<Game.Transfer> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "configuration", nullable = false)
    private String configJson;

    @Column(name = "rounds", nullable = false)
    private String roundJson;

    @Column(name = "state", nullable = false)
    private String gameState;

    @Column(name = "active", nullable = false)
    private Boolean active = true;

    @ManyToOne
    @JoinColumn(name = "playlist_id", nullable = false)
    private Playlist playlist;

    @OneToMany(mappedBy = "game")
    private Set<PlayerGame> participants = new HashSet<>();

    public synchronized void addPlayerGame(PlayerGame playerGame) {
        this.participants.add(playerGame);
    }

    @Getter
    @AllArgsConstructor
    public static class Transfer {
        private UUID id;
        private String configJson;
        private String roundJson;
        private long playlistId;
        private String gameState;
    }

    @Override
    public Transfer toTransfer() {
        return new Transfer(
            id,
            configJson,
            roundJson,
            playlist.getId(),
            gameState
        );
    }
}
