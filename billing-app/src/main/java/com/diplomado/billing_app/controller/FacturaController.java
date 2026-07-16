package com.diplomado.billing_app.controller;

import com.diplomado.billing_app.model.Factura;
import com.diplomado.billing_app.repository.FacturaRepository;
import com.diplomado.billing_app.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.StringReader;
import java.nio.file.Files;
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
        if (userId == null) return "redirect:/login";

        String sql = "SELECT * FROM facturas WHERE propietario_id = " + userId;
        List<Map<String, Object>> facturas = jdbcTemplate.queryForList(sql);
        model.addAttribute("facturas", facturas);
        return "dashboard";
    }

    @GetMapping("/buscar")
    public String buscarPorCliente(@RequestParam(required = false) String cliente, HttpServletRequest request, Model model) {
        Long userId = getUserIdFromCookies(request);
        if (userId == null) return "redirect:/login";

        String sql;
        // CORRECCIÓN: Si el buscador está vacío, mostramos todos. Si tiene texto, inyectamos el veneno (SQLi).
        if (cliente == null || cliente.trim().isEmpty()) {
            sql = "SELECT * FROM facturas WHERE propietario_id = " + userId;
        } else {
            sql = "SELECT * FROM facturas WHERE propietario_id = " + userId + " AND cliente = '" + cliente + "'";
        }

        List<Map<String, Object>> facturas = jdbcTemplate.queryForList(sql);
        model.addAttribute("facturas", facturas);
        return "dashboard";
    }

    @GetMapping("/ver/{id}")
    public String verDetalleFactura(@PathVariable Long id, HttpServletRequest request, Model model) {
        Long loggedInUserId = getUserIdFromCookies(request);
        if (loggedInUserId == null) return "redirect:/login";

        Factura factura = facturaRepository.findById(id).orElse(new Factura(0L, "N/A", "N/A", 0.0));
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

        File file = new File(archivo);

        if (file.exists()) {
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");
            Files.copy(file.toPath(), response.getOutputStream());
            response.getOutputStream().flush();
        } else {
            response.sendError(404, "El PDF de la factura aún no ha sido generado por el sistema batch.");
        }
    }

    @PostMapping("/importar-xml")
    public String importarXML(@RequestParam String xmlData, Model model, HttpServletRequest request) {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(new InputSource(new StringReader(xmlData)));

            String clienteExtraido = doc.getElementsByTagName("cliente").item(0).getTextContent();
            model.addAttribute("mensajeXML", "Factura importada para: " + clienteExtraido);
        } catch (Exception e) {
            model.addAttribute("mensajeXML", "Error procesando XML: " + e.getMessage());
        }
        return dashboard(request, model);
    }
}