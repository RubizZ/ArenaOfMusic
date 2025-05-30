package es.ucm.fdi.iw.dto.game;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameConfigDTO {
    long playlistId;

    String gameAnswerMode;

    String answerType;

    int rounds;

    int fragmentDuration;

    long hostId;

    int maxPlayers;

    int numPlayers;

    Boolean multiplayer;

    public void parseGameConfigDTO(String gameConfig) {
        String[] parts = gameConfig.split(",");
        this.playlistId = Long.parseLong(parts[0].split("=")[1]);
        this.gameAnswerMode = parts[1].split("=")[1];
        this.answerType = parts[2].split("=")[1];
        this.rounds = Integer.parseInt(parts[3].split("=")[1]);
        this.fragmentDuration = Integer.parseInt(parts[4].split("=")[1]);
        this.hostId = Long.parseLong(parts[5].split("=")[1]);
        this.maxPlayers = Integer.parseInt(parts[6].split("=")[1]);
        this.numPlayers = Integer.parseInt(parts[7].split("=")[1]);
        this.multiplayer = Boolean.parseBoolean(parts[8].split("=")[1]);
    }

    @Override
    public String toString() {
        return "playlistId=" + playlistId +
                ", gameAnswerMode=" + gameAnswerMode +
                ", answerType=" + answerType +
                ", rounds=" + rounds +
                ", fragmentDuration=" + fragmentDuration +
                ", hostId=" + hostId +
                ", maxPlayers=" + maxPlayers +
                ", numPlayers=" + numPlayers +
                ", multiplayer=" + multiplayer;
    }
}
