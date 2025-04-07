package es.ucm.fdi.iw.dto.game;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GamePlayerDTO {

    private long id;
    
    private String username;
}
