package es.ucm.fdi.iw.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameConfigDTO {
    long playlistId;

    String gameMode;

    int rounds;

    int fragmentDuration;

    public void parseGameConfigDTO(String gameConfig) {
        String[] parts = gameConfig.split(",");
        this.playlistId = Long.parseLong(parts[0].split("=")[1]);
        this.gameMode = parts[1].split("=")[1];
        this.rounds = Integer.parseInt(parts[2].split(gameConfig)[1]);
        this.fragmentDuration = Integer.parseInt(parts[3].split("=")[1]);
    }

    @Override
    public String toString() {
        return  "playlistId=" + playlistId +
                ", gameMode='" + gameMode +
                ", rounds=" + rounds +
                ", fragmentDuration=" + fragmentDuration;
    }
}
