package es.ucm.fdi.iw.model;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Converter;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "song")
@NamedQuery(
    name = "Song.findByPlaylistId",
    query = "SELECT s FROM Song s JOIN s.playlists p WHERE p.id = :playlistId"
)
public class Song implements Transferable<Song.Transfer> {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "gen")
    @SequenceGenerator(name = "gen", sequenceName = "gen")
    private long id;

    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    @Convert(converter = ListConverter.class)
    private List<String> artists;

    @Column
    private String album;

    @ManyToMany(mappedBy = "songs")
    private Set<Playlist> playlists;

    @Getter
    @AllArgsConstructor
    public class Transfer {
        private long id;
        private boolean active;
        private String name;
        private List<String> artists;
        private String album;
    }

    @Override
    public Transfer toTransfer() {
        return new Transfer(id, active, name, artists, album);
    }

    /**
     * Convierte entre una lista de strings, a un JSON, y viceversa
     */
    @Converter
    public static class ListConverter implements AttributeConverter<List<String>, String> {

        private final ObjectMapper objectMapper = new ObjectMapper();

        @Override
        public String convertToDatabaseColumn(List<String> artistas) {
            try {
                return objectMapper.writeValueAsString(artistas);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        public List<String> convertToEntityAttribute(String dbData) {
            try {
                return objectMapper.readValue(dbData,
                        new com.fasterxml.jackson.core.type.TypeReference<List<String>>() {
                        });
            } catch (JsonProcessingException e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}
