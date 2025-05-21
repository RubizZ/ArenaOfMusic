package es.ucm.fdi.iw.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import es.ucm.fdi.iw.dto.game.GameConfigDTO;
import es.ucm.fdi.iw.dto.game.GamePlayerDTO;
import es.ucm.fdi.iw.dto.game.GameRoundsDTO;
import es.ucm.fdi.iw.dto.game.RoundInfoDTO;
import es.ucm.fdi.iw.dto.game.RoundResponseDTO;
import es.ucm.fdi.iw.model.Game;
import es.ucm.fdi.iw.model.PlayerGame;
import es.ucm.fdi.iw.model.PlayerGameId;
import es.ucm.fdi.iw.model.Playlist;
import es.ucm.fdi.iw.model.Song;
import es.ucm.fdi.iw.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PartidaService {

    @PersistenceContext
    private EntityManager entityManager;

    public Map<String, Object> getPlaylist(Game game) {
        GameConfigDTO gameConfig = new GameConfigDTO();
        gameConfig.parseGameConfigDTO(game.getConfigJson());
        GameRoundsDTO rounds = new GameRoundsDTO();
        rounds = rounds.parse(game.getRoundJson());

        Playlist playlist = entityManager.find(Playlist.class, gameConfig.getPlaylistId());
        Map<String, Object> GamePlaylist = new HashMap<>();
        GamePlaylist.put("name", playlist.getName());
        GamePlaylist.put("songs", gameConfig.getRounds());

        List<Song> canciones = getSongsByGame(rounds.getSongsIds());
        GamePlaylist.put("canciones", canciones);

        return GamePlaylist;
    }

    private List<Song> getSongsByGame(List<Long> ids) {
        return entityManager.createNamedQuery("Song.getSongsOfList", Song.class)
                .setParameter("ids", ids)
                .getResultList();
    }

    public PriorityQueue<PlayerGame> getSortedParticipants(Game game) {
        List<PlayerGame> players = game.getParticipants();
        PriorityQueue<PlayerGame> sortedParticipants = new PriorityQueue<>(
                Comparator.comparingInt(PlayerGame::getScore).reversed());

        sortedParticipants.addAll(players);

        return sortedParticipants;

    }

    public List<Map<String, Object>> getGameResults(GameRoundsDTO gameRoundsDTO) {
        List<Map<String, Object>> songResults = new ArrayList<>();
        for (int i = 0; i < gameRoundsDTO.getRounds().size(); i++) {
            Map<String, Object> songResult = new HashMap<>();
            RoundInfoDTO roundInfo = gameRoundsDTO.getRound(i);
            Song song = entityManager.find(Song.class, roundInfo.getSongId());

            Map<String, Object> songInfo = new HashMap<>();
            songResult.put("id", song.getId());
            songInfo.put("name", song.getName());
            songInfo.put("artists", song.getArtists());

            songResult.put("song", songInfo);
            songResult.put("responses", roundInfo.getUserAnswers());

            songResults.add(songResult);
        }
        return songResults;
    }

    public List<Playlist> getActivePlaylists() {
        TypedQuery<Playlist> playlists;
        try {
            playlists = entityManager.createNamedQuery("Playlist.active", Playlist.class);
        } catch (Exception e) {
            throw new RuntimeException("No se pudieron obtener las playlist.");
        }
        return playlists.getResultList();
    }

    @Transactional
    public UUID createPartida(GameConfigDTO gameConfig) throws RuntimeException, IllegalArgumentException {
        try {
            Playlist playlist = entityManager.find(Playlist.class, gameConfig.getPlaylistId());
            if (playlist == null || !playlist.isActive()) {
                throw new IllegalArgumentException(
                        "La playlist seleccionada no existe o no se encuentra disponible.");
            }

            Game game = new Game();
            game.setConfigJson(gameConfig.toString());
            game.setRoundJson("[]");
            game.setGameState(Game.GameState.WAITING);
            game.setPlaylist(playlist);

            entityManager.persist(game);

            return game.getId();
        } catch (Exception e) {
            throw new RuntimeException("No se pudo crear la partida.");
        }
    }

    public Game getGameById(UUID gameId) {
        Game game = entityManager.find(Game.class, gameId);
        if (game == null || !game.getActive()) {
            throw new IllegalArgumentException("La partida no existe.");
        }
        return game;
    }

    public Boolean isPlayerInGame(long userId, UUID gameId) {
        PlayerGameId checkId = new PlayerGameId(gameId, userId);
        PlayerGame playerGame = entityManager.find(PlayerGame.class, checkId);
        return playerGame != null;
    }
    @Transactional
    public PlayerGame addPlayerIntoGame(long userId, UUID gameId)
            throws IllegalArgumentException, IllegalStateException {
        Game game = entityManager.find(Game.class, gameId);
        User user = entityManager.find(User.class, userId);

        PlayerGameId checkId = new PlayerGameId(gameId, userId);
        if (entityManager.find(PlayerGame.class, checkId) != null) {
            throw new IllegalStateException("El usuario ya está en esta partida.");
        }

        PlayerGame playerGame = new PlayerGame();

        playerGame.setGame(game);
        playerGame.setUser(user);

        PlayerGameId playerGameId = new PlayerGameId(game.getId(), user.getId());
        playerGame.setId(playerGameId);

        playerGame.setScore(0);
        playerGame.setPosition(0);

        entityManager.persist(playerGame);

        GameConfigDTO gameConfig = new GameConfigDTO();
        gameConfig.parseGameConfigDTO(game.getConfigJson());
        gameConfig.setNumPlayers(gameConfig.getNumPlayers() + 1);
        game.setConfigJson(gameConfig.toString());

        return playerGame;
    }

    @Transactional
    public void addPlayerGameToUser(long userId, PlayerGame pg) {
        User user = entityManager.find(User.class, userId);
        user.addPlayerGame(pg);
    }

    @Transactional
    public void addPlayerGameToGame(UUID gameId, PlayerGame pg) {
        Game game = entityManager.find(Game.class, gameId);
        game.addPlayerGame(pg);
    }

    @Transactional
    public void addPlayerToGame(UUID gameId, long userId) throws Exception {
        try {
            PlayerGame pg = addPlayerIntoGame(userId, gameId);
            addPlayerGameToUser(userId, pg);
            addPlayerGameToGame(gameId, pg);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }

    }

    public Set<GamePlayerDTO> getGamePlayers(UUID gameId) {
        Game game = entityManager.find(Game.class, gameId);

        List<PlayerGame> gamePlayers = game.getParticipants();
        Set<GamePlayerDTO> players = new HashSet<>();

        for (PlayerGame playerGame : gamePlayers) {
            User player = entityManager.find(User.class, playerGame.getUser().getId());
            GamePlayerDTO playerDTO = new GamePlayerDTO();
            playerDTO.setId(player.getId());
            playerDTO.setUsername(player.getUsername());
            playerDTO.setEXP_total(player.getEXP_total());
            playerDTO.setTotalWins(entityManager.createNamedQuery("PlayerGame.countWinsByUser", Long.class)
                    .setParameter("userId", player.getId())
                    .getSingleResult());
            playerDTO.setProfileImage(player.getProfileImage());
            players.add(playerDTO);
        }

        return players;
    }

    public List<Song> getSongsByPlaylistId(long playlistId) {
        try {
            return entityManager.createNamedQuery("Song.findByPlaylistId", Song.class)
                    .setParameter("playlistId", playlistId)
                    .getResultList();
        } catch (Exception e) {
            throw new RuntimeException("No se pudieron obtener las canciones de la playlist.", e);
        }
    }

    @Transactional
    public void cargarCanciones(Game game) {
        GameConfigDTO gameConfigDTO = new GameConfigDTO();
        gameConfigDTO.parseGameConfigDTO(game.getConfigJson());

        Playlist playlist = game.getPlaylist();

        List<Song> cancionesAleatorias = getSongsByPlaylistId(playlist.getId());

        // Verificar si hay suficientes canciones para el número de rondas
        if (cancionesAleatorias.size() < gameConfigDTO.getRounds()) {
            throw new IllegalArgumentException(
                    "No hay suficientes canciones en la playlist para el número de rondas configurado.");
        }

        // Mezclar las canciones aleatoriamente
        Collections.shuffle(cancionesAleatorias);

        // Seleccionar las primeras `gameConfigDTO.getRounds()` canciones
        cancionesAleatorias = cancionesAleatorias.subList(0, gameConfigDTO.getRounds());

        GameRoundsDTO gameRoundsDTO = new GameRoundsDTO();
        gameRoundsDTO.setSongsIds(cancionesAleatorias.stream()
                .map(Song::getId)
                .collect(Collectors.toList()));

        gameRoundsDTO.setRoundNumber(0);

        game.setRoundJson(gameRoundsDTO.toString());

    }

    @Transactional
    public void startGame(Game game) {
        cargarCanciones(game);
        game.setGameState(Game.GameState.PLAYING);
    }

    @Transactional
    public RoundInfoDTO iniciarRonda(Game game) {
        GameRoundsDTO gameRoundsDTO = new GameRoundsDTO();
        RoundInfoDTO roundInfo = new RoundInfoDTO();

        gameRoundsDTO = gameRoundsDTO.parse(game.getRoundJson());
        Long songId = gameRoundsDTO.getSong(gameRoundsDTO.getRoundNumber());
        Song song = entityManager.find(Song.class, songId);

        roundInfo.setRoundNumber(gameRoundsDTO.getRoundNumber() + 1);
        roundInfo.setSongId(song.getId());
        // roundInfo.setSongName(song.getName());
        gameRoundsDTO.addRound(roundInfo);
        game.setRoundJson(gameRoundsDTO.toString());
        entityManager.persist(game);

        return roundInfo;
    }

    @Transactional
    public RoundResponseDTO finalizarRonda(Game game, Map<Long, String> userAnswers) {
        GameRoundsDTO gameRoundsDTO = new GameRoundsDTO();
        RoundInfoDTO roundInfo = new RoundInfoDTO();
        RoundResponseDTO roundResponse = new RoundResponseDTO();
        gameRoundsDTO = gameRoundsDTO.parse(game.getRoundJson());
        roundInfo = gameRoundsDTO.getRound(gameRoundsDTO.getRoundNumber() - 1);
        gameRoundsDTO.setRound(gameRoundsDTO.getRoundNumber() - 1, roundInfo);
        Song song = entityManager.find(Song.class, gameRoundsDTO.getSong(gameRoundsDTO.getRoundNumber() - 1));
        roundResponse.setSongId(song.getId());
        roundResponse.setSongName(song.getName());

        Map<Long, Boolean> userTry = new HashMap<>();
        userAnswers.forEach((key, value) -> {
            PlayerGame playerGame = entityManager.find(PlayerGame.class,
                    new PlayerGameId(game.getId(), key));
            int score = 0;
            if (value.equalsIgnoreCase(song.getName())) {
                score += 10;
                userTry.put(key, true); // Guardar el intento correcto
            } else {
                userTry.put(key, false); // Guardar el intento incorrecto
            }
            if (playerGame != null) {
                playerGame.setScore(playerGame.getScore() + score); // Incrementar el puntaje del jugador
                entityManager.persist(playerGame); // Persistir el cambio en la base de datos
            }
            roundResponse.getResult().put(key, score); // Guardar el puntaje del jugador en el resultado
        });
        roundInfo.setUserAnswers(userTry);
        game.setRoundJson(roundInfo.toString());

        GameConfigDTO gameConfig = new GameConfigDTO();
        gameConfig.parseGameConfigDTO(game.getConfigJson());
        game.setRoundJson(gameRoundsDTO.toString());

        return roundResponse;
    }

    @Transactional
    public void finalizarPartida(Game game) {

        game.setGameState(Game.GameState.FINISHED);

        List<PlayerGame> players = game.getParticipants();
        PriorityQueue<PlayerGame> priorityQueue = new PriorityQueue<>(
                Comparator.comparingInt(PlayerGame::getScore).reversed());

        priorityQueue.addAll(players);

        GameConfigDTO gameConfig = new GameConfigDTO();
        gameConfig.parseGameConfigDTO(game.getConfigJson());

        int position = gameConfig.getMaxPlayers() > 1 ? 1 : 0;
        while (!priorityQueue.isEmpty()) {
            PlayerGame playerGame = priorityQueue.poll();
            User user = entityManager.find(User.class, playerGame.getUser().getId());
            if (user != null) {
                user.setEXP(user.getEXP() + playerGame.getScore());
                user.setEXP_total(user.getEXP_total() + playerGame.getScore());
                System.out.println("El jugador " + user.getUsername() + " ha ganado " + playerGame.getScore()
                        + " puntos de EXP.");
            }
            playerGame.setPosition(position++);
            // position++;
        }

    }

    public List<String> getTitles() {
        List<String> titulos = new ArrayList<>();
        try {
            titulos = entityManager.createNamedQuery("Song.getActiveSongsTitles", String.class)
                    .getResultList();
        } catch (Exception e) {
            throw new RuntimeException("No se pudieron obtener los títulos de las canciones.", e);
        }
        return titulos;
    }

    public int getPosition(Game game, long id) {

        PlayerGame playerGame = entityManager.find(PlayerGame.class,
                new PlayerGameId(game.getId(), id));
        if (playerGame != null) {
            return playerGame.getPosition();
        } else {
            throw new IllegalArgumentException("El jugador no está en la partida.");
        }
    }

    @Transactional
    public void abandonarPartida(Game game) {
        game.setGameState(Game.GameState.ABANDONED);
    }

    public File generarFragmento(File audioOriginal, int duracion) throws IOException, InterruptedException {
        // 1. Obtener duración total del audio
        ProcessBuilder probeBuilder = new ProcessBuilder(
                "ffprobe", "-v", "error",
                "-show_entries", "format=duration",
                "-of", "default=noprint_wrappers=1:nokey=1",
                audioOriginal.getAbsolutePath());
        Process probe = probeBuilder.start();
        BufferedReader reader = new BufferedReader(new InputStreamReader(probe.getInputStream()));
        double totalDuration = Double.parseDouble(reader.readLine().trim());
        probe.waitFor();

        // 2. Si la canción es más corta, devolver el original
        if (totalDuration <= duracion)
            return audioOriginal;

        // 3. Calcular inicio aleatorio
        double start = Math.random() * (totalDuration - duracion);

        // 4. Crear archivo temporal
        File tempFile = File.createTempFile("fragment_", ".mp3");

        // 5. Ejecutar ffmpeg para recortar
        ProcessBuilder ffmpegBuilder = new ProcessBuilder(
                "ffmpeg", "-y",
                "-ss", String.valueOf(start),
                "-t", String.valueOf(duracion),
                "-i", audioOriginal.getAbsolutePath(),
                "-c:a", "libmp3lame",
                tempFile.getAbsolutePath());
        ffmpegBuilder.redirectErrorStream(true);
        Process ffmpeg = ffmpegBuilder.start();
        ffmpeg.waitFor();

        return tempFile;
    }

}
