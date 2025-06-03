package es.ucm.fdi.iw.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.ucm.fdi.iw.model.Message;
import es.ucm.fdi.iw.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
@Transactional
public class MessageService {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public Message sendMessage(User sender, User recipient, String text) {
        Message m = new Message();
        m.setSender(sender);
        m.setRecipient(recipient);
        m.setText(text);
        m.setDateSent(LocalDateTime.now());

        entityManager.persist(m);

        messagingTemplate.convertAndSendToUser(recipient.getUsername(), "/queue/updates",
                Map.of("type", "message", "data", m.toTransfer()));

        return m;
    }

    @Transactional
    public List<Message> getConversation(User me, User friend) {
        List<Message> messages = entityManager.createQuery(
                "SELECT m FROM Message m "
                        + "WHERE (m.sender = :me AND m.recipient = :friend) "
                        + "OR (m.sender = :friend AND m.recipient = :me) "
                        + "ORDER BY m.dateSent",
                Message.class)
                .setParameter("me", me)
                .setParameter("friend", friend)
                .getResultList();

        messages.stream()
                .filter(m -> m.getDateRead() == null && m.getRecipient().getId() == me.getId())
                .forEach(m -> {
                    m.setDateRead(LocalDateTime.now());
                    entityManager.merge(m);
                });

        entityManager.flush();

        return messages;
    }

    @Transactional
    public Map<Long, Long> countUnreadMessages(User me) {
        return entityManager.createQuery(
                "SELECT m.sender.id, COUNT(m) FROM Message m "
                        + "WHERE m.recipient = :me AND m.dateRead IS NULL "
                        + "GROUP BY m.sender.id",
                Object[].class)
                .setParameter("me", me)
                .getResultList()
                .stream()
                .collect(Collectors.toMap(
                        r -> (Long) r[0], // id del amigo
                        r -> (Long) r[1] // número de mensajes no leídos
                ));
    }
}
