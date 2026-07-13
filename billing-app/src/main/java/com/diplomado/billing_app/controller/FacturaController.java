package com.diplomado.billingapp.controller;

import com.diplomado.billingapp.model.Factura;
import com.diplomado.billingapp.repository.FacturaRepository;
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
}