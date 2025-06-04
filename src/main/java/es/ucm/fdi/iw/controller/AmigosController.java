package es.ucm.fdi.iw.controller;

import java.security.Principal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import es.ucm.fdi.iw.model.User;
import es.ucm.fdi.iw.service.AmigosService;
import es.ucm.fdi.iw.service.BlockService;
import es.ucm.fdi.iw.service.ReportService;
import es.ucm.fdi.iw.service.UserService;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("/amigos")
public class AmigosController {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private UserService userService;

    @Autowired
    private AmigosService amigosService;

    @Autowired
    private BlockService blockService;

    @Autowired
    private ReportService reportService;

    @ModelAttribute
    public void populateModel(HttpSession session, Model model) {
        for (String name : new String[] { "u", "url", "ws" }) {
            model.addAttribute(name, session.getAttribute(name));
        }
    }

    @GetMapping()
    public String amigos(@RequestParam(name = "view", required = false) String viewType, Model model, Principal principal) {
        if (viewType == null || viewType.isBlank()) {
            viewType = "amigos";
        }

        String me = principal.getName();

        model.addAttribute("friends", amigosService.getFriends(me, null));
        model.addAttribute("requests", amigosService.getRequests(me, null));
        model.addAttribute("selectedUser", null);
        model.addAttribute("viewType", viewType);

        return "amigos";
    }

    // Obtener la lista de amigos o solicitudes
    @PostMapping("/list")
    @ResponseBody
    public List<Map<String, Object>> list(@RequestBody Map<String, String> filters, Principal principal)
    {
        String me = principal.getName();
        String view = filters.get("view");
        String search = filters.get("search");

        // Obtener la lista según la vista
        if("solicitudes".equalsIgnoreCase(view)) {
            return amigosService.getRequests(me, search);
        }
        else {
            return amigosService.getFriends(me, search);
        }
    }

    // Obtener detalles del perfil de un amigo
    @PostMapping("/profile")
    @ResponseBody
    public Map<String, Object> profile(@RequestBody Map<String, Object> friendData, Principal principal) {
        String friend = friendData.get("username").toString();
        return amigosService.getFriendProfile(principal.getName(), friend);
    }

    // Aceptar solicitud de amistad
    @PostMapping("/request/accept")
    @ResponseBody
    public Map<String, Boolean> acceptRequest(@RequestBody Map<String, String> requestData, Principal principal) {
        boolean result = amigosService.acceptRequest(principal.getName(), requestData.get("username"));
        return Map.of("success", result);
    }

    // Rechazar solicitud de amistad
    @PostMapping("/request/reject")
    @ResponseBody
    public Map<String, Boolean> rejectRequest(@RequestBody Map<String, String> requestData, Principal principal) {
        boolean result = amigosService.rejectRequest(principal.getName(), requestData.get("username"));
        return Map.of("success", result);
    }

    // Eliminar usuario de la lista de amigos
    @PostMapping("/delete")
    @ResponseBody
    public Map<String, Boolean> deleteFriend(@RequestBody Map<String, String> friendData, Principal principal) {
        boolean result = amigosService.deleteFriend(principal.getName(), friendData.get("username"));
        return Map.of("success", result);
    }

    // Bloquear usuario
    @PostMapping("/block")
    @ResponseBody
    public Map<String, Boolean> blockUser(@RequestBody Map<String, String> blockData, Principal principal) {
        boolean result = blockService.blockUser(principal.getName(), blockData.get("username"));
        return Map.of("success", result);
    }

    // Desbloquear usuario
    @PostMapping("/unblock")
    @ResponseBody
    public Map<String, Boolean> unblockUser(@RequestBody Map<String, String> unblockData, Principal principal) {
        boolean result = blockService.unblockUser(principal.getName(), unblockData.get("username"));
        return Map.of("success", result);
    }

    // Enviar solicitud de amistad
    @PostMapping("/request/send")
    @ResponseBody
    public Map<String, Boolean> sendRequest(@RequestBody Map<String, String> requestData, Principal principal) {
        boolean result = amigosService.sendRequest(principal.getName(), requestData.get("username"));
        return Map.of("success", result);
    }

    // Actualizar el estado de conexión del usuario
    @PostMapping("/updateStatus")
    @ResponseBody
    @Transactional
    public void updateStatus(Principal principal) {
        User user = userService.findByUsername(principal.getName());
        user.setLastLogin(new Date());
        entityManager.merge(user);
    }

    // Reportar usuario
    @PostMapping("/report")
    @ResponseBody
    public Map<String, Boolean> reportUser(@RequestBody Map<String, String> reportData, Principal principal) {
        reportService.createReport(principal.getName(), reportData.get("reportedUsername"), Integer.parseInt(reportData.get("reason")));
        return Map.of("success", true);
    }

    @GetMapping("/ver-perfil/{name}")
    public String verPerfilUsuario(@PathVariable String name, Model model, Principal principal) {
        User me = userService.findByUsername(principal.getName());
        User target = userService.findByUsername(name);
        if (target == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no existe.");
        }

        if(me.getId() != target.getId() && !amigosService.areFriends(me.getUsername(), target.getUsername())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes permiso para ver este perfil.");
        }

        model.addAttribute("user", target);
        return "ver-perfil";
    }
}
