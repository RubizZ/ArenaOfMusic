package es.ucm.fdi.iw.service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import es.ucm.fdi.iw.AudioConverter;
import es.ucm.fdi.iw.dto.NewSongDTO;
import es.ucm.fdi.iw.dto.SongSearchFiltersDTO;
import es.ucm.fdi.iw.model.Cancion;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.experimental.StandardException;

@Service
public class SongService {

    @Autowired
    private EntityManager entityManager;

    private static final String UPLOAD_DIR = "iwdata/songs/";

    public void addNewSong(NewSongDTO data) throws IOException {
        Cancion song = new Cancion();

        song.setActive(true);
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
            BufferedImage bufferedImage = ImageIO.read(data.getCover().getInputStream());
            ImageIO.write(bufferedImage, "webp", imgDest);

            File songDest = new File(UPLOAD_DIR + song.getId() + "/audio.mp3");
            AudioConverter.convertToMP3(data.getAudio(), songDest);

        } catch (IOException e) {
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

            throw new RuntimeException("Error al guardar la cancion", e);
        }
    }

    public void modifyExistingSong(Cancion.Transfer song, @Nullable MultipartFile audio, @Nullable MultipartFile img)
            throws IllegalArgumentException, RuntimeException {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaUpdate<Cancion> update = cb.createCriteriaUpdate(Cancion.class);
        Root<Cancion> updateRoot = update.from(Cancion.class);
        update.where(cb.equal(updateRoot.get("id"), song.getId()));
        update.set("active", song.isActive());
        update.set("name", song.getName());
        update.set("artists", song.getArtists());
        update.set("album", song.getAlbum());

        entityManager.getTransaction().begin();
        int n = entityManager.createQuery(update).executeUpdate();

        if (n < 1)
            throw new IllegalArgumentException("No existe la cancion con id " + song.getId());

        String timestamp = String.valueOf(new Date().getTime());
        Path mainPath = Paths.get(UPLOAD_DIR + song.getId());
        Path oldPath = Paths.get(UPLOAD_DIR + song.getId() + "/old");

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
                    BufferedImage bufferedImage = ImageIO.read(img.getInputStream());
                    ImageIO.write(bufferedImage, "webp", imgDest);
                }

                if (audio != null) {
                    File songDest = new File(UPLOAD_DIR + song.getId() + "/audio.mp3");
                    AudioConverter.convertToMP3(audio, songDest);
                }
            }

            entityManager.getTransaction().commit();

        } catch (IOException e) {
            try {
                entityManager.getTransaction().rollback();

                if (Files.list(oldPath).findAny().isEmpty()) {
                    Files.delete(oldPath);
                } else {

                    Path mainImgPath = mainPath.resolve("cover.webp");
                    if (!Files.exists(mainImgPath)) {
                        Files.move(oldPath.resolve("cover.webp"), mainImgPath);
                        Path mainSongPath = mainPath.resolve("audio.mp3");
                        if (!Files.exists(mainSongPath)) {
                            Files.move(oldPath.resolve("audio.mp3"), mainSongPath);
                        }
                    }
                }

                throw new RuntimeException("No se ha podido convertir la nueva cancion, se han revertido los cambios",
                        e);
            } catch (IOException ex) {
                disableExistingSong(song.getId());

                e.addSuppressed(ex);
                throw new RuntimeException(
                        "No se ha podido convertir la nueva cancion y se han perdido los archivos antiguos, la cancion se ha desactivado",
                        e);

            }
        }
    }

    private String getFileExtension(Path file) {
        String fileName = file.getFileName().toString();
        int lastDotIndex = fileName.lastIndexOf(".");
        return (lastDotIndex == -1) ? "" : fileName.substring(lastDotIndex + 1);
    }

    public void deleteExistingSong(long id) throws IllegalArgumentException {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaDelete<Cancion> delete = cb.createCriteriaDelete(Cancion.class);
        Root<Cancion> deleteRoot = delete.from(Cancion.class);
        delete.where(cb.equal(deleteRoot.get("id"), id));
        entityManager.getTransaction().begin();
        int n = entityManager.createQuery(delete).executeUpdate();
        if (n < 1)
            throw new IllegalArgumentException("No existe ninguna cancion con el id " + id);
        try {
            deleteDirectoryCascade(Paths.get(UPLOAD_DIR + id));
        } catch (IOException e) {
            entityManager.getTransaction().rollback();
            disableExistingSong(id);

            throw new RuntimeException(
                    "No se han podido eliminar los archivos de la cancion", e);
        }
    }

    public void disableExistingSong(long id) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaUpdate<Cancion> update = cb.createCriteriaUpdate(Cancion.class);
        Root<Cancion> updateRoot = update.from(Cancion.class);
        update.where(cb.equal(updateRoot.get("id"), id));
        update.set("active", false);
        entityManager.getTransaction().begin();
        entityManager.createQuery(update).executeUpdate();
        entityManager.getTransaction().commit();
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
        } catch (IOException ex) {
            throw new IOException("Error al eliminar el directorio o su contenido: " + dirPath, ex);
        }
    }

    public Page<Cancion.Transfer> searchSongs(SongSearchFiltersDTO filters, Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        CriteriaQuery<Cancion> select = cb.createQuery(Cancion.class);
        Root<Cancion> selectRoot = select.from(Cancion.class);

        List<Predicate> predicates = buildPredicates(cb, selectRoot, filters);

        select.where(predicates.toArray(new Predicate[0]));

        if (pageable.getSort().isSorted()) {
            List<Order> orders = pageable.getSort().stream()
                    .map(order -> order.isAscending() ? cb.asc(selectRoot.get(order.getProperty()))
                            : cb.desc(selectRoot.get(order.getProperty())))
                    .toList();
            select.orderBy(orders);
        }

        TypedQuery<Cancion> query = entityManager.createQuery(select);
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());
        List<Cancion> resultList = query.getResultList();

        long totalElements = countTotalElements(filters);

        Page<Cancion> page = new PageImpl<>(resultList, pageable, totalElements);

        return page.map((Cancion c) -> {
            return c.toTransfer();
        });
    }

    private List<Predicate> buildPredicates(CriteriaBuilder cb, Root<Cancion> root, SongSearchFiltersDTO filters) {
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
            String artistJson = filters.getArtists().stream()
                    .map(artist -> artist.toLowerCase())
                    .collect(Collectors.joining(".*"));

            predicates.add(cb.like(cb.lower(root.get("artists").as(String.class)),
                    "%" + artistJson + "%")); // TODO Cambiar filtros para que sean restrictivos y pensar en
                                              // lower/uppercase
        }

        if (filters.getAlbum() != null) {
            predicates.add(cb.like(cb.lower(root.get("album")),
                    "%" + filters.getAlbum().toLowerCase() + "%"));
        }

        return predicates;
    }

    private long countTotalElements(SongSearchFiltersDTO filters) {
        StringBuilder jpql = new StringBuilder("SELECT COUNT(c) FROM Cancion c WHERE 1=1");

        Map<String, Object> parameters = new HashMap<>();

        if (filters.getId() != null) {
            jpql.append(" AND c.id = :id");
            parameters.put("id", filters.getId());
        }

        if (filters.getActive() != null) {
            jpql.append(" AND c.active = :active");
            parameters.put("active", filters.getActive());
        }

        if (filters.getName() != null) {
            jpql.append(" AND LOWER(c.name) LIKE :name");
            parameters.put("name", "%" + filters.getName().toLowerCase() + "%");
        }

        if (filters.getArtists() != null && !filters.getArtists().isEmpty()) {
            String artistJson = filters.getArtists().stream()
                    .map(artist -> artist.toLowerCase())
                    .collect(Collectors.joining(".*"));

            jpql.append(" AND LOWER(c.artists) LIKE :artists");
            parameters.put("artists", "%" + artistJson + "%");
        }

        if (filters.getAlbum() != null) {
            jpql.append(" AND LOWER(c.album) LIKE :album");
            parameters.put("album", "%" + filters.getAlbum().toLowerCase() + "%");
        }

        Query countQuery = entityManager.createQuery(jpql.toString());
        parameters.forEach((key, value) -> countQuery.setParameter(key, value));

        return ((Number) countQuery.getSingleResult()).longValue();
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
        Root<Cancion> root = query.from(Cancion.class);
        query.select(cb.count(root)).where(cb.equal(root.get("id"), id));
        Long count = entityManager.createQuery(query).getSingleResult();

        return count != 0;
    }

    // Excepcion usada para comunicar que no existe un archivo de una cancion si
    // existente en BD
    @StandardException
    public class NoDataException extends Exception {

    }

}
