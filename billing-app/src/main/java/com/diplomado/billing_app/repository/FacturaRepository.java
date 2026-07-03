package com.diplomado.billingapp.repository;

import com.diplomado.billingapp.model.Factura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FacturaRepository extends JpaRepository<Factura, Long> {
    // Esto nos da métodos como .findAll(), .save(), .findById() sin escribir código extra.
}