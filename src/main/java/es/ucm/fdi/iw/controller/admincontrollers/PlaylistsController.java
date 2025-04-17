package es.ucm.fdi.iw.controller.admincontrollers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import es.ucm.fdi.iw.model.Playlist;
import es.ucm.fdi.iw.model.Song;
import es.ucm.fdi.iw.service.PlaylistService;
import es.ucm.fdi.iw.service.SongService;
import es.ucm.fdi.iw.util.FileGetter;
import es.ucm.fdi.iw.util.NoDataException;
import es.ucm.fdi.iw.util.AudioConverter.AudioConversionException;
import es.ucm.fdi.iw.util.ImageConverter.ImageConversionException;
import es.ucm.fdi.iw.util.ImageConverter.UnsupportedImageException;
import es.ucm.fdi.iw.dto.ModifiedPlaylistDTO;
import es.ucm.fdi.iw.dto.ModifiedSongDTO;
import es.ucm.fdi.iw.dto.NewPlaylistDTO;
import es.ucm.fdi.iw.dto.NewSongDTO;
import es.ucm.fdi.iw.dto.PlaylistSearchFiltersDTO;
import es.ucm.fdi.iw.dto.SongSearchFiltersDTO;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping("admin/playlists")
public class PlaylistsController {

    @Autowired
    private SongService songService;

    @Autowired
    private PlaylistService playlistService;

    private static final List<String> availableViewTypes = List.of("new-playlist", "new-song", "list");

    @ModelAttribute
    public void populateModel(HttpSession session, Model model) {
        for (String name : new String[] { "u", "url", "ws" }) {
            model.addAttribute(name, session.getAttribute(name));
        }
    }

    @GetMapping({ "", "/" })
    public String index(Model model, @RequestParam(name = "view", required = false) String viewType,
            @RequestParam(required = false) String search, @RequestParam(required = false) String songUpload,
            @RequestParam(required = false) String playlistUpload,
            @RequestParam(required = false) String id) {// TODO Hacer que el GET acepte todo tipo de filtros
        model.addAttribute("viewType", viewType == null || viewType.isBlank() ? "list" : viewType);
        model.addAttribute("search", search == null || search.isBlank() ? "playlists" : search);

        if (songUpload != null) {
            model.addAttribute("songUpload", songUpload);
        }
        if (playlistUpload != null) {
            model.addAttribute("playlistUpload", playlistUpload);
        }
        if (id != null) {
            model.addAttribute("id", id);
        }
        return "admin/playlists";
    }

    @PostMapping("/submitSong")
    public ResponseEntity<?> submitSong(@ModelAttribute NewSongDTO song, Model model) {
        try {
            long id = songService.addNewSong(song);
            return ResponseEntity.ok(id);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (AudioConversionException | UnsupportedImageException | ImageConversionException e) {
            return ResponseEntity.status(415).body("Error en la conversion de archivos: " + e.getMessage());
        }
    }

    @PostMapping("/modifySong")
    public ResponseEntity<?> modifySong(@ModelAttribute ModifiedSongDTO song, Model model) {
        try {
            songService.modifyExistingSong(song);
            return ResponseEntity.ok().build();
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (AudioConversionException | ImageConversionException | UnsupportedImageException e) {
            return ResponseEntity.status(415).body("Error en la conversion de archivos: " + e.getMessage());
        }

    }

    @PostMapping("/deleteSong")
    public ResponseEntity<Void> deleteSong(@RequestParam Long id) {
        try {
            songService.deleteExistingSong(id);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).build();
        } catch (IOException e) {
            return ResponseEntity.status(500).build();
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/searchSongs")
    public ResponseEntity<Page<Song.Transfer>> searchSongs(@ModelAttribute SongSearchFiltersDTO filters,
            @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable,
            @RequestParam(required = false) Boolean all) {
        if (all != null && all)
            return ResponseEntity.ok(songService.searchSongs(filters));
        else
            return ResponseEntity.ok(songService.searchSongs(filters, pageable));
    }

    @PostMapping("/submitPlaylist")
    public ResponseEntity<?> submitPlaylist(@ModelAttribute NewPlaylistDTO npdto) {
        try {
            return ResponseEntity.ok(playlistService.addNewPlaylist(npdto));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        } catch (UnsupportedImageException | ImageConversionException e) {
            return ResponseEntity.status(415).body("Error en la conversion de la portada: " + e.getMessage());
        }
    }

    @PostMapping("/addSongToPlaylist")
    public ResponseEntity<?> addSongToPlaylist(@RequestParam Long playlistId, @RequestParam Long songId) {
        try {
            playlistService.addSongToPlaylist(playlistId, songId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/removeSongFromPlaylist")
    public ResponseEntity<Void> removeSongFromPlaylist(@RequestParam Long playlistId, @RequestParam Long songId) {
        try {
            playlistService.removeSongFromPlaylist(playlistId, songId);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/deletePlaylist")
    public ResponseEntity<Void> deletePlaylist(@RequestParam Long id) {
        try {
            playlistService.deletePlaylist(id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/searchPlaylists")
    public ResponseEntity<Page<Playlist.Transfer>> searchPlaylists(
            @ModelAttribute PlaylistSearchFiltersDTO filters,
            @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(playlistService.searchPlaylists(filters, pageable));
    }

    @PostMapping("/modifyPlaylist")
    public ResponseEntity<?> modifyPlaylist(@ModelAttribute ModifiedPlaylistDTO playlist) {
        try {
            playlistService.modifyPlaylist(playlist);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(e.getMessage());
        } catch (UnsupportedImageException | ImageConversionException e) {
            return ResponseEntity.status(415).body("Error en la conversion de la portada: " + e.getMessage());
        }
    }

    @GetMapping("/song/{id}/cover")
    public ResponseEntity<byte[]> getSongCover(@PathVariable Long id) {
        return responseEntityFromFileGetter(() -> songService.getSongCover(id));
    }

    @GetMapping("/song/{id}/audio")
    public ResponseEntity<byte[]> getSongAudio(@PathVariable Long id) {
        return responseEntityFromFileGetter(() -> songService.getSongAudio(id));
    }

    @GetMapping("/playlist/{id}/cover")
    public ResponseEntity<byte[]> getPlaylistCover(@PathVariable Long id) {
        return responseEntityFromFileGetter(() -> playlistService.getPlaylistCover(id));
    }

    // Funcion que se encarga de obtener un archivo por su id segun el proveedor que
    // se le pase
    private ResponseEntity<byte[]> responseEntityFromFileGetter(FileGetter fileGetter) {
        try {
            File file = fileGetter.get();

            byte[] fileContent = Files.readAllBytes(file.toPath());
            String contentType = Files.probeContentType(file.toPath());

            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.valueOf(contentType))
                    .body(fileContent);

        } catch (IOException e) {
            return ResponseEntity.status(500).body(new byte[0]);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(new byte[0]);
        } catch (NoDataException e) {
            return ResponseEntity.status(410).body(new byte[0]);
        }
    }

}
