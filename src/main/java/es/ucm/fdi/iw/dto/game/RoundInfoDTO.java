package es.ucm.fdi.iw.dto.game;

import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
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
    Map<Long, Boolean> userAnswers = new HashMap<>();
    List<String> options = new ArrayList<>();

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(roundNumber).append("|")
          .append(songId).append("|");

        // Opciones separadas por coma y codificadas en Base64 para evitar problemas con caracteres especiales
        for (int i = 0; i < options.size(); i++) {
            sb.append(Base64.getEncoder().encodeToString(options.get(i).getBytes()));
            if (i < options.size() - 1) sb.append(";");
        }
        sb.append("|");

        userAnswers.forEach((key, value) -> {
            sb.append(key).append("=").append(value).append(";");
        });

        return sb.toString();
    }

    public static RoundInfoDTO parse(String input) {
        String[] parts = input.split("\\|", -1);
        RoundInfoDTO dto = new RoundInfoDTO();
        dto.roundNumber = Integer.parseInt(parts[0]);
        dto.songId = Long.parseLong(parts[1]);

        // Opciones
        dto.options = new ArrayList<>();
        if (!parts[2].isEmpty()) {
            String[] encodedOptions = parts[2].split(";");
            for (String encoded : encodedOptions) {
                if (!encoded.isEmpty()) {
                    dto.options.add(new String(Base64.getDecoder().decode(encoded)));
                }
            }
        }

        // userAnswers
        if (parts.length > 3 && !parts[3].isEmpty()) {
            String[] entries = parts[3].split(";");
            for (String entry : entries) {
                if (!entry.isEmpty()) {
                    String[] kv = entry.split("=", 2);
                    Long key = Long.parseLong(kv[0]);
                    Boolean value = Boolean.parseBoolean(kv[1]);
                    dto.userAnswers.put(key, value);
                }
            }
        }
        return dto;
    }
}
