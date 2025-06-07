package com.biblioteca.biblioteca.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HomeController {

    @GetMapping("/home")
    @ResponseBody
    public String home() {
        return "¡Bienvenido a la Biblioteca! La aplicación está funcionando correctamente.";
    }
    
    @GetMapping("/check")
    @ResponseBody
    public String check() {
        return "Sistema funcionando correctamente. Puedes acceder a la aplicación en: <br>" +
               "<a href='/'>Página principal</a><br>" +
               "<a href='/auth/login'>Iniciar sesión</a><br>" +
               "<a href='/auth/register'>Registrarse</a><br>";
    }
}
