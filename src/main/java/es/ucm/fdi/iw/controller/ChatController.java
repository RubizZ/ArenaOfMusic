package es.ucm.fdi.iw.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import es.ucm.fdi.iw.model.Message;
import es.ucm.fdi.iw.model.User;
import es.ucm.fdi.iw.service.UserService;
import es.ucm.fdi.iw.service.MessageService;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@Controller
@RequestMapping("/chat")
public class ChatController {

    @Autowired
    private UserService userService;

    @Autowired
    private MessageService messageService;

    @ModelAttribute
    public void populateModel(HttpSession session, Model model) {
        for (String name : new String[] { "u", "url", "ws" }) {
            model.addAttribute(name, session.getAttribute(name));
        }
    }

    // Muestra el chat con el usuario
    @GetMapping("/{username}")
    public String chatByUsername(@PathVariable String username, Model model) {
        User friend = userService.findByUsername(username);
        model.addAttribute("friend", friend);
        return "chat";
    }

    @GetMapping("/{username}/messages")
    @ResponseBody
    public List<Message.Transfer> getHistory(@PathVariable String username, HttpSession session)
    {
        User me = (User)session.getAttribute("u");
        User friend = userService.findByUsername(username);

        List<Message> messages = messageService.getConversation(me, friend);

        return messages.stream().map(Message::toTransfer).toList();
    }

    @PostMapping("/{username}/messages")
    @ResponseBody
    public Message.Transfer postMessage(
        @PathVariable String username,
        @RequestBody Map<String, String> data,
        HttpSession session)
    {
        User me = (User)session.getAttribute("u");
        User friend = userService.findByUsername(username);
        String text = data.get("message");

        Message m = messageService.sendMessage(me, friend, text);
        return m.toTransfer();
    }

}
