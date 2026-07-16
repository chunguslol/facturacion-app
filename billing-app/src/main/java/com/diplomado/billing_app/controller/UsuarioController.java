package com.diplomado.billing_app.controller;

import com.diplomado.billing_app.model.Usuario;
import com.diplomado.billing_app.repository.UsuarioRepository;
import com.diplomado.billing_app.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    private Long getUserId(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie c : request.getCookies()) {
                if ("AUTH_TOKEN".equals(c.getName())) return JwtUtil.getUserIdFromToken(c.getValue());
            }
        }
        return null;
    }

    @GetMapping("/perfil")
    public String verPerfil(HttpServletRequest request, Model model) {
        Long userId = getUserId(request);
        if (userId == null) return "redirect:/login";
        model.addAttribute("usuario", usuarioRepository.findById(userId).orElse(null));
        return "perfil";
    }

    // VULNERABILIDAD CRÍTICA: Asignación Masiva (Mass Assignment / Insecure Binding)
    // El objeto 'Usuario' recibe los datos del formulario. Si un atacante añade el parámetro '&rol=ADMIN'
    // en la petición HTTP, Spring Boot lo mapeará automáticamente y le dará privilegios de Administrador.
    @PostMapping("/perfil/actualizar")
    public String actualizarPerfil(Usuario usuarioActualizado, HttpServletRequest request) {
        Long userId = getUserId(request);
        if (userId != null) {
            Usuario userDb = usuarioRepository.findById(userId).orElse(null);
            if (userDb != null) {
                userDb.setUsername(usuarioActualizado.getUsername());
                // El error: ¡No filtramos el campo ROL! Si el atacante lo manda, se sobreescribe.
                if (usuarioActualizado.getRol() != null) {
                    userDb.setRol(usuarioActualizado.getRol());
                }
                usuarioRepository.save(userDb);
            }
        }
        return "redirect:/api/usuarios/perfil?actualizado=true";
    }
}