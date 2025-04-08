package es.ucm.fdi.iw.dto.game;

import java.util.Map;

import es.ucm.fdi.iw.model.Song;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoundResponseDTO {
    private Song song;

    private Map<Long, Integer> result;
}
