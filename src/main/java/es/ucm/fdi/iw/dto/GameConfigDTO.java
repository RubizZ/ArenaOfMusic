package es.ucm.fdi.iw.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GameConfigDTO {
    long playlistId;

    String gameMode;

    int rounds;

    int fragmentDuration;
}
