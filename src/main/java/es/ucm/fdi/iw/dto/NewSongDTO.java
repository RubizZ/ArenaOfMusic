package es.ucm.fdi.iw.dto;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public final class NewSongDTO {

    private MultipartFile audio;
    private MultipartFile cover;
    private String name;
    private boolean active;
    private List<String> artists;
    private String album;

}
