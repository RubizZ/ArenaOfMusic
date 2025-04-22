package es.ucm.fdi.iw.dto.game;

import java.util.LinkedHashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoundResponseDTO {
    private Long songId;
    private String songName;
    private Map<Long, Integer> result = new LinkedHashMap<>();
}
