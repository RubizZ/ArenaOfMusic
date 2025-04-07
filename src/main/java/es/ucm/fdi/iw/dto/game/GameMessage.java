package es.ucm.fdi.iw.dto.game;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameMessage {
    private String type;
    private String message;
    private Object data;

    public GameMessage(String type, Object data) {
        this.type = type;
        this.data = data;
    }
}
