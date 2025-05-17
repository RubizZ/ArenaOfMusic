package es.ucm.fdi.iw.service;

import java.time.LocalDateTime;
import java.util.List;

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

    public Message sendMessage(User sender, User recipient, String text)
    {
        Message m = new Message();
        m.setSender(sender);
        m.setRecipient(recipient);
        m.setText(text);
        m.setDateSent(LocalDateTime.now());

        entityManager.persist(m);

        messagingTemplate.convertAndSend("/user/" + recipient.getUsername() + "/queue/updates", m.toTransfer());

        return m;
    }

    @Transactional(readOnly = true)
    public List<Message> getConversation(User a, User b)
    {
        return entityManager.createQuery(
                "SELECT m FROM Message m "
                + "WHERE (m.sender = :a AND m.recipient = :b) "
                + "OR (m.sender = :b AND m.recipient = :a) "
                + "ORDER BY m.dateSent", Message.class)
                .setParameter("a", a)
                .setParameter("b", b)
                .getResultList();
    }
}
