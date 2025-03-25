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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import es.ucm.fdi.iw.model.Song;
import es.ucm.fdi.iw.service.SongService;
import es.ucm.fdi.iw.service.SongService.NoDataException;
import es.ucm.fdi.iw.dto.ModifiedSongDTO;
import es.ucm.fdi.iw.dto.NewSongDTO;
import es.ucm.fdi.iw.dto.SongSearchFiltersDTO;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping("admin/playlists")
public class PlaylistsController {

    @Autowired
    private SongService songService;

    private static final List<String> availableViewTypes = List.of("new-playlist", "new-song", "list");

    @ModelAttribute
    public void populateModel(HttpSession session, Model model) {
        for (String name : new String[] { "u", "url", "ws" }) {
            model.addAttribute(name, session.getAttribute(name));
        }
    }

    @GetMapping({ "", "/" })
    public String index(@RequestParam(name = "view", required = false) String viewType, Model model) {
        model.addAttribute("viewType", viewType == null || viewType.isBlank() ? "list" : viewType);
        return "admin/playlists";
    }

    @PostMapping("/submitSong")
    public String submitSong(RedirectAttributes redirectAttributes,
            @ModelAttribute NewSongDTO song, Model model) throws IOException {

        try {
            songService.addNewSong(song);
        } catch (IOException e) {
            // TODO
            e.printStackTrace();
        } catch (RuntimeException e) {
            // TODO
            e.printStackTrace();
        }

        redirectAttributes.addFlashAttribute("list", "songs"); // TODO fix
        return "redirect:/admin/playlists";
    }

    @PostMapping("/modifySong")
    public ResponseEntity<Void> modifySong(RedirectAttributes redirectAttributes,
            @ModelAttribute ModifiedSongDTO song, Model model) throws IOException {

        try {
            songService.modifyExistingSong(song);
        } catch (IOException e) {
            // TODO
            e.printStackTrace();
        } catch (RuntimeException e) {
            // TODO
            e.printStackTrace();
        }

        return ResponseEntity.ok().build();
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
            @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(songService.searchSongs(filters, pageable));
    }

    @GetMapping("/song/{id}/cover")
    public ResponseEntity<byte[]> getSongCover(@PathVariable Long id) {

        try {
            File file = songService.getSongCover(id);

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

    @GetMapping("/song/{id}/audio")
    public ResponseEntity<byte[]> getSongAudio(@PathVariable Long id) {
        try {
            File file = songService.getSongAudio(id);

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
