package es.ucm.fdi.iw.dto;

import java.util.List;

import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NewPlaylistDTO {

    private String name;

    @Nullable
    private Boolean active;

    @Nullable
    private MultipartFile cover;

    @Nullable
    private String desc;
}
