package es.ucm.fdi.iw.model;

import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
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
@Table(name = "hist_partidas")
public class Game implements Transferable<Game.Transfer> {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "hist_partidas_gen")
    @SequenceGenerator(name = "hist_partidas_gen", sequenceName = "hist_partidas_seq")
    private long id;

    @Column(name = "config", nullable = false)
    private String configJson;

    @Column(name = "ronda", nullable = false)
    private String roundJson;

    @ManyToOne
    @JoinColumn(name = "playlist_id", nullable = false)
    private Playlist playlist;

    @ManyToMany(mappedBy = "game")
    private Set<User> participants;

    @Getter
    @AllArgsConstructor
    public static class Transfer {
        private long id;
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
            playlist.getId()
        );
    }
}
