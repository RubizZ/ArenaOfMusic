package es.ucm.fdi.iw.dto.game;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoundInfoDTO {
    int roundNumber;
    Long songId;
    String songName;
    Map<Long, String> userAnswers = new HashMap<>();

    // @Override
    // public String toString() {
    // StringBuilder userAnswersString = new StringBuilder();
    // for (Map.Entry<Long, String> entry : userAnswers.entrySet()) {
    // if (userAnswersString.length() > 0) {
    // userAnswersString.append(";"); // Separador entre respuestas
    // }
    // userAnswersString.append(entry.getKey()).append(":").append(entry.getValue());
    // }
    // return "roundNumber=" + roundNumber +
    // "~song=" + songId + // Manejar valores nulos
    // "~userAnswers=" + userAnswersString;
    // }

    // public void parseRoundInfoDTO(String roundInfo) {
    // try {
    // String[] parts = roundInfo.split("~", 3); // Dividir en 3 partes principales
    // if (parts.length != 3) {
    // throw new IllegalArgumentException("Invalid input format: missing fields");
    // }

    // // Parse roundNumber
    // String[] roundNumberPart = parts[0].split("=");
    // if (roundNumberPart.length != 2 || roundNumberPart[1].isEmpty()) {
    // throw new IllegalArgumentException("Invalid roundNumber format");
    // }
    // this.roundNumber = Integer.parseInt(roundNumberPart[1]);

    // // Parse song
    // String[] songPart = parts[1].split("=");
    // if (songPart.length != 2) {
    // throw new IllegalArgumentException("Invalid song format");
    // }
    // this.songId = Long.parseLong(songPart[1]);

    // // Parse userAnswers
    // String[] userAnswersPart = parts[2].split("=");
    // if (userAnswersPart.length != 2) {
    // throw new IllegalArgumentException("Invalid userAnswers format");
    // }
    // String[] answers = userAnswersPart[1].split(";");
    // for (String answer : answers) {
    // if (!answer.isEmpty()) {
    // String[] keyValue = answer.split(":");
    // if (keyValue.length == 2) {
    // try {
    // Long key = Long.parseLong(keyValue[0]);
    // String value = keyValue[1];
    // userAnswers.put(key, value);
    // } catch (NumberFormatException e) {
    // throw new IllegalArgumentException("Invalid key in userAnswers: " +
    // keyValue[0]);
    // }
    // } else {
    // throw new IllegalArgumentException("Invalid answer format: " + answer);
    // }
    // }
    // }
    // } catch (Exception e) {
    // throw new RuntimeException("Failed to parse RoundInfoDTO: " + e.getMessage(),
    // e);
    // }
    // }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(roundNumber).append("|")
                .append(songId).append("|")
                .append(Base64.getEncoder().encodeToString(songName.getBytes())).append("|");

        userAnswers.forEach((key, value) -> {
            String encodedValue = Base64.getEncoder().encodeToString(value.getBytes());
            sb.append(key).append("=").append(encodedValue).append(";");
        });

        return sb.toString();
    }

    public static RoundInfoDTO parse(String input) {
        String[] parts = input.split("\\|", -1);
        RoundInfoDTO dto = new RoundInfoDTO();
        dto.roundNumber = Integer.parseInt(parts[0]);
        dto.songId = Long.parseLong(parts[1]);
        dto.songName = new String(Base64.getDecoder().decode(parts[2]));

        if (!parts[3].isEmpty()) {
            String[] entries = parts[3].split(";");
            for (String entry : entries) {
                if (!entry.isEmpty()) {
                    String[] kv = entry.split("=", 2);
                    Long key = Long.parseLong(kv[0]);
                    String value = new String(Base64.getDecoder().decode(kv[1]));
                    dto.userAnswers.put(key, value);
                }
            }
        }
        return dto;
    }
}
