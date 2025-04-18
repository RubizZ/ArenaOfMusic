package es.ucm.fdi.iw.service;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;

import es.ucm.fdi.iw.dto.ModifiedSongDTO;
import es.ucm.fdi.iw.dto.NewSongDTO;
import es.ucm.fdi.iw.dto.SongSearchFiltersDTO;
import es.ucm.fdi.iw.model.Playlist;
import es.ucm.fdi.iw.model.Song;
import es.ucm.fdi.iw.util.AudioConverter;
import es.ucm.fdi.iw.util.NoDataException;
import es.ucm.fdi.iw.util.AudioConverter.AudioConversionException;
import es.ucm.fdi.iw.util.ImageConverter;
import es.ucm.fdi.iw.util.ImageConverter.ImageConversionException;
import es.ucm.fdi.iw.util.ImageConverter.UnsupportedImageException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import lombok.experimental.StandardException;

@Service
public class SongService {

    @Autowired
    private EntityManager entityManager;

    private static final String UPLOAD_DIR = "iwdata/songs/";

    @Transactional
    public long addNewSong(NewSongDTO data)
            throws IllegalArgumentException, IOException, UnsupportedImageException, ImageConversionException,
            AudioConversionException {

        if (data.getName() == null || data.getName().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la canción no puede estar vacío");
        }
        if (data.getArtists() == null || data.getArtists().isEmpty()) {
            throw new IllegalArgumentException("Los artistas de la canción no pueden estar vacíos");
        }
        if (data.getAlbum() == null || data.getAlbum().isEmpty()) {
            throw new IllegalArgumentException("El álbum de la canción no puede estar vacío");
        }
        if (data.getCover() == null || data.getCover().isEmpty()) {
            throw new IllegalArgumentException("La portada de la canción no puede estar vacía");
        }
        if (data.getAudio() == null || data.getAudio().isEmpty()) {
            throw new IllegalArgumentException("El audio de la canción no puede estar vacío");
        }

        Song song = new Song();

        song.setActive(data.isActive());
        song.setName(data.getName());
        song.setArtists(data.getArtists());
        song.setAlbum(data.getAlbum());

        entityManager.persist(song);
        entityManager.flush();

        try {
            Path path = Paths.get(UPLOAD_DIR + song.getId());
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            } else {
                deleteDirectoryCascade(path);
                Files.createDirectories(path);
            }

            File imgDest = new File(UPLOAD_DIR + song.getId() + "/cover.webp");
            ImageConverter.readAndConvertImage(data.getCover().getInputStream(), imgDest.toPath());

            File songDest = new File(UPLOAD_DIR + song.getId() + "/audio.mp3");
            AudioConverter.convertToMP3(data.getAudio(), songDest);

            return song.getId();

        } catch (UnsupportedImageException | ImageConversionException | AudioConversionException | IOException e) {
            entityManager.remove(song);
            Path dirPath = Paths.get(UPLOAD_DIR + song.getId());

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

    @Transactional(rollbackOn = { IOException.class, RuntimeException.class, AudioConversionException.class })
    public void modifyExistingSong(ModifiedSongDTO song)
            throws IllegalArgumentException, IOException, AudioConversionException, UnsupportedImageException,
            ImageConversionException {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaUpdate<Song> update = cb.createCriteriaUpdate(Song.class);
        Root<Song> updateRoot = update.from(Song.class);
        update.where(cb.equal(updateRoot.get("id"), song.getId()));
        if (song.getActive() != null) {
            update.set("active", song.getActive());
        }
        if (song.getName() != null) {
            update.set("name", song.getName());
        }
        if (song.getArtists() != null) {
            update.set("artists", song.getArtists());
        }
        if (song.getAlbum() != null) {
            update.set("album", song.getAlbum());
        }

        int n = entityManager.createQuery(update).executeUpdate();

        if (n < 1)
            throw new IllegalArgumentException("No existe la cancion con id " + song.getId());

        String timestamp = String.valueOf(System.currentTimeMillis());
        Path mainPath = Paths.get(UPLOAD_DIR + song.getId());
        Path oldPath = Paths.get(UPLOAD_DIR + song.getId() + "/old");

        MultipartFile audio = song.getAudio();
        MultipartFile img = song.getCover();

        try {
            if (audio != null || img != null) {

                if (!Files.exists(oldPath))
                    Files.createDirectories(oldPath);

                Files.walkFileTree(mainPath, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        Path destFile = oldPath.resolve(timestamp + "." + getFileExtension(file));
                        Files.move(file, destFile, StandardCopyOption.REPLACE_EXISTING);
                        return FileVisitResult.CONTINUE;
                    }
                });

                if (img != null) {
                    File imgDest = new File(UPLOAD_DIR + song.getId() + "/cover.webp");
                    ImageConverter.readAndConvertImage(img.getInputStream(), imgDest.toPath());
                } else
                    Files.copy(oldPath.resolve(timestamp + ".webp"),
                            new File(UPLOAD_DIR + song.getId() + "/cover.webp").toPath());

                if (audio != null) {
                    File songDest = new File(UPLOAD_DIR + song.getId() + "/audio.mp3");
                    AudioConverter.convertToMP3(audio, songDest);
                } else
                    Files.copy(oldPath.resolve(timestamp + ".mp3"),
                            new File(UPLOAD_DIR + song.getId() + "/audio.mp3").toPath());
            }

        } catch (IOException | AudioConversionException e) {
            // Signal to Spring to rollback the transaction
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

            try {
                if (Files.list(oldPath).findAny().isEmpty()) {
                    Files.delete(oldPath);
                } else {
                    Path mainImgPath = mainPath.resolve("cover.webp");
                    if (!Files.exists(mainImgPath)) {
                        Files.move(oldPath.resolve(timestamp + ".webp"), mainImgPath);
                        Path mainSongPath = mainPath.resolve("audio.mp3");
                        if (!Files.exists(mainSongPath)) {
                            Files.move(oldPath.resolve(timestamp + ".mp3"), mainSongPath);
                        }
                    }
                }
            } catch (IOException ex) {
                disableExistingSong(song.getId());

                e.addSuppressed(ex);
                throw e;
            }

            throw e;
        }
    }

    @Transactional(rollbackOn = { IOException.class, RuntimeException.class })
    public void deleteExistingSong(long id) throws IllegalArgumentException, IOException {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaDelete<Song> delete = cb.createCriteriaDelete(Song.class);
        Root<Song> deleteRoot = delete.from(Song.class);
        delete.where(cb.equal(deleteRoot.get("id"), id));
        int n = entityManager.createQuery(delete).executeUpdate();
        if (n < 1)
            throw new IllegalArgumentException("No existe ninguna cancion con el id " + id);
        try {
            deleteDirectoryCascade(Paths.get(UPLOAD_DIR + id));
        } catch (IOException e) {
            // Signal to Spring to rollback the transaction
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

            disableExistingSong(id);
            throw new IOException("No se han podido eliminar los archivos de la cancion", e);
        }
    }

    @Transactional
    public void disableExistingSong(long id) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaUpdate<Song> update = cb.createCriteriaUpdate(Song.class);
        Root<Song> updateRoot = update.from(Song.class);
        update.where(cb.equal(updateRoot.get("id"), id));
        update.set("active", false);
        entityManager.createQuery(update).executeUpdate();
    }

    private String getFileExtension(Path file) {
        String fileName = file.getFileName().toString();
        int lastDotIndex = fileName.lastIndexOf(".");
        return (lastDotIndex == -1) ? "" : fileName.substring(lastDotIndex + 1);
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

    public Page<Song.Transfer> searchSongs(SongSearchFiltersDTO filters, Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        CriteriaQuery<Song> select = cb.createQuery(Song.class);
        Root<Song> selectRoot = select.from(Song.class);

        List<Predicate> predicates = buildPredicates(cb, selectRoot, filters);

        select.where(predicates.toArray(new Predicate[0]));

        if (pageable.isPaged() && pageable.getSort().isSorted()) {
            List<Order> orders = pageable.getSort().stream()
                    .map(order -> order.isAscending() ? cb.asc(selectRoot.get(order.getProperty()))
                            : cb.desc(selectRoot.get(order.getProperty())))
                    .toList();
            select.orderBy(orders);
        }

        TypedQuery<Song> query = entityManager.createQuery(select);
        if (pageable.isPaged()) {
            query.setFirstResult((int) pageable.getOffset());
            query.setMaxResults(pageable.getPageSize());
        }
        List<Song> resultList = query.getResultList();

        Page<Song> page = new PageImpl<>(resultList, pageable, resultList.size());

        return page.map(Song::toTransfer);
    }

    public Page<Song.Transfer> searchSongs(SongSearchFiltersDTO filters) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        CriteriaQuery<Song> select = cb.createQuery(Song.class);
        Root<Song> selectRoot = select.from(Song.class);

        List<Predicate> predicates = buildPredicates(cb, selectRoot, filters);

        select.where(predicates.toArray(new Predicate[0]));

        TypedQuery<Song> query = entityManager.createQuery(select);
        List<Song> resultList = query.getResultList();

        List<Song.Transfer> result = resultList.stream().map(Song::toTransfer).collect(Collectors.toList());
        return new PageImpl<>(result, Pageable.unpaged(), result.size());
    }

    private List<Predicate> buildPredicates(CriteriaBuilder cb, Root<Song> root, SongSearchFiltersDTO filters) {
        List<Predicate> predicates = new ArrayList<>();

        if (filters.getId() != null) {
            predicates.add(cb.equal(root.get("id"), filters.getId()));
        }

        if (filters.getActive() != null) {
            predicates.add(cb.equal(root.get("active"), filters.getActive()));
        }

        if (filters.getName() != null) {
            predicates.add(cb.like(cb.lower(root.get("name")),
                    "%" + filters.getName().toLowerCase() + "%"));
        }

        if (filters.getArtists() != null && !filters.getArtists().isEmpty()) {
            List<Predicate> artistPredicates = new ArrayList<>();
            for (String artist : filters.getArtists()) {
                artistPredicates.add(cb.like(cb.lower(root.get("artists")), "%" + artist.toLowerCase() + "%"));
            }
            predicates.add(cb.and(artistPredicates.toArray(new Predicate[0])));
        }

        if (filters.getAlbum() != null) {
            predicates.add(cb.like(cb.lower(root.get("album")),
                    "%" + filters.getAlbum().toLowerCase() + "%"));
        }

        if (filters.getPlaylists() != null && !filters.getPlaylists().isEmpty()) {
            Join<Song, Playlist> playlistJoin = root.join("playlists");
            predicates.add(playlistJoin.get("id").in(filters.getPlaylists()));
        }

        return predicates;
    }

    public File getSongCover(long id) throws IllegalArgumentException, NoDataException {
        if (!existsSong(id))
            throw new IllegalArgumentException("No existe la canción con id " + id);

        Path coverPath = Paths.get(UPLOAD_DIR + id + "/cover.webp");
        if (Files.exists(coverPath)) {
            return coverPath.toFile();
        } else {
            throw new NoDataException("No existe la portada para la canción con id " + id);
        }
    }

    public File getSongAudio(long id) throws IllegalArgumentException, NoDataException {
        if (!existsSong(id))
            throw new IllegalArgumentException("No existe la canción con id " + id);

        Path audioPath = Paths.get(UPLOAD_DIR + id + "/audio.mp3");
        if (Files.exists(audioPath)) {
            return audioPath.toFile();
        } else {
            throw new NoDataException("No existe el audio para la canción con id " + id);
        }
    }

    private boolean existsSong(long id) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = cb.createQuery(Long.class);
        Root<Song> root = query.from(Song.class);
        query.select(cb.count(root)).where(cb.equal(root.get("id"), id));
        Long count = entityManager.createQuery(query).getSingleResult();

        return count != 0;
    }
}
