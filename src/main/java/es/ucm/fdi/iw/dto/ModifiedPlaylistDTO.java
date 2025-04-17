package es.ucm.fdi.iw.dto;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import io.micrometer.common.lang.Nullable;
import lombok.Data;

@Data
public class ModifiedPlaylistDTO {

    private long id;

    @Nullable
    private String name;

    @Nullable
    private Boolean active;

    @Nullable
    private String desc;

    @Nullable
    private MultipartFile cover;

    @Nullable
    private List<Long> addedSongs;

    @Nullable
    private List<Long> removedSongs;
}
