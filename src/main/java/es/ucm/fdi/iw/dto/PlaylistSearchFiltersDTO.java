package es.ucm.fdi.iw.dto;

import org.springframework.lang.Nullable;

import lombok.Data;

@Data
public class PlaylistSearchFiltersDTO {

    @Nullable
    private Long id;

    @Nullable
    private String name;

}
