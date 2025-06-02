package es.ucm.fdi.iw.controller;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import es.ucm.fdi.iw.LocalData;
import es.ucm.fdi.iw.dto.game.GameRoundsDTO;
import es.ucm.fdi.iw.model.Game;
import es.ucm.fdi.iw.model.User;
import es.ucm.fdi.iw.service.PartidaService;
import es.ucm.fdi.iw.service.PerfilService;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Controller
public class PerfilController {
    @ModelAttribute
    public void populateModel(HttpSession session, Model model) {
        for (String name : new String[] { "u", "url", "ws" }) {
            model.addAttribute(name, session.getAttribute(name));
        }
    }

    @Autowired
    private PerfilService perfilService;
    @Autowired
    private PartidaService partidaService;

    private static Log log = LogFactory.getLog(LocalData.class);

    @Getter
    @AllArgsConstructor
    public static class GameSummary {
        private UUID gameId;
        private int position;
        private int guessedSongs;
        private int totalSongs;
        private String playlistName;
        private String creationDateTime;
    }

    @GetMapping("/perfil")
    public String perfil(Model model, HttpSession session) {

        User user = (User) session.getAttribute("u");

        List<Game> games = perfilService.getUserGames(user);
        games.sort(Comparator.comparing(Game::getCreationDateTime).reversed());

        List<GameSummary> summaries = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy - HH:mm");

        for (Game game : games) {
            try {
                if (!game.getActive() || game.getGameState() != Game.GameState.FINISHED)
                    continue;

                int position = partidaService.getPosition(game, user.getId());

                GameRoundsDTO gameRounds = new GameRoundsDTO().parse(game.getRoundJson());
                List<Map<String, Object>> songResults = partidaService.getGameResults(gameRounds);

                int guessedSongs = 0;
                for (var p : game.getParticipants()) {
                    if (p.getUser().getId() == user.getId()) {
                        guessedSongs = p.getScore() / 10;
                        break;
                    }
                }
                String playlistName = game.getPlaylist().getName();
                int totalSongs = songResults.size();
                String formattedDate = sdf.format(game.getCreationDateTime());

                summaries.add(new GameSummary(
                        game.getId(),
                        position,
                        guessedSongs,
                        totalSongs,
                        playlistName,
                        formattedDate));

            } catch (Exception e) {
                // log error se vuoi
                continue;
            }
        }

        model.addAttribute("games", summaries);
        return "perfil";
    }

    @PostMapping("/perfil/editar")
    @ResponseBody
    public ResponseEntity<?> editarPerfilJson(
            @RequestBody Map<String, String> data,
            HttpSession session) {
        User user = perfilService.findById(((User) session.getAttribute("u")).getId());

        try {
            perfilService.actualizarPerfil(
                    user,
                    data.get("username"),
                    data.get("email"),
                    data.get("description"),
                    data.get("oldPassword"),
                    data.get("password"), data.get("img"));

            session.setAttribute("u", perfilService.findById(user.getId())); // actualiza sesión
            return ResponseEntity.ok(Map.of("message", "Perfil actualizado correctamente"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}