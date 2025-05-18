package es.ucm.fdi.iw.model;

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

    @Column(name = "config", nullable = false)
    private String configJson;

    @Column(name = "ronda", nullable = false)
    private String roundJson;

    @ManyToOne
    @JoinColumn(name = "playlist_id", nullable = false)
    private Playlist playlist;

    @OneToMany(mappedBy = "game")
    private Set<PlayerGame> participants;

    @Getter
    @AllArgsConstructor
    public static class Transfer {
        private UUID id;
        private String configJson;
        private String roundJson;
        private long playlistId;
    }

    @Override
    public Transfer toTransfer() {
        return new Transfer(
                id,
                configJson,
                roundJson,
                playlist.getId());
    }
}
