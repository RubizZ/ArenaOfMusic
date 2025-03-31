package es.ucm.fdi.iw.model;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlayerGameId implements Serializable {

    @Column(name = "game_id", nullable = false)
    private UUID gameId;

    @Column(name = "user_id", nullable = false)
    private Long userId;


    // Sobrescribir equals para comparar correctamente las claves compuestas
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        PlayerGameId that = (PlayerGameId) obj;
        return gameId.equals(that.gameId) && userId.equals(that.userId);
    }

    // Sobrescribir hashCode para generar un valor consistente
    @Override
    public int hashCode() {
        return Objects.hash(gameId, userId);
    }
}
