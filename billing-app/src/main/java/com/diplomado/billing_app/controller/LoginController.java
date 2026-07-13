package com.diplomado.billing_app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {

    // VULNERABILIDAD A02: Credenciales "quemadas" (hardcoded) en el código.
    // SonarQube marcará esto como una vulnerabilidad bloqueante crítica.
    private final String ADMIN_USER = "admin";
    private final String ADMIN_PASS = "123456"; // Contraseña en texto plano sin Hash

    @GetMapping("/login")
    public String showLogin() {
        return "login"; // Retorna la vista login.html
    }

    @PostMapping("/login")
    public String doLogin(@RequestParam String username, @RequestParam String password) {
        // VULNERABILIDAD A02: Validación insegura. Un atacante que acceda al código
        // o a la base de datos no necesita descifrar nada.
        if(ADMIN_USER.equals(username) && ADMIN_PASS.equals(password)) {
            return "redirect:/api/facturas/"; // Redirige al Dashboard si es exitoso
        }
        return "redirect:/login?error=true";
    }
}