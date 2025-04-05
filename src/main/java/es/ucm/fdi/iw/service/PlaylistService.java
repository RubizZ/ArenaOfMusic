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
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.ucm.fdi.iw.dto.ModifiedPlaylistDTO;
import es.ucm.fdi.iw.dto.NewPlaylistDTO;
import es.ucm.fdi.iw.dto.PlaylistSearchFiltersDTO;
import es.ucm.fdi.iw.model.Playlist;
import es.ucm.fdi.iw.model.Song;
import es.ucm.fdi.iw.util.ImageConverter.ImageConversionException;
import es.ucm.fdi.iw.util.ImageConverter.UnsupportedImageException;
import es.ucm.fdi.iw.util.ImageConverter;
import es.ucm.fdi.iw.util.NoDataException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;

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

        String checkSql = "SELECT COUNT(*) FROM playlist_song WHERE playlist_id = ?1 AND song_id = ?2";
        Long count = ((Number) entityManager.createNativeQuery(checkSql)
                .setParameter(1, playlistId)
                .setParameter(2, songId)
                .getSingleResult()).longValue();

        if (count > 0) {
            throw new IllegalArgumentException("La cancion ya esta en la playlist");
        }

        String insertSql = "INSERT INTO playlist_song (playlist_id, song_id) VALUES (?1, ?2)";
        entityManager.createNativeQuery(insertSql)
                .setParameter(1, playlistId)
                .setParameter(2, songId)
                .executeUpdate();

        entityManager.clear();
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

    public Page<Playlist.Transfer> searchPlaylists(PlaylistSearchFiltersDTO filters,
            Pageable pageable) {

        StringBuilder queryBuilder = new StringBuilder("SELECT p FROM Playlist p WHERE 1=1");
        if (filters.getId() != null) {
            queryBuilder.append(" AND p.id = :id");
        }
        if (filters.getName() != null) {
            queryBuilder.append(" AND p.name LIKE :name");
        }
        if (pageable.isPaged() && pageable.getSort().isSorted()) {
            queryBuilder.append(" ORDER BY ");
            pageable.getSort().forEach(order -> {
                queryBuilder.append("p.").append(order.getProperty()).append(" ").append(order.getDirection())
                        .append(", ");
            });
            queryBuilder.setLength(queryBuilder.length() - 2);
        }

        TypedQuery<Playlist> query = entityManager.createQuery(queryBuilder.toString(), Playlist.class);
        if (filters.getId() != null) {
            query.setParameter("id", filters.getId());
        }
        if (filters.getName() != null) {
            query.setParameter("name", "%" + filters.getName() + "%");
        }
        if (pageable.isPaged()) {
            query.setFirstResult((int) pageable.getOffset());
            query.setMaxResults(pageable.getPageSize());
        }
        List<Playlist> playlists = query.getResultList();
        List<Playlist.Transfer> transfers = playlists.stream()
                .map(playlist -> playlist.toTransfer())
                .collect(Collectors.toList());
        return new PageImpl<>(transfers, pageable, query.getResultList().size());
    }

    @Transactional(rollbackFor = { Exception.class })
    public void modifyPlaylist(ModifiedPlaylistDTO playlist)
            throws UnsupportedImageException, ImageConversionException, IOException {
        Playlist p = entityManager.find(Playlist.class, playlist.getId());
        try {
            if (p == null)
                throw new IllegalArgumentException("La playlist no existe");

            if (playlist.getName() != null) {
                p.setName(playlist.getName());
            }
            if (playlist.getActive() != null) {
                p.setActive(playlist.getActive());
            }
            if (playlist.getDesc() != null) {
                p.setDescription(playlist.getDesc());
            }

            if (playlist.getAddedSongs() != null) {
                for (Long songId : playlist.getAddedSongs()) {
                    Song song = entityManager.find(Song.class, songId);
                    if (song == null) {
                        throw new IllegalArgumentException("La cancion no existe");
                    }
                    String checkSql = "SELECT COUNT(*) FROM playlist_song WHERE playlist_id = ?1 AND song_id = ?2";
                    Long count = ((Number) entityManager.createNativeQuery(checkSql)
                            .setParameter(1, playlist.getId())
                            .setParameter(2, songId)
                            .getSingleResult()).longValue();
                    if (count > 0) {
                        throw new IllegalArgumentException("La cancion ya esta en la playlist");
                    }

                    String insertSql = "INSERT INTO playlist_song (playlist_id, song_id) VALUES (?1, ?2)";
                    entityManager.createNativeQuery(insertSql)
                            .setParameter(1, playlist.getId())
                            .setParameter(2, songId)
                            .executeUpdate();

                    entityManager.clear();
                }
            }

            if (playlist.getRemovedSongs() != null) {
                for (Long songId : playlist.getRemovedSongs()) {
                    Song song = entityManager.find(Song.class, songId);
                    if (song == null) {
                        throw new IllegalArgumentException("La cancion no existe");
                    }
                    Query query = entityManager.createNativeQuery(
                            "DELETE FROM playlist_song " +
                                    "WHERE playlist_id = :playlistId AND song_id = :songId");
                    query.setParameter("playlistId", playlist.getId());
                    query.setParameter("songId", songId);
                    int rowsAffected = query.executeUpdate();
                    if (rowsAffected == 0) {
                        throw new IllegalArgumentException("La cancion no esta en la playlist");
                    }
                    entityManager.clear();

                }
            }

            if (playlist.getCover() != null) {
                Path path = Paths.get(UPLOAD_DIR + playlist.getId());
                Path oldPath = Paths.get(UPLOAD_DIR + playlist.getId() + "/old");
                String timestamp = String.valueOf(System.currentTimeMillis());
                try {
                    if (!Files.exists(path)) {
                        Files.createDirectories(path);
                    } else {
                        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                            @Override
                            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                                Path destFile = oldPath.resolve(timestamp + "." + getFileExtension(file));
                                Files.move(file, destFile, StandardCopyOption.REPLACE_EXISTING);
                                return FileVisitResult.CONTINUE;
                            }

                            private String getFileExtension(Path file) {
                                String fileName = file.getFileName().toString();
                                int dotIndex = fileName.lastIndexOf('.');
                                return (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1);
                            }
                        });
                    }

                    File imgDest = new File(UPLOAD_DIR + playlist.getId() + "/cover.webp");
                    ImageConverter.readAndConvertImage(playlist.getCover().getInputStream(), imgDest.toPath());

                } catch (IOException e) {
                    try {
                        Files.move(oldPath.resolve(timestamp + ".webp"), path.resolve("cover.webp"),
                                StandardCopyOption.REPLACE_EXISTING);
                        if (oldPath.toFile().listFiles().length == 0) {
                            Files.delete(oldPath);
                        }
                    } catch (IOException ex) {
                        e.addSuppressed(ex);
                    }

                    throw e;
                }
            }
        } catch (Exception e) {
            entityManager.clear();
            throw e;
        }

        entityManager.persist(p);
        entityManager.flush();

    }
}
