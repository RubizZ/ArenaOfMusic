package es.ucm.fdi.iw.dto.game;

import java.util.ArrayList;
import java.util.Base64;
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
        if (roundNumber < 0 || roundNumber > songsIds.size()) {
            throw new IndexOutOfBoundsException("Round number out of bounds: " + roundNumber);
        }
        return songsIds.get(roundNumber);
    }

    public void setSong(int roundNumber, Long song) {
        if (roundNumber < 0 || roundNumber > songsIds.size()) {
            throw new IndexOutOfBoundsException("Round number out of bounds: " + roundNumber);
        }
        this.songsIds.set(roundNumber, song);
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

    // @Override
    // public String toString() {
    // // Construir el string de songsIds
    // String songsIdsString = String.join(";",
    // songsIds.stream().map(String::valueOf).toArray(String[]::new));

    // // Construir el string de rounds usando el toString de RoundInfoDTO
    // StringBuilder roundsString = new StringBuilder();
    // for (RoundInfoDTO round : rounds) {
    // if (roundsString.length() > 0) {
    // roundsString.append(";"); // Separador entre rondas
    // }
    // roundsString.append(round.toString()); // Usa el toString de RoundInfoDTO
    // }

    // // Construir el string final
    // return "songsIds=" + songsIdsString +
    // "|rounds=" + roundsString;
    // }

    // public void parseGameRoundsDTO(String gameRounds) {
    // try {
    // String[] parts = gameRounds.split("\\|"); // Separador principal
    // for (String part : parts) {
    // String[] keyValue = part.split("=");
    // if (keyValue.length == 2) {
    // if (keyValue[0].equals("songsIds")) {
    // String[] ids = keyValue[1].split(";");
    // for (String id : ids) {
    // this.songsIds.add(Long.parseLong(id));
    // }
    // } else if (keyValue[0].equals("rounds")) {
    // String[] roundsArray = keyValue[1].split(";");
    // for (String roundString : roundsArray) {
    // RoundInfoDTO round = new RoundInfoDTO();
    // round.parseRoundInfoDTO(roundString); // Usa el parse de RoundInfoDTO
    // this.rounds.add(round);
    // }
    // }
    // }
    // }
    // } catch (Exception e) {
    // throw new RuntimeException("Failed to parse GameRoundsDTO: " +
    // e.getMessage(), e);
    // }

    // }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        songsIds.forEach(id -> sb.append(id).append(","));
        if (!songsIds.isEmpty())
            sb.setLength(sb.length() - 1); // Quita la última coma

        sb.append("|").append(roundNumber).append("|");

        if (!rounds.isEmpty()) {
            rounds.forEach(r -> {
                String roundStr = r.toString();
                String encodedRound = Base64.getEncoder().encodeToString(roundStr.getBytes());
                sb.append("{").append(encodedRound).append("}");
            });
        }

        return sb.toString();
    }

    public static GameRoundsDTO parse(String input) {
        String[] parts = input.split("\\|", -1);
        GameRoundsDTO dto = new GameRoundsDTO();

        if (!parts[0].isEmpty()) {
            for (String id : parts[0].split(",")) {
                dto.songsIds.add(Long.parseLong(id));
            }
        }

        dto.roundNumber = Integer.parseInt(parts[1]);

        if (!parts[2].isEmpty()) {
            String roundsPart = parts[2];
            int start = 0;
            while (start < roundsPart.length()) {
                int open = roundsPart.indexOf('{', start);
                int close = roundsPart.indexOf('}', open);
                if (open == -1 || close == -1)
                    break;
                String encodedRound = roundsPart.substring(open + 1, close);
                String decodedRound = new String(Base64.getDecoder().decode(encodedRound));
                dto.rounds.add(RoundInfoDTO.parse(decodedRound));
                start = close + 1;
            }
        }
        return dto;
    }
}
