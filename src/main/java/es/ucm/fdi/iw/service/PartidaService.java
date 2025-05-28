package es.ucm.fdi.iw.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
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

import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
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
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PartidaService {

    @PersistenceContext
    private EntityManager entityManager;

    // GAME LOOP LOGIC

    @Transactional
    public UUID createGame(GameConfigDTO gameConfig) throws RuntimeException, IllegalArgumentException {
        // Crear una nueva partida
        try {
            // Verificar si la playlist existe y está activa
            Playlist playlist = entityManager.find(Playlist.class, gameConfig.getPlaylistId());
            if (playlist == null || !playlist.isActive()) {
                throw new IllegalArgumentException(
                        "La playlist seleccionada no existe o no se encuentra disponible.");
            }
            // Se crea la partida con la configuración dada, sin información de rondas, en
            // estado Waiting y con la playlist seleccionada
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

    @Transactional
    public void playerEntersTheGame(UUID gameId, long userId) {

        try {
            // Agregar el jugador a la partida
            PlayerGame pg = addPlayerIntoGame(userId, gameId);
            // Agrgar partida al registro del jugador
            addPlayerGameToUser(userId, pg);
            // Agregar jugador al registro de la partida
            addPlayerGameToGame(gameId, pg);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    @Transactional
    public PlayerGame addPlayerIntoGame(long userId, UUID gameId) {
        // Agregar el jugador a la partida
        Game game = entityManager.find(Game.class, gameId);
        User user = entityManager.find(User.class, userId);

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
        // Agrgar partida al registro del jugador
        User user = entityManager.find(User.class, userId);
        user.addPlayerGame(pg);
    }

    @Transactional
    public void addPlayerGameToGame(UUID gameId, PlayerGame pg) {
        // Agregar jugador al registro de la partida
        Game game = entityManager.find(Game.class, gameId);
        game.addPlayerGame(pg);
    }

    @Transactional
    public void loadSongs(Game game) {
        // Cargar las canciones de la playlist en la partida
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

        // Seleccionar las canciones para las rondas de la partida de 0 a 'rounds'
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
        // Iniciar la partida
        // Cargar las canciones para la partida
        loadSongs(game);
        // Settear el estado de la partida a "PLAYING"
        game.setGameState(Game.GameState.PLAYING);
    }

    @Transactional
    public RoundInfoDTO startRound(Game game) {
        // Iniciar una nueva ronda
        // Obtener la configuración del juego
        GameRoundsDTO gameRoundsDTO = new GameRoundsDTO();
        RoundInfoDTO roundInfo = new RoundInfoDTO();

        // Obtener la información de la ronda que toca
        gameRoundsDTO = gameRoundsDTO.parse(game.getRoundJson());
        Long songId = gameRoundsDTO.getSong(gameRoundsDTO.getRoundNumber());
        Song song = entityManager.find(Song.class, songId);

        GameConfigDTO gameConfig = new GameConfigDTO();
        gameConfig.parseGameConfigDTO(game.getConfigJson());
        if(gameConfig.getGameMode().equals("options")){
            List<String> options = new ArrayList<>();
            // Generar opciones aleatorias para la ronda
            List<Song> allSongs = getSongsByPlaylistId(game.getPlaylist().getId());
            allSongs.remove(song); // Eliminar la canción actual de las opciones
            Collections.shuffle(allSongs);
            // Seleccionar 3 canciones aleatorias diferentes
            for (int i = 0; i < 3 && i < allSongs.size(); i++) {
                options.add(allSongs.get(i).getName());
            }
            // Agregar la canción actual a las opciones
            options.add(song.getName());
            // Mezclar las opciones
            Collections.shuffle(options);
            // Establecer las opciones en la ronda
            roundInfo.setOptions(options);  
        }
        // Cargar la información de la nueva ronda
        roundInfo.setRoundNumber(gameRoundsDTO.getRoundNumber() + 1);
        roundInfo.setSongId(song.getId());
        gameRoundsDTO.addRound(roundInfo);
        game.setRoundJson(gameRoundsDTO.toString());
        entityManager.persist(game);

        return roundInfo;
    }

    @Transactional
    public RoundResponseDTO endRound(Game game, Map<Long, String> userAnswers) {
        // Terminar la ronda actual
        // Obtener la configuración del juego
        GameRoundsDTO gameRoundsDTO = new GameRoundsDTO();
        RoundInfoDTO roundInfo = new RoundInfoDTO();
        // Obtener la información de la ronda que termina
        RoundResponseDTO roundResponse = new RoundResponseDTO();
        gameRoundsDTO = gameRoundsDTO.parse(game.getRoundJson());
        roundInfo = gameRoundsDTO.getRound(gameRoundsDTO.getRoundNumber() - 1);
        gameRoundsDTO.setRound(gameRoundsDTO.getRoundNumber() - 1, roundInfo);
        Song song = entityManager.find(Song.class, gameRoundsDTO.getSong(gameRoundsDTO.getRoundNumber() - 1));
        roundResponse.setSongId(song.getId());
        roundResponse.setSongName(song.getName());

        // Procesar las respuestas de los jugadores
        Map<Long, Boolean> userTry = new HashMap<>();
        userAnswers.forEach((key, value) -> {

            PlayerGame playerGame = entityManager.find(PlayerGame.class,
                    new PlayerGameId(game.getId(), key));
            int score = 0;
            if (value.equalsIgnoreCase(song.getName())) {
                // Guardar el intento correcto
                score += 10;
                userTry.put(key, true);
            } else {
                // Guardar el intento incorrecto
                userTry.put(key, false);
            }
            if (playerGame != null) {
                // Actualizar el puntaje del jugador
                playerGame.setScore(playerGame.getScore() + score);
                entityManager.persist(playerGame);
            }
            // Guardar el puntaje del jugador en el resultado
            roundResponse.getResult().put(key, score);
        });
        roundInfo.setUserAnswers(userTry);
        game.setRoundJson(roundInfo.toString());

        // Actualizar la información de la ronda en el juego
        GameConfigDTO gameConfig = new GameConfigDTO();
        gameConfig.parseGameConfigDTO(game.getConfigJson());
        game.setRoundJson(gameRoundsDTO.toString());

        // Devolver el resultado de la ronda
        return roundResponse;
    }

    @Transactional
    public void endGame(Game game) {
        // Terminar la partida
        // Settear el estado de la partida a "FINISHED"
        game.setGameState(Game.GameState.FINISHED);

        // Obtener la lista de jugadores de la partida y ordenar por puntaje
        List<PlayerGame> players = game.getParticipants();
        PriorityQueue<PlayerGame> priorityQueue = new PriorityQueue<>(
                Comparator.comparingInt(PlayerGame::getScore).reversed());

        priorityQueue.addAll(players);

        // Obtener la información de la partida
        GameConfigDTO gameConfig = new GameConfigDTO();
        gameConfig.parseGameConfigDTO(game.getConfigJson());

        // Actualizar el puntaje de los jugadores y asignar posiciones
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
        }

    }

    @Transactional
    public void leaveGame(Game game) {
        // Abandonar la partida

        // Implementar la lógica para que el jugador abandone la partida
        // y se settee el estado de la partida a "ABANDONED" cuando no haya jugadores
        // (Cuando se implemente el modo de juego multiplayer)

        // Settear el estado de la partida a "ABANDONED"
        game.setGameState(Game.GameState.ABANDONED);
    }

    // FIN GAME LOOP LOGIC

    // GAME GETTERS and AUX METHODS

    public Map<String, Object> getPlaylist(Game game) {
        // Obtener la configuración del juego
        GameConfigDTO gameConfig = new GameConfigDTO();
        gameConfig.parseGameConfigDTO(game.getConfigJson());

        // Obtener la información de las rondas
        GameRoundsDTO rounds = new GameRoundsDTO();
        rounds = rounds.parse(game.getRoundJson());

        // Obtener la información de la playlist
        Playlist playlist = entityManager.find(Playlist.class, gameConfig.getPlaylistId());
        Map<String, Object> GamePlaylist = new HashMap<>();
        GamePlaylist.put("name", playlist.getName());
        GamePlaylist.put("songs", gameConfig.getRounds());

        // Obtener las de las canciones de la playlist
        List<Song> canciones = getSongsByGame(rounds.getSongsIds());
        GamePlaylist.put("canciones", canciones);

        return GamePlaylist;
    }

    private List<Song> getSongsByGame(List<Long> ids) {
        // Devuelve la información de las canciones de la lista
        return entityManager.createNamedQuery("Song.getSongsOfList", Song.class)
                .setParameter("ids", ids)
                .getResultList();
    }

    public PriorityQueue<PlayerGame> getSortedParticipants(Game game) {
        // Obtener la lista de jugadores de la partida
        List<PlayerGame> players = game.getParticipants();

        // Crear una PriorityQueue para ordenar a los jugadores por su puntaje
        PriorityQueue<PlayerGame> sortedParticipants = new PriorityQueue<>(
                Comparator.comparingInt(PlayerGame::getScore).reversed());
        sortedParticipants.addAll(players);

        return sortedParticipants;
    }

    public List<Map<String, Object>> getGameResults(GameRoundsDTO gameRoundsDTO) {
        // Obtener la información de las canciones y los resultados de los jugadores
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
        // Obtener la lista de playlists activas
        TypedQuery<Playlist> playlists;
        try {
            playlists = entityManager.createNamedQuery("Playlist.active", Playlist.class);
        } catch (Exception e) {
            throw new RuntimeException("No se pudieron obtener las playlist.");
        }
        return playlists.getResultList();
    }

    public Set<GamePlayerDTO> getGamePlayers(UUID gameId) {
        // Obtener la lista de jugadores de la partida
        Game game = entityManager.find(Game.class, gameId);

        List<PlayerGame> gamePlayers = game.getParticipants();
        Set<GamePlayerDTO> players = new HashSet<>();

        for (PlayerGame playerGame : gamePlayers) {
            // Obtener la información del jugador durante la partida
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
        // Obtener la lista de canciones de la playlist
        try {
            return entityManager.createNamedQuery("Song.findByPlaylistId", Song.class)
                    .setParameter("playlistId", playlistId)
                    .getResultList();
        } catch (Exception e) {
            throw new RuntimeException("No se pudieron obtener las canciones de la playlist.", e);
        }
    }

    public Game getGameById(UUID gameId) {
        // Obtener la partida por su ID
        Game game = entityManager.find(Game.class, gameId);
        if (game == null || !game.getActive()) {
            throw new IllegalArgumentException("La partida no existe.");
        }
        return game;
    }

    public Boolean isPlayerInGame(long userId, UUID gameId) {
        // Verificar si el jugador está en la partida
        PlayerGameId checkId = new PlayerGameId(gameId, userId);
        PlayerGame playerGame = entityManager.find(PlayerGame.class, checkId);
        return playerGame != null;
    }

    public List<String> getTitles() throws RuntimeException {
        // Obtener la lista de títulos de canciones activas
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
        // Obtener la posición del jugador en la partida
        PlayerGame playerGame = entityManager.find(PlayerGame.class,
                new PlayerGameId(game.getId(), id));
        if (playerGame != null) {
            return playerGame.getPosition();
        } else {
            throw new IllegalArgumentException("El jugador no está en la partida.");
        }
    }

    public File generateFragment(File audioOriginal, int duracion) throws IOException, InterruptedException {
        // Método para generar un fragmento de audio aleatorio de una canción usando
        // FFmpegFrameGrabber y FFmpegFrameRecorder
        // 1. Obtener duración total del audio
        avutil.av_log_set_level(avutil.AV_LOG_QUIET);
        try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(audioOriginal)) {
            grabber.start();
            double totalDuration = grabber.getLengthInTime() / 1_000_000.0; // microsegundos a segundos

            // 2. Si la canción es más corta que el tiempo requerido, devolver el original
            if (totalDuration <= duracion) {
                grabber.stop();
                return audioOriginal;
            }

            // 3. Calcular inicio aleatorio
            double start = Math.random() * (totalDuration - duracion);

            // 4. Crear archivo temporal
            File tempFile = File.createTempFile("fragment_", ".mp3");

            // 5. Posicionar el grabber en el tiempo de inicio
            grabber.setTimestamp((long) (start * 1_000_000)); // segundos a microsegundos

            // 6. Configurar el recorder
            try (FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(
                    tempFile, grabber.getAudioChannels())) {
                recorder.setFormat("mp3");
                recorder.setSampleRate(grabber.getSampleRate());
                recorder.setAudioChannels(grabber.getAudioChannels());
                recorder.setAudioCodec(org.bytedeco.ffmpeg.global.avcodec.AV_CODEC_ID_MP3);
                recorder.start();

                // 7. Grabar los frames de audio durante la duración solicitada
                long endTimestamp = (long) ((start + duracion) * 1_000_000);
                while (grabber.getTimestamp() < endTimestamp) {
                    org.bytedeco.javacv.Frame frame = grabber.grab();
                    if (frame == null)
                        break;
                    if (frame.samples != null) {
                        recorder.record(frame);
                    }
                }
                recorder.stop();
            }
            grabber.stop();

            // 8. Devolver el archivo temporal con el fragmento
            return tempFile;
        }
    }

    // FIN GAME GETTERS and AUX METHODS
}
