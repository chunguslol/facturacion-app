package com.diplomado.billing_app.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "facturas")
public class Factura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long propietarioId;
    private String cliente;
    private String detalle;
    private Double monto;

    // Constructores, Getters y Setters
    public Factura() {}

    public Factura(Long propietarioId, String cliente, String detalle, Double monto) {
        this.propietarioId = propietarioId;
        this.cliente = cliente;
        this.detalle = detalle;
        this.monto = monto;
    }

    public Long getId() { return id; }
    public Long getPropietarioId() { return propietarioId; }
    public String getCliente() { return cliente; }
    public void setCliente(String cliente) { this.cliente = cliente; }
    public String getDetalle() { return detalle; }
    public void setDetalle(String detalle) { this.detalle = detalle; }
    public Double getMonto() { return monto; }
    public void setMonto(Double monto) { this.monto = monto; }
}