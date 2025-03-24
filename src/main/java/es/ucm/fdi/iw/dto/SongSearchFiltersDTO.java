package es.ucm.fdi.iw.dto;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import lombok.Data;

@Data
public class SongSearchFiltersDTO {
    @Nullable
    private Long id;
    @Nullable
    private Boolean active;
    @Nullable
    private String name;
    @Nullable
    private List<String> artists;
    @Nullable
    private String album;
}
