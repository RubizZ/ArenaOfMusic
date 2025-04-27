package es.ucm.fdi.iw.model;

import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "canciones")
public class Cancion implements Transferable<Cancion.Transfer> {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gen")
    @SequenceGenerator(name = "gen", sequenceName = "gen")
    private long id;

    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String artist;

    @Column
    private String album;

    @ManyToMany
    @JoinTable(name = "playlist_cancion", joinColumns = @JoinColumn(name = "cancion_id"), inverseJoinColumns = @JoinColumn(name = "playlist_id"))
    private Set<Playlist> playlists;

    @Getter
    @AllArgsConstructor
    public class Transfer {
        private long id;
        private boolean active;
        private String name;
        private String artist;
        private String album;
    }

    @Override
    public Transfer toTransfer() {
        return new Transfer(id, active, name, artist, album);
    }
}
