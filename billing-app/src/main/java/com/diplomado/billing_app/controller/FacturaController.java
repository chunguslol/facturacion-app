package com.diplomado.billing_app.controller;

import com.diplomado.billing_app.model.Factura;
import com.diplomado.billing_app.repository.FacturaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/facturas")
public class FacturaController {

    @Autowired
    private FacturaRepository facturaRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/")
    public String index() {
        return "index"; // Esto busca el archivo index.html en /templates
    }


    @GetMapping("/listar")
    public List<Factura> listarFacturas() {
        return facturaRepository.findAll();
    }

    // MÉTODO VULNERABLE (SQL INJECTION)
    // SonarQube marcará esto como una vulnerabilidad CRÍTICA
    // Estamos concatenando el parámetro 'cliente' directamente a la consulta SQL
    @GetMapping("/buscar")
    public List<Map<String, Object>> buscarPorCliente(@RequestParam String cliente) {
        String sql = "SELECT * FROM facturas WHERE cliente = '" + cliente + "'";
        return jdbcTemplate.queryForList(sql);
    }

    // VULNERABILIDAD A01 / B1: Insecure Direct Object Reference (IDOR)
    // El sistema confía ciegamente en el ID proporcionado en la URL.
    // Explotación: Si entro a /api/facturas/ver/1 y cambio el 1 por un 5,
    // podré ver la factura de otra empresa sin autorización.
    @GetMapping("/ver/{id}")
    public String verDetalleFactura(@PathVariable Long id, org.springframework.ui.Model model) {
        Factura factura = facturaRepository.findById(id).orElse(new Factura("Desconocido", "No existe", 0.0));
        model.addAttribute("factura", factura);
        return "detalle"; // Carga la vista detalle.html
    }
}