package com.biblioteca.biblioteca.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
public class DebugController {

    private static final Logger logger = LoggerFactory.getLogger(DebugController.class);

    @GetMapping("/debug/session")
    @ResponseBody
    public String debugSession(HttpServletRequest request) {
        StringBuilder debug = new StringBuilder();

        // Información de la sesión
        HttpSession session = request.getSession(false);
        debug.append("=== SESIÓN ===\n");
        debug.append("Sesión existe: ").append(session != null).append("\n");
        if (session != null) {
            debug.append("ID de sesión: ").append(session.getId()).append("\n");
            debug.append("Tiempo creación: ").append(session.getCreationTime()).append("\n");
            debug.append("Último acceso: ").append(session.getLastAccessedTime()).append("\n");
            debug.append("Tiempo máximo inactivo: ").append(session.getMaxInactiveInterval()).append("\n");
            debug.append("Es nueva: ").append(session.isNew()).append("\n");
        }

        // Información de autenticación
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        debug.append("\n=== AUTENTICACIÓN ===\n");
        debug.append("Authentication existe: ").append(auth != null).append("\n");
        if (auth != null) {
            debug.append("Está autenticado: ").append(auth.isAuthenticated()).append("\n");
            debug.append("Nombre: ").append(auth.getName()).append("\n");
            debug.append("Tipo de principal: ").append(auth.getPrincipal().getClass().getSimpleName()).append("\n");
            debug.append("Autoridades: ").append(auth.getAuthorities()).append("\n");
        }

        return debug.toString();
    }
}