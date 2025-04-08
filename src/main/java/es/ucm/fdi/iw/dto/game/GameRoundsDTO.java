package es.ucm.fdi.iw.dto.game;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameRoundsDTO {

    private List<Long> songsIds = new ArrayList<>();
    private int roundNumber;
    private List<RoundInfoDTO> rounds = new ArrayList<>();

    public Long getSong(int roundNumber) {
        if (roundNumber < 0 || roundNumber >= rounds.size()) {
            throw new IndexOutOfBoundsException("Round number out of bounds: " + roundNumber);
        }
        return rounds.get(roundNumber).getSong();
    }

    public void setSong(int roundNumber, Long song) {
        if (roundNumber < 0 || roundNumber >= rounds.size()) {
            throw new IndexOutOfBoundsException("Round number out of bounds: " + roundNumber);
        }
        this.rounds.get(roundNumber).setSong(song);
    }

    public RoundInfoDTO getRound(int roundNumber) {
        if (roundNumber < 0 || roundNumber >= rounds.size()) {
            throw new IndexOutOfBoundsException("Round number out of bounds: " + roundNumber);
        }
        return rounds.get(roundNumber);
    }

    public void setRound(int roundNumber, RoundInfoDTO round) {
        if (roundNumber < 0 || roundNumber >= rounds.size()) {
            throw new IndexOutOfBoundsException("Round number out of bounds: " + roundNumber);
        }
        this.rounds.set(roundNumber, round);
    }

    public void addRound(RoundInfoDTO round) {
        this.rounds.add(round);
        this.roundNumber++;
    }

    @Override
    public String toString() {
        // Construir el string de songsIds
        String songsIdsString = String.join(";", songsIds.stream().map(String::valueOf).toArray(String[]::new));

        // Construir el string de rounds usando el toString de RoundInfoDTO
        StringBuilder roundsString = new StringBuilder();
        for (RoundInfoDTO round : rounds) {
            if (roundsString.length() > 0) {
                roundsString.append(";"); // Separador entre rondas
            }
            roundsString.append(round.toString()); // Usa el toString de RoundInfoDTO
        }

        // Construir el string final
        return "songsIds=" + songsIdsString +
                "|roundNumber=" + roundNumber +
                "|rounds=" + roundsString;
    }

    public void parseGameRoundsDTO(String gameRounds) {
        try {
            String[] parts = gameRounds.split("\\|"); // Separador principal
            for (String part : parts) {
                String[] keyValue = part.split("=");
                if (keyValue.length == 2) {
                    if (keyValue[0].equals("songsIds")) {
                        String[] ids = keyValue[1].split(";");
                        for (String id : ids) {
                            this.songsIds.add(Long.parseLong(id));
                        }
                    } else if (keyValue[0].equals("roundNumber")) {
                        this.roundNumber = Integer.parseInt(keyValue[1]);
                    } else if (keyValue[0].equals("rounds")) {
                        String[] roundsArray = keyValue[1].split(";");
                        for (String roundString : roundsArray) {
                            RoundInfoDTO round = new RoundInfoDTO();
                            round.parseRoundInfoDTO(roundString); // Usa el parse de RoundInfoDTO
                            this.rounds.add(round);
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse GameRoundsDTO: " + e.getMessage(), e);
        }

    }
}
