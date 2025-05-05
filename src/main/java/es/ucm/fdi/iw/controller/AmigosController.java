package es.ucm.fdi.iw.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import es.ucm.fdi.iw.service.AmigosService;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/amigos")
public class AmigosController {

    @Autowired
    private AmigosService amigosService;

    @ModelAttribute
    public void populateModel(HttpSession session, Model model) {
        for (String name : new String[] { "u", "url", "ws" }) {
            model.addAttribute(name, session.getAttribute(name));
        }
    }

    @GetMapping()
    public String amigos(@RequestParam(name = "view", required = false) String viewType, Model model) {
        if (viewType == null || viewType.isBlank()) {
            viewType = "amigos";
        }

        model.addAttribute("friends", amigosService.getFriends());
        model.addAttribute("requests", amigosService.getRequests());
        model.addAttribute("selectedUser", amigosService.getSelectedUser());
        model.addAttribute("viewType", viewType);

        return "amigos";
    }

    // Obtener la lista de amigos o solicitudes
    @PostMapping("/list")
    @ResponseBody
    public List<Map<String, Object>> list(@RequestBody Map<String, String> filters) {
        String view = filters.get("view");
        String search = filters.get("search");
        List<Map<String, Object>> list;

        // Obtener la lista según la vista
        if("solicitudes".equalsIgnoreCase(view)) {
            list = amigosService.getRequests();
        }
        else {
            list = amigosService.getFriends();
        }

        // Filtrar la lista por búsqueda
        if(search != null && !search.trim().isEmpty()) {
            String searchLower = search.toLowerCase();
            list = list.stream()
            .filter(item -> item.get("username")
            .toString().toLowerCase()
            .contains(searchLower))
            .toList();
        }

        return list;
    }

    // Obtener detalles del perfil de un amigo
    @PostMapping("/profile")
    @ResponseBody
    public Map<String, Object> profile(@RequestBody Map<String, Object> friendData) {
        return amigosService.getFriendProfile(friendData);
    }

    // Aceptar solicitud de amistad
    @PostMapping("/request/accept")
    @ResponseBody
    public Map<String, Object> acceptRequest(@RequestBody Map<String, Object> requestData) {
        boolean result = amigosService.acceptRequest(requestData);
        return Map.of("success", result);
    }

    // Rechazar solicitud de amistad
    @PostMapping("/request/reject")
    @ResponseBody
    public Map<String, Object> rejectRequest(@RequestBody Map<String, Object> requestData) {
        boolean result = amigosService.rejectRequest(requestData);
        return Map.of("success", result);
    }
}
