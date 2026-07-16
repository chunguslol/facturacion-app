package com.diplomado.billing_app.controller;

import com.diplomado.billing_app.model.Usuario;
import com.diplomado.billing_app.repository.UsuarioRepository;
import com.diplomado.billing_app.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.DigestUtils;
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

    @PostMapping("/perfil/actualizar")
    public String actualizarPerfil(Usuario usuarioActualizado, HttpServletRequest request) {
        Long userId = getUserId(request);
        if (userId != null) {
            Usuario userDb = usuarioRepository.findById(userId).orElse(null);
            if (userDb != null) {
                userDb.setUsername(usuarioActualizado.getUsername());
                if (usuarioActualizado.getRol() != null) {
                    userDb.setRol(usuarioActualizado.getRol());
                }
                usuarioRepository.save(userDb);
            }
        }
        return "redirect:/api/usuarios/perfil?actualizado=true";
    }

    // =====================================================================
    // NUEVAS FUNCIONES DE ADMINISTRACIÓN (VULNERABLES)
    // =====================================================================

    // VULNERABILIDAD A01: Falta de Control de Acceso a Nivel de Función (Broken Access Control)
    // EL VENENO: Verificamos que el usuario esté logueado (userId != null), pero JAMÁS
    // verificamos si su rol es "ADMIN". Cualquier usuario que adivine o conozca esta URL
    // podrá acceder al panel de control de la empresa.
    @GetMapping("/gestion")
    public String gestionarUsuarios(HttpServletRequest request, Model model) {
        Long userId = getUserId(request);
        if (userId == null) return "redirect:/login"; // Solo verifica autenticación, NO autorización

        model.addAttribute("usuarios", usuarioRepository.findAll());
        return "gestion-usuarios";
    }

    // VULNERABILIDAD A02 & A01: Escalada de privilegios y contraseñas débiles
    @PostMapping("/crear")
    public String crearUsuario(HttpServletRequest request, @RequestParam String username, @RequestParam String rol) {
        Long userId = getUserId(request);
        if (userId == null) return "redirect:/login";

        // EL VENENO: Contraseña predeterminada corporativa quemada en código y en MD5
        String defaultPassword = org.springframework.util.DigestUtils.md5DigestAsHex("Cambiar123".getBytes());
        usuarioRepository.save(new Usuario(username, defaultPassword, rol));

        return "redirect:/api/usuarios/gestion";
    }
}