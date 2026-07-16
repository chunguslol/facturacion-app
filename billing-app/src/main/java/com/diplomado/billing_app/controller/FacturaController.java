package com.diplomado.billing_app.controller;

import com.diplomado.billing_app.model.Factura;
import com.diplomado.billing_app.repository.FacturaRepository;
import com.diplomado.billing_app.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.Cookie;
import java.io.File;
import java.nio.file.Files;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/api/facturas")
public class FacturaController {

    @Autowired
    private FacturaRepository facturaRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Long getUserIdFromCookies(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("AUTH_TOKEN".equals(cookie.getName())) {
                    return JwtUtil.getUserIdFromToken(cookie.getValue());
                }
            }
        }
        return null;
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpServletRequest request, Model model) {
        Long userId = getUserIdFromCookies(request);
        if (userId == null) return "redirect:/login"; // No autenticado

        // Muestra todas las facturas del usuario conectado
        String sql = "SELECT * FROM facturas WHERE propietario_id = " + userId;
        List<Map<String, Object>> facturas = jdbcTemplate.queryForList(sql);
        model.addAttribute("facturas", facturas);
        return "dashboard";
    }

    // VULNERABILIDAD A03: SQL Injection clásico.
    @GetMapping("/buscar")
    public String buscarPorCliente(@RequestParam String cliente, HttpServletRequest request, Model model) {
        Long userId = getUserIdFromCookies(request);
        if (userId == null) return "redirect:/login";

        // EL VENENO: Concatenación directa de input de usuario en la base de datos.
        String sql = "SELECT * FROM facturas WHERE propietario_id = " + userId + " AND cliente = '" + cliente + "'";
        List<Map<String, Object>> facturas = jdbcTemplate.queryForList(sql);
        model.addAttribute("facturas", facturas);
        return "dashboard";
    }

    // VULNERABILIDAD A01 / B1: Broken Object Level Authorization (IDOR)
    @GetMapping("/ver/{id}")
    public String verDetalleFactura(@PathVariable Long id, HttpServletRequest request, Model model) {
        Long loggedInUserId = getUserIdFromCookies(request);
        if (loggedInUserId == null) return "redirect:/login";

        Factura factura = facturaRepository.findById(id).orElse(new Factura(0L, "N/A", "N/A", 0.0));

        // EL VENENO (BOLA): Consultamos la factura por ID, PERO JAMÁS VERIFICAMOS
        // si factura.getPropietarioId() == loggedInUserId.
        // Cualquier usuario logueado puede ver facturas de la competencia cambiando el ID en la URL.
        model.addAttribute("factura", factura);
        return "detalle";
    }

    @PostMapping("/crear")
    public String crearFactura(@RequestParam String cliente, @RequestParam String detalle, @RequestParam Double monto, HttpServletRequest request) {
        Long userId = getUserIdFromCookies(request);
        if (userId != null) {
            facturaRepository.save(new Factura(userId, cliente, detalle, monto));
        }
        return "redirect:/api/facturas/dashboard";
    }

    @GetMapping("/descargar")
    public void descargarComprobante(@RequestParam String archivo, HttpServletResponse response, HttpServletRequest request) throws Exception {
        Long userId = getUserIdFromCookies(request);
        if (userId == null) return;

        // Construcción insegura de la ruta del archivo
        File file = new File(archivo);

        if (file.exists()) {
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");
            Files.copy(file.toPath(), response.getOutputStream());
            response.getOutputStream().flush();
        } else {
            // Simulamos que el archivo PDF de la factura no se generó aún para facturas nuevas
            response.sendError(404, "El PDF de la factura aún no ha sido generado por el sistema batch.");
        }
    }
}