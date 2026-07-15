package com.diplomado.billing_app.controller;

import com.diplomado.billing_app.model.Usuario;
import com.diplomado.billing_app.repository.UsuarioRepository;
import com.diplomado.billing_app.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping("/login")
    public String showLogin() {
        return "login";
    }

    @PostMapping("/login")
    public String doLogin(@RequestParam String username, @RequestParam String password, HttpServletResponse response) {
        Usuario user = usuarioRepository.findByUsername(username);

        // VULNERABILIDAD A02: Uso de MD5 para hashear contraseñas (Totalmente obsoleto y vulnerable a colisiones/Rainbow tables)
        String md5Password = DigestUtils.md5DigestAsHex(password.getBytes());

        if (user != null && user.getPasswordHash().equals(md5Password)) {
            // Genera el JWT vulnerable y lo guarda en una cookie
            String token = JwtUtil.generateToken(user.getUsername(), user.getId());
            Cookie cookie = new Cookie("AUTH_TOKEN", token);
            cookie.setPath("/");
            response.addCookie(cookie);
            return "redirect:/api/facturas/dashboard";
        }
        return "redirect:/login?error=true";
    }
}