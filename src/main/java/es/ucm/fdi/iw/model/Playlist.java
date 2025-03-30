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
@Table(name = "playlist")
public class Playlist implements Transferable<Playlist.Transfer> {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gen")
    @SequenceGenerator(name = "gen", sequenceName = "gen")
    private long id;

    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false)
    private String name;

    @Column
    private String description;

    @ManyToMany
    @JoinTable(name = "playlist_song", joinColumns = @JoinColumn(name = "playlist_id"), inverseJoinColumns = @JoinColumn(name = "song_id"))
    private Set<Song> songs;

    public synchronized void addSong(Song song) {
        songs.add(song);
    }

    public synchronized void removeSong(Song song) {
        songs.remove(song);
    }

    @Getter
    @AllArgsConstructor
    public class Transfer {
        private long id;
        private boolean active;
        private String name;
        private String description;
    }

    @Override
    public Transfer toTransfer() {
        return new Transfer(id, active, name, description);
    }

}
