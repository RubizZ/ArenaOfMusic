package es.ucm.fdi.iw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;

import es.ucm.fdi.iw.LoginSuccessHandler;
import es.ucm.fdi.iw.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;

@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private LoginSuccessHandler loginSuccessHandler;

    @Autowired
    private AuthenticationManager authenticationManager;

    @GetMapping("/login")
    public String login(Model model, HttpServletRequest request) {
        boolean error = request.getQueryString() != null && request.getQueryString().indexOf("error") != -1;
        model.addAttribute("loginError", error);
        return "login";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("registerError", null);
        return "register";
    }

    @GetMapping("/user_available")
    @ResponseBody
    public String isUserAvailable(@RequestParam String username) {
        if (userService.usernameExists(username)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already in use");
        }
        return "{\"avaible\": \"true\"}";
    }

    @PostMapping("/register")
    @Transactional
    public String register(@RequestParam String username,
            @RequestParam String password,
            @RequestParam String email,
            Model model,
            HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        // Check if the username already exists
        if (userService.usernameExists(username)) {
            model.addAttribute("registerError", "Username already used!");
            return "register";
        }

        // Register the user
        userService.registerUser(username, password, email);

        try {
            // Do the automatic login
            UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username,
                    password);

            Authentication authentication = authenticationManager.authenticate(authRequest);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Use loginSuccessHandler to handle the session
            loginSuccessHandler.onAuthenticationSuccess(request, response, authentication);
            request.getSession().setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                    SecurityContextHolder.getContext());

            return null; // Avoid double redirect
        } catch (Exception e) {
            e.printStackTrace();

            return "login"; // Go back to login page on error

        }
    }
}
