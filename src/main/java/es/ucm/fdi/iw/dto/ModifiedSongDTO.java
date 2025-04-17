package es.ucm.fdi.iw.dto;

import java.util.List;

import org.springframework.lang.Nullable;
import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public final class ModifiedSongDTO {

    private long id;

    @Nullable
    private MultipartFile audio = null;

    @Nullable
    private MultipartFile cover = null;

    @Nullable
    private String name = null;

    @Nullable
    private Boolean active = null;

    @Nullable
    private List<String> artists = null;

    @Nullable
    private String album = null;

}