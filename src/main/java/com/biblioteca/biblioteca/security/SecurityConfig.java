package com.biblioteca.biblioteca.security;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.security.Key;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);

    private final JwtAuthEntryPoint authEntryPoint;
    private final Key jwtSecretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.authEntryPoint = new JwtAuthEntryPoint();
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        log.info("Configurando SecurityFilterChain");

        http
                .csrf().disable()
                .headers().frameOptions().disable().and()

                // Configuración híbrida: sesiones para web, stateless para API
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false)
                .and()
                .and()

                // Configurar RequestCache para no guardar peticiones a login
                .requestCache()
                .requestCache(new HttpSessionRequestCache() {
                    @Override
                    public void saveRequest(HttpServletRequest request, HttpServletResponse response) {
                        String requestURI = request.getRequestURI();
                        // No guardar peticiones a rutas de login/auth
                        if (!requestURI.contains("/login") && !requestURI.contains("/auth/")) {
                            super.saveRequest(request, response);
                        }
                    }
                })
                .and()

                .authorizeRequests()
                // Recursos públicos
                .antMatchers("/.well-known/**").permitAll()
                .antMatchers("/", "/index", "/css/**", "/js/**", "/images/**",
                        "/webjars/**", "/favicon.ico", "/test/**", "/h2-console/**").permitAll()

                // Autenticación
                .antMatchers("/auth/**").permitAll()
                .antMatchers("/api/auth/**").permitAll()

                // API endpoints (requieren JWT)
                .antMatchers("/api/admin/**").hasRole("ADMIN")
                .antMatchers("/api/librarian/**").hasAnyRole("LIBRARIAN", "ADMIN")
                .antMatchers("/api/books/**").hasAnyRole("LIBRARIAN", "ADMIN")

                // Web endpoints (requieren sesión)
                .antMatchers("/admin/**").hasRole("ADMIN")
                .antMatchers("/books/**", "/loans/**", "/readers/**").hasAnyRole("LIBRARIAN", "ADMIN")
                .antMatchers("/dashboard").authenticated()

                .anyRequest().authenticated()
                .and()

                // Configuración del login por formulario
                .formLogin()
                .loginPage("/auth/login")
                .loginProcessingUrl("/auth/login")
                .usernameParameter("username")  // Cambia según tu formulario
                .passwordParameter("password")
                .defaultSuccessUrl("/dashboard", true)
                .successHandler((request, response, authentication) -> {
                    log.info("=== LOGIN EXITOSO ===");
                    log.info("Usuario autenticado: {}", authentication.getName());
                    log.info("Autoridades: {}", authentication.getAuthorities());

                    // Crear sesión y loggear detalles
                    HttpSession session = request.getSession(true);
                    log.info("Sesión creada - ID: {}", session.getId());

                    // LIMPIAR EL REQUEST CACHE para evitar redirecciones no deseadas
                    HttpSessionRequestCache requestCache = new HttpSessionRequestCache();
                    requestCache.removeRequest(request, response);
                    log.info("RequestCache limpiado");

                    response.sendRedirect("/dashboard");
                })
                .failureHandler((request, response, exception) -> {
                    log.error("Fallo en el login: {}", exception.getMessage());
                    response.sendRedirect("/auth/login?error=true");
                })
                .permitAll()
                .and()

                // Remember me
                .rememberMe()
                .key("bibliotecaRememberMeKey")
                .tokenValiditySeconds(86400)
                .userDetailsService(userDetailsService)
                .and()

                // Logout
                .logout()
                .logoutUrl("/logout")
                .deleteCookies("JSESSIONID", "remember-me")
                .clearAuthentication(true)
                .invalidateHttpSession(true)
                .logoutSuccessUrl("/auth/login?logout=true")
                .permitAll()
                .and()

                // Exception handling
                .exceptionHandling()
                .authenticationEntryPoint((request, response, authException) -> {
                    String requestURI = request.getRequestURI();
                    log.error("Error de autenticación en {}: {}", requestURI, authException.getMessage());

                    // Si es una petición a la API, usar JWT entry point
                    if (requestURI.startsWith("/api/")) {
                        authEntryPoint.commence(request, response, authException);
                    } else {
                        // Si es web, redirigir al login
                        response.sendRedirect("/auth/login?expired=true");
                    }
                })
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    log.error("Acceso denegado en {}: {}", request.getRequestURI(), accessDeniedException.getMessage());
                    response.sendRedirect("/auth/login?denied=true");
                });

        // Solo agregar el filtro JWT para rutas de API - TEMPORALMENTE DESACTIVADO PARA DEBUG
        // http.addFilterBefore(new JwtAuthenticationFilter(jwtSecretKey, userDetailsService),
        //                    UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public Key jwtSecretKey() {
        return jwtSecretKey;
    }
}