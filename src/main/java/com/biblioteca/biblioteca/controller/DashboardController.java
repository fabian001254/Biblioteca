package com.biblioteca.biblioteca.controller;

import com.biblioteca.biblioteca.model.User;
import com.biblioteca.biblioteca.model.Enums.LoanStatus;
import com.biblioteca.biblioteca.model.Enums.Role;
import com.biblioteca.biblioteca.repository.BookRepository;
import com.biblioteca.biblioteca.repository.LoanRepository;
import com.biblioteca.biblioteca.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
public class DashboardController {

    private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private LoanRepository loanRepository;

    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpServletRequest request) {
        try {
            logger.info("=== ACCESO AL DASHBOARD ===");

            // Log detalles de la sesión
            HttpSession session = request.getSession(false);
            logger.info("Sesión actual: {}", session != null ? session.getId() : "null");
            logger.info("Sesión válida: {}", session != null && !session.isNew());

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            logger.info("Authentication del SecurityContext: {}", auth);
            logger.info("¿Está autenticado?: {}", auth != null && auth.isAuthenticated());
            logger.info("Nombre del principal: {}", auth != null ? auth.getName() : "null");
            logger.info("Tipo de principal: {}", auth != null ? auth.getPrincipal().getClass().getSimpleName() : "null");
            logger.info("Autoridades: {}", auth != null ? auth.getAuthorities() : "null");

            if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
                logger.warn("=== REDIRECCIÓN A LOGIN ===");
                logger.warn("Auth null: {}", auth == null);
                logger.warn("Auth not authenticated: {}", auth != null && !auth.isAuthenticated());
                logger.warn("Anonymous user: {}", auth != null && "anonymousUser".equals(auth.getName()));
                return "redirect:/auth/login";
            }

            String email = auth.getName();
            logger.info("Buscando usuario con email: '{}'", email);

            User user = userRepository.findByEmail(email).orElse(null);

            if (user == null) {
                logger.error("Usuario no encontrado en base de datos: '{}'", email);
                logger.error("=== USUARIO NO ENCONTRADO, REDIRIGIENDO ===");
                return "redirect:/auth/login";
            }

            logger.info("Usuario encontrado: {} con rol: {}", user.getEmail(), user.getRole());

            model.addAttribute("user", user);

            // Estadísticas básicas para todos
            long totalBooks = bookRepository.count();
            model.addAttribute("totalBooks", totalBooks);
            logger.info("Total de libros: {}", totalBooks);

            // Cargar estadísticas según el rol del usuario
            switch (user.getRole()) {
                case ADMIN:
                    logger.info("Cargando estadísticas para ADMIN");
                    long activeLoansAdmin = loanRepository.countByStatus(LoanStatus.ACTIVE);
                    long totalUsers = userRepository.count();
                    long overdueLoansAdmin = loanRepository.countByStatus(LoanStatus.OVERDUE);

                    model.addAttribute("activeLoans", activeLoansAdmin);
                    model.addAttribute("totalUsers", totalUsers);
                    model.addAttribute("overdueLoans", overdueLoansAdmin);

                    logger.info("Estadísticas ADMIN - Libros: {}, Préstamos activos: {}, Usuarios: {}, Préstamos vencidos: {}",
                            totalBooks, activeLoansAdmin, totalUsers, overdueLoansAdmin);
                    break;

                case LIBRARIAN:
                    logger.info("Cargando estadísticas para LIBRARIAN");
                    long totalReaders = userRepository.countByRole(Role.READER);
                    long activeLoansLib = loanRepository.countByStatus(LoanStatus.ACTIVE);
                    long overdueLoansLib = loanRepository.countByStatus(LoanStatus.OVERDUE);

                    model.addAttribute("totalReaders", totalReaders);
                    model.addAttribute("activeLoans", activeLoansLib);
                    model.addAttribute("overdueLoans", overdueLoansLib);

                    logger.info("Estadísticas LIBRARIAN - Lectores: {}, Préstamos activos: {}, Préstamos vencidos: {}",
                            totalReaders, activeLoansLib, overdueLoansLib);
                    break;

                case READER:
                    logger.info("Cargando estadísticas para READER");
                    int myLoans = loanRepository.findByUserId(user.getId()).size();
                    model.addAttribute("myLoans", myLoans);

                    logger.info("Estadísticas READER - Mis préstamos: {}", myLoans);
                    break;

                default:
                    logger.warn("Rol no reconocido: {}", user.getRole());
            }

            logger.info("=== DASHBOARD CARGADO EXITOSAMENTE ===");
            logger.info("Retornando vista 'dashboard'");
            return "dashboard";

        } catch (Exception e) {
            logger.error("=== ERROR EN DASHBOARD ===", e);
            return "redirect:/auth/login?error=dashboard";
        }
    }
}