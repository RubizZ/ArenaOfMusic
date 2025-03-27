package es.ucm.fdi.iw.service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.ucm.fdi.iw.dto.NewPlaylistDTO;
import es.ucm.fdi.iw.model.Playlist;
import es.ucm.fdi.iw.model.Song;
import es.ucm.fdi.iw.util.ImageConverter.ImageConversionException;
import es.ucm.fdi.iw.util.ImageConverter.UnsupportedImageException;
import es.ucm.fdi.iw.util.ImageConverter;
import es.ucm.fdi.iw.util.NoDataException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;

@Service
public class PlaylistService {

    @Autowired
    private EntityManager entityManager;

    private static final String UPLOAD_DIR = "iwdata/playlists/";

    @Transactional
    public long addNewPlaylist(NewPlaylistDTO npdto) throws IllegalArgumentException, IOException,
            UnsupportedImageException, ImageConversionException {

        if (npdto.getName() == null || npdto.getName().isBlank()) {
            throw new IllegalArgumentException("El nombre de la playlist no puede estar vacio");
        }

        Playlist playlist = new Playlist();

        playlist.setName(npdto.getName());
        playlist.setActive(npdto.getActive() != null ? npdto.getActive() : true);
        playlist.setDescription(npdto.getDesc());

        entityManager.persist(playlist);

        if (npdto.getCover() == null)
            return playlist.getId();

        try {
            Path path = Paths.get(UPLOAD_DIR + playlist.getId());
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            } else {
                deleteDirectoryCascade(path);
                Files.createDirectories(path);
            }

            File imgDest = new File(UPLOAD_DIR + playlist.getId() + "/cover.webp");
            ImageConverter.readAndConvertImage(npdto.getCover().getInputStream(), imgDest.toPath());
            return playlist.getId();

        } catch (IOException e) {
            entityManager.remove(playlist);

            Path dirPath = Paths.get(UPLOAD_DIR + playlist.getId());

            IOException cleanupException = null;
            try {
                deleteDirectoryCascade(dirPath);
            } catch (IOException ex) {
                cleanupException = ex;
            }

            if (cleanupException != null) {
                e.addSuppressed(cleanupException);
            }

            throw e;
        }
    }

    private void deleteDirectoryCascade(Path dirPath) throws IOException {
        try {
            Files.walkFileTree(dirPath, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (NoSuchFileException ex) {
            // No hacer nada, el directorio ya no existe
        } catch (IOException ex) {
            throw new IOException("Error al eliminar el directorio o su contenido: " + dirPath, ex);
        }
    }

    @Transactional(rollbackFor = { IOException.class })
    public void deletePlaylist(long id) throws IOException {
        Playlist playlist = entityManager.find(Playlist.class, id);
        if (playlist == null)
            throw new IllegalArgumentException("La playlist no existe");

        entityManager.remove(playlist);

        try {
            deleteDirectoryCascade(Paths.get(UPLOAD_DIR + id));
        } catch (IOException e) {
            disablePlaylist(id);
            throw new IOException("No se han podido eliminar los archivos de la playlist", e);
        }
    }

    @Transactional
    public void disablePlaylist(long id) {
        Playlist playlist = entityManager.find(Playlist.class, id);
        playlist.setActive(false);
        entityManager.persist(playlist);
    }

    @Transactional
    public void addSongToPlaylist(long playlistId, long songId) throws IllegalArgumentException {
        Playlist playlist = entityManager.find(Playlist.class, playlistId);
        if (playlist == null)
            throw new IllegalArgumentException("La playlist no existe");

        Song song = entityManager.find(Song.class, songId);
        if (song == null)
            throw new IllegalArgumentException("La cancion no existe");

        Query query = entityManager.createNativeQuery(
                "INSERT INTO playlist_song (playlist_id, song_id) " +
                        "SELECT :playlistId, :songId " +
                        "WHERE NOT EXISTS (" +
                        "  SELECT 1 FROM playlist_song " +
                        "  WHERE playlist_id = :playlistId AND song_id = :songId" +
                        ")");
        query.setParameter("playlistId", playlistId);
        query.setParameter("songId", songId);

        int rowsAffected = query.executeUpdate();

        if (rowsAffected == 0) {
            throw new IllegalArgumentException("La cancion ya esta en la playlist");
        }
    }

    @Transactional
    public void removeSongFromPlaylist(long playlistId, long songId) {
        Playlist playlist = entityManager.find(Playlist.class, playlistId);
        if (playlist == null)
            throw new IllegalArgumentException("La playlist no existe");

        Song song = entityManager.find(Song.class, songId);
        if (song == null)
            throw new IllegalArgumentException("La cancion no existe");

        Query query = entityManager.createNativeQuery(
                "DELETE FROM playlist_song " +
                        "WHERE playlist_id = :playlistId AND song_id = :songId");
        query.setParameter("playlistId", playlistId);
        query.setParameter("songId", songId);

        int rowsAffected = query.executeUpdate();

        if (rowsAffected == 0) {
            throw new IllegalArgumentException("La cancion no esta en la playlist");
        }
    }

    public File getPlaylistCover(long id) throws IllegalArgumentException, NoDataException {
        Playlist playlist = entityManager.find(Playlist.class, id);
        if (playlist == null)
            throw new IllegalArgumentException("La playlist no existe");

        File cover = new File(UPLOAD_DIR + id + "/cover.webp");
        if (!cover.exists())
            throw new NoDataException("La playlist no tiene imagen de portada");
        return cover;
    }

}
