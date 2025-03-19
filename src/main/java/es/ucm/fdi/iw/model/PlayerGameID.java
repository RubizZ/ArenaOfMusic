package es.ucm.fdi.iw.model;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayerGameID implements Serializable {
    @Column(name = "game_id", nullable = false)
    private Long gameId;

    @Column(name = "user_id", nullable = false)
    private Long userId;
}
