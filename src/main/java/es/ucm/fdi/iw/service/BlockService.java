package es.ucm.fdi.iw.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.ucm.fdi.iw.model.Block;
import es.ucm.fdi.iw.model.BlockId;
import es.ucm.fdi.iw.model.User;
import jakarta.persistence.EntityManager;

@Service
@Transactional
public class BlockService {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private UserService userService;

    // Bloquea a un usuario
    public boolean blockUser(String blockerUsername, String blockedUsername)
    {
        if (blockerUsername.equalsIgnoreCase(blockedUsername)) {
            return false;
        }

        User blocker = userService.findByUsername(blockerUsername);
        User blocked = userService.findByUsername(blockedUsername);
        BlockId blockId = new BlockId(blocker.getId(), blocked.getId());

        // Comprobar si ya existe el bloqueo
        Block existingBlock = entityManager.find(Block.class, blockId);
        if (existingBlock != null) {
            return false;
        }

        // Borrar amistades o peticiones de amistad existentes
        entityManager.createQuery(
            "DELETE FROM Friendship f " +
            "WHERE (f.user1.id = :blockerId AND f.user2.id = :blockedId) " +
            "OR (f.user1.id = :blockedId AND f.user2.id = :blockerId)")
            .setParameter("blockerId", blocker.getId())
            .setParameter("blockedId", blocked.getId())
            .executeUpdate();

        // Crear el bloqueo
        Block block = new Block();
        block.setId(blockId);
        block.setBlocker(blocker);
        block.setBlocked(blocked);
        block.setBlockDate(LocalDateTime.now());
        entityManager.persist(block);

        return true;
    }

    // Desbloquea a un usuario
    public boolean unblockUser(String blockerUsername, String blockedUsername)
    {
        User blocker = userService.findByUsername(blockerUsername);
        User blocked = userService.findByUsername(blockedUsername);
        BlockId blockId = new BlockId(blocker.getId(), blocked.getId());

        Block existingBlock = entityManager.find(Block.class, blockId);
        if (existingBlock == null) {
            return false;
        }

        entityManager.remove(existingBlock);
        return true;
    }

    // Comprueba si un usuario está bloqueado
    public boolean isBlocked(Long blockerId, Long blockedId)
    {
        return entityManager.find(Block.class, new BlockId(blockerId, blockedId)) != null
            || entityManager.find(Block.class, new BlockId(blockedId, blockerId)) != null;
    }
}
