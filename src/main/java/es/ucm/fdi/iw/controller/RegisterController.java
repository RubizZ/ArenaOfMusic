package es.ucm.fdi.iw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import es.ucm.fdi.iw.LoginSuccessHandler;
import es.ucm.fdi.iw.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;

@Controller
public class RegisterController {

    @Autowired
    private UserService userService;

    @Autowired
    private LoginSuccessHandler loginSuccessHandler;

    @Autowired
    private AuthenticationManager authenticationManager;

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("registerError", null);
        return "register";
    }

    @PostMapping("/register")
    @Transactional
    public String register(@RequestParam String username,
            @RequestParam String password,
            @RequestParam String firstName,
            @RequestParam String lastName,
            Model model,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        // Controlla se il nome utente esiste già
        if (userService.usernameExists(username)) {
            model.addAttribute("registerError", "Username already used!");
            return "register";
        }

        // Registra l'utente nel database
        userService.registerUser(username, password, firstName, lastName);

        try {
            // Effettua il login automatico
            UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username,
                    password);

            Authentication authentication = authenticationManager.authenticate(authRequest);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Usa il loginSuccessHandler per gestire la sessione
            loginSuccessHandler.onAuthenticationSuccess(request, response, authentication);
            request.getSession().setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                    SecurityContextHolder.getContext());

            return null; // Evita il doppio redirect
        } catch (Exception e) {
            e.printStackTrace();
             
            // model.addAttribute("registerError", "Registrazione riuscita, ma errore nel
            // login.");
            return "login"; // Ritorna alla pagina di login in caso di errore
        }
    }
}
