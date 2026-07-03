package com.diplomado.billingapp.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "facturas")
public class Factura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String cliente;
    private String detalle;
    private Double monto;

    // Constructores, Getters y Setters
    public Factura() {}

    public Factura(String cliente, String detalle, Double monto) {
        this.cliente = cliente;
        this.detalle = detalle;
        this.monto = monto;
    }

    public Long getId() { return id; }
    public String getCliente() { return cliente; }
    public void setCliente(String cliente) { this.cliente = cliente; }
    public String getDetalle() { return detalle; }
    public void setDetalle(String detalle) { this.detalle = detalle; }
    public Double getMonto() { return monto; }
    public void setMonto(Double monto) { this.monto = monto; }
}