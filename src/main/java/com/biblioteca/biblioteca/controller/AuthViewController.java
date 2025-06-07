package com.biblioteca.biblioteca.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthViewController {

    private static final Logger logger = LoggerFactory.getLogger(AuthViewController.class);

    @GetMapping("/auth/login")
    public String login(Model model) {
        // AGREGAR ESTA VERIFICACIÓN AL INICIO
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            logger.info("Usuario {} ya autenticado, redirigiendo a dashboard", auth.getName());
            return "redirect:/dashboard";
        }

        // El resto de tu lógica existente aquí...
        return "auth/login";
    }

    // Si tienes otros métodos como register, agregar la misma verificación
    @GetMapping("/auth/register")
    public String register(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            return "redirect:/dashboard";
        }

        return "auth/register";
    }
}