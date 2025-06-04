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
                //String encodedRound = Base64.getEncoder().encodeToString(roundStr.getBytes());
                sb.append("{").append(roundStr).append("}");
            });
        }

        return sb.toString();
    }

    public GameRoundsDTO parse(String input) {
        String[] parts = input.split("\\|", 3);
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
                //String decodedRound = new String(Base64.getDecoder().decode(encodedRound));
                dto.rounds.add(RoundInfoDTO.parse(encodedRound));
                start = close + 1;
            }
        }
        return dto;
    }
}
